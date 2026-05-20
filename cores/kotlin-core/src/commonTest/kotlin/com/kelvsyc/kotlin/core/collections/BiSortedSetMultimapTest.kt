package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class BiSortedSetMultimapTest : FunSpec({

    context("construction") {
        test("emptyBiSortedSetMultimap is empty") {
            val m = emptyBiSortedSetMultimap<Int, String>(naturalOrder(), naturalOrder())
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }

        test("biSortedSetMultimapOf with pairs, keys in key comparator order") {
            val m = biSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(), 3 to "c", 1 to "a", 2 to "b")
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("biSortedSetMultimapOf with Comparable types uses natural order") {
            val m = biSortedSetMultimapOf(3 to "c", 1 to "a", 2 to "b")
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("duplicate key-value pairs are silently ignored") {
            val m = biSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(), 1 to "a", 1 to "a", 1 to "b")
            m[1].toList() shouldContainExactly listOf("a", "b")
            m.size shouldBe 2
        }

        test("Iterable.toBiSortedSetMultimap") {
            val m = listOf(3 to "c", 1 to "a", 2 to "b").toBiSortedSetMultimap(naturalOrder(), naturalOrder())
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("Iterable.toBiSortedSetMultimap with natural order") {
            val m = listOf(3 to "c", 1 to "a").toBiSortedSetMultimap()
            m.keys.toList() shouldContainExactly listOf(1, 3)
        }

        test("buildBiSortedSetMultimap") {
            val m = buildBiSortedSetMultimap(naturalOrder<Int>(), naturalOrder<String>()) {
                put(3, "c"); put(1, "a"); put(2, "b")
            }
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }
    }

    context("value ordering") {
        test("values within each key are in valueComparator order") {
            val m = biSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(),
                1 to "c", 1 to "a", 1 to "b")
            m[1].toList() shouldContainExactly listOf("a", "b", "c")
        }

        test("values within each key are in reverse valueComparator order when reversed") {
            val m = biSortedSetMultimapOf(naturalOrder<Int>(), reverseOrder<String>(),
                1 to "a", 1 to "b", 1 to "c")
            m[1].toList() shouldContainExactly listOf("c", "b", "a")
        }

        test("asMap values are SortedSet") {
            val m = biSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(), 1 to "b", 1 to "a")
            m.asMap[1].shouldBeInstanceOf<SortedSet<String>>()
            m.asMap[1]!!.toList() shouldContainExactly listOf("a", "b")
        }

        test("asMap is SortedMap<Int, SortedSet<String>>") {
            val m = biSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(), 1 to "a", 2 to "b")
            m.asMap.shouldBeInstanceOf<SortedMap<Int, SortedSet<String>>>()
        }
    }

    context("BiSortedSetMultimap is a SortedSetMultimap") {
        test("biSortedSetMultimapOf returns BiSortedSetMultimap which is also SortedSetMultimap") {
            val m: SortedSetMultimap<Int, String> = biSortedSetMultimapOf(naturalOrder(), naturalOrder(), 1 to "a")
            m.shouldBeInstanceOf<BiSortedSetMultimap<Int, String>>()
        }
    }

    context("firstKey and lastKey") {
        val m = biSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(), 3 to "c", 1 to "a", 2 to "b")

        test("firstKey returns least key") { m.firstKey() shouldBe 1 }
        test("lastKey returns greatest key") { m.lastKey() shouldBe 3 }

        test("firstKey throws on empty") {
            shouldThrow<NoSuchElementException> {
                emptyBiSortedSetMultimap<Int, String>(naturalOrder(), naturalOrder()).firstKey()
            }
        }

        test("firstKeyOrNull is null on empty") {
            emptyBiSortedSetMultimap<Int, String>(naturalOrder(), naturalOrder()).firstKeyOrNull().shouldBeNull()
        }
    }

    context("floor, ceiling, lower, higher") {
        val m = biSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(),
            1 to "a", 3 to "c", 5 to "e", 7 to "g", 9 to "i")

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
        val m = biSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(),
            1 to "a", 2 to "b", 3 to "c", 4 to "d", 5 to "e")

        test("headMultimap exclusive") {
            m.headMultimap(3, false).keys.toList() shouldContainExactly listOf(1, 2)
        }

        test("headMultimap inclusive") {
            m.headMultimap(3, true).keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("tailMultimap inclusive") {
            m.tailMultimap(3, true).keys.toList() shouldContainExactly listOf(3, 4, 5)
        }

        test("subMultimap") {
            m.subMultimap(2, true, 4, false).keys.toList() shouldContainExactly listOf(2, 3)
        }

        test("descendingMultimap reverses key order") {
            m.descendingMultimap().keys.toList() shouldContainExactly listOf(5, 4, 3, 2, 1)
        }

        test("snapshot preserves valueComparator") {
            val source = mutableBiSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(),
                1 to "c", 1 to "a", 1 to "b", 2 to "z")
            val snap = source.headMultimap(2, false)
            snap[1].toList() shouldContainExactly listOf("a", "b", "c")
            snap[1].shouldBeInstanceOf<SortedSet<String>>()
        }

        test("range view is a snapshot — source mutation does not affect it") {
            val source = mutableBiSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(),
                1 to "a", 2 to "b", 3 to "c")
            val snap = source.headMultimap(3, false)
            source.put(1, "z")
            snap[1].toList() shouldContainExactly listOf("a")
        }
    }

    context("mutation") {
        test("remove(key, value) evicts key when bucket becomes empty") {
            val m = mutableBiSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(), 1 to "a")
            m.remove(1, "a")
            m.containsKey(1).shouldBeFalse()
        }

        test("replaceValues with empty set evicts key") {
            val m = mutableBiSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(),
                1 to "a", 2 to "b")
            m.replaceValues(1, emptyList())
            m.containsKey(1).shouldBeFalse()
            m.size shouldBe 1
        }

        test("replaceValues result is sorted by valueComparator") {
            val m = mutableBiSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(),
                1 to "a", 2 to "b")
            m.replaceValues(1, listOf("z", "m", "a"))
            m[1].toList() shouldContainExactly listOf("a", "m", "z")
        }

        test("get returns empty set for absent key") {
            val m = emptyBiSortedSetMultimap<Int, String>(naturalOrder(), naturalOrder())
            m[99].shouldBeEmpty()
        }

        test("clear empties the multimap") {
            val m = mutableBiSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(), 1 to "a")
            m.clear()
            m.isEmpty().shouldBeTrue()
        }
    }

    context("equality") {
        test("equal multimaps have equal hashCode") {
            val a = biSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(), 1 to "a", 2 to "b")
            val b = biSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(), 1 to "a", 2 to "b")
            (a == b).shouldBeTrue()
            a.hashCode() shouldBe b.hashCode()
        }

        test("different multimaps are not equal") {
            val a = biSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(), 1 to "a")
            val b = biSortedSetMultimapOf(naturalOrder<Int>(), naturalOrder<String>(), 1 to "b")
            (a == b).shouldBeFalse()
        }
    }
})
