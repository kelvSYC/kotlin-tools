package com.kelvsyc.kotlin.guava

import com.google.common.math.DoubleMath
import java.math.RoundingMode

/**
 * Returns the base-2 logarithm of this value, rounded with [mode] to an [Int].
 *
 * @throws IllegalArgumentException if this value is not positive, infinite, or NaN
 * @throws ArithmeticException if [mode] is [RoundingMode.UNNECESSARY] and this value is not a finite power of two
 * @see DoubleMath.log2
 */
fun Double.log2(mode: RoundingMode): Int = DoubleMath.log2(this, mode)
