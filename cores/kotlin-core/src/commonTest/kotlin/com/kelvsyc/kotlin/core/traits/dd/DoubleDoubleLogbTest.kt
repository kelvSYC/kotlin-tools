package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointLogb
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DoubleDoubleLogbTest : FunSpec({

    context("FloatingPointLogb.Companion.doubleDouble") {
        val ops = FloatingPointLogb.doubleDouble

        context("logb") {
            test("NaN returns NaN") {
                val r = with(ops) { DoubleDouble.NaN.logb() }
                r.high.isNaN() shouldBe true
            }

            test("positive infinity returns positive infinity") {
                val r = with(ops) { DoubleDouble.POSITIVE_INFINITY.logb() }
                r.high shouldBe Double.POSITIVE_INFINITY
                r.low shouldBe 0.0
            }

            test("negative infinity returns positive infinity") {
                val r = with(ops) { DoubleDouble.NEGATIVE_INFINITY.logb() }
                r.high shouldBe Double.POSITIVE_INFINITY
                r.low shouldBe 0.0
            }

            test("zero returns negative infinity") {
                val r = with(ops) { DoubleDouble(0.0, 0.0).logb() }
                r.high shouldBe Double.NEGATIVE_INFINITY
                r.low shouldBe 0.0
            }

            test("4.0 returns 2.0") {
                val r = with(ops) { DoubleDouble(4.0, 0.0).logb() }
                r.high shouldBe 2.0
                r.low shouldBe 0.0
            }

            test("1.0 returns 0.0") {
                val r = with(ops) { DoubleDouble(1.0, 0.0).logb() }
                r.high shouldBe 0.0
                r.low shouldBe 0.0
            }

            test("4.0 with non-zero lo still returns 2.0") {
                val r = with(ops) { DoubleDouble(4.0, 1e-15).logb() }
                r.high shouldBe 2.0
                r.low shouldBe 0.0
            }

            test("lo component in result is always 0.0") {
                val r = with(ops) { DoubleDouble(8.0, 0.5).logb() }
                r.low shouldBe 0.0
            }
        }

        context("ilogb") {
            test("NaN returns Int.MAX_VALUE") {
                with(ops) { DoubleDouble.NaN.ilogb() } shouldBe Int.MAX_VALUE
            }

            test("positive infinity returns Int.MAX_VALUE") {
                with(ops) { DoubleDouble.POSITIVE_INFINITY.ilogb() } shouldBe Int.MAX_VALUE
            }

            test("negative infinity returns Int.MAX_VALUE") {
                with(ops) { DoubleDouble.NEGATIVE_INFINITY.ilogb() } shouldBe Int.MAX_VALUE
            }

            test("zero returns Int.MIN_VALUE") {
                with(ops) { DoubleDouble(0.0, 0.0).ilogb() } shouldBe Int.MIN_VALUE
            }

            test("4.0 returns 2") {
                with(ops) { DoubleDouble(4.0, 0.0).ilogb() } shouldBe 2
            }

            test("1.0 returns 0") {
                with(ops) { DoubleDouble(1.0, 0.0).ilogb() } shouldBe 0
            }

            test("4.0 with non-zero lo still returns 2") {
                with(ops) { DoubleDouble(4.0, 1e-15).ilogb() } shouldBe 2
            }
        }
    }
})
