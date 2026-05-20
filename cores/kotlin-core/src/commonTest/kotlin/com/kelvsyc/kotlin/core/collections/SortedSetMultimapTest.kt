package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class SortedSetMultimapTest : FunSpec({

    context("construction") {
        test("emptySortedSetMultimap is empty") {
            val m = emptySortedSetMultimap<Int, String>(naturalOrder())
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }

        test("sortedSetMultimapOf with pairs, keys in comparator order") {
            val m = sortedSetMultimapOf(naturalOrder<Int>(), 3 to "c", 1 to "a", 2 to "b")
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("sortedSetMultimapOf with Comparable keys uses natural order") {
            val m = sortedSetMultimapOf(3 to "c", 1 to "a", 2 to "b")
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("duplicate key-value pairs are silently ignored") {
            val m = sortedSetMultimapOf(naturalOrder<Int>(), 1 to "a", 1 to "a", 1 to "b")
            m[1] shouldContainExactlyInAnyOrder listOf("a", "b")
            m.size shouldBe 2
        }

        test("Iterable.toSortedSetMultimap") {
            val m = listOf(3 to "c", 1 to "a", 2 to "b").toSortedSetMultimap(naturalOrder())
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("Iterable.toSortedSetMultimap with natural order") {
            val m = listOf(3 to "c", 1 to "a").toSortedSetMultimap()
            m.keys.toList() shouldContainExactly listOf(1, 3)
        }

        test("buildSortedSetMultimap") {
            val m = buildSortedSetMultimap(naturalOrder<Int>()) {
                put(3, "c"); put(1, "a"); put(2, "b")
            }
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }
    }

    context("properties") {
        val m = sortedSetMultimapOf(naturalOrder<Int>(), 1 to "a", 2 to "b", 1 to "c", 3 to "d")

        test("asMap is SortedMap<Int, Set<String>>") {
            m.asMap.shouldBeInstanceOf<SortedMap<Int, Set<String>>>()
            m.asMap.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("keys is SortedSet in comparator order") {
            m.keys.shouldBeInstanceOf<SortedSet<Int>>()
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("size is total distinct key-value count") {
            m.size shouldBe 4
        }

        test("get returns empty set for absent key") {
            m[99].shouldBeEmpty()
        }

        test("containsEntry returns true for present pair") {
            m.containsEntry(1, "a").shouldBeTrue()
        }

        test("containsEntry returns false for absent pair") {
            m.containsEntry(1, "z").shouldBeFalse()
        }
    }

    context("firstKey and lastKey") {
        val m = sortedSetMultimapOf(naturalOrder<Int>(), 3 to "c", 1 to "a", 2 to "b")

        test("firstKey returns least key") { m.firstKey() shouldBe 1 }
        test("lastKey returns greatest key") { m.lastKey() shouldBe 3 }

        test("firstKey throws on empty") {
            shouldThrow<NoSuchElementException> { emptySortedSetMultimap<Int, String>(naturalOrder()).firstKey() }
        }

        test("lastKey throws on empty") {
            shouldThrow<NoSuchElementException> { emptySortedSetMultimap<Int, String>(naturalOrder()).lastKey() }
        }

        test("firstKeyOrNull is null on empty") {
            emptySortedSetMultimap<Int, String>(naturalOrder()).firstKeyOrNull().shouldBeNull()
        }

        test("lastKeyOrNull returns greatest key") { m.lastKeyOrNull() shouldBe 3 }
    }

    context("floor, ceiling, lower, higher") {
        val m = sortedSetMultimapOf(naturalOrder<Int>(), 1 to "a", 3 to "c", 5 to "e", 7 to "g", 9 to "i")

        test("floorKey") {
            m.floorKey(5) shouldBe 5
            m.floorKey(4) shouldBe 3
            m.floorKey(0).shouldBeNull()
        }

        test("ceilingKey") {
            m.ceilingKey(5) shouldBe 5
            m.ceilingKey(4) shouldBe 5
            m.ceilingKey(10).shouldBeNull()
        }

        test("lowerKey") {
            m.lowerKey(5) shouldBe 3
            m.lowerKey(1).shouldBeNull()
        }

        test("higherKey") {
            m.higherKey(5) shouldBe 7
            m.higherKey(9).shouldBeNull()
        }
    }

    context("range views") {
        val m = sortedSetMultimapOf(naturalOrder<Int>(), 1 to "a", 2 to "b", 3 to "c", 4 to "d", 5 to "e")

        test("headMultimap exclusive") {
            m.headMultimap(3, false).keys.toList() shouldContainExactly listOf(1, 2)
        }

        test("headMultimap inclusive") {
            m.headMultimap(3, true).keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("tailMultimap inclusive") {
            m.tailMultimap(3, true).keys.toList() shouldContainExactly listOf(3, 4, 5)
        }

        test("tailMultimap exclusive") {
            m.tailMultimap(3, false).keys.toList() shouldContainExactly listOf(4, 5)
        }

        test("subMultimap") {
            m.subMultimap(2, true, 4, false).keys.toList() shouldContainExactly listOf(2, 3)
        }

        test("descendingMultimap reverses key order") {
            m.descendingMultimap().keys.toList() shouldContainExactly listOf(5, 4, 3, 2, 1)
        }

        test("range view is a snapshot") {
            val source = mutableSortedSetMultimapOf(naturalOrder<Int>(), 1 to "a", 2 to "b", 3 to "c")
            val snap = source.headMultimap(3, false)
            source.put(1, "aa")
            snap[1] shouldContainExactlyInAnyOrder listOf("a")
        }
    }

    context("mutation") {
        test("put returns true for new pair, false for duplicate") {
            val m = mutableSortedSetMultimapOf<Int, String>(naturalOrder())
            m.put(1, "a").shouldBeTrue()
            m.put(1, "a").shouldBeFalse()
        }

        test("remove(key) evicts key") {
            val m = mutableSortedSetMultimapOf(naturalOrder<Int>(), 1 to "a", 1 to "b", 2 to "c")
            m.remove(1) shouldContainExactlyInAnyOrder listOf("a", "b")
            m.containsKey(1).shouldBeFalse()
        }

        test("remove(key, value) evicts key when bucket becomes empty") {
            val m = mutableSortedSetMultimapOf(naturalOrder<Int>(), 1 to "a")
            m.remove(1, "a")
            m.containsKey(1).shouldBeFalse()
        }

        test("replaceValues with empty set evicts key") {
            val m = mutableSortedSetMultimapOf(naturalOrder<Int>(), 1 to "a", 2 to "b")
            m.replaceValues(1, emptyList())
            m.containsKey(1).shouldBeFalse()
            m.size shouldBe 1
        }

        test("clear empties the multimap") {
            val m = mutableSortedSetMultimapOf(naturalOrder<Int>(), 1 to "a", 2 to "b")
            m.clear()
            m.isEmpty().shouldBeTrue()
        }
    }

    context("equality") {
        test("equal multimaps have equal hashCode") {
            val a = sortedSetMultimapOf(naturalOrder<Int>(), 1 to "a", 2 to "b")
            val b = sortedSetMultimapOf(naturalOrder<Int>(), 1 to "a", 2 to "b")
            (a == b).shouldBeTrue()
            a.hashCode() shouldBe b.hashCode()
        }

        test("different multimaps are not equal") {
            val a = sortedSetMultimapOf(naturalOrder<Int>(), 1 to "a")
            val b = sortedSetMultimapOf(naturalOrder<Int>(), 1 to "b")
            (a == b).shouldBeFalse()
        }
    }
})
