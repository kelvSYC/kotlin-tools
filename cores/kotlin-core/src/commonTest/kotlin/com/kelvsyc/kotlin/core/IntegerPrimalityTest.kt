package com.kelvsyc.kotlin.core

import com.kelvsyc.kotlin.core.traits.integral.Primality
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.integral.long
import com.kelvsyc.kotlin.core.traits.integral.uint
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class IntegerPrimalityTest : FunSpec({

    // ── Primality.int ─────────────────────────────────────────────────────────

    context("Primality.int") {
        val ops = Primality.int

        test("negative is not prime") { with(ops) { (-1).isPrime() } shouldBe false }
        test("0 is not prime") { with(ops) { 0.isPrime() } shouldBe false }
        test("1 is not prime") { with(ops) { 1.isPrime() } shouldBe false }
        test("2 is prime") { with(ops) { 2.isPrime() } shouldBe true }
        test("3 is prime") { with(ops) { 3.isPrime() } shouldBe true }
        test("4 is not prime") { with(ops) { 4.isPrime() } shouldBe false }
        test("5 is prime") { with(ops) { 5.isPrime() } shouldBe true }
        test("6 is not prime") { with(ops) { 6.isPrime() } shouldBe false }
        test("97 is prime") { with(ops) { 97.isPrime() } shouldBe true }
        test("100 is not prime") { with(ops) { 100.isPrime() } shouldBe false }
        test("large prime: 999983") { with(ops) { 999983.isPrime() } shouldBe true }
        test("large composite: 999983 * 2") { with(ops) { (999983 * 2).isPrime() } shouldBe false }
        test("Int.MAX_VALUE is prime (Mersenne prime 2^31 - 1)") { with(ops) { Int.MAX_VALUE.isPrime() } shouldBe true }
    }

    // ── Primality.long ────────────────────────────────────────────────────────

    context("Primality.long") {
        val ops = Primality.long

        test("negative is not prime") { with(ops) { (-1L).isPrime() } shouldBe false }
        test("0L is not prime") { with(ops) { 0L.isPrime() } shouldBe false }
        test("2L is prime") { with(ops) { 2L.isPrime() } shouldBe true }
        test("large prime: 999999999999999877") { with(ops) { 999999999999999877L.isPrime() } shouldBe true }
        test("Long.MAX_VALUE is not prime") { with(ops) { Long.MAX_VALUE.isPrime() } shouldBe false }
    }

    // ── Primality.uint ────────────────────────────────────────────────────────

    context("Primality.uint") {
        val ops = Primality.uint

        test("0u is not prime") { with(ops) { 0u.isPrime() } shouldBe false }
        test("1u is not prime") { with(ops) { 1u.isPrime() } shouldBe false }
        test("2u is prime") { with(ops) { 2u.isPrime() } shouldBe true }
        test("97u is prime") { with(ops) { 97u.isPrime() } shouldBe true }
        test("large prime: 4294967291u (largest prime below 2^32)") {
            with(ops) { 4294967291u.isPrime() } shouldBe true
        }
        test("UInt.MAX_VALUE is not prime") { with(ops) { UInt.MAX_VALUE.isPrime() } shouldBe false }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("Primality.int is stable") { Primality.int shouldBe Primality.int }
        test("Primality.long is stable") { Primality.long shouldBe Primality.long }
        test("Primality.uint is stable") { Primality.uint shouldBe Primality.uint }
        test("Primality.int and Primality.long are distinct") { Primality.int shouldNotBe Primality.long }
        test("Primality.int and Primality.uint are distinct") {
            (Primality.int as Any) shouldNotBe (Primality.uint as Any)
        }
    }

    // ── Convenience extensions ────────────────────────────────────────────────

    context("Int.isPrime") {
        test("2 is prime") { 2.isPrime shouldBe true }
        test("4 is not prime") { 4.isPrime shouldBe false }
        test("Int.MAX_VALUE is prime") { Int.MAX_VALUE.isPrime shouldBe true }
    }

    context("Long.isPrime") {
        test("2L is prime") { 2L.isPrime shouldBe true }
        test("4L is not prime") { 4L.isPrime shouldBe false }
        test("Long.MAX_VALUE is not prime") { Long.MAX_VALUE.isPrime shouldBe false }
    }
})
