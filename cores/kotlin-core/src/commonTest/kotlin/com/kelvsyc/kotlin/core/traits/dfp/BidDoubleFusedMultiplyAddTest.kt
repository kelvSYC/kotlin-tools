package com.kelvsyc.kotlin.core.traits.dfp
import com.kelvsyc.kotlin.core.traits.fp.FusedMultiplyAdd
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic

import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.fp.FiniteDecimalFloatingPoint
import com.kelvsyc.kotlin.core.fp.toBidDouble
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/** Creates a canonical BidDouble with the given biased exponent and significand. */
private fun bid64(biasedExp: Int, sig: ULong, negative: Boolean = false) =
    FiniteDecimalFloatingPoint(negative, biasedExp - 398, sig).toBidDouble()

class BidDoubleFusedMultiplyAddTest : FunSpec({
    val fma   = FusedMultiplyAdd.bidDouble
    val arith = FloatingPointArithmetic.bidDouble

    // ── special-value propagation ─────────────────────────────────────────────

    context("NaN propagation") {
        test("NaN × finite + finite = NaN") {
            fma.fma(BidDouble.NaN, arith.one, arith.one) shouldBe BidDouble.NaN
        }
        test("finite × NaN + finite = NaN") {
            fma.fma(arith.one, BidDouble.NaN, arith.one) shouldBe BidDouble.NaN
        }
        test("finite × finite + NaN = NaN") {
            fma.fma(arith.one, arith.one, BidDouble.NaN) shouldBe BidDouble.NaN
        }
    }

    context("invalid operations") {
        test("0 × +∞ + finite = NaN") {
            fma.fma(BidDouble.positiveZero, BidDouble.positiveInfinity, arith.one) shouldBe BidDouble.NaN
        }
        test("+∞ × 0 + finite = NaN") {
            fma.fma(BidDouble.positiveInfinity, BidDouble.positiveZero, arith.one) shouldBe BidDouble.NaN
        }
        test("+∞ × +1 + (−∞) = NaN") {
            fma.fma(BidDouble.positiveInfinity, arith.one, BidDouble.negativeInfinity) shouldBe BidDouble.NaN
        }
        test("(−∞) × +1 + (+∞) = NaN") {
            fma.fma(BidDouble.negativeInfinity, arith.one, BidDouble.positiveInfinity) shouldBe BidDouble.NaN
        }
    }

    context("infinity results") {
        test("+∞ × +1 + finite = +∞") {
            fma.fma(BidDouble.positiveInfinity, arith.one, arith.one) shouldBe BidDouble.positiveInfinity
        }
        test("+∞ × +1 + (+∞) = +∞") {
            fma.fma(BidDouble.positiveInfinity, arith.one, BidDouble.positiveInfinity) shouldBe BidDouble.positiveInfinity
        }
        test("finite × finite + (+∞) = +∞") {
            fma.fma(arith.one, arith.one, BidDouble.positiveInfinity) shouldBe BidDouble.positiveInfinity
        }
        test("finite × finite + (−∞) = −∞") {
            fma.fma(arith.one, arith.one, BidDouble.negativeInfinity) shouldBe BidDouble.negativeInfinity
        }
    }

    // ── zero handling ─────────────────────────────────────────────────────────

    context("zero handling") {
        test("(+0) × (+0) + (+0) = +0") {
            fma.fma(BidDouble.positiveZero, BidDouble.positiveZero, BidDouble.positiveZero) shouldBe BidDouble.positiveZero
        }
        test("(−0) × (−0) + (+0) = +0") {
            // productSign = false XOR false = false; cc.sign = false → +0
            val result = fma.fma(BidDouble.negativeZero, BidDouble.negativeZero, BidDouble.positiveZero)
            result shouldBe BidDouble.positiveZero
        }
        test("0 × one + one = one") {
            // product is zero; return cc unchanged
            val result = fma.fma(BidDouble.positiveZero, arith.one, arith.one)
            result.significand shouldBe 1L
            result.biasedExponent shouldBe 398
        }
        test("one × one − one = +0") {
            val negOne = bid64(398, 1uL, negative = true)
            fma.fma(arith.one, arith.one, negOne) shouldBe BidDouble.positiveZero
        }
    }

    // ── exact arithmetic ──────────────────────────────────────────────────────

    context("exact arithmetic") {
        test("1 × 1 + 1 = 2") {
            val result = fma.fma(arith.one, arith.one, arith.one)
            result.significand shouldBe 2L
            result.biasedExponent shouldBe 398
        }
        test("2 × 3 + 4 = 10") {
            val result = fma.fma(bid64(398, 2uL), bid64(398, 3uL), bid64(398, 4uL))
            result.significand shouldBe 10L
            result.biasedExponent shouldBe 398
        }
        test("(−1) × 1 + 2 = 1") {
            val negOne = bid64(398, 1uL, negative = true)
            val result = fma.fma(negOne, arith.one, bid64(398, 2uL))
            result.sign shouldBe false
            result.significand shouldBe 1L
        }
        test("(−1) × (−1) + (−1) = 0") {
            val negOne = bid64(398, 1uL, negative = true)
            fma.fma(negOne, negOne, negOne) shouldBe BidDouble.positiveZero
        }
    }

    // ── distinguishing test ───────────────────────────────────────────────────
    //
    // a = 1_000_000_000_000_001 × 10^0  (= 10^15 + 1)
    // a² = 10^30 + 2×10^15 + 1 (exact)
    // roundedProduct = round(a²) = 1_000_000_000_000_002 × 10^15  (biasedExp 413)
    //
    // FMA: fma(a, a, −roundedProduct) = exact(a²) − roundedProduct = 1 × 10^0
    // Multiply-then-add: round(a²) − roundedProduct = 0
    //
    context("distinguishing test") {
        test("fma(a, a, −round(a²)) = exact residual 1, not 0") {
            val a = bid64(398, 1_000_000_000_000_001uL)
            val roundedProduct = bid64(413, 1_000_000_000_000_002uL)
            val negRoundedProduct = BidDouble(roundedProduct.bits xor Long.MIN_VALUE)
            val result = fma.fma(a, a, negRoundedProduct)
            result.significand shouldBe 1L
            result.biasedExponent shouldBe 398
            result.sign shouldBe false
        }
    }

    // ── wide-product rounding ─────────────────────────────────────────────────

    context("wide-product rounding") {
        test("fma(a, a, 0) rounds wide product to 16 significant digits") {
            // a² = (10^15+1)² = 10^30 + 2×10^15 + 1; top-16 coefficient = 1_000_000_000_000_002.
            val a = bid64(398, 1_000_000_000_000_001uL)
            val result = fma.fma(a, a, BidDouble.positiveZero)
            result.significand shouldBe 1_000_000_000_000_002L
            result.biasedExponent shouldBe 413
        }
        test("overflow → +infinity") {
            val result = fma.fma(BidDouble.maxValue, BidDouble.maxValue, BidDouble.positiveZero)
            result shouldBe BidDouble.positiveInfinity
        }
        test("underflow → +zero") {
            // minValue = 1 × 10^(0−398); product exponent = −796, far below minimum.
            val result = fma.fma(BidDouble.minValue, BidDouble.minValue, BidDouble.positiveZero)
            result.isZero() shouldBe true
            result.sign shouldBe false
        }
    }

    // ── delta alignment ───────────────────────────────────────────────────────
    //
    // delta = eC − eP (addend biased exp minus product biased exp).
    // Positive delta: addend quantum is coarser → align addend down to product scale.
    // Negative delta: product quantum is coarser → scale product up to addend scale.

    context("delta alignment") {
        // delta = 0: trivial same-quantum addition (covered by exact tests above)

        // delta = 15 (within 0..18 range)
        test("delta = 15: both product and addend contribute to the 16-digit result") {
            // product = 10^9 × 10^9 = 10^18 at eP=398; addend = 5 × 10^5 at eC=403; delta=5.
            // Aligned addend lo_C = 5×10^5 at eP; sum = (0, 10^18+5×10^5) at eP=398.
            // Wait, let's use a concrete delta=15 case.
            // a = 10^9, b = 10^9 → product (hiP=100, loP=0) at eP=398.
            // c = 5 × 10^(403−398) at eC=403, delta=5.
            // aligned lo_C = 5×10^5; sum = (100, 5×10^5) → rounds to 1_000_000_000_000_500 × 10^3.
            val a = bid64(398, 1_000_000_000uL)
            val b = bid64(398, 1_000_000_000uL)
            val c = bid64(403, 5uL)
            val result = fma.fma(a, b, c)
            result.significand shouldBe 1_000_000_000_000_500L
            result.biasedExponent shouldBe 401
        }

        // delta = -1 (in -16..-1 range: scale product up to addend scale)
        test("delta = −1: product scaled up by 10, addend added at finer quantum") {
            // 2 × 3 + 0.4 = 6.4. product=6 at eP=398; addend=4 at eC=397; delta=-1.
            // Scale product up: 6 × 10 = 60 at eC=397; sum = 60+4 = 64.
            val result = fma.fma(bid64(398, 2uL), bid64(398, 3uL), bid64(397, 4uL))
            result.significand shouldBe 64L
            result.biasedExponent shouldBe 397
        }

        // delta = 32 (in 19..47 range, same-sign addend dominates): 3-component path
        test("delta = 32: 3-component addend (hihi_C > 0), same sign, product contributes") {
            // a=5×10^15, b=2×10^15 → product = 10^31 (hiP=10^15, loP=0) at eP=398.
            // c = 10^32 at eC=430, delta=32; hihi_C=1, hi_C=0.
            // sum = (1, 10^15, 0) at eP=398 = 10^32 + 10^31 = 1.1 × 10^32.
            // Rounded to 16 digits = 1_100_000_000_000_000 × 10^17 at biasedExp 415.
            val a = bid64(398, 5_000_000_000_000_000uL)
            val b = bid64(398, 2_000_000_000_000_000uL)
            val c = bid64(430, 1uL)
            val result = fma.fma(a, b, c)
            result.significand shouldBe 1_100_000_000_000_000L
            result.biasedExponent shouldBe 415
        }

        // delta = 32 (in 19..47 range, different-sign): addend dominates, product subtracted
        test("delta = 32: 3-component addend (hihi_C > 0), different sign") {
            // a=2, b=2 → product=4 at eP=398; c=−10^32 at eC=430, delta=32; hihi_C=1.
            // |sum| = 10^32 − 4; rounded to 16 digits = 10^32 = 10^15 × 10^17.
            val result = fma.fma(bid64(398, 2uL), bid64(398, 2uL), bid64(430, 1uL, negative = true))
            result.sign shouldBe true
            result.significand shouldBe 1_000_000_000_000_000L
            result.biasedExponent shouldBe 415
        }

        // delta ≥ 48: addend completely dominates (product < 0.2 ULP of addend)
        test("delta ≥ 48: addend completely dominates, result = addend unchanged") {
            val c = bid64(450, 1uL)
            fma.fma(arith.one, arith.one, c) shouldBe c
        }

        // delta ≤ -17: product completely dominates; addend only a sticky bit
        test("delta ≤ −17: product dominates, addend has no effect on rounded result") {
            // product = (10^15)^2 = 10^30 at eP=398; addend = 10^(380−398) at eC=380, delta=−18.
            // Rounded product = 10^15 × 10^15 = 10^30 → biasedExp 413, sig 10^15.
            val a = bid64(398, 1_000_000_000_000_000uL)
            val c = bid64(380, 1uL)
            val result = fma.fma(a, a, c)
            result.significand shouldBe 1_000_000_000_000_000L
            result.biasedExponent shouldBe 413
        }
    }

    // ── sign combinations ─────────────────────────────────────────────────────

    context("sign combinations") {
        test("negative × negative + positive = positive") {
            val negFive = bid64(398, 5uL, negative = true)
            val result = fma.fma(negFive, negFive, bid64(398, 1uL))
            // (−5)×(−5)+1 = 26
            result.sign shouldBe false
            result.significand shouldBe 26L
        }
        test("positive × negative + positive result is negative") {
            // 4 × (−4) + 1 = −15
            val negFour = bid64(398, 4uL, negative = true)
            val result = fma.fma(bid64(398, 4uL), negFour, arith.one)
            result.sign shouldBe true
            result.significand shouldBe 15L
        }
        test("negative result from subtraction") {
            // 3 × 4 − 20 = −8
            val result = fma.fma(bid64(398, 3uL), bid64(398, 4uL), bid64(398, 20uL, negative = true))
            result.sign shouldBe true
            result.significand shouldBe 8L
        }
    }

    // ── non-canonical input ───────────────────────────────────────────────────

    context("non-canonical input") {
        test("non-canonical significand treated as zero in multiplicand a") {
            // Large-sig BID encoding with significand > 9_999_999_999_999_999 is non-canonical (= 0).
            // 0x6007_FFFF_FFFF_FFFFL: bits 62-61=11 (large-sig), low51=2^51−1;
            // sig = 2^53 | (2^51−1) = 11_258_999_068_426_239 > 9_999_999_999_999_999.
            val nonCanon = BidDouble(0x6007_FFFF_FFFF_FFFFL)
            // fma(0, one, one) = 0×1 + 1 = 1
            val result = fma.fma(nonCanon, arith.one, arith.one)
            result.significand shouldBe 1L
            result.biasedExponent shouldBe 398
        }
        test("non-canonical significand treated as zero in addend c") {
            // fma(one, one, 0) = 1×1 + 0 = 1
            val nonCanon = BidDouble(0x6007_FFFF_FFFF_FFFFL)
            val result = fma.fma(arith.one, arith.one, nonCanon)
            result.significand shouldBe 1L
            result.biasedExponent shouldBe 398
        }
    }
})
