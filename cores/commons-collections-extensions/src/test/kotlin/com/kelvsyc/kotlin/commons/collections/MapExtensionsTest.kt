package com.kelvsyc.kotlin.commons.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class MapExtensionsTest : FunSpec({

    context("invertedMap") {
        test("swaps keys and values") {
            val original = mapOf("a" to 1, "b" to 2)
            val inverted = original.invertedMap()
            inverted shouldBe mapOf(1 to "a", 2 to "b")
        }

        test("handles duplicate values (one survives)") {
            val original = mapOf("a" to 1, "b" to 1)
            val inverted = original.invertedMap()
            inverted.size shouldBe 1
            inverted.containsValue("a") || inverted.containsValue("b") shouldBe true
        }

        test("works with empty map") {
            val original = emptyMap<String, Int>()
            val inverted = original.invertedMap()
            inverted shouldBe emptyMap()
        }

        test("works with single entry") {
            val original = mapOf("x" to 42)
            val inverted = original.invertedMap()
            inverted shouldBe mapOf(42 to "x")
        }
    }

    context("toMultiValuedMap") {
        test("converts map of collections to MultiValuedMap") {
            val original = mapOf("k" to listOf(1, 2, 3))
            val multiValuedMap = original.toMultiValuedMap()
            multiValuedMap["k"].toList().shouldContainExactlyInAnyOrder(listOf(1, 2, 3))
        }

        test("handles multiple keys") {
            val original = mapOf("a" to listOf(1, 2), "b" to listOf(3, 4, 5))
            val multiValuedMap = original.toMultiValuedMap()
            multiValuedMap["a"].toList().shouldContainExactlyInAnyOrder(listOf(1, 2))
            multiValuedMap["b"].toList().shouldContainExactlyInAnyOrder(listOf(3, 4, 5))
        }

        test("handles empty collections") {
            val original = mapOf("k" to emptyList<Int>())
            val multiValuedMap = original.toMultiValuedMap()
            multiValuedMap["k"].size shouldBe 0
        }

        test("handles empty map") {
            val original = emptyMap<String, Collection<Int>>()
            val multiValuedMap = original.toMultiValuedMap()
            multiValuedMap.size() shouldBe 0
        }
    }

})
