package com.kelvsyc.kotlin.core.fp

import com.kelvsyc.kotlin.core.Converter
import java.math.BigDecimal

/**
 * Returns the exact mathematical value of this `DoubleDouble` as a [BigDecimal].
 *
 * `BigDecimal(double)` is exact for each component, and their sum gives the full ~31-digit precision
 * of the double-double representation without any rounding.
 *
 * @throws NumberFormatException if [DoubleDouble.high] is NaN or infinite.
 */
fun DoubleDouble.toBigDecimal(): BigDecimal = BigDecimal(high) + BigDecimal(low)

private object DoubleDoubleBigDecimalConverter : Converter<DoubleDouble, BigDecimal>() {
    override fun doForward(a: DoubleDouble): BigDecimal = a.toBigDecimal()
    override fun doBackward(b: BigDecimal): DoubleDouble {
        val hi = b.toDouble()
        val lo = (b - BigDecimal(hi)).toDouble()
        return DoubleDouble(hi, lo)
    }
}

/**
 * [Converter] between `DoubleDouble` and [BigDecimal].
 *
 * Forward: exact conversion via [toBigDecimal]; throws for NaN or infinite values.
 * Backward: extracts the best possible `DoubleDouble` from a [BigDecimal] by computing the nearest
 * [Double] for `high` and then representing the residual in `low`. Lossy for values with more than
 * ~31 decimal digits of precision.
 */
val DoubleDouble.Companion.bigDecimalConverter: Converter<DoubleDouble, BigDecimal>
    get() = DoubleDoubleBigDecimalConverter
