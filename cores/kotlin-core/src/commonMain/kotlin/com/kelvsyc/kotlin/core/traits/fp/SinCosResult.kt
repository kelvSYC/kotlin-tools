package com.kelvsyc.kotlin.core.traits.fp

/**
 * The result of a combined sine-and-cosine operation: [sin] is `sin(x)` and [cos] is `cos(x)`.
 *
 * Supports destructuring:
 * ```kotlin
 * val (s, c) = with(FloatingPointSinCos.float) { angle.sincos() }
 * ```
 */
data class SinCosResult<T>(val sin: T, val cos: T)
