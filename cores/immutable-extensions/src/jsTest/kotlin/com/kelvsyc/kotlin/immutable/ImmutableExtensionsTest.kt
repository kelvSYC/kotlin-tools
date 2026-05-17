package com.kelvsyc.kotlin.immutable

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldBeFalse

class ImmutableExtensionsTest : FunSpec({
    // ── asSequence() ─────────────────────────────────────────────────────────

    test("ImmutableList asSequence yields all elements") {
        immutableListOf(1, 2, 3).asSequence().toList() shouldBe listOf(1, 2, 3)
    }

    test("ImmutableSet asSequence yields all elements") {
        immutableSetOf(1, 2, 3).asSequence().toSet() shouldBe setOf(1, 2, 3)
    }

    test("ImmutableMap asSequence yields key-value pairs") {
        val pairs = immutableMapOf("a" to 1, "b" to 2).asSequence().toSet()
        pairs shouldBe setOf("a" to 1, "b" to 2)
    }

    test("asSequence enables stdlib filter") {
        immutableListOf(1, 2, 3, 4, 5).asSequence().filter { it % 2 == 0 }.toList() shouldBe listOf(2, 4)
    }

    test("asSequence enables stdlib map") {
        immutableListOf(1, 2, 3).asSequence().map { it * 10 }.toList() shouldBe listOf(10, 20, 30)
    }

    test("asSequence enables stdlib any") {
        immutableListOf(1, 2, 3).asSequence().any { it > 2 }.shouldBeTrue()
    }

    // ── isNotEmpty() ─────────────────────────────────────────────────────────

    test("list isNotEmpty returns false for empty") {
        immutableListOf<Int>().isNotEmpty().shouldBeFalse()
    }

    test("list isNotEmpty returns true for non-empty") {
        immutableListOf(1).isNotEmpty().shouldBeTrue()
    }

    test("map isNotEmpty returns false for empty") {
        immutableMapOf<String, Int>().isNotEmpty().shouldBeFalse()
    }

    test("map isNotEmpty returns true for non-empty") {
        immutableMapOf("a" to 1).isNotEmpty().shouldBeTrue()
    }

    test("set isNotEmpty returns false for empty") {
        immutableSetOf<Int>().isNotEmpty().shouldBeFalse()
    }

    test("set isNotEmpty returns true for non-empty") {
        immutableSetOf(1).isNotEmpty().shouldBeTrue()
    }

    // ── Safe list access ──────────────────────────────────────────────────────

    test("getOrElse returns element for valid index") {
        immutableListOf(10, 20, 30).getOrElse(1) { -1 } shouldBe 20
    }

    test("getOrElse returns default for out-of-bounds index") {
        immutableListOf(10, 20).getOrElse(5) { -1 } shouldBe -1
    }

    test("firstOrNull returns null for empty list") {
        immutableListOf<Int>().firstOrNull().shouldBeNull()
    }

    test("firstOrNull returns first element") {
        immutableListOf(10, 20, 30).firstOrNull() shouldBe 10
    }

    test("lastOrNull returns null for empty list") {
        immutableListOf<Int>().lastOrNull().shouldBeNull()
    }

    test("lastOrNull returns last element") {
        immutableListOf(10, 20, 30).lastOrNull() shouldBe 30
    }

    // ── forEachIndexed ────────────────────────────────────────────────────────

    test("forEachIndexed provides correct indices and values") {
        val results = mutableListOf<Pair<Int, String>>()
        immutableListOf("a", "b", "c").forEachIndexed { i, v -> results.add(i to v) }
        results shouldBe listOf(0 to "a", 1 to "b", 2 to "c")
    }

    test("forEachIndexed on empty list does nothing") {
        var count = 0
        immutableListOf<String>().forEachIndexed { _, _ -> count++ }
        count shouldBe 0
    }

    // ── Map safe access and typed views ──────────────────────────────────────

    test("getOrDefault returns value for existing key") {
        immutableMapOf("a" to 1).getOrDefault("a", -1) shouldBe 1
    }

    test("getOrDefault returns default for missing key") {
        immutableMapOf("a" to 1).getOrDefault("b", -1) shouldBe -1
    }

    test("getOrElse returns value for existing key") {
        immutableMapOf("a" to 1).getOrElse("a") { -1 } shouldBe 1
    }

    test("getOrElse returns computed default for missing key") {
        immutableMapOf("a" to 1).getOrElse("b") { -1 } shouldBe -1
    }

    test("keyList returns all keys") {
        immutableMapOf("a" to 1, "b" to 2).keyList().toSet() shouldBe setOf("a", "b")
    }

    test("valueList returns all values") {
        immutableMapOf("a" to 1, "b" to 2).valueList().toSet() shouldBe setOf(1, 2)
    }
})
