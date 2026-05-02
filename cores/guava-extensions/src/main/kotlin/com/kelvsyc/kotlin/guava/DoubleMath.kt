package com.kelvsyc.kotlin.guava

import com.google.common.math.DoubleMath
import java.math.BigInteger
import java.math.RoundingMode

/**
 * Returns the base-2 logarithm of this value, rounded with [mode] to an [Int].
 *
 * @throws IllegalArgumentException if this value is not positive, infinite, or NaN
 * @throws ArithmeticException if [mode] is [RoundingMode.UNNECESSARY] and this value is not a finite power of two
 * @see DoubleMath.log2
 */
fun Double.log2(mode: RoundingMode): Int = DoubleMath.log2(this, mode)

/**
 * Returns the [Int] nearest to this value, using [mode] to resolve ties or non-integer values.
 *
 * @throws ArithmeticException if this value is infinite, NaN, or out of [Int] range
 * @throws ArithmeticException if [mode] is [RoundingMode.UNNECESSARY] and this value is not an integer
 * @see DoubleMath.roundToInt
 */
fun Double.roundToInt(mode: RoundingMode): Int = DoubleMath.roundToInt(this, mode)

/**
 * Returns the [Long] nearest to this value, using [mode] to resolve ties or non-integer values.
 *
 * @throws ArithmeticException if this value is infinite, NaN, or out of [Long] range
 * @throws ArithmeticException if [mode] is [RoundingMode.UNNECESSARY] and this value is not an integer
 * @see DoubleMath.roundToLong
 */
fun Double.roundToLong(mode: RoundingMode): Long = DoubleMath.roundToLong(this, mode)

/**
 * Returns the [BigInteger] nearest to this value, using [mode] to resolve ties or non-integer values.
 *
 * @throws ArithmeticException if this value is infinite or NaN
 * @throws ArithmeticException if [mode] is [RoundingMode.UNNECESSARY] and this value is not an integer
 * @see DoubleMath.roundToBigInteger
 */
fun Double.roundToBigInteger(mode: RoundingMode): BigInteger = DoubleMath.roundToBigInteger(this, mode)
