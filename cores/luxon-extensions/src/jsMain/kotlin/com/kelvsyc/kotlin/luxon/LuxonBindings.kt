package com.kelvsyc.kotlin.luxon

// Instance types — not annotated with @JsModule; returned by companion factories.

external class LuxonDateTime {
    val zoneName: String
    val year: Int
    val month: Int
    val day: Int
    val hour: Int
    val minute: Int
    val second: Int
    val millisecond: Int
    val isValid: Boolean
    fun plus(duration: LuxonDuration): LuxonDateTime
    fun minus(duration: LuxonDuration): LuxonDateTime
    fun until(other: LuxonDateTime): LuxonInterval
    fun toMillis(): Double
    fun setZone(zone: String): LuxonDateTime
}

external class LuxonDuration {
    val years: Double
    val months: Double
    val weeks: Double
    val days: Double
    val hours: Double
    val minutes: Double
    val seconds: Double
    val milliseconds: Double
    val isValid: Boolean
    fun toMillis(): Double
}

external class LuxonInterval {
    val start: LuxonDateTime
    val end: LuxonDateTime
    val isValid: Boolean
}

// Companion-like objects for static factory methods — accessed via LuxonModule properties.

internal external class LuxonDateTimeCompanion {
    fun fromMillis(ms: Double): LuxonDateTime
    fun fromObject(obj: dynamic): LuxonDateTime
}

internal external class LuxonDurationCompanion {
    fun fromMillis(ms: Double): LuxonDuration
    fun fromObject(obj: dynamic): LuxonDuration
}

internal external class LuxonIntervalCompanion {
    fun fromDateTimes(start: LuxonDateTime, end: LuxonDateTime): LuxonInterval
}

@JsModule("luxon")
@JsNonModule
internal external object LuxonModule {
    val DateTime: LuxonDateTimeCompanion
    val Duration: LuxonDurationCompanion
    val Interval: LuxonIntervalCompanion
}
