package com.kelvsyc.kotlin.commons.lang.math

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.lang3.math.Fraction

class FractionExtensionsTest : FunSpec({
    val half = Fraction.ONE_HALF
    val third = Fraction.ONE_THIRD
    val quarter = Fraction.ONE_QUARTER

    context("destructuring") {
        test("component1 extracts numerator") {
            val (n, _) = half
            n shouldBe 1
        }
        test("component2 extracts denominator") {
            val (_, d) = half
            d shouldBe 2
        }
    }

    context("unary operators") {
        test("unaryPlus returns the same fraction") {
            +half shouldBe half
        }
        test("unaryMinus negates the fraction") {
            -half shouldBe Fraction.getFraction(-1, 2)
        }
    }

    context("arithmetic operators with Fraction") {
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

    context("arithmetic operators with Int") {
        test("plus: 1/2 + 1 = 3/2") {
            (half + 1) shouldBe Fraction.getFraction(3, 2)
        }
        test("minus: 1/2 - 1 = -1/2") {
            (half - 1) shouldBe Fraction.getFraction(-1, 2)
        }
        test("times: 1/3 * 2 = 2/3") {
            (third * 2) shouldBe Fraction.getFraction(2, 3)
        }
        test("div: 1/2 / 2 = 1/4") {
            (half / 2) shouldBe quarter
        }
    }
})
