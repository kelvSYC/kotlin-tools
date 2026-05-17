package com.kelvsyc.kotlin.luxon

import com.kelvsyc.kotlin.core.Converter
import kotlinx.datetime.DateTimePeriod
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Converts this Luxon [LuxonDuration] to a [Duration] by summing all units to milliseconds.
 *
 * **Loss of precision:** Calendar units (years, months) are converted using Luxon's own fixed
 * factors (approximately 30.4375 days/month). The result is suitable only for sub-day durations
 * where no calendar units are present. Use [toKotlinDateTimePeriod] to preserve calendar structure.
 */
fun LuxonDuration.toKotlinDuration(): Duration = toMillis().toLong().milliseconds

/**
 * Converts this [Duration] to a Luxon [LuxonDuration] expressed entirely in milliseconds.
 *
 * Sub-millisecond precision is truncated.
 */
fun Duration.toLuxonDuration(): LuxonDuration =
    LuxonModule.Duration.fromMillis(inWholeMilliseconds.toDouble())

/**
 * A [Converter] between [LuxonDuration] and [Duration].
 *
 * See [toKotlinDuration] for precision caveats when calendar units are present.
 */
val luxonDurationToKotlinDuration: Converter<LuxonDuration, Duration> = Converter.of(
    forward = { it.toKotlinDuration() },
    backward = { it.toLuxonDuration() },
)

/**
 * Converts this Luxon [LuxonDuration] to a [DateTimePeriod], preserving calendar structure.
 *
 * Luxon weeks are folded into days (1 week = 7 days). Sub-millisecond precision in [LuxonDuration]
 * is not representable and is truncated.
 */
fun LuxonDuration.toKotlinDateTimePeriod(): DateTimePeriod = DateTimePeriod(
    years = years.toInt(),
    months = months.toInt(),
    days = days.toInt() + weeks.toInt() * 7,
    hours = hours.toInt(),
    minutes = minutes.toInt(),
    seconds = seconds.toInt(),
    nanoseconds = milliseconds.toLong() * 1_000_000L,
)

/**
 * Converts this [DateTimePeriod] to a Luxon [LuxonDuration].
 *
 * [DateTimePeriod.nanoseconds] below 1 millisecond precision are truncated.
 * Days are not split into weeks.
 */
fun DateTimePeriod.toLuxonDuration(): LuxonDuration {
    val obj: dynamic = js("{}")
    obj.years = years
    obj.months = months
    obj.days = days
    obj.hours = hours
    obj.minutes = minutes
    obj.seconds = seconds
    obj.milliseconds = nanoseconds.toDouble() / 1_000_000.0
    return LuxonModule.Duration.fromObject(obj)
}

/**
 * A [Converter] between [LuxonDuration] and [DateTimePeriod].
 *
 * See [toKotlinDateTimePeriod] for notes on weeks and nanosecond precision.
 */
val luxonDurationToDateTimePeriod: Converter<LuxonDuration, DateTimePeriod> = Converter.of(
    forward = { it.toKotlinDateTimePeriod() },
    backward = { it.toLuxonDuration() },
)
