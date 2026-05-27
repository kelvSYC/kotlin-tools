package com.kelvsyc.kotlin.commons.numbers.complex

import com.kelvsyc.kotlin.core.traits.complex.ComplexExpLog
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.complex.Complex as CommonsComplex
import kotlin.math.PI

class CommonsComplexExpLogTest : FunSpec({
    val ops = ComplexExpLog.commonsComplex

    fun c(re: Double, im: Double): CommonsComplex = CommonsComplex.ofCartesian(re, im)

    context("exp") {
        test("exp(0+0i) = 1+0i") {
            with(ops) {
                val z = c(0.0, 0.0).exp()
                z.real() shouldBe (1.0 plusOrMinus 1e-15)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-15)
            }
        }
        test("exp(0+πi) ≈ -1+0i (Euler's identity)") {
            with(ops) {
                val z = c(0.0, PI).exp()
                z.real() shouldBe (-1.0 plusOrMinus 1e-15)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-15)
            }
        }
        test("exp(1+0i) = e+0i") {
            with(ops) {
                val z = c(1.0, 0.0).exp()
                z.real() shouldBe (kotlin.math.E plusOrMinus 1e-15)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-15)
            }
        }
    }

    context("ln") {
        test("ln(1+0i) = 0+0i") {
            with(ops) {
                val z = c(1.0, 0.0).ln()
                z.real() shouldBe (0.0 plusOrMinus 1e-15)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-15)
            }
        }
        test("ln(-1+0i) ≈ 0+πi") {
            with(ops) {
                val z = c(-1.0, 0.0).ln()
                z.real() shouldBe (0.0 plusOrMinus 1e-15)
                z.imaginary() shouldBe (PI plusOrMinus 1e-15)
            }
        }
        test("exp(ln(z)) ≈ z round-trip") {
            with(ops) {
                val orig = c(3.0, 4.0)
                val roundTrip = orig.ln().exp()
                roundTrip.real() shouldBe (orig.real() plusOrMinus 1e-14)
                roundTrip.imaginary() shouldBe (orig.imaginary() plusOrMinus 1e-14)
            }
        }
    }

    context("pow scalar") {
        test("pow(2+0i, 2.0) ≈ 4+0i") {
            with(ops) {
                val z = c(2.0, 0.0).pow(2.0)
                z.real() shouldBe (4.0 plusOrMinus 1e-14)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-14)
            }
        }
        test("pow(0+i, 2.0) ≈ -1+0i") {
            with(ops) {
                val z = c(0.0, 1.0).pow(2.0)
                z.real() shouldBe (-1.0 plusOrMinus 1e-14)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-14)
            }
        }
    }

    context("powComplex") {
        test("powComplex(z, 1+0i) ≈ z") {
            with(ops) {
                val base = c(2.0, 3.0)
                val z = base.powComplex(c(1.0, 0.0))
                z.real() shouldBe (base.real() plusOrMinus 1e-14)
                z.imaginary() shouldBe (base.imaginary() plusOrMinus 1e-14)
            }
        }
        test("powComplex(e+0i, 1+0i) = e+0i") {
            with(ops) {
                val z = c(kotlin.math.E, 0.0).powComplex(c(1.0, 0.0))
                z.real() shouldBe (kotlin.math.E plusOrMinus 1e-14)
                z.imaginary() shouldBe (0.0 plusOrMinus 1e-14)
            }
        }
    }

    context("structural accessors") {
        test("real and imaginary") {
            with(ops) {
                c(3.0, 4.0).real() shouldBe 3.0
                c(3.0, 4.0).imaginary() shouldBe 4.0
            }
        }
        test("of(real, imaginary)") {
            with(ops) {
                val z = of(3.0, 4.0)
                z.real() shouldBe 3.0
                z.imaginary() shouldBe 4.0
            }
        }
    }
})
