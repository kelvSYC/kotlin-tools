package com.kelvsyc.kotlin.commons.numbers.complex

import com.kelvsyc.kotlin.core.traits.complex.ComplexCubeRoot
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.complex.Complex as CommonsComplex
import kotlin.math.sqrt

class CommonsComplexCubeRootTest : FunSpec({
    val ops = ComplexCubeRoot.commonsComplex

    fun c(re: Double, im: Double): CommonsComplex = CommonsComplex.ofCartesian(re, im)

    context("cbrt") {
        test("cbrt(1+0i) = 1+0i") {
            with(ops) {
                val z = c(1.0, 0.0).cbrt()
                z.real() shouldBe (1.0 plusOrMinus 1e-15)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-15)
            }
        }
        test("cbrt(-1+0i) = 0.5 + (√3/2)i (principal root)") {
            with(ops) {
                val z = c(-1.0, 0.0).cbrt()
                z.real() shouldBe (0.5 plusOrMinus 1e-15)
                z.imaginary() shouldBe (sqrt(3.0) / 2.0 plusOrMinus 1e-15)
            }
        }
        test("cbrt(8+0i) = 2+0i") {
            with(ops) {
                val z = c(8.0, 0.0).cbrt()
                z.real() shouldBe (2.0 plusOrMinus 1e-14)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-14)
            }
        }
        test("cbrt(0+0i) = 0+0i") {
            with(ops) {
                val z = c(0.0, 0.0).cbrt()
                z.real() shouldBe (0.0 plusOrMinus 1e-15)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-15)
            }
        }
        test("cbrt(z)³ ≈ z round-trip for z = 3+4i") {
            with(ops) {
                val orig = c(3.0, 4.0)
                val cr = orig.cbrt()
                val a = cr.real(); val b = cr.imaginary()
                // (a+bi)³ = a³ - 3ab² + i(3a²b - b³)
                val cubeRe = a * a * a - 3 * a * b * b
                val cubeIm = 3 * a * a * b - b * b * b
                cubeRe shouldBe (orig.real() plusOrMinus 1e-13)
                cubeIm shouldBe (orig.imaginary() plusOrMinus 1e-13)
            }
        }
    }
})
