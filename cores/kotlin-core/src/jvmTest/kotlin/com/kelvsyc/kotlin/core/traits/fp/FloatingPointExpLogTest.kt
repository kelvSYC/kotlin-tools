package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.floats.shouldBeNaN
import io.kotest.matchers.shouldBe

class FloatingPointExpLogTest : FunSpec({

    // ── FloatingPointExpLog.Companion.double ──────────────────────────────────

    context("FloatingPointExpLog.Companion.double") {
        val trait = FloatingPointExpLog.double

        context("special values") {
            test("exp(NaN) = NaN") { with(trait) { Double.NaN.exp() }.shouldBeNaN() }
            test("exp(+∞) = +∞") { with(trait) { Double.POSITIVE_INFINITY.exp() } shouldBe Double.POSITIVE_INFINITY }
            test("exp(-∞) = 0") { with(trait) { Double.NEGATIVE_INFINITY.exp() } shouldBe 0.0 }
            test("ln(+∞) = +∞") { with(trait) { Double.POSITIVE_INFINITY.ln() } shouldBe Double.POSITIVE_INFINITY }
            test("ln(0) = -∞") { with(trait) { 0.0.ln() } shouldBe Double.NEGATIVE_INFINITY }
            test("ln(NaN) = NaN") { with(trait) { Double.NaN.ln() }.shouldBeNaN() }
        }

        context("exact results") {
            test("exp(0) = 1") { with(trait) { 0.0.exp() } shouldBe 1.0 }
            test("expm1(0) = 0") { with(trait) { 0.0.expm1() } shouldBe 0.0 }
            test("ln(1) = 0") { with(trait) { 1.0.ln() } shouldBe 0.0 }
            test("ln1p(0) = 0") { with(trait) { 0.0.ln1p() } shouldBe 0.0 }
            test("log2(4) = 2") { with(trait) { 4.0.log2() } shouldBe 2.0 }
            test("log2(8) = 3") { with(trait) { 8.0.log2() } shouldBe 3.0 }
            test("log10(100) = 2") { with(trait) { 100.0.log10() } shouldBe 2.0 }
            test("pow(2, 10) = 1024") { with(trait) { 2.0.pow(10.0) } shouldBe 1024.0 }
        }

    }

    // ── FloatingPointExpLog.Companion.float ───────────────────────────────────

    context("FloatingPointExpLog.Companion.float") {
        val trait = FloatingPointExpLog.float

        context("special values") {
            test("exp(NaN) = NaN") { with(trait) { Float.NaN.exp() }.shouldBeNaN() }
            test("exp(+∞) = +∞") { with(trait) { Float.POSITIVE_INFINITY.exp() } shouldBe Float.POSITIVE_INFINITY }
            test("exp(-∞) = 0") { with(trait) { Float.NEGATIVE_INFINITY.exp() } shouldBe 0.0f }
            test("ln(+∞) = +∞") { with(trait) { Float.POSITIVE_INFINITY.ln() } shouldBe Float.POSITIVE_INFINITY }
            test("ln(0) = -∞") { with(trait) { 0.0f.ln() } shouldBe Float.NEGATIVE_INFINITY }
            test("ln(NaN) = NaN") { with(trait) { Float.NaN.ln() }.shouldBeNaN() }
        }

        context("exact results") {
            test("exp(0) = 1") { with(trait) { 0.0f.exp() } shouldBe 1.0f }
            test("expm1(0) = 0") { with(trait) { 0.0f.expm1() } shouldBe 0.0f }
            test("ln(1) = 0") { with(trait) { 1.0f.ln() } shouldBe 0.0f }
            test("ln1p(0) = 0") { with(trait) { 0.0f.ln1p() } shouldBe 0.0f }
            test("log2(4) = 2") { with(trait) { 4.0f.log2() } shouldBe 2.0f }
            test("log2(8) = 3") { with(trait) { 8.0f.log2() } shouldBe 3.0f }
            test("log10(100) = 2") { with(trait) { 100.0f.log10() } shouldBe 2.0f }
            test("pow(2, 10) = 1024") { with(trait) { 2.0f.pow(10.0f) } shouldBe 1024.0f }
        }

    }

    // ── FloatingPointExpLog.Companion.bfloat16 ────────────────────────────────

    context("FloatingPointExpLog.Companion.bfloat16") {
        val trait = FloatingPointExpLog.bfloat16

        context("special values") {
            test("exp(NaN) = NaN") { with(trait) { BFloat16.NaN.exp() }.isNaN() shouldBe true }
            test("exp(+∞) = +∞") { with(trait) { BFloat16.POSITIVE_INFINITY.exp() }.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
            test("exp(-∞) = 0") { with(trait) { BFloat16.NEGATIVE_INFINITY.exp() }.toFloat() shouldBe 0.0f }
            test("ln(+∞) = +∞") { with(trait) { BFloat16.POSITIVE_INFINITY.ln() }.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
            test("ln(0) = -∞") { with(trait) { BFloat16(0.0f).ln() }.bits shouldBe BFloat16.NEGATIVE_INFINITY.bits }
            test("ln(NaN) = NaN") { with(trait) { BFloat16.NaN.ln() }.isNaN() shouldBe true }
        }

        context("exact results") {
            test("exp(0) = 1") { with(trait) { BFloat16(0.0f).exp() }.toFloat() shouldBe 1.0f }
            test("expm1(0) = 0") { with(trait) { BFloat16(0.0f).expm1() }.toFloat() shouldBe 0.0f }
            test("ln(1) = 0") { with(trait) { BFloat16(1.0f).ln() }.toFloat() shouldBe 0.0f }
            test("ln1p(0) = 0") { with(trait) { BFloat16(0.0f).ln1p() }.toFloat() shouldBe 0.0f }
            test("log2(4) = 2") { with(trait) { BFloat16(4.0f).log2() }.toFloat() shouldBe 2.0f }
            test("log10(100) = 2") { with(trait) { BFloat16(100.0f).log10() }.toFloat() shouldBe 2.0f }
            test("pow(2, 10) = 1024") { with(trait) { BFloat16(2.0f).pow(BFloat16(10.0f)) }.toFloat() shouldBe 1024.0f }
        }
    }

    // ── FloatingPointExpLog.Companion.float16 ─────────────────────────────────

    context("FloatingPointExpLog.Companion.float16") {
        val trait = FloatingPointExpLog.float16

        context("special values") {
            test("exp(NaN) = NaN") { with(trait) { Float16.NaN.exp() }.isNaN() shouldBe true }
            test("exp(+∞) = +∞") { with(trait) { Float16.POSITIVE_INFINITY.exp() }.bits shouldBe Float16.POSITIVE_INFINITY.bits }
            test("exp(-∞) = 0") { with(trait) { Float16.NEGATIVE_INFINITY.exp() }.toFloat() shouldBe 0.0f }
            test("ln(+∞) = +∞") { with(trait) { Float16.POSITIVE_INFINITY.ln() }.bits shouldBe Float16.POSITIVE_INFINITY.bits }
            test("ln(0) = -∞") { with(trait) { Float16(0.0f).ln() }.bits shouldBe Float16.NEGATIVE_INFINITY.bits }
            test("ln(NaN) = NaN") { with(trait) { Float16.NaN.ln() }.isNaN() shouldBe true }
        }

        context("exact results") {
            test("exp(0) = 1") { with(trait) { Float16(0.0f).exp() }.toFloat() shouldBe 1.0f }
            test("expm1(0) = 0") { with(trait) { Float16(0.0f).expm1() }.toFloat() shouldBe 0.0f }
            test("ln(1) = 0") { with(trait) { Float16(1.0f).ln() }.toFloat() shouldBe 0.0f }
            test("ln1p(0) = 0") { with(trait) { Float16(0.0f).ln1p() }.toFloat() shouldBe 0.0f }
            test("log2(4) = 2") { with(trait) { Float16(4.0f).log2() }.toFloat() shouldBe 2.0f }
            test("log10(100) = 2") { with(trait) { Float16(100.0f).log10() }.toFloat() shouldBe 2.0f }
            test("pow(2, 10) = 1024") { with(trait) { Float16(2.0f).pow(Float16(10.0f)) }.toFloat() shouldBe 1024.0f }
        }
    }
})
