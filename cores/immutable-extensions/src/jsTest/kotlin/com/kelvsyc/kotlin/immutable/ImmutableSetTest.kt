package com.kelvsyc.kotlin.immutable

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldBeFalse

class ImmutableSetTest : FunSpec({
    test("empty set has size 0") {
        immutableSetOf<Int>().size shouldBe 0
    }

    test("empty set isEmpty") {
        immutableSetOf<Int>().isEmpty() shouldBe true
    }

    test("immutableSetOf creates set with elements") {
        val set = immutableSetOf(1, 2, 3)
        set.size shouldBe 3
    }

    test("has returns true for member") {
        immutableSetOf(1, 2, 3).has(2).shouldBeTrue()
    }

    test("has returns false for non-member") {
        immutableSetOf(1, 2, 3).has(9).shouldBeFalse()
    }

    test("toKotlinSet round-trips") {
        val original = setOf(1, 2, 3)
        original.toImmutableSet().toKotlinSet() shouldBe original
    }

    test("plus adds an element") {
        val result = immutableSetOf(1, 2) + 3
        result.has(3).shouldBeTrue()
        result.size shouldBe 3
    }

    test("plus unions two sets") {
        val result = immutableSetOf(1, 2) + immutableSetOf(3, 4)
        result.toKotlinSet() shouldBe setOf(1, 2, 3, 4)
    }

    test("minus removes an element") {
        val result = immutableSetOf(1, 2, 3) - 2
        result.has(2).shouldBeFalse()
        result.size shouldBe 2
    }

    test("minus subtracts a set") {
        val result = immutableSetOf(1, 2, 3) - immutableSetOf(2, 3)
        result.toKotlinSet() shouldBe setOf(1)
    }

    test("contains operator returns true for member") {
        (2 in immutableSetOf(1, 2, 3)).shouldBeTrue()
    }

    test("contains operator returns false for non-member") {
        (9 in immutableSetOf(1, 2, 3)).shouldBeFalse()
    }

    test("sortedBy sorts by key") {
        immutableSetOf(3, 1, 2).sortedBy { it }.toKotlinSet() shouldBe setOf(1, 2, 3)
    }

    test("kotlinSetToImmutableSet converter round-trips") {
        val converter = kotlinSetToImmutableSet<Int>()
        val original = setOf(1, 2, 3)
        converter(original).toKotlinSet() shouldBe original
        converter.reverse(converter(original)) shouldBe original
    }
})
