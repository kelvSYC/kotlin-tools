package com.kelvsyc.kotlin.core.traits.complex

import com.kelvsyc.kotlin.core.Complex
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.math.sqrt

class ComplexCubeRootTest : FunSpec({

    // ── Float ─────────────────────────────────────────────────────────────────

    context("ComplexCubeRoot.Companion.float") {
        val ops = ComplexCubeRoot.float

        test("cbrt(1+0i) = 1+0i") {
            val r = with(ops) { Complex(1.0f, 0.0f).cbrt() }
            r.real shouldBe (1.0f plusOrMinus 1e-6f)
            r.imaginary shouldBe (0.0f plusOrMinus 1e-6f)
        }
        test("cbrt(-1+0i) = 0.5 + i·(√3/2) (principal root)") {
            val r = with(ops) { Complex(-1.0f, 0.0f).cbrt() }
            r.real shouldBe (0.5f plusOrMinus 1e-6f)
            r.imaginary shouldBe (sqrt(3.0f) / 2 plusOrMinus 1e-6f)
        }
        test("cbrt(8+0i) = 2+0i") {
            val r = with(ops) { Complex(8.0f, 0.0f).cbrt() }
            r.real shouldBe (2.0f plusOrMinus 1e-5f)
            r.imaginary shouldBe (0.0f plusOrMinus 1e-5f)
        }
        test("cbrt(z)^3 ≈ z for 2+2i") {
            val z = Complex(2.0f, 2.0f)
            val cr = with(ops) { z.cbrt() }
            val a = cr.real; val b = cr.imaginary
            // (a+bi)^3 = a^3 - 3ab^2 + i(3a^2b - b^3)
            val re3 = a * a * a - 3 * a * b * b
            val im3 = 3 * a * a * b - b * b * b
            re3 shouldBe (2.0f plusOrMinus 1e-5f)
            im3 shouldBe (2.0f plusOrMinus 1e-5f)
        }
    }

    // ── Double ────────────────────────────────────────────────────────────────

    context("ComplexCubeRoot.Companion.double") {
        val ops = ComplexCubeRoot.double

        test("cbrt(1+0i) = 1+0i") {
            val r = with(ops) { Complex(1.0, 0.0).cbrt() }
            r.real shouldBe (1.0 plusOrMinus 1e-14)
            r.imaginary shouldBe (0.0 plusOrMinus 1e-14)
        }
        test("cbrt(-1+0i) = 0.5 + i·(√3/2) (principal root)") {
            val r = with(ops) { Complex(-1.0, 0.0).cbrt() }
            r.real shouldBe (0.5 plusOrMinus 1e-14)
            r.imaginary shouldBe (sqrt(3.0) / 2 plusOrMinus 1e-14)
        }
        test("cbrt(8+0i) = 2+0i") {
            val r = with(ops) { Complex(8.0, 0.0).cbrt() }
            r.real shouldBe (2.0 plusOrMinus 1e-14)
            r.imaginary shouldBe (0.0 plusOrMinus 1e-14)
        }
        test("cbrt(0+i) = √3/2 + 0.5i") {
            val r = with(ops) { Complex(0.0, 1.0).cbrt() }
            r.real shouldBe (sqrt(3.0) / 2 plusOrMinus 1e-14)
            r.imaginary shouldBe (0.5 plusOrMinus 1e-14)
        }
        test("cbrt(z)^3 ≈ z for 2+2i") {
            val z = Complex(2.0, 2.0)
            val cr = with(ops) { z.cbrt() }
            val a = cr.real; val b = cr.imaginary
            val re3 = a * a * a - 3 * a * b * b
            val im3 = 3 * a * a * b - b * b * b
            re3 shouldBe (2.0 plusOrMinus 1e-13)
            im3 shouldBe (2.0 plusOrMinus 1e-13)
        }
    }
})
