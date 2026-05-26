package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FloatingPointNearestRoundingTest : FunSpec({

    // ── BFloat16 ──────────────────────────────────────────────────────────────

    context("FloatingPointNearestRounding.Companion.bfloat16") {
        val ops = FloatingPointNearestRounding.bfloat16

        context("roundHalfUp (ties away from zero)") {
            test("tie rounds away from zero (positive)") {
                with(ops) { BFloat16(2.5f).roundHalfUp() } shouldBe BFloat16(3.0f)
            }
            test("tie rounds away from zero (negative)") {
                with(ops) { BFloat16(-2.5f).roundHalfUp() } shouldBe BFloat16(-3.0f)
            }
            test("non-tie rounds to nearest") {
                with(ops) { BFloat16(2.3f).roundHalfUp() } shouldBe BFloat16(2.0f)
                with(ops) { BFloat16(2.7f).roundHalfUp() } shouldBe BFloat16(3.0f)
            }
            test("NaN returns NaN") { with(ops) { BFloat16.NaN.roundHalfUp() }.isNaN() shouldBe true }
            test("+∞ returns +∞") { with(ops) { BFloat16.POSITIVE_INFINITY.roundHalfUp() } shouldBe BFloat16.POSITIVE_INFINITY }
        }

        context("roundHalfDown (ties toward zero)") {
            test("tie rounds toward zero (positive)") {
                with(ops) { BFloat16(2.5f).roundHalfDown() } shouldBe BFloat16(2.0f)
            }
            test("tie rounds toward zero (negative)") {
                with(ops) { BFloat16(-2.5f).roundHalfDown() } shouldBe BFloat16(-2.0f)
            }
            test("non-tie rounds to nearest") {
                with(ops) { BFloat16(2.3f).roundHalfDown() } shouldBe BFloat16(2.0f)
                with(ops) { BFloat16(2.7f).roundHalfDown() } shouldBe BFloat16(3.0f)
            }
            test("NaN returns NaN") { with(ops) { BFloat16.NaN.roundHalfDown() }.isNaN() shouldBe true }
            test("+∞ returns +∞") { with(ops) { BFloat16.POSITIVE_INFINITY.roundHalfDown() } shouldBe BFloat16.POSITIVE_INFINITY }
        }

        context("roundEven (ties to even)") {
            test("tie to even rounds down (2.5 → 2)") {
                with(ops) { BFloat16(2.5f).roundEven() } shouldBe BFloat16(2.0f)
            }
            test("tie to even rounds up (3.5 → 4)") {
                with(ops) { BFloat16(3.5f).roundEven() } shouldBe BFloat16(4.0f)
            }
            test("tie to even (negative): -2.5 → -2") {
                with(ops) { BFloat16(-2.5f).roundEven() } shouldBe BFloat16(-2.0f)
            }
            test("non-tie rounds to nearest") {
                with(ops) { BFloat16(2.3f).roundEven() } shouldBe BFloat16(2.0f)
                with(ops) { BFloat16(2.7f).roundEven() } shouldBe BFloat16(3.0f)
            }
            test("NaN returns NaN") { with(ops) { BFloat16.NaN.roundEven() }.isNaN() shouldBe true }
            test("+∞ returns +∞") { with(ops) { BFloat16.POSITIVE_INFINITY.roundEven() } shouldBe BFloat16.POSITIVE_INFINITY }
        }
    }

    // ── Float16 ───────────────────────────────────────────────────────────────

    context("FloatingPointNearestRounding.Companion.float16") {
        val ops = FloatingPointNearestRounding.float16

        context("roundHalfUp (ties away from zero)") {
            test("tie rounds away from zero (positive)") {
                with(ops) { Float16(2.5f).roundHalfUp() } shouldBe Float16(3.0f)
            }
            test("tie rounds away from zero (negative)") {
                with(ops) { Float16(-2.5f).roundHalfUp() } shouldBe Float16(-3.0f)
            }
            test("non-tie rounds to nearest") {
                with(ops) { Float16(2.3f).roundHalfUp() } shouldBe Float16(2.0f)
                with(ops) { Float16(2.7f).roundHalfUp() } shouldBe Float16(3.0f)
            }
            test("NaN returns NaN") { with(ops) { Float16.NaN.roundHalfUp() }.isNaN() shouldBe true }
            test("+∞ returns +∞") { with(ops) { Float16.POSITIVE_INFINITY.roundHalfUp() } shouldBe Float16.POSITIVE_INFINITY }
        }

        context("roundHalfDown (ties toward zero)") {
            test("tie rounds toward zero (positive)") {
                with(ops) { Float16(2.5f).roundHalfDown() } shouldBe Float16(2.0f)
            }
            test("tie rounds toward zero (negative)") {
                with(ops) { Float16(-2.5f).roundHalfDown() } shouldBe Float16(-2.0f)
            }
            test("non-tie rounds to nearest") {
                with(ops) { Float16(2.3f).roundHalfDown() } shouldBe Float16(2.0f)
                with(ops) { Float16(2.7f).roundHalfDown() } shouldBe Float16(3.0f)
            }
            test("NaN returns NaN") { with(ops) { Float16.NaN.roundHalfDown() }.isNaN() shouldBe true }
            test("+∞ returns +∞") { with(ops) { Float16.POSITIVE_INFINITY.roundHalfDown() } shouldBe Float16.POSITIVE_INFINITY }
        }

        context("roundEven (ties to even)") {
            test("tie to even rounds down (2.5 → 2)") {
                with(ops) { Float16(2.5f).roundEven() } shouldBe Float16(2.0f)
            }
            test("tie to even rounds up (3.5 → 4)") {
                with(ops) { Float16(3.5f).roundEven() } shouldBe Float16(4.0f)
            }
            test("tie to even (negative): -2.5 → -2") {
                with(ops) { Float16(-2.5f).roundEven() } shouldBe Float16(-2.0f)
            }
            test("non-tie rounds to nearest") {
                with(ops) { Float16(2.3f).roundEven() } shouldBe Float16(2.0f)
                with(ops) { Float16(2.7f).roundEven() } shouldBe Float16(3.0f)
            }
            test("NaN returns NaN") { with(ops) { Float16.NaN.roundEven() }.isNaN() shouldBe true }
            test("+∞ returns +∞") { with(ops) { Float16.POSITIVE_INFINITY.roundEven() } shouldBe Float16.POSITIVE_INFINITY }
        }
    }

    // ── Float ─────────────────────────────────────────────────────────────────

    context("FloatingPointNearestRounding.Companion.float") {
        val ops = FloatingPointNearestRounding.float

        context("roundHalfUp (ties away from zero)") {
            test("tie rounds away from zero (positive)") { with(ops) { 2.5f.roundHalfUp() } shouldBe 3.0f }
            test("tie rounds away from zero (negative)") { with(ops) { (-2.5f).roundHalfUp() } shouldBe -3.0f }
            test("non-tie rounds to nearest") {
                with(ops) { 2.3f.roundHalfUp() } shouldBe 2.0f
                with(ops) { 2.7f.roundHalfUp() } shouldBe 3.0f
            }
            test("NaN returns NaN") { with(ops) { Float.NaN.roundHalfUp() }.isNaN() shouldBe true }
            test("+∞ returns +∞") { with(ops) { Float.POSITIVE_INFINITY.roundHalfUp() } shouldBe Float.POSITIVE_INFINITY }
        }

        context("roundHalfDown (ties toward zero)") {
            test("tie rounds toward zero (positive)") { with(ops) { 2.5f.roundHalfDown() } shouldBe 2.0f }
            test("tie rounds toward zero (negative)") { with(ops) { (-2.5f).roundHalfDown() } shouldBe -2.0f }
            test("non-tie rounds to nearest") {
                with(ops) { 2.3f.roundHalfDown() } shouldBe 2.0f
                with(ops) { 2.7f.roundHalfDown() } shouldBe 3.0f
            }
            test("NaN returns NaN") { with(ops) { Float.NaN.roundHalfDown() }.isNaN() shouldBe true }
            test("+∞ returns +∞") { with(ops) { Float.POSITIVE_INFINITY.roundHalfDown() } shouldBe Float.POSITIVE_INFINITY }
        }

        context("roundEven (ties to even)") {
            test("tie to even rounds down (2.5 → 2)") { with(ops) { 2.5f.roundEven() } shouldBe 2.0f }
            test("tie to even rounds up (3.5 → 4)")   { with(ops) { 3.5f.roundEven() } shouldBe 4.0f }
            test("tie to even (negative): -2.5 → -2") { with(ops) { (-2.5f).roundEven() } shouldBe -2.0f }
            test("non-tie rounds to nearest") {
                with(ops) { 2.3f.roundEven() } shouldBe 2.0f
                with(ops) { 2.7f.roundEven() } shouldBe 3.0f
            }
            test("NaN returns NaN") { with(ops) { Float.NaN.roundEven() }.isNaN() shouldBe true }
            test("+∞ returns +∞") { with(ops) { Float.POSITIVE_INFINITY.roundEven() } shouldBe Float.POSITIVE_INFINITY }
        }
    }

    // ── Double ────────────────────────────────────────────────────────────────

    context("FloatingPointNearestRounding.Companion.double") {
        val ops = FloatingPointNearestRounding.double

        context("roundHalfUp (ties away from zero)") {
            test("tie rounds away from zero (positive)") { with(ops) { 2.5.roundHalfUp() } shouldBe 3.0 }
            test("tie rounds away from zero (negative)") { with(ops) { (-2.5).roundHalfUp() } shouldBe -3.0 }
            test("non-tie rounds to nearest") {
                with(ops) { 2.3.roundHalfUp() } shouldBe 2.0
                with(ops) { 2.7.roundHalfUp() } shouldBe 3.0
            }
            test("NaN returns NaN") { with(ops) { Double.NaN.roundHalfUp() }.isNaN() shouldBe true }
            test("+∞ returns +∞") { with(ops) { Double.POSITIVE_INFINITY.roundHalfUp() } shouldBe Double.POSITIVE_INFINITY }
        }

        context("roundHalfDown (ties toward zero)") {
            test("tie rounds toward zero (positive)") { with(ops) { 2.5.roundHalfDown() } shouldBe 2.0 }
            test("tie rounds toward zero (negative)") { with(ops) { (-2.5).roundHalfDown() } shouldBe -2.0 }
            test("non-tie rounds to nearest") {
                with(ops) { 2.3.roundHalfDown() } shouldBe 2.0
                with(ops) { 2.7.roundHalfDown() } shouldBe 3.0
            }
            test("NaN returns NaN") { with(ops) { Double.NaN.roundHalfDown() }.isNaN() shouldBe true }
            test("+∞ returns +∞") { with(ops) { Double.POSITIVE_INFINITY.roundHalfDown() } shouldBe Double.POSITIVE_INFINITY }
        }

        context("roundEven (ties to even)") {
            test("tie to even rounds down (2.5 → 2)") { with(ops) { 2.5.roundEven() } shouldBe 2.0 }
            test("tie to even rounds up (3.5 → 4)")   { with(ops) { 3.5.roundEven() } shouldBe 4.0 }
            test("tie to even (negative): -2.5 → -2") { with(ops) { (-2.5).roundEven() } shouldBe -2.0 }
            test("non-tie rounds to nearest") {
                with(ops) { 2.3.roundEven() } shouldBe 2.0
                with(ops) { 2.7.roundEven() } shouldBe 3.0
            }
            test("NaN returns NaN") { with(ops) { Double.NaN.roundEven() }.isNaN() shouldBe true }
            test("+∞ returns +∞") { with(ops) { Double.POSITIVE_INFINITY.roundEven() } shouldBe Double.POSITIVE_INFINITY }
        }
    }
})
