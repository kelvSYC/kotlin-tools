package com.kelvsyc.kotlin.guava

import com.google.common.collect.BoundType
import com.google.common.collect.Range
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RangesTest : FunSpec({

    context("lowerBoundTypeOrNull") {
        test("returns BoundType for bounded range") {
            Range.closed(3, 10).lowerBoundTypeOrNull() shouldBe BoundType.CLOSED
            Range.open(3, 10).lowerBoundTypeOrNull() shouldBe BoundType.OPEN
        }

        test("returns null for unbounded lower") {
            Range.atMost(10).lowerBoundTypeOrNull() shouldBe null
        }
    }

    context("lowerEndpointOrNull") {
        test("returns endpoint for bounded range") {
            Range.closed(3, 10).lowerEndpointOrNull() shouldBe 3
        }

        test("returns null for unbounded lower") {
            Range.atMost(10).lowerEndpointOrNull() shouldBe null
        }
    }

    context("upperBoundTypeOrNull") {
        test("returns BoundType for bounded range") {
            Range.closed(3, 10).upperBoundTypeOrNull() shouldBe BoundType.CLOSED
            Range.open(3, 10).upperBoundTypeOrNull() shouldBe BoundType.OPEN
        }

        test("returns null for unbounded upper") {
            Range.atLeast(3).upperBoundTypeOrNull() shouldBe null
        }
    }

    context("upperEndpointOrNull") {
        test("returns endpoint for bounded range") {
            Range.closed(3, 10).upperEndpointOrNull() shouldBe 10
        }

        test("returns null for unbounded upper") {
            Range.atLeast(3).upperEndpointOrNull() shouldBe null
        }
    }

    context("ClosedRange<T>.toGuavaRange()") {
        test("produces a closed Guava range") {
            val r: ClosedRange<Int> = 3..10
            r.toGuavaRange() shouldBe Range.closed(3, 10)
        }

        test("single-element range") {
            val r: ClosedRange<Int> = 5..5
            r.toGuavaRange() shouldBe Range.closed(5, 5)
        }
    }

    context("OpenEndRange<T>.toGuavaRange()") {
        test("produces a closedOpen Guava range") {
            val r: OpenEndRange<Int> = 3..<10
            r.toGuavaRange() shouldBe Range.closedOpen(3, 10)
        }
    }

    context("Range<T>.toClosedRange()") {
        test("closed bounded range converts correctly") {
            val r = Range.closed(3, 10).toClosedRange()
            r.start shouldBe 3
            r.endInclusive shouldBe 10
        }

        test("throws for unbounded lower") {
            shouldThrow<IllegalArgumentException> { Range.atMost(10).toClosedRange() }
        }

        test("throws for unbounded upper") {
            shouldThrow<IllegalArgumentException> { Range.atLeast(3).toClosedRange() }
        }

        test("throws for open lower bound") {
            shouldThrow<IllegalArgumentException> { Range.openClosed(3, 10).toClosedRange() }
        }

        test("throws for open upper bound") {
            shouldThrow<IllegalArgumentException> { Range.closedOpen(3, 10).toClosedRange() }
        }
    }

    context("Range<T>.toOpenEndRange()") {
        test("closedOpen bounded range converts correctly") {
            val r = Range.closedOpen(3, 10).toOpenEndRange()
            r.start shouldBe 3
            r.endExclusive shouldBe 10
        }

        test("throws for unbounded lower") {
            shouldThrow<IllegalArgumentException> { Range.lessThan(10).toOpenEndRange() }
        }

        test("throws for unbounded upper") {
            shouldThrow<IllegalArgumentException> { Range.atLeast(3).toOpenEndRange() }
        }

        test("throws for open lower bound") {
            shouldThrow<IllegalArgumentException> { Range.open(3, 10).toOpenEndRange() }
        }

        test("throws for closed upper bound") {
            shouldThrow<IllegalArgumentException> { Range.closed(3, 10).toOpenEndRange() }
        }
    }

    context("closedRangeToGuavaRange<T>()") {
        val conv = closedRangeToGuavaRange<Int>()

        test("forward produces closed Guava range") {
            conv(3..10) shouldBe Range.closed(3, 10)
        }

        test("backward round-trips start and endInclusive") {
            val r = conv.reverse(Range.closed(3, 10))
            r.start shouldBe 3
            r.endInclusive shouldBe 10
        }

        test("backward throws for non-closed range") {
            shouldThrow<IllegalArgumentException> { conv.reverse(Range.closedOpen(3, 10)) }
        }
    }

    context("openEndRangeToGuavaRange<T>()") {
        val conv = openEndRangeToGuavaRange<Int>()

        test("forward produces closedOpen Guava range") {
            conv(3..<10) shouldBe Range.closedOpen(3, 10)
        }

        test("backward round-trips start and endExclusive") {
            val r = conv.reverse(Range.closedOpen(3, 10))
            r.start shouldBe 3
            r.endExclusive shouldBe 10
        }

        test("backward throws for non-closedOpen range") {
            shouldThrow<IllegalArgumentException> { conv.reverse(Range.closed(3, 10)) }
        }
    }
})
