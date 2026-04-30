package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.fp.FiniteDecimalFloatingPoint
import com.kelvsyc.kotlin.core.fp.toBidDouble
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/** Creates a canonical BidDouble with the given biased exponent and significand. */
private fun bid64(biasedExp: Int, sig: ULong, negative: Boolean = false) =
    FiniteDecimalFloatingPoint(negative, biasedExp - 398, sig).toBidDouble()

class BidDoubleArithmeticTest : FunSpec({
    val arith = FloatingPointArithmetic.bidDouble

    // ── identity elements ─────────────────────────────────────────────────────

    test("zero is positiveZero") { arith.zero shouldBe BidDouble.positiveZero }
    test("one has significand 1 and biasedExponent 398") {
        arith.one.significand shouldBe 1L
        arith.one.biasedExponent shouldBe 398
    }

    // ── add ───────────────────────────────────────────────────────────────────

    context("add") {
        test("NaN + finite = NaN") {
            with(arith) { BidDouble.NaN.add(BidDouble.positiveZero) } shouldBe BidDouble.NaN
        }
        test("finite + NaN = NaN") {
            with(arith) { BidDouble.positiveZero.add(BidDouble.NaN) } shouldBe BidDouble.NaN
        }
        test("+inf + +inf = +inf") {
            with(arith) { BidDouble.positiveInfinity.add(BidDouble.positiveInfinity) } shouldBe BidDouble.positiveInfinity
        }
        test("+inf + -inf = NaN") {
            with(arith) { BidDouble.positiveInfinity.add(BidDouble.negativeInfinity) } shouldBe BidDouble.NaN
        }
        test("finite + +inf = +inf") {
            with(arith) { BidDouble.maxValue.add(BidDouble.positiveInfinity) } shouldBe BidDouble.positiveInfinity
        }
        test("+0 + +0 = +0") {
            with(arith) { BidDouble.positiveZero.add(BidDouble.positiveZero) } shouldBe BidDouble.positiveZero
        }
        test("-0 + -0 = -0") {
            with(arith) { BidDouble.negativeZero.add(BidDouble.negativeZero) } shouldBe BidDouble.negativeZero
        }
        test("+0 + -0 = +0") {
            with(arith) { BidDouble.positiveZero.add(BidDouble.negativeZero) } shouldBe BidDouble.positiveZero
        }
        test("1 + 1 = 2") {
            val one = arith.one
            val result = with(arith) { one.add(one) }
            result.significand shouldBe 2L
            result.biasedExponent shouldBe 398
        }
        test("cancellation x + (-x) = +0") {
            val x = bid64(398, 5uL)
            val negX = BidDouble(x.bits xor Long.MIN_VALUE)
            with(arith) { x.add(negX) } shouldBe BidDouble.positiveZero
        }
        test("1_000_000_000_000_000 + 1 = 1_000_000_000_000_001") {
            val a = bid64(398, 1_000_000_000_000_000uL)
            val b = bid64(398, 1uL)
            val result = with(arith) { a.add(b) }
            result.significand shouldBe 1_000_000_000_000_001L
            result.biasedExponent shouldBe 398
        }
        test("small + large (diff ≥ 17): result is large unchanged") {
            val result = with(arith) { BidDouble.minValue.add(BidDouble.maxValue) }
            result shouldBe BidDouble.maxValue
        }
        test("diff=16 smaller rounds away (rem < half)") {
            // a = 1_000_000_000_000_000 × 10^1 (biasedExp=399); b = 4 × 10^(-15) (biasedExp=383)
            // diff=16; s1Lo=0, sigS=4; sumLo=4; trunc=sig_L; rem=4 < 5×10^15 → rounds away
            val a = bid64(399, 1_000_000_000_000_000uL)
            val b = bid64(383, 4uL)
            val result = with(arith) { a.add(b) }
            result.significand shouldBe 1_000_000_000_000_000L
            result.biasedExponent shouldBe 399
        }
    }

    // ── subtract ──────────────────────────────────────────────────────────────

    context("subtract") {
        test("x - x = +0") {
            val x = bid64(398, 7uL)
            with(arith) { x.subtract(x) } shouldBe BidDouble.positiveZero
        }
        test("5 - 3 = 2") {
            val result = with(arith) { bid64(398, 5uL).subtract(bid64(398, 3uL)) }
            result.significand shouldBe 2L
            result.sign shouldBe false
        }
        test("3 - 5 = -2") {
            val result = with(arith) { bid64(398, 3uL).subtract(bid64(398, 5uL)) }
            result.sign shouldBe true
            result.significand shouldBe 2L
        }
    }

    // ── multiply ──────────────────────────────────────────────────────────────

    context("multiply") {
        test("NaN × finite = NaN") {
            with(arith) { BidDouble.NaN.multiply(arith.one) } shouldBe BidDouble.NaN
        }
        test("0 × infinity = NaN") {
            with(arith) { BidDouble.positiveZero.multiply(BidDouble.positiveInfinity) } shouldBe BidDouble.NaN
        }
        test("infinity × finite positive = +inf") {
            with(arith) { BidDouble.positiveInfinity.multiply(arith.one) } shouldBe BidDouble.positiveInfinity
        }
        test("1 × 1 = 1") {
            val result = with(arith) { arith.one.multiply(arith.one) }
            result.significand shouldBe 1L
            result.biasedExponent shouldBe 398
        }
        test("3 × 4 = 12") {
            val result = with(arith) { bid64(398, 3uL).multiply(bid64(398, 4uL)) }
            result.significand shouldBe 12L
        }
        test("negative × positive = negative") {
            val negOne = bid64(398, 1uL, negative = true)
            val result = with(arith) { negOne.multiply(arith.one) }
            result.sign shouldBe true
        }
        test("negative × negative = positive") {
            val negOne = bid64(398, 1uL, negative = true)
            val result = with(arith) { negOne.multiply(negOne) }
            result.sign shouldBe false
        }
        test("1_000_000_000_000_000 × 1_000_000_000_000_000 = 10^15 × 10^15") {
            // sig product hi=10^14, lo=0; rounded to 10^15; biasedExp=413
            val a = bid64(398, 1_000_000_000_000_000uL)
            val result = with(arith) { a.multiply(a) }
            result.significand shouldBe 1_000_000_000_000_000L
            result.biasedExponent shouldBe 413
        }
        test("overflow produces infinity") {
            val result = with(arith) { BidDouble.maxValue.multiply(BidDouble.maxValue) }
            result shouldBe BidDouble.positiveInfinity
        }
    }

    // ── divide ────────────────────────────────────────────────────────────────

    context("divide") {
        test("NaN / finite = NaN") {
            with(arith) { BidDouble.NaN.divide(arith.one) } shouldBe BidDouble.NaN
        }
        test("0 / 0 = NaN") {
            with(arith) { BidDouble.positiveZero.divide(BidDouble.positiveZero) } shouldBe BidDouble.NaN
        }
        test("1 / 0 = +inf") {
            with(arith) { arith.one.divide(BidDouble.positiveZero) } shouldBe BidDouble.positiveInfinity
        }
        test("inf / inf = NaN") {
            with(arith) { BidDouble.positiveInfinity.divide(BidDouble.positiveInfinity) } shouldBe BidDouble.NaN
        }
        test("finite / inf = +0") {
            val result = with(arith) { arith.one.divide(BidDouble.positiveInfinity) }
            result.isZero() shouldBe true
            result.sign shouldBe false
        }
        test("1 / 1 = 1") {
            val result = with(arith) { arith.one.divide(arith.one) }
            result.significand shouldBe 1L
            result.biasedExponent shouldBe 398
        }
        test("10 / 5 = 2") {
            val result = with(arith) { bid64(398, 10uL).divide(bid64(398, 5uL)) }
            result.significand shouldBe 2L
        }
        test("1 / 3 rounds to 3_333_333_333_333_333 × 10^(-16)") {
            // fracQ = 333...333 (18 digits), excess=2, trunc=3333333333333333, rem=33 < 50 → rounds down
            val result = with(arith) { arith.one.divide(bid64(398, 3uL)) }
            result.significand shouldBe 3_333_333_333_333_333L
            result.biasedExponent shouldBe 382
        }
        test("positive / negative = negative") {
            val negOne = bid64(398, 1uL, negative = true)
            val result = with(arith) { arith.one.divide(negOne) }
            result.sign shouldBe true
        }
        test("underflow: minValue / maxValue → zero") {
            val result = with(arith) { BidDouble.minValue.divide(BidDouble.maxValue) }
            result.isZero() shouldBe true
        }
    }

    // ── compareTo ─────────────────────────────────────────────────────────────

    context("compareTo") {
        test("consistent with BidDouble.comparator") {
            val a = BidDouble.minValue; val b = BidDouble.maxValue
            with(arith) { a.compareTo(b) } shouldBe BidDouble.comparator.compare(a, b)
        }
        test("NaN compares after everything") {
            with(arith) { BidDouble.NaN.compareTo(BidDouble.maxValue) } shouldBe 1
        }
    }

    // ── canonicalization ──────────────────────────────────────────────────────

    context("arithmetic canonicalizes inputs") {
        test("non-canonical large-sig (significand > 9_999_999_999_999_999) treated as zero in add") {
            // Large-sig BID encoding with significand > 9_999_999_999_999_999 is non-canonical (= 0).
            // 0x6007_FFFF_FFFF_FFFFL: bits 62-61=11 (large-sig), low51=2^51−1.
            // Reconstructed sig = 2^53 | (2^51−1) = 11_258_999_068_426_239 > 9_999_999_999_999_999.
            val nonCanon = BidDouble(0x6007_FFFF_FFFF_FFFFL)
            val result = with(arith) { nonCanon.add(arith.one) }
            // nonCanon.canonical() = +0, so result = 0 + 1 = 1.
            result.significand shouldBe 1L
        }
    }
})
