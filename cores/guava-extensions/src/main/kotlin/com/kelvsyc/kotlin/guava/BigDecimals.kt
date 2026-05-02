package com.kelvsyc.kotlin.guava

import com.google.common.math.BigDecimalMath
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Returns this value rounded to a [Double] with the specified [mode].
 *
 * @see BigDecimalMath.roundToDouble
 */
fun BigDecimal.roundToDouble(mode: RoundingMode): Double = BigDecimalMath.roundToDouble(this, mode)
