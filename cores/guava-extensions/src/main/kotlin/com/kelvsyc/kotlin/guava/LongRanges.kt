package com.kelvsyc.kotlin.guava

import com.google.common.collect.BoundType
import com.google.common.collect.Range
import com.kelvsyc.kotlin.core.Converter

/**
 * Converts this range to a closed Guava [Range].
 *
 * Because [LongRange] implements both [ClosedRange] and [OpenEndRange], this overload explicitly
 * maps to [Range.closed] rather than [Range.closedOpen].
 */
fun LongRange.toGuavaRange(): Range<Long> = Range.closed(start, endInclusive)

/**
 * Converts this range to a [LongRange].
 *
 * @throws IllegalArgumentException if the range is unbounded on either side, or if either endpoint is open.
 */
fun Range<Long>.toLongRange(): LongRange {
    require(hasLowerBound()) { "Range must have a lower bound" }
    require(hasUpperBound()) { "Range must have an upper bound" }
    require(lowerBoundType() == BoundType.CLOSED) { "Lower bound must be closed" }
    require(upperBoundType() == BoundType.CLOSED) { "Upper bound must be closed" }
    return LongRange(lowerEndpoint(), upperEndpoint())
}

/** [Converter] between [LongRange] and Guava [Range]. The backward direction requires a bounded, closed range. */
val longRangeToGuavaRange: Converter<LongRange, Range<Long>> = Converter.of(
    forward = { it.toGuavaRange() },
    backward = { it.toLongRange() }
)
