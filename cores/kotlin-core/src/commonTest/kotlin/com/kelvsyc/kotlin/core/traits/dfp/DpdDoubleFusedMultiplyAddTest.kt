package com.kelvsyc.kotlin.core.traits.dfp
import com.kelvsyc.kotlin.core.traits.fp.FusedMultiplyAdd

import com.kelvsyc.kotlin.core.DpdDouble
import com.kelvsyc.kotlin.core.fp.FiniteDecimalFloatingPoint
import com.kelvsyc.kotlin.core.fp.toDpdDouble
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun dpd64(biasedExp: Int, sig: ULong, negative: Boolean = false) =
    FiniteDecimalFloatingPoint(negative, biasedExp - 398, sig).toDpdDouble()

/**
 * Smoke tests for [FusedMultiplyAdd.dpdDouble].
 *
 * Full algorithmic coverage lives in [BidDoubleFusedMultiplyAddTest]. These tests verify that the
 * DPD instance delegates correctly through the BID↔DPD converter and produces numerically correct
 * results on a representative cross-section of cases, including the distinguishing test.
 */
class DpdDoubleFusedMultiplyAddTest : FunSpec({
    val fma = FusedMultiplyAdd.dpdDouble

    // ── special values ────────────────────────────────────────────────────────

    context("special values") {
        test("NaN × finite + finite = NaN") {
            fma.fma(DpdDouble.NaN, dpd64(398, 1uL), dpd64(398, 2uL)).isNaN() shouldBe true
        }
        test("0 × ∞ + finite = NaN") {
            fma.fma(DpdDouble.positiveZero, DpdDouble.positiveInfinity, dpd64(398, 1uL)).isNaN() shouldBe true
        }
        test("+∞ × +∞ + (−∞) = NaN") {
            fma.fma(DpdDouble.positiveInfinity, DpdDouble.positiveInfinity, DpdDouble.negativeInfinity).isNaN() shouldBe true
        }
        test("finite × finite + +∞ = +∞") {
            fma.fma(dpd64(398, 3uL), dpd64(398, 4uL), DpdDouble.positiveInfinity) shouldBe DpdDouble.positiveInfinity
        }
        test("+0 × finite + +0 = +0") {
            val result = fma.fma(DpdDouble.positiveZero, dpd64(398, 5uL), DpdDouble.positiveZero)
            result.isZero() shouldBe true
            result.sign shouldBe false
        }
    }

    // ── basic finite cases ────────────────────────────────────────────────────

    context("basic finite cases (BID↔DPD round-trip correctness)") {
        test("2 × 3 + 4 = 10") {
            val result = fma.fma(dpd64(398, 2uL), dpd64(398, 3uL), dpd64(398, 4uL))
            result.isZero() shouldBe false
            result.sign shouldBe false
            result.significand shouldBe 10L
        }
        test("2 × 3 + (−4) = 2 (positive result)") {
            val result = fma.fma(dpd64(398, 2uL), dpd64(398, 3uL), dpd64(398, 4uL, negative = true))
            result.isZero() shouldBe false
            result.sign shouldBe false
            result.significand shouldBe 2L
        }
        test("2 × 3 + (−10) = −4 (negative result)") {
            val result = fma.fma(dpd64(398, 2uL), dpd64(398, 3uL), dpd64(398, 10uL, negative = true))
            result.sign shouldBe true
            result.isZero() shouldBe false
        }
        test("1 × 1 + (−1) = +0") {
            val result = fma.fma(dpd64(398, 1uL), dpd64(398, 1uL), dpd64(398, 1uL, negative = true))
            result.isZero() shouldBe true
            result.sign shouldBe false
        }
    }

    // ── distinguishing test ───────────────────────────────────────────────────
    //
    // a = 1_000_000_000_000_001 × 10^0 (= 10^15 + 1, biasedExp 398).
    // a² = 10^30 + 2×10^15 + 1 (exact, 31 digits).
    // roundedProduct = 1_000_000_000_000_002 × 10^15 (biasedExp 413).
    // fma(a, a, −roundedProduct) = 1 × 10^0; naive two-step gives 0.

    context("distinguishing test (single- vs double-rounding)") {
        test("fma(a, a, −round(a²)) = exact residual 1, not 0") {
            val a = dpd64(398, 1_000_000_000_000_001uL)
            val negRoundedProduct = dpd64(413, 1_000_000_000_000_002uL, negative = true)
            val result = fma.fma(a, a, negRoundedProduct)
            result.significand shouldBe 1L
            result.biasedExponent shouldBe 398
            result.sign shouldBe false
        }
    }

    // ── sign handling ─────────────────────────────────────────────────────────

    context("sign handling") {
        test("(−2) × 3 + 10 = positive 4") {
            val result = fma.fma(dpd64(398, 2uL, negative = true), dpd64(398, 3uL), dpd64(398, 10uL))
            result.isZero() shouldBe false
            result.sign shouldBe false
        }
        test("(−2) × (−3) + (−4) = positive 2") {
            val result = fma.fma(dpd64(398, 2uL, negative = true), dpd64(398, 3uL, negative = true), dpd64(398, 4uL, negative = true))
            result.isZero() shouldBe false
            result.sign shouldBe false
        }
    }
})
