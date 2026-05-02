package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BFloat16FusedMultiplyAddTest : FunSpec({

    context("FusedMultiplyAdd.Companion.bfloat16") {

        // Category 1: distinguishing test — proves the implementation is not faked.
        //
        // a = 2^5 + 1 = 33, b = 2^5 − 1 = 31. The exact product 33 × 31 = 1023, which is not
        // representable in BFloat16 (8 significant bits; 1023 = 1.11111111_1 × 2^9 rounds to
        // 1.0000000 × 2^10 = 1024). A naive a * b + (−a*b) collapses to 0; a true FMA returns
        // the exact rounding error −1.
        context("distinguishing: fma(a, b, -(a*b)) yields exact product error, not zero") {
            val a = BFloat16(33.0f)   // 2^5 + 1 — exactly representable
            val b = BFloat16(31.0f)   // 2^5 − 1 — exactly representable
            val roundedProduct = a * b  // BFloat16(1024.0f) — rounds up from exact 1023

            test("a * b is rounded (precondition)") {
                roundedProduct.toFloat() shouldBe 1024.0f
            }

            test("fma(a, b, -roundedProduct) returns the rounding error -1") {
                FusedMultiplyAdd.bfloat16.fma(a, b, -roundedProduct).toFloat() shouldBe -1.0f
            }

            test("naive a * b + (-roundedProduct) collapses to 0 (confirming the test is meaningful)") {
                (a * b + (-roundedProduct)).toFloat() shouldBe 0.0f
            }
        }

        // Category 2: correctly-rounded result where naive arithmetic differs.
        //
        // BFloat16.MAX_VALUE × 2 overflows to Infinity in both BFloat16 and Float32, but the true
        // value BFloat16.MAX_VALUE × 2 + (−BFloat16.MAX_VALUE) = BFloat16.MAX_VALUE is finite.
        // A naive multiply-then-add propagates the spurious infinity; FMA returns the correct result.
        context("correctly-rounded: fma avoids spurious overflow when product and addend cancel") {
            test("fma(MAX_VALUE, 2.0, -MAX_VALUE) = MAX_VALUE; naive overflows to Infinity") {
                FusedMultiplyAdd.bfloat16.fma(BFloat16.MAX_VALUE, BFloat16(2.0f), -BFloat16.MAX_VALUE).bits shouldBe BFloat16.MAX_VALUE.bits
            }

            test("naive MAX_VALUE * 2.0 overflows (confirming the test is meaningful)") {
                (BFloat16.MAX_VALUE * BFloat16(2.0f)).isInfinite() shouldBe true
            }
        }

        // Category 3: special value propagation (IEEE 754 semantics).
        context("special values") {
            test("NaN in first operand propagates") {
                FusedMultiplyAdd.bfloat16.fma(BFloat16.NaN, BFloat16(1.0f), BFloat16(1.0f)).isNaN() shouldBe true
            }

            test("NaN in second operand propagates") {
                FusedMultiplyAdd.bfloat16.fma(BFloat16(1.0f), BFloat16.NaN, BFloat16(1.0f)).isNaN() shouldBe true
            }

            test("NaN in third operand propagates") {
                FusedMultiplyAdd.bfloat16.fma(BFloat16(1.0f), BFloat16(1.0f), BFloat16.NaN).isNaN() shouldBe true
            }

            test("Infinity * 0.0 + c is NaN (invalid operation)") {
                FusedMultiplyAdd.bfloat16.fma(BFloat16.POSITIVE_INFINITY, BFloat16(0.0f), BFloat16(1.0f)).isNaN() shouldBe true
            }

            test("0.0 * Infinity + c is NaN (invalid operation)") {
                FusedMultiplyAdd.bfloat16.fma(BFloat16(0.0f), BFloat16.POSITIVE_INFINITY, BFloat16(1.0f)).isNaN() shouldBe true
            }

            test("+Infinity * positive + (-Infinity) is NaN (infinity minus infinity)") {
                FusedMultiplyAdd.bfloat16.fma(BFloat16.POSITIVE_INFINITY, BFloat16(1.0f), BFloat16.NEGATIVE_INFINITY).isNaN() shouldBe true
            }

            test("+Infinity * positive + finite is +Infinity") {
                FusedMultiplyAdd.bfloat16.fma(BFloat16.POSITIVE_INFINITY, BFloat16(2.0f), BFloat16(1.0f)).bits shouldBe BFloat16.POSITIVE_INFINITY.bits
            }

            test("+Infinity * negative + finite is -Infinity") {
                FusedMultiplyAdd.bfloat16.fma(BFloat16.POSITIVE_INFINITY, BFloat16(-2.0f), BFloat16(1.0f)).bits shouldBe BFloat16.NEGATIVE_INFINITY.bits
            }

            test("+Infinity * positive + (+Infinity) is +Infinity") {
                FusedMultiplyAdd.bfloat16.fma(BFloat16.POSITIVE_INFINITY, BFloat16(1.0f), BFloat16.POSITIVE_INFINITY).bits shouldBe BFloat16.POSITIVE_INFINITY.bits
            }
        }

        // Category 4: exact cases — result is exact, FMA and naive must agree.
        context("exact cases") {
            test("fma(2.0, 3.0, 4.0) = 10.0") {
                FusedMultiplyAdd.bfloat16.fma(BFloat16(2.0f), BFloat16(3.0f), BFloat16(4.0f)).toFloat() shouldBe 10.0f
            }

            test("fma(1.0, 1.0, 0.0) = 1.0") {
                FusedMultiplyAdd.bfloat16.fma(BFloat16(1.0f), BFloat16(1.0f), BFloat16(0.0f)).toFloat() shouldBe 1.0f
            }

            test("fma(-1.0, 2.0, 4.0) = 2.0") {
                FusedMultiplyAdd.bfloat16.fma(BFloat16(-1.0f), BFloat16(2.0f), BFloat16(4.0f)).toFloat() shouldBe 2.0f
            }

            test("fma(0.5, 0.5, 0.25) = 0.5") {
                FusedMultiplyAdd.bfloat16.fma(BFloat16(0.5f), BFloat16(0.5f), BFloat16(0.25f)).toFloat() shouldBe 0.5f
            }

            test("fma(0.0, x, c) = c (zero product leaves addend unchanged)") {
                FusedMultiplyAdd.bfloat16.fma(BFloat16(0.0f), BFloat16(123.0f), BFloat16(7.0f)).toFloat() shouldBe 7.0f
            }
        }

        context("singleton identity") {
            test("Companion.bfloat16 returns the same instance on repeated access") {
                FusedMultiplyAdd.bfloat16 shouldBe FusedMultiplyAdd.bfloat16
            }
        }
    }
})
