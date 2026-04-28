package com.kelvsyc.kotlin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class DpdFloatTest : FunSpec({

    // Bit patterns used throughout this file.
    //
    // All patterns use the same combination/continuation field layout as BidFloat. For small
    // significands (≤ 7 decimal digits, leading digit ≤ 7) the DPD and BID bit patterns often
    // coincide because single-digit declets are equal to their BCD counterparts.
    //
    // Case 1 (combination[10:9] ≠ 11, normal encoding):
    //   0x32800001 — +1 × 10^0   (biasedExp=101, leadingDigit=0, declet2=encodeDeclet(001)=1)
    //   0xB2800001 — −1 × 10^0   (same with sign bit set)
    //   0x32800002 — +2 × 10^0   (declet2=encodeDeclet(002)=2)
    //   0x32000010 — +10 × 10^−1 (biasedExp=100, declet2=encodeDeclet(010)=0x10=16; cohort of 1.0)
    //   0x32800000 — +0 with biasedExp=101
    //   0x00000000 — +0 (biasedExp=0)
    //   0x80000000 — −0
    //
    // Case 2 (combination[10:9] = 11, large-significand encoding; leadingDigit ∈ {8,9}):
    //   0x6CBE7F9F — +9,999,999 × 10^0 (biasedExp=101, leadingDigit=9, declet1=declet2=0x39F)
    //   0x6CA00000 — +8,000,000 × 10^0 (biasedExp=101, leadingDigit=8, declet1=declet2=0)
    //
    // Special values (combination[10:6]=11110 for Inf, 11111 for NaN):
    //   0x78000000 — +infinity
    //   0xF8000000 — −infinity
    //   0x7C000000 — signalling NaN (G[5]=0)
    //   0x7E000000 — quiet NaN     (G[5]=1; used by hash and the NaN constant)
    //   0x7E000001 — quiet NaN with non-zero payload

    // ── Bit-field accessors ───────────────────────────────────────────────────

    context("sign") {
        test("positive value has false sign") {
            DpdFloat(0x32800001).sign shouldBe false
        }
        test("negative value has true sign") {
            DpdFloat(0xB2800001.toInt()).sign shouldBe true
        }
        test("+0 has false sign") {
            DpdFloat(0x00000000).sign shouldBe false
        }
        test("-0 has true sign") {
            DpdFloat(0x80000000.toInt()).sign shouldBe true
        }
    }

    context("combination") {
        test("1.0 has expected combination field") {
            // bits[30:20] of 0x32800001 = 0x328
            DpdFloat(0x32800001).combination shouldBe 0x328
        }
        test("large-sig value has top two combination bits set") {
            DpdFloat(0x6CBE7F9F).combination ushr 9 shouldBe 3
        }
        test("NaN has top five combination bits all set") {
            DpdFloat(0x7C000000).combination ushr 6 shouldBe 0x1F
        }
        test("infinity has top five combination bits equal to 11110") {
            DpdFloat(0x78000000).combination ushr 6 shouldBe 0x1E
        }
    }

    context("continuation") {
        test("1.0 has continuation equal to declet2=1") {
            DpdFloat(0x32800001).continuation shouldBe 1
        }
        test("10 × 10^-1 has continuation of 0x10 (declet2=encodeDeclet(010))") {
            DpdFloat(0x32000010).continuation shouldBe 0x10
        }
        test("+0 has zero continuation") {
            DpdFloat(0x00000000).continuation shouldBe 0
        }
    }

    context("biasedExponent") {
        test("1.0 has biased exponent 101") {
            DpdFloat(0x32800001).biasedExponent shouldBe 101
        }
        test("10 × 10^-1 has biased exponent 100") {
            DpdFloat(0x32000010).biasedExponent shouldBe 100
        }
        test("large-sig value 9999999 × 10^0 has biased exponent 101") {
            DpdFloat(0x6CBE7F9F).biasedExponent shouldBe 101
        }
        test("large-sig boundary 8000000 × 10^0 has biased exponent 101") {
            DpdFloat(0x6CA00000).biasedExponent shouldBe 101
        }
        test("+0 with biasedExp=101 decodes correctly") {
            DpdFloat(0x32800000).biasedExponent shouldBe 101
        }
    }

    context("leadingDigit") {
        test("1.0 has leading digit 0") {
            DpdFloat(0x32800001).leadingDigit shouldBe 0
        }
        test("9999999 × 10^0 has leading digit 9") {
            DpdFloat(0x6CBE7F9F).leadingDigit shouldBe 9
        }
        test("8000000 × 10^0 has leading digit 8") {
            DpdFloat(0x6CA00000).leadingDigit shouldBe 8
        }
        test("leading digit is independent of sign") {
            DpdFloat(0x32800001).leadingDigit shouldBe DpdFloat(0xB2800001.toInt()).leadingDigit
        }
    }

    context("declet1 and declet2") {
        test("1.0 has declet1=0 and declet2=1") {
            DpdFloat(0x32800001).declet1 shouldBe 0
            DpdFloat(0x32800001).declet2 shouldBe 1
        }
        test("9999999 × 10^0 has declet1=declet2=0x39F (encodes 999)") {
            DpdFloat(0x6CBE7F9F).declet1 shouldBe 0x39F
            DpdFloat(0x6CBE7F9F).declet2 shouldBe 0x39F
        }
        test("8000000 × 10^0 has declet1=declet2=0 (encodes 000)") {
            DpdFloat(0x6CA00000).declet1 shouldBe 0
            DpdFloat(0x6CA00000).declet2 shouldBe 0
        }
    }

    context("significand") {
        test("1.0 has significand 1") {
            DpdFloat(0x32800001).significand shouldBe 1
        }
        test("2.0 has significand 2") {
            DpdFloat(0x32800002).significand shouldBe 2
        }
        test("10 × 10^-1 has significand 10") {
            DpdFloat(0x32000010).significand shouldBe 10
        }
        test("large-sig: 9999999 × 10^0 has significand 9999999") {
            DpdFloat(0x6CBE7F9F).significand shouldBe 9999999
        }
        test("large-sig boundary: 8000000 × 10^0 has significand 8000000") {
            DpdFloat(0x6CA00000).significand shouldBe 8000000
        }
        test("significand is independent of sign") {
            DpdFloat(0x32800001).significand shouldBe DpdFloat(0xB2800001.toInt()).significand
        }
    }

    // ── Classification ────────────────────────────────────────────────────────

    context("isNaN") {
        test("quiet NaN is NaN") {
            DpdFloat(0x7E000000).isNaN() shouldBe true
        }
        test("signalling NaN is NaN") {
            DpdFloat(0x7C000000).isNaN() shouldBe true
        }
        test("NaN with non-zero payload is NaN") {
            DpdFloat(0x7E000001).isNaN() shouldBe true
        }
        test("negative NaN is NaN") {
            DpdFloat(0xFE000000.toInt()).isNaN() shouldBe true
        }
        test("positive infinity is not NaN") {
            DpdFloat(0x78000000).isNaN() shouldBe false
        }
        test("finite value is not NaN") {
            DpdFloat(0x32800001).isNaN() shouldBe false
        }
        test("+0 is not NaN") {
            DpdFloat(0x00000000).isNaN() shouldBe false
        }
    }

    context("isInfinite") {
        test("+infinity is infinite") {
            DpdFloat(0x78000000).isInfinite() shouldBe true
        }
        test("-infinity is infinite") {
            DpdFloat(0xF8000000.toInt()).isInfinite() shouldBe true
        }
        test("NaN is not infinite") {
            DpdFloat(0x7E000000).isInfinite() shouldBe false
        }
        test("finite value is not infinite") {
            DpdFloat(0x32800001).isInfinite() shouldBe false
        }
        test("+0 is not infinite") {
            DpdFloat(0x00000000).isInfinite() shouldBe false
        }
    }

    context("isZero") {
        test("+0 (biasedExp=0) is zero") {
            DpdFloat(0x00000000).isZero() shouldBe true
        }
        test("-0 is zero") {
            DpdFloat(0x80000000.toInt()).isZero() shouldBe true
        }
        test("+0 with biasedExp=101 is zero") {
            DpdFloat(0x32800000).isZero() shouldBe true
        }
        test("1.0 is not zero") {
            DpdFloat(0x32800001).isZero() shouldBe false
        }
        test("NaN is not zero") {
            DpdFloat(0x7E000000).isZero() shouldBe false
        }
        test("infinity is not zero") {
            DpdFloat(0x78000000).isZero() shouldBe false
        }
    }

    context("isNormal") {
        test("minNormal is normal") {
            DpdFloat.minNormal.isNormal() shouldBe true
        }
        test("maxValue is normal") {
            DpdFloat.maxValue.isNormal() shouldBe true
        }
        test("1.0 is normal") {
            DpdFloat(0x32800001).isNormal() shouldBe true
        }
        test("minValue (subnormal) is not normal") {
            DpdFloat.minValue.isNormal() shouldBe false
        }
        test("+0 is not normal") {
            DpdFloat.positiveZero.isNormal() shouldBe false
        }
        test("NaN is not normal") {
            DpdFloat.NaN.isNormal() shouldBe false
        }
        test("infinity is not normal") {
            DpdFloat.positiveInfinity.isNormal() shouldBe false
        }
    }

    context("isSubnormal") {
        test("minValue is subnormal") {
            DpdFloat.minValue.isSubnormal() shouldBe true
        }
        test("minNormal is not subnormal") {
            DpdFloat.minNormal.isSubnormal() shouldBe false
        }
        test("1.0 is not subnormal") {
            DpdFloat(0x32800001).isSubnormal() shouldBe false
        }
        test("+0 is not subnormal") {
            DpdFloat.positiveZero.isSubnormal() shouldBe false
        }
        test("NaN is not subnormal") {
            DpdFloat.NaN.isSubnormal() shouldBe false
        }
        test("infinity is not subnormal") {
            DpdFloat.positiveInfinity.isSubnormal() shouldBe false
        }
    }

    // ── Equivalence equality and hashing ─────────────────────────────────────

    context("equalTo") {
        test("same bit pattern is equal to itself") {
            DpdFloat.equalTo(DpdFloat(0x32800001), DpdFloat(0x32800001)) shouldBe true
        }
        test("different values are not equal") {
            DpdFloat.equalTo(DpdFloat(0x32800001), DpdFloat(0x32800002)) shouldBe false
        }
        test("cohort-distinct representations of 1.0 are not equal") {
            // 1×10^0 (0x32800001) and 10×10^-1 (0x32000010) are numerically equal but different bit patterns
            DpdFloat.equalTo(DpdFloat(0x32800001), DpdFloat(0x32000010)) shouldBe false
        }
        test("+0 and -0 are not equal") {
            DpdFloat.equalTo(DpdFloat(0x00000000), DpdFloat(0x80000000.toInt())) shouldBe false
        }
        test("NaN equals NaN regardless of payload") {
            DpdFloat.equalTo(DpdFloat(0x7E000000), DpdFloat(0x7E000001)) shouldBe true
        }
        test("NaN equals NaN with different sign bit") {
            DpdFloat.equalTo(DpdFloat(0x7E000000), DpdFloat(0xFE000000.toInt())) shouldBe true
        }
        test("NaN is not equal to a finite value") {
            DpdFloat.equalTo(DpdFloat(0x7E000000), DpdFloat(0x32800001)) shouldBe false
        }
    }

    context("hash") {
        test("all NaN payloads hash to the same value") {
            DpdFloat.hash(DpdFloat(0x7E000000)) shouldBe DpdFloat.hash(DpdFloat(0x7E000001))
            DpdFloat.hash(DpdFloat(0x7E000000)) shouldBe DpdFloat.hash(DpdFloat(0xFE000000.toInt()))
        }
        test("+0 and -0 hash to different values") {
            DpdFloat.hash(DpdFloat(0x00000000)) shouldNotBe DpdFloat.hash(DpdFloat(0x80000000.toInt()))
        }
        test("cohort-distinct representations hash differently") {
            DpdFloat.hash(DpdFloat(0x32800001)) shouldNotBe DpdFloat.hash(DpdFloat(0x32000010))
        }
        test("hash is consistent with equalTo: equal values have equal hashes") {
            val nan1 = DpdFloat(0x7E000000)
            val nan2 = DpdFloat(0x7E000001)
            DpdFloat.equalTo(nan1, nan2) shouldBe true
            DpdFloat.hash(nan1) shouldBe DpdFloat.hash(nan2)
        }
    }

    // ── Constants ─────────────────────────────────────────────────────────────
    //
    // Bit patterns for the companion constants (DPD-specific, differ from BidFloat where noted):
    //   NaN              0x7E000000 — G[0..4]=11111, G[5]=1 (quiet), no payload, sign=0
    //   positiveInfinity 0x78000000 — G[0..4]=11110, sign=0
    //   negativeInfinity 0xF8000000 — G[0..4]=11110, sign=1
    //   maxValue         0x77FE7F9F — large-sig: biasedExp=191, leadingDigit=9, declet1=declet2=0x39F
    //   minValue         0x00000001 — biasedExp=0, leadingDigit=0, declet2=encodeDeclet(001)=1
    //   minNormal        0x00100000 — biasedExp=0, leadingDigit=1, declet1=declet2=0
    //   epsilon          0x2F800001 — biasedExp=95, leadingDigit=0, declet2=1

    context("constants") {
        context("NaN") {
            test("is classified as NaN") {
                DpdFloat.NaN.isNaN() shouldBe true
            }
            test("has positive sign bit") {
                DpdFloat.NaN.sign shouldBe false
            }
        }

        context("positiveInfinity") {
            test("is classified as infinite") {
                DpdFloat.positiveInfinity.isInfinite() shouldBe true
            }
            test("is not NaN") {
                DpdFloat.positiveInfinity.isNaN() shouldBe false
            }
            test("has positive sign bit") {
                DpdFloat.positiveInfinity.sign shouldBe false
            }
        }

        context("negativeInfinity") {
            test("is classified as infinite") {
                DpdFloat.negativeInfinity.isInfinite() shouldBe true
            }
            test("is not NaN") {
                DpdFloat.negativeInfinity.isNaN() shouldBe false
            }
            test("has negative sign bit") {
                DpdFloat.negativeInfinity.sign shouldBe true
            }
        }

        context("maxValue") {
            test("is finite") {
                DpdFloat.maxValue.isNaN() shouldBe false
                DpdFloat.maxValue.isInfinite() shouldBe false
            }
            test("has significand 9999999") {
                DpdFloat.maxValue.significand shouldBe 9999999
            }
            test("has biased exponent 191 (unbiased 90)") {
                DpdFloat.maxValue.biasedExponent shouldBe 191
            }
            test("uses large-sig encoding") {
                DpdFloat.maxValue.combination ushr 9 shouldBe 3
            }
        }

        context("minValue") {
            test("is finite and non-zero") {
                DpdFloat.minValue.isNaN() shouldBe false
                DpdFloat.minValue.isInfinite() shouldBe false
                DpdFloat.minValue.isZero() shouldBe false
            }
            test("has significand 1") {
                DpdFloat.minValue.significand shouldBe 1
            }
            test("has minimum biased exponent 0 (unbiased -101)") {
                DpdFloat.minValue.biasedExponent shouldBe 0
            }
            test("is subnormal") {
                DpdFloat.minValue.isSubnormal() shouldBe true
            }
        }

        context("minNormal") {
            test("is finite and non-zero") {
                DpdFloat.minNormal.isNaN() shouldBe false
                DpdFloat.minNormal.isInfinite() shouldBe false
                DpdFloat.minNormal.isZero() shouldBe false
            }
            test("has significand 1000000 (= 10^(p-1), the minimum normal significand)") {
                DpdFloat.minNormal.significand shouldBe 1000000
            }
            test("has minimum biased exponent 0") {
                DpdFloat.minNormal.biasedExponent shouldBe 0
            }
            test("is normal") {
                DpdFloat.minNormal.isNormal() shouldBe true
            }
            test("is strictly greater than minValue") {
                DpdFloat.comparator.compare(DpdFloat.minNormal, DpdFloat.minValue) shouldBeGreaterThan 0
            }
        }

        context("epsilon") {
            test("is finite and non-zero") {
                DpdFloat.epsilon.isNaN() shouldBe false
                DpdFloat.epsilon.isInfinite() shouldBe false
                DpdFloat.epsilon.isZero() shouldBe false
            }
            test("has significand 1") {
                DpdFloat.epsilon.significand shouldBe 1
            }
            test("has biased exponent 95 (unbiased -6, so value = 10^-6)") {
                DpdFloat.epsilon.biasedExponent shouldBe 95
            }
            test("is strictly less than maxValue") {
                DpdFloat.comparator.compare(DpdFloat.epsilon, DpdFloat.maxValue) shouldBeLessThan 0
            }
        }
    }

    // ── Total ordering (comparator) ───────────────────────────────────────────
    //
    // Additional patterns:
    //   0x36000001 — +1 × 10^7  (biasedExp=108, leadingDigit=0, declet2=1; fast-path: diff=7)
    //   0x32800015 — +15 × 10^0 (biasedExp=101, declet2=encodeDeclet(015)=0x15=21)
    //   0x33000002 — +2 × 10^1  (biasedExp=102, declet2=2; 2×10^1=20 > 15)
    //   0x32800025 — +25 × 10^0 (biasedExp=101, declet2=encodeDeclet(025)=0x25=37; 25 > 20)
    //   0xB2800002 — −2 × 10^0

    context("comparator") {
        val cmp = DpdFloat.comparator

        context("NaN ordering") {
            test("NaN compares equal to NaN in total order") {
                cmp.compare(DpdFloat(0x7E000000), DpdFloat(0x7C000000)) shouldBe 0
            }
            test("NaN is ordered after positive infinity") {
                cmp.compare(DpdFloat(0x7E000000), DpdFloat(0x78000000)) shouldBeGreaterThan 0
            }
            test("NaN is ordered after finite values") {
                cmp.compare(DpdFloat(0x7E000000), DpdFloat(0x32800001)) shouldBeGreaterThan 0
            }
            test("finite value is ordered before NaN") {
                cmp.compare(DpdFloat(0x32800001), DpdFloat(0x7E000000)) shouldBeLessThan 0
            }
        }

        context("infinity ordering") {
            test("+infinity is ordered after finite values") {
                cmp.compare(DpdFloat(0x78000000), DpdFloat(0x32800001)) shouldBeGreaterThan 0
            }
            test("-infinity is ordered before finite values") {
                cmp.compare(DpdFloat(0xF8000000.toInt()), DpdFloat(0x32800001)) shouldBeLessThan 0
            }
            test("+infinity is ordered after -infinity") {
                cmp.compare(DpdFloat(0x78000000), DpdFloat(0xF8000000.toInt())) shouldBeGreaterThan 0
            }
            test("+infinity compares equal to itself") {
                cmp.compare(DpdFloat(0x78000000), DpdFloat(0x78000000)) shouldBe 0
            }
        }

        context("zero ordering") {
            test("-0 is strictly less than +0") {
                cmp.compare(DpdFloat(0x80000000.toInt()), DpdFloat(0x00000000)) shouldBeLessThan 0
            }
            test("+0 is strictly greater than -0") {
                cmp.compare(DpdFloat(0x00000000), DpdFloat(0x80000000.toInt())) shouldBeGreaterThan 0
            }
            test("+0 with different biased exponents compare equal") {
                cmp.compare(DpdFloat(0x00000000), DpdFloat(0x32800000)) shouldBe 0
            }
        }

        context("sign ordering") {
            test("positive value is greater than negative value") {
                cmp.compare(DpdFloat(0x32800001), DpdFloat(0xB2800001.toInt())) shouldBeGreaterThan 0
            }
            test("negative value is less than positive value") {
                cmp.compare(DpdFloat(0xB2800001.toInt()), DpdFloat(0x32800001)) shouldBeLessThan 0
            }
        }

        context("finite value ordering") {
            test("+1 < +2 (same exponent, significand differs)") {
                cmp.compare(DpdFloat(0x32800001), DpdFloat(0x32800002)) shouldBeLessThan 0
            }
            test("+2 > +1") {
                cmp.compare(DpdFloat(0x32800002), DpdFloat(0x32800001)) shouldBeGreaterThan 0
            }
            test("-1 > -2 (magnitudes reversed by sign)") {
                cmp.compare(DpdFloat(0xB2800001.toInt()), DpdFloat(0xB2800002.toInt())) shouldBeGreaterThan 0
            }
            test("cohort-distinct representations of 1.0 compare equal") {
                // 1×10^0 (0x32800001) vs 10×10^-1 (0x32000010)
                cmp.compare(DpdFloat(0x32800001), DpdFloat(0x32000010)) shouldBe 0
            }
        }

        context("exponent fast path (|diff| > 6)") {
            test("1×10^7 > 1×10^0 when exponent difference is 7") {
                cmp.compare(DpdFloat(0x36000001), DpdFloat(0x32800001)) shouldBeGreaterThan 0
            }
            test("1×10^0 < 1×10^7 when exponent difference is 7") {
                cmp.compare(DpdFloat(0x32800001), DpdFloat(0x36000001)) shouldBeLessThan 0
            }
        }

        context("exponent scaling path (|diff| ≤ 6)") {
            test("15×10^0 < 2×10^1 (= 20) when exponent difference is 1") {
                cmp.compare(DpdFloat(0x32800015), DpdFloat(0x33000002)) shouldBeLessThan 0
            }
            test("25×10^0 > 2×10^1 (= 20) when exponent difference is 1") {
                cmp.compare(DpdFloat(0x32800025), DpdFloat(0x33000002)) shouldBeGreaterThan 0
            }
        }
    }

    // ── Partial ordering (partialComparator) ──────────────────────────────────

    context("partialComparator") {
        val pcmp = DpdFloat.partialComparator

        context("NaN returns null") {
            test("NaN vs finite returns null") {
                pcmp.compare(DpdFloat(0x7E000000), DpdFloat(0x32800001)) shouldBe null
            }
            test("finite vs NaN returns null") {
                pcmp.compare(DpdFloat(0x32800001), DpdFloat(0x7E000000)) shouldBe null
            }
            test("NaN vs NaN returns null") {
                pcmp.compare(DpdFloat(0x7E000000), DpdFloat(0x7C000000)) shouldBe null
            }
            test("NaN vs infinity returns null") {
                pcmp.compare(DpdFloat(0x7E000000), DpdFloat(0x78000000)) shouldBe null
            }
        }

        context("non-NaN results are consistent with comparator") {
            test("+1 < +2") {
                pcmp.compare(DpdFloat(0x32800001), DpdFloat(0x32800002))!! shouldBeLessThan 0
            }
            test("+1 == 10×10^-1 (cohort)") {
                pcmp.compare(DpdFloat(0x32800001), DpdFloat(0x32000010)) shouldBe 0
            }
            test("-0 < +0") {
                pcmp.compare(DpdFloat(0x80000000.toInt()), DpdFloat(0x00000000))!! shouldBeLessThan 0
            }
        }
    }

    // ── Numerical equality ────────────────────────────────────────────────────

    context("numericalEquality") {
        val eq = DpdFloat.numericalEquality
        with(eq) {
            test("same value is numerically equal to itself") {
                DpdFloat(0x32800001).isEqualTo(DpdFloat(0x32800001)) shouldBe true
            }
            test("cohort-distinct representations of 1.0 are numerically equal") {
                // 1×10^0 vs 10×10^-1
                DpdFloat(0x32800001).isEqualTo(DpdFloat(0x32000010)) shouldBe true
            }
            test("+0 and -0 are numerically equal") {
                DpdFloat(0x00000000).isEqualTo(DpdFloat(0x80000000.toInt())) shouldBe true
            }
            test("different values are not numerically equal") {
                DpdFloat(0x32800001).isEqualTo(DpdFloat(0x32800002)) shouldBe false
            }
            test("NaN is not numerically equal to anything") {
                DpdFloat(0x7E000000).isEqualTo(DpdFloat(0x7E000000)) shouldBe false
                DpdFloat(0x7E000000).isEqualTo(DpdFloat(0x32800001)) shouldBe false
            }
            test("values with opposite signs are not numerically equal") {
                DpdFloat(0x32800001).isEqualTo(DpdFloat(0xB2800001.toInt())) shouldBe false
            }
        }
    }

    // ── sign trait ────────────────────────────────────────────────────────────

    context("sign trait") {
        val s = DpdFloat.sign

        test("isNegative is true for negative values") {
            with(s) { DpdFloat(0xB2800001.toInt()).isNegative() } shouldBe true
        }
        test("isNegative is false for positive values") {
            with(s) { DpdFloat(0x32800001).isNegative() } shouldBe false
        }
        test("negate flips sign bit") {
            with(s) { DpdFloat(0x32800001).negate() } shouldBe DpdFloat(0xB2800001.toInt())
        }
        test("abs clears sign bit") {
            with(s) { DpdFloat(0xB2800001.toInt()).abs() } shouldBe DpdFloat(0x32800001)
        }
        test("copySign copies positive sign") {
            with(s) { DpdFloat(0xB2800001.toInt()).copySign(DpdFloat(0x32800001)) } shouldBe DpdFloat(0x32800001)
        }
        test("copySign copies negative sign") {
            with(s) { DpdFloat(0x32800001).copySign(DpdFloat(0xB2800001.toInt())) } shouldBe DpdFloat(0xB2800001.toInt())
        }
    }
})
