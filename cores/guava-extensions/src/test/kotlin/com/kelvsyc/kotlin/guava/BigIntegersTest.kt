package com.kelvsyc.kotlin.guava

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigInteger
import java.math.RoundingMode

class BigIntegersTest : FunSpec({

    context("isPowerOfTwo") {
        test("1 is a power of two") { BigInteger.ONE.isPowerOfTwo shouldBe true }
        test("2 is a power of two") { BigInteger.TWO.isPowerOfTwo shouldBe true }
        test("1024 is a power of two") { BigInteger.valueOf(1024L).isPowerOfTwo shouldBe true }
        test("large power of two") { BigInteger.ONE.shiftLeft(100).isPowerOfTwo shouldBe true }
        test("3 is not a power of two") { BigInteger.valueOf(3L).isPowerOfTwo shouldBe false }
        test("6 is not a power of two") { BigInteger.valueOf(6L).isPowerOfTwo shouldBe false }
        test("0 is not a power of two") { BigInteger.ZERO.isPowerOfTwo shouldBe false }
        test("negative is not a power of two") { BigInteger.valueOf(-2L).isPowerOfTwo shouldBe false }
    }

    context("floorPowerOfTwo") {
        test("floor(1) = 1") { BigInteger.ONE.floorPowerOfTwo shouldBe BigInteger.ONE }
        test("floor(2) = 2") { BigInteger.TWO.floorPowerOfTwo shouldBe BigInteger.TWO }
        test("floor(3) = 2") { BigInteger.valueOf(3L).floorPowerOfTwo shouldBe BigInteger.TWO }
        test("floor(4) = 4") { BigInteger.valueOf(4L).floorPowerOfTwo shouldBe BigInteger.valueOf(4L) }
        test("floor(1000) = 512") { BigInteger.valueOf(1000L).floorPowerOfTwo shouldBe BigInteger.valueOf(512L) }
        test("floor(large non-power) rounds down") {
            val n = BigInteger.ONE.shiftLeft(100).add(BigInteger.ONE)
            n.floorPowerOfTwo shouldBe BigInteger.ONE.shiftLeft(100)
        }
        test("floor(0) throws") { shouldThrow<IllegalArgumentException> { BigInteger.ZERO.floorPowerOfTwo } }
        test("floor(negative) throws") { shouldThrow<IllegalArgumentException> { BigInteger.valueOf(-1L).floorPowerOfTwo } }
    }

    context("ceilingPowerOfTwo") {
        test("ceiling(1) = 1") { BigInteger.ONE.ceilingPowerOfTwo shouldBe BigInteger.ONE }
        test("ceiling(2) = 2") { BigInteger.TWO.ceilingPowerOfTwo shouldBe BigInteger.TWO }
        test("ceiling(3) = 4") { BigInteger.valueOf(3L).ceilingPowerOfTwo shouldBe BigInteger.valueOf(4L) }
        test("ceiling(4) = 4") { BigInteger.valueOf(4L).ceilingPowerOfTwo shouldBe BigInteger.valueOf(4L) }
        test("ceiling(1000) = 1024") { BigInteger.valueOf(1000L).ceilingPowerOfTwo shouldBe BigInteger.valueOf(1024L) }
        test("ceiling(large power) is exact") {
            val p = BigInteger.ONE.shiftLeft(100)
            p.ceilingPowerOfTwo shouldBe p
        }
        test("ceiling(large non-power) rounds up") {
            val n = BigInteger.ONE.shiftLeft(100).add(BigInteger.ONE)
            n.ceilingPowerOfTwo shouldBe BigInteger.ONE.shiftLeft(101)
        }
        test("ceiling(0) throws") { shouldThrow<IllegalArgumentException> { BigInteger.ZERO.ceilingPowerOfTwo } }
        test("ceiling(negative) throws") { shouldThrow<IllegalArgumentException> { BigInteger.valueOf(-1L).ceilingPowerOfTwo } }
    }

    context("BigInteger.log2") {
        test("exact power of two: floor") { BigInteger.valueOf(4L).log2(RoundingMode.FLOOR) shouldBe 2 }
        test("exact power of two: ceiling") { BigInteger.valueOf(4L).log2(RoundingMode.CEILING) shouldBe 2 }
        test("exact power of two: unnecessary") { BigInteger.valueOf(4L).log2(RoundingMode.UNNECESSARY) shouldBe 2 }

        test("non-power of two: floor rounds down") { BigInteger.valueOf(5L).log2(RoundingMode.FLOOR) shouldBe 2 }
        test("non-power of two: ceiling rounds up") { BigInteger.valueOf(5L).log2(RoundingMode.CEILING) shouldBe 3 }
        test("non-power of two: unnecessary throws") {
            shouldThrow<ArithmeticException> { BigInteger.valueOf(5L).log2(RoundingMode.UNNECESSARY) }
        }

        test("large power of two") { BigInteger.ONE.shiftLeft(100).log2(RoundingMode.FLOOR) shouldBe 100 }
        test("large non-power: floor") { BigInteger.ONE.shiftLeft(100).add(BigInteger.ONE).log2(RoundingMode.FLOOR) shouldBe 100 }
        test("large non-power: ceiling") { BigInteger.ONE.shiftLeft(100).add(BigInteger.ONE).log2(RoundingMode.CEILING) shouldBe 101 }

        test("non-positive throws") { shouldThrow<IllegalArgumentException> { BigInteger.ZERO.log2(RoundingMode.FLOOR) } }
        test("negative throws") { shouldThrow<IllegalArgumentException> { BigInteger.valueOf(-1L).log2(RoundingMode.FLOOR) } }
    }
})
