package com.kelvsyc.kotlin.guava

import com.google.common.primitives.UnsignedLong
import com.kelvsyc.kotlin.core.traits.integral.PowerOfTwo
import io.kotest.assertions.throwables.shouldThrow
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

    context("PowerOfTwo.unsignedLong") {
        val ops = PowerOfTwo.unsignedLong

        context("isPowerOfTwo") {
            test("1 is a power of two") { with(ops) { UnsignedLong.fromLongBits(1L).isPowerOfTwo() } shouldBe true }
            test("1024 is a power of two") { with(ops) { UnsignedLong.fromLongBits(1024L).isPowerOfTwo() } shouldBe true }
            test("2^63 is a power of two") { with(ops) { UnsignedLong.fromLongBits(Long.MIN_VALUE).isPowerOfTwo() } shouldBe true }
            test("3 is not a power of two") { with(ops) { UnsignedLong.fromLongBits(3L).isPowerOfTwo() } shouldBe false }
            test("ZERO is not a power of two") { with(ops) { UnsignedLong.ZERO.isPowerOfTwo() } shouldBe false }
        }

        context("floorPowerOfTwo") {
            test("floor(1) = 1") { with(ops) { UnsignedLong.ONE.floorPowerOfTwo() } shouldBe UnsignedLong.ONE }
            test("floor(3) = 2") { with(ops) { UnsignedLong.fromLongBits(3L).floorPowerOfTwo() } shouldBe UnsignedLong.fromLongBits(2L) }
            test("floor(MAX_VALUE) = 2^63") { with(ops) { UnsignedLong.MAX_VALUE.floorPowerOfTwo() } shouldBe UnsignedLong.fromLongBits(Long.MIN_VALUE) }
            test("floor(ZERO) throws") { shouldThrow<IllegalArgumentException> { with(ops) { UnsignedLong.ZERO.floorPowerOfTwo() } } }
        }

        context("ceilingPowerOfTwo") {
            test("ceiling(1) = 1") { with(ops) { UnsignedLong.ONE.ceilingPowerOfTwo() } shouldBe UnsignedLong.ONE }
            test("ceiling(3) = 4") { with(ops) { UnsignedLong.fromLongBits(3L).ceilingPowerOfTwo() } shouldBe UnsignedLong.fromLongBits(4L) }
            test("ceiling(2^63) = 2^63") { with(ops) { UnsignedLong.fromLongBits(Long.MIN_VALUE).ceilingPowerOfTwo() } shouldBe UnsignedLong.fromLongBits(Long.MIN_VALUE) }
            test("ceiling(2^63 + 1) overflows") { shouldThrow<ArithmeticException> { with(ops) { UnsignedLong.fromLongBits(Long.MIN_VALUE + 1L).ceilingPowerOfTwo() } } }
            test("ceiling(ZERO) throws") { shouldThrow<IllegalArgumentException> { with(ops) { UnsignedLong.ZERO.ceilingPowerOfTwo() } } }
        }
    }
})
