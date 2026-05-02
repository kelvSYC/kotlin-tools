package com.kelvsyc.kotlin.guava

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.RoundingMode

class IntegerMathTest : FunSpec({

    // ── Int.log2 ──────────────────────────────────────────────────────────────

    context("Int.log2") {
        test("exact power of two: floor") { 4.log2(RoundingMode.FLOOR) shouldBe 2 }
        test("exact power of two: ceiling") { 4.log2(RoundingMode.CEILING) shouldBe 2 }
        test("exact power of two: unnecessary") { 4.log2(RoundingMode.UNNECESSARY) shouldBe 2 }

        test("non-power of two: floor rounds down") { 5.log2(RoundingMode.FLOOR) shouldBe 2 }
        test("non-power of two: ceiling rounds up") { 5.log2(RoundingMode.CEILING) shouldBe 3 }
        test("non-power of two: unnecessary throws") {
            shouldThrow<ArithmeticException> { 5.log2(RoundingMode.UNNECESSARY) }
        }

        test("half_up below geometric midpoint rounds down") {
            // geometric midpoint of 2^2 and 2^3 is sqrt(32) ≈ 5.657; 5 < 5.657 → floor
            5.log2(RoundingMode.HALF_UP) shouldBe 2
        }
        test("half_up above geometric midpoint rounds up") {
            // 6 > 5.657 → ceil
            6.log2(RoundingMode.HALF_UP) shouldBe 3
        }

        test("non-positive throws") { shouldThrow<IllegalArgumentException> { 0.log2(RoundingMode.FLOOR) } }
        test("negative throws") { shouldThrow<IllegalArgumentException> { (-1).log2(RoundingMode.FLOOR) } }
    }

    // ── Long.log2 ─────────────────────────────────────────────────────────────

    context("Long.log2") {
        test("exact power of two: floor") { 4L.log2(RoundingMode.FLOOR) shouldBe 2 }
        test("exact power of two: ceiling") { 4L.log2(RoundingMode.CEILING) shouldBe 2 }
        test("exact power of two: unnecessary") { 4L.log2(RoundingMode.UNNECESSARY) shouldBe 2 }

        test("non-power of two: floor rounds down") { 5L.log2(RoundingMode.FLOOR) shouldBe 2 }
        test("non-power of two: ceiling rounds up") { 5L.log2(RoundingMode.CEILING) shouldBe 3 }
        test("non-power of two: unnecessary throws") {
            shouldThrow<ArithmeticException> { 5L.log2(RoundingMode.UNNECESSARY) }
        }

        test("large value: floor") { Long.MAX_VALUE.log2(RoundingMode.FLOOR) shouldBe 62 }
        test("large value: ceiling") { Long.MAX_VALUE.log2(RoundingMode.CEILING) shouldBe 63 }

        test("non-positive throws") { shouldThrow<IllegalArgumentException> { 0L.log2(RoundingMode.FLOOR) } }
    }
})
