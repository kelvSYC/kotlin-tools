package com.kelvsyc.kotlin.core.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class BuildListMultimapTest : FunSpec({

    context("buildListMultimap") {
        test("empty builder produces an empty multimap") {
            buildListMultimap<String, Int> {}.isEmpty().shouldBeTrue()
        }

        test("put inside builder groups values by key") {
            val m = buildListMultimap<String, Int> {
                put("a", 1)
                put("b", 2)
                put("a", 3)
            }
            m.asMap shouldBe mapOf("a" to listOf(1, 3), "b" to listOf(2))
        }

        test("result is a read-only ListMultimap") {
            val m: ListMultimap<String, Int> = buildListMultimap { put("a", 1) }
            m["a"] shouldBe listOf(1)
        }
    }

    context("buildListMultimap with capacity") {
        test("empty builder with capacity produces an empty multimap") {
            buildListMultimap<String, Int>(4) {}.isEmpty().shouldBeTrue()
        }

        test("elements are correct when capacity is provided") {
            val m = buildListMultimap<String, Int>(4) {
                put("a", 1)
                put("b", 2)
                put("a", 3)
            }
            m.asMap shouldBe mapOf("a" to listOf(1, 3), "b" to listOf(2))
        }
    }
})
