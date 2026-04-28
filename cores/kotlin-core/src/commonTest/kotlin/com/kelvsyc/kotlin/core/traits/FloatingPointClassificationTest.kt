package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.Float16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FloatingPointClassificationTest : FunSpec({

    // ── Binary16 (Float16) ────────────────────────────────────────────────────

    context("Binary16.Companion classification") {
        val cls = Binary16.classification

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
    }

    context("Binary16.Companion sign") {
        val sgn = Binary16.sign

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

    // ── BinaryBFloat16 (BFloat16) ─────────────────────────────────────────────

    context("BinaryBFloat16.Companion classification") {
        val cls = BinaryBFloat16.classification

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
    }

    context("BinaryBFloat16.Companion sign") {
        val sgn = BinaryBFloat16.sign

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
})
