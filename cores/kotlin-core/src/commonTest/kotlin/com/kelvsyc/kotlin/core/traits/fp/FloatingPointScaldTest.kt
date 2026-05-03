package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.bidDouble64Pack
import com.kelvsyc.kotlin.core.bidFloat32Pack
import com.kelvsyc.kotlin.core.fp.bidDpdDouble
import com.kelvsyc.kotlin.core.fp.bidDpdFloat
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FloatingPointScaldTest : FunSpec({

    // ── BidFloat ──────────────────────────────────────────────────────────────

    context("FloatingPointScald.Companion.bidFloat") {
        val ops = FloatingPointScald.bidFloat

        test("positive value scaled up: biasedExponent increases") {
            // 123 × 10^0 (biasedExp=101) → 123 × 10^2 (biasedExp=103)
            val x = BidFloat(bidFloat32Pack(101, 123))
            val r = with(ops) { x.scald(2) }
            r.biasedExponent shouldBe 103
            r.significand shouldBe 123
            r.sign shouldBe false
        }
        test("negative value scaled up preserves sign") {
            val x = BidFloat(Int.MIN_VALUE or bidFloat32Pack(101, 123))
            val r = with(ops) { x.scald(2) }
            r.biasedExponent shouldBe 103
            r.significand shouldBe 123
            r.sign shouldBe true
        }
        test("positive value scaled down: biasedExponent decreases") {
            val x = BidFloat(bidFloat32Pack(103, 123))
            val r = with(ops) { x.scald(-2) }
            r.biasedExponent shouldBe 101
            r.significand shouldBe 123
        }
        test("overflow: biasedExponent > 191 → positive infinity") {
            val x = BidFloat(bidFloat32Pack(190, 1))
            val r = with(ops) { x.scald(5) }
            r.isInfinite() shouldBe true
            r.sign shouldBe false
        }
        test("overflow negative: biasedExponent > 191 → negative infinity") {
            val x = BidFloat(Int.MIN_VALUE or bidFloat32Pack(190, 1))
            val r = with(ops) { x.scald(5) }
            r.isInfinite() shouldBe true
            r.sign shouldBe true
        }
        test("underflow: biasedExponent < 0 → positive zero") {
            val x = BidFloat(bidFloat32Pack(1, 1))
            val r = with(ops) { x.scald(-5) }
            r.isZero() shouldBe true
            r.sign shouldBe false
        }
        test("underflow negative: biasedExponent < 0 → negative zero") {
            val x = BidFloat(Int.MIN_VALUE or bidFloat32Pack(1, 1))
            val r = with(ops) { x.scald(-5) }
            r.isZero() shouldBe true
            r.sign shouldBe true
        }
        test("NaN returns NaN") {
            with(ops) { BidFloat.NaN.scald(5) }.isNaN() shouldBe true
        }
        test("positive infinity returns positive infinity") {
            with(ops) { BidFloat.positiveInfinity.scald(5) }.isInfinite() shouldBe true
        }
        test("zero returns zero") {
            with(ops) { BidFloat.positiveZero.scald(5) }.isZero() shouldBe true
        }
    }

    // ── BidDouble ─────────────────────────────────────────────────────────────

    context("FloatingPointScald.Companion.bidDouble") {
        val ops = FloatingPointScald.bidDouble

        test("positive value scaled up: biasedExponent increases") {
            // 123 × 10^0 (biasedExp=398) → 123 × 10^2 (biasedExp=400)
            val x = BidDouble(bidDouble64Pack(398, 123L))
            val r = with(ops) { x.scald(2) }
            r.biasedExponent shouldBe 400
            r.significand shouldBe 123L
            r.sign shouldBe false
        }
        test("negative value scaled up preserves sign") {
            val x = BidDouble(Long.MIN_VALUE or bidDouble64Pack(398, 123L))
            val r = with(ops) { x.scald(2) }
            r.biasedExponent shouldBe 400
            r.significand shouldBe 123L
            r.sign shouldBe true
        }
        test("overflow: biasedExponent > 767 → positive infinity") {
            val x = BidDouble(bidDouble64Pack(765, 1L))
            val r = with(ops) { x.scald(5) }
            r.isInfinite() shouldBe true
            r.sign shouldBe false
        }
        test("underflow: biasedExponent < 0 → positive zero") {
            val x = BidDouble(bidDouble64Pack(2, 1L))
            val r = with(ops) { x.scald(-5) }
            r.isZero() shouldBe true
            r.sign shouldBe false
        }
        test("NaN returns NaN") {
            with(ops) { BidDouble.NaN.scald(5) }.isNaN() shouldBe true
        }
        test("positive infinity returns positive infinity") {
            with(ops) { BidDouble.positiveInfinity.scald(5) }.isInfinite() shouldBe true
        }
        test("zero returns zero") {
            with(ops) { BidDouble.positiveZero.scald(5) }.isZero() shouldBe true
        }
    }

    // ── DpdFloat ──────────────────────────────────────────────────────────────

    context("FloatingPointScald.Companion.dpdFloat") {
        val ops = FloatingPointScald.dpdFloat

        test("finite value scald round-trips through BID") {
            // 123 × 10^0 as DpdFloat; scald(2) → 123 × 10^2
            val bidSrc = BidFloat(bidFloat32Pack(101, 123))
            val dpdSrc = bidDpdFloat(bidSrc)
            val dpdResult = with(ops) { dpdSrc.scald(2) }
            val bidResult = bidDpdFloat.reverse(dpdResult)
            bidResult.biasedExponent shouldBe 103
            bidResult.significand shouldBe 123
        }
        test("NaN returns NaN") {
            with(ops) { bidDpdFloat(BidFloat.NaN).scald(5) }.let {
                bidDpdFloat.reverse(it).isNaN() shouldBe true
            }
        }
    }

    // ── DpdDouble ─────────────────────────────────────────────────────────────

    context("FloatingPointScald.Companion.dpdDouble") {
        val ops = FloatingPointScald.dpdDouble

        test("finite value scald round-trips through BID") {
            // 123 × 10^0 as DpdDouble; scald(2) → 123 × 10^2
            val bidSrc = BidDouble(bidDouble64Pack(398, 123L))
            val dpdSrc = bidDpdDouble(bidSrc)
            val dpdResult = with(ops) { dpdSrc.scald(2) }
            val bidResult = bidDpdDouble.reverse(dpdResult)
            bidResult.biasedExponent shouldBe 400
            bidResult.significand shouldBe 123L
        }
        test("NaN returns NaN") {
            with(ops) { bidDpdDouble(BidDouble.NaN).scald(5) }.let {
                bidDpdDouble.reverse(it).isNaN() shouldBe true
            }
        }
    }
})
