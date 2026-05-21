package com.kelvsyc.kotlin.core.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.enums.enumEntries

enum class SortedEnumBiMapTestDirection { NORTH, SOUTH, EAST, WEST }

class SortedEnumBiMapTest : FunSpec({

    val alpha = compareBy<String> { it }

    context("construction") {
        test("sortedEnumBiMapOf with no pairs produces an empty map") {
            sortedEnumBiMapOf<String, SortedEnumBiMapTestDirection>(alpha).isEmpty().shouldBeTrue()
        }

        test("sortedEnumBiMapOf exposes comparator and valueEnumEntries") {
            val m = sortedEnumBiMapOf<String, SortedEnumBiMapTestDirection>(alpha)
            m.comparator shouldBe alpha
            m.valueEnumEntries shouldBe enumEntries<SortedEnumBiMapTestDirection>()
        }

        test("sortedEnumBiMapOf with pairs") {
            val m = sortedEnumBiMapOf(alpha, "b" to SortedEnumBiMapTestDirection.SOUTH, "a" to SortedEnumBiMapTestDirection.NORTH)
            m.size shouldBe 2
            m["a"] shouldBe SortedEnumBiMapTestDirection.NORTH
        }
    }

    context("sorted key order") {
        test("keys iterate in comparator order") {
            val m = sortedEnumBiMapOf(alpha, "c" to SortedEnumBiMapTestDirection.EAST, "a" to SortedEnumBiMapTestDirection.NORTH, "b" to SortedEnumBiMapTestDirection.SOUTH)
            m.keys.toList() shouldBe listOf("a", "b", "c")
        }
    }

    context("navigable key methods") {
        val m = sortedEnumBiMapOf(
            alpha,
            "apple" to SortedEnumBiMapTestDirection.NORTH,
            "banana" to SortedEnumBiMapTestDirection.SOUTH,
            "cherry" to SortedEnumBiMapTestDirection.EAST,
            "date" to SortedEnumBiMapTestDirection.WEST,
        )

        test("firstKey and lastKey") {
            m.firstKey() shouldBe "apple"
            m.lastKey() shouldBe "date"
        }

        test("floorKey") {
            m.floorKey("banana") shouldBe "banana"
            m.floorKey("blueberry") shouldBe "banana"
        }

        test("ceilingKey") {
            m.ceilingKey("banana") shouldBe "banana"
            m.ceilingKey("apricot") shouldBe "banana"
        }

        test("lowerKey") {
            m.lowerKey("banana") shouldBe "apple"
            m.lowerKey("apple").shouldBeNull()
        }

        test("higherKey") {
            m.higherKey("cherry") shouldBe "date"
            m.higherKey("date").shouldBeNull()
        }

        test("keys is a SortedSet") {
            m.keys.shouldBeInstanceOf<SortedSet<String>>()
        }
    }

    context("range views") {
        val m = sortedEnumBiMapOf(
            alpha,
            "apple" to SortedEnumBiMapTestDirection.NORTH,
            "banana" to SortedEnumBiMapTestDirection.SOUTH,
            "cherry" to SortedEnumBiMapTestDirection.EAST,
        )

        test("headMap returns SortedBiMap with correct entries") {
            val h = m.headMap("banana", true)
            h.keys.toList() shouldBe listOf("apple", "banana")
            h.shouldBeInstanceOf<SortedBiMap<String, SortedEnumBiMapTestDirection>>()
        }

        test("tailMap returns SortedBiMap with correct entries") {
            val t = m.tailMap("banana", false)
            t.keys.toList() shouldBe listOf("cherry")
        }

        test("range view inverse is a BiMap") {
            m.headMap("banana", true).inverse[SortedEnumBiMapTestDirection.NORTH] shouldBe "apple"
        }
    }

    context("inverse") {
        test("inverse is EnumSortedBiMap with enum keys") {
            val m = sortedEnumBiMapOf(alpha, "a" to SortedEnumBiMapTestDirection.NORTH, "b" to SortedEnumBiMapTestDirection.SOUTH)
            m.inverse[SortedEnumBiMapTestDirection.NORTH] shouldBe "a"
            m.inverse.enumEntries shouldBe enumEntries<SortedEnumBiMapTestDirection>()
        }
    }
})
