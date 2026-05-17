package com.kelvsyc.kotlin.luxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant

class DateTimeConversionsTest : FunSpec({
    test("LuxonDateTime round-trips through Instant") {
        val instant = Instant.fromEpochMilliseconds(1_700_000_000_000L)
        instant.toLuxonDateTime("UTC").toKotlinInstant() shouldBe instant
    }

    test("toLuxonDateTime sets the requested timezone") {
        val instant = Instant.fromEpochMilliseconds(1_700_000_000_000L)
        instant.toLuxonDateTime("America/New_York").zoneName shouldBe "America/New_York"
    }

    test("Converter forward direction converts Instant to LuxonDateTime") {
        val instant = Instant.fromEpochMilliseconds(1_700_000_000_000L)
        val converter = instantToLuxonDateTime("UTC")
        converter(instant).toKotlinInstant() shouldBe instant
    }

    test("Converter reverse direction converts LuxonDateTime to Instant") {
        val instant = Instant.fromEpochMilliseconds(1_700_000_000_000L)
        val converter = instantToLuxonDateTime("UTC")
        converter.reverse(converter(instant)) shouldBe instant
    }
})
