package com.kelvsyc.kotlin.core.traits.complex

import com.kelvsyc.kotlin.core.Complex
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.math.PI

class ComplexArgTest : FunSpec({

    // ── Float ─────────────────────────────────────────────────────────────────

    context("ComplexArg.Companion.float") {
        val ops = ComplexArg.float

        context("arg") {
            test("arg(1+0i) = 0") { with(ops) { Complex(1.0f, 0.0f).arg() } shouldBe 0.0f }
            test("arg(0+i) = π/2") { with(ops) { Complex(0.0f, 1.0f).arg() } shouldBe (PI.toFloat() / 2 plusOrMinus 1e-6f) }
            test("arg(-1+0i) = π") { with(ops) { Complex(-1.0f, 0.0f).arg() } shouldBe (PI.toFloat() plusOrMinus 1e-6f) }
            test("arg(0-i) = -π/2") { with(ops) { Complex(0.0f, -1.0f).arg() } shouldBe (-PI.toFloat() / 2 plusOrMinus 1e-6f) }
            test("arg(1+i) = π/4") { with(ops) { Complex(1.0f, 1.0f).arg() } shouldBe (PI.toFloat() / 4 plusOrMinus 1e-6f) }
        }

        context("argPi") {
            test("argPi(1+0i) = 0") { with(ops) { Complex(1.0f, 0.0f).argPi() } shouldBe 0.0f }
            test("argPi(0+i) = 0.5") { with(ops) { Complex(0.0f, 1.0f).argPi() } shouldBe (0.5f plusOrMinus 1e-6f) }
            test("argPi(-1+0i) = 1") { with(ops) { Complex(-1.0f, 0.0f).argPi() } shouldBe (1.0f plusOrMinus 1e-6f) }
            test("argPi(0-i) = -0.5") { with(ops) { Complex(0.0f, -1.0f).argPi() } shouldBe (-0.5f plusOrMinus 1e-6f) }
            test("argPi(1+i) = 0.25") { with(ops) { Complex(1.0f, 1.0f).argPi() } shouldBe (0.25f plusOrMinus 1e-6f) }
        }
    }

    // ── Double ────────────────────────────────────────────────────────────────

    context("ComplexArg.Companion.double") {
        val ops = ComplexArg.double

        context("arg") {
            test("arg(1+0i) = 0") { with(ops) { Complex(1.0, 0.0).arg() } shouldBe 0.0 }
            test("arg(0+i) = π/2") { with(ops) { Complex(0.0, 1.0).arg() } shouldBe (PI / 2 plusOrMinus 1e-15) }
            test("arg(-1+0i) = π") { with(ops) { Complex(-1.0, 0.0).arg() } shouldBe (PI plusOrMinus 1e-15) }
            test("arg(0-i) = -π/2") { with(ops) { Complex(0.0, -1.0).arg() } shouldBe (-PI / 2 plusOrMinus 1e-15) }
            test("arg(1+i) = π/4") { with(ops) { Complex(1.0, 1.0).arg() } shouldBe (PI / 4 plusOrMinus 1e-15) }
        }

        context("argPi") {
            test("argPi(1+0i) = 0") { with(ops) { Complex(1.0, 0.0).argPi() } shouldBe 0.0 }
            test("argPi(0+i) = 0.5") { with(ops) { Complex(0.0, 1.0).argPi() } shouldBe (0.5 plusOrMinus 1e-15) }
            test("argPi(-1+0i) = 1") { with(ops) { Complex(-1.0, 0.0).argPi() } shouldBe (1.0 plusOrMinus 1e-15) }
            test("argPi(0-i) = -0.5") { with(ops) { Complex(0.0, -1.0).argPi() } shouldBe (-0.5 plusOrMinus 1e-15) }
            test("argPi(1+i) = 0.25") { with(ops) { Complex(1.0, 1.0).argPi() } shouldBe (0.25 plusOrMinus 1e-15) }
        }
    }
})
