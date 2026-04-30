package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.DpdFloat
import com.kelvsyc.kotlin.core.fp.FiniteDecimalFloatingPoint
import com.kelvsyc.kotlin.core.fp.toDpdFloat
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun dpd(biasedExp: Int, sig: UInt, negative: Boolean = false) =
    FiniteDecimalFloatingPoint(negative, biasedExp - 101, sig).toDpdFloat()

/**
 * Smoke tests for [FloatingPointArithmetic.dpdFloat].
 *
 * Full algorithmic coverage lives in [BidFloatArithmeticTest]. These tests verify that the
 * DPD instance delegates correctly through the BID↔DPD converter and produces numerically correct
 * results on a representative cross-section of cases.
 */
class DpdFloatArithmeticTest : FunSpec({
    val arith = FloatingPointArithmetic.dpdFloat

    // ── identity elements ─────────────────────────────────────────────────────

    test("zero is positiveZero") { arith.zero shouldBe DpdFloat.positiveZero }
    test("one has significand 1 and biasedExponent 101") {
        arith.one.significand shouldBe 1
        arith.one.biasedExponent shouldBe 101
    }

    // ── classification ────────────────────────────────────────────────────────

    context("isNaN") {
        test("NaN returns true") { with(arith) { DpdFloat.NaN.isNaN() } shouldBe true }
        test("infinity returns false") { with(arith) { DpdFloat.positiveInfinity.isNaN() } shouldBe false }
        test("finite returns false") { with(arith) { arith.one.isNaN() } shouldBe false }
        test("zero returns false") { with(arith) { arith.zero.isNaN() } shouldBe false }
    }

    context("isInfinite") {
        test("positiveInfinity returns true") { with(arith) { DpdFloat.positiveInfinity.isInfinite() } shouldBe true }
        test("negativeInfinity returns true") { with(arith) { DpdFloat.negativeInfinity.isInfinite() } shouldBe true }
        test("NaN returns false") { with(arith) { DpdFloat.NaN.isInfinite() } shouldBe false }
        test("finite returns false") { with(arith) { arith.one.isInfinite() } shouldBe false }
    }

    context("isFinite") {
        test("finite value returns true") { with(arith) { arith.one.isFinite() } shouldBe true }
        test("zero returns true") { with(arith) { arith.zero.isFinite() } shouldBe true }
        test("infinity returns false") { with(arith) { DpdFloat.positiveInfinity.isFinite() } shouldBe false }
        test("NaN returns false") { with(arith) { DpdFloat.NaN.isFinite() } shouldBe false }
    }

    context("isZero") {
        test("positiveZero returns true") { with(arith) { DpdFloat.positiveZero.isZero() } shouldBe true }
        test("negativeZero returns true") { with(arith) { DpdFloat.negativeZero.isZero() } shouldBe true }
        test("nonzero finite returns false") { with(arith) { arith.one.isZero() } shouldBe false }
        test("NaN returns false") { with(arith) { DpdFloat.NaN.isZero() } shouldBe false }
        test("infinity returns false") { with(arith) { DpdFloat.positiveInfinity.isZero() } shouldBe false }
    }

    context("isNegative") {
        test("negative finite returns true") { with(arith) { dpd(101, 1u, negative = true).isNegative() } shouldBe true }
        test("negativeZero returns true") { with(arith) { DpdFloat.negativeZero.isNegative() } shouldBe true }
        test("positive finite returns false") { with(arith) { arith.one.isNegative() } shouldBe false }
        test("positiveZero returns false") { with(arith) { DpdFloat.positiveZero.isNegative() } shouldBe false }
    }

    // ── sign operations ───────────────────────────────────────────────────────

    context("negate") {
        test("negating positive gives negative") {
            with(arith) { dpd(101, 5u).negate() }.sign shouldBe true
        }
        test("negating negative gives positive") {
            with(arith) { dpd(101, 5u, negative = true).negate() }.sign shouldBe false
        }
        test("negating +0 gives -0") {
            with(arith) { DpdFloat.positiveZero.negate() } shouldBe DpdFloat.negativeZero
        }
        test("negating -0 gives +0") {
            with(arith) { DpdFloat.negativeZero.negate() } shouldBe DpdFloat.positiveZero
        }
        test("double negation is identity") {
            val x = dpd(101, 5u)
            with(arith) { x.negate().negate() } shouldBe x
        }
        test("negating NaN produces NaN") {
            with(arith) { DpdFloat.NaN.negate() }.isNaN() shouldBe true
        }
    }

    context("abs") {
        test("abs of negative value clears sign") {
            with(arith) { dpd(101, 5u, negative = true).abs() }.sign shouldBe false
        }
        test("abs of positive value is unchanged") {
            val x = dpd(101, 5u)
            with(arith) { x.abs() } shouldBe x
        }
        test("abs of negativeZero is positiveZero") {
            with(arith) { DpdFloat.negativeZero.abs() } shouldBe DpdFloat.positiveZero
        }
        test("abs of NaN is NaN") {
            with(arith) { DpdFloat.NaN.abs() }.isNaN() shouldBe true
        }
    }

    // ── arithmetic ────────────────────────────────────────────────────────────

    context("add") {
        test("1 + 1 = 2") {
            val result = with(arith) { arith.one.add(arith.one) }
            result.significand shouldBe 2
            result.biasedExponent shouldBe 101
        }
        test("NaN + finite = NaN") {
            with(arith) { DpdFloat.NaN.add(arith.one) }.isNaN() shouldBe true
        }
        test("+inf + +inf = +inf") {
            with(arith) { DpdFloat.positiveInfinity.add(DpdFloat.positiveInfinity) } shouldBe DpdFloat.positiveInfinity
        }
        test("+inf + -inf = NaN") {
            with(arith) { DpdFloat.positiveInfinity.add(DpdFloat.negativeInfinity) }.isNaN() shouldBe true
        }
        test("-0 + -0 = -0") {
            with(arith) { DpdFloat.negativeZero.add(DpdFloat.negativeZero) } shouldBe DpdFloat.negativeZero
        }
        test("cancellation x + (-x) = +0") {
            val x = dpd(101, 5u)
            val negX = DpdFloat(x.bits xor Int.MIN_VALUE)
            with(arith) { x.add(negX) } shouldBe DpdFloat.positiveZero
        }
    }

    context("subtract") {
        test("5 - 3 = 2") {
            val result = with(arith) { dpd(101, 5u).subtract(dpd(101, 3u)) }
            result.significand shouldBe 2
            result.sign shouldBe false
        }
        test("3 - 5 = -2") {
            val result = with(arith) { dpd(101, 3u).subtract(dpd(101, 5u)) }
            result.sign shouldBe true
            result.significand shouldBe 2
        }
        test("NaN - finite = NaN") {
            with(arith) { DpdFloat.NaN.subtract(arith.one) }.isNaN() shouldBe true
        }
    }

    context("multiply") {
        test("3 × 4 = 12") {
            val result = with(arith) { dpd(101, 3u).multiply(dpd(101, 4u)) }
            result.significand shouldBe 12
        }
        test("0 × infinity = NaN") {
            with(arith) { DpdFloat.positiveZero.multiply(DpdFloat.positiveInfinity) }.isNaN() shouldBe true
        }
        test("negative × positive = negative") {
            with(arith) { dpd(101, 1u, negative = true).multiply(arith.one) }.sign shouldBe true
        }
        test("NaN × finite = NaN") {
            with(arith) { DpdFloat.NaN.multiply(arith.one) }.isNaN() shouldBe true
        }
    }

    context("divide") {
        test("10 / 5 = 2") {
            val result = with(arith) { dpd(101, 10u).divide(dpd(101, 5u)) }
            result.significand shouldBe 2
        }
        test("1 / 0 = +inf") {
            with(arith) { arith.one.divide(DpdFloat.positiveZero) } shouldBe DpdFloat.positiveInfinity
        }
        test("0 / 0 = NaN") {
            with(arith) { DpdFloat.positiveZero.divide(DpdFloat.positiveZero) }.isNaN() shouldBe true
        }
        test("finite / inf = +0") {
            val result = with(arith) { arith.one.divide(DpdFloat.positiveInfinity) }
            result.isZero() shouldBe true
            result.sign shouldBe false
        }
    }

    context("compareTo") {
        test("1 < 2") {
            (with(arith) { dpd(101, 1u).compareTo(dpd(101, 2u)) } < 0) shouldBe true
        }
        test("2 > 1") {
            (with(arith) { dpd(101, 2u).compareTo(dpd(101, 1u)) } > 0) shouldBe true
        }
        test("x == x") {
            val x = dpd(101, 5u)
            with(arith) { x.compareTo(x) } shouldBe 0
        }
        test("NaN compares after everything") {
            (with(arith) { DpdFloat.NaN.compareTo(DpdFloat.maxValue) } > 0) shouldBe true
        }
        test("consistent with DpdFloat.comparator") {
            val a = DpdFloat.minValue; val b = DpdFloat.maxValue
            with(arith) { a.compareTo(b) } shouldBe DpdFloat.comparator.compare(a, b)
        }
    }
})
