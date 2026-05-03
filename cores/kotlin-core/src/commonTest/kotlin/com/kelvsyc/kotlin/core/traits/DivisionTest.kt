package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.double
import com.kelvsyc.kotlin.core.traits.fp.float
import com.kelvsyc.kotlin.core.traits.integral.IntegerArithmetic
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.integral.long
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DivisionTest : FunSpec({

    // ── IntegerArithmetic instances satisfy Division<T> ───────────────────────

    context("Division via IntegerArithmetic.int (truncating)") {
        val ops: Division<Int> = IntegerArithmetic.int

        test("exact") { with(ops) { 12.divide(4) } shouldBe 3 }
        test("truncates toward zero (positive)") { with(ops) { 7.divide(2) } shouldBe 3 }
        test("truncates toward zero (negative)") { with(ops) { (-7).divide(2) } shouldBe -3 }
    }

    context("Division via IntegerArithmetic.long (truncating)") {
        val ops: Division<Long> = IntegerArithmetic.long

        test("exact") { with(ops) { 12L.divide(4L) } shouldBe 3L }
        test("truncates toward zero") { with(ops) { 7L.divide(2L) } shouldBe 3L }
    }

    // ── FloatingPointArithmetic instances satisfy Division<T> ─────────────────

    context("Division via FloatingPointArithmetic.float") {
        val ops: Division<Float> = FloatingPointArithmetic.float

        test("exact") { with(ops) { 10.0f.divide(4.0f) } shouldBe 2.5f }
        test("divide by zero yields infinity") { with(ops) { 1.0f.divide(0.0f) } shouldBe Float.POSITIVE_INFINITY }
    }

    context("Division via FloatingPointArithmetic.double") {
        val ops: Division<Double> = FloatingPointArithmetic.double

        test("exact") { with(ops) { 10.0.divide(4.0) } shouldBe 2.5 }
        test("divide by zero yields infinity") { with(ops) { 1.0.divide(0.0) } shouldBe Double.POSITIVE_INFINITY }
    }
})
