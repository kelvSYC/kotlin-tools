package com.kelvsyc.kotlin.core.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class BuildSetMultisetTest : FunSpec({

    context("buildSetMultiset") {
        test("empty builder produces an empty multiset") {
            buildSetMultiset<String> {}.isEmpty().shouldBeTrue()
        }

        test("add inside builder appends elements") {
            val m = buildSetMultiset<String> {
                add("a")
                add("b")
                add("a")
            }
            m.asMap shouldBe mapOf("a" to 2, "b" to 1)
        }

        test("add with count inside builder adds multiple occurrences") {
            val m = buildSetMultiset<String> {
                add("a", 3)
                add("b")
            }
            m.asMap shouldBe mapOf("a" to 3, "b" to 1)
        }

        test("result is a read-only SetMultiset") {
            val m: SetMultiset<String> = buildSetMultiset { add("a") }
            m.count("a") shouldBe 1
        }
    }

    context("buildSetMultiset with capacity") {
        test("empty builder with capacity produces an empty multiset") {
            buildSetMultiset<String>(4) {}.isEmpty().shouldBeTrue()
        }

        test("elements are correct when capacity is provided") {
            val m = buildSetMultiset<String>(4) {
                add("a")
                add("b")
                add("a")
            }
            m.asMap shouldBe mapOf("a" to 2, "b" to 1)
        }
    }
})
