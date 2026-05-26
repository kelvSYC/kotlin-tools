package com.kelvsyc.kotlin.core.traits.complex

import com.kelvsyc.kotlin.core.Complex
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe

class ComplexSquareRootTest : FunSpec({

    // ── Float ─────────────────────────────────────────────────────────────────

    context("ComplexSquareRoot.Companion.float") {
        val ops = ComplexSquareRoot.float

        test("sqrt(4+0i) = 2+0i") {
            val r = with(ops) { Complex(4.0f, 0.0f).sqrt() }
            r.real shouldBe (2.0f plusOrMinus 1e-6f)
            r.imaginary shouldBe (0.0f plusOrMinus 1e-6f)
        }
        test("sqrt(-1+0i) = 0+i") {
            val r = with(ops) { Complex(-1.0f, 0.0f).sqrt() }
            r.real shouldBe (0.0f plusOrMinus 1e-6f)
            r.imaginary shouldBe (1.0f plusOrMinus 1e-6f)
        }
        test("sqrt(0+0i) = 0+0i") {
            val r = with(ops) { Complex(0.0f, 0.0f).sqrt() }
            r.real shouldBe 0.0f
            r.imaginary shouldBe 0.0f
        }
        test("sqrt(z)^2 ≈ z for 3+4i") {
            val z = Complex(3.0f, 4.0f)
            val sq = with(ops) { z.sqrt() }
            val re2 = sq.real * sq.real - sq.imaginary * sq.imaginary
            val im2 = 2 * sq.real * sq.imaginary
            re2 shouldBe (3.0f plusOrMinus 1e-5f)
            im2 shouldBe (4.0f plusOrMinus 1e-5f)
        }
        test("sqrt of purely imaginary: sqrt(0+2i) has positive real part") {
            val r = with(ops) { Complex(0.0f, 2.0f).sqrt() }
            r.real shouldBe (1.0f plusOrMinus 1e-6f)
            r.imaginary shouldBe (1.0f plusOrMinus 1e-6f)
        }
    }

    // ── Double ────────────────────────────────────────────────────────────────

    context("ComplexSquareRoot.Companion.double") {
        val ops = ComplexSquareRoot.double

        test("sqrt(4+0i) = 2+0i") {
            val r = with(ops) { Complex(4.0, 0.0).sqrt() }
            r.real shouldBe (2.0 plusOrMinus 1e-14)
            r.imaginary shouldBe (0.0 plusOrMinus 1e-14)
        }
        test("sqrt(-1+0i) = 0+i") {
            val r = with(ops) { Complex(-1.0, 0.0).sqrt() }
            r.real shouldBe (0.0 plusOrMinus 1e-14)
            r.imaginary shouldBe (1.0 plusOrMinus 1e-14)
        }
        test("sqrt(0+0i) = 0+0i") {
            val r = with(ops) { Complex(0.0, 0.0).sqrt() }
            r.real shouldBe 0.0
            r.imaginary shouldBe 0.0
        }
        test("sqrt(z)^2 ≈ z for 3+4i") {
            val z = Complex(3.0, 4.0)
            val sq = with(ops) { z.sqrt() }
            val re2 = sq.real * sq.real - sq.imaginary * sq.imaginary
            val im2 = 2 * sq.real * sq.imaginary
            re2 shouldBe (3.0 plusOrMinus 1e-13)
            im2 shouldBe (4.0 plusOrMinus 1e-13)
        }
        test("sqrt of purely imaginary: sqrt(0+2i) has positive real part") {
            val r = with(ops) { Complex(0.0, 2.0).sqrt() }
            r.real shouldBe (1.0 plusOrMinus 1e-14)
            r.imaginary shouldBe (1.0 plusOrMinus 1e-14)
        }
        test("numerically stable for small imaginary: sqrt(1+1e-30i)") {
            val r = with(ops) { Complex(1.0, 1e-30).sqrt() }
            r.real shouldBe (1.0 plusOrMinus 1e-14)
            // imaginary should be approximately 5e-31, not 0
            (r.imaginary > 0.0) shouldBe true
        }
    }
})
