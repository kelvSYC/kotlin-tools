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
 * Returns the base-10 logarithm of this value, rounded with [mode] to an [Int].
 *
 * @throws IllegalArgumentException if this value is not positive
 * @throws ArithmeticException if [mode] is [RoundingMode.UNNECESSARY] and this value is not a power of ten
 * @see IntMath.log10
 */
fun Int.log10(mode: RoundingMode): Int = IntMath.log10(this, mode)

/**
 * Returns the square root of this value, rounded with [mode] to an [Int].
 *
 * @throws IllegalArgumentException if this value is negative
 * @throws ArithmeticException if [mode] is [RoundingMode.UNNECESSARY] and this value is not a perfect square
 * @see IntMath.sqrt
 */
fun Int.sqrt(mode: RoundingMode): Int = IntMath.sqrt(this, mode)

/**
 * Returns the base-2 logarithm of this value, rounded with [mode] to an [Int].
 *
 * @throws IllegalArgumentException if this value is not positive
 * @throws ArithmeticException if [mode] is [RoundingMode.UNNECESSARY] and this value is not a power of two
 * @see LongMath.log2
 */
fun Long.log2(mode: RoundingMode): Int = LongMath.log2(this, mode)

/**
 * Returns the base-10 logarithm of this value, rounded with [mode] to an [Int].
 *
 * @throws IllegalArgumentException if this value is not positive
 * @throws ArithmeticException if [mode] is [RoundingMode.UNNECESSARY] and this value is not a power of ten
 * @see LongMath.log10
 */
fun Long.log10(mode: RoundingMode): Int = LongMath.log10(this, mode)

/**
 * Returns this value rounded to a [Double] with the specified [mode].
 *
 * Unlike a simple cast, this respects the rounding mode for values that cannot be represented
 * exactly as a [Double] (e.g. using [RoundingMode.FLOOR] or [RoundingMode.HALF_EVEN]).
 *
 * @see LongMath.roundToDouble
 */
fun Long.roundToDouble(mode: RoundingMode): Double = LongMath.roundToDouble(this, mode)

/**
 * Returns the square root of this value, rounded with [mode] to an [Int].
 *
 * @throws IllegalArgumentException if this value is negative
 * @throws ArithmeticException if [mode] is [RoundingMode.UNNECESSARY] and this value is not a perfect square
 * @see LongMath.sqrt
 */
fun Long.sqrt(mode: RoundingMode): Long = LongMath.sqrt(this, mode)
