package com.kelvsyc.kotlin.luxon

import com.kelvsyc.kotlin.core.Converter
import kotlinx.datetime.Instant

/**
 * Converts this Luxon [LuxonInterval] to a [ClosedRange] of [Instant].
 *
 * Both endpoints are converted via [LuxonDateTime.toKotlinInstant]. Open-ended Luxon intervals
 * cannot be represented as a [ClosedRange]; ensure the interval is bounded before calling this.
 */
fun LuxonInterval.toInstantRange(): ClosedRange<Instant> =
    start.toKotlinInstant()..end.toKotlinInstant()

/**
 * Converts this [ClosedRange] of [Instant] to a Luxon [LuxonInterval].
 *
 * Both endpoints are converted to UTC [LuxonDateTime] via [Instant.toLuxonDateTime].
 */
fun ClosedRange<Instant>.toLuxonInterval(): LuxonInterval = LuxonModule.Interval.fromDateTimes(
    start.toLuxonDateTime(),
    endInclusive.toLuxonDateTime(),
)

/**
 * A [Converter] between [LuxonInterval] and [ClosedRange] of [Instant].
 *
 * See [toInstantRange] for notes on open-ended intervals.
 */
val luxonIntervalToInstantRange: Converter<LuxonInterval, ClosedRange<Instant>> = Converter.of(
    forward = { it.toInstantRange() },
    backward = { it.toLuxonInterval() },
)
