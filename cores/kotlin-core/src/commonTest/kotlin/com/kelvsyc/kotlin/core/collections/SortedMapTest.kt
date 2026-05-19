package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class SortedMapTest : FunSpec({

    context("construction") {
        test("emptySortedMap is empty") {
            val m = emptySortedMap<Int, String>(naturalOrder())
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }

        test("sortedMapOf with pairs maintains sorted key order") {
            val m = sortedMapOf(naturalOrder<Int>(), 3 to "c", 1 to "a", 2 to "b")
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
            m.values.toList() shouldContainExactly listOf("a", "b", "c")
        }

        test("sortedMapOf with Comparable key type uses natural order") {
            val m = sortedMapOf(3 to "c", 1 to "a", 2 to "b")
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("sortedMapOf with reverse comparator") {
            val m = sortedMapOf(reverseOrder<Int>(), 1 to "a", 2 to "b", 3 to "c")
            m.keys.toList() shouldContainExactly listOf(3, 2, 1)
        }

        test("Iterable.toSortedMap") {
            val m = listOf(3 to "c", 1 to "a", 2 to "b").toSortedMap(naturalOrder())
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("buildSortedMap") {
            val m = buildSortedMap(naturalOrder<Int>()) {
                put(3, "c"); put(1, "a"); put(2, "b")
            }
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }
    }

    context("keys type") {
        test("keys is a SortedSet") {
            val m = sortedMapOf(naturalOrder<Int>(), 1 to "a", 2 to "b", 3 to "c")
            m.keys.shouldBeInstanceOf<SortedSet<Int>>()
        }

        test("keys are in comparator order") {
            val m = sortedMapOf(naturalOrder<Int>(), 3 to "c", 1 to "a", 2 to "b")
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }
    }

    context("access") {
        val m = sortedMapOf(naturalOrder<Int>(), 1 to "one", 3 to "three", 5 to "five")

        test("get returns value for present key") {
            m[3] shouldBe "three"
        }

        test("get returns null for absent key") {
            m[2].shouldBeNull()
        }

        test("containsKey") {
            m.containsKey(1).shouldBeTrue()
            m.containsKey(2).shouldBeFalse()
        }

        test("firstKey and lastKey") {
            m.firstKey() shouldBe 1
            m.lastKey() shouldBe 5
        }

        test("firstKeyOrNull and lastKeyOrNull") {
            m.firstKeyOrNull() shouldBe 1
            m.lastKeyOrNull() shouldBe 5
        }

        test("firstKey throws on empty map") {
            shouldThrow<NoSuchElementException> { emptySortedMap<Int, String>(naturalOrder()).firstKey() }
        }

        test("lastKey throws on empty map") {
            shouldThrow<NoSuchElementException> { emptySortedMap<Int, String>(naturalOrder()).lastKey() }
        }

        test("firstKeyOrNull is null on empty map") {
            emptySortedMap<Int, String>(naturalOrder()).firstKeyOrNull().shouldBeNull()
        }

        test("lastKeyOrNull is null on empty map") {
            emptySortedMap<Int, String>(naturalOrder()).lastKeyOrNull().shouldBeNull()
        }
    }

    context("floorKey, ceilingKey, lowerKey, higherKey") {
        val m = sortedMapOf(naturalOrder<Int>(), 1 to "a", 3 to "c", 5 to "e", 7 to "g", 9 to "i")

        test("floorKey returns greatest key <= given") {
            m.floorKey(5) shouldBe 5
            m.floorKey(4) shouldBe 3
            m.floorKey(0).shouldBeNull()
        }

        test("ceilingKey returns least key >= given") {
            m.ceilingKey(5) shouldBe 5
            m.ceilingKey(4) shouldBe 5
            m.ceilingKey(10).shouldBeNull()
        }

        test("lowerKey returns greatest key < given") {
            m.lowerKey(5) shouldBe 3
            m.lowerKey(1).shouldBeNull()
        }

        test("higherKey returns least key > given") {
            m.higherKey(5) shouldBe 7
            m.higherKey(9).shouldBeNull()
        }
    }

    context("range views — snapshots") {
        val m = sortedMapOf(naturalOrder<Int>(), 1 to "a", 2 to "b", 3 to "c", 4 to "d", 5 to "e")

        test("headMap exclusive") {
            m.headMap(3, inclusive = false).keys.toList() shouldContainExactly listOf(1, 2)
        }

        test("headMap inclusive") {
            m.headMap(3, inclusive = true).keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("tailMap inclusive") {
            m.tailMap(3, inclusive = true).keys.toList() shouldContainExactly listOf(3, 4, 5)
        }

        test("tailMap exclusive") {
            m.tailMap(3, inclusive = false).keys.toList() shouldContainExactly listOf(4, 5)
        }

        test("subMap inclusive-exclusive") {
            m.subMap(2, true, 4, false).keys.toList() shouldContainExactly listOf(2, 3)
        }

        test("subMap inclusive-inclusive") {
            m.subMap(2, true, 4, true).keys.toList() shouldContainExactly listOf(2, 3, 4)
        }

        test("headMap is a snapshot — mutation of mutable original does not affect it") {
            val mut = mutableSortedMapOf(naturalOrder<Int>(), 1 to "a", 2 to "b", 3 to "c", 4 to "d", 5 to "e")
            val snap = mut.headMap(4, inclusive = false)
            mut.put(0, "zero")
            snap.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("range view on empty result is empty") {
            m.subMap(2, false, 3, false).isEmpty().shouldBeTrue()
        }

        test("subMap throws when fromKey > toKey") {
            shouldThrow<IllegalArgumentException> { m.subMap(4, true, 2, true) }
        }

        test("values in range view are correct") {
            m.subMap(2, true, 4, true).values.toList() shouldContainExactly listOf("b", "c", "d")
        }
    }

    context("descending") {
        test("descendingMap reverses key order") {
            val m = sortedMapOf(naturalOrder<Int>(), 1 to "a", 2 to "b", 3 to "c")
            m.descendingMap().keys.toList() shouldContainExactly listOf(3, 2, 1)
        }

        test("descendingKeySet reverses key order") {
            val m = sortedMapOf(naturalOrder<Int>(), 1 to "a", 2 to "b", 3 to "c")
            m.descendingKeySet().toList() shouldContainExactly listOf(3, 2, 1)
        }

        test("descendingMap is a snapshot") {
            val mut = mutableSortedMapOf(naturalOrder<Int>(), 1 to "a", 2 to "b", 3 to "c")
            val snap = mut.descendingMap()
            mut.put(4, "d")
            snap.keys.toList() shouldContainExactly listOf(3, 2, 1)
        }
    }

    context("equality and hashCode") {
        test("two sorted maps with same entries are equal") {
            val a = sortedMapOf(naturalOrder<Int>(), 1 to "a", 2 to "b", 3 to "c")
            val b = sortedMapOf(naturalOrder<Int>(), 3 to "c", 1 to "a", 2 to "b")
            a shouldBe b
        }

        test("sorted map equals a plain Map with same entries") {
            val a = sortedMapOf(naturalOrder<Int>(), 1 to "a", 2 to "b")
            val b: Map<Int, String> = mapOf(1 to "a", 2 to "b")
            a shouldBe b
        }

        test("hashCode matches a plain Map with same entries") {
            val a = sortedMapOf(naturalOrder<Int>(), 1 to "a", 2 to "b")
            val b: Map<Int, String> = mapOf(1 to "a", 2 to "b")
            a.hashCode() shouldBe b.hashCode()
        }
    }

    context("stress") {
        test("1000 random insertions yield sorted keys") {
            val pairs = (1..1000).shuffled(kotlin.random.Random(seed = 99L)).map { it to it.toString() }
            val m = sortedMapOf(naturalOrder<Int>(), *pairs.toTypedArray())
            m.keys.toList() shouldContainExactly (1..1000).toList()
        }
    }
})
