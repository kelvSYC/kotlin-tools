package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.PartialComparator

/**
 * `BinaryFloatingPoint` is a trait type containing metadata on a type [T] that uses a base-2
 * floating-point representation.
 *
 * This covers both standard IEEE 754 interchange formats (see [IeeeBinaryFloatingPoint]) and
 * compound formats that achieve higher precision by pairing two IEEE values (see
 * [DoubleBinaryFloatingPoint]).
 */
interface BinaryFloatingPoint<T> {
    /**
     * Value equality suitable for numerical comparisons.
     *
     * NaN is not equal to anything, including itself, and positive zero equals negative zero.
     * This follows IEEE 754 §5.11 compareQuietEqual semantics.
     */
    val numericalEquality: ValueEquality<T>

    /**
     * Value equality suitable for use in collections and as an equivalence relation.
     *
     * All NaN payloads are considered equal to each other, and positive zero is not equal to
     * negative zero.
     */
    val equivalenceEquality: ValueEquality<T>

    /**
     * Positive infinity: a value greater than [maxValue].
     */
    val positiveInfinity: T

    /**
     * Negative infinity: a value less than the negation of [maxValue].
     */
    val negativeInfinity: T

    /**
     * Positive zero: the additive identity with a clear sign bit.
     *
     * Both zeros are equal under [numericalEquality] and under IEEE 754 `==`, but they are
     * distinct under [equivalenceEquality] and under the total [comparator] (negative zero
     * compares as strictly less than positive zero).
     */
    val positiveZero: T

    /**
     * Negative zero: the additive identity with the sign bit set.
     *
     * @see positiveZero
     */
    val negativeZero: T

    /**
     * The largest finite value representable in this format.
     */
    val maxValue: T

    /**
     * The smallest positive value representable in this format.
     *
     * This is a subnormal value. For the smallest positive *normal* value see
     * [IeeeBinaryFloatingPoint.minNormal].
     */
    val minValue: T

    /**
     * A canonical quiet NaN value.
     *
     * NaN is unordered and not equal to any value including itself under IEEE 754 numerical
     * equality. There may be other NaN bit patterns; see the concrete type for details.
     */
    val NaN: T

    /**
     * The machine epsilon: the difference between 1.0 and the next larger representable value.
     *
     * This is the relative precision of the format near 1.0.
     */
    val epsilon: T

    /**
     * [Comparator] defining a total ordering over all values, including NaN.
     *
     * Consistent with [Float.compareTo] and [Double.compareTo]: negative zero compares as strictly
     * less than positive zero, and NaN is ordered after all finite values and infinities. Every
     * pair of values produces a defined result — use this for sorting and ordered collections.
     *
     * @see partialComparator
     */
    val comparator: Comparator<T>

    /**
     * [PartialComparator] for values, returning `null` when either operand is NaN, reflecting the
     * IEEE 754 rule that NaN is unordered with respect to every value including itself. Non-NaN
     * values are ordered consistently with [comparator].
     *
     * Use this when NaN-unordered semantics are required, e.g. implementing IEEE 754
     * `compareQuietLess` or propagating the unordered result to the caller.
     *
     * @see comparator
     */
    val partialComparator: PartialComparator<T>

    /**
     * Classification predicates for this format, providing `isNaN`, `isInfinite`, `isFinite`,
     * and `isZero`.
     *
     * IEEE 754 interchange formats narrow this to [IeeeFloatingPointClassification], which adds
     * `isNormal` and `isSubnormal`. Compound formats such as `DoubleDouble` expose only the four
     * base predicates.
     */
    val classification: FloatingPointClassification<T>

    /**
     * IEEE 754-2008 §5.5.1 copy operations for this format: sign-bit manipulation defined for
     * all bit patterns, including NaN, infinity, and both zeros.
     *
     * For compound types the sign is that of the high component; [FloatingPointSign.negate] must
     * flip every component to preserve the canonical pair invariant.
     *
     * @see FloatingPointSign for the distinction between a sign-bit flip and arithmetic negation.
     */
    val sign: FloatingPointSign<T>
}

/**
 * `DoubleBinaryFloatingPoint` is a marker trait for compound binary floating-point types that
 * represent a value as the unevaluated sum of two non-overlapping IEEE values (`high` + `low`).
 *
 * This includes `DoubleDouble` (Double + Double, ~106 significant bits) and a hypothetical
 * `DoubleFloat` (Float + Float, ~46 significant bits). These types provide no fixed exponent
 * field or significand integer, so the IEEE structural metadata in [IeeeBinaryFloatingPoint]
 * does not apply.
 */
interface DoubleBinaryFloatingPoint<T> : BinaryFloatingPoint<T>

/**
 * `IeeeBinaryFloatingPoint` is a trait type that contains metadata on the type [T] relating to
 * standard IEEE 754 binary floating-point numbers.
 *
 * Extends [BinaryFloatingPoint] with the structural layout fields that are specific to IEEE 754
 * interchange formats: bit widths, exponent bias, the significand integer type, and the smallest
 * normal value. Also narrows [classification] to [IeeeFloatingPointClassification], which adds
 * `isNormal` and `isSubnormal`.
 */
interface IeeeBinaryFloatingPoint<T> : BinaryFloatingPoint<T> {
    /**
     * The number of bits used to represent the floating-point number
     */
    val sizeBits: Int

    /**
     * The number of bits used to represent the mantissa (significand without the implicit leading 1 bit).
     */
    val mantissaBits: Int

    /**
     * The number of bits used to represent the exponent.
     */
    val exponentBits: Int

    /**
     * The bias value used for the exponent.
     */
    val exponentBias: Int

    /**
     * The type traits for an unsigned integral type that can hold the significand.
     */
    val significandTraits: UnsignedIntegral<*>

    /**
     * The smallest positive normal value representable in this format.
     *
     * Values smaller than this are subnormals and have reduced precision. Equal to `2^emin`
     * where `emin = 1 - emax` is the minimum normal unbiased exponent.
     */
    val minNormal: T

    /**
     * Classification predicates for this format, providing `isNaN`, `isInfinite`, `isFinite`,
     * `isZero`, `isNormal`, and `isSubnormal`.
     *
     * A value is *normal* when its biased exponent is neither all-zeros nor all-ones — that is,
     * the implicit leading 1 bit is present and the value is neither subnormal, zero, infinite,
     * nor NaN. A value is *subnormal* when the biased exponent is all-zeros and the mantissa is
     * nonzero; such values have reduced precision and no implicit leading bit.
     */
    override val classification: IeeeFloatingPointClassification<T>

    /**
     * Returns `true` if this value is a positive integer power of two (including `2^0 = 1`).
     *
     * A finite positive value is a power of two when its binary significand is exactly 1 — that
     * is, the mantissa bits are all zero for a normal value, or exactly one bit is set for a
     * subnormal (representing a negative power of two below the normal range).  Negative values,
     * zero, NaN, and infinity all return `false`.
     *
     * Powers of two include both large integers (`2, 4, 8, …`) and small fractions
     * (`0.5, 0.25, 0.125, …`).
     */
    fun T.isPowerOfTwo(): Boolean
}

// ── Binary16 (Float16) ────────────────────────────────────────────────────────

/**
 * Trait type containing metadata on standard `binary16` floating-point numbers.
 */
interface Binary16<T> : IeeeBinaryFloatingPoint<T> {
    override val sizeBits: Int get() = 16
    override val mantissaBits: Int get() = 10
    override val exponentBits: Int get() = 5
    override val exponentBias: Int get() = 15
    override val significandTraits: UInt16<UShort>

}

// ── BinaryBFloat16 (BFloat16) ─────────────────────────────────────────────────

/**
 * Trait type containing metadata on `bfloat16` floating-point numbers.
 *
 * `bfloat16` shares its 8-bit exponent (bias 127) with `binary32` but truncates the mantissa to 7 bits,
 * giving the same dynamic range as `Float` at reduced precision.
 */
interface BinaryBFloat16<T> : IeeeBinaryFloatingPoint<T> {
    override val sizeBits: Int get() = 16
    override val mantissaBits: Int get() = 7
    override val exponentBits: Int get() = 8
    override val exponentBias: Int get() = 127
    override val significandTraits: UInt16<UShort>

}

// ── Binary32 (Float) ──────────────────────────────────────────────────────────

/**
 * Trait type containing metadata on standard `binary32` floating-point numbers.
 */
interface Binary32<T> : IeeeBinaryFloatingPoint<T> {
    override val sizeBits: Int get() = 32
    override val mantissaBits: Int get() = 23
    override val exponentBits: Int get() = 8
    override val exponentBias: Int get() = 127
    override val significandTraits: UInt32<UInt>

    companion object : Binary32<Float> {
        override val significandTraits: UInt32<UInt> get() = UInt32

        override val numericalEquality: ValueEquality<Float> = object : ValueEquality<Float> {
            // Statically-typed Float == uses IEEE 754: NaN != NaN, +0 == -0.
            override fun Float.isEqualTo(other: Float): Boolean = this == other
        }

        override val equivalenceEquality: ValueEquality<Float> = object : ValueEquality<Float> {
            // Float.equals uses equivalence semantics: NaN == NaN, +0 != -0.
            override fun Float.isEqualTo(other: Float): Boolean = this.equals(other)
        }

        override val positiveInfinity: Float get() = Float.POSITIVE_INFINITY
        override val negativeInfinity: Float get() = Float.NEGATIVE_INFINITY
        override val positiveZero: Float get() = 0.0f
        override val negativeZero: Float get() = -0.0f
        override val maxValue: Float get() = Float.MAX_VALUE
        override val minValue: Float get() = Float.MIN_VALUE
        override val NaN: Float get() = Float.NaN
        // 2^-126: the smallest positive normal binary32 value (not in Kotlin stdlib).
        override val minNormal: Float get() = Float.fromBits(0x00800000)
        // 2^-23: the gap between 1.0f and the next representable value (= 1 ULP at 1.0).
        override val epsilon: Float get() = Float.fromBits(0x34000000)
        override val comparator: Comparator<Float> get() = Comparator { a, b -> a.compareTo(b) }
        override val partialComparator: PartialComparator<Float>
            get() = PartialComparator { a, b -> if (a.isNaN() || b.isNaN()) null else a.compareTo(b) }

        // Float.isNaN/isInfinite/isFinite are Kotlin stdlib extension functions, not member
        // functions. All predicates here use bit manipulation to avoid the dispatch issue where
        // calling this.isNaN() inside override fun Float.isNaN() would recurse into the override.
        override val classification: IeeeFloatingPointClassification<Float> =
            object : IeeeFloatingPointClassification<Float> {
                override fun Float.isNaN(): Boolean = (toRawBits() and 0x7FFFFFFF) > 0x7F800000
                override fun Float.isInfinite(): Boolean = (toRawBits() and 0x7FFFFFFF) == 0x7F800000
                override fun Float.isFinite(): Boolean = (toRawBits() and 0x7F800000) != 0x7F800000
                // IEEE 754 == treats +0 and -0 as equal; toRawBits clears the sign to test both.
                override fun Float.isZero(): Boolean = (toRawBits() and 0x7FFFFFFF) == 0
                // Biased exponent 1..254 → normal (0 = subnormal/zero, 255 = NaN/infinity).
                override fun Float.isNormal(): Boolean = (toRawBits() ushr 23) and 0xFF in 1..254
                override fun Float.isSubnormal(): Boolean {
                    val b = toRawBits()
                    return (b ushr 23) and 0xFF == 0 && b and 0x7FFFFFFF != 0
                }
                override fun Float.isInteger(): Boolean {
                    val b = toRawBits() and Int.MAX_VALUE  // clear sign bit
                    if (b >= 0x7F800000) return false      // NaN or infinite
                    if (b == 0) return true                 // ±0
                    val biasedExp = b ushr 23
                    if (biasedExp >= 150) return true       // unbiased ≥ 23: no fractional bits
                    if (biasedExp < 127) return false       // |value| < 1: not an integer
                    // 127..149: check that the (150 − biasedExp) fractional bits are all zero.
                    return (b and ((1 shl (150 - biasedExp)) - 1)) == 0
                }
            }

        // b > 0 ensures positive and non-zero. biasedExp == 0 → subnormal: power-of-2 iff the
        // mantissa field itself has exactly one bit set. biasedExp in 1..254 → normal: power-of-2
        // iff all 23 mantissa bits are zero (significand = 1.000…).
        override fun Float.isPowerOfTwo(): Boolean {
            val b = toRawBits()
            if (b <= 0) return false
            val biasedExp = b ushr 23
            if (biasedExp == 255) return false
            if (biasedExp == 0) return (b and (b - 1)) == 0
            return (b and 0x7FFFFF) == 0
        }

        override val sign: FloatingPointSign<Float> = object : FloatingPointSign<Float> {
            // All operations use bit manipulation on the raw Int representation.
            // Float.unaryMinus() is a member operator (no dispatch issue like isNaN etc.).
            override fun Float.isNegative(): Boolean = toRawBits() < 0
            override fun Float.negate(): Float = -this
            // Clear sign bit: Int.MAX_VALUE = 0x7FFFFFFF masks off bit 31.
            override fun Float.abs(): Float = Float.fromBits(toRawBits() and Int.MAX_VALUE)
            override fun Float.copySign(other: Float): Float =
                Float.fromBits((toRawBits() and Int.MAX_VALUE) or (other.toRawBits() and Int.MIN_VALUE))
        }
    }
}

// ── Binary64 (Double) ─────────────────────────────────────────────────────────

/**
 * Trait type containing metadata on standard `binary64` floating-point numbers.
 */
interface Binary64<T> : IeeeBinaryFloatingPoint<T> {
    override val sizeBits: Int get() = 64
    override val mantissaBits: Int get() = 52
    override val exponentBits: Int get() = 11
    override val exponentBias: Int get() = 1023
    override val significandTraits: UInt64<ULong>

    companion object : Binary64<Double> {
        override val significandTraits: UInt64<ULong> get() = UInt64

        override val numericalEquality: ValueEquality<Double> = object : ValueEquality<Double> {
            // Statically-typed Double == uses IEEE 754: NaN != NaN, +0 == -0.
            override fun Double.isEqualTo(other: Double): Boolean = this == other
        }

        override val equivalenceEquality: ValueEquality<Double> = object : ValueEquality<Double> {
            // Double.equals uses equivalence semantics: NaN == NaN, +0 != -0.
            override fun Double.isEqualTo(other: Double): Boolean = this.equals(other)
        }

        override val positiveInfinity: Double get() = Double.POSITIVE_INFINITY
        override val negativeInfinity: Double get() = Double.NEGATIVE_INFINITY
        override val positiveZero: Double get() = 0.0
        override val negativeZero: Double get() = -0.0
        override val maxValue: Double get() = Double.MAX_VALUE
        override val minValue: Double get() = Double.MIN_VALUE
        override val NaN: Double get() = Double.NaN
        // 2^-1022: the smallest positive normal binary64 value (not in Kotlin stdlib).
        override val minNormal: Double get() = Double.fromBits(0x0010000000000000L)
        // 2^-52: the gap between 1.0 and the next representable value (= 1 ULP at 1.0).
        override val epsilon: Double get() = Double.fromBits(0x3CB0000000000000L)
        override val comparator: Comparator<Double> get() = Comparator { a, b -> a.compareTo(b) }
        override val partialComparator: PartialComparator<Double>
            get() = PartialComparator { a, b -> if (a.isNaN() || b.isNaN()) null else a.compareTo(b) }

        // Double.isNaN/isInfinite/isFinite are Kotlin stdlib extension functions; same
        // bit-manipulation approach as Binary32 to avoid the dispatch/recursion issue.
        override val classification: IeeeFloatingPointClassification<Double> =
            object : IeeeFloatingPointClassification<Double> {
                override fun Double.isNaN(): Boolean = (toRawBits() and Long.MAX_VALUE) > 0x7FF0000000000000L
                override fun Double.isInfinite(): Boolean = (toRawBits() and Long.MAX_VALUE) == 0x7FF0000000000000L
                override fun Double.isFinite(): Boolean = (toRawBits() and 0x7FF0000000000000L) != 0x7FF0000000000000L
                // IEEE 754 == treats +0 and -0 as equal; masking the sign bit tests both.
                override fun Double.isZero(): Boolean = (toRawBits() and Long.MAX_VALUE) == 0L
                // Biased exponent 1..2046 → normal (0 = subnormal/zero, 2047 = NaN/infinity).
                override fun Double.isNormal(): Boolean =
                    ((toRawBits() ushr 52) and 0x7FFL).toInt() in 1..2046
                override fun Double.isSubnormal(): Boolean {
                    val b = toRawBits()
                    return (b ushr 52) and 0x7FFL == 0L && b and Long.MAX_VALUE != 0L
                }
                override fun Double.isInteger(): Boolean {
                    val b = toRawBits() and Long.MAX_VALUE   // clear sign bit
                    if (b >= 0x7FF0000000000000L) return false   // NaN or infinite
                    if (b == 0L) return true                      // ±0
                    val biasedExp = (b ushr 52).toInt()
                    if (biasedExp >= 1075) return true    // unbiased ≥ 52: no fractional bits
                    if (biasedExp < 1023) return false    // |value| < 1: not an integer
                    // 1023..1074: check that the (1075 − biasedExp) fractional bits are all zero.
                    return (b and ((1L shl (1075 - biasedExp)) - 1L)) == 0L
                }
            }

        // b > 0 ensures positive and non-zero. biasedExp == 0 → subnormal: power-of-2 iff the
        // mantissa field itself has exactly one bit set. biasedExp in 1..2046 → normal: power-of-2
        // iff all 52 mantissa bits are zero (significand = 1.000…).
        override fun Double.isPowerOfTwo(): Boolean {
            val b = toRawBits()
            if (b <= 0L) return false
            val biasedExp = (b ushr 52).toInt()
            if (biasedExp == 2047) return false
            if (biasedExp == 0) return (b and (b - 1L)) == 0L
            return (b and 0x000FFFFFFFFFFFFFL) == 0L
        }

        override val sign: FloatingPointSign<Double> = object : FloatingPointSign<Double> {
            // All operations use bit manipulation on the raw Long representation.
            override fun Double.isNegative(): Boolean = toRawBits() < 0L
            override fun Double.negate(): Double = -this
            // Clear sign bit: Long.MAX_VALUE = 0x7FFFFFFFFFFFFFFF masks off bit 63.
            override fun Double.abs(): Double = Double.fromBits(toRawBits() and Long.MAX_VALUE)
            override fun Double.copySign(other: Double): Double =
                Double.fromBits((toRawBits() and Long.MAX_VALUE) or (other.toRawBits() and Long.MIN_VALUE))
        }
    }
}
