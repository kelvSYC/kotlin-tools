package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointHypot
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DoubleDoubleHypotTest : FunSpec({

    context("FloatingPointHypot.Companion.doubleDouble") {
        val ops = FloatingPointHypot.doubleDouble

        context("special cases") {
            test("NaN in x returns NaN") {
                with(ops) { DoubleDouble.NaN.hypot(DoubleDouble(1.0, 0.0)) }.high.isNaN() shouldBe true
            }

            test("NaN in y returns NaN") {
                with(ops) { DoubleDouble(1.0, 0.0).hypot(DoubleDouble.NaN) }.high.isNaN() shouldBe true
            }

            test("positive infinity in x returns positive infinity") {
                val r = with(ops) { DoubleDouble.POSITIVE_INFINITY.hypot(DoubleDouble(1.0, 0.0)) }
                r.high shouldBe Double.POSITIVE_INFINITY
            }

            test("positive infinity in y returns positive infinity") {
                val r = with(ops) { DoubleDouble(1.0, 0.0).hypot(DoubleDouble.POSITIVE_INFINITY) }
                r.high shouldBe Double.POSITIVE_INFINITY
            }

            test("infinity dominates NaN: hypot(+∞, NaN) = +∞") {
                val r = with(ops) { DoubleDouble.POSITIVE_INFINITY.hypot(DoubleDouble.NaN) }
                r.high shouldBe Double.POSITIVE_INFINITY
            }

            test("infinity dominates NaN: hypot(NaN, +∞) = +∞") {
                val r = with(ops) { DoubleDouble.NaN.hypot(DoubleDouble.POSITIVE_INFINITY) }
                r.high shouldBe Double.POSITIVE_INFINITY
            }

            test("hypot(0, 0) = 0") {
                val r = with(ops) { DoubleDouble(0.0, 0.0).hypot(DoubleDouble(0.0, 0.0)) }
                r.high shouldBe 0.0
                r.low shouldBe 0.0
            }

            test("hypot(3, 0) = 3") {
                val r = with(ops) { DoubleDouble(3.0, 0.0).hypot(DoubleDouble(0.0, 0.0)) }
                r.high shouldBe 3.0
            }

            test("hypot(0, 5) = 5") {
                val r = with(ops) { DoubleDouble(0.0, 0.0).hypot(DoubleDouble(5.0, 0.0)) }
                r.high shouldBe 5.0
            }
        }

        context("pythagorean triples") {
            test("hypot(3, 4) ≈ 5") {
                val r = with(ops) { DoubleDouble(3.0, 0.0).hypot(DoubleDouble(4.0, 0.0)) }
                r.high shouldBe 5.0
            }

            test("hypot(5, 12) ≈ 13") {
                val r = with(ops) { DoubleDouble(5.0, 0.0).hypot(DoubleDouble(12.0, 0.0)) }
                r.high shouldBe 13.0
            }
        }

        context("symmetry") {
            test("hypot(3, 4) == hypot(4, 3)") {
                val r1 = with(ops) { DoubleDouble(3.0, 0.0).hypot(DoubleDouble(4.0, 0.0)) }
                val r2 = with(ops) { DoubleDouble(4.0, 0.0).hypot(DoubleDouble(3.0, 0.0)) }
                r1.high shouldBe r2.high
            }

            test("hypot(5, 12) == hypot(12, 5)") {
                val r1 = with(ops) { DoubleDouble(5.0, 0.0).hypot(DoubleDouble(12.0, 0.0)) }
                val r2 = with(ops) { DoubleDouble(12.0, 0.0).hypot(DoubleDouble(5.0, 0.0)) }
                r1.high shouldBe r2.high
            }
        }

        context("precision") {
            test("hypot(1, 1) high component matches sqrt(2)") {
                val r = with(ops) { DoubleDouble(1.0, 0.0).hypot(DoubleDouble(1.0, 0.0)) }
                r.high shouldBe kotlin.math.sqrt(2.0)
            }
        }

        context("absolute value handling") {
            test("hypot(-3, 4) ≈ 5") {
                val r = with(ops) { DoubleDouble(-3.0, 0.0).hypot(DoubleDouble(4.0, 0.0)) }
                r.high shouldBe 5.0
            }

            test("hypot(3, -4) ≈ 5") {
                val r = with(ops) { DoubleDouble(3.0, 0.0).hypot(DoubleDouble(-4.0, 0.0)) }
                r.high shouldBe 5.0
            }

            test("hypot(-3, -4) ≈ 5") {
                val r = with(ops) { DoubleDouble(-3.0, 0.0).hypot(DoubleDouble(-4.0, 0.0)) }
                r.high shouldBe 5.0
            }
        }
    }
})
