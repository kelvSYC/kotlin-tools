package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.enums.enumEntries

private enum class Color { RED, GREEN, BLUE }
private enum class Empty

class EnumMapTest : FunSpec({

    context("construction") {
        test("emptyEnumMap produces an empty map") {
            val m = emptyEnumMap<Color, Int>()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }

        test("emptyEnumMap exposes enumEntries") {
            val m = emptyEnumMap<Color, Int>()
            m.enumEntries shouldBe enumEntries<Color>()
        }

        test("enumMapOf with no pairs produces an empty map") {
            enumMapOf<Color, Int>().isEmpty().shouldBeTrue()
        }

        test("enumMapOf with pairs") {
            val m = enumMapOf(Color.RED to 1, Color.BLUE to 3)
            m.size shouldBe 2
            m[Color.RED] shouldBe 1
            m[Color.BLUE] shouldBe 3
        }

        test("enumMapOf last-write-wins for duplicate keys") {
            val m = enumMapOf(Color.RED to 1, Color.RED to 99)
            m.size shouldBe 1
            m[Color.RED] shouldBe 99
        }

        test("buildEnumMap") {
            val m = buildEnumMap<Color, Int> {
                put(Color.GREEN, 2)
                put(Color.BLUE, 3)
            }
            m.size shouldBe 2
            m[Color.GREEN] shouldBe 2
        }

        test("toEnumMap from Map") {
            val m = mapOf(Color.RED to 1, Color.GREEN to 2).toEnumMap()
            m.size shouldBe 2
            m[Color.RED] shouldBe 1
        }

        test("mutableEnumMapOf empty") {
            val m = mutableEnumMapOf<Color, Int>()
            m.isEmpty().shouldBeTrue()
        }

        test("mutableEnumMapOf with pairs") {
            val m = mutableEnumMapOf(Color.RED to 1)
            m.size shouldBe 1
        }

        test("toMutableEnumMap from Map") {
            val m = mapOf(Color.RED to 1).toMutableEnumMap()
            m[Color.RED] shouldBe 1
            m[Color.GREEN] = 2
            m.size shouldBe 2
        }

        test("empty enum type") {
            val m = emptyEnumMap<Empty, String>()
            m.size shouldBe 0
            m.enumEntries.size shouldBe 0
        }
    }

    context("get and containsKey") {
        test("get returns value for present key") {
            val m = enumMapOf(Color.RED to 10)
            m[Color.RED] shouldBe 10
        }

        test("get returns null for absent key") {
            val m = enumMapOf(Color.RED to 10)
            m[Color.GREEN].shouldBeNull()
        }

        test("containsKey true for present key") {
            val m = enumMapOf(Color.RED to 10)
            m.containsKey(Color.RED).shouldBeTrue()
        }

        test("containsKey false for absent key") {
            val m = enumMapOf(Color.RED to 10)
            m.containsKey(Color.GREEN).shouldBeFalse()
        }
    }

    context("containsValue") {
        test("containsValue true for present value") {
            val m = enumMapOf(Color.RED to 10)
            m.containsValue(10).shouldBeTrue()
        }

        test("containsValue false for absent value") {
            val m = enumMapOf(Color.RED to 10)
            m.containsValue(99).shouldBeFalse()
        }
    }

    context("nullable values") {
        test("put and get null value") {
            val m = mutableEnumMapOf<Color, Int?>()
            m[Color.RED] = null
            m.size shouldBe 1
            m.containsKey(Color.RED).shouldBeTrue()
            m[Color.RED].shouldBeNull()
        }

        test("distinguish absent from null") {
            val m = mutableEnumMapOf<Color, Int?>()
            m[Color.RED] = null
            m.containsKey(Color.RED).shouldBeTrue()
            m.containsKey(Color.GREEN).shouldBeFalse()
            m[Color.RED].shouldBeNull()
            m[Color.GREEN].shouldBeNull()
        }

        test("containsValue with null") {
            val m = mutableEnumMapOf<Color, Int?>(Color.RED to null)
            m.containsValue(null).shouldBeTrue()
        }
    }

    context("mutation") {
        test("put returns previous value") {
            val m = mutableEnumMapOf(Color.RED to 1)
            m.put(Color.RED, 2) shouldBe 1
            m[Color.RED] shouldBe 2
        }

        test("put returns null for new key") {
            val m = mutableEnumMapOf<Color, Int>()
            m.put(Color.RED, 1).shouldBeNull()
        }

        test("remove returns previous value") {
            val m = mutableEnumMapOf(Color.RED to 1)
            m.remove(Color.RED) shouldBe 1
            m.containsKey(Color.RED).shouldBeFalse()
            m.size shouldBe 0
        }

        test("remove returns null for absent key") {
            val m = mutableEnumMapOf<Color, Int>()
            m.remove(Color.RED).shouldBeNull()
        }

        test("clear empties the map") {
            val m = mutableEnumMapOf(Color.RED to 1, Color.GREEN to 2, Color.BLUE to 3)
            m.clear()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }

        test("putAll from map") {
            val m = mutableEnumMapOf<Color, Int>()
            m.putAll(mapOf(Color.RED to 1, Color.BLUE to 3))
            m.size shouldBe 2
        }
    }

    context("views") {
        test("keys contains present keys in ordinal order") {
            val m = enumMapOf(Color.BLUE to 3, Color.RED to 1)
            m.keys.toList().shouldContainExactly(Color.RED, Color.BLUE)
        }

        test("values in ordinal order") {
            val m = enumMapOf(Color.BLUE to 3, Color.RED to 1)
            m.values.toList().shouldContainExactly(1, 3)
        }

        test("entries in ordinal order") {
            val m = enumMapOf(Color.BLUE to 3, Color.RED to 1)
            val entries = m.entries.map { it.key to it.value }
            entries.shouldContainExactly(Color.RED to 1, Color.BLUE to 3)
        }

        test("keys view is live-backed") {
            val m = mutableEnumMapOf(Color.RED to 1, Color.GREEN to 2)
            m.keys.remove(Color.RED)
            m.containsKey(Color.RED).shouldBeFalse()
            m.size shouldBe 1
        }

        test("values view is live-backed") {
            val m = mutableEnumMapOf(Color.RED to 1, Color.GREEN to 2)
            val iter = m.values.iterator()
            iter.next()
            iter.remove()
            m.size shouldBe 1
        }

        test("entry setValue mutates backing map") {
            val m = mutableEnumMapOf(Color.RED to 1)
            val entry = m.entries.first()
            entry.setValue(99)
            m[Color.RED] shouldBe 99
        }
    }

    context("equality") {
        test("equal to a regular map with same entries") {
            val enumMap = enumMapOf(Color.RED to 1, Color.GREEN to 2)
            val regularMap = mapOf(Color.RED to 1, Color.GREEN to 2)
            enumMap shouldBe regularMap
            regularMap shouldBe enumMap
        }

        test("not equal when different values") {
            val m1 = enumMapOf(Color.RED to 1)
            val m2 = enumMapOf(Color.RED to 2)
            m1 shouldNotBe m2
        }

        test("not equal when different keys") {
            val m1 = enumMapOf(Color.RED to 1)
            val m2 = enumMapOf(Color.GREEN to 1)
            m1 shouldNotBe m2
        }

        test("empty enum maps are equal") {
            emptyEnumMap<Color, Int>() shouldBe emptyEnumMap<Color, Int>()
        }

        test("hashCode consistent with Map contract") {
            val enumMap = enumMapOf(Color.RED to 1, Color.GREEN to 2)
            val regularMap = mapOf(Color.RED to 1, Color.GREEN to 2)
            enumMap.hashCode() shouldBe regularMap.hashCode()
        }
    }

    context("iterator remove") {
        test("iterator remove via entries") {
            val m = mutableEnumMapOf(Color.RED to 1, Color.GREEN to 2, Color.BLUE to 3)
            val iter = m.entries.iterator()
            iter.next()
            iter.remove()
            m.size shouldBe 2
            m.containsKey(Color.RED).shouldBeFalse()
        }

        test("iterator remove via keys") {
            val m = mutableEnumMapOf(Color.RED to 1, Color.GREEN to 2)
            val iter = m.keys.iterator()
            iter.next()
            iter.remove()
            m.size shouldBe 1
        }
    }

    context("immutability") {
        test("read-only factory results cannot be cast to MutableEnumMap") {
            shouldThrow<ClassCastException> { emptyEnumMap<Color, Int>() as MutableEnumMap<Color, Int> }
            shouldThrow<ClassCastException> { enumMapOf(Color.RED to 1) as MutableEnumMap<Color, Int> }
            shouldThrow<ClassCastException> { buildEnumMap<Color, Int> { put(Color.RED, 1) } as MutableEnumMap<Color, Int> }
        }
    }
})
