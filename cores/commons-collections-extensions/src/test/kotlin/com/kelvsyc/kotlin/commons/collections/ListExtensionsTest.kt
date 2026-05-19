package com.kelvsyc.kotlin.commons.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ListExtensionsTest : FunSpec({

    context("longestCommonSubsequence (no equator)") {
        test("returns lcs for numeric list") {
            val result = listOf(1, 2, 3, 4).longestCommonSubsequence(listOf(1, 3, 4, 5))
            result shouldBe listOf(1, 3, 4)
        }

        test("returns lcs for string list") {
            val result = listOf("a", "b", "c").longestCommonSubsequence(listOf("a", "c"))
            result shouldBe listOf("a", "c")
        }

        test("returns empty list when no common subsequence") {
            val result = listOf(1, 2).longestCommonSubsequence(listOf(3, 4))
            result shouldBe emptyList()
        }

        test("returns full list when one is subsequence of other") {
            val result = listOf(1, 2, 3).longestCommonSubsequence(listOf(1, 2, 3, 4, 5))
            result shouldBe listOf(1, 2, 3)
        }
    }

    context("longestCommonSubsequence (with equator)") {
        test("case-insensitive string comparison") {
            val result = listOf("A", "B", "C").longestCommonSubsequence(
                listOf("a", "c")
            ) { a, b -> a.equals(b, ignoreCase = true) }
            result shouldBe listOf("A", "C")
        }

        test("custom equator with numeric tolerance") {
            val result = listOf(1, 2, 3, 4).longestCommonSubsequence(
                listOf(1, 3, 5, 6)
            ) { a, b -> kotlin.math.abs(a - b) <= 1 }
            result shouldBe listOf(2, 3, 4)
        }
    }

    context("listUnion") {
        test("combines two lists preserving order") {
            val result = listOf(1, 2, 3).listUnion(listOf(4, 5))
            result shouldBe listOf(1, 2, 3, 4, 5)
        }

        test("includes all elements from both lists (multiset union semantics)") {
            val result = listOf(1, 2, 3).listUnion(listOf(3, 4, 5))
            result shouldBe listOf(1, 2, 3, 3, 4, 5)
        }

        test("works with empty lists") {
            val result1 = listOf(1, 2).listUnion(emptyList())
            result1 shouldBe listOf(1, 2)

            val result2 = emptyList<Int>().listUnion(listOf(1, 2))
            result2 shouldBe listOf(1, 2)
        }
    }

    context("listIntersection") {
        test("returns common elements") {
            val result = listOf(1, 2, 3).listIntersection(listOf(2, 3, 4))
            result shouldBe listOf(2, 3)
        }

        test("preserves order from first list") {
            val result = listOf(3, 2, 1).listIntersection(listOf(1, 2))
            result shouldBe listOf(2, 1)
        }

        test("returns empty when no common elements") {
            val result = listOf(1, 2).listIntersection(listOf(3, 4))
            result shouldBe emptyList()
        }

        test("works with empty lists") {
            val result = listOf(1, 2).listIntersection(emptyList())
            result shouldBe emptyList()
        }
    }

    context("listSubtract") {
        test("removes elements of second list from first") {
            val result = listOf(1, 2, 3, 4).listSubtract(listOf(2, 4))
            result shouldBe listOf(1, 3)
        }

        test("preserves order") {
            val result = listOf(5, 4, 3, 2, 1).listSubtract(listOf(2, 4))
            result shouldBe listOf(5, 3, 1)
        }

        test("returns full list when subtracting empty") {
            val result = listOf(1, 2, 3).listSubtract(emptyList())
            result shouldBe listOf(1, 2, 3)
        }

        test("returns empty when subtracting all elements") {
            val result = listOf(1, 2, 3).listSubtract(listOf(1, 2, 3))
            result shouldBe emptyList()
        }

        test("handles duplicates (removes first occurrence for each element)") {
            val result = listOf(1, 1, 2, 3).listSubtract(listOf(1))
            result shouldBe listOf(1, 2, 3)
        }
    }

})
