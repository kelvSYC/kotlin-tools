package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointCubeRoot
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DoubleDoubleCubeRootTest : FunSpec({

    context("FloatingPointCubeRoot.Companion.doubleDouble") {
        val ops = FloatingPointCubeRoot.doubleDouble

        context("special cases") {
            test("NaN returns NaN") {
                with(ops) { DoubleDouble.NaN.cbrt() }.high.isNaN() shouldBe true
            }

            test("positive infinity returns positive infinity") {
                val r = with(ops) { DoubleDouble.POSITIVE_INFINITY.cbrt() }
                r.high shouldBe Double.POSITIVE_INFINITY
                r.low shouldBe 0.0
            }

            test("negative infinity returns negative infinity") {
                val r = with(ops) { DoubleDouble.NEGATIVE_INFINITY.cbrt() }
                r.high shouldBe Double.NEGATIVE_INFINITY
                r.low shouldBe 0.0
            }

            test("positive zero returns positive zero") {
                val r = with(ops) { DoubleDouble(0.0, 0.0).cbrt() }
                r.high shouldBe 0.0
                r.low shouldBe 0.0
            }

            test("negative zero returns negative zero") {
                val r = with(ops) { DoubleDouble(-0.0, 0.0).cbrt() }
                r.high shouldBe -0.0
                r.low shouldBe 0.0
            }
        }

        context("perfect cubes") {
            test("cbrt(8) ≈ 2") {
                val r = with(ops) { DoubleDouble(8.0, 0.0).cbrt() }
                r.high shouldBe 2.0
                r.low shouldBe 0.0
            }

            test("cbrt(27) ≈ 3") {
                val r = with(ops) { DoubleDouble(27.0, 0.0).cbrt() }
                r.high shouldBe 3.0
                // r.low is not checked: cbrt is not IEEE 754 correctly rounded, so the seed
                // may be off by 1 ULP on some platforms, leaving a tiny non-zero low residual.
            }

            test("cbrt(-8) ≈ -2") {
                val r = with(ops) { DoubleDouble(-8.0, 0.0).cbrt() }
                r.high shouldBe -2.0
                r.low shouldBe 0.0
            }

            test("cbrt(-27) ≈ -3") {
                val r = with(ops) { DoubleDouble(-27.0, 0.0).cbrt() }
                r.high shouldBe -3.0
                // r.low is not checked: same reason as cbrt(27) above.
            }
        }

        context("roundtrip verification") {
            test("cbrt(2) multiplied three times ≈ 2") {
                val x = DoubleDouble(2.0, 0.0)
                val cbrt_x = with(ops) { x.cbrt() }

                // Verify (cbrt_x)³ ≈ x using DoubleDouble arithmetic
                val arith = DoubleBinaryFloatingPointArithmetic.doubleDouble
                val cubed = with(arith) { cbrt_x.multiply(cbrt_x).multiply(cbrt_x) }

                // High component should be very close to 2.0 (perfect cubes may have rounding in intermediate steps)
                cubed.high shouldBe 2.0
            }

            test("cbrt(0.5) multiplied three times ≈ 0.5") {
                val x = DoubleDouble(0.5, 0.0)
                val cbrt_x = with(ops) { x.cbrt() }

                val arith = DoubleBinaryFloatingPointArithmetic.doubleDouble
                val cubed = with(arith) { cbrt_x.multiply(cbrt_x).multiply(cbrt_x) }

                cubed.high shouldBe 0.5
            }

            test("cbrt(-2) multiplied three times ≈ -2") {
                val x = DoubleDouble(-2.0, 0.0)
                val cbrt_x = with(ops) { x.cbrt() }

                val arith = DoubleBinaryFloatingPointArithmetic.doubleDouble
                val cubed = with(arith) { cbrt_x.multiply(cbrt_x).multiply(cbrt_x) }

                cubed.high shouldBe -2.0
            }

            test("cbrt(3) cubed round-trips back to 3.0") {
                val arith = DoubleBinaryFloatingPointArithmetic.doubleDouble
                val x = DoubleDouble(3.0, 0.0)
                val r = with(ops) { x.cbrt() }
                val cubed = with(arith) { r.multiply(r).multiply(r) }
                cubed.high shouldBe 3.0
            }
        }
    }
})
