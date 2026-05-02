package com.kelvsyc.kotlin.guava

import com.google.common.primitives.UnsignedInteger
import com.kelvsyc.kotlin.core.traits.integral.PowerOfTwo
import io.kotest.assertions.throwables.shouldThrow
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

    context("PowerOfTwo.unsignedInteger") {
        val ops = PowerOfTwo.unsignedInteger

        context("isPowerOfTwo") {
            test("1 is a power of two") { with(ops) { UnsignedInteger.fromIntBits(1).isPowerOfTwo() } shouldBe true }
            test("1024 is a power of two") { with(ops) { UnsignedInteger.fromIntBits(1024).isPowerOfTwo() } shouldBe true }
            test("2^31 is a power of two") { with(ops) { UnsignedInteger.fromIntBits(Int.MIN_VALUE).isPowerOfTwo() } shouldBe true }
            test("3 is not a power of two") { with(ops) { UnsignedInteger.fromIntBits(3).isPowerOfTwo() } shouldBe false }
            test("ZERO is not a power of two") { with(ops) { UnsignedInteger.ZERO.isPowerOfTwo() } shouldBe false }
        }

        context("floorPowerOfTwo") {
            test("floor(1) = 1") { with(ops) { UnsignedInteger.ONE.floorPowerOfTwo() } shouldBe UnsignedInteger.ONE }
            test("floor(3) = 2") { with(ops) { UnsignedInteger.fromIntBits(3).floorPowerOfTwo() } shouldBe UnsignedInteger.fromIntBits(2) }
            test("floor(MAX_VALUE) = 2^31") { with(ops) { UnsignedInteger.MAX_VALUE.floorPowerOfTwo() } shouldBe UnsignedInteger.fromIntBits(Int.MIN_VALUE) }
            test("floor(ZERO) throws") { shouldThrow<IllegalArgumentException> { with(ops) { UnsignedInteger.ZERO.floorPowerOfTwo() } } }
        }

        context("ceilingPowerOfTwo") {
            test("ceiling(1) = 1") { with(ops) { UnsignedInteger.ONE.ceilingPowerOfTwo() } shouldBe UnsignedInteger.ONE }
            test("ceiling(3) = 4") { with(ops) { UnsignedInteger.fromIntBits(3).ceilingPowerOfTwo() } shouldBe UnsignedInteger.fromIntBits(4) }
            test("ceiling(2^31) = 2^31") { with(ops) { UnsignedInteger.fromIntBits(Int.MIN_VALUE).ceilingPowerOfTwo() } shouldBe UnsignedInteger.fromIntBits(Int.MIN_VALUE) }
            test("ceiling(2^31 + 1) overflows") { shouldThrow<ArithmeticException> { with(ops) { UnsignedInteger.fromIntBits(Int.MIN_VALUE + 1).ceilingPowerOfTwo() } } }
            test("ceiling(ZERO) throws") { shouldThrow<IllegalArgumentException> { with(ops) { UnsignedInteger.ZERO.ceilingPowerOfTwo() } } }
        }
    }
})
