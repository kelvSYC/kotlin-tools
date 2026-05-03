package com.kelvsyc.kotlin.core.traits

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.BigInteger

class SignedTest : FunSpec({

    // ── BigInteger ────────────────────────────────────────────────────────────

    context("Signed.Companion.bigInteger") {
        val ops = Signed.bigInteger

        test("isNegative: positive") { with(ops) { BigInteger.TWO.isNegative() } shouldBe false }
        test("isNegative: negative") { with(ops) { BigInteger.TWO.negate().isNegative() } shouldBe true }
        test("isNegative: zero") { with(ops) { BigInteger.ZERO.isNegative() } shouldBe false }
        test("isPositive: positive") { with(ops) { BigInteger.TWO.isPositive() } shouldBe true }
        test("isPositive: negative") { with(ops) { BigInteger.TWO.negate().isPositive() } shouldBe false }
        test("isPositive: zero") { with(ops) { BigInteger.ZERO.isPositive() } shouldBe false }
        test("negate") { with(ops) { BigInteger.TWO.negate() } shouldBe BigInteger.valueOf(-2) }
        test("negate is involutory") {
            val x = BigInteger.valueOf(42)
            with(ops) { x.negate().negate() } shouldBe x
        }
        test("abs of positive") { with(ops) { BigInteger.TWO.abs() } shouldBe BigInteger.TWO }
        test("abs of negative") {
            with(ops) { BigInteger.valueOf(-5).abs() } shouldBe BigInteger.valueOf(5)
        }
        test("abs of zero") { with(ops) { BigInteger.ZERO.abs() } shouldBe BigInteger.ZERO }
    }

    // ── BigDecimal ────────────────────────────────────────────────────────────

    context("Signed.Companion.bigDecimal") {
        val ops = Signed.bigDecimal

        test("isNegative: positive") { with(ops) { BigDecimal("3.14").isNegative() } shouldBe false }
        test("isNegative: negative") { with(ops) { BigDecimal("-3.14").isNegative() } shouldBe true }
        test("isNegative: zero") { with(ops) { BigDecimal.ZERO.isNegative() } shouldBe false }
        test("isPositive: positive") { with(ops) { BigDecimal("3.14").isPositive() } shouldBe true }
        test("isPositive: negative") { with(ops) { BigDecimal("-3.14").isPositive() } shouldBe false }
        test("isPositive: zero") { with(ops) { BigDecimal.ZERO.isPositive() } shouldBe false }
        test("negate") {
            with(ops) { BigDecimal("2.5").negate() } shouldBe BigDecimal("-2.5")
        }
        test("negate is involutory") {
            val x = BigDecimal("99.9")
            with(ops) { x.negate().negate() } shouldBe x
        }
        test("abs of positive") {
            with(ops) { BigDecimal("1.5").abs() } shouldBe BigDecimal("1.5")
        }
        test("abs of negative") {
            with(ops) { BigDecimal("-1.5").abs() } shouldBe BigDecimal("1.5")
        }
        test("abs of zero") { with(ops) { BigDecimal.ZERO.abs() } shouldBe BigDecimal.ZERO }
    }
})
