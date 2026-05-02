package com.kelvsyc.kotlin.core.traits.fp

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.floats.shouldBeNaN
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class FusedMultiplyAddTest : FunSpec({

    // ── FusedMultiplyAdd.Companion.double ─────────────────────────────────────

    context("FusedMultiplyAdd.Companion.double") {

        // Category 1: distinguishing test — proves the implementation is not faked.
        //
        // a = 2^27 + 1, b = 2^27 − 1. The exact product a × b = 2^54 − 1, which is not
        // representable as a Double (ulp at 2^54 is 4), so it rounds to 2^54. A naive
        // a * b + (−a*b) collapses to 0; a true FMA returns the exact rounding error −1.0.
        context("distinguishing: fma(a, b, -(a*b)) yields exact product error, not zero") {
            val a = (1L shl 27).toDouble() + 1.0   // 134217729.0
            val b = (1L shl 27).toDouble() - 1.0   // 134217727.0
            val roundedProduct = a * b              // 2^54 = 18014398509481984.0 (rounded)

            test("a * b is rounded (precondition)") {
                roundedProduct shouldBe 18014398509481984.0
            }

            test("fma(a, b, -roundedProduct) returns the rounding error -1.0") {
                FusedMultiplyAdd.double.fma(a, b, -roundedProduct) shouldBe -1.0
            }

            test("naive a * b + (-roundedProduct) collapses to 0.0 (confirming the test is meaningful)") {
                (a * b) + (-roundedProduct) shouldBe 0.0
            }
        }

        // Category 2: correctly-rounded result where naive arithmetic differs.
        //
        // When a * b overflows to Infinity but the true value a * b + c is finite, naive
        // addition propagates the spurious infinity while FMA returns the correct finite result.
        context("correctly-rounded: fma avoids spurious overflow when product and addend cancel") {
            test("fma(MAX_VALUE, 2.0, -MAX_VALUE) = MAX_VALUE; naive overflows to Infinity") {
                FusedMultiplyAdd.double.fma(Double.MAX_VALUE, 2.0, -Double.MAX_VALUE) shouldBe Double.MAX_VALUE
            }

            test("naive Double.MAX_VALUE * 2.0 overflows (confirming the test is meaningful)") {
                Double.MAX_VALUE * 2.0 shouldBe Double.POSITIVE_INFINITY
            }
        }

        // Category 3: special value propagation (IEEE 754 semantics).
        context("special values") {
            test("NaN in first operand propagates") {
                FusedMultiplyAdd.double.fma(Double.NaN, 1.0, 1.0).shouldBeNaN()
            }

            test("NaN in second operand propagates") {
                FusedMultiplyAdd.double.fma(1.0, Double.NaN, 1.0).shouldBeNaN()
            }

            test("NaN in third operand propagates") {
                FusedMultiplyAdd.double.fma(1.0, 1.0, Double.NaN).shouldBeNaN()
            }

            test("Infinity * 0.0 + c is NaN (invalid operation)") {
                FusedMultiplyAdd.double.fma(Double.POSITIVE_INFINITY, 0.0, 1.0).shouldBeNaN()
            }

            test("0.0 * Infinity + c is NaN (invalid operation)") {
                FusedMultiplyAdd.double.fma(0.0, Double.POSITIVE_INFINITY, 1.0).shouldBeNaN()
            }

            test("+Infinity * positive + (-Infinity) is NaN (infinity minus infinity)") {
                FusedMultiplyAdd.double.fma(Double.POSITIVE_INFINITY, 1.0, Double.NEGATIVE_INFINITY).shouldBeNaN()
            }

            test("+Infinity * positive + finite is +Infinity") {
                FusedMultiplyAdd.double.fma(Double.POSITIVE_INFINITY, 2.0, 1.0) shouldBe Double.POSITIVE_INFINITY
            }

            test("+Infinity * negative + finite is -Infinity") {
                FusedMultiplyAdd.double.fma(Double.POSITIVE_INFINITY, -2.0, 1.0) shouldBe Double.NEGATIVE_INFINITY
            }

            test("+Infinity * positive + (+Infinity) is +Infinity") {
                FusedMultiplyAdd.double.fma(Double.POSITIVE_INFINITY, 1.0, Double.POSITIVE_INFINITY) shouldBe Double.POSITIVE_INFINITY
            }
        }

        // Category 4: exact cases — result is exact, FMA and naive must agree.
        context("exact cases") {
            test("fma(2.0, 3.0, 4.0) = 10.0") {
                FusedMultiplyAdd.double.fma(2.0, 3.0, 4.0) shouldBe 10.0
            }

            test("fma(1.0, 1.0, 0.0) = 1.0") {
                FusedMultiplyAdd.double.fma(1.0, 1.0, 0.0) shouldBe 1.0
            }

            test("fma(-1.0, 2.0, 4.0) = 2.0") {
                FusedMultiplyAdd.double.fma(-1.0, 2.0, 4.0) shouldBe 2.0
            }

            test("fma(0.5, 0.5, 0.25) = 0.5") {
                FusedMultiplyAdd.double.fma(0.5, 0.5, 0.25) shouldBe 0.5
            }

            test("fma(0.0, x, c) = c (zero product leaves addend unchanged)") {
                FusedMultiplyAdd.double.fma(0.0, 12345.0, 7.0) shouldBe 7.0
            }
        }
    }

    // ── FusedMultiplyAdd.Companion.float ──────────────────────────────────────

    context("FusedMultiplyAdd.Companion.float") {

        // Category 1: distinguishing test.
        //
        // a = 2^13 + 1, b = 2^13 − 1. Exact product = 2^26 − 1; ulp at 2^26 is 8 for Float
        // (mantissa bits = 23, so spacing = 2^(26-23) = 8), so it rounds to 2^26.
        context("distinguishing: fma(a, b, -(a*b)) yields exact product error, not zero") {
            val a = (1 shl 13).toFloat() + 1.0f   // 8193.0f
            val b = (1 shl 13).toFloat() - 1.0f   // 8191.0f
            val roundedProduct = a * b             // 2^26 = 67108864.0f (rounded)

            test("a * b is rounded (precondition)") {
                roundedProduct shouldBe 67108864.0f
            }

            test("fma(a, b, -roundedProduct) returns the rounding error -1.0f") {
                FusedMultiplyAdd.float.fma(a, b, -roundedProduct) shouldBe -1.0f
            }

            test("naive a * b + (-roundedProduct) collapses to 0.0f (confirming the test is meaningful)") {
                (a * b) + (-roundedProduct) shouldBe 0.0f
            }
        }

        // Category 2: correctly-rounded result where naive arithmetic differs.
        context("correctly-rounded: fma avoids spurious overflow when product and addend cancel") {
            test("fma(MAX_VALUE, 2.0f, -MAX_VALUE) = MAX_VALUE; naive overflows to Infinity") {
                FusedMultiplyAdd.float.fma(Float.MAX_VALUE, 2.0f, -Float.MAX_VALUE) shouldBe Float.MAX_VALUE
            }

            test("naive Float.MAX_VALUE * 2.0f overflows (confirming the test is meaningful)") {
                Float.MAX_VALUE * 2.0f shouldBe Float.POSITIVE_INFINITY
            }
        }

        // Category 3: special value propagation.
        context("special values") {
            test("NaN in first operand propagates") {
                FusedMultiplyAdd.float.fma(Float.NaN, 1.0f, 1.0f).shouldBeNaN()
            }

            test("NaN in second operand propagates") {
                FusedMultiplyAdd.float.fma(1.0f, Float.NaN, 1.0f).shouldBeNaN()
            }

            test("NaN in third operand propagates") {
                FusedMultiplyAdd.float.fma(1.0f, 1.0f, Float.NaN).shouldBeNaN()
            }

            test("Infinity * 0.0f + c is NaN (invalid operation)") {
                FusedMultiplyAdd.float.fma(Float.POSITIVE_INFINITY, 0.0f, 1.0f).shouldBeNaN()
            }

            test("0.0f * Infinity + c is NaN (invalid operation)") {
                FusedMultiplyAdd.float.fma(0.0f, Float.POSITIVE_INFINITY, 1.0f).shouldBeNaN()
            }

            test("+Infinity * positive + (-Infinity) is NaN (infinity minus infinity)") {
                FusedMultiplyAdd.float.fma(Float.POSITIVE_INFINITY, 1.0f, Float.NEGATIVE_INFINITY).shouldBeNaN()
            }

            test("+Infinity * positive + finite is +Infinity") {
                FusedMultiplyAdd.float.fma(Float.POSITIVE_INFINITY, 2.0f, 1.0f) shouldBe Float.POSITIVE_INFINITY
            }

            test("+Infinity * negative + finite is -Infinity") {
                FusedMultiplyAdd.float.fma(Float.POSITIVE_INFINITY, -2.0f, 1.0f) shouldBe Float.NEGATIVE_INFINITY
            }

            test("+Infinity * positive + (+Infinity) is +Infinity") {
                FusedMultiplyAdd.float.fma(Float.POSITIVE_INFINITY, 1.0f, Float.POSITIVE_INFINITY) shouldBe Float.POSITIVE_INFINITY
            }
        }

        // Category 4: exact cases.
        context("exact cases") {
            test("fma(2.0f, 3.0f, 4.0f) = 10.0f") {
                FusedMultiplyAdd.float.fma(2.0f, 3.0f, 4.0f) shouldBe 10.0f
            }

            test("fma(1.0f, 1.0f, 0.0f) = 1.0f") {
                FusedMultiplyAdd.float.fma(1.0f, 1.0f, 0.0f) shouldBe 1.0f
            }

            test("fma(-1.0f, 2.0f, 4.0f) = 2.0f") {
                FusedMultiplyAdd.float.fma(-1.0f, 2.0f, 4.0f) shouldBe 2.0f
            }

            test("fma(0.5f, 0.5f, 0.25f) = 0.5f") {
                FusedMultiplyAdd.float.fma(0.5f, 0.5f, 0.25f) shouldBe 0.5f
            }

            test("fma(0.0f, x, c) = c (zero product leaves addend unchanged)") {
                FusedMultiplyAdd.float.fma(0.0f, 12345.0f, 7.0f) shouldBe 7.0f
            }
        }
    }

    // ── Cross-instance: double and float instances are distinct singletons ─────

    context("singleton identity") {
        test("Companion.double returns the same instance on repeated access") {
            FusedMultiplyAdd.double shouldBe FusedMultiplyAdd.double
        }

        test("Companion.float returns the same instance on repeated access") {
            FusedMultiplyAdd.float shouldBe FusedMultiplyAdd.float
        }

        test("Companion.double and Companion.float are distinct instances") {
            FusedMultiplyAdd.double shouldNotBe FusedMultiplyAdd.float
        }
    }
})
