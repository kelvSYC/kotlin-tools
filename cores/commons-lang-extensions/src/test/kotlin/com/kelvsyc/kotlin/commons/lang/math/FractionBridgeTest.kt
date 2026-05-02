package com.kelvsyc.kotlin.commons.lang.math

import com.kelvsyc.kotlin.core.traits.rational.RationalArithmetic
import com.kelvsyc.kotlin.core.traits.rational.int
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.lang3.math.Fraction

class FractionBridgeTest : FunSpec({
    val arithmetic = RationalArithmetic.int

    context("FractionRationalConverter (forward)") {
        test("numerator and denominator are preserved") {
            val r = FractionRationalConverter(Fraction.getFraction(3, 4))
            r.numerator shouldBe 3
            r.denominator shouldBe 4
        }
        test("negative fraction preserves sign on numerator") {
            val r = FractionRationalConverter(Fraction.getFraction(-1, 3))
            r.numerator shouldBe -1
            r.denominator shouldBe 3
        }
        test("zero converts correctly") {
            val r = FractionRationalConverter(Fraction.ZERO)
            with(arithmetic) { r.isZero() shouldBe true }
        }
        test("Fraction normalises 2/4 to 1/2 before conversion") {
            val r = FractionRationalConverter(Fraction.getFraction(2, 4))
            r.numerator shouldBe 1
            r.denominator shouldBe 2
        }
    }

    context("FractionRationalConverter.reverse") {
        test("numerator and denominator are preserved") {
            val f = FractionRationalConverter.reverse(arithmetic.run { of(3, 4) })
            f.numerator shouldBe 3
            f.denominator shouldBe 4
        }
        test("negative rational preserves sign on numerator") {
            val f = FractionRationalConverter.reverse(arithmetic.run { of(-1, 3) })
            f.numerator shouldBe -1
            f.denominator shouldBe 3
        }
        test("zero converts correctly") {
            FractionRationalConverter.reverse(arithmetic.zero) shouldBe Fraction.ZERO
        }
    }

    context("extension functions delegate to converter") {
        test("Fraction.toRational() matches forward conversion") {
            val fraction = Fraction.getFraction(5, 7)
            fraction.toRational() shouldBe FractionRationalConverter(fraction)
        }
        test("Rational<Int>.toFraction() matches reverse conversion") {
            val rational = arithmetic.run { of(5, 7) }
            rational.toFraction() shouldBe FractionRationalConverter.reverse(rational)
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
