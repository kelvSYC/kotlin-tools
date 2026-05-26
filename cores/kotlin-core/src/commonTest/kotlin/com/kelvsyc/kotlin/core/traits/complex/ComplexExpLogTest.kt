package com.kelvsyc.kotlin.core.traits.complex

import com.kelvsyc.kotlin.core.Complex
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.math.PI

class ComplexExpLogTest : FunSpec({

    // ── Float ─────────────────────────────────────────────────────────────────

    context("ComplexExpLog.Companion.float") {
        val ops = ComplexExpLog.float

        context("exp") {
            test("exp(0+0i) = 1+0i") {
                val r = with(ops) { Complex(0.0f, 0.0f).exp() }
                r.real shouldBe (1.0f plusOrMinus 1e-6f)
                r.imaginary shouldBe (0.0f plusOrMinus 1e-6f)
            }
            test("exp(0+πi) ≈ -1+0i (Euler's identity)") {
                val r = with(ops) { Complex(0.0f, PI.toFloat()).exp() }
                r.real shouldBe (-1.0f plusOrMinus 1e-6f)
                r.imaginary shouldBe (0.0f plusOrMinus 1e-6f)
            }
            test("exp(1+0i) = e+0i") {
                val r = with(ops) { Complex(1.0f, 0.0f).exp() }
                r.real shouldBe (kotlin.math.E.toFloat() plusOrMinus 1e-6f)
                r.imaginary shouldBe (0.0f plusOrMinus 1e-6f)
            }
            test("exp(0+π/2·i) ≈ 0+i") {
                val r = with(ops) { Complex(0.0f, PI.toFloat() / 2).exp() }
                r.real shouldBe (0.0f plusOrMinus 1e-6f)
                r.imaginary shouldBe (1.0f plusOrMinus 1e-6f)
            }
        }

        context("ln") {
            test("ln(1+0i) = 0+0i") {
                val r = with(ops) { Complex(1.0f, 0.0f).ln() }
                r.real shouldBe (0.0f plusOrMinus 1e-6f)
                r.imaginary shouldBe (0.0f plusOrMinus 1e-6f)
            }
            test("ln(-1+0i) ≈ 0+πi") {
                val r = with(ops) { Complex(-1.0f, 0.0f).ln() }
                r.real shouldBe (0.0f plusOrMinus 1e-6f)
                r.imaginary shouldBe (PI.toFloat() plusOrMinus 1e-6f)
            }
            test("ln(e+0i) = 1+0i") {
                val r = with(ops) { Complex(kotlin.math.E.toFloat(), 0.0f).ln() }
                r.real shouldBe (1.0f plusOrMinus 1e-6f)
                r.imaginary shouldBe (0.0f plusOrMinus 1e-6f)
            }
            test("ln(exp(z)) ≈ z for 1+i") {
                val z = Complex(1.0f, 1.0f)
                val r = with(ops) { z.exp().ln() }
                r.real shouldBe (1.0f plusOrMinus 1e-5f)
                r.imaginary shouldBe (1.0f plusOrMinus 1e-5f)
            }
        }

        context("pow(T)") {
            test("(2+0i)^2 = 4+0i") {
                val r = with(ops) { Complex(2.0f, 0.0f).pow(2.0f) }
                r.real shouldBe (4.0f plusOrMinus 1e-5f)
                r.imaginary shouldBe (0.0f plusOrMinus 1e-5f)
            }
            test("(0+i)^2 ≈ -1+0i") {
                val r = with(ops) { Complex(0.0f, 1.0f).pow(2.0f) }
                r.real shouldBe (-1.0f plusOrMinus 1e-5f)
                r.imaginary shouldBe (0.0f plusOrMinus 1e-5f)
            }
        }

        context("powComplex(C)") {
            test("(2+0i)^(2+0i) = 4+0i") {
                val r = with(ops) { Complex(2.0f, 0.0f).powComplex(Complex(2.0f, 0.0f)) }
                r.real shouldBe (4.0f plusOrMinus 1e-5f)
                r.imaginary shouldBe (0.0f plusOrMinus 1e-5f)
            }
        }
    }

    // ── Double ────────────────────────────────────────────────────────────────

    context("ComplexExpLog.Companion.double") {
        val ops = ComplexExpLog.double

        context("exp") {
            test("exp(0+0i) = 1+0i") {
                val r = with(ops) { Complex(0.0, 0.0).exp() }
                r.real shouldBe (1.0 plusOrMinus 1e-14)
                r.imaginary shouldBe (0.0 plusOrMinus 1e-14)
            }
            test("exp(0+πi) ≈ -1+0i (Euler's identity)") {
                val r = with(ops) { Complex(0.0, PI).exp() }
                r.real shouldBe (-1.0 plusOrMinus 1e-14)
                r.imaginary shouldBe (0.0 plusOrMinus 1e-14)
            }
            test("exp(1+0i) = e+0i") {
                val r = with(ops) { Complex(1.0, 0.0).exp() }
                r.real shouldBe (kotlin.math.E plusOrMinus 1e-14)
                r.imaginary shouldBe (0.0 plusOrMinus 1e-14)
            }
            test("exp(0+π/2·i) ≈ 0+i") {
                val r = with(ops) { Complex(0.0, PI / 2).exp() }
                r.real shouldBe (0.0 plusOrMinus 1e-14)
                r.imaginary shouldBe (1.0 plusOrMinus 1e-14)
            }
        }

        context("ln") {
            test("ln(1+0i) = 0+0i") {
                val r = with(ops) { Complex(1.0, 0.0).ln() }
                r.real shouldBe (0.0 plusOrMinus 1e-14)
                r.imaginary shouldBe (0.0 plusOrMinus 1e-14)
            }
            test("ln(-1+0i) ≈ 0+πi") {
                val r = with(ops) { Complex(-1.0, 0.0).ln() }
                r.real shouldBe (0.0 plusOrMinus 1e-14)
                r.imaginary shouldBe (PI plusOrMinus 1e-14)
            }
            test("ln(e+0i) = 1+0i") {
                val r = with(ops) { Complex(kotlin.math.E, 0.0).ln() }
                r.real shouldBe (1.0 plusOrMinus 1e-14)
                r.imaginary shouldBe (0.0 plusOrMinus 1e-14)
            }
            test("ln(exp(z)) ≈ z for 1+i") {
                val z = Complex(1.0, 1.0)
                val r = with(ops) { z.exp().ln() }
                r.real shouldBe (1.0 plusOrMinus 1e-13)
                r.imaginary shouldBe (1.0 plusOrMinus 1e-13)
            }
        }

        context("pow(T)") {
            test("(2+0i)^2 = 4+0i") {
                val r = with(ops) { Complex(2.0, 0.0).pow(2.0) }
                r.real shouldBe (4.0 plusOrMinus 1e-13)
                r.imaginary shouldBe (0.0 plusOrMinus 1e-13)
            }
            test("(0+i)^2 ≈ -1+0i") {
                val r = with(ops) { Complex(0.0, 1.0).pow(2.0) }
                r.real shouldBe (-1.0 plusOrMinus 1e-13)
                r.imaginary shouldBe (0.0 plusOrMinus 1e-13)
            }
        }

        context("powComplex(C)") {
            test("(2+0i)^(2+0i) = 4+0i") {
                val r = with(ops) { Complex(2.0, 0.0).powComplex(Complex(2.0, 0.0)) }
                r.real shouldBe (4.0 plusOrMinus 1e-13)
                r.imaginary shouldBe (0.0 plusOrMinus 1e-13)
            }
        }
    }
})
