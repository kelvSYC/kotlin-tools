package com.kelvsyc.kotlin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class BidDoubleTest : FunSpec({

    // Bit patterns used throughout this file.
    //
    // Case 1 (combination < 0x1800): biasedExp at combination[12:3], sig at combination[2:0]++continuation.
    //   0x31C0_0000_0000_0001L — +1 × 10^0   (biasedExp=398, sig=1)
    //   0xB1C0_0000_0000_0001L — −1 × 10^0   (sign set)
    //   0x31C0_0000_0000_0002L — +2 × 10^0
    //   0x31A0_0000_0000_000AL — +10 × 10^−1 (biasedExp=397, sig=10; cohort of 1.0)
    //   0x0000_0000_0000_0000L — +0 (biasedExp=0, sig=0)
    //   Long.MIN_VALUE        — −0
    //
    // Case 2 (combination[12:11] = 11, bit 10 = 0): large-significand encoding.
    //   maxValue = 0x77FB_86F2_6FC0_FFFFL — +9999999999999999 × 10^369
    //
    // Special values (combination[12:8] = 11110 for ∞, 11111 for NaN):
    //   0x7800_0000_0000_0000L — +infinity
    //   Long.MIN_VALUE or 0x7800_0000_0000_0000L — −infinity
    //   0x7E00_0000_0000_0000L — NaN

    // ── Bit-field accessors ───────────────────────────────────────────────────

    context("sign") {
        test("positive value has false sign") {
            BidDouble(0x31C0_0000_0000_0001L).sign shouldBe false
        }
        test("negative value has true sign") {
            BidDouble(0xB1C0_0000_0000_0001uL.toLong()).sign shouldBe true
        }
        test("+0 has false sign") {
            BidDouble(0L).sign shouldBe false
        }
        test("-0 has true sign") {
            BidDouble(Long.MIN_VALUE).sign shouldBe true
        }
    }

    context("combination") {
        test("1.0 has combination 0xC70 (biasedExp=398, leading-digit=0)") {
            BidDouble(0x31C0_0000_0000_0001L).combination shouldBe 0xC70
        }
        test("maxValue has combination with top two bits set (large-sig encoding)") {
            BidDouble.maxValue.combination ushr 11 shouldBe 3
        }
        test("NaN has top five combination bits all set") {
            BidDouble.NaN.combination ushr 8 shouldBe 0x1F
        }
        test("infinity has top five combination bits equal to 11110") {
            BidDouble.positiveInfinity.combination ushr 8 shouldBe 0x1E
        }
    }

    context("continuation") {
        test("1.0 has continuation equal to 1") {
            BidDouble(0x31C0_0000_0000_0001L).continuation shouldBe 1L
        }
        test("+0 has zero continuation") {
            BidDouble(0L).continuation shouldBe 0L
        }
    }

    context("biasedExponent") {
        test("1.0 has biased exponent 398") {
            BidDouble(0x31C0_0000_0000_0001L).biasedExponent shouldBe 398
        }
        test("10×10^−1 has biased exponent 397") {
            BidDouble(0x31A0_0000_0000_000AL).biasedExponent shouldBe 397
        }
        test("maxValue has biased exponent 767") {
            BidDouble.maxValue.biasedExponent shouldBe 767
        }
        test("minValue has biased exponent 0") {
            BidDouble.minValue.biasedExponent shouldBe 0
        }
    }

    context("significand") {
        test("1.0 has significand 1 (normal encoding)") {
            BidDouble(0x31C0_0000_0000_0001L).significand shouldBe 1L
        }
        test("10×10^−1 has significand 10") {
            BidDouble(0x31A0_0000_0000_000AL).significand shouldBe 10L
        }
        test("minNormal has significand 1_000_000_000_000_000") {
            BidDouble.minNormal.significand shouldBe 1_000_000_000_000_000L
        }
        test("maxValue has significand 9_999_999_999_999_999 (large-sig encoding)") {
            BidDouble.maxValue.significand shouldBe 9_999_999_999_999_999L
        }
    }

    // ── Classification ────────────────────────────────────────────────────────

    context("isNaN") {
        test("NaN is NaN") { BidDouble.NaN.isNaN() shouldBe true }
        test("1.0 is not NaN") { BidDouble(0x31C0_0000_0000_0001L).isNaN() shouldBe false }
        test("+∞ is not NaN") { BidDouble.positiveInfinity.isNaN() shouldBe false }
    }

    context("isInfinite") {
        test("+∞ is infinite") { BidDouble.positiveInfinity.isInfinite() shouldBe true }
        test("-∞ is infinite") { BidDouble.negativeInfinity.isInfinite() shouldBe true }
        test("NaN is not infinite") { BidDouble.NaN.isInfinite() shouldBe false }
        test("1.0 is not infinite") { BidDouble(0x31C0_0000_0000_0001L).isInfinite() shouldBe false }
    }

    context("isZero") {
        test("+0 is zero") { BidDouble.positiveZero.isZero() shouldBe true }
        test("-0 is zero") { BidDouble.negativeZero.isZero() shouldBe true }
        test("1.0 is not zero") { BidDouble(0x31C0_0000_0000_0001L).isZero() shouldBe false }
        test("NaN is not zero") { BidDouble.NaN.isZero() shouldBe false }
    }

    context("isNormal") {
        test("1.0 is normal") { BidDouble(0x31C0_0000_0000_0001L).isNormal() shouldBe true }
        test("minNormal is normal") { BidDouble.minNormal.isNormal() shouldBe true }
        test("minValue (subnormal) is not normal") { BidDouble.minValue.isNormal() shouldBe false }
        test("+0 is not normal") { BidDouble.positiveZero.isNormal() shouldBe false }
    }

    context("isSubnormal") {
        test("minValue is subnormal") { BidDouble.minValue.isSubnormal() shouldBe true }
        test("minNormal is not subnormal") { BidDouble.minNormal.isSubnormal() shouldBe false }
        test("1.0 is not subnormal") { BidDouble(0x31C0_0000_0000_0001L).isSubnormal() shouldBe false }
        test("+0 is not subnormal") { BidDouble.positiveZero.isSubnormal() shouldBe false }
    }

    // ── Constants ─────────────────────────────────────────────────────────────

    context("constants") {
        test("NaN isNaN") { BidDouble.NaN.isNaN() shouldBe true }
        test("positiveInfinity isInfinite and positive") {
            BidDouble.positiveInfinity.isInfinite() shouldBe true
            BidDouble.positiveInfinity.sign shouldBe false
        }
        test("negativeInfinity isInfinite and negative") {
            BidDouble.negativeInfinity.isInfinite() shouldBe true
            BidDouble.negativeInfinity.sign shouldBe true
        }
        test("positiveZero is zero with false sign") {
            BidDouble.positiveZero.isZero() shouldBe true
            BidDouble.positiveZero.sign shouldBe false
        }
        test("negativeZero is zero with true sign") {
            BidDouble.negativeZero.isZero() shouldBe true
            BidDouble.negativeZero.sign shouldBe true
        }
        test("maxValue has biasedExp=767 and sig=9_999_999_999_999_999") {
            BidDouble.maxValue.biasedExponent shouldBe 767
            BidDouble.maxValue.significand shouldBe 9_999_999_999_999_999L
        }
        test("minValue has biasedExp=0 and sig=1") {
            BidDouble.minValue.biasedExponent shouldBe 0
            BidDouble.minValue.significand shouldBe 1L
        }
        test("minNormal has biasedExp=0 and sig=10^15") {
            BidDouble.minNormal.biasedExponent shouldBe 0
            BidDouble.minNormal.significand shouldBe 1_000_000_000_000_000L
        }
        test("epsilon has biasedExp=383 and sig=1") {
            BidDouble.epsilon.biasedExponent shouldBe 383
            BidDouble.epsilon.significand shouldBe 1L
        }
    }

    // ── Equivalence equality ──────────────────────────────────────────────────

    context("equivalenceEquality") {
        val eq = BidDouble.equivalenceEquality
        test("same bit pattern is equal") {
            with(eq) {
                BidDouble(0x31C0_0000_0000_0001L).isEqualTo(BidDouble(0x31C0_0000_0000_0001L)) shouldBe true
            }
        }
        test("cohort-distinct values are not equivalent") {
            // 1×10^0 ≠ 10×10^−1 by bit pattern
            with(eq) {
                BidDouble(0x31C0_0000_0000_0001L).isEqualTo(BidDouble(0x31A0_0000_0000_000AL)) shouldBe false
            }
        }
        test("all NaN values are equivalent") {
            with(eq) {
                BidDouble.NaN.isEqualTo(BidDouble(0x7C00_0000_0000_0001L)) shouldBe true
            }
        }
        test("+0 and −0 are not equivalent") {
            with(eq) {
                BidDouble.positiveZero.isEqualTo(BidDouble.negativeZero) shouldBe false
            }
        }
    }

    // ── Numerical equality ────────────────────────────────────────────────────

    context("numericalEquality") {
        val eq = BidDouble.numericalEquality
        test("cohort-distinct representations are numerically equal") {
            with(eq) {
                BidDouble(0x31C0_0000_0000_0001L).isEqualTo(BidDouble(0x31A0_0000_0000_000AL)) shouldBe true
            }
        }
        test("+0 and −0 are numerically equal") {
            with(eq) { BidDouble.positiveZero.isEqualTo(BidDouble.negativeZero) shouldBe true }
        }
        test("NaN is not equal to itself") {
            with(eq) { BidDouble.NaN.isEqualTo(BidDouble.NaN) shouldBe false }
        }
        test("different values are not equal") {
            with(eq) {
                BidDouble(0x31C0_0000_0000_0001L).isEqualTo(BidDouble(0x31C0_0000_0000_0002L)) shouldBe false
            }
        }
    }

    // ── Total ordering ────────────────────────────────────────────────────────

    context("comparator") {
        val cmp = BidDouble.comparator
        test("NaN > everything") {
            cmp.compare(BidDouble.NaN, BidDouble.maxValue) shouldBe 1
        }
        test("+∞ > maxValue") {
            cmp.compare(BidDouble.positiveInfinity, BidDouble.maxValue) shouldBe 1
        }
        test("-∞ < everything finite") {
            cmp.compare(BidDouble.negativeInfinity, BidDouble.minValue) shouldBe -1
        }
        test("1.0 < 2.0") {
            cmp.compare(BidDouble(0x31C0_0000_0000_0001L), BidDouble(0x31C0_0000_0000_0002L)) shouldBe -1
        }
        test("cohort-distinct values compare as equal") {
            cmp.compare(BidDouble(0x31C0_0000_0000_0001L), BidDouble(0x31A0_0000_0000_000AL)) shouldBe 0
        }
        test("+0 is greater than −0 in total order (sign check before zero check)") {
            cmp.compare(BidDouble.positiveZero, BidDouble.negativeZero) shouldBe 1
        }
        test("−1.0 < +1.0") {
            cmp.compare(BidDouble(0xB1C0_0000_0000_0001uL.toLong()), BidDouble(0x31C0_0000_0000_0001L)) shouldBe -1
        }
    }

    // ── Partial ordering ──────────────────────────────────────────────────────

    context("partialComparator") {
        val cmp = BidDouble.partialComparator
        test("NaN compared to anything returns null") {
            cmp.compare(BidDouble.NaN, BidDouble.positiveZero) shouldBe null
        }
        test("NaN compared to NaN returns null") {
            cmp.compare(BidDouble.NaN, BidDouble.NaN) shouldBe null
        }
        test("finite values compare numerically") {
            cmp.compare(BidDouble(0x31C0_0000_0000_0001L), BidDouble(0x31C0_0000_0000_0002L)) shouldBe -1
        }
    }

    // ── Structural hash ───────────────────────────────────────────────────────

    context("hash") {
        test("all NaN values produce the same hash") {
            BidDouble.hash(BidDouble.NaN) shouldBe BidDouble.hash(BidDouble(0x7C00_0000_0000_0001L))
        }
        test("+0 and −0 produce different hashes") {
            BidDouble.hash(BidDouble.positiveZero) shouldNotBe BidDouble.hash(BidDouble.negativeZero)
        }
    }
})
