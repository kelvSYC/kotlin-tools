package com.kelvsyc.kotlin.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal
import java.math.BigInteger

class DpdFloatBigDecimalExtensionsTest : FunSpec({

    // Bit patterns used throughout this file — same as DpdFloatTest.
    //
    //   0x32800001 — +1 × 10^0   (biasedExp=101, sig=1)
    //   0xB2800001 — −1 × 10^0   (sign set)
    //   0x32000010 — +10 × 10^−1 (biasedExp=100, sig=10; cohort of 1.0)
    //   0x00000000 — +0 (biasedExp=0, sig=0)
    // Special: 0x78000000 — +infinity
    //          0xF8000000 — −infinity
    //          0x7E000000 — NaN

    context("DpdFloat.toBigDecimal()") {

        context("finite values") {
            test("+1 × 10^0 equals BigDecimal(1, scale=0)") {
                DpdFloat(0x32800001).toBigDecimal() shouldBe BigDecimal(BigInteger.ONE, 0)
            }

            test("-1 × 10^0 equals BigDecimal(-1, scale=0)") {
                DpdFloat(0xB2800001.toInt()).toBigDecimal() shouldBe BigDecimal(BigInteger.ONE.negate(), 0)
            }

            test("+0 (biasedExp=0) has zero value") {
                DpdFloat(0x00000000).toBigDecimal() shouldBeEqualComparingTo BigDecimal.ZERO
            }
        }

        context("scale reflects stored encoding") {
            test("1 × 10^0 has scale 0") {
                DpdFloat(0x32800001).toBigDecimal().scale() shouldBe 0
            }

            test("10 × 10^-1 has scale 1") {
                // biasedExponent=100, so scale = 101 - 100 = 1
                DpdFloat(0x32000010).toBigDecimal().scale() shouldBe 1
            }

            test("+0 (biasedExp=0) has scale 101") {
                DpdFloat(0x00000000).toBigDecimal().scale() shouldBe 101
            }
        }

        context("cohort-distinct representations") {
            test("1 × 10^0 and 10 × 10^-1 compare equal via compareTo") {
                val a = DpdFloat(0x32800001).toBigDecimal()
                val b = DpdFloat(0x32000010).toBigDecimal()
                a shouldBeEqualComparingTo b
            }

            test("1 × 10^0 and 10 × 10^-1 have different scales") {
                val a = DpdFloat(0x32800001).toBigDecimal()
                val b = DpdFloat(0x32000010).toBigDecimal()
                a.scale() shouldNotBe b.scale()
            }
        }

        context("special values throw") {
            test("NaN throws NumberFormatException") {
                shouldThrow<NumberFormatException> { DpdFloat(0x7E000000).toBigDecimal() }
            }

            test("+infinity throws NumberFormatException") {
                shouldThrow<NumberFormatException> { DpdFloat(0x78000000).toBigDecimal() }
            }

            test("-infinity throws NumberFormatException") {
                shouldThrow<NumberFormatException> { DpdFloat(0xF8000000.toInt()).toBigDecimal() }
            }
        }
    }
})
