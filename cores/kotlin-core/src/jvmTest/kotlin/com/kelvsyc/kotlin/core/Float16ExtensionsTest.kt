package com.kelvsyc.kotlin.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class Float16ExtensionsTest : FunSpec({

    // ── Float16.toBigDecimal() ────────────────────────────────────────────────

    context("Float16.toBigDecimal()") {

        context("exact values") {
            test("positive zero") {
                Float16(0.0f).toBigDecimal() shouldBeEqualComparingTo BigDecimal.ZERO
            }

            test("negative zero") {
                Float16(-0.0f).toBigDecimal() shouldBeEqualComparingTo BigDecimal.ZERO
            }

            test("one") {
                Float16(1.0f).toBigDecimal() shouldBeEqualComparingTo BigDecimal.ONE
            }

            test("negative one") {
                Float16(-1.0f).toBigDecimal() shouldBeEqualComparingTo BigDecimal.ONE.negate()
            }

            test("0.5 is exact in binary") {
                Float16(0.5f).toBigDecimal() shouldBeEqualComparingTo BigDecimal("0.5")
            }

            test("MAX_VALUE maps to 65504") {
                Float16.MAX_VALUE.toBigDecimal() shouldBeEqualComparingTo BigDecimal("65504")
            }
        }

        context("subnormals") {
            test("MIN_VALUE (smallest subnormal) is exact") {
                // Float16.MIN_VALUE = 2^-24; exact in binary, so BigDecimal conversion is exact.
                val expected = BigDecimal(Float16.MIN_VALUE.toDouble())
                Float16.MIN_VALUE.toBigDecimal() shouldBeEqualComparingTo expected
            }

            test("MIN_NORMAL is exact") {
                val expected = BigDecimal(Float16.MIN_NORMAL.toDouble())
                Float16.MIN_NORMAL.toBigDecimal() shouldBeEqualComparingTo expected
            }
        }

        context("special values throw") {
            test("NaN throws NumberFormatException") {
                shouldThrow<NumberFormatException> { Float16.NaN.toBigDecimal() }
            }

            test("POSITIVE_INFINITY throws NumberFormatException") {
                shouldThrow<NumberFormatException> { Float16.POSITIVE_INFINITY.toBigDecimal() }
            }

            test("NEGATIVE_INFINITY throws NumberFormatException") {
                shouldThrow<NumberFormatException> { Float16.NEGATIVE_INFINITY.toBigDecimal() }
            }
        }

        context("exactness: BigDecimal(double) gives the stored value, not the decimal shorthand") {
            // 0.1 is not exactly representable in binary16; the stored value differs from 0.1.
            // toBigDecimal() should return the exact stored value, not 0.1.
            test("Float16(0.1f) stored value differs from 0.1") {
                val exact = Float16(0.1f).toBigDecimal()
                exact shouldBe BigDecimal(Float16(0.1f).toDouble())
            }
        }
    }

    // ── Float16.Companion.bigDecimalConverter ─────────────────────────────────

    context("Float16.Companion.bigDecimalConverter") {
        val conv = Float16.bigDecimalConverter

        context("forward (Float16 → BigDecimal)") {
            test("forward(1.0) = BigDecimal(1.0)") {
                conv(Float16(1.0f)) shouldBeEqualComparingTo BigDecimal.ONE
            }

            test("forward(0.5) = 0.5") {
                conv(Float16(0.5f)) shouldBeEqualComparingTo BigDecimal("0.5")
            }

            test("forward is consistent with toBigDecimal()") {
                val v = Float16(2.0f)
                conv(v) shouldBeEqualComparingTo v.toBigDecimal()
            }
        }

        context("backward (BigDecimal → Float16)") {
            test("backward(1.0) = Float16(1.0f)") {
                conv.reverse(BigDecimal.ONE) shouldBe Float16(1.0f)
            }

            test("backward(0.5) = Float16(0.5f)") {
                conv.reverse(BigDecimal("0.5")) shouldBe Float16(0.5f)
            }

            test("backward rounds to nearest representable Float16") {
                // BigDecimal("0.1") → nearest Float16 to 0.1
                conv.reverse(BigDecimal("0.1")) shouldBe Float16(0.1f)
            }

            test("backward for out-of-range value produces infinity") {
                conv.reverse(BigDecimal("1e100")) shouldBe Float16.POSITIVE_INFINITY
            }

            test("backward for large negative value produces negative infinity") {
                conv.reverse(BigDecimal("-1e100")) shouldBe Float16.NEGATIVE_INFINITY
            }
        }

        context("round-trip for exactly representable values") {
            test("round-trip: Float16(2.0f) → BigDecimal → Float16") {
                val original = Float16(2.0f)
                conv.reverse(conv(original)) shouldBe original
            }

            test("round-trip: Float16(0.5f) → BigDecimal → Float16") {
                val original = Float16(0.5f)
                conv.reverse(conv(original)) shouldBe original
            }

            test("round-trip: Float16(-4.0f) → BigDecimal → Float16") {
                val original = Float16(-4.0f)
                conv.reverse(conv(original)) shouldBe original
            }
        }

        context("singleton identity") {
            test("bigDecimalConverter returns the same instance on repeated access") {
                Float16.bigDecimalConverter shouldBe Float16.bigDecimalConverter
            }
        }
    }
})
