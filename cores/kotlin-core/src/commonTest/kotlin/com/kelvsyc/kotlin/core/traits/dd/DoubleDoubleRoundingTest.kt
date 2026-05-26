package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointRounding
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DoubleDoubleRoundingTest : FunSpec({

    context("FloatingPointRounding.Companion.doubleDouble") {
        val ops = FloatingPointRounding.doubleDouble

        context("trunc") {
            test("positive non-integer rounds toward zero") {
                val r = with(ops) { DoubleDouble.create(1.7, 0.0).trunc() }
                r.high shouldBe 1.0
                r.low shouldBe 0.0
            }
            test("negative non-integer rounds toward zero") {
                val r = with(ops) { DoubleDouble.create(-1.7, 0.0).trunc() }
                r.high shouldBe -1.0
                r.low shouldBe 0.0
            }
            test("integer hi, positive lo: lo has no fractional effect") {
                // trunc(3.0 + 0.3) = trunc(3.3) = 3.0
                val r = with(ops) { DoubleDouble.create(3.0, 0.3).trunc() }
                r.high shouldBe 3.0
                r.low shouldBe 0.0
            }
            test("integer hi, negative lo: lo carries borrow") {
                // trunc(3.0 + (-0.3)) = trunc(2.7) = 2.0
                val r = with(ops) { DoubleDouble.create(3.0, -0.3).trunc() }
                r.high shouldBe 2.0
                r.low shouldBe 0.0
            }
            test("NaN returns NaN") {
                with(ops) { DoubleDouble.NaN.trunc() }.high.isNaN() shouldBe true
            }
        }

        context("roundUp") {
            test("positive non-integer rounds away from zero") {
                val r = with(ops) { DoubleDouble.create(1.3, 0.0).roundUp() }
                r.high shouldBe 2.0
                r.low shouldBe 0.0
            }
            test("negative non-integer rounds away from zero") {
                val r = with(ops) { DoubleDouble.create(-1.3, 0.0).roundUp() }
                r.high shouldBe -2.0
                r.low shouldBe 0.0
            }
            test("integer is unchanged") {
                val r = with(ops) { DoubleDouble.create(3.0, 0.0).roundUp() }
                r.high shouldBe 3.0
                r.low shouldBe 0.0
            }
            test("NaN returns NaN") {
                with(ops) { DoubleDouble.NaN.roundUp() }.high.isNaN() shouldBe true
            }
        }

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
