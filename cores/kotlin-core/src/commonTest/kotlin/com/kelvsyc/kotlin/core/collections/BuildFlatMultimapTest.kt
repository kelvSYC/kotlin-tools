package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class BuildFlatMultimapTest : FunSpec({

    context("buildFlatMultimap") {
        test("empty builder produces an empty multimap") {
            buildFlatMultimap<String, Int> {}.isEmpty().shouldBeTrue()
        }

        test("put inside builder appends elements in order") {
            val m = buildFlatMultimap<String, Int> {
                put("a", 1)
                put("b", 2)
                put("a", 3)
            }
            m.entries.toList() shouldBe listOf("a" to 1, "b" to 2, "a" to 3)
        }

        test("result is a read-only FlatMultimap") {
            val m: FlatMultimap<String, Int> = buildFlatMultimap { put("a", 1) }
            m["a"] shouldBe listOf(1)
        }
    }

    context("buildFlatMultimap with capacity") {
        test("empty builder with capacity produces an empty multimap") {
            buildFlatMultimap<String, Int>(4) {}.isEmpty().shouldBeTrue()
        }

        test("elements are correct when capacity is provided") {
            val m = buildFlatMultimap<String, Int>(4) {
                put("a", 1)
                put("b", 2)
                put("a", 3)
            }
            m.entries.toList() shouldBe listOf("a" to 1, "b" to 2, "a" to 3)
        }
    }

    context("immutability") {
        test("buildFlatMultimap result cannot be cast to MutableFlatMultimap") {
            shouldThrow<ClassCastException> { buildFlatMultimap<String, Int> { put("a", 1) } as MutableFlatMultimap<String, Int> }
            shouldThrow<ClassCastException> { buildFlatMultimap<String, Int>(4) { put("a", 1) } as MutableFlatMultimap<String, Int> }
        }
    }
})
