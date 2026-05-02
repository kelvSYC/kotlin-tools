package com.kelvsyc.kotlin.guava

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigInteger
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

    context("Double.roundToInt") {
        test("exact integer: unnecessary") { 3.0.roundToInt(RoundingMode.UNNECESSARY) shouldBe 3 }
        test("2.5 ceiling rounds up") { 2.5.roundToInt(RoundingMode.CEILING) shouldBe 3 }
        test("2.5 floor rounds down") { 2.5.roundToInt(RoundingMode.FLOOR) shouldBe 2 }
        test("2.5 half_up rounds up") { 2.5.roundToInt(RoundingMode.HALF_UP) shouldBe 3 }
        test("2.5 half_down rounds down") { 2.5.roundToInt(RoundingMode.HALF_DOWN) shouldBe 2 }
        test("2.5 half_even rounds to even (2)") { 2.5.roundToInt(RoundingMode.HALF_EVEN) shouldBe 2 }
        test("3.5 half_even rounds to even (4)") { 3.5.roundToInt(RoundingMode.HALF_EVEN) shouldBe 4 }
        test("non-integer: unnecessary throws") {
            shouldThrow<ArithmeticException> { 2.5.roundToInt(RoundingMode.UNNECESSARY) }
        }
        test("infinity throws") { shouldThrow<ArithmeticException> { Double.POSITIVE_INFINITY.roundToInt(RoundingMode.FLOOR) } }
        test("NaN throws") { shouldThrow<ArithmeticException> { Double.NaN.roundToInt(RoundingMode.FLOOR) } }
    }

    context("Double.roundToLong") {
        test("exact integer: unnecessary") { 3.0.roundToLong(RoundingMode.UNNECESSARY) shouldBe 3L }
        test("2.5 half_even rounds to even (2)") { 2.5.roundToLong(RoundingMode.HALF_EVEN) shouldBe 2L }
        test("large value: floor") {
            // 2^53 is exactly representable
            9007199254740992.0.roundToLong(RoundingMode.UNNECESSARY) shouldBe 9007199254740992L
        }
        test("non-integer: unnecessary throws") {
            shouldThrow<ArithmeticException> { 2.5.roundToLong(RoundingMode.UNNECESSARY) }
        }
        test("infinity throws") { shouldThrow<ArithmeticException> { Double.POSITIVE_INFINITY.roundToLong(RoundingMode.FLOOR) } }
    }

    context("Double.roundToBigInteger") {
        test("exact integer: unnecessary") {
            3.0.roundToBigInteger(RoundingMode.UNNECESSARY) shouldBe BigInteger.valueOf(3L)
        }
        test("2.5 floor rounds down") { 2.5.roundToBigInteger(RoundingMode.FLOOR) shouldBe BigInteger.TWO }
        test("2.5 ceiling rounds up") { 2.5.roundToBigInteger(RoundingMode.CEILING) shouldBe BigInteger.valueOf(3L) }
        test("non-integer: unnecessary throws") {
            shouldThrow<ArithmeticException> { 2.5.roundToBigInteger(RoundingMode.UNNECESSARY) }
        }
        test("infinity throws") {
            shouldThrow<ArithmeticException> { Double.POSITIVE_INFINITY.roundToBigInteger(RoundingMode.FLOOR) }
        }
        test("NaN throws") {
            shouldThrow<ArithmeticException> { Double.NaN.roundToBigInteger(RoundingMode.FLOOR) }
        }
    }
})
