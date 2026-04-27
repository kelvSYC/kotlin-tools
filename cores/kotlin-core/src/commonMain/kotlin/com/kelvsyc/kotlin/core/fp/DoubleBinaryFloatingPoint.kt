package com.kelvsyc.kotlin.core.fp

/**
 * A "double binary floating point" is a generic representation of emulating the precision of a floating point number
 * by using two floating point numbers - a larger "high" value and a smaller non-overlapping "low" value; the number
 * represented is the sum of the two values.
 *
 * As with [FiniteBinaryFloatingPoint], [equals] and [hashCode] reflect structural identity of the fields rather
 * than numerical equality.
 */
interface DoubleBinaryFloatingPoint<T> {
    val high: T
    val low: T
}
