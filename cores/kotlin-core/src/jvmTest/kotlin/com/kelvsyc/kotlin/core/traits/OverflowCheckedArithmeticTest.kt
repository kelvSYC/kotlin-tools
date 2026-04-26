package com.kelvsyc.kotlin.core.traits

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class OverflowCheckedArithmeticTest : FunSpec({

    // ── OverflowCheckedArithmetic.Companion.int ───────────────────────────────

    context("OverflowCheckedArithmetic.Companion.int") {
        val ops = OverflowCheckedArithmetic.int

        context("constants") {
            test("zero is 0") { ops.zero shouldBe 0 }
            test("one is 1") { ops.one shouldBe 1 }
        }

        context("add") {
            test("2 + 3 = 5") { with(ops) { 2.add(3) } shouldBe 5 }
            test("zero + x = x") { with(ops) { 0.add(7) } shouldBe 7 }
            test("negative addend") { with(ops) { 5.add(-3) } shouldBe 2 }
            test("MAX_VALUE + 0 does not throw") {
                with(ops) { Int.MAX_VALUE.add(0) } shouldBe Int.MAX_VALUE
            }
            test("MAX_VALUE + 1 throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MAX_VALUE.add(1) } }
            }
            test("MIN_VALUE + (-1) throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MIN_VALUE.add(-1) } }
            }
        }

        context("subtract") {
            test("5 - 3 = 2") { with(ops) { 5.subtract(3) } shouldBe 2 }
            test("x - x = 0") { with(ops) { 5.subtract(5) } shouldBe 0 }
            test("MIN_VALUE - 0 does not throw") {
                with(ops) { Int.MIN_VALUE.subtract(0) } shouldBe Int.MIN_VALUE
            }
            test("MIN_VALUE - 1 throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MIN_VALUE.subtract(1) } }
            }
            test("MAX_VALUE - (-1) throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MAX_VALUE.subtract(-1) } }
            }
        }

        context("multiply") {
            test("2 * 3 = 6") { with(ops) { 2.multiply(3) } shouldBe 6 }
            test("x * zero = zero") { with(ops) { Int.MAX_VALUE.multiply(0) } shouldBe 0 }
            test("x * one = x") { with(ops) { Int.MAX_VALUE.multiply(1) } shouldBe Int.MAX_VALUE }
            test("MAX_VALUE * 2 throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MAX_VALUE.multiply(2) } }
            }
            test("MIN_VALUE * 2 throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MIN_VALUE.multiply(2) } }
            }
        }

        context("divide") {
            test("6 / 2 = 3") { with(ops) { 6.divide(2) } shouldBe 3 }
            test("truncates toward zero: -7 / 2 = -3") { with(ops) { (-7).divide(2) } shouldBe -3 }
            test("0 / x = 0") { with(ops) { 0.divide(Int.MAX_VALUE) } shouldBe 0 }
            test("MIN_VALUE / -1 throws ArithmeticException (overflow)") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MIN_VALUE.divide(-1) } }
            }
            test("divide by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1.divide(0) } }
            }
        }

        context("rem") {
            test("7 % 3 = 1") { with(ops) { 7.rem(3) } shouldBe 1 }
            test("remainder has sign of dividend: -7 % 3 = -1") { with(ops) { (-7).rem(3) } shouldBe -1 }
            test("rem by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1.rem(0) } }
            }
        }

        context("compareTo") {
            test("1 < 2") { (with(ops) { 1.compareTo(2) } < 0) shouldBe true }
            test("1 == 1") { with(ops) { 1.compareTo(1) } shouldBe 0 }
            test("negative < positive") { (with(ops) { (-1).compareTo(1) } < 0) shouldBe true }
        }
    }

    // ── OverflowCheckedArithmetic.Companion.long ──────────────────────────────

    context("OverflowCheckedArithmetic.Companion.long") {
        val ops = OverflowCheckedArithmetic.long

        context("constants") {
            test("zero is 0L") { ops.zero shouldBe 0L }
            test("one is 1L") { ops.one shouldBe 1L }
        }

        context("add") {
            test("2L + 3L = 5L") { with(ops) { 2L.add(3L) } shouldBe 5L }
            test("MAX_VALUE + 1L throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MAX_VALUE.add(1L) } }
            }
            test("MIN_VALUE + (-1L) throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MIN_VALUE.add(-1L) } }
            }
        }

        context("subtract") {
            test("5L - 3L = 2L") { with(ops) { 5L.subtract(3L) } shouldBe 2L }
            test("MIN_VALUE - 1L throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MIN_VALUE.subtract(1L) } }
            }
        }

        context("multiply") {
            test("2L * 3L = 6L") { with(ops) { 2L.multiply(3L) } shouldBe 6L }
            test("MAX_VALUE * 2L throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MAX_VALUE.multiply(2L) } }
            }
        }

        context("divide") {
            test("6L / 2L = 3L") { with(ops) { 6L.divide(2L) } shouldBe 3L }
            test("MIN_VALUE / -1L throws ArithmeticException (overflow)") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MIN_VALUE.divide(-1L) } }
            }
            test("divide by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1L.divide(0L) } }
            }
        }

        context("rem") {
            test("7L % 3L = 1L") { with(ops) { 7L.rem(3L) } shouldBe 1L }
            test("rem by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1L.rem(0L) } }
            }
        }

        context("compareTo") {
            test("1L < 2L") { (with(ops) { 1L.compareTo(2L) } < 0) shouldBe true }
            test("negative < positive") { (with(ops) { (-1L).compareTo(1L) } < 0) shouldBe true }
        }
    }

    // ── OverflowCheckedSignedArithmetic.Companion.int ─────────────────────────

    context("OverflowCheckedSignedArithmetic.Companion.int") {
        val ops = OverflowCheckedSignedArithmetic.int

        context("constants") {
            test("zero is 0") { ops.zero shouldBe 0 }
            test("one is 1") { ops.one shouldBe 1 }
        }

        context("add") {
            test("2 + 3 = 5") { with(ops) { 2.add(3) } shouldBe 5 }
            test("MAX_VALUE + 1 throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MAX_VALUE.add(1) } }
            }
        }

        context("subtract") {
            test("5 - 3 = 2") { with(ops) { 5.subtract(3) } shouldBe 2 }
            test("MIN_VALUE - 1 throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MIN_VALUE.subtract(1) } }
            }
        }

        context("multiply") {
            test("2 * 3 = 6") { with(ops) { 2.multiply(3) } shouldBe 6 }
            test("MAX_VALUE * 2 throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MAX_VALUE.multiply(2) } }
            }
        }

        context("divide") {
            test("6 / 2 = 3") { with(ops) { 6.divide(2) } shouldBe 3 }
            test("MIN_VALUE / -1 throws ArithmeticException (overflow)") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MIN_VALUE.divide(-1) } }
            }
            test("divide by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1.divide(0) } }
            }
        }

        context("rem") {
            test("7 % 3 = 1") { with(ops) { 7.rem(3) } shouldBe 1 }
            test("rem by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1.rem(0) } }
            }
        }

        context("compareTo") {
            test("1 < 2") { (with(ops) { 1.compareTo(2) } < 0) shouldBe true }
            test("negative < positive") { (with(ops) { (-1).compareTo(1) } < 0) shouldBe true }
        }

        context("negate") {
            test("negate of positive gives negative") { with(ops) { 5.negate() } shouldBe -5 }
            test("negate of negative gives positive") { with(ops) { (-5).negate() } shouldBe 5 }
            test("negate of zero gives zero") { with(ops) { 0.negate() } shouldBe 0 }
            test("MIN_VALUE throws ArithmeticException (no representable negation)") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MIN_VALUE.negate() } }
            }
        }

        context("abs") {
            test("abs of positive is unchanged") { with(ops) { 5.abs() } shouldBe 5 }
            test("abs of negative removes sign") { with(ops) { (-5).abs() } shouldBe 5 }
            test("abs of zero is zero") { with(ops) { 0.abs() } shouldBe 0 }
            test("MIN_VALUE throws ArithmeticException (no representable absolute value)") {
                shouldThrow<ArithmeticException> { with(ops) { Int.MIN_VALUE.abs() } }
            }
        }
    }

    // ── OverflowCheckedSignedArithmetic.Companion.long ────────────────────────

    context("OverflowCheckedSignedArithmetic.Companion.long") {
        val ops = OverflowCheckedSignedArithmetic.long

        context("constants") {
            test("zero is 0L") { ops.zero shouldBe 0L }
            test("one is 1L") { ops.one shouldBe 1L }
        }

        context("add") {
            test("2L + 3L = 5L") { with(ops) { 2L.add(3L) } shouldBe 5L }
            test("MAX_VALUE + 1L throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MAX_VALUE.add(1L) } }
            }
        }

        context("subtract") {
            test("5L - 3L = 2L") { with(ops) { 5L.subtract(3L) } shouldBe 2L }
            test("MIN_VALUE - 1L throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MIN_VALUE.subtract(1L) } }
            }
        }

        context("multiply") {
            test("2L * 3L = 6L") { with(ops) { 2L.multiply(3L) } shouldBe 6L }
            test("MAX_VALUE * 2L throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MAX_VALUE.multiply(2L) } }
            }
        }

        context("divide") {
            test("6L / 2L = 3L") { with(ops) { 6L.divide(2L) } shouldBe 3L }
            test("MIN_VALUE / -1L throws ArithmeticException (overflow)") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MIN_VALUE.divide(-1L) } }
            }
            test("divide by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1L.divide(0L) } }
            }
        }

        context("rem") {
            test("7L % 3L = 1L") { with(ops) { 7L.rem(3L) } shouldBe 1L }
            test("rem by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1L.rem(0L) } }
            }
        }

        context("negate") {
            test("negate of positive gives negative") { with(ops) { 5L.negate() } shouldBe -5L }
            test("negate of negative gives positive") { with(ops) { (-5L).negate() } shouldBe 5L }
            test("MIN_VALUE throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MIN_VALUE.negate() } }
            }
        }

        context("abs") {
            test("abs of positive is unchanged") { with(ops) { 5L.abs() } shouldBe 5L }
            test("abs of negative removes sign") { with(ops) { (-5L).abs() } shouldBe 5L }
            test("MIN_VALUE throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { Long.MIN_VALUE.abs() } }
            }
        }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("OverflowCheckedArithmetic.int is stable") {
            OverflowCheckedArithmetic.int shouldBe OverflowCheckedArithmetic.int
        }
        test("OverflowCheckedArithmetic.long is stable") {
            OverflowCheckedArithmetic.long shouldBe OverflowCheckedArithmetic.long
        }
        test("OverflowCheckedSignedArithmetic.int is stable") {
            OverflowCheckedSignedArithmetic.int shouldBe OverflowCheckedSignedArithmetic.int
        }
        test("OverflowCheckedSignedArithmetic.long is stable") {
            OverflowCheckedSignedArithmetic.long shouldBe OverflowCheckedSignedArithmetic.long
        }
        test("OverflowCheckedArithmetic.int and OverflowCheckedSignedArithmetic.int are distinct") {
            OverflowCheckedArithmetic.int shouldNotBe OverflowCheckedSignedArithmetic.int
        }
        test("int and long instances are distinct") {
            OverflowCheckedArithmetic.int shouldNotBe OverflowCheckedArithmetic.long
        }
    }
})
