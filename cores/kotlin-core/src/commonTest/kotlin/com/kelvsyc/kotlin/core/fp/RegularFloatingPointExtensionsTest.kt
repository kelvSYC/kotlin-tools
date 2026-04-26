package com.kelvsyc.kotlin.core.fp

import com.kelvsyc.kotlin.core.Float16
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RegularFloatingPointExtensionsTest : FunSpec({

    // ── Float → RegularBinaryFloatingPoint ────────────────────────────────────

    context("Float.toRegularBinaryFloatingPoint") {
        test("+0 produces zero significand and subnormal exponent") {
            val r = 0.0f.toRegularBinaryFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -149
            r.significand shouldBe 0u
        }

        test("-0 sets the sign flag") {
            val r = (-0.0f).toRegularBinaryFloatingPoint()
            r.sign shouldBe true
            r.significand shouldBe 0u
        }

        test("1.0 encodes as normal with implicit leading bit") {
            val r = 1.0f.toRegularBinaryFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -23          // 127 - 150
            r.significand shouldBe 0x800000u // implicit 1 + zero fraction
        }

        test("-1.0 sets the sign flag") {
            val r = (-1.0f).toRegularBinaryFloatingPoint()
            r.sign shouldBe true
            r.exponent shouldBe -23
            r.significand shouldBe 0x800000u
        }

        test("2.0 has exponent one higher than 1.0") {
            val r = 2.0f.toRegularBinaryFloatingPoint()
            r.exponent shouldBe -22
            r.significand shouldBe 0x800000u
        }

        test("1.5 encodes fraction bits correctly") {
            val r = 1.5f.toRegularBinaryFloatingPoint()
            r.exponent shouldBe -23
            r.significand shouldBe 0xC00000u  // 1.1 in binary × 2^0
        }

        test("subnormal (Float.MIN_VALUE) encodes without implicit leading bit") {
            val r = Float.MIN_VALUE.toRegularBinaryFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -149
            r.significand shouldBe 1u
        }

        test("throws for positive infinity") {
            shouldThrow<IllegalArgumentException> { Float.POSITIVE_INFINITY.toRegularBinaryFloatingPoint() }
        }

        test("throws for negative infinity") {
            shouldThrow<IllegalArgumentException> { Float.NEGATIVE_INFINITY.toRegularBinaryFloatingPoint() }
        }

        test("throws for NaN") {
            shouldThrow<IllegalArgumentException> { Float.NaN.toRegularBinaryFloatingPoint() }
        }
    }

    // ── RegularBinaryFloatingPoint<UInt> → Float ──────────────────────────────

    context("RegularBinaryFloatingPoint<UInt>.toFloat") {
        test("+0 (zero significand, positive sign) returns +0.0f") {
            val r = RegularBinaryFloatingPoint(false, -149, 0u)
            r.toFloat() shouldBe 0.0f
            r.toFloat().toRawBits() shouldBe 0  // not -0
        }

        test("-0 (zero significand, negative sign) returns -0.0f") {
            val r = RegularBinaryFloatingPoint(true, -149, 0u)
            r.toFloat().toRawBits() shouldBe (-0.0f).toRawBits()
        }

        test("normal: exp=-23, sig=0x800000 reconstructs 1.0f") {
            RegularBinaryFloatingPoint(false, -23, 0x800000u).toFloat() shouldBe 1.0f
        }

        test("normal: exp=-22, sig=0x800000 reconstructs 2.0f") {
            RegularBinaryFloatingPoint(false, -22, 0x800000u).toFloat() shouldBe 2.0f
        }

        test("subnormal: exp=-149, sig=1 reconstructs Float.MIN_VALUE") {
            RegularBinaryFloatingPoint(false, -149, 1u).toFloat() shouldBe Float.MIN_VALUE
        }

        test("overflow produces positive infinity") {
            RegularBinaryFloatingPoint(false, 128, 0x800000u).toFloat() shouldBe Float.POSITIVE_INFINITY
        }

        test("overflow produces negative infinity for negative sign") {
            RegularBinaryFloatingPoint(true, 128, 0x800000u).toFloat() shouldBe Float.NEGATIVE_INFINITY
        }

        test("underflow produces +0.0f") {
            RegularBinaryFloatingPoint(false, -200, 1u).toFloat() shouldBe 0.0f
        }
    }

    context("Float round-trip through RegularBinaryFloatingPoint") {
        listOf(1.0f, -1.0f, 2.0f, 0.5f, 1.5f, Float.MIN_VALUE, Float.MAX_VALUE).forEach { v ->
            test("$v round-trips") {
                v.toRegularBinaryFloatingPoint().toFloat() shouldBe v
            }
        }
    }

    // ── Double → RegularBinaryFloatingPoint ───────────────────────────────────

    context("Double.toRegularBinaryFloatingPoint") {
        test("1.0 encodes with correct bias and implicit leading bit") {
            val r = 1.0.toRegularBinaryFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -52           // 1023 - 1075
            r.significand shouldBe 0x10000000000000uL
        }

        test("subnormal (Double.MIN_VALUE) encodes without implicit leading bit") {
            val r = Double.MIN_VALUE.toRegularBinaryFloatingPoint()
            r.exponent shouldBe -1074
            r.significand shouldBe 1uL
        }

        test("throws for NaN") {
            shouldThrow<IllegalArgumentException> { Double.NaN.toRegularBinaryFloatingPoint() }
        }
    }

    context("Double round-trip through RegularBinaryFloatingPoint") {
        listOf(1.0, -1.0, 2.0, 0.5, Double.MIN_VALUE, Double.MAX_VALUE).forEach { v ->
            test("$v round-trips") {
                v.toRegularBinaryFloatingPoint().toDouble() shouldBe v
            }
        }
    }

    // ── Float16 → RegularBinaryFloatingPoint ──────────────────────────────────

    context("Float16.toRegularBinaryFloatingPoint") {
        test("+0 produces zero significand") {
            val r = Float16(0).toRegularBinaryFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -24
            r.significand shouldBe 0u.toUShort()
        }

        test("-0 sets the sign flag") {
            val r = (-Float16(0)).toRegularBinaryFloatingPoint()
            r.sign shouldBe true
            r.significand shouldBe 0u.toUShort()
        }

        test("1.0 encodes with implicit leading bit and exponent -10") {
            val r = Float16(0x3C00.toShort()).toRegularBinaryFloatingPoint()
            r.sign shouldBe false
            r.exponent shouldBe -10           // 15 - 25
            r.significand shouldBe 1024u.toUShort()  // 2^10
        }

        test("-1.0 sets the sign flag") {
            val r = Float16(0xBC00.toShort()).toRegularBinaryFloatingPoint()
            r.sign shouldBe true
            r.exponent shouldBe -10
        }

        test("2.0 has exponent one higher than 1.0") {
            val r = Float16(0x4000.toShort()).toRegularBinaryFloatingPoint()
            r.exponent shouldBe -9
            r.significand shouldBe 1024u.toUShort()
        }

        test("MIN_VALUE (smallest subnormal) encodes as significand 1 at exp -24") {
            val r = Float16.MIN_VALUE.toRegularBinaryFloatingPoint()
            r.exponent shouldBe -24
            r.significand shouldBe 1u.toUShort()
        }

        test("MAX_VALUE encodes with full significand") {
            val r = Float16.MAX_VALUE.toRegularBinaryFloatingPoint()
            r.exponent shouldBe 5             // 30 - 25
            r.significand shouldBe 2047u.toUShort()  // 0x7FF = 1024 + 1023
        }

        test("throws for positive infinity") {
            shouldThrow<IllegalArgumentException> { Float16.POSITIVE_INFINITY.toRegularBinaryFloatingPoint() }
        }

        test("throws for NaN") {
            shouldThrow<IllegalArgumentException> { Float16.NaN.toRegularBinaryFloatingPoint() }
        }
    }

    // ── RegularBinaryFloatingPoint<UShort> → Float16 ─────────────────────────

    context("RegularBinaryFloatingPoint<UShort>.toFloat16") {
        test("+0 (zero significand) returns +0") {
            val r = RegularBinaryFloatingPoint(false, -24, 0u.toUShort())
            r.toFloat16().bits shouldBe 0.toShort()
        }

        test("-0 (zero significand, negative sign) returns -0") {
            val r = RegularBinaryFloatingPoint(true, -24, 0u.toUShort())
            r.toFloat16().bits shouldBe 0x8000.toShort()
        }

        test("exp=-10, sig=1024 reconstructs 1.0") {
            RegularBinaryFloatingPoint(false, -10, 1024u.toUShort()).toFloat16().bits shouldBe 0x3C00.toShort()
        }

        test("exp=-10, sig=1024 with negative sign reconstructs -1.0") {
            RegularBinaryFloatingPoint(true, -10, 1024u.toUShort()).toFloat16().bits shouldBe 0xBC00.toShort()
        }

        test("overflow produces positive infinity") {
            RegularBinaryFloatingPoint(false, 16, 1024u.toUShort()).toFloat16().bits shouldBe 0x7C00.toShort()
        }

        test("underflow produces +0") {
            RegularBinaryFloatingPoint(false, -100, 1u.toUShort()).toFloat16().bits shouldBe 0.toShort()
        }

        test("subnormal result: exp=-24, sig=1 gives MIN_VALUE") {
            RegularBinaryFloatingPoint(false, -24, 1u.toUShort()).toFloat16().bits shouldBe 0x0001.toShort()
        }
    }

    context("Float16 round-trip through RegularBinaryFloatingPoint") {
        val cases = listOf(
            Float16(0x3C00.toShort()),  // 1.0
            Float16(0xBC00.toShort()),  // -1.0
            Float16(0x4000.toShort()),  // 2.0
            Float16(0x3800.toShort()),  // 0.5
            Float16.MIN_VALUE,
            Float16.MIN_NORMAL,
            Float16.MAX_VALUE,
            Float16(0x0200.toShort()),  // subnormal with multiple bits
        )
        cases.forEach { v ->
            test("${v.bits.toInt().and(0xFFFF).toString(16)} round-trips") {
                v.toRegularBinaryFloatingPoint().toFloat16().bits shouldBe v.bits
            }
        }
    }
})
