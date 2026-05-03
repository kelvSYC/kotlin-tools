package com.kelvsyc.kotlin.commons.numbers

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.fraction.BigFraction
import org.apache.commons.numbers.fraction.Fraction

class StringExtensionsTest : FunSpec() {
    init {
        test("toFraction valid") { "1/2".toFraction() shouldBe Fraction.of(1, 2) }
        test("toFractionOrNull valid") { "1/2".toFractionOrNull() shouldBe Fraction.of(1, 2) }
        test("toFractionOrNull invalid") { "foobar".toFractionOrNull().shouldBeNull() }
        test("toBigFraction valid") { "1/2".toBigFraction() shouldBe BigFraction.of(1, 2) }
        test("toBigFractionOrNull valid") { "1/2".toBigFractionOrNull() shouldBe BigFraction.of(1, 2) }
        test("toBigFractionOrNull invalid") { "foobar".toBigFractionOrNull().shouldBeNull() }
    }
}
