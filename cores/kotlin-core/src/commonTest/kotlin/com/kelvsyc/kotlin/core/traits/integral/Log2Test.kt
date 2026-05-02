package com.kelvsyc.kotlin.core.traits.integral

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class Log2Test : FunSpec({

    // ── Log2.Companion.int (signed) ───────────────────────────────────────────

    context("Log2.Companion.int") {
        val ops = Log2.int

        context("floorLog2") {
            test("floor(1) = 0") { with(ops) { 1.floorLog2() } shouldBe 0 }
            test("floor(2) = 1") { with(ops) { 2.floorLog2() } shouldBe 1 }
            test("floor(3) = 1") { with(ops) { 3.floorLog2() } shouldBe 1 }
            test("floor(4) = 2") { with(ops) { 4.floorLog2() } shouldBe 2 }
            test("floor(5) = 2") { with(ops) { 5.floorLog2() } shouldBe 2 }
            test("floor(1024) = 10") { with(ops) { 1024.floorLog2() } shouldBe 10 }
            test("floor(Int.MAX_VALUE) = 30") { with(ops) { Int.MAX_VALUE.floorLog2() } shouldBe 30 }
            test("floor(0) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0.floorLog2() } } }
            test("floor(-1) throws") { shouldThrow<IllegalArgumentException> { with(ops) { (-1).floorLog2() } } }
        }

        context("ceilLog2") {
            test("ceil(1) = 0") { with(ops) { 1.ceilLog2() } shouldBe 0 }
            test("ceil(2) = 1") { with(ops) { 2.ceilLog2() } shouldBe 1 }
            test("ceil(3) = 2") { with(ops) { 3.ceilLog2() } shouldBe 2 }
            test("ceil(4) = 2") { with(ops) { 4.ceilLog2() } shouldBe 2 }
            test("ceil(5) = 3") { with(ops) { 5.ceilLog2() } shouldBe 3 }
            test("ceil(1024) = 10") { with(ops) { 1024.ceilLog2() } shouldBe 10 }
            test("ceil(1025) = 11") { with(ops) { 1025.ceilLog2() } shouldBe 11 }
            test("ceil(Int.MAX_VALUE) = 31") { with(ops) { Int.MAX_VALUE.ceilLog2() } shouldBe 31 }
            test("ceil(0) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0.ceilLog2() } } }
            test("ceil(-1) throws") { shouldThrow<IllegalArgumentException> { with(ops) { (-1).ceilLog2() } } }
        }
    }

    // ── Log2.Companion.long (signed) ──────────────────────────────────────────

    context("Log2.Companion.long") {
        val ops = Log2.long

        context("floorLog2") {
            test("floor(1L) = 0") { with(ops) { 1L.floorLog2() } shouldBe 0 }
            test("floor(3L) = 1") { with(ops) { 3L.floorLog2() } shouldBe 1 }
            test("floor(Long.MAX_VALUE) = 62") { with(ops) { Long.MAX_VALUE.floorLog2() } shouldBe 62 }
            test("floor(0L) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0L.floorLog2() } } }
            test("floor(-1L) throws") { shouldThrow<IllegalArgumentException> { with(ops) { (-1L).floorLog2() } } }
        }

        context("ceilLog2") {
            test("ceil(1L) = 0") { with(ops) { 1L.ceilLog2() } shouldBe 0 }
            test("ceil(3L) = 2") { with(ops) { 3L.ceilLog2() } shouldBe 2 }
            test("ceil(Long.MAX_VALUE) = 63") { with(ops) { Long.MAX_VALUE.ceilLog2() } shouldBe 63 }
            test("ceil(0L) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0L.ceilLog2() } } }
        }
    }

    // ── Log2.Companion.uint (unsigned) ────────────────────────────────────────

    context("Log2.Companion.uint") {
        val ops = Log2.uint

        context("floorLog2") {
            test("floor(1u) = 0") { with(ops) { 1u.floorLog2() } shouldBe 0 }
            test("floor(3u) = 1") { with(ops) { 3u.floorLog2() } shouldBe 1 }
            test("floor(UInt.MAX_VALUE) = 31") { with(ops) { UInt.MAX_VALUE.floorLog2() } shouldBe 31 }
            test("floor(0u) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0u.floorLog2() } } }
        }

        context("ceilLog2") {
            test("ceil(1u) = 0") { with(ops) { 1u.ceilLog2() } shouldBe 0 }
            test("ceil(3u) = 2") { with(ops) { 3u.ceilLog2() } shouldBe 2 }
            test("ceil(UInt.MAX_VALUE) = 32") { with(ops) { UInt.MAX_VALUE.ceilLog2() } shouldBe 32 }
            test("ceil(0u) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0u.ceilLog2() } } }
        }
    }

    // ── Log2.Companion.ulong (unsigned) ───────────────────────────────────────

    context("Log2.Companion.ulong") {
        val ops = Log2.ulong

        context("floorLog2") {
            test("floor(1uL) = 0") { with(ops) { 1uL.floorLog2() } shouldBe 0 }
            test("floor(3uL) = 1") { with(ops) { 3uL.floorLog2() } shouldBe 1 }
            test("floor(ULong.MAX_VALUE) = 63") { with(ops) { ULong.MAX_VALUE.floorLog2() } shouldBe 63 }
            test("floor(0uL) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0uL.floorLog2() } } }
        }

        context("ceilLog2") {
            test("ceil(1uL) = 0") { with(ops) { 1uL.ceilLog2() } shouldBe 0 }
            test("ceil(3uL) = 2") { with(ops) { 3uL.ceilLog2() } shouldBe 2 }
            test("ceil(ULong.MAX_VALUE) = 64") { with(ops) { ULong.MAX_VALUE.ceilLog2() } shouldBe 64 }
            test("ceil(0uL) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0uL.ceilLog2() } } }
        }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("Log2.int is stable") { Log2.int shouldBe Log2.int }
        test("Log2.long is stable") { Log2.long shouldBe Log2.long }
        test("Log2.uint is stable") { Log2.uint shouldBe Log2.uint }
        test("Log2.ulong is stable") { Log2.ulong shouldBe Log2.ulong }
        test("Log2.int and Log2.long are distinct") { Log2.int shouldNotBe Log2.long }
        test("Log2.int and Log2.uint are distinct") { (Log2.int as Any) shouldNotBe (Log2.uint as Any) }
    }
})
