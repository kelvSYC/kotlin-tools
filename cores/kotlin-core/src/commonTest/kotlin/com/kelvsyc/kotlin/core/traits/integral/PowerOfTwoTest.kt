package com.kelvsyc.kotlin.core.traits.integral

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class PowerOfTwoTest : FunSpec({

    // ── PowerOfTwo.Companion.int (signed) ─────────────────────────────────────

    context("PowerOfTwo.Companion.int") {
        val ops = PowerOfTwo.int

        context("isPowerOfTwo") {
            test("1 is a power of two") { with(ops) { 1.isPowerOfTwo() } shouldBe true }
            test("2 is a power of two") { with(ops) { 2.isPowerOfTwo() } shouldBe true }
            test("1024 is a power of two") { with(ops) { 1024.isPowerOfTwo() } shouldBe true }
            test("2^30 is a power of two") { with(ops) { (1 shl 30).isPowerOfTwo() } shouldBe true }
            test("3 is not a power of two") { with(ops) { 3.isPowerOfTwo() } shouldBe false }
            test("6 is not a power of two") { with(ops) { 6.isPowerOfTwo() } shouldBe false }
            test("0 is not a power of two") { with(ops) { 0.isPowerOfTwo() } shouldBe false }
            test("-1 is not a power of two") { with(ops) { (-1).isPowerOfTwo() } shouldBe false }
        }

        context("floorPowerOfTwo") {
            test("floor(1) = 1") { with(ops) { 1.floorPowerOfTwo() } shouldBe 1 }
            test("floor(2) = 2") { with(ops) { 2.floorPowerOfTwo() } shouldBe 2 }
            test("floor(3) = 2") { with(ops) { 3.floorPowerOfTwo() } shouldBe 2 }
            test("floor(4) = 4") { with(ops) { 4.floorPowerOfTwo() } shouldBe 4 }
            test("floor(5) = 4") { with(ops) { 5.floorPowerOfTwo() } shouldBe 4 }
            test("floor(1000) = 512") { with(ops) { 1000.floorPowerOfTwo() } shouldBe 512 }
            test("floor(Int.MAX_VALUE) = 2^30") { with(ops) { Int.MAX_VALUE.floorPowerOfTwo() } shouldBe (1 shl 30) }
            test("floor(0) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0.floorPowerOfTwo() } } }
            test("floor(-1) throws") { shouldThrow<IllegalArgumentException> { with(ops) { (-1).floorPowerOfTwo() } } }
        }

        context("ceilingPowerOfTwo") {
            test("ceiling(1) = 1") { with(ops) { 1.ceilingPowerOfTwo() } shouldBe 1 }
            test("ceiling(2) = 2") { with(ops) { 2.ceilingPowerOfTwo() } shouldBe 2 }
            test("ceiling(3) = 4") { with(ops) { 3.ceilingPowerOfTwo() } shouldBe 4 }
            test("ceiling(4) = 4") { with(ops) { 4.ceilingPowerOfTwo() } shouldBe 4 }
            test("ceiling(5) = 8") { with(ops) { 5.ceilingPowerOfTwo() } shouldBe 8 }
            test("ceiling(1000) = 1024") { with(ops) { 1000.ceilingPowerOfTwo() } shouldBe 1024 }
            test("ceiling(2^30) = 2^30") { with(ops) { (1 shl 30).ceilingPowerOfTwo() } shouldBe (1 shl 30) }
            test("ceiling(2^30 + 1) overflows") { shouldThrow<ArithmeticException> { with(ops) { ((1 shl 30) + 1).ceilingPowerOfTwo() } } }
            test("ceiling(0) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0.ceilingPowerOfTwo() } } }
            test("ceiling(-1) throws") { shouldThrow<IllegalArgumentException> { with(ops) { (-1).ceilingPowerOfTwo() } } }
        }
    }

    // ── PowerOfTwo.Companion.long (signed) ────────────────────────────────────

    context("PowerOfTwo.Companion.long") {
        val ops = PowerOfTwo.long

        context("isPowerOfTwo") {
            test("1L is a power of two") { with(ops) { 1L.isPowerOfTwo() } shouldBe true }
            test("2^62 is a power of two") { with(ops) { (1L shl 62).isPowerOfTwo() } shouldBe true }
            test("3L is not a power of two") { with(ops) { 3L.isPowerOfTwo() } shouldBe false }
            test("0L is not a power of two") { with(ops) { 0L.isPowerOfTwo() } shouldBe false }
            test("-1L is not a power of two") { with(ops) { (-1L).isPowerOfTwo() } shouldBe false }
        }

        context("floorPowerOfTwo") {
            test("floor(1L) = 1L") { with(ops) { 1L.floorPowerOfTwo() } shouldBe 1L }
            test("floor(3L) = 2L") { with(ops) { 3L.floorPowerOfTwo() } shouldBe 2L }
            test("floor(Long.MAX_VALUE) = 2^62") { with(ops) { Long.MAX_VALUE.floorPowerOfTwo() } shouldBe (1L shl 62) }
            test("floor(0L) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0L.floorPowerOfTwo() } } }
        }

        context("ceilingPowerOfTwo") {
            test("ceiling(1L) = 1L") { with(ops) { 1L.ceilingPowerOfTwo() } shouldBe 1L }
            test("ceiling(3L) = 4L") { with(ops) { 3L.ceilingPowerOfTwo() } shouldBe 4L }
            test("ceiling(2^62) = 2^62") { with(ops) { (1L shl 62).ceilingPowerOfTwo() } shouldBe (1L shl 62) }
            test("ceiling(2^62 + 1) overflows") { shouldThrow<ArithmeticException> { with(ops) { ((1L shl 62) + 1L).ceilingPowerOfTwo() } } }
            test("ceiling(0L) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0L.ceilingPowerOfTwo() } } }
        }
    }

    // ── PowerOfTwo.Companion.uint (unsigned) ──────────────────────────────────

    context("PowerOfTwo.Companion.uint") {
        val ops = PowerOfTwo.uint

        context("isPowerOfTwo") {
            test("1u is a power of two") { with(ops) { 1u.isPowerOfTwo() } shouldBe true }
            test("2^31 is a power of two") { with(ops) { (1u shl 31).isPowerOfTwo() } shouldBe true }
            test("3u is not a power of two") { with(ops) { 3u.isPowerOfTwo() } shouldBe false }
            test("0u is not a power of two") { with(ops) { 0u.isPowerOfTwo() } shouldBe false }
        }

        context("floorPowerOfTwo") {
            test("floor(1u) = 1u") { with(ops) { 1u.floorPowerOfTwo() } shouldBe 1u }
            test("floor(3u) = 2u") { with(ops) { 3u.floorPowerOfTwo() } shouldBe 2u }
            test("floor(UInt.MAX_VALUE) = 2^31") { with(ops) { UInt.MAX_VALUE.floorPowerOfTwo() } shouldBe (1u shl 31) }
            test("floor(0u) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0u.floorPowerOfTwo() } } }
        }

        context("ceilingPowerOfTwo") {
            test("ceiling(1u) = 1u") { with(ops) { 1u.ceilingPowerOfTwo() } shouldBe 1u }
            test("ceiling(3u) = 4u") { with(ops) { 3u.ceilingPowerOfTwo() } shouldBe 4u }
            test("ceiling(2^31) = 2^31") { with(ops) { (1u shl 31).ceilingPowerOfTwo() } shouldBe (1u shl 31) }
            test("ceiling(2^31 + 1) overflows") { shouldThrow<ArithmeticException> { with(ops) { ((1u shl 31) + 1u).ceilingPowerOfTwo() } } }
            test("ceiling(0u) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0u.ceilingPowerOfTwo() } } }
        }
    }

    // ── PowerOfTwo.Companion.ulong (unsigned) ─────────────────────────────────

    context("PowerOfTwo.Companion.ulong") {
        val ops = PowerOfTwo.ulong

        context("isPowerOfTwo") {
            test("1uL is a power of two") { with(ops) { 1uL.isPowerOfTwo() } shouldBe true }
            test("2^63 is a power of two") { with(ops) { (1uL shl 63).isPowerOfTwo() } shouldBe true }
            test("3uL is not a power of two") { with(ops) { 3uL.isPowerOfTwo() } shouldBe false }
            test("0uL is not a power of two") { with(ops) { 0uL.isPowerOfTwo() } shouldBe false }
        }

        context("floorPowerOfTwo") {
            test("floor(1uL) = 1uL") { with(ops) { 1uL.floorPowerOfTwo() } shouldBe 1uL }
            test("floor(3uL) = 2uL") { with(ops) { 3uL.floorPowerOfTwo() } shouldBe 2uL }
            test("floor(ULong.MAX_VALUE) = 2^63") { with(ops) { ULong.MAX_VALUE.floorPowerOfTwo() } shouldBe (1uL shl 63) }
            test("floor(0uL) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0uL.floorPowerOfTwo() } } }
        }

        context("ceilingPowerOfTwo") {
            test("ceiling(1uL) = 1uL") { with(ops) { 1uL.ceilingPowerOfTwo() } shouldBe 1uL }
            test("ceiling(3uL) = 4uL") { with(ops) { 3uL.ceilingPowerOfTwo() } shouldBe 4uL }
            test("ceiling(2^63) = 2^63") { with(ops) { (1uL shl 63).ceilingPowerOfTwo() } shouldBe (1uL shl 63) }
            test("ceiling(2^63 + 1) overflows") { shouldThrow<ArithmeticException> { with(ops) { ((1uL shl 63) + 1uL).ceilingPowerOfTwo() } } }
            test("ceiling(0uL) throws") { shouldThrow<IllegalArgumentException> { with(ops) { 0uL.ceilingPowerOfTwo() } } }
        }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("PowerOfTwo.int is stable") { PowerOfTwo.int shouldBe PowerOfTwo.int }
        test("PowerOfTwo.long is stable") { PowerOfTwo.long shouldBe PowerOfTwo.long }
        test("PowerOfTwo.uint is stable") { PowerOfTwo.uint shouldBe PowerOfTwo.uint }
        test("PowerOfTwo.ulong is stable") { PowerOfTwo.ulong shouldBe PowerOfTwo.ulong }
        test("PowerOfTwo.int and PowerOfTwo.long are distinct") { PowerOfTwo.int shouldNotBe PowerOfTwo.long }
        test("PowerOfTwo.int and PowerOfTwo.uint are distinct") { (PowerOfTwo.int as Any) shouldNotBe (PowerOfTwo.uint as Any) }
    }
})
