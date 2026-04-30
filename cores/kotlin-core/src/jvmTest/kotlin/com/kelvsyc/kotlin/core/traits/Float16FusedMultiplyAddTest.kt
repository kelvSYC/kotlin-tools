package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.Float16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class Float16FusedMultiplyAddTest : FunSpec({

    context("FusedMultiplyAdd.Companion.float16") {

        // Category 1: distinguishing test — proves the implementation is not faked.
        //
        // a = 2^6 + 1 = 65, b = 2^6 − 1 = 63. The exact product 65 × 63 = 4095, which is not
        // representable in Float16 (11 significant bits; 4095 = 1.11111111111 × 2^11 rounds to
        // 1.0000000000 × 2^12 = 4096). A naive a * b + (−a*b) collapses to 0; a true FMA returns
        // the exact rounding error −1.
        context("distinguishing: fma(a, b, -(a*b)) yields exact product error, not zero") {
            val a = Float16(65.0f)   // 2^6 + 1 — exactly representable
            val b = Float16(63.0f)   // 2^6 − 1 — exactly representable
            val roundedProduct = a * b  // Float16(4096.0f) — rounds up from exact 4095

            test("a * b is rounded (precondition)") {
                roundedProduct.toFloat() shouldBe 4096.0f
            }

            test("fma(a, b, -roundedProduct) returns the rounding error -1") {
                FusedMultiplyAdd.float16.fma(a, b, -roundedProduct).toFloat() shouldBe -1.0f
            }

            test("naive a * b + (-roundedProduct) collapses to 0 (confirming the test is meaningful)") {
                (a * b + (-roundedProduct)).toFloat() shouldBe 0.0f
            }
        }

        // Category 2: correctly-rounded result where naive arithmetic differs.
        //
        // Float16.MAX_VALUE = 65504. 65504 × 2 = 131008, which overflows Float16 to Infinity.
        // The true value 65504 × 2 + (−65504) = 65504 is finite and equals MAX_VALUE.
        // A naive multiply-then-add propagates the spurious infinity; FMA returns the correct result.
        context("correctly-rounded: fma avoids spurious overflow when product and addend cancel") {
            test("fma(MAX_VALUE, 2.0, -MAX_VALUE) = MAX_VALUE; naive overflows to Infinity") {
                FusedMultiplyAdd.float16.fma(Float16.MAX_VALUE, Float16(2.0f), -Float16.MAX_VALUE).bits shouldBe Float16.MAX_VALUE.bits
            }

            test("naive MAX_VALUE * 2.0 overflows (confirming the test is meaningful)") {
                (Float16.MAX_VALUE * Float16(2.0f)).isInfinite() shouldBe true
            }
        }

        // Category 3: special value propagation (IEEE 754 semantics).
        context("special values") {
            test("NaN in first operand propagates") {
                FusedMultiplyAdd.float16.fma(Float16.NaN, Float16(1.0f), Float16(1.0f)).isNaN() shouldBe true
            }

            test("NaN in second operand propagates") {
                FusedMultiplyAdd.float16.fma(Float16(1.0f), Float16.NaN, Float16(1.0f)).isNaN() shouldBe true
            }

            test("NaN in third operand propagates") {
                FusedMultiplyAdd.float16.fma(Float16(1.0f), Float16(1.0f), Float16.NaN).isNaN() shouldBe true
            }

            test("Infinity * 0.0 + c is NaN (invalid operation)") {
                FusedMultiplyAdd.float16.fma(Float16.POSITIVE_INFINITY, Float16(0.0f), Float16(1.0f)).isNaN() shouldBe true
            }

            test("0.0 * Infinity + c is NaN (invalid operation)") {
                FusedMultiplyAdd.float16.fma(Float16(0.0f), Float16.POSITIVE_INFINITY, Float16(1.0f)).isNaN() shouldBe true
            }

            test("+Infinity * positive + (-Infinity) is NaN (infinity minus infinity)") {
                FusedMultiplyAdd.float16.fma(Float16.POSITIVE_INFINITY, Float16(1.0f), Float16.NEGATIVE_INFINITY).isNaN() shouldBe true
            }

            test("+Infinity * positive + finite is +Infinity") {
                FusedMultiplyAdd.float16.fma(Float16.POSITIVE_INFINITY, Float16(2.0f), Float16(1.0f)).bits shouldBe Float16.POSITIVE_INFINITY.bits
            }

            test("+Infinity * negative + finite is -Infinity") {
                FusedMultiplyAdd.float16.fma(Float16.POSITIVE_INFINITY, Float16(-2.0f), Float16(1.0f)).bits shouldBe Float16.NEGATIVE_INFINITY.bits
            }

            test("+Infinity * positive + (+Infinity) is +Infinity") {
                FusedMultiplyAdd.float16.fma(Float16.POSITIVE_INFINITY, Float16(1.0f), Float16.POSITIVE_INFINITY).bits shouldBe Float16.POSITIVE_INFINITY.bits
            }
        }

        // Category 4: exact cases — result is exact, FMA and naive must agree.
        context("exact cases") {
            test("fma(2.0, 3.0, 4.0) = 10.0") {
                FusedMultiplyAdd.float16.fma(Float16(2.0f), Float16(3.0f), Float16(4.0f)).toFloat() shouldBe 10.0f
            }

            test("fma(1.0, 1.0, 0.0) = 1.0") {
                FusedMultiplyAdd.float16.fma(Float16(1.0f), Float16(1.0f), Float16(0.0f)).toFloat() shouldBe 1.0f
            }

            test("fma(-1.0, 2.0, 4.0) = 2.0") {
                FusedMultiplyAdd.float16.fma(Float16(-1.0f), Float16(2.0f), Float16(4.0f)).toFloat() shouldBe 2.0f
            }

            test("fma(0.5, 0.5, 0.25) = 0.5") {
                FusedMultiplyAdd.float16.fma(Float16(0.5f), Float16(0.5f), Float16(0.25f)).toFloat() shouldBe 0.5f
            }

            test("fma(0.0, x, c) = c (zero product leaves addend unchanged)") {
                FusedMultiplyAdd.float16.fma(Float16(0.0f), Float16(123.0f), Float16(7.0f)).toFloat() shouldBe 7.0f
            }
        }

        context("singleton identity") {
            test("Companion.float16 returns the same instance on repeated access") {
                FusedMultiplyAdd.float16 shouldBe FusedMultiplyAdd.float16
            }
        }
    }
})
