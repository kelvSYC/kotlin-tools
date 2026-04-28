package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.fp.FiniteDecimalFloatingPoint
import com.kelvsyc.kotlin.core.fp.toBidFloat
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun bid(biasedExp: Int, sig: UInt, negative: Boolean = false) =
    FiniteDecimalFloatingPoint(negative, biasedExp - 101, sig).toBidFloat()

/**
 * Tests for [FloatingPointRemainder.bidFloatIeee754].
 *
 * Remainder values are verified against the definition `x − n×y` where
 * `n = round-half-even(x/y)`, computed by hand for each case.
 */
class BidFloatRemainderTest : FunSpec({
    val rem = FloatingPointRemainder.bidFloatIeee754

    // ── special-value rules (IEEE 754-2008 §5.3.1) ───────────────────────────

    context("special values") {
        test("NaN rem finite = NaN") {
            with(rem) { BidFloat.NaN.rem(bid(101, 1u)) } shouldBe BidFloat.NaN
        }
        test("finite rem NaN = NaN") {
            with(rem) { bid(101, 1u).rem(BidFloat.NaN) } shouldBe BidFloat.NaN
        }
        test("∞ rem finite = NaN (invalid operation)") {
            with(rem) { BidFloat.positiveInfinity.rem(bid(101, 1u)) } shouldBe BidFloat.NaN
        }
        test("finite rem 0 = NaN (invalid operation)") {
            with(rem) { bid(101, 1u).rem(BidFloat.positiveZero) } shouldBe BidFloat.NaN
        }
        test("0 rem finite = +0 (sign of x preserved)") {
            val result = with(rem) { BidFloat.positiveZero.rem(bid(101, 1u)) }
            result shouldBe BidFloat.positiveZero
        }
        test("-0 rem finite = -0 (sign of x preserved)") {
            val result = with(rem) { BidFloat.negativeZero.rem(bid(101, 1u)) }
            result shouldBe BidFloat.negativeZero
        }
        test("finite rem ∞ = finite (x unchanged)") {
            val x = bid(101, 3u)
            with(rem) { x.rem(BidFloat.positiveInfinity) } shouldBe x
        }
    }

    // ── same exponent (rExp == yExp), rSig < ySig ─────────────────────────────
    // x and y share a quantum; |x| < |y|.

    context("same quantum, |x| < |y|") {
        // 3 rem 7: n = round(3/7) = 0, result = 3
        test("3 rem 7 = 3") {
            val result = with(rem) { bid(101, 3u).rem(bid(101, 7u)) }
            result.significand shouldBe 3
            result.biasedExponent shouldBe 101
            result.sign shouldBe false
        }
        // 6 rem 7: n = round(6/7) = 1, result = 6 - 7 = -1
        test("6 rem 7 = -1 (|x| > |y|/2 → n rounds up)") {
            val result = with(rem) { bid(101, 6u).rem(bid(101, 7u)) }
            result.significand shouldBe 1
            result.sign shouldBe true
        }
    }

    // ── same exponent, rSig >= ySig (loop-skipped branch, extra step) ─────────

    context("same quantum, |x| >= |y|") {
        // 7 rem 3: q = 2 (even), r = 1, twoR = 2 < 3 → result = 1
        test("7 rem 3 = 1") {
            val result = with(rem) { bid(101, 7u).rem(bid(101, 3u)) }
            result.significand shouldBe 1
            result.sign shouldBe false
        }
        // 5 rem 3: q = 1 (odd), r = 2, twoR = 4 > 3 → n = 2, result = 2 - 3 = -1
        test("5 rem 3 = -1 (round-up)") {
            val result = with(rem) { bid(101, 5u).rem(bid(101, 3u)) }
            result.significand shouldBe 1
            result.sign shouldBe true
        }
        // 10 rem 4: q = 2 (even), r = 2, twoR = 4 == ySig=4 → tie, q even → keep → result = 2
        test("10 rem 4 = 2 (tie, q even → keep)") {
            val result = with(rem) { bid(101, 10u).rem(bid(101, 4u)) }
            result.significand shouldBe 2
            result.sign shouldBe false
        }
        // 6 rem 4: q = 1 (odd), r = 2, twoR = 4 == ySig=4 → tie, q odd → increment → result = 2 - 4 = -2
        test("6 rem 4 = -2 (tie, q odd → round up)") {
            val result = with(rem) { bid(101, 6u).rem(bid(101, 4u)) }
            result.significand shouldBe 2
            result.sign shouldBe true
        }
    }

    // ── main loop (rExp > yExp): exponent reduction ───────────────────────────

    context("rExp > yExp (loop reduces exponent)") {
        // x = 1 × 10^2 = 100, y = 3 × 10^0 = 3
        // n = round(100/3) = round(33.333…) = 33, result = 100 - 99 = 1
        test("100 rem 3 = 1") {
            val result = with(rem) { bid(103, 1u).rem(bid(101, 3u)) }
            result.significand shouldBe 1
            result.sign shouldBe false
        }
        // x = 1 × 10^2 = 100, y = 4 × 10^0 = 4
        // n = round(25.0) = 25 (exact), result = 0 → sign of x (+)
        test("100 rem 4 = +0 (exact quotient → sign of x)") {
            val result = with(rem) { bid(103, 1u).rem(bid(101, 4u)) }
            result.isZero() shouldBe true
            result.sign shouldBe false
        }
        // x = 1 × 10^2 = 100, y = 6 × 10^0 = 6
        // n = round(16.666…) = 17, result = 100 - 102 = -2
        test("100 rem 6 = -2 (round-up across exponent boundary)") {
            val result = with(rem) { bid(103, 1u).rem(bid(101, 6u)) }
            result.significand shouldBe 2
            result.sign shouldBe true
        }
        // Half-tie in loop: x=7×10^1=70, y=4×10^0=4
        // n = round(70/4) = round(17.5) = 18 (ties-even), result = 70 - 72 = -2
        test("70 rem 4 = -2 (tie: n=17.5 → 18, odd → round up)") {
            val result = with(rem) { bid(102, 7u).rem(bid(101, 4u)) }
            result.significand shouldBe 2
            result.sign shouldBe true
        }
        // Half-tie: x=9×10^1=90, y=4×10^0=4
        // n = round(22.5) = 22 (ties-even, even), result = 90 - 88 = 2
        test("90 rem 4 = 2 (tie: n=22.5 → 22, even → keep)") {
            val result = with(rem) { bid(102, 9u).rem(bid(101, 4u)) }
            result.significand shouldBe 2
            result.sign shouldBe false
        }
    }

    // ── rExp < yExp: finer quantum, half-comparison at x's scale ─────────────

    context("rExp < yExp (x has finer quantum)") {
        // x = 5 × 10^(-1) = 0.5, y = 1 × 10^0 = 1
        // n = round(0.5) = 0 (ties-even, even), result = 0.5
        test("0.5 rem 1.0 = 0.5 (tie n=0 even → keep)") {
            val result = with(rem) { bid(100, 5u).rem(bid(101, 1u)) }
            result.significand shouldBe 5
            result.biasedExponent shouldBe 100
            result.sign shouldBe false
        }
        // x = 15 × 10^(-1) = 1.5, y = 1 × 10^0 = 1
        // n = round(1.5) = 2 (ties-even, odd q=1 → increment), result = 1.5 - 2 = -0.5
        test("1.5 rem 1.0 = -0.5 (tie n=1 odd → round up)") {
            val result = with(rem) { bid(100, 15u).rem(bid(101, 1u)) }
            result.significand shouldBe 5
            result.biasedExponent shouldBe 100
            result.sign shouldBe true
        }
        // x = 25 × 10^(-1) = 2.5, y = 1 × 10^0 = 1
        // n = round(2.5) = 2 (ties-even, even), result = 2.5 - 2 = 0.5
        test("2.5 rem 1.0 = 0.5 (tie n=2 even → keep)") {
            val result = with(rem) { bid(100, 25u).rem(bid(101, 1u)) }
            result.significand shouldBe 5
            result.biasedExponent shouldBe 100
            result.sign shouldBe false
        }
        // x = 7 × 10^(-1) = 0.7, y = 1 × 10^0 = 1
        // n = round(0.7) = 1, result = 0.7 - 1 = -0.3
        test("0.7 rem 1.0 = -0.3 (|x| > |y|/2 → n rounds up)") {
            val result = with(rem) { bid(100, 7u).rem(bid(101, 1u)) }
            result.significand shouldBe 3
            result.biasedExponent shouldBe 100
            result.sign shouldBe true
        }
        // x = 3 × 10^(-1) = 0.3, y = 1 × 10^0 = 1
        // n = round(0.3) = 0, result = 0.3
        test("0.3 rem 1.0 = 0.3 (|x| < |y|/2 → n=0)") {
            val result = with(rem) { bid(100, 3u).rem(bid(101, 1u)) }
            result.significand shouldBe 3
            result.biasedExponent shouldBe 100
            result.sign shouldBe false
        }
        // x = 7 × 10^0 = 7, y = 15 × 10^(-1) = 1.5  (diff=-1 so rExp > yExp … not this branch)
        // Instead: x = 75 × 10^(-1) = 7.5, y = 1 × 10^1 = 10
        // n = round(0.75) = 1, result = 7.5 - 10 = -2.5
        test("7.5 rem 10 = -2.5 (|x| > |y|/2)") {
            val result = with(rem) { bid(100, 75u).rem(bid(102, 1u)) }
            result.significand shouldBe 25
            result.biasedExponent shouldBe 100
            result.sign shouldBe true
        }
    }

    // ── sign handling ─────────────────────────────────────────────────────────

    context("sign handling") {
        // IEEE 754: sign of y does not affect |remainder|; result sign follows x - n×y.
        // -7 rem 3: n = round(-7/3) = round(-2.333) = -2, result = -7 - (-2)(3) = -7 + 6 = -1
        test("-7 rem 3 = -1 (negative x)") {
            val result = with(rem) { bid(101, 7u, negative = true).rem(bid(101, 3u)) }
            result.significand shouldBe 1
            result.sign shouldBe true
        }
        // 7 rem -3: n = round(7/(-3)) = round(-2.333) = -2, result = 7 - (-2)(-3) = 7 - 6 = 1
        test("7 rem -3 = 1 (negative y, result matches positive-y case magnitude)") {
            val result = with(rem) { bid(101, 7u).rem(bid(101, 3u, negative = true)) }
            result.significand shouldBe 1
            result.sign shouldBe false
        }
        // -7 rem -3: n = round((-7)/(-3)) = round(2.333) = 2, result = -7 - (2)(-3) = -7 + 6 = -1
        test("-7 rem -3 = -1 (both negative)") {
            val result = with(rem) { bid(101, 7u, negative = true).rem(bid(101, 3u, negative = true)) }
            result.significand shouldBe 1
            result.sign shouldBe true
        }
        // Exact zero: sign of x must be preserved
        test("exact zero result preserves sign of x: -4 rem 2 = -0") {
            val result = with(rem) { bid(101, 4u, negative = true).rem(bid(101, 2u)) }
            result.isZero() shouldBe true
            result.sign shouldBe true
        }
    }

    // ── y with trailing decimal zeros (normalization) ─────────────────────────

    context("y with trailing zeros (normalized internally)") {
        // y = 20 × 10^0 = 20 (trailing zero), normalized to ySig=2, yExp=102
        // x = 7 × 10^0 = 7. After normalization: diff = yExp(102) - rExp(101) = 1.
        // scaledY = 2 × 10 = 20. 7 < 20, so |x| < |y|. twoR = 14 < 20 → n=0, result = 7.
        test("7 rem 20 = 7 (y has trailing zero; |x| < |y|/2)") {
            val result = with(rem) { bid(101, 7u).rem(bid(101, 20u)) }
            result.significand shouldBe 7
            result.sign shouldBe false
        }
        // y = 20 (ySig=20, biasedExp=101). x = 11 (ySig=11, biasedExp=101).
        // diff=1, scaledY=20. twoR=22 > 20 → n rounds up. result = 11 - 20 = -9 at rExp=101.
        // But wait: we're in rExp < yExp branch since after normalization yExp=102 > rExp=101.
        // scaledY=2*10=20. rSig=11. twoR=22>20 → remSign=!xSign=true, remSig=20-11=9.
        test("11 rem 20 = -9 (y has trailing zero; |x| > |y|/2)") {
            val result = with(rem) { bid(101, 11u).rem(bid(101, 20u)) }
            result.significand shouldBe 9
            result.sign shouldBe true
        }
        // y = 20, x = 10: twoR = 20 = scaledY (tie). q = 0 (even) → keep → result = 10.
        test("10 rem 20 = 10 (tie, q=0 even → keep)") {
            val result = with(rem) { bid(101, 10u).rem(bid(101, 20u)) }
            result.significand shouldBe 10
            result.sign shouldBe false
        }
        // y = 20, x = 30: q=1(odd), r=10, twoR=20=scaledY, tie → round up → 10-20=-10.
        test("30 rem 20 = -10 (tie, q=1 odd → round up)") {
            val result = with(rem) { bid(101, 30u).rem(bid(101, 20u)) }
            result.significand shouldBe 10
            result.sign shouldBe true
        }
    }

    // ── canonicalization ──────────────────────────────────────────────────────

    context("non-canonical inputs are treated as zero") {
        // Non-canonical large-sig BidFloat (significand > 9,999,999) → canonical() = ±0.
        // 0 rem y = 0 (with sign of x = +).
        val nonCanon = BidFloat(0x6CBFFFFF)
        test("non-canonical x rem finite = +0") {
            val result = with(rem) { nonCanon.rem(bid(101, 1u)) }
            result.isZero() shouldBe true
            result.sign shouldBe false
        }
    }
})
