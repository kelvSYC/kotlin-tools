package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.DpdDouble
import com.kelvsyc.kotlin.core.DpdFloat
import com.kelvsyc.kotlin.core.Float16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FloatingPointClassificationTest : FunSpec({

    // ── Float16 (binary16) ────────────────────────────────────────────────────

    context("Float16.Companion classification") {
        val cls = Float16.classification

        context("isNaN") {
            test("NaN returns true") {
                with(cls) { Float16.NaN.isNaN() } shouldBe true
            }
            test("positive infinity returns false") {
                with(cls) { Float16.POSITIVE_INFINITY.isNaN() } shouldBe false
            }
            test("normal finite returns false") {
                with(cls) { Float16(0x3C00.toShort()).isNaN() } shouldBe false
            }
            test("positive zero returns false") {
                with(cls) { Float16(0).isNaN() } shouldBe false
            }
        }

        context("isInfinite") {
            test("positive infinity returns true") {
                with(cls) { Float16.POSITIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("negative infinity returns true") {
                with(cls) { Float16.NEGATIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("NaN returns false") {
                with(cls) { Float16.NaN.isInfinite() } shouldBe false
            }
            test("finite returns false") {
                with(cls) { Float16(0x3C00.toShort()).isInfinite() } shouldBe false
            }
        }

        context("isFinite") {
            test("normal finite returns true") {
                with(cls) { Float16(0x3C00.toShort()).isFinite() } shouldBe true
            }
            test("positive zero returns true") {
                with(cls) { Float16(0).isFinite() } shouldBe true
            }
            test("positive infinity returns false") {
                with(cls) { Float16.POSITIVE_INFINITY.isFinite() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { Float16.NaN.isFinite() } shouldBe false
            }
        }

        context("isZero") {
            test("positive zero returns true") {
                with(cls) { Float16(0).isZero() } shouldBe true
            }
            test("negative zero returns true") {
                with(cls) { Float16(0x8000.toShort()).isZero() } shouldBe true
            }
            test("nonzero finite returns false") {
                with(cls) { Float16(0x3C00.toShort()).isZero() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { Float16.NaN.isZero() } shouldBe false
            }
        }

        context("isNormal") {
            test("normal finite returns true") {
                with(cls) { Float16(0x3C00.toShort()).isNormal() } shouldBe true
            }
            test("subnormal returns false") {
                with(cls) { Float16.MIN_VALUE.isNormal() } shouldBe false
            }
            test("positive zero returns false") {
                with(cls) { Float16(0).isNormal() } shouldBe false
            }
            test("positive infinity returns false") {
                with(cls) { Float16.POSITIVE_INFINITY.isNormal() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { Float16.NaN.isNormal() } shouldBe false
            }
        }

        context("isSubnormal") {
            test("subnormal returns true") {
                with(cls) { Float16.MIN_VALUE.isSubnormal() } shouldBe true
            }
            test("normal finite returns false") {
                with(cls) { Float16(0x3C00.toShort()).isSubnormal() } shouldBe false
            }
            test("positive zero returns false") {
                with(cls) { Float16(0).isSubnormal() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { Float16.NaN.isSubnormal() } shouldBe false
            }
        }

        context("isInteger") {
            test("positive zero returns true") {
                with(cls) { Float16(0).isInteger() } shouldBe true
            }
            test("one returns true") {
                with(cls) { Float16(0x3C00.toShort()).isInteger() } shouldBe true
            }
            test("integer at biasedExp=25 boundary returns true") {
                // biasedExp=25 ≥ bias+mantissaBits (25) → always integer; value = 1024.0
                with(cls) { Float16(0x6400.toShort()).isInteger() } shouldBe true
            }
            test("non-integer at biasedExp=24 returns false") {
                // biasedExp=24, mantissa=1 → value = 512.5 (one fractional bit set)
                with(cls) { Float16(0x6001.toShort()).isInteger() } shouldBe false
            }
            test("subnormal returns false") {
                with(cls) { Float16.MIN_VALUE.isInteger() } shouldBe false
            }
            test("positive infinity returns false") {
                with(cls) { Float16.POSITIVE_INFINITY.isInteger() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { Float16.NaN.isInteger() } shouldBe false
            }
        }
    }

    context("Float16.Companion isPowerOfTwo") {
        test("one is a power of two") {
            with(Float16) { Float16(0x3C00.toShort()).isPowerOfTwo() } shouldBe true
        }
        test("two is a power of two") {
            with(Float16) { Float16(0x4000.toShort()).isPowerOfTwo() } shouldBe true
        }
        test("three is not a power of two") {
            // biasedExp=16, mantissa=0x200 → value = 3.0; mantissa non-zero → not a power of two
            with(Float16) { Float16(0x4200.toShort()).isPowerOfTwo() } shouldBe false
        }
        test("smallest subnormal is a power of two") {
            // MIN_VALUE = 0x0001: single mantissa bit set → 2^(−24)
            with(Float16) { Float16.MIN_VALUE.isPowerOfTwo() } shouldBe true
        }
        test("two-bit subnormal is not a power of two") {
            with(Float16) { Float16(0x0003.toShort()).isPowerOfTwo() } shouldBe false
        }
        test("positive zero is not a power of two") {
            with(Float16) { Float16(0).isPowerOfTwo() } shouldBe false
        }
        test("negative value is not a power of two") {
            with(Float16) { Float16(0xC000.toShort()).isPowerOfTwo() } shouldBe false
        }
        test("NaN is not a power of two") {
            with(Float16) { Float16.NaN.isPowerOfTwo() } shouldBe false
        }
    }

    context("Float16.Companion sign") {
        val sgn = Float16.sign

        context("isNegative") {
            test("negative value returns true") {
                with(sgn) { Float16(0xBC00.toShort()).isNegative() } shouldBe true
            }
            test("negative zero returns true") {
                with(sgn) { Float16(0x8000.toShort()).isNegative() } shouldBe true
            }
            test("positive value returns false") {
                with(sgn) { Float16(0x3C00.toShort()).isNegative() } shouldBe false
            }
            test("positive zero returns false") {
                with(sgn) { Float16(0).isNegative() } shouldBe false
            }
        }

        context("isPositive") {
            test("positive value returns true") {
                with(sgn) { Float16(0x3C00.toShort()).isPositive() } shouldBe true
            }
            test("positive zero returns true") {
                with(sgn) { Float16(0).isPositive() } shouldBe true
            }
            test("negative value returns false") {
                with(sgn) { Float16(0xBC00.toShort()).isPositive() } shouldBe false
            }
            test("negative zero returns false") {
                with(sgn) { Float16(0x8000.toShort()).isPositive() } shouldBe false
            }
        }

        context("negate") {
            test("negate positive value gives negative") {
                // Float16(0x3C00) = 1.0; negate flips sign bit → 0xBC00 = -1.0
                with(sgn) { Float16(0x3C00.toShort()).negate() } shouldBe Float16(0xBC00.toShort())
            }
            test("negate negative value gives positive") {
                with(sgn) { Float16(0xBC00.toShort()).negate() } shouldBe Float16(0x3C00.toShort())
            }
            test("negate positive zero gives negative zero") {
                with(sgn) { Float16(0).negate() } shouldBe Float16(0x8000.toShort())
            }
            test("negate negative zero gives positive zero") {
                with(sgn) { Float16(0x8000.toShort()).negate() } shouldBe Float16(0)
            }
            test("negate is involutory") {
                val v = Float16(0x3C00.toShort())
                with(sgn) { v.negate().negate() } shouldBe v
            }
        }

        context("abs") {
            test("abs of positive value is unchanged") {
                with(sgn) { Float16(0x3C00.toShort()).abs() } shouldBe Float16(0x3C00.toShort())
            }
            test("abs of negative value gives positive") {
                with(sgn) { Float16(0xBC00.toShort()).abs() } shouldBe Float16(0x3C00.toShort())
            }
            test("abs of negative zero is positive zero") {
                with(sgn) { Float16(0x8000.toShort()).abs() } shouldBe Float16(0)
            }
            test("abs clears sign bit of negative NaN") {
                // Float16.NaN = 0x7E00 (positive); negative NaN = 0xFE00
                val negNaN = Float16(0xFE00.toShort())
                with(sgn) { negNaN.abs() } shouldBe Float16.NaN
            }
        }

        context("copySign") {
            test("copySign(positive, positive) stays positive") {
                with(sgn) { Float16(0x3C00.toShort()).copySign(Float16(0x3C00.toShort())) } shouldBe Float16(0x3C00.toShort())
            }
            test("copySign(positive, negative) gives negative") {
                with(sgn) { Float16(0x3C00.toShort()).copySign(Float16(0xBC00.toShort())) } shouldBe Float16(0xBC00.toShort())
            }
            test("copySign(negative, positive) gives positive") {
                with(sgn) { Float16(0xBC00.toShort()).copySign(Float16(0x3C00.toShort())) } shouldBe Float16(0x3C00.toShort())
            }
            test("copySign positive zero with negative gives negative zero") {
                with(sgn) { Float16(0).copySign(Float16(0xBC00.toShort())) } shouldBe Float16(0x8000.toShort())
            }
            test("copySign copies sign to NaN, preserving payload") {
                // Float16.NaN = 0x7E00 (positive); with negative sign → 0xFE00
                with(sgn) { Float16.NaN.copySign(Float16(0xBC00.toShort())) } shouldBe Float16(0xFE00.toShort())
            }
        }
    }

    // ── BFloat16 (bfloat16) ───────────────────────────────────────────────────

    context("BFloat16.Companion classification") {
        val cls = BFloat16.classification

        context("isNaN") {
            test("NaN returns true") {
                with(cls) { BFloat16.NaN.isNaN() } shouldBe true
            }
            test("positive infinity returns false") {
                with(cls) { BFloat16.POSITIVE_INFINITY.isNaN() } shouldBe false
            }
            test("normal finite returns false") {
                // BFloat16(0x3F80) = 1.0 (exponent=127, mantissa=0)
                with(cls) { BFloat16(0x3F80.toShort()).isNaN() } shouldBe false
            }
            test("positive zero returns false") {
                with(cls) { BFloat16(0).isNaN() } shouldBe false
            }
        }

        context("isInfinite") {
            test("positive infinity returns true") {
                with(cls) { BFloat16.POSITIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("negative infinity returns true") {
                with(cls) { BFloat16.NEGATIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("NaN returns false") {
                with(cls) { BFloat16.NaN.isInfinite() } shouldBe false
            }
            test("finite returns false") {
                with(cls) { BFloat16(0x3F80.toShort()).isInfinite() } shouldBe false
            }
        }

        context("isFinite") {
            test("normal finite returns true") {
                with(cls) { BFloat16(0x3F80.toShort()).isFinite() } shouldBe true
            }
            test("positive zero returns true") {
                with(cls) { BFloat16(0).isFinite() } shouldBe true
            }
            test("positive infinity returns false") {
                with(cls) { BFloat16.POSITIVE_INFINITY.isFinite() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { BFloat16.NaN.isFinite() } shouldBe false
            }
        }

        context("isZero") {
            test("positive zero returns true") {
                with(cls) { BFloat16(0).isZero() } shouldBe true
            }
            test("negative zero returns true") {
                with(cls) { BFloat16(0x8000.toShort()).isZero() } shouldBe true
            }
            test("nonzero finite returns false") {
                with(cls) { BFloat16(0x3F80.toShort()).isZero() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { BFloat16.NaN.isZero() } shouldBe false
            }
        }

        context("isNormal") {
            test("normal finite returns true") {
                with(cls) { BFloat16(0x3F80.toShort()).isNormal() } shouldBe true
            }
            test("subnormal returns false") {
                with(cls) { BFloat16.MIN_VALUE.isNormal() } shouldBe false
            }
            test("positive zero returns false") {
                with(cls) { BFloat16(0).isNormal() } shouldBe false
            }
            test("positive infinity returns false") {
                with(cls) { BFloat16.POSITIVE_INFINITY.isNormal() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { BFloat16.NaN.isNormal() } shouldBe false
            }
        }

        context("isSubnormal") {
            test("subnormal returns true") {
                with(cls) { BFloat16.MIN_VALUE.isSubnormal() } shouldBe true
            }
            test("normal finite returns false") {
                with(cls) { BFloat16(0x3F80.toShort()).isSubnormal() } shouldBe false
            }
            test("positive zero returns false") {
                with(cls) { BFloat16(0).isSubnormal() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { BFloat16.NaN.isSubnormal() } shouldBe false
            }
        }

        context("isInteger") {
            test("positive zero returns true") {
                with(cls) { BFloat16(0).isInteger() } shouldBe true
            }
            test("one returns true") {
                // BFloat16(0x3F80) = 1.0: biasedExp=127, mantissa=0
                with(cls) { BFloat16(0x3F80.toShort()).isInteger() } shouldBe true
            }
            test("integer at biasedExp=134 boundary returns true") {
                // biasedExp=134 ≥ bias+mantissaBits (134) → always integer; value = 128.0
                with(cls) { BFloat16(0x4300.toShort()).isInteger() } shouldBe true
            }
            test("integer at biasedExp=133 with zero fractional bit returns true") {
                // biasedExp=133, mantissa=0 → value = 64.0 (no fractional bits set)
                with(cls) { BFloat16(0x4280.toShort()).isInteger() } shouldBe true
            }
            test("non-integer at biasedExp=133 returns false") {
                // biasedExp=133, mantissa=1 → value = 64.5 (one fractional bit set)
                with(cls) { BFloat16(0x4281.toShort()).isInteger() } shouldBe false
            }
            test("subnormal returns false") {
                with(cls) { BFloat16.MIN_VALUE.isInteger() } shouldBe false
            }
            test("positive infinity returns false") {
                with(cls) { BFloat16.POSITIVE_INFINITY.isInteger() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { BFloat16.NaN.isInteger() } shouldBe false
            }
        }
    }

    context("BFloat16.Companion isPowerOfTwo") {
        test("one is a power of two") {
            with(BFloat16) { BFloat16(0x3F80.toShort()).isPowerOfTwo() } shouldBe true
        }
        test("two is a power of two") {
            // BFloat16(0x4000) = 2.0: biasedExp=128, mantissa=0
            with(BFloat16) { BFloat16(0x4000.toShort()).isPowerOfTwo() } shouldBe true
        }
        test("three is not a power of two") {
            // BFloat16(0x4040) = 3.0: biasedExp=128, mantissa=0x40 → non-zero
            with(BFloat16) { BFloat16(0x4040.toShort()).isPowerOfTwo() } shouldBe false
        }
        test("smallest subnormal is a power of two") {
            with(BFloat16) { BFloat16.MIN_VALUE.isPowerOfTwo() } shouldBe true
        }
        test("two-bit subnormal is not a power of two") {
            with(BFloat16) { BFloat16(0x0003.toShort()).isPowerOfTwo() } shouldBe false
        }
        test("positive zero is not a power of two") {
            with(BFloat16) { BFloat16(0).isPowerOfTwo() } shouldBe false
        }
        test("negative value is not a power of two") {
            with(BFloat16) { BFloat16(0xC000.toShort()).isPowerOfTwo() } shouldBe false
        }
        test("NaN is not a power of two") {
            with(BFloat16) { BFloat16.NaN.isPowerOfTwo() } shouldBe false
        }
    }

    context("BFloat16.Companion sign") {
        val sgn = BFloat16.sign

        context("isNegative") {
            test("negative value returns true") {
                with(sgn) { BFloat16(0xBF80.toShort()).isNegative() } shouldBe true
            }
            test("negative zero returns true") {
                with(sgn) { BFloat16(0x8000.toShort()).isNegative() } shouldBe true
            }
            test("positive value returns false") {
                with(sgn) { BFloat16(0x3F80.toShort()).isNegative() } shouldBe false
            }
            test("positive zero returns false") {
                with(sgn) { BFloat16(0).isNegative() } shouldBe false
            }
        }

        context("isPositive") {
            test("positive value returns true") {
                with(sgn) { BFloat16(0x3F80.toShort()).isPositive() } shouldBe true
            }
            test("positive zero returns true") {
                with(sgn) { BFloat16(0).isPositive() } shouldBe true
            }
            test("negative value returns false") {
                with(sgn) { BFloat16(0xBF80.toShort()).isPositive() } shouldBe false
            }
            test("negative zero returns false") {
                with(sgn) { BFloat16(0x8000.toShort()).isPositive() } shouldBe false
            }
        }

        context("negate") {
            test("negate positive value gives negative") {
                // BFloat16(0x3F80) = 1.0; negate flips sign bit → 0xBF80 = -1.0
                with(sgn) { BFloat16(0x3F80.toShort()).negate() } shouldBe BFloat16(0xBF80.toShort())
            }
            test("negate negative value gives positive") {
                with(sgn) { BFloat16(0xBF80.toShort()).negate() } shouldBe BFloat16(0x3F80.toShort())
            }
            test("negate positive zero gives negative zero") {
                with(sgn) { BFloat16(0).negate() } shouldBe BFloat16(0x8000.toShort())
            }
            test("negate negative zero gives positive zero") {
                with(sgn) { BFloat16(0x8000.toShort()).negate() } shouldBe BFloat16(0)
            }
            test("negate is involutory") {
                val v = BFloat16(0x3F80.toShort())
                with(sgn) { v.negate().negate() } shouldBe v
            }
        }

        context("abs") {
            test("abs of positive value is unchanged") {
                with(sgn) { BFloat16(0x3F80.toShort()).abs() } shouldBe BFloat16(0x3F80.toShort())
            }
            test("abs of negative value gives positive") {
                with(sgn) { BFloat16(0xBF80.toShort()).abs() } shouldBe BFloat16(0x3F80.toShort())
            }
            test("abs of negative zero is positive zero") {
                with(sgn) { BFloat16(0x8000.toShort()).abs() } shouldBe BFloat16(0)
            }
            test("abs clears sign bit of negative NaN") {
                // BFloat16.NaN = 0x7FC0 (positive); negative NaN = 0xFFC0
                val negNaN = BFloat16(0xFFC0.toShort())
                with(sgn) { negNaN.abs() } shouldBe BFloat16.NaN
            }
        }

        context("copySign") {
            test("copySign(positive, positive) stays positive") {
                with(sgn) { BFloat16(0x3F80.toShort()).copySign(BFloat16(0x3F80.toShort())) } shouldBe BFloat16(0x3F80.toShort())
            }
            test("copySign(positive, negative) gives negative") {
                with(sgn) { BFloat16(0x3F80.toShort()).copySign(BFloat16(0xBF80.toShort())) } shouldBe BFloat16(0xBF80.toShort())
            }
            test("copySign(negative, positive) gives positive") {
                with(sgn) { BFloat16(0xBF80.toShort()).copySign(BFloat16(0x3F80.toShort())) } shouldBe BFloat16(0x3F80.toShort())
            }
            test("copySign positive zero with negative gives negative zero") {
                with(sgn) { BFloat16(0).copySign(BFloat16(0xBF80.toShort())) } shouldBe BFloat16(0x8000.toShort())
            }
            test("copySign copies sign to NaN, preserving payload") {
                // BFloat16.NaN = 0x7FC0 (positive); with negative sign → 0xFFC0
                with(sgn) { BFloat16.NaN.copySign(BFloat16(0xBF80.toShort())) } shouldBe BFloat16(0xFFC0.toShort())
            }
        }
    }

    // ── Binary32 (Float) ──────────────────────────────────────────────────────

    context("Binary32.Companion classification") {
        val cls = Binary32.classification

        context("isNaN") {
            test("NaN returns true") {
                with(cls) { Float.NaN.isNaN() } shouldBe true
            }
            test("positive infinity returns false") {
                with(cls) { Float.POSITIVE_INFINITY.isNaN() } shouldBe false
            }
            test("normal finite returns false") {
                with(cls) { 1.0f.isNaN() } shouldBe false
            }
            test("positive zero returns false") {
                with(cls) { 0.0f.isNaN() } shouldBe false
            }
        }

        context("isInfinite") {
            test("positive infinity returns true") {
                with(cls) { Float.POSITIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("negative infinity returns true") {
                with(cls) { Float.NEGATIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("NaN returns false") {
                with(cls) { Float.NaN.isInfinite() } shouldBe false
            }
            test("finite returns false") {
                with(cls) { 1.0f.isInfinite() } shouldBe false
            }
        }

        context("isFinite") {
            test("normal finite returns true") {
                with(cls) { 1.0f.isFinite() } shouldBe true
            }
            test("positive zero returns true") {
                with(cls) { 0.0f.isFinite() } shouldBe true
            }
            test("positive infinity returns false") {
                with(cls) { Float.POSITIVE_INFINITY.isFinite() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { Float.NaN.isFinite() } shouldBe false
            }
        }

        context("isZero") {
            test("positive zero returns true") {
                with(cls) { 0.0f.isZero() } shouldBe true
            }
            test("negative zero returns true") {
                with(cls) { (-0.0f).isZero() } shouldBe true
            }
            test("nonzero finite returns false") {
                with(cls) { 1.0f.isZero() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { Float.NaN.isZero() } shouldBe false
            }
        }

        context("isNormal") {
            test("normal finite returns true") {
                with(cls) { 1.0f.isNormal() } shouldBe true
            }
            test("subnormal returns false") {
                // Float.MIN_VALUE = smallest positive float = 2^-149, biased exponent 0
                with(cls) { Float.MIN_VALUE.isNormal() } shouldBe false
            }
            test("positive zero returns false") {
                with(cls) { 0.0f.isNormal() } shouldBe false
            }
            test("positive infinity returns false") {
                with(cls) { Float.POSITIVE_INFINITY.isNormal() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { Float.NaN.isNormal() } shouldBe false
            }
        }

        context("isSubnormal") {
            test("subnormal returns true") {
                with(cls) { Float.MIN_VALUE.isSubnormal() } shouldBe true
            }
            test("normal finite returns false") {
                with(cls) { 1.0f.isSubnormal() } shouldBe false
            }
            test("positive zero returns false") {
                with(cls) { 0.0f.isSubnormal() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { Float.NaN.isSubnormal() } shouldBe false
            }
        }

        context("isInteger") {
            test("positive zero returns true") {
                with(cls) { 0.0f.isInteger() } shouldBe true
            }
            test("one returns true") {
                with(cls) { 1.0f.isInteger() } shouldBe true
            }
            test("0.5 returns false") {
                with(cls) { 0.5f.isInteger() } shouldBe false
            }
            test("integer at biasedExp=150 boundary returns true") {
                // biasedExp=150 ≥ bias+mantissaBits (150) → always integer; value = 2^23 = 8388608.0
                with(cls) { Float.fromBits(0x4B000000).isInteger() } shouldBe true
            }
            test("non-integer at biasedExp=149 returns false") {
                // biasedExp=149, mantissa=1 → value = 4194304.5 (one fractional bit set)
                with(cls) { Float.fromBits(0x4A800001).isInteger() } shouldBe false
            }
            test("positive infinity returns false") {
                with(cls) { Float.POSITIVE_INFINITY.isInteger() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { Float.NaN.isInteger() } shouldBe false
            }
        }
    }

    context("Binary32.Companion isPowerOfTwo") {
        test("one is a power of two") {
            with(Binary32) { 1.0f.isPowerOfTwo() } shouldBe true
        }
        test("two is a power of two") {
            with(Binary32) { 2.0f.isPowerOfTwo() } shouldBe true
        }
        test("three is not a power of two") {
            with(Binary32) { 3.0f.isPowerOfTwo() } shouldBe false
        }
        test("smallest subnormal is a power of two") {
            // Float.MIN_VALUE = bits=0x00000001: single bit → 2^(−149)
            with(Binary32) { Float.MIN_VALUE.isPowerOfTwo() } shouldBe true
        }
        test("two-bit subnormal is not a power of two") {
            with(Binary32) { Float.fromBits(3).isPowerOfTwo() } shouldBe false
        }
        test("positive zero is not a power of two") {
            with(Binary32) { 0.0f.isPowerOfTwo() } shouldBe false
        }
        test("negative value is not a power of two") {
            with(Binary32) { (-2.0f).isPowerOfTwo() } shouldBe false
        }
        test("NaN is not a power of two") {
            with(Binary32) { Float.NaN.isPowerOfTwo() } shouldBe false
        }
    }

    context("Binary32.Companion sign") {
        val sgn = Binary32.sign

        context("isNegative") {
            test("negative value returns true") {
                with(sgn) { (-1.0f).isNegative() } shouldBe true
            }
            test("negative zero returns true") {
                with(sgn) { (-0.0f).isNegative() } shouldBe true
            }
            test("positive value returns false") {
                with(sgn) { 1.0f.isNegative() } shouldBe false
            }
            test("positive zero returns false") {
                with(sgn) { 0.0f.isNegative() } shouldBe false
            }
        }

        context("isPositive") {
            test("positive value returns true") {
                with(sgn) { 1.0f.isPositive() } shouldBe true
            }
            test("positive zero returns true") {
                with(sgn) { 0.0f.isPositive() } shouldBe true
            }
            test("negative value returns false") {
                with(sgn) { (-1.0f).isPositive() } shouldBe false
            }
            test("negative zero returns false") {
                with(sgn) { (-0.0f).isPositive() } shouldBe false
            }
        }

        context("negate") {
            test("negate positive value gives negative") {
                with(sgn) { 1.0f.negate() } shouldBe -1.0f
            }
            test("negate negative value gives positive") {
                with(sgn) { (-1.0f).negate() } shouldBe 1.0f
            }
            test("negate positive zero gives negative zero") {
                with(sgn) { 0.0f.negate() }.toRawBits() shouldBe (-0.0f).toRawBits()
            }
            test("negate negative zero gives positive zero") {
                with(sgn) { (-0.0f).negate() } shouldBe 0.0f
            }
            test("negate is involutory") {
                with(sgn) { 1.0f.negate().negate() } shouldBe 1.0f
            }
        }

        context("abs") {
            test("abs of positive value is unchanged") {
                with(sgn) { 1.0f.abs() } shouldBe 1.0f
            }
            test("abs of negative value gives positive") {
                with(sgn) { (-1.0f).abs() } shouldBe 1.0f
            }
            test("abs of negative zero is positive zero") {
                with(sgn) { (-0.0f).abs() }.toRawBits() shouldBe 0
            }
            test("abs clears sign bit of negative NaN") {
                val negNaN = Float.fromBits(Float.NaN.toRawBits() or Int.MIN_VALUE)
                with(sgn) { negNaN.abs().isNegative() } shouldBe false
            }
        }

        context("copySign") {
            test("copySign(positive, positive) stays positive") {
                with(sgn) { 1.0f.copySign(2.0f) } shouldBe 1.0f
            }
            test("copySign(positive, negative) gives negative") {
                with(sgn) { 1.0f.copySign(-2.0f) } shouldBe -1.0f
            }
            test("copySign(negative, positive) gives positive") {
                with(sgn) { (-1.0f).copySign(2.0f) } shouldBe 1.0f
            }
            test("copySign positive zero with negative gives negative zero") {
                with(sgn) { 0.0f.copySign(-1.0f) }.toRawBits() shouldBe (-0.0f).toRawBits()
            }
            test("copySign copies sign to NaN, preserving payload") {
                val result = with(sgn) { Float.NaN.copySign(-1.0f) }
                result.toRawBits() shouldBe (Float.NaN.toRawBits() or Int.MIN_VALUE)
            }
        }
    }

    // ── Binary64 (Double) ─────────────────────────────────────────────────────

    context("Binary64.Companion classification") {
        val cls = Binary64.classification

        context("isNaN") {
            test("NaN returns true") {
                with(cls) { Double.NaN.isNaN() } shouldBe true
            }
            test("positive infinity returns false") {
                with(cls) { Double.POSITIVE_INFINITY.isNaN() } shouldBe false
            }
            test("normal finite returns false") {
                with(cls) { 1.0.isNaN() } shouldBe false
            }
            test("positive zero returns false") {
                with(cls) { 0.0.isNaN() } shouldBe false
            }
        }

        context("isInfinite") {
            test("positive infinity returns true") {
                with(cls) { Double.POSITIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("negative infinity returns true") {
                with(cls) { Double.NEGATIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("NaN returns false") {
                with(cls) { Double.NaN.isInfinite() } shouldBe false
            }
            test("finite returns false") {
                with(cls) { 1.0.isInfinite() } shouldBe false
            }
        }

        context("isFinite") {
            test("normal finite returns true") {
                with(cls) { 1.0.isFinite() } shouldBe true
            }
            test("positive zero returns true") {
                with(cls) { 0.0.isFinite() } shouldBe true
            }
            test("positive infinity returns false") {
                with(cls) { Double.POSITIVE_INFINITY.isFinite() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { Double.NaN.isFinite() } shouldBe false
            }
        }

        context("isZero") {
            test("positive zero returns true") {
                with(cls) { 0.0.isZero() } shouldBe true
            }
            test("negative zero returns true") {
                with(cls) { (-0.0).isZero() } shouldBe true
            }
            test("nonzero finite returns false") {
                with(cls) { 1.0.isZero() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { Double.NaN.isZero() } shouldBe false
            }
        }

        context("isNormal") {
            test("normal finite returns true") {
                with(cls) { 1.0.isNormal() } shouldBe true
            }
            test("subnormal returns false") {
                // Double.MIN_VALUE = smallest positive double = 2^-1074, biased exponent 0
                with(cls) { Double.MIN_VALUE.isNormal() } shouldBe false
            }
            test("positive zero returns false") {
                with(cls) { 0.0.isNormal() } shouldBe false
            }
            test("positive infinity returns false") {
                with(cls) { Double.POSITIVE_INFINITY.isNormal() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { Double.NaN.isNormal() } shouldBe false
            }
        }

        context("isSubnormal") {
            test("subnormal returns true") {
                with(cls) { Double.MIN_VALUE.isSubnormal() } shouldBe true
            }
            test("normal finite returns false") {
                with(cls) { 1.0.isSubnormal() } shouldBe false
            }
            test("positive zero returns false") {
                with(cls) { 0.0.isSubnormal() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { Double.NaN.isSubnormal() } shouldBe false
            }
        }

        context("isInteger") {
            test("positive zero returns true") {
                with(cls) { 0.0.isInteger() } shouldBe true
            }
            test("one returns true") {
                with(cls) { 1.0.isInteger() } shouldBe true
            }
            test("0.5 returns false") {
                with(cls) { 0.5.isInteger() } shouldBe false
            }
            test("integer at biasedExp=1075 boundary returns true") {
                // biasedExp=1075 ≥ bias+mantissaBits (1075) → always integer; value = 2^52
                with(cls) { Double.fromBits(0x4330000000000000L).isInteger() } shouldBe true
            }
            test("non-integer at biasedExp=1074 returns false") {
                // biasedExp=1074, mantissa=1 → value = 2^51 + 0.5 (one fractional bit set)
                with(cls) { Double.fromBits(0x4320000000000001L).isInteger() } shouldBe false
            }
            test("positive infinity returns false") {
                with(cls) { Double.POSITIVE_INFINITY.isInteger() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { Double.NaN.isInteger() } shouldBe false
            }
        }
    }

    context("Binary64.Companion isPowerOfTwo") {
        test("one is a power of two") {
            with(Binary64) { 1.0.isPowerOfTwo() } shouldBe true
        }
        test("two is a power of two") {
            with(Binary64) { 2.0.isPowerOfTwo() } shouldBe true
        }
        test("three is not a power of two") {
            with(Binary64) { 3.0.isPowerOfTwo() } shouldBe false
        }
        test("smallest subnormal is a power of two") {
            // Double.MIN_VALUE = bits=1L: single bit → 2^(−1074)
            with(Binary64) { Double.MIN_VALUE.isPowerOfTwo() } shouldBe true
        }
        test("two-bit subnormal is not a power of two") {
            with(Binary64) { Double.fromBits(3L).isPowerOfTwo() } shouldBe false
        }
        test("positive zero is not a power of two") {
            with(Binary64) { 0.0.isPowerOfTwo() } shouldBe false
        }
        test("negative value is not a power of two") {
            with(Binary64) { (-2.0).isPowerOfTwo() } shouldBe false
        }
        test("NaN is not a power of two") {
            with(Binary64) { Double.NaN.isPowerOfTwo() } shouldBe false
        }
    }

    context("Binary64.Companion sign") {
        val sgn = Binary64.sign

        context("isNegative") {
            test("negative value returns true") {
                with(sgn) { (-1.0).isNegative() } shouldBe true
            }
            test("negative zero returns true") {
                with(sgn) { (-0.0).isNegative() } shouldBe true
            }
            test("positive value returns false") {
                with(sgn) { 1.0.isNegative() } shouldBe false
            }
            test("positive zero returns false") {
                with(sgn) { 0.0.isNegative() } shouldBe false
            }
        }

        context("isPositive") {
            test("positive value returns true") {
                with(sgn) { 1.0.isPositive() } shouldBe true
            }
            test("positive zero returns true") {
                with(sgn) { 0.0.isPositive() } shouldBe true
            }
            test("negative value returns false") {
                with(sgn) { (-1.0).isPositive() } shouldBe false
            }
            test("negative zero returns false") {
                with(sgn) { (-0.0).isPositive() } shouldBe false
            }
        }

        context("negate") {
            test("negate positive value gives negative") {
                with(sgn) { 1.0.negate() } shouldBe -1.0
            }
            test("negate negative value gives positive") {
                with(sgn) { (-1.0).negate() } shouldBe 1.0
            }
            test("negate positive zero gives negative zero") {
                with(sgn) { 0.0.negate() }.toRawBits() shouldBe (-0.0).toRawBits()
            }
            test("negate negative zero gives positive zero") {
                with(sgn) { (-0.0).negate() } shouldBe 0.0
            }
            test("negate is involutory") {
                with(sgn) { 1.0.negate().negate() } shouldBe 1.0
            }
        }

        context("abs") {
            test("abs of positive value is unchanged") {
                with(sgn) { 1.0.abs() } shouldBe 1.0
            }
            test("abs of negative value gives positive") {
                with(sgn) { (-1.0).abs() } shouldBe 1.0
            }
            test("abs of negative zero is positive zero") {
                with(sgn) { (-0.0).abs() }.toRawBits() shouldBe 0L
            }
            test("abs clears sign bit of negative NaN") {
                val negNaN = Double.fromBits(Double.NaN.toRawBits() or Long.MIN_VALUE)
                with(sgn) { negNaN.abs().isNegative() } shouldBe false
            }
        }

        context("copySign") {
            test("copySign(positive, positive) stays positive") {
                with(sgn) { 1.0.copySign(2.0) } shouldBe 1.0
            }
            test("copySign(positive, negative) gives negative") {
                with(sgn) { 1.0.copySign(-2.0) } shouldBe -1.0
            }
            test("copySign(negative, positive) gives positive") {
                with(sgn) { (-1.0).copySign(2.0) } shouldBe 1.0
            }
            test("copySign positive zero with negative gives negative zero") {
                with(sgn) { 0.0.copySign(-1.0) }.toRawBits() shouldBe (-0.0).toRawBits()
            }
            test("copySign copies sign to NaN, preserving payload") {
                val result = with(sgn) { Double.NaN.copySign(-1.0) }
                result.toRawBits() shouldBe (Double.NaN.toRawBits() or Long.MIN_VALUE)
            }
        }
    }

    // ── BidFloat (decimal32 BID) ──────────────────────────────────────────────

    context("BidFloat.Companion classification") {
        val cls = BidFloat.classification

        context("isNaN") {
            test("NaN returns true") {
                with(cls) { BidFloat.NaN.isNaN() } shouldBe true
            }
            test("positive infinity returns false") {
                with(cls) { BidFloat.positiveInfinity.isNaN() } shouldBe false
            }
            test("finite returns false") {
                // biasedExponent=101 (unbiased 0), significand=1 → 1.0
                with(cls) { BidFloat(0x32800001).isNaN() } shouldBe false
            }
            test("positive zero returns false") {
                with(cls) { BidFloat.positiveZero.isNaN() } shouldBe false
            }
        }

        context("isInfinite") {
            test("positive infinity returns true") {
                with(cls) { BidFloat.positiveInfinity.isInfinite() } shouldBe true
            }
            test("negative infinity returns true") {
                with(cls) { BidFloat.negativeInfinity.isInfinite() } shouldBe true
            }
            test("NaN returns false") {
                with(cls) { BidFloat.NaN.isInfinite() } shouldBe false
            }
            test("finite returns false") {
                with(cls) { BidFloat(0x32800001).isInfinite() } shouldBe false
            }
        }

        context("isFinite") {
            test("finite returns true") {
                with(cls) { BidFloat(0x32800001).isFinite() } shouldBe true
            }
            test("positive zero returns true") {
                with(cls) { BidFloat.positiveZero.isFinite() } shouldBe true
            }
            test("positive infinity returns false") {
                with(cls) { BidFloat.positiveInfinity.isFinite() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { BidFloat.NaN.isFinite() } shouldBe false
            }
        }

        context("isZero") {
            test("positive zero returns true") {
                with(cls) { BidFloat.positiveZero.isZero() } shouldBe true
            }
            test("negative zero returns true") {
                with(cls) { BidFloat.negativeZero.isZero() } shouldBe true
            }
            test("nonzero finite returns false") {
                with(cls) { BidFloat(0x32800001).isZero() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { BidFloat.NaN.isZero() } shouldBe false
            }
        }

        context("isNormal") {
            test("normal finite returns true") {
                // minNormal: biasedExponent=0, significand=1_000_000 — leading digit non-zero at min exponent
                with(cls) { BidFloat.minNormal.isNormal() } shouldBe true
            }
            test("normal with nonzero biasedExponent returns true") {
                // biasedExponent=1, significand=100_000 → 10^(-95) = minNormal; non-zero biased exponent, still normal
                with(cls) { BidFloat(0x008186A0).isNormal() } shouldBe true
            }
            test("subnormal returns false") {
                // minValue: biasedExponent=0, significand=1 — leading digit zero (< 10^6)
                with(cls) { BidFloat.minValue.isNormal() } shouldBe false
            }
            test("subnormal with nonzero biasedExponent returns false") {
                // biasedExponent=1, significand=1 → 1×10^(-100) < minNormal (10^(-95))
                with(cls) { BidFloat(0x00800001).isNormal() } shouldBe false
            }
            test("positive zero returns false") {
                with(cls) { BidFloat.positiveZero.isNormal() } shouldBe false
            }
            test("positive infinity returns false") {
                with(cls) { BidFloat.positiveInfinity.isNormal() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { BidFloat.NaN.isNormal() } shouldBe false
            }
        }

        context("isSubnormal") {
            test("subnormal returns true") {
                with(cls) { BidFloat.minValue.isSubnormal() } shouldBe true
            }
            test("subnormal with nonzero biasedExponent returns true") {
                // biasedExponent=1, significand=1 → 1×10^(-100) < minNormal (10^(-95))
                with(cls) { BidFloat(0x00800001).isSubnormal() } shouldBe true
            }
            test("normal finite returns false") {
                with(cls) { BidFloat.minNormal.isSubnormal() } shouldBe false
            }
            test("normal with nonzero biasedExponent returns false") {
                // biasedExponent=1, significand=100_000 → 10^(-95) = minNormal
                with(cls) { BidFloat(0x008186A0).isSubnormal() } shouldBe false
            }
            test("positive zero returns false") {
                with(cls) { BidFloat.positiveZero.isSubnormal() } shouldBe false
            }
            test("NaN returns false") {
                with(cls) { BidFloat.NaN.isSubnormal() } shouldBe false
            }
        }

        context("isInteger") {
            test("NaN returns false") {
                with(cls) { BidFloat.NaN.isInteger() } shouldBe false
            }
            test("positive infinity returns false") {
                with(cls) { BidFloat.positiveInfinity.isInteger() } shouldBe false
            }
            test("positive zero returns true") {
                with(cls) { BidFloat.positiveZero.isInteger() } shouldBe true
            }
            test("1.0 (biasedExp=101) returns true") {
                // biasedExp=101 ≥ 101 → unbiased exp=0, trivially integer
                with(cls) { BidFloat(0x32800001).isInteger() } shouldBe true
            }
            test("0.1 (sig=1, biasedExp=100) returns false") {
                // sig=1, biasedExp=100: sig % 10^1 = 1 ≠ 0 → not integer
                with(cls) { BidFloat(0x32000001).isInteger() } shouldBe false
            }
            test("10.0 (biasedExp=101, sig=10) returns true") {
                with(cls) { BidFloat(0x3280000A).isInteger() } shouldBe true
            }
            test("cohort 1.0 (sig=10, biasedExp=100) returns true") {
                // 10×10^−1 = 1.0; sig%10 = 0 → integer
                with(cls) { BidFloat(0x3200000A).isInteger() } shouldBe true
            }
            test("cohort 1.0 (sig=1000000, biasedExp=95) returns true") {
                // 1_000_000×10^−6 = 1.0; sig % 10^6 = 0 → integer
                with(cls) { BidFloat(0x2F8F4240).isInteger() } shouldBe true
            }
            test("fracExp > 6 guard: 10^-7 (sig=1, biasedExp=94) returns false") {
                with(cls) { BidFloat(0x2F000001).isInteger() } shouldBe false
            }
        }
    }

    context("BidFloat.Companion isPowerOfTen") {
        test("NaN returns false") {
            with(BidFloat) { BidFloat.NaN.isPowerOfTen() } shouldBe false
        }
        test("positive infinity returns false") {
            with(BidFloat) { BidFloat.positiveInfinity.isPowerOfTen() } shouldBe false
        }
        test("positive zero returns false") {
            with(BidFloat) { BidFloat.positiveZero.isPowerOfTen() } shouldBe false
        }
        test("1.0 is a power of ten") {
            with(BidFloat) { BidFloat(0x32800001).isPowerOfTen() } shouldBe true
        }
        test("10.0 is a power of ten") {
            with(BidFloat) { BidFloat(0x3280000A).isPowerOfTen() } shouldBe true
        }
        test("100.0 (sig=100) is a power of ten") {
            // strip two trailing zeros → sig=1 → true
            with(BidFloat) { BidFloat(0x32800064).isPowerOfTen() } shouldBe true
        }
        test("2.0 is not a power of ten") {
            with(BidFloat) { BidFloat(0x32800002).isPowerOfTen() } shouldBe false
        }
        test("cohort 1.0 (sig=10, biasedExp=100) is a power of ten") {
            // strip one trailing zero → sig=1 → true
            with(BidFloat) { BidFloat(0x3200000A).isPowerOfTen() } shouldBe true
        }
        test("negative value returns false") {
            with(BidFloat) { BidFloat(0x3280000A or Int.MIN_VALUE).isPowerOfTen() } shouldBe false
        }
    }

    context("BidFloat.Companion sign") {
        val sgn = BidFloat.sign
        // 0x32800001: biasedExponent=101, significand=1 → 1.0 in decimal32 BID
        val pos = BidFloat(0x32800001)
        val neg = BidFloat(pos.bits or Int.MIN_VALUE)

        context("isNegative") {
            test("negative value returns true") {
                with(sgn) { neg.isNegative() } shouldBe true
            }
            test("negative zero returns true") {
                with(sgn) { BidFloat.negativeZero.isNegative() } shouldBe true
            }
            test("positive value returns false") {
                with(sgn) { pos.isNegative() } shouldBe false
            }
            test("positive zero returns false") {
                with(sgn) { BidFloat.positiveZero.isNegative() } shouldBe false
            }
        }

        context("isPositive") {
            test("positive value returns true") {
                with(sgn) { pos.isPositive() } shouldBe true
            }
            test("positive zero returns true") {
                with(sgn) { BidFloat.positiveZero.isPositive() } shouldBe true
            }
            test("negative value returns false") {
                with(sgn) { neg.isPositive() } shouldBe false
            }
            test("negative zero returns false") {
                with(sgn) { BidFloat.negativeZero.isPositive() } shouldBe false
            }
        }

        context("negate") {
            test("negate positive value gives negative") {
                with(sgn) { pos.negate() } shouldBe neg
            }
            test("negate negative value gives positive") {
                with(sgn) { neg.negate() } shouldBe pos
            }
            test("negate positive zero gives negative zero") {
                with(sgn) { BidFloat.positiveZero.negate() } shouldBe BidFloat.negativeZero
            }
            test("negate negative zero gives positive zero") {
                with(sgn) { BidFloat.negativeZero.negate() } shouldBe BidFloat.positiveZero
            }
            test("negate is involutory") {
                with(sgn) { pos.negate().negate() } shouldBe pos
            }
        }

        context("abs") {
            test("abs of positive value is unchanged") {
                with(sgn) { pos.abs() } shouldBe pos
            }
            test("abs of negative value gives positive") {
                with(sgn) { neg.abs() } shouldBe pos
            }
            test("abs of negative zero is positive zero") {
                with(sgn) { BidFloat.negativeZero.abs() } shouldBe BidFloat.positiveZero
            }
            test("abs clears sign bit of negative NaN") {
                val negNaN = BidFloat(BidFloat.NaN.bits or Int.MIN_VALUE)
                with(sgn) { negNaN.abs() } shouldBe BidFloat.NaN
            }
        }

        context("copySign") {
            test("copySign(positive, positive) stays positive") {
                with(sgn) { pos.copySign(pos) } shouldBe pos
            }
            test("copySign(positive, negative) gives negative") {
                with(sgn) { pos.copySign(neg) } shouldBe neg
            }
            test("copySign(negative, positive) gives positive") {
                with(sgn) { neg.copySign(pos) } shouldBe pos
            }
            test("copySign positive zero with negative gives negative zero") {
                with(sgn) { BidFloat.positiveZero.copySign(neg) } shouldBe BidFloat.negativeZero
            }
            test("copySign copies sign to NaN, preserving payload") {
                val negNaN = BidFloat(BidFloat.NaN.bits or Int.MIN_VALUE)
                with(sgn) { BidFloat.NaN.copySign(neg) } shouldBe negNaN
            }
        }
    }

    // ── BidDouble (decimal64 BID) ─────────────────────────────────────────────

    context("BidDouble.Companion classification") {
        val cls = BidDouble.classification

        context("isInteger") {
            test("NaN returns false") {
                with(cls) { BidDouble.NaN.isInteger() } shouldBe false
            }
            test("positive infinity returns false") {
                with(cls) { BidDouble.positiveInfinity.isInteger() } shouldBe false
            }
            test("positive zero returns true") {
                with(cls) { BidDouble.positiveZero.isInteger() } shouldBe true
            }
            test("1.0 (biasedExp=398) returns true") {
                // biasedExp=398 ≥ 398 → unbiased exp=0, trivially integer
                with(cls) { BidDouble(0x31C0000000000001L).isInteger() } shouldBe true
            }
            test("0.1 (sig=1, biasedExp=397) returns false") {
                // sig=1, biasedExp=397: sig % 10^1 = 1 ≠ 0 → not integer
                with(cls) { BidDouble(0x31A0000000000001L).isInteger() } shouldBe false
            }
            test("10.0 (biasedExp=398, sig=10) returns true") {
                with(cls) { BidDouble(0x31C000000000000AL).isInteger() } shouldBe true
            }
            test("cohort 1.0 (sig=100, biasedExp=396) returns true") {
                // 100×10^−2 = 1.0; sig%100 = 0 → integer
                with(cls) { BidDouble(0x3180000000000064L).isInteger() } shouldBe true
            }
            test("fracExp > 15 guard: sig=1, biasedExp=382 returns false") {
                with(cls) { BidDouble(0x2FC0000000000001L).isInteger() } shouldBe false
            }
        }
    }

    context("BidDouble.Companion isPowerOfTen") {
        test("NaN returns false") {
            with(BidDouble) { BidDouble.NaN.isPowerOfTen() } shouldBe false
        }
        test("positive infinity returns false") {
            with(BidDouble) { BidDouble.positiveInfinity.isPowerOfTen() } shouldBe false
        }
        test("positive zero returns false") {
            with(BidDouble) { BidDouble.positiveZero.isPowerOfTen() } shouldBe false
        }
        test("1.0 is a power of ten") {
            with(BidDouble) { BidDouble(0x31C0000000000001L).isPowerOfTen() } shouldBe true
        }
        test("10.0 is a power of ten") {
            with(BidDouble) { BidDouble(0x31C000000000000AL).isPowerOfTen() } shouldBe true
        }
        test("100.0 (sig=100) is a power of ten") {
            with(BidDouble) { BidDouble(0x31C0000000000064L).isPowerOfTen() } shouldBe true
        }
        test("0.1 (10^-1) is a power of ten") {
            // sig=1, biasedExp=397: strip no zeros → sig=1 → true
            with(BidDouble) { BidDouble(0x31A0000000000001L).isPowerOfTen() } shouldBe true
        }
        test("2.0 is not a power of ten") {
            with(BidDouble) { BidDouble(0x31C0000000000002L).isPowerOfTen() } shouldBe false
        }
        test("negative value returns false") {
            with(BidDouble) { BidDouble(0x31C0000000000001L xor Long.MIN_VALUE).isPowerOfTen() } shouldBe false
        }
    }

    // ── DpdFloat (decimal32 DPD) ──────────────────────────────────────────────

    context("DpdFloat.Companion classification") {
        val cls = DpdFloat.classification

        context("isInteger") {
            test("NaN returns false") {
                with(cls) { DpdFloat.NaN.isInteger() } shouldBe false
            }
            test("positive infinity returns false") {
                with(cls) { DpdFloat.positiveInfinity.isInteger() } shouldBe false
            }
            test("positive zero returns true") {
                with(cls) { DpdFloat.positiveZero.isInteger() } shouldBe true
            }
            test("1.0 (biasedExp=101) returns true") {
                // DPD bit pattern same as BID for single-digit sig ≤ 7
                with(cls) { DpdFloat(0x32800001).isInteger() } shouldBe true
            }
            test("0.1 (sig=1, biasedExp=100) returns false") {
                with(cls) { DpdFloat(0x32000001).isInteger() } shouldBe false
            }
            test("10.0 (biasedExp=101, DPD declet=0x010) returns true") {
                // DPD: lower declet encodes {0,1,0} → 010 → 0x010; value = 0*10^6 + 0 + 010 = 10
                with(cls) { DpdFloat(0x32800010).isInteger() } shouldBe true
            }
            test("cohort 1.0 (leadingDigit=1, biasedExp=95) returns true") {
                // combination=(95<<3)|1=761=0x2F9, continuation=0 → sig=1_000_000; sig%10^6=0
                with(cls) { DpdFloat(0x2F900000).isInteger() } shouldBe true
            }
            test("fracExp > 6 guard: sig=1, biasedExp=94 returns false") {
                with(cls) { DpdFloat(0x2F000001).isInteger() } shouldBe false
            }
        }
    }

    context("DpdFloat.Companion isPowerOfTen") {
        test("NaN returns false") {
            with(DpdFloat) { DpdFloat.NaN.isPowerOfTen() } shouldBe false
        }
        test("positive zero returns false") {
            with(DpdFloat) { DpdFloat.positiveZero.isPowerOfTen() } shouldBe false
        }
        test("1.0 is a power of ten") {
            with(DpdFloat) { DpdFloat(0x32800001).isPowerOfTen() } shouldBe true
        }
        test("10.0 is a power of ten") {
            with(DpdFloat) { DpdFloat(0x32800010).isPowerOfTen() } shouldBe true
        }
        test("100.0 (DPD lower declet=0x080) is a power of ten") {
            // DPD lower declet: (1<<7)|(0<<4)|(0) = 0x080 encodes {1,0,0} = 100
            with(DpdFloat) { DpdFloat(0x32800080).isPowerOfTen() } shouldBe true
        }
        test("0.1 (sig=1, biasedExp=100) is a power of ten") {
            with(DpdFloat) { DpdFloat(0x32000001).isPowerOfTen() } shouldBe true
        }
        test("2.0 is not a power of ten") {
            with(DpdFloat) { DpdFloat(0x32800002).isPowerOfTen() } shouldBe false
        }
        test("negative value returns false") {
            with(DpdFloat) { DpdFloat(0x32800001 or Int.MIN_VALUE).isPowerOfTen() } shouldBe false
        }
    }

    // ── DpdDouble (decimal64 DPD) ─────────────────────────────────────────────

    context("DpdDouble.Companion classification") {
        val cls = DpdDouble.classification

        context("isInteger") {
            test("NaN returns false") {
                with(cls) { DpdDouble.NaN.isInteger() } shouldBe false
            }
            test("positive infinity returns false") {
                with(cls) { DpdDouble.positiveInfinity.isInteger() } shouldBe false
            }
            test("positive zero returns true") {
                with(cls) { DpdDouble.positiveZero.isInteger() } shouldBe true
            }
            test("1.0 (biasedExp=398) returns true") {
                // DPD bit pattern same as BID for single-digit sig ≤ 7
                with(cls) { DpdDouble(0x31C0000000000001L).isInteger() } shouldBe true
            }
            test("0.1 (sig=1, biasedExp=397) returns false") {
                with(cls) { DpdDouble(0x31A0000000000001L).isInteger() } shouldBe false
            }
            test("10.0 (biasedExp=398, last declet=0x010) returns true") {
                with(cls) { DpdDouble(0x31C0000000000010L).isInteger() } shouldBe true
            }
            test("cohort 1.0 (leadingDigit=1, biasedExp=383) returns true") {
                // combination=(383<<3)|1=3065=0xBF9; bits=(0xBF9L shl 50)=0x2FE4000000000000L
                // sig = 10^15; biasedExp=383; sig % 10^15 = 0 → integer
                with(cls) { DpdDouble(0x2FE4000000000000L).isInteger() } shouldBe true
            }
            test("fracExp > 15 guard: sig=1, biasedExp=382 returns false") {
                with(cls) { DpdDouble(0x2FC0000000000001L).isInteger() } shouldBe false
            }
        }
    }

    context("DpdDouble.Companion isPowerOfTen") {
        test("NaN returns false") {
            with(DpdDouble) { DpdDouble.NaN.isPowerOfTen() } shouldBe false
        }
        test("positive zero returns false") {
            with(DpdDouble) { DpdDouble.positiveZero.isPowerOfTen() } shouldBe false
        }
        test("1.0 is a power of ten") {
            with(DpdDouble) { DpdDouble(0x31C0000000000001L).isPowerOfTen() } shouldBe true
        }
        test("10.0 is a power of ten") {
            with(DpdDouble) { DpdDouble(0x31C0000000000010L).isPowerOfTen() } shouldBe true
        }
        test("100.0 (last declet=0x080) is a power of ten") {
            with(DpdDouble) { DpdDouble(0x31C0000000000080L).isPowerOfTen() } shouldBe true
        }
        test("0.1 (sig=1, biasedExp=397) is a power of ten") {
            with(DpdDouble) { DpdDouble(0x31A0000000000001L).isPowerOfTen() } shouldBe true
        }
        test("2.0 is not a power of ten") {
            with(DpdDouble) { DpdDouble(0x31C0000000000002L).isPowerOfTen() } shouldBe false
        }
        test("negative value returns false") {
            with(DpdDouble) { DpdDouble(0x31C0000000000001L xor Long.MIN_VALUE).isPowerOfTen() } shouldBe false
        }
    }
})
