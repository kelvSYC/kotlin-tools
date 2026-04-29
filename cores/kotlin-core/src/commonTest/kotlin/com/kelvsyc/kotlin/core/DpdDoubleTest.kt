package com.kelvsyc.kotlin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class DpdDoubleTest : FunSpec({

    // Bit patterns used throughout this file.
    //
    // For decimal64 DPD, the 64-bit layout is:
    //   bit 63: sign
    //   bits 62..50: combination field (13 bits, G field)
    //   bits 49..0: continuation field (5 × 10-bit declets)
    //
    // Key patterns (normal/small-leading-digit encoding where biasedExp < 0x180):
    //   combination = (biasedExp shl 3) or leadingDigit
    //
    //   0x31C0_0000_0000_0001L — +1 × 10^0 (biasedExp=398, ld=0, d5=encodeDeclet(1)=1)
    //   0xB1C0_0000_0000_0001L — −1 × 10^0 (sign set)
    //   0x31C0_0000_0000_0003L — +3 × 10^0 (ld=0, d5=3)
    //   0x0000_0000_0000_0000L — +0
    //   Long.MIN_VALUE        — −0
    //
    // DPD special values (same combination-field encoding as BID):
    //   0x7800_0000_0000_0000L — +∞
    //   0x7E00_0000_0000_0000L — canonical NaN

    // ── Bit-field accessors ───────────────────────────────────────────────────

    context("sign") {
        test("positive value has false sign") {
            DpdDouble(0x31C0_0000_0000_0001L).sign shouldBe false
        }
        test("negative value has true sign") {
            DpdDouble(0xB1C0_0000_0000_0001uL.toLong()).sign shouldBe true
        }
        test("+0 has false sign") {
            DpdDouble(0L).sign shouldBe false
        }
        test("-0 has true sign") {
            DpdDouble(Long.MIN_VALUE).sign shouldBe true
        }
    }

    context("combination") {
        test("1×10^0 has combination 0xC70 (biasedExp=398, leadingDigit=0)") {
            DpdDouble(0x31C0_0000_0000_0001L).combination shouldBe 0xC70
        }
        test("maxValue has top two combination bits set (large-leading-digit encoding)") {
            DpdDouble.maxValue.combination ushr 11 shouldBe 3
        }
        test("NaN has top five combination bits all set") {
            DpdDouble.NaN.combination ushr 8 shouldBe 0x1F
        }
        test("infinity has top five combination bits equal to 11110") {
            DpdDouble.positiveInfinity.combination ushr 8 shouldBe 0x1E
        }
    }

    context("biasedExponent") {
        test("1×10^0 has biased exponent 398") {
            DpdDouble(0x31C0_0000_0000_0001L).biasedExponent shouldBe 398
        }
        test("maxValue has biased exponent 767") {
            DpdDouble.maxValue.biasedExponent shouldBe 767
        }
        test("minValue has biased exponent 0") {
            DpdDouble.minValue.biasedExponent shouldBe 0
        }
    }

    context("declets") {
        test("1×10^0 has declet5=1 and all other declets zero") {
            val v = DpdDouble(0x31C0_0000_0000_0001L)
            v.declet1 shouldBe 0
            v.declet2 shouldBe 0
            v.declet3 shouldBe 0
            v.declet4 shouldBe 0
            v.declet5 shouldBe 1
        }
        test("+0 has all declets zero") {
            val v = DpdDouble(0L)
            v.declet1 shouldBe 0
            v.declet5 shouldBe 0
        }
    }

    context("significand") {
        test("1×10^0 has significand 1") {
            DpdDouble(0x31C0_0000_0000_0001L).significand shouldBe 1L
        }
        test("3×10^0 has significand 3") {
            DpdDouble(0x31C0_0000_0000_0003L).significand shouldBe 3L
        }
        test("minNormal has significand 10^15") {
            DpdDouble.minNormal.significand shouldBe 1_000_000_000_000_000L
        }
        test("maxValue has significand 9_999_999_999_999_999") {
            DpdDouble.maxValue.significand shouldBe 9_999_999_999_999_999L
        }
    }

    // ── Classification ────────────────────────────────────────────────────────

    context("isNaN") {
        test("NaN is NaN") { DpdDouble.NaN.isNaN() shouldBe true }
        test("1×10^0 is not NaN") { DpdDouble(0x31C0_0000_0000_0001L).isNaN() shouldBe false }
        test("+∞ is not NaN") { DpdDouble.positiveInfinity.isNaN() shouldBe false }
    }

    context("isInfinite") {
        test("+∞ is infinite") { DpdDouble.positiveInfinity.isInfinite() shouldBe true }
        test("-∞ is infinite") { DpdDouble.negativeInfinity.isInfinite() shouldBe true }
        test("NaN is not infinite") { DpdDouble.NaN.isInfinite() shouldBe false }
        test("1×10^0 is not infinite") { DpdDouble(0x31C0_0000_0000_0001L).isInfinite() shouldBe false }
    }

    context("isZero") {
        test("+0 is zero") { DpdDouble.positiveZero.isZero() shouldBe true }
        test("-0 is zero") { DpdDouble.negativeZero.isZero() shouldBe true }
        test("1×10^0 is not zero") { DpdDouble(0x31C0_0000_0000_0001L).isZero() shouldBe false }
    }

    context("isNormal / isSubnormal") {
        test("1×10^0 is normal") { DpdDouble(0x31C0_0000_0000_0001L).isNormal() shouldBe true }
        test("minNormal is normal") { DpdDouble.minNormal.isNormal() shouldBe true }
        test("minValue is subnormal") { DpdDouble.minValue.isSubnormal() shouldBe true }
        test("minNormal is not subnormal") { DpdDouble.minNormal.isSubnormal() shouldBe false }
        test("+0 is not subnormal") { DpdDouble.positiveZero.isSubnormal() shouldBe false }
    }

    // ── Constants ─────────────────────────────────────────────────────────────

    context("constants") {
        test("maxValue has biasedExp=767 and sig=9_999_999_999_999_999") {
            DpdDouble.maxValue.biasedExponent shouldBe 767
            DpdDouble.maxValue.significand shouldBe 9_999_999_999_999_999L
        }
        test("minValue has biasedExp=0 and sig=1") {
            DpdDouble.minValue.biasedExponent shouldBe 0
            DpdDouble.minValue.significand shouldBe 1L
        }
        test("minNormal has biasedExp=0 and sig=10^15") {
            DpdDouble.minNormal.biasedExponent shouldBe 0
            DpdDouble.minNormal.significand shouldBe 1_000_000_000_000_000L
        }
        test("epsilon has biasedExp=383 and sig=1") {
            DpdDouble.epsilon.biasedExponent shouldBe 383
            DpdDouble.epsilon.significand shouldBe 1L
        }
    }

    // ── Equivalence equality ──────────────────────────────────────────────────

    context("equivalenceEquality") {
        val eq = DpdDouble.equivalenceEquality
        test("same bit pattern is equal") {
            with(eq) {
                DpdDouble(0x31C0_0000_0000_0001L).isEqualTo(DpdDouble(0x31C0_0000_0000_0001L)) shouldBe true
            }
        }
        test("all NaN values are equivalent") {
            with(eq) {
                DpdDouble.NaN.isEqualTo(DpdDouble(0x7C00_0000_0000_0001L)) shouldBe true
            }
        }
        test("+0 and −0 are not equivalent") {
            with(eq) { DpdDouble.positiveZero.isEqualTo(DpdDouble.negativeZero) shouldBe false }
        }
    }

    // ── Numerical equality ────────────────────────────────────────────────────

    context("numericalEquality") {
        val eq = DpdDouble.numericalEquality
        test("+0 and −0 are numerically equal") {
            with(eq) { DpdDouble.positiveZero.isEqualTo(DpdDouble.negativeZero) shouldBe true }
        }
        test("NaN is not equal to itself") {
            with(eq) { DpdDouble.NaN.isEqualTo(DpdDouble.NaN) shouldBe false }
        }
    }

    // ── Total ordering ────────────────────────────────────────────────────────

    context("comparator") {
        val cmp = DpdDouble.comparator
        test("NaN > everything") {
            cmp.compare(DpdDouble.NaN, DpdDouble.maxValue) shouldBe 1
        }
        test("+∞ > finite values") {
            cmp.compare(DpdDouble.positiveInfinity, DpdDouble.maxValue) shouldBe 1
        }
        test("-∞ < finite values") {
            cmp.compare(DpdDouble.negativeInfinity, DpdDouble.minValue) shouldBe -1
        }
        test("1 < 3") {
            cmp.compare(DpdDouble(0x31C0_0000_0000_0001L), DpdDouble(0x31C0_0000_0000_0003L)) shouldBe -1
        }
        test("+0 is greater than −0 in total order (sign check before zero check)") {
            cmp.compare(DpdDouble.positiveZero, DpdDouble.negativeZero) shouldBe 1
        }
    }

    // ── Sign trait ────────────────────────────────────────────────────────────

    context("sign trait") {
        val signTrait = DpdDouble.sign
        test("negate flips sign bit") {
            with(signTrait) {
                DpdDouble(0x31C0_0000_0000_0001L).negate().sign shouldBe true
            }
        }
        test("abs clears sign bit") {
            with(signTrait) {
                DpdDouble(0xB1C0_0000_0000_0001uL.toLong()).abs().sign shouldBe false
            }
        }
        test("isNegative matches sign") {
            with(signTrait) {
                DpdDouble(0xB1C0_0000_0000_0001uL.toLong()).isNegative() shouldBe true
                DpdDouble(0x31C0_0000_0000_0001L).isNegative() shouldBe false
            }
        }
    }

    // ── Hash ──────────────────────────────────────────────────────────────────

    context("hash") {
        test("all NaN values produce the same hash") {
            DpdDouble.hash(DpdDouble.NaN) shouldBe DpdDouble.hash(DpdDouble(0x7C00_0000_0000_0001L))
        }
        test("+0 and −0 produce different hashes") {
            DpdDouble.hash(DpdDouble.positiveZero) shouldNotBe DpdDouble.hash(DpdDouble.negativeZero)
        }
    }
})
