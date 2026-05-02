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

    // ── Int.log10 ─────────────────────────────────────────────────────────────

    context("Int.log10") {
        test("exact power of ten: floor") { 100.log10(RoundingMode.FLOOR) shouldBe 2 }
        test("exact power of ten: ceiling") { 100.log10(RoundingMode.CEILING) shouldBe 2 }
        test("exact power of ten: unnecessary") { 100.log10(RoundingMode.UNNECESSARY) shouldBe 2 }

        test("non-power of ten: floor rounds down") { 101.log10(RoundingMode.FLOOR) shouldBe 2 }
        test("non-power of ten: ceiling rounds up") { 101.log10(RoundingMode.CEILING) shouldBe 3 }
        test("non-power of ten: unnecessary throws") {
            shouldThrow<ArithmeticException> { 101.log10(RoundingMode.UNNECESSARY) }
        }

        test("non-positive throws") { shouldThrow<IllegalArgumentException> { 0.log10(RoundingMode.FLOOR) } }
        test("negative throws") { shouldThrow<IllegalArgumentException> { (-1).log10(RoundingMode.FLOOR) } }
    }

    // ── Int.sqrt ──────────────────────────────────────────────────────────────

    context("Int.sqrt") {
        test("exact square: floor") { 9.sqrt(RoundingMode.FLOOR) shouldBe 3 }
        test("exact square: ceiling") { 9.sqrt(RoundingMode.CEILING) shouldBe 3 }
        test("exact square: unnecessary") { 9.sqrt(RoundingMode.UNNECESSARY) shouldBe 3 }

        test("non-square: floor rounds down") { 8.sqrt(RoundingMode.FLOOR) shouldBe 2 }
        test("non-square: ceiling rounds up") { 8.sqrt(RoundingMode.CEILING) shouldBe 3 }
        test("non-square: unnecessary throws") {
            shouldThrow<ArithmeticException> { 8.sqrt(RoundingMode.UNNECESSARY) }
        }

        test("zero: floor") { 0.sqrt(RoundingMode.FLOOR) shouldBe 0 }
        test("negative throws") { shouldThrow<IllegalArgumentException> { (-1).sqrt(RoundingMode.FLOOR) } }
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

    // ── Long.log10 ────────────────────────────────────────────────────────────

    context("Long.log10") {
        test("exact power of ten: floor") { 1000L.log10(RoundingMode.FLOOR) shouldBe 3 }
        test("exact power of ten: ceiling") { 1000L.log10(RoundingMode.CEILING) shouldBe 3 }
        test("exact power of ten: unnecessary") { 1000L.log10(RoundingMode.UNNECESSARY) shouldBe 3 }

        test("non-power of ten: floor rounds down") { 1001L.log10(RoundingMode.FLOOR) shouldBe 3 }
        test("non-power of ten: ceiling rounds up") { 1001L.log10(RoundingMode.CEILING) shouldBe 4 }
        test("non-power of ten: unnecessary throws") {
            shouldThrow<ArithmeticException> { 1001L.log10(RoundingMode.UNNECESSARY) }
        }

        test("large value: floor") { Long.MAX_VALUE.log10(RoundingMode.FLOOR) shouldBe 18 }
        test("large value: ceiling") { Long.MAX_VALUE.log10(RoundingMode.CEILING) shouldBe 19 }

        test("non-positive throws") { shouldThrow<IllegalArgumentException> { 0L.log10(RoundingMode.FLOOR) } }
    }

    // ── Long.roundToDouble ────────────────────────────────────────────────────

    context("Long.roundToDouble") {
        test("exact representable value: floor") {
            // 2^53 is exactly representable as Double
            (1L shl 53).roundToDouble(RoundingMode.FLOOR) shouldBe 9007199254740992.0
        }
        test("value requiring rounding: floor rounds toward negative infinity") {
            // 2^53 + 1 cannot be represented exactly; floor rounds down
            ((1L shl 53) + 1L).roundToDouble(RoundingMode.FLOOR) shouldBe 9007199254740992.0
        }
        test("value requiring rounding: ceiling rounds toward positive infinity") {
            ((1L shl 53) + 1L).roundToDouble(RoundingMode.CEILING) shouldBe 9007199254740994.0
        }
    }

    // ── Long.sqrt ─────────────────────────────────────────────────────────────

    context("Long.sqrt") {
        test("exact square: floor") { 25L.sqrt(RoundingMode.FLOOR) shouldBe 5L }
        test("exact square: ceiling") { 25L.sqrt(RoundingMode.CEILING) shouldBe 5L }
        test("exact square: unnecessary") { 25L.sqrt(RoundingMode.UNNECESSARY) shouldBe 5L }

        test("non-square: floor rounds down") { 26L.sqrt(RoundingMode.FLOOR) shouldBe 5L }
        test("non-square: ceiling rounds up") { 26L.sqrt(RoundingMode.CEILING) shouldBe 6L }
        test("non-square: unnecessary throws") {
            shouldThrow<ArithmeticException> { 26L.sqrt(RoundingMode.UNNECESSARY) }
        }

        test("large value: floor") { Long.MAX_VALUE.sqrt(RoundingMode.FLOOR) shouldBe 3037000499L }
        test("large value: ceiling") { Long.MAX_VALUE.sqrt(RoundingMode.CEILING) shouldBe 3037000500L }

        test("zero: floor") { 0L.sqrt(RoundingMode.FLOOR) shouldBe 0L }
        test("negative throws") { shouldThrow<IllegalArgumentException> { (-1L).sqrt(RoundingMode.FLOOR) } }
    }
})
