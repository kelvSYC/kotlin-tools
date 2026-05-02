package com.kelvsyc.kotlin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class UnsignedConversionExtensionsTest : FunSpec({

    // ── BooleanArray → unsigned scalar ────────────────────────────────────────

    context("BooleanArray.toUByte") {
        test("all-false produces 0u") {
            BooleanArray(8) { false }.toUByte() shouldBe 0u.toUByte()
        }
        test("all-true produces UByte.MAX_VALUE") {
            BooleanArray(8) { true }.toUByte() shouldBe UByte.MAX_VALUE
        }
        test("index 7 (MSB) does not produce a negative value") {
            booleanArrayOf(false, false, false, false, false, false, false, true).toUByte() shouldBe 128u.toUByte()
        }
        test("matches toByte().toUByte()") {
            val bits = BooleanArray(8) { it % 3 == 0 }
            bits.toUByte() shouldBe bits.toByte().toUByte()
        }
    }

    context("BooleanArray.toUShort") {
        test("all-true produces UShort.MAX_VALUE") {
            BooleanArray(16) { true }.toUShort() shouldBe UShort.MAX_VALUE
        }
        test("matches toShort().toUShort()") {
            val bits = BooleanArray(16) { it % 2 == 0 }
            bits.toUShort() shouldBe bits.toShort().toUShort()
        }
    }

    context("BooleanArray.toUInt") {
        test("all-true produces UInt.MAX_VALUE") {
            BooleanArray(32) { true }.toUInt() shouldBe UInt.MAX_VALUE
        }
        test("index 31 sets the high bit without sign") {
            BooleanArray(32) { it == 31 }.toUInt() shouldBe 0x80000000u
        }
        test("matches toInt().toUInt()") {
            val bits = BooleanArray(32) { it % 5 == 0 }
            bits.toUInt() shouldBe bits.toInt().toUInt()
        }
    }

    context("BooleanArray.toULong") {
        test("all-true produces ULong.MAX_VALUE") {
            BooleanArray(64) { true }.toULong() shouldBe ULong.MAX_VALUE
        }
        test("index 63 sets the high bit without sign") {
            BooleanArray(64) { it == 63 }.toULong() shouldBe 0x8000000000000000uL
        }
        test("matches toLong().toULong()") {
            val bits = BooleanArray(64) { it % 7 == 0 }
            bits.toULong() shouldBe bits.toLong().toULong()
        }
    }

    // ── Unsigned scalar → BooleanArray ────────────────────────────────────────

    context("UByte.toBooleanArray") {
        test("0u produces all-false") {
            0u.toUByte().toBooleanArray() shouldBe BooleanArray(8) { false }
        }
        test("UByte.MAX_VALUE produces all-true") {
            UByte.MAX_VALUE.toBooleanArray() shouldBe BooleanArray(8) { true }
        }
        test("128u sets only index 7 (no sign extension)") {
            128u.toUByte().toBooleanArray() shouldBe BooleanArray(8) { it == 7 }
        }
        test("result size is UByte.SIZE_BITS") {
            42u.toUByte().toBooleanArray().size shouldBe UByte.SIZE_BITS
        }
    }

    context("UInt.toBooleanArray") {
        test("UInt.MAX_VALUE produces all-true") {
            UInt.MAX_VALUE.toBooleanArray() shouldBe BooleanArray(32) { true }
        }
        test("0x80000000u sets only index 31 (no sign extension)") {
            0x80000000u.toBooleanArray() shouldBe BooleanArray(32) { it == 31 }
        }
    }

    context("ULong.toBooleanArray") {
        test("ULong.MAX_VALUE produces all-true") {
            ULong.MAX_VALUE.toBooleanArray() shouldBe BooleanArray(64) { true }
        }
        test("high bit set without sign extension") {
            0x8000000000000000uL.toBooleanArray() shouldBe BooleanArray(64) { it == 63 }
        }
    }

    // ── Unsigned scalar → byte-width arrays ───────────────────────────────────

    context("UShort.toByteArray") {
        test("little-endian: 0x1234u → [0x34, 0x12]") {
            0x1234u.toUShort().toByteArray() shouldBe byteArrayOf(0x34, 0x12)
        }
        test("UShort.MAX_VALUE produces all -1 bytes") {
            UShort.MAX_VALUE.toByteArray() shouldBe byteArrayOf(-1, -1)
        }
        test("result size is UShort.SIZE_BYTES") {
            0u.toUShort().toByteArray().size shouldBe UShort.SIZE_BYTES
        }
    }

    context("UInt.toByteArray") {
        test("little-endian: 0x12345678u → [0x78, 0x56, 0x34, 0x12]") {
            0x12345678u.toByteArray() shouldBe byteArrayOf(0x78, 0x56, 0x34, 0x12)
        }
        test("UInt.MAX_VALUE produces all -1 bytes") {
            UInt.MAX_VALUE.toByteArray() shouldBe ByteArray(4) { -1 }
        }
    }

    context("UInt.toShortArray") {
        test("little-endian: 0x12345678u → [0x5678, 0x1234]") {
            0x12345678u.toShortArray() shouldBe shortArrayOf(0x5678.toShort(), 0x1234.toShort())
        }
    }

    context("ULong.toByteArray") {
        test("little-endian ordering") {
            0x12345678uL.toByteArray() shouldBe byteArrayOf(0x78, 0x56, 0x34, 0x12, 0, 0, 0, 0)
        }
        test("ULong.MAX_VALUE produces all -1 bytes") {
            ULong.MAX_VALUE.toByteArray() shouldBe ByteArray(8) { -1 }
        }
    }

    context("ULong.toIntArray") {
        test("little-endian: 0x0FEDCBA9_12345678uL → [0x12345678, 0x0FEDCBA9]") {
            0x0FEDCBA9_12345678uL.toIntArray() shouldBe intArrayOf(0x12345678, 0x0FEDCBA9)
        }
    }

    // ── Round-trips ───────────────────────────────────────────────────────────

    context("round-trips") {
        test("UByte → BooleanArray → UByte") {
            listOf(0u, 1u, 42u, 127u, 128u, UByte.MAX_VALUE).forEach { v ->
                v.toUByte().toBooleanArray().toUByte() shouldBe v.toUByte()
            }
        }
        test("UInt → ByteArray → Int (signed round-trip via bits)") {
            listOf(0u, 1u, 0x12345678u, UInt.MAX_VALUE).forEach { v ->
                v.toByteArray().toInt().toUInt() shouldBe v
            }
        }
        test("ULong → IntArray → Long (signed round-trip via bits)") {
            listOf(0uL, 1uL, 0x0FEDCBA9_12345678uL, ULong.MAX_VALUE).forEach { v ->
                v.toIntArray().toLong().toULong() shouldBe v
            }
        }
    }
})
