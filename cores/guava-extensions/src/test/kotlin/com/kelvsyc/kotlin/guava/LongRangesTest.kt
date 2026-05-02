package com.kelvsyc.kotlin.guava

import com.google.common.collect.Range
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LongRangesTest : FunSpec({

    context("LongRange.toGuavaRange()") {
        test("produces a closed Guava range") {
            (3L..10L).toGuavaRange() shouldBe Range.closed(3L, 10L)
        }

        test("single-element range") {
            (5L..5L).toGuavaRange() shouldBe Range.closed(5L, 5L)
        }

        test("large values") {
            (Long.MIN_VALUE..Long.MAX_VALUE).toGuavaRange() shouldBe Range.closed(Long.MIN_VALUE, Long.MAX_VALUE)
        }
    }

    context("Range<Long>.toLongRange()") {
        test("closed bounded range converts correctly") {
            Range.closed(3L, 10L).toLongRange() shouldBe 3L..10L
        }

        test("throws for open lower bound") {
            shouldThrow<IllegalArgumentException> { Range.openClosed(3L, 10L).toLongRange() }
        }

        test("throws for open upper bound") {
            shouldThrow<IllegalArgumentException> { Range.closedOpen(3L, 10L).toLongRange() }
        }

        test("throws for unbounded lower") {
            shouldThrow<IllegalArgumentException> { Range.atMost(10L).toLongRange() }
        }

        test("throws for unbounded upper") {
            shouldThrow<IllegalArgumentException> { Range.atLeast(3L).toLongRange() }
        }
    }

    context("longRangeToGuavaRange") {
        val conv = longRangeToGuavaRange

        test("forward produces closed Guava range") {
            conv(3L..10L) shouldBe Range.closed(3L, 10L)
        }

        test("backward returns LongRange") {
            conv.reverse(Range.closed(3L, 10L)) shouldBe 3L..10L
        }

        test("round-trip forward then backward") {
            val r = 1L..1_000_000L
            conv.reverse(conv(r)) shouldBe r
        }

        test("round-trip backward then forward") {
            val r = Range.closed(1L, 1_000_000L)
            conv(conv.reverse(r)) shouldBe r
        }

        test("backward throws for non-closed range") {
            shouldThrow<IllegalArgumentException> { conv.reverse(Range.closedOpen(3L, 10L)) }
        }

        test("singleton identity") {
            longRangeToGuavaRange shouldBe longRangeToGuavaRange
        }
    }
})
