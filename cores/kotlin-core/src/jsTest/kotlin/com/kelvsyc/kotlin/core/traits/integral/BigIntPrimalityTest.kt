package com.kelvsyc.kotlin.core.traits.integral

import com.kelvsyc.kotlin.core.bigIntOf
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BigIntPrimalityTest : FunSpec({
    val ops = Primality.bigInt

    context("non-prime values") {
        test("0 is not prime") { with(ops) { bigIntOf(0).isPrime() } shouldBe false }
        test("1 is not prime") { with(ops) { bigIntOf(1).isPrime() } shouldBe false }
        test("-5 is not prime") { with(ops) { bigIntOf(-5).isPrime() } shouldBe false }
        test("4 is not prime") { with(ops) { bigIntOf(4).isPrime() } shouldBe false }
        test("100 is not prime") { with(ops) { bigIntOf(100).isPrime() } shouldBe false }
        test("2^62 is not prime (even, power of 2)") {
            with(ops) { bigIntOf("4611686018427387904").isPrime() } shouldBe false
        }
    }

    context("prime values") {
        test("2 is prime") { with(ops) { bigIntOf(2).isPrime() } shouldBe true }
        test("3 is prime") { with(ops) { bigIntOf(3).isPrime() } shouldBe true }
        test("5 is prime") { with(ops) { bigIntOf(5).isPrime() } shouldBe true }
        test("97 is prime") { with(ops) { bigIntOf(97).isPrime() } shouldBe true }
        test("Long.MAX_VALUE is not prime") {
            with(ops) { bigIntOf(Long.MAX_VALUE.toString()).isPrime() } shouldBe false
        }
        test("M61 Mersenne prime: 2^61 - 1 is prime") {
            with(ops) { bigIntOf("2305843009213693951").isPrime() } shouldBe true
        }
    }
})
