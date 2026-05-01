package com.kelvsyc.kotlin.core

import java.math.BigDecimal
import java.math.BigInteger

/**
 * Returns the exact mathematical value of this `DpdFloat` as a [BigDecimal].
 *
 * Unlike binary floating-point types, `DpdFloat` cannot be exactly widened to a [Double] — decimal
 * fractions are generally not representable in binary. The conversion is constructed directly from
 * the decoded significand and exponent:
 *
 * ```
 * value = (−1)^sign × significand × 10^(biasedExponent − 101)
 *       = BigDecimal(±significand, scale = 101 − biasedExponent)
 * ```
 *
 * The result is always exact and carries the same scale as the stored encoding (i.e. cohort-distinct
 * representations produce [BigDecimal] values that differ in scale but compare equal under
 * [BigDecimal.compareTo]).
 *
 * @throws NumberFormatException if this value is NaN or infinite.
 */
fun DpdFloat.toBigDecimal(): BigDecimal {
    if (isNaN() || isInfinite()) throw NumberFormatException("Cannot convert $this to BigDecimal: value is ${if (isNaN()) "NaN" else "infinite"}")
    val unscaled = if (sign) -significand.toLong() else significand.toLong()
    return BigDecimal(BigInteger.valueOf(unscaled), 101 - biasedExponent)
}
