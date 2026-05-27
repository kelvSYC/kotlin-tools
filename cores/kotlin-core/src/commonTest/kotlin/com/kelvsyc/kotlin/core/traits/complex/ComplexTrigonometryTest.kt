package com.kelvsyc.kotlin.core.traits.complex

import com.kelvsyc.kotlin.core.Complex
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.math.PI

class ComplexTrigonometryTest : FunSpec({

    // ── Float ─────────────────────────────────────────────────────────────────

    context("ComplexTrigonometry.Companion.float") {
        val ops = ComplexTrigonometry.float
        val eps = 1e-5f

        context("sin") {
            test("sin(0+0i) = 0+0i") {
                val r = with(ops) { Complex(0.0f, 0.0f).sin() }
                r.real shouldBe (0.0f plusOrMinus eps); r.imaginary shouldBe (0.0f plusOrMinus eps)
            }
            test("sin²(z) + cos²(z) = 1 for 1+i") {
                val z = Complex(1.0f, 1.0f)
                val s = with(ops) { z.sin() }; val c = with(ops) { z.cos() }
                val id = s.real * s.real - s.imaginary * s.imaginary + c.real * c.real - c.imaginary * c.imaginary
                id shouldBe (1.0f plusOrMinus 1e-4f)
            }
        }

        context("cos") {
            test("cos(0+0i) = 1+0i") {
                val r = with(ops) { Complex(0.0f, 0.0f).cos() }
                r.real shouldBe (1.0f plusOrMinus eps); r.imaginary shouldBe (0.0f plusOrMinus eps)
            }
        }

        context("sinh") {
            test("sinh(0+0i) = 0+0i") {
                val r = with(ops) { Complex(0.0f, 0.0f).sinh() }
                r.real shouldBe (0.0f plusOrMinus eps); r.imaginary shouldBe (0.0f plusOrMinus eps)
            }
            test("cosh²(z) - sinh²(z) = 1 for 1+i") {
                val z = Complex(1.0f, 1.0f)
                val sh = with(ops) { z.sinh() }; val ch = with(ops) { z.cosh() }
                val id = ch.real * ch.real - ch.imaginary * ch.imaginary - (sh.real * sh.real - sh.imaginary * sh.imaginary)
                id shouldBe (1.0f plusOrMinus 1e-4f)
            }
        }

        context("cosh") {
            test("cosh(0+0i) = 1+0i") {
                val r = with(ops) { Complex(0.0f, 0.0f).cosh() }
                r.real shouldBe (1.0f plusOrMinus eps); r.imaginary shouldBe (0.0f plusOrMinus eps)
            }
        }

        context("asin round-trip") {
            test("asin(sin(0.5+0.5i)) ≈ 0.5+0.5i") {
                val z = Complex(0.5f, 0.5f)
                val r = with(ops) { z.sin().asin() }
                r.real shouldBe (0.5f plusOrMinus 1e-4f); r.imaginary shouldBe (0.5f plusOrMinus 1e-4f)
            }
        }

        context("atanh round-trip") {
            test("atanh(tanh(0.5+0.3i)) ≈ 0.5+0.3i") {
                val z = Complex(0.5f, 0.3f)
                val r = with(ops) { z.tanh().atanh() }
                r.real shouldBe (0.5f plusOrMinus 1e-4f); r.imaginary shouldBe (0.3f plusOrMinus 1e-4f)
            }
        }
    }

    // ── Double ────────────────────────────────────────────────────────────────

    context("ComplexTrigonometry.Companion.double") {
        val ops = ComplexTrigonometry.double
        val eps = 1e-13

        context("sin") {
            test("sin(0+0i) = 0+0i") {
                val r = with(ops) { Complex(0.0, 0.0).sin() }
                r.real shouldBe (0.0 plusOrMinus eps); r.imaginary shouldBe (0.0 plusOrMinus eps)
            }
            test("sin(π/2+0i) ≈ 1+0i") {
                val r = with(ops) { Complex(PI / 2, 0.0).sin() }
                r.real shouldBe (1.0 plusOrMinus eps); r.imaginary shouldBe (0.0 plusOrMinus eps)
            }
            test("sin²(z) + cos²(z) = 1 for 1+i") {
                val z = Complex(1.0, 1.0)
                val s = with(ops) { z.sin() }; val c = with(ops) { z.cos() }
                val id = s.real * s.real - s.imaginary * s.imaginary + c.real * c.real - c.imaginary * c.imaginary
                id shouldBe (1.0 plusOrMinus 1e-12)
            }
        }

        context("cos") {
            test("cos(0+0i) = 1+0i") {
                val r = with(ops) { Complex(0.0, 0.0).cos() }
                r.real shouldBe (1.0 plusOrMinus eps); r.imaginary shouldBe (0.0 plusOrMinus eps)
            }
            test("cos(π+0i) ≈ -1+0i") {
                val r = with(ops) { Complex(PI, 0.0).cos() }
                r.real shouldBe (-1.0 plusOrMinus eps); r.imaginary shouldBe (0.0 plusOrMinus eps)
            }
        }

        context("sinh") {
            test("sinh(0+0i) = 0+0i") {
                val r = with(ops) { Complex(0.0, 0.0).sinh() }
                r.real shouldBe (0.0 plusOrMinus eps); r.imaginary shouldBe (0.0 plusOrMinus eps)
            }
            test("cosh²(z) - sinh²(z) = 1 for 1+i") {
                val z = Complex(1.0, 1.0)
                val sh = with(ops) { z.sinh() }; val ch = with(ops) { z.cosh() }
                val id = ch.real * ch.real - ch.imaginary * ch.imaginary - (sh.real * sh.real - sh.imaginary * sh.imaginary)
                id shouldBe (1.0 plusOrMinus 1e-12)
            }
        }

        context("cosh") {
            test("cosh(0+0i) = 1+0i") {
                val r = with(ops) { Complex(0.0, 0.0).cosh() }
                r.real shouldBe (1.0 plusOrMinus eps); r.imaginary shouldBe (0.0 plusOrMinus eps)
            }
        }

        context("asin round-trip") {
            test("asin(sin(1+i)) ≈ 1+i") {
                val z = Complex(1.0, 1.0)
                val r = with(ops) { z.sin().asin() }
                r.real shouldBe (1.0 plusOrMinus 1e-12); r.imaginary shouldBe (1.0 plusOrMinus 1e-12)
            }
        }

        context("acos round-trip") {
            test("acos(cos(0.5+0.5i)) ≈ 0.5+0.5i") {
                val z = Complex(0.5, 0.5)
                val r = with(ops) { z.cos().acos() }
                r.real shouldBe (0.5 plusOrMinus 1e-12); r.imaginary shouldBe (0.5 plusOrMinus 1e-12)
            }
        }

        context("atan round-trip") {
            test("atan(tan(0.3+0.3i)) ≈ 0.3+0.3i") {
                val z = Complex(0.3, 0.3)
                val r = with(ops) { z.tan().atan() }
                r.real shouldBe (0.3 plusOrMinus 1e-12); r.imaginary shouldBe (0.3 plusOrMinus 1e-12)
            }
        }

        context("asinh round-trip") {
            test("asinh(sinh(1+i)) ≈ 1+i") {
                val z = Complex(1.0, 1.0)
                val r = with(ops) { z.sinh().asinh() }
                r.real shouldBe (1.0 plusOrMinus 1e-12); r.imaginary shouldBe (1.0 plusOrMinus 1e-12)
            }
        }

        context("acosh round-trip") {
            test("acosh(cosh(1+0.5i)) ≈ 1+0.5i") {
                val z = Complex(1.0, 0.5)
                val r = with(ops) { z.cosh().acosh() }
                r.real shouldBe (1.0 plusOrMinus 1e-12); r.imaginary shouldBe (0.5 plusOrMinus 1e-12)
            }
        }

        context("atanh round-trip") {
            test("atanh(tanh(0.5+0.3i)) ≈ 0.5+0.3i") {
                val z = Complex(0.5, 0.3)
                val r = with(ops) { z.tanh().atanh() }
                r.real shouldBe (0.5 plusOrMinus 1e-12); r.imaginary shouldBe (0.3 plusOrMinus 1e-12)
            }
        }
    }
})
