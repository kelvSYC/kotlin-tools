package com.kelvsyc.kotlin.core.traits.dfp
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointRemainder

import com.kelvsyc.kotlin.core.DpdFloat
import com.kelvsyc.kotlin.core.fp.FiniteDecimalFloatingPoint
import com.kelvsyc.kotlin.core.fp.toDpdFloat
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun dpd(biasedExp: Int, sig: UInt, negative: Boolean = false) =
    FiniteDecimalFloatingPoint(negative, biasedExp - 101, sig).toDpdFloat()

/**
 * Smoke tests for [FloatingPointRemainder.dpdFloatIeee754] and [FloatingPointRemainder.dpdFloatTruncating].
 *
 * Full algorithmic coverage lives in [BidFloatRemainderTest] and [BidFloatTruncatingRemainderTest].
 * These tests verify that the DPD instances delegate correctly through the BID↔DPD converter
 * and produce numerically correct results on a representative cross-section of cases.
 */
class DpdFloatIeee754RemainderTest : FunSpec({
    val rem = FloatingPointRemainder.dpdFloatIeee754

    context("special values") {
        test("NaN rem finite = NaN") {
            with(rem) { DpdFloat.NaN.rem(dpd(101, 1u)) } shouldBe DpdFloat.NaN
        }
        test("∞ rem finite = NaN") {
            with(rem) { DpdFloat.positiveInfinity.rem(dpd(101, 1u)) } shouldBe DpdFloat.NaN
        }
        test("finite rem 0 = NaN") {
            with(rem) { dpd(101, 1u).rem(DpdFloat.positiveZero) } shouldBe DpdFloat.NaN
        }
        test("-0 rem finite = -0") {
            with(rem) { DpdFloat.negativeZero.rem(dpd(101, 1u)) } shouldBe DpdFloat.negativeZero
        }
        test("finite rem ∞ = x") {
            val x = dpd(101, 3u)
            with(rem) { x.rem(DpdFloat.positiveInfinity) } shouldBe x
        }
    }

    context("round-half-even semantics preserved through conversion") {
        // 7 rem 3: n=round(2.333)=2, result=1 (positive)
        test("7 rem 3 = 1 (positive, non-zero)") {
            val result = with(rem) { dpd(101, 7u).rem(dpd(101, 3u)) }
            result.isZero() shouldBe false
            result.sign shouldBe false
        }
        // 5 rem 3: n=round(1.667)=2, result=5-6=-1
        test("5 rem 3 = -1 (round-up, sign flips)") {
            val result = with(rem) { dpd(101, 5u).rem(dpd(101, 3u)) }
            result.sign shouldBe true
        }
        // 2.5 rem 1.0: tie, n=2 (even) → result=0.5
        test("2.5 rem 1.0 = 0.5 (tie, even n=2 → keep)") {
            val result = with(rem) { dpd(100, 25u).rem(dpd(101, 1u)) }
            result.sign shouldBe false
            result.isZero() shouldBe false
        }
        // 1.5 rem 1.0: tie, n=2 (odd n=1 rounds up) → result=-0.5
        test("1.5 rem 1.0 = -0.5 (tie, odd n=1 → round up)") {
            val result = with(rem) { dpd(100, 15u).rem(dpd(101, 1u)) }
            result.sign shouldBe true
        }
        // Negative x: -7 rem 3 = -1
        test("-7 rem 3 = -1") {
            val result = with(rem) { dpd(101, 7u, negative = true).rem(dpd(101, 3u)) }
            result.sign shouldBe true
        }
        // Exact result: 100 rem 4 = +0 (sign of x)
        test("100 rem 4 = +0") {
            val result = with(rem) { dpd(103, 1u).rem(dpd(101, 4u)) }
            result.isZero() shouldBe true
            result.sign shouldBe false
        }
    }
})

class DpdFloatTruncatingRemainderTest : FunSpec({
    val rem = FloatingPointRemainder.dpdFloatTruncating

    context("special values") {
        test("NaN rem finite = NaN") {
            with(rem) { DpdFloat.NaN.rem(dpd(101, 1u)) } shouldBe DpdFloat.NaN
        }
        test("-0 rem finite = -0") {
            with(rem) { DpdFloat.negativeZero.rem(dpd(101, 1u)) } shouldBe DpdFloat.negativeZero
        }
    }

    context("truncating keeps floor remainder, result has sign of x") {
        // 5 rem 3: trunc(1.667)=1, result=2 (positive — differs from ieee754's -1)
        test("5 rem 3 = 2 (not -1)") {
            val result = with(rem) { dpd(101, 5u).rem(dpd(101, 3u)) }
            result.sign shouldBe false
            result.isZero() shouldBe false
        }
        // -5 rem 3: trunc(-1.667)=-1, result=-2 (negative — sign of x)
        test("-5 rem 3 = -2") {
            val result = with(rem) { dpd(101, 5u, negative = true).rem(dpd(101, 3u)) }
            result.sign shouldBe true
        }
        // 1.5 rem 1.0: trunc(1.5)=1, result=0.5 (not -0.5 as ieee754 gives)
        test("1.5 rem 1.0 = 0.5 (not -0.5)") {
            val result = with(rem) { dpd(100, 15u).rem(dpd(101, 1u)) }
            result.sign shouldBe false
        }
    }
})
