package com.kelvsyc.kotlin.core.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class BuildSetMultimapTest : FunSpec({

    context("buildSetMultimap") {
        test("empty builder produces an empty multimap") {
            buildSetMultimap<String, Int> {}.isEmpty().shouldBeTrue()
        }

        test("put inside builder adds pairs") {
            val m = buildSetMultimap<String, Int> {
                put("a", 1)
                put("b", 2)
                put("a", 3)
            }
            m.asMap shouldBe mapOf("a" to setOf(1, 3), "b" to setOf(2))
        }

        test("put returns true for a new pair") {
            buildSetMultimap<String, Int> {
                put("a", 1).shouldBeTrue()
            }
        }

        test("put returns false for a duplicate pair") {
            buildSetMultimap<String, Int> {
                put("a", 1)
                put("a", 1).shouldBeFalse()
            }
        }

        test("result is a read-only SetMultimap") {
            val m: SetMultimap<String, Int> = buildSetMultimap { put("a", 1) }
            m["a"] shouldBe setOf(1)
        }
    }

    context("buildSetMultimap with capacity") {
        test("empty builder with capacity produces an empty multimap") {
            buildSetMultimap<String, Int>(4) {}.isEmpty().shouldBeTrue()
        }

        test("elements are correct when capacity is provided") {
            val m = buildSetMultimap<String, Int>(4) {
                put("a", 1)
                put("b", 2)
                put("a", 3)
            }
            m.asMap shouldBe mapOf("a" to setOf(1, 3), "b" to setOf(2))
        }
    }
})
