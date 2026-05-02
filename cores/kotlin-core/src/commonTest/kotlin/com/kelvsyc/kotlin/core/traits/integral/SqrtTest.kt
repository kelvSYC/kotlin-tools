package com.kelvsyc.kotlin.core.traits.integral

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class SqrtTest : FunSpec({

    // ── Sqrt.Companion.int (signed) ───────────────────────────────────────────

    context("Sqrt.Companion.int") {
        val ops = Sqrt.int

        context("floorSqrt") {
            test("floor(0) = 0") { with(ops) { 0.floorSqrt() } shouldBe 0 }
            test("floor(1) = 1") { with(ops) { 1.floorSqrt() } shouldBe 1 }
            test("floor(2) = 1") { with(ops) { 2.floorSqrt() } shouldBe 1 }
            test("floor(3) = 1") { with(ops) { 3.floorSqrt() } shouldBe 1 }
            test("floor(4) = 2") { with(ops) { 4.floorSqrt() } shouldBe 2 }
            test("floor(8) = 2") { with(ops) { 8.floorSqrt() } shouldBe 2 }
            test("floor(9) = 3") { with(ops) { 9.floorSqrt() } shouldBe 3 }
            test("floor(100) = 10") { with(ops) { 100.floorSqrt() } shouldBe 10 }
            test("floor(Int.MAX_VALUE) = 46340") { with(ops) { Int.MAX_VALUE.floorSqrt() } shouldBe 46340 }
            test("negative throws") { shouldThrow<IllegalArgumentException> { with(ops) { (-1).floorSqrt() } } }
        }

        context("ceilSqrt") {
            test("ceil(0) = 0") { with(ops) { 0.ceilSqrt() } shouldBe 0 }
            test("ceil(1) = 1") { with(ops) { 1.ceilSqrt() } shouldBe 1 }
            test("ceil(2) = 2") { with(ops) { 2.ceilSqrt() } shouldBe 2 }
            test("ceil(3) = 2") { with(ops) { 3.ceilSqrt() } shouldBe 2 }
            test("ceil(4) = 2") { with(ops) { 4.ceilSqrt() } shouldBe 2 }
            test("ceil(5) = 3") { with(ops) { 5.ceilSqrt() } shouldBe 3 }
            test("ceil(9) = 3") { with(ops) { 9.ceilSqrt() } shouldBe 3 }
            test("ceil(100) = 10") { with(ops) { 100.ceilSqrt() } shouldBe 10 }
            test("ceil(Int.MAX_VALUE) = 46341") { with(ops) { Int.MAX_VALUE.ceilSqrt() } shouldBe 46341 }
            test("negative throws") { shouldThrow<IllegalArgumentException> { with(ops) { (-1).ceilSqrt() } } }
        }
    }

    // ── Sqrt.Companion.long (signed) ──────────────────────────────────────────

    context("Sqrt.Companion.long") {
        val ops = Sqrt.long

        context("floorSqrt") {
            test("floor(0L) = 0") { with(ops) { 0L.floorSqrt() } shouldBe 0L }
            test("floor(1L) = 1") { with(ops) { 1L.floorSqrt() } shouldBe 1L }
            test("floor(4L) = 2") { with(ops) { 4L.floorSqrt() } shouldBe 2L }
            test("floor(Long.MAX_VALUE) = 3037000499") {
                with(ops) { Long.MAX_VALUE.floorSqrt() } shouldBe 3037000499L
            }
            test("negative throws") { shouldThrow<IllegalArgumentException> { with(ops) { (-1L).floorSqrt() } } }
        }

        context("ceilSqrt") {
            test("ceil(0L) = 0") { with(ops) { 0L.ceilSqrt() } shouldBe 0L }
            test("ceil(4L) = 2") { with(ops) { 4L.ceilSqrt() } shouldBe 2L }
            test("ceil(5L) = 3") { with(ops) { 5L.ceilSqrt() } shouldBe 3L }
            test("ceil(Long.MAX_VALUE) = 3037000500") {
                with(ops) { Long.MAX_VALUE.ceilSqrt() } shouldBe 3037000500L
            }
            test("negative throws") { shouldThrow<IllegalArgumentException> { with(ops) { (-1L).ceilSqrt() } } }
        }
    }

    // ── Sqrt.Companion.uint (unsigned) ────────────────────────────────────────

    context("Sqrt.Companion.uint") {
        val ops = Sqrt.uint

        context("floorSqrt") {
            test("floor(0u) = 0") { with(ops) { 0u.floorSqrt() } shouldBe 0u }
            test("floor(4u) = 2") { with(ops) { 4u.floorSqrt() } shouldBe 2u }
            test("floor(UInt.MAX_VALUE) = 65535") { with(ops) { UInt.MAX_VALUE.floorSqrt() } shouldBe 65535u }
        }

        context("ceilSqrt") {
            test("ceil(0u) = 0") { with(ops) { 0u.ceilSqrt() } shouldBe 0u }
            test("ceil(4u) = 2") { with(ops) { 4u.ceilSqrt() } shouldBe 2u }
            test("ceil(5u) = 3") { with(ops) { 5u.ceilSqrt() } shouldBe 3u }
            test("ceil(UInt.MAX_VALUE) = 65536") { with(ops) { UInt.MAX_VALUE.ceilSqrt() } shouldBe 65536u }
        }
    }

    // ── Sqrt.Companion.ulong (unsigned) ───────────────────────────────────────

    context("Sqrt.Companion.ulong") {
        val ops = Sqrt.ulong

        context("floorSqrt") {
            test("floor(0uL) = 0") { with(ops) { 0uL.floorSqrt() } shouldBe 0uL }
            test("floor(4uL) = 2") { with(ops) { 4uL.floorSqrt() } shouldBe 2uL }
            test("floor(ULong.MAX_VALUE) = 4294967295") {
                with(ops) { ULong.MAX_VALUE.floorSqrt() } shouldBe 4294967295uL
            }
        }

        context("ceilSqrt") {
            test("ceil(0uL) = 0") { with(ops) { 0uL.ceilSqrt() } shouldBe 0uL }
            test("ceil(4uL) = 2") { with(ops) { 4uL.ceilSqrt() } shouldBe 2uL }
            test("ceil(5uL) = 3") { with(ops) { 5uL.ceilSqrt() } shouldBe 3uL }
            test("ceil(ULong.MAX_VALUE) = 4294967296") {
                with(ops) { ULong.MAX_VALUE.ceilSqrt() } shouldBe 4294967296uL
            }
        }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("Sqrt.int is stable") { Sqrt.int shouldBe Sqrt.int }
        test("Sqrt.long is stable") { Sqrt.long shouldBe Sqrt.long }
        test("Sqrt.uint is stable") { Sqrt.uint shouldBe Sqrt.uint }
        test("Sqrt.ulong is stable") { Sqrt.ulong shouldBe Sqrt.ulong }
        test("Sqrt.int and Sqrt.long are distinct") { Sqrt.int shouldNotBe Sqrt.long }
        test("Sqrt.int and Sqrt.uint are distinct") { (Sqrt.int as Any) shouldNotBe (Sqrt.uint as Any) }
    }
})
