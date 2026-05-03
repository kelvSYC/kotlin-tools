package com.kelvsyc.kotlin.commons.numbers

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.core.DD

class DdExtensionsTest : FunSpec() {
    private val a = DD.of(3.0)
    private val b = DD.of(2.0)

    init {
        test("unaryPlus is identity") { +a shouldBe a }
        test("unaryMinus negates") { (-a).hi() shouldBe (-3.0 plusOrMinus 1e-15) }
        test("plus double") { (a + 1.0).hi() shouldBe (4.0 plusOrMinus 1e-15) }
        test("plus DD") { (a + b).hi() shouldBe (5.0 plusOrMinus 1e-15) }
        test("minus double") { (a - 1.0).hi() shouldBe (2.0 plusOrMinus 1e-15) }
        test("minus DD") { (a - b).hi() shouldBe (1.0 plusOrMinus 1e-15) }
        test("times Int") { (a * 2).hi() shouldBe (6.0 plusOrMinus 1e-15) }
        test("times double") { (a * 2.0).hi() shouldBe (6.0 plusOrMinus 1e-15) }
        test("times DD") { (a * b).hi() shouldBe (6.0 plusOrMinus 1e-15) }
        test("div double") { (a / 2.0).hi() shouldBe (1.5 plusOrMinus 1e-15) }
        test("div DD") { (a / b).hi() shouldBe (1.5 plusOrMinus 1e-15) }
    }
}
