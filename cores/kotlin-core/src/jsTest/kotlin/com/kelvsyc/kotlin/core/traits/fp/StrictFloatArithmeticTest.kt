package com.kelvsyc.kotlin.core.traits.fp

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StrictFloatArithmeticTest : FunSpec({
    val ops = strictFloatArithmetic

    context("constants") {
        test("zero is 0.0f") {
            ops.zero shouldBe 0.0f
        }
        test("one is 1.0f") {
            ops.one shouldBe 1.0f
        }
    }

    context("classification") {
        test("NaN is detected") {
            with(ops) { Float.NaN.isNaN() } shouldBe true
        }
        test("infinity is detected") {
            with(ops) { Float.POSITIVE_INFINITY.isInfinite() } shouldBe true
        }
        test("finite value is finite") {
            with(ops) { 1.0f.isFinite() } shouldBe true
        }
        test("zero is detected") {
            with(ops) { 0.0f.isZero() } shouldBe true
        }
        test("negative zero is detected") {
            with(ops) { (-0.0f).isZero() } shouldBe true
        }
    }

    context("sign") {
        test("negative value is negative") {
            with(ops) { (-1.0f).isNegative() } shouldBe true
        }
        test("negate inverts sign") {
            with(ops) { 1.0f.negate() } shouldBe -1.0f
        }
        test("abs removes sign") {
            with(ops) { (-1.0f).abs() } shouldBe 1.0f
        }
        test("copySign copies sign bit") {
            with(ops) { 1.0f.copySign(-0.0f) }.toRawBits() shouldBe (-1.0f).toRawBits()
        }
    }

    context("arithmetic — exact cases") {
        test("1.0f + 1.0f = 2.0f") {
            with(ops) { 1.0f.add(1.0f) } shouldBe 2.0f
        }
        test("2.0f - 1.0f = 1.0f") {
            with(ops) { 2.0f.subtract(1.0f) } shouldBe 1.0f
        }
        test("2.0f * 3.0f = 6.0f") {
            with(ops) { 2.0f.multiply(3.0f) } shouldBe 6.0f
        }
        test("6.0f / 2.0f = 3.0f") {
            with(ops) { 6.0f.divide(2.0f) } shouldBe 3.0f
        }
    }

    context("binary32 rounding guarantee") {
        test("add rounds to binary32: 2^24 + 1.0f rounds to 2^24") {
            val a = (1 shl 24).toFloat()
            with(ops) { a.add(1.0f) } shouldBe a
        }

        test("multiply rounds to binary32: 8193.0f * 8191.0f rounds to 2^26") {
            with(ops) { 8193.0f.multiply(8191.0f) } shouldBe 67108864.0f
        }

        test("add rounds to binary32: 1.0f + 2^-24 rounds to 1.0f") {
            val eps = Float.fromBits(0x33800000)
            with(ops) { 1.0f.add(eps) } shouldBe 1.0f
        }
    }

    context("special values") {
        test("NaN + x = NaN") {
            with(ops) { Float.NaN.add(1.0f) }.isNaN() shouldBe true
        }
        test("+Infinity + finite = +Infinity") {
            with(ops) { Float.POSITIVE_INFINITY.add(1.0f) } shouldBe Float.POSITIVE_INFINITY
        }
        test("+Infinity * 0 = NaN") {
            with(ops) { Float.POSITIVE_INFINITY.multiply(0.0f) }.isNaN() shouldBe true
        }
        test("finite / 0 = Infinity") {
            with(ops) { 1.0f.divide(0.0f) } shouldBe Float.POSITIVE_INFINITY
        }
        test("0 / 0 = NaN") {
            with(ops) { 0.0f.divide(0.0f) }.isNaN() shouldBe true
        }
    }

    context("compareTo") {
        test("1.0f < 2.0f") {
            (with(ops) { 1.0f.compareTo(2.0f) } < 0) shouldBe true
        }
        test("negative zero < positive zero (total order)") {
            (with(ops) { (-0.0f).compareTo(0.0f) } < 0) shouldBe true
        }
        test("NaN is ordered after +Infinity") {
            (with(ops) { Float.NaN.compareTo(Float.POSITIVE_INFINITY) } > 0) shouldBe true
        }
    }
})
