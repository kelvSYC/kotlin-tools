package com.kelvsyc.kotlin.core.traits.rational

import com.kelvsyc.kotlin.core.Rational
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RationalArithmeticTest : FunSpec({
    val ops = RationalArithmetic.long

    fun r(n: Long, d: Long): Rational<Long> = ops.of(n, d)

    // ── Construction / normalisation ──────────────────────────────────────────

    context("of") {
        test("already reduced positive fraction") {
            val x = r(3, 4)
            x.numerator shouldBe 3L
            x.denominator shouldBe 4L
        }
        test("reduces common factor") {
            val x = r(6, 4)
            x.numerator shouldBe 3L
            x.denominator shouldBe 2L
        }
        test("negative numerator stays negative") {
            val x = r(-3, 4)
            x.numerator shouldBe -3L
            x.denominator shouldBe 4L
        }
        test("negative denominator is normalised to positive") {
            val x = r(3, -4)
            x.numerator shouldBe -3L
            x.denominator shouldBe 4L
        }
        test("both negative normalises to positive fraction") {
            val x = r(-3, -4)
            x.numerator shouldBe 3L
            x.denominator shouldBe 4L
        }
        test("zero numerator always gives 0/1") {
            r(0, 7).numerator shouldBe 0L
            r(0, 7).denominator shouldBe 1L
            r(0, -7).numerator shouldBe 0L
            r(0, -7).denominator shouldBe 1L
        }
        test("zero denominator throws") {
            shouldThrow<ArithmeticException> { r(1, 0) }
        }
    }

    // ── Structural properties ─────────────────────────────────────────────────

    context("isZero") {
        test("zero is zero") { with(ops) { r(0, 1).isZero() shouldBe true } }
        test("non-zero is not zero") { with(ops) { r(1, 2).isZero() shouldBe false } }
    }

    context("isWhole") {
        test("integer fraction is whole") { with(ops) { r(6, 2).isWhole() shouldBe true } }
        test("non-integer is not whole") { with(ops) { r(1, 2).isWhole() shouldBe false } }
    }

    context("isPositive / isNegative") {
        test("positive fraction") {
            with(ops) {
                r(1, 2).isPositive() shouldBe true
                r(1, 2).isNegative() shouldBe false
            }
        }
        test("negative fraction") {
            with(ops) {
                r(-1, 2).isPositive() shouldBe false
                r(-1, 2).isNegative() shouldBe true
            }
        }
        test("zero is neither") {
            with(ops) {
                r(0, 1).isPositive() shouldBe false
                r(0, 1).isNegative() shouldBe false
            }
        }
    }

    context("sign") {
        test("positive") { with(ops) { r(3, 4).sign() shouldBe 1L } }
        test("negative") { with(ops) { r(-3, 4).sign() shouldBe -1L } }
        test("zero") { with(ops) { r(0, 1).sign() shouldBe 0L } }
    }

    context("reciprocal") {
        test("positive fraction") {
            with(ops) {
                val x = r(2, 3).reciprocal()
                x.numerator shouldBe 3L
                x.denominator shouldBe 2L
            }
        }
        test("negative fraction flips sign to denominator") {
            with(ops) {
                val x = r(-2, 3).reciprocal()
                x.numerator shouldBe -3L
                x.denominator shouldBe 2L
            }
        }
        test("whole number") {
            with(ops) {
                val x = r(4, 1).reciprocal()
                x.numerator shouldBe 1L
                x.denominator shouldBe 4L
            }
        }
        test("reciprocal of zero throws") {
            with(ops) { shouldThrow<ArithmeticException> { r(0, 1).reciprocal() } }
        }
    }

    // ── Arithmetic ────────────────────────────────────────────────────────────

    context("negate") {
        test("negates numerator") {
            with(ops) {
                val x = r(3, 4).negate()
                x.numerator shouldBe -3L
                x.denominator shouldBe 4L
            }
        }
        test("double negate is identity") {
            with(ops) {
                val x = r(3, 4)
                x.negate().negate().numerator shouldBe x.numerator
                x.negate().negate().denominator shouldBe x.denominator
            }
        }
    }

    context("add") {
        test("same denominator") {
            with(ops) {
                val x = r(1, 4).add(r(2, 4))
                x.numerator shouldBe 3L
                x.denominator shouldBe 4L
            }
        }
        test("different denominators reduces result") {
            with(ops) {
                val x = r(1, 3).add(r(1, 6))
                x.numerator shouldBe 1L
                x.denominator shouldBe 2L
            }
        }
        test("add gives zero") {
            with(ops) {
                val x = r(1, 3).add(r(-1, 3))
                x shouldBe zero
            }
        }
        test("add zero is identity") {
            with(ops) {
                val x = r(3, 7)
                x.add(zero).numerator shouldBe x.numerator
                x.add(zero).denominator shouldBe x.denominator
            }
        }
    }

    context("subtract") {
        test("same denominator") {
            with(ops) {
                val x = r(3, 4).subtract(r(1, 4))
                x.numerator shouldBe 1L
                x.denominator shouldBe 2L
            }
        }
        test("yields negative result") {
            with(ops) {
                val x = r(1, 4).subtract(r(3, 4))
                x.numerator shouldBe -1L
                x.denominator shouldBe 2L
            }
        }
    }

    context("multiply") {
        test("basic product") {
            with(ops) {
                val x = r(2, 3).multiply(r(3, 4))
                x.numerator shouldBe 1L
                x.denominator shouldBe 2L
            }
        }
        test("multiply by zero") {
            with(ops) { r(5, 7).multiply(zero) shouldBe zero }
        }
        test("multiply by one") {
            with(ops) {
                val x = r(5, 7)
                val y = x.multiply(one)
                y.numerator shouldBe x.numerator
                y.denominator shouldBe x.denominator
            }
        }
        test("negative times negative") {
            with(ops) {
                val x = r(-2, 3).multiply(r(-3, 4))
                x.numerator shouldBe 1L
                x.denominator shouldBe 2L
            }
        }
    }

    context("divide") {
        test("basic quotient") {
            with(ops) {
                val x = r(1, 2).divide(r(3, 4))
                x.numerator shouldBe 2L
                x.denominator shouldBe 3L
            }
        }
        test("divide by itself is one") {
            with(ops) {
                val x = r(5, 7).divide(r(5, 7))
                x shouldBe one
            }
        }
        test("divide by zero throws") {
            with(ops) { shouldThrow<ArithmeticException> { r(1, 2).divide(zero) } }
        }
    }

    context("compareTo") {
        test("lesser than") {
            with(ops) { r(1, 3).compareTo(r(1, 2)) shouldBe -1 }
        }
        test("equal") {
            with(ops) { r(2, 4).compareTo(r(1, 2)) shouldBe 0 }
        }
        test("greater than") {
            with(ops) { r(2, 3).compareTo(r(1, 2)) shouldBe 1 }
        }
        test("negative less than positive") {
            with(ops) { r(-1, 3).compareTo(r(1, 3)) shouldBe -1 }
        }
    }

    context("integerPart and fractionalPart") {
        test("whole number") {
            with(ops) {
                r(6, 3).integerPart() shouldBe 2L
                r(6, 3).fractionalPart() shouldBe zero
            }
        }
        test("positive fraction") {
            with(ops) {
                r(7, 3).integerPart() shouldBe 2L
                r(7, 3).fractionalPart().numerator shouldBe 1L
                r(7, 3).fractionalPart().denominator shouldBe 3L
            }
        }
        test("negative fraction truncates toward zero") {
            with(ops) {
                r(-7, 3).integerPart() shouldBe -2L
                r(-7, 3).fractionalPart().numerator shouldBe -1L
                r(-7, 3).fractionalPart().denominator shouldBe 3L
            }
        }
        test("integerPart + fractionalPart == original") {
            with(ops) {
                val x = r(11, 4)
                of(x.integerPart(), 1L).add(x.fractionalPart()).let {
                    it.numerator shouldBe x.numerator
                    it.denominator shouldBe x.denominator
                }
            }
        }
    }

    context("floor and ceil") {
        test("positive exact") {
            with(ops) {
                r(6, 2).floor() shouldBe 3L
                r(6, 2).ceil() shouldBe 3L
            }
        }
        test("positive non-exact") {
            with(ops) {
                r(7, 2).floor() shouldBe 3L
                r(7, 2).ceil() shouldBe 4L
            }
        }
        test("negative non-exact") {
            with(ops) {
                r(-7, 2).floor() shouldBe -4L
                r(-7, 2).ceil() shouldBe -3L
            }
        }
    }
})

