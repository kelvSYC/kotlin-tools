package com.kelvsyc.kotlin.commons.numbers.fraction

import com.kelvsyc.kotlin.core.traits.rational.RationalArithmetic
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.fraction.Fraction

class FractionRationalArithmeticTest : FunSpec({
    val ops = RationalArithmetic.fraction

    fun f(n: Int, d: Int): Fraction = ops.of(n, d)

    // ── Construction / normalisation ──────────────────────────────────────────

    context("of") {
        test("already reduced") {
            val x = f(3, 4)
            with(ops) { x.numerator() shouldBe 3 }
            with(ops) { x.denominator() shouldBe 4 }
        }
        test("reduces common factor") {
            val x = f(6, 4)
            with(ops) { x.numerator() shouldBe 3 }
            with(ops) { x.denominator() shouldBe 2 }
        }
        test("negative denominator normalised") {
            val x = f(3, -4)
            with(ops) { x.numerator() shouldBe -3 }
            with(ops) { x.denominator() shouldBe 4 }
        }
        test("zero denominator throws") {
            shouldThrow<ArithmeticException> { f(1, 0) }
        }
    }

    // ── Structural properties ─────────────────────────────────────────────────

    context("isZero / isPositive / isNegative") {
        test("zero") {
            with(ops) {
                f(0, 1).isZero() shouldBe true
                f(0, 1).isPositive() shouldBe false
                f(0, 1).isNegative() shouldBe false
            }
        }
        test("positive") {
            with(ops) {
                f(1, 2).isZero() shouldBe false
                f(1, 2).isPositive() shouldBe true
                f(1, 2).isNegative() shouldBe false
            }
        }
        test("negative") {
            with(ops) {
                f(-1, 2).isPositive() shouldBe false
                f(-1, 2).isNegative() shouldBe true
            }
        }
    }

    context("isWhole") {
        test("whole") { with(ops) { f(6, 2).isWhole() shouldBe true } }
        test("not whole") { with(ops) { f(1, 2).isWhole() shouldBe false } }
    }

    // ── Arithmetic ────────────────────────────────────────────────────────────

    context("negate") {
        test("negates numerator") {
            with(ops) { f(3, 4).negate() shouldBe f(-3, 4) }
        }
    }

    context("reciprocal") {
        test("positive") { with(ops) { f(2, 3).reciprocal() shouldBe f(3, 2) } }
        test("negative") { with(ops) { f(-2, 3).reciprocal() shouldBe f(-3, 2) } }
        test("zero throws") { with(ops) { shouldThrow<ArithmeticException> { f(0, 1).reciprocal() } } }
    }

    context("add") {
        test("same denominator") { with(ops) { f(1, 4).add(f(2, 4)) shouldBe f(3, 4) } }
        test("different denominators") { with(ops) { f(1, 3).add(f(1, 6)) shouldBe f(1, 2) } }
        test("add zero is identity") { with(ops) { f(3, 7).add(zero) shouldBe f(3, 7) } }
    }

    context("subtract") {
        test("same denominator") { with(ops) { f(3, 4).subtract(f(1, 4)) shouldBe f(1, 2) } }
        test("yields negative") { with(ops) { f(1, 4).subtract(f(3, 4)) shouldBe f(-1, 2) } }
    }

    context("multiply") {
        test("basic product") { with(ops) { f(2, 3).multiply(f(3, 4)) shouldBe f(1, 2) } }
        test("by zero") { with(ops) { f(5, 7).multiply(zero) shouldBe zero } }
        test("by one") { with(ops) { f(5, 7).multiply(one) shouldBe f(5, 7) } }
    }

    context("divide") {
        test("basic quotient") { with(ops) { f(1, 2).divide(f(3, 4)) shouldBe f(2, 3) } }
        test("by itself is one") { with(ops) { f(5, 7).divide(f(5, 7)) shouldBe one } }
        test("by zero throws") { with(ops) { shouldThrow<ArithmeticException> { f(1, 2).divide(zero) } } }
    }

    context("compareTo") {
        test("lesser") { with(ops) { f(1, 3).compareTo(f(1, 2)) shouldBe -1 } }
        test("equal") { with(ops) { f(2, 4).compareTo(f(1, 2)) shouldBe 0 } }
        test("greater") { with(ops) { f(2, 3).compareTo(f(1, 2)) shouldBe 1 } }
    }

    context("integerPart and fractionalPart") {
        test("whole") {
            with(ops) {
                f(6, 3).integerPart() shouldBe 2
                f(6, 3).fractionalPart() shouldBe zero
            }
        }
        test("positive fraction") {
            with(ops) {
                f(7, 3).integerPart() shouldBe 2
                with(ops) { f(7, 3).fractionalPart().numerator() shouldBe 1 }
                with(ops) { f(7, 3).fractionalPart().denominator() shouldBe 3 }
            }
        }
    }

    context("floor and ceil") {
        test("positive exact") {
            with(ops) {
                f(6, 2).floor() shouldBe 3
                f(6, 2).ceil() shouldBe 3
            }
        }
        test("positive non-exact") {
            with(ops) {
                f(7, 2).floor() shouldBe 3
                f(7, 2).ceil() shouldBe 4
            }
        }
        test("negative non-exact") {
            with(ops) {
                f(-7, 2).floor() shouldBe -4
                f(-7, 2).ceil() shouldBe -3
            }
        }
    }
})
