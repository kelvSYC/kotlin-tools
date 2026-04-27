package com.kelvsyc.kotlin.core.fp

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class DoubleDoubleTest : FunSpec({

    // ── Constants ─────────────────────────────────────────────────────────────

    context("constants") {
        test("NaN.isNaN()") { DoubleDouble.NaN.isNaN() shouldBe true }
        test("POSITIVE_INFINITY.isInfinite()") { DoubleDouble.POSITIVE_INFINITY.isInfinite() shouldBe true }
        test("NEGATIVE_INFINITY.isInfinite()") { DoubleDouble.NEGATIVE_INFINITY.isInfinite() shouldBe true }
        test("ZERO.high is positive zero") { DoubleDouble.ZERO.high.toRawBits() shouldBe 0L }
        test("ZERO.low is zero") { DoubleDouble.ZERO.low shouldBe 0.0 }
        test("ONE.high is 1.0") { DoubleDouble.ONE.high shouldBe 1.0 }
        test("ONE.low is 0.0") { DoubleDouble.ONE.low shouldBe 0.0 }
    }

    // ── create ────────────────────────────────────────────────────────────────

    context("create") {
        test("stores components as-is when |high| > |low|") {
            val dd = DoubleDouble.create(1.0, 1e-17)
            dd.high shouldBe 1.0
            dd.low shouldBe 1e-17
        }
        test("zero low is always valid") {
            val dd = DoubleDouble.create(42.0, 0.0)
            dd.high shouldBe 42.0
        }
        test("equal magnitudes are valid (boundary)") {
            val dd = DoubleDouble.create(1.0, -1.0)
            dd.high shouldBe 1.0
        }
        test("throws when |high| < |low|") {
            shouldThrow<IllegalArgumentException> { DoubleDouble.create(1e-17, 1.0) }
        }
    }

    // ── Classification ────────────────────────────────────────────────────────

    context("isNaN") {
        test("NaN returns true") { DoubleDouble.NaN.isNaN() shouldBe true }
        test("positive infinity returns false") { DoubleDouble.POSITIVE_INFINITY.isNaN() shouldBe false }
        test("finite value returns false") { DoubleDouble.ONE.isNaN() shouldBe false }
        test("zero returns false") { DoubleDouble.ZERO.isNaN() shouldBe false }
    }

    context("isInfinite") {
        test("positive infinity returns true") { DoubleDouble.POSITIVE_INFINITY.isInfinite() shouldBe true }
        test("negative infinity returns true") { DoubleDouble.NEGATIVE_INFINITY.isInfinite() shouldBe true }
        test("NaN returns false") { DoubleDouble.NaN.isInfinite() shouldBe false }
        test("finite value returns false") { DoubleDouble.ONE.isInfinite() shouldBe false }
    }

    context("isFinite") {
        test("finite value returns true") { DoubleDouble.ONE.isFinite() shouldBe true }
        test("zero returns true") { DoubleDouble.ZERO.isFinite() shouldBe true }
        test("positive infinity returns false") { DoubleDouble.POSITIVE_INFINITY.isFinite() shouldBe false }
        test("NaN returns false") { DoubleDouble.NaN.isFinite() shouldBe false }
    }

    // ── Sign ──────────────────────────────────────────────────────────────────

    context("unaryMinus") {
        test("negates both components") {
            val r = -DoubleDouble.create(1.0, 1e-17)
            r.high shouldBe -1.0
            r.low shouldBe -1e-17
        }
        test("double negation is identity") {
            val v = DoubleDouble.create(1.5, 1e-17)
            -(-v) shouldBe v
        }
        test("negating NaN produces NaN") {
            (-DoubleDouble.NaN).isNaN() shouldBe true
        }
        test("negating POSITIVE_INFINITY gives a negative infinity") {
            // unaryMinus flips the sign of both components; -0.0 ≠ 0.0 under Double.equals,
            // so structural equality with NEGATIVE_INFINITY (which has low = 0.0) would fail.
            val r = -DoubleDouble.POSITIVE_INFINITY
            r.isInfinite() shouldBe true
            (r.high < 0) shouldBe true
        }
        test("negating positive zero flips sign bit on high") {
            (-DoubleDouble.ZERO).high.toRawBits() shouldBe (-0.0).toRawBits()
        }
    }

    // ── Ordering ──────────────────────────────────────────────────────────────

    context("compareTo") {
        test("1 < 2") {
            (DoubleDouble.ONE < DoubleDouble.create(2.0, 0.0)) shouldBe true
        }
        test("2 > 1") {
            (DoubleDouble.create(2.0, 0.0) > DoubleDouble.ONE) shouldBe true
        }
        test("equal values compare as 0") {
            DoubleDouble.ONE.compareTo(DoubleDouble.ONE) shouldBe 0
        }
        test("high component takes precedence over low") {
            val a = DoubleDouble.create(2.0, -1e-10)
            val b = DoubleDouble.create(1.0, 1e-10)
            (a > b) shouldBe true
        }
        test("low component breaks tie when high components are equal") {
            val a = DoubleDouble.create(1.0, 1e-17)
            val b = DoubleDouble.create(1.0, -1e-17)
            (a > b) shouldBe true
        }
        test("NaN is ordered after +Infinity") {
            (DoubleDouble.NaN > DoubleDouble.POSITIVE_INFINITY) shouldBe true
        }
        test("NaN equals NaN under compareTo") {
            DoubleDouble.NaN.compareTo(DoubleDouble.NaN) shouldBe 0
        }
        test("-Infinity < ZERO") {
            (DoubleDouble.NEGATIVE_INFINITY < DoubleDouble.ZERO) shouldBe true
        }
    }

    // ── Identity ──────────────────────────────────────────────────────────────

    context("equals") {
        test("same instance is equal") {
            val v = DoubleDouble.ONE
            v shouldBe v
        }
        test("matching components are equal") {
            DoubleDouble.create(1.0, 1e-17) shouldBe DoubleDouble.create(1.0, 1e-17)
        }
        test("differing low components are not equal") {
            DoubleDouble.create(1.0, 1e-17) shouldNotBe DoubleDouble.create(1.0, 2e-17)
        }
        test("NaN equals NaN (structural)") {
            DoubleDouble.NaN shouldBe DoubleDouble.NaN
        }
        test("positive zero and negative zero are not structurally equal") {
            DoubleDouble.ZERO shouldNotBe DoubleDouble(-0.0, 0.0)
        }
        test("non-DoubleDouble is never equal") {
            (DoubleDouble.ONE.equals("1.0")) shouldBe false
        }
    }

    context("hashCode") {
        test("equal values have equal hash codes") {
            DoubleDouble.create(1.0, 1e-17).hashCode() shouldBe DoubleDouble.create(1.0, 1e-17).hashCode()
        }
        test("NaN hash code is consistent") {
            DoubleDouble.NaN.hashCode() shouldBe DoubleDouble.NaN.hashCode()
        }
    }

    context("toString") {
        test("formats as (high + low)") {
            DoubleDouble.create(1.0, 1e-17).toString() shouldBe "(1.0 + 1.0E-17)"
        }
        test("NaN") {
            DoubleDouble.NaN.toString() shouldBe "(NaN + 0.0)"
        }
        test("ZERO") {
            DoubleDouble.ZERO.toString() shouldBe "(0.0 + 0.0)"
        }
    }

    // ── ValueEquality ─────────────────────────────────────────────────────────

    context("numericalEquality") {
        val eq = DoubleDouble.numericalEquality

        test("equal finite values") {
            with(eq) { DoubleDouble.ONE.isEqualTo(DoubleDouble.ONE) } shouldBe true
        }
        test("different values are not equal") {
            with(eq) { DoubleDouble.ONE.isEqualTo(DoubleDouble.ZERO) } shouldBe false
        }
        test("NaN is not equal to itself") {
            with(eq) { DoubleDouble.NaN.isEqualTo(DoubleDouble.NaN) } shouldBe false
        }
        test("NaN is not equal to a finite value") {
            with(eq) { DoubleDouble.NaN.isEqualTo(DoubleDouble.ONE) } shouldBe false
        }
        test("positive zero equals negative zero") {
            with(eq) { DoubleDouble.ZERO.isEqualTo(DoubleDouble(-0.0, 0.0)) } shouldBe true
        }
    }

    context("equivalenceEquality") {
        val eq = DoubleDouble.equivalenceEquality

        test("equal finite values") {
            with(eq) { DoubleDouble.ONE.isEqualTo(DoubleDouble.ONE) } shouldBe true
        }
        test("different values are not equal") {
            with(eq) { DoubleDouble.ONE.isEqualTo(DoubleDouble.ZERO) } shouldBe false
        }
        test("NaN equals NaN") {
            with(eq) { DoubleDouble.NaN.isEqualTo(DoubleDouble.NaN) } shouldBe true
        }
        test("NaN is not equal to a finite value") {
            with(eq) { DoubleDouble.NaN.isEqualTo(DoubleDouble.ONE) } shouldBe false
        }
        test("positive zero does not equal negative zero") {
            with(eq) { DoubleDouble.ZERO.isEqualTo(DoubleDouble(-0.0, 0.0)) } shouldBe false
        }
    }
})
