package com.kelvsyc.kotlin.core.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class MutableSortedMapTest : FunSpec({

    context("construction") {
        test("mutableSortedMapOf is mutable and empty") {
            val m = mutableSortedMapOf<Int, String>(naturalOrder())
            m.isEmpty().shouldBeTrue()
            m.shouldBeInstanceOf<MutableSortedMap<Int, String>>()
        }

        test("mutableSortedMapOf with Comparable key uses natural order") {
            val m = mutableSortedMapOf(3 to "c", 1 to "a", 2 to "b")
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }
    }

    context("mutation") {
        test("put inserts and returns null for new key") {
            val m = mutableSortedMapOf<Int, String>(naturalOrder())
            m.put(1, "a").shouldBeNull()
            m[1] shouldBe "a"
        }

        test("put replaces value for existing key and returns old value") {
            val m = mutableSortedMapOf(naturalOrder<Int>(), 1 to "a")
            m.put(1, "z") shouldBe "a"
            m[1] shouldBe "z"
        }

        test("keys stay sorted after multiple puts") {
            val m = mutableSortedMapOf<Int, String>(naturalOrder())
            m.put(5, "e"); m.put(2, "b"); m.put(8, "h"); m.put(1, "a")
            m.keys.toList() shouldContainExactly listOf(1, 2, 5, 8)
        }

        test("remove deletes key and returns value") {
            val m = mutableSortedMapOf(naturalOrder<Int>(), 1 to "a", 2 to "b", 3 to "c")
            m.remove(2) shouldBe "b"
            m.containsKey(2).shouldBeFalse()
            m.keys.toList() shouldContainExactly listOf(1, 3)
        }

        test("remove returns null for absent key") {
            val m = mutableSortedMapOf(naturalOrder<Int>(), 1 to "a")
            m.remove(99).shouldBeNull()
        }

        test("clear empties the map") {
            val m = mutableSortedMapOf(naturalOrder<Int>(), 1 to "a", 2 to "b")
            m.clear()
            m.isEmpty().shouldBeTrue()
        }

        test("putAll merges entries in sorted order") {
            val m = mutableSortedMapOf(naturalOrder<Int>(), 3 to "c")
            m.putAll(mapOf(1 to "a", 2 to "b"))
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }
    }

    context("keys type") {
        test("keys is a MutableSortedSet") {
            val m = mutableSortedMapOf(naturalOrder<Int>(), 1 to "a", 2 to "b")
            m.keys.shouldBeInstanceOf<MutableSortedSet<Int>>()
        }

        test("range views on MutableSortedMap return MutableSortedMap") {
            val m = mutableSortedMapOf(naturalOrder<Int>(), 1 to "a", 2 to "b", 3 to "c", 4 to "d")
            m.headMap(3, inclusive = false).shouldBeInstanceOf<MutableSortedMap<Int, String>>()
            m.tailMap(2, inclusive = true).shouldBeInstanceOf<MutableSortedMap<Int, String>>()
            m.descendingMap().shouldBeInstanceOf<MutableSortedMap<Int, String>>()
            m.descendingKeySet().shouldBeInstanceOf<MutableSortedSet<Int>>()
        }

        test("mutations to mutable range snapshot do not affect original") {
            val m = mutableSortedMapOf(naturalOrder<Int>(), 1 to "a", 2 to "b", 3 to "c")
            val snap = m.headMap(3, inclusive = false)
            snap.put(0, "zero")
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }
    }

    context("stress") {
        test("1000 random put/remove operations preserve sorted key order") {
            val rng = kotlin.random.Random(seed = 77L)
            val m = mutableSortedMapOf<Int, Int>(naturalOrder())
            val mirror = mutableSortedMapOf<Int, Int>(naturalOrder())
            repeat(1000) {
                val k = rng.nextInt(200)
                if (rng.nextBoolean()) { m.put(k, k); mirror.put(k, k) }
                else { m.remove(k); mirror.remove(k) }
            }
            m.keys.toList() shouldContainExactly mirror.keys.toList()
        }
    }
})
