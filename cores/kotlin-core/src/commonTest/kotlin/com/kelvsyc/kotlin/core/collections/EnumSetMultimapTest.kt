package com.kelvsyc.kotlin.core.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.enums.enumEntries

private enum class Direction { NORTH, EAST, SOUTH, WEST }
private enum class EmptyDirection

class EnumSetMultimapTest : FunSpec({

    context("construction") {
        test("emptyEnumSetMultimap produces an empty multimap") {
            val m = emptyEnumSetMultimap<Direction, Int>()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }

        test("emptyEnumSetMultimap exposes enumEntries") {
            val m = emptyEnumSetMultimap<Direction, Int>()
            m.enumEntries shouldBe enumEntries<Direction>()
        }

        test("enumSetMultimapOf with no pairs produces an empty multimap") {
            enumSetMultimapOf<Direction, Int>().isEmpty().shouldBeTrue()
        }

        test("enumSetMultimapOf with pairs") {
            val m = enumSetMultimapOf(Direction.NORTH to 1, Direction.SOUTH to 3, Direction.NORTH to 2)
            m.size shouldBe 3
            m[Direction.NORTH].shouldContainExactlyInAnyOrder(1, 2)
            m[Direction.SOUTH].shouldContainExactlyInAnyOrder(3)
        }

        test("enumSetMultimapOf discards duplicate pairs") {
            val m = enumSetMultimapOf(Direction.NORTH to 1, Direction.NORTH to 1)
            m.size shouldBe 1
            m[Direction.NORTH].shouldContainExactly(1)
        }

        test("buildEnumSetMultimap") {
            val m = buildEnumSetMultimap<Direction, Int> {
                put(Direction.EAST, 2)
                put(Direction.EAST, 5)
                put(Direction.EAST, 2)
                put(Direction.SOUTH, 3)
            }
            m.size shouldBe 3
            m[Direction.EAST].shouldContainExactlyInAnyOrder(2, 5)
        }

        test("toEnumSetMultimap from Iterable") {
            val m = listOf(Direction.NORTH to 1, Direction.EAST to 2, Direction.NORTH to 3).toEnumSetMultimap()
            m.size shouldBe 3
            m[Direction.NORTH].shouldContainExactlyInAnyOrder(1, 3)
        }

        test("toEnumSetMultimap from Sequence") {
            val m = sequenceOf(Direction.NORTH to 1, Direction.NORTH to 2).toEnumSetMultimap()
            m[Direction.NORTH].shouldContainExactlyInAnyOrder(1, 2)
        }

        test("toEnumSetMultimap from SetMultimap") {
            val source = setMultimapOf(Direction.SOUTH to 10, Direction.NORTH to 1, Direction.SOUTH to 20)
            val m = source.toEnumSetMultimap()
            m.size shouldBe 3
            m[Direction.SOUTH].shouldContainExactlyInAnyOrder(10, 20)
            m[Direction.NORTH].shouldContainExactly(1)
        }

        test("mutableEnumSetMultimapOf empty") {
            val m = mutableEnumSetMultimapOf<Direction, Int>()
            m.isEmpty().shouldBeTrue()
        }

        test("mutableEnumSetMultimapOf with pairs") {
            val m = mutableEnumSetMultimapOf(Direction.NORTH to 1, Direction.NORTH to 2)
            m.size shouldBe 2
        }

        test("toMutableEnumSetMultimap from Iterable") {
            val m = listOf(Direction.NORTH to 1).toMutableEnumSetMultimap()
            m.put(Direction.EAST, 2)
            m.size shouldBe 2
        }

        test("toMutableEnumSetMultimap from SetMultimap") {
            val source = setMultimapOf(Direction.NORTH to 1)
            val m = source.toMutableEnumSetMultimap()
            m.put(Direction.NORTH, 2)
            m[Direction.NORTH].shouldContainExactlyInAnyOrder(1, 2)
        }

        test("empty enum type") {
            val m = emptyEnumSetMultimap<EmptyDirection, String>()
            m.size shouldBe 0
            m.enumEntries.size shouldBe 0
        }
    }

    context("get and containsKey") {
        test("get returns set for present key") {
            val m = enumSetMultimapOf(Direction.NORTH to 10, Direction.NORTH to 20)
            m[Direction.NORTH].shouldContainExactlyInAnyOrder(10, 20)
        }

        test("get returns empty set for absent key") {
            val m = enumSetMultimapOf(Direction.NORTH to 10)
            m[Direction.EAST].shouldBeEmpty()
        }

        test("containsKey true for present key") {
            val m = enumSetMultimapOf(Direction.NORTH to 10)
            m.containsKey(Direction.NORTH).shouldBeTrue()
        }

        test("containsKey false for absent key") {
            val m = enumSetMultimapOf(Direction.NORTH to 10)
            m.containsKey(Direction.EAST).shouldBeFalse()
        }
    }

    context("containsValue and containsEntry") {
        test("containsValue true for present value") {
            val m = enumSetMultimapOf(Direction.NORTH to 10)
            m.containsValue(10).shouldBeTrue()
        }

        test("containsValue false for absent value") {
            val m = enumSetMultimapOf(Direction.NORTH to 10)
            m.containsValue(99).shouldBeFalse()
        }

        test("containsEntry true for present pair") {
            val m = enumSetMultimapOf(Direction.NORTH to 10, Direction.NORTH to 20)
            m.containsEntry(Direction.NORTH, 10).shouldBeTrue()
            m.containsEntry(Direction.NORTH, 20).shouldBeTrue()
        }

        test("containsEntry false for wrong key") {
            val m = enumSetMultimapOf(Direction.NORTH to 10)
            m.containsEntry(Direction.EAST, 10).shouldBeFalse()
        }

        test("containsEntry false for wrong value") {
            val m = enumSetMultimapOf(Direction.NORTH to 10)
            m.containsEntry(Direction.NORTH, 99).shouldBeFalse()
        }
    }

    context("mutation") {
        test("put adds new value") {
            val m = mutableEnumSetMultimapOf(Direction.NORTH to 1)
            m.put(Direction.NORTH, 2).shouldBeTrue()
            m[Direction.NORTH].shouldContainExactlyInAnyOrder(1, 2)
            m.size shouldBe 2
        }

        test("put returns false for duplicate") {
            val m = mutableEnumSetMultimapOf(Direction.NORTH to 1)
            m.put(Direction.NORTH, 1).shouldBeFalse()
            m.size shouldBe 1
        }

        test("put creates new key") {
            val m = mutableEnumSetMultimapOf<Direction, Int>()
            m.put(Direction.NORTH, 1).shouldBeTrue()
            m[Direction.NORTH].shouldContainExactly(1)
            m.size shouldBe 1
        }

        test("putAll with key and values") {
            val m = mutableEnumSetMultimapOf<Direction, Int>()
            m.putAll(Direction.NORTH, listOf(1, 2, 3))
            m[Direction.NORTH].shouldContainExactlyInAnyOrder(1, 2, 3)
            m.size shouldBe 3
        }

        test("putAll with duplicates is deduplicated") {
            val m = mutableEnumSetMultimapOf<Direction, Int>()
            m.putAll(Direction.NORTH, listOf(1, 1, 2, 2))
            m[Direction.NORTH].shouldContainExactlyInAnyOrder(1, 2)
            m.size shouldBe 2
        }

        test("putAll from SetMultimap") {
            val m = mutableEnumSetMultimapOf<Direction, Int>()
            val source = setMultimapOf(Direction.NORTH to 1, Direction.SOUTH to 2)
            m.putAll(source)
            m.size shouldBe 2
            m[Direction.NORTH].shouldContainExactly(1)
            m[Direction.SOUTH].shouldContainExactly(2)
        }

        test("putAll from pairs") {
            val m = mutableEnumSetMultimapOf<Direction, Int>()
            m.putAll(listOf(Direction.NORTH to 1, Direction.NORTH to 2, Direction.EAST to 3))
            m.size shouldBe 3
        }

        test("replaceValues replaces existing") {
            val m = mutableEnumSetMultimapOf(Direction.NORTH to 1, Direction.NORTH to 2)
            val old = m.replaceValues(Direction.NORTH, listOf(10, 20, 30))
            old.shouldContainExactlyInAnyOrder(1, 2)
            m[Direction.NORTH].shouldContainExactlyInAnyOrder(10, 20, 30)
            m.size shouldBe 3
        }

        test("replaceValues with empty removes key") {
            val m = mutableEnumSetMultimapOf(Direction.NORTH to 1, Direction.NORTH to 2)
            val old = m.replaceValues(Direction.NORTH, emptyList())
            old.shouldContainExactlyInAnyOrder(1, 2)
            m.containsKey(Direction.NORTH).shouldBeFalse()
            m.size shouldBe 0
        }

        test("replaceValues for absent key returns empty") {
            val m = mutableEnumSetMultimapOf<Direction, Int>()
            val old = m.replaceValues(Direction.NORTH, listOf(1))
            old.shouldBeEmpty()
            m[Direction.NORTH].shouldContainExactly(1)
            m.size shouldBe 1
        }

        test("remove key returns removed values") {
            val m = mutableEnumSetMultimapOf(Direction.NORTH to 1, Direction.NORTH to 2, Direction.EAST to 3)
            val removed = m.remove(Direction.NORTH)
            removed.shouldContainExactlyInAnyOrder(1, 2)
            m.containsKey(Direction.NORTH).shouldBeFalse()
            m.size shouldBe 1
        }

        test("remove absent key returns empty set") {
            val m = mutableEnumSetMultimapOf<Direction, Int>()
            m.remove(Direction.NORTH).shouldBeEmpty()
        }

        test("remove key-value pair") {
            val m = mutableEnumSetMultimapOf(Direction.NORTH to 1, Direction.NORTH to 2)
            m.remove(Direction.NORTH, 1).shouldBeTrue()
            m[Direction.NORTH].shouldContainExactly(2)
            m.size shouldBe 1
        }

        test("remove key-value returns false when not present") {
            val m = mutableEnumSetMultimapOf(Direction.NORTH to 1)
            m.remove(Direction.NORTH, 99).shouldBeFalse()
            m.size shouldBe 1
        }

        test("remove key-value cleans up empty set") {
            val m = mutableEnumSetMultimapOf(Direction.NORTH to 1)
            m.remove(Direction.NORTH, 1).shouldBeTrue()
            m.containsKey(Direction.NORTH).shouldBeFalse()
            m.size shouldBe 0
        }

        test("clear empties the multimap") {
            val m = mutableEnumSetMultimapOf(Direction.NORTH to 1, Direction.EAST to 2, Direction.SOUTH to 3)
            m.clear()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }
    }

    context("ordering") {
        test("keys iterate in enum declaration order regardless of insertion order") {
            val m = enumSetMultimapOf(Direction.SOUTH to 3, Direction.NORTH to 1, Direction.EAST to 2)
            m.keys.toList().shouldContainExactly(Direction.NORTH, Direction.EAST, Direction.SOUTH)
        }

        test("asMap keys in enum declaration order") {
            val m = enumSetMultimapOf(Direction.SOUTH to 3, Direction.NORTH to 1)
            m.asMap.keys.toList().shouldContainExactly(Direction.NORTH, Direction.SOUTH)
        }
    }

    context("asMap") {
        test("asMap returns EnumMap") {
            val m = enumSetMultimapOf(Direction.NORTH to 1)
            m.asMap.shouldBeInstanceOf<EnumMap<Direction, Set<Int>>>()
        }

        test("asMap is a live view") {
            val m = mutableEnumSetMultimapOf(Direction.NORTH to 1)
            val map = m.asMap
            m.put(Direction.NORTH, 2)
            map[Direction.NORTH]!!.shouldContainExactlyInAnyOrder(1, 2)
        }

        test("asMap skips absent keys") {
            val m = enumSetMultimapOf(Direction.NORTH to 1, Direction.SOUTH to 3)
            m.asMap.size shouldBe 2
            m.asMap.containsKey(Direction.EAST).shouldBeFalse()
        }
    }

    context("equality") {
        test("equal to a SetMultimap with same content") {
            val enum = enumSetMultimapOf(Direction.NORTH to 1, Direction.SOUTH to 2)
            val regular = setMultimapOf(Direction.NORTH to 1, Direction.SOUTH to 2)
            enum shouldBe regular
        }

        test("not equal when different values") {
            val m1 = enumSetMultimapOf(Direction.NORTH to 1)
            val m2 = enumSetMultimapOf(Direction.NORTH to 2)
            m1 shouldNotBe m2
        }

        test("not equal when different keys") {
            val m1 = enumSetMultimapOf(Direction.NORTH to 1)
            val m2 = enumSetMultimapOf(Direction.EAST to 1)
            m1 shouldNotBe m2
        }

        test("empty enum set multimaps are equal") {
            emptyEnumSetMultimap<Direction, Int>() shouldBe emptyEnumSetMultimap<Direction, Int>()
        }

        test("hashCode consistent with asMap") {
            val m = enumSetMultimapOf(Direction.NORTH to 1, Direction.EAST to 2)
            m.hashCode() shouldBe m.asMap.hashCode()
        }

        test("equal multimaps have same hashCode") {
            val m1 = enumSetMultimapOf(Direction.NORTH to 1, Direction.SOUTH to 2)
            val m2 = setMultimapOf(Direction.NORTH to 1, Direction.SOUTH to 2)
            m1.hashCode() shouldBe m2.hashCode()
        }
    }

    context("size tracking") {
        test("size increments on put of new value") {
            val m = mutableEnumSetMultimapOf<Direction, Int>()
            m.put(Direction.NORTH, 1)
            m.size shouldBe 1
            m.put(Direction.NORTH, 2)
            m.size shouldBe 2
        }

        test("size unchanged on duplicate put") {
            val m = mutableEnumSetMultimapOf(Direction.NORTH to 1)
            m.put(Direction.NORTH, 1)
            m.size shouldBe 1
        }

        test("size decrements by set size on remove key") {
            val m = mutableEnumSetMultimapOf(Direction.NORTH to 1, Direction.NORTH to 2, Direction.EAST to 3)
            m.remove(Direction.NORTH)
            m.size shouldBe 1
        }

        test("size resets to zero on clear") {
            val m = mutableEnumSetMultimapOf(Direction.NORTH to 1, Direction.EAST to 2)
            m.clear()
            m.size shouldBe 0
        }
    }
})
