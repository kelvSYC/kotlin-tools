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

class SortedListMultimapTest : FunSpec({

    context("construction") {
        test("emptySortedListMultimap is empty") {
            val m = emptySortedListMultimap<Int, String>(naturalOrder())
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }

        test("sortedListMultimapOf with pairs preserves insertion order per key in comparator key order") {
            val m = sortedListMultimapOf(naturalOrder<Int>(), 3 to "c", 1 to "a", 2 to "b", 1 to "aa")
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
            m[1] shouldContainExactly listOf("a", "aa")
        }

        test("sortedListMultimapOf with Comparable keys uses natural order") {
            val m = sortedListMultimapOf(3 to "c", 1 to "a", 2 to "b")
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("sortedListMultimapOf with reverse comparator") {
            val m = sortedListMultimapOf(reverseOrder<Int>(), 1 to "a", 2 to "b", 3 to "c")
            m.keys.toList() shouldContainExactly listOf(3, 2, 1)
        }

        test("Iterable.toSortedListMultimap") {
            val m = listOf(3 to "c", 1 to "a", 2 to "b").toSortedListMultimap(naturalOrder())
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("Iterable.toSortedListMultimap with natural order") {
            val m = listOf(3 to "c", 1 to "a", 2 to "b").toSortedListMultimap()
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("buildSortedListMultimap") {
            val m = buildSortedListMultimap(naturalOrder<Int>()) {
                put(3, "c"); put(1, "a"); put(2, "b"); put(1, "aa")
            }
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
            m[1] shouldContainExactly listOf("a", "aa")
        }
    }

    context("properties") {
        val m = sortedListMultimapOf(naturalOrder<Int>(), 1 to "a", 2 to "b", 1 to "aa", 3 to "c")

        test("asMap is SortedMap with keys in comparator order") {
            m.asMap.shouldBeInstanceOf<SortedMap<Int, List<String>>>()
            m.asMap.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("keys is SortedSet in comparator order") {
            m.keys.shouldBeInstanceOf<SortedSet<Int>>()
            m.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("size is total key-value count") {
            m.size shouldBe 4
        }

        test("values are in key-grouped key-comparator order") {
            m.values.toList() shouldContainExactly listOf("a", "aa", "b", "c")
        }

        test("entries are in key-grouped key-comparator order") {
            m.entries.toList() shouldContainExactly listOf(1 to "a", 1 to "aa", 2 to "b", 3 to "c")
        }

        test("get returns values in insertion order for key") {
            m[1] shouldContainExactly listOf("a", "aa")
        }

        test("get returns empty list for absent key") {
            m[99].shouldBeEmpty()
        }

        test("containsKey returns true for present key") {
            m.containsKey(1).shouldBeTrue()
        }

        test("containsKey returns false for absent key") {
            m.containsKey(99).shouldBeFalse()
        }

        test("duplicate key-value pairs are preserved") {
            val dup = sortedListMultimapOf(naturalOrder<Int>(), 1 to "a", 1 to "a")
            dup[1] shouldContainExactly listOf("a", "a")
            dup.size shouldBe 2
        }
    }

    context("firstKey and lastKey") {
        test("firstKey returns least key") {
            sortedListMultimapOf(naturalOrder<Int>(), 3 to "c", 1 to "a", 2 to "b").firstKey() shouldBe 1
        }

        test("lastKey returns greatest key") {
            sortedListMultimapOf(naturalOrder<Int>(), 3 to "c", 1 to "a", 2 to "b").lastKey() shouldBe 3
        }

        test("firstKey throws on empty multimap") {
            shouldThrow<NoSuchElementException> { emptySortedListMultimap<Int, String>(naturalOrder()).firstKey() }
        }

        test("lastKey throws on empty multimap") {
            shouldThrow<NoSuchElementException> { emptySortedListMultimap<Int, String>(naturalOrder()).lastKey() }
        }

        test("firstKeyOrNull is null on empty multimap") {
            emptySortedListMultimap<Int, String>(naturalOrder()).firstKeyOrNull().shouldBeNull()
        }

        test("lastKeyOrNull is null on empty multimap") {
            emptySortedListMultimap<Int, String>(naturalOrder()).lastKeyOrNull().shouldBeNull()
        }

        test("firstKeyOrNull returns least key on non-empty") {
            sortedListMultimapOf(naturalOrder<Int>(), 3 to "c", 1 to "a").firstKeyOrNull() shouldBe 1
        }

        test("lastKeyOrNull returns greatest key on non-empty") {
            sortedListMultimapOf(naturalOrder<Int>(), 3 to "c", 1 to "a").lastKeyOrNull() shouldBe 3
        }
    }

    context("floor, ceiling, lower, higher") {
        val m = sortedListMultimapOf(naturalOrder<Int>(), 1 to "a", 3 to "c", 5 to "e", 7 to "g", 9 to "i")

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

    context("range views") {
        val m = sortedListMultimapOf(naturalOrder<Int>(), 1 to "a", 2 to "b", 3 to "c", 4 to "d", 5 to "e")

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

        test("range view is a snapshot — source mutation does not affect it") {
            val source = mutableSortedListMultimapOf(naturalOrder<Int>(), 1 to "a", 2 to "b", 3 to "c")
            val snap = source.headMultimap(3, false)
            source.put(1, "aa")
            snap[1] shouldContainExactly listOf("a")
        }

        test("range view is SortedListMultimap") {
            m.headMultimap(3, true).shouldBeInstanceOf<SortedListMultimap<Int, String>>()
        }
    }

    context("mutation") {
        test("put appends to the list for a key") {
            val m = mutableSortedListMultimapOf<Int, String>(naturalOrder())
            m.put(1, "a"); m.put(1, "b")
            m[1] shouldContainExactly listOf("a", "b")
        }

        test("remove(key) removes all values and evicts key") {
            val m = mutableSortedListMultimapOf(naturalOrder<Int>(), 1 to "a", 1 to "b", 2 to "c")
            m.remove(1) shouldContainExactly listOf("a", "b")
            m.containsKey(1).shouldBeFalse()
            m.size shouldBe 1
        }

        test("remove(key, value) removes first occurrence") {
            val m = mutableSortedListMultimapOf(naturalOrder<Int>(), 1 to "a", 1 to "a", 1 to "b")
            m.remove(1, "a").shouldBeTrue()
            m[1] shouldContainExactly listOf("a", "b")
        }

        test("remove(key, value) evicts key when bucket becomes empty") {
            val m = mutableSortedListMultimapOf(naturalOrder<Int>(), 1 to "a")
            m.remove(1, "a")
            m.containsKey(1).shouldBeFalse()
        }

        test("replaceValues replaces values for key") {
            val m = mutableSortedListMultimapOf(naturalOrder<Int>(), 1 to "a", 1 to "b", 2 to "c")
            val old = m.replaceValues(1, listOf("x", "y"))
            old shouldContainExactly listOf("a", "b")
            m[1] shouldContainExactly listOf("x", "y")
        }

        test("replaceValues with empty list evicts key") {
            val m = mutableSortedListMultimapOf(naturalOrder<Int>(), 1 to "a", 2 to "b")
            m.replaceValues(1, emptyList())
            m.containsKey(1).shouldBeFalse()
            m.size shouldBe 1
        }

        test("clear empties the multimap") {
            val m = mutableSortedListMultimapOf(naturalOrder<Int>(), 1 to "a", 2 to "b")
            m.clear()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }
    }

    context("equality") {
        test("equal multimaps have equal asMap") {
            val a = sortedListMultimapOf(naturalOrder<Int>(), 1 to "a", 2 to "b")
            val b = sortedListMultimapOf(naturalOrder<Int>(), 1 to "a", 2 to "b")
            (a == b).shouldBeTrue()
            a.hashCode() shouldBe b.hashCode()
        }

        test("different multimaps are not equal") {
            val a = sortedListMultimapOf(naturalOrder<Int>(), 1 to "a")
            val b = sortedListMultimapOf(naturalOrder<Int>(), 1 to "b")
            (a == b).shouldBeFalse()
        }
    }
})
