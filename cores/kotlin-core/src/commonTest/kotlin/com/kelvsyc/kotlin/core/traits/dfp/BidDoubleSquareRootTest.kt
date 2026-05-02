package com.kelvsyc.kotlin.core.traits.dfp
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSquareRoot

import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.fp.FiniteDecimalFloatingPoint
import com.kelvsyc.kotlin.core.fp.toBidDouble
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun bid64(biasedExp: Int, sig: ULong, negative: Boolean = false) =
    FiniteDecimalFloatingPoint(negative, biasedExp - 398, sig).toBidDouble()

/**
 * Tests for [FloatingPointSquareRoot.bidDouble].
 *
 * Expected significands and biasedExponents are verified by hand against the split-Long algorithm.
 * For each case: N = sig × 10^k, I = floor(sqrt(N)), round up iff N − I² > I.
 */
class BidDoubleSquareRootTest : FunSpec({
    val sqrt = FloatingPointSquareRoot.bidDouble

    // ── Special values ────────────────────────────────────────────────────────

    context("special values") {
        test("NaN → NaN") {
            with(sqrt) { BidDouble.NaN.sqrt() }.isNaN() shouldBe true
        }
        test("+∞ → +∞") {
            with(sqrt) { BidDouble.positiveInfinity.sqrt() } shouldBe BidDouble.positiveInfinity
        }
        test("-∞ → NaN") {
            with(sqrt) { BidDouble.negativeInfinity.sqrt() }.isNaN() shouldBe true
        }
        test("+0 → +0") {
            with(sqrt) { BidDouble.positiveZero.sqrt() } shouldBe BidDouble.positiveZero
        }
        test("-0 → -0 (IEEE 754 §5.4.1)") {
            with(sqrt) { BidDouble.negativeZero.sqrt() } shouldBe BidDouble.negativeZero
        }
        test("negative finite → NaN") {
            with(sqrt) { bid64(398, 1uL, negative = true).sqrt() }.isNaN() shouldBe true
        }
    }

    // ── Perfect squares: one result per digit-count bracket ──────────────────
    // N = sig × 10^k = 10^30 in all cases below → isqrt = 10^15, rem = 0.
    // Result biasedExponent = eAdj/2 − k/2 + 398 where eAdj = eOrig (all even here).

    context("perfect square: d=1, k=30") {
        // value = 1 × 10^0; N = 1 × 10^30; I = 10^15; biasedExp = 0/2 − 15 + 398 = 383
        test("sqrt(1) = 1_000_000_000_000_000 × 10^(383−398)") {
            val r = with(sqrt) { bid64(398, 1uL).sqrt() }
            r.significand shouldBe 1_000_000_000_000_000L
            r.biasedExponent shouldBe 383
        }
        // value = 4 × 10^0; N = 4 × 10^30; I = 2 × 10^15
        test("sqrt(4) = 2") {
            val r = with(sqrt) { bid64(398, 4uL).sqrt() }
            r.significand shouldBe 2_000_000_000_000_000L
            r.biasedExponent shouldBe 383
        }
    }

    context("perfect square: d=3, k=28") {
        // value = 100 × 10^0 = 10^2; N = 100 × 10^28 = 10^30; I = 10^15; biasedExp = 0−14+398 = 384
        // result = 10^15 × 10^(384−398) = 10^15 × 10^−14 = 10 ✓
        test("sqrt(100) = 10") {
            val r = with(sqrt) { bid64(398, 100uL).sqrt() }
            r.significand shouldBe 1_000_000_000_000_000L
            r.biasedExponent shouldBe 384
        }
    }

    context("perfect square: d=5, k=26") {
        // value = 10_000; N = 10^4 × 10^26 = 10^30; biasedExp = 0−13+398 = 385
        test("sqrt(10_000) = 100") {
            val r = with(sqrt) { bid64(398, 10_000uL).sqrt() }
            r.significand shouldBe 1_000_000_000_000_000L
            r.biasedExponent shouldBe 385
        }
    }

    context("perfect square: d=7, k=24") {
        // value = 10^6; N = 10^6 × 10^24 = 10^30; biasedExp = 0−12+398 = 386
        test("sqrt(10^6) = 10^3") {
            val r = with(sqrt) { bid64(398, 1_000_000uL).sqrt() }
            r.significand shouldBe 1_000_000_000_000_000L
            r.biasedExponent shouldBe 386
        }
        // value = 9 × 10^6; N = 9 × 10^30; I = 3 × 10^15
        test("sqrt(9_000_000) = 3000") {
            val r = with(sqrt) { bid64(398, 9_000_000uL).sqrt() }
            r.significand shouldBe 3_000_000_000_000_000L
            r.biasedExponent shouldBe 386
        }
    }

    context("perfect square: d=9, k=22") {
        // value = 10^8; N = 10^8 × 10^22 = 10^30; biasedExp = 0−11+398 = 387
        test("sqrt(10^8) = 10^4") {
            val r = with(sqrt) { bid64(398, 100_000_000uL).sqrt() }
            r.significand shouldBe 1_000_000_000_000_000L
            r.biasedExponent shouldBe 387
        }
    }

    context("perfect square: d=15, k=16") {
        // value = 10^14 (sig=10^14, biasedExp=398); N = 10^14 × 10^16 = 10^30; biasedExp = 0−8+398 = 390
        test("sqrt(10^14) = 10^7") {
            val r = with(sqrt) { bid64(398, 100_000_000_000_000uL).sqrt() }
            r.significand shouldBe 1_000_000_000_000_000L
            r.biasedExponent shouldBe 390
        }
    }

    // ── Odd exponent: d=17 produced by ×10 adjustment, k=14 ──────────────────

    context("perfect square via odd-exponent adjustment: d=17, k=14") {
        // biasedExp=399 → eOrig=1 (odd); s=10^15×10=10^16 (17 digits); k=14.
        // N = 10^16 × 10^14 = 10^30; I = 10^15; biasedExp = 0/2 − 7 + 398 = 391.
        // result = 10^15 × 10^(391−398) = 10^15 × 10^−7 = 10^8. sqrt(10^16) = 10^8 ✓.
        test("sqrt(10^16) = 10^8") {
            val r = with(sqrt) { bid64(399, 1_000_000_000_000_000uL).sqrt() }
            r.significand shouldBe 1_000_000_000_000_000L
            r.biasedExponent shouldBe 391
        }
    }

    // ── Correct rounding ──────────────────────────────────────────────────────
    // All inputs use d=1, k=30 (biasedExp=398). N = sig × 10^30.
    // Round up iff N − I² > I (no halfway case for integer sqrt inputs).

    context("rounding") {
        // sqrt(2) × 10^15 = 1414213562373095.0488… → rem < I → round down
        test("sqrt(2) rounds down to 1_414_213_562_373_095") {
            val r = with(sqrt) { bid64(398, 2uL).sqrt() }
            r.significand shouldBe 1_414_213_562_373_095L
            r.biasedExponent shouldBe 383
        }
        // sqrt(3) × 10^15 = 1732050807568877.293… → rem < I → round down
        test("sqrt(3) rounds down to 1_732_050_807_568_877") {
            val r = with(sqrt) { bid64(398, 3uL).sqrt() }
            r.significand shouldBe 1_732_050_807_568_877L
            r.biasedExponent shouldBe 383
        }
        // sqrt(5) × 10^15 = 2236067977499789.696… → rem > I → round up
        test("sqrt(5) rounds up to 2_236_067_977_499_790") {
            val r = with(sqrt) { bid64(398, 5uL).sqrt() }
            r.significand shouldBe 2_236_067_977_499_790L
            r.biasedExponent shouldBe 383
        }
        // sqrt(7) × 10^15 = 2645751311064590.590… → rem > I → round up
        test("sqrt(7) rounds up to 2_645_751_311_064_591") {
            val r = with(sqrt) { bid64(398, 7uL).sqrt() }
            r.significand shouldBe 2_645_751_311_064_591L
            r.biasedExponent shouldBe 383
        }
    }

    // ── Odd unbiased exponent ─────────────────────────────────────────────────

    context("odd unbiased exponent") {
        // biasedExp=399 → eOrig=1 (odd); s=10, eAdj=0; d=2, k=30.
        // N = 10 × 10^30 = 10^31; sqrt = sqrt(10) × 10^15 = 3162277660168379.33…
        // rem < I → round down; biasedExp = 0/2 − 15 + 398 = 383.
        test("sqrt(10) = 3_162_277_660_168_379 × 10^−15") {
            val r = with(sqrt) { bid64(399, 1uL).sqrt() }
            r.significand shouldBe 3_162_277_660_168_379L
            r.biasedExponent shouldBe 383
        }
        // biasedExp=401 → eOrig=3 (odd); s=10, eAdj=2; k=30.
        // Same N=10^31; biasedExp = 2/2 − 15 + 398 = 384.
        test("sqrt(10^3) = 3_162_277_660_168_379 × 10^−14") {
            val r = with(sqrt) { bid64(401, 1uL).sqrt() }
            r.significand shouldBe 3_162_277_660_168_379L
            r.biasedExponent shouldBe 384
        }
    }

    // ── Cohort independence ───────────────────────────────────────────────────

    context("cohort independence") {
        // 1 × 10^0 and 100 × 10^−2 represent the same value; both should give identical results.
        test("sqrt(1.0) is the same for two different cohort members") {
            val r1 = with(sqrt) { bid64(398, 1uL).sqrt() }
            val r2 = with(sqrt) { bid64(396, 100uL).sqrt() }
            r1 shouldBe r2
        }
    }

    // ── Extreme values ────────────────────────────────────────────────────────

    context("extreme values") {
        // minValue = 1×10^(0−398); eOrig=−398 (even); d=1, k=30; N=10^30; I=10^15; rem=0.
        // biasedExp = −398/2 − 15 + 398 = −199 − 15 + 398 = 184.
        test("sqrt(minValue) = 10^15 × 10^(184−398) = 10^−199") {
            val r = with(sqrt) { BidDouble.minValue.sqrt() }
            r.significand shouldBe 1_000_000_000_000_000L
            r.biasedExponent shouldBe 184
        }
        // maxValue = 9_999_999_999_999_999 × 10^369; eOrig=369 (odd); s=9.99…×10^16, d=17, k=14.
        // biasedExp = 368/2 − 7 + 398 = 184 − 7 + 398 = 575. Result is finite.
        test("sqrt(maxValue) is finite with biasedExponent 575") {
            val r = with(sqrt) { BidDouble.maxValue.sqrt() }
            r.isNaN() shouldBe false
            r.isInfinite() shouldBe false
            r.biasedExponent shouldBe 575
        }
        // minNormal = 10^15 × 10^(0−398) = 10^−383; eOrig=−398 (even); d=16, k=16.
        // N = 10^15 × 10^16 = 10^31; I = sqrt(10) × 10^15 (rounds down).
        // biasedExp = −398/2 − 8 + 398 = −199 − 8 + 398 = 191.
        test("sqrt(minNormal) has biasedExponent 191") {
            val r = with(sqrt) { BidDouble.minNormal.sqrt() }
            r.isNaN() shouldBe false
            r.biasedExponent shouldBe 191
        }
    }
})
