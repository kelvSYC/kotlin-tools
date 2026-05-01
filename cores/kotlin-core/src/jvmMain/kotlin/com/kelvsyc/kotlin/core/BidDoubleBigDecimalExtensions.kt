package com.kelvsyc.kotlin.core

import java.math.BigDecimal
import java.math.BigInteger

/**
 * Returns the exact mathematical value of this `BidDouble` as a [BigDecimal].
 *
 * Unlike binary floating-point types, `BidDouble` cannot be exactly widened to a [Double] — decimal
 * fractions are generally not representable in binary. The conversion is therefore constructed
 * directly from the BID encoding:
 *
 * ```
 * value = (−1)^sign × significand × 10^(biasedExponent − 398)
 *       = BigDecimal(±significand, scale = 398 − biasedExponent)
 * ```
 *
 * The result is always exact and carries the same scale as the stored encoding (i.e. cohort-distinct
 * representations produce [BigDecimal] values that differ in scale but compare equal under
 * [BigDecimal.compareTo]).
 *
 * @throws NumberFormatException if this value is NaN or infinite.
 */
fun BidDouble.toBigDecimal(): BigDecimal {
    if (isNaN() || isInfinite()) throw NumberFormatException("Cannot convert $this to BigDecimal: value is ${if (isNaN()) "NaN" else "infinite"}")
    val unscaled = if (sign) -significand else significand
    return BigDecimal(BigInteger.valueOf(unscaled), 398 - biasedExponent)
}
