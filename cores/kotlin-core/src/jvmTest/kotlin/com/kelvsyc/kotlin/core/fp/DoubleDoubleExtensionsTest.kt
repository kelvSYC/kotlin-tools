package com.kelvsyc.kotlin.core.fp

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class DoubleDoubleExtensionsTest : FunSpec({

    // ── DoubleDouble.toBigDecimal() ───────────────────────────────────────────

    context("DoubleDouble.toBigDecimal()") {

        context("exact values") {
            test("ZERO") {
                DoubleDouble.ZERO.toBigDecimal() shouldBeEqualComparingTo BigDecimal.ZERO
            }

            test("ONE") {
                DoubleDouble.ONE.toBigDecimal() shouldBeEqualComparingTo BigDecimal.ONE
            }

            test("negative one") {
                (-DoubleDouble.ONE).toBigDecimal() shouldBeEqualComparingTo BigDecimal.ONE.negate()
            }

            test("result equals BigDecimal(high) + BigDecimal(low)") {
                val dd = DoubleDouble.create(1.0, 1e-16)
                val expected = BigDecimal(1.0) + BigDecimal(1e-16)
                dd.toBigDecimal() shouldBeEqualComparingTo expected
            }

            test("low component contributes precision beyond a single Double") {
                // 1.0 + 1e-20 cannot be represented as a single Double (lost in rounding),
                // but toBigDecimal() captures both components exactly.
                val dd = DoubleDouble.create(1.0, 1e-20)
                val result = dd.toBigDecimal()
                result shouldBeEqualComparingTo BigDecimal(1.0) + BigDecimal(1e-20)
            }
        }

        context("special values throw") {
            test("NaN throws NumberFormatException") {
                shouldThrow<NumberFormatException> { DoubleDouble.NaN.toBigDecimal() }
            }

            test("POSITIVE_INFINITY throws NumberFormatException") {
                shouldThrow<NumberFormatException> { DoubleDouble.POSITIVE_INFINITY.toBigDecimal() }
            }

            test("NEGATIVE_INFINITY throws NumberFormatException") {
                shouldThrow<NumberFormatException> { DoubleDouble.NEGATIVE_INFINITY.toBigDecimal() }
            }
        }
    }

    // ── DoubleDouble.Companion.bigDecimalConverter ────────────────────────────

    context("DoubleDouble.Companion.bigDecimalConverter") {
        val conv = DoubleDouble.bigDecimalConverter

        context("forward (DoubleDouble → BigDecimal)") {
            test("forward(ONE) = BigDecimal.ONE") {
                conv(DoubleDouble.ONE) shouldBeEqualComparingTo BigDecimal.ONE
            }

            test("forward is consistent with toBigDecimal()") {
                val dd = DoubleDouble.create(1.5, 1e-17)
                conv(dd) shouldBeEqualComparingTo dd.toBigDecimal()
            }
        }

        context("backward (BigDecimal → DoubleDouble)") {
            test("backward(1.0) high = 1.0, low = 0.0") {
                val dd = conv.reverse(BigDecimal.ONE)
                dd.high shouldBe 1.0
                dd.low shouldBe 0.0
            }

            test("backward(0.5) high = 0.5") {
                val dd = conv.reverse(BigDecimal("0.5"))
                dd.high shouldBe 0.5
            }

            test("backward satisfies DoubleDouble invariant: |high| >= |low|") {
                val bd = BigDecimal("1.000000000000000000001")
                val dd = conv.reverse(bd)
                (kotlin.math.abs(dd.high) >= kotlin.math.abs(dd.low)) shouldBe true
            }
        }

        context("round-trip for exactly representable values") {
            test("round-trip: DoubleDouble(1.0, 1e-16) → BigDecimal → DoubleDouble") {
                val original = DoubleDouble.create(1.0, 1e-16)
                val roundTripped = conv.reverse(conv(original))
                roundTripped shouldBe original
            }

            test("round-trip: DoubleDouble.ONE → BigDecimal → DoubleDouble") {
                val roundTripped = conv.reverse(conv(DoubleDouble.ONE))
                roundTripped shouldBe DoubleDouble.ONE
            }

            test("round-trip: DoubleDouble(2.0, 0.0) → BigDecimal → DoubleDouble") {
                val original = DoubleDouble.create(2.0, 0.0)
                conv.reverse(conv(original)) shouldBe original
            }
        }

        context("singleton identity") {
            test("bigDecimalConverter returns the same instance on repeated access") {
                DoubleDouble.bigDecimalConverter shouldBe DoubleDouble.bigDecimalConverter
            }
        }
    }
})
