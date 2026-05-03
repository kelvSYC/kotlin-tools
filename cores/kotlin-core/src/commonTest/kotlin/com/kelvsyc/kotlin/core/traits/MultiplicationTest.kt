package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.double
import com.kelvsyc.kotlin.core.traits.fp.float
import com.kelvsyc.kotlin.core.traits.integral.IntegerArithmetic
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.integral.long
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MultiplicationTest : FunSpec({

    // ── IntegerArithmetic instances satisfy Multiplication<T> ─────────────────

    context("Multiplication via IntegerArithmetic.int") {
        val ops: Multiplication<Int> = IntegerArithmetic.int

        test("one") { ops.one shouldBe 1 }
        test("multiply") { with(ops) { 3.multiply(4) } shouldBe 12 }
    }

    context("Multiplication via IntegerArithmetic.long") {
        val ops: Multiplication<Long> = IntegerArithmetic.long

        test("one") { ops.one shouldBe 1L }
        test("multiply") { with(ops) { 3L.multiply(4L) } shouldBe 12L }
    }

    // ── FloatingPointArithmetic instances satisfy Multiplication<T> ───────────

    context("Multiplication via FloatingPointArithmetic.float") {
        val ops: Multiplication<Float> = FloatingPointArithmetic.float

        test("one") { ops.one shouldBe 1.0f }
        test("multiply") { with(ops) { 2.5f.multiply(4.0f) } shouldBe 10.0f }
    }

    context("Multiplication via FloatingPointArithmetic.double") {
        val ops: Multiplication<Double> = FloatingPointArithmetic.double

        test("one") { ops.one shouldBe 1.0 }
        test("multiply") { with(ops) { 2.5.multiply(4.0) } shouldBe 10.0 }
    }
})
