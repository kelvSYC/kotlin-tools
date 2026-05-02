package com.kelvsyc.kotlin.core.traits.dfp

import com.kelvsyc.kotlin.core.traits.integral.UInt64

/**
 * `Decimal64` is a trait containing structural metadata on a `decimal64` floating-point type [T].
 *
 * `decimal64` is the 64-bit decimal interchange format defined in IEEE 754-2008 §3.5.2. It provides
 * 16 significant decimal digits and a biased-exponent range of 0–767 (unbiased −398 to +369, where
 * `Emax = 384` and `bias = 398`).
 */
interface Decimal64<T> : DecimalFloatingPoint<T> {
    override val sizeBits: Int get() = 64
    override val combinationBits: Int get() = 13
    override val continuationBits: Int get() = 50
    override val significandDigits: Int get() = 16
    override val exponentBits: Int get() = 10
    override val exponentBias: Int get() = 398
}

/**
 * `Bid64` is a trait containing structural metadata on a `decimal64` floating-point type [T], whose significand
 * is encoded in binary integer decimal format.
 *
 * The BID significand for `decimal64` fits in 54 bits (max value 9,999,999,999,999,999), stored as a [ULong]
 * split across the combination and continuation fields.
 */
interface Bid64<T> : Decimal64<T>, BinaryIntegerDecimal<T> {
    override val significandTraits: UInt64<ULong> get() = UInt64
}

/**
 * `Dpd64` is a trait containing structural metadata on a `decimal64` floating-point type [T], whose significand
 * is encoded in densely packed decimal format.
 *
 * The continuation field holds five 10-bit declets encoding the lower 15 of the 16 significand digits; the
 * leading digit is encoded in the combination field.
 */
interface Dpd64<T> : Decimal64<T>, DenselyPackedDecimal<T>
