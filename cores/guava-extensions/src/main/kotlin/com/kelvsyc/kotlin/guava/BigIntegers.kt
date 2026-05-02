package com.kelvsyc.kotlin.guava

import com.google.common.math.BigIntegerMath
import java.math.BigInteger

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
