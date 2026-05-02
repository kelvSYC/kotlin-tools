package com.kelvsyc.kotlin.guava

import com.google.common.collect.BoundType
import com.google.common.collect.Range
import com.kelvsyc.kotlin.core.Converter

fun <T : Comparable<T>> Range<T>.lowerBoundTypeOrNull(): BoundType? = if (hasLowerBound()) lowerBoundType() else null
fun <T : Comparable<T>> Range<T>.lowerEndpointOrNull(): T? = if (hasLowerBound()) lowerEndpoint() else null
fun <T : Comparable<T>> Range<T>.upperBoundTypeOrNull(): BoundType? = if (hasUpperBound()) upperBoundType() else null
fun <T : Comparable<T>> Range<T>.upperEndpointOrNull(): T? = if (hasUpperBound()) upperEndpoint() else null

fun <T : Comparable<T>> ClosedRange<T>.toGuavaRange(): Range<T> = Range.closed(start, endInclusive)
fun <T : Comparable<T>> OpenEndRange<T>.toGuavaRange(): Range<T> = Range.closedOpen(start, endExclusive)

/**
 * Converts this range to a [ClosedRange].
 *
 * @throws IllegalArgumentException if the range is unbounded on either side, or if either endpoint is open.
 */
fun <T : Comparable<T>> Range<T>.toClosedRange(): ClosedRange<T> {
    require(hasLowerBound()) { "Range must have a lower bound" }
    require(hasUpperBound()) { "Range must have an upper bound" }
    require(lowerBoundType() == BoundType.CLOSED) { "Lower bound must be closed" }
    require(upperBoundType() == BoundType.CLOSED) { "Upper bound must be closed" }
    val lower = lowerEndpoint()
    val upper = upperEndpoint()
    return object : ClosedRange<T> {
        override val start: T = lower
        override val endInclusive: T = upper
    }
}

/**
 * Converts this range to an [OpenEndRange].
 *
 * @throws IllegalArgumentException if the range is unbounded on either side, if the lower bound is open,
 * or if the upper bound is closed.
 */
fun <T : Comparable<T>> Range<T>.toOpenEndRange(): OpenEndRange<T> {
    require(hasLowerBound()) { "Range must have a lower bound" }
    require(hasUpperBound()) { "Range must have an upper bound" }
    require(lowerBoundType() == BoundType.CLOSED) { "Lower bound must be closed" }
    require(upperBoundType() == BoundType.OPEN) { "Upper bound must be open" }
    val lower = lowerEndpoint()
    val upper = upperEndpoint()
    return object : OpenEndRange<T> {
        override val start: T = lower
        override val endExclusive: T = upper
    }
}

/**
 * Creates a [Converter] between [ClosedRange] and Guava [Range].
 *
 * The backward direction requires a bounded range closed on both ends; otherwise throws [IllegalArgumentException].
 */
fun <T : Comparable<T>> closedRangeToGuavaRange(): Converter<ClosedRange<T>, Range<T>> = Converter.of(
    forward = { it.toGuavaRange() },
    backward = { it.toClosedRange() }
)

/**
 * Creates a [Converter] between [OpenEndRange] and Guava [Range].
 *
 * The backward direction requires a bounded range with a closed lower bound and an open upper bound;
 * otherwise throws [IllegalArgumentException].
 */
fun <T : Comparable<T>> openEndRangeToGuavaRange(): Converter<OpenEndRange<T>, Range<T>> = Converter.of(
    forward = { it.toGuavaRange() },
    backward = { it.toOpenEndRange() }
)
