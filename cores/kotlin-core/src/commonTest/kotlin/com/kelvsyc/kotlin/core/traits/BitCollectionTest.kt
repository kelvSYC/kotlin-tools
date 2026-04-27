package com.kelvsyc.kotlin.core.traits

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class BitCollectionTest : FunSpec({

    // ── BitCollection.Companion.int ───────────────────────────────────────────

    context("BitCollection.Companion.int") {
        val ops = BitCollection.int

        context("constants") {
            test("sizeBits = 32") { ops.sizeBits shouldBe 32 }
            test("allSet = -1") { ops.allSet shouldBe -1 }
            test("allClear = 0") { ops.allClear shouldBe 0 }
            test("lsb = 1") { ops.lsb shouldBe 1 }
            test("msb = Int.MIN_VALUE") { ops.msb shouldBe Int.MIN_VALUE }
        }

        context("invert (default via bitwiseXor allSet)") {
            test("invert allClear = allSet") { with(ops) { 0.invert() } shouldBe -1 }
            test("invert allSet = allClear") { with(ops) { (-1).invert() } shouldBe 0 }
            test("double invert is identity") { with(ops) { 42.invert().invert() } shouldBe 42 }
        }

        context("leftRotate / rightRotate") {
            test("rotate 1 left by 1") { with(ops) { 1.leftRotate(1) } shouldBe 2 }
            test("rotate lsb left by 31 = msb") { with(ops) { 1.leftRotate(31) } shouldBe Int.MIN_VALUE }
            test("leftRotate by 0 is identity") { with(ops) { 42.leftRotate(0) } shouldBe 42 }
            test("rightRotate by 0 is identity") { with(ops) { 42.rightRotate(0) } shouldBe 42 }
            test("leftRotate then rightRotate is identity") { with(ops) { 0x12345678.leftRotate(13).rightRotate(13) } shouldBe 0x12345678 }
            test("rotate msb right by 1") { with(ops) { Int.MIN_VALUE.rightRotate(1) } shouldBe 0x40000000 }
        }

        context("countLeadingClearBits") {
            test("allClear has 32 leading clear bits") { with(ops) { 0.countLeadingClearBits() } shouldBe 32 }
            test("allSet has 0 leading clear bits") { with(ops) { (-1).countLeadingClearBits() } shouldBe 0 }
            test("msb has 0 leading clear bits") { with(ops) { Int.MIN_VALUE.countLeadingClearBits() } shouldBe 0 }
            test("1 has 31 leading clear bits") { with(ops) { 1.countLeadingClearBits() } shouldBe 31 }
        }

        context("countTrailingClearBits") {
            test("allClear has 32 trailing clear bits") { with(ops) { 0.countTrailingClearBits() } shouldBe 32 }
            test("allSet has 0 trailing clear bits") { with(ops) { (-1).countTrailingClearBits() } shouldBe 0 }
            test("lsb has 0 trailing clear bits") { with(ops) { 1.countTrailingClearBits() } shouldBe 0 }
            test("msb has 31 trailing clear bits") { with(ops) { Int.MIN_VALUE.countTrailingClearBits() } shouldBe 31 }
            test("4 has 2 trailing clear bits") { with(ops) { 4.countTrailingClearBits() } shouldBe 2 }
        }

        context("countSetBits") {
            test("allClear has 0 set bits") { with(ops) { 0.countSetBits() } shouldBe 0 }
            test("allSet has 32 set bits") { with(ops) { (-1).countSetBits() } shouldBe 32 }
            test("lsb has 1 set bit") { with(ops) { 1.countSetBits() } shouldBe 1 }
            test("0b1010 has 2 set bits") { with(ops) { 0b1010.countSetBits() } shouldBe 2 }
        }

        context("lowestSetBit") {
            test("allClear returns null") { with(ops) { 0.lowestSetBit() } shouldBe null }
            test("lsb returns 0") { with(ops) { 1.lowestSetBit() } shouldBe 0 }
            test("msb returns 31") { with(ops) { Int.MIN_VALUE.lowestSetBit() } shouldBe 31 }
            test("4 (0b100) returns 2") { with(ops) { 4.lowestSetBit() } shouldBe 2 }
        }

        context("highestSetBit") {
            test("allClear returns null") { with(ops) { 0.highestSetBit() } shouldBe null }
            test("lsb returns 0") { with(ops) { 1.highestSetBit() } shouldBe 0 }
            test("msb returns 31") { with(ops) { Int.MIN_VALUE.highestSetBit() } shouldBe 31 }
            test("0b110 returns 2") { with(ops) { 0b110.highestSetBit() } shouldBe 2 }
        }

        context("takeLowestSetBit") {
            test("allSet isolates lsb") { with(ops) { (-1).takeLowestSetBit() } shouldBe 1 }
            test("0b110 isolates bit 1") { with(ops) { 0b110.takeLowestSetBit() } shouldBe 0b010 }
            test("power-of-two is unchanged") { with(ops) { 8.takeLowestSetBit() } shouldBe 8 }
        }

        context("takeHighestSetBit") {
            test("lsb is unchanged") { with(ops) { 1.takeHighestSetBit() } shouldBe 1 }
            test("0b110 isolates bit 2") { with(ops) { 0b110.takeHighestSetBit() } shouldBe 0b100 }
            test("power-of-two is unchanged") { with(ops) { 8.takeHighestSetBit() } shouldBe 8 }
        }
    }

    // ── BitCollection.Companion.long ──────────────────────────────────────────

    context("BitCollection.Companion.long") {
        val ops = BitCollection.long

        context("constants") {
            test("sizeBits = 64") { ops.sizeBits shouldBe 64 }
            test("allSet = -1L") { ops.allSet shouldBe -1L }
            test("allClear = 0L") { ops.allClear shouldBe 0L }
            test("lsb = 1L") { ops.lsb shouldBe 1L }
            test("msb = Long.MIN_VALUE") { ops.msb shouldBe Long.MIN_VALUE }
        }

        context("countLeadingClearBits") {
            test("allClear has 64 leading clear bits") { with(ops) { 0L.countLeadingClearBits() } shouldBe 64 }
            test("allSet has 0 leading clear bits") { with(ops) { (-1L).countLeadingClearBits() } shouldBe 0 }
            test("1L has 63 leading clear bits") { with(ops) { 1L.countLeadingClearBits() } shouldBe 63 }
        }

        context("countTrailingClearBits") {
            test("allClear has 64 trailing clear bits") { with(ops) { 0L.countTrailingClearBits() } shouldBe 64 }
            test("msb has 63 trailing clear bits") { with(ops) { Long.MIN_VALUE.countTrailingClearBits() } shouldBe 63 }
        }

        context("lowestSetBit / highestSetBit") {
            test("allClear lowestSetBit is null") { with(ops) { 0L.lowestSetBit() } shouldBe null }
            test("allClear highestSetBit is null") { with(ops) { 0L.highestSetBit() } shouldBe null }
            test("msb lowestSetBit = 63") { with(ops) { Long.MIN_VALUE.lowestSetBit() } shouldBe 63 }
            test("msb highestSetBit = 63") { with(ops) { Long.MIN_VALUE.highestSetBit() } shouldBe 63 }
        }

        context("leftRotate / rightRotate") {
            test("leftRotate then rightRotate is identity") { with(ops) { 0x123456789ABCDEFL.leftRotate(17).rightRotate(17) } shouldBe 0x123456789ABCDEFL }
        }
    }

    // ── BitCollection.Companion.uint ──────────────────────────────────────────

    context("BitCollection.Companion.uint") {
        val ops = BitCollection.uint

        context("constants") {
            test("sizeBits = 32") { ops.sizeBits shouldBe 32 }
            test("allSet = UInt.MAX_VALUE") { ops.allSet shouldBe UInt.MAX_VALUE }
            test("allClear = 0u") { ops.allClear shouldBe 0u }
            test("lsb = 1u") { ops.lsb shouldBe 1u }
            test("msb = 0x80000000u") { ops.msb shouldBe 0x80000000u }
        }

        context("invert") {
            test("invert allClear = allSet") { with(ops) { 0u.invert() } shouldBe UInt.MAX_VALUE }
            test("double invert is identity") { with(ops) { 42u.invert().invert() } shouldBe 42u }
        }

        context("countLeadingClearBits") {
            test("allClear has 32 leading clear bits") { with(ops) { 0u.countLeadingClearBits() } shouldBe 32 }
            test("allSet has 0 leading clear bits") { with(ops) { UInt.MAX_VALUE.countLeadingClearBits() } shouldBe 0 }
            test("1u has 31 leading clear bits") { with(ops) { 1u.countLeadingClearBits() } shouldBe 31 }
        }

        context("lowestSetBit / highestSetBit") {
            test("allClear lowestSetBit is null") { with(ops) { 0u.lowestSetBit() } shouldBe null }
            test("allClear highestSetBit is null") { with(ops) { 0u.highestSetBit() } shouldBe null }
            test("lsb lowestSetBit = 0") { with(ops) { 1u.lowestSetBit() } shouldBe 0 }
            test("msb highestSetBit = 31") { with(ops) { 0x80000000u.highestSetBit() } shouldBe 31 }
        }

        context("leftRotate / rightRotate") {
            test("leftRotate then rightRotate is identity") { with(ops) { 0xDEADBEEFu.leftRotate(7).rightRotate(7) } shouldBe 0xDEADBEEFu }
        }
    }

    // ── BitCollection.Companion.ulong ─────────────────────────────────────────

    context("BitCollection.Companion.ulong") {
        val ops = BitCollection.ulong

        context("constants") {
            test("sizeBits = 64") { ops.sizeBits shouldBe 64 }
            test("allSet = ULong.MAX_VALUE") { ops.allSet shouldBe ULong.MAX_VALUE }
            test("allClear = 0uL") { ops.allClear shouldBe 0uL }
            test("lsb = 1uL") { ops.lsb shouldBe 1uL }
            test("msb = 0x8000000000000000uL") { ops.msb shouldBe 0x8000000000000000uL }
        }

        context("countTrailingClearBits") {
            test("allClear has 64 trailing clear bits") { with(ops) { 0uL.countTrailingClearBits() } shouldBe 64 }
            test("lsb has 0 trailing clear bits") { with(ops) { 1uL.countTrailingClearBits() } shouldBe 0 }
        }

        context("lowestSetBit / highestSetBit") {
            test("allClear lowestSetBit is null") { with(ops) { 0uL.lowestSetBit() } shouldBe null }
            test("msb lowestSetBit = 63") { with(ops) { 0x8000000000000000uL.lowestSetBit() } shouldBe 63 }
        }
    }

    // ── BitCollection.Companion.ushort ────────────────────────────────────────

    context("BitCollection.Companion.ushort") {
        val ops = BitCollection.ushort

        context("constants") {
            test("sizeBits = 16") { ops.sizeBits shouldBe 16 }
            test("allSet = UShort.MAX_VALUE") { ops.allSet shouldBe UShort.MAX_VALUE }
            test("allClear = 0u") { ops.allClear shouldBe 0u.toUShort() }
            test("lsb = 1u") { ops.lsb shouldBe 1u.toUShort() }
            test("msb = 0x8000u") { ops.msb shouldBe 0x8000u.toUShort() }
        }

        context("invert") {
            test("invert allClear = allSet") { with(ops) { 0u.toUShort().invert() } shouldBe UShort.MAX_VALUE }
            test("double invert is identity") { with(ops) { 42u.toUShort().invert().invert() } shouldBe 42u.toUShort() }
        }

        context("countLeadingClearBits") {
            test("allClear has 16 leading clear bits") { with(ops) { 0u.toUShort().countLeadingClearBits() } shouldBe 16 }
            test("allSet has 0 leading clear bits") { with(ops) { UShort.MAX_VALUE.countLeadingClearBits() } shouldBe 0 }
            test("1u has 15 leading clear bits") { with(ops) { 1u.toUShort().countLeadingClearBits() } shouldBe 15 }
        }

        context("countTrailingClearBits") {
            test("allClear has 16 trailing clear bits") { with(ops) { 0u.toUShort().countTrailingClearBits() } shouldBe 16 }
            test("msb has 15 trailing clear bits") { with(ops) { 0x8000u.toUShort().countTrailingClearBits() } shouldBe 15 }
        }

        context("lowestSetBit / highestSetBit") {
            test("allClear lowestSetBit is null") { with(ops) { 0u.toUShort().lowestSetBit() } shouldBe null }
            test("allClear highestSetBit is null") { with(ops) { 0u.toUShort().highestSetBit() } shouldBe null }
            test("lsb lowestSetBit = 0") { with(ops) { 1u.toUShort().lowestSetBit() } shouldBe 0 }
            test("msb highestSetBit = 15") { with(ops) { 0x8000u.toUShort().highestSetBit() } shouldBe 15 }
        }

        context("leftRotate / rightRotate") {
            test("leftRotate then rightRotate is identity") {
                with(ops) { 0xABCDu.toUShort().leftRotate(5).rightRotate(5) } shouldBe 0xABCDu.toUShort()
            }
            test("rotate msb right by 1 gives 0x4000") {
                with(ops) { 0x8000u.toUShort().rightRotate(1) } shouldBe 0x4000u.toUShort()
            }
            test("rotate lsb left by 15 gives msb") {
                with(ops) { 1u.toUShort().leftRotate(15) } shouldBe 0x8000u.toUShort()
            }
        }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("BitCollection.int is stable") { BitCollection.int shouldBeSameInstanceAs BitCollection.int }
        test("BitCollection.long is stable") { BitCollection.long shouldBeSameInstanceAs BitCollection.long }
        test("BitCollection.uint is stable") { BitCollection.uint shouldBeSameInstanceAs BitCollection.uint }
        test("BitCollection.ulong is stable") { BitCollection.ulong shouldBeSameInstanceAs BitCollection.ulong }
        test("BitCollection.ushort is stable") { BitCollection.ushort shouldBeSameInstanceAs BitCollection.ushort }
        test("int and long are distinct") { BitCollection.int shouldNotBe BitCollection.long }
        test("int and uint are distinct") { BitCollection.int shouldNotBe BitCollection.uint }
    }
})
