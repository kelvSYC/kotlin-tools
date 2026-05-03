package com.kelvsyc.kotlin.commons.numbers.fraction

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bigInt
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll
import java.math.BigInteger

class BigFractionExtensionsTest : FunSpec() {
    init {
        test("component1 extracts numerator") {
            checkAll(arbitraryBigFraction) { it.component1() shouldBe it.numerator }
        }
        test("component2 extracts denominator") {
            checkAll(arbitraryBigFraction) { it.component2() shouldBe it.denominator }
        }
        test("unaryPlus is identity") {
            checkAll(arbitraryBigFraction) { +it shouldBe it }
        }
        test("unaryMinus negates") {
            checkAll(arbitraryBigFraction) { (-it) shouldBe it.negate() }
        }
        test("plus int") {
            checkAll(arbitraryBigFraction, Arb.int()) { f, n -> (f + n) shouldBe f.add(n) }
        }
        test("plus long") {
            checkAll(arbitraryBigFraction, Arb.long()) { f, n -> (f + n) shouldBe f.add(n) }
        }
        test("plus BigInteger") {
            checkAll(arbitraryBigFraction, Arb.bigInt(16)) { f, n -> (f + n) shouldBe f.add(n) }
        }
        test("plus BigFraction") {
            checkAll(arbitraryBigFraction, arbitraryBigFraction) { a, b -> (a + b) shouldBe a.add(b) }
        }
        test("minus int") {
            checkAll(arbitraryBigFraction, Arb.int()) { f, n -> (f - n) shouldBe f.subtract(n) }
        }
        test("minus long") {
            checkAll(arbitraryBigFraction, Arb.long()) { f, n -> (f - n) shouldBe f.subtract(n) }
        }
        test("minus BigInteger") {
            checkAll(arbitraryBigFraction, Arb.bigInt(16)) { f, n -> (f - n) shouldBe f.subtract(n) }
        }
        test("minus BigFraction") {
            checkAll(arbitraryBigFraction, arbitraryBigFraction) { a, b -> (a - b) shouldBe a.subtract(b) }
        }
        test("times int") {
            checkAll(arbitraryBigFraction, Arb.int()) { f, n -> (f * n) shouldBe f.multiply(n) }
        }
        test("times long") {
            checkAll(arbitraryBigFraction, Arb.long()) { f, n -> (f * n) shouldBe f.multiply(n) }
        }
        test("times BigInteger") {
            checkAll(arbitraryBigFraction, Arb.bigInt(16)) { f, n -> (f * n) shouldBe f.multiply(n) }
        }
        test("times BigFraction") {
            checkAll(arbitraryBigFraction, arbitraryBigFraction) { a, b -> (a * b) shouldBe a.multiply(b) }
        }
        test("div int") {
            checkAll(arbitraryBigFraction, Arb.int().filter { it != 0 }) { f, n -> (f / n) shouldBe f.divide(n) }
        }
        test("div long") {
            checkAll(arbitraryBigFraction, Arb.long().filter { it != 0L }) { f, n -> (f / n) shouldBe f.divide(n) }
        }
        test("div BigInteger") {
            checkAll(arbitraryBigFraction, Arb.bigInt(16).filter { it != BigInteger.ZERO }) { f, n ->
                (f / n) shouldBe f.divide(n)
            }
        }
        test("div BigFraction") {
            checkAll(arbitraryBigFraction, arbitraryBigFraction.filter { it.numerator != BigInteger.ZERO }) { a, b ->
                (a / b) shouldBe a.divide(b)
            }
        }
    }
}
