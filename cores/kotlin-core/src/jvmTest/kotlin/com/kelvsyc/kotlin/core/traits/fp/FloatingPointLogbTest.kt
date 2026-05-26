package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.floats.shouldBeNaN
import io.kotest.matchers.shouldBe

class FloatingPointLogbTest : FunSpec({

    // ── FloatingPointLogb.Companion.double ────────────────────────────────────

    context("FloatingPointLogb.Companion.double") {
        val trait = FloatingPointLogb.double
        val minNormal = java.lang.Double.MIN_NORMAL

        context("logb special values") {
            test("logb(NaN) = NaN")   { with(trait) { Double.NaN.logb() }.shouldBeNaN() }
            test("logb(+∞) = +∞")    { with(trait) { Double.POSITIVE_INFINITY.logb() } shouldBe Double.POSITIVE_INFINITY }
            test("logb(-∞) = +∞")    { with(trait) { Double.NEGATIVE_INFINITY.logb() } shouldBe Double.POSITIVE_INFINITY }
            test("logb(+0) = -∞")    { with(trait) { 0.0.logb() } shouldBe Double.NEGATIVE_INFINITY }
            test("logb(-0) = -∞")    { with(trait) { (-0.0).logb() } shouldBe Double.NEGATIVE_INFINITY }
        }

        context("logb exact results") {
            test("logb(1.0) = 0.0")             { with(trait) { 1.0.logb() } shouldBe 0.0 }
            test("logb(2.0) = 1.0")             { with(trait) { 2.0.logb() } shouldBe 1.0 }
            test("logb(0.5) = -1.0")            { with(trait) { 0.5.logb() } shouldBe -1.0 }
            test("logb(-1.0) = 0.0")            { with(trait) { (-1.0).logb() } shouldBe 0.0 }
            test("logb(MAX_VALUE) = 1023.0")    { with(trait) { Double.MAX_VALUE.logb() } shouldBe 1023.0 }
            test("logb(MIN_NORMAL) = -1022.0")  { with(trait) { minNormal.logb() } shouldBe -1022.0 }
            test("logb(MIN_VALUE) = -1074.0")   { with(trait) { Double.MIN_VALUE.logb() } shouldBe -1074.0 }
        }

        context("ilogb special values") {
            test("ilogb(NaN) = Int.MAX_VALUE")  { with(trait) { Double.NaN.ilogb() } shouldBe Int.MAX_VALUE }
            test("ilogb(+∞) = Int.MAX_VALUE")   { with(trait) { Double.POSITIVE_INFINITY.ilogb() } shouldBe Int.MAX_VALUE }
            test("ilogb(-∞) = Int.MAX_VALUE")   { with(trait) { Double.NEGATIVE_INFINITY.ilogb() } shouldBe Int.MAX_VALUE }
            test("ilogb(0) = Int.MIN_VALUE")    { with(trait) { 0.0.ilogb() } shouldBe Int.MIN_VALUE }
        }

        context("ilogb exact results") {
            test("ilogb(1.0) = 0")            { with(trait) { 1.0.ilogb() } shouldBe 0 }
            test("ilogb(2.0) = 1")            { with(trait) { 2.0.ilogb() } shouldBe 1 }
            test("ilogb(0.5) = -1")           { with(trait) { 0.5.ilogb() } shouldBe -1 }
            test("ilogb(-1.0) = 0")           { with(trait) { (-1.0).ilogb() } shouldBe 0 }
            test("ilogb(MAX_VALUE) = 1023")   { with(trait) { Double.MAX_VALUE.ilogb() } shouldBe 1023 }
            test("ilogb(MIN_NORMAL) = -1022") { with(trait) { minNormal.ilogb() } shouldBe -1022 }
            test("ilogb(MIN_VALUE) = -1074")  { with(trait) { Double.MIN_VALUE.ilogb() } shouldBe -1074 }
        }
    }

    // ── FloatingPointLogb.Companion.float ─────────────────────────────────────

    context("FloatingPointLogb.Companion.float") {
        val trait = FloatingPointLogb.float
        val minNormal = java.lang.Float.MIN_NORMAL

        context("logb special values") {
            test("logb(NaN) = NaN")  { with(trait) { Float.NaN.logb() }.shouldBeNaN() }
            test("logb(+∞) = +∞")   { with(trait) { Float.POSITIVE_INFINITY.logb() } shouldBe Float.POSITIVE_INFINITY }
            test("logb(-∞) = +∞")   { with(trait) { Float.NEGATIVE_INFINITY.logb() } shouldBe Float.POSITIVE_INFINITY }
            test("logb(+0) = -∞")   { with(trait) { 0.0f.logb() } shouldBe Float.NEGATIVE_INFINITY }
            test("logb(-0) = -∞")   { with(trait) { (-0.0f).logb() } shouldBe Float.NEGATIVE_INFINITY }
        }

        context("logb exact results") {
            test("logb(1.0) = 0.0")            { with(trait) { 1.0f.logb() } shouldBe 0.0f }
            test("logb(2.0) = 1.0")            { with(trait) { 2.0f.logb() } shouldBe 1.0f }
            test("logb(0.5) = -1.0")           { with(trait) { 0.5f.logb() } shouldBe -1.0f }
            test("logb(-1.0) = 0.0")           { with(trait) { (-1.0f).logb() } shouldBe 0.0f }
            test("logb(MAX_VALUE) = 127.0")    { with(trait) { Float.MAX_VALUE.logb() } shouldBe 127.0f }
            test("logb(MIN_NORMAL) = -126.0")  { with(trait) { minNormal.logb() } shouldBe -126.0f }
            test("logb(MIN_VALUE) = -149.0")   { with(trait) { Float.MIN_VALUE.logb() } shouldBe -149.0f }
        }

        context("ilogb special values") {
            test("ilogb(NaN) = Int.MAX_VALUE")  { with(trait) { Float.NaN.ilogb() } shouldBe Int.MAX_VALUE }
            test("ilogb(+∞) = Int.MAX_VALUE")   { with(trait) { Float.POSITIVE_INFINITY.ilogb() } shouldBe Int.MAX_VALUE }
            test("ilogb(-∞) = Int.MAX_VALUE")   { with(trait) { Float.NEGATIVE_INFINITY.ilogb() } shouldBe Int.MAX_VALUE }
            test("ilogb(0) = Int.MIN_VALUE")    { with(trait) { 0.0f.ilogb() } shouldBe Int.MIN_VALUE }
        }

        context("ilogb exact results") {
            test("ilogb(1.0) = 0")           { with(trait) { 1.0f.ilogb() } shouldBe 0 }
            test("ilogb(2.0) = 1")           { with(trait) { 2.0f.ilogb() } shouldBe 1 }
            test("ilogb(0.5) = -1")          { with(trait) { 0.5f.ilogb() } shouldBe -1 }
            test("ilogb(-1.0) = 0")          { with(trait) { (-1.0f).ilogb() } shouldBe 0 }
            test("ilogb(MAX_VALUE) = 127")   { with(trait) { Float.MAX_VALUE.ilogb() } shouldBe 127 }
            test("ilogb(MIN_NORMAL) = -126") { with(trait) { minNormal.ilogb() } shouldBe -126 }
            test("ilogb(MIN_VALUE) = -149")  { with(trait) { Float.MIN_VALUE.ilogb() } shouldBe -149 }
        }
    }

    // ── FloatingPointLogb.Companion.bfloat16 ─────────────────────────────────

    context("FloatingPointLogb.Companion.bfloat16") {
        val trait = FloatingPointLogb.bfloat16

        context("logb special values") {
            test("logb(NaN) = NaN")  { with(trait) { BFloat16.NaN.logb() }.isNaN() shouldBe true }
            test("logb(+∞) = +∞")   { with(trait) { BFloat16.POSITIVE_INFINITY.logb() }.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
            test("logb(-∞) = +∞")   { with(trait) { BFloat16.NEGATIVE_INFINITY.logb() }.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
            test("logb(0) = -∞")    { with(trait) { BFloat16(0.0f).logb() }.bits shouldBe BFloat16.NEGATIVE_INFINITY.bits }
        }

        context("logb exact results") {
            test("logb(1.0) = 0.0")            { with(trait) { BFloat16(1.0f).logb() }.toFloat() shouldBe 0.0f }
            test("logb(2.0) = 1.0")            { with(trait) { BFloat16(2.0f).logb() }.toFloat() shouldBe 1.0f }
            test("logb(0.5) = -1.0")           { with(trait) { BFloat16(0.5f).logb() }.toFloat() shouldBe -1.0f }
            test("logb(-1.0) = 0.0")           { with(trait) { BFloat16(-1.0f).logb() }.toFloat() shouldBe 0.0f }
            test("logb(MAX_VALUE) = 127.0")    { with(trait) { BFloat16.MAX_VALUE.logb() }.toFloat() shouldBe 127.0f }
            test("logb(MIN_NORMAL) = -126.0")  { with(trait) { BFloat16.MIN_NORMAL.logb() }.toFloat() shouldBe -126.0f }
            test("logb(MIN_VALUE) = -133.0")   { with(trait) { BFloat16.MIN_VALUE.logb() }.toFloat() shouldBe -133.0f }
        }

        context("ilogb special values") {
            test("ilogb(NaN) = Int.MAX_VALUE")  { with(trait) { BFloat16.NaN.ilogb() } shouldBe Int.MAX_VALUE }
            test("ilogb(+∞) = Int.MAX_VALUE")   { with(trait) { BFloat16.POSITIVE_INFINITY.ilogb() } shouldBe Int.MAX_VALUE }
            test("ilogb(-∞) = Int.MAX_VALUE")   { with(trait) { BFloat16.NEGATIVE_INFINITY.ilogb() } shouldBe Int.MAX_VALUE }
            test("ilogb(0) = Int.MIN_VALUE")    { with(trait) { BFloat16(0.0f).ilogb() } shouldBe Int.MIN_VALUE }
        }

        context("ilogb exact results") {
            test("ilogb(1.0) = 0")           { with(trait) { BFloat16(1.0f).ilogb() } shouldBe 0 }
            test("ilogb(2.0) = 1")           { with(trait) { BFloat16(2.0f).ilogb() } shouldBe 1 }
            test("ilogb(0.5) = -1")          { with(trait) { BFloat16(0.5f).ilogb() } shouldBe -1 }
            test("ilogb(MAX_VALUE) = 127")   { with(trait) { BFloat16.MAX_VALUE.ilogb() } shouldBe 127 }
            test("ilogb(MIN_NORMAL) = -126") { with(trait) { BFloat16.MIN_NORMAL.ilogb() } shouldBe -126 }
            test("ilogb(MIN_VALUE) = -133")  { with(trait) { BFloat16.MIN_VALUE.ilogb() } shouldBe -133 }
        }
    }

    // ── FloatingPointLogb.Companion.float16 ──────────────────────────────────

    context("FloatingPointLogb.Companion.float16") {
        val trait = FloatingPointLogb.float16

        context("logb special values") {
            test("logb(NaN) = NaN")  { with(trait) { Float16.NaN.logb() }.isNaN() shouldBe true }
            test("logb(+∞) = +∞")   { with(trait) { Float16.POSITIVE_INFINITY.logb() }.bits shouldBe Float16.POSITIVE_INFINITY.bits }
            test("logb(-∞) = +∞")   { with(trait) { Float16.NEGATIVE_INFINITY.logb() }.bits shouldBe Float16.POSITIVE_INFINITY.bits }
            test("logb(0) = -∞")    { with(trait) { Float16(0.0f).logb() }.bits shouldBe Float16.NEGATIVE_INFINITY.bits }
        }

        context("logb exact results") {
            test("logb(1.0) = 0.0")           { with(trait) { Float16(1.0f).logb() }.toFloat() shouldBe 0.0f }
            test("logb(2.0) = 1.0")           { with(trait) { Float16(2.0f).logb() }.toFloat() shouldBe 1.0f }
            test("logb(0.5) = -1.0")          { with(trait) { Float16(0.5f).logb() }.toFloat() shouldBe -1.0f }
            test("logb(-1.0) = 0.0")          { with(trait) { Float16(-1.0f).logb() }.toFloat() shouldBe 0.0f }
            test("logb(MAX_VALUE) = 15.0")    { with(trait) { Float16.MAX_VALUE.logb() }.toFloat() shouldBe 15.0f }
            test("logb(MIN_NORMAL) = -14.0")  { with(trait) { Float16.MIN_NORMAL.logb() }.toFloat() shouldBe -14.0f }
            test("logb(MIN_VALUE) = -24.0")   { with(trait) { Float16.MIN_VALUE.logb() }.toFloat() shouldBe -24.0f }
        }

        context("ilogb special values") {
            test("ilogb(NaN) = Int.MAX_VALUE")  { with(trait) { Float16.NaN.ilogb() } shouldBe Int.MAX_VALUE }
            test("ilogb(+∞) = Int.MAX_VALUE")   { with(trait) { Float16.POSITIVE_INFINITY.ilogb() } shouldBe Int.MAX_VALUE }
            test("ilogb(-∞) = Int.MAX_VALUE")   { with(trait) { Float16.NEGATIVE_INFINITY.ilogb() } shouldBe Int.MAX_VALUE }
            test("ilogb(0) = Int.MIN_VALUE")    { with(trait) { Float16(0.0f).ilogb() } shouldBe Int.MIN_VALUE }
        }

        context("ilogb exact results") {
            test("ilogb(1.0) = 0")          { with(trait) { Float16(1.0f).ilogb() } shouldBe 0 }
            test("ilogb(2.0) = 1")          { with(trait) { Float16(2.0f).ilogb() } shouldBe 1 }
            test("ilogb(0.5) = -1")         { with(trait) { Float16(0.5f).ilogb() } shouldBe -1 }
            test("ilogb(MAX_VALUE) = 15")   { with(trait) { Float16.MAX_VALUE.ilogb() } shouldBe 15 }
            test("ilogb(MIN_NORMAL) = -14") { with(trait) { Float16.MIN_NORMAL.ilogb() } shouldBe -14 }
            test("ilogb(MIN_VALUE) = -24")  { with(trait) { Float16.MIN_VALUE.ilogb() } shouldBe -24 }
        }
    }
})
