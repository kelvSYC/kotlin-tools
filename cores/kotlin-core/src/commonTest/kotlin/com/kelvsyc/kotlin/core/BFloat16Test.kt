package com.kelvsyc.kotlin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class BFloat16Test : FunSpec({

    // ── Constants ─────────────────────────────────────────────────────────────

    context("constants") {
        test("NaN has exponent 255 and non-zero mantissa") {
            BFloat16.NaN.biasedExponent shouldBe 255
            BFloat16.NaN.mantissa shouldNotBe 0
        }

        test("POSITIVE_INFINITY has exponent 255 and zero mantissa") {
            BFloat16.POSITIVE_INFINITY.biasedExponent shouldBe 255
            BFloat16.POSITIVE_INFINITY.mantissa shouldBe 0
            BFloat16.POSITIVE_INFINITY.sign shouldBe false
        }

        test("NEGATIVE_INFINITY has exponent 255 and zero mantissa and negative sign") {
            BFloat16.NEGATIVE_INFINITY.biasedExponent shouldBe 255
            BFloat16.NEGATIVE_INFINITY.mantissa shouldBe 0
            BFloat16.NEGATIVE_INFINITY.sign shouldBe true
        }

        test("MAX_VALUE is the largest finite bit pattern") {
            BFloat16.MAX_VALUE.bits shouldBe 0x7F7F.toShort()
        }

        test("MIN_VALUE is the smallest positive bit pattern") {
            BFloat16.MIN_VALUE.bits shouldBe 0x0001.toShort()
        }

        test("MIN_NORMAL is the smallest normal bit pattern") {
            BFloat16.MIN_NORMAL.bits shouldBe 0x0080.toShort()
        }

        test("EPSILON is 2^-7") {
            BFloat16.EPSILON.bits shouldBe 0x3C00.toShort()
            BFloat16.EPSILON.biasedExponent shouldBe 120   // 120 - 127 = -7 unbiased
            BFloat16.EPSILON.mantissa shouldBe 0
        }

        test("MIN_VALUE is subnormal, MIN_NORMAL is normal") {
            BFloat16.MIN_VALUE.isSubnormal() shouldBe true
            BFloat16.MIN_NORMAL.isSubnormal() shouldBe false
        }
    }

    // ── Bit-field accessors ───────────────────────────────────────────────────

    context("sign") {
        test("positive value has false sign") {
            BFloat16(0x3F80.toShort()).sign shouldBe false  // 1.0
        }

        test("negative value has true sign") {
            BFloat16(0xBF80.toShort()).sign shouldBe true   // -1.0
        }

        test("+0 has false sign") {
            BFloat16(0).sign shouldBe false
        }

        test("-0 has true sign") {
            (-BFloat16(0)).sign shouldBe true
        }
    }

    context("biasedExponent") {
        test("1.0 has biased exponent 127") {
            BFloat16(0x3F80.toShort()).biasedExponent shouldBe 127
        }

        test("2.0 has biased exponent 128") {
            BFloat16(0x4000.toShort()).biasedExponent shouldBe 128
        }

        test("subnormal has biased exponent 0") {
            BFloat16.MIN_VALUE.biasedExponent shouldBe 0
        }

        test("infinity has biased exponent 255") {
            BFloat16.POSITIVE_INFINITY.biasedExponent shouldBe 255
        }
    }

    context("mantissa") {
        test("1.0 has zero mantissa") {
            BFloat16(0x3F80.toShort()).mantissa shouldBe 0
        }

        test("MAX_VALUE has full mantissa") {
            BFloat16.MAX_VALUE.mantissa shouldBe 0x7F
        }

        test("mantissa is independent of sign") {
            BFloat16(0x3F80.toShort()).mantissa shouldBe BFloat16(0xBF80.toShort()).mantissa
        }
    }

    // ── Classification ────────────────────────────────────────────────────────

    context("isNaN") {
        test("NaN is NaN") { BFloat16.NaN.isNaN() shouldBe true }
        test("positive infinity is not NaN") { BFloat16.POSITIVE_INFINITY.isNaN() shouldBe false }
        test("zero is not NaN") { BFloat16(0).isNaN() shouldBe false }
        test("finite value is not NaN") { BFloat16(0x3F80.toShort()).isNaN() shouldBe false }
        test("NaN with different payload is still NaN") {
            BFloat16(0x7F81.toShort()).isNaN() shouldBe true
        }
    }

    context("isInfinite") {
        test("POSITIVE_INFINITY is infinite") { BFloat16.POSITIVE_INFINITY.isInfinite() shouldBe true }
        test("NEGATIVE_INFINITY is infinite") { BFloat16.NEGATIVE_INFINITY.isInfinite() shouldBe true }
        test("NaN is not infinite") { BFloat16.NaN.isInfinite() shouldBe false }
        test("finite value is not infinite") { BFloat16(0x3F80.toShort()).isInfinite() shouldBe false }
    }

    context("isFinite") {
        test("finite value is finite") { BFloat16(0x3F80.toShort()).isFinite() shouldBe true }
        test("zero is finite") { BFloat16(0).isFinite() shouldBe true }
        test("infinity is not finite") { BFloat16.POSITIVE_INFINITY.isFinite() shouldBe false }
        test("NaN is not finite") { BFloat16.NaN.isFinite() shouldBe false }
    }

    context("isZero") {
        test("+0 is zero") { BFloat16(0).isZero() shouldBe true }
        test("-0 is zero") { (-BFloat16(0)).isZero() shouldBe true }
        test("MIN_VALUE is not zero") { BFloat16.MIN_VALUE.isZero() shouldBe false }
        test("finite non-zero is not zero") { BFloat16(0x3F80.toShort()).isZero() shouldBe false }
    }

    context("isSubnormal") {
        test("MIN_VALUE is subnormal") { BFloat16.MIN_VALUE.isSubnormal() shouldBe true }
        test("subnormal with multiple bits is subnormal") {
            BFloat16(0x0040.toShort()).isSubnormal() shouldBe true
        }
        test("+0 is not subnormal") { BFloat16(0).isSubnormal() shouldBe false }
        test("-0 is not subnormal") { (-BFloat16(0)).isSubnormal() shouldBe false }
        test("MIN_NORMAL is not subnormal") { BFloat16.MIN_NORMAL.isSubnormal() shouldBe false }
        test("normal value is not subnormal") { BFloat16(0x3F80.toShort()).isSubnormal() shouldBe false }
    }

    context("isNormal") {
        test("normal value is normal") { BFloat16(0x3F80.toShort()).isNormal() shouldBe true }
        test("zero is not normal") { BFloat16(0).isNormal() shouldBe false }
        test("-0 is not normal") { (-BFloat16(0)).isNormal() shouldBe false }
        test("subnormal is not normal") { BFloat16.MIN_VALUE.isNormal() shouldBe false }
        test("MIN_NORMAL is normal") { BFloat16.MIN_NORMAL.isNormal() shouldBe true }
        test("MAX_VALUE is normal") { BFloat16.MAX_VALUE.isNormal() shouldBe true }
        test("infinity is not normal") { BFloat16.POSITIVE_INFINITY.isNormal() shouldBe false }
        test("NaN is not normal") { BFloat16.NaN.isNormal() shouldBe false }
    }

    // ── Sign operations ───────────────────────────────────────────────────────

    context("unaryMinus") {
        test("negating a positive value gives negative") {
            (-BFloat16(0x3F80.toShort())).bits shouldBe 0xBF80.toShort()
        }

        test("negating a negative value gives positive") {
            (-BFloat16(0xBF80.toShort())).bits shouldBe 0x3F80.toShort()
        }

        test("negating +0 gives -0") {
            (-BFloat16(0)).bits shouldBe 0x8000.toShort()
        }

        test("double negation is identity") {
            val v = BFloat16(0x3F80.toShort())
            (-(-v)).bits shouldBe v.bits
        }
    }

    context("abs") {
        test("abs of positive value is unchanged") {
            BFloat16(0x3F80.toShort()).abs().bits shouldBe 0x3F80.toShort()
        }

        test("abs of negative value clears sign") {
            BFloat16(0xBF80.toShort()).abs().bits shouldBe 0x3F80.toShort()
        }

        test("abs of -0 is +0") {
            (-BFloat16(0)).abs().bits shouldBe 0.toShort()
        }

        test("abs of NaN clears sign bit but remains NaN") {
            BFloat16(0xFFC0.toShort()).abs().isNaN() shouldBe true
            BFloat16(0xFFC0.toShort()).abs().sign shouldBe false
        }
    }

    // ── Float-like equality and hashing ──────────────────────────────────────

    context("equalTo") {
        test("same value is equal to itself") {
            BFloat16.equalTo(BFloat16(0x3F80.toShort()), BFloat16(0x3F80.toShort())) shouldBe true
        }

        test("different values are not equal") {
            BFloat16.equalTo(BFloat16(0x3F80.toShort()), BFloat16(0x4000.toShort())) shouldBe false
        }

        test("NaN is equal to NaN regardless of payload") {
            BFloat16.equalTo(BFloat16.NaN, BFloat16(0x7F81.toShort())) shouldBe true
        }

        test("NaN is not equal to a finite value") {
            BFloat16.equalTo(BFloat16.NaN, BFloat16(0x3F80.toShort())) shouldBe false
        }

        test("+0 and -0 are not equal") {
            BFloat16.equalTo(BFloat16(0), -BFloat16(0)) shouldBe false
        }
    }

    context("hash") {
        test("all NaN payloads hash to the same value") {
            BFloat16.hash(BFloat16.NaN) shouldBe BFloat16.hash(BFloat16(0x7F81.toShort()))
            BFloat16.hash(BFloat16.NaN) shouldBe BFloat16.hash(BFloat16(0x7FFF.toShort()))
        }

        test("+0 and -0 hash to different values") {
            BFloat16.hash(BFloat16(0)) shouldNotBe BFloat16.hash(-BFloat16(0))
        }

        test("equal values hash equally") {
            BFloat16.hash(BFloat16(0x3F80.toShort())) shouldBe BFloat16.hash(BFloat16(0x3F80.toShort()))
        }

        test("hash is consistent with equalTo: equal values have equal hashes") {
            val nan1 = BFloat16.NaN
            val nan2 = BFloat16(0x7F81.toShort())
            BFloat16.equalTo(nan1, nan2) shouldBe true
            BFloat16.hash(nan1) shouldBe BFloat16.hash(nan2)
        }
    }

    // ── ulp ───────────────────────────────────────────────────────────────────

    context("ulp") {
        test("non-NaN value has non-NaN ulp") { BFloat16(0x3F80.toShort()).ulp().isNaN() shouldBe false }
        test("NaN ulp is NaN") { BFloat16.NaN.ulp().isNaN() shouldBe true }

        test("positive infinity ulp is positive infinity") {
            BFloat16.POSITIVE_INFINITY.ulp().bits shouldBe BFloat16.POSITIVE_INFINITY.bits
        }

        test("negative infinity ulp is positive infinity") {
            BFloat16.NEGATIVE_INFINITY.ulp().bits shouldBe BFloat16.POSITIVE_INFINITY.bits
        }

        test("+0 ulp is MIN_VALUE") {
            BFloat16(0).ulp().bits shouldBe BFloat16.MIN_VALUE.bits
        }

        test("-0 ulp is MIN_VALUE") {
            (-BFloat16(0)).ulp().bits shouldBe BFloat16.MIN_VALUE.bits
        }

        test("any subnormal ulp is MIN_VALUE") {
            BFloat16(0x0040.toShort()).ulp().bits shouldBe BFloat16.MIN_VALUE.bits
        }

        test("ulp of 1.0 is EPSILON") {
            BFloat16(0x3F80.toShort()).ulp().bits shouldBe BFloat16.EPSILON.bits
        }

        test("ulp of -1.0 equals ulp of 1.0") {
            BFloat16(0xBF80.toShort()).ulp().bits shouldBe BFloat16(0x3F80.toShort()).ulp().bits
        }

        test("ulp of MIN_NORMAL is MIN_VALUE") {
            BFloat16.MIN_NORMAL.ulp().bits shouldBe BFloat16.MIN_VALUE.bits
        }

        test("ulp doubles when biased exponent increments: ulp(2.0) is twice ulp(1.0)") {
            val ulp1 = BFloat16(0x3F80.toShort()).ulp()
            val ulp2 = BFloat16(0x4000.toShort()).ulp()
            ulp1.mantissa shouldBe 0
            ulp2.mantissa shouldBe 0
            ulp2.biasedExponent shouldBe ulp1.biasedExponent + 1
        }

        test("ulp is always positive") {
            listOf(BFloat16(0x3F80.toShort()), BFloat16(0xBF80.toShort()), BFloat16.MIN_VALUE)
                .forEach { v -> v.ulp().sign shouldBe false }
        }
    }

    // ── nextUp ────────────────────────────────────────────────────────────────

    context("nextUp") {
        test("NaN nextUp is NaN") { BFloat16.NaN.nextUp().isNaN() shouldBe true }

        test("positive infinity nextUp is positive infinity") {
            BFloat16.POSITIVE_INFINITY.nextUp().bits shouldBe BFloat16.POSITIVE_INFINITY.bits
        }

        test("negative infinity nextUp is negative MAX_VALUE") {
            BFloat16.NEGATIVE_INFINITY.nextUp().bits shouldBe (-BFloat16.MAX_VALUE).bits
        }

        test("+0 nextUp is MIN_VALUE") {
            BFloat16(0).nextUp().bits shouldBe BFloat16.MIN_VALUE.bits
        }

        test("-0 nextUp is MIN_VALUE") {
            (-BFloat16(0)).nextUp().bits shouldBe BFloat16.MIN_VALUE.bits
        }

        test("MAX_VALUE nextUp is positive infinity") {
            BFloat16.MAX_VALUE.nextUp().bits shouldBe BFloat16.POSITIVE_INFINITY.bits
        }

        test("negative MIN_VALUE nextUp is -0") {
            (-BFloat16.MIN_VALUE).nextUp().bits shouldBe 0x8000.toShort()
        }

        test("finite positive value nextUp increments bits") {
            val v = BFloat16(0x3F80.toShort())
            v.nextUp().bits shouldBe (v.bits + 1).toShort()
        }

        test("finite negative value nextUp decrements bits toward zero") {
            val v = BFloat16(0xBF80.toShort())  // -1.0
            val expected = (v.bits.toInt() and 0xFFFF) - 1
            v.nextUp().bits shouldBe expected.toShort()
        }
    }

    // ── nextDown ──────────────────────────────────────────────────────────────

    context("nextDown") {
        test("NaN nextDown is NaN") { BFloat16.NaN.nextDown().isNaN() shouldBe true }

        test("negative infinity nextDown is negative infinity") {
            BFloat16.NEGATIVE_INFINITY.nextDown().bits shouldBe BFloat16.NEGATIVE_INFINITY.bits
        }

        test("positive infinity nextDown is MAX_VALUE") {
            BFloat16.POSITIVE_INFINITY.nextDown().bits shouldBe BFloat16.MAX_VALUE.bits
        }

        test("+0 nextDown is negative MIN_VALUE") {
            BFloat16(0).nextDown().bits shouldBe (-BFloat16.MIN_VALUE).bits
        }

        test("-0 nextDown is negative MIN_VALUE") {
            (-BFloat16(0)).nextDown().bits shouldBe (-BFloat16.MIN_VALUE).bits
        }

        test("MIN_VALUE nextDown is +0") {
            BFloat16.MIN_VALUE.nextDown().bits shouldBe 0.toShort()
        }

        test("negative MAX_VALUE nextDown is negative infinity") {
            (-BFloat16.MAX_VALUE).nextDown().bits shouldBe BFloat16.NEGATIVE_INFINITY.bits
        }

        test("nextDown reverses nextUp for a normal value") {
            val v = BFloat16(0x3F80.toShort())
            v.nextUp().nextDown().bits shouldBe v.bits
        }

        test("nextUp reverses nextDown for a normal value") {
            val v = BFloat16(0x3F80.toShort())
            v.nextDown().nextUp().bits shouldBe v.bits
        }
    }

    // ── Extension conversions ─────────────────────────────────────────────────

    context("Float.toBFloat16 and Double.toBFloat16") {
        test("1.0f converts to 1.0 BFloat16") {
            1.0f.toBFloat16().bits shouldBe 0x3F80.toShort()
        }

        test("0.0f converts to +0") {
            0.0f.toBFloat16().bits shouldBe 0.toShort()
        }

        test("Double delegates through Float: 1.0 converts to 1.0 BFloat16") {
            1.0.toBFloat16().bits shouldBe 0x3F80.toShort()
        }

        test("Float.toBFloat16 and BFloat16(Float) give the same result") {
            val f = 1.5f
            f.toBFloat16().bits shouldBe BFloat16(f).bits
        }
    }

    // ── Converter round-trip ──────────────────────────────────────────────────

    context("converter") {
        test("1.0 round-trips through converter") {
            val bits = BFloat16.converter(1.0f)
            BFloat16.converter.reverse(bits) shouldBe 1.0f
        }

        test("NaN input produces canonical quiet NaN bits") {
            val nanBits = BFloat16.converter(Float.NaN)
            BFloat16(nanBits).isNaN() shouldBe true
            nanBits shouldBe BFloat16.NaN.bits
        }

        test("positive infinity round-trips") {
            val bits = BFloat16.converter(Float.POSITIVE_INFINITY)
            BFloat16.converter.reverse(bits) shouldBe Float.POSITIVE_INFINITY
        }

        test("negative infinity round-trips") {
            val bits = BFloat16.converter(Float.NEGATIVE_INFINITY)
            BFloat16.converter.reverse(bits) shouldBe Float.NEGATIVE_INFINITY
        }

        test("backward conversion of zero bits is +0") {
            BFloat16.converter.reverse(0.toShort()) shouldBe 0.0f
        }
    }

    // ── toBits / toRawBits ────────────────────────────────────────────────────

    context("toBits and toRawBits") {
        test("toBits of a normal value equals its raw bits") {
            val v = BFloat16(0x3F80.toShort())
            v.toBits() shouldBe v.bits
        }

        test("toRawBits equals bits") {
            val v = BFloat16(0x3F80.toShort())
            v.toRawBits() shouldBe v.bits
        }

        test("toBits normalises all NaN payloads to the canonical NaN") {
            val otherNaN = BFloat16(0x7F81.toShort())
            otherNaN.toBits() shouldBe BFloat16.NaN.bits
        }

        test("toRawBits preserves NaN payloads") {
            val otherNaN = BFloat16(0x7F81.toShort())
            otherNaN.toRawBits() shouldBe 0x7F81.toShort()
        }

        test("toBits and toRawBits agree for non-NaN values") {
            val v = BFloat16.MAX_VALUE
            v.toBits() shouldBe v.toRawBits()
        }
    }

    // ── Numeric narrowing conversions ─────────────────────────────────────────

    context("toInt") {
        test("1.0 converts to 1") { BFloat16(0x3F80.toShort()).toInt() shouldBe 1 }
        test("truncates toward zero: 1.9 → 1") { BFloat16(1.9f).toInt() shouldBe 1 }
        test("truncates toward zero: -1.9 → -1") { BFloat16(-1.9f).toInt() shouldBe -1 }
        test("0 converts to 0") { BFloat16(0).toInt() shouldBe 0 }
    }

    context("toLong") {
        test("1.0 converts to 1L") { BFloat16(0x3F80.toShort()).toLong() shouldBe 1L }
        test("truncates toward zero: -2.5 → -2") { BFloat16(-2.5f).toLong() shouldBe -2L }
    }

    context("toByte") {
        test("42.0 converts to 42") { BFloat16(42.0f).toByte() shouldBe 42.toByte() }
        test("truncates: 42.5 → 42") { BFloat16(42.5f).toByte() shouldBe 42.toByte() }
    }

    context("toShort (integer)") {
        test("100.0 converts to 100") { BFloat16(100.0f).toShort() shouldBe 100.toShort() }
        test("result is numeric value, not bit pattern") {
            // 1.0 as BFloat16 has bits 0x3F80 = 16256; toShort() should give 1, not 16256
            BFloat16(0x3F80.toShort()).toShort() shouldBe 1.toShort()
        }
    }

    // ── rem ───────────────────────────────────────────────────────────────────

    context("rem") {
        test("5 % 3 = 2") {
            val five = BFloat16(5.0f)
            val three = BFloat16(3.0f)
            (five % three).toFloat() shouldBe 2.0f
        }

        test("rem result has same sign as dividend") {
            val negFive = BFloat16(-5.0f)
            val three = BFloat16(3.0f)
            (negFive % three).toFloat() shouldBe -2.0f
        }
    }

    // ── copySign ──────────────────────────────────────────────────────────────

    context("copySign") {
        test("copies positive sign onto magnitude") {
            BFloat16.copySign(BFloat16(1.0f), BFloat16(2.0f)).sign shouldBe false
        }

        test("copies negative sign onto magnitude") {
            BFloat16.copySign(BFloat16(1.0f), BFloat16(-2.0f)).sign shouldBe true
        }

        test("magnitude is unchanged except for sign") {
            val mag = BFloat16(0x3F80.toShort())  // 1.0
            BFloat16.copySign(mag, BFloat16(-1.0f)).bits shouldBe 0xBF80.toShort()
        }

        test("works on NaN magnitude: preserves payload, changes sign") {
            val result = BFloat16.copySign(BFloat16.NaN, BFloat16(-1.0f))
            result.isNaN() shouldBe true
            result.sign shouldBe true
        }

        test("copies sign from -0") {
            BFloat16.copySign(BFloat16(1.0f), -BFloat16(0)).sign shouldBe true
        }
    }

    // ── sqrt, floor, ceil, round ──────────────────────────────────────────────

    context("sqrt") {
        test("sqrt(4.0) = 2.0") { BFloat16(4.0f).sqrt().toFloat() shouldBe 2.0f }
        test("sqrt(1.0) = 1.0") { BFloat16(0x3F80.toShort()).sqrt().toFloat() shouldBe 1.0f }
        test("sqrt of NaN is NaN") { BFloat16.NaN.sqrt().isNaN() shouldBe true }
        test("sqrt of negative is NaN") { BFloat16(-1.0f).sqrt().isNaN() shouldBe true }
        test("sqrt of positive infinity is positive infinity") {
            BFloat16.POSITIVE_INFINITY.sqrt().bits shouldBe BFloat16.POSITIVE_INFINITY.bits
        }
    }

    context("floor") {
        test("floor(1.5) = 1.0") { BFloat16(1.5f).floor().toFloat() shouldBe 1.0f }
        test("floor(-1.5) = -2.0") { BFloat16(-1.5f).floor().toFloat() shouldBe -2.0f }
        test("floor of integer is unchanged") { BFloat16(3.0f).floor().toFloat() shouldBe 3.0f }
        test("floor of NaN is NaN") { BFloat16.NaN.floor().isNaN() shouldBe true }
        test("floor of infinity is infinity") {
            BFloat16.POSITIVE_INFINITY.floor().bits shouldBe BFloat16.POSITIVE_INFINITY.bits
        }
    }

    context("ceil") {
        test("ceil(1.5) = 2.0") { BFloat16(1.5f).ceil().toFloat() shouldBe 2.0f }
        test("ceil(-1.5) = -1.0") { BFloat16(-1.5f).ceil().toFloat() shouldBe -1.0f }
        test("ceil of integer is unchanged") { BFloat16(3.0f).ceil().toFloat() shouldBe 3.0f }
        test("ceil of NaN is NaN") { BFloat16.NaN.ceil().isNaN() shouldBe true }
    }

    context("round") {
        test("round(1.4) = 1.0") { BFloat16(1.4f).round().toFloat() shouldBe 1.0f }
        test("round(1.6) = 2.0") { BFloat16(1.6f).round().toFloat() shouldBe 2.0f }
        test("round(2.5) rounds to even: 2.0") { BFloat16(2.5f).round().toFloat() shouldBe 2.0f }
        test("round(3.5) rounds to even: 4.0") { BFloat16(3.5f).round().toFloat() shouldBe 4.0f }
        test("round of NaN is NaN") { BFloat16.NaN.round().isNaN() shouldBe true }
    }

    // ── min / max ─────────────────────────────────────────────────────────────

    context("min") {
        test("min(1.0, 2.0) = 1.0") {
            BFloat16.min(BFloat16(1.0f), BFloat16(2.0f)).toFloat() shouldBe 1.0f
        }

        test("min(-1.0, 1.0) = -1.0") {
            BFloat16.min(BFloat16(-1.0f), BFloat16(1.0f)).toFloat() shouldBe -1.0f
        }

        test("min with NaN returns NaN") {
            BFloat16.min(BFloat16.NaN, BFloat16(1.0f)).isNaN() shouldBe true
        }

        test("min(-0, +0) returns a zero") {
            BFloat16.min(-BFloat16(0), BFloat16(0)).isZero() shouldBe true
        }
    }

    context("max") {
        test("max(1.0, 2.0) = 2.0") {
            BFloat16.max(BFloat16(1.0f), BFloat16(2.0f)).toFloat() shouldBe 2.0f
        }

        test("max(-1.0, 1.0) = 1.0") {
            BFloat16.max(BFloat16(-1.0f), BFloat16(1.0f)).toFloat() shouldBe 1.0f
        }

        test("max with NaN returns NaN") {
            BFloat16.max(BFloat16.NaN, BFloat16(1.0f)).isNaN() shouldBe true
        }
    }
})
