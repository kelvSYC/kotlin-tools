package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.floats.shouldBeNaN
import io.kotest.matchers.shouldBe

class FloatingPointSinhCoshTest : FunSpec({

    // ── FloatingPointSinhCosh.Companion.double ────────────────────────────────

    context("FloatingPointSinhCosh.Companion.double") {
        val trait = FloatingPointSinhCosh.double

        context("special values") {
            test("sinhcosh(NaN).sinh = NaN") { with(trait) { Double.NaN.sinhcosh() }.sinh.shouldBeNaN() }
            test("sinhcosh(NaN).cosh = NaN") { with(trait) { Double.NaN.sinhcosh() }.cosh.shouldBeNaN() }
            test("sinhcosh(+∞).sinh = +∞") { with(trait) { Double.POSITIVE_INFINITY.sinhcosh() }.sinh shouldBe Double.POSITIVE_INFINITY }
            test("sinhcosh(+∞).cosh = +∞") { with(trait) { Double.POSITIVE_INFINITY.sinhcosh() }.cosh shouldBe Double.POSITIVE_INFINITY }
            test("sinhcosh(-∞).sinh = -∞") { with(trait) { Double.NEGATIVE_INFINITY.sinhcosh() }.sinh shouldBe Double.NEGATIVE_INFINITY }
            test("sinhcosh(-∞).cosh = +∞") { with(trait) { Double.NEGATIVE_INFINITY.sinhcosh() }.cosh shouldBe Double.POSITIVE_INFINITY }
        }

        context("exact results") {
            test("sinhcosh(0.0).sinh = 0.0") { with(trait) { 0.0.sinhcosh() }.sinh shouldBe 0.0 }
            test("sinhcosh(0.0).cosh = 1.0") { with(trait) { 0.0.sinhcosh() }.cosh shouldBe 1.0 }
            test("sinh is odd: sinhcosh(-x).sinh = -sinhcosh(x).sinh") {
                val pos = with(trait) { 1.0.sinhcosh() }.sinh
                val neg = with(trait) { (-1.0).sinhcosh() }.sinh
                neg shouldBe -pos
            }
            test("cosh is even: sinhcosh(-x).cosh = sinhcosh(x).cosh") {
                val pos = with(trait) { 1.0.sinhcosh() }.cosh
                val neg = with(trait) { (-1.0).sinhcosh() }.cosh
                neg shouldBe pos
            }
        }
    }

    // ── FloatingPointSinhCosh.Companion.float ─────────────────────────────────

    context("FloatingPointSinhCosh.Companion.float") {
        val trait = FloatingPointSinhCosh.float

        context("special values") {
            test("sinhcosh(NaN).sinh = NaN") { with(trait) { Float.NaN.sinhcosh() }.sinh.shouldBeNaN() }
            test("sinhcosh(NaN).cosh = NaN") { with(trait) { Float.NaN.sinhcosh() }.cosh.shouldBeNaN() }
            test("sinhcosh(+∞).sinh = +∞") { with(trait) { Float.POSITIVE_INFINITY.sinhcosh() }.sinh shouldBe Float.POSITIVE_INFINITY }
            test("sinhcosh(+∞).cosh = +∞") { with(trait) { Float.POSITIVE_INFINITY.sinhcosh() }.cosh shouldBe Float.POSITIVE_INFINITY }
            test("sinhcosh(-∞).sinh = -∞") { with(trait) { Float.NEGATIVE_INFINITY.sinhcosh() }.sinh shouldBe Float.NEGATIVE_INFINITY }
            test("sinhcosh(-∞).cosh = +∞") { with(trait) { Float.NEGATIVE_INFINITY.sinhcosh() }.cosh shouldBe Float.POSITIVE_INFINITY }
        }

        context("exact results") {
            test("sinhcosh(0.0).sinh = 0.0") { with(trait) { 0.0f.sinhcosh() }.sinh shouldBe 0.0f }
            test("sinhcosh(0.0).cosh = 1.0") { with(trait) { 0.0f.sinhcosh() }.cosh shouldBe 1.0f }
            test("sinh is odd: sinhcosh(-x).sinh = -sinhcosh(x).sinh") {
                val pos = with(trait) { 1.0f.sinhcosh() }.sinh
                val neg = with(trait) { (-1.0f).sinhcosh() }.sinh
                neg shouldBe -pos
            }
            test("cosh is even: sinhcosh(-x).cosh = sinhcosh(x).cosh") {
                val pos = with(trait) { 1.0f.sinhcosh() }.cosh
                val neg = with(trait) { (-1.0f).sinhcosh() }.cosh
                neg shouldBe pos
            }
        }
    }

    // ── FloatingPointSinhCosh.Companion.bfloat16 ─────────────────────────────

    context("FloatingPointSinhCosh.Companion.bfloat16") {
        val trait = FloatingPointSinhCosh.bfloat16

        context("special values") {
            test("sinhcosh(NaN).sinh = NaN") { with(trait) { BFloat16.NaN.sinhcosh() }.sinh.isNaN() shouldBe true }
            test("sinhcosh(NaN).cosh = NaN") { with(trait) { BFloat16.NaN.sinhcosh() }.cosh.isNaN() shouldBe true }
            test("sinhcosh(+∞).sinh = +∞") { with(trait) { BFloat16.POSITIVE_INFINITY.sinhcosh() }.sinh.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
            test("sinhcosh(+∞).cosh = +∞") { with(trait) { BFloat16.POSITIVE_INFINITY.sinhcosh() }.cosh.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
            test("sinhcosh(-∞).sinh = -∞") { with(trait) { BFloat16.NEGATIVE_INFINITY.sinhcosh() }.sinh.bits shouldBe BFloat16.NEGATIVE_INFINITY.bits }
            test("sinhcosh(-∞).cosh = +∞") { with(trait) { BFloat16.NEGATIVE_INFINITY.sinhcosh() }.cosh.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
        }

        context("exact results") {
            test("sinhcosh(0.0).sinh = 0.0") { with(trait) { BFloat16(0.0f).sinhcosh() }.sinh.toFloat() shouldBe 0.0f }
            test("sinhcosh(0.0).cosh = 1.0") { with(trait) { BFloat16(0.0f).sinhcosh() }.cosh.toFloat() shouldBe 1.0f }
            test("sinh is odd: sinhcosh(-x).sinh = -sinhcosh(x).sinh") {
                val pos = with(trait) { BFloat16(1.0f).sinhcosh() }.sinh.toFloat()
                val neg = with(trait) { BFloat16(-1.0f).sinhcosh() }.sinh.toFloat()
                neg shouldBe -pos
            }
            test("cosh is even: sinhcosh(-x).cosh = sinhcosh(x).cosh") {
                val pos = with(trait) { BFloat16(1.0f).sinhcosh() }.cosh.bits
                val neg = with(trait) { BFloat16(-1.0f).sinhcosh() }.cosh.bits
                neg shouldBe pos
            }
        }
    }

    // ── FloatingPointSinhCosh.Companion.float16 ──────────────────────────────

    context("FloatingPointSinhCosh.Companion.float16") {
        val trait = FloatingPointSinhCosh.float16

        context("special values") {
            test("sinhcosh(NaN).sinh = NaN") { with(trait) { Float16.NaN.sinhcosh() }.sinh.isNaN() shouldBe true }
            test("sinhcosh(NaN).cosh = NaN") { with(trait) { Float16.NaN.sinhcosh() }.cosh.isNaN() shouldBe true }
            test("sinhcosh(+∞).sinh = +∞") { with(trait) { Float16.POSITIVE_INFINITY.sinhcosh() }.sinh.bits shouldBe Float16.POSITIVE_INFINITY.bits }
            test("sinhcosh(+∞).cosh = +∞") { with(trait) { Float16.POSITIVE_INFINITY.sinhcosh() }.cosh.bits shouldBe Float16.POSITIVE_INFINITY.bits }
            test("sinhcosh(-∞).sinh = -∞") { with(trait) { Float16.NEGATIVE_INFINITY.sinhcosh() }.sinh.bits shouldBe Float16.NEGATIVE_INFINITY.bits }
            test("sinhcosh(-∞).cosh = +∞") { with(trait) { Float16.NEGATIVE_INFINITY.sinhcosh() }.cosh.bits shouldBe Float16.POSITIVE_INFINITY.bits }
        }

        context("exact results") {
            test("sinhcosh(0.0).sinh = 0.0") { with(trait) { Float16(0.0f).sinhcosh() }.sinh.toFloat() shouldBe 0.0f }
            test("sinhcosh(0.0).cosh = 1.0") { with(trait) { Float16(0.0f).sinhcosh() }.cosh.toFloat() shouldBe 1.0f }
            test("sinh is odd: sinhcosh(-x).sinh = -sinhcosh(x).sinh") {
                val pos = with(trait) { Float16(1.0f).sinhcosh() }.sinh.toFloat()
                val neg = with(trait) { Float16(-1.0f).sinhcosh() }.sinh.toFloat()
                neg shouldBe -pos
            }
            test("cosh is even: sinhcosh(-x).cosh = sinhcosh(x).cosh") {
                val pos = with(trait) { Float16(1.0f).sinhcosh() }.cosh.bits
                val neg = with(trait) { Float16(-1.0f).sinhcosh() }.cosh.bits
                neg shouldBe pos
            }
        }
    }
})
