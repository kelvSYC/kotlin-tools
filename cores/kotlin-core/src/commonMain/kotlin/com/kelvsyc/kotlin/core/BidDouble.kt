package com.kelvsyc.kotlin.core

import com.kelvsyc.kotlin.core.PartialComparator
import com.kelvsyc.kotlin.core.traits.ValueEquality
import com.kelvsyc.kotlin.core.traits.dfp.Bid64
import com.kelvsyc.kotlin.core.traits.dfp.DecimalFloatingPointCohorts
import com.kelvsyc.kotlin.core.traits.dfp.DecimalFloatingPointEncoding
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSign
import com.kelvsyc.kotlin.core.traits.fp.IeeeFloatingPointClassification

// Powers of 10 used when scaling significands for comparison and cohort operations. Index 0..16.
private val DECIMAL64_POW10 = longArrayOf(
    1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L, 10_000_000L, 100_000_000L,
    1_000_000_000L, 10_000_000_000L, 100_000_000_000L, 1_000_000_000_000L,
    10_000_000_000_000L, 100_000_000_000_000L, 1_000_000_000_000_000L, 10_000_000_000_000_000L
)

private fun bidRoundHalfEven(trunc: Long, rem: Long, div: Long): Long {
    val half = div / 2L
    return when {
        rem > half -> trunc + 1L
        rem < half -> trunc
        else -> if (trunc % 2L == 0L) trunc else trunc + 1L
    }
}

/**
 * Compares |a| and |b| where [largerExp] is the significand from the value with the higher biased exponent,
 * [shift] is the non-negative exponent difference, and [other] is the significand of the lower-exponent value.
 *
 * Returns positive if largerExp×10^shift > other, negative if less, zero if equal.
 *
 * Uses ULong to safely detect overflow: if the product exceeds [ULong.MAX_VALUE] the larger-exponent
 * value is necessarily greater (both significands are < 10^16 < 2^64).
 */
private fun compareScaled(largerSig: Long, shift: Int, otherSig: Long): Int {
    val lu = largerSig.toULong()
    val pow = DECIMAL64_POW10[shift].toULong()
    if (lu > ULong.MAX_VALUE / pow) return 1
    return (lu * pow).compareTo(otherSig.toULong())
}

/**
 * Compares two positive finite [BidDouble] values without normalising first.
 *
 * The significand is at most 9,999,999,999,999,999 (< 10^16), so if the biased-exponent difference
 * exceeds 15 the higher-exponent value is always larger. Otherwise both sides are scaled to a common
 * quantum using [DECIMAL64_POW10] and compared via [compareScaled], which uses ULong to avoid overflow.
 */
private fun comparePositiveFinite(a: BidDouble, b: BidDouble): Int {
    val diff = a.biasedExponent - b.biasedExponent
    return when {
        diff > 15  -> 1
        diff < -15 -> -1
        diff >= 0  -> compareScaled(a.significand, diff, b.significand)
        else       -> -compareScaled(b.significand, -diff, a.significand)
    }
}

/**
 * Value representing a `decimal64` floating-point number with the significand in binary integer decimal format.
 *
 * The 64-bit layout follows IEEE 754-2008 §3.5.2:
 * - Bit 63: sign
 * - Bits 62–50: combination field (the G field in the standard)
 * - Bits 49–0: continuation field (the T field in the standard)
 *
 * The combination/G field encodes both the biased exponent and the most significant decimal digit of the
 * significand using a two-case encoding. The continuation/T field holds the remaining 50 bits of the
 * BID-encoded significand.
 *
 * ## Equality and hashing
 *
 * Kotlin reserves `equals` and `hashCode` in value classes for future language use; they cannot be
 * overridden here. The compiler-generated `equals` compares raw [bits], which gives bit-pattern equality:
 * `+0` and `−0` are not equal (correct), but cohort-distinct representations of the same mathematical
 * value are also not equal.
 *
 * Use [BidDouble.equalTo] and [BidDouble.hash] when [Double]-like semantics (NaN-normalised,
 * cohort-unaware bit equality) are needed. When cohort-aware numerical equality is needed, use
 * [Companion.numericalEquality].
 */
@JvmInline
value class BidDouble(val bits: Long) {
    companion object : Bid64<BidDouble> {
        /**
         * Returns `true` if [a] and [b] are equal under equivalence semantics: all NaN values are
         * equal to each other regardless of payload, and all other values are compared by bit pattern
         * (so `+0` and `−0` are not equal, and cohort-distinct representations are not equal).
         *
         * Use this in `equals`/`hashCode` implementations of types that embed a [BidDouble].
         *
         * @see hash
         */
        fun equalTo(a: BidDouble, b: BidDouble): Boolean {
            if (a.isNaN() || b.isNaN()) return a.isNaN() && b.isNaN()
            return a.bits == b.bits
        }

        /**
         * Returns a hash code consistent with [equalTo]: all NaN values produce the same hash,
         * and `+0` and `−0` produce different hashes.
         *
         * @see equalTo
         */
        fun hash(value: BidDouble): Int = if (value.isNaN()) 0x7E000000 else value.bits.hashCode()

        /**
         * Reduces a (biasedExponent, significand) pair to canonical form by stripping trailing
         * decimal zeros from the significand and incrementing the exponent accordingly.
         *
         * Two finite non-zero BidDouble values are numerically equal if and only if they produce the
         * same canonical pair (and have the same sign).
         */
        private fun normalize(biasedExponent: Int, significand: Long): Pair<Int, Long> {
            var exp = biasedExponent
            var sig = significand
            while (sig % 10L == 0L) { sig /= 10L; exp++ }
            return exp to sig
        }

        override val numericalEquality: ValueEquality<BidDouble> = object : ValueEquality<BidDouble> {
            override fun BidDouble.isEqualTo(other: BidDouble): Boolean {
                if (isNaN() || other.isNaN()) return false
                if (isZero() && other.isZero()) return true
                if (sign != other.sign) return false
                val (ea, sa) = normalize(biasedExponent, significand)
                val (eb, sb) = normalize(other.biasedExponent, other.significand)
                return ea == eb && sa == sb
            }
        }

        override val equivalenceEquality: ValueEquality<BidDouble> = object : ValueEquality<BidDouble> {
            override fun BidDouble.isEqualTo(other: BidDouble): Boolean = equalTo(this, other)
        }

        override val comparator: Comparator<BidDouble> = Comparator { a, b ->
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

        override val partialComparator: PartialComparator<BidDouble> = PartialComparator { a, b ->
            if (a.isNaN() || b.isNaN()) null else comparator.compare(a, b)
        }

        // G[0..4]=11111, G[5]=1 (quiet), no payload, positive sign.
        override val NaN: BidDouble get() = BidDouble(0x7E00_0000_0000_0000L)
        // G[0..4]=11110, positive sign.
        override val positiveInfinity: BidDouble get() = BidDouble(0x7800_0000_0000_0000L)
        // G[0..4]=11110, negative sign.
        override val negativeInfinity: BidDouble get() = BidDouble(Long.MIN_VALUE or 0x7800_0000_0000_0000L)
        // Sign bit clear, combination=0, continuation=0 → biasedExponent=0, significand=0, positive.
        override val positiveZero: BidDouble get() = BidDouble(0L)
        // Sign bit set (Long.MIN_VALUE = 0x8000000000000000) → negative zero.
        override val negativeZero: BidDouble get() = BidDouble(Long.MIN_VALUE)
        // 9999999999999999 × 10^369: large-significand encoding, biasedExponent=767, significand=9999999999999999.
        override val maxValue: BidDouble get() = BidDouble(0x77FB_86F2_6FC0_FFFFL)
        // 1 × 10^(-398): biasedExponent=0, significand=1.
        override val minValue: BidDouble get() = BidDouble(1L)
        // 1000000000000000 × 10^(-398) = 10^(-383): biasedExponent=0, significand=10^15.
        override val minNormal: BidDouble get() = BidDouble(1_000_000_000_000_000L)
        // 1 × 10^(-15): biasedExponent=383, significand=1.
        override val epsilon: BidDouble get() = BidDouble(0x2FE0_0000_0000_0001L)

        override val classification: IeeeFloatingPointClassification<BidDouble> =
            object : IeeeFloatingPointClassification<BidDouble> {
                override fun BidDouble.isNaN(): Boolean = this.isNaN()
                override fun BidDouble.isInfinite(): Boolean = this.isInfinite()
                override fun BidDouble.isFinite(): Boolean = !this.isNaN() && !this.isInfinite()
                override fun BidDouble.isZero(): Boolean = this.isZero()
                override fun BidDouble.isNormal(): Boolean = this.isNormal()
                override fun BidDouble.isSubnormal(): Boolean = this.isSubnormal()
                override fun BidDouble.isInteger(): Boolean = this.isInteger()
            }

        override fun BidDouble.isPowerOfTen(): Boolean {
            if (isNaN() || isInfinite() || isZero() || sign) return false
            var sig = significand
            while (sig % 10L == 0L) sig /= 10L
            return sig == 1L
        }

        override val sign: FloatingPointSign<BidDouble> = object : FloatingPointSign<BidDouble> {
            override fun BidDouble.isNegative(): Boolean = bits < 0
            override fun BidDouble.negate(): BidDouble = BidDouble(bits xor Long.MIN_VALUE)
            override fun BidDouble.abs(): BidDouble = BidDouble(bits and Long.MAX_VALUE)
            override fun BidDouble.copySign(other: BidDouble): BidDouble =
                BidDouble((bits and Long.MAX_VALUE) or (other.bits and Long.MIN_VALUE))
        }

        override val encoding: DecimalFloatingPointEncoding<BidDouble> = object : DecimalFloatingPointEncoding<BidDouble> {
            // Canonical quiet NaN: combination == 0x1F80 (G[0..4]=11111, G[5]=1, rest 0) and continuation == 0.
            override fun BidDouble.isCanonical(): Boolean {
                if (isNaN()) return combination == 0x1F80 && continuation == 0L
                if (isInfinite()) return true
                // Large-significand encoding: significand = 2^53 | low51; non-canonical if > 9,999,999,999,999,999.
                return (combination ushr 11) != 3 || significand <= 9_999_999_999_999_999L
            }

            override fun BidDouble.canonical(): BidDouble {
                if (isCanonical()) return this
                val signBit = if (sign) Long.MIN_VALUE else 0L
                if (isNaN()) return BidDouble(signBit or 0x7E00_0000_0000_0000L)
                return BidDouble(signBit)
            }

            // G[5] = combination bit 7 (13-bit combination, MSB = bit 12). Quiet NaN: G[5] = 1.
            override fun BidDouble.isQuietNaN(): Boolean = isNaN() && (combination and 0x80) != 0
            override fun BidDouble.isSignalingNaN(): Boolean = isNaN() && (combination and 0x80) == 0
        }

        override val cohorts: DecimalFloatingPointCohorts<BidDouble> = object : DecimalFloatingPointCohorts<BidDouble> {
            override fun BidDouble.reduce(): BidDouble {
                if (isNaN() || isInfinite()) return this
                val signBit = if (sign) Long.MIN_VALUE else 0L
                // Zero: preferred exponent 0 (biasedExp = 398), per IEEE 754-2008 §5.3.3.
                if (isZero()) return BidDouble(signBit or bidDouble64Pack(398, 0L))
                var sig = significand
                var biasedExp = biasedExponent
                while (sig % 10L == 0L && biasedExp < 767) { sig /= 10L; biasedExp++ }
                return BidDouble(signBit or bidDouble64Pack(biasedExp, sig))
            }

            override fun BidDouble.quantum(): BidDouble {
                if (isNaN() || isInfinite()) return this
                val signBit = if (sign) Long.MIN_VALUE else 0L
                return BidDouble(signBit or bidDouble64Pack(biasedExponent, 1L))
            }

            override fun BidDouble.quantize(quantum: BidDouble): BidDouble {
                if (isNaN() || quantum.isNaN() || quantum.isInfinite() || isInfinite()) return NaN
                val signBit = if (sign) Long.MIN_VALUE else 0L
                val targetExp = quantum.biasedExponent
                if (isZero()) return BidDouble(signBit or bidDouble64Pack(targetExp, 0L))

                val expDiff = targetExp - biasedExponent
                var sig = significand

                when {
                    expDiff == 0 -> { /* no scaling */ }
                    expDiff > 0 -> {
                        if (expDiff > 16) {
                            sig = 0L
                        } else {
                            val divisor = DECIMAL64_POW10[expDiff]
                            sig = bidRoundHalfEven(sig / divisor, sig % divisor, divisor)
                        }
                    }
                    else -> {
                        val scale = -expDiff
                        if (scale > 16) return NaN
                        sig *= DECIMAL64_POW10[scale]
                        if (sig > 9_999_999_999_999_999L) return NaN
                    }
                }

                if (sig == 0L) return BidDouble(signBit or bidDouble64Pack(targetExp, 0L))
                if (targetExp > 767 || targetExp < 0) return NaN
                return BidDouble(signBit or bidDouble64Pack(targetExp, sig))
            }
        }
    }

    /**
     * The sign bit. `true` for negative values (including negative zero and negative NaN).
     */
    val sign: Boolean
        get() = bits < 0

    /**
     * The 13-bit combination field (IEEE 754-2008 calls this the G field), extracted from bits 62–50.
     *
     * This field encodes both the biased exponent and the leading decimal digit of the significand
     * using a two-case encoding based on the top two bits G[0..1]:
     *
     * - G[0..1] ≠ 11 (combination < 0x1800): exponent occupies G[0..9] (bits 62–53); the top 3
     *   significand bits occupy G[10..12] (bits 52–50). Significand range: 0–9,007,199,254,740,991.
     * - G[0..1] = 11, G[2] = 0 (combination in 0x1800..0x1BFF): "large significand" encoding.
     *   Exponent occupies G[3..12] (bits 59–50); significand = 2^53 + bits[50..0]. Range up to 9,999,999,999,999,999.
     * - G[0..4] = 11110: infinity.
     * - G[0..4] = 11111: NaN.
     */
    val combination: Int
        get() = (bits ushr 50).toInt() and 0x1FFF

    /**
     * The 50-bit continuation field (IEEE 754-2008 calls this the T field), extracted from bits 49–0.
     *
     * In BID encoding (used here), this holds the low 50 bits of the integer significand.
     * The high bits of the significand are recovered from [combination]; see [significand].
     */
    val continuation: Long
        get() = bits and 0x3_FFFF_FFFF_FFFFL

    /**
     * The 10-bit biased exponent. The unbiased exponent is `biasedExponent - 398`.
     *
     * The bias of 398 comes from IEEE 754-2008 Table 3.6 (Emax = 384, p = 16: bias = Emax + p − 2 = 398).
     * Valid biased exponents are 0–767, representing unbiased exponents −398 to +369.
     *
     * The bit position of the exponent within [combination] depends on the encoding case:
     * - Normal (combination < 0x1800): exponent = combination[12..3] — the top 10 of the 13 combination bits.
     * - Large-significand (top 2 bits = 11): exponent = combination[10..1] — the middle 10 combination bits.
     *
     * Undefined for NaN and infinity; check [isNaN] or [isInfinite] first.
     */
    val biasedExponent: Int
        get() = if ((combination ushr 11) != 3) combination ushr 3
                else (combination ushr 1) and 0x3FF

    /**
     * The integer significand (coefficient), a value in the range 0–9,999,999,999,999,999.
     *
     * In BID encoding the entire 16-digit significand is stored as a single binary integer, split
     * across [combination] and [continuation]:
     *
     * - Normal encoding (combination < 0x1800): significand = G[10..12] (3 bits) concatenated with
     *   T (50 bits) = 53-bit integer, range 0–9,007,199,254,740,991.
     * - Large-significand encoding (top 2 combination bits = 11, bit 10 = 0): significand = 2^53 +
     *   bits[50..0]. This covers 9,007,199,254,740,992–9,999,999,999,999,999.
     *
     * Undefined for NaN and infinity; check [isNaN] or [isInfinite] first.
     */
    val significand: Long
        get() = if ((combination ushr 11) != 3) {
            ((combination and 0x7).toLong() shl 50) or continuation
        } else {
            0x20_0000_0000_0000L or (bits and 0x7_FFFF_FFFF_FFFFL)
        }

    /**
     * Returns `true` if this value is a NaN (Not a Number).
     *
     * Indicated by G[0..4] = 11111 — the top 5 bits of the combination field are all set.
     * Both quiet NaN (G[5] = 1) and signalling NaN (G[5] = 0) return `true`.
     */
    fun isNaN(): Boolean = (combination ushr 8) and 0x1F == 0x1F

    /**
     * Returns `true` if this value is positive or negative infinity.
     *
     * Indicated by G[0..4] = 11110 — the top 5 bits of the combination field are 11110.
     */
    fun isInfinite(): Boolean = (combination ushr 8) and 0x1F == 0x1E

    /**
     * Returns `true` if this value is finite (not NaN, not infinite) and has a zero significand.
     *
     * Both +0 and −0 return `true`. Use [sign] to distinguish them.
     */
    fun isZero(): Boolean = !isNaN() && !isInfinite() && significand == 0L

    /**
     * Returns `true` if this value is a normal finite number.
     *
     * Per IEEE 754-2008, a finite non-zero value is normal when its significand, scaled to the minimum
     * quantum exponent, has a non-zero leading digit — equivalently `significand × 10^biasedExponent ≥ 10^15`.
     * NaN, infinity, and zero all return `false`.
     */
    fun isNormal(): Boolean = !isNaN() && !isInfinite() && !isZero() && !isSubnormal()

    /**
     * Returns `true` if this value is subnormal.
     *
     * A finite non-zero value is subnormal when its significand, scaled to the minimum quantum exponent,
     * has a zero leading digit — equivalently `significand × 10^biasedExponent < 10^15`. For
     * `biasedExponent ≥ 15`, the scaled significand is always at least 1, so such values are always normal.
     *
     * NaN, infinity, and zero all return `false`.
     */
    fun isSubnormal(): Boolean {
        if (isNaN() || isInfinite() || isZero()) return false
        if (biasedExponent >= 15) return false
        return significand < DECIMAL64_POW10[15 - biasedExponent]
    }

    /**
     * Returns `true` if this value represents a mathematical integer (including zero).
     *
     * The value is `significand × 10^(biasedExponent − 398)`. When the unbiased exponent is
     * non-negative the value is trivially an integer. When negative, the value is an integer iff
     * the last `−(biasedExponent − 398)` decimal digits of the significand are all zero. NaN and
     * infinity return `false`.
     */
    fun isInteger(): Boolean {
        if (isNaN() || isInfinite()) return false
        if (isZero() || biasedExponent >= 398) return true
        val fracExp = 398 - biasedExponent           // number of required trailing zeros
        if (fracExp > 15) return false               // significand < 10^16, can't have ≥ 16 trailing zeros
        return significand % DECIMAL64_POW10[fracExp] == 0L
    }
}
