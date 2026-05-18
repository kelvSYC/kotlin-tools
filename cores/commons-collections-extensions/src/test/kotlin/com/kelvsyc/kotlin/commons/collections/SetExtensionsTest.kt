package com.kelvsyc.kotlin.commons.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SetExtensionsTest : FunSpec({

    context("disjunction") {
        test("returns elements in either set but not both") {
            val set1 = setOf(1, 2, 3)
            val set2 = setOf(2, 3, 4)
            val result = set1.disjunction(set2)
            result shouldBe setOf(1, 4)
        }

        test("works with empty set") {
            val set1 = setOf(1, 2)
            val set2 = emptySet<Int>()
            val result = set1.disjunction(set2)
            result shouldBe setOf(1, 2)
        }

        test("returns empty when sets are identical") {
            val set1 = setOf(1, 2)
            val set2 = setOf(1, 2)
            val result = set1.disjunction(set2)
            result shouldBe emptySet()
        }

        test("works with single element sets") {
            val set1 = setOf(1)
            val set2 = setOf(2)
            val result = set1.disjunction(set2)
            result shouldBe setOf(1, 2)
        }

        test("works with string sets") {
            val set1 = setOf("a", "b", "c")
            val set2 = setOf("c", "d", "e")
            val result = set1.disjunction(set2)
            result shouldBe setOf("a", "b", "d", "e")
        }
    }

})
