package com.kelvsyc.kotlin.commons.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class CollectionExtensionsTest : FunSpec({

    context("multisetUnion") {
        test("max frequency per element") {
            val result = listOf("a", "a", "b").multisetUnion(listOf("a", "b", "b"))
            result.count { it == "a" } shouldBe 2
            result.count { it == "b" } shouldBe 2
            result.size shouldBe 4
        }
    }

    context("multisetIntersection") {
        test("min frequency per element") {
            val result = listOf("a", "a", "b").multisetIntersection(listOf("a", "b", "b"))
            result.count { it == "a" } shouldBe 1
            result.count { it == "b" } shouldBe 1
            result.size shouldBe 2
        }
    }

    context("multisetSubtract") {
        test("frequency difference (non-negative)") {
            val result = listOf("a", "a", "b").multisetSubtract(listOf("a", "b"))
            result.count { it == "a" } shouldBe 1
            result.count { it == "b" } shouldBe 0
            result.size shouldBe 1
        }
    }

    context("multisetDisjunction") {
        test("symmetric difference by frequency") {
            val result = listOf("a", "a", "b").multisetDisjunction(listOf("a", "b", "b"))
            result.count { it == "a" } shouldBe 1
            result.count { it == "b" } shouldBe 1
            result.size shouldBe 2
        }
    }

    context("isSubCollectionOf") {
        test("returns true when elements with frequencies are subset") {
            listOf("a", "b").isSubCollectionOf(listOf("a", "a", "b")) shouldBe true
        }
        test("returns false when frequencies exceed superset") {
            listOf("a", "a").isSubCollectionOf(listOf("a", "b")) shouldBe false
        }
    }

    context("isEqualCollection") {
        test("returns true when element frequencies match (order insensitive)") {
            listOf("a", "a", "b").isEqualCollection(listOf("b", "a", "a")) shouldBe true
        }
        test("returns false when frequencies differ") {
            listOf("a", "a", "b").isEqualCollection(listOf("a", "b")) shouldBe false
        }
    }

    context("cardinalityMap") {
        test("returns map of element to count") {
            val result = listOf("a", "a", "b").cardinalityMap()
            result shouldBe mapOf("a" to 2, "b" to 1)
        }
    }

    context("permutations") {
        test("returns all permutations") {
            val result = listOf(1, 2, 3).permutations()
            result.size shouldBe 6
            result.map { it.toSet() }.toSet().size shouldBe 1  // all have same elements
            result.toSet().size shouldBe 6  // all are distinct
        }
    }

})
