package com.kelvsyc.kotlin.core.traits.fp

/**
 * The result of a combined hyperbolic-sine-and-cosine operation: [sinh] is `sinh(x)` and
 * [cosh] is `cosh(x)`.
 *
 * Supports destructuring:
 * ```kotlin
 * val (s, c) = with(FloatingPointSinhCosh.double) { x.sinhcosh() }
 * ```
 */
data class SinhCoshResult<T>(val sinh: T, val cosh: T)
