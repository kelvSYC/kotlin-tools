package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.floats.shouldBeNaN
import io.kotest.matchers.shouldBe

class FloatingPointIeee754ExpLogTest : FunSpec({

    // ── FloatingPointIeee754ExpLog.Companion.double ───────────────────────────

    context("FloatingPointIeee754ExpLog.Companion.double") {
        val trait = FloatingPointIeee754ExpLog.double

        context("exp2m1") {
            test("exp2m1(0) = 0") { with(trait) { 0.0.exp2m1() } shouldBe 0.0 }
            test("exp2m1(1) = 1") { with(trait) { 1.0.exp2m1() } shouldBe 1.0 }
            test("exp2m1(-1) = -0.5") { with(trait) { (-1.0).exp2m1() } shouldBe -0.5 }
            test("exp2m1(NaN) = NaN") { with(trait) { Double.NaN.exp2m1() }.shouldBeNaN() }
            test("exp2m1(+∞) = +∞") { with(trait) { Double.POSITIVE_INFINITY.exp2m1() } shouldBe Double.POSITIVE_INFINITY }
            test("exp2m1(-∞) = -1") { with(trait) { Double.NEGATIVE_INFINITY.exp2m1() } shouldBe -1.0 }
        }

        context("exp10m1") {
            test("exp10m1(0) = 0") { with(trait) { 0.0.exp10m1() } shouldBe 0.0 }
            test("exp10m1(1) = 9") { with(trait) { 1.0.exp10m1() } shouldBe 9.0 }
            test("exp10m1(NaN) = NaN") { with(trait) { Double.NaN.exp10m1() }.shouldBeNaN() }
            test("exp10m1(+∞) = +∞") { with(trait) { Double.POSITIVE_INFINITY.exp10m1() } shouldBe Double.POSITIVE_INFINITY }
            test("exp10m1(-∞) = -1") { with(trait) { Double.NEGATIVE_INFINITY.exp10m1() } shouldBe -1.0 }
        }

        context("log2p1") {
            test("log2p1(0) = 0") { with(trait) { 0.0.log2p1() } shouldBe 0.0 }
            test("log2p1(1) = 1") { with(trait) { 1.0.log2p1() } shouldBe 1.0 }
            test("log2p1(3) = 2") { with(trait) { 3.0.log2p1() } shouldBe 2.0 }
            test("log2p1(NaN) = NaN") { with(trait) { Double.NaN.log2p1() }.shouldBeNaN() }
            test("log2p1(-1) = -∞") { with(trait) { (-1.0).log2p1() } shouldBe Double.NEGATIVE_INFINITY }
            test("log2p1(+∞) = +∞") { with(trait) { Double.POSITIVE_INFINITY.log2p1() } shouldBe Double.POSITIVE_INFINITY }
            test("log2p1(x < -1) = NaN") { with(trait) { (-2.0).log2p1() }.shouldBeNaN() }
        }

        context("log10p1") {
            test("log10p1(0) = 0") { with(trait) { 0.0.log10p1() } shouldBe 0.0 }
            // log10p1(9) = log10(10) = 1 exactly, but ln1p(9)*LOG10E is ≤ 2 ULP not guaranteed exact
            test("log10p1(9) within 2 ULP of 1") {
                val result = with(trait) { 9.0.log10p1() }
                (kotlin.math.abs(result - 1.0) / java.lang.Math.ulp(1.0) < 3.0) shouldBe true
            }
            test("log10p1(NaN) = NaN") { with(trait) { Double.NaN.log10p1() }.shouldBeNaN() }
            test("log10p1(-1) = -∞") { with(trait) { (-1.0).log10p1() } shouldBe Double.NEGATIVE_INFINITY }
            test("log10p1(+∞) = +∞") { with(trait) { Double.POSITIVE_INFINITY.log10p1() } shouldBe Double.POSITIVE_INFINITY }
            test("log10p1(x < -1) = NaN") { with(trait) { (-2.0).log10p1() }.shouldBeNaN() }
        }
    }

    // ── FloatingPointIeee754ExpLog.Companion.float ────────────────────────────

    context("FloatingPointIeee754ExpLog.Companion.float") {
        val trait = FloatingPointIeee754ExpLog.float

        context("exp2m1") {
            test("exp2m1(0) = 0") { with(trait) { 0.0f.exp2m1() } shouldBe 0.0f }
            test("exp2m1(1) = 1") { with(trait) { 1.0f.exp2m1() } shouldBe 1.0f }
            test("exp2m1(-1) = -0.5") { with(trait) { (-1.0f).exp2m1() } shouldBe -0.5f }
            test("exp2m1(NaN) = NaN") { with(trait) { Float.NaN.exp2m1() }.shouldBeNaN() }
            test("exp2m1(+∞) = +∞") { with(trait) { Float.POSITIVE_INFINITY.exp2m1() } shouldBe Float.POSITIVE_INFINITY }
            test("exp2m1(-∞) = -1") { with(trait) { Float.NEGATIVE_INFINITY.exp2m1() } shouldBe -1.0f }
        }

        context("exp10m1") {
            test("exp10m1(0) = 0") { with(trait) { 0.0f.exp10m1() } shouldBe 0.0f }
            test("exp10m1(1) = 9") { with(trait) { 1.0f.exp10m1() } shouldBe 9.0f }
            test("exp10m1(NaN) = NaN") { with(trait) { Float.NaN.exp10m1() }.shouldBeNaN() }
            test("exp10m1(+∞) = +∞") { with(trait) { Float.POSITIVE_INFINITY.exp10m1() } shouldBe Float.POSITIVE_INFINITY }
            test("exp10m1(-∞) = -1") { with(trait) { Float.NEGATIVE_INFINITY.exp10m1() } shouldBe -1.0f }
        }

        context("log2p1") {
            test("log2p1(0) = 0") { with(trait) { 0.0f.log2p1() } shouldBe 0.0f }
            test("log2p1(1) = 1") { with(trait) { 1.0f.log2p1() } shouldBe 1.0f }
            test("log2p1(3) = 2") { with(trait) { 3.0f.log2p1() } shouldBe 2.0f }
            test("log2p1(NaN) = NaN") { with(trait) { Float.NaN.log2p1() }.shouldBeNaN() }
            test("log2p1(-1) = -∞") { with(trait) { (-1.0f).log2p1() } shouldBe Float.NEGATIVE_INFINITY }
            test("log2p1(+∞) = +∞") { with(trait) { Float.POSITIVE_INFINITY.log2p1() } shouldBe Float.POSITIVE_INFINITY }
            test("log2p1(x < -1) = NaN") { with(trait) { (-2.0f).log2p1() }.shouldBeNaN() }
        }

        context("log10p1") {
            test("log10p1(0) = 0") { with(trait) { 0.0f.log10p1() } shouldBe 0.0f }
            test("log10p1(9) = 1") { with(trait) { 9.0f.log10p1() } shouldBe 1.0f }
            test("log10p1(NaN) = NaN") { with(trait) { Float.NaN.log10p1() }.shouldBeNaN() }
            test("log10p1(-1) = -∞") { with(trait) { (-1.0f).log10p1() } shouldBe Float.NEGATIVE_INFINITY }
            test("log10p1(+∞) = +∞") { with(trait) { Float.POSITIVE_INFINITY.log10p1() } shouldBe Float.POSITIVE_INFINITY }
            test("log10p1(x < -1) = NaN") { with(trait) { (-2.0f).log10p1() }.shouldBeNaN() }
        }
    }

    // ── FloatingPointIeee754ExpLog.Companion.bfloat16 ────────────────────────

    context("FloatingPointIeee754ExpLog.Companion.bfloat16") {
        val trait = FloatingPointIeee754ExpLog.bfloat16

        context("exp2m1") {
            test("exp2m1(0) = 0") { with(trait) { BFloat16(0.0f).exp2m1() }.toFloat() shouldBe 0.0f }
            test("exp2m1(1) = 1") { with(trait) { BFloat16(1.0f).exp2m1() }.toFloat() shouldBe 1.0f }
            test("exp2m1(-1) = -0.5") { with(trait) { BFloat16(-1.0f).exp2m1() }.toFloat() shouldBe -0.5f }
            test("exp2m1(NaN) = NaN") { with(trait) { BFloat16.NaN.exp2m1() }.isNaN() shouldBe true }
            test("exp2m1(+∞) = +∞") { with(trait) { BFloat16.POSITIVE_INFINITY.exp2m1() }.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
            test("exp2m1(-∞) = -1") { with(trait) { BFloat16.NEGATIVE_INFINITY.exp2m1() }.toFloat() shouldBe -1.0f }
        }

        context("exp10m1") {
            test("exp10m1(0) = 0") { with(trait) { BFloat16(0.0f).exp10m1() }.toFloat() shouldBe 0.0f }
            test("exp10m1(1) = 9") { with(trait) { BFloat16(1.0f).exp10m1() }.toFloat() shouldBe 9.0f }
            test("exp10m1(NaN) = NaN") { with(trait) { BFloat16.NaN.exp10m1() }.isNaN() shouldBe true }
            test("exp10m1(+∞) = +∞") { with(trait) { BFloat16.POSITIVE_INFINITY.exp10m1() }.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
            test("exp10m1(-∞) = -1") { with(trait) { BFloat16.NEGATIVE_INFINITY.exp10m1() }.toFloat() shouldBe -1.0f }
        }

        context("log2p1") {
            test("log2p1(0) = 0") { with(trait) { BFloat16(0.0f).log2p1() }.toFloat() shouldBe 0.0f }
            test("log2p1(1) = 1") { with(trait) { BFloat16(1.0f).log2p1() }.toFloat() shouldBe 1.0f }
            test("log2p1(3) = 2") { with(trait) { BFloat16(3.0f).log2p1() }.toFloat() shouldBe 2.0f }
            test("log2p1(NaN) = NaN") { with(trait) { BFloat16.NaN.log2p1() }.isNaN() shouldBe true }
            test("log2p1(-1) = -∞") { with(trait) { BFloat16(-1.0f).log2p1() }.bits shouldBe BFloat16.NEGATIVE_INFINITY.bits }
            test("log2p1(+∞) = +∞") { with(trait) { BFloat16.POSITIVE_INFINITY.log2p1() }.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
        }

        context("log10p1") {
            test("log10p1(0) = 0") { with(trait) { BFloat16(0.0f).log10p1() }.toFloat() shouldBe 0.0f }
            test("log10p1(9) = 1") { with(trait) { BFloat16(9.0f).log10p1() }.toFloat() shouldBe 1.0f }
            test("log10p1(NaN) = NaN") { with(trait) { BFloat16.NaN.log10p1() }.isNaN() shouldBe true }
            test("log10p1(-1) = -∞") { with(trait) { BFloat16(-1.0f).log10p1() }.bits shouldBe BFloat16.NEGATIVE_INFINITY.bits }
            test("log10p1(+∞) = +∞") { with(trait) { BFloat16.POSITIVE_INFINITY.log10p1() }.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
        }
    }

    // ── FloatingPointIeee754ExpLog.Companion.float16 ─────────────────────────

    context("FloatingPointIeee754ExpLog.Companion.float16") {
        val trait = FloatingPointIeee754ExpLog.float16

        context("exp2m1") {
            test("exp2m1(0) = 0") { with(trait) { Float16(0.0f).exp2m1() }.toFloat() shouldBe 0.0f }
            test("exp2m1(1) = 1") { with(trait) { Float16(1.0f).exp2m1() }.toFloat() shouldBe 1.0f }
            test("exp2m1(-1) = -0.5") { with(trait) { Float16(-1.0f).exp2m1() }.toFloat() shouldBe -0.5f }
            test("exp2m1(NaN) = NaN") { with(trait) { Float16.NaN.exp2m1() }.isNaN() shouldBe true }
            test("exp2m1(+∞) = +∞") { with(trait) { Float16.POSITIVE_INFINITY.exp2m1() }.bits shouldBe Float16.POSITIVE_INFINITY.bits }
            test("exp2m1(-∞) = -1") { with(trait) { Float16.NEGATIVE_INFINITY.exp2m1() }.toFloat() shouldBe -1.0f }
        }

        context("exp10m1") {
            test("exp10m1(0) = 0") { with(trait) { Float16(0.0f).exp10m1() }.toFloat() shouldBe 0.0f }
            test("exp10m1(1) = 9") { with(trait) { Float16(1.0f).exp10m1() }.toFloat() shouldBe 9.0f }
            test("exp10m1(NaN) = NaN") { with(trait) { Float16.NaN.exp10m1() }.isNaN() shouldBe true }
            test("exp10m1(+∞) = +∞") { with(trait) { Float16.POSITIVE_INFINITY.exp10m1() }.bits shouldBe Float16.POSITIVE_INFINITY.bits }
            test("exp10m1(-∞) = -1") { with(trait) { Float16.NEGATIVE_INFINITY.exp10m1() }.toFloat() shouldBe -1.0f }
        }

        context("log2p1") {
            test("log2p1(0) = 0") { with(trait) { Float16(0.0f).log2p1() }.toFloat() shouldBe 0.0f }
            test("log2p1(1) = 1") { with(trait) { Float16(1.0f).log2p1() }.toFloat() shouldBe 1.0f }
            test("log2p1(3) = 2") { with(trait) { Float16(3.0f).log2p1() }.toFloat() shouldBe 2.0f }
            test("log2p1(NaN) = NaN") { with(trait) { Float16.NaN.log2p1() }.isNaN() shouldBe true }
            test("log2p1(-1) = -∞") { with(trait) { Float16(-1.0f).log2p1() }.bits shouldBe Float16.NEGATIVE_INFINITY.bits }
            test("log2p1(+∞) = +∞") { with(trait) { Float16.POSITIVE_INFINITY.log2p1() }.bits shouldBe Float16.POSITIVE_INFINITY.bits }
        }

        context("log10p1") {
            test("log10p1(0) = 0") { with(trait) { Float16(0.0f).log10p1() }.toFloat() shouldBe 0.0f }
            test("log10p1(9) = 1") { with(trait) { Float16(9.0f).log10p1() }.toFloat() shouldBe 1.0f }
            test("log10p1(NaN) = NaN") { with(trait) { Float16.NaN.log10p1() }.isNaN() shouldBe true }
            test("log10p1(-1) = -∞") { with(trait) { Float16(-1.0f).log10p1() }.bits shouldBe Float16.NEGATIVE_INFINITY.bits }
            test("log10p1(+∞) = +∞") { with(trait) { Float16.POSITIVE_INFINITY.log10p1() }.bits shouldBe Float16.POSITIVE_INFINITY.bits }
        }
    }
})
