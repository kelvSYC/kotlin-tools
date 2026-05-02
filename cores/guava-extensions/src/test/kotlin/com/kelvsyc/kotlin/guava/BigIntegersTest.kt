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

    context("BigInteger.log10") {
        test("exact power of ten: floor") { BigInteger.valueOf(1000L).log10(RoundingMode.FLOOR) shouldBe 3 }
        test("exact power of ten: ceiling") { BigInteger.valueOf(1000L).log10(RoundingMode.CEILING) shouldBe 3 }
        test("exact power of ten: unnecessary") { BigInteger.valueOf(1000L).log10(RoundingMode.UNNECESSARY) shouldBe 3 }

        test("non-power of ten: floor rounds down") { BigInteger.valueOf(1001L).log10(RoundingMode.FLOOR) shouldBe 3 }
        test("non-power of ten: ceiling rounds up") { BigInteger.valueOf(1001L).log10(RoundingMode.CEILING) shouldBe 4 }
        test("non-power of ten: unnecessary throws") {
            shouldThrow<ArithmeticException> { BigInteger.valueOf(1001L).log10(RoundingMode.UNNECESSARY) }
        }

        test("large value: floor") { BigInteger.TEN.pow(50).log10(RoundingMode.FLOOR) shouldBe 50 }
        test("non-positive throws") { shouldThrow<IllegalArgumentException> { BigInteger.ZERO.log10(RoundingMode.FLOOR) } }
        test("negative throws") { shouldThrow<IllegalArgumentException> { BigInteger.valueOf(-1L).log10(RoundingMode.FLOOR) } }
    }

    context("BigInteger.roundToDouble") {
        test("exact representable value") {
            // 2^53 is exactly representable as Double
            BigInteger.ONE.shiftLeft(53).roundToDouble(RoundingMode.UNNECESSARY) shouldBe 9007199254740992.0
        }
        test("large non-representable: floor rounds down") {
            // 2^53 + 1 cannot be represented exactly as Double
            val n = BigInteger.ONE.shiftLeft(53).add(BigInteger.ONE)
            n.roundToDouble(RoundingMode.FLOOR) shouldBe 9007199254740992.0
        }
        test("large non-representable: ceiling rounds up") {
            val n = BigInteger.ONE.shiftLeft(53).add(BigInteger.ONE)
            n.roundToDouble(RoundingMode.CEILING) shouldBe 9007199254740994.0
        }
    }

    context("BigInteger.sqrt") {
        test("exact square: floor") { BigInteger.valueOf(9L).sqrt(RoundingMode.FLOOR) shouldBe BigInteger.valueOf(3L) }
        test("exact square: ceiling") { BigInteger.valueOf(9L).sqrt(RoundingMode.CEILING) shouldBe BigInteger.valueOf(3L) }
        test("exact square: unnecessary") { BigInteger.valueOf(9L).sqrt(RoundingMode.UNNECESSARY) shouldBe BigInteger.valueOf(3L) }

        test("non-square: floor rounds down") { BigInteger.valueOf(8L).sqrt(RoundingMode.FLOOR) shouldBe BigInteger.valueOf(2L) }
        test("non-square: ceiling rounds up") { BigInteger.valueOf(8L).sqrt(RoundingMode.CEILING) shouldBe BigInteger.valueOf(3L) }
        test("non-square: unnecessary throws") {
            shouldThrow<ArithmeticException> { BigInteger.valueOf(8L).sqrt(RoundingMode.UNNECESSARY) }
        }

        test("large perfect square") {
            BigInteger.ONE.shiftLeft(100).sqrt(RoundingMode.FLOOR) shouldBe BigInteger.ONE.shiftLeft(50)
        }
        test("zero: floor") { BigInteger.ZERO.sqrt(RoundingMode.FLOOR) shouldBe BigInteger.ZERO }
        test("negative throws") { shouldThrow<IllegalArgumentException> { BigInteger.valueOf(-1L).sqrt(RoundingMode.FLOOR) } }
    }
})
