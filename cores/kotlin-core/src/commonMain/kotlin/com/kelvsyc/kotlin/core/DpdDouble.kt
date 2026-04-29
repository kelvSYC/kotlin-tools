package com.kelvsyc.kotlin.core

import com.kelvsyc.kotlin.core.PartialComparator
import com.kelvsyc.kotlin.core.fp.encodeDeclet
import com.kelvsyc.kotlin.core.fp.packDpd64
import com.kelvsyc.kotlin.core.traits.DecimalFloatingPointCohorts
import com.kelvsyc.kotlin.core.traits.DecimalFloatingPointEncoding
import com.kelvsyc.kotlin.core.traits.Dpd64
import com.kelvsyc.kotlin.core.traits.FloatingPointSign
import com.kelvsyc.kotlin.core.traits.IeeeFloatingPointClassification
import com.kelvsyc.kotlin.core.traits.ValueEquality

// Powers of 10 indexed by exponent (0..16), used when comparing and scaling significands.
private val DECIMAL64_POW10 = longArrayOf(
    1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L, 10_000_000L, 100_000_000L,
    1_000_000_000L, 10_000_000_000L, 100_000_000_000L, 1_000_000_000_000L,
    10_000_000_000_000L, 100_000_000_000_000L, 1_000_000_000_000_000L, 10_000_000_000_000_000L
)

/**
 * Decodes a 10-bit DPD declet into a 3-digit decimal value (0–999).
 *
 * Implements the five encoding cases from IEEE 754-2008 Table 3.4. Identical to the decode used
 * in [DpdFloat]; extracted here to avoid a cross-file private function dependency.
 */
private fun decodeDeclet(d: Int): Int {
    val b = d and 0x3FF
    return when {
        b and 0x8 == 0 -> ((b ushr 7) and 0x7) * 100 + ((b ushr 4) and 0x7) * 10 + (b and 0x7)
        b and 0xE == 0x8 -> ((b ushr 7) and 0x7) * 100 + ((b ushr 4) and 0x7) * 10 + 8 + (b and 0x1)
        b and 0xE == 0xA -> {
            val d1 = (b ushr 7) and 0x7
            val d2 = 8 + ((b ushr 4) and 0x1)
            val d3 = ((b ushr 4) and 0x6) or (b and 0x1)
            d1 * 100 + d2 * 10 + d3
        }
        b and 0xE == 0xC -> {
            val d1 = 8 + ((b ushr 7) and 0x1)
            val d2 = (b ushr 4) and 0x7
            val d3 = ((b ushr 7) and 0x6) or (b and 0x1)
            d1 * 100 + d2 * 10 + d3
        }
        else -> when ((b ushr 8) and 0x3) {
            0 -> {
                val d1 = 8 + ((b ushr 7) and 0x1)
                val d2 = 8 + ((b ushr 4) and 0x1)
                val d3 = ((b ushr 4) and 0x6) or (b and 0x1)
                d1 * 100 + d2 * 10 + d3
            }
            1 -> {
                val d1 = 8 + ((b ushr 7) and 0x1)
                val d2 = (b ushr 4) and 0x7
                val d3 = 8 + (b and 0x1)
                d1 * 100 + d2 * 10 + d3
            }
            2 -> {
                val d1 = (b ushr 5) and 0x7
                val d2 = 8 + ((b ushr 4) and 0x1)
                val d3 = 8 + (b and 0x1)
                d1 * 100 + d2 * 10 + d3
            }
            else -> {
                val d1 = 8 + ((b ushr 7) and 0x1)
                val d2 = 8 + ((b ushr 4) and 0x1)
                val d3 = 8 + (b and 0x1)
                d1 * 100 + d2 * 10 + d3
            }
        }
    }
}

/**
 * Returns positive if |largerSig × 10^shift| > |otherSig|, using ULong to avoid overflow.
 *
 * Both significands are < 10^16. If the product exceeds [ULong.MAX_VALUE] the larger-exponent
 * value is necessarily greater.
 */
private fun compareScaled(largerSig: Long, shift: Int, otherSig: Long): Int {
    val lu = largerSig.toULong()
    val pow = DECIMAL64_POW10[shift].toULong()
    if (lu > ULong.MAX_VALUE / pow) return 1
    return (lu * pow).compareTo(otherSig.toULong())
}

/**
 * Compares two positive finite [DpdDouble] values without normalising first.
 */
private fun comparePositiveFinite(a: DpdDouble, b: DpdDouble): Int {
    val diff = a.biasedExponent - b.biasedExponent
    return when {
        diff > 15  -> 1
        diff < -15 -> -1
        diff >= 0  -> compareScaled(a.significand, diff, b.significand)
        else       -> -compareScaled(b.significand, -diff, a.significand)
    }
}

/**
 * Value representing a `decimal64` floating-point number with the significand in densely packed decimal format.
 *
 * The 64-bit layout follows IEEE 754-2008 §3.5.2:
 * - Bit 63: sign
 * - Bits 62–50: combination field (the G field in the standard)
 * - Bits 49–0: continuation field (the T field in the standard)
 *
 * The continuation field holds five 10-bit *declets* (bits 49–40, 39–30, 29–20, 19–10, and 9–0),
 * each encoding three decimal digits using the densely packed decimal scheme from IEEE 754-2008 Table 3.4.
 *
 * ## Equality and hashing
 *
 * See [BidDouble] for the general discussion of decimal cohort equality. Use [DpdDouble.equalTo]
 * and [DpdDouble.hash] for [Double]-like semantics, or [Companion.numericalEquality] for cohort-aware
 * numerical equality.
 */
@JvmInline
value class DpdDouble(val bits: Long) {
    companion object : Dpd64<DpdDouble> {
        /**
         * Returns `true` if [a] and [b] are equal under equivalence semantics: all NaN values are
         * equal to each other regardless of payload, and all other values are compared by bit pattern.
         *
         * @see hash
         */
        fun equalTo(a: DpdDouble, b: DpdDouble): Boolean {
            if (a.isNaN() || b.isNaN()) return a.isNaN() && b.isNaN()
            return a.bits == b.bits
        }

        /**
         * Returns a hash code consistent with [equalTo]: all NaN values produce the same hash.
         *
         * @see equalTo
         */
        fun hash(value: DpdDouble): Int = if (value.isNaN()) 0x7E000000 else value.bits.hashCode()

        /**
         * Reduces a (biasedExponent, significand) pair to canonical form by stripping trailing
         * decimal zeros from the significand and incrementing the exponent accordingly.
         */
        private fun normalize(biasedExponent: Int, significand: Long): Pair<Int, Long> {
            var exp = biasedExponent
            var sig = significand
            while (sig % 10L == 0L) { sig /= 10L; exp++ }
            return exp to sig
        }

        private fun dpdRepack(sign: Boolean, biasedExp: Int, sig: Long): DpdDouble {
            val signBit = if (sign) Long.MIN_VALUE else 0L
            val ld = (sig / 1_000_000_000_000_000L).toInt()
            var rem = sig % 1_000_000_000_000_000L
            val d1 = encodeDeclet((rem / 1_000_000_000_000L).toInt()); rem %= 1_000_000_000_000L
            val d2 = encodeDeclet((rem / 1_000_000_000L).toInt()); rem %= 1_000_000_000L
            val d3 = encodeDeclet((rem / 1_000_000L).toInt()); rem %= 1_000_000L
            val d4 = encodeDeclet((rem / 1_000L).toInt()); rem %= 1_000L
            val d5 = encodeDeclet(rem.toInt())
            return DpdDouble(signBit or packDpd64(biasedExp, ld, d1, d2, d3, d4, d5))
        }

        override val numericalEquality: ValueEquality<DpdDouble> = object : ValueEquality<DpdDouble> {
            override fun DpdDouble.isEqualTo(other: DpdDouble): Boolean {
                if (isNaN() || other.isNaN()) return false
                if (isZero() && other.isZero()) return true
                if (sign != other.sign) return false
                val (ea, sa) = normalize(biasedExponent, significand)
                val (eb, sb) = normalize(other.biasedExponent, other.significand)
                return ea == eb && sa == sb
            }
        }

        override val equivalenceEquality: ValueEquality<DpdDouble> = object : ValueEquality<DpdDouble> {
            override fun DpdDouble.isEqualTo(other: DpdDouble): Boolean = equalTo(this, other)
        }

        override val comparator: Comparator<DpdDouble> = Comparator { a, b ->
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

        override val partialComparator: PartialComparator<DpdDouble> = PartialComparator { a, b ->
            if (a.isNaN() || b.isNaN()) null else comparator.compare(a, b)
        }

        // G[0..4]=11111, G[5]=1 (quiet), no payload, positive sign.
        override val NaN: DpdDouble get() = DpdDouble(0x7E00_0000_0000_0000L)
        // G[0..4]=11110, positive sign.
        override val positiveInfinity: DpdDouble get() = DpdDouble(0x7800_0000_0000_0000L)
        // G[0..4]=11110, negative sign.
        override val negativeInfinity: DpdDouble get() = DpdDouble(Long.MIN_VALUE or 0x7800_0000_0000_0000L)
        // Sign bit clear, combination=0, continuation=0.
        override val positiveZero: DpdDouble get() = DpdDouble(0L)
        // Sign bit set (Long.MIN_VALUE = 0x8000000000000000).
        override val negativeZero: DpdDouble get() = DpdDouble(Long.MIN_VALUE)
        // 9,999,999,999,999,999 × 10^369: leadingDigit=9 (large-sig), biasedExp=767, all five declets=encodeDeclet(999)=0x39F.
        // combination = 0x1800 | (767 shl 1) | 1 = 0x1DFF; bits = packDpd64(767, 9, 0x39F, 0x39F, 0x39F, 0x39F, 0x39F).
        override val maxValue: DpdDouble get() = DpdDouble(0x77FF_9FE7_F9FE_7F9FL)
        // 1 × 10^(-398): leadingDigit=0, declets=(0,0,0,0,1). biasedExp=0, combination=0.
        override val minValue: DpdDouble get() = DpdDouble(1L)
        // 10^15 × 10^(-398) = 10^(-383): leadingDigit=1, declets=(0,0,0,0,0). biasedExp=0, combination=1.
        // bits = (1L shl 50) = 0x0004_0000_0000_0000L.
        override val minNormal: DpdDouble get() = DpdDouble(0x0004_0000_0000_0000L)
        // 1 × 10^(-15): biasedExp=383, leadingDigit=0, declets=(0,0,0,0,1). combination=0xBF8.
        override val epsilon: DpdDouble get() = DpdDouble(0x2FE0_0000_0000_0001L)

        override val classification: IeeeFloatingPointClassification<DpdDouble> =
            object : IeeeFloatingPointClassification<DpdDouble> {
                override fun DpdDouble.isNaN(): Boolean = this.isNaN()
                override fun DpdDouble.isInfinite(): Boolean = this.isInfinite()
                override fun DpdDouble.isFinite(): Boolean = !this.isNaN() && !this.isInfinite()
                override fun DpdDouble.isZero(): Boolean = this.isZero()
                override fun DpdDouble.isNormal(): Boolean = this.isNormal()
                override fun DpdDouble.isSubnormal(): Boolean = this.isSubnormal()
            }

        override val sign: FloatingPointSign<DpdDouble> = object : FloatingPointSign<DpdDouble> {
            override fun DpdDouble.isNegative(): Boolean = bits < 0
            override fun DpdDouble.negate(): DpdDouble = DpdDouble(bits xor Long.MIN_VALUE)
            override fun DpdDouble.abs(): DpdDouble = DpdDouble(bits and Long.MAX_VALUE)
            override fun DpdDouble.copySign(other: DpdDouble): DpdDouble =
                DpdDouble((bits and Long.MAX_VALUE) or (other.bits and Long.MIN_VALUE))
        }

        override val encoding: DecimalFloatingPointEncoding<DpdDouble> = object : DecimalFloatingPointEncoding<DpdDouble> {
            // A declet is non-canonical when b[3:1]=111, b[9:8]=11, and b[6:5]!=00 (24 undefined patterns).
            private fun isNonCanonicalDeclet(d: Int): Boolean =
                (d and 0xE) == 0xE && (d and 0x300) == 0x300 && (d and 0x60) != 0

            override fun DpdDouble.isCanonical(): Boolean {
                if (isNaN()) return combination == 0x1F80 && continuation == 0L
                if (isInfinite()) return true
                return !isNonCanonicalDeclet(declet1) &&
                    !isNonCanonicalDeclet(declet2) &&
                    !isNonCanonicalDeclet(declet3) &&
                    !isNonCanonicalDeclet(declet4) &&
                    !isNonCanonicalDeclet(declet5)
            }

            override fun DpdDouble.canonical(): DpdDouble {
                if (isCanonical()) return this
                val signBit = if (sign) Long.MIN_VALUE else 0L
                if (isNaN()) return DpdDouble(signBit or 0x7E00_0000_0000_0000L)
                return DpdDouble(signBit)
            }

            // G[5] = combination bit 7. Quiet NaN: G[5] = 1.
            override fun DpdDouble.isQuietNaN(): Boolean = isNaN() && (combination and 0x80) != 0
            override fun DpdDouble.isSignalingNaN(): Boolean = isNaN() && (combination and 0x80) == 0
        }

        override val cohorts: DecimalFloatingPointCohorts<DpdDouble> = object : DecimalFloatingPointCohorts<DpdDouble> {
            override fun DpdDouble.reduce(): DpdDouble {
                if (isNaN() || isInfinite()) return this
                // Zero: preferred exponent 0 (biasedExp = 398), per IEEE 754-2008 §5.3.3.
                if (isZero()) return dpdRepack(sign, 398, 0L)
                var sig = significand
                var biasedExp = biasedExponent
                while (sig % 10L == 0L && biasedExp < 767) { sig /= 10L; biasedExp++ }
                return dpdRepack(sign, biasedExp, sig)
            }

            override fun DpdDouble.quantum(): DpdDouble {
                if (isNaN() || isInfinite()) return this
                return dpdRepack(sign, biasedExponent, 1L)
            }

            override fun DpdDouble.quantize(quantum: DpdDouble): DpdDouble {
                if (isNaN() || quantum.isNaN() || quantum.isInfinite() || isInfinite()) return NaN
                val targetExp = quantum.biasedExponent
                if (isZero()) return dpdRepack(sign, targetExp, 0L)

                val expDiff = targetExp - biasedExponent
                var sig = significand

                when {
                    expDiff == 0 -> { /* no scaling */ }
                    expDiff > 0 -> {
                        if (expDiff > 16) {
                            sig = 0L
                        } else {
                            val divisor = DECIMAL64_POW10[expDiff]
                            val half = divisor / 2L
                            val trunc = sig / divisor
                            val rem = sig % divisor
                            sig = when {
                                rem > half -> trunc + 1L
                                rem < half -> trunc
                                else -> if (trunc % 2L == 0L) trunc else trunc + 1L
                            }
                        }
                    }
                    else -> {
                        val scale = -expDiff
                        if (scale > 16) return NaN
                        sig *= DECIMAL64_POW10[scale]
                        if (sig > 9_999_999_999_999_999L) return NaN
                    }
                }

                if (sig == 0L) return dpdRepack(sign, targetExp, 0L)
                if (targetExp > 767 || targetExp < 0) return NaN
                return dpdRepack(sign, targetExp, sig)
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
     * This field uses the same two-case encoding as [BidDouble.combination] to represent both the
     * biased exponent and the leading decimal digit of the significand.
     */
    val combination: Int
        get() = (bits ushr 50).toInt() and 0x1FFF

    /**
     * The 50-bit continuation field (IEEE 754-2008 calls this the T field), extracted from bits 49–0.
     *
     * In DPD encoding this field holds five 10-bit declets: bits 49–40 ([declet1]) through bits 9–0
     * ([declet5]), each encoding three decimal digits.
     */
    val continuation: Long
        get() = bits and 0x3_FFFF_FFFF_FFFFL

    /**
     * The 10-bit biased exponent. The unbiased exponent is `biasedExponent - 398`.
     *
     * Extracted from [combination] using the same two-case logic as [BidDouble.biasedExponent].
     * Undefined for NaN and infinity; check [isNaN] or [isInfinite] first.
     */
    val biasedExponent: Int
        get() = if ((combination ushr 11) != 3) combination ushr 3
                else (combination ushr 1) and 0x3FF

    /**
     * The leading decimal digit (0–9) of the significand, encoded in [combination].
     *
     * - Normal encoding (combination < 0x1800): `combination[2:0]`, range 0–7.
     * - Large-significand encoding (top 2 combination bits = 11): `8 + combination[0]`, range 8–9.
     *
     * Undefined for NaN and infinity.
     */
    val leadingDigit: Int
        get() = if ((combination ushr 11) != 3) combination and 0x7
                else 8 or (combination and 0x1)

    /** The first 10-bit DPD declet, occupying bits 49–40, encoding significand digits 2–4. */
    val declet1: Int get() = ((continuation ushr 40) and 0x3FF).toInt()

    /** The second 10-bit DPD declet, occupying bits 39–30, encoding significand digits 5–7. */
    val declet2: Int get() = ((continuation ushr 30) and 0x3FF).toInt()

    /** The third 10-bit DPD declet, occupying bits 29–20, encoding significand digits 8–10. */
    val declet3: Int get() = ((continuation ushr 20) and 0x3FF).toInt()

    /** The fourth 10-bit DPD declet, occupying bits 19–10, encoding significand digits 11–13. */
    val declet4: Int get() = ((continuation ushr 10) and 0x3FF).toInt()

    /** The fifth 10-bit DPD declet, occupying bits 9–0, encoding significand digits 14–16. */
    val declet5: Int get() = (continuation and 0x3FF).toInt()

    /**
     * The integer significand (coefficient), a value in the range 0–9,999,999,999,999,999.
     *
     * Reconstructed by decoding [leadingDigit] and the five declets using the DPD scheme.
     * Undefined for NaN and infinity; check [isNaN] or [isInfinite] first.
     */
    val significand: Long
        get() = leadingDigit.toLong() * 1_000_000_000_000_000L +
            decodeDeclet(declet1).toLong() * 1_000_000_000_000L +
            decodeDeclet(declet2).toLong() * 1_000_000_000L +
            decodeDeclet(declet3).toLong() * 1_000_000L +
            decodeDeclet(declet4).toLong() * 1_000L +
            decodeDeclet(declet5).toLong()

    /**
     * Returns `true` if this value is a NaN (Not a Number).
     *
     * Indicated by G[0..4] = 11111 — the top 5 bits of the combination field are all set.
     */
    fun isNaN(): Boolean = (combination ushr 8) and 0x1F == 0x1F

    /**
     * Returns `true` if this value is positive or negative infinity.
     *
     * Indicated by G[0..4] = 11110 — the top 5 bits of the combination field are 11110.
     */
    fun isInfinite(): Boolean = (combination ushr 8) and 0x1F == 0x1E

    /**
     * Returns `true` if this value is finite and has a zero significand.
     *
     * Both +0 and −0 return `true`. Use [sign] to distinguish them.
     */
    fun isZero(): Boolean = !isNaN() && !isInfinite() && significand == 0L

    /**
     * Returns `true` if this value is a normal finite number.
     *
     * A finite non-zero value is normal when its significand, scaled to the minimum quantum exponent,
     * has a non-zero leading digit — equivalently `significand × 10^biasedExponent ≥ 10^15`.
     * NaN, infinity, and zero all return `false`.
     */
    fun isNormal(): Boolean = !isNaN() && !isInfinite() && !isZero() && !isSubnormal()

    /**
     * Returns `true` if this value is subnormal.
     *
     * A finite non-zero value is subnormal when `significand × 10^biasedExponent < 10^15`.
     * For `biasedExponent ≥ 15` the value is always normal (if finite and non-zero).
     * NaN, infinity, and zero all return `false`.
     */
    fun isSubnormal(): Boolean {
        if (isNaN() || isInfinite() || isZero()) return false
        if (biasedExponent >= 15) return false
        return significand < DECIMAL64_POW10[15 - biasedExponent]
    }
}
