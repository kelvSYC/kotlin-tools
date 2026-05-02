package com.kelvsyc.kotlin.commons.lang

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.lang3.Range

class RangeExtensionsTest : FunSpec({

    context("ClosedRange.toCommonsRange") {
        test("minimum and maximum match the original bounds") {
            val range = (1..10).toCommonsRange()
            range.minimum shouldBe 1
            range.maximum shouldBe 10
        }
        test("single-element range produces equal minimum and maximum") {
            val range = (5..5).toCommonsRange()
            range.minimum shouldBe 5
            range.maximum shouldBe 5
        }
        test("works with String ranges") {
            val range = ("apple".."mango").toCommonsRange()
            range.minimum shouldBe "apple"
            range.maximum shouldBe "mango"
        }
        test("contains is consistent with the original range") {
            val range = (1..10).toCommonsRange()
            range.contains(1) shouldBe true
            range.contains(5) shouldBe true
            range.contains(10) shouldBe true
            range.contains(11) shouldBe false
        }
    }

    context("Range.toClosedRange") {
        test("start and endInclusive match the original bounds") {
            val range = Range.of(1, 10).toClosedRange()
            range.start shouldBe 1
            range.endInclusive shouldBe 10
        }
        test("single-element range round-trips correctly") {
            val range = Range.of(7, 7).toClosedRange()
            range.start shouldBe 7
            range.endInclusive shouldBe 7
        }
        test("works with String ranges") {
            val range = Range.of("apple", "mango").toClosedRange()
            range.start shouldBe "apple"
            range.endInclusive shouldBe "mango"
        }
    }

    context("round-trip") {
        test("ClosedRange → CommonsRange → ClosedRange preserves bounds") {
            val original = 3..17
            val roundTripped = original.toCommonsRange().toClosedRange()
            roundTripped.start shouldBe original.start
            roundTripped.endInclusive shouldBe original.endInclusive
        }
        test("CommonsRange → ClosedRange → CommonsRange preserves bounds") {
            val original = Range.of(3, 17)
            val roundTripped = original.toClosedRange().toCommonsRange()
            roundTripped.minimum shouldBe original.minimum
            roundTripped.maximum shouldBe original.maximum
        }
    }
})
