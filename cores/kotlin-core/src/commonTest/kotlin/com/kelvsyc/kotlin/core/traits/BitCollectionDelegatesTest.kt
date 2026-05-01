package com.kelvsyc.kotlin.core.traits

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BitCollectionDelegatesTest : FunSpec({

    // ── bitFlag(value, bit) ───────────────────────────────────────────────────

    context("bitFlag snapshot (Int)") {
        val ops = BitCollection.int

        test("bit 0 set") {
            val flag: Boolean by ops.bitFlag(0b00000101, 0)
            flag shouldBe true
        }
        test("bit 0 clear") {
            val flag: Boolean by ops.bitFlag(0b00000100, 0)
            flag shouldBe false
        }
        test("bit 2 set") {
            val flag: Boolean by ops.bitFlag(0b00000101, 2)
            flag shouldBe true
        }
        test("bit 31 set (msb)") {
            val flag: Boolean by ops.bitFlag(Int.MIN_VALUE, 31)
            flag shouldBe true
        }
        test("bit 31 clear") {
            val flag: Boolean by ops.bitFlag(0b00000001, 31)
            flag shouldBe false
        }
        test("allClear: no bit is set") {
            for (i in 0 until 32) {
                val flag: Boolean by ops.bitFlag(0, i)
                flag shouldBe false
            }
        }
        test("allSet: every bit is set") {
            for (i in 0 until 32) {
                val flag: Boolean by ops.bitFlag(-1, i)
                flag shouldBe true
            }
        }
    }

    context("bitFlag out-of-range (Int)") {
        val ops = BitCollection.int

        test("bit == sizeBits throws") {
            shouldThrow<IllegalArgumentException> { ops.bitFlag(0, 32) }
        }
        test("bit > sizeBits throws") {
            shouldThrow<IllegalArgumentException> { ops.bitFlag(0, 33) }
        }
        test("bit negative throws") {
            shouldThrow<IllegalArgumentException> { ops.bitFlag(0, -1) }
        }
    }

    context("mutableBitFlag out-of-range (Int)") {
        val ops = BitCollection.int
        var raw = 0

        test("bit == sizeBits throws") {
            shouldThrow<IllegalArgumentException> { ops.mutableBitFlag({ raw }, { raw = it }, 32) }
        }
        test("bit negative throws") {
            shouldThrow<IllegalArgumentException> { ops.mutableBitFlag({ raw }, { raw = it }, -1) }
        }
    }

    // ── bitFlag(getter, bit) ──────────────────────────────────────────────────

    context("bitFlag supplier (UByte)") {
        val ops = BitCollection.ubyte
        var raw: UByte = 0b00000101u.toUByte()
        val flag0: Boolean by ops.bitFlag({ raw }, 0)
        val flag1: Boolean by ops.bitFlag({ raw }, 1)

        test("reads current value of backing field") {
            flag0 shouldBe true
            flag1 shouldBe false
        }
        test("reflects update to backing field") {
            raw = 0b00000010u.toUByte()
            flag0 shouldBe false
            flag1 shouldBe true
        }
    }

    // ── mutableBitFlag ────────────────────────────────────────────────────────

    context("mutableBitFlag (UInt)") {
        val ops = BitCollection.uint
        var raw: UInt = 0u

        var flag0: Boolean by ops.mutableBitFlag({ raw }, { raw = it }, 0)
        var flag3: Boolean by ops.mutableBitFlag({ raw }, { raw = it }, 3)

        test("initial state: bits clear") {
            flag0 shouldBe false
            flag3 shouldBe false
        }
        test("set bit 0") {
            flag0 = true
            raw shouldBe 0b00000001u
            flag0 shouldBe true
            flag3 shouldBe false
        }
        test("set bit 3") {
            flag3 = true
            raw shouldBe 0b00001001u
            flag0 shouldBe true
            flag3 shouldBe true
        }
        test("clear bit 0") {
            flag0 = false
            raw shouldBe 0b00001000u
            flag0 shouldBe false
            flag3 shouldBe true
        }
        test("setting already-set bit is idempotent") {
            flag3 = true
            raw shouldBe 0b00001000u
        }
        test("clearing already-clear bit is idempotent") {
            flag0 = false
            raw shouldBe 0b00001000u
        }
    }

    context("mutableBitFlag msb (Long)") {
        val ops = BitCollection.long
        var raw: Long = 0L
        var msb: Boolean by ops.mutableBitFlag({ raw }, { raw = it }, 63)

        test("set msb") {
            msb = true
            raw shouldBe Long.MIN_VALUE
            msb shouldBe true
        }
        test("clear msb") {
            msb = false
            raw shouldBe 0L
            msb shouldBe false
        }
    }

    // ── bitRange ──────────────────────────────────────────────────────────────

    context("bitRange (UByte)") {
        val ops = BitCollection.ubyte

        test("extract bits [0, 3) — low nibble fragment") {
            // 0b10110101: bits 0-2 = 0b101 = 5
            val field: UByte by ops.bitRange({ 0b10110101u.toUByte() }, start = 0, count = 3)
            field shouldBe 5u.toUByte()
        }
        test("extract bits [4, 8) — high nibble") {
            // 0b10110101: bits 4-7 = 0b1011 = 11
            val field: UByte by ops.bitRange({ 0b10110101u.toUByte() }, start = 4, count = 4)
            field shouldBe 11u.toUByte()
        }
        test("extract single bit as range (start=2, count=1)") {
            // 0b00000100: bit 2 is set → range value = 1
            val field: UByte by ops.bitRange({ 0b00000100u.toUByte() }, start = 2, count = 1)
            field shouldBe 1u.toUByte()
        }
        test("extract full word (start=0, count=8)") {
            val raw = 0b10101010u.toUByte()
            val field: UByte by ops.bitRange({ raw }, start = 0, count = 8)
            field shouldBe raw
        }
        test("bits outside range do not bleed in") {
            // 0b11110000: bits [2, 5) = bits 2,3,4 = 0b100 = 4 (bit4=1, bits2-3=0)
            val field: UByte by ops.bitRange({ 0b11110000u.toUByte() }, start = 2, count = 3)
            field shouldBe 4u.toUByte()
        }
        test("supplier is re-evaluated on each access") {
            var raw: UByte = 0b00000011u.toUByte()
            val field: UByte by ops.bitRange({ raw }, start = 0, count = 4)
            field shouldBe 3u.toUByte()
            raw = 0b00001001u.toUByte()
            field shouldBe 9u.toUByte()
        }
        test("invalid: start negative") {
            shouldThrow<IllegalArgumentException> { ops.bitRange({ 0u.toUByte() }, start = -1, count = 3) }
        }
        test("invalid: count zero") {
            shouldThrow<IllegalArgumentException> { ops.bitRange({ 0u.toUByte() }, start = 0, count = 0) }
        }
        test("invalid: range exceeds sizeBits") {
            shouldThrow<IllegalArgumentException> { ops.bitRange({ 0u.toUByte() }, start = 6, count = 3) }
        }
    }

    context("bitRange boundary (Int)") {
        val ops = BitCollection.int

        test("extract bits [28, 32) — top nibble") {
            // 0xA0000000: bits 28-31 = 0xA = 10
            val field: Int by ops.bitRange({ 0xA0000000.toInt() }, start = 28, count = 4)
            field shouldBe 10
        }
        test("MSB only (start=31, count=1)") {
            val field: Int by ops.bitRange({ Int.MIN_VALUE }, start = 31, count = 1)
            field shouldBe 1
        }
    }

    // ── mutableBitRange ───────────────────────────────────────────────────────

    context("mutableBitRange (UInt)") {
        val ops = BitCollection.uint
        var raw: UInt = 0u

        var nibble: UInt by ops.mutableBitRange({ raw }, { raw = it }, start = 4, count = 4)

        test("initial value is zero") {
            nibble shouldBe 0u
        }
        test("write a value into the range") {
            nibble = 0b1010u
            raw shouldBe 0b10100000u
            nibble shouldBe 0b1010u
        }
        test("write leaves bits outside the range unchanged") {
            raw = 0b11111111_00001111u  // bits 4-7 = 0, others set
            nibble = 0b0110u
            // bits 4-7 become 0110; all others unchanged
            raw shouldBe 0b11111111_01101111u
            nibble shouldBe 0b0110u
        }
        test("extra bits in supplied value are masked off") {
            raw = 0u
            nibble = 0b11111111u  // only 4 bits fit; top 4 should be discarded
            raw shouldBe 0b11110000u
            nibble shouldBe 0b1111u
        }
        test("write zero clears the range") {
            raw = 0b11111111u
            nibble = 0u
            raw shouldBe 0b00001111u
            nibble shouldBe 0u
        }
    }

    context("mutableBitRange single-bit range matches mutableBitFlag (ULong)") {
        val ops = BitCollection.ulong
        var raw: ULong = 0uL
        var viaRange: ULong by ops.mutableBitRange({ raw }, { raw = it }, start = 7, count = 1)
        var viaFlag: Boolean by ops.mutableBitFlag({ raw }, { raw = it }, 7)

        test("set via range, read via flag") {
            viaRange = 1uL
            viaFlag shouldBe true
        }
        test("clear via flag, read via range") {
            viaFlag = false
            viaRange shouldBe 0uL
        }
    }
})
