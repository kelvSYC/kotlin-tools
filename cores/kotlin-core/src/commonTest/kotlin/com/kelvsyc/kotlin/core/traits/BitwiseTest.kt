package com.kelvsyc.kotlin.core.traits

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class BitwiseTest : FunSpec({

    // ── Bitwise.Companion.int ─────────────────────────────────────────────────

    context("Bitwise.Companion.int") {
        val ops = Bitwise.int

        context("bitwiseAnd") {
            test("0b101 and 0b011 = 0b001") { with(ops) { 5.bitwiseAnd(3) } shouldBe 1 }
            test("x and 0 = 0") { with(ops) { 7.bitwiseAnd(0) } shouldBe 0 }
            test("x and -1 = x") { with(ops) { 7.bitwiseAnd(-1) } shouldBe 7 }
            test("x and x = x") { with(ops) { 42.bitwiseAnd(42) } shouldBe 42 }
        }

        context("bitwiseOr") {
            test("0b101 or 0b011 = 0b111") { with(ops) { 5.bitwiseOr(3) } shouldBe 7 }
            test("x or 0 = x") { with(ops) { 7.bitwiseOr(0) } shouldBe 7 }
            test("x or -1 = -1") { with(ops) { 7.bitwiseOr(-1) } shouldBe -1 }
            test("x or x = x") { with(ops) { 42.bitwiseOr(42) } shouldBe 42 }
        }

        context("bitwiseXor") {
            test("0b101 xor 0b011 = 0b110") { with(ops) { 5.bitwiseXor(3) } shouldBe 6 }
            test("x xor 0 = x") { with(ops) { 7.bitwiseXor(0) } shouldBe 7 }
            test("x xor x = 0") { with(ops) { 42.bitwiseXor(42) } shouldBe 0 }
            test("x xor -1 = x.invert()") { with(ops) { 5.bitwiseXor(-1) } shouldBe 5.inv() }
        }

        context("invert") {
            test("invert 0 = -1") { with(ops) { 0.invert() } shouldBe -1 }
            test("invert -1 = 0") { with(ops) { (-1).invert() } shouldBe 0 }
            test("double invert is identity") { with(ops) { 42.invert().invert() } shouldBe 42 }
            test("invert 5 = -6") { with(ops) { 5.invert() } shouldBe -6 }
        }
    }

    // ── Bitwise.Companion.long ────────────────────────────────────────────────

    context("Bitwise.Companion.long") {
        val ops = Bitwise.long

        context("bitwiseAnd") {
            test("5L and 3L = 1L") { with(ops) { 5L.bitwiseAnd(3L) } shouldBe 1L }
            test("x and -1L = x") { with(ops) { 7L.bitwiseAnd(-1L) } shouldBe 7L }
        }

        context("bitwiseOr") {
            test("5L or 3L = 7L") { with(ops) { 5L.bitwiseOr(3L) } shouldBe 7L }
            test("x or 0L = x") { with(ops) { 7L.bitwiseOr(0L) } shouldBe 7L }
        }

        context("bitwiseXor") {
            test("5L xor 3L = 6L") { with(ops) { 5L.bitwiseXor(3L) } shouldBe 6L }
            test("x xor x = 0L") { with(ops) { 42L.bitwiseXor(42L) } shouldBe 0L }
        }

        context("invert") {
            test("invert 0L = -1L") { with(ops) { 0L.invert() } shouldBe -1L }
            test("double invert is identity") { with(ops) { 42L.invert().invert() } shouldBe 42L }
        }
    }

    // ── Bitwise.Companion.uint ────────────────────────────────────────────────

    context("Bitwise.Companion.uint") {
        val ops = Bitwise.uint

        context("bitwiseAnd") {
            test("5u and 3u = 1u") { with(ops) { 5u.bitwiseAnd(3u) } shouldBe 1u }
            test("x and MAX = x") { with(ops) { 7u.bitwiseAnd(UInt.MAX_VALUE) } shouldBe 7u }
        }

        context("bitwiseOr") {
            test("5u or 3u = 7u") { with(ops) { 5u.bitwiseOr(3u) } shouldBe 7u }
            test("x or 0u = x") { with(ops) { 7u.bitwiseOr(0u) } shouldBe 7u }
        }

        context("bitwiseXor") {
            test("5u xor 3u = 6u") { with(ops) { 5u.bitwiseXor(3u) } shouldBe 6u }
            test("x xor x = 0u") { with(ops) { 42u.bitwiseXor(42u) } shouldBe 0u }
        }

        context("invert") {
            test("invert 0u = MAX") { with(ops) { 0u.invert() } shouldBe UInt.MAX_VALUE }
            test("invert MAX = 0u") { with(ops) { UInt.MAX_VALUE.invert() } shouldBe 0u }
            test("double invert is identity") { with(ops) { 42u.invert().invert() } shouldBe 42u }
        }
    }

    // ── Bitwise.Companion.ulong ───────────────────────────────────────────────

    context("Bitwise.Companion.ulong") {
        val ops = Bitwise.ulong

        context("bitwiseAnd") {
            test("5uL and 3uL = 1uL") { with(ops) { 5uL.bitwiseAnd(3uL) } shouldBe 1uL }
        }

        context("invert") {
            test("invert 0uL = MAX") { with(ops) { 0uL.invert() } shouldBe ULong.MAX_VALUE }
            test("double invert is identity") { with(ops) { 42uL.invert().invert() } shouldBe 42uL }
        }
    }

    // ── Bitwise.Companion.ushort ──────────────────────────────────────────────

    context("Bitwise.Companion.ushort") {
        val ops = Bitwise.ushort

        context("bitwiseAnd") {
            test("5u and 3u = 1u") { with(ops) { 5u.toUShort().bitwiseAnd(3u.toUShort()) } shouldBe 1u.toUShort() }
        }

        context("invert") {
            test("invert 0u = MAX") { with(ops) { 0u.toUShort().invert() } shouldBe UShort.MAX_VALUE }
            test("double invert is identity") { with(ops) { 42u.toUShort().invert().invert() } shouldBe 42u.toUShort() }
        }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("Bitwise.int is stable") { Bitwise.int shouldBe Bitwise.int }
        test("Bitwise.long is stable") { Bitwise.long shouldBe Bitwise.long }
        test("Bitwise.uint is stable") { Bitwise.uint shouldBe Bitwise.uint }
        test("Bitwise.ulong is stable") { Bitwise.ulong shouldBe Bitwise.ulong }
        test("Bitwise.ushort is stable") { Bitwise.ushort shouldBe Bitwise.ushort }
        test("int and long are distinct") { Bitwise.int shouldNotBe Bitwise.long }
        test("int and uint are distinct") { Bitwise.int shouldNotBe Bitwise.uint }
    }
})
