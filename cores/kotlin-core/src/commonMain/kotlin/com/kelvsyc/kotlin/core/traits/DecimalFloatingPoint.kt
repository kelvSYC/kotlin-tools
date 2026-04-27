package com.kelvsyc.kotlin.core.traits

/**
 * `DecimalFloatingPoint` is a trait type that contains structural metadata on a decimal floating-point type [T].
 */
interface DecimalFloatingPoint<T> {
    val sizeBits: Int

    val continuationBits: Int

    val combinationBits: Int

    val significandDigits: Int

    val exponentBits: Int

    val exponentBias: Int

    val numericalEquality: ValueEquality<T>

    val equivalenceEquality: ValueEquality<T>
}

/**
 * `BinaryIntegerDecimal` is a trait type containing structural metadata for decimal floating point significands
 * that have been encoded in binary integer decimal format.
 */
interface BinaryIntegerDecimal<T> : DecimalFloatingPoint<T> {
    val significandTraits: UnsignedIntegral<*>
}

/**
 * `DenselyPackedDecimal` is a trait type containing structural metadata for decimal floating point significands
 * that have been encoded in densely packed decimal format.
 */
interface DenselyPackedDecimal<T> : DecimalFloatingPoint<T> {
    val decletCount: Int
        get() = (significandDigits - 1) / 3
}
/**
 * `Decimal32` is a trait containing structural metadata on a `decimal32` floating-point type [T].
 */
interface Decimal32<T> : DecimalFloatingPoint<T> {
    override val sizeBits: Int get() = 32
    override val combinationBits: Int get() = 11
    override val continuationBits: Int get() = 20
    override val significandDigits: Int get() = 7
    override val exponentBits: Int get() = 8
    override val exponentBias: Int get() = 101
}

/**
 * `Bid32` is a trait containing structural metadata on a `decimal32` floating-point type [T], whose significand is
 * encoded in binary integer decimal format.
 */
interface Bid32<T> : Decimal32<T>, BinaryIntegerDecimal<T> {
    override val significandTraits: UInt32<UInt> get() = UInt32
}

/**
 * `Dpd32` is a trait containing structural metadata on a `decimal32` floating-point type [T], whose significand is
 * encoded in densely packed decimal format.
 */
interface Dpd32<T> : Decimal32<T>, DenselyPackedDecimal<T>
