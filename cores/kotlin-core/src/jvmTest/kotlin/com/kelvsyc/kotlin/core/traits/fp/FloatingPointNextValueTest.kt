package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.floats.shouldBeNaN
import io.kotest.matchers.shouldBe

class FloatingPointNextValueTest : FunSpec({

    // ── FloatingPointNextValue.Companion.double ───────────────────────────────

    context("FloatingPointNextValue.Companion.double") {
        val trait = FloatingPointNextValue.double

        context("nextUp special values") {
            test("nextUp(NaN) = NaN") { with(trait) { Double.NaN.nextUp() }.shouldBeNaN() }
            test("nextUp(+∞) = +∞") { with(trait) { Double.POSITIVE_INFINITY.nextUp() } shouldBe Double.POSITIVE_INFINITY }
            test("nextUp(-∞) = -MAX_VALUE") { with(trait) { Double.NEGATIVE_INFINITY.nextUp() } shouldBe -Double.MAX_VALUE }
        }

        context("nextDown special values") {
            test("nextDown(NaN) = NaN") { with(trait) { Double.NaN.nextDown() }.shouldBeNaN() }
            test("nextDown(-∞) = -∞") { with(trait) { Double.NEGATIVE_INFINITY.nextDown() } shouldBe Double.NEGATIVE_INFINITY }
            test("nextDown(+∞) = MAX_VALUE") { with(trait) { Double.POSITIVE_INFINITY.nextDown() } shouldBe Double.MAX_VALUE }
        }

        context("exact results") {
            test("nextUp(0.0) = MIN_VALUE") { with(trait) { 0.0.nextUp() } shouldBe Double.MIN_VALUE }
            test("nextDown(0.0) = -MIN_VALUE") { with(trait) { 0.0.nextDown() } shouldBe -Double.MIN_VALUE }
            test("nextUp(MAX_VALUE) = +∞") { with(trait) { Double.MAX_VALUE.nextUp() } shouldBe Double.POSITIVE_INFINITY }
            test("nextDown(-MAX_VALUE) = -∞") { with(trait) { (-Double.MAX_VALUE).nextDown() } shouldBe Double.NEGATIVE_INFINITY }
            test("nextDown(nextUp(x)) = x") { with(trait) { 1.0.nextUp().nextDown() } shouldBe 1.0 }
            test("nextUp(nextDown(x)) = x") { with(trait) { 1.0.nextDown().nextUp() } shouldBe 1.0 }
        }
    }

    // ── FloatingPointNextValue.Companion.float ────────────────────────────────

    context("FloatingPointNextValue.Companion.float") {
        val trait = FloatingPointNextValue.float

        context("nextUp special values") {
            test("nextUp(NaN) = NaN") { with(trait) { Float.NaN.nextUp() }.shouldBeNaN() }
            test("nextUp(+∞) = +∞") { with(trait) { Float.POSITIVE_INFINITY.nextUp() } shouldBe Float.POSITIVE_INFINITY }
            test("nextUp(-∞) = -MAX_VALUE") { with(trait) { Float.NEGATIVE_INFINITY.nextUp() } shouldBe -Float.MAX_VALUE }
        }

        context("nextDown special values") {
            test("nextDown(NaN) = NaN") { with(trait) { Float.NaN.nextDown() }.shouldBeNaN() }
            test("nextDown(-∞) = -∞") { with(trait) { Float.NEGATIVE_INFINITY.nextDown() } shouldBe Float.NEGATIVE_INFINITY }
            test("nextDown(+∞) = MAX_VALUE") { with(trait) { Float.POSITIVE_INFINITY.nextDown() } shouldBe Float.MAX_VALUE }
        }

        context("exact results") {
            test("nextUp(0.0) = MIN_VALUE") { with(trait) { 0.0f.nextUp() } shouldBe Float.MIN_VALUE }
            test("nextDown(0.0) = -MIN_VALUE") { with(trait) { 0.0f.nextDown() } shouldBe -Float.MIN_VALUE }
            test("nextUp(MAX_VALUE) = +∞") { with(trait) { Float.MAX_VALUE.nextUp() } shouldBe Float.POSITIVE_INFINITY }
            test("nextDown(-MAX_VALUE) = -∞") { with(trait) { (-Float.MAX_VALUE).nextDown() } shouldBe Float.NEGATIVE_INFINITY }
            test("nextDown(nextUp(x)) = x") { with(trait) { 1.0f.nextUp().nextDown() } shouldBe 1.0f }
            test("nextUp(nextDown(x)) = x") { with(trait) { 1.0f.nextDown().nextUp() } shouldBe 1.0f }
        }
    }

    // ── FloatingPointNextValue.Companion.bfloat16 ────────────────────────────

    context("FloatingPointNextValue.Companion.bfloat16") {
        val trait = FloatingPointNextValue.bfloat16

        context("nextUp special values") {
            test("nextUp(NaN) = NaN") { with(trait) { BFloat16.NaN.nextUp() }.isNaN() shouldBe true }
            test("nextUp(+∞) = +∞") { with(trait) { BFloat16.POSITIVE_INFINITY.nextUp() }.bits shouldBe BFloat16.POSITIVE_INFINITY.bits }
            test("nextUp(-∞) is finite") { with(trait) { BFloat16.NEGATIVE_INFINITY.nextUp() }.isInfinite() shouldBe false }
        }

        context("nextDown special values") {
            test("nextDown(NaN) = NaN") { with(trait) { BFloat16.NaN.nextDown() }.isNaN() shouldBe true }
            test("nextDown(-∞) = -∞") { with(trait) { BFloat16.NEGATIVE_INFINITY.nextDown() }.bits shouldBe BFloat16.NEGATIVE_INFINITY.bits }
            test("nextDown(+∞) is finite") { with(trait) { BFloat16.POSITIVE_INFINITY.nextDown() }.isInfinite() shouldBe false }
        }

        context("exact results") {
            test("nextUp(0.0) is positive") { with(trait) { BFloat16(0.0f).nextUp() }.toFloat() shouldBe BFloat16.MIN_VALUE.toFloat() }
            test("nextDown(0.0) is negative") { with(trait) { BFloat16(0.0f).nextDown() }.toFloat() shouldBe -BFloat16.MIN_VALUE.toFloat() }
            test("nextDown(nextUp(x)) = x") {
                val x = BFloat16(1.0f)
                with(trait) { x.nextUp().nextDown() }.bits shouldBe x.bits
            }
            test("nextUp(nextDown(x)) = x") {
                val x = BFloat16(1.0f)
                with(trait) { x.nextDown().nextUp() }.bits shouldBe x.bits
            }
        }
    }

    // ── FloatingPointNextValue.Companion.float16 ─────────────────────────────

    context("FloatingPointNextValue.Companion.float16") {
        val trait = FloatingPointNextValue.float16

        context("nextUp special values") {
            test("nextUp(NaN) = NaN") { with(trait) { Float16.NaN.nextUp() }.isNaN() shouldBe true }
            test("nextUp(+∞) = +∞") { with(trait) { Float16.POSITIVE_INFINITY.nextUp() }.bits shouldBe Float16.POSITIVE_INFINITY.bits }
            test("nextUp(-∞) is finite") { with(trait) { Float16.NEGATIVE_INFINITY.nextUp() }.isInfinite() shouldBe false }
        }

        context("nextDown special values") {
            test("nextDown(NaN) = NaN") { with(trait) { Float16.NaN.nextDown() }.isNaN() shouldBe true }
            test("nextDown(-∞) = -∞") { with(trait) { Float16.NEGATIVE_INFINITY.nextDown() }.bits shouldBe Float16.NEGATIVE_INFINITY.bits }
            test("nextDown(+∞) is finite") { with(trait) { Float16.POSITIVE_INFINITY.nextDown() }.isInfinite() shouldBe false }
        }

        context("exact results") {
            test("nextUp(0.0) is positive") { with(trait) { Float16(0.0f).nextUp() }.toFloat() shouldBe Float16.MIN_VALUE.toFloat() }
            test("nextDown(0.0) is negative") { with(trait) { Float16(0.0f).nextDown() }.toFloat() shouldBe -Float16.MIN_VALUE.toFloat() }
            test("nextDown(nextUp(x)) = x") {
                val x = Float16(1.0f)
                with(trait) { x.nextUp().nextDown() }.bits shouldBe x.bits
            }
            test("nextUp(nextDown(x)) = x") {
                val x = Float16(1.0f)
                with(trait) { x.nextDown().nextUp() }.bits shouldBe x.bits
            }
        }
    }
})
