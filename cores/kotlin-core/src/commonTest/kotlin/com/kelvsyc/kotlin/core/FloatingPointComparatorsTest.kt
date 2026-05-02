package com.kelvsyc.kotlin.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe

class FloatingPointComparatorsTest : FunSpec({

    // ── Double.fuzzyComparator ────────────────────────────────────────────────

    context("Double.fuzzyComparator") {
        val cmp = Double.fuzzyComparator(0.01)

        test("values within tolerance compare as equal") {
            cmp.compare(1.0, 1.005) shouldBe 0
        }

        // 0.125 == 1/8 is exactly representable, so |1.0 - 1.125| == 0.125 exactly.
        test("values at exact tolerance boundary compare as equal") {
            Double.fuzzyComparator(0.125).compare(1.0, 1.125) shouldBe 0
        }

        test("values beyond tolerance preserve natural order (less)") {
            cmp.compare(1.0, 1.02) shouldBeLessThan 0
        }

        test("values beyond tolerance preserve natural order (greater)") {
            cmp.compare(1.02, 1.0) shouldBeGreaterThan 0
        }

        test("zero tolerance: equal values compare as equal") {
            Double.fuzzyComparator(0.0).compare(1.0, 1.0) shouldBe 0
        }

        test("zero tolerance: distinct values preserve natural order") {
            Double.fuzzyComparator(0.0).compare(1.0, 2.0) shouldBeLessThan 0
        }

        test("NaN left operand sorts greater than finite") {
            cmp.compare(Double.NaN, 1.0) shouldBeGreaterThan 0
        }

        test("NaN right operand sorts less than NaN on left") {
            cmp.compare(1.0, Double.NaN) shouldBeLessThan 0
        }

        test("NaN on both sides compares as equal") {
            cmp.compare(Double.NaN, Double.NaN) shouldBe 0
        }

        test("negative tolerance throws") {
            shouldThrow<IllegalArgumentException> { Double.fuzzyComparator(-0.01) }
        }

        test("NaN tolerance throws") {
            shouldThrow<IllegalArgumentException> { Double.fuzzyComparator(Double.NaN) }
        }
    }

    // ── Float.fuzzyComparator ─────────────────────────────────────────────────

    context("Float.fuzzyComparator") {
        val cmp = Float.fuzzyComparator(0.01f)

        test("values within tolerance compare as equal") {
            cmp.compare(1.0f, 1.005f) shouldBe 0
        }

        test("values at exact tolerance boundary compare as equal") {
            Float.fuzzyComparator(0.125f).compare(1.0f, 1.125f) shouldBe 0
        }

        test("values beyond tolerance preserve natural order (less)") {
            cmp.compare(1.0f, 1.02f) shouldBeLessThan 0
        }

        test("values beyond tolerance preserve natural order (greater)") {
            cmp.compare(1.02f, 1.0f) shouldBeGreaterThan 0
        }

        test("NaN left operand sorts greater than finite") {
            cmp.compare(Float.NaN, 1.0f) shouldBeGreaterThan 0
        }

        test("NaN right operand sorts less than NaN on left") {
            cmp.compare(1.0f, Float.NaN) shouldBeLessThan 0
        }

        test("NaN on both sides compares as equal") {
            cmp.compare(Float.NaN, Float.NaN) shouldBe 0
        }

        test("negative tolerance throws") {
            shouldThrow<IllegalArgumentException> { Float.fuzzyComparator(-0.01f) }
        }

        test("NaN tolerance throws") {
            shouldThrow<IllegalArgumentException> { Float.fuzzyComparator(Float.NaN) }
        }
    }

    // ── Float16.fuzzyComparator ───────────────────────────────────────────────

    context("Float16.fuzzyComparator") {
        val tol = 0.01f.toFloat16()
        val cmp = Float16.fuzzyComparator(tol)

        test("values within tolerance compare as equal") {
            cmp.compare(1.0f.toFloat16(), 1.005f.toFloat16()) shouldBe 0
        }

        test("values at exact tolerance boundary compare as equal") {
            Float16.fuzzyComparator(0.125f.toFloat16()).compare(1.0f.toFloat16(), 1.125f.toFloat16()) shouldBe 0
        }

        test("values beyond tolerance preserve natural order (less)") {
            cmp.compare(1.0f.toFloat16(), 1.1f.toFloat16()) shouldBeLessThan 0
        }

        test("values beyond tolerance preserve natural order (greater)") {
            cmp.compare(1.1f.toFloat16(), 1.0f.toFloat16()) shouldBeGreaterThan 0
        }

        test("zero tolerance: equal values compare as equal") {
            Float16.fuzzyComparator(0.0f.toFloat16()).compare(1.0f.toFloat16(), 1.0f.toFloat16()) shouldBe 0
        }

        test("zero tolerance: distinct values preserve natural order") {
            Float16.fuzzyComparator(0.0f.toFloat16()).compare(1.0f.toFloat16(), 2.0f.toFloat16()) shouldBeLessThan 0
        }

        test("NaN left operand sorts greater than finite") {
            cmp.compare(Float16.NaN, 1.0f.toFloat16()) shouldBeGreaterThan 0
        }

        test("NaN right operand sorts less than NaN on left") {
            cmp.compare(1.0f.toFloat16(), Float16.NaN) shouldBeLessThan 0
        }

        test("NaN on both sides compares as equal") {
            cmp.compare(Float16.NaN, Float16.NaN) shouldBe 0
        }

        test("negative tolerance throws") {
            shouldThrow<IllegalArgumentException> { Float16.fuzzyComparator((-0.01f).toFloat16()) }
        }

        test("NaN tolerance throws") {
            shouldThrow<IllegalArgumentException> { Float16.fuzzyComparator(Float16.NaN) }
        }
    }

    // ── BFloat16.fuzzyComparator ──────────────────────────────────────────────

    context("BFloat16.fuzzyComparator") {
        val tol = 0.01f.toBFloat16()
        val cmp = BFloat16.fuzzyComparator(tol)

        test("values within tolerance compare as equal") {
            cmp.compare(1.0f.toBFloat16(), 1.005f.toBFloat16()) shouldBe 0
        }

        test("values at exact tolerance boundary compare as equal") {
            BFloat16.fuzzyComparator(0.125f.toBFloat16()).compare(1.0f.toBFloat16(), 1.125f.toBFloat16()) shouldBe 0
        }

        test("values beyond tolerance preserve natural order (less)") {
            cmp.compare(1.0f.toBFloat16(), 1.1f.toBFloat16()) shouldBeLessThan 0
        }

        test("values beyond tolerance preserve natural order (greater)") {
            cmp.compare(1.1f.toBFloat16(), 1.0f.toBFloat16()) shouldBeGreaterThan 0
        }

        test("zero tolerance: equal values compare as equal") {
            BFloat16.fuzzyComparator(0.0f.toBFloat16()).compare(1.0f.toBFloat16(), 1.0f.toBFloat16()) shouldBe 0
        }

        test("zero tolerance: distinct values preserve natural order") {
            BFloat16.fuzzyComparator(0.0f.toBFloat16()).compare(1.0f.toBFloat16(), 2.0f.toBFloat16()) shouldBeLessThan 0
        }

        test("NaN left operand sorts greater than finite") {
            cmp.compare(BFloat16.NaN, 1.0f.toBFloat16()) shouldBeGreaterThan 0
        }

        test("NaN right operand sorts less than NaN on left") {
            cmp.compare(1.0f.toBFloat16(), BFloat16.NaN) shouldBeLessThan 0
        }

        test("NaN on both sides compares as equal") {
            cmp.compare(BFloat16.NaN, BFloat16.NaN) shouldBe 0
        }

        test("negative tolerance throws") {
            shouldThrow<IllegalArgumentException> { BFloat16.fuzzyComparator((-0.01f).toBFloat16()) }
        }

        test("NaN tolerance throws") {
            shouldThrow<IllegalArgumentException> { BFloat16.fuzzyComparator(BFloat16.NaN) }
        }
    }
})
