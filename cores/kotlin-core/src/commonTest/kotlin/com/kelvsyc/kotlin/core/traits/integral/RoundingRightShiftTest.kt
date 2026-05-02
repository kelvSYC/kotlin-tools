package com.kelvsyc.kotlin.core.traits.integral

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class RoundingRightShiftTest : FunSpec({

    // ── RoundingRightShift.Companion.int ──────────────────────────────────────
    //
    // All five cases are exercised here. Lighter tests are used for other types.

    context("RoundingRightShift.Companion.int") {
        val ops = RoundingRightShift.int

        test("shift by 0 is identity") { with(ops) { 7.roundingRightShift(0) } shouldBe 7 }

        context("exact (no fractional part)") {
            test("12 rrs 2 = 3 (12/4 exact)") { with(ops) { 12.roundingRightShift(2) } shouldBe 3 }
            test("8 rrs 3 = 1 (8/8 exact)") { with(ops) { 8.roundingRightShift(3) } shouldBe 1 }
            test("0 rrs 1 = 0") { with(ops) { 0.roundingRightShift(1) } shouldBe 0 }
        }

        context("below halfway (round bit = 0): truncate") {
            test("13 rrs 2 = 3 (13/4 = 3.25, below halfway)") { with(ops) { 13.roundingRightShift(2) } shouldBe 3 }
            test("9 rrs 2 = 2 (9/4 = 2.25, below halfway)") { with(ops) { 9.roundingRightShift(2) } shouldBe 2 }
        }

        context("above halfway (round bit = 1, sticky ≠ 0): round up") {
            test("15 rrs 2 = 4 (15/4 = 3.75, above halfway)") { with(ops) { 15.roundingRightShift(2) } shouldBe 4 }
            test("11 rrs 2 = 3 (11/4 = 2.75, above halfway)") { with(ops) { 11.roundingRightShift(2) } shouldBe 3 }
            test("7 rrs 3 = 1 (7/8 = 0.875, above halfway)") { with(ops) { 7.roundingRightShift(3) } shouldBe 1 }
        }

        context("exactly halfway (round bit = 1, sticky = 0): round to even") {
            test("14 rrs 2 = 4 (14/4 = 3.5, q=3 odd → round up to 4)") { with(ops) { 14.roundingRightShift(2) } shouldBe 4 }
            test("18 rrs 2 = 4 (18/4 = 4.5, q=4 even → keep 4)") { with(ops) { 18.roundingRightShift(2) } shouldBe 4 }
            test("3 rrs 1 = 2 (3/2 = 1.5, q=1 odd → round up to 2)") { with(ops) { 3.roundingRightShift(1) } shouldBe 2 }
            test("5 rrs 1 = 2 (5/2 = 2.5, q=2 even → keep 2)") { with(ops) { 5.roundingRightShift(1) } shouldBe 2 }
            test("2 rrs 2 = 0 (2/4 = 0.5, q=0 even → keep 0)") { with(ops) { 2.roundingRightShift(2) } shouldBe 0 }
            test("6 rrs 2 = 2 (6/4 = 1.5, q=1 odd → round up to 2)") { with(ops) { 6.roundingRightShift(2) } shouldBe 2 }
        }

    }

    // ── RoundingRightShift.Companion.long ─────────────────────────────────────

    context("RoundingRightShift.Companion.long") {
        val ops = RoundingRightShift.long

        test("shift by 0 is identity") { with(ops) { 7L.roundingRightShift(0) } shouldBe 7L }
        test("exact: 12L rrs 2 = 3L") { with(ops) { 12L.roundingRightShift(2) } shouldBe 3L }
        test("below halfway: 13L rrs 2 = 3L") { with(ops) { 13L.roundingRightShift(2) } shouldBe 3L }
        test("above halfway: 15L rrs 2 = 4L") { with(ops) { 15L.roundingRightShift(2) } shouldBe 4L }
        test("half-even, odd quotient: 14L rrs 2 = 4L") { with(ops) { 14L.roundingRightShift(2) } shouldBe 4L }
        test("half-even, even quotient: 18L rrs 2 = 4L") { with(ops) { 18L.roundingRightShift(2) } shouldBe 4L }
    }

    // ── RoundingRightShift.Companion.byte ─────────────────────────────────────

    context("RoundingRightShift.Companion.byte") {
        val ops = RoundingRightShift.byte

        test("shift by 0 is identity") { with(ops) { 7.toByte().roundingRightShift(0) } shouldBe 7.toByte() }
        test("exact") { with(ops) { 12.toByte().roundingRightShift(2) } shouldBe 3.toByte() }
        test("below halfway") { with(ops) { 13.toByte().roundingRightShift(2) } shouldBe 3.toByte() }
        test("above halfway") { with(ops) { 15.toByte().roundingRightShift(2) } shouldBe 4.toByte() }
        test("half-even, odd quotient: 14 rrs 2 = 4") { with(ops) { 14.toByte().roundingRightShift(2) } shouldBe 4.toByte() }
        test("half-even, even quotient: 18 rrs 2 = 4 (keep)") { with(ops) { 18.toByte().roundingRightShift(2) } shouldBe 4.toByte() }
    }

    // ── RoundingRightShift.Companion.short ────────────────────────────────────

    context("RoundingRightShift.Companion.short") {
        val ops = RoundingRightShift.short

        test("shift by 0 is identity") { with(ops) { 7.toShort().roundingRightShift(0) } shouldBe 7.toShort() }
        test("above halfway") { with(ops) { 15.toShort().roundingRightShift(2) } shouldBe 4.toShort() }
        test("half-even, odd quotient") { with(ops) { 14.toShort().roundingRightShift(2) } shouldBe 4.toShort() }
        test("half-even, even quotient") { with(ops) { 18.toShort().roundingRightShift(2) } shouldBe 4.toShort() }
    }

    // ── RoundingRightShift.Companion.uint ─────────────────────────────────────

    context("RoundingRightShift.Companion.uint") {
        val ops = RoundingRightShift.uint

        test("shift by 0 is identity") { with(ops) { 7u.roundingRightShift(0) } shouldBe 7u }
        test("exact") { with(ops) { 12u.roundingRightShift(2) } shouldBe 3u }
        test("below halfway") { with(ops) { 13u.roundingRightShift(2) } shouldBe 3u }
        test("above halfway") { with(ops) { 15u.roundingRightShift(2) } shouldBe 4u }
        test("half-even, odd quotient: 14u rrs 2 = 4u") { with(ops) { 14u.roundingRightShift(2) } shouldBe 4u }
        test("half-even, even quotient: 18u rrs 2 = 4u (keep)") { with(ops) { 18u.roundingRightShift(2) } shouldBe 4u }
    }

    // ── RoundingRightShift.Companion.ulong ────────────────────────────────────

    context("RoundingRightShift.Companion.ulong") {
        val ops = RoundingRightShift.ulong

        test("shift by 0 is identity") { with(ops) { 7uL.roundingRightShift(0) } shouldBe 7uL }
        test("above halfway") { with(ops) { 15uL.roundingRightShift(2) } shouldBe 4uL }
        test("half-even, odd quotient") { with(ops) { 14uL.roundingRightShift(2) } shouldBe 4uL }
        test("half-even, even quotient") { with(ops) { 18uL.roundingRightShift(2) } shouldBe 4uL }
    }

    // ── RoundingRightShift.Companion.ubyte ────────────────────────────────────

    context("RoundingRightShift.Companion.ubyte") {
        val ops = RoundingRightShift.ubyte

        test("shift by 0 is identity") { with(ops) { 7u.toUByte().roundingRightShift(0) } shouldBe 7u.toUByte() }
        test("exact") { with(ops) { 12u.toUByte().roundingRightShift(2) } shouldBe 3u.toUByte() }
        test("above halfway") { with(ops) { 15u.toUByte().roundingRightShift(2) } shouldBe 4u.toUByte() }
        test("half-even, odd quotient") { with(ops) { 14u.toUByte().roundingRightShift(2) } shouldBe 4u.toUByte() }
        test("half-even, even quotient") { with(ops) { 18u.toUByte().roundingRightShift(2) } shouldBe 4u.toUByte() }
    }

    // ── RoundingRightShift.Companion.ushort ───────────────────────────────────

    context("RoundingRightShift.Companion.ushort") {
        val ops = RoundingRightShift.ushort

        test("shift by 0 is identity") { with(ops) { 7u.toUShort().roundingRightShift(0) } shouldBe 7u.toUShort() }
        test("half-even, odd quotient") { with(ops) { 14u.toUShort().roundingRightShift(2) } shouldBe 4u.toUShort() }
        test("half-even, even quotient") { with(ops) { 18u.toUShort().roundingRightShift(2) } shouldBe 4u.toUShort() }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("byte is stable") { RoundingRightShift.byte shouldBeSameInstanceAs RoundingRightShift.byte }
        test("short is stable") { RoundingRightShift.short shouldBeSameInstanceAs RoundingRightShift.short }
        test("int is stable") { RoundingRightShift.int shouldBeSameInstanceAs RoundingRightShift.int }
        test("long is stable") { RoundingRightShift.long shouldBeSameInstanceAs RoundingRightShift.long }
        test("ubyte is stable") { RoundingRightShift.ubyte shouldBeSameInstanceAs RoundingRightShift.ubyte }
        test("ushort is stable") { RoundingRightShift.ushort shouldBeSameInstanceAs RoundingRightShift.ushort }
        test("uint is stable") { RoundingRightShift.uint shouldBeSameInstanceAs RoundingRightShift.uint }
        test("ulong is stable") { RoundingRightShift.ulong shouldBeSameInstanceAs RoundingRightShift.ulong }
        test("int and long are distinct") { RoundingRightShift.int shouldNotBe RoundingRightShift.long }
        test("byte and ubyte are distinct") { RoundingRightShift.byte shouldNotBe RoundingRightShift.ubyte }
    }
})
