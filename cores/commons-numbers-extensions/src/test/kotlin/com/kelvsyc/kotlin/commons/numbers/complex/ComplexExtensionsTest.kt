package com.kelvsyc.kotlin.commons.numbers.complex

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import org.apache.commons.numbers.complex.Complex

class ComplexExtensionsTest : FunSpec() {
    init {
        test("component1 extracts real") {
            checkAll(arbitraryComplex) { it.component1() shouldBe it.real }
        }
        test("component2 extracts imaginary") {
            checkAll(arbitraryComplex) { it.component2() shouldBe it.imaginary }
        }
        test("unaryPlus is identity") {
            checkAll(arbitraryComplex) { +it shouldBe it }
        }
        test("unaryMinus negates") {
            checkAll(arbitraryComplex) { (-it) shouldBe it.negate() }
        }
        test("plus double") {
            checkAll(arbitraryComplex) { c -> (c + 1.0) shouldBe c.add(1.0) }
        }
        test("plus Complex") {
            checkAll(arbitraryComplex, arbitraryComplex) { a, b -> (a + b) shouldBe a.add(b) }
        }
        test("minus double") {
            checkAll(arbitraryComplex) { c -> (c - 1.0) shouldBe c.subtract(1.0) }
        }
        test("minus Complex") {
            checkAll(arbitraryComplex, arbitraryComplex) { a, b -> (a - b) shouldBe a.subtract(b) }
        }
        test("times double") {
            checkAll(arbitraryComplex) { c -> (c * 2.0) shouldBe c.multiply(2.0) }
        }
        test("times Complex") {
            checkAll(arbitraryComplex, arbitraryComplex) { a, b -> (a * b) shouldBe a.multiply(b) }
        }
        test("div double") {
            checkAll(arbitraryComplex) { c -> (c / 2.0) shouldBe c.divide(2.0) }
        }
        test("div Complex") {
            checkAll(arbitraryComplex, arbitraryComplex) { a, b -> (a / b) shouldBe a.divide(b) }
        }
    }
}
