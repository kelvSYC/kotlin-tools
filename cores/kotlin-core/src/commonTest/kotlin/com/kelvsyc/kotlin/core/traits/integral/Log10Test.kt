package com.kelvsyc.kotlin.core.traits.integral

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class Log10Test : FunSpec({

    // ── Log10.Companion.int (signed) ──────────────────────────────────────────

    context("Log10.Companion.int") {
        val ops = Log10.int

        context("floorLog10") {
            test("floor(1) = 0") { with(ops) { 1.floorLog10() } shouldBe 0 }
            test("floor(9) = 0") { with(ops) { 9.floorLog10() } shouldBe 0 }
            test("floor(10) = 1") { with(ops) { 10.floorLog10() } shouldBe 1 }
            test("floor(99) = 1") { with(ops) { 99.floorLog10() } shouldBe 1 }
            test("floor(100) = 2") { with(ops) { 100.floorLog10() } shouldBe 2 }
            test("floor(1000) = 3") { with(ops) { 1000.floorLog10() } shouldBe 3 }
            test("floor(999_999_999) = 8") { with(ops) { 999_999_999.floorLog10() } shouldBe 8 }
            test("floor(1_000_000_000) = 9") { with(ops) { 1_000_000_000.floorLog10() } shouldBe 9 }
            test("floor(Int.MAX_VALUE) = 9") { with(ops) { Int.MAX_VALUE.floorLog10() } shouldBe 9 }
            test("floor(0) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0.floorLog10() } } }
            test("floor(-1) throws") { shouldThrow<IllegalArgumentException> { with(ops) { (-1).floorLog10() } } }
        }

        context("ceilLog10") {
            test("ceil(1) = 0") { with(ops) { 1.ceilLog10() } shouldBe 0 }
            test("ceil(9) = 1") { with(ops) { 9.ceilLog10() } shouldBe 1 }
            test("ceil(10) = 1") { with(ops) { 10.ceilLog10() } shouldBe 1 }
            test("ceil(11) = 2") { with(ops) { 11.ceilLog10() } shouldBe 2 }
            test("ceil(100) = 2") { with(ops) { 100.ceilLog10() } shouldBe 2 }
            test("ceil(101) = 3") { with(ops) { 101.ceilLog10() } shouldBe 3 }
            test("ceil(Int.MAX_VALUE) = 10") { with(ops) { Int.MAX_VALUE.ceilLog10() } shouldBe 10 }
            test("ceil(0) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0.ceilLog10() } } }
            test("ceil(-1) throws") { shouldThrow<IllegalArgumentException> { with(ops) { (-1).ceilLog10() } } }
        }
    }

    // ── Log10.Companion.long (signed) ─────────────────────────────────────────

    context("Log10.Companion.long") {
        val ops = Log10.long

        context("floorLog10") {
            test("floor(1L) = 0") { with(ops) { 1L.floorLog10() } shouldBe 0 }
            test("floor(10L) = 1") { with(ops) { 10L.floorLog10() } shouldBe 1 }
            test("floor(1_000_000_000_000_000_000L) = 18") {
                with(ops) { 1_000_000_000_000_000_000L.floorLog10() } shouldBe 18
            }
            test("floor(Long.MAX_VALUE) = 18") { with(ops) { Long.MAX_VALUE.floorLog10() } shouldBe 18 }
            test("floor(0L) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0L.floorLog10() } } }
        }

        context("ceilLog10") {
            test("ceil(1L) = 0") { with(ops) { 1L.ceilLog10() } shouldBe 0 }
            test("ceil(10L) = 1") { with(ops) { 10L.ceilLog10() } shouldBe 1 }
            test("ceil(11L) = 2") { with(ops) { 11L.ceilLog10() } shouldBe 2 }
            test("ceil(1_000_000_000_000_000_000L) = 18") {
                with(ops) { 1_000_000_000_000_000_000L.ceilLog10() } shouldBe 18
            }
            test("ceil(Long.MAX_VALUE) = 19") { with(ops) { Long.MAX_VALUE.ceilLog10() } shouldBe 19 }
            test("ceil(0L) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0L.ceilLog10() } } }
        }
    }

    // ── Log10.Companion.uint (unsigned) ───────────────────────────────────────

    context("Log10.Companion.uint") {
        val ops = Log10.uint

        context("floorLog10") {
            test("floor(1u) = 0") { with(ops) { 1u.floorLog10() } shouldBe 0 }
            test("floor(10u) = 1") { with(ops) { 10u.floorLog10() } shouldBe 1 }
            test("floor(UInt.MAX_VALUE) = 9") { with(ops) { UInt.MAX_VALUE.floorLog10() } shouldBe 9 }
            test("floor(0u) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0u.floorLog10() } } }
        }

        context("ceilLog10") {
            test("ceil(1u) = 0") { with(ops) { 1u.ceilLog10() } shouldBe 0 }
            test("ceil(10u) = 1") { with(ops) { 10u.ceilLog10() } shouldBe 1 }
            test("ceil(11u) = 2") { with(ops) { 11u.ceilLog10() } shouldBe 2 }
            test("ceil(UInt.MAX_VALUE) = 10") { with(ops) { UInt.MAX_VALUE.ceilLog10() } shouldBe 10 }
            test("ceil(0u) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0u.ceilLog10() } } }
        }
    }

    // ── Log10.Companion.ulong (unsigned) ──────────────────────────────────────

    context("Log10.Companion.ulong") {
        val ops = Log10.ulong

        context("floorLog10") {
            test("floor(1uL) = 0") { with(ops) { 1uL.floorLog10() } shouldBe 0 }
            test("floor(10uL) = 1") { with(ops) { 10uL.floorLog10() } shouldBe 1 }
            test("floor(10_000_000_000_000_000_000uL) = 19") {
                with(ops) { 10_000_000_000_000_000_000uL.floorLog10() } shouldBe 19
            }
            test("floor(ULong.MAX_VALUE) = 19") { with(ops) { ULong.MAX_VALUE.floorLog10() } shouldBe 19 }
            test("floor(0uL) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0uL.floorLog10() } } }
        }

        context("ceilLog10") {
            test("ceil(1uL) = 0") { with(ops) { 1uL.ceilLog10() } shouldBe 0 }
            test("ceil(10uL) = 1") { with(ops) { 10uL.ceilLog10() } shouldBe 1 }
            test("ceil(11uL) = 2") { with(ops) { 11uL.ceilLog10() } shouldBe 2 }
            test("ceil(10_000_000_000_000_000_000uL) = 19") {
                with(ops) { 10_000_000_000_000_000_000uL.ceilLog10() } shouldBe 19
            }
            test("ceil(ULong.MAX_VALUE) = 20") { with(ops) { ULong.MAX_VALUE.ceilLog10() } shouldBe 20 }
            test("ceil(0uL) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0uL.ceilLog10() } } }
        }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("Log10.int is stable") { Log10.int shouldBe Log10.int }
        test("Log10.long is stable") { Log10.long shouldBe Log10.long }
        test("Log10.uint is stable") { Log10.uint shouldBe Log10.uint }
        test("Log10.ulong is stable") { Log10.ulong shouldBe Log10.ulong }
        test("Log10.int and Log10.long are distinct") { Log10.int shouldNotBe Log10.long }
        test("Log10.int and Log10.uint are distinct") { (Log10.int as Any) shouldNotBe (Log10.uint as Any) }
    }
})
