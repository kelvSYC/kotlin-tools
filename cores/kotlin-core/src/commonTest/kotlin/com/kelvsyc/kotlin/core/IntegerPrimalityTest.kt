package com.kelvsyc.kotlin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IntegerPrimalityTest : FunSpec({

    // ── Int.isPrime ───────────────────────────────────────────────────────────

    context("Int.isPrime") {
        test("negative is not prime") { (-1).isPrime shouldBe false }
        test("0 is not prime") { 0.isPrime shouldBe false }
        test("1 is not prime") { 1.isPrime shouldBe false }
        test("2 is prime") { 2.isPrime shouldBe true }
        test("3 is prime") { 3.isPrime shouldBe true }
        test("4 is not prime") { 4.isPrime shouldBe false }
        test("5 is prime") { 5.isPrime shouldBe true }
        test("6 is not prime") { 6.isPrime shouldBe false }
        test("7 is prime") { 7.isPrime shouldBe true }
        test("9 is not prime (3^2)") { 9.isPrime shouldBe false }
        test("97 is prime") { 97.isPrime shouldBe true }
        test("100 is not prime") { 100.isPrime shouldBe false }
        test("large prime: 999983") { 999983.isPrime shouldBe true }
        test("large composite: 999979*2") { (999979 * 2).isPrime shouldBe false }
        test("Int.MAX_VALUE is prime (Mersenne prime 2^31 - 1)") { Int.MAX_VALUE.isPrime shouldBe true }
    }

    // ── Long.isPrime ──────────────────────────────────────────────────────────

    context("Long.isPrime") {
        test("negative is not prime") { (-1L).isPrime shouldBe false }
        test("0L is not prime") { 0L.isPrime shouldBe false }
        test("1L is not prime") { 1L.isPrime shouldBe false }
        test("2L is prime") { 2L.isPrime shouldBe true }
        test("3L is prime") { 3L.isPrime shouldBe true }
        test("4L is not prime") { 4L.isPrime shouldBe false }
        test("large prime: 999999999999999877") { 999999999999999877L.isPrime shouldBe true }
        test("Long.MAX_VALUE is not prime") { Long.MAX_VALUE.isPrime shouldBe false }
    }
})
