package com.kelvsyc.kotlin.core.traits.integral

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigInteger

class BigIntegerPrimalityTest : FunSpec({
    context("identity conversion (BigInteger -> BigInteger)") {
        val ops = BigIntegerPrimality<BigInteger> { it }

        test("0 is not prime") { with(ops) { BigInteger.ZERO.isPrime() } shouldBe false }
        test("1 is not prime") { with(ops) { BigInteger.ONE.isPrime() } shouldBe false }
        test("2 is prime") { with(ops) { BigInteger.TWO.isPrime() } shouldBe true }
        test("97 is prime") { with(ops) { BigInteger.valueOf(97L).isPrime() } shouldBe true }
        test("100 is not prime") { with(ops) { BigInteger.valueOf(100L).isPrime() } shouldBe false }
        test("negative input is not prime") {
            with(ops) { BigInteger.valueOf(-7L).isPrime() } shouldBe false
        }
        test("Mersenne prime 2^521 - 1 is prime") {
            val m521 = BigInteger.ONE.shiftLeft(521) - BigInteger.ONE
            with(ops) { m521.isPrime() } shouldBe true
        }
    }

    context("Long-backed conversion") {
        val ops = BigIntegerPrimality<Long> { BigInteger.valueOf(it) }

        test("0L is not prime") { with(ops) { 0L.isPrime() } shouldBe false }
        test("1L is not prime") { with(ops) { 1L.isPrime() } shouldBe false }
        test("17L is prime") { with(ops) { 17L.isPrime() } shouldBe true }
        test("21L is not prime") { with(ops) { 21L.isPrime() } shouldBe false }
        test("negative Long is not prime") { with(ops) { (-13L).isPrime() } shouldBe false }
        test("large prime within Long range: 999999999999999877") {
            with(ops) { 999999999999999877L.isPrime() } shouldBe true
        }
    }

    context("custom certainty") {
        test("certainty=1 still classifies small primes correctly") {
            val ops = BigIntegerPrimality<BigInteger>(certainty = 1) { it }
            with(ops) { BigInteger.valueOf(31L).isPrime() } shouldBe true
            with(ops) { BigInteger.valueOf(32L).isPrime() } shouldBe false
        }
    }
})
