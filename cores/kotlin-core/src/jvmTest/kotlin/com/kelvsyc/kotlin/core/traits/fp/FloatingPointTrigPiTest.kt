package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.floats.shouldBeNaN
import io.kotest.matchers.shouldBe

private fun Double.withinUlp(expected: Double, n: Int): Boolean =
    kotlin.math.abs(this - expected) / java.lang.Math.ulp(expected) < n + 1.0

class FloatingPointTrigPiTest : FunSpec({

    // ── FloatingPointTrigPi.Companion.double ──────────────────────────────────

    context("FloatingPointTrigPi.Companion.double") {
        val trait = FloatingPointTrigPi.double

        context("sinPi") {
            test("sinPi(0) = 0")       { with(trait) { 0.0.sinPi() } shouldBe 0.0 }
            test("sinPi(0.5) = 1")     { with(trait) { 0.5.sinPi() } shouldBe 1.0 }
            test("sinPi(1) = 0")       { with(trait) { 1.0.sinPi() } shouldBe 0.0 }
            test("sinPi(-0.5) = -1")   { with(trait) { (-0.5).sinPi() } shouldBe -1.0 }
            test("sinPi(NaN) = NaN")   { with(trait) { Double.NaN.sinPi() }.shouldBeNaN() }
            test("sinPi(+∞) = NaN")    { with(trait) { Double.POSITIVE_INFINITY.sinPi() }.shouldBeNaN() }
            test("sinPi(-∞) = NaN")    { with(trait) { Double.NEGATIVE_INFINITY.sinPi() }.shouldBeNaN() }
            test("sinPi(0.25) within 2 ULP of √2/2") {
                (with(trait) { 0.25.sinPi() }.withinUlp(kotlin.math.sqrt(2.0) / 2.0, 2)) shouldBe true
            }
        }

        context("cosPi") {
            test("cosPi(0) = 1")       { with(trait) { 0.0.cosPi() } shouldBe 1.0 }
            test("cosPi(0.5) = 0")     { with(trait) { 0.5.cosPi() } shouldBe 0.0 }
            test("cosPi(1) = -1")      { with(trait) { 1.0.cosPi() } shouldBe -1.0 }
            test("cosPi(NaN) = NaN")   { with(trait) { Double.NaN.cosPi() }.shouldBeNaN() }
            test("cosPi(+∞) = NaN")    { with(trait) { Double.POSITIVE_INFINITY.cosPi() }.shouldBeNaN() }
            test("cosPi(0.25) within 2 ULP of √2/2") {
                (with(trait) { 0.25.cosPi() }.withinUlp(kotlin.math.sqrt(2.0) / 2.0, 2)) shouldBe true
            }
        }

        context("tanPi") {
            test("tanPi(0) = 0")       { with(trait) { 0.0.tanPi() } shouldBe 0.0 }
            test("tanPi(0.25) within 2 ULP of 1")  { (with(trait) { 0.25.tanPi() }.withinUlp(1.0, 2)) shouldBe true }
            test("tanPi(-0.25) within 2 ULP of -1") { (with(trait) { (-0.25).tanPi() }.withinUlp(-1.0, 2)) shouldBe true }
            test("tanPi(0.5) = +∞")    { with(trait) { 0.5.tanPi() } shouldBe Double.POSITIVE_INFINITY }
            test("tanPi(-0.5) = -∞")   { with(trait) { (-0.5).tanPi() } shouldBe Double.NEGATIVE_INFINITY }
            test("tanPi(1) = 0")       { with(trait) { 1.0.tanPi() } shouldBe 0.0 }
            test("tanPi(NaN) = NaN")   { with(trait) { Double.NaN.tanPi() }.shouldBeNaN() }
            test("tanPi(+∞) = NaN")    { with(trait) { Double.POSITIVE_INFINITY.tanPi() }.shouldBeNaN() }
        }

        context("asinPi") {
            test("asinPi(0) = 0")      { with(trait) { 0.0.asinPi() } shouldBe 0.0 }
            test("asinPi(1) = 0.5")    { with(trait) { 1.0.asinPi() } shouldBe 0.5 }
            test("asinPi(-1) = -0.5")  { with(trait) { (-1.0).asinPi() } shouldBe -0.5 }
            test("asinPi(NaN) = NaN")  { with(trait) { Double.NaN.asinPi() }.shouldBeNaN() }
            test("asinPi(2) = NaN")    { with(trait) { 2.0.asinPi() }.shouldBeNaN() }
        }

        context("acosPi") {
            test("acosPi(1) = 0")      { with(trait) { 1.0.acosPi() } shouldBe 0.0 }
            test("acosPi(0) = 0.5")    { with(trait) { 0.0.acosPi() } shouldBe 0.5 }
            test("acosPi(-1) = 1")     { with(trait) { (-1.0).acosPi() } shouldBe 1.0 }
            test("acosPi(NaN) = NaN")  { with(trait) { Double.NaN.acosPi() }.shouldBeNaN() }
            test("acosPi(2) = NaN")    { with(trait) { 2.0.acosPi() }.shouldBeNaN() }
        }

        context("atanPi") {
            test("atanPi(0) = 0")      { with(trait) { 0.0.atanPi() } shouldBe 0.0 }
            test("atanPi(1) = 0.25")   { with(trait) { 1.0.atanPi() } shouldBe 0.25 }
            test("atanPi(+∞) = 0.5")   { with(trait) { Double.POSITIVE_INFINITY.atanPi() } shouldBe 0.5 }
            test("atanPi(-∞) = -0.5")  { with(trait) { Double.NEGATIVE_INFINITY.atanPi() } shouldBe -0.5 }
            test("atanPi(NaN) = NaN")  { with(trait) { Double.NaN.atanPi() }.shouldBeNaN() }
        }

        context("atan2Pi") {
            test("atan2Pi(0, 1) = 0")  { with(trait) { 0.0.atan2Pi(1.0) } shouldBe 0.0 }
            test("atan2Pi(1, 0) = 0.5"){ with(trait) { 1.0.atan2Pi(0.0) } shouldBe 0.5 }
            test("atan2Pi(0, -1) = 1") { with(trait) { 0.0.atan2Pi(-1.0) } shouldBe 1.0 }
            test("atan2Pi(1, 1) = 0.25"){ with(trait) { 1.0.atan2Pi(1.0) } shouldBe 0.25 }
            test("atan2Pi(NaN, 0) = NaN"){ with(trait) { Double.NaN.atan2Pi(0.0) }.shouldBeNaN() }
        }
    }

    // ── FloatingPointTrigPi.Companion.float ───────────────────────────────────

    context("FloatingPointTrigPi.Companion.float") {
        val trait = FloatingPointTrigPi.float

        context("sinPi") {
            test("sinPi(0) = 0")       { with(trait) { 0.0f.sinPi() } shouldBe 0.0f }
            test("sinPi(0.5) = 1")     { with(trait) { 0.5f.sinPi() } shouldBe 1.0f }
            test("sinPi(1) = 0")       { with(trait) { 1.0f.sinPi() } shouldBe 0.0f }
            test("sinPi(-0.5) = -1")   { with(trait) { (-0.5f).sinPi() } shouldBe -1.0f }
            test("sinPi(NaN) = NaN")   { with(trait) { Float.NaN.sinPi() }.shouldBeNaN() }
            test("sinPi(+∞) = NaN")    { with(trait) { Float.POSITIVE_INFINITY.sinPi() }.shouldBeNaN() }
            test("sinPi(0.25) = √2/2") { with(trait) { 0.25f.sinPi() } shouldBe (kotlin.math.sqrt(2.0f) / 2.0f) }
        }

        context("cosPi") {
            test("cosPi(0) = 1")       { with(trait) { 0.0f.cosPi() } shouldBe 1.0f }
            test("cosPi(0.5) = 0")     { with(trait) { 0.5f.cosPi() } shouldBe 0.0f }
            test("cosPi(1) = -1")      { with(trait) { 1.0f.cosPi() } shouldBe -1.0f }
            test("cosPi(NaN) = NaN")   { with(trait) { Float.NaN.cosPi() }.shouldBeNaN() }
            test("cosPi(+∞) = NaN")    { with(trait) { Float.POSITIVE_INFINITY.cosPi() }.shouldBeNaN() }
        }

        context("tanPi") {
            test("tanPi(0) = 0")       { with(trait) { 0.0f.tanPi() } shouldBe 0.0f }
            test("tanPi(0.25) = 1")    { with(trait) { 0.25f.tanPi() } shouldBe 1.0f }
            test("tanPi(-0.25) = -1")  { with(trait) { (-0.25f).tanPi() } shouldBe -1.0f }
            test("tanPi(0.5) = +∞")    { with(trait) { 0.5f.tanPi() } shouldBe Float.POSITIVE_INFINITY }
            test("tanPi(-0.5) = -∞")   { with(trait) { (-0.5f).tanPi() } shouldBe Float.NEGATIVE_INFINITY }
            test("tanPi(1) = 0")       { with(trait) { 1.0f.tanPi() } shouldBe 0.0f }
            test("tanPi(NaN) = NaN")   { with(trait) { Float.NaN.tanPi() }.shouldBeNaN() }
        }

        context("asinPi") {
            test("asinPi(0) = 0")      { with(trait) { 0.0f.asinPi() } shouldBe 0.0f }
            test("asinPi(1) = 0.5")    { with(trait) { 1.0f.asinPi() } shouldBe 0.5f }
            test("asinPi(-1) = -0.5")  { with(trait) { (-1.0f).asinPi() } shouldBe -0.5f }
            test("asinPi(NaN) = NaN")  { with(trait) { Float.NaN.asinPi() }.shouldBeNaN() }
            test("asinPi(2) = NaN")    { with(trait) { 2.0f.asinPi() }.shouldBeNaN() }
        }

        context("acosPi") {
            test("acosPi(1) = 0")      { with(trait) { 1.0f.acosPi() } shouldBe 0.0f }
            test("acosPi(0) = 0.5")    { with(trait) { 0.0f.acosPi() } shouldBe 0.5f }
            test("acosPi(-1) = 1")     { with(trait) { (-1.0f).acosPi() } shouldBe 1.0f }
            test("acosPi(NaN) = NaN")  { with(trait) { Float.NaN.acosPi() }.shouldBeNaN() }
        }

        context("atanPi") {
            test("atanPi(0) = 0")      { with(trait) { 0.0f.atanPi() } shouldBe 0.0f }
            test("atanPi(1) = 0.25")   { with(trait) { 1.0f.atanPi() } shouldBe 0.25f }
            test("atanPi(+∞) = 0.5")   { with(trait) { Float.POSITIVE_INFINITY.atanPi() } shouldBe 0.5f }
            test("atanPi(-∞) = -0.5")  { with(trait) { Float.NEGATIVE_INFINITY.atanPi() } shouldBe -0.5f }
            test("atanPi(NaN) = NaN")  { with(trait) { Float.NaN.atanPi() }.shouldBeNaN() }
        }

        context("atan2Pi") {
            test("atan2Pi(0, 1) = 0")  { with(trait) { 0.0f.atan2Pi(1.0f) } shouldBe 0.0f }
            test("atan2Pi(1, 0) = 0.5"){ with(trait) { 1.0f.atan2Pi(0.0f) } shouldBe 0.5f }
            test("atan2Pi(0, -1) = 1") { with(trait) { 0.0f.atan2Pi(-1.0f) } shouldBe 1.0f }
            test("atan2Pi(1, 1) = 0.25"){ with(trait) { 1.0f.atan2Pi(1.0f) } shouldBe 0.25f }
            test("atan2Pi(NaN, 0) = NaN"){ with(trait) { Float.NaN.atan2Pi(0.0f) }.shouldBeNaN() }
        }
    }

    // ── FloatingPointTrigPi.Companion.bfloat16 ────────────────────────────────

    context("FloatingPointTrigPi.Companion.bfloat16") {
        val trait = FloatingPointTrigPi.bfloat16

        context("sinPi") {
            test("sinPi(0) = 0")     { with(trait) { BFloat16(0.0f).sinPi() }.toFloat() shouldBe 0.0f }
            test("sinPi(0.5) = 1")   { with(trait) { BFloat16(0.5f).sinPi() }.toFloat() shouldBe 1.0f }
            test("sinPi(1) = 0")     { with(trait) { BFloat16(1.0f).sinPi() }.toFloat() shouldBe 0.0f }
            test("sinPi(-0.5) = -1") { with(trait) { BFloat16(-0.5f).sinPi() }.toFloat() shouldBe -1.0f }
            test("sinPi(NaN) = NaN") { with(trait) { BFloat16.NaN.sinPi() }.isNaN() shouldBe true }
            test("sinPi(+∞) = NaN")  { with(trait) { BFloat16.POSITIVE_INFINITY.sinPi() }.isNaN() shouldBe true }
        }

        context("cosPi") {
            test("cosPi(0) = 1")     { with(trait) { BFloat16(0.0f).cosPi() }.toFloat() shouldBe 1.0f }
            test("cosPi(0.5) = 0")   { with(trait) { BFloat16(0.5f).cosPi() }.toFloat() shouldBe 0.0f }
            test("cosPi(1) = -1")    { with(trait) { BFloat16(1.0f).cosPi() }.toFloat() shouldBe -1.0f }
            test("cosPi(NaN) = NaN") { with(trait) { BFloat16.NaN.cosPi() }.isNaN() shouldBe true }
        }

        context("tanPi") {
            test("tanPi(0) = 0")     { with(trait) { BFloat16(0.0f).tanPi() }.toFloat() shouldBe 0.0f }
            test("tanPi(0.25) = 1")  { with(trait) { BFloat16(0.25f).tanPi() }.toFloat() shouldBe 1.0f }
            test("tanPi(0.5) = +∞")  { with(trait) { BFloat16(0.5f).tanPi() }.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
            test("tanPi(-0.5) = -∞") { with(trait) { BFloat16(-0.5f).tanPi() }.bits shouldBe BFloat16.NEGATIVE_INFINITY.bits }
            test("tanPi(NaN) = NaN") { with(trait) { BFloat16.NaN.tanPi() }.isNaN() shouldBe true }
        }

        context("asinPi") {
            test("asinPi(0) = 0")    { with(trait) { BFloat16(0.0f).asinPi() }.toFloat() shouldBe 0.0f }
            test("asinPi(1) = 0.5")  { with(trait) { BFloat16(1.0f).asinPi() }.toFloat() shouldBe 0.5f }
            test("asinPi(-1) = -0.5"){ with(trait) { BFloat16(-1.0f).asinPi() }.toFloat() shouldBe -0.5f }
            test("asinPi(NaN) = NaN"){ with(trait) { BFloat16.NaN.asinPi() }.isNaN() shouldBe true }
        }

        context("acosPi") {
            test("acosPi(1) = 0")    { with(trait) { BFloat16(1.0f).acosPi() }.toFloat() shouldBe 0.0f }
            test("acosPi(0) = 0.5")  { with(trait) { BFloat16(0.0f).acosPi() }.toFloat() shouldBe 0.5f }
            test("acosPi(-1) = 1")   { with(trait) { BFloat16(-1.0f).acosPi() }.toFloat() shouldBe 1.0f }
            test("acosPi(NaN) = NaN"){ with(trait) { BFloat16.NaN.acosPi() }.isNaN() shouldBe true }
        }

        context("atanPi") {
            test("atanPi(0) = 0")    { with(trait) { BFloat16(0.0f).atanPi() }.toFloat() shouldBe 0.0f }
            test("atanPi(+∞) = 0.5") { with(trait) { BFloat16.POSITIVE_INFINITY.atanPi() }.toFloat() shouldBe 0.5f }
            test("atanPi(-∞) = -0.5"){ with(trait) { BFloat16.NEGATIVE_INFINITY.atanPi() }.toFloat() shouldBe -0.5f }
            test("atanPi(NaN) = NaN"){ with(trait) { BFloat16.NaN.atanPi() }.isNaN() shouldBe true }
        }

        context("atan2Pi") {
            test("atan2Pi(0, 1) = 0") { with(trait) { BFloat16(0.0f).atan2Pi(BFloat16(1.0f)) }.toFloat() shouldBe 0.0f }
            test("atan2Pi(1, 0) = 0.5"){ with(trait) { BFloat16(1.0f).atan2Pi(BFloat16(0.0f)) }.toFloat() shouldBe 0.5f }
            test("atan2Pi(0, -1) = 1") { with(trait) { BFloat16(0.0f).atan2Pi(BFloat16(-1.0f)) }.toFloat() shouldBe 1.0f }
            test("atan2Pi(NaN, 0) = NaN"){ with(trait) { BFloat16.NaN.atan2Pi(BFloat16(0.0f)) }.isNaN() shouldBe true }
        }
    }

    // ── FloatingPointTrigPi.Companion.float16 ────────────────────────────────

    context("FloatingPointTrigPi.Companion.float16") {
        val trait = FloatingPointTrigPi.float16

        context("sinPi") {
            test("sinPi(0) = 0")     { with(trait) { Float16(0.0f).sinPi() }.toFloat() shouldBe 0.0f }
            test("sinPi(0.5) = 1")   { with(trait) { Float16(0.5f).sinPi() }.toFloat() shouldBe 1.0f }
            test("sinPi(1) = 0")     { with(trait) { Float16(1.0f).sinPi() }.toFloat() shouldBe 0.0f }
            test("sinPi(-0.5) = -1") { with(trait) { Float16(-0.5f).sinPi() }.toFloat() shouldBe -1.0f }
            test("sinPi(NaN) = NaN") { with(trait) { Float16.NaN.sinPi() }.isNaN() shouldBe true }
            test("sinPi(+∞) = NaN")  { with(trait) { Float16.POSITIVE_INFINITY.sinPi() }.isNaN() shouldBe true }
        }

        context("cosPi") {
            test("cosPi(0) = 1")     { with(trait) { Float16(0.0f).cosPi() }.toFloat() shouldBe 1.0f }
            test("cosPi(0.5) = 0")   { with(trait) { Float16(0.5f).cosPi() }.toFloat() shouldBe 0.0f }
            test("cosPi(1) = -1")    { with(trait) { Float16(1.0f).cosPi() }.toFloat() shouldBe -1.0f }
            test("cosPi(NaN) = NaN") { with(trait) { Float16.NaN.cosPi() }.isNaN() shouldBe true }
        }

        context("tanPi") {
            test("tanPi(0) = 0")     { with(trait) { Float16(0.0f).tanPi() }.toFloat() shouldBe 0.0f }
            test("tanPi(0.25) = 1")  { with(trait) { Float16(0.25f).tanPi() }.toFloat() shouldBe 1.0f }
            test("tanPi(0.5) = +∞")  { with(trait) { Float16(0.5f).tanPi() }.bits shouldBe Float16.POSITIVE_INFINITY.bits }
            test("tanPi(-0.5) = -∞") { with(trait) { Float16(-0.5f).tanPi() }.bits shouldBe Float16.NEGATIVE_INFINITY.bits }
            test("tanPi(NaN) = NaN") { with(trait) { Float16.NaN.tanPi() }.isNaN() shouldBe true }
        }

        context("asinPi") {
            test("asinPi(0) = 0")    { with(trait) { Float16(0.0f).asinPi() }.toFloat() shouldBe 0.0f }
            test("asinPi(1) = 0.5")  { with(trait) { Float16(1.0f).asinPi() }.toFloat() shouldBe 0.5f }
            test("asinPi(-1) = -0.5"){ with(trait) { Float16(-1.0f).asinPi() }.toFloat() shouldBe -0.5f }
            test("asinPi(NaN) = NaN"){ with(trait) { Float16.NaN.asinPi() }.isNaN() shouldBe true }
        }

        context("acosPi") {
            test("acosPi(1) = 0")    { with(trait) { Float16(1.0f).acosPi() }.toFloat() shouldBe 0.0f }
            test("acosPi(0) = 0.5")  { with(trait) { Float16(0.0f).acosPi() }.toFloat() shouldBe 0.5f }
            test("acosPi(-1) = 1")   { with(trait) { Float16(-1.0f).acosPi() }.toFloat() shouldBe 1.0f }
            test("acosPi(NaN) = NaN"){ with(trait) { Float16.NaN.acosPi() }.isNaN() shouldBe true }
        }

        context("atanPi") {
            test("atanPi(0) = 0")    { with(trait) { Float16(0.0f).atanPi() }.toFloat() shouldBe 0.0f }
            test("atanPi(+∞) = 0.5") { with(trait) { Float16.POSITIVE_INFINITY.atanPi() }.toFloat() shouldBe 0.5f }
            test("atanPi(-∞) = -0.5"){ with(trait) { Float16.NEGATIVE_INFINITY.atanPi() }.toFloat() shouldBe -0.5f }
            test("atanPi(NaN) = NaN"){ with(trait) { Float16.NaN.atanPi() }.isNaN() shouldBe true }
        }

        context("atan2Pi") {
            test("atan2Pi(0, 1) = 0") { with(trait) { Float16(0.0f).atan2Pi(Float16(1.0f)) }.toFloat() shouldBe 0.0f }
            test("atan2Pi(1, 0) = 0.5"){ with(trait) { Float16(1.0f).atan2Pi(Float16(0.0f)) }.toFloat() shouldBe 0.5f }
            test("atan2Pi(0, -1) = 1") { with(trait) { Float16(0.0f).atan2Pi(Float16(-1.0f)) }.toFloat() shouldBe 1.0f }
            test("atan2Pi(NaN, 0) = NaN"){ with(trait) { Float16.NaN.atan2Pi(Float16(0.0f)) }.isNaN() shouldBe true }
        }
    }
})
