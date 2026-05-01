package com.kelvsyc.kotlin.core.traits

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class BitShiftTest : FunSpec({

    // ── BitShift ──────────────────────────────────────────────────────────────

    context("BitShift.Companion.int") {
        val ops = BitShift.int

        context("leftShift") {
            test("1 shl 3 = 8") { with(ops) { 1.leftShift(3) } shouldBe 8 }
            test("x shl 0 = x") { with(ops) { 42.leftShift(0) } shouldBe 42 }
            test("-1 shl 1 = -2") { with(ops) { (-1).leftShift(1) } shouldBe -2 }
        }

        context("logicalRightShift") {
            test("8 ushr 3 = 1") { with(ops) { 8.logicalRightShift(3) } shouldBe 1 }
            test("x ushr 0 = x") { with(ops) { 42.logicalRightShift(0) } shouldBe 42 }
            test("-1 ushr 1 = Int.MAX_VALUE") { with(ops) { (-1).logicalRightShift(1) } shouldBe Int.MAX_VALUE }
            test("negative value ushr fills with 0, not 1") { with(ops) { Int.MIN_VALUE.logicalRightShift(1) } shouldBe 0x40000000 }
        }
    }

    context("BitShift.Companion.long") {
        val ops = BitShift.long

        context("leftShift") {
            test("1L shl 3 = 8L") { with(ops) { 1L.leftShift(3) } shouldBe 8L }
            test("x shl 0 = x") { with(ops) { 42L.leftShift(0) } shouldBe 42L }
        }

        context("logicalRightShift") {
            test("8L ushr 3 = 1L") { with(ops) { 8L.logicalRightShift(3) } shouldBe 1L }
            test("-1L ushr 1 = Long.MAX_VALUE") { with(ops) { (-1L).logicalRightShift(1) } shouldBe Long.MAX_VALUE }
        }
    }

    context("BitShift.Companion.uint") {
        val ops = BitShift.uint

        context("leftShift") {
            test("1u shl 3 = 8u") { with(ops) { 1u.leftShift(3) } shouldBe 8u }
        }

        context("logicalRightShift") {
            test("8u ushr 3 = 1u") { with(ops) { 8u.logicalRightShift(3) } shouldBe 1u }
            test("msb ushr 1 = 0x40000000u") { with(ops) { 0x80000000u.logicalRightShift(1) } shouldBe 0x40000000u }
        }
    }

    context("BitShift.Companion.ulong") {
        val ops = BitShift.ulong

        context("logicalRightShift") {
            test("msb ushr 1 = 0x4000000000000000uL") {
                with(ops) { 0x8000000000000000uL.logicalRightShift(1) } shouldBe 0x4000000000000000uL
            }
        }
    }

    context("BitShift.Companion.ushort") {
        val ops = BitShift.ushort

        context("leftShift") {
            test("1u shl 3 = 8u") { with(ops) { 1u.toUShort().leftShift(3) } shouldBe 8u.toUShort() }
            test("msb shl 1 wraps to 0") { with(ops) { 0x8000u.toUShort().leftShift(1) } shouldBe 0u.toUShort() }
        }

        context("logicalRightShift") {
            test("msb ushr 1 = 0x4000u") { with(ops) { 0x8000u.toUShort().logicalRightShift(1) } shouldBe 0x4000u.toUShort() }
        }
    }

    context("BitShift singleton identity") {
        test("BitShift.int is stable") { BitShift.int shouldBeSameInstanceAs BitShift.int }
        test("BitShift.long is stable") { BitShift.long shouldBeSameInstanceAs BitShift.long }
        test("BitShift.int and long are distinct") { BitShift.int shouldNotBe BitShift.long }
    }

    // ── ArithmeticRightShift ──────────────────────────────────────────────────

    context("ArithmeticRightShift.Companion.int") {
        val ops = ArithmeticRightShift.int

        test("positive value: fills with 0") { with(ops) { 8.arithmeticRightShift(3) } shouldBe 1 }
        test("x shr 0 = x") { with(ops) { 42.arithmeticRightShift(0) } shouldBe 42 }
        test("-1 shr 1 = -1 (fills with sign bit)") { with(ops) { (-1).arithmeticRightShift(1) } shouldBe -1 }
        test("-2 shr 1 = -1") { with(ops) { (-2).arithmeticRightShift(1) } shouldBe -1 }
        test("Int.MIN_VALUE shr 1 = -0x40000000 - 1 + 1") {
            with(ops) { Int.MIN_VALUE.arithmeticRightShift(1) } shouldBe (Int.MIN_VALUE / 2)
        }
        test("-8 shr 3 = -1") { with(ops) { (-8).arithmeticRightShift(3) } shouldBe -1 }
        test("-7 shr 3 = -1") { with(ops) { (-7).arithmeticRightShift(3) } shouldBe -1 }
        test("sign bit propagates fully: -1 shr 31 = -1") { with(ops) { (-1).arithmeticRightShift(31) } shouldBe -1 }
    }

    context("ArithmeticRightShift.Companion.long") {
        val ops = ArithmeticRightShift.long

        test("-1L shr 1 = -1L") { with(ops) { (-1L).arithmeticRightShift(1) } shouldBe -1L }
        test("positive value fills with 0") { with(ops) { 16L.arithmeticRightShift(4) } shouldBe 1L }
        test("sign bit propagates fully") { with(ops) { (-1L).arithmeticRightShift(63) } shouldBe -1L }
    }

    context("ArithmeticRightShift singleton identity") {
        test("ArithmeticRightShift.int is stable") { ArithmeticRightShift.int shouldBeSameInstanceAs ArithmeticRightShift.int }
        test("ArithmeticRightShift.long is stable") { ArithmeticRightShift.long shouldBeSameInstanceAs ArithmeticRightShift.long }
        test("int and long are distinct") { ArithmeticRightShift.int shouldNotBe ArithmeticRightShift.long }
    }

    // ── StickyRightShift ──────────────────────────────────────────────────────

    context("StickyRightShift.Companion.int") {
        val ops = StickyRightShift.int

        test("shift by 0 is identity") { with(ops) { 7.stickyRightShift(0) } shouldBe 7 }
        test("no bits lost: same as logical right shift") { with(ops) { 8.stickyRightShift(3) } shouldBe 1 }
        test("lost bits set sticky lsb") { with(ops) { 7.stickyRightShift(3) } shouldBe 1 }
        test("all bits shifted out leaves lsb=1") { with(ops) { 1.stickyRightShift(1) } shouldBe 1 }
        test("allSet shifted by 1 is allSet ushr 1 or 1") {
            with(ops) { (-1).stickyRightShift(1) } shouldBe (Int.MAX_VALUE or 1)
        }
        test("negative value: lost bit sticks") { with(ops) { Int.MIN_VALUE.stickyRightShift(1) } shouldBe 0x40000000 }
        test("0b1100 srs 2 = 0b11 (no lost bits)") { with(ops) { 0b1100.stickyRightShift(2) } shouldBe 0b11 }
        test("0b1101 srs 2 = 0b11 or 1 = 0b11 (lost 1 bit, already sticky)") {
            with(ops) { 0b1101.stickyRightShift(2) } shouldBe (0b11 or 1)
        }
        test("0b1110 srs 2 = 0b11 or 1 (lost set bits)") { with(ops) { 0b1110.stickyRightShift(2) } shouldBe (0b11 or 1) }
    }

    context("StickyRightShift.Companion.long") {
        val ops = StickyRightShift.long

        test("shift by 0 is identity") { with(ops) { 42L.stickyRightShift(0) } shouldBe 42L }
        test("no bits lost: clean shift") { with(ops) { 16L.stickyRightShift(4) } shouldBe 1L }
        test("lost bits set sticky lsb") { with(ops) { 3L.stickyRightShift(2) } shouldBe 1L }
    }

    context("StickyRightShift.Companion.uint") {
        val ops = StickyRightShift.uint

        test("shift by 0 is identity") { with(ops) { 7u.stickyRightShift(0) } shouldBe 7u }
        test("no bits lost") { with(ops) { 8u.stickyRightShift(3) } shouldBe 1u }
        test("lost bits set sticky lsb") { with(ops) { 7u.stickyRightShift(3) } shouldBe 1u }
        test("all bits shifted: result is 1") { with(ops) { 1u.stickyRightShift(1) } shouldBe 1u }
    }

    context("StickyRightShift.Companion.ulong") {
        val ops = StickyRightShift.ulong

        test("shift by 0 is identity") { with(ops) { 42uL.stickyRightShift(0) } shouldBe 42uL }
        test("lost bits set sticky lsb") { with(ops) { 3uL.stickyRightShift(2) } shouldBe 1uL }
    }

    context("StickyRightShift.Companion.byte") {
        val ops = StickyRightShift.byte

        test("shift by 0 is identity") { with(ops) { 7.toByte().stickyRightShift(0) } shouldBe 7.toByte() }
        test("no bits lost: clean shift") { with(ops) { 8.toByte().stickyRightShift(3) } shouldBe 1.toByte() }
        test("lost bits set sticky lsb") { with(ops) { 7.toByte().stickyRightShift(3) } shouldBe 1.toByte() }
        test("all bits shifted out: result is 1") { with(ops) { 1.toByte().stickyRightShift(1) } shouldBe 1.toByte() }
        test("negative value: lost bit sticks") {
            with(ops) { Byte.MIN_VALUE.stickyRightShift(1) } shouldBe 0x40.toByte()
        }
    }

    context("StickyRightShift.Companion.short") {
        val ops = StickyRightShift.short

        test("shift by 0 is identity") { with(ops) { 7.toShort().stickyRightShift(0) } shouldBe 7.toShort() }
        test("no bits lost: clean shift") { with(ops) { 8.toShort().stickyRightShift(3) } shouldBe 1.toShort() }
        test("lost bits set sticky lsb") { with(ops) { 7.toShort().stickyRightShift(3) } shouldBe 1.toShort() }
        test("negative value: lost bit sticks") {
            with(ops) { Short.MIN_VALUE.stickyRightShift(1) } shouldBe 0x4000.toShort()
        }
    }

    context("StickyRightShift.Companion.ushort") {
        val ops = StickyRightShift.ushort

        test("shift by 0 is identity") { with(ops) { 7u.toUShort().stickyRightShift(0) } shouldBe 7u.toUShort() }
        test("lost bits set sticky lsb") { with(ops) { 7u.toUShort().stickyRightShift(3) } shouldBe 1u.toUShort() }
        test("no bits lost: clean shift") { with(ops) { 8u.toUShort().stickyRightShift(3) } shouldBe 1u.toUShort() }
    }

    context("StickyRightShift.Companion.ubyte") {
        val ops = StickyRightShift.ubyte

        test("shift by 0 is identity") { with(ops) { 7u.toUByte().stickyRightShift(0) } shouldBe 7u.toUByte() }
        test("no bits lost: clean shift") { with(ops) { 8u.toUByte().stickyRightShift(3) } shouldBe 1u.toUByte() }
        test("lost bits set sticky lsb") { with(ops) { 7u.toUByte().stickyRightShift(3) } shouldBe 1u.toUByte() }
        test("all bits shifted out: result is 1") { with(ops) { 1u.toUByte().stickyRightShift(1) } shouldBe 1u.toUByte() }
        test("msb shifted right by 1") { with(ops) { 0x80u.toUByte().stickyRightShift(1) } shouldBe 0x40u.toUByte() }
    }

    context("StickyRightShift singleton identity") {
        test("StickyRightShift.byte is stable") { StickyRightShift.byte shouldBeSameInstanceAs StickyRightShift.byte }
        test("StickyRightShift.short is stable") { StickyRightShift.short shouldBeSameInstanceAs StickyRightShift.short }
        test("StickyRightShift.int is stable") { StickyRightShift.int shouldBeSameInstanceAs StickyRightShift.int }
        test("StickyRightShift.long is stable") { StickyRightShift.long shouldBeSameInstanceAs StickyRightShift.long }
        test("StickyRightShift.ubyte is stable") { StickyRightShift.ubyte shouldBeSameInstanceAs StickyRightShift.ubyte }
        test("int and long are distinct") { StickyRightShift.int shouldNotBe StickyRightShift.long }
        test("byte and ubyte are distinct") { StickyRightShift.byte shouldNotBe StickyRightShift.ubyte }
    }
})
