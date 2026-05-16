package com.kelvsyc.kotlin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan

class BigIntTest : FunSpec({

    context("bigIntOf") {
        test("from Int") {
            val a = bigIntOf(42)
            a.toDecimalString() shouldBe "42"
        }
        test("from String") {
            val a = bigIntOf("12345678901234567890")
            a.toDecimalString() shouldBe "12345678901234567890"
        }
        test("negative from String") {
            val a = bigIntOf("-9999999999999999999")
            a.toDecimalString() shouldBe "-9999999999999999999"
        }
    }

    context("operator extensions") {
        val a = bigIntOf("100")
        val b = bigIntOf("30")

        test("plus") { (a + b).toDecimalString() shouldBe "130" }
        test("minus") { (a - b).toDecimalString() shouldBe "70" }
        test("times") { (a * b).toDecimalString() shouldBe "3000" }
        test("div truncates toward zero") { (a / b).toDecimalString() shouldBe "3" }
        test("rem") { (a % b).toDecimalString() shouldBe "10" }
        test("unaryMinus") { (-a).toDecimalString() shouldBe "-100" }
    }

    context("compareTo") {
        val one = bigIntOf(1)
        val two = bigIntOf(2)

        test("less than") { (one.compareTo(two) < 0) shouldBe true }
        test("greater than") { (two.compareTo(one) > 0) shouldBe true }
        test("equal") { one.compareTo(bigIntOf(1)) shouldBe 0 }
        test("negative less than positive") { ((-one).compareTo(one) < 0) shouldBe true }
    }

    context("comparator") {
        val list = listOf(bigIntOf(3), bigIntOf(1), bigIntOf(2))
        val sorted = list.sortedWith(BigInt.comparator)

        test("sorts ascending") {
            sorted[0].toDecimalString() shouldBe "1"
            sorted[1].toDecimalString() shouldBe "2"
            sorted[2].toDecimalString() shouldBe "3"
        }
    }

    context("large value round-trip") {
        val large = "99999999999999999999999999999999"
        test("toString round-trip") { bigIntOf(large).toDecimalString() shouldBe large }
    }
})
