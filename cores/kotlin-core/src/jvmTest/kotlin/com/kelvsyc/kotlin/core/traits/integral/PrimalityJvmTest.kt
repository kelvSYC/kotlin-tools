package com.kelvsyc.kotlin.core.traits.integral

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigInteger

class PrimalityJvmTest : FunSpec({

    // ── Primality.ulong ───────────────────────────────────────────────────────

    context("Primality.ulong") {
        val ops = Primality.ulong

        test("0uL is not prime") { with(ops) { 0uL.isPrime() } shouldBe false }
        test("1uL is not prime") { with(ops) { 1uL.isPrime() } shouldBe false }
        test("2uL is prime") { with(ops) { 2uL.isPrime() } shouldBe true }
        test("97uL is prime") { with(ops) { 97uL.isPrime() } shouldBe true }

        // Values within Long range — delegates to the faster ULong Miller-Rabin path.
        test("Long.MAX_VALUE is not prime") {
            with(ops) { Long.MAX_VALUE.toULong().isPrime() } shouldBe false
        }
        test("large prime within Long range: 999999999999999877") {
            with(ops) { 999999999999999877uL.isPrime() } shouldBe true
        }

        // Values above Long.MAX_VALUE — exercises the BigInteger path.
        // 18446744073709551557 is the largest prime <= ULong.MAX_VALUE.
        test("largest prime below ULong.MAX_VALUE: 18446744073709551557") {
            with(ops) { 18446744073709551557uL.isPrime() } shouldBe true
        }
        test("ULong.MAX_VALUE is not prime (= 3 × 6148914691236517205 + ...)") {
            with(ops) { ULong.MAX_VALUE.isPrime() } shouldBe false
        }
        // A large composite above Long.MAX_VALUE
        test("18446744073709551615 is not prime (ULong.MAX_VALUE = 3 × ...)") {
            with(ops) { 18446744073709551615uL.isPrime() } shouldBe false
        }
    }

    // ── Primality.bigInteger ──────────────────────────────────────────────────

    context("Primality.bigInteger") {
        val ops = Primality.bigInteger

        test("BigInteger.ZERO is not prime") { with(ops) { BigInteger.ZERO.isPrime() } shouldBe false }
        test("BigInteger.ONE is not prime") { with(ops) { BigInteger.ONE.isPrime() } shouldBe false }
        test("BigInteger.TWO is prime") { with(ops) { BigInteger.TWO.isPrime() } shouldBe true }
        test("97 is prime") { with(ops) { BigInteger.valueOf(97L).isPrime() } shouldBe true }
        test("100 is not prime") { with(ops) { BigInteger.valueOf(100L).isPrime() } shouldBe false }

        // Large known prime: 2^521 - 1 (Mersenne prime M521)
        test("Mersenne prime 2^521 - 1 is prime") {
            val m521 = BigInteger.ONE.shiftLeft(521) - BigInteger.ONE
            with(ops) { m521.isPrime() } shouldBe true
        }
        // Large known composite: 2^521 (a power of two)
        test("2^521 is not prime") {
            with(ops) { BigInteger.ONE.shiftLeft(521).isPrime() } shouldBe false
        }
        // Negative value
        test("negative BigInteger is not prime") {
            with(ops) { BigInteger.valueOf(-7L).isPrime() } shouldBe false
        }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("Primality.ulong is stable") { Primality.ulong shouldBe Primality.ulong }
        test("Primality.bigInteger is stable") { Primality.bigInteger shouldBe Primality.bigInteger }
        test("Primality.ulong and Primality.long are distinct") {
            Primality.ulong shouldNotBe Primality.long
        }
        test("Primality.bigInteger and Primality.long are distinct") {
            (Primality.bigInteger as Any) shouldNotBe Primality.long
        }
    }
})
