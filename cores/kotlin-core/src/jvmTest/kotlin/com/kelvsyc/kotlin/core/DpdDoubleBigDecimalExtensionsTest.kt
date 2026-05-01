package com.kelvsyc.kotlin.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal
import java.math.BigInteger

class DpdDoubleBigDecimalExtensionsTest : FunSpec({

    // Bit patterns used throughout this file — same as DpdDoubleTest.
    //
    // Normal encoding (biasedExp < 0x180): combination = (biasedExp shl 3) or leadingDigit
    //
    //   0x31C0_0000_0000_0001L — +1 × 10^0   (biasedExp=398, sig=1, d5=encodeDeclet(001)=1)
    //   0xB1C0_0000_0000_0001uL.toLong() — −1 × 10^0 (sign set)
    //   0x31A0_0000_0000_0010L — +10 × 10^−1 (biasedExp=397, sig=10, d5=encodeDeclet(010)=0x10; cohort of 1.0)
    //   0L                    — +0 (biasedExp=0, sig=0)
    // Special: 0x7800_0000_0000_0000L — +infinity
    //          Long.MIN_VALUE or 0x7800_0000_0000_0000L — −infinity
    //          0x7E00_0000_0000_0000L — NaN

    context("DpdDouble.toBigDecimal()") {

        context("finite values") {
            test("+1 × 10^0 equals BigDecimal(1, scale=0)") {
                DpdDouble(0x31C0_0000_0000_0001L).toBigDecimal() shouldBe BigDecimal(BigInteger.ONE, 0)
            }

            test("-1 × 10^0 equals BigDecimal(-1, scale=0)") {
                DpdDouble(0xB1C0_0000_0000_0001uL.toLong()).toBigDecimal() shouldBe BigDecimal(BigInteger.ONE.negate(), 0)
            }

            test("+0 (biasedExp=0) has zero value") {
                DpdDouble(0L).toBigDecimal() shouldBeEqualComparingTo BigDecimal.ZERO
            }
        }

        context("scale reflects stored encoding") {
            test("1 × 10^0 has scale 0") {
                DpdDouble(0x31C0_0000_0000_0001L).toBigDecimal().scale() shouldBe 0
            }

            test("10 × 10^-1 has scale 1") {
                // biasedExponent=397, so scale = 398 - 397 = 1
                DpdDouble(0x31A0_0000_0000_0010L).toBigDecimal().scale() shouldBe 1
            }

            test("+0 (biasedExp=0) has scale 398") {
                DpdDouble(0L).toBigDecimal().scale() shouldBe 398
            }
        }

        context("cohort-distinct representations") {
            test("1 × 10^0 and 10 × 10^-1 compare equal via compareTo") {
                val a = DpdDouble(0x31C0_0000_0000_0001L).toBigDecimal()
                val b = DpdDouble(0x31A0_0000_0000_0010L).toBigDecimal()
                a shouldBeEqualComparingTo b
            }

            test("1 × 10^0 and 10 × 10^-1 have different scales") {
                val a = DpdDouble(0x31C0_0000_0000_0001L).toBigDecimal()
                val b = DpdDouble(0x31A0_0000_0000_0010L).toBigDecimal()
                a.scale() shouldNotBe b.scale()
            }
        }

        context("special values throw") {
            test("NaN throws NumberFormatException") {
                shouldThrow<NumberFormatException> { DpdDouble(0x7E00_0000_0000_0000L).toBigDecimal() }
            }

            test("+infinity throws NumberFormatException") {
                shouldThrow<NumberFormatException> { DpdDouble(0x7800_0000_0000_0000L).toBigDecimal() }
            }

            test("-infinity throws NumberFormatException") {
                shouldThrow<NumberFormatException> { DpdDouble(Long.MIN_VALUE or 0x7800_0000_0000_0000L).toBigDecimal() }
            }
        }
    }
})
