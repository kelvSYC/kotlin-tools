package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.floats.shouldBeNaN
import io.kotest.matchers.shouldBe

class FloatingPointCubeRootTest : FunSpec({

    // ── FloatingPointCubeRoot.Companion.double ────────────────────────────────

    context("FloatingPointCubeRoot.Companion.double") {
        val trait = FloatingPointCubeRoot.double

        context("special values") {
            test("cbrt(NaN) = NaN") { with(trait) { Double.NaN.cbrt() }.shouldBeNaN() }
            test("cbrt(+∞) = +∞") { with(trait) { Double.POSITIVE_INFINITY.cbrt() } shouldBe Double.POSITIVE_INFINITY }
            test("cbrt(-∞) = -∞") { with(trait) { Double.NEGATIVE_INFINITY.cbrt() } shouldBe Double.NEGATIVE_INFINITY }
            test("cbrt(0.0) = 0.0") { with(trait) { 0.0.cbrt() } shouldBe 0.0 }
            test("cbrt(-0.0) = -0.0") { with(trait) { (-0.0).cbrt() } shouldBe -0.0 }
        }

        context("exact results") {
            test("cbrt(1) = 1") { with(trait) { 1.0.cbrt() } shouldBe 1.0 }
            test("cbrt(-1) = -1") { with(trait) { (-1.0).cbrt() } shouldBe -1.0 }
            test("cbrt(8) = 2") { with(trait) { 8.0.cbrt() } shouldBe 2.0 }
            test("cbrt(-8) = -2") { with(trait) { (-8.0).cbrt() } shouldBe -2.0 }
            test("cbrt(27) = 3") { with(trait) { 27.0.cbrt() } shouldBe 3.0 }
        }
    }

    // ── FloatingPointCubeRoot.Companion.float ─────────────────────────────────

    context("FloatingPointCubeRoot.Companion.float") {
        val trait = FloatingPointCubeRoot.float

        context("special values") {
            test("cbrt(NaN) = NaN") { with(trait) { Float.NaN.cbrt() }.shouldBeNaN() }
            test("cbrt(+∞) = +∞") { with(trait) { Float.POSITIVE_INFINITY.cbrt() } shouldBe Float.POSITIVE_INFINITY }
            test("cbrt(-∞) = -∞") { with(trait) { Float.NEGATIVE_INFINITY.cbrt() } shouldBe Float.NEGATIVE_INFINITY }
            test("cbrt(0.0) = 0.0") { with(trait) { 0.0f.cbrt() } shouldBe 0.0f }
            test("cbrt(-0.0) = -0.0") { with(trait) { (-0.0f).cbrt() } shouldBe -0.0f }
        }

        context("exact results") {
            test("cbrt(1) = 1") { with(trait) { 1.0f.cbrt() } shouldBe 1.0f }
            test("cbrt(-1) = -1") { with(trait) { (-1.0f).cbrt() } shouldBe -1.0f }
            test("cbrt(8) = 2") { with(trait) { 8.0f.cbrt() } shouldBe 2.0f }
            test("cbrt(-8) = -2") { with(trait) { (-8.0f).cbrt() } shouldBe -2.0f }
            test("cbrt(27) = 3") { with(trait) { 27.0f.cbrt() } shouldBe 3.0f }
        }
    }

    // ── FloatingPointCubeRoot.Companion.bfloat16 ──────────────────────────────

    context("FloatingPointCubeRoot.Companion.bfloat16") {
        val trait = FloatingPointCubeRoot.bfloat16

        context("special values") {
            test("cbrt(NaN) = NaN") { with(trait) { BFloat16.NaN.cbrt() }.isNaN() shouldBe true }
            test("cbrt(+∞) = +∞") { with(trait) { BFloat16.POSITIVE_INFINITY.cbrt() }.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
            test("cbrt(-∞) = -∞") { with(trait) { BFloat16.NEGATIVE_INFINITY.cbrt() }.bits shouldBe BFloat16.NEGATIVE_INFINITY.bits }
            test("cbrt(0.0) = 0.0") { with(trait) { BFloat16(0.0f).cbrt() }.toFloat() shouldBe 0.0f }
        }

        context("exact results") {
            test("cbrt(1) = 1") { with(trait) { BFloat16(1.0f).cbrt() }.toFloat() shouldBe 1.0f }
            test("cbrt(-1) = -1") { with(trait) { BFloat16(-1.0f).cbrt() }.toFloat() shouldBe -1.0f }
            test("cbrt(8) = 2") { with(trait) { BFloat16(8.0f).cbrt() }.toFloat() shouldBe 2.0f }
            test("cbrt(-8) = -2") { with(trait) { BFloat16(-8.0f).cbrt() }.toFloat() shouldBe -2.0f }
        }
    }

    // ── FloatingPointCubeRoot.Companion.float16 ───────────────────────────────

    context("FloatingPointCubeRoot.Companion.float16") {
        val trait = FloatingPointCubeRoot.float16

        context("special values") {
            test("cbrt(NaN) = NaN") { with(trait) { Float16.NaN.cbrt() }.isNaN() shouldBe true }
            test("cbrt(+∞) = +∞") { with(trait) { Float16.POSITIVE_INFINITY.cbrt() }.bits shouldBe Float16.POSITIVE_INFINITY.bits }
            test("cbrt(-∞) = -∞") { with(trait) { Float16.NEGATIVE_INFINITY.cbrt() }.bits shouldBe Float16.NEGATIVE_INFINITY.bits }
            test("cbrt(0.0) = 0.0") { with(trait) { Float16(0.0f).cbrt() }.toFloat() shouldBe 0.0f }
        }

        context("exact results") {
            test("cbrt(1) = 1") { with(trait) { Float16(1.0f).cbrt() }.toFloat() shouldBe 1.0f }
            test("cbrt(-1) = -1") { with(trait) { Float16(-1.0f).cbrt() }.toFloat() shouldBe -1.0f }
            test("cbrt(8) = 2") { with(trait) { Float16(8.0f).cbrt() }.toFloat() shouldBe 2.0f }
            test("cbrt(-8) = -2") { with(trait) { Float16(-8.0f).cbrt() }.toFloat() shouldBe -2.0f }
        }
    }
})
