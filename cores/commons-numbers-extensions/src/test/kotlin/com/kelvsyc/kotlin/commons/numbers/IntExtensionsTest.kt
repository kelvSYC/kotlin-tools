package com.kelvsyc.kotlin.commons.numbers

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.fraction.Fraction

class IntExtensionsTest : FunSpec() {
    init {
        test("toDD") { 3.toDD().hi() shouldBe 3.0 }
        test("toFraction") { 3.toFraction() shouldBe Fraction.of(3) }
        test("toBigFraction") { 3.toBigFraction().numeratorAsInt shouldBe 3 }
    }
}
