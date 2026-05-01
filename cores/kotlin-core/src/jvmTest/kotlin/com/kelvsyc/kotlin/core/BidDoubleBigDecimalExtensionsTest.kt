package com.kelvsyc.kotlin.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal
import java.math.BigInteger

class BidDoubleBigDecimalExtensionsTest : FunSpec({

    // Bit patterns used throughout this file — same as BidDoubleTest.
    //
    //   0x31C0_0000_0000_0001L — +1 × 10^0   (biasedExp=398, sig=1)
    //   0xB1C0_0000_0000_0001uL.toLong() — −1 × 10^0   (sign set)
    //   0x31C0_0000_0000_0002L — +2 × 10^0   (sig=2)
    //   0x31A0_0000_0000_000AL — +10 × 10^−1 (biasedExp=397, sig=10; cohort of 1.0)
    //   0L                    — +0 (biasedExp=0, sig=0)
    // Special: 0x7800_0000_0000_0000L — +infinity
    //          Long.MIN_VALUE or 0x7800_0000_0000_0000L — −infinity
    //          0x7E00_0000_0000_0000L — NaN

    context("BidDouble.toBigDecimal()") {

        context("finite values") {
            test("+1 × 10^0 equals BigDecimal(1, scale=0)") {
                BidDouble(0x31C0_0000_0000_0001L).toBigDecimal() shouldBe BigDecimal(BigInteger.ONE, 0)
            }

            test("-1 × 10^0 equals BigDecimal(-1, scale=0)") {
                BidDouble(0xB1C0_0000_0000_0001uL.toLong()).toBigDecimal() shouldBe BigDecimal(BigInteger.ONE.negate(), 0)
            }

            test("+2 × 10^0 equals BigDecimal(2, scale=0)") {
                BidDouble(0x31C0_0000_0000_0002L).toBigDecimal() shouldBe BigDecimal(BigInteger.valueOf(2L), 0)
            }

            test("+0 (biasedExp=0) has zero value") {
                BidDouble(0L).toBigDecimal() shouldBeEqualComparingTo BigDecimal.ZERO
            }
        }

        context("scale reflects stored encoding") {
            test("1 × 10^0 has scale 0") {
                BidDouble(0x31C0_0000_0000_0001L).toBigDecimal().scale() shouldBe 0
            }

            test("10 × 10^-1 has scale 1") {
                // biasedExponent=397, so scale = 398 - 397 = 1
                BidDouble(0x31A0_0000_0000_000AL).toBigDecimal().scale() shouldBe 1
            }

            test("+0 (biasedExp=0) has scale 398") {
                BidDouble(0L).toBigDecimal().scale() shouldBe 398
            }
        }

        context("cohort-distinct representations") {
            test("1 × 10^0 and 10 × 10^-1 compare equal via compareTo") {
                val a = BidDouble(0x31C0_0000_0000_0001L).toBigDecimal()
                val b = BidDouble(0x31A0_0000_0000_000AL).toBigDecimal()
                a shouldBeEqualComparingTo b
            }

            test("1 × 10^0 and 10 × 10^-1 have different scales") {
                val a = BidDouble(0x31C0_0000_0000_0001L).toBigDecimal()
                val b = BidDouble(0x31A0_0000_0000_000AL).toBigDecimal()
                a.scale() shouldNotBe b.scale()
            }
        }

        context("special values throw") {
            test("NaN throws NumberFormatException") {
                shouldThrow<NumberFormatException> { BidDouble(0x7E00_0000_0000_0000L).toBigDecimal() }
            }

            test("+infinity throws NumberFormatException") {
                shouldThrow<NumberFormatException> { BidDouble(0x7800_0000_0000_0000L).toBigDecimal() }
            }

            test("-infinity throws NumberFormatException") {
                shouldThrow<NumberFormatException> { BidDouble(Long.MIN_VALUE or 0x7800_0000_0000_0000L).toBigDecimal() }
            }
        }
    }
})
