package com.kelvsyc.kotlin.guava

import com.google.common.primitives.UnsignedInteger
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class UnsignedIntegerExtensionsTest : FunSpec({

    context("operator div") {
        test("10 / 3 = 3") {
            val a = UnsignedInteger.valueOf(10L)
            val b = UnsignedInteger.valueOf(3L)
            (a / b) shouldBe UnsignedInteger.valueOf(3L)
        }

        test("large value: MAX_VALUE / 2") {
            val max = UnsignedInteger.MAX_VALUE
            val two = UnsignedInteger.valueOf(2L)
            (max / two) shouldBe UnsignedInteger.fromIntBits(Int.MAX_VALUE)
        }
    }

    context("operator rem") {
        test("10 % 3 = 1") {
            val a = UnsignedInteger.valueOf(10L)
            val b = UnsignedInteger.valueOf(3L)
            (a % b) shouldBe UnsignedInteger.valueOf(1L)
        }

        test("9 % 3 = 0") {
            val a = UnsignedInteger.valueOf(9L)
            val b = UnsignedInteger.valueOf(3L)
            (a % b) shouldBe UnsignedInteger.ZERO
        }
    }

    context("UInt.toUnsignedInteger()") {
        test("zero") {
            0u.toUnsignedInteger() shouldBe UnsignedInteger.ZERO
        }

        test("one") {
            1u.toUnsignedInteger() shouldBe UnsignedInteger.ONE
        }

        test("MAX_VALUE round-trips") {
            UInt.MAX_VALUE.toUnsignedInteger() shouldBe UnsignedInteger.MAX_VALUE
        }

        test("mid-range value") {
            100u.toUnsignedInteger() shouldBe UnsignedInteger.valueOf(100L)
        }
    }

    context("UnsignedInteger.toUInt()") {
        test("zero") {
            UnsignedInteger.ZERO.toUInt() shouldBe 0u
        }

        test("one") {
            UnsignedInteger.ONE.toUInt() shouldBe 1u
        }

        test("MAX_VALUE round-trips") {
            UnsignedInteger.MAX_VALUE.toUInt() shouldBe UInt.MAX_VALUE
        }

        test("mid-range value") {
            UnsignedInteger.valueOf(100L).toUInt() shouldBe 100u
        }
    }

    context("uIntToUnsignedInteger") {
        val conv = uIntToUnsignedInteger

        test("forward 0u") {
            conv(0u) shouldBe UnsignedInteger.ZERO
        }

        test("forward MAX_VALUE") {
            conv(UInt.MAX_VALUE) shouldBe UnsignedInteger.MAX_VALUE
        }

        test("backward ZERO") {
            conv.reverse(UnsignedInteger.ZERO) shouldBe 0u
        }

        test("backward MAX_VALUE") {
            conv.reverse(UnsignedInteger.MAX_VALUE) shouldBe UInt.MAX_VALUE
        }

        test("round-trip forward then backward") {
            val v = 123456u
            conv.reverse(conv(v)) shouldBe v
        }

        test("round-trip backward then forward") {
            val v = UnsignedInteger.valueOf(654321L)
            conv(conv.reverse(v)) shouldBe v
        }

        test("singleton identity") {
            uIntToUnsignedInteger shouldBe uIntToUnsignedInteger
        }
    }
})
