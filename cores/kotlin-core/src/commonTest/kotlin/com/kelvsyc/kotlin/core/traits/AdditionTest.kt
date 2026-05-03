package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.double
import com.kelvsyc.kotlin.core.traits.fp.float
import com.kelvsyc.kotlin.core.traits.integral.IntegerArithmetic
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.integral.long
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AdditionTest : FunSpec({

    // ── IntegerArithmetic instances satisfy Addition<T> ───────────────────────

    context("Addition via IntegerArithmetic.int") {
        val ops: Addition<Int> = IntegerArithmetic.int

        test("zero") { ops.zero shouldBe 0 }
        test("add") { with(ops) { 3.add(4) } shouldBe 7 }
        test("subtract") { with(ops) { 10.subtract(3) } shouldBe 7 }
    }

    context("Addition via IntegerArithmetic.long") {
        val ops: Addition<Long> = IntegerArithmetic.long

        test("zero") { ops.zero shouldBe 0L }
        test("add") { with(ops) { 3L.add(4L) } shouldBe 7L }
        test("subtract") { with(ops) { 10L.subtract(3L) } shouldBe 7L }
    }

    // ── FloatingPointArithmetic instances satisfy Addition<T> ─────────────────

    context("Addition via FloatingPointArithmetic.float") {
        val ops: Addition<Float> = FloatingPointArithmetic.float

        test("zero") { ops.zero shouldBe 0.0f }
        test("add") { with(ops) { 1.5f.add(2.5f) } shouldBe 4.0f }
        test("subtract") { with(ops) { 5.0f.subtract(3.0f) } shouldBe 2.0f }
    }

    context("Addition via FloatingPointArithmetic.double") {
        val ops: Addition<Double> = FloatingPointArithmetic.double

        test("zero") { ops.zero shouldBe 0.0 }
        test("add") { with(ops) { 1.5.add(2.5) } shouldBe 4.0 }
        test("subtract") { with(ops) { 5.0.subtract(3.0) } shouldBe 2.0 }
    }
})
