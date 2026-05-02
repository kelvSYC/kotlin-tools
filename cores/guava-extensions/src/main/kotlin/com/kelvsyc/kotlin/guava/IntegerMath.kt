package com.kelvsyc.kotlin.guava

import com.google.common.math.IntMath
import com.google.common.math.LongMath
import java.math.RoundingMode

/**
 * Returns the base-2 logarithm of this value, rounded with [mode] to an [Int].
 *
 * @throws IllegalArgumentException if this value is not positive
 * @throws ArithmeticException if [mode] is [RoundingMode.UNNECESSARY] and this value is not a power of two
 * @see IntMath.log2
 */
fun Int.log2(mode: RoundingMode): Int = IntMath.log2(this, mode)

/**
 * Returns the base-2 logarithm of this value, rounded with [mode] to an [Int].
 *
 * @throws IllegalArgumentException if this value is not positive
 * @throws ArithmeticException if [mode] is [RoundingMode.UNNECESSARY] and this value is not a power of two
 * @see LongMath.log2
 */
fun Long.log2(mode: RoundingMode): Int = LongMath.log2(this, mode)
