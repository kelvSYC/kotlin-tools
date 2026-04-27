package com.kelvsyc.kotlin.core

/**
 * [PartialComparator] for [Float] values that returns `null` when either operand is NaN, reflecting the
 * IEEE 754 rule that NaN is unordered with respect to every value including itself. Non-NaN values are
 * compared by [Float.compareTo].
 */
val Float.Companion.partialComparator: PartialComparator<Float>
    get() = PartialComparator { a, b -> if (a.isNaN() || b.isNaN()) null else a.compareTo(b) }

/**
 * [PartialComparator] for [Double] values that returns `null` when either operand is NaN, reflecting the
 * IEEE 754 rule that NaN is unordered with respect to every value including itself. Non-NaN values are
 * compared by [Double.compareTo].
 */
val Double.Companion.partialComparator: PartialComparator<Double>
    get() = PartialComparator { a, b -> if (a.isNaN() || b.isNaN()) null else a.compareTo(b) }
