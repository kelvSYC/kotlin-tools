package com.kelvsyc.kotlin.commons.numbers.complex

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import org.apache.commons.numbers.complex.Complex as CommonsComplex

class ComplexBridgeTest : FunSpec() {
    init {
        test("CommonsComplex.toKotlinComplex preserves real and imaginary") {
            checkAll(arbitraryComplex) { c ->
                val k = c.toKotlinComplex()
                k.real shouldBe c.real
                k.imaginary shouldBe c.imaginary
            }
        }
        test("Complex<Double>.toCommonsComplex preserves real and imaginary") {
            checkAll(arbitraryComplex) { c ->
                val k = c.toKotlinComplex()
                val back = k.toCommonsComplex()
                back.real shouldBe k.real
                back.imaginary shouldBe k.imaginary
            }
        }
        test("round-trip CommonsComplex → KotlinComplex → CommonsComplex") {
            checkAll(arbitraryComplex) { c ->
                c.toKotlinComplex().toCommonsComplex() shouldBe c
            }
        }
        test("commonsComplexConverter forward matches toKotlinComplex") {
            checkAll(arbitraryComplex) { c ->
                commonsComplexConverter(c) shouldBe c.toKotlinComplex()
            }
        }
        test("commonsComplexConverter reverse matches toCommonsComplex") {
            val converter = commonsComplexConverter.reverse
            val c = CommonsComplex.ofCartesian(1.0, 2.0)
            converter(c.toKotlinComplex()) shouldBe c
        }
    }
}
