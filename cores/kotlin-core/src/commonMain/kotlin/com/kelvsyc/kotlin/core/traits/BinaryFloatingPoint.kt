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

    /**
     * Classification predicates for this format, providing `isNaN`, `isInfinite`, `isFinite`,
     * `isZero`, `isNormal`, and `isSubnormal`.
     *
     * A value is *normal* when its biased exponent is neither all-zeros nor all-ones — that is,
     * the implicit leading 1 bit is present and the value is neither subnormal, zero, infinite,
     * nor NaN. A value is *subnormal* when the biased exponent is all-zeros and the mantissa is
     * nonzero; such values have reduced precision and no implicit leading bit.
     */
    val classification: IeeeFloatingPointClassification<T>

    /**
     * IEEE 754-2008 §5.5.1 copy operations for this format: sign-bit manipulation defined for
     * all bit patterns, including NaN, infinity, and both zeros.
     *
     * @see FloatingPointSign for the distinction between a sign-bit flip and arithmetic negation.
     */
    val sign: FloatingPointSign<T>
}

// ── Binary16 (Float16) ────────────────────────────────────────────────────────

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
        override val positiveZero: Float16 get() = Float16(0)
        override val negativeZero: Float16 get() = Float16(0x8000.toShort())
        override val maxValue: Float16 get() = Float16.MAX_VALUE
        override val minValue: Float16 get() = Float16.MIN_VALUE
        override val NaN: Float16 get() = Float16.NaN
        override val minNormal: Float16 get() = Float16.MIN_NORMAL
        override val epsilon: Float16 get() = Float16.EPSILON
        override val comparator: Comparator<Float16> get() = Float16.comparator
        override val partialComparator: PartialComparator<Float16> get() = Float16.partialComparator

        // Float16 exposes all predicates as member functions; calling this.foo() inside an
        // override of the same name resolves to the member function (no recursion).
        override val classification: IeeeFloatingPointClassification<Float16> =
            object : IeeeFloatingPointClassification<Float16> {
                override fun Float16.isNaN(): Boolean = this.isNaN()
                override fun Float16.isInfinite(): Boolean = this.isInfinite()
                override fun Float16.isFinite(): Boolean = this.isFinite()
                override fun Float16.isZero(): Boolean = this.isZero()
                override fun Float16.isNormal(): Boolean = this.isNormal()
                override fun Float16.isSubnormal(): Boolean = this.isSubnormal()
            }

        override val sign: FloatingPointSign<Float16> = object : FloatingPointSign<Float16> {
            // All operations use bit-level methods on the backing Short.
            override fun Float16.isNegative(): Boolean = this.sign
            override fun Float16.negate(): Float16 = -this
            override fun Float16.abs(): Float16 = this.abs()
            // Bit manipulation avoids name-shadowing by the trait's own member extension copySign.
            override fun Float16.copySign(other: Float16): Float16 =
                Float16(((bits.toInt() and 0x7FFF) or (other.bits.toInt() and 0x8000)).toShort())
        }
    }
}

// ── BinaryBFloat16 (BFloat16) ─────────────────────────────────────────────────

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
        override val positiveZero: BFloat16 get() = BFloat16(0)
        override val negativeZero: BFloat16 get() = BFloat16(0x8000.toShort())
        override val maxValue: BFloat16 get() = BFloat16.MAX_VALUE
        override val minValue: BFloat16 get() = BFloat16.MIN_VALUE
        override val NaN: BFloat16 get() = BFloat16.NaN
        override val minNormal: BFloat16 get() = BFloat16.MIN_NORMAL
        override val epsilon: BFloat16 get() = BFloat16.EPSILON
        override val comparator: Comparator<BFloat16> get() = BFloat16.comparator
        override val partialComparator: PartialComparator<BFloat16> get() = BFloat16.partialComparator

        // BFloat16 exposes all predicates as member functions; same reasoning as Binary16.
        override val classification: IeeeFloatingPointClassification<BFloat16> =
            object : IeeeFloatingPointClassification<BFloat16> {
                override fun BFloat16.isNaN(): Boolean = this.isNaN()
                override fun BFloat16.isInfinite(): Boolean = this.isInfinite()
                override fun BFloat16.isFinite(): Boolean = this.isFinite()
                override fun BFloat16.isZero(): Boolean = this.isZero()
                override fun BFloat16.isNormal(): Boolean = this.isNormal()
                override fun BFloat16.isSubnormal(): Boolean = this.isSubnormal()
            }

        override val sign: FloatingPointSign<BFloat16> = object : FloatingPointSign<BFloat16> {
            // All operations use bit-level methods on the backing Short.
            override fun BFloat16.isNegative(): Boolean = this.sign
            override fun BFloat16.negate(): BFloat16 = -this
            override fun BFloat16.abs(): BFloat16 = this.abs()
            override fun BFloat16.copySign(other: BFloat16): BFloat16 =
                BFloat16(((bits.toInt() and 0x7FFF) or (other.bits.toInt() and 0x8000)).toShort())
        }
    }
}

// ── Binary32 (Float) ──────────────────────────────────────────────────────────

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
