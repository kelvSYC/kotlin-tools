package com.kelvsyc.kotlin.core.traits

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class GcdTest : FunSpec({

    // ── Gcd.Companion.int (signed) ────────────────────────────────────────────

    context("Gcd.Companion.int") {
        val ops = Gcd.int

        context("gcd") {
            test("gcd(12, 8) = 4") { with(ops) { 12.gcd(8) } shouldBe 4 }
            test("gcd(8, 12) = 4 (commutative)") { with(ops) { 8.gcd(12) } shouldBe 4 }
            test("gcd(7, 3) = 1 (coprime)") { with(ops) { 7.gcd(3) } shouldBe 1 }
            test("gcd(x, x) = x") { with(ops) { 6.gcd(6) } shouldBe 6 }
            test("gcd(x, 0) = x") { with(ops) { 5.gcd(0) } shouldBe 5 }
            test("gcd(0, x) = x") { with(ops) { 0.gcd(5) } shouldBe 5 }
            test("gcd(0, 0) = 0") { with(ops) { 0.gcd(0) } shouldBe 0 }
            test("gcd(-12, 8) = 4 (negative first arg)") { with(ops) { (-12).gcd(8) } shouldBe 4 }
            test("gcd(12, -8) = 4 (negative second arg)") { with(ops) { 12.gcd(-8) } shouldBe 4 }
            test("gcd(-12, -8) = 4 (both negative)") { with(ops) { (-12).gcd(-8) } shouldBe 4 }
            test("MIN_VALUE wraps (documented overflow)") {
                with(ops) { Int.MIN_VALUE.gcd(Int.MIN_VALUE) } shouldBe Int.MIN_VALUE
            }
        }

        context("lcm") {
            test("lcm(4, 6) = 12") { with(ops) { 4.lcm(6) } shouldBe 12 }
            test("lcm(6, 4) = 12 (commutative)") { with(ops) { 6.lcm(4) } shouldBe 12 }
            test("lcm(3, 5) = 15 (coprime)") { with(ops) { 3.lcm(5) } shouldBe 15 }
            test("lcm(x, x) = x") { with(ops) { 7.lcm(7) } shouldBe 7 }
            test("lcm(x, 0) = 0") { with(ops) { 5.lcm(0) } shouldBe 0 }
            test("lcm(0, x) = 0") { with(ops) { 0.lcm(5) } shouldBe 0 }
            test("lcm(0, 0) = 0") { with(ops) { 0.lcm(0) } shouldBe 0 }
            test("lcm(-4, 6) = 12 (negative first arg)") { with(ops) { (-4).lcm(6) } shouldBe 12 }
            test("lcm(4, -6) = 12 (negative second arg)") { with(ops) { 4.lcm(-6) } shouldBe 12 }
            test("lcm(-4, -6) = 12 (both negative)") { with(ops) { (-4).lcm(-6) } shouldBe 12 }
            test("invariant: lcm(a, b) == |a| / gcd(a, b) * |b|") {
                val a = 12; val b = 8
                val expected = with(ops) { a / a.gcd(b) * b }
                with(ops) { a.lcm(b) } shouldBe expected
            }
        }
    }

    // ── Gcd.Companion.long (signed) ───────────────────────────────────────────

    context("Gcd.Companion.long") {
        val ops = Gcd.long

        context("gcd") {
            test("gcd(12L, 8L) = 4L") { with(ops) { 12L.gcd(8L) } shouldBe 4L }
            test("gcd(8L, 12L) = 4L (commutative)") { with(ops) { 8L.gcd(12L) } shouldBe 4L }
            test("gcd(x, 0L) = x") { with(ops) { 5L.gcd(0L) } shouldBe 5L }
            test("gcd(0L, x) = x") { with(ops) { 0L.gcd(5L) } shouldBe 5L }
            test("gcd(0L, 0L) = 0L") { with(ops) { 0L.gcd(0L) } shouldBe 0L }
            test("gcd(-12L, 8L) = 4L") { with(ops) { (-12L).gcd(8L) } shouldBe 4L }
            test("gcd(12L, -8L) = 4L") { with(ops) { 12L.gcd(-8L) } shouldBe 4L }
            test("MIN_VALUE wraps (documented overflow)") {
                with(ops) { Long.MIN_VALUE.gcd(Long.MIN_VALUE) } shouldBe Long.MIN_VALUE
            }
        }

        context("lcm") {
            test("lcm(4L, 6L) = 12L") { with(ops) { 4L.lcm(6L) } shouldBe 12L }
            test("lcm(3L, 5L) = 15L (coprime)") { with(ops) { 3L.lcm(5L) } shouldBe 15L }
            test("lcm(x, 0L) = 0L") { with(ops) { 5L.lcm(0L) } shouldBe 0L }
            test("lcm(0L, x) = 0L") { with(ops) { 0L.lcm(5L) } shouldBe 0L }
            test("lcm(-4L, 6L) = 12L") { with(ops) { (-4L).lcm(6L) } shouldBe 12L }
            test("lcm(4L, -6L) = 12L") { with(ops) { 4L.lcm(-6L) } shouldBe 12L }
        }
    }

    // ── Gcd.Companion.uint (unsigned) ─────────────────────────────────────────

    context("Gcd.Companion.uint") {
        val ops = Gcd.uint

        context("gcd") {
            test("gcd(12u, 8u) = 4u") { with(ops) { 12u.gcd(8u) } shouldBe 4u }
            test("gcd(8u, 12u) = 4u (commutative)") { with(ops) { 8u.gcd(12u) } shouldBe 4u }
            test("gcd(7u, 3u) = 1u (coprime)") { with(ops) { 7u.gcd(3u) } shouldBe 1u }
            test("gcd(x, 0u) = x") { with(ops) { 5u.gcd(0u) } shouldBe 5u }
            test("gcd(0u, x) = x") { with(ops) { 0u.gcd(5u) } shouldBe 5u }
            test("gcd(0u, 0u) = 0u") { with(ops) { 0u.gcd(0u) } shouldBe 0u }
        }

        context("lcm") {
            test("lcm(4u, 6u) = 12u") { with(ops) { 4u.lcm(6u) } shouldBe 12u }
            test("lcm(3u, 5u) = 15u (coprime)") { with(ops) { 3u.lcm(5u) } shouldBe 15u }
            test("lcm(x, 0u) = 0u") { with(ops) { 5u.lcm(0u) } shouldBe 0u }
            test("lcm(0u, x) = 0u") { with(ops) { 0u.lcm(5u) } shouldBe 0u }
            test("invariant: lcm(a, b) == a / gcd(a, b) * b") {
                val a = 12u; val b = 8u
                val expected = with(ops) { a / a.gcd(b) * b }
                with(ops) { a.lcm(b) } shouldBe expected
            }
        }
    }

    // ── Gcd.Companion.ulong (unsigned) ────────────────────────────────────────

    context("Gcd.Companion.ulong") {
        val ops = Gcd.ulong

        context("gcd") {
            test("gcd(12uL, 8uL) = 4uL") { with(ops) { 12uL.gcd(8uL) } shouldBe 4uL }
            test("gcd(x, 0uL) = x") { with(ops) { 5uL.gcd(0uL) } shouldBe 5uL }
            test("gcd(0uL, 0uL) = 0uL") { with(ops) { 0uL.gcd(0uL) } shouldBe 0uL }
        }

        context("lcm") {
            test("lcm(4uL, 6uL) = 12uL") { with(ops) { 4uL.lcm(6uL) } shouldBe 12uL }
            test("lcm(x, 0uL) = 0uL") { with(ops) { 5uL.lcm(0uL) } shouldBe 0uL }
        }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("Gcd.int is stable") { Gcd.int shouldBe Gcd.int }
        test("Gcd.long is stable") { Gcd.long shouldBe Gcd.long }
        test("Gcd.uint is stable") { Gcd.uint shouldBe Gcd.uint }
        test("Gcd.ulong is stable") { Gcd.ulong shouldBe Gcd.ulong }
        test("Gcd.int and Gcd.long are distinct") { Gcd.int shouldNotBe Gcd.long }
        test("Gcd.int and Gcd.uint are distinct") { (Gcd.int as Any) shouldNotBe (Gcd.uint as Any) }
    }
})
