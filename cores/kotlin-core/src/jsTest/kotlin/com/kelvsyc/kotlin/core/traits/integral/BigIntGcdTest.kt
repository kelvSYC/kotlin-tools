package com.kelvsyc.kotlin.core.traits.integral

import com.kelvsyc.kotlin.core.bigIntOf
import com.kelvsyc.kotlin.core.toDecimalString
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BigIntGcdTest : FunSpec({
    val ops = Gcd.bigInt

    context("gcd") {
        test("gcd(12, 8) = 4") {
            with(ops) { bigIntOf(12).gcd(bigIntOf(8)) }.toDecimalString() shouldBe "4"
        }
        test("gcd(0, 5) = 5") {
            with(ops) { bigIntOf(0).gcd(bigIntOf(5)) }.toDecimalString() shouldBe "5"
        }
        test("gcd(0, 0) = 0") {
            with(ops) { bigIntOf(0).gcd(bigIntOf(0)) }.toDecimalString() shouldBe "0"
        }
        test("gcd of negatives uses absolute values") {
            with(ops) { bigIntOf(-12).gcd(bigIntOf(8)) }.toDecimalString() shouldBe "4"
        }
        test("gcd of coprime values = 1") {
            with(ops) { bigIntOf(7).gcd(bigIntOf(13)) }.toDecimalString() shouldBe "1"
        }
        test("large values") {
            with(ops) { bigIntOf("1000000000000000000").gcd(bigIntOf("999999999999999999")) }.toDecimalString() shouldBe "1"
        }
    }

    context("lcm") {
        test("lcm(4, 6) = 12") {
            with(ops) { bigIntOf(4).lcm(bigIntOf(6)) }.toDecimalString() shouldBe "12"
        }
        test("lcm(0, 5) = 0") {
            with(ops) { bigIntOf(0).lcm(bigIntOf(5)) }.toDecimalString() shouldBe "0"
        }
    }
})
