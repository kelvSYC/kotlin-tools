package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.fp.FiniteDecimalFloatingPoint
import com.kelvsyc.kotlin.core.fp.toBidFloat
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/** Creates a canonical BidFloat with the given biased exponent and significand. */
private fun bid(biasedExp: Int, sig: UInt) = FiniteDecimalFloatingPoint(false, biasedExp - 101, sig).toBidFloat()
private fun bid(biasedExp: Int, sig: UInt, negative: Boolean) = FiniteDecimalFloatingPoint(negative, biasedExp - 101, sig).toBidFloat()

class BidFloatArithmeticTest : FunSpec({
    val arith = FloatingPointArithmetic.bidFloat

    // ── identity elements ─────────────────────────────────────────────────────

    test("zero is positiveZero") { arith.zero shouldBe BidFloat.positiveZero }
    test("one has significand 1 and biasedExponent 101") {
        arith.one.significand shouldBe 1
        arith.one.biasedExponent shouldBe 101
    }

    // ── add ───────────────────────────────────────────────────────────────────

    context("add") {
        test("NaN + finite = NaN") {
            with(arith) { BidFloat.NaN.add(BidFloat.positiveZero) } shouldBe BidFloat.NaN
        }
        test("finite + NaN = NaN") {
            with(arith) { BidFloat.positiveZero.add(BidFloat.NaN) } shouldBe BidFloat.NaN
        }
        test("+inf + +inf = +inf") {
            with(arith) { BidFloat.positiveInfinity.add(BidFloat.positiveInfinity) } shouldBe BidFloat.positiveInfinity
        }
        test("+inf + -inf = NaN") {
            with(arith) { BidFloat.positiveInfinity.add(BidFloat.negativeInfinity) } shouldBe BidFloat.NaN
        }
        test("finite + +inf = +inf") {
            with(arith) { BidFloat.maxValue.add(BidFloat.positiveInfinity) } shouldBe BidFloat.positiveInfinity
        }
        test("+0 + +0 = +0") {
            with(arith) { BidFloat.positiveZero.add(BidFloat.positiveZero) } shouldBe BidFloat.positiveZero
        }
        test("-0 + -0 = -0") {
            with(arith) { BidFloat.negativeZero.add(BidFloat.negativeZero) } shouldBe BidFloat.negativeZero
        }
        test("+0 + -0 = +0") {
            with(arith) { BidFloat.positiveZero.add(BidFloat.negativeZero) } shouldBe BidFloat.positiveZero
        }
        test("1 + 1 = 2") {
            val one = arith.one
            val result = with(arith) { one.add(one) }
            result.significand shouldBe 2
            result.biasedExponent shouldBe 101
        }
        test("cancellation x + (-x) = +0") {
            val x = bid(101, 5u)
            val negX = BidFloat(x.bits xor Int.MIN_VALUE)
            with(arith) { x.add(negX) } shouldBe BidFloat.positiveZero
        }
        test("1_000_000 × 10^0 + 1 × 10^0 = 1_000_001 × 10^0") {
            val a = bid(101, 1_000_000u)
            val b = bid(101, 1u)
            val result = with(arith) { a.add(b) }
            result.significand shouldBe 1_000_001
            result.biasedExponent shouldBe 101
        }
        test("small + large (diff ≥ 8): result is large unchanged") {
            // diff = 191 between biased exponents → result is maxValue
            val result = with(arith) { BidFloat.minValue.add(BidFloat.maxValue) }
            result shouldBe BidFloat.maxValue
        }
        test("1_000_000 × 10^1 + 5 × 10^(-6): smaller rounds away (diff=7, rem < half)") {
            // total = 1_000_000 × 10^7 + 5; rounding to 7 digits → rem=5 < 5_000_000 → trunc=1_000_000
            val a = bid(102, 1_000_000u)  // 1_000_000 × 10^1
            val b = bid(95, 5u)           // 5 × 10^(-6)
            val result = with(arith) { a.add(b) }
            result.significand shouldBe 1_000_000
            result.biasedExponent shouldBe 102
        }
    }

    // ── subtract ──────────────────────────────────────────────────────────────

    context("subtract") {
        test("x - x = +0") {
            val x = bid(101, 7u)
            with(arith) { x.subtract(x) } shouldBe BidFloat.positiveZero
        }
        test("5 - 3 = 2") {
            val result = with(arith) { bid(101, 5u).subtract(bid(101, 3u)) }
            result.significand shouldBe 2
            result.sign shouldBe false
        }
        test("3 - 5 = -2") {
            val result = with(arith) { bid(101, 3u).subtract(bid(101, 5u)) }
            result.sign shouldBe true
            result.significand shouldBe 2
        }
    }

    // ── multiply ──────────────────────────────────────────────────────────────

    context("multiply") {
        test("NaN × finite = NaN") {
            with(arith) { BidFloat.NaN.multiply(arith.one) } shouldBe BidFloat.NaN
        }
        test("0 × infinity = NaN") {
            with(arith) { BidFloat.positiveZero.multiply(BidFloat.positiveInfinity) } shouldBe BidFloat.NaN
        }
        test("infinity × finite positive = +inf") {
            with(arith) { BidFloat.positiveInfinity.multiply(arith.one) } shouldBe BidFloat.positiveInfinity
        }
        test("1 × 1 = 1") {
            val result = with(arith) { arith.one.multiply(arith.one) }
            result.significand shouldBe 1
            result.biasedExponent shouldBe 101
        }
        test("3 × 4 = 12") {
            val result = with(arith) { bid(101, 3u).multiply(bid(101, 4u)) }
            result.significand shouldBe 12
        }
        test("negative × positive = negative") {
            val negOne = bid(101, 1u, negative = true)
            val result = with(arith) { negOne.multiply(arith.one) }
            result.sign shouldBe true
        }
        test("negative × negative = positive") {
            val negOne = bid(101, 1u, negative = true)
            val result = with(arith) { negOne.multiply(negOne) }
            result.sign shouldBe false
        }
        test("1_000_000 × 1_000_000 = 1_000_000 × 10^6 (biasedExp=107)") {
            // sig product = 10^12, rounds to 1_000_000 at biasedExp = 101+101-101+6 = 107
            val a = bid(101, 1_000_000u)
            val result = with(arith) { a.multiply(a) }
            result.significand shouldBe 1_000_000
            result.biasedExponent shouldBe 107
        }
        test("overflow produces infinity") {
            val result = with(arith) { BidFloat.maxValue.multiply(BidFloat.maxValue) }
            result shouldBe BidFloat.positiveInfinity
        }
    }

    // ── divide ────────────────────────────────────────────────────────────────

    context("divide") {
        test("NaN / finite = NaN") {
            with(arith) { BidFloat.NaN.divide(arith.one) } shouldBe BidFloat.NaN
        }
        test("0 / 0 = NaN") {
            with(arith) { BidFloat.positiveZero.divide(BidFloat.positiveZero) } shouldBe BidFloat.NaN
        }
        test("1 / 0 = +inf") {
            with(arith) { arith.one.divide(BidFloat.positiveZero) } shouldBe BidFloat.positiveInfinity
        }
        test("inf / inf = NaN") {
            with(arith) { BidFloat.positiveInfinity.divide(BidFloat.positiveInfinity) } shouldBe BidFloat.NaN
        }
        test("finite / inf = +0") {
            val result = with(arith) { arith.one.divide(BidFloat.positiveInfinity) }
            result.isZero() shouldBe true
            result.sign shouldBe false
        }
        test("1 / 1 = 1") {
            val result = with(arith) { arith.one.divide(arith.one) }
            result.significand shouldBe 1
        }
        test("10 / 5 = 2") {
            val result = with(arith) { bid(101, 10u).divide(bid(101, 5u)) }
            result.significand shouldBe 2
        }
        test("1 / 3 rounds to 3_333_333 × 10^(-7)") {
            val result = with(arith) { arith.one.divide(bid(101, 3u)) }
            result.significand shouldBe 3_333_333
        }
        test("positive / negative = negative") {
            val negOne = bid(101, 1u, negative = true)
            val result = with(arith) { arith.one.divide(negOne) }
            result.sign shouldBe true
        }
        test("underflow: minValue / maxValue → zero") {
            val result = with(arith) { BidFloat.minValue.divide(BidFloat.maxValue) }
            result.isZero() shouldBe true
        }
    }

    // ── compareTo ─────────────────────────────────────────────────────────────

    context("compareTo") {
        test("consistent with BidFloat.comparator") {
            val a = BidFloat.minValue; val b = BidFloat.maxValue
            with(arith) { a.compareTo(b) } shouldBe BidFloat.comparator.compare(a, b)
        }
        test("NaN compares after everything") {
            with(arith) { BidFloat.NaN.compareTo(BidFloat.maxValue) } shouldBe 1
        }
    }

    // ── canonicalization ──────────────────────────────────────────────────────

    context("arithmetic canonicalizes inputs") {
        test("non-canonical (large-sig sig > 9_999_999) treated as zero in add") {
            // Large-sig encoding with significand > 9,999,999: non-canonical, treated as zero.
            // combination top-2 bits = 11 → ushr 9 == 3 (large-sig). sig = 0x800000 | low21.
            // 0x6CBFFFFF: sig = 0x9FFFFF = 10,485,759 > 9,999,999. Non-canonical.
            val nonCanon = BidFloat(0x6CBFFFFF)
            val result = with(arith) { nonCanon.add(arith.one) }
            // nonCanon.canonical() = +0, so result = 0 + 1 = 1.
            result.significand shouldBe 1
        }
    }
})
