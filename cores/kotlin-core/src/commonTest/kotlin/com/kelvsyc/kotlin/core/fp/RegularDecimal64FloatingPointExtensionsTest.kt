package com.kelvsyc.kotlin.core.fp

import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.DpdDouble
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RegularDecimal64FloatingPointExtensionsTest : FunSpec({

    // ── BidDouble → FiniteDecimalFloatingPoint ────────────────────────────────

    context("BidDouble.toRegularDecimalFloatingPoint") {
        test("+0 produces zero significand at minimum exponent") {
            val r = BidDouble.positiveZero.toRegularDecimalFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -398
            r.significand shouldBe 0uL
        }

        test("-0 sets the sign flag") {
            val r = BidDouble.negativeZero.toRegularDecimalFloatingPoint()
            r.sign shouldBe true
            r.significand shouldBe 0uL
        }

        test("minValue encodes as significand=1 at exponent −398") {
            val r = BidDouble.minValue.toRegularDecimalFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -398
            r.significand shouldBe 1uL
        }

        test("minNormal encodes as significand=10^15 at exponent −398") {
            val r = BidDouble.minNormal.toRegularDecimalFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -398
            r.significand shouldBe 1_000_000_000_000_000uL
        }

        test("epsilon encodes as significand=1 at exponent −15") {
            val r = BidDouble.epsilon.toRegularDecimalFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -15
            r.significand shouldBe 1uL
        }

        test("maxValue encodes as significand=9_999_999_999_999_999 at exponent 369") {
            val r = BidDouble.maxValue.toRegularDecimalFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe 369
            r.significand shouldBe 9_999_999_999_999_999uL
        }

        test("throws for NaN") {
            shouldThrow<IllegalArgumentException> { BidDouble.NaN.toRegularDecimalFloatingPoint() }
        }

        test("throws for positive infinity") {
            shouldThrow<IllegalArgumentException> { BidDouble.positiveInfinity.toRegularDecimalFloatingPoint() }
        }

        test("throws for negative infinity") {
            shouldThrow<IllegalArgumentException> { BidDouble.negativeInfinity.toRegularDecimalFloatingPoint() }
        }
    }

    // ── FiniteDecimalFloatingPoint<ULong> → BidDouble ─────────────────────────

    context("FiniteDecimalFloatingPoint<ULong>.toBidDouble") {
        test("+0 returns positiveZero") {
            FiniteDecimalFloatingPoint(false, -398, 0uL).toBidDouble() shouldBe BidDouble.positiveZero
        }

        test("-0 returns negativeZero") {
            FiniteDecimalFloatingPoint(true, -398, 0uL).toBidDouble() shouldBe BidDouble.negativeZero
        }

        test("significand=1 at exponent −398 reconstructs minValue") {
            FiniteDecimalFloatingPoint(false, -398, 1uL).toBidDouble() shouldBe BidDouble.minValue
        }

        test("significand=10^15 at exponent −398 reconstructs minNormal") {
            FiniteDecimalFloatingPoint(false, -398, 1_000_000_000_000_000uL).toBidDouble() shouldBe BidDouble.minNormal
        }

        test("significand=1 at exponent −15 reconstructs epsilon") {
            FiniteDecimalFloatingPoint(false, -15, 1uL).toBidDouble() shouldBe BidDouble.epsilon
        }

        test("significand=9_999_999_999_999_999 at exponent 369 reconstructs maxValue") {
            FiniteDecimalFloatingPoint(false, 369, 9_999_999_999_999_999uL).toBidDouble() shouldBe BidDouble.maxValue
        }

        test("overflow biased exponent produces positive infinity") {
            FiniteDecimalFloatingPoint(false, 370, 9_999_999_999_999_999uL).toBidDouble() shouldBe BidDouble.positiveInfinity
        }

        test("overflow produces negative infinity for negative sign") {
            FiniteDecimalFloatingPoint(true, 370, 9_999_999_999_999_999uL).toBidDouble() shouldBe BidDouble.negativeInfinity
        }

        test("underflow produces positive zero") {
            FiniteDecimalFloatingPoint(false, -420, 1uL).toBidDouble() shouldBe BidDouble.positiveZero
        }

        test("17-digit significand is rounded to 16 digits") {
            // 1_234_567_890_123_456_7 rounded half-even → 1_234_567_890_123_457; exponent adjusted +1.
            val r = FiniteDecimalFloatingPoint(false, 0, 12_345_678_901_234_567uL).toBidDouble()
            r.significand shouldBe 1_234_567_890_123_457L
            r.biasedExponent shouldBe 399  // exponent=0+1=1; biasedExp=1+398=399
        }
    }

    // ── BidDouble round-trip ──────────────────────────────────────────────────

    context("BidDouble round-trip through FiniteDecimalFloatingPoint") {
        val cases = listOf(
            BidDouble.positiveZero,
            BidDouble.negativeZero,
            BidDouble.minValue,
            BidDouble.minNormal,
            BidDouble.epsilon,
            BidDouble.maxValue,
            BidDouble(0x31C0_0000_0000_0001L),  // 1 × 10^0
            BidDouble(0x31A0_0000_0000_000AL),  // 10 × 10^−1, cohort of 1 × 10^0
        )
        cases.forEach { v ->
            test("0x${v.bits.toULong().toString(16)} round-trips") {
                v.toRegularDecimalFloatingPoint().toBidDouble() shouldBe v
            }
        }
    }

    // ── DpdDouble → FiniteDecimalFloatingPoint ────────────────────────────────

    context("DpdDouble.toRegularDecimalFloatingPoint") {
        test("+0 produces zero significand at exponent −398") {
            val r = DpdDouble.positiveZero.toRegularDecimalFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -398
            r.significand shouldBe 0uL
        }

        test("-0 sets the sign flag") {
            val r = DpdDouble.negativeZero.toRegularDecimalFloatingPoint()
            r.sign shouldBe true
            r.significand shouldBe 0uL
        }

        test("minValue encodes as significand=1 at exponent −398") {
            val r = DpdDouble.minValue.toRegularDecimalFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -398
            r.significand shouldBe 1uL
        }

        test("minNormal encodes as significand=10^15 at exponent −398") {
            val r = DpdDouble.minNormal.toRegularDecimalFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -398
            r.significand shouldBe 1_000_000_000_000_000uL
        }

        test("epsilon encodes as significand=1 at exponent −15") {
            val r = DpdDouble.epsilon.toRegularDecimalFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -15
            r.significand shouldBe 1uL
        }

        test("maxValue encodes as significand=9_999_999_999_999_999 at exponent 369") {
            val r = DpdDouble.maxValue.toRegularDecimalFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe 369
            r.significand shouldBe 9_999_999_999_999_999uL
        }

        test("throws for NaN") {
            shouldThrow<IllegalArgumentException> { DpdDouble.NaN.toRegularDecimalFloatingPoint() }
        }

        test("throws for positive infinity") {
            shouldThrow<IllegalArgumentException> { DpdDouble.positiveInfinity.toRegularDecimalFloatingPoint() }
        }

        test("throws for negative infinity") {
            shouldThrow<IllegalArgumentException> { DpdDouble.negativeInfinity.toRegularDecimalFloatingPoint() }
        }
    }

    // ── FiniteDecimalFloatingPoint<ULong> → DpdDouble ─────────────────────────

    context("FiniteDecimalFloatingPoint<ULong>.toDpdDouble") {
        test("+0 returns positiveZero") {
            FiniteDecimalFloatingPoint(false, -398, 0uL).toDpdDouble() shouldBe DpdDouble.positiveZero
        }

        test("-0 returns negativeZero") {
            FiniteDecimalFloatingPoint(true, -398, 0uL).toDpdDouble() shouldBe DpdDouble.negativeZero
        }

        test("significand=1 at exponent −398 reconstructs minValue") {
            FiniteDecimalFloatingPoint(false, -398, 1uL).toDpdDouble() shouldBe DpdDouble.minValue
        }

        test("significand=10^15 at exponent −398 reconstructs minNormal") {
            FiniteDecimalFloatingPoint(false, -398, 1_000_000_000_000_000uL).toDpdDouble() shouldBe DpdDouble.minNormal
        }

        test("significand=1 at exponent −15 reconstructs epsilon") {
            FiniteDecimalFloatingPoint(false, -15, 1uL).toDpdDouble() shouldBe DpdDouble.epsilon
        }

        test("significand=9_999_999_999_999_999 at exponent 369 reconstructs maxValue") {
            FiniteDecimalFloatingPoint(false, 369, 9_999_999_999_999_999uL).toDpdDouble() shouldBe DpdDouble.maxValue
        }

        test("overflow biased exponent produces positive infinity") {
            FiniteDecimalFloatingPoint(false, 370, 9_999_999_999_999_999uL).toDpdDouble() shouldBe DpdDouble.positiveInfinity
        }

        test("underflow produces positive zero") {
            FiniteDecimalFloatingPoint(false, -420, 1uL).toDpdDouble() shouldBe DpdDouble.positiveZero
        }

        test("17-digit significand is rounded to 16 digits") {
            val r = FiniteDecimalFloatingPoint(false, 0, 12_345_678_901_234_567uL).toDpdDouble()
            r.significand shouldBe 1_234_567_890_123_457L
            r.biasedExponent shouldBe 399
        }
    }

    // ── DpdDouble round-trip ──────────────────────────────────────────────────

    context("DpdDouble round-trip through FiniteDecimalFloatingPoint") {
        val cases = listOf(
            DpdDouble.positiveZero,
            DpdDouble.negativeZero,
            DpdDouble.minValue,
            DpdDouble.minNormal,
            DpdDouble.epsilon,
            DpdDouble.maxValue,
            DpdDouble(0x31C0_0000_0000_0001L),  // 1 × 10^0
            DpdDouble(0x31C0_0000_0000_0003L),  // 3 × 10^0
        )
        cases.forEach { v ->
            test("0x${v.bits.toULong().toString(16)} round-trips") {
                v.toRegularDecimalFloatingPoint().toDpdDouble() shouldBe v
            }
        }
    }

    // ── BID↔DPD cross-encoding round-trip ────────────────────────────────────

    context("BID→FDP→DPD and DPD→FDP→BID round-trips via bidDpdDouble") {
        test("minValue BID→DPD→BID") {
            val fdp = BidDouble.minValue.toRegularDecimalFloatingPoint()
            fdp.toDpdDouble().toRegularDecimalFloatingPoint().toBidDouble() shouldBe BidDouble.minValue
        }
        test("maxValue DPD→BID→DPD") {
            val fdp = DpdDouble.maxValue.toRegularDecimalFloatingPoint()
            fdp.toBidDouble().toRegularDecimalFloatingPoint().toDpdDouble() shouldBe DpdDouble.maxValue
        }
    }
})
