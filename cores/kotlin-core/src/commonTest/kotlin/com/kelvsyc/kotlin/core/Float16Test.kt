package com.kelvsyc.kotlin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class Float16Test : FunSpec({

    // ── Constants ─────────────────────────────────────────────────────────────

    context("constants") {
        test("NaN has exponent 31 and non-zero mantissa") {
            Float16.NaN.biasedExponent shouldBe 31
            Float16.NaN.mantissa shouldNotBe 0
        }

        test("POSITIVE_INFINITY has exponent 31 and zero mantissa") {
            Float16.POSITIVE_INFINITY.biasedExponent shouldBe 31
            Float16.POSITIVE_INFINITY.mantissa shouldBe 0
            Float16.POSITIVE_INFINITY.sign shouldBe false
        }

        test("NEGATIVE_INFINITY has exponent 31 and zero mantissa and negative sign") {
            Float16.NEGATIVE_INFINITY.biasedExponent shouldBe 31
            Float16.NEGATIVE_INFINITY.mantissa shouldBe 0
            Float16.NEGATIVE_INFINITY.sign shouldBe true
        }

        test("MAX_VALUE is the largest finite bit pattern") {
            Float16.MAX_VALUE.bits shouldBe 0x7BFF.toShort()
        }

        test("MIN_VALUE is the smallest positive bit pattern") {
            Float16.MIN_VALUE.bits shouldBe 0x0001.toShort()
        }

        test("MIN_NORMAL is the smallest normal bit pattern") {
            Float16.MIN_NORMAL.bits shouldBe 0x0400.toShort()
        }

        test("EPSILON is 2^-10") {
            Float16.EPSILON.bits shouldBe 0x1400.toShort()
            Float16.EPSILON.biasedExponent shouldBe 5   // 5 - 15 = -10 unbiased
            Float16.EPSILON.mantissa shouldBe 0
        }

        test("MIN_VALUE is less than MIN_NORMAL (subnormal vs normal boundary)") {
            // MIN_VALUE is a subnormal; MIN_NORMAL is the smallest normal
            Float16.MIN_VALUE.isSubnormal() shouldBe true
            Float16.MIN_NORMAL.isSubnormal() shouldBe false
        }
    }

    // ── Bit-field accessors ───────────────────────────────────────────────────

    context("sign") {
        test("positive value has false sign") {
            Float16(0x3C00.toShort()).sign shouldBe false  // 1.0
        }

        test("negative value has true sign") {
            Float16(0xBC00.toShort()).sign shouldBe true   // -1.0
        }

        test("+0 has false sign") {
            Float16(0).sign shouldBe false
        }

        test("-0 has true sign") {
            (-Float16(0)).sign shouldBe true
        }
    }

    context("biasedExponent") {
        test("1.0 has biased exponent 15") {
            Float16(0x3C00.toShort()).biasedExponent shouldBe 15
        }

        test("2.0 has biased exponent 16") {
            Float16(0x4000.toShort()).biasedExponent shouldBe 16
        }

        test("subnormal has biased exponent 0") {
            Float16.MIN_VALUE.biasedExponent shouldBe 0
        }

        test("infinity has biased exponent 31") {
            Float16.POSITIVE_INFINITY.biasedExponent shouldBe 31
        }
    }

    context("mantissa") {
        test("1.0 has zero mantissa") {
            Float16(0x3C00.toShort()).mantissa shouldBe 0
        }

        test("MAX_VALUE has full mantissa") {
            Float16.MAX_VALUE.mantissa shouldBe 0x3FF
        }

        test("mantissa is independent of sign") {
            Float16(0x3C00.toShort()).mantissa shouldBe Float16(0xBC00.toShort()).mantissa
        }
    }

    // ── Classification ────────────────────────────────────────────────────────

    context("isNaN") {
        test("NaN is NaN") { Float16.NaN.isNaN() shouldBe true }
        test("positive infinity is not NaN") { Float16.POSITIVE_INFINITY.isNaN() shouldBe false }
        test("zero is not NaN") { Float16(0).isNaN() shouldBe false }
        test("finite value is not NaN") { Float16(0x3C00.toShort()).isNaN() shouldBe false }
        test("NaN with different payload is still NaN") {
            Float16(0x7C01.toShort()).isNaN() shouldBe true
        }
    }

    context("isInfinite") {
        test("POSITIVE_INFINITY is infinite") { Float16.POSITIVE_INFINITY.isInfinite() shouldBe true }
        test("NEGATIVE_INFINITY is infinite") { Float16.NEGATIVE_INFINITY.isInfinite() shouldBe true }
        test("NaN is not infinite") { Float16.NaN.isInfinite() shouldBe false }
        test("finite value is not infinite") { Float16(0x3C00.toShort()).isInfinite() shouldBe false }
    }

    context("isFinite") {
        test("finite value is finite") { Float16(0x3C00.toShort()).isFinite() shouldBe true }
        test("zero is finite") { Float16(0).isFinite() shouldBe true }
        test("infinity is not finite") { Float16.POSITIVE_INFINITY.isFinite() shouldBe false }
        test("NaN is not finite") { Float16.NaN.isFinite() shouldBe false }
    }

    context("isZero") {
        test("+0 is zero") { Float16(0).isZero() shouldBe true }
        test("-0 is zero") { (-Float16(0)).isZero() shouldBe true }
        test("MIN_VALUE is not zero") { Float16.MIN_VALUE.isZero() shouldBe false }
        test("finite non-zero is not zero") { Float16(0x3C00.toShort()).isZero() shouldBe false }
    }

    context("isSubnormal") {
        test("MIN_VALUE is subnormal") { Float16.MIN_VALUE.isSubnormal() shouldBe true }
        test("subnormal with multiple bits is subnormal") {
            Float16(0x0200.toShort()).isSubnormal() shouldBe true
        }
        test("+0 is not subnormal") { Float16(0).isSubnormal() shouldBe false }
        test("-0 is not subnormal") { (-Float16(0)).isSubnormal() shouldBe false }
        test("MIN_NORMAL is not subnormal") { Float16.MIN_NORMAL.isSubnormal() shouldBe false }
        test("normal value is not subnormal") { Float16(0x3C00.toShort()).isSubnormal() shouldBe false }
    }

    // ── Sign operations ───────────────────────────────────────────────────────

    context("unaryMinus") {
        test("negating a positive value gives negative") {
            (-Float16(0x3C00.toShort())).bits shouldBe 0xBC00.toShort()
        }

        test("negating a negative value gives positive") {
            (-Float16(0xBC00.toShort())).bits shouldBe 0x3C00.toShort()
        }

        test("negating +0 gives -0") {
            (-Float16(0)).bits shouldBe 0x8000.toShort()
        }

        test("double negation is identity") {
            val v = Float16(0x3C00.toShort())
            (-(-v)).bits shouldBe v.bits
        }
    }

    context("abs") {
        test("abs of positive value is unchanged") {
            Float16(0x3C00.toShort()).abs().bits shouldBe 0x3C00.toShort()
        }

        test("abs of negative value clears sign") {
            Float16(0xBC00.toShort()).abs().bits shouldBe 0x3C00.toShort()
        }

        test("abs of -0 is +0") {
            (-Float16(0)).abs().bits shouldBe 0.toShort()
        }

        test("abs of NaN clears sign bit but remains NaN") {
            Float16(0xFE00.toShort()).abs().isNaN() shouldBe true
            Float16(0xFE00.toShort()).abs().sign shouldBe false
        }
    }

    // ── Float-like equality and hashing ──────────────────────────────────────

    context("equalTo") {
        test("same value is equal to itself") {
            Float16.equalTo(Float16(0x3C00.toShort()), Float16(0x3C00.toShort())) shouldBe true
        }

        test("different values are not equal") {
            Float16.equalTo(Float16(0x3C00.toShort()), Float16(0x4000.toShort())) shouldBe false
        }

        test("NaN is equal to NaN regardless of payload") {
            Float16.equalTo(Float16.NaN, Float16(0x7C01.toShort())) shouldBe true
        }

        test("NaN is not equal to a finite value") {
            Float16.equalTo(Float16.NaN, Float16(0x3C00.toShort())) shouldBe false
        }

        test("+0 and -0 are not equal") {
            Float16.equalTo(Float16(0), -Float16(0)) shouldBe false
        }
    }

    context("hash") {
        test("all NaN payloads hash to the same value") {
            Float16.hash(Float16.NaN) shouldBe Float16.hash(Float16(0x7C01.toShort()))
            Float16.hash(Float16.NaN) shouldBe Float16.hash(Float16(0x7FFF.toShort()))
        }

        test("+0 and -0 hash to different values") {
            Float16.hash(Float16(0)) shouldNotBe Float16.hash(-Float16(0))
        }

        test("equal values hash equally") {
            Float16.hash(Float16(0x3C00.toShort())) shouldBe Float16.hash(Float16(0x3C00.toShort()))
        }

        test("hash is consistent with equalTo: equal values have equal hashes") {
            // NaN case
            val nan1 = Float16.NaN
            val nan2 = Float16(0x7C01.toShort())
            Float16.equalTo(nan1, nan2) shouldBe true
            Float16.hash(nan1) shouldBe Float16.hash(nan2)
        }
    }

    // ── ulp ───────────────────────────────────────────────────────────────────

    context("ulp") {
        test("NaN ulp is NaN") { Float16(0x3C00.toShort()).ulp().isNaN() shouldBe false }  // sanity
        test("NaN ulp is NaN") { Float16.NaN.ulp().isNaN() shouldBe true }

        test("positive infinity ulp is positive infinity") {
            Float16.POSITIVE_INFINITY.ulp().bits shouldBe Float16.POSITIVE_INFINITY.bits
        }

        test("negative infinity ulp is positive infinity") {
            Float16.NEGATIVE_INFINITY.ulp().bits shouldBe Float16.POSITIVE_INFINITY.bits
        }

        test("+0 ulp is MIN_VALUE") {
            Float16(0).ulp().bits shouldBe Float16.MIN_VALUE.bits
        }

        test("-0 ulp is MIN_VALUE") {
            (-Float16(0)).ulp().bits shouldBe Float16.MIN_VALUE.bits
        }

        test("any subnormal ulp is MIN_VALUE") {
            Float16(0x0200.toShort()).ulp().bits shouldBe Float16.MIN_VALUE.bits
        }

        test("ulp of 1.0 is EPSILON") {
            Float16(0x3C00.toShort()).ulp().bits shouldBe Float16.EPSILON.bits
        }

        test("ulp of -1.0 equals ulp of 1.0") {
            Float16(0xBC00.toShort()).ulp().bits shouldBe Float16(0x3C00.toShort()).ulp().bits
        }

        test("ulp of MIN_NORMAL is MIN_VALUE") {
            // The smallest normal's ulp is the same as the largest subnormal step
            Float16.MIN_NORMAL.ulp().bits shouldBe Float16.MIN_VALUE.bits
        }

        test("ulp doubles in value when the exponent increases: ulp(2.0) is twice ulp(1.0)") {
            // Both ULPs are normal values with zero mantissa; the value doubles when biasedExponent increments by 1
            val ulp1 = Float16(0x3C00.toShort()).ulp()
            val ulp2 = Float16(0x4000.toShort()).ulp()
            ulp1.mantissa shouldBe 0
            ulp2.mantissa shouldBe 0
            ulp2.biasedExponent shouldBe ulp1.biasedExponent + 1
        }

        test("ulp is always positive") {
            listOf(Float16(0x3C00.toShort()), Float16(0xBC00.toShort()), Float16.MIN_VALUE)
                .forEach { v -> v.ulp().sign shouldBe false }
        }
    }

    // ── nextUp ────────────────────────────────────────────────────────────────

    context("nextUp") {
        test("NaN nextUp is NaN") { Float16.NaN.nextUp().isNaN() shouldBe true }

        test("positive infinity nextUp is positive infinity") {
            Float16.POSITIVE_INFINITY.nextUp().bits shouldBe Float16.POSITIVE_INFINITY.bits
        }

        test("negative infinity nextUp is negative MAX_VALUE") {
            Float16.NEGATIVE_INFINITY.nextUp().bits shouldBe (-Float16.MAX_VALUE).bits
        }

        test("+0 nextUp is MIN_VALUE") {
            Float16(0).nextUp().bits shouldBe Float16.MIN_VALUE.bits
        }

        test("-0 nextUp is MIN_VALUE") {
            (-Float16(0)).nextUp().bits shouldBe Float16.MIN_VALUE.bits
        }

        test("MAX_VALUE nextUp is positive infinity") {
            Float16.MAX_VALUE.nextUp().bits shouldBe Float16.POSITIVE_INFINITY.bits
        }

        test("negative MIN_VALUE nextUp is -0") {
            (-Float16.MIN_VALUE).nextUp().bits shouldBe 0x8000.toShort()
        }

        test("finite positive value nextUp increments bits") {
            val v = Float16(0x3C00.toShort())
            v.nextUp().bits shouldBe (v.bits + 1).toShort()
        }

        test("finite negative value nextUp decrements bits toward zero") {
            val v = Float16(0xBC00.toShort())  // -1.0
            val expected = (v.bits.toInt() and 0xFFFF) - 1
            v.nextUp().bits shouldBe expected.toShort()
        }
    }

    // ── nextDown ──────────────────────────────────────────────────────────────

    context("nextDown") {
        test("NaN nextDown is NaN") { Float16.NaN.nextDown().isNaN() shouldBe true }

        test("negative infinity nextDown is negative infinity") {
            Float16.NEGATIVE_INFINITY.nextDown().bits shouldBe Float16.NEGATIVE_INFINITY.bits
        }

        test("positive infinity nextDown is MAX_VALUE") {
            Float16.POSITIVE_INFINITY.nextDown().bits shouldBe Float16.MAX_VALUE.bits
        }

        test("+0 nextDown is negative MIN_VALUE") {
            Float16(0).nextDown().bits shouldBe (-Float16.MIN_VALUE).bits
        }

        test("-0 nextDown is negative MIN_VALUE") {
            (-Float16(0)).nextDown().bits shouldBe (-Float16.MIN_VALUE).bits
        }

        test("MIN_VALUE nextDown is +0") {
            Float16.MIN_VALUE.nextDown().bits shouldBe 0.toShort()
        }

        test("negative MAX_VALUE nextDown is negative infinity") {
            (-Float16.MAX_VALUE).nextDown().bits shouldBe Float16.NEGATIVE_INFINITY.bits
        }

        test("nextDown reverses nextUp for a normal value") {
            val v = Float16(0x3C00.toShort())
            v.nextUp().nextDown().bits shouldBe v.bits
        }

        test("nextUp reverses nextDown for a normal value") {
            val v = Float16(0x3C00.toShort())
            v.nextDown().nextUp().bits shouldBe v.bits
        }
    }

    // ── Extension conversions ─────────────────────────────────────────────────

    context("Float.toFloat16 and Double.toFloat16") {
        test("1.0f converts to 1.0 Float16") {
            1.0f.toFloat16().bits shouldBe 0x3C00.toShort()
        }

        test("0.0f converts to +0") {
            0.0f.toFloat16().bits shouldBe 0.toShort()
        }

        test("Double delegates through Float: 1.0 converts to 1.0 Float16") {
            1.0.toFloat16().bits shouldBe 0x3C00.toShort()
        }

        test("Float.toFloat16 and Float16(Float) give the same result") {
            val f = 1.5f
            f.toFloat16().bits shouldBe Float16(f).bits
        }
    }

    // ── isNormal ──────────────────────────────────────────────────────────────

    context("isNormal") {
        test("normal value is normal") { Float16(0x3C00.toShort()).isNormal() shouldBe true }
        test("zero is not normal") { Float16(0).isNormal() shouldBe false }
        test("-0 is not normal") { (-Float16(0)).isNormal() shouldBe false }
        test("subnormal is not normal") { Float16.MIN_VALUE.isNormal() shouldBe false }
        test("MIN_NORMAL is normal") { Float16.MIN_NORMAL.isNormal() shouldBe true }
        test("MAX_VALUE is normal") { Float16.MAX_VALUE.isNormal() shouldBe true }
        test("infinity is not normal") { Float16.POSITIVE_INFINITY.isNormal() shouldBe false }
        test("NaN is not normal") { Float16.NaN.isNormal() shouldBe false }
    }

    // ── toBits / toRawBits ────────────────────────────────────────────────────

    context("toBits and toRawBits") {
        test("toBits of a normal value equals its raw bits") {
            val v = Float16(0x3C00.toShort())
            v.toBits() shouldBe v.bits
        }

        test("toRawBits equals bits") {
            val v = Float16(0x3C00.toShort())
            v.toRawBits() shouldBe v.bits
        }

        test("toBits normalises all NaN payloads to the canonical NaN") {
            val otherNaN = Float16(0x7C01.toShort())
            otherNaN.toBits() shouldBe Float16.NaN.bits
        }

        test("toRawBits preserves NaN payloads") {
            val otherNaN = Float16(0x7C01.toShort())
            otherNaN.toRawBits() shouldBe 0x7C01.toShort()
        }

        test("toBits and toRawBits agree for non-NaN values") {
            val v = Float16.MAX_VALUE
            v.toBits() shouldBe v.toRawBits()
        }
    }

    // ── Numeric narrowing conversions ─────────────────────────────────────────

    context("toInt") {
        test("1.0 converts to 1") { Float16(0x3C00.toShort()).toInt() shouldBe 1 }
        test("truncates toward zero: 1.9 → 1") { Float16(1.9f).toInt() shouldBe 1 }
        test("truncates toward zero: -1.9 → -1") { Float16(-1.9f).toInt() shouldBe -1 }
        test("0 converts to 0") { Float16(0).toInt() shouldBe 0 }
    }

    context("toLong") {
        test("1.0 converts to 1L") { Float16(0x3C00.toShort()).toLong() shouldBe 1L }
        test("truncates toward zero: -2.5 → -2") { Float16(-2.5f).toLong() shouldBe -2L }
    }

    context("toByte") {
        test("42.0 converts to 42") { Float16(42.0f).toByte() shouldBe 42.toByte() }
        test("truncates: 42.9 → 42") { Float16(42.9f).toByte() shouldBe 42.toByte() }
    }

    context("toShort (integer)") {
        test("100.0 converts to 100") { Float16(100.0f).toShort() shouldBe 100.toShort() }
        test("result is numeric value, not bit pattern") {
            // 1.0 as Float16 has bits 0x3C00 = 15360; toShort() should give 1, not 15360
            Float16(0x3C00.toShort()).toShort() shouldBe 1.toShort()
        }
    }

    // ── rem ───────────────────────────────────────────────────────────────────

    context("rem") {
        test("5 % 3 = 2") {
            val five = Float16(5.0f)
            val three = Float16(3.0f)
            (five % three).toFloat() shouldBe 2.0f
        }

        test("rem result has same sign as dividend") {
            val negFive = Float16(-5.0f)
            val three = Float16(3.0f)
            (negFive % three).toFloat() shouldBe -2.0f
        }
    }

    // ── copySign ──────────────────────────────────────────────────────────────

    context("copySign") {
        test("copies positive sign onto magnitude") {
            Float16.copySign(Float16(1.0f), Float16(2.0f)).sign shouldBe false
        }

        test("copies negative sign onto magnitude") {
            Float16.copySign(Float16(1.0f), Float16(-2.0f)).sign shouldBe true
        }

        test("magnitude is unchanged except for sign") {
            val mag = Float16(0x3C00.toShort())  // 1.0
            Float16.copySign(mag, Float16(-1.0f)).bits shouldBe 0xBC00.toShort()
        }

        test("works on NaN magnitude: preserves payload, changes sign") {
            val result = Float16.copySign(Float16.NaN, Float16(-1.0f))
            result.isNaN() shouldBe true
            result.sign shouldBe true
        }

        test("copies sign from -0") {
            Float16.copySign(Float16(1.0f), -Float16(0)).sign shouldBe true
        }
    }

    // ── sqrt, floor, ceil, round ──────────────────────────────────────────────

    context("sqrt") {
        test("sqrt(4.0) = 2.0") { Float16(4.0f).sqrt().toFloat() shouldBe 2.0f }
        test("sqrt(1.0) = 1.0") { Float16(0x3C00.toShort()).sqrt().toFloat() shouldBe 1.0f }
        test("sqrt of NaN is NaN") { Float16.NaN.sqrt().isNaN() shouldBe true }
        test("sqrt of negative is NaN") { Float16(-1.0f).sqrt().isNaN() shouldBe true }
        test("sqrt of positive infinity is positive infinity") {
            Float16.POSITIVE_INFINITY.sqrt().bits shouldBe Float16.POSITIVE_INFINITY.bits
        }
    }

    context("floor") {
        test("floor(1.5) = 1.0") { Float16(1.5f).floor().toFloat() shouldBe 1.0f }
        test("floor(-1.5) = -2.0") { Float16(-1.5f).floor().toFloat() shouldBe -2.0f }
        test("floor of integer is unchanged") { Float16(3.0f).floor().toFloat() shouldBe 3.0f }
        test("floor of NaN is NaN") { Float16.NaN.floor().isNaN() shouldBe true }
        test("floor of infinity is infinity") {
            Float16.POSITIVE_INFINITY.floor().bits shouldBe Float16.POSITIVE_INFINITY.bits
        }
    }

    context("ceil") {
        test("ceil(1.5) = 2.0") { Float16(1.5f).ceil().toFloat() shouldBe 2.0f }
        test("ceil(-1.5) = -1.0") { Float16(-1.5f).ceil().toFloat() shouldBe -1.0f }
        test("ceil of integer is unchanged") { Float16(3.0f).ceil().toFloat() shouldBe 3.0f }
        test("ceil of NaN is NaN") { Float16.NaN.ceil().isNaN() shouldBe true }
    }

    context("round") {
        test("round(1.4) = 1.0") { Float16(1.4f).round().toFloat() shouldBe 1.0f }
        test("round(1.6) = 2.0") { Float16(1.6f).round().toFloat() shouldBe 2.0f }
        test("round(2.5) rounds to even: 2.0") { Float16(2.5f).round().toFloat() shouldBe 2.0f }
        test("round(3.5) rounds to even: 4.0") { Float16(3.5f).round().toFloat() shouldBe 4.0f }
        test("round of NaN is NaN") { Float16.NaN.round().isNaN() shouldBe true }
    }

    // ── min / max ─────────────────────────────────────────────────────────────

    context("min") {
        test("min(1.0, 2.0) = 1.0") {
            Float16.min(Float16(1.0f), Float16(2.0f)).toFloat() shouldBe 1.0f
        }

        test("min(-1.0, 1.0) = -1.0") {
            Float16.min(Float16(-1.0f), Float16(1.0f)).toFloat() shouldBe -1.0f
        }

        test("min with NaN returns NaN") {
            Float16.min(Float16.NaN, Float16(1.0f)).isNaN() shouldBe true
        }

        test("min(-0, +0) returns a zero") {
            Float16.min(-Float16(0), Float16(0)).isZero() shouldBe true
        }
    }

    context("max") {
        test("max(1.0, 2.0) = 2.0") {
            Float16.max(Float16(1.0f), Float16(2.0f)).toFloat() shouldBe 2.0f
        }

        test("max(-1.0, 1.0) = 1.0") {
            Float16.max(Float16(-1.0f), Float16(1.0f)).toFloat() shouldBe 1.0f
        }

        test("max with NaN returns NaN") {
            Float16.max(Float16.NaN, Float16(1.0f)).isNaN() shouldBe true
        }
    }
})
