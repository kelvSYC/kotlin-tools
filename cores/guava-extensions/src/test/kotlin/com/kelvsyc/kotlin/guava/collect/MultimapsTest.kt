package com.kelvsyc.kotlin.guava.collect

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class MultimapsTest : FunSpec({

    context("multimapOf") {
        test("empty") { multimapOf<String, Int>().isEmpty shouldBe true }
        test("single entry") {
            val m = multimapOf("a" to 1)
            m.size() shouldBe 1
            m["a"] shouldContainExactlyInAnyOrder listOf(1)
        }
        test("vararg entries — duplicate keys accumulate") {
            val m = multimapOf("a" to 1, "a" to 2, "b" to 3)
            m.size() shouldBe 3
            m["a"] shouldContainExactlyInAnyOrder listOf(1, 2)
        }
    }

    context("buildMultimap") {
        test("builder accumulates entries") {
            val m = buildMultimap<String, Int> {
                put("x", 10)
                put("x", 20)
                put("y", 30)
            }
            m["x"] shouldContainExactlyInAnyOrder listOf(10, 20)
            m["y"] shouldContainExactlyInAnyOrder listOf(30)
        }
    }

    context("toImmutableMultimap") {
        test("converts mutable multimap to immutable copy") {
            val m = multimapOf("a" to 1, "b" to 2)
            val copy = m.toImmutableMultimap()
            copy.size() shouldBe 2
        }
    }

    context("listMultimapOf") {
        test("empty") { listMultimapOf<String, Int>().isEmpty shouldBe true }
        test("vararg preserves insertion order") {
            val m = listMultimapOf("a" to 1, "a" to 2)
            m["a"] shouldBe listOf(1, 2)
        }
    }

    context("setMultimapOf") {
        test("empty") { setMultimapOf<String, Int>().isEmpty shouldBe true }
        test("duplicate values deduplicated") {
            val m = setMultimapOf("a" to 1, "a" to 1, "a" to 2)
            m["a"].size shouldBe 2
        }
    }
})
