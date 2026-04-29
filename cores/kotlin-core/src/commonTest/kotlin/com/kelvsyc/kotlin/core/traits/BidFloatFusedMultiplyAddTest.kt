package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.fp.FiniteDecimalFloatingPoint
import com.kelvsyc.kotlin.core.fp.toBidFloat
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun bid(biasedExp: Int, sig: UInt, negative: Boolean = false) =
    FiniteDecimalFloatingPoint(negative, biasedExp - 101, sig).toBidFloat()

/**
 * Tests for [FusedMultiplyAdd.bidFloat].
 *
 * The key property tested is single-rounding: `fma(a, b, c)` must equal the nearest
 * representable value to the infinite-precision `a × b + c`. The "distinguishing test"
 * verifies this property by constructing a case where the exact rounding error of
 * `a × b` is recoverable via FMA but lost by the naive two-operation sequence.
 */
class BidFloatFusedMultiplyAddTest : FunSpec({
    val fma = FusedMultiplyAdd.bidFloat

    // ── special-value rules (IEEE 754-2008) ──────────────────────────────────

    context("special values") {
        test("NaN × finite + finite = NaN") {
            fma.fma(BidFloat.NaN, bid(101, 1u), bid(101, 2u)) shouldBe BidFloat.NaN
        }
        test("finite × NaN + finite = NaN") {
            fma.fma(bid(101, 1u), BidFloat.NaN, bid(101, 2u)) shouldBe BidFloat.NaN
        }
        test("finite × finite + NaN = NaN") {
            fma.fma(bid(101, 1u), bid(101, 2u), BidFloat.NaN) shouldBe BidFloat.NaN
        }
        test("0 × ∞ + finite = NaN (invalid operation)") {
            fma.fma(BidFloat.positiveZero, BidFloat.positiveInfinity, bid(101, 1u)) shouldBe BidFloat.NaN
        }
        test("∞ × 0 + finite = NaN (invalid operation)") {
            fma.fma(BidFloat.positiveInfinity, BidFloat.positiveZero, bid(101, 1u)) shouldBe BidFloat.NaN
        }
        test("+∞ × +∞ + finite = +∞") {
            fma.fma(BidFloat.positiveInfinity, BidFloat.positiveInfinity, bid(101, 1u)) shouldBe BidFloat.positiveInfinity
        }
        test("+∞ × +∞ + (−∞) = NaN (∞ − ∞)") {
            fma.fma(BidFloat.positiveInfinity, BidFloat.positiveInfinity, BidFloat.negativeInfinity) shouldBe BidFloat.NaN
        }
        test("+∞ × −1 + +∞ = NaN (−∞ + ∞)") {
            fma.fma(BidFloat.positiveInfinity, bid(101, 1u, negative = true), BidFloat.positiveInfinity) shouldBe BidFloat.NaN
        }
        test("finite × finite + +∞ = +∞ (finite product, infinite addend)") {
            fma.fma(bid(101, 3u), bid(101, 4u), BidFloat.positiveInfinity) shouldBe BidFloat.positiveInfinity
        }
        test("finite × finite + (−∞) = −∞") {
            fma.fma(bid(101, 3u), bid(101, 4u), BidFloat.negativeInfinity) shouldBe BidFloat.negativeInfinity
        }
        test("+0 × finite + +0 = +0") {
            val result = fma.fma(BidFloat.positiveZero, bid(101, 5u), BidFloat.positiveZero)
            result.isZero() shouldBe true
            result.sign shouldBe false
        }
        test("−0 × finite + −0 = −0 (both contributing zeros are negative)") {
            val result = fma.fma(BidFloat.negativeZero, bid(101, 5u), BidFloat.negativeZero)
            result.isZero() shouldBe true
            result.sign shouldBe true
        }
        test("finite × +0 + finite = finite (zero product, use addend)") {
            val c = bid(101, 7u)
            fma.fma(bid(101, 3u), BidFloat.positiveZero, c) shouldBe c
        }
    }

    // ── exact trivial cases (delta = 0, no rounding needed) ──────────────────

    context("exact same-exponent cases") {
        // 2 × 3 + 4 = 10; all at biasedExp=101
        test("2 × 3 + 4 = 10") {
            val result = fma.fma(bid(101, 2u), bid(101, 3u), bid(101, 4u))
            result.significand shouldBe 10
            result.biasedExponent shouldBe 101
            result.sign shouldBe false
        }
        // 2 × 3 + (−4) = 2
        test("2 × 3 + (−4) = 2") {
            val result = fma.fma(bid(101, 2u), bid(101, 3u), bid(101, 4u, negative = true))
            result.significand shouldBe 2
            result.biasedExponent shouldBe 101
            result.sign shouldBe false
        }
        // (−2) × 3 + 4 = −2
        test("(−2) × 3 + 4 = −2") {
            val result = fma.fma(bid(101, 2u, negative = true), bid(101, 3u), bid(101, 4u))
            result.significand shouldBe 2
            result.sign shouldBe true
        }
        // 1 × 1 + (−1) = 0
        test("1 × 1 + (−1) = +0 (complete cancellation)") {
            val result = fma.fma(bid(101, 1u), bid(101, 1u), bid(101, 1u, negative = true))
            result.isZero() shouldBe true
            result.sign shouldBe false
        }
    }

    // ── distinguishing test: single-rounding vs. double-rounding ─────────────
    //
    // a = b = 1_000_001 (1_000_001 × 10^0).
    // Exact product  = 1_000_001² = 1_000_002_000_001 (13 significant digits).
    // Rounded product (7 digits, half-even) = 1_000_002 × 10^6  (biasedExp 107).
    // Rounding error = 1 × 10^0 = bid(101, 1).
    //
    // fma(a, b, −rounded_product)
    //   exact = 1_000_002_000_001 − 1_000_002_000_000 = 1  → bid(101, 1)
    //
    // naive (a * b) + (−rounded_product)
    //   (a * b) rounds to 1_000_002 × 10^6; then + (−1_000_002 × 10^6) = 0  → wrong.

    context("distinguishing test (single- vs double-rounding)") {
        val a = bid(101, 1_000_001u)
        val roundedProduct = bid(107, 1_000_002u)

        test("fma(1_000_001, 1_000_001, −roundedProduct) = 1 (recovers rounding error)") {
            val result = fma.fma(a, a, bid(107, 1_000_002u, negative = true))
            result.significand shouldBe 1
            result.biasedExponent shouldBe 101
            result.sign shouldBe false
        }

        // Verify that the rounded product itself is what we expect, so the test premise holds.
        test("round(1_000_001²) = 1_000_002 × 10^6  (premise check)") {
            val result = fma.fma(a, a, BidFloat.positiveZero)
            result.significand shouldBe 1_000_002
            result.biasedExponent shouldBe 107
            result.sign shouldBe false
        }
    }

    // ── wide-product rounding (9_999_999²) ───────────────────────────────────
    //
    // 9_999_999² = 99_999_980_000_001 (14 digits).
    // Excess = 7; div = 10^7.  99_999_980_000_001 / 10^7 = 9_999_998, rem = 1 < 5_000_000.
    // Rounded significand = 9_999_998, biasedExp = 101 + 7 = 108.

    context("wide product rounding") {
        test("9_999_999 × 9_999_999 + 0 rounds to 9_999_998 × 10^7") {
            val result = fma.fma(bid(101, 9_999_999u), bid(101, 9_999_999u), BidFloat.positiveZero)
            result.significand shouldBe 9_999_998
            result.biasedExponent shouldBe 108
            result.sign shouldBe false
        }

        // Round-half-even on the wide product: adjust addend so the remainder is exactly half.
        // We need rem == div/2.  div = 10^7, half = 5_000_000.
        // 9_999_999² = 99_999_980_000_001; we want sP+c to end in exactly 5_000_000 units at exp 101.
        // sP at exp 101 = 99_999_980_000_001.  Target tail = 5_000_000 → c = 5_000_000 - 1 = 4_999_999.
        // Rounded: trunc = 9_999_998 (even) → keep → 9_999_998.
        test("round-half-even: tie goes to even (9_999_998)") {
            val result = fma.fma(
                bid(101, 9_999_999u),
                bid(101, 9_999_999u),
                bid(101, 4_999_999u),   // shifts remainder to exactly half
            )
            result.significand shouldBe 9_999_998
            result.biasedExponent shouldBe 108
        }
    }

    // ── delta alignment range coverage ────────────────────────────────────────

    context("delta alignment (eC − eP)") {
        // delta = 0: same quantum, exact arithmetic
        // 3 × 2 = 6, c = 4 × 10^0 → 10 × 10^0
        test("delta = 0: 3 × 2 + 4 = 10") {
            val result = fma.fma(bid(101, 3u), bid(101, 2u), bid(101, 4u))
            result.significand shouldBe 10
            result.biasedExponent shouldBe 101
        }

        // delta = −4: product quantum finer than addend, [-4,11] safe path
        // a=2, b=3 → product 6 at biasedExp 101; c=4 at biasedExp 97.
        // sumSig = 6 × 10^4 + 4 = 60_004; sumExp = 97.
        test("delta = −4: 2 × 3 + 0.0004 = 6.0004") {
            val result = fma.fma(bid(101, 2u), bid(101, 3u), bid(97, 4u))
            result.significand shouldBe 60_004
            result.biasedExponent shouldBe 97
            result.sign shouldBe false
        }

        // delta = 11: addend quantum coarser, still in [-4,11] safe path
        // product = 6 at biasedExp 101; c = 4 at biasedExp 112.
        // sumSig = 6 + 4×10^11 = 400_000_000_006, sumExp = 101.
        // digits = 12, excess = 5, div = 10^5.
        // s = 400_000_000_006 / 100_000 = 4_000_000 rem 6 < 50_000 → 4_000_000.
        // e = 101 + 5 = 106.
        test("delta = 11: addend dominates, rounds to 4_000_000 × 10^5") {
            val result = fma.fma(bid(101, 2u), bid(101, 3u), bid(112, 4u))
            result.significand shouldBe 4_000_000
            result.biasedExponent shouldBe 106
            result.sign shouldBe false
        }

        // delta = −5: product quantum coarser, overflow-checked path, no overflow
        // product = 6 at biasedExp 101; c = 4 at biasedExp 96.
        // scale = 10^5; sPscaled = 600_000; sumSig = 600_004; sumExp = 96.
        test("delta = −5: 2 × 3 + 0.000004 = 6.000004") {
            val result = fma.fma(bid(101, 2u), bid(101, 3u), bid(96, 4u))
            result.significand shouldBe 600_004
            result.biasedExponent shouldBe 96
            result.sign shouldBe false
        }

        // delta = 12: addend quantum very coarse, overflow-checked path, no overflow
        // product = 6 at biasedExp 101; c = 4 at biasedExp 113.
        // scale = 10^12; sCscaled = 4_000_000_000_000; sumSig = 4_000_000_000_006.
        // digits = 13, excess = 6; s = 4_000_000 rem 6 < 500_000 → 4_000_000; e = 107.
        test("delta = 12: exact arithmetic, rounds to 4_000_000 × 10^6") {
            val result = fma.fma(bid(101, 2u), bid(101, 3u), bid(113, 4u))
            result.significand shouldBe 4_000_000
            result.biasedExponent shouldBe 107
            result.sign shouldBe false
        }

        // delta > 18: addend so coarse that product cannot affect result; addend returned directly.
        // product = 6 at biasedExp 101; c = 4 at biasedExp 120; delta = 19.
        test("delta = 19: addend dominates directly (no scaling)") {
            val result = fma.fma(bid(101, 2u), bid(101, 3u), bid(120, 4u))
            result.significand shouldBe 4
            result.biasedExponent shouldBe 120
            result.sign shouldBe false
        }

        // delta < −18: product so much finer that addend cannot affect result.
        // a=9_999_999, b=9_999_999 → sP = 99_999_980_000_001 at biasedExp 101; c at biasedExp 80; delta = −21.
        // fmaRound: digits=14, excess=7, s=9_999_998, e=108.
        test("delta = −21: product dominates directly") {
            val result = fma.fma(bid(101, 9_999_999u), bid(101, 9_999_999u), bid(80, 1u))
            result.significand shouldBe 9_999_998
            result.biasedExponent shouldBe 108
            result.sign shouldBe false
        }
    }

    // ── exponent overflow and underflow ───────────────────────────────────────

    context("overflow and underflow") {
        // Largest representable value × 10 overflows to +∞.
        // a = 9_999_999 at biasedExp 191 (max); b = 9_999_999 at biasedExp 101.
        // eP = 191 + 101 - 101 = 191; sP = 99_999_980_000_001; digits = 14.
        // After rounding: e = 191 + 7 = 198 > 191 → +∞.
        test("overflow: product exponent > 191 → +∞") {
            val result = fma.fma(bid(191, 9_999_999u), bid(101, 9_999_999u), BidFloat.positiveZero)
            result shouldBe BidFloat.positiveInfinity
        }

        // Underflow: product at biasedExp = -7 (relative to bias) rounds to zero.
        // a = 1 at biasedExp 0; b = 1 at biasedExp 0; c = 0.
        // eP = 0 + 0 - 101 = -101; shift = 101 >= 8 → +0.
        test("underflow: product biasedExp deeply negative → +0") {
            val result = fma.fma(bid(0, 1u), bid(0, 1u), BidFloat.positiveZero)
            result.isZero() shouldBe true
            result.sign shouldBe false
        }
    }

    // ── sign and cancellation ─────────────────────────────────────────────────

    context("sign and cancellation") {
        test("(−a) × b + c: negative product combined with positive addend") {
            // (−2) × 3 + 10 = 4
            val result = fma.fma(bid(101, 2u, negative = true), bid(101, 3u), bid(101, 10u))
            result.significand shouldBe 4
            result.sign shouldBe false
        }
        test("a × (−b) + c: same as above by symmetry") {
            val result = fma.fma(bid(101, 2u), bid(101, 3u, negative = true), bid(101, 10u))
            result.significand shouldBe 4
            result.sign shouldBe false
        }
        test("negative sum produces negative result") {
            // 2 × 3 + (−10) = −4
            val result = fma.fma(bid(101, 2u), bid(101, 3u), bid(101, 10u, negative = true))
            result.significand shouldBe 4
            result.sign shouldBe true
        }
    }

    // ── non-canonical inputs ──────────────────────────────────────────────────

    context("non-canonical inputs are treated as zero") {
        // BidFloat with significand > 9_999_999 in large-sig encoding → canonical = ±0.
        val nonCanon = BidFloat(0x6CBFFFFF)
        test("non-canonical a treated as zero: 0 × finite + finite = finite") {
            val c = bid(101, 7u)
            fma.fma(nonCanon, bid(101, 3u), c) shouldBe c
        }
        test("non-canonical c treated as zero: finite × finite + 0 = product") {
            val result = fma.fma(bid(101, 2u), bid(101, 3u), nonCanon)
            result.significand shouldBe 6
            result.biasedExponent shouldBe 101
            result.sign shouldBe false
        }
    }
})
