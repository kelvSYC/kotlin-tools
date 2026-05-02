package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class FloatingPointArithmeticTest : FunSpec({

    // ── BFloat16 ──────────────────────────────────────────────────────────────

    context("FloatingPointArithmetic.Companion.bfloat16") {
        val ops = FloatingPointArithmetic.bfloat16

        context("constants") {
            test("zero is positive zero") {
                ops.zero shouldBe BFloat16(0)
            }
            test("one is 1.0") {
                ops.one shouldBe BFloat16(0x3F80.toShort())
            }
        }

        context("isNaN") {
            test("NaN returns true") {
                with(ops) { BFloat16.NaN.isNaN() } shouldBe true
            }
            test("positive infinity returns false") {
                with(ops) { BFloat16.POSITIVE_INFINITY.isNaN() } shouldBe false
            }
            test("finite value returns false") {
                with(ops) { BFloat16(0x3F80.toShort()).isNaN() } shouldBe false
            }
            test("zero returns false") {
                with(ops) { BFloat16(0).isNaN() } shouldBe false
            }
        }

        context("isInfinite") {
            test("positive infinity returns true") {
                with(ops) { BFloat16.POSITIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("negative infinity returns true") {
                with(ops) { BFloat16.NEGATIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("NaN returns false") {
                with(ops) { BFloat16.NaN.isInfinite() } shouldBe false
            }
            test("finite value returns false") {
                with(ops) { BFloat16(0x3F80.toShort()).isInfinite() } shouldBe false
            }
        }

        context("isFinite") {
            test("finite value returns true") {
                with(ops) { BFloat16(0x3F80.toShort()).isFinite() } shouldBe true
            }
            test("zero returns true") {
                with(ops) { BFloat16(0).isFinite() } shouldBe true
            }
            test("positive infinity returns false") {
                with(ops) { BFloat16.POSITIVE_INFINITY.isFinite() } shouldBe false
            }
            test("NaN returns false") {
                with(ops) { BFloat16.NaN.isFinite() } shouldBe false
            }
        }

        context("isZero") {
            test("positive zero returns true") {
                with(ops) { BFloat16(0).isZero() } shouldBe true
            }
            test("negative zero returns true") {
                with(ops) { BFloat16(0x8000.toShort()).isZero() } shouldBe true
            }
            test("nonzero finite returns false") {
                with(ops) { BFloat16(0x3F80.toShort()).isZero() } shouldBe false
            }
            test("NaN returns false") {
                with(ops) { BFloat16.NaN.isZero() } shouldBe false
            }
            test("infinity returns false") {
                with(ops) { BFloat16.POSITIVE_INFINITY.isZero() } shouldBe false
            }
        }

        context("isNegative") {
            test("negative value returns true") {
                with(ops) { BFloat16(0xBF80.toShort()).isNegative() } shouldBe true
            }
            test("negative zero returns true") {
                with(ops) { BFloat16(0x8000.toShort()).isNegative() } shouldBe true
            }
            test("positive value returns false") {
                with(ops) { BFloat16(0x3F80.toShort()).isNegative() } shouldBe false
            }
            test("positive zero returns false") {
                with(ops) { BFloat16(0).isNegative() } shouldBe false
            }
        }

        context("negate") {
            test("negating a positive value gives negative") {
                with(ops) { BFloat16(0x3F80.toShort()).negate() }.bits shouldBe 0xBF80.toShort()
            }
            test("negating a negative value gives positive") {
                with(ops) { BFloat16(0xBF80.toShort()).negate() }.bits shouldBe 0x3F80.toShort()
            }
            test("negating positive zero gives negative zero") {
                with(ops) { BFloat16(0).negate() }.bits shouldBe 0x8000.toShort()
            }
            test("negating negative zero gives positive zero") {
                with(ops) { BFloat16(0x8000.toShort()).negate() }.bits shouldBe 0.toShort()
            }
            test("double negation is identity") {
                val v = BFloat16(0x3F80.toShort())
                with(ops) { v.negate().negate() }.bits shouldBe v.bits
            }
            test("negating NaN produces NaN") {
                with(ops) { BFloat16.NaN.negate() }.isNaN() shouldBe true
            }
        }

        context("abs") {
            test("abs of positive value is unchanged") {
                with(ops) { BFloat16(0x3F80.toShort()).abs() }.bits shouldBe 0x3F80.toShort()
            }
            test("abs of negative value clears sign") {
                with(ops) { BFloat16(0xBF80.toShort()).abs() }.bits shouldBe 0x3F80.toShort()
            }
            test("abs of negative zero is positive zero") {
                with(ops) { BFloat16(0x8000.toShort()).abs() }.bits shouldBe 0.toShort()
            }
            test("abs of NaN is NaN") {
                with(ops) { BFloat16.NaN.abs() }.isNaN() shouldBe true
            }
            test("abs of negative infinity is positive infinity") {
                with(ops) { BFloat16.NEGATIVE_INFINITY.abs() }.bits shouldBe BFloat16.POSITIVE_INFINITY.bits
            }
        }

        context("add") {
            test("1.0 + 1.0 = 2.0") {
                with(ops) { BFloat16(0x3F80.toShort()).add(BFloat16(0x3F80.toShort())) } shouldBe BFloat16(0x4000.toShort())
            }
            test("NaN + x = NaN") {
                with(ops) { BFloat16.NaN.add(BFloat16(0x3F80.toShort())) }.isNaN() shouldBe true
            }
        }

        context("compareTo") {
            test("1.0 < 2.0") {
                (with(ops) { BFloat16(0x3F80.toShort()).compareTo(BFloat16(0x4000.toShort())) } < 0) shouldBe true
            }
            test("NaN is ordered after +Infinity") {
                (with(ops) { BFloat16.NaN.compareTo(BFloat16.POSITIVE_INFINITY) } > 0) shouldBe true
            }
        }
    }

    // ── Float16 ───────────────────────────────────────────────────────────────

    context("FloatingPointArithmetic.Companion.float16") {
        val ops = FloatingPointArithmetic.float16

        context("constants") {
            test("zero is positive zero") {
                ops.zero shouldBe Float16(0)
            }
            test("one is 1.0") {
                ops.one shouldBe Float16(0x3C00.toShort())
            }
        }

        context("isNaN") {
            test("NaN returns true") {
                with(ops) { Float16.NaN.isNaN() } shouldBe true
            }
            test("positive infinity returns false") {
                with(ops) { Float16.POSITIVE_INFINITY.isNaN() } shouldBe false
            }
            test("finite value returns false") {
                with(ops) { Float16(0x3C00.toShort()).isNaN() } shouldBe false
            }
            test("zero returns false") {
                with(ops) { Float16(0).isNaN() } shouldBe false
            }
        }

        context("isInfinite") {
            test("positive infinity returns true") {
                with(ops) { Float16.POSITIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("negative infinity returns true") {
                with(ops) { Float16.NEGATIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("NaN returns false") {
                with(ops) { Float16.NaN.isInfinite() } shouldBe false
            }
            test("finite value returns false") {
                with(ops) { Float16(0x3C00.toShort()).isInfinite() } shouldBe false
            }
        }

        context("isFinite") {
            test("finite value returns true") {
                with(ops) { Float16(0x3C00.toShort()).isFinite() } shouldBe true
            }
            test("zero returns true") {
                with(ops) { Float16(0).isFinite() } shouldBe true
            }
            test("positive infinity returns false") {
                with(ops) { Float16.POSITIVE_INFINITY.isFinite() } shouldBe false
            }
            test("NaN returns false") {
                with(ops) { Float16.NaN.isFinite() } shouldBe false
            }
        }

        context("isZero") {
            test("positive zero returns true") {
                with(ops) { Float16(0).isZero() } shouldBe true
            }
            test("negative zero returns true") {
                with(ops) { Float16(0x8000.toShort()).isZero() } shouldBe true
            }
            test("nonzero finite returns false") {
                with(ops) { Float16(0x3C00.toShort()).isZero() } shouldBe false
            }
            test("NaN returns false") {
                with(ops) { Float16.NaN.isZero() } shouldBe false
            }
            test("infinity returns false") {
                with(ops) { Float16.POSITIVE_INFINITY.isZero() } shouldBe false
            }
        }

        context("isNegative") {
            test("negative value returns true") {
                with(ops) { Float16(0xBC00.toShort()).isNegative() } shouldBe true
            }
            test("negative zero returns true") {
                with(ops) { Float16(0x8000.toShort()).isNegative() } shouldBe true
            }
            test("positive value returns false") {
                with(ops) { Float16(0x3C00.toShort()).isNegative() } shouldBe false
            }
            test("positive zero returns false") {
                with(ops) { Float16(0).isNegative() } shouldBe false
            }
        }

        context("negate") {
            test("negating a positive value gives negative") {
                with(ops) { Float16(0x3C00.toShort()).negate() }.bits shouldBe 0xBC00.toShort()
            }
            test("negating a negative value gives positive") {
                with(ops) { Float16(0xBC00.toShort()).negate() }.bits shouldBe 0x3C00.toShort()
            }
            test("negating positive zero gives negative zero") {
                with(ops) { Float16(0).negate() }.bits shouldBe 0x8000.toShort()
            }
            test("negating negative zero gives positive zero") {
                with(ops) { Float16(0x8000.toShort()).negate() }.bits shouldBe 0.toShort()
            }
            test("double negation is identity") {
                val v = Float16(0x3C00.toShort())
                with(ops) { v.negate().negate() }.bits shouldBe v.bits
            }
            test("negating NaN produces NaN") {
                with(ops) { Float16.NaN.negate() }.isNaN() shouldBe true
            }
        }

        context("abs") {
            test("abs of positive value is unchanged") {
                with(ops) { Float16(0x3C00.toShort()).abs() }.bits shouldBe 0x3C00.toShort()
            }
            test("abs of negative value clears sign") {
                with(ops) { Float16(0xBC00.toShort()).abs() }.bits shouldBe 0x3C00.toShort()
            }
            test("abs of negative zero is positive zero") {
                with(ops) { Float16(0x8000.toShort()).abs() }.bits shouldBe 0.toShort()
            }
            test("abs of NaN is NaN") {
                with(ops) { Float16.NaN.abs() }.isNaN() shouldBe true
            }
            test("abs of negative infinity is positive infinity") {
                with(ops) { Float16.NEGATIVE_INFINITY.abs() }.bits shouldBe Float16.POSITIVE_INFINITY.bits
            }
        }

        context("add") {
            test("1.0 + 1.0 = 2.0") {
                with(ops) { Float16(1.0f).add(Float16(1.0f)) } shouldBe Float16(2.0f)
            }
            test("zero + x = x") {
                val x = Float16(1.5f)
                with(ops) { zero.add(x) } shouldBe x
            }
            test("NaN + x = NaN") {
                with(ops) { Float16.NaN.add(Float16(1.0f)) }.isNaN() shouldBe true
            }
            test("+Infinity + finite = +Infinity") {
                with(ops) { Float16.POSITIVE_INFINITY.add(Float16(1.0f)) }.bits shouldBe Float16.POSITIVE_INFINITY.bits
            }
            test("+Infinity + (-Infinity) = NaN") {
                with(ops) { Float16.POSITIVE_INFINITY.add(Float16.NEGATIVE_INFINITY) }.isNaN() shouldBe true
            }
        }

        context("subtract") {
            test("2.0 - 1.0 = 1.0") {
                with(ops) { Float16(2.0f).subtract(Float16(1.0f)) } shouldBe Float16(1.0f)
            }
            test("x - zero = x") {
                val x = Float16(1.5f)
                with(ops) { x.subtract(zero) } shouldBe x
            }
            test("x - x = 0") {
                with(ops) { Float16(1.0f).subtract(Float16(1.0f)) }.isZero() shouldBe true
            }
            test("NaN - x = NaN") {
                with(ops) { Float16.NaN.subtract(Float16(1.0f)) }.isNaN() shouldBe true
            }
        }

        context("multiply") {
            test("2.0 * 3.0 = 6.0") {
                with(ops) { Float16(2.0f).multiply(Float16(3.0f)) } shouldBe Float16(6.0f)
            }
            test("x * one = x") {
                val x = Float16(1.5f)
                with(ops) { x.multiply(one) } shouldBe x
            }
            test("x * zero = zero") {
                with(ops) { Float16(1.5f).multiply(zero).isZero() } shouldBe true
            }
            test("NaN * x = NaN") {
                with(ops) { Float16.NaN.multiply(Float16(2.0f)) }.isNaN() shouldBe true
            }
            test("+Infinity * nonzero finite = +Infinity") {
                with(ops) { Float16.POSITIVE_INFINITY.multiply(Float16(2.0f)) }.bits shouldBe Float16.POSITIVE_INFINITY.bits
            }
            test("+Infinity * 0 = NaN (invalid operation)") {
                with(ops) { Float16.POSITIVE_INFINITY.multiply(zero) }.isNaN() shouldBe true
            }
        }

        context("divide") {
            test("6.0 / 2.0 = 3.0") {
                with(ops) { Float16(6.0f).divide(Float16(2.0f)) } shouldBe Float16(3.0f)
            }
            test("x / one = x") {
                val x = Float16(1.5f)
                with(ops) { x.divide(one) } shouldBe x
            }
            test("finite / 0 = Infinity") {
                with(ops) { Float16(1.0f).divide(zero).isInfinite() } shouldBe true
            }
            test("0 / 0 = NaN") {
                with(ops) { zero.divide(zero).isNaN() } shouldBe true
            }
            test("NaN / x = NaN") {
                with(ops) { Float16.NaN.divide(Float16(2.0f)) }.isNaN() shouldBe true
            }
        }

        context("compareTo") {
            test("1.0 < 2.0") {
                (with(ops) { Float16(1.0f).compareTo(Float16(2.0f)) } < 0) shouldBe true
            }
            test("2.0 > 1.0") {
                (with(ops) { Float16(2.0f).compareTo(Float16(1.0f)) } > 0) shouldBe true
            }
            test("1.0 == 1.0") {
                with(ops) { Float16(1.0f).compareTo(Float16(1.0f)) } shouldBe 0
            }
            test("negative zero < positive zero (total order)") {
                (with(ops) { Float16(0x8000.toShort()).compareTo(Float16(0)) } < 0) shouldBe true
            }
            test("NaN is ordered after +Infinity (total order)") {
                (with(ops) { Float16.NaN.compareTo(Float16.POSITIVE_INFINITY) } > 0) shouldBe true
            }
            test("-Infinity < MIN_VALUE") {
                (with(ops) { Float16.NEGATIVE_INFINITY.compareTo(Float16.MIN_VALUE) } < 0) shouldBe true
            }
        }
    }

    // ── Float ─────────────────────────────────────────────────────────────────

    context("FloatingPointArithmetic.Companion.float") {
        val ops = FloatingPointArithmetic.float

        context("constants") {
            test("zero is 0.0f") {
                ops.zero shouldBe 0.0f
            }
            test("one is 1.0f") {
                ops.one shouldBe 1.0f
            }
        }

        context("isNaN") {
            test("NaN returns true") {
                with(ops) { Float.NaN.isNaN() } shouldBe true
            }
            test("positive infinity returns false") {
                with(ops) { Float.POSITIVE_INFINITY.isNaN() } shouldBe false
            }
            test("finite value returns false") {
                with(ops) { 1.0f.isNaN() } shouldBe false
            }
        }

        context("isInfinite") {
            test("positive infinity returns true") {
                with(ops) { Float.POSITIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("negative infinity returns true") {
                with(ops) { Float.NEGATIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("NaN returns false") {
                with(ops) { Float.NaN.isInfinite() } shouldBe false
            }
            test("finite value returns false") {
                with(ops) { 1.0f.isInfinite() } shouldBe false
            }
        }

        context("isFinite") {
            test("finite value returns true") {
                with(ops) { 1.0f.isFinite() } shouldBe true
            }
            test("zero returns true") {
                with(ops) { 0.0f.isFinite() } shouldBe true
            }
            test("positive infinity returns false") {
                with(ops) { Float.POSITIVE_INFINITY.isFinite() } shouldBe false
            }
            test("NaN returns false") {
                with(ops) { Float.NaN.isFinite() } shouldBe false
            }
        }

        context("isZero") {
            test("positive zero returns true") {
                with(ops) { 0.0f.isZero() } shouldBe true
            }
            test("negative zero returns true") {
                with(ops) { (-0.0f).isZero() } shouldBe true
            }
            test("nonzero finite returns false") {
                with(ops) { 1.0f.isZero() } shouldBe false
            }
            test("NaN returns false") {
                with(ops) { Float.NaN.isZero() } shouldBe false
            }
            test("infinity returns false") {
                with(ops) { Float.POSITIVE_INFINITY.isZero() } shouldBe false
            }
        }

        context("isNegative") {
            test("negative value returns true") {
                with(ops) { (-1.0f).isNegative() } shouldBe true
            }
            test("negative zero returns true") {
                with(ops) { (-0.0f).isNegative() } shouldBe true
            }
            test("positive value returns false") {
                with(ops) { 1.0f.isNegative() } shouldBe false
            }
            test("positive zero returns false") {
                with(ops) { 0.0f.isNegative() } shouldBe false
            }
        }

        context("negate") {
            test("negating a positive value gives negative") {
                with(ops) { 1.0f.negate() } shouldBe -1.0f
            }
            test("negating a negative value gives positive") {
                with(ops) { (-1.0f).negate() } shouldBe 1.0f
            }
            test("negating positive zero gives negative zero") {
                with(ops) { 0.0f.negate() }.toRawBits() shouldBe (-0.0f).toRawBits()
            }
            test("negating negative zero gives positive zero") {
                with(ops) { (-0.0f).negate() } shouldBe 0.0f
            }
            test("double negation is identity") {
                with(ops) { 1.5f.negate().negate() } shouldBe 1.5f
            }
            test("negating NaN produces NaN") {
                with(ops) { Float.NaN.negate() }.isNaN() shouldBe true
            }
        }

        context("abs") {
            test("abs of positive value is unchanged") {
                with(ops) { 1.0f.abs() } shouldBe 1.0f
            }
            test("abs of negative value removes sign") {
                with(ops) { (-1.0f).abs() } shouldBe 1.0f
            }
            test("abs of negative zero is positive zero") {
                with(ops) { (-0.0f).abs() }.toRawBits() shouldBe 0.0f.toRawBits()
            }
            test("abs of NaN is NaN") {
                with(ops) { Float.NaN.abs() }.isNaN() shouldBe true
            }
            test("abs of negative infinity is positive infinity") {
                with(ops) { Float.NEGATIVE_INFINITY.abs() } shouldBe Float.POSITIVE_INFINITY
            }
        }

        context("add") {
            test("1.0f + 1.0f = 2.0f") {
                with(ops) { 1.0f.add(1.0f) } shouldBe 2.0f
            }
            test("zero + x = x") {
                with(ops) { 0.0f.add(1.5f) } shouldBe 1.5f
            }
            test("NaN + x = NaN") {
                with(ops) { Float.NaN.add(1.0f) }.isNaN() shouldBe true
            }
            test("+Infinity + finite = +Infinity") {
                with(ops) { Float.POSITIVE_INFINITY.add(1.0f) } shouldBe Float.POSITIVE_INFINITY
            }
            test("+Infinity + (-Infinity) = NaN") {
                with(ops) { Float.POSITIVE_INFINITY.add(Float.NEGATIVE_INFINITY) }.isNaN() shouldBe true
            }
        }

        context("subtract") {
            test("2.0f - 1.0f = 1.0f") {
                with(ops) { 2.0f.subtract(1.0f) } shouldBe 1.0f
            }
            test("x - zero = x") {
                with(ops) { 1.5f.subtract(0.0f) } shouldBe 1.5f
            }
            test("x - x = 0") {
                with(ops) { 1.0f.subtract(1.0f) } shouldBe 0.0f
            }
            test("NaN - x = NaN") {
                with(ops) { Float.NaN.subtract(1.0f) }.isNaN() shouldBe true
            }
        }

        context("multiply") {
            test("2.0f * 3.0f = 6.0f") {
                with(ops) { 2.0f.multiply(3.0f) } shouldBe 6.0f
            }
            test("x * one = x") {
                with(ops) { 1.5f.multiply(1.0f) } shouldBe 1.5f
            }
            test("x * zero = zero") {
                with(ops) { 1.5f.multiply(0.0f) } shouldBe 0.0f
            }
            test("NaN * x = NaN") {
                with(ops) { Float.NaN.multiply(2.0f) }.isNaN() shouldBe true
            }
            test("+Infinity * nonzero finite = +Infinity") {
                with(ops) { Float.POSITIVE_INFINITY.multiply(2.0f) } shouldBe Float.POSITIVE_INFINITY
            }
            test("+Infinity * 0 = NaN (invalid operation)") {
                with(ops) { Float.POSITIVE_INFINITY.multiply(0.0f) }.isNaN() shouldBe true
            }
        }

        context("divide") {
            test("6.0f / 2.0f = 3.0f") {
                with(ops) { 6.0f.divide(2.0f) } shouldBe 3.0f
            }
            test("x / one = x") {
                with(ops) { 1.5f.divide(1.0f) } shouldBe 1.5f
            }
            test("finite / 0 = Infinity") {
                with(ops) { 1.0f.divide(0.0f) } shouldBe Float.POSITIVE_INFINITY
            }
            test("0 / 0 = NaN") {
                with(ops) { 0.0f.divide(0.0f) }.isNaN() shouldBe true
            }
            test("NaN / x = NaN") {
                with(ops) { Float.NaN.divide(2.0f) }.isNaN() shouldBe true
            }
        }

        context("compareTo") {
            test("1.0f < 2.0f") {
                (with(ops) { 1.0f.compareTo(2.0f) } < 0) shouldBe true
            }
            test("2.0f > 1.0f") {
                (with(ops) { 2.0f.compareTo(1.0f) } > 0) shouldBe true
            }
            test("1.0f == 1.0f") {
                with(ops) { 1.0f.compareTo(1.0f) } shouldBe 0
            }
            test("negative zero < positive zero (total order)") {
                (with(ops) { (-0.0f).compareTo(0.0f) } < 0) shouldBe true
            }
            test("NaN is ordered after +Infinity (total order)") {
                (with(ops) { Float.NaN.compareTo(Float.POSITIVE_INFINITY) } > 0) shouldBe true
            }
            test("-Infinity < MIN_VALUE") {
                (with(ops) { Float.NEGATIVE_INFINITY.compareTo(Float.MIN_VALUE) } < 0) shouldBe true
            }
        }
    }

    // ── Double ────────────────────────────────────────────────────────────────

    context("FloatingPointArithmetic.Companion.double") {
        val ops = FloatingPointArithmetic.double

        context("constants") {
            test("zero is 0.0") {
                ops.zero shouldBe 0.0
            }
            test("one is 1.0") {
                ops.one shouldBe 1.0
            }
        }

        context("isNaN") {
            test("NaN returns true") {
                with(ops) { Double.NaN.isNaN() } shouldBe true
            }
            test("positive infinity returns false") {
                with(ops) { Double.POSITIVE_INFINITY.isNaN() } shouldBe false
            }
            test("finite value returns false") {
                with(ops) { 1.0.isNaN() } shouldBe false
            }
        }

        context("isInfinite") {
            test("positive infinity returns true") {
                with(ops) { Double.POSITIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("negative infinity returns true") {
                with(ops) { Double.NEGATIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("NaN returns false") {
                with(ops) { Double.NaN.isInfinite() } shouldBe false
            }
            test("finite value returns false") {
                with(ops) { 1.0.isInfinite() } shouldBe false
            }
        }

        context("isFinite") {
            test("finite value returns true") {
                with(ops) { 1.0.isFinite() } shouldBe true
            }
            test("zero returns true") {
                with(ops) { 0.0.isFinite() } shouldBe true
            }
            test("positive infinity returns false") {
                with(ops) { Double.POSITIVE_INFINITY.isFinite() } shouldBe false
            }
            test("NaN returns false") {
                with(ops) { Double.NaN.isFinite() } shouldBe false
            }
        }

        context("isZero") {
            test("positive zero returns true") {
                with(ops) { 0.0.isZero() } shouldBe true
            }
            test("negative zero returns true") {
                with(ops) { (-0.0).isZero() } shouldBe true
            }
            test("nonzero finite returns false") {
                with(ops) { 1.0.isZero() } shouldBe false
            }
            test("NaN returns false") {
                with(ops) { Double.NaN.isZero() } shouldBe false
            }
            test("infinity returns false") {
                with(ops) { Double.POSITIVE_INFINITY.isZero() } shouldBe false
            }
        }

        context("isNegative") {
            test("negative value returns true") {
                with(ops) { (-1.0).isNegative() } shouldBe true
            }
            test("negative zero returns true") {
                with(ops) { (-0.0).isNegative() } shouldBe true
            }
            test("positive value returns false") {
                with(ops) { 1.0.isNegative() } shouldBe false
            }
            test("positive zero returns false") {
                with(ops) { 0.0.isNegative() } shouldBe false
            }
        }

        context("negate") {
            test("negating a positive value gives negative") {
                with(ops) { 1.0.negate() } shouldBe -1.0
            }
            test("negating a negative value gives positive") {
                with(ops) { (-1.0).negate() } shouldBe 1.0
            }
            test("negating positive zero gives negative zero") {
                with(ops) { 0.0.negate() }.toRawBits() shouldBe (-0.0).toRawBits()
            }
            test("negating negative zero gives positive zero") {
                with(ops) { (-0.0).negate() } shouldBe 0.0
            }
            test("double negation is identity") {
                with(ops) { 1.5.negate().negate() } shouldBe 1.5
            }
            test("negating NaN produces NaN") {
                with(ops) { Double.NaN.negate() }.isNaN() shouldBe true
            }
        }

        context("abs") {
            test("abs of positive value is unchanged") {
                with(ops) { 1.0.abs() } shouldBe 1.0
            }
            test("abs of negative value removes sign") {
                with(ops) { (-1.0).abs() } shouldBe 1.0
            }
            test("abs of negative zero is positive zero") {
                with(ops) { (-0.0).abs() }.toRawBits() shouldBe 0.0.toRawBits()
            }
            test("abs of NaN is NaN") {
                with(ops) { Double.NaN.abs() }.isNaN() shouldBe true
            }
            test("abs of negative infinity is positive infinity") {
                with(ops) { Double.NEGATIVE_INFINITY.abs() } shouldBe Double.POSITIVE_INFINITY
            }
        }

        context("add") {
            test("1.0 + 1.0 = 2.0") {
                with(ops) { 1.0.add(1.0) } shouldBe 2.0
            }
            test("zero + x = x") {
                with(ops) { 0.0.add(1.5) } shouldBe 1.5
            }
            test("NaN + x = NaN") {
                with(ops) { Double.NaN.add(1.0) }.isNaN() shouldBe true
            }
            test("+Infinity + finite = +Infinity") {
                with(ops) { Double.POSITIVE_INFINITY.add(1.0) } shouldBe Double.POSITIVE_INFINITY
            }
            test("+Infinity + (-Infinity) = NaN") {
                with(ops) { Double.POSITIVE_INFINITY.add(Double.NEGATIVE_INFINITY) }.isNaN() shouldBe true
            }
        }

        context("subtract") {
            test("2.0 - 1.0 = 1.0") {
                with(ops) { 2.0.subtract(1.0) } shouldBe 1.0
            }
            test("x - zero = x") {
                with(ops) { 1.5.subtract(0.0) } shouldBe 1.5
            }
            test("x - x = 0") {
                with(ops) { 1.0.subtract(1.0) } shouldBe 0.0
            }
            test("NaN - x = NaN") {
                with(ops) { Double.NaN.subtract(1.0) }.isNaN() shouldBe true
            }
        }

        context("multiply") {
            test("2.0 * 3.0 = 6.0") {
                with(ops) { 2.0.multiply(3.0) } shouldBe 6.0
            }
            test("x * one = x") {
                with(ops) { 1.5.multiply(1.0) } shouldBe 1.5
            }
            test("x * zero = zero") {
                with(ops) { 1.5.multiply(0.0) } shouldBe 0.0
            }
            test("NaN * x = NaN") {
                with(ops) { Double.NaN.multiply(2.0) }.isNaN() shouldBe true
            }
            test("+Infinity * nonzero finite = +Infinity") {
                with(ops) { Double.POSITIVE_INFINITY.multiply(2.0) } shouldBe Double.POSITIVE_INFINITY
            }
            test("+Infinity * 0 = NaN (invalid operation)") {
                with(ops) { Double.POSITIVE_INFINITY.multiply(0.0) }.isNaN() shouldBe true
            }
        }

        context("divide") {
            test("6.0 / 2.0 = 3.0") {
                with(ops) { 6.0.divide(2.0) } shouldBe 3.0
            }
            test("x / one = x") {
                with(ops) { 1.5.divide(1.0) } shouldBe 1.5
            }
            test("finite / 0 = Infinity") {
                with(ops) { 1.0.divide(0.0) } shouldBe Double.POSITIVE_INFINITY
            }
            test("0 / 0 = NaN") {
                with(ops) { 0.0.divide(0.0) }.isNaN() shouldBe true
            }
            test("NaN / x = NaN") {
                with(ops) { Double.NaN.divide(2.0) }.isNaN() shouldBe true
            }
        }

        context("compareTo") {
            test("1.0 < 2.0") {
                (with(ops) { 1.0.compareTo(2.0) } < 0) shouldBe true
            }
            test("2.0 > 1.0") {
                (with(ops) { 2.0.compareTo(1.0) } > 0) shouldBe true
            }
            test("1.0 == 1.0") {
                with(ops) { 1.0.compareTo(1.0) } shouldBe 0
            }
            test("negative zero < positive zero (total order)") {
                (with(ops) { (-0.0).compareTo(0.0) } < 0) shouldBe true
            }
            test("NaN is ordered after +Infinity (total order)") {
                (with(ops) { Double.NaN.compareTo(Double.POSITIVE_INFINITY) } > 0) shouldBe true
            }
            test("-Infinity < MIN_VALUE") {
                (with(ops) { Double.NEGATIVE_INFINITY.compareTo(Double.MIN_VALUE) } < 0) shouldBe true
            }
        }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("Companion.bfloat16 returns the same instance on repeated access") {
            FloatingPointArithmetic.bfloat16 shouldBe FloatingPointArithmetic.bfloat16
        }
        test("Companion.float16 returns the same instance on repeated access") {
            FloatingPointArithmetic.float16 shouldBe FloatingPointArithmetic.float16
        }
        test("Companion.float returns the same instance on repeated access") {
            FloatingPointArithmetic.float shouldBe FloatingPointArithmetic.float
        }
        test("Companion.double returns the same instance on repeated access") {
            FloatingPointArithmetic.double shouldBe FloatingPointArithmetic.double
        }
        test("all four instances are distinct") {
            FloatingPointArithmetic.bfloat16 shouldNotBe FloatingPointArithmetic.float16
            FloatingPointArithmetic.float16 shouldNotBe FloatingPointArithmetic.float
            FloatingPointArithmetic.float shouldNotBe FloatingPointArithmetic.double
            FloatingPointArithmetic.bfloat16 shouldNotBe FloatingPointArithmetic.double
        }
    }
})
