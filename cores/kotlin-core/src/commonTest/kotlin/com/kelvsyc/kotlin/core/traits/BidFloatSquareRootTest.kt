package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.fp.FiniteDecimalFloatingPoint
import com.kelvsyc.kotlin.core.fp.toBidFloat
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun bid(biasedExp: Int, sig: UInt, negative: Boolean = false) =
    FiniteDecimalFloatingPoint(negative, biasedExp - 101, sig).toBidFloat()

/**
 * Tests for [FloatingPointSquareRoot.bidFloat].
 *
 * Expected significands and biasedExponents are verified by hand against the algorithm described
 * in [BidFloatSquareRoot.kt].  True mathematical square roots are cross-checked via the identity
 * `result.significand × 10^(result.biasedExponent − 101)`.
 */
class BidFloatSquareRootTest : FunSpec({
    val sqrt = FloatingPointSquareRoot.bidFloat

    // ── special values ────────────────────────────────────────────────────────

    context("special values") {
        test("NaN → NaN") {
            with(sqrt) { BidFloat.NaN.sqrt() }.isNaN() shouldBe true
        }
        test("+∞ → +∞") {
            with(sqrt) { BidFloat.positiveInfinity.sqrt() } shouldBe BidFloat.positiveInfinity
        }
        test("-∞ → NaN") {
            with(sqrt) { BidFloat.negativeInfinity.sqrt() }.isNaN() shouldBe true
        }
        test("+0 → +0") {
            with(sqrt) { BidFloat.positiveZero.sqrt() } shouldBe BidFloat.positiveZero
        }
        test("-0 → -0 (IEEE 754 §5.4.1)") {
            with(sqrt) { BidFloat.negativeZero.sqrt() } shouldBe BidFloat.negativeZero
        }
        test("negative finite → NaN") {
            with(sqrt) { bid(101, 1u, negative = true).sqrt() }.isNaN() shouldBe true
        }
        test("negative finite large magnitude → NaN") {
            with(sqrt) { bid(150, 5u, negative = true).sqrt() }.isNaN() shouldBe true
        }
    }

    // ── perfect squares: even exponent ───────────────────────────────────────
    // isqrt produces an exact result (rem = 0) so no rounding fires.

    context("perfect squares: d=1 input, k=12 scale") {
        // sig=1, biasedExp=101 → scaled = 1×10^12; isqrt = 10^6; result biasedExp = 0−6+101 = 95
        test("sqrt(1) = 1 (significand 1_000_000 × 10^−6)") {
            val r = with(sqrt) { bid(101, 1u).sqrt() }
            r.significand shouldBe 1_000_000
            r.biasedExponent shouldBe 95
        }
        // sig=4: scaled = 4×10^12; isqrt = 2×10^6
        test("sqrt(4) = 2") {
            val r = with(sqrt) { bid(101, 4u).sqrt() }
            r.significand shouldBe 2_000_000
            r.biasedExponent shouldBe 95
        }
        // sig=9: scaled = 9×10^12; isqrt = 3×10^6
        test("sqrt(9) = 3") {
            val r = with(sqrt) { bid(101, 9u).sqrt() }
            r.significand shouldBe 3_000_000
            r.biasedExponent shouldBe 95
        }
    }

    context("perfect squares: d=2 input, k=12 scale") {
        // sig=25: scaled = 25×10^12; isqrt = 5×10^6; result biasedExp = 0−6+101 = 95
        test("sqrt(25) = 5") {
            val r = with(sqrt) { bid(101, 25u).sqrt() }
            r.significand shouldBe 5_000_000
            r.biasedExponent shouldBe 95
        }
    }

    context("perfect squares: d=3 input, k=10 scale") {
        // sig=100: scaled = 100×10^10 = 10^12; isqrt = 10^6; result biasedExp = 0−5+101 = 96
        test("sqrt(100) = 10") {
            val r = with(sqrt) { bid(101, 100u).sqrt() }
            r.significand shouldBe 1_000_000
            r.biasedExponent shouldBe 96
        }
    }

    context("perfect squares: d=5 input, k=8 scale") {
        // sig=10000: scaled = 10000×10^8 = 10^12; isqrt = 10^6; result biasedExp = 0−4+101 = 97
        test("sqrt(10,000) = 100") {
            val r = with(sqrt) { bid(101, 10_000u).sqrt() }
            r.significand shouldBe 1_000_000
            r.biasedExponent shouldBe 97
        }
    }

    context("perfect squares: d=7 input, k=6 scale") {
        // sig=1_000_000: scaled = 10^12; isqrt = 10^6; result biasedExp = 0−3+101 = 98
        test("sqrt(1_000_000) = 1000") {
            val r = with(sqrt) { bid(101, 1_000_000u).sqrt() }
            r.significand shouldBe 1_000_000
            r.biasedExponent shouldBe 98
        }
        // sig=9_000_000: scaled = 9×10^12; isqrt = 3×10^6
        test("sqrt(9_000_000) = 3000") {
            val r = with(sqrt) { bid(101, 9_000_000u).sqrt() }
            r.significand shouldBe 3_000_000
            r.biasedExponent shouldBe 98
        }
    }

    // ── correct rounding ──────────────────────────────────────────────────────
    // rem > I  → round up;  rem ≤ I → round down.  No halfway case is possible for sqrt.

    context("rounding") {
        // sqrt(2) = 1.41421356...; 8th digit ≥ 5 → round up
        test("sqrt(2) rounds up to 1.414214") {
            val r = with(sqrt) { bid(101, 2u).sqrt() }
            r.significand shouldBe 1_414_214
            r.biasedExponent shouldBe 95
        }
        // sqrt(3) = 1.73205080...; 8th digit = 8 → round up
        test("sqrt(3) rounds up to 1.732051") {
            val r = with(sqrt) { bid(101, 3u).sqrt() }
            r.significand shouldBe 1_732_051
            r.biasedExponent shouldBe 95
        }
        // sqrt(5) = 2.23606797...; 8th digit = 9 → round up
        test("sqrt(5) rounds up to 2.236068") {
            val r = with(sqrt) { bid(101, 5u).sqrt() }
            r.significand shouldBe 2_236_068
            r.biasedExponent shouldBe 95
        }
        // sqrt(7) = 2.64575131...; 8th digit = 3 → round down
        test("sqrt(7) rounds down to 2.645751") {
            val r = with(sqrt) { bid(101, 7u).sqrt() }
            r.significand shouldBe 2_645_751
            r.biasedExponent shouldBe 95
        }
        // Boundary: rem = I (scaled = I²+I < (I+0.5)²) → round down (not up).
        // sig=9_999_999, biasedExp=100 → unbiased e=−1 (odd) → s=99_999_990, eAdj=−2, k=6.
        // scaled=99_999_990_000_000; I=9_999_999; rem=9_999_999=I → round down.
        test("rem = I boundary: rounds down (not up)") {
            val r = with(sqrt) { bid(100, 9_999_999u).sqrt() }
            r.significand shouldBe 9_999_999
            r.biasedExponent shouldBe 97   // −2/2 − 6/2 + 101 = −1 − 3 + 101
        }
    }

    // ── odd unbiased exponent: makeEven adjustment ────────────────────────────
    // When the unbiased exponent is odd, the significand is multiplied by 10 and the exponent
    // is decremented by one before the main computation.

    context("odd unbiased exponent") {
        // sig=1, biasedExp=100 → unbiased e=−1 (odd); s=10, eAdj=−2; scaled=10^13;
        // isqrt(10^13)=3_162_277; rem>I → round up; result biasedExp=−2/2−6+101=94
        test("sqrt(10^−1) ≈ sqrt(10)×10^−1") {
            val r = with(sqrt) { bid(100, 1u).sqrt() }
            r.significand shouldBe 3_162_278
            r.biasedExponent shouldBe 94
        }
        // sig=1, biasedExp=102 → unbiased e=1 (odd); same scaled value; result biasedExp=95
        test("sqrt(10^1) ≈ sqrt(10)") {
            val r = with(sqrt) { bid(102, 1u).sqrt() }
            r.significand shouldBe 3_162_278
            r.biasedExponent shouldBe 95
        }
        // sig=1, biasedExp=104 → unbiased e=3 (odd); result biasedExp=96
        test("sqrt(10^3) ≈ sqrt(10)×10") {
            val r = with(sqrt) { bid(104, 1u).sqrt() }
            r.significand shouldBe 3_162_278
            r.biasedExponent shouldBe 96
        }
        // sig=2, biasedExp=102 → unbiased e=1 (odd); s=20; scaled=20×10^12=2×10^13;
        // sqrt(20)=4.472135955...; round up to 4_472_136; biasedExp=0/2−6+101=95
        test("sqrt(20) rounds up to 4.472136") {
            val r = with(sqrt) { bid(102, 2u).sqrt() }
            r.significand shouldBe 4_472_136
            r.biasedExponent shouldBe 95
        }
    }

    // ── cohort independence ───────────────────────────────────────────────────
    // Two encodings of the same mathematical value must produce the same sqrt.

    context("cohort independence") {
        // 0.01 = 1×10^−2 (biasedExp=99) = 100×10^−4 (biasedExp=97)
        // Both should give sqrt(0.01) = 0.1 = 1_000_000×10^−7 (biasedExp=94).
        test("sqrt(0.01) same for both cohorts of 0.01") {
            val r1 = with(sqrt) { bid(99, 1u).sqrt() }
            val r2 = with(sqrt) { bid(97, 100u).sqrt() }
            r1 shouldBe r2
            r1.significand shouldBe 1_000_000
            r1.biasedExponent shouldBe 94
        }
        // 9×10^4 = 9×10^4 (biasedExp=105, sig=9) = 90000×10^0 (biasedExp=101, sig=90000)
        // sqrt(90000) = 300 = 3_000_000×10^−4 (biasedExp=97)
        test("sqrt(9×10^4) same for both cohorts") {
            val r1 = with(sqrt) { bid(105, 9u).sqrt() }
            val r2 = with(sqrt) { bid(101, 90_000u).sqrt() }
            r1 shouldBe r2
            r1.significand shouldBe 3_000_000
            r1.biasedExponent shouldBe 97
        }
    }

    // ── extreme values ────────────────────────────────────────────────────────

    context("extreme values") {
        // minValue = 1×10^−101 (biasedExp=0, sig=1). Unbiased e=−101 (odd) → s=10, eAdj=−102.
        // scaled=10^13; isqrt=3_162_277; rem>I → 3_162_278; biasedExp=−51−6+101=44.
        // True value: sqrt(10)×10^−51 ≈ 3.162278×10^−51.
        test("sqrt(minValue) ≈ sqrt(10)×10^−51") {
            val r = with(sqrt) { BidFloat.minValue.sqrt() }
            r.significand shouldBe 3_162_278
            r.biasedExponent shouldBe 44
        }
        // maxValue = 9_999_999×10^90 (biasedExp=191, sig=9_999_999). Unbiased e=90 (even), d=7, k=6.
        // scaled=9_999_999_000_000; isqrt=3_162_277; rem>I → 3_162_278; biasedExp=45−3+101=143.
        // True value: sqrt(9.999999)×10^45 ≈ 3.162278×10^48.
        test("sqrt(maxValue) ≈ 3.162278×10^48") {
            val r = with(sqrt) { BidFloat.maxValue.sqrt() }
            r.significand shouldBe 3_162_278
            r.biasedExponent shouldBe 143
        }
        // minNormal = 1_000_000×10^−101 = 10^−95 (biasedExp=0, sig=1_000_000).
        // Unbiased e=−101 (odd) → s=10_000_000, eAdj=−102. d=8, k=6.
        // scaled=10_000_000×10^6=10^13; isqrt=3_162_277; rem>I → 3_162_278; biasedExp=−51−3+101=47.
        // True value: sqrt(10^−95) = 10^−47.5 = sqrt(10)×10^−48 ≈ 3.162278×10^−48.
        test("sqrt(minNormal) ≈ sqrt(10)×10^−48") {
            val r = with(sqrt) { BidFloat.minNormal.sqrt() }
            r.significand shouldBe 3_162_278
            r.biasedExponent shouldBe 47
        }
    }
})
