package com.kelvsyc.kotlin.commons.numbers.complex

import com.kelvsyc.kotlin.core.traits.complex.ComplexSquareRoot
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.complex.Complex as CommonsComplex
import kotlin.math.sqrt

class CommonsComplexSquareRootTest : FunSpec({
    val ops = ComplexSquareRoot.commonsComplex

    fun c(re: Double, im: Double): CommonsComplex = CommonsComplex.ofCartesian(re, im)

    context("sqrt") {
        test("sqrt(4+0i) = 2+0i") {
            with(ops) {
                val z = c(4.0, 0.0).sqrt()
                z.real() shouldBe (2.0 plusOrMinus 1e-15)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-15)
            }
        }
        test("sqrt(-1+0i) = 0+i (principal square root)") {
            with(ops) {
                val z = c(-1.0, 0.0).sqrt()
                z.real() shouldBe (0.0 plusOrMinus 1e-15)
                z.imaginary() shouldBe (1.0 plusOrMinus 1e-15)
            }
        }
        test("sqrt(0+4i) = √2 + √2·i") {
            with(ops) {
                val z = c(0.0, 4.0).sqrt()
                z.real() shouldBe (sqrt(2.0) plusOrMinus 1e-15)
                z.imaginary() shouldBe (sqrt(2.0) plusOrMinus 1e-15)
            }
        }
        test("sqrt(0+0i) = 0+0i") {
            with(ops) {
                val z = c(0.0, 0.0).sqrt()
                z.real() shouldBe (0.0 plusOrMinus 1e-15)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-15)
            }
        }
        test("sqrt(z)² ≈ z round-trip") {
            with(ops) {
                val orig = c(3.0, 4.0)
                val s = orig.sqrt()
                val sq = s.real() * s.real() - s.imaginary() * s.imaginary()
                val sqIm = 2.0 * s.real() * s.imaginary()
                sq shouldBe (orig.real() plusOrMinus 1e-14)
                sqIm shouldBe (orig.imaginary() plusOrMinus 1e-14)
            }
        }
    }
})
