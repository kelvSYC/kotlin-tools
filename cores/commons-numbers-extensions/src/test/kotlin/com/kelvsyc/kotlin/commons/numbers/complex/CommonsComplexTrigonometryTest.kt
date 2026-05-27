package com.kelvsyc.kotlin.commons.numbers.complex

import com.kelvsyc.kotlin.core.traits.complex.ComplexTrigonometry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.complex.Complex as CommonsComplex

class CommonsComplexTrigonometryTest : FunSpec({
    val ops = ComplexTrigonometry.commonsComplex

    fun c(re: Double, im: Double): CommonsComplex = CommonsComplex.ofCartesian(re, im)

    context("forward trig at origin") {
        test("sin(0+0i) = 0+0i") {
            with(ops) {
                val z = c(0.0, 0.0).sin()
                z.real() shouldBe (0.0 plusOrMinus 1e-15)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-15)
            }
        }
        test("cos(0+0i) = 1+0i") {
            with(ops) {
                val z = c(0.0, 0.0).cos()
                z.real() shouldBe (1.0 plusOrMinus 1e-15)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-15)
            }
        }
        test("tan(0+0i) = 0+0i") {
            with(ops) {
                val z = c(0.0, 0.0).tan()
                z.real() shouldBe (0.0 plusOrMinus 1e-15)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-15)
            }
        }
        test("sinh(0+0i) = 0+0i") {
            with(ops) {
                val z = c(0.0, 0.0).sinh()
                z.real() shouldBe (0.0 plusOrMinus 1e-15)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-15)
            }
        }
        test("cosh(0+0i) = 1+0i") {
            with(ops) {
                val z = c(0.0, 0.0).cosh()
                z.real() shouldBe (1.0 plusOrMinus 1e-15)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-15)
            }
        }
        test("tanh(0+0i) = 0+0i") {
            with(ops) {
                val z = c(0.0, 0.0).tanh()
                z.real() shouldBe (0.0 plusOrMinus 1e-15)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-15)
            }
        }
    }

    context("Pythagorean identity sin² + cos² = 1") {
        test("sin² + cos² = 1 at z = 1+i") {
            with(ops) {
                val z = c(1.0, 1.0)
                val s = z.sin(); val co = z.cos()
                val sumSq = s.real() * s.real() - s.imaginary() * s.imaginary() +
                    co.real() * co.real() - co.imaginary() * co.imaginary()
                sumSq shouldBe (1.0 plusOrMinus 1e-14)
            }
        }
    }

    context("inverse trig round-trips") {
        test("asin(sin(z)) ≈ z for z = 0.5+0.3i") {
            with(ops) {
                val z = c(0.5, 0.3)
                val roundTrip = z.sin().asin()
                roundTrip.real() shouldBe (z.real() plusOrMinus 1e-13)
                roundTrip.imaginary() shouldBe (z.imaginary() plusOrMinus 1e-13)
            }
        }
        test("atan(tan(z)) ≈ z for z = 0.3+0.4i") {
            with(ops) {
                val z = c(0.3, 0.4)
                val roundTrip = z.tan().atan()
                roundTrip.real() shouldBe (z.real() plusOrMinus 1e-13)
                roundTrip.imaginary() shouldBe (z.imaginary() plusOrMinus 1e-13)
            }
        }
        test("asinh(sinh(z)) ≈ z for z = 0.5+0.3i") {
            with(ops) {
                val z = c(0.5, 0.3)
                val roundTrip = z.sinh().asinh()
                roundTrip.real() shouldBe (z.real() plusOrMinus 1e-13)
                roundTrip.imaginary() shouldBe (z.imaginary() plusOrMinus 1e-13)
            }
        }
        test("atanh(tanh(z)) ≈ z for z = 0.3+0.4i") {
            with(ops) {
                val z = c(0.3, 0.4)
                val roundTrip = z.tanh().atanh()
                roundTrip.real() shouldBe (z.real() plusOrMinus 1e-13)
                roundTrip.imaginary() shouldBe (z.imaginary() plusOrMinus 1e-13)
            }
        }
    }
})
