package com.kelvsyc.kotlin.luxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant

class IntervalConversionsTest : FunSpec({
    test("LuxonInterval converts to ClosedRange<Instant>") {
        val start = Instant.fromEpochMilliseconds(1_000_000_000_000L)
        val end = Instant.fromEpochMilliseconds(2_000_000_000_000L)
        val interval = LuxonModule.Interval.fromDateTimes(
            start.toLuxonDateTime(),
            end.toLuxonDateTime(),
        )
        val range = interval.toInstantRange()
        range.start shouldBe start
        range.endInclusive shouldBe end
    }

    test("ClosedRange<Instant> round-trips through LuxonInterval") {
        val start = Instant.fromEpochMilliseconds(1_000_000_000_000L)
        val end = Instant.fromEpochMilliseconds(2_000_000_000_000L)
        val range = start..end
        val recovered = range.toLuxonInterval().toInstantRange()
        recovered.start shouldBe start
        recovered.endInclusive shouldBe end
    }

    test("luxonIntervalToInstantRange converter round-trips") {
        val start = Instant.fromEpochMilliseconds(1_000_000_000_000L)
        val end = Instant.fromEpochMilliseconds(2_000_000_000_000L)
        val interval = LuxonModule.Interval.fromDateTimes(
            start.toLuxonDateTime(),
            end.toLuxonDateTime(),
        )
        val range = luxonIntervalToInstantRange(interval)
        range.start shouldBe start
        range.endInclusive shouldBe end
    }
})
