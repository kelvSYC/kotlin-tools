package com.kelvsyc.kotlin.core

import kotlin.math.abs

/**
 * Returns a [Comparator] that treats two [Double] values as equal when their absolute difference
 * is at most [tolerance], and otherwise orders them by [Double.compareTo].
 *
 * NaN values are not collapsed by this comparator: when a subtraction involves NaN the absolute
 * difference is NaN, which is not `<=` any finite tolerance, so the pair falls through to
 * [Double.compareTo] — where NaN sorts greater than all other values.
 *
 * **Non-transitivity warning:** when [tolerance] > 0 this comparator does not satisfy the
 * transitivity requirement of [Comparator]. It is suitable for pairwise approximate equality
 * checks, but must not be used as the comparator argument to a general-purpose sort.
 *
 * @throws IllegalArgumentException if [tolerance] is negative or NaN
 */
fun Double.Companion.fuzzyComparator(tolerance: Double): Comparator<Double> {
    require(tolerance >= 0.0) { "tolerance must be non-negative; got $tolerance" }
    return Comparator { a, b -> if (abs(a - b) <= tolerance) 0 else a.compareTo(b) }
}

/**
 * Returns a [Comparator] that treats two [Float] values as equal when their absolute difference
 * is at most [tolerance], and otherwise orders them by [Float.compareTo].
 *
 * See [Double.Companion.fuzzyComparator] for a discussion of NaN behaviour and non-transitivity.
 *
 * @throws IllegalArgumentException if [tolerance] is negative or NaN
 */
fun Float.Companion.fuzzyComparator(tolerance: Float): Comparator<Float> {
    require(tolerance >= 0.0f) { "tolerance must be non-negative; got $tolerance" }
    return Comparator { a, b -> if (abs(a - b) <= tolerance) 0 else a.compareTo(b) }
}

/**
 * Returns a [Comparator] that treats two [Float16] values as equal when their absolute difference
 * is at most [tolerance] (by [Float16.comparator]), and otherwise orders them by [Float16.comparator].
 *
 * See [Double.Companion.fuzzyComparator] for a discussion of NaN behaviour and non-transitivity.
 *
 * @throws IllegalArgumentException if [tolerance] is negative or NaN
 */
fun Float16.Companion.fuzzyComparator(tolerance: Float16): Comparator<Float16> {
    require(tolerance.toFloat() >= 0.0f) { "tolerance must be non-negative; got $tolerance" }
    return Comparator { a, b ->
        if (comparator.compare((a - b).abs(), tolerance) <= 0) 0 else comparator.compare(a, b)
    }
}

/**
 * Returns a [Comparator] that treats two [BFloat16] values as equal when their absolute difference
 * is at most [tolerance] (by [BFloat16.comparator]), and otherwise orders them by [BFloat16.comparator].
 *
 * See [Double.Companion.fuzzyComparator] for a discussion of NaN behaviour and non-transitivity.
 *
 * @throws IllegalArgumentException if [tolerance] is negative or NaN
 */
fun BFloat16.Companion.fuzzyComparator(tolerance: BFloat16): Comparator<BFloat16> {
    require(tolerance.toFloat() >= 0.0f) { "tolerance must be non-negative; got $tolerance" }
    return Comparator { a, b ->
        if (comparator.compare((a - b).abs(), tolerance) <= 0) 0 else comparator.compare(a, b)
    }
}
