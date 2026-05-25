package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.floats.shouldBeNaN
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class FloatingPointExp2Test : FunSpec({

    // ── FloatingPointExp2.Companion.double ────────────────────────────────────

    context("FloatingPointExp2.Companion.double") {
        val trait = FloatingPointExp2.double

        // For integer x, Cody-Waite reduces to scalbn(exp(0), n) = 2^n exactly.
        context("exact integer powers of 2") {
            test("exp2(0) = 1") { with(trait) { 0.0.exp2() } shouldBe 1.0 }
            test("exp2(1) = 2") { with(trait) { 1.0.exp2() } shouldBe 2.0 }
            test("exp2(2) = 4") { with(trait) { 2.0.exp2() } shouldBe 4.0 }
            test("exp2(10) = 1024") { with(trait) { 10.0.exp2() } shouldBe 1024.0 }
            test("exp2(-1) = 0.5") { with(trait) { (-1.0).exp2() } shouldBe 0.5 }
            test("exp2(-2) = 0.25") { with(trait) { (-2.0).exp2() } shouldBe 0.25 }
        }

        context("special values") {
            test("exp2(NaN) = NaN") { with(trait) { Double.NaN.exp2() }.shouldBeNaN() }
            test("exp2(+∞) = +∞") { with(trait) { Double.POSITIVE_INFINITY.exp2() } shouldBe Double.POSITIVE_INFINITY }
            test("exp2(-∞) = 0") { with(trait) { Double.NEGATIVE_INFINITY.exp2() } shouldBe 0.0 }
            test("exp2(+0) = 1") { with(trait) { 0.0.exp2() } shouldBe 1.0 }
            test("exp2(-0) = 1") { with(trait) { (-0.0).exp2() } shouldBe 1.0 }
        }

        // For x = 100.0, Cody-Waite computes scalbn(exp(0.0 * LN2), 100) = 2^100 exactly.
        // The naive approach exp(100.0 * ln(2.0)) accumulates ~18 ULP error from the
        // floating-point representation of LN2, so it does not equal 2^100.
        context("distinguishing: Cody-Waite gives exact 2^100 where naive exp(x*LN2) diverges") {
            val exact2to100 = java.lang.Math.scalb(1.0, 100)   // 2^100, bit-exact

            test("exp2(100.0) = 2^100 exactly") {
                with(trait) { 100.0.exp2() } shouldBe exact2to100
            }

            test("naive exp(100.0 * ln(2.0)) is not exactly 2^100") {
                kotlin.math.exp(100.0 * kotlin.math.ln(2.0)) shouldNotBe exact2to100
            }
        }
    }

    // ── FloatingPointExp2.Companion.float ─────────────────────────────────────

    context("FloatingPointExp2.Companion.float") {
        val trait = FloatingPointExp2.float

        context("exact integer powers of 2") {
            test("exp2(0) = 1") { with(trait) { 0.0f.exp2() } shouldBe 1.0f }
            test("exp2(1) = 2") { with(trait) { 1.0f.exp2() } shouldBe 2.0f }
            test("exp2(2) = 4") { with(trait) { 2.0f.exp2() } shouldBe 4.0f }
            test("exp2(10) = 1024") { with(trait) { 10.0f.exp2() } shouldBe 1024.0f }
            test("exp2(-1) = 0.5") { with(trait) { (-1.0f).exp2() } shouldBe 0.5f }
            test("exp2(-2) = 0.25") { with(trait) { (-2.0f).exp2() } shouldBe 0.25f }
        }

        context("special values") {
            test("exp2(NaN) = NaN") { with(trait) { Float.NaN.exp2() }.shouldBeNaN() }
            test("exp2(+∞) = +∞") { with(trait) { Float.POSITIVE_INFINITY.exp2() } shouldBe Float.POSITIVE_INFINITY }
            test("exp2(-∞) = 0") { with(trait) { Float.NEGATIVE_INFINITY.exp2() } shouldBe 0.0f }
            test("exp2(+0) = 1") { with(trait) { 0.0f.exp2() } shouldBe 1.0f }
            test("exp2(-0) = 1") { with(trait) { (-0.0f).exp2() } shouldBe 1.0f }
        }

        // For x = 100.0f, Cody-Waite computes scalbn(exp(0.0f * LN2_F), 100) = 2^100f exactly.
        // The naive approach exp(100.0f * ln(2.0f)) accumulates ~31 ULP error.
        context("distinguishing: Cody-Waite gives exact 2^100f where naive exp(x*LN2) diverges") {
            val exact2to100f = java.lang.Math.scalb(1.0, 100).toFloat()  // 2^100, bit-exact

            test("exp2(100.0f) = 2^100f exactly") {
                with(trait) { 100.0f.exp2() } shouldBe exact2to100f
            }

            test("naive exp(100.0f * ln(2.0f)) is not exactly 2^100f") {
                kotlin.math.exp(100.0f * kotlin.math.ln(2.0f)) shouldNotBe exact2to100f
            }
        }
    }

    // ── FloatingPointExp2.Companion.bfloat16 ──────────────────────────────────

    context("FloatingPointExp2.Companion.bfloat16") {
        val trait = FloatingPointExp2.bfloat16

        context("exact integer powers of 2") {
            test("exp2(0) = 1") { with(trait) { BFloat16(0.0f).exp2() }.toFloat() shouldBe 1.0f }
            test("exp2(1) = 2") { with(trait) { BFloat16(1.0f).exp2() }.toFloat() shouldBe 2.0f }
            test("exp2(2) = 4") { with(trait) { BFloat16(2.0f).exp2() }.toFloat() shouldBe 4.0f }
            test("exp2(10) = 1024") { with(trait) { BFloat16(10.0f).exp2() }.toFloat() shouldBe 1024.0f }
            test("exp2(-1) = 0.5") { with(trait) { BFloat16(-1.0f).exp2() }.toFloat() shouldBe 0.5f }
            test("exp2(-2) = 0.25") { with(trait) { BFloat16(-2.0f).exp2() }.toFloat() shouldBe 0.25f }
        }

        context("special values") {
            test("exp2(NaN) = NaN") { with(trait) { BFloat16.NaN.exp2() }.isNaN() shouldBe true }
            test("exp2(+∞) = +∞") { with(trait) { BFloat16.POSITIVE_INFINITY.exp2() }.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
            test("exp2(-∞) = 0") { with(trait) { BFloat16.NEGATIVE_INFINITY.exp2() }.toFloat() shouldBe 0.0f }
            test("exp2(0) = 1") { with(trait) { BFloat16(0.0f).exp2() }.toFloat() shouldBe 1.0f }
        }
    }

    // ── FloatingPointExp2.Companion.float16 ───────────────────────────────────

    context("FloatingPointExp2.Companion.float16") {
        val trait = FloatingPointExp2.float16

        context("exact integer powers of 2") {
            test("exp2(0) = 1") { with(trait) { Float16(0.0f).exp2() }.toFloat() shouldBe 1.0f }
            test("exp2(1) = 2") { with(trait) { Float16(1.0f).exp2() }.toFloat() shouldBe 2.0f }
            test("exp2(2) = 4") { with(trait) { Float16(2.0f).exp2() }.toFloat() shouldBe 4.0f }
            test("exp2(10) = 1024") { with(trait) { Float16(10.0f).exp2() }.toFloat() shouldBe 1024.0f }
            test("exp2(-1) = 0.5") { with(trait) { Float16(-1.0f).exp2() }.toFloat() shouldBe 0.5f }
            test("exp2(-2) = 0.25") { with(trait) { Float16(-2.0f).exp2() }.toFloat() shouldBe 0.25f }
        }

        context("special values") {
            test("exp2(NaN) = NaN") { with(trait) { Float16.NaN.exp2() }.isNaN() shouldBe true }
            test("exp2(+∞) = +∞") { with(trait) { Float16.POSITIVE_INFINITY.exp2() }.bits shouldBe Float16.POSITIVE_INFINITY.bits }
            test("exp2(-∞) = 0") { with(trait) { Float16.NEGATIVE_INFINITY.exp2() }.toFloat() shouldBe 0.0f }
            test("exp2(0) = 1") { with(trait) { Float16(0.0f).exp2() }.toFloat() shouldBe 1.0f }
        }
    }
})
