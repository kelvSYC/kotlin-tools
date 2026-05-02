package com.kelvsyc.kotlin.core.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class BuildListMultisetTest : FunSpec({

    context("buildListMultiset") {
        test("empty builder produces an empty multiset") {
            buildListMultiset<String> {}.isEmpty().shouldBeTrue()
        }

        test("add inside builder appends elements in order") {
            val m = buildListMultiset<String> {
                add("a")
                add("b")
                add("a")
            }
            m.toList() shouldBe listOf("a", "b", "a")
        }

        test("add with count inside builder appends multiple occurrences at the end") {
            val m = buildListMultiset<String> {
                add("a", 2)
                add("b")
            }
            m.toList() shouldBe listOf("a", "a", "b")
        }

        test("result is a read-only ListMultiset") {
            val m: ListMultiset<String> = buildListMultiset { add("a") }
            m.count("a") shouldBe 1
        }
    }

    context("buildListMultiset with capacity") {
        test("empty builder with capacity produces an empty multiset") {
            buildListMultiset<String>(4) {}.isEmpty().shouldBeTrue()
        }

        test("elements are correct when capacity is provided") {
            val m = buildListMultiset<String>(4) {
                add("a")
                add("b")
                add("a")
            }
            m.toList() shouldBe listOf("a", "b", "a")
        }
    }
})
