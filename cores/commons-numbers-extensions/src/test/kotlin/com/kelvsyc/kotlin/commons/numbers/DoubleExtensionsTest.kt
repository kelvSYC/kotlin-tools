package com.kelvsyc.kotlin.commons.numbers

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.complex.Complex

class DoubleExtensionsTest : FunSpec() {
    init {
        test("i creates imaginary Complex") {
            5.0.i shouldBe Complex.ofCartesian(0.0, 5.0)
        }
        test("toDD") {
            3.0.toDD().hi() shouldBe 3.0
        }
        test("toFraction") {
            val f = 0.5.toFraction()
            f.numerator shouldBe 1
            f.denominator shouldBe 2
        }
        test("toBigFraction") {
            val bf = 0.5.toBigFraction()
            bf.numeratorAsInt shouldBe 1
            bf.denominatorAsInt shouldBe 2
        }
        test("minus Complex") {
            val complex = Complex.ofCartesian(1.0, 2.0)
            (3.0 - complex) shouldBe Complex.ofCartesian(2.0, -2.0)
        }
    }
}
