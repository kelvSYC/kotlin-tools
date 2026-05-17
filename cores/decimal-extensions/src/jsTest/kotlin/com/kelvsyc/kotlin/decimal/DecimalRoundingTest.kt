package com.kelvsyc.kotlin.decimal

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DecimalRoundingTest : FunSpec({
    test("ceil rounds up") {
        Decimal("1.1").ceil().toKotlinString() shouldBe "2"
        Decimal("-1.1").ceil().toKotlinString() shouldBe "-1"
    }

    test("floor rounds down") {
        Decimal("1.9").floor().toKotlinString() shouldBe "1"
        Decimal("-1.1").floor().toKotlinString() shouldBe "-2"
    }

    test("round rounds to nearest") {
        Decimal("1.5").round().toKotlinString() shouldBe "2"
        Decimal("1.4").round().toKotlinString() shouldBe "1"
    }

    test("truncated removes fractional part") {
        Decimal("1.9").truncated().toKotlinString() shouldBe "1"
        Decimal("-1.9").truncated().toKotlinString() shouldBe "-1"
    }

    test("toDecimalPlaces with HALF_UP rounding") {
        Decimal("1.555").toDecimalPlaces(2, DecimalRounding.HALF_UP).toKotlinString() shouldBe "1.56"
    }

    test("toDecimalPlaces with FLOOR rounding") {
        Decimal("1.555").toDecimalPlaces(2, DecimalRounding.FLOOR).toKotlinString() shouldBe "1.55"
    }

    test("toSignificantDigits with HALF_EVEN rounding") {
        Decimal("1.2345").toSignificantDigits(3, DecimalRounding.HALF_EVEN).toKotlinString() shouldBe "1.23"
    }

    test("DecimalRounding enum codes match Decimal.js constants") {
        DecimalRounding.UP.code shouldBe 0
        DecimalRounding.HALF_UP.code shouldBe 4
        DecimalRounding.HALF_EVEN.code shouldBe 6
        DecimalRounding.EUCLID.code shouldBe 9
    }
})
