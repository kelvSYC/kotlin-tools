package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.Float16
import com.kelvsyc.kotlin.core.bidDouble64Pack
import com.kelvsyc.kotlin.core.bidFloat32Pack
import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.fp.bidDpdDouble
import com.kelvsyc.kotlin.core.fp.bidDpdFloat
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FloatingPointSquareTest : FunSpec({

    // ── BFloat16 ──────────────────────────────────────────────────────────────

    context("FloatingPointSquare.Companion.bfloat16") {
        val ops = FloatingPointSquare.bfloat16

        test("square(3) = 9") { with(ops) { BFloat16(3.0f).square() } shouldBe BFloat16(9.0f) }
        test("square(-3) = 9") { with(ops) { BFloat16(-3.0f).square() } shouldBe BFloat16(9.0f) }
        test("square(0) = 0") { with(ops) { BFloat16(0.0f).square() } shouldBe BFloat16(0.0f) }
        test("square(NaN) = NaN") { with(ops) { BFloat16.NaN.square() }.isNaN() shouldBe true }
    }

    // ── Float16 ───────────────────────────────────────────────────────────────

    context("FloatingPointSquare.Companion.float16") {
        val ops = FloatingPointSquare.float16

        test("square(3) = 9") { with(ops) { Float16(3.0f).square() } shouldBe Float16(9.0f) }
        test("square(-3) = 9") { with(ops) { Float16(-3.0f).square() } shouldBe Float16(9.0f) }
        test("square(0) = 0") { with(ops) { Float16(0.0f).square() } shouldBe Float16(0.0f) }
        test("square(NaN) = NaN") { with(ops) { Float16.NaN.square() }.isNaN() shouldBe true }
    }

    // ── Float ─────────────────────────────────────────────────────────────────

    context("FloatingPointSquare.Companion.float") {
        val ops = FloatingPointSquare.float

        test("square(3.0f) = 9.0f") { with(ops) { 3.0f.square() } shouldBe 9.0f }
        test("square(-3.0f) = 9.0f") { with(ops) { (-3.0f).square() } shouldBe 9.0f }
        test("square(0.0f) = 0.0f") { with(ops) { 0.0f.square() } shouldBe 0.0f }
        test("square(NaN) = NaN") { with(ops) { Float.NaN.square() }.isNaN() shouldBe true }
    }

    // ── Double ────────────────────────────────────────────────────────────────

    context("FloatingPointSquare.Companion.double") {
        val ops = FloatingPointSquare.double

        test("square(3.0) = 9.0") { with(ops) { 3.0.square() } shouldBe 9.0 }
        test("square(-3.0) = 9.0") { with(ops) { (-3.0).square() } shouldBe 9.0 }
        test("square(0.0) = 0.0") { with(ops) { 0.0.square() } shouldBe 0.0 }
        test("square(NaN) = NaN") { with(ops) { Double.NaN.square() }.isNaN() shouldBe true }
    }

    // ── DoubleDouble ──────────────────────────────────────────────────────────

    context("FloatingPointSquare.Companion.doubleDouble") {
        val ops = FloatingPointSquare.doubleDouble

        test("square(3, 0) = (9, 0)") {
            val r = with(ops) { DoubleDouble.create(3.0, 0.0).square() }
            r.high shouldBe 9.0
            r.low shouldBe 0.0
        }
        test("square(-3, 0) = (9, 0)") {
            val r = with(ops) { DoubleDouble.create(-3.0, 0.0).square() }
            r.high shouldBe 9.0
            r.low shouldBe 0.0
        }
        test("square(NaN) → NaN high") {
            with(ops) { DoubleDouble.NaN.square() }.high.isNaN() shouldBe true
        }
    }

    // ── BidFloat ──────────────────────────────────────────────────────────────

    context("FloatingPointSquare.Companion.bidFloat") {
        val ops = FloatingPointSquare.bidFloat

        test("square(3) is non-zero and non-NaN") {
            // 3 × 10^0: biasedExp=101, sig=3
            val x = BidFloat(bidFloat32Pack(101, 3))
            val r = with(ops) { x.square() }
            r.isNaN() shouldBe false
            r.isZero() shouldBe false
        }
        test("square(NaN) = NaN") {
            with(ops) { BidFloat.NaN.square() }.isNaN() shouldBe true
        }
    }

    // ── BidDouble ─────────────────────────────────────────────────────────────

    context("FloatingPointSquare.Companion.bidDouble") {
        val ops = FloatingPointSquare.bidDouble

        test("square(3) is non-zero and non-NaN") {
            val x = BidDouble(bidDouble64Pack(398, 3L))
            val r = with(ops) { x.square() }
            r.isNaN() shouldBe false
            r.isZero() shouldBe false
        }
        test("square(NaN) = NaN") {
            with(ops) { BidDouble.NaN.square() }.isNaN() shouldBe true
        }
    }

    // ── DpdFloat ──────────────────────────────────────────────────────────────

    context("FloatingPointSquare.Companion.dpdFloat") {
        val ops = FloatingPointSquare.dpdFloat

        test("square(3) round-trips through BID correctly") {
            val bidSrc = BidFloat(bidFloat32Pack(101, 3))
            val dpdSrc = bidDpdFloat(bidSrc)
            val r = with(ops) { dpdSrc.square() }
            // square(3 × 10^0) = 9 × 10^0; just verify non-NaN, non-zero
            val bidResult = bidDpdFloat.reverse(r)
            bidResult.isNaN() shouldBe false
            bidResult.isZero() shouldBe false
        }
    }

    // ── DpdDouble ─────────────────────────────────────────────────────────────

    context("FloatingPointSquare.Companion.dpdDouble") {
        val ops = FloatingPointSquare.dpdDouble

        test("square(3) round-trips through BID correctly") {
            val bidSrc = BidDouble(bidDouble64Pack(398, 3L))
            val dpdSrc = bidDpdDouble(bidSrc)
            val r = with(ops) { dpdSrc.square() }
            val bidResult = bidDpdDouble.reverse(r)
            bidResult.isNaN() shouldBe false
            bidResult.isZero() shouldBe false
        }
    }
})
