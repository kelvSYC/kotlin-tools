package com.kelvsyc.kotlin.core.traits

/**
 * `Decimal32` is a trait containing structural metadata on a `decimal32` floating-point type [T].
 *
 * `decimal32` is the 32-bit decimal interchange format defined in IEEE 754-2008 §3.5.2. It provides
 * 7 significant decimal digits and a biased-exponent range of 0–191 (unbiased −101 to +90, where
 * `Emax = 96` and `bias = 101`).
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
 * `Bid32` is a trait containing structural metadata on a `decimal32` floating-point type [T], whose significand
 * is encoded in binary integer decimal format.
 *
 * The BID significand for `decimal32` fits in 24 bits (max value 9,999,999), which is stored as a [UInt32]
 * split across the combination and continuation fields.
 */
interface Bid32<T> : Decimal32<T>, BinaryIntegerDecimal<T> {
    override val significandTraits: UInt32<UInt> get() = UInt32
}

/**
 * `Dpd32` is a trait containing structural metadata on a `decimal32` floating-point type [T], whose significand
 * is encoded in densely packed decimal format.
 *
 * The continuation field holds two 10-bit declets encoding the lower six of the seven significand digits; the
 * leading digit is encoded in the combination field.
 */
interface Dpd32<T> : Decimal32<T>, DenselyPackedDecimal<T>
