package com.kelvsyc.kotlin.core.traits.dfp
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSquareRoot

import com.kelvsyc.kotlin.core.DpdFloat
import com.kelvsyc.kotlin.core.fp.FiniteDecimalFloatingPoint
import com.kelvsyc.kotlin.core.fp.toDpdFloat
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun dpd(biasedExp: Int, sig: UInt, negative: Boolean = false) =
    FiniteDecimalFloatingPoint(negative, biasedExp - 101, sig).toDpdFloat()

/**
 * Smoke tests for [FloatingPointSquareRoot.dpdFloat].
 *
 * Full algorithmic coverage — rounding, exponent adjustment, extreme values — lives in
 * [BidFloatSquareRootTest]. These tests verify that [DelegatingDpdSquareRoot] correctly threads
 * the BID↔DPD converter and produces numerically correct results on a representative sample.
 */
class DpdFloatSquareRootTest : FunSpec({
    val sqrt = FloatingPointSquareRoot.dpdFloat

    // ── special values ────────────────────────────────────────────────────────

    context("special values") {
        test("NaN → NaN") {
            with(sqrt) { DpdFloat.NaN.sqrt() }.isNaN() shouldBe true
        }
        test("+∞ → +∞") {
            with(sqrt) { DpdFloat.positiveInfinity.sqrt() } shouldBe DpdFloat.positiveInfinity
        }
        test("-∞ → NaN") {
            with(sqrt) { DpdFloat.negativeInfinity.sqrt() }.isNaN() shouldBe true
        }
        test("+0 → +0") {
            with(sqrt) { DpdFloat.positiveZero.sqrt() } shouldBe DpdFloat.positiveZero
        }
        test("-0 → -0") {
            with(sqrt) { DpdFloat.negativeZero.sqrt() } shouldBe DpdFloat.negativeZero
        }
        test("negative finite → NaN") {
            with(sqrt) { dpd(101, 1u, negative = true).sqrt() }.isNaN() shouldBe true
        }
    }

    // ── representative finite values ──────────────────────────────────────────

    context("perfect square") {
        test("sqrt(4) = 2") {
            val r = with(sqrt) { dpd(101, 4u).sqrt() }
            r.significand shouldBe 2_000_000
            r.biasedExponent shouldBe 95
        }
    }

    context("rounding") {
        test("sqrt(2) rounds up to 1.414214") {
            val r = with(sqrt) { dpd(101, 2u).sqrt() }
            r.significand shouldBe 1_414_214
            r.biasedExponent shouldBe 95
        }
        test("sqrt(7) rounds down to 2.645751") {
            val r = with(sqrt) { dpd(101, 7u).sqrt() }
            r.significand shouldBe 2_645_751
            r.biasedExponent shouldBe 95
        }
    }

    context("odd unbiased exponent") {
        // Exercises the makeEven step inside BidFloatSquareRoot via the DPD→BID→DPD path.
        test("sqrt(10^1) ≈ sqrt(10)") {
            val r = with(sqrt) { dpd(102, 1u).sqrt() }
            r.significand shouldBe 3_162_278
            r.biasedExponent shouldBe 95
        }
    }
})
