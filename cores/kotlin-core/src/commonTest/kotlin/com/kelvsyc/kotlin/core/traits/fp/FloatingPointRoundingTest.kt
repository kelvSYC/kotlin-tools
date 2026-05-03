package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
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

})
