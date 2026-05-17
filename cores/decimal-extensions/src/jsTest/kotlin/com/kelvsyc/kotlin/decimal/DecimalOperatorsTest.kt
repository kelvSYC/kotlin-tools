package com.kelvsyc.kotlin.decimal

import com.kelvsyc.kotlin.core.asComparator
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.booleans.shouldBeTrue

class DecimalOperatorsTest : FunSpec({
    test("plus operator") {
        (Decimal("1") + Decimal("2")).toKotlinString() shouldBe "3"
    }

    test("minus operator") {
        (Decimal("5") - Decimal("3")).toKotlinString() shouldBe "2"
    }

    test("times operator") {
        (Decimal("4") * Decimal("3")).toKotlinString() shouldBe "12"
    }

    test("div operator") {
        (Decimal("10") / Decimal("4")).toKotlinString() shouldBe "2.5"
    }

    test("rem operator") {
        (Decimal("10") % Decimal("3")).toKotlinString() shouldBe "1"
    }

    test("unaryMinus operator") {
        (-Decimal("7")).toKotlinString() shouldBe "-7"
    }

    test("partialComparator returns null for NaN operands") {
        Decimal.partialComparator.compare(Decimal("NaN"), Decimal("1")).shouldBeNull()
        Decimal.partialComparator.compare(Decimal("1"), Decimal("NaN")).shouldBeNull()
        Decimal.partialComparator.compare(Decimal("NaN"), Decimal("NaN")).shouldBeNull()
    }

    test("partialComparator compares finite values correctly") {
        Decimal.partialComparator.compare(Decimal("1"), Decimal("2")) shouldBe -1
        Decimal.partialComparator.compare(Decimal("2"), Decimal("2")) shouldBe 0
        Decimal.partialComparator.compare(Decimal("3"), Decimal("2")) shouldBe 1
    }

    test("partialComparator asComparator places NaN last with positive fallback") {
        val cmp = Decimal.partialComparator.asComparator(1)
        (cmp.compare(Decimal("NaN"), Decimal("1")) > 0).shouldBeTrue()
    }
})
