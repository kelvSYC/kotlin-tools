package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointScalb
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.core.DD

class DdFloatingPointScalbTest : FunSpec() {
    init {
        context("FloatingPointScalb.Companion.dd") {
            val ops = FloatingPointScalb.dd

            test("positive value scaled up") {
                val r = with(ops) { DD.of(2.0).scalb(1) }
                r.hi() shouldBe 4.0
                r.lo() shouldBe 0.0
            }
            test("positive value scaled down") {
                val r = with(ops) { DD.of(4.0).scalb(-1) }
                r.hi() shouldBe 2.0
                r.lo() shouldBe 0.0
            }
            test("negative value scaled up") {
                val r = with(ops) { DD.of(-2.0).scalb(1) }
                r.hi() shouldBe -4.0
                r.lo() shouldBe 0.0
            }
            test("both components scaled independently") {
                // DD.ofSum(3.0, 0.5) is a valid DD with hi=3.5 or hi=3.0, lo=0.5
                // scalb(2) multiplies both by 4
                val dd = DD.ofSum(3.0, 0.5)
                val r = with(ops) { dd.scalb(2) }
                r.hi() shouldBe dd.hi() * 4.0
                r.lo() shouldBe dd.lo() * 4.0
            }
            test("NaN returns NaN") {
                with(ops) { DD.of(Double.NaN).scalb(3) }.hi().isNaN() shouldBe true
            }
            test("large positive exponent") {
                val r = with(ops) { DD.of(1.0).scalb(10) }
                r.hi() shouldBe 1024.0
                r.lo() shouldBe 0.0
            }
        }
    }
}
