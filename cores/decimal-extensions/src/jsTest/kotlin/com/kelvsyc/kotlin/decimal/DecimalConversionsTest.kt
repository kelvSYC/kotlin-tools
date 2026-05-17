package com.kelvsyc.kotlin.decimal

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DecimalConversionsTest : FunSpec({
    test("Decimal converts to Double") {
        Decimal("1.5").toKotlinDouble() shouldBe 1.5
    }

    test("Double converts to Decimal") {
        2.5.toDecimal().toKotlinString() shouldBe "2.5"
    }

    test("decimalToDouble converter round-trips finite value") {
        val d = Decimal("3.14")
        decimalToDouble(d) shouldBe 3.14
    }

    test("decimalToDouble reverse converts Double to Decimal") {
        decimalToDouble.reverse(1.25).toKotlinString() shouldBe "1.25"
    }

    test("Decimal converts to String") {
        Decimal("123.456").toKotlinString() shouldBe "123.456"
    }

    test("String converts to Decimal") {
        "9.99".toDecimal().toKotlinString() shouldBe "9.99"
    }

    test("decimalToString converter round-trips") {
        val d = Decimal("1000000.000001")
        decimalToString(d) shouldBe "1000000.000001"
    }

    test("Long converts to Decimal via String") {
        999999999999999999L.toDecimal().toKotlinString() shouldBe "999999999999999999"
    }

    test("Decimal integer converts to Long") {
        Decimal("42").toKotlinLong() shouldBe 42L
    }

    test("decimalToLong converter round-trips integer") {
        val d = Decimal("100")
        decimalToLong(d) shouldBe 100L
        decimalToLong.reverse(100L).toKotlinString() shouldBe "100"
    }
})
