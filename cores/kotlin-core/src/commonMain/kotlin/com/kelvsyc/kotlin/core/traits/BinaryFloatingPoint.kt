package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.Float16

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
    }
}
