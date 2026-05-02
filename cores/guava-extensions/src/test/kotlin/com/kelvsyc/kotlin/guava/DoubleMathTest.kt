package com.kelvsyc.kotlin.guava

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.RoundingMode

class DoubleMathTest : FunSpec({

    context("Double.log2") {
        test("exact power of two: floor") { 4.0.log2(RoundingMode.FLOOR) shouldBe 2 }
        test("exact power of two: ceiling") { 4.0.log2(RoundingMode.CEILING) shouldBe 2 }
        test("exact power of two: unnecessary") { 4.0.log2(RoundingMode.UNNECESSARY) shouldBe 2 }

        test("non-power of two: floor rounds down") { 5.0.log2(RoundingMode.FLOOR) shouldBe 2 }
        test("non-power of two: ceiling rounds up") { 5.0.log2(RoundingMode.CEILING) shouldBe 3 }
        test("non-power of two: unnecessary throws") {
            shouldThrow<ArithmeticException> { 5.0.log2(RoundingMode.UNNECESSARY) }
        }

        test("fractional value: floor") { 2.5.log2(RoundingMode.FLOOR) shouldBe 1 }
        test("fractional value: ceiling") { 2.5.log2(RoundingMode.CEILING) shouldBe 2 }

        test("non-positive throws") { shouldThrow<IllegalArgumentException> { 0.0.log2(RoundingMode.FLOOR) } }
        test("negative throws") { shouldThrow<IllegalArgumentException> { (-1.0).log2(RoundingMode.FLOOR) } }
        test("NaN throws") { shouldThrow<IllegalArgumentException> { Double.NaN.log2(RoundingMode.FLOOR) } }
        test("positive infinity throws") {
            shouldThrow<IllegalArgumentException> { Double.POSITIVE_INFINITY.log2(RoundingMode.FLOOR) }
        }
    }
})
