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

class SortedFlatMultimapTest : FunSpec({

    // Lex key-then-value comparator used in most tests.
    val lexKV = compareBy<Pair<Int, String>> { it.first }.thenBy { it.second }
    // Value-first comparator — demonstrates that key order is NOT guaranteed.
    val lexVK = compareBy<Pair<Int, String>> { it.second }.thenBy { it.first }

    context("construction") {
        test("emptySortedFlatMultimap is empty") {
            val m = emptySortedFlatMultimap<Int, String>(lexKV)
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }

        test("sortedFlatMultimapOf with explicit comparator") {
            val m = sortedFlatMultimapOf(lexKV, 3 to "c", 1 to "a", 2 to "b")
            m.entries.toList() shouldContainExactly listOf(1 to "a", 2 to "b", 3 to "c")
        }

        test("sortedFlatMultimapOf with natural order uses lex key-then-value") {
            val m = sortedFlatMultimapOf(3 to "c", 1 to "a", 2 to "b")
            m.entries.toList() shouldContainExactly listOf(1 to "a", 2 to "b", 3 to "c")
        }

        test("value-first comparator produces value-first entry order") {
            val m = sortedFlatMultimapOf(lexVK, 1 to "c", 2 to "a", 3 to "b")
            m.entries.toList() shouldContainExactly listOf(2 to "a", 3 to "b", 1 to "c")
        }

        test("Iterable.toSortedFlatMultimap") {
            val m = listOf(3 to "c", 1 to "a", 2 to "b").toSortedFlatMultimap(lexKV)
            m.entries.toList() shouldContainExactly listOf(1 to "a", 2 to "b", 3 to "c")
        }

        test("Iterable.toSortedFlatMultimap natural order") {
            val m = listOf(3 to "c", 1 to "a", 2 to "b").toSortedFlatMultimap()
            m.entries.toList() shouldContainExactly listOf(1 to "a", 2 to "b", 3 to "c")
        }

        test("buildSortedFlatMultimap") {
            val m = buildSortedFlatMultimap(lexKV) {
                put(3, "c"); put(1, "a"); put(2, "b"); put(1, "a")
            }
            m.entries.toList() shouldContainExactly listOf(1 to "a", 1 to "a", 2 to "b", 3 to "c")
        }
    }

    context("entries are in comparator order, not insertion order") {
        test("lex key-then-value: entries reflect comparator, not insertion order") {
            val m = sortedFlatMultimapOf(lexKV, 3 to "c", 1 to "b", 1 to "a", 2 to "d")
            m.entries.toList() shouldContainExactly listOf(1 to "a", 1 to "b", 2 to "d", 3 to "c")
        }

        test("value-first: entries ordered by value, not by key") {
            val m = sortedFlatMultimapOf(lexVK, 1 to "z", 2 to "a", 3 to "m")
            m.entries.toList() shouldContainExactly listOf(2 to "a", 3 to "m", 1 to "z")
        }

        test("keys has no guaranteed order with a value-first comparator") {
            // keys is Set<K> — not SortedSet<K> — because comparator does not sort keys
            val m = sortedFlatMultimapOf(lexVK, 1 to "z", 2 to "a", 3 to "m")
            m.keys shouldBe setOf(1, 2, 3)
            m.keys.shouldBeInstanceOf<Set<Int>>()
        }

        test("asMap is Map not SortedMap") {
            val m = sortedFlatMultimapOf(lexVK, 1 to "z", 2 to "a")
            // asMap<K, List<V>> — values per key are in pair-sort order for that key
            m.asMap[1] shouldContainExactly listOf("z")
            m.asMap[2] shouldContainExactly listOf("a")
        }
    }

    context("get returns values in pair-sort order for that key") {
        test("single key multiple values — in comparator order") {
            val m = sortedFlatMultimapOf(lexKV, 1 to "c", 1 to "a", 1 to "b")
            m[1] shouldContainExactly listOf("a", "b", "c")
        }

        test("value-first comparator: per-key values in value comparator order") {
            val m = sortedFlatMultimapOf(lexVK, 1 to "c", 1 to "a", 1 to "b")
            m[1] shouldContainExactly listOf("a", "b", "c")
        }

        test("absent key returns empty list") {
            emptySortedFlatMultimap<Int, String>(lexKV)[99].shouldBeEmpty()
        }
    }

    context("duplicate pairs — comparator-equal pairs are both preserved") {
        test("identical pairs are both stored") {
            val m = sortedFlatMultimapOf(lexKV, 1 to "a", 1 to "a")
            m.size shouldBe 2
            m.entries.toList() shouldContainExactly listOf(1 to "a", 1 to "a")
        }

        test("value-first comparator: pairs with same value but different keys compare as equal but both stored") {
            // lexVK: compare by value then key — (1,"x") and (2,"x") compare equal by value, differ by key
            // Both must be preserved since their actual content differs
            val m = sortedFlatMultimapOf(lexVK, 1 to "x", 2 to "x")
            m.size shouldBe 2
            m.containsKey(1).shouldBeTrue()
            m.containsKey(2).shouldBeTrue()
        }
    }

    context("firstEntry and lastEntry") {
        val m = sortedFlatMultimapOf(lexKV, 3 to "c", 1 to "a", 2 to "b")

        test("firstEntry returns least pair") { m.firstEntry() shouldBe (1 to "a") }
        test("lastEntry returns greatest pair") { m.lastEntry() shouldBe (3 to "c") }

        test("firstEntry throws on empty") {
            shouldThrow<NoSuchElementException> {
                emptySortedFlatMultimap<Int, String>(lexKV).firstEntry()
            }
        }
        test("lastEntry throws on empty") {
            shouldThrow<NoSuchElementException> {
                emptySortedFlatMultimap<Int, String>(lexKV).lastEntry()
            }
        }
        test("firstEntryOrNull is null on empty") {
            emptySortedFlatMultimap<Int, String>(lexKV).firstEntryOrNull().shouldBeNull()
        }
        test("lastEntryOrNull is null on empty") {
            emptySortedFlatMultimap<Int, String>(lexKV).lastEntryOrNull().shouldBeNull()
        }
        test("firstEntryOrNull returns least on non-empty") {
            m.firstEntryOrNull() shouldBe (1 to "a")
        }
        test("lastEntryOrNull returns greatest on non-empty") {
            m.lastEntryOrNull() shouldBe (3 to "c")
        }
    }

    context("floor, ceiling, lower, higher") {
        val m = sortedFlatMultimapOf(lexKV, 1 to "a", 3 to "c", 5 to "e", 7 to "g", 9 to "i")

        test("floorEntry at exact match") { m.floorEntry(5 to "e") shouldBe (5 to "e") }
        test("floorEntry between entries") { m.floorEntry(4 to "d") shouldBe (3 to "c") }
        test("floorEntry before all entries") { m.floorEntry(0 to "z").shouldBeNull() }

        test("ceilingEntry at exact match") { m.ceilingEntry(5 to "e") shouldBe (5 to "e") }
        test("ceilingEntry between entries") { m.ceilingEntry(4 to "d") shouldBe (5 to "e") }
        test("ceilingEntry beyond all entries") { m.ceilingEntry(10 to "z").shouldBeNull() }

        test("lowerEntry strictly below") { m.lowerEntry(5 to "e") shouldBe (3 to "c") }
        test("lowerEntry before all entries") { m.lowerEntry(1 to "a").shouldBeNull() }

        test("higherEntry strictly above") { m.higherEntry(5 to "e") shouldBe (7 to "g") }
        test("higherEntry after all entries") { m.higherEntry(9 to "i").shouldBeNull() }
    }

    context("range views") {
        val m = sortedFlatMultimapOf(lexKV, 1 to "a", 2 to "b", 3 to "c", 4 to "d", 5 to "e")

        test("headMultimap exclusive") {
            m.headMultimap(3 to "c", false).entries.toList() shouldContainExactly
                listOf(1 to "a", 2 to "b")
        }
        test("headMultimap inclusive") {
            m.headMultimap(3 to "c", true).entries.toList() shouldContainExactly
                listOf(1 to "a", 2 to "b", 3 to "c")
        }
        test("tailMultimap inclusive") {
            m.tailMultimap(3 to "c", true).entries.toList() shouldContainExactly
                listOf(3 to "c", 4 to "d", 5 to "e")
        }
        test("tailMultimap exclusive") {
            m.tailMultimap(3 to "c", false).entries.toList() shouldContainExactly
                listOf(4 to "d", 5 to "e")
        }
        test("subMultimap") {
            m.subMultimap(2 to "b", true, 4 to "d", false).entries.toList() shouldContainExactly
                listOf(2 to "b", 3 to "c")
        }
        test("descendingMultimap reverses entry order") {
            m.descendingMultimap().entries.toList() shouldContainExactly
                listOf(5 to "e", 4 to "d", 3 to "c", 2 to "b", 1 to "a")
        }

        test("range view is a snapshot — source mutation does not affect it") {
            val source = mutableSortedFlatMultimapOf(lexKV, 1 to "a", 2 to "b", 3 to "c")
            val snap = source.headMultimap(3 to "c", false)
            source.put(1, "z")
            snap[1] shouldContainExactly listOf("a")
        }

        test("snapshot mutations do not affect source") {
            val source = mutableSortedFlatMultimapOf(lexKV, 1 to "a", 2 to "b")
            val snap = source.tailMultimap(1 to "a", true)
            snap.put(1, "z")
            source[1] shouldContainExactly listOf("a")
        }

        test("range view is SortedFlatMultimap") {
            m.headMultimap(3 to "c", true).shouldBeInstanceOf<SortedFlatMultimap<Int, String>>()
        }

        test("mutable range view is MutableSortedFlatMultimap") {
            val source = mutableSortedFlatMultimapOf(lexKV, 1 to "a", 2 to "b")
            source.headMultimap(2 to "b", true).shouldBeInstanceOf<MutableSortedFlatMultimap<Int, String>>()
        }
    }

    context("mutation") {
        test("put appends new pair in comparator order") {
            val m = mutableSortedFlatMultimapOf<Int, String>(lexKV)
            m.put(3, "c"); m.put(1, "a"); m.put(2, "b")
            m.entries.toList() shouldContainExactly listOf(1 to "a", 2 to "b", 3 to "c")
        }

        test("put preserves duplicate pairs") {
            val m = mutableSortedFlatMultimapOf<Int, String>(lexKV)
            m.put(1, "a"); m.put(1, "a")
            m.size shouldBe 2
        }

        test("remove(key) removes all pairs for that key and evicts key") {
            val m = mutableSortedFlatMultimapOf(lexKV, 1 to "a", 1 to "b", 2 to "c")
            m.remove(1) shouldContainExactly listOf("a", "b")
            m.containsKey(1).shouldBeFalse()
            m.size shouldBe 1
        }

        test("remove(key, value) removes first matching pair") {
            val m = mutableSortedFlatMultimapOf(lexKV, 1 to "a", 1 to "a", 1 to "b")
            m.remove(1, "a").shouldBeTrue()
            m[1] shouldContainExactly listOf("a", "b")
        }

        test("remove(key, value) returns false for absent pair") {
            val m = mutableSortedFlatMultimapOf(lexKV, 1 to "a")
            m.remove(1, "z").shouldBeFalse()
        }

        test("remove(key, value) evicts key when last pair is removed") {
            val m = mutableSortedFlatMultimapOf(lexKV, 1 to "a")
            m.remove(1, "a")
            m.containsKey(1).shouldBeFalse()
            m.isEmpty().shouldBeTrue()
        }

        test("replaceValues replaces all pairs for key in sort order") {
            val m = mutableSortedFlatMultimapOf(lexKV, 1 to "a", 1 to "b", 2 to "c")
            val old = m.replaceValues(1, listOf("z", "x"))
            old shouldContainExactly listOf("a", "b")
            m[1] shouldContainExactly listOf("x", "z")
            m.entries.toList() shouldContainExactly listOf(1 to "x", 1 to "z", 2 to "c")
        }

        test("replaceValues with empty list evicts key") {
            val m = mutableSortedFlatMultimapOf(lexKV, 1 to "a", 2 to "b")
            m.replaceValues(1, emptyList())
            m.containsKey(1).shouldBeFalse()
            m.size shouldBe 1
        }

        test("clear empties the multimap") {
            val m = mutableSortedFlatMultimapOf(lexKV, 1 to "a", 2 to "b")
            m.clear()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }
    }

    context("key-based operations via side index") {
        test("containsKey returns true for present key") {
            sortedFlatMultimapOf(lexKV, 1 to "a").containsKey(1).shouldBeTrue()
        }
        test("containsKey returns false for absent key") {
            sortedFlatMultimapOf(lexKV, 1 to "a").containsKey(99).shouldBeFalse()
        }
        test("containsValue returns true for present value") {
            sortedFlatMultimapOf(lexKV, 1 to "a").containsValue("a").shouldBeTrue()
        }
        test("containsEntry returns true for present pair") {
            sortedFlatMultimapOf(lexKV, 1 to "a").containsEntry(1, "a").shouldBeTrue()
        }
        test("containsEntry returns false for absent pair") {
            sortedFlatMultimapOf(lexKV, 1 to "a").containsEntry(1, "b").shouldBeFalse()
        }
    }

    context("equality") {
        test("equal multimaps have equal asMap") {
            val a = sortedFlatMultimapOf(lexKV, 1 to "a", 2 to "b")
            val b = sortedFlatMultimapOf(lexKV, 1 to "a", 2 to "b")
            (a == b).shouldBeTrue()
            a.hashCode() shouldBe b.hashCode()
        }

        test("different multimaps are not equal") {
            val a = sortedFlatMultimapOf(lexKV, 1 to "a")
            val b = sortedFlatMultimapOf(lexKV, 1 to "b")
            (a == b).shouldBeFalse()
        }

        test("comparators do not affect equality — only asMap content matters") {
            val a = sortedFlatMultimapOf(lexKV, 1 to "a", 2 to "b")
            val b = sortedFlatMultimapOf(lexVK, 1 to "a", 2 to "b")
            (a == b).shouldBeTrue()
        }
    }
})
