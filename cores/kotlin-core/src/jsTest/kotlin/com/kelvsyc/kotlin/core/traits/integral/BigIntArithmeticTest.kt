package com.kelvsyc.kotlin.core.traits.integral

import com.kelvsyc.kotlin.core.bigIntOf
import com.kelvsyc.kotlin.core.toDecimalString
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BigIntArithmeticTest : FunSpec({
    val ops = SignedIntegerArithmetic.bigInt

    context("constants") {
        test("zero") { ops.zero.toDecimalString() shouldBe "0" }
        test("one") { ops.one.toDecimalString() shouldBe "1" }
    }

    context("add") {
        test("2 + 3 = 5") { with(ops) { bigIntOf(2).add(bigIntOf(3)) }.toDecimalString() shouldBe "5" }
        test("large values") {
            val large = bigIntOf("99999999999999999999")
            with(ops) { large.add(bigIntOf(1)) }.toDecimalString() shouldBe "100000000000000000000"
        }
        test("negative addend") { with(ops) { bigIntOf(5).add(bigIntOf(-3)) }.toDecimalString() shouldBe "2" }
    }

    context("subtract") {
        test("10 - 3 = 7") { with(ops) { bigIntOf(10).subtract(bigIntOf(3)) }.toDecimalString() shouldBe "7" }
        test("result can be negative") { with(ops) { bigIntOf(3).subtract(bigIntOf(5)) }.toDecimalString() shouldBe "-2" }
    }

    context("multiply") {
        test("6 * 7 = 42") { with(ops) { bigIntOf(6).multiply(bigIntOf(7)) }.toDecimalString() shouldBe "42" }
        test("negative factor") { with(ops) { bigIntOf(4).multiply(bigIntOf(-3)) }.toDecimalString() shouldBe "-12" }
    }

    context("divide") {
        test("7 / 2 = 3 (truncates toward zero)") {
            with(ops) { bigIntOf(7).divide(bigIntOf(2)) }.toDecimalString() shouldBe "3"
        }
        test("-7 / 2 = -3") { with(ops) { bigIntOf(-7).divide(bigIntOf(2)) }.toDecimalString() shouldBe "-3" }
        test("divide by zero throws") {
            shouldThrow<ArithmeticException> { with(ops) { bigIntOf(1).divide(bigIntOf(0)) } }
        }
    }

    context("rem") {
        test("7 % 3 = 1") { with(ops) { bigIntOf(7).rem(bigIntOf(3)) }.toDecimalString() shouldBe "1" }
        test("-7 % 3 = -1 (sign of dividend)") {
            with(ops) { bigIntOf(-7).rem(bigIntOf(3)) }.toDecimalString() shouldBe "-1"
        }
        test("rem by zero throws") {
            shouldThrow<ArithmeticException> { with(ops) { bigIntOf(1).rem(bigIntOf(0)) } }
        }
    }

    context("floorDiv and mod (defaults from SignedIntegerArithmetic)") {
        test("-7 floorDiv 2 = -4") {
            with(ops) { bigIntOf(-7).floorDiv(bigIntOf(2)) }.toDecimalString() shouldBe "-4"
        }
        test("-7 mod 3 = 2") {
            with(ops) { bigIntOf(-7).mod(bigIntOf(3)) }.toDecimalString() shouldBe "2"
        }
        test("invariant: a == b * floorDiv(a,b) + mod(a,b)") {
            val a = bigIntOf(-7); val b = bigIntOf(3)
            with(ops) { b.multiply(a.floorDiv(b)).add(a.mod(b)) }.toDecimalString() shouldBe "-7"
        }
    }

    context("negate") {
        test("negate positive") { with(ops) { bigIntOf(5).negate() }.toDecimalString() shouldBe "-5" }
        test("negate zero") { with(ops) { bigIntOf(0).negate() }.toDecimalString() shouldBe "0" }
    }

    context("abs") {
        test("abs of positive") { with(ops) { bigIntOf(5).abs() }.toDecimalString() shouldBe "5" }
        test("abs of negative") { with(ops) { bigIntOf(-5).abs() }.toDecimalString() shouldBe "5" }
        test("abs of zero") { with(ops) { bigIntOf(0).abs() }.toDecimalString() shouldBe "0" }
    }

    context("ArithmeticRightShift.bigInt") {
        val ars = ArithmeticRightShift.bigInt

        test("8 shr 3 = 1") {
            with(ars) { bigIntOf(8).arithmeticRightShift(3) }.toDecimalString() shouldBe "1"
        }
        test("-8 shr 3 = -1 (sign-extends)") {
            with(ars) { bigIntOf(-8).arithmeticRightShift(3) }.toDecimalString() shouldBe "-1"
        }
        test("-1 shr 1 = -1") {
            with(ars) { bigIntOf(-1).arithmeticRightShift(1) }.toDecimalString() shouldBe "-1"
        }
        test("shift by 0 is identity") {
            with(ars) { bigIntOf(42).arithmeticRightShift(0) }.toDecimalString() shouldBe "42"
        }
    }
})
