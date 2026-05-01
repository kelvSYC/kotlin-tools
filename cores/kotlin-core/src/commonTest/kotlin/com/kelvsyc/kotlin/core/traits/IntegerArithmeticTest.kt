package com.kelvsyc.kotlin.core.traits

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class IntegerArithmeticTest : FunSpec({

    // ── IntegerArithmetic.Companion.int ───────────────────────────────────────

    context("IntegerArithmetic.Companion.int") {
        val ops = IntegerArithmetic.int

        context("constants") {
            test("zero is 0") { ops.zero shouldBe 0 }
            test("one is 1") { ops.one shouldBe 1 }
        }

        context("add") {
            test("2 + 3 = 5") { with(ops) { 2.add(3) } shouldBe 5 }
            test("zero + x = x") { with(ops) { 0.add(7) } shouldBe 7 }
            test("x + zero = x") { with(ops) { 7.add(0) } shouldBe 7 }
            test("negative addend") { with(ops) { 5.add(-3) } shouldBe 2 }
            test("wraps on overflow") { with(ops) { Int.MAX_VALUE.add(1) } shouldBe Int.MIN_VALUE }
        }

        context("subtract") {
            test("5 - 3 = 2") { with(ops) { 5.subtract(3) } shouldBe 2 }
            test("x - zero = x") { with(ops) { 7.subtract(0) } shouldBe 7 }
            test("x - x = 0") { with(ops) { 5.subtract(5) } shouldBe 0 }
            test("subtract larger value gives negative") { with(ops) { 3.subtract(5) } shouldBe -2 }
            test("wraps on underflow") { with(ops) { Int.MIN_VALUE.subtract(1) } shouldBe Int.MAX_VALUE }
        }

        context("multiply") {
            test("2 * 3 = 6") { with(ops) { 2.multiply(3) } shouldBe 6 }
            test("x * one = x") { with(ops) { 7.multiply(1) } shouldBe 7 }
            test("x * zero = zero") { with(ops) { 7.multiply(0) } shouldBe 0 }
            test("negative factor") { with(ops) { 4.multiply(-3) } shouldBe -12 }
            test("wraps on overflow") { with(ops) { Int.MAX_VALUE.multiply(2) } shouldBe -2 }
        }

        context("divide") {
            test("6 / 2 = 3") { with(ops) { 6.divide(2) } shouldBe 3 }
            test("x / one = x") { with(ops) { 7.divide(1) } shouldBe 7 }
            test("zero / x = zero") { with(ops) { 0.divide(5) } shouldBe 0 }
            test("truncates toward zero: 7 / 2 = 3") { with(ops) { 7.divide(2) } shouldBe 3 }
            test("truncates toward zero: -7 / 2 = -3") { with(ops) { (-7).divide(2) } shouldBe -3 }
            test("truncates toward zero: 7 / -2 = -3") { with(ops) { 7.divide(-2) } shouldBe -3 }
            test("truncates toward zero: -7 / -2 = 3") { with(ops) { (-7).divide(-2) } shouldBe 3 }
            test("divide by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1.divide(0) } }
            }
        }

        context("rem") {
            test("7 % 3 = 1") { with(ops) { 7.rem(3) } shouldBe 1 }
            test("6 % 3 = 0") { with(ops) { 6.rem(3) } shouldBe 0 }
            test("remainder has sign of dividend: -7 % 3 = -1") { with(ops) { (-7).rem(3) } shouldBe -1 }
            test("remainder has sign of dividend: 7 % -3 = 1") { with(ops) { 7.rem(-3) } shouldBe 1 }
            test("rem by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1.rem(0) } }
            }
        }

        context("compareTo") {
            test("1 < 2") { (with(ops) { 1.compareTo(2) } < 0) shouldBe true }
            test("2 > 1") { (with(ops) { 2.compareTo(1) } > 0) shouldBe true }
            test("1 == 1") { with(ops) { 1.compareTo(1) } shouldBe 0 }
            test("negative < positive") { (with(ops) { (-1).compareTo(1) } < 0) shouldBe true }
            test("MIN_VALUE < MAX_VALUE") {
                (with(ops) { Int.MIN_VALUE.compareTo(Int.MAX_VALUE) } < 0) shouldBe true
            }
        }
    }

    // ── IntegerArithmetic.Companion.long ──────────────────────────────────────

    context("IntegerArithmetic.Companion.long") {
        val ops = IntegerArithmetic.long

        context("constants") {
            test("zero is 0L") { ops.zero shouldBe 0L }
            test("one is 1L") { ops.one shouldBe 1L }
        }

        context("add") {
            test("2L + 3L = 5L") { with(ops) { 2L.add(3L) } shouldBe 5L }
            test("zero + x = x") { with(ops) { 0L.add(7L) } shouldBe 7L }
            test("wraps on overflow") { with(ops) { Long.MAX_VALUE.add(1L) } shouldBe Long.MIN_VALUE }
        }

        context("subtract") {
            test("5L - 3L = 2L") { with(ops) { 5L.subtract(3L) } shouldBe 2L }
            test("x - x = 0") { with(ops) { 5L.subtract(5L) } shouldBe 0L }
            test("wraps on underflow") { with(ops) { Long.MIN_VALUE.subtract(1L) } shouldBe Long.MAX_VALUE }
        }

        context("multiply") {
            test("2L * 3L = 6L") { with(ops) { 2L.multiply(3L) } shouldBe 6L }
            test("x * zero = zero") { with(ops) { 7L.multiply(0L) } shouldBe 0L }
        }

        context("divide") {
            test("6L / 2L = 3L") { with(ops) { 6L.divide(2L) } shouldBe 3L }
            test("truncates toward zero: -7L / 2L = -3L") { with(ops) { (-7L).divide(2L) } shouldBe -3L }
            test("divide by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1L.divide(0L) } }
            }
        }

        context("rem") {
            test("7L % 3L = 1L") { with(ops) { 7L.rem(3L) } shouldBe 1L }
            test("remainder has sign of dividend: -7L % 3L = -1L") { with(ops) { (-7L).rem(3L) } shouldBe -1L }
            test("rem by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1L.rem(0L) } }
            }
        }

        context("compareTo") {
            test("1L < 2L") { (with(ops) { 1L.compareTo(2L) } < 0) shouldBe true }
            test("1L == 1L") { with(ops) { 1L.compareTo(1L) } shouldBe 0 }
            test("negative < positive") { (with(ops) { (-1L).compareTo(1L) } < 0) shouldBe true }
        }
    }

    // ── SignedIntegerArithmetic.Companion.int ─────────────────────────────────

    context("SignedIntegerArithmetic.Companion.int") {
        val ops = SignedIntegerArithmetic.int

        context("constants") {
            test("zero is 0") { ops.zero shouldBe 0 }
            test("one is 1") { ops.one shouldBe 1 }
        }

        context("add") {
            test("2 + 3 = 5") { with(ops) { 2.add(3) } shouldBe 5 }
            test("wraps on overflow") { with(ops) { Int.MAX_VALUE.add(1) } shouldBe Int.MIN_VALUE }
        }

        context("subtract") {
            test("5 - 3 = 2") { with(ops) { 5.subtract(3) } shouldBe 2 }
            test("wraps on underflow") { with(ops) { Int.MIN_VALUE.subtract(1) } shouldBe Int.MAX_VALUE }
        }

        context("multiply") {
            test("2 * 3 = 6") { with(ops) { 2.multiply(3) } shouldBe 6 }
            test("wraps on overflow") { with(ops) { Int.MAX_VALUE.multiply(2) } shouldBe -2 }
        }

        context("divide") {
            test("6 / 2 = 3") { with(ops) { 6.divide(2) } shouldBe 3 }
            test("truncates toward zero: -7 / 2 = -3") { with(ops) { (-7).divide(2) } shouldBe -3 }
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
            test("negative < positive") { (with(ops) { (-1).compareTo(1) } < 0) shouldBe true }
        }

        context("negate") {
            test("negate of positive gives negative") { with(ops) { 5.negate() } shouldBe -5 }
            test("negate of negative gives positive") { with(ops) { (-5).negate() } shouldBe 5 }
            test("negate of zero gives zero") { with(ops) { 0.negate() } shouldBe 0 }
            test("double negation is identity") { with(ops) { 7.negate().negate() } shouldBe 7 }
            test("MIN_VALUE wraps to itself on overflow") {
                with(ops) { Int.MIN_VALUE.negate() } shouldBe Int.MIN_VALUE
            }
        }

        context("abs") {
            test("abs of positive is unchanged") { with(ops) { 5.abs() } shouldBe 5 }
            test("abs of negative removes sign") { with(ops) { (-5).abs() } shouldBe 5 }
            test("abs of zero is zero") { with(ops) { 0.abs() } shouldBe 0 }
            test("MIN_VALUE wraps to itself on overflow") {
                with(ops) { Int.MIN_VALUE.abs() } shouldBe Int.MIN_VALUE
            }
        }

        context("floorDiv") {
            test("7 floorDiv 2 = 3 (same signs, same as truncated)") { with(ops) { 7.floorDiv(2) } shouldBe 3 }
            test("-7 floorDiv 2 = -4 (rounds toward -inf, not 0)") { with(ops) { (-7).floorDiv(2) } shouldBe -4 }
            test("7 floorDiv -2 = -4 (rounds toward -inf)") { with(ops) { 7.floorDiv(-2) } shouldBe -4 }
            test("-7 floorDiv -2 = 3 (same signs, same as truncated)") { with(ops) { (-7).floorDiv(-2) } shouldBe 3 }
            test("exact division: 6 floorDiv 2 = 3") { with(ops) { 6.floorDiv(2) } shouldBe 3 }
            test("floorDiv by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1.floorDiv(0) } }
            }
            test("MIN_VALUE floorDiv -1 wraps (wrapping instance)") {
                with(ops) { Int.MIN_VALUE.floorDiv(-1) } shouldBe Int.MIN_VALUE
            }
        }

        context("mod") {
            test("7 mod 3 = 1 (same signs, same as rem)") { with(ops) { 7.mod(3) } shouldBe 1 }
            test("-7 mod 3 = 2 (result has sign of divisor)") { with(ops) { (-7).mod(3) } shouldBe 2 }
            test("7 mod -3 = -2 (result has sign of divisor)") { with(ops) { 7.mod(-3) } shouldBe -2 }
            test("-7 mod -3 = -1 (same signs, same as rem)") { with(ops) { (-7).mod(-3) } shouldBe -1 }
            test("exact division: 6 mod 3 = 0") { with(ops) { 6.mod(3) } shouldBe 0 }
            test("mod by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1.mod(0) } }
            }
            test("invariant: a == b * floorDiv(a, b) + mod(a, b)") {
                with(ops) { (-7).floorDiv(3).multiply(3).add((-7).mod(3)) } shouldBe -7
            }
        }
    }

    // ── SignedIntegerArithmetic.Companion.long ────────────────────────────────

    context("SignedIntegerArithmetic.Companion.long") {
        val ops = SignedIntegerArithmetic.long

        context("constants") {
            test("zero is 0L") { ops.zero shouldBe 0L }
            test("one is 1L") { ops.one shouldBe 1L }
        }

        context("add") {
            test("2L + 3L = 5L") { with(ops) { 2L.add(3L) } shouldBe 5L }
            test("wraps on overflow") { with(ops) { Long.MAX_VALUE.add(1L) } shouldBe Long.MIN_VALUE }
        }

        context("subtract") {
            test("5L - 3L = 2L") { with(ops) { 5L.subtract(3L) } shouldBe 2L }
            test("wraps on underflow") { with(ops) { Long.MIN_VALUE.subtract(1L) } shouldBe Long.MAX_VALUE }
        }

        context("multiply") {
            test("2L * 3L = 6L") { with(ops) { 2L.multiply(3L) } shouldBe 6L }
        }

        context("divide") {
            test("6L / 2L = 3L") { with(ops) { 6L.divide(2L) } shouldBe 3L }
            test("truncates toward zero: -7L / 2L = -3L") { with(ops) { (-7L).divide(2L) } shouldBe -3L }
            test("divide by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1L.divide(0L) } }
            }
        }

        context("rem") {
            test("7L % 3L = 1L") { with(ops) { 7L.rem(3L) } shouldBe 1L }
            test("remainder has sign of dividend: -7L % 3L = -1L") { with(ops) { (-7L).rem(3L) } shouldBe -1L }
            test("rem by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1L.rem(0L) } }
            }
        }

        context("compareTo") {
            test("1L < 2L") { (with(ops) { 1L.compareTo(2L) } < 0) shouldBe true }
            test("negative < positive") { (with(ops) { (-1L).compareTo(1L) } < 0) shouldBe true }
        }

        context("negate") {
            test("negating a positive gives negative") { with(ops) { 5L.negate() } shouldBe -5L }
            test("negating a negative gives positive") { with(ops) { (-5L).negate() } shouldBe 5L }
            test("negating zero gives zero") { with(ops) { 0L.negate() } shouldBe 0L }
            test("MIN_VALUE wraps to itself on overflow") {
                with(ops) { Long.MIN_VALUE.negate() } shouldBe Long.MIN_VALUE
            }
        }

        context("abs") {
            test("abs of positive is unchanged") { with(ops) { 5L.abs() } shouldBe 5L }
            test("abs of negative removes sign") { with(ops) { (-5L).abs() } shouldBe 5L }
            test("abs of zero is zero") { with(ops) { 0L.abs() } shouldBe 0L }
            test("MIN_VALUE wraps to itself on overflow") {
                with(ops) { Long.MIN_VALUE.abs() } shouldBe Long.MIN_VALUE
            }
        }

        context("floorDiv") {
            test("-7L floorDiv 2L = -4L") { with(ops) { (-7L).floorDiv(2L) } shouldBe -4L }
            test("7L floorDiv -2L = -4L") { with(ops) { 7L.floorDiv(-2L) } shouldBe -4L }
            test("-7L floorDiv -2L = 3L") { with(ops) { (-7L).floorDiv(-2L) } shouldBe 3L }
            test("floorDiv by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1L.floorDiv(0L) } }
            }
        }

        context("mod") {
            test("-7L mod 3L = 2L") { with(ops) { (-7L).mod(3L) } shouldBe 2L }
            test("7L mod -3L = -2L") { with(ops) { 7L.mod(-3L) } shouldBe -2L }
            test("mod by zero throws ArithmeticException") {
                shouldThrow<ArithmeticException> { with(ops) { 1L.mod(0L) } }
            }
            test("invariant: a == b * floorDiv(a, b) + mod(a, b)") {
                with(ops) { (-7L).floorDiv(3L).multiply(3L).add((-7L).mod(3L)) } shouldBe -7L
            }
        }
    }

    // ── SignedIntegerArithmetic sign predicates ───────────────────────────────

    context("SignedIntegerArithmetic.Companion.int sign predicates") {
        val ops = SignedIntegerArithmetic.int

        context("isNegative") {
            test("negative value") { with(ops) { (-5).isNegative() } shouldBe true }
            test("zero is not negative") { with(ops) { 0.isNegative() } shouldBe false }
            test("positive is not negative") { with(ops) { 5.isNegative() } shouldBe false }
            test("MIN_VALUE is negative") { with(ops) { Int.MIN_VALUE.isNegative() } shouldBe true }
        }

        context("isPositive") {
            test("positive value") { with(ops) { 5.isPositive() } shouldBe true }
            test("zero is not positive") { with(ops) { 0.isPositive() } shouldBe false }
            test("negative is not positive") { with(ops) { (-5).isPositive() } shouldBe false }
        }

        context("isZero") {
            test("zero") { with(ops) { 0.isZero() } shouldBe true }
            test("positive is not zero") { with(ops) { 1.isZero() } shouldBe false }
            test("negative is not zero") { with(ops) { (-1).isZero() } shouldBe false }
        }

        context("sign") {
            test("positive gives 1") { with(ops) { 42.sign() } shouldBe 1 }
            test("negative gives -1") { with(ops) { (-42).sign() } shouldBe -1 }
            test("zero gives 0") { with(ops) { 0.sign() } shouldBe 0 }
            test("MIN_VALUE gives -1") { with(ops) { Int.MIN_VALUE.sign() } shouldBe -1 }
            test("MAX_VALUE gives 1") { with(ops) { Int.MAX_VALUE.sign() } shouldBe 1 }
        }
    }

    context("SignedIntegerArithmetic.Companion.long sign predicates") {
        val ops = SignedIntegerArithmetic.long

        context("isNegative") {
            test("negative value") { with(ops) { (-5L).isNegative() } shouldBe true }
            test("zero is not negative") { with(ops) { 0L.isNegative() } shouldBe false }
        }

        context("isPositive") {
            test("positive value") { with(ops) { 5L.isPositive() } shouldBe true }
            test("zero is not positive") { with(ops) { 0L.isPositive() } shouldBe false }
        }

        context("isZero") {
            test("zero") { with(ops) { 0L.isZero() } shouldBe true }
            test("non-zero") { with(ops) { 1L.isZero() } shouldBe false }
        }

        context("sign") {
            test("positive gives 1L") { with(ops) { 42L.sign() } shouldBe 1L }
            test("negative gives -1L") { with(ops) { (-42L).sign() } shouldBe -1L }
            test("zero gives 0L") { with(ops) { 0L.sign() } shouldBe 0L }
            test("Long.MIN_VALUE gives -1L") { with(ops) { Long.MIN_VALUE.sign() } shouldBe -1L }
        }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("IntegerArithmetic.int is stable") {
            IntegerArithmetic.int shouldBe IntegerArithmetic.int
        }
        test("IntegerArithmetic.long is stable") {
            IntegerArithmetic.long shouldBe IntegerArithmetic.long
        }
        test("SignedIntegerArithmetic.int is stable") {
            SignedIntegerArithmetic.int shouldBe SignedIntegerArithmetic.int
        }
        test("SignedIntegerArithmetic.long is stable") {
            SignedIntegerArithmetic.long shouldBe SignedIntegerArithmetic.long
        }
        test("IntegerArithmetic.int and SignedIntegerArithmetic.int are distinct instances") {
            IntegerArithmetic.int shouldNotBe SignedIntegerArithmetic.int
        }
        test("int and long instances are distinct") {
            IntegerArithmetic.int shouldNotBe IntegerArithmetic.long
        }
    }
})
