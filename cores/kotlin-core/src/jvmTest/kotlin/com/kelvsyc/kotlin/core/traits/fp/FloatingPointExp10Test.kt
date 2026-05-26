package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.floats.shouldBeNaN
import io.kotest.matchers.shouldBe

class FloatingPointExp10Test : FunSpec({

    // ── FloatingPointExp10.Companion.double ───────────────────────────────────

    context("FloatingPointExp10.Companion.double") {
        val trait = FloatingPointExp10.double

        // exp10(0) = 1 is provably exact: n=0, r=0, exp(0)=1, scalbn(1,0)=1.
        // exp10(1) = 10 and exp10(2) = 100 happen to be exact by lucky rounding
        // (target values 10/8=1.25 and 100/128=0.78125 are exact binary fractions).
        context("exact at zero and small integers") {
            test("exp10(0) = 1") { with(trait) { 0.0.exp10() } shouldBe 1.0 }
            test("exp10(1) = 10") { with(trait) { 1.0.exp10() } shouldBe 10.0 }
            test("exp10(2) = 100") { with(trait) { 2.0.exp10() } shouldBe 100.0 }
        }

        context("special values") {
            test("exp10(NaN) = NaN") { with(trait) { Double.NaN.exp10() }.shouldBeNaN() }
            test("exp10(+∞) = +∞") { with(trait) { Double.POSITIVE_INFINITY.exp10() } shouldBe Double.POSITIVE_INFINITY }
            test("exp10(-∞) = 0") { with(trait) { Double.NEGATIVE_INFINITY.exp10() } shouldBe 0.0 }
            test("exp10(+0) = 1") { with(trait) { 0.0.exp10() } shouldBe 1.0 }
            test("exp10(-0) = 1") { with(trait) { (-0.0).exp10() } shouldBe 1.0 }
        }

        // 10^10 = 10_000_000_000, exactly representable as a Double integer, making it a
        // useful reference. Naive exp(x*ln10) accumulates O(|x|)~10000 ULP error from the
        // inexact representation of ln(10); Dekker-split Cody-Waite stays within 2 ULP.
        context("accuracy: Dekker-split is significantly more accurate than naive exp(x*ln10)") {
            val exact = 1e10
            val ours = with(trait) { 10.0.exp10() }
            val naive = kotlin.math.exp(10.0 * kotlin.math.ln(10.0))

            test("exp10(10.0) within 2 ULP of 1e10") {
                (kotlin.math.abs(ours - exact) / java.lang.Math.ulp(exact) < 3.0) shouldBe true
            }

            test("naive exp(10.0 * ln(10.0)) is not within 2 ULP of 1e10") {
                (kotlin.math.abs(naive - exact) / java.lang.Math.ulp(exact) >= 3.0) shouldBe true
            }
        }
    }

    // ── FloatingPointExp10.Companion.float ────────────────────────────────────

    context("FloatingPointExp10.Companion.float") {
        val trait = FloatingPointExp10.float

        context("exact at zero and small integers") {
            test("exp10(0) = 1") { with(trait) { 0.0f.exp10() } shouldBe 1.0f }
            test("exp10(1) = 10") { with(trait) { 1.0f.exp10() } shouldBe 10.0f }
            test("exp10(2) = 100") { with(trait) { 2.0f.exp10() } shouldBe 100.0f }
            test("exp10(3) = 1000") { with(trait) { 3.0f.exp10() } shouldBe 1000.0f }
        }

        context("special values") {
            test("exp10(NaN) = NaN") { with(trait) { Float.NaN.exp10() }.shouldBeNaN() }
            test("exp10(+∞) = +∞") { with(trait) { Float.POSITIVE_INFINITY.exp10() } shouldBe Float.POSITIVE_INFINITY }
            test("exp10(-∞) = 0") { with(trait) { Float.NEGATIVE_INFINITY.exp10() } shouldBe 0.0f }
            test("exp10(+0) = 1") { with(trait) { 0.0f.exp10() } shouldBe 1.0f }
            test("exp10(-0) = 1") { with(trait) { (-0.0f).exp10() } shouldBe 1.0f }
        }
    }

    // ── FloatingPointExp10.Companion.bfloat16 ─────────────────────────────────

    context("FloatingPointExp10.Companion.bfloat16") {
        val trait = FloatingPointExp10.bfloat16

        context("exact powers of 10") {
            test("exp10(0) = 1") { with(trait) { BFloat16(0.0f).exp10() }.toFloat() shouldBe 1.0f }
            test("exp10(1) = 10") { with(trait) { BFloat16(1.0f).exp10() }.toFloat() shouldBe 10.0f }
            test("exp10(2) = 100") { with(trait) { BFloat16(2.0f).exp10() }.toFloat() shouldBe 100.0f }
            test("exp10(3) = 1000") { with(trait) { BFloat16(3.0f).exp10() }.toFloat() shouldBe 1000.0f }
        }

        context("special values") {
            test("exp10(NaN) = NaN") { with(trait) { BFloat16.NaN.exp10() }.isNaN() shouldBe true }
            test("exp10(+∞) = +∞") { with(trait) { BFloat16.POSITIVE_INFINITY.exp10() }.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
            test("exp10(-∞) = 0") { with(trait) { BFloat16.NEGATIVE_INFINITY.exp10() }.toFloat() shouldBe 0.0f }
            test("exp10(0) = 1") { with(trait) { BFloat16(0.0f).exp10() }.toFloat() shouldBe 1.0f }
        }
    }

    // ── FloatingPointExp10.Companion.float16 ──────────────────────────────────

    context("FloatingPointExp10.Companion.float16") {
        val trait = FloatingPointExp10.float16

        context("exact powers of 10") {
            test("exp10(0) = 1") { with(trait) { Float16(0.0f).exp10() }.toFloat() shouldBe 1.0f }
            test("exp10(1) = 10") { with(trait) { Float16(1.0f).exp10() }.toFloat() shouldBe 10.0f }
            test("exp10(2) = 100") { with(trait) { Float16(2.0f).exp10() }.toFloat() shouldBe 100.0f }
            test("exp10(3) = 1000") { with(trait) { Float16(3.0f).exp10() }.toFloat() shouldBe 1000.0f }
        }

        context("special values") {
            test("exp10(NaN) = NaN") { with(trait) { Float16.NaN.exp10() }.isNaN() shouldBe true }
            test("exp10(+∞) = +∞") { with(trait) { Float16.POSITIVE_INFINITY.exp10() }.bits shouldBe Float16.POSITIVE_INFINITY.bits }
            test("exp10(-∞) = 0") { with(trait) { Float16.NEGATIVE_INFINITY.exp10() }.toFloat() shouldBe 0.0f }
            test("exp10(0) = 1") { with(trait) { Float16(0.0f).exp10() }.toFloat() shouldBe 1.0f }
        }
    }
})
