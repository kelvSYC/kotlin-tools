package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import com.kelvsyc.kotlin.core.PartialComparator

/**
 * `BinaryFloatingPoint` is a trait type that contains metadata on the type [T] relating to standard binary
 * floating-point numbers.
 */
interface BinaryFloatingPoint<T> {
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
     * negative zero. This follows [Float.equals] semantics.
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
     * The largest finite value representable in this format.
     *
     * For a format with mantissa bits `p` and maximum unbiased exponent `emax`, this is
     * `(2 - 2^(-p)) × 2^emax`.
     */
    val maxValue: T

    /**
     * The smallest positive value representable in this format.
     *
     * This is a subnormal value. For the smallest positive *normal* value see [minNormal].
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
     * The smallest positive normal value representable in this format.
     *
     * Values smaller than this are subnormals and have reduced precision. Equal to `2^emin`
     * where `emin = 1 - emax` is the minimum normal unbiased exponent.
     */
    val minNormal: T

    /**
     * The machine epsilon: the difference between 1.0 and the next larger representable value.
     *
     * Equal to `2^(1-p)` where `p` is the number of significand bits including the implicit
     * leading 1. This is the relative precision of the format near 1.0.
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
}

/**
 * Trait type containing metadata on standard `binary16` floating-point numbers.
 */
interface Binary16<T> : BinaryFloatingPoint<T> {
    override val sizeBits: Int get() = 16
    override val mantissaBits: Int get() = 10
    override val exponentBits: Int get() = 5
    override val exponentBias: Int get() = 15
    override val significandTraits: UInt16<UShort>

    companion object : Binary16<Float16> {
        override val significandTraits: UInt16<UShort> get() = UInt16

        override val numericalEquality: ValueEquality<Float16> = object : ValueEquality<Float16> {
            // Float16 has no statically-typed ==, so widen to Float where IEEE 754 == applies.
            override fun Float16.isEqualTo(other: Float16): Boolean = toFloat() == other.toFloat()
        }

        override val equivalenceEquality: ValueEquality<Float16> = object : ValueEquality<Float16> {
            override fun Float16.isEqualTo(other: Float16): Boolean = Float16.equalTo(this, other)
        }

        override val positiveInfinity: Float16 get() = Float16.POSITIVE_INFINITY
        override val negativeInfinity: Float16 get() = Float16.NEGATIVE_INFINITY
        override val maxValue: Float16 get() = Float16.MAX_VALUE
        override val minValue: Float16 get() = Float16.MIN_VALUE
        override val NaN: Float16 get() = Float16.NaN
        override val minNormal: Float16 get() = Float16.MIN_NORMAL
        override val epsilon: Float16 get() = Float16.EPSILON
        override val comparator: Comparator<Float16> get() = Float16.comparator
        override val partialComparator: PartialComparator<Float16> get() = Float16.partialComparator
    }
}

/**
 * Trait type containing metadata on `bfloat16` floating-point numbers.
 *
 * `bfloat16` shares its 8-bit exponent (bias 127) with `binary32` but truncates the mantissa to 7 bits,
 * giving the same dynamic range as `Float` at reduced precision.
 */
interface BinaryBFloat16<T> : BinaryFloatingPoint<T> {
    override val sizeBits: Int get() = 16
    override val mantissaBits: Int get() = 7
    override val exponentBits: Int get() = 8
    override val exponentBias: Int get() = 127
    override val significandTraits: UInt16<UShort>

    companion object : BinaryBFloat16<BFloat16> {
        override val significandTraits: UInt16<UShort> get() = UInt16

        override val numericalEquality: ValueEquality<BFloat16> = object : ValueEquality<BFloat16> {
            // BFloat16 has no statically-typed ==, so widen to Float where IEEE 754 == applies.
            override fun BFloat16.isEqualTo(other: BFloat16): Boolean = toFloat() == other.toFloat()
        }

        override val equivalenceEquality: ValueEquality<BFloat16> = object : ValueEquality<BFloat16> {
            override fun BFloat16.isEqualTo(other: BFloat16): Boolean = BFloat16.equalTo(this, other)
        }

        override val positiveInfinity: BFloat16 get() = BFloat16.POSITIVE_INFINITY
        override val negativeInfinity: BFloat16 get() = BFloat16.NEGATIVE_INFINITY
        override val maxValue: BFloat16 get() = BFloat16.MAX_VALUE
        override val minValue: BFloat16 get() = BFloat16.MIN_VALUE
        override val NaN: BFloat16 get() = BFloat16.NaN
        override val minNormal: BFloat16 get() = BFloat16.MIN_NORMAL
        override val epsilon: BFloat16 get() = BFloat16.EPSILON
        override val comparator: Comparator<BFloat16> get() = BFloat16.comparator
        override val partialComparator: PartialComparator<BFloat16> get() = BFloat16.partialComparator
    }
}

/**
 * Trait type containing metadata on standard `binary32` floating-point numbers.
 */
interface Binary32<T> : BinaryFloatingPoint<T> {
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
    }
}

/**
 * Trait type containing metadata on standard `binary64` floating-point numbers.
 */
interface Binary64<T> : BinaryFloatingPoint<T> {
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
    }
}
