package com.kelvsyc.kotlin.luxon

import kotlin.time.Duration

/** Adds a [Duration] to this [LuxonDateTime], returning a new [LuxonDateTime]. */
operator fun LuxonDateTime.plus(duration: Duration): LuxonDateTime = plus(duration.toLuxonDuration())

/** Subtracts a [Duration] from this [LuxonDateTime], returning a new [LuxonDateTime]. */
operator fun LuxonDateTime.minus(duration: Duration): LuxonDateTime = minus(duration.toLuxonDuration())

/**
 * Creates a [LuxonInterval] spanning from this [LuxonDateTime] to [other].
 *
 * Enables `startDateTime..endDateTime` range syntax for Luxon date-times.
 */
operator fun LuxonDateTime.rangeTo(other: LuxonDateTime): LuxonInterval = until(other)
