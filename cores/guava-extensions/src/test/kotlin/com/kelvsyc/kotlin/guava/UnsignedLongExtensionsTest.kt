package com.kelvsyc.kotlin.guava

import com.google.common.primitives.UnsignedLong
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class UnsignedLongExtensionsTest : FunSpec({

    context("operator div") {
        test("10 / 3 = 3") {
            val a = UnsignedLong.valueOf(10L)
            val b = UnsignedLong.valueOf(3L)
            (a / b) shouldBe UnsignedLong.valueOf(3L)
        }

        test("large value: MAX_VALUE / 2") {
            val max = UnsignedLong.MAX_VALUE
            val two = UnsignedLong.valueOf(2L)
            (max / two) shouldBe UnsignedLong.fromLongBits(Long.MAX_VALUE)
        }
    }

    context("operator rem") {
        test("10 % 3 = 1") {
            val a = UnsignedLong.valueOf(10L)
            val b = UnsignedLong.valueOf(3L)
            (a % b) shouldBe UnsignedLong.valueOf(1L)
        }

        test("9 % 3 = 0") {
            val a = UnsignedLong.valueOf(9L)
            val b = UnsignedLong.valueOf(3L)
            (a % b) shouldBe UnsignedLong.ZERO
        }
    }

    context("ULong.toUnsignedLong()") {
        test("zero") {
            0uL.toUnsignedLong() shouldBe UnsignedLong.ZERO
        }

        test("one") {
            1uL.toUnsignedLong() shouldBe UnsignedLong.ONE
        }

        test("MAX_VALUE round-trips") {
            ULong.MAX_VALUE.toUnsignedLong() shouldBe UnsignedLong.MAX_VALUE
        }

        test("mid-range value") {
            100uL.toUnsignedLong() shouldBe UnsignedLong.valueOf(100L)
        }
    }

    context("UnsignedLong.toULong()") {
        test("zero") {
            UnsignedLong.ZERO.toULong() shouldBe 0uL
        }

        test("one") {
            UnsignedLong.ONE.toULong() shouldBe 1uL
        }

        test("MAX_VALUE round-trips") {
            UnsignedLong.MAX_VALUE.toULong() shouldBe ULong.MAX_VALUE
        }

        test("mid-range value") {
            UnsignedLong.valueOf(100L).toULong() shouldBe 100uL
        }
    }

    context("uLongToUnsignedLong") {
        val conv = uLongToUnsignedLong

        test("forward 0uL") {
            conv(0uL) shouldBe UnsignedLong.ZERO
        }

        test("forward MAX_VALUE") {
            conv(ULong.MAX_VALUE) shouldBe UnsignedLong.MAX_VALUE
        }

        test("backward ZERO") {
            conv.reverse(UnsignedLong.ZERO) shouldBe 0uL
        }

        test("backward MAX_VALUE") {
            conv.reverse(UnsignedLong.MAX_VALUE) shouldBe ULong.MAX_VALUE
        }

        test("round-trip forward then backward") {
            val v = 9876543210uL
            conv.reverse(conv(v)) shouldBe v
        }

        test("round-trip backward then forward") {
            val v = UnsignedLong.valueOf(1234567890L)
            conv(conv.reverse(v)) shouldBe v
        }

        test("singleton identity") {
            uLongToUnsignedLong shouldBe uLongToUnsignedLong
        }
    }
})
