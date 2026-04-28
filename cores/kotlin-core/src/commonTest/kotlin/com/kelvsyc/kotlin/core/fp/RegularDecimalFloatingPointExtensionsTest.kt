package com.kelvsyc.kotlin.core.fp

import com.kelvsyc.kotlin.core.BidFloat
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class RegularDecimalFloatingPointExtensionsTest : FunSpec({

    // ── BidFloat → FiniteDecimalFloatingPoint ─────────────────────────────────

    context("BidFloat.toRegularDecimalFloatingPoint") {
        test("+0 produces zero significand at minimum exponent") {
            val r = BidFloat.positiveZero.toRegularDecimalFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -101
            r.significand shouldBe 0u
        }

        test("-0 sets the sign flag") {
            val r = BidFloat.negativeZero.toRegularDecimalFloatingPoint()
            r.sign shouldBe true
            r.significand shouldBe 0u
        }

        test("minValue encodes as significand=1 at minimum exponent") {
            val r = BidFloat.minValue.toRegularDecimalFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -101
            r.significand shouldBe 1u
        }

        test("minNormal encodes as significand=1_000_000 at minimum exponent") {
            val r = BidFloat.minNormal.toRegularDecimalFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -101
            r.significand shouldBe 1_000_000u
        }

        test("epsilon encodes as significand=1 at exponent -6") {
            val r = BidFloat.epsilon.toRegularDecimalFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -6
            r.significand shouldBe 1u
        }

        test("maxValue encodes as significand=9_999_999 at maximum exponent") {
            val r = BidFloat.maxValue.toRegularDecimalFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe 90
            r.significand shouldBe 9_999_999u
        }

        test("large-significand encoded value (leading digit 8-9) extracts correctly") {
            // 9_000_000 × 10^0: biasedExponent = 101, uses large-significand BID encoding.
            // combination = 0x600 | (101 shl 1) | 0 = 0x6CA; low21 = 611_392 = 0x95440.
            // word = (0x6CA shl 20) | 0x95440 = 0x6CA95440.
            val r = BidFloat(0x6CA95440).toRegularDecimalFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe 0
            r.significand shouldBe 9_000_000u
        }

        test("throws for NaN") {
            shouldThrow<IllegalArgumentException> { BidFloat.NaN.toRegularDecimalFloatingPoint() }
        }

        test("throws for positive infinity") {
            shouldThrow<IllegalArgumentException> { BidFloat.positiveInfinity.toRegularDecimalFloatingPoint() }
        }

        test("throws for negative infinity") {
            shouldThrow<IllegalArgumentException> { BidFloat.negativeInfinity.toRegularDecimalFloatingPoint() }
        }
    }

    // ── FiniteDecimalFloatingPoint<UInt> → BidFloat ───────────────────────────

    context("FiniteDecimalFloatingPoint<UInt>.toBidFloat") {
        test("+0 (zero significand, positive sign) returns positiveZero") {
            FiniteDecimalFloatingPoint(false, -101, 0u).toBidFloat() shouldBe BidFloat.positiveZero
        }

        test("-0 (zero significand, negative sign) returns negativeZero") {
            FiniteDecimalFloatingPoint(true, -101, 0u).toBidFloat() shouldBe BidFloat.negativeZero
        }

        test("significand=1 at minimum exponent reconstructs minValue") {
            FiniteDecimalFloatingPoint(false, -101, 1u).toBidFloat() shouldBe BidFloat.minValue
        }

        test("significand=1_000_000 at minimum exponent reconstructs minNormal") {
            FiniteDecimalFloatingPoint(false, -101, 1_000_000u).toBidFloat() shouldBe BidFloat.minNormal
        }

        test("significand=1 at exponent -6 reconstructs epsilon") {
            FiniteDecimalFloatingPoint(false, -6, 1u).toBidFloat() shouldBe BidFloat.epsilon
        }

        test("significand=9_999_999 at maximum exponent reconstructs maxValue") {
            FiniteDecimalFloatingPoint(false, 90, 9_999_999u).toBidFloat() shouldBe BidFloat.maxValue
        }

        test("large-significand value (leading digit 8-9) packs correctly") {
            FiniteDecimalFloatingPoint(false, 0, 9_000_000u).toBidFloat().bits shouldBe 0x6CA95440
        }

        test("overflow biased exponent produces positive infinity") {
            FiniteDecimalFloatingPoint(false, 91, 9_999_999u).toBidFloat() shouldBe BidFloat.positiveInfinity
        }

        test("overflow biased exponent produces negative infinity for negative sign") {
            FiniteDecimalFloatingPoint(true, 91, 9_999_999u).toBidFloat() shouldBe BidFloat.negativeInfinity
        }

        test("underflow produces positive zero") {
            FiniteDecimalFloatingPoint(false, -110, 1u).toBidFloat() shouldBe BidFloat.positiveZero
        }

        test("8-digit significand is rounded to 7 digits") {
            // 12_345_678 rounded to 7 digits = 1_234_568 (round half-up); exponent adjusted by +1.
            val r = FiniteDecimalFloatingPoint(false, 0, 12_345_678u).toBidFloat()
            r.significand shouldBe 1_234_568
            r.biasedExponent shouldBe 102  // exponent=0+1=1, biasedExp=1+101=102
        }
    }

    // ── Round-trip ────────────────────────────────────────────────────────────

    context("BidFloat round-trip through FiniteDecimalFloatingPoint") {
        val cases = listOf(
            BidFloat.positiveZero,
            BidFloat.negativeZero,
            BidFloat.minValue,
            BidFloat.minNormal,
            BidFloat.epsilon,
            BidFloat.maxValue,
            BidFloat(0x6CA95440),  // 9_000_000 × 10^0, large-significand encoding
            BidFloat(0x2F800001),  // 1 × 10^(-6)
        )
        cases.forEach { v ->
            test("${v.bits.toString(16)} round-trips") {
                v.toRegularDecimalFloatingPoint().toBidFloat() shouldBe v
            }
        }
    }

    // ── Cohort equality ───────────────────────────────────────────────────────

    context("FiniteDecimalFloatingPoint.uIntCohortEquality") {
        val eq = FiniteDecimalFloatingPoint.uIntCohortEquality

        test("any two zeros are cohort-equal regardless of sign and exponent") {
            with(eq) {
                FiniteDecimalFloatingPoint(false, -101, 0u).isEqualTo(FiniteDecimalFloatingPoint(true, 5, 0u)) shouldBe true
            }
        }

        test("cohort-distinct representations of the same value are equal") {
            // 1 × 10^0  ==  10 × 10^(-1)
            with(eq) {
                FiniteDecimalFloatingPoint(false, 0, 1u).isEqualTo(FiniteDecimalFloatingPoint(false, -1, 10u)) shouldBe true
            }
        }

        test("values with different magnitudes are not equal") {
            with(eq) {
                FiniteDecimalFloatingPoint(false, 0, 1u).isEqualTo(FiniteDecimalFloatingPoint(false, 0, 2u)) shouldBe false
            }
        }

        test("values with opposite signs are not equal") {
            with(eq) {
                FiniteDecimalFloatingPoint(false, 0, 1u).isEqualTo(FiniteDecimalFloatingPoint(true, 0, 1u)) shouldBe false
            }
        }

        test("zero is not equal to a non-zero value") {
            with(eq) {
                FiniteDecimalFloatingPoint(false, 0, 0u).isEqualTo(FiniteDecimalFloatingPoint(false, 0, 1u)) shouldBe false
            }
        }

        test("structural equals reflects raw field identity, not cohort membership") {
            FiniteDecimalFloatingPoint(false, 0, 1u) shouldNotBe FiniteDecimalFloatingPoint(false, -1, 10u)
        }
    }
})
