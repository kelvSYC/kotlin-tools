package com.kelvsyc.kotlin.commons.collections.collect

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.collections4.BidiMap

class BidiMapsTest : FunSpec({

    context("dualHashBidiMapOf") {
        test("empty map") {
            val map = dualHashBidiMapOf<String, Int>()
            map.size shouldBe 0
        }
        test("map with pairs has correct entries") {
            val map = dualHashBidiMapOf("a" to 1, "b" to 2)
            map["a"] shouldBe 1
            map["b"] shouldBe 2
        }
    }

    context("dualTreeBidiMapOf") {
        test("empty sorted map") {
            val map = dualTreeBidiMapOf<String, Int>()
            map.size shouldBe 0
        }
        test("map with pairs has correct entries") {
            val map = dualTreeBidiMapOf("a" to 1, "b" to 2)
            map["a"] shouldBe 1
        }
    }

    context("buildBidiMap") {
        test("builder actions are applied") {
            val map = buildBidiMap<String, Int> {
                put("x", 10)
                put("y", 20)
            }
            map["x"] shouldBe 10
            map["y"] shouldBe 20
        }
    }

    context("inverse property") {
        test("inverse maps values to keys") {
            val map = dualHashBidiMapOf("a" to 1, "b" to 2)
            map.inverse[1] shouldBe "a"
            map.inverse[2] shouldBe "b"
        }
        test("inverse of inverse is original") {
            val map = dualHashBidiMapOf("a" to 1)
            map.inverse.inverse["a"] shouldBe 1
        }
    }
})
