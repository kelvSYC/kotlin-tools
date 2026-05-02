package com.kelvsyc.kotlin.guava

import com.google.common.math.BigIntegerMath
import java.math.BigInteger
import java.math.RoundingMode

/**
 * Returns `true` if this value is a positive power of two.
 *
 * @see BigIntegerMath.isPowerOfTwo
 */
val BigInteger.isPowerOfTwo: Boolean
    get() = BigIntegerMath.isPowerOfTwo(this)

/**
 * Returns the largest power of two less than or equal to this value.
 *
 * @throws IllegalArgumentException if this value is not positive
 * @see BigIntegerMath.floorPowerOfTwo
 */
val BigInteger.floorPowerOfTwo: BigInteger
    get() = BigIntegerMath.floorPowerOfTwo(this)

/**
 * Returns the smallest power of two greater than or equal to this value.
 *
 * @throws IllegalArgumentException if this value is not positive
 * @throws ArithmeticException if the result would overflow
 * @see BigIntegerMath.ceilingPowerOfTwo
 */
val BigInteger.ceilingPowerOfTwo: BigInteger
    get() = BigIntegerMath.ceilingPowerOfTwo(this)

/**
 * Returns the base-2 logarithm of this value, rounded with [mode] to an [Int].
 *
 * @throws IllegalArgumentException if this value is not positive
 * @throws ArithmeticException if [mode] is [RoundingMode.UNNECESSARY] and this value is not a power of two
 * @see BigIntegerMath.log2
 */
fun BigInteger.log2(mode: RoundingMode): Int = BigIntegerMath.log2(this, mode)
