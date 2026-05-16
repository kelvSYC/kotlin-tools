package com.kelvsyc.kotlin.core.traits.integral

import com.kelvsyc.kotlin.core.bigIntOf
import com.kelvsyc.kotlin.core.toDecimalString
import com.kelvsyc.kotlin.core.traits.fp.IntegerPower
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BigIntPowerTest : FunSpec({
    val ops = IntegerPower.bigInt

    context("pow") {
        test("2 ** 0 = 1") { with(ops) { bigIntOf(2).pow(0) }.toDecimalString() shouldBe "1" }
        test("2 ** 1 = 2") { with(ops) { bigIntOf(2).pow(1) }.toDecimalString() shouldBe "2" }
        test("2 ** 10 = 1024") { with(ops) { bigIntOf(2).pow(10) }.toDecimalString() shouldBe "1024" }
        test("2 ** 64 = 18446744073709551616") {
            with(ops) { bigIntOf(2).pow(64) }.toDecimalString() shouldBe "18446744073709551616"
        }
        test("0 ** 5 = 0") { with(ops) { bigIntOf(0).pow(5) }.toDecimalString() shouldBe "0" }
        test("negative base: (-2) ** 3 = -8") {
            with(ops) { bigIntOf(-2).pow(3) }.toDecimalString() shouldBe "-8"
        }
        test("negative exponent throws IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> { with(ops) { bigIntOf(2).pow(-1) } }
        }
    }
})
