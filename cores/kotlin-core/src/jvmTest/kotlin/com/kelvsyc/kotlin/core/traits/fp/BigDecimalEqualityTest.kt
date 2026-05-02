package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.traits.ValueEquality
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.math.BigDecimal

class BigDecimalEqualityTest : FunSpec({

    // ── ValueEquality.Companion.bigDecimalNumerical ───────────────────────────

    context("ValueEquality.Companion.bigDecimalNumerical") {
        val eq = ValueEquality.bigDecimalNumerical

        test("same value same scale") {
            with(eq) { BigDecimal("1.0").isEqualTo(BigDecimal("1.0")) } shouldBe true
        }
        test("same value different scale: 1.0 == 1.00") {
            with(eq) { BigDecimal("1.0").isEqualTo(BigDecimal("1.00")) } shouldBe true
        }
        test("same value different scale: 1 == 1.000") {
            with(eq) { BigDecimal("1").isEqualTo(BigDecimal("1.000")) } shouldBe true
        }
        test("zero scales: 0 == 0.00") {
            with(eq) { BigDecimal("0").isEqualTo(BigDecimal("0.00")) } shouldBe true
        }
        test("different values are not equal") {
            with(eq) { BigDecimal("1.0").isEqualTo(BigDecimal("2.0")) } shouldBe false
        }
        test("1.0 != 1.1") {
            with(eq) { BigDecimal("1.0").isEqualTo(BigDecimal("1.1")) } shouldBe false
        }
        test("negative values: -1.0 == -1.00") {
            with(eq) { BigDecimal("-1.0").isEqualTo(BigDecimal("-1.00")) } shouldBe true
        }
        test("-0 == 0 (no signed zero in BigDecimal)") {
            with(eq) { BigDecimal("0").isEqualTo(BigDecimal("0")) } shouldBe true
        }
    }

    // ── ValueEquality.Companion.bigDecimalEquivalence ─────────────────────────

    context("ValueEquality.Companion.bigDecimalEquivalence") {
        val eq = ValueEquality.bigDecimalEquivalence

        test("same value same scale") {
            with(eq) { BigDecimal("1.0").isEqualTo(BigDecimal("1.0")) } shouldBe true
        }
        test("same value different scale: 1.0 != 1.00") {
            with(eq) { BigDecimal("1.0").isEqualTo(BigDecimal("1.00")) } shouldBe false
        }
        test("same value different scale: 1 != 1.000") {
            with(eq) { BigDecimal("1").isEqualTo(BigDecimal("1.000")) } shouldBe false
        }
        test("zero scales: 0 != 0.00") {
            with(eq) { BigDecimal("0").isEqualTo(BigDecimal("0.00")) } shouldBe false
        }
        test("same scale same value: 1.00 == 1.00") {
            with(eq) { BigDecimal("1.00").isEqualTo(BigDecimal("1.00")) } shouldBe true
        }
        test("different values are not equal") {
            with(eq) { BigDecimal("1.0").isEqualTo(BigDecimal("2.0")) } shouldBe false
        }
    }

    // ── The two instances are distinct ────────────────────────────────────────

    context("the two semantics differ for same-value different-scale inputs") {
        val num = ValueEquality.bigDecimalNumerical
        val eqv = ValueEquality.bigDecimalEquivalence
        val a = BigDecimal("1.0")
        val b = BigDecimal("1.00")

        test("numerical treats them as equal") { with(num) { a.isEqualTo(b) } shouldBe true }
        test("equivalence treats them as distinct") { with(eqv) { a.isEqualTo(b) } shouldBe false }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("bigDecimalNumerical is stable") {
            ValueEquality.bigDecimalNumerical shouldBeSameInstanceAs ValueEquality.bigDecimalNumerical
        }
        test("bigDecimalEquivalence is stable") {
            ValueEquality.bigDecimalEquivalence shouldBeSameInstanceAs ValueEquality.bigDecimalEquivalence
        }
        test("the two instances are distinct") {
            ValueEquality.bigDecimalNumerical shouldNotBe ValueEquality.bigDecimalEquivalence
        }
    }
})
