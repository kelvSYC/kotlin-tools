package com.kelvsyc.kotlin.commons.numbers.complex

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.complex.Complex as CommonsComplex

class ComplexValueEqualityTest : FunSpec() {
    private val nan = CommonsComplex.ofCartesian(Double.NaN, Double.NaN)
    private val posZero = CommonsComplex.ofCartesian(0.0, 0.0)
    private val negZero = CommonsComplex.ofCartesian(-0.0, -0.0)
    private val one = CommonsComplex.ofCartesian(1.0, 2.0)

    init {
        context("numericalEquality") {
            val eq = commonsComplexNumericalEquality

            test("equal values are equal") {
                with(eq) { one.isEqualTo(CommonsComplex.ofCartesian(1.0, 2.0)) shouldBe true }
            }
            test("NaN is not equal to itself") {
                with(eq) { nan.isEqualTo(nan) shouldBe false }
            }
            test("+0 equals -0") {
                with(eq) { posZero.isEqualTo(negZero) shouldBe true }
            }
            test("distinct values are not equal") {
                with(eq) { one.isEqualTo(posZero) shouldBe false }
            }
        }

        context("equivalenceEquality") {
            val eq = commonsComplexEquivalenceEquality

            test("equal values are equal") {
                with(eq) { one.isEqualTo(CommonsComplex.ofCartesian(1.0, 2.0)) shouldBe true }
            }
            test("NaN equals NaN") {
                with(eq) { nan.isEqualTo(nan) shouldBe true }
            }
            test("+0 does not equal -0") {
                with(eq) { posZero.isEqualTo(negZero) shouldBe false }
            }
            test("distinct values are not equal") {
                with(eq) { one.isEqualTo(posZero) shouldBe false }
            }
        }
    }
}
