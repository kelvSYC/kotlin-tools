package com.kelvsyc.kotlin.luxon

import com.kelvsyc.kotlin.core.Converter
import kotlinx.datetime.Instant

/** Converts this Luxon [LuxonDateTime] to a [kotlinx.datetime.Instant] via epoch milliseconds. */
fun LuxonDateTime.toKotlinInstant(): Instant = Instant.fromEpochMilliseconds(toMillis().toLong())

/**
 * Converts this [Instant] to a Luxon [LuxonDateTime] in the given [zone].
 *
 * @param zone IANA timezone name (e.g. `"UTC"`, `"America/New_York"`). Defaults to `"UTC"`.
 */
fun Instant.toLuxonDateTime(zone: String = "UTC"): LuxonDateTime =
    LuxonModule.DateTime.fromMillis(toEpochMilliseconds().toDouble()).setZone(zone)

/**
 * Returns a [Converter] between [Instant] and [LuxonDateTime] for the given [zone].
 *
 * The forward direction converts [Instant] → [LuxonDateTime] in [zone]; the reverse converts
 * [LuxonDateTime] → [Instant] (timezone is discarded, only epoch milliseconds are preserved).
 *
 * @param zone IANA timezone name. Defaults to `"UTC"`.
 */
fun instantToLuxonDateTime(zone: String = "UTC"): Converter<Instant, LuxonDateTime> = Converter.of(
    forward = { it.toLuxonDateTime(zone) },
    backward = { it.toKotlinInstant() },
)
