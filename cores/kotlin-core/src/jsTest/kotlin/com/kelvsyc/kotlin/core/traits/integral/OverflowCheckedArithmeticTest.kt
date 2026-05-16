package com.kelvsyc.kotlin.core.traits.integral

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class OverflowCheckedArithmeticTest : FunSpec({
    context("OverflowCheckedArithmetic.int") {
        val ops = OverflowCheckedArithmetic.int

        context("add") {
            test("within range succeeds") {
                with(ops) { (Int.MAX_VALUE - 1).add(1) } shouldBe Int.MAX_VALUE
            }
            test("MAX_VALUE + 1 throws") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MAX_VALUE.add(1) } }
            }
            test("MIN_VALUE + (-1) throws") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MIN_VALUE.add(-1) } }
            }
        }

        context("subtract") {
            test("within range succeeds") {
                with(ops) { (Int.MIN_VALUE + 1).subtract(1) } shouldBe Int.MIN_VALUE
            }
            test("MIN_VALUE - 1 throws") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MIN_VALUE.subtract(1) } }
            }
            test("MAX_VALUE - (-1) throws") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MAX_VALUE.subtract(-1) } }
            }
        }

        context("multiply") {
            test("within range succeeds") {
                with(ops) { 100.multiply(100) } shouldBe 10000
            }
            test("MAX_VALUE * 2 throws") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MAX_VALUE.multiply(2) } }
            }
            test("MIN_VALUE * 2 throws") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MIN_VALUE.multiply(2) } }
            }
        }

        context("divide") {
            test("normal division succeeds") {
                with(ops) { 10.divide(3) } shouldBe 3
            }
            test("divide by zero throws") {
                shouldThrow<ArithmeticException> { with(ops) { 1.divide(0) } }
            }
            test("MIN_VALUE / -1 throws") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MIN_VALUE.divide(-1) } }
            }
        }
    }

    context("OverflowCheckedSignedArithmetic.int") {
        val ops = OverflowCheckedSignedArithmetic.int

        context("negate") {
            test("normal negate succeeds") {
                with(ops) { 5.negate() } shouldBe -5
            }
            test("MIN_VALUE negate throws") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MIN_VALUE.negate() } }
            }
        }

        context("abs") {
            test("negative value") {
                with(ops) { (-5).abs() } shouldBe 5
            }
            test("MIN_VALUE abs throws") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MIN_VALUE.abs() } }
            }
        }

        context("arithmetic (inherited overflow checks)") {
            test("MAX_VALUE + 1 throws") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MAX_VALUE.add(1) } }
            }
            test("MIN_VALUE / -1 throws") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MIN_VALUE.divide(-1) } }
            }
        }
    }
})
