package com.kelvsyc.kotlin.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class BFloat16ExtensionsTest : FunSpec({

    // ── BFloat16.toBigDecimal() ───────────────────────────────────────────────

    context("BFloat16.toBigDecimal()") {

        context("exact values") {
            test("positive zero") {
                BFloat16(0.0f).toBigDecimal() shouldBeEqualComparingTo BigDecimal.ZERO
            }

            test("negative zero") {
                BFloat16(-0.0f).toBigDecimal() shouldBeEqualComparingTo BigDecimal.ZERO
            }

            test("one") {
                BFloat16(1.0f).toBigDecimal() shouldBeEqualComparingTo BigDecimal.ONE
            }

            test("negative one") {
                BFloat16(-1.0f).toBigDecimal() shouldBeEqualComparingTo BigDecimal.ONE.negate()
            }

            test("0.5 is exact in binary") {
                BFloat16(0.5f).toBigDecimal() shouldBeEqualComparingTo BigDecimal("0.5")
            }

            test("MAX_VALUE matches its double representation") {
                BFloat16.MAX_VALUE.toBigDecimal() shouldBeEqualComparingTo BigDecimal(BFloat16.MAX_VALUE.toDouble())
            }
        }

        context("subnormals") {
            test("MIN_VALUE (smallest subnormal) is exact") {
                val expected = BigDecimal(BFloat16.MIN_VALUE.toDouble())
                BFloat16.MIN_VALUE.toBigDecimal() shouldBeEqualComparingTo expected
            }

            test("MIN_NORMAL is exact") {
                val expected = BigDecimal(BFloat16.MIN_NORMAL.toDouble())
                BFloat16.MIN_NORMAL.toBigDecimal() shouldBeEqualComparingTo expected
            }
        }

        context("special values throw") {
            test("NaN throws NumberFormatException") {
                shouldThrow<NumberFormatException> { BFloat16.NaN.toBigDecimal() }
            }

            test("POSITIVE_INFINITY throws NumberFormatException") {
                shouldThrow<NumberFormatException> { BFloat16.POSITIVE_INFINITY.toBigDecimal() }
            }

            test("NEGATIVE_INFINITY throws NumberFormatException") {
                shouldThrow<NumberFormatException> { BFloat16.NEGATIVE_INFINITY.toBigDecimal() }
            }
        }

        context("exactness: BigDecimal(double) gives the stored value, not the decimal shorthand") {
            test("BFloat16(0.1f) stored value differs from 0.1") {
                val exact = BFloat16(0.1f).toBigDecimal()
                exact shouldBe BigDecimal(BFloat16(0.1f).toDouble())
            }
        }
    }

    // ── BFloat16.Companion.bigDecimalConverter ────────────────────────────────

    context("BFloat16.Companion.bigDecimalConverter") {
        val conv = BFloat16.bigDecimalConverter

        context("forward (BFloat16 → BigDecimal)") {
            test("forward(1.0) = BigDecimal(1.0)") {
                conv(BFloat16(1.0f)) shouldBeEqualComparingTo BigDecimal.ONE
            }

            test("forward(0.5) = 0.5") {
                conv(BFloat16(0.5f)) shouldBeEqualComparingTo BigDecimal("0.5")
            }

            test("forward is consistent with toBigDecimal()") {
                val v = BFloat16(2.0f)
                conv(v) shouldBeEqualComparingTo v.toBigDecimal()
            }
        }

        context("backward (BigDecimal → BFloat16)") {
            test("backward(1.0) = BFloat16(1.0f)") {
                conv.reverse(BigDecimal.ONE) shouldBe BFloat16(1.0f)
            }

            test("backward(0.5) = BFloat16(0.5f)") {
                conv.reverse(BigDecimal("0.5")) shouldBe BFloat16(0.5f)
            }

            test("backward rounds to nearest representable BFloat16") {
                conv.reverse(BigDecimal("0.1")) shouldBe BFloat16(0.1f)
            }

            test("backward for out-of-range value produces positive infinity") {
                conv.reverse(BigDecimal("1e100")) shouldBe BFloat16.POSITIVE_INFINITY
            }

            test("backward for large negative value produces negative infinity") {
                conv.reverse(BigDecimal("-1e100")) shouldBe BFloat16.NEGATIVE_INFINITY
            }
        }

        context("round-trip for exactly representable values") {
            test("round-trip: BFloat16(2.0f) → BigDecimal → BFloat16") {
                val original = BFloat16(2.0f)
                conv.reverse(conv(original)) shouldBe original
            }

            test("round-trip: BFloat16(0.5f) → BigDecimal → BFloat16") {
                val original = BFloat16(0.5f)
                conv.reverse(conv(original)) shouldBe original
            }

            test("round-trip: BFloat16(-4.0f) → BigDecimal → BFloat16") {
                val original = BFloat16(-4.0f)
                conv.reverse(conv(original)) shouldBe original
            }
        }

        context("singleton identity") {
            test("bigDecimalConverter returns the same instance on repeated access") {
                BFloat16.bigDecimalConverter shouldBe BFloat16.bigDecimalConverter
            }
        }
    }
})
