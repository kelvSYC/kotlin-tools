package com.kelvsyc.kotlin.core.traits.dfp
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic

import com.kelvsyc.kotlin.core.DpdDouble
import com.kelvsyc.kotlin.core.fp.FiniteDecimalFloatingPoint
import com.kelvsyc.kotlin.core.fp.toDpdDouble
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun dpd64(biasedExp: Int, sig: ULong, negative: Boolean = false) =
    FiniteDecimalFloatingPoint(negative, biasedExp - 398, sig).toDpdDouble()

/**
 * Smoke tests for [FloatingPointArithmetic.dpdDouble].
 *
 * Full algorithmic coverage lives in [BidDoubleArithmeticTest]. These tests verify that the
 * DPD instance delegates correctly through the BID↔DPD converter and produces numerically correct
 * results on a representative cross-section of cases.
 */
class DpdDoubleArithmeticTest : FunSpec({
    val arith = FloatingPointArithmetic.dpdDouble

    // ── identity elements ─────────────────────────────────────────────────────

    test("zero is positiveZero") { arith.zero shouldBe DpdDouble.positiveZero }
    test("one has significand 1 and biasedExponent 398") {
        arith.one.significand shouldBe 1L
        arith.one.biasedExponent shouldBe 398
    }

    // ── classification ────────────────────────────────────────────────────────

    context("isNaN") {
        test("NaN returns true") { with(arith) { DpdDouble.NaN.isNaN() } shouldBe true }
        test("infinity returns false") { with(arith) { DpdDouble.positiveInfinity.isNaN() } shouldBe false }
        test("finite returns false") { with(arith) { arith.one.isNaN() } shouldBe false }
        test("zero returns false") { with(arith) { arith.zero.isNaN() } shouldBe false }
    }

    context("isInfinite") {
        test("positiveInfinity returns true") { with(arith) { DpdDouble.positiveInfinity.isInfinite() } shouldBe true }
        test("negativeInfinity returns true") { with(arith) { DpdDouble.negativeInfinity.isInfinite() } shouldBe true }
        test("NaN returns false") { with(arith) { DpdDouble.NaN.isInfinite() } shouldBe false }
        test("finite returns false") { with(arith) { arith.one.isInfinite() } shouldBe false }
    }

    context("isFinite") {
        test("finite value returns true") { with(arith) { arith.one.isFinite() } shouldBe true }
        test("zero returns true") { with(arith) { arith.zero.isFinite() } shouldBe true }
        test("infinity returns false") { with(arith) { DpdDouble.positiveInfinity.isFinite() } shouldBe false }
        test("NaN returns false") { with(arith) { DpdDouble.NaN.isFinite() } shouldBe false }
    }

    context("isZero") {
        test("positiveZero returns true") { with(arith) { DpdDouble.positiveZero.isZero() } shouldBe true }
        test("negativeZero returns true") { with(arith) { DpdDouble.negativeZero.isZero() } shouldBe true }
        test("nonzero finite returns false") { with(arith) { arith.one.isZero() } shouldBe false }
        test("NaN returns false") { with(arith) { DpdDouble.NaN.isZero() } shouldBe false }
        test("infinity returns false") { with(arith) { DpdDouble.positiveInfinity.isZero() } shouldBe false }
    }

    context("isNegative") {
        test("negative finite returns true") { with(arith) { dpd64(398, 1uL, negative = true).isNegative() } shouldBe true }
        test("negativeZero returns true") { with(arith) { DpdDouble.negativeZero.isNegative() } shouldBe true }
        test("positive finite returns false") { with(arith) { arith.one.isNegative() } shouldBe false }
        test("positiveZero returns false") { with(arith) { DpdDouble.positiveZero.isNegative() } shouldBe false }
    }

    // ── sign operations ───────────────────────────────────────────────────────

    context("negate") {
        test("negating positive gives negative") {
            with(arith) { dpd64(398, 5uL).negate() }.sign shouldBe true
        }
        test("negating negative gives positive") {
            with(arith) { dpd64(398, 5uL, negative = true).negate() }.sign shouldBe false
        }
        test("negating +0 gives -0") {
            with(arith) { DpdDouble.positiveZero.negate() } shouldBe DpdDouble.negativeZero
        }
        test("negating -0 gives +0") {
            with(arith) { DpdDouble.negativeZero.negate() } shouldBe DpdDouble.positiveZero
        }
        test("double negation is identity") {
            val x = dpd64(398, 5uL)
            with(arith) { x.negate().negate() } shouldBe x
        }
        test("negating NaN produces NaN") {
            with(arith) { DpdDouble.NaN.negate() }.isNaN() shouldBe true
        }
    }

    context("abs") {
        test("abs of negative value clears sign") {
            with(arith) { dpd64(398, 5uL, negative = true).abs() }.sign shouldBe false
        }
        test("abs of positive value is unchanged") {
            val x = dpd64(398, 5uL)
            with(arith) { x.abs() } shouldBe x
        }
        test("abs of negativeZero is positiveZero") {
            with(arith) { DpdDouble.negativeZero.abs() } shouldBe DpdDouble.positiveZero
        }
        test("abs of NaN is NaN") {
            with(arith) { DpdDouble.NaN.abs() }.isNaN() shouldBe true
        }
    }

    // ── arithmetic ────────────────────────────────────────────────────────────

    context("add") {
        test("1 + 1 = 2") {
            val result = with(arith) { arith.one.add(arith.one) }
            result.significand shouldBe 2L
            result.biasedExponent shouldBe 398
        }
        test("NaN + finite = NaN") {
            with(arith) { DpdDouble.NaN.add(arith.one) }.isNaN() shouldBe true
        }
        test("+inf + +inf = +inf") {
            with(arith) { DpdDouble.positiveInfinity.add(DpdDouble.positiveInfinity) } shouldBe DpdDouble.positiveInfinity
        }
        test("+inf + -inf = NaN") {
            with(arith) { DpdDouble.positiveInfinity.add(DpdDouble.negativeInfinity) }.isNaN() shouldBe true
        }
        test("-0 + -0 = -0") {
            with(arith) { DpdDouble.negativeZero.add(DpdDouble.negativeZero) } shouldBe DpdDouble.negativeZero
        }
        test("cancellation x + (-x) = +0") {
            val x = dpd64(398, 5uL)
            val negX = DpdDouble(x.bits xor Long.MIN_VALUE)
            with(arith) { x.add(negX) } shouldBe DpdDouble.positiveZero
        }
    }

    context("subtract") {
        test("5 - 3 = 2") {
            val result = with(arith) { dpd64(398, 5uL).subtract(dpd64(398, 3uL)) }
            result.significand shouldBe 2L
            result.sign shouldBe false
        }
        test("3 - 5 = -2") {
            val result = with(arith) { dpd64(398, 3uL).subtract(dpd64(398, 5uL)) }
            result.sign shouldBe true
            result.significand shouldBe 2L
        }
        test("NaN - finite = NaN") {
            with(arith) { DpdDouble.NaN.subtract(arith.one) }.isNaN() shouldBe true
        }
    }

    context("multiply") {
        test("3 × 4 = 12") {
            val result = with(arith) { dpd64(398, 3uL).multiply(dpd64(398, 4uL)) }
            result.significand shouldBe 12L
        }
        test("0 × infinity = NaN") {
            with(arith) { DpdDouble.positiveZero.multiply(DpdDouble.positiveInfinity) }.isNaN() shouldBe true
        }
        test("negative × positive = negative") {
            with(arith) { dpd64(398, 1uL, negative = true).multiply(arith.one) }.sign shouldBe true
        }
        test("NaN × finite = NaN") {
            with(arith) { DpdDouble.NaN.multiply(arith.one) }.isNaN() shouldBe true
        }
    }

    context("divide") {
        test("10 / 5 = 2") {
            val result = with(arith) { dpd64(398, 10uL).divide(dpd64(398, 5uL)) }
            result.significand shouldBe 2L
        }
        test("1 / 0 = +inf") {
            with(arith) { arith.one.divide(DpdDouble.positiveZero) } shouldBe DpdDouble.positiveInfinity
        }
        test("0 / 0 = NaN") {
            with(arith) { DpdDouble.positiveZero.divide(DpdDouble.positiveZero) }.isNaN() shouldBe true
        }
        test("finite / inf = +0") {
            val result = with(arith) { arith.one.divide(DpdDouble.positiveInfinity) }
            result.isZero() shouldBe true
            result.sign shouldBe false
        }
    }

    context("compareTo") {
        test("1 < 2") {
            (with(arith) { dpd64(398, 1uL).compareTo(dpd64(398, 2uL)) } < 0) shouldBe true
        }
        test("2 > 1") {
            (with(arith) { dpd64(398, 2uL).compareTo(dpd64(398, 1uL)) } > 0) shouldBe true
        }
        test("x == x") {
            val x = dpd64(398, 5uL)
            with(arith) { x.compareTo(x) } shouldBe 0
        }
        test("NaN compares after everything") {
            (with(arith) { DpdDouble.NaN.compareTo(DpdDouble.maxValue) } > 0) shouldBe true
        }
        test("consistent with DpdDouble.comparator") {
            val a = DpdDouble.minValue; val b = DpdDouble.maxValue
            with(arith) { a.compareTo(b) } shouldBe DpdDouble.comparator.compare(a, b)
        }
    }
})
