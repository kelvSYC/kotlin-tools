package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointRemainder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DoubleDoubleRemainderTest : FunSpec({

    context("FloatingPointRemainder.Companion.doubleDoubleTruncating") {
        val ops = FloatingPointRemainder.doubleDoubleTruncating

        context("special cases") {
            test("NaN rem y returns NaN") {
                with(ops) { DoubleDouble.NaN.rem(DoubleDouble(3.0, 0.0)) }.high.isNaN() shouldBe true
            }

            test("x rem NaN returns NaN") {
                with(ops) { DoubleDouble(3.0, 0.0).rem(DoubleDouble.NaN) }.high.isNaN() shouldBe true
            }

            test("positive infinity rem y returns NaN") {
                with(ops) { DoubleDouble.POSITIVE_INFINITY.rem(DoubleDouble(3.0, 0.0)) }.high.isNaN() shouldBe true
            }

            test("negative infinity rem y returns NaN") {
                with(ops) { DoubleDouble.NEGATIVE_INFINITY.rem(DoubleDouble(3.0, 0.0)) }.high.isNaN() shouldBe true
            }

            test("x rem zero returns NaN") {
                with(ops) { DoubleDouble(3.0, 0.0).rem(DoubleDouble(0.0, 0.0)) }.high.isNaN() shouldBe true
            }

            test("x rem positive infinity returns x") {
                val r = with(ops) { DoubleDouble(3.0, 0.0).rem(DoubleDouble.POSITIVE_INFINITY) }
                r.high shouldBe 3.0
                r.low shouldBe 0.0
            }

            test("x rem negative infinity returns x") {
                val r = with(ops) { DoubleDouble(3.0, 0.0).rem(DoubleDouble.NEGATIVE_INFINITY) }
                r.high shouldBe 3.0
                r.low shouldBe 0.0
            }

            test("zero rem y returns zero") {
                val r = with(ops) { DoubleDouble(0.0, 0.0).rem(DoubleDouble(3.0, 0.0)) }
                r.high shouldBe 0.0
                r.low shouldBe 0.0
            }
        }

        context("sign follows dividend") {
            test("7 rem 3 = 1 (positive / positive)") {
                val r = with(ops) { DoubleDouble(7.0, 0.0).rem(DoubleDouble(3.0, 0.0)) }
                r.high shouldBe 1.0
            }

            test("-7 rem 3 = -1 (negative / positive)") {
                val r = with(ops) { DoubleDouble(-7.0, 0.0).rem(DoubleDouble(3.0, 0.0)) }
                r.high shouldBe -1.0
            }

            test("7 rem -3 = 1 (positive / negative)") {
                val r = with(ops) { DoubleDouble(7.0, 0.0).rem(DoubleDouble(-3.0, 0.0)) }
                r.high shouldBe 1.0
            }

            test("-7 rem -3 = -1 (negative / negative)") {
                val r = with(ops) { DoubleDouble(-7.0, 0.0).rem(DoubleDouble(-3.0, 0.0)) }
                r.high shouldBe -1.0
            }

            test("6 rem 3 = 0 (exact division)") {
                val r = with(ops) { DoubleDouble(6.0, 0.0).rem(DoubleDouble(3.0, 0.0)) }
                r.high shouldBe 0.0
                r.low shouldBe 0.0
            }
        }
    }

    context("FloatingPointRemainder.Companion.doubleDoubleIeee754") {
        val ops = FloatingPointRemainder.doubleDoubleIeee754

        context("special cases") {
            test("NaN rem y returns NaN") {
                with(ops) { DoubleDouble.NaN.rem(DoubleDouble(3.0, 0.0)) }.high.isNaN() shouldBe true
            }

            test("x rem NaN returns NaN") {
                with(ops) { DoubleDouble(3.0, 0.0).rem(DoubleDouble.NaN) }.high.isNaN() shouldBe true
            }

            test("positive infinity rem y returns NaN") {
                with(ops) { DoubleDouble.POSITIVE_INFINITY.rem(DoubleDouble(3.0, 0.0)) }.high.isNaN() shouldBe true
            }

            test("x rem zero returns NaN") {
                with(ops) { DoubleDouble(3.0, 0.0).rem(DoubleDouble(0.0, 0.0)) }.high.isNaN() shouldBe true
            }

            test("x rem positive infinity returns x") {
                val r = with(ops) { DoubleDouble(3.0, 0.0).rem(DoubleDouble.POSITIVE_INFINITY) }
                r.high shouldBe 3.0
                r.low shouldBe 0.0
            }

            test("zero rem y returns zero") {
                val r = with(ops) { DoubleDouble(0.0, 0.0).rem(DoubleDouble(3.0, 0.0)) }
                r.high shouldBe 0.0
                r.low shouldBe 0.0
            }
        }

        context("result in [-|y|/2, +|y|/2]") {
            // roundEven(7/3) = roundEven(2.333) = 2, so 7 - 2*3 = 1
            test("7 rem 3 = 1") {
                val r = with(ops) { DoubleDouble(7.0, 0.0).rem(DoubleDouble(3.0, 0.0)) }
                r.high shouldBe 1.0
            }

            // roundEven(5/3) = roundEven(1.667) = 2, so 5 - 2*3 = -1
            test("5 rem 3 = -1 (nearest even rounds up, result is negative)") {
                val r = with(ops) { DoubleDouble(5.0, 0.0).rem(DoubleDouble(3.0, 0.0)) }
                r.high shouldBe -1.0
            }

            // roundEven(-7/3) = roundEven(-2.333) = -2, so -7 - (-2)*3 = -1
            test("-7 rem 3 = -1") {
                val r = with(ops) { DoubleDouble(-7.0, 0.0).rem(DoubleDouble(3.0, 0.0)) }
                r.high shouldBe -1.0
            }
        }

        context("tie-breaking rounds to even") {
            // roundEven(2.5) = 2, so 12.5 - 2*5 = 2.5
            test("12.5 rem 5 = 2.5 (roundEven(2.5) = 2)") {
                val r = with(ops) { DoubleDouble(12.5, 0.0).rem(DoubleDouble(5.0, 0.0)) }
                r.high shouldBe 2.5
            }

            // roundEven(3.5) = 4, so 17.5 - 4*5 = -2.5
            test("17.5 rem 5 = -2.5 (roundEven(3.5) = 4)") {
                val r = with(ops) { DoubleDouble(17.5, 0.0).rem(DoubleDouble(5.0, 0.0)) }
                r.high shouldBe -2.5
            }
        }

        context("differs from truncating") {
            // Truncating: trunc(5/3) = 1, so 5 - 1*3 = 2
            // IEEE 754:   roundEven(5/3) = 2, so 5 - 2*3 = -1
            test("5 rem 3 differs from truncating rem") {
                val truncating = with(FloatingPointRemainder.doubleDoubleTruncating) {
                    DoubleDouble(5.0, 0.0).rem(DoubleDouble(3.0, 0.0))
                }
                val ieee = with(ops) { DoubleDouble(5.0, 0.0).rem(DoubleDouble(3.0, 0.0)) }
                truncating.high shouldBe 2.0
                ieee.high shouldBe -1.0
            }
        }
    }
})
