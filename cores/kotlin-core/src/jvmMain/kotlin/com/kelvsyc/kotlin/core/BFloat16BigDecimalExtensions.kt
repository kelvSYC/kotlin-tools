package com.kelvsyc.kotlin.core

import java.math.BigDecimal

/**
 * Returns the exact mathematical value of this `BFloat16` as a [BigDecimal].
 *
 * Every finite `BFloat16` value is exactly representable as a [Double] (8-bit exponent, 7-bit mantissa),
 * so widening through [toDouble] and wrapping in `BigDecimal(double)` yields the exact stored value with
 * no rounding.
 *
 * @throws NumberFormatException if this value is NaN or infinite.
 */
fun BFloat16.toBigDecimal(): BigDecimal = BigDecimal(toDouble())

private object BFloat16BigDecimalConverter : Converter<BFloat16, BigDecimal>() {
    override fun doForward(a: BFloat16): BigDecimal = a.toBigDecimal()
    override fun doBackward(b: BigDecimal): BFloat16 = BFloat16(b.toFloat())
}

/**
 * [Converter] between `BFloat16` and [BigDecimal].
 *
 * Forward: exact conversion via [toBigDecimal]; throws for NaN or infinite values.
 * Backward: narrows [BigDecimal] to the nearest `BFloat16` (lossy); out-of-range values become ±infinity.
 */
val BFloat16.Companion.bigDecimalConverter: Converter<BFloat16, BigDecimal>
    get() = BFloat16BigDecimalConverter
