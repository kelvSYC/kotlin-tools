package com.kelvsyc.kotlin.immutable

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldBeNull

class ImmutableListTest : FunSpec({
    test("empty list has size 0") {
        immutableListOf<Int>().size shouldBe 0
    }

    test("empty list isEmpty") {
        immutableListOf<Int>().isEmpty() shouldBe true
    }

    test("immutableListOf creates list with elements") {
        val list = immutableListOf(1, 2, 3)
        list.size shouldBe 3
        list.get(0) shouldBe 1
        list.get(1) shouldBe 2
        list.get(2) shouldBe 3
    }

    test("get returns null for out-of-bounds index") {
        immutableListOf(1, 2).get(5).shouldBeNull()
    }

    test("toKotlinList round-trips") {
        val original = listOf(10, 20, 30)
        original.toImmutableList().toKotlinList() shouldBe original
    }

    test("toImmutableList from Iterable") {
        val set = setOf(1, 2, 3)
        set.toImmutableList().size shouldBe 3
    }

    test("plus appends element") {
        (immutableListOf(1, 2) + 3).toKotlinList() shouldBe listOf(1, 2, 3)
    }

    test("plus concatenates lists") {
        (immutableListOf(1, 2) + immutableListOf(3, 4)).toKotlinList() shouldBe listOf(1, 2, 3, 4)
    }

    test("minus removes element at index") {
        (immutableListOf("a", "b", "c") - 1).toKotlinList() shouldBe listOf("a", "c")
    }

    test("get operator returns element") {
        immutableListOf("x", "y", "z")[1] shouldBe "y"
    }

    test("get operator returns null for out-of-bounds") {
        immutableListOf("x")[5].shouldBeNull()
    }

    test("sortedBy sorts ascending by key") {
        immutableListOf(3, 1, 2).sortedBy { it }.toKotlinList() shouldBe listOf(1, 2, 3)
    }

    test("sortedBy with comparator sorts descending") {
        immutableListOf(1, 2, 3)
            .sortedBy({ it }, Comparator { a, b -> b - a })
            .toKotlinList() shouldBe listOf(3, 2, 1)
    }

    test("sortedBy sorts strings by length") {
        immutableListOf("banana", "fig", "apple")
            .sortedBy { it.length }
            .toKotlinList() shouldBe listOf("fig", "apple", "banana")
    }

    test("kotlinListToImmutableList converter round-trips") {
        val converter = kotlinListToImmutableList<String>()
        val original = listOf("a", "b", "c")
        converter(original).toKotlinList() shouldBe original
        converter.reverse(converter(original)) shouldBe original
    }
})
