package com.kelvsyc.kotlin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.comparables.shouldBeGreaterThan

class PartialComparatorTest : FunSpec({

    // A partial comparator over floats that treats NaN as incomparable.
    val floatPartial = Float.partialComparator

    // ── reversed ─────────────────────────────────────────────────────────────

    context("reversed") {
        val reversed = floatPartial.reversed()

        test("flips negative result to positive") {
            reversed.compare(2f, 1f)!! shouldBeLessThan 0
        }

        test("flips positive result to negative") {
            reversed.compare(1f, 2f)!! shouldBeGreaterThan 0
        }

        test("preserves zero") {
            reversed.compare(1f, 1f) shouldBe 0
        }

        test("propagates null for NaN left operand") {
            reversed.compare(Float.NaN, 1f) shouldBe null
        }

        test("propagates null for NaN right operand") {
            reversed.compare(1f, Float.NaN) shouldBe null
        }

        test("propagates null for NaN both operands") {
            reversed.compare(Float.NaN, Float.NaN) shouldBe null
        }
    }

    // ── thenComparing (PartialComparator overload) ────────────────────────────

    context("thenComparing with PartialComparator") {
        // Primary: compare by integer part; secondary: compare by fractional part (via Float partial).
        // We approximate this with a pair-based partial comparator for clarity.
        data class Pair(val primary: Float, val secondary: Float)

        val byPrimary = compareByPartial(floatPartial) { p: Pair -> p.primary }
        val bySecondary = compareByPartial(floatPartial) { p: Pair -> p.secondary }
        val combined = byPrimary.thenComparing(bySecondary)

        test("primary ordering respected when primaries differ") {
            combined.compare(Pair(1f, 9f), Pair(2f, 0f))!! shouldBeLessThan 0
        }

        test("secondary ordering used when primaries are equal") {
            combined.compare(Pair(1f, 0f), Pair(1f, 1f))!! shouldBeLessThan 0
        }

        test("returns zero when both keys are equal") {
            combined.compare(Pair(1f, 2f), Pair(1f, 2f)) shouldBe 0
        }

        test("null from primary propagates — secondary is not consulted") {
            combined.compare(Pair(Float.NaN, 0f), Pair(Float.NaN, 1f)) shouldBe null
        }

        test("null from secondary propagates when primaries are equal") {
            combined.compare(Pair(1f, Float.NaN), Pair(1f, 0f)) shouldBe null
        }

        test("secondary not consulted when primary is non-zero") {
            // primary differs, secondary NaN should not matter
            combined.compare(Pair(1f, Float.NaN), Pair(2f, Float.NaN))!! shouldBeLessThan 0
        }
    }

    // ── thenComparing (Comparator overload) ───────────────────────────────────

    context("thenComparing with Comparator") {
        data class Pair(val primary: Float, val secondary: Int)

        val byPrimary = compareByPartial(floatPartial) { p: Pair -> p.primary }
        val bySecondary = compareBy<Pair> { it.secondary }
        val combined = byPrimary.thenComparing(bySecondary)

        test("primary ordering respected when primaries differ") {
            combined.compare(Pair(1f, 9), Pair(2f, 0))!! shouldBeLessThan 0
        }

        test("total secondary ordering used when primaries are equal") {
            combined.compare(Pair(1f, 0), Pair(1f, 1))!! shouldBeLessThan 0
        }

        test("null from primary propagates even though secondary is total") {
            combined.compare(Pair(Float.NaN, 0), Pair(Float.NaN, 1)) shouldBe null
        }
    }

    // ── asPartialComparator ───────────────────────────────────────────────────

    context("asPartialComparator") {
        val total = compareBy<Int> { it }
        val partial = total.asPartialComparator()

        test("returns negative for less-than") {
            partial.compare(1, 2)!! shouldBeLessThan 0
        }

        test("returns positive for greater-than") {
            partial.compare(2, 1)!! shouldBeGreaterThan 0
        }

        test("returns zero for equal") {
            partial.compare(1, 1) shouldBe 0
        }

        test("never returns null") {
            // Total order — all pairs are comparable
            partial.compare(Int.MIN_VALUE, Int.MAX_VALUE)!! shouldBeLessThan 0
        }
    }

    // ── asComparator ─────────────────────────────────────────────────────────

    context("asComparator") {
        val total = floatPartial.asComparator(fallback = 0)

        test("returns negative for less-than") {
            total.compare(1f, 2f) shouldBeLessThan 0
        }

        test("returns positive for greater-than") {
            total.compare(2f, 1f) shouldBeGreaterThan 0
        }

        test("returns zero for equal") {
            total.compare(1f, 1f) shouldBe 0
        }

        test("uses fallback for NaN left operand") {
            total.compare(Float.NaN, 1f) shouldBe 0
        }

        test("uses fallback for NaN right operand") {
            total.compare(1f, Float.NaN) shouldBe 0
        }

        test("fallback value is respected") {
            val nanLast = floatPartial.asComparator(fallback = 1)
            nanLast.compare(Float.NaN, 1f) shouldBe 1
        }
    }
})
