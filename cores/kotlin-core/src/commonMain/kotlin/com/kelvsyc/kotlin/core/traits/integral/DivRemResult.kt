package com.kelvsyc.kotlin.core.traits.integral

/**
 * The result of a combined division-and-remainder operation: [quotient] is the truncated quotient
 * (toward zero) and [remainder] is the truncating remainder (sign of the dividend).
 *
 * Supports destructuring:
 * ```kotlin
 * val (q, r) = with(DivRem.int) { 7.divRem(3) }
 * ```
 */
data class DivRemResult<T>(val quotient: T, val remainder: T)
