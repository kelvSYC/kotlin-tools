package com.kelvsyc.kotlin.core.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.enums.enumEntries

private enum class Suit { CLUBS, DIAMONDS, HEARTS, SPADES }
private enum class EmptySuit

class EnumListMultimapTest : FunSpec({

    context("construction") {
        test("emptyEnumListMultimap produces an empty multimap") {
            val m = emptyEnumListMultimap<Suit, Int>()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }

        test("emptyEnumListMultimap exposes enumEntries") {
            val m = emptyEnumListMultimap<Suit, Int>()
            m.enumEntries shouldBe enumEntries<Suit>()
        }

        test("enumListMultimapOf with no pairs produces an empty multimap") {
            enumListMultimapOf<Suit, Int>().isEmpty().shouldBeTrue()
        }

        test("enumListMultimapOf with pairs") {
            val m = enumListMultimapOf(Suit.CLUBS to 1, Suit.HEARTS to 3, Suit.CLUBS to 2)
            m.size shouldBe 3
            m[Suit.CLUBS].shouldContainExactly(1, 2)
            m[Suit.HEARTS].shouldContainExactly(3)
        }

        test("buildEnumListMultimap") {
            val m = buildEnumListMultimap<Suit, Int> {
                put(Suit.DIAMONDS, 2)
                put(Suit.DIAMONDS, 5)
                put(Suit.HEARTS, 3)
            }
            m.size shouldBe 3
            m[Suit.DIAMONDS].shouldContainExactly(2, 5)
        }

        test("toEnumListMultimap from Iterable") {
            val m = listOf(Suit.CLUBS to 1, Suit.DIAMONDS to 2, Suit.CLUBS to 3).toEnumListMultimap()
            m.size shouldBe 3
            m[Suit.CLUBS].shouldContainExactly(1, 3)
        }

        test("toEnumListMultimap from Sequence") {
            val m = sequenceOf(Suit.CLUBS to 1, Suit.CLUBS to 2).toEnumListMultimap()
            m[Suit.CLUBS].shouldContainExactly(1, 2)
        }

        test("toEnumListMultimap from ListMultimap") {
            val source = listMultimapOf(Suit.HEARTS to 10, Suit.CLUBS to 1, Suit.HEARTS to 20)
            val m = source.toEnumListMultimap()
            m.size shouldBe 3
            m[Suit.HEARTS].shouldContainExactly(10, 20)
            m[Suit.CLUBS].shouldContainExactly(1)
        }

        test("mutableEnumListMultimapOf empty") {
            val m = mutableEnumListMultimapOf<Suit, Int>()
            m.isEmpty().shouldBeTrue()
        }

        test("mutableEnumListMultimapOf with pairs") {
            val m = mutableEnumListMultimapOf(Suit.CLUBS to 1, Suit.CLUBS to 2)
            m.size shouldBe 2
        }

        test("toMutableEnumListMultimap from Iterable") {
            val m = listOf(Suit.CLUBS to 1).toMutableEnumListMultimap()
            m.put(Suit.DIAMONDS, 2)
            m.size shouldBe 2
        }

        test("toMutableEnumListMultimap from ListMultimap") {
            val source = listMultimapOf(Suit.CLUBS to 1)
            val m = source.toMutableEnumListMultimap()
            m.put(Suit.CLUBS, 2)
            m[Suit.CLUBS].shouldContainExactly(1, 2)
        }

        test("empty enum type") {
            val m = emptyEnumListMultimap<EmptySuit, String>()
            m.size shouldBe 0
            m.enumEntries.size shouldBe 0
        }
    }

    context("get and containsKey") {
        test("get returns list for present key") {
            val m = enumListMultimapOf(Suit.CLUBS to 10, Suit.CLUBS to 20)
            m[Suit.CLUBS].shouldContainExactly(10, 20)
        }

        test("get returns empty list for absent key") {
            val m = enumListMultimapOf(Suit.CLUBS to 10)
            m[Suit.DIAMONDS].shouldBeEmpty()
        }

        test("containsKey true for present key") {
            val m = enumListMultimapOf(Suit.CLUBS to 10)
            m.containsKey(Suit.CLUBS).shouldBeTrue()
        }

        test("containsKey false for absent key") {
            val m = enumListMultimapOf(Suit.CLUBS to 10)
            m.containsKey(Suit.DIAMONDS).shouldBeFalse()
        }
    }

    context("containsValue and containsEntry") {
        test("containsValue true for present value") {
            val m = enumListMultimapOf(Suit.CLUBS to 10)
            m.containsValue(10).shouldBeTrue()
        }

        test("containsValue false for absent value") {
            val m = enumListMultimapOf(Suit.CLUBS to 10)
            m.containsValue(99).shouldBeFalse()
        }

        test("containsEntry true for present pair") {
            val m = enumListMultimapOf(Suit.CLUBS to 10, Suit.CLUBS to 20)
            m.containsEntry(Suit.CLUBS, 10).shouldBeTrue()
            m.containsEntry(Suit.CLUBS, 20).shouldBeTrue()
        }

        test("containsEntry false for wrong key") {
            val m = enumListMultimapOf(Suit.CLUBS to 10)
            m.containsEntry(Suit.DIAMONDS, 10).shouldBeFalse()
        }

        test("containsEntry false for wrong value") {
            val m = enumListMultimapOf(Suit.CLUBS to 10)
            m.containsEntry(Suit.CLUBS, 99).shouldBeFalse()
        }
    }

    context("mutation") {
        test("put appends to existing key") {
            val m = mutableEnumListMultimapOf(Suit.CLUBS to 1)
            m.put(Suit.CLUBS, 2)
            m[Suit.CLUBS].shouldContainExactly(1, 2)
            m.size shouldBe 2
        }

        test("put creates new key") {
            val m = mutableEnumListMultimapOf<Suit, Int>()
            m.put(Suit.CLUBS, 1)
            m[Suit.CLUBS].shouldContainExactly(1)
            m.size shouldBe 1
        }

        test("putAll with key and values") {
            val m = mutableEnumListMultimapOf<Suit, Int>()
            m.putAll(Suit.CLUBS, listOf(1, 2, 3))
            m[Suit.CLUBS].shouldContainExactly(1, 2, 3)
            m.size shouldBe 3
        }

        test("putAll with empty values is no-op") {
            val m = mutableEnumListMultimapOf<Suit, Int>()
            m.putAll(Suit.CLUBS, emptyList())
            m.isEmpty().shouldBeTrue()
        }

        test("putAll from ListMultimap") {
            val m = mutableEnumListMultimapOf<Suit, Int>()
            val source = listMultimapOf(Suit.CLUBS to 1, Suit.HEARTS to 2)
            m.putAll(source)
            m.size shouldBe 2
            m[Suit.CLUBS].shouldContainExactly(1)
            m[Suit.HEARTS].shouldContainExactly(2)
        }

        test("putAll from pairs") {
            val m = mutableEnumListMultimapOf<Suit, Int>()
            m.putAll(listOf(Suit.CLUBS to 1, Suit.CLUBS to 2, Suit.DIAMONDS to 3))
            m.size shouldBe 3
        }

        test("replaceValues replaces existing") {
            val m = mutableEnumListMultimapOf(Suit.CLUBS to 1, Suit.CLUBS to 2)
            val old = m.replaceValues(Suit.CLUBS, listOf(10, 20, 30))
            old.shouldContainExactly(1, 2)
            m[Suit.CLUBS].shouldContainExactly(10, 20, 30)
            m.size shouldBe 3
        }

        test("replaceValues with empty removes key") {
            val m = mutableEnumListMultimapOf(Suit.CLUBS to 1, Suit.CLUBS to 2)
            val old = m.replaceValues(Suit.CLUBS, emptyList())
            old.shouldContainExactly(1, 2)
            m.containsKey(Suit.CLUBS).shouldBeFalse()
            m.size shouldBe 0
        }

        test("replaceValues for absent key returns empty") {
            val m = mutableEnumListMultimapOf<Suit, Int>()
            val old = m.replaceValues(Suit.CLUBS, listOf(1))
            old.shouldBeEmpty()
            m[Suit.CLUBS].shouldContainExactly(1)
            m.size shouldBe 1
        }

        test("replaceValues for absent key with empty values") {
            val m = mutableEnumListMultimapOf<Suit, Int>()
            val old = m.replaceValues(Suit.CLUBS, emptyList())
            old.shouldBeEmpty()
            m.size shouldBe 0
        }

        test("remove key returns removed values") {
            val m = mutableEnumListMultimapOf(Suit.CLUBS to 1, Suit.CLUBS to 2, Suit.DIAMONDS to 3)
            val removed = m.remove(Suit.CLUBS)
            removed.shouldContainExactly(1, 2)
            m.containsKey(Suit.CLUBS).shouldBeFalse()
            m.size shouldBe 1
        }

        test("remove absent key returns empty list") {
            val m = mutableEnumListMultimapOf<Suit, Int>()
            m.remove(Suit.CLUBS).shouldBeEmpty()
        }

        test("remove key-value pair removes first occurrence") {
            val m = mutableEnumListMultimapOf(Suit.CLUBS to 1, Suit.CLUBS to 2, Suit.CLUBS to 1)
            m.remove(Suit.CLUBS, 1).shouldBeTrue()
            m[Suit.CLUBS].shouldContainExactly(2, 1)
            m.size shouldBe 2
        }

        test("remove key-value pair returns false when not present") {
            val m = mutableEnumListMultimapOf(Suit.CLUBS to 1)
            m.remove(Suit.CLUBS, 99).shouldBeFalse()
            m.size shouldBe 1
        }

        test("remove key-value cleans up empty list") {
            val m = mutableEnumListMultimapOf(Suit.CLUBS to 1)
            m.remove(Suit.CLUBS, 1).shouldBeTrue()
            m.containsKey(Suit.CLUBS).shouldBeFalse()
            m.size shouldBe 0
        }

        test("clear empties the multimap") {
            val m = mutableEnumListMultimapOf(Suit.CLUBS to 1, Suit.DIAMONDS to 2, Suit.HEARTS to 3)
            m.clear()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }
    }

    context("ordering") {
        test("keys iterate in enum declaration order regardless of insertion order") {
            val m = enumListMultimapOf(Suit.HEARTS to 3, Suit.CLUBS to 1, Suit.DIAMONDS to 2)
            m.keys.toList().shouldContainExactly(Suit.CLUBS, Suit.DIAMONDS, Suit.HEARTS)
        }

        test("asMap keys in enum declaration order") {
            val m = enumListMultimapOf(Suit.HEARTS to 3, Suit.CLUBS to 1)
            m.asMap.keys.toList().shouldContainExactly(Suit.CLUBS, Suit.HEARTS)
        }

        test("entries follow enum key order") {
            val m = enumListMultimapOf(Suit.HEARTS to 3, Suit.CLUBS to 1, Suit.CLUBS to 2)
            m.entries.toList().shouldContainExactly(
                Suit.CLUBS to 1,
                Suit.CLUBS to 2,
                Suit.HEARTS to 3,
            )
        }
    }

    context("asMap") {
        test("asMap returns EnumMap") {
            val m = enumListMultimapOf(Suit.CLUBS to 1)
            m.asMap.shouldBeInstanceOf<EnumMap<Suit, List<Int>>>()
        }

        test("asMap is a live view") {
            val m = mutableEnumListMultimapOf(Suit.CLUBS to 1)
            val map = m.asMap
            m.put(Suit.CLUBS, 2)
            map[Suit.CLUBS]!!.shouldContainExactly(1, 2)
        }

        test("asMap skips absent keys") {
            val m = enumListMultimapOf(Suit.CLUBS to 1, Suit.HEARTS to 3)
            m.asMap.size shouldBe 2
            m.asMap.containsKey(Suit.DIAMONDS).shouldBeFalse()
        }

        test("asMap values are not externally mutable") {
            val m = enumListMultimapOf(Suit.CLUBS to 1)
            val values = m.asMap[Suit.CLUBS]!!
            values.shouldBeInstanceOf<List<Int>>()
        }
    }

    context("equality") {
        test("equal to a ListMultimap with same content") {
            val enum = enumListMultimapOf(Suit.CLUBS to 1, Suit.HEARTS to 2)
            val regular = listMultimapOf(Suit.CLUBS to 1, Suit.HEARTS to 2)
            enum shouldBe regular
        }

        test("not equal when different values") {
            val m1 = enumListMultimapOf(Suit.CLUBS to 1)
            val m2 = enumListMultimapOf(Suit.CLUBS to 2)
            m1 shouldNotBe m2
        }

        test("not equal when different keys") {
            val m1 = enumListMultimapOf(Suit.CLUBS to 1)
            val m2 = enumListMultimapOf(Suit.DIAMONDS to 1)
            m1 shouldNotBe m2
        }

        test("empty enum list multimaps are equal") {
            emptyEnumListMultimap<Suit, Int>() shouldBe emptyEnumListMultimap<Suit, Int>()
        }

        test("hashCode consistent with asMap") {
            val m = enumListMultimapOf(Suit.CLUBS to 1, Suit.DIAMONDS to 2)
            m.hashCode() shouldBe m.asMap.hashCode()
        }

        test("equal multimaps have same hashCode") {
            val m1 = enumListMultimapOf(Suit.CLUBS to 1, Suit.HEARTS to 2)
            val m2 = listMultimapOf(Suit.CLUBS to 1, Suit.HEARTS to 2)
            m1.hashCode() shouldBe m2.hashCode()
        }
    }

    context("size tracking") {
        test("size increments on put") {
            val m = mutableEnumListMultimapOf<Suit, Int>()
            m.put(Suit.CLUBS, 1)
            m.size shouldBe 1
            m.put(Suit.CLUBS, 2)
            m.size shouldBe 2
            m.put(Suit.DIAMONDS, 3)
            m.size shouldBe 3
        }

        test("size decrements on remove key-value") {
            val m = mutableEnumListMultimapOf(Suit.CLUBS to 1, Suit.CLUBS to 2)
            m.remove(Suit.CLUBS, 1)
            m.size shouldBe 1
        }

        test("size decrements by list length on remove key") {
            val m = mutableEnumListMultimapOf(Suit.CLUBS to 1, Suit.CLUBS to 2, Suit.DIAMONDS to 3)
            m.remove(Suit.CLUBS)
            m.size shouldBe 1
        }

        test("size adjusts correctly on replaceValues") {
            val m = mutableEnumListMultimapOf(Suit.CLUBS to 1, Suit.CLUBS to 2)
            m.replaceValues(Suit.CLUBS, listOf(10, 20, 30))
            m.size shouldBe 3
        }

        test("size resets to zero on clear") {
            val m = mutableEnumListMultimapOf(Suit.CLUBS to 1, Suit.DIAMONDS to 2)
            m.clear()
            m.size shouldBe 0
        }
    }
})
