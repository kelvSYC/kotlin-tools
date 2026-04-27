package com.kelvsyc.kotlin.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal
import java.math.BigInteger

class BidFloatBigDecimalExtensionsTest : FunSpec({

    // Bit patterns used throughout this file — same as BidFloatTest.
    //
    // Case 1:  0x32800001 — +1 × 10^0   (biasedExp=101, sig=1)
    //          0xB2800001 — −1 × 10^0   (biasedExp=101, sig=1, sign set)
    //          0x32800002 — +2 × 10^0   (biasedExp=101, sig=2)
    //          0x3200000A — +10 × 10^−1 (biasedExp=100, sig=10; cohort of 1.0)
    //          0x32800000 — +0 with biasedExp=101
    //          0x00000000 — +0 (biasedExp=0)
    //          0x80000000 — −0
    // Case 2:  0x6CB8967F — +9999999 × 10^0 (biasedExp=101, sig=9999999)
    // Special: 0x78000000 — +infinity
    //          0x7C000000 — NaN

    context("BidFloat.toBigDecimal()") {

        context("finite values") {
            test("+1 × 10^0 equals BigDecimal(1, scale=0)") {
                val result = BidFloat(0x32800001).toBigDecimal()
                result shouldBe BigDecimal(BigInteger.ONE, 0)
            }

            test("-1 × 10^0 equals BigDecimal(-1, scale=0)") {
                val result = BidFloat(0xB2800001.toInt()).toBigDecimal()
                result shouldBe BigDecimal(BigInteger.ONE.negate(), 0)
            }

            test("+2 × 10^0 equals BigDecimal(2, scale=0)") {
                BidFloat(0x32800002).toBigDecimal() shouldBe BigDecimal(BigInteger.valueOf(2), 0)
            }

            test("+0 (biasedExp=0) has zero value") {
                BidFloat(0x00000000).toBigDecimal() shouldBeEqualComparingTo BigDecimal.ZERO
            }

            test("-0 has zero value") {
                BidFloat(0x80000000.toInt()).toBigDecimal() shouldBeEqualComparingTo BigDecimal.ZERO
            }

            test("+0 with biasedExp=101 has zero value") {
                BidFloat(0x32800000).toBigDecimal() shouldBeEqualComparingTo BigDecimal.ZERO
            }

            test("Case-2 maximum significand: 9999999 × 10^0") {
                BidFloat(0x6CB8967F).toBigDecimal() shouldBe BigDecimal(BigInteger.valueOf(9999999), 0)
            }
        }

        context("scale reflects stored encoding") {
            test("1 × 10^0 has scale 0") {
                BidFloat(0x32800001).toBigDecimal().scale() shouldBe 0
            }

            test("10 × 10^-1 has scale 1") {
                // biasedExponent=100, so scale = 101 - 100 = 1
                BidFloat(0x3200000A).toBigDecimal().scale() shouldBe 1
            }

            test("+0 with biasedExp=0 has scale 101") {
                BidFloat(0x00000000).toBigDecimal().scale() shouldBe 101
            }
        }

        context("cohort-distinct representations") {
            test("1 × 10^0 and 10 × 10^-1 compare equal via compareTo") {
                val a = BidFloat(0x32800001).toBigDecimal()
                val b = BidFloat(0x3200000A).toBigDecimal()
                a shouldBeEqualComparingTo b
            }

            test("1 × 10^0 and 10 × 10^-1 have different scales") {
                val a = BidFloat(0x32800001).toBigDecimal()
                val b = BidFloat(0x3200000A).toBigDecimal()
                a.scale() shouldNotBe b.scale()
            }

            test("1 × 10^0 and 10 × 10^-1 are not bitwise-equal BigDecimals") {
                val a = BidFloat(0x32800001).toBigDecimal()
                val b = BidFloat(0x3200000A).toBigDecimal()
                (a == b) shouldBe false
            }
        }

        context("special values throw") {
            test("NaN throws NumberFormatException") {
                shouldThrow<NumberFormatException> { BidFloat(0x7C000000).toBigDecimal() }
            }

            test("+infinity throws NumberFormatException") {
                shouldThrow<NumberFormatException> { BidFloat(0x78000000).toBigDecimal() }
            }

            test("-infinity throws NumberFormatException") {
                shouldThrow<NumberFormatException> { BidFloat(0xF8000000.toInt()).toBigDecimal() }
            }
        }
    }
})
