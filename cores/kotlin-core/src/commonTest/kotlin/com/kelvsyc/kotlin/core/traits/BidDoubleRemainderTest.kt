package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.fp.FiniteDecimalFloatingPoint
import com.kelvsyc.kotlin.core.fp.toBidDouble
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun bid64(biasedExp: Int, sig: ULong, negative: Boolean = false) =
    FiniteDecimalFloatingPoint(negative, biasedExp - 398, sig).toBidDouble()

/**
 * Tests for [FloatingPointRemainder.bidDoubleIeee754] and [FloatingPointRemainder.bidDoubleTruncating].
 *
 * Mirrors the structure of [BidFloatRemainderTest] with decimal64 values.
 * Key differences: step limit is 2 (not 6), early-return threshold is diff ≥ 17 (not 8),
 * and overflow guard fires when ySig × 10^diff would overflow Long.
 */
class BidDoubleIeee754RemainderTest : FunSpec({
    val rem = FloatingPointRemainder.bidDoubleIeee754

    // ── Special values ────────────────────────────────────────────────────────

    context("special values") {
        test("NaN rem finite = NaN") {
            with(rem) { BidDouble.NaN.rem(bid64(398, 1uL)) } shouldBe BidDouble.NaN
        }
        test("finite rem NaN = NaN") {
            with(rem) { bid64(398, 1uL).rem(BidDouble.NaN) } shouldBe BidDouble.NaN
        }
        test("∞ rem finite = NaN") {
            with(rem) { BidDouble.positiveInfinity.rem(bid64(398, 1uL)) } shouldBe BidDouble.NaN
        }
        test("finite rem 0 = NaN") {
            with(rem) { bid64(398, 1uL).rem(BidDouble.positiveZero) } shouldBe BidDouble.NaN
        }
        test("+0 rem finite = +0 (sign of x preserved)") {
            with(rem) { BidDouble.positiveZero.rem(bid64(398, 1uL)) } shouldBe BidDouble.positiveZero
        }
        test("-0 rem finite = -0 (sign of x preserved)") {
            with(rem) { BidDouble.negativeZero.rem(bid64(398, 1uL)) } shouldBe BidDouble.negativeZero
        }
        test("finite rem ∞ = x unchanged") {
            val x = bid64(398, 3uL)
            with(rem) { x.rem(BidDouble.positiveInfinity) } shouldBe x
        }
    }

    // ── Same quantum, |x| < |y| ───────────────────────────────────────────────

    context("same quantum, |x| < |y|") {
        // 3 rem 7: n = round(3/7) = 0 → result = 3
        test("3 rem 7 = 3") {
            val result = with(rem) { bid64(398, 3uL).rem(bid64(398, 7uL)) }
            result.significand shouldBe 3L
            result.biasedExponent shouldBe 398
            result.sign shouldBe false
        }
        // 6 rem 7: n = round(6/7) = 1 → result = 6 - 7 = -1
        test("6 rem 7 = -1 (|x| > |y|/2 → n rounds up)") {
            val result = with(rem) { bid64(398, 6uL).rem(bid64(398, 7uL)) }
            result.significand shouldBe 1L
            result.sign shouldBe true
        }
    }

    // ── Same quantum, |x| ≥ |y| ──────────────────────────────────────────────

    context("same quantum, |x| >= |y|") {
        // 7 rem 3: q=2 (even), r=1, twoR=2 < 3 → result = 1
        test("7 rem 3 = 1") {
            val result = with(rem) { bid64(398, 7uL).rem(bid64(398, 3uL)) }
            result.significand shouldBe 1L
            result.sign shouldBe false
        }
        // 5 rem 3: q=1 (odd), r=2, twoR=4 > 3 → n=2, result = -1
        test("5 rem 3 = -1 (round-up)") {
            val result = with(rem) { bid64(398, 5uL).rem(bid64(398, 3uL)) }
            result.significand shouldBe 1L
            result.sign shouldBe true
        }
        // 10 rem 4: q=2 (even), r=2, twoR=4 == ySig → tie, q even → keep → result = 2
        test("10 rem 4 = 2 (tie, q even → keep)") {
            val result = with(rem) { bid64(398, 10uL).rem(bid64(398, 4uL)) }
            result.significand shouldBe 2L
            result.sign shouldBe false
        }
        // 6 rem 4: q=1 (odd), r=2, twoR=4 == ySig → tie, q odd → round up → result = -2
        test("6 rem 4 = -2 (tie, q odd → round up)") {
            val result = with(rem) { bid64(398, 6uL).rem(bid64(398, 4uL)) }
            result.significand shouldBe 2L
            result.sign shouldBe true
        }
    }

    // ── Main loop (rExp > yExp), step limit = 2 ───────────────────────────────

    context("rExp > yExp (loop reduces exponent in steps of ≤2)") {
        // x = 1×10^2 = 100, y = 3×10^0 = 3; step k=2 twice
        // 100 rem 3: n = round(100/3) = round(33.333) = 33; result = 100 - 99 = 1
        test("100 rem 3 = 1") {
            val result = with(rem) { bid64(400, 1uL).rem(bid64(398, 3uL)) }
            result.significand shouldBe 1L
            result.sign shouldBe false
        }
        // x = 1×10^4 = 10000, y = 3: loop steps k=2 twice, then equality step
        // 10000 rem 3: 10000 = 3333×3 + 1 → n=3333, result = 1
        test("10000 rem 3 = 1 (loop applies multiple k=2 steps)") {
            val result = with(rem) { bid64(402, 1uL).rem(bid64(398, 3uL)) }
            result.significand shouldBe 1L
            result.sign shouldBe false
        }
        // Half-tie: x = 7×10^1 = 70, y = 4
        // n = round(70/4) = round(17.5) = 18 (ties-even: q=17 odd → round up)
        // result = 70 - 72 = -2
        test("70 rem 4 = -2 (tie: n=17.5, q=17 odd → round up)") {
            val result = with(rem) { bid64(399, 7uL).rem(bid64(398, 4uL)) }
            result.significand shouldBe 2L
            result.sign shouldBe true
        }
        // Half-tie: x = 9×10^1 = 90, y = 4
        // n = round(22.5) = 22 (ties-even: q=22 even → keep)
        // result = 90 - 88 = 2
        test("90 rem 4 = 2 (tie: n=22.5, q=22 even → keep)") {
            val result = with(rem) { bid64(399, 9uL).rem(bid64(398, 4uL)) }
            result.significand shouldBe 2L
            result.sign shouldBe false
        }
    }

    // ── rExp < yExp: finer quantum ────────────────────────────────────────────

    context("rExp < yExp (x has finer quantum)") {
        // x = 5×10^−1, y = 1×10^0: diff=1, scaledY=10, twoR=10 == scaledY → tie, q=0 even → keep
        test("0.5 rem 1.0 = 0.5 (tie n=0 even → keep)") {
            val result = with(rem) { bid64(397, 5uL).rem(bid64(398, 1uL)) }
            result.significand shouldBe 5L
            result.biasedExponent shouldBe 397
            result.sign shouldBe false
        }
        // x = 15×10^−1 = 1.5, y = 1: diff=1, scaledY=10, rSig=15, twoR=30 > 20 → round up
        // result = -0.5 (sign flips)
        test("1.5 rem 1.0 = -0.5 (tie n=1 odd → round up)") {
            val result = with(rem) { bid64(397, 15uL).rem(bid64(398, 1uL)) }
            result.significand shouldBe 5L
            result.biasedExponent shouldBe 397
            result.sign shouldBe true
        }
        // x = 3×10^−1, y = 1: twoR=6 < scaledY=10 → n=0, result = 0.3
        test("0.3 rem 1.0 = 0.3 (|x| < |y|/2 → n=0)") {
            val result = with(rem) { bid64(397, 3uL).rem(bid64(398, 1uL)) }
            result.significand shouldBe 3L
            result.sign shouldBe false
        }
        // diff = 17: early return (|x| < |y|/2 always)
        // x = 1×10^−1 (biasedExp=397), y = 1×10^16 (biasedExp=414): diff=17 → return cx
        test("diff ≥ 17 returns x unchanged") {
            val x = bid64(397, 1uL)
            val result = with(rem) { x.rem(bid64(414, 1uL)) }
            result shouldBe x
        }
        // Overflow guard: ySig=10^15 (biasedExp=413), diff=4 after normalizing y → ySig×10^4 overflows Long
        // x = 1×10^0 (biasedExp=398), y = 10^15×10^4 = 10^19 (biasedExp=417 before strip, but after strip ySig=10^15, yExp=417+??)
        // Simplest: ySig itself triggers guard. x=1 (biasedExp=398), y=10^15 (biasedExp=413, after strip ySig=1, yExp=413+15=428 → diff=30 ≥ 17 → early return)
        // Better: ySig=10^15, no trailing zeros. y has biasedExp=402, diff=4. ySig×10^4 = 10^19 > Long.MAX → guard → return cx.
        test("overflow guard fires when ySig×10^diff exceeds Long.MAX_VALUE") {
            val x = bid64(398, 1uL)
            val y = bid64(402, 1_000_000_000_000_000uL)  // 10^15 × 10^4, ySig=10^15, yExp=402
            // diff = 402 - 398 = 4; ySig×10^4 = 10^19 > Long.MAX → return cx
            val result = with(rem) { x.rem(y) }
            result shouldBe x
        }
    }

    // ── Sign handling ─────────────────────────────────────────────────────────

    context("sign handling") {
        test("-7 rem 3 = -1 (negative x)") {
            val result = with(rem) { bid64(398, 7uL, negative = true).rem(bid64(398, 3uL)) }
            result.significand shouldBe 1L
            result.sign shouldBe true
        }
        test("7 rem -3 = 1 (negative y, result sign follows x-n×y)") {
            val result = with(rem) { bid64(398, 7uL).rem(bid64(398, 3uL, negative = true)) }
            result.significand shouldBe 1L
            result.sign shouldBe false
        }
        test("exact zero preserves sign of x: -4 rem 2 = -0") {
            val result = with(rem) { bid64(398, 4uL, negative = true).rem(bid64(398, 2uL)) }
            result.isZero() shouldBe true
            result.sign shouldBe true
        }
    }

    // ── Trailing zeros in y ───────────────────────────────────────────────────

    context("y with trailing zeros (normalized internally)") {
        // y = 20×10^0; strips to ySig=2, yExp=399. x=7 at yExp=398 → diff=1.
        // scaledY=20; twoR=14 < 20 → n=0, result=7.
        test("7 rem 20 = 7 (y has trailing zero, |x| < |y|/2)") {
            val result = with(rem) { bid64(398, 7uL).rem(bid64(398, 20uL)) }
            result.significand shouldBe 7L
            result.sign shouldBe false
        }
        // y=20, x=11: twoR=22 > 20 → round up → -9
        test("11 rem 20 = -9 (y has trailing zero, |x| > |y|/2)") {
            val result = with(rem) { bid64(398, 11uL).rem(bid64(398, 20uL)) }
            result.significand shouldBe 9L
            result.sign shouldBe true
        }
    }

    // ── Canonicalization ──────────────────────────────────────────────────────

    context("non-canonical inputs treated as zero") {
        // Large-sig BID encoding with significand > 9_999_999_999_999_999 → canonical() = ±0.
        // Non-canonical: combination top 2 bits = 11, bit 52 set → significand has bit 53 set but > max.
        // combination=0x1801 → large-sig path; sig = (1L shl 53) | 0x3_FFFF_FFFF_FFFF = 13_510_798_882_111_487 > max
        val nonCanon = BidDouble(0x6007_FFFF_FFFF_FFFF)
        test("non-canonical x rem finite = +0") {
            val result = with(rem) { nonCanon.rem(bid64(398, 1uL)) }
            result.isZero() shouldBe true
            result.sign shouldBe false
        }
    }
})

class BidDoubleTruncatingRemainderTest : FunSpec({
    val rem = FloatingPointRemainder.bidDoubleTruncating

    context("special values") {
        test("NaN rem finite = NaN") {
            with(rem) { BidDouble.NaN.rem(bid64(398, 1uL)) } shouldBe BidDouble.NaN
        }
        test("∞ rem finite = NaN") {
            with(rem) { BidDouble.positiveInfinity.rem(bid64(398, 1uL)) } shouldBe BidDouble.NaN
        }
        test("finite rem 0 = NaN") {
            with(rem) { bid64(398, 1uL).rem(BidDouble.positiveZero) } shouldBe BidDouble.NaN
        }
        test("-0 rem finite = -0 (sign of x)") {
            with(rem) { BidDouble.negativeZero.rem(bid64(398, 1uL)) } shouldBe BidDouble.negativeZero
        }
        test("finite rem ∞ = x") {
            val x = bid64(398, 3uL)
            with(rem) { x.rem(BidDouble.positiveInfinity) } shouldBe x
        }
    }

    context("truncating keeps floor remainder regardless of half") {
        // 5 rem 3: trunc(5/3)=1, result=2 (ieee754 gives -1)
        test("5 rem 3 = 2 (not -1)") {
            val result = with(rem) { bid64(398, 5uL).rem(bid64(398, 3uL)) }
            result.significand shouldBe 2L
            result.sign shouldBe false
        }
        // 6 rem 4: trunc(6/4)=1, result=2 (ieee754 gives -2)
        test("6 rem 4 = 2 (not -2)") {
            val result = with(rem) { bid64(398, 6uL).rem(bid64(398, 4uL)) }
            result.significand shouldBe 2L
            result.sign shouldBe false
        }
        // 1.5 rem 1.0: trunc(1.5)=1, result=0.5 (ieee754 gives -0.5)
        test("1.5 rem 1.0 = 0.5 (not -0.5)") {
            val result = with(rem) { bid64(397, 15uL).rem(bid64(398, 1uL)) }
            result.significand shouldBe 5L
            result.biasedExponent shouldBe 397
            result.sign shouldBe false
        }
    }

    context("result always has sign of x") {
        // -5 rem 3: trunc(-5/3)=−1, result = -5 - (-1)(3) = -2
        test("-5 rem 3 = -2") {
            val result = with(rem) { bid64(398, 5uL, negative = true).rem(bid64(398, 3uL)) }
            result.significand shouldBe 2L
            result.sign shouldBe true
        }
        // 5 rem -3: trunc(5/-3)=−1, result = 5 - (-1)(-3) = 2
        test("5 rem -3 = 2") {
            val result = with(rem) { bid64(398, 5uL).rem(bid64(398, 3uL, negative = true)) }
            result.significand shouldBe 2L
            result.sign shouldBe false
        }
        test("-4 rem 2 = -0 (exact zero, sign of x)") {
            val result = with(rem) { bid64(398, 4uL, negative = true).rem(bid64(398, 2uL)) }
            result.isZero() shouldBe true
            result.sign shouldBe true
        }
    }
})
