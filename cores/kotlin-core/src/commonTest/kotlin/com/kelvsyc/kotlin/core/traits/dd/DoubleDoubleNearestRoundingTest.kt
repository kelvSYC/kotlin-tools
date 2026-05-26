package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointNearestRounding
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DoubleDoubleNearestRoundingTest : FunSpec({

    context("FloatingPointNearestRounding.Companion.doubleDouble") {
        val ops = FloatingPointNearestRounding.doubleDouble

        context("roundHalfUp") {
            test("positive non-integer rounds to nearest (away from zero at tie)") {
                val r = with(ops) { DoubleDouble.create(1.3, 0.0).roundHalfUp() }
                r.high shouldBe 1.0
                r.low shouldBe 0.0
            }
            test("positive non-integer 1.7 rounds up") {
                val r = with(ops) { DoubleDouble.create(1.7, 0.0).roundHalfUp() }
                r.high shouldBe 2.0
                r.low shouldBe 0.0
            }
            test("positive half-integer 0.5 rounds away from zero to 1.0") {
                val r = with(ops) { DoubleDouble.create(0.5, 0.0).roundHalfUp() }
                r.high shouldBe 1.0
                r.low shouldBe 0.0
            }
            test("positive half-integer 1.5 rounds away from zero to 2.0") {
                val r = with(ops) { DoubleDouble.create(1.5, 0.0).roundHalfUp() }
                r.high shouldBe 2.0
                r.low shouldBe 0.0
            }
            test("negative non-integer -1.3 rounds to nearest") {
                val r = with(ops) { DoubleDouble.create(-1.3, 0.0).roundHalfUp() }
                r.high shouldBe -1.0
                r.low shouldBe 0.0
            }
            test("negative non-integer -1.7 rounds away from zero") {
                val r = with(ops) { DoubleDouble.create(-1.7, 0.0).roundHalfUp() }
                r.high shouldBe -2.0
                r.low shouldBe 0.0
            }
            test("negative half-integer -0.5 rounds away from zero to -1.0") {
                val r = with(ops) { DoubleDouble.create(-0.5, 0.0).roundHalfUp() }
                r.high shouldBe -1.0
                r.low shouldBe 0.0
            }
            test("negative half-integer -1.5 rounds away from zero to -2.0") {
                val r = with(ops) { DoubleDouble.create(-1.5, 0.0).roundHalfUp() }
                r.high shouldBe -2.0
                r.low shouldBe 0.0
            }
            test("integer hi, positive lo: lo rounds to 0") {
                val r = with(ops) { DoubleDouble.create(3.0, 0.3).roundHalfUp() }
                r.high shouldBe 3.0
                r.low shouldBe 0.0
            }
            test("integer hi, negative lo: lo rounds to 0") {
                val r = with(ops) { DoubleDouble.create(3.0, -0.3).roundHalfUp() }
                r.high shouldBe 3.0
                r.low shouldBe 0.0
            }
            test("NaN returns NaN") {
                with(ops) { DoubleDouble.NaN.roundHalfUp() }.high.isNaN() shouldBe true
            }
            test("positive infinity returns positive infinity") {
                val r = with(ops) { DoubleDouble.POSITIVE_INFINITY.roundHalfUp() }
                r.high shouldBe Double.POSITIVE_INFINITY
                r.low shouldBe 0.0
            }
            test("negative infinity returns negative infinity") {
                val r = with(ops) { DoubleDouble.NEGATIVE_INFINITY.roundHalfUp() }
                r.high shouldBe Double.NEGATIVE_INFINITY
                r.low shouldBe 0.0
            }
        }

        context("roundHalfDown") {
            test("positive half-integer 0.5 rounds toward zero to 0.0") {
                val r = with(ops) { DoubleDouble.create(0.5, 0.0).roundHalfDown() }
                r.high shouldBe 0.0
                r.low shouldBe 0.0
            }
            test("positive half-integer 1.5 rounds toward zero to 1.0") {
                val r = with(ops) { DoubleDouble.create(1.5, 0.0).roundHalfDown() }
                r.high shouldBe 1.0
                r.low shouldBe 0.0
            }
            test("positive half-integer 2.5 rounds toward zero to 2.0") {
                val r = with(ops) { DoubleDouble.create(2.5, 0.0).roundHalfDown() }
                r.high shouldBe 2.0
                r.low shouldBe 0.0
            }
            test("negative half-integer -0.5 rounds toward zero to 0.0") {
                val r = with(ops) { DoubleDouble.create(-0.5, 0.0).roundHalfDown() }
                r.high shouldBe 0.0
                r.low shouldBe 0.0
            }
            test("negative half-integer -1.5 rounds toward zero to -1.0") {
                val r = with(ops) { DoubleDouble.create(-1.5, 0.0).roundHalfDown() }
                r.high shouldBe -1.0
                r.low shouldBe 0.0
            }
            test("positive non-integer 1.3 rounds to nearest") {
                val r = with(ops) { DoubleDouble.create(1.3, 0.0).roundHalfDown() }
                r.high shouldBe 1.0
                r.low shouldBe 0.0
            }
            test("positive non-integer 1.7 rounds to nearest") {
                val r = with(ops) { DoubleDouble.create(1.7, 0.0).roundHalfDown() }
                r.high shouldBe 2.0
                r.low shouldBe 0.0
            }
            test("integer hi, positive lo: lo rounds to 0") {
                val r = with(ops) { DoubleDouble.create(3.0, 0.3).roundHalfDown() }
                r.high shouldBe 3.0
                r.low shouldBe 0.0
            }
            test("NaN returns NaN") {
                with(ops) { DoubleDouble.NaN.roundHalfDown() }.high.isNaN() shouldBe true
            }
        }

        context("roundEven") {
            test("positive half-integer 0.5 rounds to even (0.0)") {
                val r = with(ops) { DoubleDouble.create(0.5, 0.0).roundEven() }
                r.high shouldBe 0.0
                r.low shouldBe 0.0
            }
            test("positive half-integer 1.5 rounds to even (2.0)") {
                val r = with(ops) { DoubleDouble.create(1.5, 0.0).roundEven() }
                r.high shouldBe 2.0
                r.low shouldBe 0.0
            }
            test("positive half-integer 2.5 rounds to even (2.0)") {
                val r = with(ops) { DoubleDouble.create(2.5, 0.0).roundEven() }
                r.high shouldBe 2.0
                r.low shouldBe 0.0
            }
            test("positive half-integer 3.5 rounds to even (4.0)") {
                val r = with(ops) { DoubleDouble.create(3.5, 0.0).roundEven() }
                r.high shouldBe 4.0
                r.low shouldBe 0.0
            }
            test("negative half-integer -0.5 rounds to even (0.0)") {
                val r = with(ops) { DoubleDouble.create(-0.5, 0.0).roundEven() }
                r.high shouldBe 0.0
                r.low shouldBe 0.0
            }
            test("negative half-integer -1.5 rounds to even (-2.0)") {
                val r = with(ops) { DoubleDouble.create(-1.5, 0.0).roundEven() }
                r.high shouldBe -2.0
                r.low shouldBe 0.0
            }
            test("negative half-integer -2.5 rounds to even (-2.0)") {
                val r = with(ops) { DoubleDouble.create(-2.5, 0.0).roundEven() }
                r.high shouldBe -2.0
                r.low shouldBe 0.0
            }
            test("positive non-integer 1.3 rounds to nearest") {
                val r = with(ops) { DoubleDouble.create(1.3, 0.0).roundEven() }
                r.high shouldBe 1.0
                r.low shouldBe 0.0
            }
            test("positive non-integer 1.7 rounds to nearest") {
                val r = with(ops) { DoubleDouble.create(1.7, 0.0).roundEven() }
                r.high shouldBe 2.0
                r.low shouldBe 0.0
            }
            test("integer hi, positive lo: lo rounds to 0") {
                val r = with(ops) { DoubleDouble.create(3.0, 0.3).roundEven() }
                r.high shouldBe 3.0
                r.low shouldBe 0.0
            }
            test("NaN returns NaN") {
                with(ops) { DoubleDouble.NaN.roundEven() }.high.isNaN() shouldBe true
            }
            test("positive infinity returns positive infinity") {
                val r = with(ops) { DoubleDouble.POSITIVE_INFINITY.roundEven() }
                r.high shouldBe Double.POSITIVE_INFINITY
                r.low shouldBe 0.0
            }
        }
    }
})
