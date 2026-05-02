package com.kelvsyc.kotlin.guava

import com.google.common.collect.Range
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IntRangesTest : FunSpec({

    context("IntRange.toGuavaRange()") {
        test("produces a closed Guava range") {
            (3..10).toGuavaRange() shouldBe Range.closed(3, 10)
        }

        test("single-element range") {
            (5..5).toGuavaRange() shouldBe Range.closed(5, 5)
        }

        test("negative bounds") {
            (-5..-1).toGuavaRange() shouldBe Range.closed(-5, -1)
        }
    }

    context("Range<Int>.toIntRange()") {
        test("closed bounded range converts correctly") {
            Range.closed(3, 10).toIntRange() shouldBe 3..10
        }

        test("single-element range") {
            Range.closed(5, 5).toIntRange() shouldBe 5..5
        }

        test("throws for open lower bound") {
            shouldThrow<IllegalArgumentException> { Range.openClosed(3, 10).toIntRange() }
        }

        test("throws for open upper bound") {
            shouldThrow<IllegalArgumentException> { Range.closedOpen(3, 10).toIntRange() }
        }

        test("throws for unbounded lower") {
            shouldThrow<IllegalArgumentException> { Range.atMost(10).toIntRange() }
        }

        test("throws for unbounded upper") {
            shouldThrow<IllegalArgumentException> { Range.atLeast(3).toIntRange() }
        }
    }

    context("intRangeToGuavaRange") {
        val conv = intRangeToGuavaRange

        test("forward produces closed Guava range") {
            conv(3..10) shouldBe Range.closed(3, 10)
        }

        test("backward returns IntRange") {
            conv.reverse(Range.closed(3, 10)) shouldBe 3..10
        }

        test("round-trip forward then backward") {
            val r = 1..100
            conv.reverse(conv(r)) shouldBe r
        }

        test("round-trip backward then forward") {
            val r = Range.closed(1, 100)
            conv(conv.reverse(r)) shouldBe r
        }

        test("backward throws for non-closed range") {
            shouldThrow<IllegalArgumentException> { conv.reverse(Range.closedOpen(3, 10)) }
        }

        test("singleton identity") {
            intRangeToGuavaRange shouldBe intRangeToGuavaRange
        }
    }
})
