package com.kelvsyc.kotlin.core.traits

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.math.BigInteger

class IntegralBigNumberExtensionsTest : FunSpec({

    // ── SignedIntegral.toBigInteger ────────────────────────────────────────────

    context("SignedIntegral.toBigInteger") {
        test("Int32: zero") { Int32.toBigInteger(0) shouldBe BigInteger.ZERO }
        test("Int32: positive") { Int32.toBigInteger(42) shouldBe BigInteger.valueOf(42) }
        test("Int32: negative") { Int32.toBigInteger(-1) shouldBe BigInteger.valueOf(-1) }
        test("Int32: MIN_VALUE") { Int32.toBigInteger(Int.MIN_VALUE) shouldBe BigInteger.valueOf(Int.MIN_VALUE.toLong()) }
        test("Int32: MAX_VALUE") { Int32.toBigInteger(Int.MAX_VALUE) shouldBe BigInteger.valueOf(Int.MAX_VALUE.toLong()) }
        test("Int64: MIN_VALUE") { Int64.toBigInteger(Long.MIN_VALUE) shouldBe BigInteger.valueOf(Long.MIN_VALUE) }
        test("Int64: MAX_VALUE") { Int64.toBigInteger(Long.MAX_VALUE) shouldBe BigInteger.valueOf(Long.MAX_VALUE) }
        test("Int8: negative byte") { Int8.toBigInteger((-1).toByte()) shouldBe BigInteger.valueOf(-1) }
        test("Int16: negative short") { Int16.toBigInteger((-1).toShort()) shouldBe BigInteger.valueOf(-1) }
    }

    // ── SignedIntegral.toBigDecimal ────────────────────────────────────────────

    context("SignedIntegral.toBigDecimal") {
        test("Int32: zero") { Int32.toBigDecimal(0) shouldBe BigDecimal.ZERO }
        test("Int32: positive") { Int32.toBigDecimal(42) shouldBe BigDecimal.valueOf(42) }
        test("Int32: negative") { Int32.toBigDecimal(-7) shouldBe BigDecimal.valueOf(-7) }
        test("Int64: MIN_VALUE") { Int64.toBigDecimal(Long.MIN_VALUE) shouldBe BigDecimal.valueOf(Long.MIN_VALUE) }
    }

    // ── UnsignedIntegral.toBigInteger ─────────────────────────────────────────

    context("UnsignedIntegral.toBigInteger") {
        test("UInt32: zero") { UInt32.toBigInteger(0u) shouldBe BigInteger.ZERO }
        test("UInt32: MAX_VALUE (high bit of Int clear)") {
            UInt32.toBigInteger(UInt.MAX_VALUE) shouldBe BigInteger.valueOf(4294967295L)
        }
        test("UInt8: MAX_VALUE") {
            UInt8.toBigInteger(UByte.MAX_VALUE) shouldBe BigInteger.valueOf(255L)
        }
        test("UInt16: MAX_VALUE") {
            UInt16.toBigInteger(UShort.MAX_VALUE) shouldBe BigInteger.valueOf(65535L)
        }
        test("UInt64: zero") { UInt64.toBigInteger(0uL) shouldBe BigInteger.ZERO }
        test("UInt64: Long.MAX_VALUE as ULong (high bit clear)") {
            UInt64.toBigInteger(Long.MAX_VALUE.toULong()) shouldBe BigInteger.valueOf(Long.MAX_VALUE)
        }
        test("UInt64: Long.MAX_VALUE + 1 as ULong (high bit set)") {
            // 2^63, the first ULong value that cannot be represented as a non-negative Long
            val value = Long.MAX_VALUE.toULong() + 1uL
            UInt64.toBigInteger(value) shouldBe BigInteger.ONE.shiftLeft(63)
        }
        test("UInt64: MAX_VALUE = 2^64 - 1") {
            UInt64.toBigInteger(ULong.MAX_VALUE) shouldBe BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE)
        }
    }

    // ── UnsignedIntegral.toBigDecimal ─────────────────────────────────────────

    context("UnsignedIntegral.toBigDecimal") {
        test("UInt32: zero") { UInt32.toBigDecimal(0u) shouldBe BigDecimal.ZERO }
        test("UInt32: MAX_VALUE") {
            UInt32.toBigDecimal(UInt.MAX_VALUE) shouldBe BigDecimal.valueOf(4294967295L)
        }
        test("UInt64: MAX_VALUE") {
            UInt64.toBigDecimal(ULong.MAX_VALUE) shouldBe
                BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE).toBigDecimal()
        }
    }
})
