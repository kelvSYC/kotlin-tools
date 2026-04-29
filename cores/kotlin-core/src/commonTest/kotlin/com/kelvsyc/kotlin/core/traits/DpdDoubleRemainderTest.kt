package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.DpdDouble
import com.kelvsyc.kotlin.core.fp.FiniteDecimalFloatingPoint
import com.kelvsyc.kotlin.core.fp.toDpdDouble
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun dpd64(biasedExp: Int, sig: ULong, negative: Boolean = false) =
    FiniteDecimalFloatingPoint(negative, biasedExp - 398, sig).toDpdDouble()

/**
 * Smoke tests for [FloatingPointRemainder.dpdDoubleIeee754] and [FloatingPointRemainder.dpdDoubleTruncating].
 *
 * Full algorithmic coverage lives in [BidDoubleIeee754RemainderTest] and [BidDoubleTruncatingRemainderTest].
 * These tests verify that the DPD instances delegate correctly through the BID↔DPD converter and
 * produce numerically correct results on a representative cross-section of cases.
 */
class DpdDoubleIeee754RemainderTest : FunSpec({
    val rem = FloatingPointRemainder.dpdDoubleIeee754

    context("special values") {
        test("NaN rem finite = NaN") {
            with(rem) { DpdDouble.NaN.rem(dpd64(398, 1uL)) } shouldBe DpdDouble.NaN
        }
        test("∞ rem finite = NaN") {
            with(rem) { DpdDouble.positiveInfinity.rem(dpd64(398, 1uL)) } shouldBe DpdDouble.NaN
        }
        test("finite rem 0 = NaN") {
            with(rem) { dpd64(398, 1uL).rem(DpdDouble.positiveZero) } shouldBe DpdDouble.NaN
        }
        test("-0 rem finite = -0") {
            with(rem) { DpdDouble.negativeZero.rem(dpd64(398, 1uL)) } shouldBe DpdDouble.negativeZero
        }
        test("finite rem ∞ = x") {
            val x = dpd64(398, 3uL)
            with(rem) { x.rem(DpdDouble.positiveInfinity) } shouldBe x
        }
    }

    context("round-half-even semantics preserved through conversion") {
        // 7 rem 3 = 1 (positive, non-zero)
        test("7 rem 3 = 1") {
            val result = with(rem) { dpd64(398, 7uL).rem(dpd64(398, 3uL)) }
            result.isZero() shouldBe false
            result.sign shouldBe false
        }
        // 5 rem 3: round-up → -1 (sign flips)
        test("5 rem 3 = -1 (round-up, sign flips)") {
            val result = with(rem) { dpd64(398, 5uL).rem(dpd64(398, 3uL)) }
            result.sign shouldBe true
        }
        // 2.5 rem 1.0: tie, n=2 (even) → result = 0.5
        test("2.5 rem 1.0 = 0.5 (tie, n=2 even → keep)") {
            val result = with(rem) { dpd64(397, 25uL).rem(dpd64(398, 1uL)) }
            result.sign shouldBe false
            result.isZero() shouldBe false
        }
        // 1.5 rem 1.0: tie, n=1 (odd) → round up → -0.5
        test("1.5 rem 1.0 = -0.5 (tie, n=1 odd → round up)") {
            val result = with(rem) { dpd64(397, 15uL).rem(dpd64(398, 1uL)) }
            result.sign shouldBe true
        }
        // Negative x: -7 rem 3 = -1
        test("-7 rem 3 = -1") {
            val result = with(rem) { dpd64(398, 7uL, negative = true).rem(dpd64(398, 3uL)) }
            result.sign shouldBe true
        }
        // Exact zero: 100 rem 4 = +0
        test("100 rem 4 = +0") {
            val result = with(rem) { dpd64(400, 1uL).rem(dpd64(398, 4uL)) }
            result.isZero() shouldBe true
            result.sign shouldBe false
        }
    }
})

class DpdDoubleTruncatingRemainderTest : FunSpec({
    val rem = FloatingPointRemainder.dpdDoubleTruncating

    context("special values") {
        test("NaN rem finite = NaN") {
            with(rem) { DpdDouble.NaN.rem(dpd64(398, 1uL)) } shouldBe DpdDouble.NaN
        }
        test("-0 rem finite = -0") {
            with(rem) { DpdDouble.negativeZero.rem(dpd64(398, 1uL)) } shouldBe DpdDouble.negativeZero
        }
    }

    context("truncating keeps floor remainder, result has sign of x") {
        // 5 rem 3: trunc(1.667)=1, result=2 (differs from ieee754's -1)
        test("5 rem 3 = 2 (not -1)") {
            val result = with(rem) { dpd64(398, 5uL).rem(dpd64(398, 3uL)) }
            result.sign shouldBe false
            result.isZero() shouldBe false
        }
        // -5 rem 3: trunc(-1.667)=-1, result=-2 (sign of x)
        test("-5 rem 3 = -2") {
            val result = with(rem) { dpd64(398, 5uL, negative = true).rem(dpd64(398, 3uL)) }
            result.sign shouldBe true
        }
        // 1.5 rem 1.0: trunc(1.5)=1, result=0.5 (not -0.5 as ieee754 gives)
        test("1.5 rem 1.0 = 0.5 (not -0.5)") {
            val result = with(rem) { dpd64(397, 15uL).rem(dpd64(398, 1uL)) }
            result.sign shouldBe false
        }
    }
})
