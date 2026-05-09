package com.kelvsyc.kotlin.core.traits.fp

import kotlin.math.round

/**
 * Pure-Kotlin IEEE 754 remainder for [Float].
 *
 * Computes the IEEE 754 remainder: `x − n × y` where `n` is the integer nearest to the exact
 * value of `x / y`, with ties rounding to even.
 */
internal fun ieee754RemFloatEmulated(x: Float, y: Float): Float =
    ieee754RemDoubleEmulated(x.toDouble(), y.toDouble()).toFloat()

/**
 * Pure-Kotlin IEEE 754 remainder for [Double].
 *
 * Computes the IEEE 754 remainder: `x − n × y` where `n` is the integer nearest to the exact
 * value of `x / y`, with ties rounding to even.
 */
internal fun ieee754RemDoubleEmulated(x: Double, y: Double): Double {
    if (x.isNaN() || y.isNaN() || x.isInfinite() || y == 0.0) return Double.NaN
    if (y.isInfinite()) return x
    val n = round(x / y)
    return x - n * y
}
