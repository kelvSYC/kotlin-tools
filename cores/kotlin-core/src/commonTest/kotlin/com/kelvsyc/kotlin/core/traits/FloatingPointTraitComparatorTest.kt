package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.Float16
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe

/**
 * Verifies that [BinaryFloatingPoint.comparator] and [BinaryFloatingPoint.partialComparator] are
 * correctly implemented for each format's companion, and that [DecimalFloatingPoint.comparator]
 * and [DecimalFloatingPoint.partialComparator] work for [BidFloat].
 *
 * These tests exercise the trait interface rather than the concrete companions directly, ensuring the
 * correct properties are exposed through the right abstraction.
 */
class FloatingPointTraitComparatorTest : FunSpec({

    // ── Binary16 (Float16) ────────────────────────────────────────────────────

    context("Binary16.comparator") {
        val trait: BinaryFloatingPoint<Float16> = Binary16
        val cmp = trait.comparator

        test("NaN is ordered after finite values") {
            cmp.compare(Float16.NaN, Float16(0x3C00.toShort())) shouldBeGreaterThan 0
        }
        test("NaN compares equal to NaN") {
            cmp.compare(Float16.NaN, Float16.NaN) shouldBe 0
        }
        test("finite ordering: 1.0 < 2.0") {
            cmp.compare(Float16(0x3C00.toShort()), Float16(0x4000.toShort())) shouldBeLessThan 0
        }
        test("-infinity is ordered before +infinity") {
            cmp.compare(Float16.NEGATIVE_INFINITY, Float16.POSITIVE_INFINITY) shouldBeLessThan 0
        }
    }

    context("Binary16.partialComparator") {
        val trait: BinaryFloatingPoint<Float16> = Binary16
        val pcmp = trait.partialComparator

        test("NaN vs finite returns null") {
            pcmp.compare(Float16.NaN, Float16(0x3C00.toShort())) shouldBe null
        }
        test("finite vs NaN returns null") {
            pcmp.compare(Float16(0x3C00.toShort()), Float16.NaN) shouldBe null
        }
        test("NaN vs NaN returns null") {
            pcmp.compare(Float16.NaN, Float16.NaN) shouldBe null
        }
        test("1.0 < 2.0") {
            pcmp.compare(Float16(0x3C00.toShort()), Float16(0x4000.toShort()))!! shouldBeLessThan 0
        }
    }

    // ── BinaryBFloat16 (BFloat16) ─────────────────────────────────────────────

    context("BinaryBFloat16.comparator") {
        val trait: BinaryFloatingPoint<BFloat16> = BinaryBFloat16
        val cmp = trait.comparator

        test("NaN is ordered after finite values") {
            cmp.compare(BFloat16.NaN, BFloat16(0x3F80.toShort())) shouldBeGreaterThan 0
        }
        test("NaN compares equal to NaN") {
            cmp.compare(BFloat16.NaN, BFloat16.NaN) shouldBe 0
        }
        test("finite ordering: 1.0 < 2.0") {
            cmp.compare(BFloat16(0x3F80.toShort()), BFloat16(0x4000.toShort())) shouldBeLessThan 0
        }
        test("-infinity is ordered before +infinity") {
            cmp.compare(BFloat16.NEGATIVE_INFINITY, BFloat16.POSITIVE_INFINITY) shouldBeLessThan 0
        }
    }

    context("BinaryBFloat16.partialComparator") {
        val trait: BinaryFloatingPoint<BFloat16> = BinaryBFloat16
        val pcmp = trait.partialComparator

        test("NaN vs finite returns null") {
            pcmp.compare(BFloat16.NaN, BFloat16(0x3F80.toShort())) shouldBe null
        }
        test("finite vs NaN returns null") {
            pcmp.compare(BFloat16(0x3F80.toShort()), BFloat16.NaN) shouldBe null
        }
        test("NaN vs NaN returns null") {
            pcmp.compare(BFloat16.NaN, BFloat16.NaN) shouldBe null
        }
        test("1.0 < 2.0") {
            pcmp.compare(BFloat16(0x3F80.toShort()), BFloat16(0x4000.toShort()))!! shouldBeLessThan 0
        }
    }

    // ── Binary32 (Float) ──────────────────────────────────────────────────────

    context("Binary32.comparator") {
        val trait: BinaryFloatingPoint<Float> = Binary32
        val cmp = trait.comparator

        test("NaN is ordered after finite values") {
            cmp.compare(Float.NaN, 1.0f) shouldBeGreaterThan 0
        }
        test("NaN is ordered after +infinity") {
            cmp.compare(Float.NaN, Float.POSITIVE_INFINITY) shouldBeGreaterThan 0
        }
        test("NaN compares equal to NaN") {
            cmp.compare(Float.NaN, Float.NaN) shouldBe 0
        }
        test("-0.0f is strictly less than +0.0f") {
            cmp.compare(-0.0f, 0.0f) shouldBeLessThan 0
        }
        test("finite ordering: 1.0f < 2.0f") {
            cmp.compare(1.0f, 2.0f) shouldBeLessThan 0
        }
    }

    context("Binary32.partialComparator") {
        val trait: BinaryFloatingPoint<Float> = Binary32
        val pcmp = trait.partialComparator

        test("NaN vs finite returns null") {
            pcmp.compare(Float.NaN, 1.0f) shouldBe null
        }
        test("finite vs NaN returns null") {
            pcmp.compare(1.0f, Float.NaN) shouldBe null
        }
        test("NaN vs NaN returns null") {
            pcmp.compare(Float.NaN, Float.NaN) shouldBe null
        }
        test("1.0f < 2.0f") {
            pcmp.compare(1.0f, 2.0f)!! shouldBeLessThan 0
        }
    }

    // ── Binary64 (Double) ─────────────────────────────────────────────────────

    context("Binary64.comparator") {
        val trait: BinaryFloatingPoint<Double> = Binary64
        val cmp = trait.comparator

        test("NaN is ordered after finite values") {
            cmp.compare(Double.NaN, 1.0) shouldBeGreaterThan 0
        }
        test("NaN is ordered after +infinity") {
            cmp.compare(Double.NaN, Double.POSITIVE_INFINITY) shouldBeGreaterThan 0
        }
        test("NaN compares equal to NaN") {
            cmp.compare(Double.NaN, Double.NaN) shouldBe 0
        }
        test("-0.0 is strictly less than +0.0") {
            cmp.compare(-0.0, 0.0) shouldBeLessThan 0
        }
        test("finite ordering: 1.0 < 2.0") {
            cmp.compare(1.0, 2.0) shouldBeLessThan 0
        }
    }

    context("Binary64.partialComparator") {
        val trait: BinaryFloatingPoint<Double> = Binary64
        val pcmp = trait.partialComparator

        test("NaN vs finite returns null") {
            pcmp.compare(Double.NaN, 1.0) shouldBe null
        }
        test("finite vs NaN returns null") {
            pcmp.compare(1.0, Double.NaN) shouldBe null
        }
        test("NaN vs NaN returns null") {
            pcmp.compare(Double.NaN, Double.NaN) shouldBe null
        }
        test("1.0 < 2.0") {
            pcmp.compare(1.0, 2.0)!! shouldBeLessThan 0
        }
    }

    // ── Bid32 (BidFloat) ──────────────────────────────────────────────────────

    context("Bid32.comparator") {
        // Access BidFloat.Companion through the DecimalFloatingPoint trait interface.
        val trait: DecimalFloatingPoint<BidFloat> = BidFloat
        val cmp = trait.comparator

        test("NaN is ordered after finite values") {
            cmp.compare(BidFloat(0x7E000000), BidFloat(0x32800001)) shouldBeGreaterThan 0
        }
        test("NaN is ordered after +infinity") {
            cmp.compare(BidFloat(0x7E000000), BidFloat(0x78000000)) shouldBeGreaterThan 0
        }
        test("NaN compares equal to NaN") {
            cmp.compare(BidFloat(0x7E000000), BidFloat(0x7C000000)) shouldBe 0
        }
        test("-0 is strictly less than +0") {
            cmp.compare(BidFloat(0x80000000.toInt()), BidFloat(0x00000000)) shouldBeLessThan 0
        }
        test("finite ordering: +1.0 < +2.0") {
            cmp.compare(BidFloat(0x32800001), BidFloat(0x32800002)) shouldBeLessThan 0
        }
        test("cohort-distinct representations compare equal") {
            // 1×10^0 vs 10×10^-1
            cmp.compare(BidFloat(0x32800001), BidFloat(0x3200000A)) shouldBe 0
        }
    }

    context("Bid32.partialComparator") {
        val trait: DecimalFloatingPoint<BidFloat> = BidFloat
        val pcmp = trait.partialComparator

        test("NaN vs finite returns null") {
            pcmp.compare(BidFloat(0x7E000000), BidFloat(0x32800001)) shouldBe null
        }
        test("finite vs NaN returns null") {
            pcmp.compare(BidFloat(0x32800001), BidFloat(0x7E000000)) shouldBe null
        }
        test("NaN vs NaN returns null") {
            pcmp.compare(BidFloat(0x7E000000), BidFloat(0x7C000000)) shouldBe null
        }
        test("+1.0 < +2.0") {
            pcmp.compare(BidFloat(0x32800001), BidFloat(0x32800002))!! shouldBeLessThan 0
        }
    }
})
