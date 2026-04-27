package com.kelvsyc.kotlin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class BidFloatTest : FunSpec({

    // Bit patterns used throughout this file.
    //
    // Case 1 (bits[30:29] ≠ 11): biasedExp at bits[30:23], significand at bits[22:0].
    //   0x32800001 — +1 × 10^0   (biasedExp=101, sig=1)
    //   0xB2800001 — −1 × 10^0   (biasedExp=101, sig=1, sign set)
    //   0x32800002 — +2 × 10^0   (biasedExp=101, sig=2)
    //   0x3200000A — +10 × 10^−1 (biasedExp=100, sig=10; cohort of 1.0)
    //   0x32800000 — +0 with biasedExp=101
    //   0x00000000 — +0 (simplest; biasedExp=0)
    //   0x80000000 — −0
    //
    // Case 2 (bits[30:29] = 11, bits[28] = 0): biasedExp at bits[28:21], sig = 0x800000 | bits[20:0].
    //   0x6CB8967F — +9999999 × 10^0 (biasedExp=101, sig=9999999; maximum significand)
    //   0x6CA00000 — +8388608 × 10^0 (biasedExp=101, sig=8388608; smallest Case-2 significand)
    //
    // Special values (bits[30:26] = 11110 for Inf, 11111 for NaN):
    //   0x78000000 — +infinity
    //   0xF8000000 — −infinity
    //   0x7C000000 — NaN (no payload)
    //   0x7C080000 — canonical quiet NaN (bit 19 = 1; used by hash)
    //   0x7C000001 — NaN with different payload

    // ── Bit-field accessors ───────────────────────────────────────────────────

    context("sign") {
        test("positive value has false sign") {
            BidFloat(0x32800001).sign shouldBe false
        }
        test("negative value has true sign") {
            BidFloat(0xB2800001.toInt()).sign shouldBe true
        }
        test("+0 has false sign") {
            BidFloat(0x00000000).sign shouldBe false
        }
        test("-0 has true sign") {
            BidFloat(0x80000000.toInt()).sign shouldBe true
        }
    }

    context("combination") {
        test("1.0 has expected combination field") {
            // bits[30:20] of 0x32800001 = 0x328
            BidFloat(0x32800001).combination shouldBe 0x328
        }
        test("Case-2 value has top two combination bits set") {
            BidFloat(0x6CB8967F).combination ushr 9 shouldBe 3
        }
        test("NaN has top five combination bits all set") {
            BidFloat(0x7C000000).combination ushr 6 shouldBe 0x1F
        }
        test("infinity has top five combination bits equal to 11110") {
            BidFloat(0x78000000).combination ushr 6 shouldBe 0x1E
        }
    }

    context("continuation") {
        test("1.0 has continuation equal to low significand bits") {
            // significand=1, high 3 bits in combination = 0, so continuation = 1
            BidFloat(0x32800001).continuation shouldBe 1
        }
        test("10 × 10^-1 has continuation of 10") {
            BidFloat(0x3200000A).continuation shouldBe 10
        }
        test("+0 has zero continuation") {
            BidFloat(0x00000000).continuation shouldBe 0
        }
    }

    context("biasedExponent") {
        test("1.0 has biased exponent 101") {
            BidFloat(0x32800001).biasedExponent shouldBe 101
        }
        test("10 × 10^-1 has biased exponent 100") {
            BidFloat(0x3200000A).biasedExponent shouldBe 100
        }
        test("Case-2 value 9999999 × 10^0 has biased exponent 101") {
            BidFloat(0x6CB8967F).biasedExponent shouldBe 101
        }
        test("Case-2 boundary 8388608 × 10^0 has biased exponent 101") {
            BidFloat(0x6CA00000).biasedExponent shouldBe 101
        }
        test("+0 with biasedExp=101 decodes correctly") {
            BidFloat(0x32800000).biasedExponent shouldBe 101
        }
    }

    context("significand") {
        test("1.0 has significand 1") {
            BidFloat(0x32800001).significand shouldBe 1
        }
        test("2.0 has significand 2") {
            BidFloat(0x32800002).significand shouldBe 2
        }
        test("10 × 10^-1 has significand 10") {
            BidFloat(0x3200000A).significand shouldBe 10
        }
        test("Case-2: 9999999 × 10^0 has significand 9999999") {
            BidFloat(0x6CB8967F).significand shouldBe 9999999
        }
        test("Case-2 boundary: 8388608 × 10^0 has significand 8388608") {
            BidFloat(0x6CA00000).significand shouldBe 8388608
        }
        test("significand is independent of sign") {
            BidFloat(0x32800001).significand shouldBe BidFloat(0xB2800001.toInt()).significand
        }
    }

    // ── Classification ────────────────────────────────────────────────────────

    context("isNaN") {
        test("NaN is NaN") {
            BidFloat(0x7C000000).isNaN() shouldBe true
        }
        test("NaN with non-zero payload is NaN") {
            BidFloat(0x7C000001).isNaN() shouldBe true
        }
        test("negative NaN is NaN") {
            BidFloat(0xFC000000.toInt()).isNaN() shouldBe true
        }
        test("positive infinity is not NaN") {
            BidFloat(0x78000000).isNaN() shouldBe false
        }
        test("finite value is not NaN") {
            BidFloat(0x32800001).isNaN() shouldBe false
        }
        test("+0 is not NaN") {
            BidFloat(0x00000000).isNaN() shouldBe false
        }
    }

    context("isInfinite") {
        test("+infinity is infinite") {
            BidFloat(0x78000000).isInfinite() shouldBe true
        }
        test("-infinity is infinite") {
            BidFloat(0xF8000000.toInt()).isInfinite() shouldBe true
        }
        test("NaN is not infinite") {
            BidFloat(0x7C000000).isInfinite() shouldBe false
        }
        test("finite value is not infinite") {
            BidFloat(0x32800001).isInfinite() shouldBe false
        }
        test("+0 is not infinite") {
            BidFloat(0x00000000).isInfinite() shouldBe false
        }
    }

    context("isZero") {
        test("+0 (biasedExp=0) is zero") {
            BidFloat(0x00000000).isZero() shouldBe true
        }
        test("-0 is zero") {
            BidFloat(0x80000000.toInt()).isZero() shouldBe true
        }
        test("+0 with biasedExp=101 is zero") {
            BidFloat(0x32800000).isZero() shouldBe true
        }
        test("1.0 is not zero") {
            BidFloat(0x32800001).isZero() shouldBe false
        }
        test("NaN is not zero") {
            BidFloat(0x7C000000).isZero() shouldBe false
        }
        test("infinity is not zero") {
            BidFloat(0x78000000).isZero() shouldBe false
        }
    }

    // ── Equivalence equality and hashing ─────────────────────────────────────

    context("equalTo") {
        test("same bit pattern is equal to itself") {
            BidFloat.equalTo(BidFloat(0x32800001), BidFloat(0x32800001)) shouldBe true
        }
        test("different values are not equal") {
            BidFloat.equalTo(BidFloat(0x32800001), BidFloat(0x32800002)) shouldBe false
        }
        test("cohort-distinct representations of 1.0 are not equal") {
            // 1×10^0 and 10×10^-1 are numerically equal but different bit patterns
            BidFloat.equalTo(BidFloat(0x32800001), BidFloat(0x3200000A)) shouldBe false
        }
        test("+0 and -0 are not equal") {
            BidFloat.equalTo(BidFloat(0x00000000), BidFloat(0x80000000.toInt())) shouldBe false
        }
        test("NaN equals NaN regardless of payload") {
            BidFloat.equalTo(BidFloat(0x7C000000), BidFloat(0x7C000001)) shouldBe true
        }
        test("NaN equals NaN with different sign bit") {
            BidFloat.equalTo(BidFloat(0x7C000000), BidFloat(0xFC000000.toInt())) shouldBe true
        }
        test("NaN is not equal to a finite value") {
            BidFloat.equalTo(BidFloat(0x7C000000), BidFloat(0x32800001)) shouldBe false
        }
    }

    context("hash") {
        test("all NaN payloads hash to the same value") {
            BidFloat.hash(BidFloat(0x7C000000)) shouldBe BidFloat.hash(BidFloat(0x7C000001))
            BidFloat.hash(BidFloat(0x7C000000)) shouldBe BidFloat.hash(BidFloat(0xFC000000.toInt()))
        }
        test("+0 and -0 hash to different values") {
            BidFloat.hash(BidFloat(0x00000000)) shouldNotBe BidFloat.hash(BidFloat(0x80000000.toInt()))
        }
        test("cohort-distinct representations hash differently") {
            BidFloat.hash(BidFloat(0x32800001)) shouldNotBe BidFloat.hash(BidFloat(0x3200000A))
        }
        test("hash is consistent with equalTo: equal values have equal hashes") {
            val nan1 = BidFloat(0x7C000000)
            val nan2 = BidFloat(0x7C000001)
            BidFloat.equalTo(nan1, nan2) shouldBe true
            BidFloat.hash(nan1) shouldBe BidFloat.hash(nan2)
        }
    }

    // ── Numerical equality ────────────────────────────────────────────────────

    context("numericalEquality") {
        val eq = BidFloat.numericalEquality
        with(eq) {
            test("same value is numerically equal to itself") {
                BidFloat(0x32800001).isEqualTo(BidFloat(0x32800001)) shouldBe true
            }
            test("cohort-distinct representations of 1.0 are numerically equal") {
                // 1×10^0 vs 10×10^-1: same value, different encoding
                BidFloat(0x32800001).isEqualTo(BidFloat(0x3200000A)) shouldBe true
            }
            test("+0 and -0 are numerically equal") {
                BidFloat(0x00000000).isEqualTo(BidFloat(0x80000000.toInt())) shouldBe true
            }
            test("different values are not numerically equal") {
                BidFloat(0x32800001).isEqualTo(BidFloat(0x32800002)) shouldBe false
            }
            test("NaN is not numerically equal to anything") {
                BidFloat(0x7C000000).isEqualTo(BidFloat(0x7C000000)) shouldBe false
                BidFloat(0x7C000000).isEqualTo(BidFloat(0x32800001)) shouldBe false
            }
            test("values with opposite signs are not numerically equal") {
                BidFloat(0x32800001).isEqualTo(BidFloat(0xB2800001.toInt())) shouldBe false
            }
        }
    }
})
