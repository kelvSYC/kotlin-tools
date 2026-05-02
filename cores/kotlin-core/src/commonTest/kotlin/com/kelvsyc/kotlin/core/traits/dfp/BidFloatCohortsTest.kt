package com.kelvsyc.kotlin.core.traits.dfp

import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.fp.FiniteDecimalFloatingPoint
import com.kelvsyc.kotlin.core.fp.toBidFloat
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun bid(biasedExp: Int, sig: UInt) = FiniteDecimalFloatingPoint(false, biasedExp - 101, sig).toBidFloat()
private fun bid(biasedExp: Int, sig: UInt, negative: Boolean) = FiniteDecimalFloatingPoint(negative, biasedExp - 101, sig).toBidFloat()

class BidFloatCohortsTest : FunSpec({
    val coh = BidFloat.cohorts
    val enc = BidFloat.encoding

    // ── reduce ────────────────────────────────────────────────────────────────

    context("BidFloat.cohorts.reduce") {
        test("NaN returns unchanged") {
            with(coh) { BidFloat.NaN.reduce() } shouldBe BidFloat.NaN
        }
        test("positive infinity returns unchanged") {
            with(coh) { BidFloat.positiveInfinity.reduce() } shouldBe BidFloat.positiveInfinity
        }
        test("negative infinity returns unchanged") {
            with(coh) { BidFloat.negativeInfinity.reduce() } shouldBe BidFloat.negativeInfinity
        }
        test("positive zero: preferred exponent 0 (biasedExp=101), sign preserved") {
            val r = with(coh) { BidFloat.positiveZero.reduce() }
            r.significand shouldBe 0
            r.biasedExponent shouldBe 101
            r.sign shouldBe false
        }
        test("negative zero: preferred exponent 0, sign preserved") {
            val r = with(coh) { BidFloat.negativeZero.reduce() }
            r.significand shouldBe 0
            r.biasedExponent shouldBe 101
            r.sign shouldBe true
        }
        test("1 × 10^0 reduces to 1 × 10^0 (no trailing zeros)") {
            val one = bid(101, 1u)
            val r = with(coh) { one.reduce() }
            r.significand shouldBe 1
            r.biasedExponent shouldBe 101
        }
        test("10 × 10^(-1) reduces to 1 × 10^0") {
            val cohortOf1 = bid(100, 10u)  // 10 × 10^(-1)
            val r = with(coh) { cohortOf1.reduce() }
            r.significand shouldBe 1
            r.biasedExponent shouldBe 101
        }
        test("1_000_000 × 10^(-101) (minNormal) reduces to 1 × 10^(-95)") {
            val r = with(coh) { BidFloat.minNormal.reduce() }
            r.significand shouldBe 1
            r.biasedExponent shouldBe 6  // -95 + 101 = 6
        }
        test("9_999_999 × 10^90 (maxValue) has no trailing zeros") {
            val r = with(coh) { BidFloat.maxValue.reduce() }
            r.significand shouldBe 9_999_999
        }
    }

    // ── quantum ───────────────────────────────────────────────────────────────

    context("BidFloat.cohorts.quantum") {
        test("NaN returns unchanged") {
            with(coh) { BidFloat.NaN.quantum() } shouldBe BidFloat.NaN
        }
        test("positive infinity returns unchanged") {
            with(coh) { BidFloat.positiveInfinity.quantum() } shouldBe BidFloat.positiveInfinity
        }
        test("quantum of epsilon (1 × 10^(-6)) is 1 × 10^(-6)") {
            val r = with(coh) { BidFloat.epsilon.quantum() }
            r.significand shouldBe 1
            r.biasedExponent shouldBe BidFloat.epsilon.biasedExponent
            r.sign shouldBe false
        }
        test("quantum of 1234567 × 10^3 is 1 × 10^3") {
            val v = bid(104, 1_234_567u)  // biasedExp=104 → exp=3
            val r = with(coh) { v.quantum() }
            r.significand shouldBe 1
            r.biasedExponent shouldBe 104
        }
        test("quantum preserves sign bit") {
            val negV = bid(101, 42u, negative = true)
            val r = with(coh) { negV.quantum() }
            r.sign shouldBe true
            r.significand shouldBe 1
            r.biasedExponent shouldBe 101
        }
    }

    // ── quantize ──────────────────────────────────────────────────────────────

    context("BidFloat.cohorts.quantize") {
        test("NaN operand produces NaN") {
            with(coh) { BidFloat.NaN.quantize(BidFloat.positiveZero) } shouldBe BidFloat.NaN
        }
        test("NaN quantum produces NaN") {
            with(coh) { BidFloat.positiveZero.quantize(BidFloat.NaN) } shouldBe BidFloat.NaN
        }
        test("infinite quantum produces NaN") {
            with(coh) { BidFloat.positiveZero.quantize(BidFloat.positiveInfinity) } shouldBe BidFloat.NaN
        }
        test("infinite this produces NaN") {
            with(coh) { BidFloat.positiveInfinity.quantize(BidFloat.positiveZero) } shouldBe BidFloat.NaN
        }
        test("zero quantized to any finite quantum gives zero at that exponent") {
            val target = BidFloat.epsilon  // biasedExp=95
            val r = with(coh) { BidFloat.positiveZero.quantize(target) }
            r.significand shouldBe 0
            r.biasedExponent shouldBe 95
        }
        test("quantize to same exponent returns same value") {
            val v = bid(101, 1u)
            val r = with(coh) { v.quantize(v) }
            r.significand shouldBe 1
            r.biasedExponent shouldBe 101
        }
        test("scale down: 100 × 10^0 quantized to 10^2 gives 1 × 10^2") {
            val v = bid(101, 100u)   // 100 × 10^0 = 100, biasedExp=101
            val q = bid(103, 1u)    // quantum at 10^2, biasedExp=103
            val r = with(coh) { v.quantize(q) }
            r.significand shouldBe 1
            r.biasedExponent shouldBe 103
        }
        test("scale up: 1 × 10^0 quantized to 10^(-2) gives 100 × 10^(-2)") {
            val v = bid(101, 1u)   // 1 × 10^0, biasedExp=101
            val q = bid(99, 1u)    // quantum at 10^(-2), biasedExp=99
            val r = with(coh) { v.quantize(q) }
            r.significand shouldBe 100
            r.biasedExponent shouldBe 99
        }
        test("scale up overflow (too many digits) produces NaN") {
            // 1_000_000 × 10^0 quantized to 10^(-1): 10_000_000 (8 digits) → NaN (invalid)
            val v = bid(101, 1_000_000u)
            val q = bid(100, 1u)    // quantum at 10^(-1), biasedExp=100
            with(coh) { v.quantize(q) } shouldBe BidFloat.NaN
        }
    }

    // ── encoding consistency ──────────────────────────────────────────────────

    context("cohort operations produce canonical results") {
        test("reduce then quantize back is numerically equal to original") {
            val original = bid(99, 100u)  // 100 × 10^(-2) = 1.0
            val reduced = with(coh) { original.reduce() }
            reduced.significand shouldBe 1
            val restored = with(coh) { reduced.quantize(original) }
            with(enc) { restored.isCanonical() } shouldBe true
            with(BidFloat.numericalEquality) { restored.isEqualTo(original) } shouldBe true
        }
    }
})
