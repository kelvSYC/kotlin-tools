package com.kelvsyc.kotlin.commons.collections.collect

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class OrderedMapsTest : FunSpec({

    context("linkedOrderedMapOf") {
        test("empty linked map") {
            val map = linkedOrderedMapOf<String, Int>()
            map.size shouldBe 0
        }
        test("map with pairs preserves insertion order") {
            val map = linkedOrderedMapOf("c" to 3, "a" to 1, "b" to 2)
            map.firstKey() shouldBe "c"
            map.lastKey() shouldBe "b"
        }
    }

    context("buildLinkedMap") {
        test("builder actions are applied") {
            val map = buildLinkedMap<String, Int> {
                put("x", 10)
                put("y", 20)
            }
            map.firstKey() shouldBe "x"
            map["y"] shouldBe 20
        }
    }
})
