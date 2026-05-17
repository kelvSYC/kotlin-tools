package com.kelvsyc.kotlin.luxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.DateTimePeriod
import kotlin.time.Duration.Companion.seconds

class DurationConversionsTest : FunSpec({
    test("LuxonDuration converts to kotlin.time.Duration") {
        LuxonModule.Duration.fromMillis(5_000.0).toKotlinDuration() shouldBe 5.seconds
    }

    test("kotlin.time.Duration converts to LuxonDuration") {
        5.seconds.toLuxonDuration().toMillis().toLong() shouldBe 5_000L
    }

    test("luxonDurationToKotlinDuration converter round-trips") {
        val luxonDur = LuxonModule.Duration.fromMillis(12_345.0)
        luxonDurationToKotlinDuration(luxonDur).toLuxonDuration().toMillis().toLong() shouldBe 12_345L
    }

    test("LuxonDuration converts to DateTimePeriod preserving calendar fields") {
        val obj: dynamic = js("{}")
        obj.years = 1
        obj.months = 2
        obj.days = 3
        obj.hours = 4
        obj.minutes = 5
        obj.seconds = 6
        obj.milliseconds = 500
        val period = LuxonModule.Duration.fromObject(obj).toKotlinDateTimePeriod()
        period.years shouldBe 1
        period.months shouldBe 2
        period.days shouldBe 3
        period.hours shouldBe 4
        period.minutes shouldBe 5
        period.seconds shouldBe 6
        period.nanoseconds shouldBe 500_000_000L
    }

    test("Luxon weeks are folded into days during DateTimePeriod conversion") {
        val obj: dynamic = js("{}")
        obj.weeks = 2
        obj.days = 3
        val period = LuxonModule.Duration.fromObject(obj).toKotlinDateTimePeriod()
        period.days shouldBe 17  // 2 * 7 + 3
    }

    test("DateTimePeriod round-trips through LuxonDuration") {
        val period = DateTimePeriod(years = 1, months = 2, days = 5, hours = 3, minutes = 30)
        val recovered = period.toLuxonDuration().toKotlinDateTimePeriod()
        recovered.years shouldBe period.years
        recovered.months shouldBe period.months
        recovered.days shouldBe period.days
        recovered.hours shouldBe period.hours
        recovered.minutes shouldBe period.minutes
        recovered.seconds shouldBe period.seconds
    }
})
