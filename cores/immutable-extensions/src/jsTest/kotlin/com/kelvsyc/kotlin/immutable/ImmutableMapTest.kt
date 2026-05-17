package com.kelvsyc.kotlin.immutable

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.nulls.shouldBeNull

class ImmutableMapTest : FunSpec({
    test("empty map has size 0") {
        immutableMapOf<String, Int>().size shouldBe 0
    }

    test("empty map isEmpty") {
        immutableMapOf<String, Int>().isEmpty() shouldBe true
    }

    test("immutableMapOf creates map with pairs") {
        val map = immutableMapOf("a" to 1, "b" to 2)
        map.size shouldBe 2
        map.get("a") shouldBe 1
        map.get("b") shouldBe 2
    }

    test("get returns null for missing key") {
        immutableMapOf("a" to 1).get("z").shouldBeNull()
    }

    test("has returns true for existing key") {
        immutableMapOf("a" to 1).has("a").shouldBeTrue()
    }

    test("has returns false for missing key") {
        immutableMapOf("a" to 1).has("z").shouldBeFalse()
    }

    test("toKotlinMap round-trips") {
        val original = mapOf("x" to 10, "y" to 20)
        original.toImmutableMap().toKotlinMap() shouldBe original
    }

    test("plus inserts a pair") {
        val result = immutableMapOf("a" to 1) + ("b" to 2)
        result.get("b") shouldBe 2
        result.size shouldBe 2
    }

    test("plus merges two maps") {
        val result = immutableMapOf("a" to 1) + immutableMapOf("b" to 2)
        result.size shouldBe 2
    }

    test("minus removes a key") {
        val result = immutableMapOf("a" to 1, "b" to 2) - "a"
        result.has("a").shouldBeFalse()
        result.size shouldBe 1
    }

    test("get operator returns value") {
        immutableMapOf("k" to 42)["k"] shouldBe 42
    }

    test("get operator returns null for missing key") {
        immutableMapOf("k" to 42)["z"].shouldBeNull()
    }

    test("contains operator returns true for existing key") {
        ("k" in immutableMapOf("k" to 42)).shouldBeTrue()
    }

    test("contains operator returns false for missing key") {
        ("z" in immutableMapOf("k" to 42)).shouldBeFalse()
    }

    test("sortedBy sorts entries by value") {
        val map = immutableMapOf("b" to 3, "a" to 1, "c" to 2)
        map.sortedBy { it }.valueList() shouldBe listOf(1, 2, 3)
    }

    test("kotlinMapToImmutableMap converter round-trips") {
        val converter = kotlinMapToImmutableMap<String, Int>()
        val original = mapOf("a" to 1, "b" to 2)
        converter(original).toKotlinMap() shouldBe original
        converter.reverse(converter(original)) shouldBe original
    }
})
