package com.kelvsyc.kotlin.core.traits.fp

/**
 * Pure-Kotlin `scalb` for [Float]: computes `x × 2^n` by repeated multiplication with clamped
 * powers of two, correctly handling overflow, underflow, and subnormals.
 */
internal fun scalbFloatEmulated(x: Float, n: Int): Float = scalbDoubleEmulated(x.toDouble(), n).toFloat()

/**
 * Pure-Kotlin `scalb` for [Double]: computes `x × 2^n` by repeated multiplication with clamped
 * powers of two, correctly handling overflow, underflow, and subnormals.
 */
internal fun scalbDoubleEmulated(x: Double, n: Int): Double {
    if (n == 0 || x == 0.0 || x.isNaN() || x.isInfinite()) return x

    var result = x
    var remaining = n
    while (remaining > 0) {
        val step = minOf(remaining, 1023)
        result *= Double.fromBits((1023L + step) shl 52)
        remaining -= step
    }
    while (remaining < 0) {
        val step = maxOf(remaining, -1022)
        result *= Double.fromBits((1023L + step) shl 52)
        remaining -= step
    }
    return result
}
