package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.floats.shouldBeNaN
import io.kotest.matchers.shouldBe

class FloatingPointHypotTest : FunSpec({

    // ── FloatingPointHypot.Companion.double ───────────────────────────────────

    context("FloatingPointHypot.Companion.double") {
        val trait = FloatingPointHypot.double

        context("special values") {
            test("hypot(NaN, 0) = NaN") { with(trait) { Double.NaN.hypot(0.0) }.shouldBeNaN() }
            test("hypot(0, NaN) = NaN") { with(trait) { 0.0.hypot(Double.NaN) }.shouldBeNaN() }
            test("hypot(+∞, NaN) = +∞") { with(trait) { Double.POSITIVE_INFINITY.hypot(Double.NaN) } shouldBe Double.POSITIVE_INFINITY }
            test("hypot(NaN, +∞) = +∞") { with(trait) { Double.NaN.hypot(Double.POSITIVE_INFINITY) } shouldBe Double.POSITIVE_INFINITY }
            test("hypot(+∞, 0) = +∞") { with(trait) { Double.POSITIVE_INFINITY.hypot(0.0) } shouldBe Double.POSITIVE_INFINITY }
            test("hypot(0, +∞) = +∞") { with(trait) { 0.0.hypot(Double.POSITIVE_INFINITY) } shouldBe Double.POSITIVE_INFINITY }
            test("hypot(-∞, 0) = +∞") { with(trait) { Double.NEGATIVE_INFINITY.hypot(0.0) } shouldBe Double.POSITIVE_INFINITY }
        }

        context("exact results") {
            test("hypot(3, 4) = 5") { with(trait) { 3.0.hypot(4.0) } shouldBe 5.0 }
            test("hypot(5, 12) = 13") { with(trait) { 5.0.hypot(12.0) } shouldBe 13.0 }
            test("hypot(0, y) = |y|") { with(trait) { 0.0.hypot(7.0) } shouldBe 7.0 }
            test("hypot(x, 0) = |x|") { with(trait) { (-7.0).hypot(0.0) } shouldBe 7.0 }
            test("hypot is symmetric") { with(trait) { 3.0.hypot(4.0) } shouldBe with(trait) { 4.0.hypot(3.0) } }
        }
    }

    // ── FloatingPointHypot.Companion.float ────────────────────────────────────

    context("FloatingPointHypot.Companion.float") {
        val trait = FloatingPointHypot.float

        context("special values") {
            test("hypot(NaN, 0) = NaN") { with(trait) { Float.NaN.hypot(0.0f) }.shouldBeNaN() }
            test("hypot(0, NaN) = NaN") { with(trait) { 0.0f.hypot(Float.NaN) }.shouldBeNaN() }
            test("hypot(+∞, NaN) = +∞") { with(trait) { Float.POSITIVE_INFINITY.hypot(Float.NaN) } shouldBe Float.POSITIVE_INFINITY }
            test("hypot(NaN, +∞) = +∞") { with(trait) { Float.NaN.hypot(Float.POSITIVE_INFINITY) } shouldBe Float.POSITIVE_INFINITY }
            test("hypot(+∞, 0) = +∞") { with(trait) { Float.POSITIVE_INFINITY.hypot(0.0f) } shouldBe Float.POSITIVE_INFINITY }
            test("hypot(0, +∞) = +∞") { with(trait) { 0.0f.hypot(Float.POSITIVE_INFINITY) } shouldBe Float.POSITIVE_INFINITY }
            test("hypot(-∞, 0) = +∞") { with(trait) { Float.NEGATIVE_INFINITY.hypot(0.0f) } shouldBe Float.POSITIVE_INFINITY }
        }

        context("exact results") {
            test("hypot(3, 4) = 5") { with(trait) { 3.0f.hypot(4.0f) } shouldBe 5.0f }
            test("hypot(5, 12) = 13") { with(trait) { 5.0f.hypot(12.0f) } shouldBe 13.0f }
            test("hypot(0, y) = |y|") { with(trait) { 0.0f.hypot(7.0f) } shouldBe 7.0f }
            test("hypot(x, 0) = |x|") { with(trait) { (-7.0f).hypot(0.0f) } shouldBe 7.0f }
            test("hypot is symmetric") { with(trait) { 3.0f.hypot(4.0f) } shouldBe with(trait) { 4.0f.hypot(3.0f) } }
        }
    }

    // ── FloatingPointHypot.Companion.bfloat16 ────────────────────────────────

    context("FloatingPointHypot.Companion.bfloat16") {
        val trait = FloatingPointHypot.bfloat16

        context("special values") {
            test("hypot(NaN, 0) = NaN") { with(trait) { BFloat16.NaN.hypot(BFloat16(0.0f)) }.isNaN() shouldBe true }
            test("hypot(+∞, 0) = +∞") { with(trait) { BFloat16.POSITIVE_INFINITY.hypot(BFloat16(0.0f)) }.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
            test("hypot(-∞, 0) = +∞") { with(trait) { BFloat16.NEGATIVE_INFINITY.hypot(BFloat16(0.0f)) }.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
        }

        context("exact results") {
            test("hypot(3, 4) = 5") { with(trait) { BFloat16(3.0f).hypot(BFloat16(4.0f)) }.toFloat() shouldBe 5.0f }
            test("hypot(0, y) = |y|") { with(trait) { BFloat16(0.0f).hypot(BFloat16(7.0f)) }.toFloat() shouldBe 7.0f }
            test("hypot(x, 0) = |x|") { with(trait) { BFloat16(-7.0f).hypot(BFloat16(0.0f)) }.toFloat() shouldBe 7.0f }
        }
    }

    // ── FloatingPointHypot.Companion.float16 ─────────────────────────────────

    context("FloatingPointHypot.Companion.float16") {
        val trait = FloatingPointHypot.float16

        context("special values") {
            test("hypot(NaN, 0) = NaN") { with(trait) { Float16.NaN.hypot(Float16(0.0f)) }.isNaN() shouldBe true }
            test("hypot(+∞, 0) = +∞") { with(trait) { Float16.POSITIVE_INFINITY.hypot(Float16(0.0f)) }.bits shouldBe Float16.POSITIVE_INFINITY.bits }
            test("hypot(-∞, 0) = +∞") { with(trait) { Float16.NEGATIVE_INFINITY.hypot(Float16(0.0f)) }.bits shouldBe Float16.POSITIVE_INFINITY.bits }
        }

        context("exact results") {
            test("hypot(3, 4) = 5") { with(trait) { Float16(3.0f).hypot(Float16(4.0f)) }.toFloat() shouldBe 5.0f }
            test("hypot(0, y) = |y|") { with(trait) { Float16(0.0f).hypot(Float16(7.0f)) }.toFloat() shouldBe 7.0f }
            test("hypot(x, 0) = |x|") { with(trait) { Float16(-7.0f).hypot(Float16(0.0f)) }.toFloat() shouldBe 7.0f }
        }
    }
})
