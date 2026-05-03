package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.core.DD

class DdBridgeTest : FunSpec() {
    init {
        context("DD.toDoubleDouble") {
            test("hi and lo are transferred") {
                val d = DD.ofSum(1.0, 0.1)
                val dd = d.toDoubleDouble()
                dd.high shouldBe d.hi()
                dd.low shouldBe d.lo()
            }
            test("single-value DD transfers correctly") {
                val d = DD.of(3.14)
                val dd = d.toDoubleDouble()
                dd.high shouldBe d.hi()
                dd.low shouldBe d.lo()
            }
            test("NaN maps to DoubleDouble.NaN") {
                val d = DD.of(Double.NaN)
                d.toDoubleDouble().isNaN() shouldBe true
            }
        }

        context("DoubleDouble.toDD") {
            test("round-trip DD → DoubleDouble → DD preserves hi and lo") {
                val d = DD.ofSum(1.0, 0.1)
                val back = d.toDoubleDouble().toDD()
                back.hi() shouldBe d.hi()
                back.lo() shouldBe d.lo()
            }
            test("single-value round-trip preserves hi and lo") {
                val d = DD.of(3.14)
                val back = d.toDoubleDouble().toDD()
                back.hi() shouldBe d.hi()
                back.lo() shouldBe d.lo()
            }
            test("NaN maps to NaN DD") {
                DoubleDouble.NaN.toDD().hi().isNaN() shouldBe true
            }
        }

        context("ddConverter") {
            test("forward matches toDoubleDouble") {
                val d = DD.of(2.0)
                DoubleDouble.ddConverter(d) shouldBe d.toDoubleDouble()
            }
            test("reverse matches toDD") {
                val dd = DD.of(2.0).toDoubleDouble()
                val back = DoubleDouble.ddConverter.reverse(dd)
                back.hi() shouldBe dd.toDD().hi()
                back.lo() shouldBe dd.toDD().lo()
            }
        }
    }
}
