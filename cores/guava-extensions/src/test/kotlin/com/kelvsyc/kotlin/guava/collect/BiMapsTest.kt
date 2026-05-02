package com.kelvsyc.kotlin.guava.collect

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class BiMapsTest : FunSpec({

    context("biMapOf") {
        test("empty") { biMapOf<String, Int>().isEmpty() shouldBe true }
        test("single entry") {
            val m = biMapOf("a" to 1)
            m["a"] shouldBe 1
            m.inverse()[1] shouldBe "a"
        }
        test("five entries via fixed-arity overload") {
            val m = biMapOf("a" to 1, "b" to 2, "c" to 3, "d" to 4, "e" to 5)
            m.size shouldBe 5
        }
        test("vararg with more than 5 entries") {
            val pairs = (1..10).map { it.toString() to it }
            val m = biMapOf(*pairs.toTypedArray())
            m.size shouldBe 10
        }
        test("duplicate value throws") {
            shouldThrow<IllegalArgumentException> { biMapOf("a" to 1, "b" to 1) }
        }
    }

    context("buildBiMap") {
        test("builder populates entries") {
            val m = buildBiMap<String, Int> { put("x", 42) }
            m["x"] shouldBe 42
        }
    }

    context("toImmutableBiMap") {
        test("converts map to BiMap") {
            val m = mapOf("a" to 1, "b" to 2).toImmutableBiMap()
            m["a"] shouldBe 1
            m.inverse()[2] shouldBe "b"
        }
        test("duplicate value throws") {
            shouldThrow<IllegalArgumentException> { mapOf("a" to 1, "b" to 1).toImmutableBiMap() }
        }
    }

    context("hashBiMapOf") {
        test("empty is mutable") {
            val m = hashBiMapOf<String, Int>()
            m["a"] = 1
            m["a"] shouldBe 1
        }
        test("with entries") {
            val m = hashBiMapOf("x" to 10, "y" to 20)
            m["x"] shouldBe 10
        }
    }

    context("enumBiMapOf") {
        test("empty enum-keyed bimap") {
            val m = enumBiMapOf<java.util.concurrent.TimeUnit, java.util.concurrent.TimeUnit>()
            m.isEmpty() shouldBe true
        }
    }

    context("enumHashBiMapOf") {
        test("empty") {
            val m = enumHashBiMapOf<java.util.concurrent.TimeUnit, String>()
            m.isEmpty() shouldBe true
        }
        test("with entries") {
            val m = enumHashBiMapOf<java.util.concurrent.TimeUnit, String>(
                java.util.concurrent.TimeUnit.SECONDS to "seconds"
            )
            m[java.util.concurrent.TimeUnit.SECONDS] shouldBe "seconds"
            m shouldNotBe null
        }
    }
})
