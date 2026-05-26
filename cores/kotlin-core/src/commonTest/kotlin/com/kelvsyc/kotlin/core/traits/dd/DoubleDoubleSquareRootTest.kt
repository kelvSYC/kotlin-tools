package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.dd.DoubleBinaryFloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSquareRoot
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DoubleDoubleSquareRootTest : FunSpec({

    context("FloatingPointSquareRoot.Companion.doubleDouble") {
        val ops = FloatingPointSquareRoot.doubleDouble

        context("special cases") {
            test("NaN returns NaN") {
                with(ops) { DoubleDouble.NaN.sqrt() }.high.isNaN() shouldBe true
            }

            test("negative returns NaN") {
                with(ops) { DoubleDouble(-1.0, 0.0).sqrt() }.high.isNaN() shouldBe true
            }

            test("positive infinity returns positive infinity") {
                val r = with(ops) { DoubleDouble.POSITIVE_INFINITY.sqrt() }
                r.high shouldBe Double.POSITIVE_INFINITY
                r.low shouldBe 0.0
            }

            test("positive zero returns positive zero") {
                val r = with(ops) { DoubleDouble(0.0, 0.0).sqrt() }
                r.high shouldBe 0.0
                r.low shouldBe 0.0
            }

            test("negative zero returns negative zero") {
                val r = with(ops) { DoubleDouble(-0.0, 0.0).sqrt() }
                r.high shouldBe -0.0
                r.low shouldBe 0.0
            }
        }

        context("perfect squares") {
            test("sqrt(4) ≈ 2") {
                val r = with(ops) { DoubleDouble(4.0, 0.0).sqrt() }
                r.high shouldBe 2.0
                r.low shouldBe 0.0
            }

            test("sqrt(9) ≈ 3") {
                val r = with(ops) { DoubleDouble(9.0, 0.0).sqrt() }
                r.high shouldBe 3.0
                r.low shouldBe 0.0
            }
        }

        context("non-perfect squares") {
            test("sqrt(2) squared ≈ 2") {
                val x = DoubleDouble(2.0, 0.0)
                val sqrt_x = with(ops) { x.sqrt() }

                // Verify (sqrt_x)² ≈ x using DoubleDouble arithmetic
                val arith = DoubleBinaryFloatingPointArithmetic.doubleDouble
                val squared = with(arith) { sqrt_x.multiply(sqrt_x) }

                // Both components should be very close
                squared.high shouldBe x.high  // 2.0 exactly
                (squared.low - x.low) shouldBe 0.0
            }

            test("sqrt(0.5) squared ≈ 0.5") {
                val x = DoubleDouble(0.5, 0.0)
                val sqrt_x = with(ops) { x.sqrt() }

                val arith = DoubleBinaryFloatingPointArithmetic.doubleDouble
                val squared = with(arith) { sqrt_x.multiply(sqrt_x) }

                squared.high shouldBe x.high  // 0.5 exactly
                (squared.low - x.low) shouldBe 0.0
            }

            test("sqrt(3) squared round-trips back to 3.0") {
                val arith = DoubleBinaryFloatingPointArithmetic.doubleDouble
                val x = DoubleDouble(3.0, 0.0)
                val r = with(ops) { x.sqrt() }
                val squared = with(arith) { r.multiply(r) }
                squared.high shouldBe 3.0
            }
        }
    }
})
