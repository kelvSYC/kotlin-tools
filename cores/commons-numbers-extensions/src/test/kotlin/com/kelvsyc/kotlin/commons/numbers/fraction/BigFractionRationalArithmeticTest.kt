package com.kelvsyc.kotlin.commons.numbers.fraction

import com.kelvsyc.kotlin.core.traits.rational.RationalArithmetic
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.fraction.BigFraction
import java.math.BigInteger

class BigFractionRationalArithmeticTest : FunSpec({
    val ops = RationalArithmetic.bigFraction

    fun f(n: Int, d: Int): BigFraction = ops.of(BigInteger.valueOf(n.toLong()), BigInteger.valueOf(d.toLong()))

    // ── Construction / normalisation ──────────────────────────────────────────

    context("of") {
        test("already reduced") {
            val x = f(3, 4)
            with(ops) { x.numerator() shouldBe BigInteger.valueOf(3) }
            with(ops) { x.denominator() shouldBe BigInteger.valueOf(4) }
        }
        test("reduces common factor") {
            val x = f(6, 4)
            with(ops) { x.numerator() shouldBe BigInteger.valueOf(3) }
            with(ops) { x.denominator() shouldBe BigInteger.valueOf(2) }
        }
        test("zero denominator throws") {
            shouldThrow<ArithmeticException> { f(1, 0) }
        }
    }

    // ── Arithmetic ────────────────────────────────────────────────────────────

    context("add") {
        test("same denominator") { with(ops) { f(1, 4).add(f(2, 4)) shouldBe f(3, 4) } }
        test("different denominators") { with(ops) { f(1, 3).add(f(1, 6)) shouldBe f(1, 2) } }
        test("add zero is identity") { with(ops) { f(3, 7).add(zero) shouldBe f(3, 7) } }
    }

    context("subtract") {
        test("same denominator") { with(ops) { f(3, 4).subtract(f(1, 4)) shouldBe f(1, 2) } }
    }

    context("multiply") {
        test("basic product") { with(ops) { f(2, 3).multiply(f(3, 4)) shouldBe f(1, 2) } }
        test("by one") { with(ops) { f(5, 7).multiply(one) shouldBe f(5, 7) } }
    }

    context("divide") {
        test("basic quotient") { with(ops) { f(1, 2).divide(f(3, 4)) shouldBe f(2, 3) } }
        test("by zero throws") { with(ops) { shouldThrow<ArithmeticException> { f(1, 2).divide(zero) } } }
    }

    context("compareTo") {
        test("lesser") { with(ops) { f(1, 3).compareTo(f(1, 2)) shouldBe -1 } }
        test("equal") { with(ops) { f(2, 4).compareTo(f(1, 2)) shouldBe 0 } }
    }

    context("floor and ceil") {
        test("positive non-exact") {
            with(ops) {
                f(7, 2).floor() shouldBe BigInteger.valueOf(3)
                f(7, 2).ceil() shouldBe BigInteger.valueOf(4)
            }
        }
    }
})
