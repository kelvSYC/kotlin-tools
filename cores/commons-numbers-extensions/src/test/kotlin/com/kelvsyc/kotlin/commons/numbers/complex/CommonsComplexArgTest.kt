package com.kelvsyc.kotlin.commons.numbers.complex

import com.kelvsyc.kotlin.core.traits.complex.ComplexArg
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.complex.Complex as CommonsComplex
import kotlin.math.PI

class CommonsComplexArgTest : FunSpec({
    val ops = ComplexArg.commonsComplex

    fun c(re: Double, im: Double): CommonsComplex = CommonsComplex.ofCartesian(re, im)

    context("arg") {
        test("arg(1+0i) = 0") {
            with(ops) { c(1.0, 0.0).arg() shouldBe (0.0 plusOrMinus 1e-15) }
        }
        test("arg(0+i) = π/2") {
            with(ops) { c(0.0, 1.0).arg() shouldBe (PI / 2 plusOrMinus 1e-15) }
        }
        test("arg(-1+0i) = π") {
            with(ops) { c(-1.0, 0.0).arg() shouldBe (PI plusOrMinus 1e-15) }
        }
        test("arg(0-i) = -π/2") {
            with(ops) { c(0.0, -1.0).arg() shouldBe (-PI / 2 plusOrMinus 1e-15) }
        }
    }

    context("argPi") {
        test("argPi(1+0i) = 0") {
            with(ops) { c(1.0, 0.0).argPi() shouldBe (0.0 plusOrMinus 1e-15) }
        }
        test("argPi(0+i) = 0.5") {
            with(ops) { c(0.0, 1.0).argPi() shouldBe (0.5 plusOrMinus 1e-15) }
        }
        test("argPi(-1+0i) = 1") {
            with(ops) { c(-1.0, 0.0).argPi() shouldBe (1.0 plusOrMinus 1e-15) }
        }
        test("argPi(0-i) = -0.5") {
            with(ops) { c(0.0, -1.0).argPi() shouldBe (-0.5 plusOrMinus 1e-15) }
        }
    }

    context("structural accessors") {
        test("real and imaginary") {
            with(ops) {
                c(3.0, 4.0).real() shouldBe 3.0
                c(3.0, 4.0).imaginary() shouldBe 4.0
            }
        }
    }
})
