package com.kelvsyc.kotlin.core

import java.math.BigDecimal

/**
 * Returns the exact mathematical value of this `Float16` as a [BigDecimal].
 *
 * Every finite `Float16` value is exactly representable as a [Double], so widening through [toDouble] and
 * wrapping in `BigDecimal(double)` yields the exact stored value with no rounding.
 *
 * @throws NumberFormatException if this value is NaN or infinite.
 */
fun Float16.toBigDecimal(): BigDecimal = BigDecimal(toDouble())

private object Float16BigDecimalConverter : Converter<Float16, BigDecimal>() {
    override fun doForward(a: Float16): BigDecimal = a.toBigDecimal()
    override fun doBackward(b: BigDecimal): Float16 = Float16(b.toFloat())
}

/**
 * [Converter] between `Float16` and [BigDecimal].
 *
 * Forward: exact conversion via [toBigDecimal]; throws for NaN or infinite values.
 * Backward: narrows [BigDecimal] to the nearest `Float16` (lossy); out-of-range values become ±infinity.
 */
val Float16.Companion.bigDecimalConverter: Converter<Float16, BigDecimal>
    get() = Float16BigDecimalConverter
