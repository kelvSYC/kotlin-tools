package com.kelvsyc.kotlin.commons.collections.collect

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class MultiValuedMapsTest : FunSpec({

    context("arrayListValuedHashMapOf") {
        test("empty map") {
            val map = arrayListValuedHashMapOf<String, Int>()
            map.size() shouldBe 0
        }
        test("map with pairs stores multiple values per key") {
            val map = arrayListValuedHashMapOf("k" to 1, "k" to 2, "j" to 3)
            map["k"].toList() shouldContainExactlyInAnyOrder listOf(1, 2)
            map["j"].toList() shouldContainExactlyInAnyOrder listOf(3)
        }
    }

    context("hashSetValuedHashMapOf") {
        test("empty map") {
            val map = hashSetValuedHashMapOf<String, Int>()
            map.size() shouldBe 0
        }
        test("set-valued map deduplicates values per key") {
            val map = hashSetValuedHashMapOf("k" to 1, "k" to 1, "k" to 2)
            map["k"].size shouldBe 2
        }
    }

    context("buildListValuedMap") {
        test("builder actions are applied") {
            val map = buildListValuedMap<String, Int> {
                put("a", 1)
                put("a", 2)
                put("b", 3)
            }
            map["a"].toList() shouldContainExactlyInAnyOrder listOf(1, 2)
            map["b"].toList() shouldContainExactlyInAnyOrder listOf(3)
        }
    }

    context("buildSetValuedMap") {
        test("builder deduplicates values") {
            val map = buildSetValuedMap<String, Int> {
                put("a", 1)
                put("a", 1)
                put("a", 2)
            }
            map["a"].size shouldBe 2
        }
    }

    context("plusAssign operator") {
        test("adds key-value pair in-place") {
            val map = buildListValuedMap<String, Int> {}
            map += "x" to 42
            map["x"].toList() shouldContainExactlyInAnyOrder listOf(42)
        }
    }
})
