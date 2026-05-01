package com.kelvsyc.kotlin.core.traits

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class SignedIntegralTest : FunSpec({

    // ── Int8.Companion ────────────────────────────────────────────────────────

    context("Int8.Companion constants") {
        val ops = Int8
        test("sizeBits = 8") { ops.sizeBits shouldBe 8 }
        test("zero = 0") { ops.zero shouldBe 0.toByte() }
        test("allSet = -1 (all bits set)") { ops.allSet shouldBe (-1).toByte() }
        test("allClear = zero (default)") { ops.allClear shouldBe ops.zero }
        test("lsb = 1") { ops.lsb shouldBe 1.toByte() }
        test("msb = Byte.MIN_VALUE") { ops.msb shouldBe Byte.MIN_VALUE }
    }

    context("Int8.Companion toLong / fromLong") {
        val ops = Int8
        test("positive value toLong") { with(ops) { 42.toByte().toLong() } shouldBe 42L }
        test("negative value sign-extends") { with(ops) { (-1).toByte().toLong() } shouldBe -1L }
        test("Byte.MIN_VALUE sign-extends") { with(ops) { Byte.MIN_VALUE.toLong() } shouldBe Byte.MIN_VALUE.toLong() }
        test("fromLong round-trips zero") { ops.fromLong(0L) shouldBe 0.toByte() }
        test("fromLong round-trips positive") { ops.fromLong(42L) shouldBe 42.toByte() }
        test("fromLong truncates to 8 bits") { ops.fromLong(0x142L) shouldBe 0x42.toByte() }
    }

    context("Int8.Companion bitwise") {
        val ops = Int8
        test("bitwiseAnd") { with(ops) { 0x0F.toByte().bitwiseAnd(0x3C.toByte()) } shouldBe 0x0C.toByte() }
        test("bitwiseOr") { with(ops) { 0x0F.toByte().bitwiseOr(0x30.toByte()) } shouldBe 0x3F.toByte() }
        test("bitwiseXor") { with(ops) { (-1).toByte().bitwiseXor(0x0F.toByte()) } shouldBe 0xF0.toByte() }
        test("invert (default via bitwiseXor allSet)") { with(ops) { 0.toByte().invert() } shouldBe (-1).toByte() }
        test("invert allSet = allClear") { with(ops) { (-1).toByte().invert() } shouldBe 0.toByte() }
    }

    context("Int8.Companion shifts") {
        val ops = Int8
        test("leftShift") { with(ops) { 1.toByte().leftShift(3) } shouldBe 8.toByte() }
        test("leftShift wraps at 8 bits") { with(ops) { 1.toByte().leftShift(7) } shouldBe Byte.MIN_VALUE }
        test("logicalRightShift fills with 0") {
            with(ops) { Byte.MIN_VALUE.logicalRightShift(1) } shouldBe 0x40.toByte()
        }
        test("logicalRightShift on -1 fills with 0") {
            with(ops) { (-1).toByte().logicalRightShift(1) } shouldBe 0x7F.toByte()
        }
        test("arithmeticRightShift preserves sign on negative") {
            with(ops) { (-2).toByte().arithmeticRightShift(1) } shouldBe (-1).toByte()
        }
        test("arithmeticRightShift -1 shr 1 = -1") {
            with(ops) { (-1).toByte().arithmeticRightShift(1) } shouldBe (-1).toByte()
        }
        test("arithmeticRightShift positive fills with 0") {
            with(ops) { 8.toByte().arithmeticRightShift(3) } shouldBe 1.toByte()
        }
    }

    context("Int8.Companion rotations") {
        val ops = Int8
        test("leftRotate by 0 is identity") { with(ops) { 42.toByte().leftRotate(0) } shouldBe 42.toByte() }
        test("leftRotate lsb by 7 = msb") { with(ops) { 1.toByte().leftRotate(7) } shouldBe Byte.MIN_VALUE }
        test("rightRotate msb by 1") { with(ops) { Byte.MIN_VALUE.rightRotate(1) } shouldBe 0x40.toByte() }
        test("leftRotate then rightRotate is identity") {
            with(ops) { 0x5A.toByte().leftRotate(3).rightRotate(3) } shouldBe 0x5A.toByte()
        }
    }

    context("Int8.Companion bit counting") {
        val ops = Int8
        test("allClear has 8 leading clear bits") { with(ops) { 0.toByte().countLeadingClearBits() } shouldBe 8 }
        test("allSet has 0 leading clear bits") { with(ops) { (-1).toByte().countLeadingClearBits() } shouldBe 0 }
        test("msb has 0 leading clear bits") { with(ops) { Byte.MIN_VALUE.countLeadingClearBits() } shouldBe 0 }
        test("1 has 7 leading clear bits") { with(ops) { 1.toByte().countLeadingClearBits() } shouldBe 7 }
        test("allClear has 8 trailing clear bits") { with(ops) { 0.toByte().countTrailingClearBits() } shouldBe 8 }
        test("allSet has 0 trailing clear bits") { with(ops) { (-1).toByte().countTrailingClearBits() } shouldBe 0 }
        test("msb has 7 trailing clear bits") { with(ops) { Byte.MIN_VALUE.countTrailingClearBits() } shouldBe 7 }
        test("allSet has 8 set bits") { with(ops) { (-1).toByte().countSetBits() } shouldBe 8 }
        test("zero has 0 set bits") { with(ops) { 0.toByte().countSetBits() } shouldBe 0 }
        test("0x5A has 4 set bits") { with(ops) { 0x5A.toByte().countSetBits() } shouldBe 4 }
    }

    context("Int8.Companion lowestSetBit / highestSetBit") {
        val ops = Int8
        test("lowestSetBit of 0 is null") { with(ops) { 0.toByte().lowestSetBit() } shouldBe null }
        test("lowestSetBit of lsb is 0") { with(ops) { 1.toByte().lowestSetBit() } shouldBe 0 }
        test("lowestSetBit of 4") { with(ops) { 4.toByte().lowestSetBit() } shouldBe 2 }
        test("highestSetBit of 0 is null") { with(ops) { 0.toByte().highestSetBit() } shouldBe null }
        test("highestSetBit of 1 is 0") { with(ops) { 1.toByte().highestSetBit() } shouldBe 0 }
        test("highestSetBit of msb is 7") { with(ops) { Byte.MIN_VALUE.highestSetBit() } shouldBe 7 }
    }

    context("Int8.Companion takeLowestSetBit / takeHighestSetBit") {
        val ops = Int8
        test("takeLowestSetBit of 6 = 2") { with(ops) { 6.toByte().takeLowestSetBit() } shouldBe 2.toByte() }
        test("takeLowestSetBit of msb = msb") { with(ops) { Byte.MIN_VALUE.takeLowestSetBit() } shouldBe Byte.MIN_VALUE }
        test("takeHighestSetBit of 0x7F = 0x40") {
            with(ops) { Byte.MAX_VALUE.takeHighestSetBit() } shouldBe 0x40.toByte()
        }
        test("takeHighestSetBit of allSet = msb") { with(ops) { (-1).toByte().takeHighestSetBit() } shouldBe Byte.MIN_VALUE }
    }

    context("Int8.Companion isEqualTo (default)") {
        val ops = Int8
        test("same value is equal") { with(ops) { 42.toByte().isEqualTo(42.toByte()) } shouldBe true }
        test("different values are not equal") { with(ops) { 42.toByte().isEqualTo(43.toByte()) } shouldBe false }
    }

    // ── Int16.Companion ───────────────────────────────────────────────────────

    context("Int16.Companion constants") {
        val ops = Int16
        test("sizeBits = 16") { ops.sizeBits shouldBe 16 }
        test("zero = 0") { ops.zero shouldBe 0.toShort() }
        test("allSet = -1 (all bits set)") { ops.allSet shouldBe (-1).toShort() }
        test("allClear = zero (default)") { ops.allClear shouldBe ops.zero }
        test("lsb = 1") { ops.lsb shouldBe 1.toShort() }
        test("msb = Short.MIN_VALUE") { ops.msb shouldBe Short.MIN_VALUE }
    }

    context("Int16.Companion toLong / fromLong") {
        val ops = Int16
        test("positive value toLong") { with(ops) { 42.toShort().toLong() } shouldBe 42L }
        test("negative value sign-extends") { with(ops) { (-1).toShort().toLong() } shouldBe -1L }
        test("Short.MIN_VALUE sign-extends") { with(ops) { Short.MIN_VALUE.toLong() } shouldBe Short.MIN_VALUE.toLong() }
        test("fromLong round-trips zero") { ops.fromLong(0L) shouldBe 0.toShort() }
        test("fromLong round-trips positive") { ops.fromLong(42L) shouldBe 42.toShort() }
        test("fromLong truncates to 16 bits") { ops.fromLong(0x10042L) shouldBe 0x42.toShort() }
    }

    context("Int16.Companion bitwise") {
        val ops = Int16
        test("bitwiseAnd") { with(ops) { (0x0F0F).toShort().bitwiseAnd(0x00FF.toShort()) } shouldBe 0x000F.toShort() }
        test("bitwiseOr") { with(ops) { (0x0F00).toShort().bitwiseOr(0x00FF.toShort()) } shouldBe 0x0FFF.toShort() }
        test("bitwiseXor") { with(ops) { (-1).toShort().bitwiseXor(0x00FF.toShort()) } shouldBe 0xFF00.toShort() }
        test("invert (default via bitwiseXor allSet)") { with(ops) { 0.toShort().invert() } shouldBe (-1).toShort() }
        test("invert allSet = allClear") { with(ops) { (-1).toShort().invert() } shouldBe 0.toShort() }
    }

    context("Int16.Companion shifts") {
        val ops = Int16
        test("leftShift") { with(ops) { 1.toShort().leftShift(3) } shouldBe 8.toShort() }
        test("leftShift wraps at 16 bits") { with(ops) { 1.toShort().leftShift(15) } shouldBe Short.MIN_VALUE }
        test("logicalRightShift fills with 0") {
            with(ops) { Short.MIN_VALUE.logicalRightShift(1) } shouldBe 0x4000.toShort()
        }
        test("logicalRightShift on -1 fills with 0") {
            with(ops) { (-1).toShort().logicalRightShift(1) } shouldBe 0x7FFF.toShort()
        }
        test("arithmeticRightShift preserves sign on negative") {
            with(ops) { (-2).toShort().arithmeticRightShift(1) } shouldBe (-1).toShort()
        }
        test("arithmeticRightShift -1 shr 1 = -1") {
            with(ops) { (-1).toShort().arithmeticRightShift(1) } shouldBe (-1).toShort()
        }
        test("arithmeticRightShift positive fills with 0") {
            with(ops) { 8.toShort().arithmeticRightShift(3) } shouldBe 1.toShort()
        }
        test("arithmeticRightShift Short.MIN_VALUE shr 1") {
            with(ops) { Short.MIN_VALUE.arithmeticRightShift(1) } shouldBe (Short.MIN_VALUE / 2).toShort()
        }
    }

    context("Int16.Companion rotations") {
        val ops = Int16
        test("leftRotate by 0 is identity") { with(ops) { 42.toShort().leftRotate(0) } shouldBe 42.toShort() }
        test("leftRotate lsb by 15 = msb") { with(ops) { 1.toShort().leftRotate(15) } shouldBe Short.MIN_VALUE }
        test("rightRotate msb by 1") { with(ops) { Short.MIN_VALUE.rightRotate(1) } shouldBe 0x4000.toShort() }
        test("leftRotate then rightRotate is identity") {
            with(ops) { 0x1234.toShort().leftRotate(7).rightRotate(7) } shouldBe 0x1234.toShort()
        }
    }

    context("Int16.Companion bit counting") {
        val ops = Int16
        test("allClear has 16 leading clear bits") { with(ops) { 0.toShort().countLeadingClearBits() } shouldBe 16 }
        test("allSet has 0 leading clear bits") { with(ops) { (-1).toShort().countLeadingClearBits() } shouldBe 0 }
        test("msb has 0 leading clear bits") { with(ops) { Short.MIN_VALUE.countLeadingClearBits() } shouldBe 0 }
        test("1 has 15 leading clear bits") { with(ops) { 1.toShort().countLeadingClearBits() } shouldBe 15 }
        test("allClear has 16 trailing clear bits") { with(ops) { 0.toShort().countTrailingClearBits() } shouldBe 16 }
        test("allSet has 0 trailing clear bits") { with(ops) { (-1).toShort().countTrailingClearBits() } shouldBe 0 }
        test("msb has 15 trailing clear bits") { with(ops) { Short.MIN_VALUE.countTrailingClearBits() } shouldBe 15 }
        test("allSet has 16 set bits") { with(ops) { (-1).toShort().countSetBits() } shouldBe 16 }
        test("zero has 0 set bits") { with(ops) { 0.toShort().countSetBits() } shouldBe 0 }
        test("0x0F0F has 8 set bits") { with(ops) { 0x0F0F.toShort().countSetBits() } shouldBe 8 }
    }

    context("Int16.Companion lowestSetBit / highestSetBit") {
        val ops = Int16
        test("lowestSetBit of 0 is null") { with(ops) { 0.toShort().lowestSetBit() } shouldBe null }
        test("lowestSetBit of lsb is 0") { with(ops) { 1.toShort().lowestSetBit() } shouldBe 0 }
        test("lowestSetBit of 4") { with(ops) { 4.toShort().lowestSetBit() } shouldBe 2 }
        test("highestSetBit of 0 is null") { with(ops) { 0.toShort().highestSetBit() } shouldBe null }
        test("highestSetBit of 1 is 0") { with(ops) { 1.toShort().highestSetBit() } shouldBe 0 }
        test("highestSetBit of msb is 15") { with(ops) { Short.MIN_VALUE.highestSetBit() } shouldBe 15 }
    }

    context("Int16.Companion takeLowestSetBit / takeHighestSetBit") {
        val ops = Int16
        test("takeLowestSetBit of 6 = 2") { with(ops) { 6.toShort().takeLowestSetBit() } shouldBe 2.toShort() }
        test("takeLowestSetBit of msb = msb") { with(ops) { Short.MIN_VALUE.takeLowestSetBit() } shouldBe Short.MIN_VALUE }
        test("takeHighestSetBit of 0x7FFF = 0x4000") {
            with(ops) { Short.MAX_VALUE.takeHighestSetBit() } shouldBe 0x4000.toShort()
        }
        test("takeHighestSetBit of allSet = msb") { with(ops) { (-1).toShort().takeHighestSetBit() } shouldBe Short.MIN_VALUE }
    }

    context("Int16.Companion isEqualTo (default)") {
        val ops = Int16
        test("same value is equal") { with(ops) { 42.toShort().isEqualTo(42.toShort()) } shouldBe true }
        test("different values are not equal") { with(ops) { 42.toShort().isEqualTo(43.toShort()) } shouldBe false }
    }

    // ── Int32.Companion ───────────────────────────────────────────────────────

    context("Int32.Companion constants") {
        val ops = Int32
        test("sizeBits = 32") { ops.sizeBits shouldBe 32 }
        test("zero = 0") { ops.zero shouldBe 0 }
        test("allSet = -1") { ops.allSet shouldBe -1 }
        test("allClear = zero (default)") { ops.allClear shouldBe ops.zero }
        test("lsb = 1") { ops.lsb shouldBe 1 }
        test("msb = Int.MIN_VALUE") { ops.msb shouldBe Int.MIN_VALUE }
    }

    context("Int32.Companion toLong / fromLong") {
        val ops = Int32
        test("positive value toLong") { with(ops) { 42.toLong() } shouldBe 42L }
        test("negative value sign-extends") { with(ops) { (-1).toLong() } shouldBe -1L }
        test("Int.MIN_VALUE sign-extends") { with(ops) { Int.MIN_VALUE.toLong() } shouldBe Int.MIN_VALUE.toLong() }
        test("fromLong truncates to 32 bits") { ops.fromLong(0x1_0000_0042L) shouldBe 0x42 }
    }

    context("Int32.Companion shifts") {
        val ops = Int32
        test("leftShift") { with(ops) { 1.leftShift(3) } shouldBe 8 }
        test("logicalRightShift fills with 0") { with(ops) { Int.MIN_VALUE.logicalRightShift(1) } shouldBe 0x40000000 }
        test("arithmeticRightShift preserves sign") { with(ops) { (-8).arithmeticRightShift(3) } shouldBe -1 }
        test("arithmeticRightShift positive fills with 0") { with(ops) { 8.arithmeticRightShift(3) } shouldBe 1 }
    }

    context("Int32.Companion rotations") {
        val ops = Int32
        test("leftRotate lsb by 31 = msb") { with(ops) { 1.leftRotate(31) } shouldBe Int.MIN_VALUE }
        test("leftRotate then rightRotate is identity") {
            with(ops) { 0x12345678.leftRotate(13).rightRotate(13) } shouldBe 0x12345678
        }
    }

    context("Int32.Companion bit counting") {
        val ops = Int32
        test("allClear has 32 leading clear bits") { with(ops) { 0.countLeadingClearBits() } shouldBe 32 }
        test("allSet has 0 leading clear bits") { with(ops) { (-1).countLeadingClearBits() } shouldBe 0 }
        test("allSet has 32 set bits") { with(ops) { (-1).countSetBits() } shouldBe 32 }
    }

    // ── Int64.Companion ───────────────────────────────────────────────────────

    context("Int64.Companion constants") {
        val ops = Int64
        test("sizeBits = 64") { ops.sizeBits shouldBe 64 }
        test("zero = 0L") { ops.zero shouldBe 0L }
        test("allSet = -1L") { ops.allSet shouldBe -1L }
        test("allClear = zero (default)") { ops.allClear shouldBe ops.zero }
        test("lsb = 1L") { ops.lsb shouldBe 1L }
        test("msb = Long.MIN_VALUE") { ops.msb shouldBe Long.MIN_VALUE }
    }

    context("Int64.Companion toLong / fromLong") {
        val ops = Int64
        test("fromLong positive") { ops.fromLong(42L) shouldBe 42L }
        test("fromLong negative") { ops.fromLong(-1L) shouldBe -1L }
        test("fromLong MIN_VALUE") { ops.fromLong(Long.MIN_VALUE) shouldBe Long.MIN_VALUE }
    }

    context("Int64.Companion shifts") {
        val ops = Int64
        test("logicalRightShift fills with 0") { with(ops) { Long.MIN_VALUE.logicalRightShift(1) } shouldBe 0x4000000000000000L }
        test("arithmeticRightShift preserves sign") { with(ops) { (-8L).arithmeticRightShift(3) } shouldBe -1L }
        test("arithmeticRightShift positive fills with 0") { with(ops) { 16L.arithmeticRightShift(4) } shouldBe 1L }
    }

    context("Int64.Companion bit counting") {
        val ops = Int64
        test("allClear has 64 leading clear bits") { with(ops) { 0L.countLeadingClearBits() } shouldBe 64 }
        test("allSet has 64 set bits") { with(ops) { (-1L).countSetBits() } shouldBe 64 }
    }

    // ── Companion property aliases ────────────────────────────────────────────

    context("BitCollection.Companion.byte") {
        test("delegates to Int8") { BitCollection.byte shouldBeSameInstanceAs Int8 }
    }

    context("BitShift.Companion.byte") {
        val ops = BitShift.byte
        test("delegates to Int8") { ops shouldBeSameInstanceAs Int8 }
        test("leftShift works") { with(ops) { 1.toByte().leftShift(3) } shouldBe 8.toByte() }
        test("logicalRightShift fills with 0") { with(ops) { Byte.MIN_VALUE.logicalRightShift(1) } shouldBe 0x40.toByte() }
    }

    context("ArithmeticRightShift.Companion.byte") {
        val ops = ArithmeticRightShift.byte
        test("delegates to Int8") { ops shouldBeSameInstanceAs Int8 }
        test("-1 shr 1 = -1") { with(ops) { (-1).toByte().arithmeticRightShift(1) } shouldBe (-1).toByte() }
        test("positive fills with 0") { with(ops) { 8.toByte().arithmeticRightShift(3) } shouldBe 1.toByte() }
    }

    context("BitCollection.Companion.short") {
        test("delegates to Int16") { BitCollection.short shouldBeSameInstanceAs Int16 }
    }

    context("BitShift.Companion.short") {
        val ops = BitShift.short
        test("delegates to Int16") { ops shouldBeSameInstanceAs Int16 }
        test("leftShift works") { with(ops) { 1.toShort().leftShift(3) } shouldBe 8.toShort() }
        test("logicalRightShift fills with 0") { with(ops) { Short.MIN_VALUE.logicalRightShift(1) } shouldBe 0x4000.toShort() }
    }

    context("ArithmeticRightShift.Companion.short") {
        val ops = ArithmeticRightShift.short
        test("delegates to Int16") { ops shouldBeSameInstanceAs Int16 }
        test("-1 shr 1 = -1") { with(ops) { (-1).toShort().arithmeticRightShift(1) } shouldBe (-1).toShort() }
        test("positive fills with 0") { with(ops) { 8.toShort().arithmeticRightShift(3) } shouldBe 1.toShort() }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("Int8 is stable") { Int8 shouldBeSameInstanceAs Int8 }
        test("Int16 is stable") { Int16 shouldBeSameInstanceAs Int16 }
        test("Int32 is stable") { Int32 shouldBeSameInstanceAs Int32 }
        test("Int64 is stable") { Int64 shouldBeSameInstanceAs Int64 }
        test("Int8 and Int16 are distinct") { Int8 shouldNotBe Int16 }
        test("Int16 and Int32 are distinct") { Int16 shouldNotBe Int32 }
        test("Int32 and Int64 are distinct") { Int32 shouldNotBe Int64 }
    }
})
