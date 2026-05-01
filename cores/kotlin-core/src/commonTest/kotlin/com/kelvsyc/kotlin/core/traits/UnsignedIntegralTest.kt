package com.kelvsyc.kotlin.core.traits

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class UnsignedIntegralTest : FunSpec({

    // ── UInt8.Companion ───────────────────────────────────────────────────────

    context("UInt8.Companion constants") {
        val ops = UInt8
        test("sizeBits = 8") { ops.sizeBits shouldBe 8 }
        test("zero = 0u") { ops.zero shouldBe 0u.toUByte() }
        test("allSet = 0xFFu") { ops.allSet shouldBe UByte.MAX_VALUE }
        test("allClear = zero (default)") { ops.allClear shouldBe ops.zero }
        test("lsb = 1u") { ops.lsb shouldBe 1u.toUByte() }
        test("msb = 0x80u") { ops.msb shouldBe 0x80u.toUByte() }
    }

    context("UInt8.Companion toULong / fromULong") {
        val ops = UInt8
        test("toULong of 42") { with(ops) { 42u.toUByte().toULong() } shouldBe 42uL }
        test("toULong of MAX_VALUE") { with(ops) { UByte.MAX_VALUE.toULong() } shouldBe 255uL }
        test("fromULong round-trips zero") { ops.fromULong(0uL) shouldBe 0u.toUByte() }
        test("fromULong round-trips value") { ops.fromULong(42uL) shouldBe 42u.toUByte() }
        test("fromULong truncates to 8 bits") { ops.fromULong(0x142uL) shouldBe 0x42u.toUByte() }
    }

    context("UInt8.Companion bitwise") {
        val ops = UInt8
        test("bitwiseAnd") { with(ops) { 0x0Fu.toUByte().bitwiseAnd(0x3Cu.toUByte()) } shouldBe 0x0Cu.toUByte() }
        test("bitwiseOr") { with(ops) { 0x0Fu.toUByte().bitwiseOr(0x30u.toUByte()) } shouldBe 0x3Fu.toUByte() }
        test("bitwiseXor") { with(ops) { UByte.MAX_VALUE.bitwiseXor(0x0Fu.toUByte()) } shouldBe 0xF0u.toUByte() }
        test("invert (default via bitwiseXor allSet)") { with(ops) { 0u.toUByte().invert() } shouldBe UByte.MAX_VALUE }
        test("invert allSet = allClear") { with(ops) { UByte.MAX_VALUE.invert() } shouldBe 0u.toUByte() }
    }

    context("UInt8.Companion shifts") {
        val ops = UInt8
        test("leftShift") { with(ops) { 1u.toUByte().leftShift(3) } shouldBe 8u.toUByte() }
        test("leftShift wraps at 8 bits") { with(ops) { 1u.toUByte().leftShift(7) } shouldBe 0x80u.toUByte() }
        test("logicalRightShift fills with 0") {
            with(ops) { 0x80u.toUByte().logicalRightShift(1) } shouldBe 0x40u.toUByte()
        }
        test("logicalRightShift on allSet fills with 0") {
            with(ops) { UByte.MAX_VALUE.logicalRightShift(1) } shouldBe 0x7Fu.toUByte()
        }
    }

    context("UInt8.Companion rotations") {
        val ops = UInt8
        test("leftRotate by 0 is identity") { with(ops) { 42u.toUByte().leftRotate(0) } shouldBe 42u.toUByte() }
        test("leftRotate lsb by 7 = msb") { with(ops) { 1u.toUByte().leftRotate(7) } shouldBe 0x80u.toUByte() }
        test("rightRotate msb by 1") { with(ops) { 0x80u.toUByte().rightRotate(1) } shouldBe 0x40u.toUByte() }
        test("leftRotate then rightRotate is identity") {
            with(ops) { 0x5Au.toUByte().leftRotate(3).rightRotate(3) } shouldBe 0x5Au.toUByte()
        }
    }

    context("UInt8.Companion bit counting") {
        val ops = UInt8
        test("allClear has 8 leading clear bits") { with(ops) { 0u.toUByte().countLeadingClearBits() } shouldBe 8 }
        test("allSet has 0 leading clear bits") { with(ops) { UByte.MAX_VALUE.countLeadingClearBits() } shouldBe 0 }
        test("msb has 0 leading clear bits") { with(ops) { 0x80u.toUByte().countLeadingClearBits() } shouldBe 0 }
        test("1u has 7 leading clear bits") { with(ops) { 1u.toUByte().countLeadingClearBits() } shouldBe 7 }
        test("allClear has 8 trailing clear bits") { with(ops) { 0u.toUByte().countTrailingClearBits() } shouldBe 8 }
        test("allSet has 0 trailing clear bits") { with(ops) { UByte.MAX_VALUE.countTrailingClearBits() } shouldBe 0 }
        test("msb has 7 trailing clear bits") { with(ops) { 0x80u.toUByte().countTrailingClearBits() } shouldBe 7 }
        test("allSet has 8 set bits") { with(ops) { UByte.MAX_VALUE.countSetBits() } shouldBe 8 }
        test("zero has 0 set bits") { with(ops) { 0u.toUByte().countSetBits() } shouldBe 0 }
        test("0x5Au has 4 set bits") { with(ops) { 0x5Au.toUByte().countSetBits() } shouldBe 4 }
    }

    context("UInt8.Companion lowestSetBit / highestSetBit") {
        val ops = UInt8
        test("lowestSetBit of 0 is null") { with(ops) { 0u.toUByte().lowestSetBit() } shouldBe null }
        test("lowestSetBit of lsb is 0") { with(ops) { 1u.toUByte().lowestSetBit() } shouldBe 0 }
        test("lowestSetBit of 4") { with(ops) { 4u.toUByte().lowestSetBit() } shouldBe 2 }
        test("highestSetBit of 0 is null") { with(ops) { 0u.toUByte().highestSetBit() } shouldBe null }
        test("highestSetBit of 1 is 0") { with(ops) { 1u.toUByte().highestSetBit() } shouldBe 0 }
        test("highestSetBit of msb is 7") { with(ops) { 0x80u.toUByte().highestSetBit() } shouldBe 7 }
    }

    context("UInt8.Companion takeLowestSetBit / takeHighestSetBit") {
        val ops = UInt8
        test("takeLowestSetBit of 6 = 2") { with(ops) { 6u.toUByte().takeLowestSetBit() } shouldBe 2u.toUByte() }
        test("takeLowestSetBit of msb = msb") { with(ops) { 0x80u.toUByte().takeLowestSetBit() } shouldBe 0x80u.toUByte() }
        test("takeHighestSetBit of 0x7F = 0x40") {
            with(ops) { 0x7Fu.toUByte().takeHighestSetBit() } shouldBe 0x40u.toUByte()
        }
        test("takeHighestSetBit of allSet = msb") { with(ops) { UByte.MAX_VALUE.takeHighestSetBit() } shouldBe 0x80u.toUByte() }
    }

    context("UInt8.Companion isEqualTo (default)") {
        val ops = UInt8
        test("same value is equal") { with(ops) { 42u.toUByte().isEqualTo(42u.toUByte()) } shouldBe true }
        test("different values are not equal") { with(ops) { 42u.toUByte().isEqualTo(43u.toUByte()) } shouldBe false }
    }

    // ── UInt16.Companion ──────────────────────────────────────────────────────

    context("UInt16.Companion constants") {
        val ops = UInt16
        test("sizeBits = 16") { ops.sizeBits shouldBe 16 }
        test("zero = 0u") { ops.zero shouldBe 0u.toUShort() }
        test("allSet = 0xFFFFu") { ops.allSet shouldBe UShort.MAX_VALUE }
        test("allClear = zero (default)") { ops.allClear shouldBe ops.zero }
        test("lsb = 1u") { ops.lsb shouldBe 1u.toUShort() }
        test("msb = 0x8000u") { ops.msb shouldBe 0x8000u.toUShort() }
    }

    context("UInt16.Companion toULong / fromULong") {
        val ops = UInt16
        test("toULong of 42") { with(ops) { 42u.toUShort().toULong() } shouldBe 42uL }
        test("toULong of MAX_VALUE") { with(ops) { UShort.MAX_VALUE.toULong() } shouldBe 65535uL }
        test("fromULong truncates to 16 bits") { ops.fromULong(0x1_0042uL) shouldBe 0x42u.toUShort() }
    }

    context("UInt16.Companion shifts") {
        val ops = UInt16
        test("leftShift") { with(ops) { 1u.toUShort().leftShift(3) } shouldBe 8u.toUShort() }
        test("logicalRightShift fills with 0") {
            with(ops) { 0x8000u.toUShort().logicalRightShift(1) } shouldBe 0x4000u.toUShort()
        }
    }

    context("UInt16.Companion bit counting") {
        val ops = UInt16
        test("allClear has 16 leading clear bits") { with(ops) { 0u.toUShort().countLeadingClearBits() } shouldBe 16 }
        test("allSet has 0 leading clear bits") { with(ops) { UShort.MAX_VALUE.countLeadingClearBits() } shouldBe 0 }
        test("allSet has 16 set bits") { with(ops) { UShort.MAX_VALUE.countSetBits() } shouldBe 16 }
    }

    // ── UInt32.Companion ──────────────────────────────────────────────────────

    context("UInt32.Companion constants") {
        val ops = UInt32
        test("sizeBits = 32") { ops.sizeBits shouldBe 32 }
        test("zero = 0u") { ops.zero shouldBe 0u }
        test("allSet = 0xFFFFFFFFu") { ops.allSet shouldBe UInt.MAX_VALUE }
        test("lsb = 1u") { ops.lsb shouldBe 1u }
        test("msb = 0x80000000u") { ops.msb shouldBe 0x80000000u }
    }

    context("UInt32.Companion toULong / fromULong") {
        val ops = UInt32
        test("toULong of MAX_VALUE") { with(ops) { UInt.MAX_VALUE.toULong() } shouldBe 4294967295uL }
        test("fromULong truncates to 32 bits") { ops.fromULong(0x1_0000_0042uL) shouldBe 0x42u }
    }

    context("UInt32.Companion shifts and rotations") {
        val ops = UInt32
        test("logicalRightShift fills with 0") { with(ops) { 0x80000000u.logicalRightShift(1) } shouldBe 0x40000000u }
        test("leftRotate lsb by 31 = msb") { with(ops) { 1u.leftRotate(31) } shouldBe 0x80000000u }
    }

    // ── UInt64.Companion ──────────────────────────────────────────────────────

    context("UInt64.Companion constants") {
        val ops = UInt64
        test("sizeBits = 64") { ops.sizeBits shouldBe 64 }
        test("zero = 0uL") { ops.zero shouldBe 0uL }
        test("allSet = ULong.MAX_VALUE") { ops.allSet shouldBe ULong.MAX_VALUE }
        test("lsb = 1uL") { ops.lsb shouldBe 1uL }
        test("msb = 0x8000000000000000uL") { ops.msb shouldBe 0x8000000000000000uL }
    }

    context("UInt64.Companion toULong / fromULong") {
        val ops = UInt64
        test("fromULong round-trips") { ops.fromULong(ULong.MAX_VALUE) shouldBe ULong.MAX_VALUE }
        // toULong() = this for ULong — trivial identity, no test needed
    }

    context("UInt64.Companion shifts") {
        val ops = UInt64
        test("logicalRightShift fills with 0") {
            with(ops) { 0x8000000000000000uL.logicalRightShift(1) } shouldBe 0x4000000000000000uL
        }
    }

    // ── Companion property aliases ────────────────────────────────────────────

    context("BitCollection.Companion.ubyte") {
        test("delegates to UInt8") { BitCollection.ubyte shouldBeSameInstanceAs UInt8 }
    }

    context("BitShift.Companion.ubyte") {
        val ops = BitShift.ubyte
        test("delegates to UInt8") { ops shouldBeSameInstanceAs UInt8 }
        test("leftShift works") { with(ops) { 1u.toUByte().leftShift(3) } shouldBe 8u.toUByte() }
        test("logicalRightShift fills with 0") { with(ops) { 0x80u.toUByte().logicalRightShift(1) } shouldBe 0x40u.toUByte() }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("UInt8 is stable") { UInt8 shouldBeSameInstanceAs UInt8 }
        test("UInt16 is stable") { UInt16 shouldBeSameInstanceAs UInt16 }
        test("UInt32 is stable") { UInt32 shouldBeSameInstanceAs UInt32 }
        test("UInt64 is stable") { UInt64 shouldBeSameInstanceAs UInt64 }
        test("UInt8 and UInt16 are distinct") { UInt8 shouldNotBe UInt16 }
        test("UInt16 and UInt32 are distinct") { UInt16 shouldNotBe UInt32 }
        test("UInt32 and UInt64 are distinct") { UInt32 shouldNotBe UInt64 }
    }
})
