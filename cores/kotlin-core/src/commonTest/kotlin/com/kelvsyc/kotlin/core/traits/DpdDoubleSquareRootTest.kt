package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.DpdDouble
import com.kelvsyc.kotlin.core.fp.FiniteDecimalFloatingPoint
import com.kelvsyc.kotlin.core.fp.toDpdDouble
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun dpd64(biasedExp: Int, sig: ULong, negative: Boolean = false) =
    FiniteDecimalFloatingPoint(negative, biasedExp - 398, sig).toDpdDouble()

/**
 * Smoke tests for [FloatingPointSquareRoot.dpdDouble].
 *
 * Full algorithmic coverage lives in [BidDoubleSquareRootTest]. These tests verify that the
 * DPD instance delegates correctly through the BID↔DPD converter on a representative sample.
 */
class DpdDoubleSquareRootTest : FunSpec({
    val sqrt = FloatingPointSquareRoot.dpdDouble

    context("special values") {
        test("NaN → NaN") {
            with(sqrt) { DpdDouble.NaN.sqrt() }.isNaN() shouldBe true
        }
        test("+∞ → +∞") {
            with(sqrt) { DpdDouble.positiveInfinity.sqrt() } shouldBe DpdDouble.positiveInfinity
        }
        test("-∞ → NaN") {
            with(sqrt) { DpdDouble.negativeInfinity.sqrt() }.isNaN() shouldBe true
        }
        test("+0 → +0") {
            with(sqrt) { DpdDouble.positiveZero.sqrt() } shouldBe DpdDouble.positiveZero
        }
        test("-0 → -0") {
            with(sqrt) { DpdDouble.negativeZero.sqrt() } shouldBe DpdDouble.negativeZero
        }
        test("negative finite → NaN") {
            with(sqrt) { dpd64(398, 1uL, negative = true).sqrt() }.isNaN() shouldBe true
        }
    }

    context("perfect squares through BID↔DPD conversion") {
        // sqrt(1): expects sig=10^15, biasedExp=383
        test("sqrt(1) = 1_000_000_000_000_000 × 10^−15") {
            val r = with(sqrt) { dpd64(398, 1uL).sqrt() }
            r.isNaN() shouldBe false
            r.isInfinite() shouldBe false
            r.significand shouldBe 1_000_000_000_000_000L
            r.biasedExponent shouldBe 383
        }
        // sqrt(4): expects sig=2×10^15, biasedExp=383
        test("sqrt(4) = 2_000_000_000_000_000 × 10^−15") {
            val r = with(sqrt) { dpd64(398, 4uL).sqrt() }
            r.significand shouldBe 2_000_000_000_000_000L
            r.biasedExponent shouldBe 383
        }
    }

    context("rounding preserved through conversion") {
        // sqrt(2) → round down to 1_414_213_562_373_095
        test("sqrt(2) rounds down correctly") {
            val r = with(sqrt) { dpd64(398, 2uL).sqrt() }
            r.isNaN() shouldBe false
            r.significand shouldBe 1_414_213_562_373_095L
            r.biasedExponent shouldBe 383
        }
        // sqrt(5) → round up to 2_236_067_977_499_790
        test("sqrt(5) rounds up correctly") {
            val r = with(sqrt) { dpd64(398, 5uL).sqrt() }
            r.isNaN() shouldBe false
            r.significand shouldBe 2_236_067_977_499_790L
            r.biasedExponent shouldBe 383
        }
    }

    context("odd exponent") {
        // sqrt(10) via biasedExp=399 (odd eOrig=1)
        test("sqrt(10) = 3_162_277_660_168_379 × 10^−15") {
            val r = with(sqrt) { dpd64(399, 1uL).sqrt() }
            r.significand shouldBe 3_162_277_660_168_379L
            r.biasedExponent shouldBe 383
        }
    }
})
