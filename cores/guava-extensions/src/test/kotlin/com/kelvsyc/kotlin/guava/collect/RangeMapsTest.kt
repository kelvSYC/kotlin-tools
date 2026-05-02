package com.kelvsyc.kotlin.guava.collect

import com.google.common.collect.Range
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldBeNull

class RangeMapsTest : FunSpec({

    context("rangeMapOf") {
        test("empty") { rangeMapOf<Int, String>().asMapOfRanges().isEmpty() shouldBe true }
        test("single entry") {
            val rm = rangeMapOf(Range.closed(1, 5) to "low")
            rm[3] shouldBe "low"
            rm[6].shouldBeNull()
        }
        test("multiple entries") {
            val rm = rangeMapOf(Range.closed(1, 5) to "low", Range.closed(6, 10) to "high")
            rm[3] shouldBe "low"
            rm[8] shouldBe "high"
        }
    }

    context("buildRangeMap") {
        test("builder populates entries") {
            val rm = buildRangeMap<Int, String> {
                put(Range.closed(1, 5), "low")
                put(Range.closed(6, 10), "high")
            }
            rm[2] shouldBe "low"
            rm[9] shouldBe "high"
        }
    }

    context("contains operator") {
        val rm = rangeMapOf(Range.closed(1, 5) to "a", Range.closed(10, 20) to "b")

        test("key within range returns true") { (3 in rm) shouldBe true }
        test("key in gap returns false") { (7 in rm) shouldBe false }
        test("key at boundary returns true") { (5 in rm) shouldBe true }
    }

    context("treeRangeMapOf") {
        test("empty is mutable") {
            val rm = treeRangeMapOf<Int, String>()
            rm.put(Range.closed(1, 5), "x")
            rm[3] shouldBe "x"
        }
        test("with entries") {
            val rm = treeRangeMapOf(Range.closed(1, 5) to "low", Range.closed(6, 10) to "high")
            rm[4] shouldBe "low"
            rm[7] shouldBe "high"
        }
    }
})
