package com.kelvsyc.kotlin.commons.numbers.fraction

import com.kelvsyc.kotlin.core.Rational
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import org.apache.commons.numbers.fraction.Fraction

class FractionBridgeTest : FunSpec() {
    init {
        // Fraction.of() does not normalize to positive denominator, so structural equality
        // between Fraction and Rational<Int> fields does not hold. Value equality (cross-
        // multiplication) is used instead.

        test("Fraction.toRational preserves value") {
            checkAll(arbitraryFraction) { f ->
                val r = f.toRational()
                r.numerator.toLong() * f.denominator shouldBe f.numerator.toLong() * r.denominator
            }
        }
        test("Rational<Int>.toFraction preserves value") {
            checkAll(arbitraryFraction) { f ->
                val r = f.toRational()
                val back = r.toFraction()
                back.numerator.toLong() * r.denominator shouldBe r.numerator.toLong() * back.denominator
            }
        }
        test("round-trip Fraction → Rational → Fraction preserves value") {
            checkAll(arbitraryFraction) { f ->
                val back = f.toRational().toFraction()
                back.numerator.toLong() * f.denominator shouldBe f.numerator.toLong() * back.denominator
            }
        }
        test("fractionConverter forward matches toRational") {
            checkAll(arbitraryFraction) { f ->
                Rational.fractionConverter(f) shouldBe f.toRational()
            }
        }
        test("fractionConverter reverse matches toFraction") {
            val converter = Rational.fractionConverter.reverse
            val f = Fraction.of(3, 4)
            val back = converter(f.toRational())
            back.numerator.toLong() * f.denominator shouldBe f.numerator.toLong() * back.denominator
        }
    }
}
