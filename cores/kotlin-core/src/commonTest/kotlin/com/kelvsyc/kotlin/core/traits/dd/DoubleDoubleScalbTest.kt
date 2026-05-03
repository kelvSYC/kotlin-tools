package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointScalb
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DoubleDoubleScalbTest : FunSpec({

    context("FloatingPointScalb.Companion.doubleDouble") {
        val ops = FloatingPointScalb.doubleDouble

        test("positive value scaled up") {
            val r = with(ops) { DoubleDouble.create(2.0, 0.0).scalb(1) }
            r.high shouldBe 4.0
            r.low shouldBe 0.0
        }
        test("positive value scaled down") {
            val r = with(ops) { DoubleDouble.create(4.0, 0.0).scalb(-1) }
            r.high shouldBe 2.0
            r.low shouldBe 0.0
        }
        test("both components scaled") {
            // (3.0 + 0.5) × 2^2 = 14.0, represented as (12.0, 2.0)
            val r = with(ops) { DoubleDouble.create(3.0, 0.5).scalb(2) }
            r.high shouldBe 12.0
            r.low shouldBe 2.0
        }
        test("NaN high returns NaN high") {
            with(ops) { DoubleDouble.NaN.scalb(3) }.high.isNaN() shouldBe true
        }
        test("positive infinity returns positive infinity") {
            val r = with(ops) { DoubleDouble.POSITIVE_INFINITY.scalb(3) }
            r.high shouldBe Double.POSITIVE_INFINITY
        }
    }
})
