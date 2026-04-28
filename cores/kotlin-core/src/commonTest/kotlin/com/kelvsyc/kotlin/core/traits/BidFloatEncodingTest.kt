package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.BidFloat
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BidFloatEncodingTest : FunSpec({
    val enc = BidFloat.encoding

    // ── isCanonical ────────────────────────────────────────────────────────────

    context("BidFloat.encoding.isCanonical") {
        test("positiveZero is canonical") {
            with(enc) { BidFloat.positiveZero.isCanonical() } shouldBe true
        }
        test("negativeZero is canonical") {
            with(enc) { BidFloat.negativeZero.isCanonical() } shouldBe true
        }
        test("canonical quiet NaN (0x7E000000) is canonical") {
            with(enc) { BidFloat.NaN.isCanonical() } shouldBe true
        }
        test("NaN with non-zero continuation is not canonical") {
            with(enc) { BidFloat(0x7E000001) .isCanonical() } shouldBe false
        }
        test("sNaN (G[5]=0) is not canonical") {
            // sNaN: combination[10..6]=11111, combination[5]=0 → combination = 0x7C0.
            with(enc) { BidFloat(0x7C000000) .isCanonical() } shouldBe false
        }
        test("positive infinity is canonical") {
            with(enc) { BidFloat.positiveInfinity.isCanonical() } shouldBe true
        }
        test("negative infinity is canonical") {
            with(enc) { BidFloat.negativeInfinity.isCanonical() } shouldBe true
        }
        test("all standard constants are canonical") {
            listOf(BidFloat.maxValue, BidFloat.minNormal, BidFloat.minValue, BidFloat.epsilon).forEach { v ->
                with(enc) { v.isCanonical() } shouldBe true
            }
        }
        test("large-sig finite with significand 9_000_000 (≤ 9_999_999) is canonical") {
            // 0x6CA95440 = 9_000_000 × 10^0, from the round-trip test
            with(enc) { BidFloat(0x6CA95440) .isCanonical() } shouldBe true
        }
        test("large-sig finite with significand > 9_999_999 is not canonical") {
            // combination top-2=11 (large-sig): combination = 0x6CB.
            // significand = 0x800000 | low21. For bits = 0x6CBFFFFF:
            //   continuation = 0xFFFFF, combination[0] = 1 → low21 = 0x1FFFFF.
            //   sig = 0x800000 | 0x1FFFFF = 0x9FFFFF = 10,485,759 > 9,999,999.
            with(enc) { BidFloat(0x6CBFFFFF).isCanonical() } shouldBe false
        }
    }

    // ── canonical ─────────────────────────────────────────────────────────────

    context("BidFloat.encoding.canonical") {
        test("canonical value returns itself") {
            with(enc) { BidFloat.positiveZero.canonical() } shouldBe BidFloat.positiveZero
            with(enc) { BidFloat.NaN.canonical() } shouldBe BidFloat.NaN
            with(enc) { BidFloat.positiveInfinity.canonical() } shouldBe BidFloat.positiveInfinity
        }
        test("non-canonical NaN (non-zero payload) → canonical quiet NaN") {
            val nonCanonNaN = BidFloat(0x7E000001)
            with(enc) { nonCanonNaN.canonical() } shouldBe BidFloat.NaN
        }
        test("non-canonical NaN, negative sign → negative canonical NaN") {
            val negNaN = BidFloat(Int.MIN_VALUE or 0x7E000001)
            val result = with(enc) { negNaN.canonical() }
            result.bits shouldBe (Int.MIN_VALUE or 0x7E000000)
        }
        test("sNaN → canonical quiet NaN (positive sign)") {
            with(enc) { BidFloat(0x7C000000).canonical() } shouldBe BidFloat.NaN
        }
        test("non-canonical large-sig finite → positive zero") {
            with(enc) { BidFloat(0x6CBFFFFF).canonical() } shouldBe BidFloat.positiveZero
        }
        test("non-canonical large-sig finite, negative sign → negative zero") {
            val nonCanon = BidFloat(Int.MIN_VALUE or 0x6CBFFFFF)
            with(enc) { nonCanon.canonical() } shouldBe BidFloat.negativeZero
        }
    }

    // ── isQuietNaN / isSignalingNaN ───────────────────────────────────────────

    context("BidFloat.encoding quiet/signaling NaN") {
        test("canonical NaN is quiet") {
            with(enc) { BidFloat.NaN.isQuietNaN() } shouldBe true
            with(enc) { BidFloat.NaN.isSignalingNaN() } shouldBe false
        }
        test("sNaN (G[5]=0) is signaling, not quiet") {
            val sNaN = BidFloat(0x7C000000)
            with(enc) { sNaN.isQuietNaN() } shouldBe false
            with(enc) { sNaN.isSignalingNaN() } shouldBe true
        }
        test("non-NaN values are neither quiet nor signaling NaN") {
            listOf(BidFloat.positiveZero, BidFloat.positiveInfinity, BidFloat.maxValue).forEach { v ->
                with(enc) { v.isQuietNaN() } shouldBe false
                with(enc) { v.isSignalingNaN() } shouldBe false
            }
        }
    }
})
