package com.kelvsyc.kotlin.core

import com.kelvsyc.kotlin.core.traits.Bid32
import com.kelvsyc.kotlin.core.traits.ValueEquality
import com.kelvsyc.kotlin.core.PartialComparator

// Powers of 10 used when scaling significands for comparison. Index is the exponent difference (0..6).
private val DECIMAL32_POW10 = longArrayOf(1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L)

/**
 * Compares two positive finite [BidFloat] values without normalising first.
 *
 * The significand is at most 9,999,999 (< 10^7), so if the biased-exponent difference exceeds 6 the
 * higher-exponent value is always larger. Otherwise both sides are scaled to a common quantum using
 * [DECIMAL32_POW10] and compared as [Long]s, avoiding any heap allocation.
 */
private fun comparePositiveFinite(a: BidFloat, b: BidFloat): Int {
    val diff = a.biasedExponent - b.biasedExponent
    return when {
        diff > 6  -> 1
        diff < -6 -> -1
        diff >= 0 -> (a.significand.toLong() * DECIMAL32_POW10[diff]).compareTo(b.significand.toLong())
        else      -> a.significand.toLong().compareTo(b.significand.toLong() * DECIMAL32_POW10[-diff])
    }
}

/**
 * Value representing a `decimal32` floating-point number with the significand in binary integer decimal format.
 *
 * The 32-bit layout follows IEEE 754-2008 §3.5.2:
 * - Bit 31: sign
 * - Bits 30–20: combination field (the G field in the standard; also called "combination" here)
 * - Bits 19–0: continuation field (the T field in the standard; also called "trailing significand")
 *
 * The combination/G field encodes both the biased exponent and the most significant decimal digit of
 * the significand. The continuation/T field holds the remaining significand bits (in BID encoding,
 * as a binary integer; in DPD encoding, as packed decimal declets — but this type always uses BID).
 *
 * ## Equality and hashing
 *
 * Kotlin reserves `equals` and `hashCode` in value classes for future language use; they cannot be
 * overridden here. The compiler-generated `equals` compares raw [bits], which gives bit-pattern
 * equality: `+0` and `−0` are not equal (correct), but two NaN values with different payloads are
 * also not equal (diverges from [Float.equals], where all NaNs are equal).
 *
 * Decimal floating-point introduces a further complication absent from binary types: the same
 * mathematical value can have multiple bit-pattern representations (called *cohorts* in IEEE 754-2008),
 * because the significand and exponent can be scaled independently. For example, `1 × 10⁰` and
 * `10 × 10⁻¹` are distinct bit patterns but the same number. Bit-pattern equality therefore fails
 * to recognise cohort-equal values as equal.
 *
 * Use [BidFloat.equalTo] and [BidFloat.hash] when [Float]-like semantics (NaN-normalised,
 * cohort-unaware bit equality) are needed. When cohort-aware numerical equality is needed, use
 * [Companion.numericalEquality].
 */
@JvmInline
value class BidFloat(val bits: Int) {
    companion object : Bid32<BidFloat> {
        /**
         * Returns `true` if [a] and [b] are equal under equivalence semantics: all NaN values are
         * equal to each other regardless of payload, and all other values are compared by bit pattern
         * (so `+0` and `−0` are not equal, and cohort-distinct representations are not equal).
         *
         * Use this in `equals`/`hashCode` implementations of types that embed a [BidFloat].
         *
         * @see hash
         */
        fun equalTo(a: BidFloat, b: BidFloat): Boolean {
            if (a.isNaN() || b.isNaN()) return a.isNaN() && b.isNaN()
            return a.bits == b.bits
        }

        /**
         * Returns a hash code consistent with [equalTo]: all NaN values produce the same hash,
         * and `+0` and `−0` produce different hashes.
         *
         * @see equalTo
         */
        fun hash(value: BidFloat): Int = if (value.isNaN()) 0x7C080000 else value.bits

        /**
         * Reduces a (biasedExponent, significand) pair to canonical form by stripping trailing
         * decimal zeros from the significand and incrementing the exponent accordingly.
         *
         * Two finite non-zero BidFloat values are numerically equal if and only if they produce the
         * same canonical pair (and have the same sign).
         */
        private fun normalize(biasedExponent: Int, significand: Int): Pair<Int, Int> {
            var exp = biasedExponent
            var sig = significand
            while (sig % 10 == 0) { sig /= 10; exp++ }
            return exp to sig
        }

        // Canonical normalization is used rather than delegating to toBigDecimal().compareTo() (JVM only).
        // For a 7-digit significand the normalization loop runs at most 6 times with integer arithmetic
        // and no allocations, which is cheaper than constructing two BigDecimal/BigInteger objects.
        override val numericalEquality: ValueEquality<BidFloat> = object : ValueEquality<BidFloat> {
            override fun BidFloat.isEqualTo(other: BidFloat): Boolean {
                if (isNaN() || other.isNaN()) return false
                if (isZero() && other.isZero()) return true
                if (sign != other.sign) return false
                val (ea, sa) = normalize(biasedExponent, significand)
                val (eb, sb) = normalize(other.biasedExponent, other.significand)
                return ea == eb && sa == sb
            }
        }

        override val equivalenceEquality: ValueEquality<BidFloat> = object : ValueEquality<BidFloat> {
            override fun BidFloat.isEqualTo(other: BidFloat): Boolean = equalTo(this, other)
        }

        override val comparator: Comparator<BidFloat> = Comparator { a, b ->
            // NaN is ordered after everything else; two NaNs are equal in the total order.
            val aNaN = a.isNaN(); val bNaN = b.isNaN()
            if (aNaN && bNaN) return@Comparator 0
            if (aNaN) return@Comparator 1
            if (bNaN) return@Comparator -1
            // Different signs: negative < positive.
            if (a.sign != b.sign) return@Comparator if (a.sign) -1 else 1
            // Both zero (any cohort, any sign): equal.
            if (a.isZero() && b.isZero()) return@Comparator 0
            // Zero vs non-zero, same sign.
            if (a.isZero()) return@Comparator if (a.sign) 1 else -1
            if (b.isZero()) return@Comparator if (b.sign) -1 else 1
            // Both infinity, same sign: equal.
            if (a.isInfinite() && b.isInfinite()) return@Comparator 0
            // Infinity vs finite, same sign.
            if (a.isInfinite()) return@Comparator if (a.sign) -1 else 1
            if (b.isInfinite()) return@Comparator if (b.sign) 1 else -1
            // Both finite and non-zero, same sign: compare magnitudes.
            val absComp = comparePositiveFinite(a, b)
            if (a.sign) -absComp else absComp
        }

        override val partialComparator: PartialComparator<BidFloat> = PartialComparator { a, b ->
            if (a.isNaN() || b.isNaN()) null else comparator.compare(a, b)
        }

        // G[0..4]=11111, G[5]=1 (quiet), no payload, positive sign.
        override val NaN: BidFloat get() = BidFloat(0x7E000000)
        // G[0..4]=11110, positive sign.
        override val positiveInfinity: BidFloat get() = BidFloat(0x78000000)
        // G[0..4]=11110, negative sign.
        override val negativeInfinity: BidFloat get() = BidFloat(Int.MIN_VALUE or 0x78000000)
        // 9999999 × 10^90: large-significand encoding, biasedExponent=191, significand=9999999.
        override val maxValue: BidFloat get() = BidFloat(0x77F8967F)
        // 1 × 10^(-101): biasedExponent=0, significand=1.
        override val minValue: BidFloat get() = BidFloat(1)
        // 1000000 × 10^(-101) = 10^(-95): biasedExponent=0, significand=1000000.
        override val minNormal: BidFloat get() = BidFloat(0x000F4240)
        // 1 × 10^(-6): biasedExponent=95, significand=1.
        override val epsilon: BidFloat get() = BidFloat(0x2F800001)
    }

    /**
     * The sign bit. `true` for negative values (including negative zero and negative NaN).
     */
    val sign: Boolean
        get() = bits < 0

    /**
     * The 11-bit combination field (IEEE 754-2008 calls this the G field), extracted from bits 30–20.
     *
     * This field encodes both the biased exponent and the leading decimal digit of the significand
     * using a two-case encoding based on the top two bits G[0..1]:
     *
     * - G[0..1] ≠ 11 (combination < 0x600): exponent occupies G[0..7] (bits 30–23); the top 3
     *   significand bits occupy G[8..10] (bits 22–20). Significand range: 0–8,388,607.
     * - G[0..1] = 11, G[2] = 0 (combination in 0x600..0x7BF): "large significand" encoding.
     *   Exponent occupies G[3..10] (bits 27–20); significand = 2²³ + bits[20..0]. Range: 8,388,608–9,999,999.
     * - G[0..4] = 11110: infinity.
     * - G[0..4] = 11111: NaN.
     *
     * The two-case split exists because a 7-digit decimal significand (max 9,999,999) requires 24 bits,
     * but only 23 bits are available in the straightforward layout. The large-significand encoding
     * reclaims 2 exponent bits from the combination field to make room for the extra significand bit.
     */
    val combination: Int
        get() = (bits ushr 20) and 0x7FF

    /**
     * The 20-bit continuation field (IEEE 754-2008 calls this the T field), extracted from bits 19–0.
     *
     * In BID encoding (used here), this holds the low 20 bits of the integer significand.
     * The high bits of the significand are recovered from [combination]; see [significand].
     *
     * In DPD encoding, this field would hold two 10-bit "declets", each encoding three decimal digits.
     */
    val continuation: Int
        get() = bits and 0xFFFFF

    /**
     * The 8-bit biased exponent. The unbiased exponent is `biasedExponent - 101`.
     *
     * The bias of 101 comes from IEEE 754-2008 Table 3.6 (emax = 96, p = 7: bias = emax + p − 2 = 101).
     * Valid biased exponents are 0–191, representing unbiased exponents −101 to +90.
     *
     * The bit position of the exponent within [combination] depends on the encoding case:
     * - Normal (combination < 0x600): exponent = combination[10..3] — the top 8 of the 11 combination bits.
     * - Large-significand (top 2 bits = 11): exponent = combination[7..0] — the bottom 8 combination bits.
     *
     * Undefined for NaN and infinity; check [isNaN] or [isInfinite] first.
     */
    val biasedExponent: Int
        get() = if ((combination ushr 9) != 3) {
            // Normal encoding: exponent is the top 8 bits of the combination field (G[0..7]).
            combination ushr 3
        } else {
            // Large-significand encoding: exponent is combination[8..1] (= bits[28..21]).
            // Bit 28 (combination[8]) is the Case 2 indicator and is always 0, giving
            // biasedExponent ≤ 127. Shift combination right by 1 to align it to [7..0].
            (combination ushr 1) and 0xFF
        }

    /**
     * The integer significand (coefficient), a value in the range 0–9,999,999.
     *
     * In BID (Binary Integer Decimal) encoding the entire 7-digit significand is stored as a single
     * binary integer, split across [combination] and [continuation]:
     *
     * - Normal encoding (combination < 0x600): significand = G[8..10] (3 bits) concatenated with
     *   T (20 bits) = 23-bit integer, range 0–8,388,607.
     * - Large-significand encoding (top 2 combination bits = 11, bit 8 = 0): significand = 2²³ +
     *   bits[20..0] (the low 21 bits of [bits], which are G[10] and T). This covers 8,388,608–9,999,999.
     *
     * The two cases together cover exactly the full 7-digit decimal range without overlap.
     *
     * Undefined for NaN and infinity; check [isNaN] or [isInfinite] first.
     */
    val significand: Int
        get() = if ((combination ushr 9) != 3) {
            // Normal encoding: high 3 bits from G[8..10], low 20 bits from T.
            ((combination and 0x7) shl 20) or continuation
        } else {
            // Large-significand encoding: implicit leading bit 2^23 plus the low 21 bits of the word.
            0x800000 or (bits and 0x1FFFFF)
        }

    /**
     * Returns `true` if this value is a NaN (Not a Number).
     *
     * Indicated by G[0..4] = 11111 — the top 5 bits of the combination field are all set.
     * Both quiet NaN (G[5] = 1) and signalling NaN (G[5] = 0) return `true`.
     */
    fun isNaN(): Boolean = (combination ushr 6) and 0x1F == 0x1F

    /**
     * Returns `true` if this value is positive or negative infinity.
     *
     * Indicated by G[0..4] = 11110 — the top 5 bits of the combination field are 11110.
     */
    fun isInfinite(): Boolean = (combination ushr 6) and 0x1F == 0x1E

    /**
     * Returns `true` if this value is finite (not NaN, not infinite) and has a zero significand.
     *
     * Both +0 and −0 return `true`. Use [sign] to distinguish them.
     */
    fun isZero(): Boolean = !isNaN() && !isInfinite() && significand == 0
}
