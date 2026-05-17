package com.kelvsyc.kotlin.luxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds

class DateTimeOperatorsTest : FunSpec({
    test("plus adds kotlin.time.Duration to LuxonDateTime") {
        val base = Instant.fromEpochMilliseconds(1_000_000_000_000L)
        val result = base.toLuxonDateTime() + 5.seconds
        result.toKotlinInstant() shouldBe Instant.fromEpochMilliseconds(1_000_000_005_000L)
    }

    test("minus subtracts kotlin.time.Duration from LuxonDateTime") {
        val base = Instant.fromEpochMilliseconds(1_000_000_005_000L)
        val result = base.toLuxonDateTime() - 5.seconds
        result.toKotlinInstant() shouldBe Instant.fromEpochMilliseconds(1_000_000_000_000L)
    }

    test("rangeTo creates a LuxonInterval spanning the two DateTimes") {
        val start = Instant.fromEpochMilliseconds(1_000_000_000_000L)
        val end = Instant.fromEpochMilliseconds(2_000_000_000_000L)
        val interval = start.toLuxonDateTime()..end.toLuxonDateTime()
        interval.start.toKotlinInstant() shouldBe start
        interval.end.toKotlinInstant() shouldBe end
    }
})
