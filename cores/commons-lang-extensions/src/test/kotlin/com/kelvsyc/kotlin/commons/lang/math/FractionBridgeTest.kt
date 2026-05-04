package com.kelvsyc.kotlin.commons.lang.math

import com.kelvsyc.kotlin.core.Rational
import com.kelvsyc.kotlin.core.traits.rational.RationalArithmetic
import com.kelvsyc.kotlin.core.traits.rational.int
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.lang3.math.Fraction

class FractionBridgeTest : FunSpec({
    val arithmetic = RationalArithmetic.int

    context("Fraction.toRational()") {
        test("numerator and denominator are preserved") {
            val r = Fraction.getFraction(3, 4).toRational()
            r.numerator shouldBe 3
            r.denominator shouldBe 4
        }
        test("negative fraction preserves sign on numerator") {
            val r = Fraction.getFraction(-1, 3).toRational()
            r.numerator shouldBe -1
            r.denominator shouldBe 3
        }
        test("zero converts correctly") {
            val r = Fraction.ZERO.toRational()
            with(arithmetic) { r.isZero() shouldBe true }
        }
        test("Fraction normalises 2/4 to 1/2 before conversion") {
            val r = Fraction.getFraction(2, 4).toRational()
            r.numerator shouldBe 1
            r.denominator shouldBe 2
        }
    }

    context("Rational<Int>.toFraction()") {
        test("numerator and denominator are preserved") {
            val f = arithmetic.run { of(3, 4) }.toFraction()
            f.numerator shouldBe 3
            f.denominator shouldBe 4
        }
        test("negative rational preserves sign on numerator") {
            val f = arithmetic.run { of(-1, 3) }.toFraction()
            f.numerator shouldBe -1
            f.denominator shouldBe 3
        }
        test("zero converts correctly") {
            arithmetic.zero.toFraction() shouldBe Fraction.ZERO
        }
    }

    context("Rational.fractionConverter") {
        test("forward matches toRational()") {
            val fraction = Fraction.getFraction(5, 7)
            Rational.fractionConverter(fraction) shouldBe fraction.toRational()
        }
        test("reverse matches toFraction()") {
            val rational = arithmetic.run { of(5, 7) }
            Rational.fractionConverter.reverse(rational) shouldBe rational.toFraction()
        }
    }

    context("round-trips") {
        test("Fraction -> Rational -> Fraction") {
            listOf(
                Fraction.ONE_HALF,
                Fraction.ONE_THIRD,
                Fraction.getFraction(-5, 7),
                Fraction.ZERO,
                Fraction.ONE,
            ).forEach { original ->
                original.toRational().toFraction() shouldBe original
            }
        }
        test("Rational -> Fraction -> Rational") {
            listOf(
                arithmetic.run { of(1, 2) },
                arithmetic.run { of(-5, 7) },
                arithmetic.zero,
                arithmetic.one,
            ).forEach { original ->
                original.toFraction().toRational() shouldBe original
            }
        }
    }
})
