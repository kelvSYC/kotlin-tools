package com.kelvsyc.kotlin.commons.numbers.fraction

import com.kelvsyc.kotlin.core.Rational
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import org.apache.commons.numbers.fraction.BigFraction

class BigFractionBridgeTest : FunSpec() {
    init {
        // BigFraction.of() does not normalize to positive denominator, so structural equality
        // between BigFraction and Rational<BigInteger> fields does not hold. Value equality
        // (cross-multiplication) is used instead.

        test("BigFraction.toRational preserves value") {
            checkAll(arbitraryBigFraction) { f ->
                val r = f.toRational()
                r.numerator.multiply(f.denominator) shouldBe f.numerator.multiply(r.denominator)
            }
        }
        test("Rational<BigInteger>.toBigFraction preserves value") {
            checkAll(arbitraryBigFraction) { f ->
                val r = f.toRational()
                val back = r.toBigFraction()
                back.numerator.multiply(r.denominator) shouldBe r.numerator.multiply(back.denominator)
            }
        }
        test("round-trip BigFraction → Rational → BigFraction preserves value") {
            checkAll(arbitraryBigFraction) { f ->
                val back = f.toRational().toBigFraction()
                back.numerator.multiply(f.denominator) shouldBe f.numerator.multiply(back.denominator)
            }
        }
        test("bigFractionConverter forward matches toRational") {
            checkAll(arbitraryBigFraction) { f ->
                Rational.bigFractionConverter(f) shouldBe f.toRational()
            }
        }
        test("bigFractionConverter reverse matches toBigFraction") {
            val converter = Rational.bigFractionConverter.reverse
            val f = BigFraction.of(3, 4)
            val back = converter(f.toRational())
            back.numerator.multiply(f.denominator) shouldBe f.numerator.multiply(back.denominator)
        }
    }
}
