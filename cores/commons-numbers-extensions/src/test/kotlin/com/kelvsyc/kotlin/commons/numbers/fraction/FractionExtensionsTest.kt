package com.kelvsyc.kotlin.commons.numbers.fraction

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import org.apache.commons.numbers.fraction.Fraction

class FractionExtensionsTest : FunSpec() {
    init {
        test("component1 extracts numerator") {
            checkAll(arbitraryFraction) { it.component1() shouldBe it.numerator }
        }
        test("component2 extracts denominator") {
            checkAll(arbitraryFraction) { it.component2() shouldBe it.denominator }
        }
        test("unaryPlus is identity") {
            checkAll(arbitraryFraction) { +it shouldBe it }
        }
        test("unaryMinus negates") {
            checkAll(arbitraryFraction) { (-it) shouldBe it.negate() }
        }
        test("plus int") {
            checkAll(arbitraryFraction, Arb.int(-1000, 1000)) { f, n -> (f + n) shouldBe f.add(n) }
        }
        test("plus Fraction") {
            checkAll(arbitraryFraction, arbitraryFraction) { a, b -> (a + b) shouldBe a.add(b) }
        }
        test("minus int") {
            checkAll(arbitraryFraction, Arb.int(-1000, 1000)) { f, n -> (f - n) shouldBe f.subtract(n) }
        }
        test("minus Fraction") {
            checkAll(arbitraryFraction, arbitraryFraction) { a, b -> (a - b) shouldBe a.subtract(b) }
        }
        test("times int") {
            checkAll(arbitraryFraction, Arb.int(-1000, 1000)) { f, n -> (f * n) shouldBe f.multiply(n) }
        }
        test("times Fraction") {
            checkAll(arbitraryFraction, arbitraryFraction) { a, b -> (a * b) shouldBe a.multiply(b) }
        }
        test("div int") {
            checkAll(arbitraryFraction, Arb.int(-1000, 1000).filter { it != 0 }) { f, n -> (f / n) shouldBe f.divide(n) }
        }
        test("div Fraction") {
            checkAll(arbitraryFraction, arbitraryFraction.filter { it != Fraction.ZERO }) { a, b ->
                (a / b) shouldBe a.divide(b)
            }
        }
        test("toBigFraction") {
            val f = Fraction.of(1, 2)
            val bf = f.toBigFraction()
            bf.numeratorAsInt shouldBe 1
            bf.denominatorAsInt shouldBe 2
        }
    }
}
