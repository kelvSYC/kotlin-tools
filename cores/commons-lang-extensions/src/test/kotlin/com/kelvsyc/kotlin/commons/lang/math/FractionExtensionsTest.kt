package com.kelvsyc.kotlin.commons.lang.math

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.lang3.math.Fraction

class FractionExtensionsTest : FunSpec({
    val half = Fraction.ONE_HALF
    val third = Fraction.ONE_THIRD
    val quarter = Fraction.ONE_QUARTER

    context("unary operators") {
        test("unaryPlus returns the same fraction") {
            +half shouldBe half
        }
        test("unaryMinus negates the fraction") {
            -half shouldBe Fraction.getFraction(-1, 2)
        }
    }

    context("arithmetic operators") {
        test("plus: 1/2 + 1/3 = 5/6") {
            (half + third) shouldBe Fraction.getFraction(5, 6)
        }
        test("minus: 1/2 - 1/3 = 1/6") {
            (half - third) shouldBe Fraction.getFraction(1, 6)
        }
        test("times: 1/2 * 1/3 = 1/6") {
            (half * third) shouldBe Fraction.getFraction(1, 6)
        }
        test("div: 1/2 / 1/4 = 2") {
            (half / quarter) shouldBe Fraction.getFraction(2, 1)
        }
    }
})
