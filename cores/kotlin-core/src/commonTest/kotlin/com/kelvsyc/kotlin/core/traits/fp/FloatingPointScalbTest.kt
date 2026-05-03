package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FloatingPointScalbTest : FunSpec({

    // ── BFloat16 ──────────────────────────────────────────────────────────────

    context("FloatingPointScalb.Companion.bfloat16") {
        val ops = FloatingPointScalb.bfloat16

        test("positive value scaled up") {
            with(ops) { BFloat16(2.0f).scalb(1) } shouldBe BFloat16(4.0f)
        }
        test("positive value scaled down") {
            with(ops) { BFloat16(4.0f).scalb(-1) } shouldBe BFloat16(2.0f)
        }
        test("negative value scaled up") {
            with(ops) { BFloat16(-2.0f).scalb(1) } shouldBe BFloat16(-4.0f)
        }
        test("NaN returns NaN") {
            with(ops) { BFloat16.NaN.scalb(3) }.isNaN() shouldBe true
        }
        test("positive infinity returns positive infinity") {
            with(ops) { BFloat16.POSITIVE_INFINITY.scalb(3) } shouldBe BFloat16.POSITIVE_INFINITY
        }
        test("negative infinity returns negative infinity") {
            with(ops) { BFloat16.NEGATIVE_INFINITY.scalb(3) } shouldBe BFloat16.NEGATIVE_INFINITY
        }
    }

    // ── Float16 ───────────────────────────────────────────────────────────────

    context("FloatingPointScalb.Companion.float16") {
        val ops = FloatingPointScalb.float16

        test("positive value scaled up") {
            with(ops) { Float16(2.0f).scalb(1) } shouldBe Float16(4.0f)
        }
        test("positive value scaled down") {
            with(ops) { Float16(4.0f).scalb(-1) } shouldBe Float16(2.0f)
        }
        test("negative value scaled up") {
            with(ops) { Float16(-2.0f).scalb(1) } shouldBe Float16(-4.0f)
        }
        test("NaN returns NaN") {
            with(ops) { Float16.NaN.scalb(3) }.isNaN() shouldBe true
        }
        test("positive infinity returns positive infinity") {
            with(ops) { Float16.POSITIVE_INFINITY.scalb(3) } shouldBe Float16.POSITIVE_INFINITY
        }
        test("negative infinity returns negative infinity") {
            with(ops) { Float16.NEGATIVE_INFINITY.scalb(3) } shouldBe Float16.NEGATIVE_INFINITY
        }
    }

    // ── Float ─────────────────────────────────────────────────────────────────

    context("FloatingPointScalb.Companion.float") {
        val ops = FloatingPointScalb.float

        test("positive value scaled up") {
            with(ops) { 2.0f.scalb(1) } shouldBe 4.0f
        }
        test("positive value scaled down") {
            with(ops) { 4.0f.scalb(-1) } shouldBe 2.0f
        }
        test("negative value scaled up") {
            with(ops) { (-2.0f).scalb(1) } shouldBe -4.0f
        }
        test("NaN returns NaN") {
            with(ops) { Float.NaN.scalb(3) }.isNaN() shouldBe true
        }
        test("positive infinity returns positive infinity") {
            with(ops) { Float.POSITIVE_INFINITY.scalb(3) } shouldBe Float.POSITIVE_INFINITY
        }
        test("negative infinity returns negative infinity") {
            with(ops) { Float.NEGATIVE_INFINITY.scalb(3) } shouldBe Float.NEGATIVE_INFINITY
        }
    }

    // ── Double ────────────────────────────────────────────────────────────────

    context("FloatingPointScalb.Companion.double") {
        val ops = FloatingPointScalb.double

        test("positive value scaled up") {
            with(ops) { 2.0.scalb(1) } shouldBe 4.0
        }
        test("positive value scaled down") {
            with(ops) { 4.0.scalb(-1) } shouldBe 2.0
        }
        test("negative value scaled up") {
            with(ops) { (-2.0).scalb(1) } shouldBe -4.0
        }
        test("NaN returns NaN") {
            with(ops) { Double.NaN.scalb(3) }.isNaN() shouldBe true
        }
        test("positive infinity returns positive infinity") {
            with(ops) { Double.POSITIVE_INFINITY.scalb(3) } shouldBe Double.POSITIVE_INFINITY
        }
        test("negative infinity returns negative infinity") {
            with(ops) { Double.NEGATIVE_INFINITY.scalb(3) } shouldBe Double.NEGATIVE_INFINITY
        }
    }

})
