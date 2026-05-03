package com.kelvsyc.kotlin.commons.numbers.complex

import com.kelvsyc.kotlin.core.traits.complex.ComplexArithmetic
import com.kelvsyc.kotlin.core.traits.complex.ComplexModulus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.complex.Complex

class CommonsComplexArithmeticTest : FunSpec({
    val ops = ComplexArithmetic.commonsComplex

    fun c(re: Double, im: Double): Complex = ops.of(re, im)

    context("structural accessors") {
        test("real and imaginary") {
            val z = c(3.0, 4.0)
            with(ops) {
                z.real() shouldBe 3.0
                z.imaginary() shouldBe 4.0
            }
        }
    }

    context("identities") {
        test("zero") {
            with(ops) {
                zero.real() shouldBe 0.0
                zero.imaginary() shouldBe 0.0
            }
        }
        test("one") {
            with(ops) {
                one.real() shouldBe 1.0
                one.imaginary() shouldBe 0.0
            }
        }
    }

    context("add and subtract") {
        test("add") {
            with(ops) {
                val z = c(1.0, 2.0).add(c(3.0, 4.0))
                z.real() shouldBe 4.0
                z.imaginary() shouldBe 6.0
            }
        }
        test("subtract") {
            with(ops) {
                val z = c(5.0, 7.0).subtract(c(2.0, 3.0))
                z.real() shouldBe 3.0
                z.imaginary() shouldBe 4.0
            }
        }
    }

    context("negate and conjugate") {
        test("negate") {
            with(ops) {
                val z = c(3.0, -4.0).negate()
                z.real() shouldBe -3.0
                z.imaginary() shouldBe 4.0
            }
        }
        test("conjugate") {
            with(ops) {
                val z = c(3.0, 4.0).conjugate()
                z.real() shouldBe 3.0
                z.imaginary() shouldBe -4.0
            }
        }
    }

    context("multiply and divide") {
        test("multiply") {
            // (1+2i)(3+4i) = (3-8) + (4+6)i = -5 + 10i
            with(ops) {
                val z = c(1.0, 2.0).multiply(c(3.0, 4.0))
                z.real() shouldBe -5.0
                z.imaginary() shouldBe 10.0
            }
        }
        test("divide") {
            // (1+2i)/(1+i) = (1+2i)(1-i)/2 = (1+2+2-1)i/2 = (3+i)/2 = 1.5 + 0.5i
            with(ops) {
                val z = c(1.0, 2.0).divide(c(1.0, 1.0))
                z.real() shouldBe (1.5 plusOrMinus 1e-15)
                z.imaginary() shouldBe (0.5 plusOrMinus 1e-15)
            }
        }
        test("multiply by one is identity") {
            with(ops) {
                val z = c(3.0, 4.0).multiply(one)
                z.real() shouldBe 3.0
                z.imaginary() shouldBe 4.0
            }
        }
    }

    context("ComplexModulus") {
        val mod = ComplexModulus.commonsComplex

        test("modulus of 3+4i is 5") {
            with(mod) { c(3.0, 4.0).modulus() shouldBe 5.0 }
        }
        test("squaredModulus of 3+4i is 25") {
            with(mod) { c(3.0, 4.0).squaredModulus() shouldBe 25.0 }
        }
        test("modulus of zero is 0") {
            with(mod) { c(0.0, 0.0).modulus() shouldBe 0.0 }
        }
    }
})
