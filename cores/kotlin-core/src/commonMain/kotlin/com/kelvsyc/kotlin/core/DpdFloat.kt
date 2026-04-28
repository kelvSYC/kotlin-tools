package com.kelvsyc.kotlin.core

import com.kelvsyc.kotlin.core.traits.Dpd32
import com.kelvsyc.kotlin.core.traits.FloatingPointSign
import com.kelvsyc.kotlin.core.traits.IeeeFloatingPointClassification
import com.kelvsyc.kotlin.core.traits.ValueEquality

// Powers of 10 indexed by exponent (0..6), used when comparing scaled significands.
private val DECIMAL32_POW10 = longArrayOf(1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L)

/**
 * Decodes a 10-bit DPD declet into a 3-digit decimal value (0–999).
 *
 * Implements the five encoding cases from IEEE 754-2008 Table 3.4. The case is determined by
 * bits b[3:1] of the declet (b[0] = LSB). When b[3]=1 and b[2:1]=11 (case 4), bits b[9:8]
 * further distinguish which pair(s) of digits occupy the large-digit slots.
 *
 * The 24 unused declet patterns (those with b[9:8]=11, b[3:1]=111, and b[6:5] ≠ 00) decode
 * to the all-large-digit formula, which may not produce the value originally intended. Callers
 * should only pass declets extracted from valid [DpdFloat] bit patterns.
 */
private fun decodeDeclet(d: Int): Int {
    val b = d and 0x3FF
    return when {
        b and 0x8 == 0 -> {
            // Case 0: b[3]=0 → all three digits in [0,7]
            ((b ushr 7) and 0x7) * 100 + ((b ushr 4) and 0x7) * 10 + (b and 0x7)
        }
        b and 0xE == 0x8 -> {
            // Case 1: b[3:1]=100 → d3 ∈ {8,9}
            ((b ushr 7) and 0x7) * 100 + ((b ushr 4) and 0x7) * 10 + 8 + (b and 0x1)
        }
        b and 0xE == 0xA -> {
            // Case 2: b[3:1]=101 → d2 ∈ {8,9}
            val d1 = (b ushr 7) and 0x7
            val d2 = 8 + ((b ushr 4) and 0x1)
            val d3 = ((b ushr 4) and 0x6) or (b and 0x1)
            d1 * 100 + d2 * 10 + d3
        }
        b and 0xE == 0xC -> {
            // Case 3: b[3:1]=110 → d1 ∈ {8,9}
            val d1 = 8 + ((b ushr 7) and 0x1)
            val d2 = (b ushr 4) and 0x7
            val d3 = ((b ushr 7) and 0x6) or (b and 0x1)
            d1 * 100 + d2 * 10 + d3
        }
        else -> when ((b ushr 8) and 0x3) {
            // Case 4: b[3:1]=111 → at least two digits ∈ {8,9}, sub-case from b[9:8]
            0 -> {
                // b[9:8]=00 → d1,d2 ∈ {8,9}; d3 ∈ [0,7] via b[6:5] and b[0]
                val d1 = 8 + ((b ushr 7) and 0x1)
                val d2 = 8 + ((b ushr 4) and 0x1)
                val d3 = ((b ushr 4) and 0x6) or (b and 0x1)
                d1 * 100 + d2 * 10 + d3
            }
            1 -> {
                // b[9:8]=01 → d1,d3 ∈ {8,9}; d2 ∈ [0,7] via b[6:4]
                val d1 = 8 + ((b ushr 7) and 0x1)
                val d2 = (b ushr 4) and 0x7
                val d3 = 8 + (b and 0x1)
                d1 * 100 + d2 * 10 + d3
            }
            2 -> {
                // b[9:8]=10 → d2,d3 ∈ {8,9}; d1 ∈ [0,7] via b[7:5]
                val d1 = (b ushr 5) and 0x7
                val d2 = 8 + ((b ushr 4) and 0x1)
                val d3 = 8 + (b and 0x1)
                d1 * 100 + d2 * 10 + d3
            }
            else -> {
                // b[9:8]=11 → all three ∈ {8,9}; b[6:5] are don't-care
                val d1 = 8 + ((b ushr 7) and 0x1)
                val d2 = 8 + ((b ushr 4) and 0x1)
                val d3 = 8 + (b and 0x1)
                d1 * 100 + d2 * 10 + d3
            }
        }
    }
}

/**
 * Compares two positive finite [DpdFloat] values without normalising first.
 *
 * The significand is at most 9,999,999 (< 10^7), so if the biased-exponent difference exceeds 6 the
 * higher-exponent value is always larger. Otherwise both sides are scaled to a common quantum using
 * [DECIMAL32_POW10] and compared as [Long]s, avoiding any heap allocation.
 */
private fun comparePositiveFinite(a: DpdFloat, b: DpdFloat): Int {
    val diff = a.biasedExponent - b.biasedExponent
    return when {
        diff > 6  -> 1
        diff < -6 -> -1
        diff >= 0 -> (a.significand.toLong() * DECIMAL32_POW10[diff]).compareTo(b.significand.toLong())
        else      -> a.significand.toLong().compareTo(b.significand.toLong() * DECIMAL32_POW10[-diff])
    }
}

/**
 * Value representing a `decimal32` floating-point number with the significand in densely packed decimal format.
 *
 * The 32-bit layout follows IEEE 754-2008 §3.5.2:
 * - Bit 31: sign
 * - Bits 30–20: combination field (the G field in the standard)
 * - Bits 19–0: continuation field (the T field in the standard)
 *
 * The combination field encodes both the biased exponent and the most significant decimal digit using
 * the same two-case encoding as [BidFloat]. The continuation field holds two 10-bit *declets* (bits
 * 19–10 and bits 9–0), each encoding three decimal digits using the densely packed decimal scheme
 * from IEEE 754-2008 Table 3.4.
 *
 * ## Equality and hashing
 *
 * Kotlin reserves `equals` and `hashCode` in value classes for future language use; they cannot be
 * overridden here. The compiler-generated `equals` compares raw [bits], which gives bit-pattern
 * equality: `+0` and `−0` are not equal (correct), but two NaN values with different payloads are
 * also not equal (diverges from [Float.equals], where all NaNs are equal).
 *
 * Decimal floating-point introduces a further complication absent from binary types: the same
 * mathematical value can have multiple bit-pattern representations (called *cohorts* in IEEE 754-2008).
 * Use [DpdFloat.equalTo] and [DpdFloat.hash] when [Float]-like semantics (NaN-normalised,
 * cohort-unaware bit equality) are needed. When cohort-aware numerical equality is needed, use
 * [Companion.numericalEquality].
 */
@JvmInline
value class DpdFloat(val bits: Int) {
    companion object : Dpd32<DpdFloat> {
        /**
         * Returns `true` if [a] and [b] are equal under equivalence semantics: all NaN values are
         * equal to each other regardless of payload, and all other values are compared by bit pattern
         * (so `+0` and `−0` are not equal, and cohort-distinct representations are not equal).
         *
         * Use this in `equals`/`hashCode` implementations of types that embed a [DpdFloat].
         *
         * @see hash
         */
        fun equalTo(a: DpdFloat, b: DpdFloat): Boolean {
            if (a.isNaN() || b.isNaN()) return a.isNaN() && b.isNaN()
            return a.bits == b.bits
        }

        /**
         * Returns a hash code consistent with [equalTo]: all NaN values produce the same hash,
         * and `+0` and `−0` produce different hashes.
         *
         * @see equalTo
         */
        fun hash(value: DpdFloat): Int = if (value.isNaN()) 0x7E000000 else value.bits

        /**
         * Reduces a (biasedExponent, significand) pair to canonical form by stripping trailing
         * decimal zeros from the significand and incrementing the exponent accordingly.
         *
         * Two finite non-zero DpdFloat values are numerically equal if and only if they produce the
         * same canonical pair (and have the same sign).
         */
        private fun normalize(biasedExponent: Int, significand: Int): Pair<Int, Int> {
            var exp = biasedExponent
            var sig = significand
            while (sig % 10 == 0) { sig /= 10; exp++ }
            return exp to sig
        }

        override val numericalEquality: ValueEquality<DpdFloat> = object : ValueEquality<DpdFloat> {
            override fun DpdFloat.isEqualTo(other: DpdFloat): Boolean {
                if (isNaN() || other.isNaN()) return false
                if (isZero() && other.isZero()) return true
                if (sign != other.sign) return false
                val (ea, sa) = normalize(biasedExponent, significand)
                val (eb, sb) = normalize(other.biasedExponent, other.significand)
                return ea == eb && sa == sb
            }
        }

        override val equivalenceEquality: ValueEquality<DpdFloat> = object : ValueEquality<DpdFloat> {
            override fun DpdFloat.isEqualTo(other: DpdFloat): Boolean = equalTo(this, other)
        }

        override val comparator: Comparator<DpdFloat> = Comparator { a, b ->
            val aNaN = a.isNaN(); val bNaN = b.isNaN()
            if (aNaN && bNaN) return@Comparator 0
            if (aNaN) return@Comparator 1
            if (bNaN) return@Comparator -1
            if (a.sign != b.sign) return@Comparator if (a.sign) -1 else 1
            if (a.isZero() && b.isZero()) return@Comparator 0
            if (a.isZero()) return@Comparator if (a.sign) 1 else -1
            if (b.isZero()) return@Comparator if (b.sign) -1 else 1
            if (a.isInfinite() && b.isInfinite()) return@Comparator 0
            if (a.isInfinite()) return@Comparator if (a.sign) -1 else 1
            if (b.isInfinite()) return@Comparator if (b.sign) 1 else -1
            val absComp = comparePositiveFinite(a, b)
            if (a.sign) -absComp else absComp
        }

        override val partialComparator: PartialComparator<DpdFloat> = PartialComparator { a, b ->
            if (a.isNaN() || b.isNaN()) null else comparator.compare(a, b)
        }

        // G[0..4]=11111, G[5]=1 (quiet), no payload, positive sign.
        override val NaN: DpdFloat get() = DpdFloat(0x7E000000)
        // G[0..4]=11110, positive sign.
        override val positiveInfinity: DpdFloat get() = DpdFloat(0x78000000)
        // G[0..4]=11110, negative sign.
        override val negativeInfinity: DpdFloat get() = DpdFloat(Int.MIN_VALUE or 0x78000000)
        // Sign bit clear, combination=0, continuation=0.
        override val positiveZero: DpdFloat get() = DpdFloat(0)
        // Sign bit set (Int.MIN_VALUE = 0x80000000).
        override val negativeZero: DpdFloat get() = DpdFloat(Int.MIN_VALUE)
        // 9,999,999 × 10^90: leadingDigit=9 (large-sig), biasedExp=191, declet1=declet2=encodeDeclet(999)=0x39F.
        // combination = 0x600 | (191 shl 1) | 1 = 0x77F; bits = (0x77F shl 20) | (0x39F shl 10) | 0x39F.
        override val maxValue: DpdFloat get() = DpdFloat(0x77FE7F9F)
        // 1 × 10^(-101): leadingDigit=0, declet1=0, declet2=encodeDeclet(001)=1. biasedExp=0, combination=0.
        override val minValue: DpdFloat get() = DpdFloat(1)
        // 1,000,000 × 10^(-101) = 10^(-95): leadingDigit=1, declet1=declet2=0. biasedExp=0, combination=1.
        override val minNormal: DpdFloat get() = DpdFloat(0x00100000)
        // 1 × 10^(-6): biasedExp=95, leadingDigit=0, declet2=1. combination = (95 shl 3) | 0 = 0x2F8.
        override val epsilon: DpdFloat get() = DpdFloat(0x2F800001)

        override val classification: IeeeFloatingPointClassification<DpdFloat> =
            object : IeeeFloatingPointClassification<DpdFloat> {
                override fun DpdFloat.isNaN(): Boolean = this.isNaN()
                override fun DpdFloat.isInfinite(): Boolean = this.isInfinite()
                override fun DpdFloat.isFinite(): Boolean = !this.isNaN() && !this.isInfinite()
                override fun DpdFloat.isZero(): Boolean = this.isZero()
                override fun DpdFloat.isNormal(): Boolean = this.isNormal()
                override fun DpdFloat.isSubnormal(): Boolean = this.isSubnormal()
            }

        override val sign: FloatingPointSign<DpdFloat> = object : FloatingPointSign<DpdFloat> {
            override fun DpdFloat.isNegative(): Boolean = bits < 0
            override fun DpdFloat.negate(): DpdFloat = DpdFloat(bits xor Int.MIN_VALUE)
            override fun DpdFloat.abs(): DpdFloat = DpdFloat(bits and Int.MAX_VALUE)
            override fun DpdFloat.copySign(other: DpdFloat): DpdFloat =
                DpdFloat((bits and Int.MAX_VALUE) or (other.bits and Int.MIN_VALUE))
        }
    }

    /**
     * The sign bit. `true` for negative values (including negative zero and negative NaN).
     */
    val sign: Boolean
        get() = bits < 0

    /**
     * The 11-bit combination field (IEEE 754-2008 calls this the G field), extracted from bits 30–20.
     *
     * This field uses the same two-case encoding as [BidFloat.combination] to represent both the
     * biased exponent and the leading decimal digit of the significand.
     */
    val combination: Int
        get() = (bits ushr 20) and 0x7FF

    /**
     * The 20-bit continuation field (IEEE 754-2008 calls this the T field), extracted from bits 19–0.
     *
     * In DPD encoding this field holds two 10-bit declets: bits 19–10 ([declet1]) encode the
     * middle three significand digits, and bits 9–0 ([declet2]) encode the low three digits.
     */
    val continuation: Int
        get() = bits and 0xFFFFF

    /**
     * The 8-bit biased exponent. The unbiased exponent is `biasedExponent - 101`.
     *
     * Extracted from [combination] using the same two-case logic as [BidFloat.biasedExponent].
     * Undefined for NaN and infinity; check [isNaN] or [isInfinite] first.
     */
    val biasedExponent: Int
        get() = if ((combination ushr 9) != 3) combination ushr 3
                else (combination ushr 1) and 0xFF

    /**
     * The leading decimal digit (0–9) of the significand, encoded in [combination].
     *
     * - Normal encoding (combination < 0x600): `combination[2:0]`, range 0–7.
     * - Large-significand encoding (top 2 combination bits = 11): `8 + combination[0]`, range 8–9.
     *
     * Undefined for NaN and infinity.
     */
    val leadingDigit: Int
        get() = if ((combination ushr 9) != 3) combination and 0x7
                else 8 or (combination and 0x1)

    /**
     * The upper 10-bit DPD declet, occupying bits 19–10 of [continuation].
     *
     * Encodes the three significand digits in positions 2, 3, 4 (hundred-thousands through thousands).
     * Decode with [decodeDeclet] (private) to obtain the integer value 0–999.
     */
    val declet1: Int
        get() = (continuation ushr 10) and 0x3FF

    /**
     * The lower 10-bit DPD declet, occupying bits 9–0 of [continuation].
     *
     * Encodes the three significand digits in positions 5, 6, 7 (hundreds through units).
     * Decode with [decodeDeclet] (private) to obtain the integer value 0–999.
     */
    val declet2: Int
        get() = continuation and 0x3FF

    /**
     * The integer significand (coefficient), a value in the range 0–9,999,999.
     *
     * Reconstructed by decoding [leadingDigit], [declet1], and [declet2] using the DPD scheme.
     * Undefined for NaN and infinity; check [isNaN] or [isInfinite] first.
     */
    val significand: Int
        get() = leadingDigit * 1_000_000 + decodeDeclet(declet1) * 1_000 + decodeDeclet(declet2)

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

    /**
     * Returns `true` if this value is a normal finite number.
     *
     * A finite non-zero value is normal when `significand × 10^biasedExponent ≥ 10^6` — the
     * significand, scaled to the minimum quantum exponent, has a non-zero leading digit.
     * NaN, infinity, and zero all return `false`.
     */
    fun isNormal(): Boolean = !isNaN() && !isInfinite() && !isZero() && !isSubnormal()

    /**
     * Returns `true` if this value is subnormal.
     *
     * A finite non-zero value is subnormal when `significand × 10^biasedExponent < 10^6`.
     * NaN, infinity, and zero all return `false`.
     */
    fun isSubnormal(): Boolean {
        if (isNaN() || isInfinite() || isZero()) return false
        if (biasedExponent >= 6) return false
        return significand < DECIMAL32_POW10[6 - biasedExponent].toInt()
    }
}
