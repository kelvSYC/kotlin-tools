package com.kelvsyc.kotlin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ConversionExtensionsTest : FunSpec({

    // ── BooleanArray → scalar ─────────────────────────────────────────────────

    context("BooleanArray.toByte") {
        test("all-false produces 0") {
            BooleanArray(8) { false }.toByte() shouldBe 0.toByte()
        }
        test("index 0 maps to bit 0 (value 1)") {
            booleanArrayOf(true, false, false, false, false, false, false, false).toByte() shouldBe 1.toByte()
        }
        test("index 1 maps to bit 1 (value 2)") {
            booleanArrayOf(false, true, false, false, false, false, false, false).toByte() shouldBe 2.toByte()
        }
        test("index 7 maps to the sign bit") {
            booleanArrayOf(false, false, false, false, false, false, false, true).toByte() shouldBe Byte.MIN_VALUE
        }
        test("all-true produces -1") {
            BooleanArray(8) { true }.toByte() shouldBe (-1).toByte()
        }
        test("shorter array uses only available bits") {
            booleanArrayOf(true).toByte() shouldBe 1.toByte()
        }
    }

    context("BooleanArray.toShort") {
        test("index 0 maps to bit 0") {
            BooleanArray(16) { it == 0 }.toShort() shouldBe 1.toShort()
        }
        test("index 15 maps to the sign bit") {
            BooleanArray(16) { it == 15 }.toShort() shouldBe Short.MIN_VALUE
        }
        test("all-true produces -1") {
            BooleanArray(16) { true }.toShort() shouldBe (-1).toShort()
        }
    }

    context("BooleanArray.toInt") {
        test("index 0 maps to bit 0") {
            BooleanArray(32) { it == 0 }.toInt() shouldBe 1
        }
        test("index 31 maps to the sign bit") {
            BooleanArray(32) { it == 31 }.toInt() shouldBe Int.MIN_VALUE
        }
        test("all-true produces -1") {
            BooleanArray(32) { true }.toInt() shouldBe -1
        }
    }

    context("BooleanArray.toLong") {
        test("index 0 maps to bit 0") {
            BooleanArray(64) { it == 0 }.toLong() shouldBe 1L
        }
        test("index 63 maps to the sign bit") {
            BooleanArray(64) { it == 63 }.toLong() shouldBe Long.MIN_VALUE
        }
        test("all-true produces -1") {
            BooleanArray(64) { true }.toLong() shouldBe -1L
        }
    }

    // ── ByteArray → wider scalar ──────────────────────────────────────────────

    context("ByteArray.toShort") {
        test("index 0 is the least significant byte") {
            byteArrayOf(1, 0).toShort() shouldBe 1.toShort()
        }
        test("index 1 is the high byte") {
            byteArrayOf(0, 1).toShort() shouldBe 256.toShort()
        }
        test("0xFF bytes produce -1") {
            byteArrayOf(-1, -1).toShort() shouldBe (-1).toShort()
        }
        test("0x12 0x34 produces 0x3412") {
            byteArrayOf(0x12, 0x34).toShort() shouldBe 0x3412.toShort()
        }
    }

    context("ByteArray.toInt") {
        test("little-endian byte order: 0x78 0x56 0x34 0x12 → 0x12345678") {
            byteArrayOf(0x78, 0x56, 0x34, 0x12).toInt() shouldBe 0x12345678
        }
        test("all 0xFF bytes produce -1") {
            byteArrayOf(-1, -1, -1, -1).toInt() shouldBe -1
        }
        test("high byte only") {
            byteArrayOf(0, 0, 0, 1).toInt() shouldBe 0x01000000
        }
    }

    context("ByteArray.toLong") {
        test("little-endian byte order") {
            byteArrayOf(0x78, 0x56, 0x34, 0x12, 0, 0, 0, 0).toLong() shouldBe 0x12345678L
        }
        test("all 0xFF bytes produce -1") {
            ByteArray(8) { -1 }.toLong() shouldBe -1L
        }
        test("all 8 bytes contribute (fixes original 4-byte bug)") {
            byteArrayOf(0, 0, 0, 0, 1, 0, 0, 0).toLong() shouldBe 0x0000_0001_0000_0000L
        }
    }

    // ── ShortArray / IntArray → wider scalar ──────────────────────────────────

    context("ShortArray.toInt") {
        test("index 0 is the least significant short") {
            shortArrayOf(1, 0).toInt() shouldBe 1
        }
        test("index 1 is the high short") {
            shortArrayOf(0, 1).toInt() shouldBe 0x00010000
        }
        test("0x5678 0x1234 → 0x12345678") {
            shortArrayOf(0x5678.toShort(), 0x1234.toShort()).toInt() shouldBe 0x12345678
        }
    }

    context("ShortArray.toLong") {
        test("index 0 is the least significant short") {
            shortArrayOf(1, 0, 0, 0).toLong() shouldBe 1L
        }
        test("0x5678 0x1234 in low position") {
            shortArrayOf(0x5678.toShort(), 0x1234.toShort(), 0, 0).toLong() shouldBe 0x12345678L
        }
    }

    context("IntArray.toLong") {
        test("index 0 is the least significant int") {
            intArrayOf(1, 0).toLong() shouldBe 1L
        }
        test("index 1 is the high int") {
            intArrayOf(0, 1).toLong() shouldBe 0x1_0000_0000L
        }
        test("0x12345678 0x0FEDCBA9 → correct Long") {
            intArrayOf(0x12345678, 0x0FEDCBA9).toLong() shouldBe 0x0FEDCBA9_12345678L
        }
    }

    // ── Scalar → BooleanArray ─────────────────────────────────────────────────

    context("Byte.toBooleanArray") {
        test("0 produces all-false") {
            0.toByte().toBooleanArray() shouldBe BooleanArray(8) { false }
        }
        test("1 sets only index 0") {
            1.toByte().toBooleanArray() shouldBe BooleanArray(8) { it == 0 }
        }
        test("-1 (all bits set) produces all-true") {
            (-1).toByte().toBooleanArray() shouldBe BooleanArray(8) { true }
        }
        test("result size is Byte.SIZE_BITS") {
            42.toByte().toBooleanArray().size shouldBe Byte.SIZE_BITS
        }
    }

    context("Short.toBooleanArray") {
        test("1 sets only index 0") {
            1.toShort().toBooleanArray() shouldBe BooleanArray(16) { it == 0 }
        }
        test("result size is Short.SIZE_BITS") {
            0.toShort().toBooleanArray().size shouldBe Short.SIZE_BITS
        }
    }

    context("Int.toBooleanArray") {
        test("1 sets only index 0") {
            1.toBooleanArray() shouldBe BooleanArray(32) { it == 0 }
        }
        test("MIN_VALUE sets only index 31") {
            Int.MIN_VALUE.toBooleanArray() shouldBe BooleanArray(32) { it == 31 }
        }
        test("result size is Int.SIZE_BITS") {
            0.toBooleanArray().size shouldBe Int.SIZE_BITS
        }
    }

    context("Long.toBooleanArray") {
        test("1L sets only index 0") {
            1L.toBooleanArray() shouldBe BooleanArray(64) { it == 0 }
        }
        test("MIN_VALUE sets only index 63") {
            Long.MIN_VALUE.toBooleanArray() shouldBe BooleanArray(64) { it == 63 }
        }
        test("result size is Long.SIZE_BITS") {
            0L.toBooleanArray().size shouldBe Long.SIZE_BITS
        }
    }

    // ── Scalar → byte-width arrays ────────────────────────────────────────────

    context("Short.toByteArray") {
        test("little-endian: 0x1234 → [0x34, 0x12]") {
            0x1234.toShort().toByteArray() shouldBe byteArrayOf(0x34, 0x12)
        }
        test("result size is Short.SIZE_BYTES") {
            0.toShort().toByteArray().size shouldBe Short.SIZE_BYTES
        }
    }

    context("Int.toByteArray") {
        test("little-endian: 0x12345678 → [0x78, 0x56, 0x34, 0x12]") {
            0x12345678.toByteArray() shouldBe byteArrayOf(0x78, 0x56, 0x34, 0x12)
        }
        test("result size is Int.SIZE_BYTES") {
            0.toByteArray().size shouldBe Int.SIZE_BYTES
        }
    }

    context("Int.toShortArray") {
        test("little-endian: 0x12345678 → [0x5678, 0x1234]") {
            0x12345678.toShortArray() shouldBe shortArrayOf(0x5678.toShort(), 0x1234.toShort())
        }
        test("result size is Int.SIZE_BYTES / Short.SIZE_BYTES") {
            0.toShortArray().size shouldBe Int.SIZE_BYTES / Short.SIZE_BYTES
        }
    }

    context("Long.toByteArray") {
        test("little-endian: 0x12345678 in low position") {
            0x12345678L.toByteArray() shouldBe byteArrayOf(0x78, 0x56, 0x34, 0x12, 0, 0, 0, 0)
        }
        test("result size is Long.SIZE_BYTES") {
            0L.toByteArray().size shouldBe Long.SIZE_BYTES
        }
    }

    context("Long.toShortArray") {
        test("little-endian ordering") {
            0x12345678L.toShortArray() shouldBe shortArrayOf(0x5678.toShort(), 0x1234.toShort(), 0, 0)
        }
        test("result size is Long.SIZE_BYTES / Short.SIZE_BYTES") {
            0L.toShortArray().size shouldBe Long.SIZE_BYTES / Short.SIZE_BYTES
        }
    }

    context("Long.toIntArray") {
        test("little-endian: 0x0FEDCBA9_12345678L → [0x12345678, 0x0FEDCBA9]") {
            0x0FEDCBA9_12345678L.toIntArray() shouldBe intArrayOf(0x12345678, 0x0FEDCBA9)
        }
        test("result size is Long.SIZE_BYTES / Int.SIZE_BYTES") {
            0L.toIntArray().size shouldBe Long.SIZE_BYTES / Int.SIZE_BYTES
        }
    }

    // ── Round-trips ───────────────────────────────────────────────────────────

    context("round-trips") {
        test("Byte → BooleanArray → Byte") {
            val values = listOf(0, 1, 42, 127, -1, -128)
            values.forEach { v ->
                v.toByte().toBooleanArray().toByte() shouldBe v.toByte()
            }
        }
        test("Int → ByteArray → Int") {
            val values = listOf(0, 1, 0x12345678, -1, Int.MIN_VALUE, Int.MAX_VALUE)
            values.forEach { v ->
                v.toByteArray().toInt() shouldBe v
            }
        }
        test("Int → ShortArray → Int") {
            val values = listOf(0, 1, 0x12345678, -1, Int.MIN_VALUE, Int.MAX_VALUE)
            values.forEach { v ->
                v.toShortArray().toInt() shouldBe v
            }
        }
        test("Long → ByteArray → Long") {
            val values = listOf(0L, 1L, 0x0FEDCBA9_12345678L, -1L, Long.MIN_VALUE, Long.MAX_VALUE)
            values.forEach { v ->
                v.toByteArray().toLong() shouldBe v
            }
        }
        test("Long → IntArray → Long") {
            val values = listOf(0L, 1L, 0x0FEDCBA9_12345678L, -1L, Long.MIN_VALUE, Long.MAX_VALUE)
            values.forEach { v ->
                v.toIntArray().toLong() shouldBe v
            }
        }
    }
})
