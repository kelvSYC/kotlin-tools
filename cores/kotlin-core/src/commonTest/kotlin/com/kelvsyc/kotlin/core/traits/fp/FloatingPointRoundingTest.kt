package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import com.kelvsyc.kotlin.core.fp.DoubleDouble
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FloatingPointRoundingTest : FunSpec({

    // ── BFloat16 ──────────────────────────────────────────────────────────────

    context("FloatingPointRounding.Companion.bfloat16") {
        val ops = FloatingPointRounding.bfloat16

        context("floor") {
            test("positive non-integer rounds down") {
                with(ops) { BFloat16(1.5f).floor() } shouldBe BFloat16(1.0f)
            }
            test("negative non-integer rounds down") {
                with(ops) { BFloat16(-1.5f).floor() } shouldBe BFloat16(-2.0f)
            }
            test("integer is unchanged") {
                with(ops) { BFloat16(3.0f).floor() } shouldBe BFloat16(3.0f)
            }
            test("NaN returns NaN") {
                with(ops) { BFloat16.NaN.floor() }.isNaN() shouldBe true
            }
            test("positive infinity returns positive infinity") {
                with(ops) { BFloat16.POSITIVE_INFINITY.floor() } shouldBe BFloat16.POSITIVE_INFINITY
            }
        }

        context("ceil") {
            test("positive non-integer rounds up") {
                with(ops) { BFloat16(1.5f).ceil() } shouldBe BFloat16(2.0f)
            }
            test("negative non-integer rounds up") {
                with(ops) { BFloat16(-1.5f).ceil() } shouldBe BFloat16(-1.0f)
            }
            test("integer is unchanged") {
                with(ops) { BFloat16(3.0f).ceil() } shouldBe BFloat16(3.0f)
            }
            test("NaN returns NaN") {
                with(ops) { BFloat16.NaN.ceil() }.isNaN() shouldBe true
            }
            test("negative infinity returns negative infinity") {
                with(ops) { BFloat16.NEGATIVE_INFINITY.ceil() } shouldBe BFloat16.NEGATIVE_INFINITY
            }
        }
    }

    // ── Float16 ───────────────────────────────────────────────────────────────

    context("FloatingPointRounding.Companion.float16") {
        val ops = FloatingPointRounding.float16

        context("floor") {
            test("positive non-integer rounds down") {
                with(ops) { Float16(1.5f).floor() } shouldBe Float16(1.0f)
            }
            test("negative non-integer rounds down") {
                with(ops) { Float16(-1.5f).floor() } shouldBe Float16(-2.0f)
            }
            test("integer is unchanged") {
                with(ops) { Float16(3.0f).floor() } shouldBe Float16(3.0f)
            }
            test("NaN returns NaN") {
                with(ops) { Float16.NaN.floor() }.isNaN() shouldBe true
            }
            test("positive infinity returns positive infinity") {
                with(ops) { Float16.POSITIVE_INFINITY.floor() } shouldBe Float16.POSITIVE_INFINITY
            }
        }

        context("ceil") {
            test("positive non-integer rounds up") {
                with(ops) { Float16(1.5f).ceil() } shouldBe Float16(2.0f)
            }
            test("negative non-integer rounds up") {
                with(ops) { Float16(-1.5f).ceil() } shouldBe Float16(-1.0f)
            }
            test("integer is unchanged") {
                with(ops) { Float16(3.0f).ceil() } shouldBe Float16(3.0f)
            }
            test("NaN returns NaN") {
                with(ops) { Float16.NaN.ceil() }.isNaN() shouldBe true
            }
            test("negative infinity returns negative infinity") {
                with(ops) { Float16.NEGATIVE_INFINITY.ceil() } shouldBe Float16.NEGATIVE_INFINITY
            }
        }
    }

    // ── Float ─────────────────────────────────────────────────────────────────

    context("FloatingPointRounding.Companion.float") {
        val ops = FloatingPointRounding.float

        context("floor") {
            test("positive non-integer rounds down") {
                with(ops) { 1.5f.floor() } shouldBe 1.0f
            }
            test("negative non-integer rounds down") {
                with(ops) { (-1.5f).floor() } shouldBe -2.0f
            }
            test("integer is unchanged") {
                with(ops) { 3.0f.floor() } shouldBe 3.0f
            }
            test("NaN returns NaN") {
                with(ops) { Float.NaN.floor() }.isNaN() shouldBe true
            }
            test("positive infinity returns positive infinity") {
                with(ops) { Float.POSITIVE_INFINITY.floor() } shouldBe Float.POSITIVE_INFINITY
            }
        }

        context("ceil") {
            test("positive non-integer rounds up") {
                with(ops) { 1.5f.ceil() } shouldBe 2.0f
            }
            test("negative non-integer rounds up") {
                with(ops) { (-1.5f).ceil() } shouldBe -1.0f
            }
            test("integer is unchanged") {
                with(ops) { 3.0f.ceil() } shouldBe 3.0f
            }
            test("NaN returns NaN") {
                with(ops) { Float.NaN.ceil() }.isNaN() shouldBe true
            }
            test("negative infinity returns negative infinity") {
                with(ops) { Float.NEGATIVE_INFINITY.ceil() } shouldBe Float.NEGATIVE_INFINITY
            }
        }
    }

    // ── Double ────────────────────────────────────────────────────────────────

    context("FloatingPointRounding.Companion.double") {
        val ops = FloatingPointRounding.double

        context("floor") {
            test("positive non-integer rounds down") {
                with(ops) { 1.5.floor() } shouldBe 1.0
            }
            test("negative non-integer rounds down") {
                with(ops) { (-1.5).floor() } shouldBe -2.0
            }
            test("integer is unchanged") {
                with(ops) { 3.0.floor() } shouldBe 3.0
            }
            test("NaN returns NaN") {
                with(ops) { Double.NaN.floor() }.isNaN() shouldBe true
            }
            test("positive infinity returns positive infinity") {
                with(ops) { Double.POSITIVE_INFINITY.floor() } shouldBe Double.POSITIVE_INFINITY
            }
        }

        context("ceil") {
            test("positive non-integer rounds up") {
                with(ops) { 1.5.ceil() } shouldBe 2.0
            }
            test("negative non-integer rounds up") {
                with(ops) { (-1.5).ceil() } shouldBe -1.0
            }
            test("integer is unchanged") {
                with(ops) { 3.0.ceil() } shouldBe 3.0
            }
            test("NaN returns NaN") {
                with(ops) { Double.NaN.ceil() }.isNaN() shouldBe true
            }
            test("negative infinity returns negative infinity") {
                with(ops) { Double.NEGATIVE_INFINITY.ceil() } shouldBe Double.NEGATIVE_INFINITY
            }
        }
    }

    // ── DoubleDouble ──────────────────────────────────────────────────────────

    context("FloatingPointRounding.Companion.doubleDouble") {
        val ops = FloatingPointRounding.doubleDouble

        context("floor") {
            test("positive non-integer hi, lo ignored") {
                // hi=1.5 is not an integer → result is (floor(1.5), 0) = (1.0, 0.0)
                val d = DoubleDouble.create(1.5, 0.0)
                val r = with(ops) { d.floor() }
                r.high shouldBe 1.0
                r.low shouldBe 0.0
            }
            test("negative non-integer hi, lo ignored") {
                val d = DoubleDouble.create(1.5, 0.0)
                val r = with(ops) { DoubleDouble.create(-1.5, 0.0).floor() }
                r.high shouldBe -2.0
                r.low shouldBe 0.0
            }
            test("integer hi, positive lo: lo has no fractional effect") {
                // floor(3.0 + 0.3) = floor(3.3) = 3.0
                val d = DoubleDouble.create(3.0, 0.3)
                val r = with(ops) { d.floor() }
                r.high shouldBe 3.0
                r.low shouldBe 0.0
            }
            test("integer hi, negative lo: lo carries borrow") {
                // floor(3.0 + (-0.3)) = floor(2.7) = 2.0
                val d = DoubleDouble.create(3.0, -0.3)
                val r = with(ops) { d.floor() }
                r.high shouldBe 2.0
                r.low shouldBe 0.0
            }
            test("NaN returns NaN") {
                with(ops) { DoubleDouble.NaN.floor() }.high.isNaN() shouldBe true
            }
            test("positive infinity returns positive infinity") {
                val r = with(ops) { DoubleDouble.POSITIVE_INFINITY.floor() }
                r.high shouldBe Double.POSITIVE_INFINITY
                r.low shouldBe 0.0
            }
        }

        context("ceil") {
            test("positive non-integer hi, lo ignored") {
                // ceil(1.5) = 2.0
                val r = with(ops) { DoubleDouble.create(1.5, 0.0).ceil() }
                r.high shouldBe 2.0
                r.low shouldBe 0.0
            }
            test("negative non-integer hi, lo ignored") {
                // ceil(-1.5) = -1.0
                val r = with(ops) { DoubleDouble.create(-1.5, 0.0).ceil() }
                r.high shouldBe -1.0
                r.low shouldBe 0.0
            }
            test("integer hi, positive lo: lo carries increment") {
                // ceil(3.0 + 0.3) = ceil(3.3) = 4.0
                val d = DoubleDouble.create(3.0, 0.3)
                val r = with(ops) { d.ceil() }
                r.high shouldBe 4.0
                r.low shouldBe 0.0
            }
            test("integer hi, negative lo: lo has no fractional effect") {
                // ceil(3.0 + (-0.3)) = ceil(2.7) = 3.0
                val d = DoubleDouble.create(3.0, -0.3)
                val r = with(ops) { d.ceil() }
                r.high shouldBe 3.0
                r.low shouldBe 0.0
            }
            test("NaN returns NaN") {
                with(ops) { DoubleDouble.NaN.ceil() }.high.isNaN() shouldBe true
            }
            test("negative infinity returns negative infinity") {
                val r = with(ops) { DoubleDouble.NEGATIVE_INFINITY.ceil() }
                r.high shouldBe Double.NEGATIVE_INFINITY
                r.low shouldBe 0.0
            }
        }
    }
})
