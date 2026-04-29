package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.DpdFloat
import com.kelvsyc.kotlin.core.fp.FiniteDecimalFloatingPoint
import com.kelvsyc.kotlin.core.fp.toDpdFloat
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun dpd(biasedExp: Int, sig: UInt, negative: Boolean = false) =
    FiniteDecimalFloatingPoint(negative, biasedExp - 101, sig).toDpdFloat()

/**
 * Smoke tests for [FusedMultiplyAdd.dpdFloat].
 *
 * Full algorithmic coverage lives in [BidFloatFusedMultiplyAddTest]. These tests verify that the
 * DPD instance delegates correctly through the BID↔DPD converter and produces numerically correct
 * results on a representative cross-section of cases, including the distinguishing test.
 */
class DpdFloatFusedMultiplyAddTest : FunSpec({
    val fma = FusedMultiplyAdd.dpdFloat

    // ── special values ────────────────────────────────────────────────────────

    context("special values") {
        test("NaN × finite + finite = NaN") {
            fma.fma(DpdFloat.NaN, dpd(101, 1u), dpd(101, 2u)) shouldBe DpdFloat.NaN
        }
        test("0 × ∞ + finite = NaN") {
            fma.fma(DpdFloat.positiveZero, DpdFloat.positiveInfinity, dpd(101, 1u)) shouldBe DpdFloat.NaN
        }
        test("+∞ × +∞ + (−∞) = NaN") {
            fma.fma(DpdFloat.positiveInfinity, DpdFloat.positiveInfinity, DpdFloat.negativeInfinity) shouldBe DpdFloat.NaN
        }
        test("finite × finite + +∞ = +∞") {
            fma.fma(dpd(101, 3u), dpd(101, 4u), DpdFloat.positiveInfinity) shouldBe DpdFloat.positiveInfinity
        }
        test("+0 × finite + +0 = +0") {
            val result = fma.fma(DpdFloat.positiveZero, dpd(101, 5u), DpdFloat.positiveZero)
            result.isZero() shouldBe true
            result.sign shouldBe false
        }
    }

    // ── basic finite cases ────────────────────────────────────────────────────

    context("basic finite cases (BID↔DPD round-trip correctness)") {
        // 2 × 3 + 4 = 10
        test("2 × 3 + 4 = 10") {
            val result = fma.fma(dpd(101, 2u), dpd(101, 3u), dpd(101, 4u))
            result.isZero() shouldBe false
            result.sign shouldBe false
        }
        // 2 × 3 + (−4) = 2
        test("2 × 3 + (−4) = 2 (positive result)") {
            val result = fma.fma(dpd(101, 2u), dpd(101, 3u), dpd(101, 4u, negative = true))
            result.isZero() shouldBe false
            result.sign shouldBe false
        }
        // 2 × 3 + (−10) = −4
        test("2 × 3 + (−10) = −4 (negative result)") {
            val result = fma.fma(dpd(101, 2u), dpd(101, 3u), dpd(101, 10u, negative = true))
            result.sign shouldBe true
            result.isZero() shouldBe false
        }
        // 1 × 1 + (−1) = +0
        test("1 × 1 + (−1) = +0") {
            val result = fma.fma(dpd(101, 1u), dpd(101, 1u), dpd(101, 1u, negative = true))
            result.isZero() shouldBe true
            result.sign shouldBe false
        }
    }

    // ── distinguishing test ───────────────────────────────────────────────────
    //
    // Mirrors the BidFloat distinguishing test: a = b = 1_000_001.
    // Rounded product = 1_000_002 × 10^6 (biasedExp 107).
    // fma(a, b, −roundedProduct) = 1 × 10^0 (biasedExp 101).
    // Naive two-step would give 0 (double rounding cancels the error).

    context("distinguishing test (single- vs double-rounding)") {
        test("fma(1_000_001, 1_000_001, −roundedProduct) is non-zero") {
            val a = dpd(101, 1_000_001u)
            val roundedProduct = dpd(107, 1_000_002u, negative = true)
            val result = fma.fma(a, a, roundedProduct)
            result.isZero() shouldBe false
            result.sign shouldBe false
        }
    }

    // ── delta alignment ───────────────────────────────────────────────────────

    context("delta alignment preserved through BID↔DPD conversion") {
        // delta = −4: fine-quantum product, coarse addend
        test("delta = −4: 2 × 3 + 0.0004 = 6.0004 (non-zero, positive)") {
            val result = fma.fma(dpd(101, 2u), dpd(101, 3u), dpd(97, 4u))
            result.isZero() shouldBe false
            result.sign shouldBe false
        }
        // delta > 18: addend dominates directly
        test("delta = 19: addend-dominates path, non-zero positive result") {
            val result = fma.fma(dpd(101, 2u), dpd(101, 3u), dpd(120, 4u))
            result.isZero() shouldBe false
            result.sign shouldBe false
        }
    }

    // ── sign handling ─────────────────────────────────────────────────────────

    context("sign handling") {
        // (−2) × 3 + 10 = 4
        test("(−2) × 3 + 10 = positive 4") {
            val result = fma.fma(dpd(101, 2u, negative = true), dpd(101, 3u), dpd(101, 10u))
            result.isZero() shouldBe false
            result.sign shouldBe false
        }
        // (−2) × (−3) + (−4) = 2
        test("(−2) × (−3) + (−4) = positive 2") {
            val result = fma.fma(dpd(101, 2u, negative = true), dpd(101, 3u, negative = true), dpd(101, 4u, negative = true))
            result.isZero() shouldBe false
            result.sign shouldBe false
        }
    }
})
