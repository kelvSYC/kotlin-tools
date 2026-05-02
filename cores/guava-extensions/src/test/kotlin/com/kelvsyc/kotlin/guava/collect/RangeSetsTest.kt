package com.kelvsyc.kotlin.guava.collect

import com.google.common.collect.Range
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RangeSetsTest : FunSpec({

    context("rangeSetOf") {
        test("empty") { rangeSetOf<Int>().isEmpty shouldBe true }
        test("single range") {
            val rs = rangeSetOf(Range.closed(1, 5))
            (3 in rs) shouldBe true
            (6 in rs) shouldBe false
        }
        test("multiple ranges — unioned") {
            val rs = rangeSetOf(Range.closed(1, 3), Range.closed(5, 7))
            (2 in rs) shouldBe true
            (6 in rs) shouldBe true
            (4 in rs) shouldBe false
        }
    }

    context("buildRangeSet") {
        test("builder adds ranges") {
            val rs = buildRangeSet<Int> { add(Range.closed(1, 10)) }
            (5 in rs) shouldBe true
            (11 in rs) shouldBe false
        }
    }

    context("toImmutableRangeSet") {
        test("from iterable of ranges") {
            val rs = listOf(Range.closed(1, 3), Range.closed(7, 9)).toImmutableRangeSet()
            (2 in rs) shouldBe true
            (8 in rs) shouldBe true
            (5 in rs) shouldBe false
        }
    }

    context("treeRangeSetOf") {
        test("empty is mutable") {
            val rs = treeRangeSetOf<Int>()
            rs.add(Range.closed(1, 5))
            rs.remove(Range.closed(3, 5))
            (2 in rs) shouldBe true
            (4 in rs) shouldBe false
        }
        test("with ranges") {
            val rs = treeRangeSetOf(Range.closed(1, 3), Range.closed(5, 7))
            (2 in rs) shouldBe true
            (4 in rs) shouldBe false
        }
    }
})
