package com.kelvsyc.kotlin.core.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class BuildListMultimapTest : FunSpec({

    context("buildListMultimap") {
        test("empty builder produces an empty multimap") {
            val m = buildListMultimap<String, Int> {}
            m.isEmpty().shouldBeTrue()
        }

        test("put inside builder adds entries") {
            val m = buildListMultimap<String, Int> {
                put("a", 1)
                put("b", 2)
                put("a", 3)
            }
            m.entries.toList() shouldBe listOf("a" to 1, "b" to 2, "a" to 3)
        }

        test("putAll inside builder adds entries from another multimap") {
            val source = listMultimapOf("a" to 1, "b" to 2)
            val m = buildListMultimap<String, Int> {
                putAll(source)
                put("a", 3)
            }
            m["a"] shouldBe listOf(1, 3)
            m["b"] shouldBe listOf(2)
        }

        test("result is a read-only ListMultimap") {
            val m: ListMultimap<String, Int> = buildListMultimap { put("a", 1) }
            m["a"] shouldBe listOf(1)
        }
    }

    context("buildListMultimap with capacity") {
        test("empty builder with capacity produces an empty multimap") {
            val m = buildListMultimap<String, Int>(4) {}
            m.isEmpty().shouldBeTrue()
        }

        test("entries are correct when capacity is provided") {
            val m = buildListMultimap<String, Int>(4) {
                put("a", 1)
                put("b", 2)
                put("a", 3)
            }
            m.entries.toList() shouldBe listOf("a" to 1, "b" to 2, "a" to 3)
        }
    }
})
