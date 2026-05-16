package com.kelvsyc.kotlin.core.traits.integral

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class OverflowCheckedLongTest : FunSpec({

    context("OverflowCheckedArithmetic.long") {
        val ops = OverflowCheckedArithmetic.long

        context("add") {
            test("within range") { with(ops) { (Long.MAX_VALUE - 1L).add(1L) } shouldBe Long.MAX_VALUE }
            test("MAX_VALUE + 1 throws") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MAX_VALUE.add(1L) } }
            }
            test("MIN_VALUE + (-1) throws") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MIN_VALUE.add(-1L) } }
            }
        }

        context("subtract") {
            test("within range") { with(ops) { (Long.MIN_VALUE + 1L).subtract(1L) } shouldBe Long.MIN_VALUE }
            test("MIN_VALUE - 1 throws") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MIN_VALUE.subtract(1L) } }
            }
            test("MAX_VALUE - (-1) throws") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MAX_VALUE.subtract(-1L) } }
            }
        }

        context("multiply") {
            test("within range") { with(ops) { 1000L.multiply(1000L) } shouldBe 1_000_000L }
            test("MAX_VALUE * 2 throws") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MAX_VALUE.multiply(2L) } }
            }
            test("MIN_VALUE * 2 throws") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MIN_VALUE.multiply(2L) } }
            }
        }

        context("divide") {
            test("normal division") { with(ops) { 10L.divide(3L) } shouldBe 3L }
            test("divide by zero throws") {
                shouldThrow<ArithmeticException> { with(ops) { 1L.divide(0L) } }
            }
            test("MIN_VALUE / -1 throws") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MIN_VALUE.divide(-1L) } }
            }
        }
    }

    context("OverflowCheckedSignedArithmetic.long") {
        val ops = OverflowCheckedSignedArithmetic.long

        context("negate") {
            test("normal negate") { with(ops) { 5L.negate() } shouldBe -5L }
            test("MIN_VALUE negate throws") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MIN_VALUE.negate() } }
            }
        }

        context("abs") {
            test("negative value") { with(ops) { (-5L).abs() } shouldBe 5L }
            test("MIN_VALUE abs throws") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MIN_VALUE.abs() } }
            }
        }

        context("arithmetic (inherited overflow checks)") {
            test("MAX_VALUE + 1 throws") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MAX_VALUE.add(1L) } }
            }
            test("MIN_VALUE / -1 throws") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MIN_VALUE.divide(-1L) } }
            }
        }
    }
})
