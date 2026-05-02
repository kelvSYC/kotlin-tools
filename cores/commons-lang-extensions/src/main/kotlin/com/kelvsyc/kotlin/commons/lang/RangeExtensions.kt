package com.kelvsyc.kotlin.commons.lang

import org.apache.commons.lang3.Range

/**
 * Returns a Commons [Range] representing the same closed interval as this [ClosedRange].
 *
 * Both bounds are inclusive. If [start] is greater than [endInclusive] according to natural ordering,
 * [Range.of] normalizes the interval by swapping the bounds.
 */
fun <T : Comparable<T>> ClosedRange<T>.toCommonsRange(): Range<T> = Range.of(start, endInclusive)

/**
 * Returns this Commons [Range] as a Kotlin [ClosedRange].
 *
 * Both [Range.minimum] and [Range.maximum] are included in the resulting range.
 */
fun <T : Comparable<T>> Range<T>.toClosedRange(): ClosedRange<T> = minimum..maximum
