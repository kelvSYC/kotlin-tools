package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.enums.enumEntries

// Grade = enum keys (for EnumSortedBiMap), Level = enum values (for SortedEnumBiMap)
private enum class Grade { A, B, C }
private enum class Level { LOW, MID, HIGH }

class EnumSortedBiMapTest : FunSpec({

    val numeric = compareBy<Int> { it }
    val alpha = compareBy<String> { it }

    // ── EnumSortedBiMap (enum keys, sorted values) ──────────────────────────────

    context("EnumSortedBiMap construction") {
        test("enumSortedBiMapOf with no pairs produces an empty map") {
            enumSortedBiMapOf<Grade, Int>(numeric).isEmpty().shouldBeTrue()
        }

        test("enumSortedBiMapOf exposes enumEntries and valueComparator") {
            val m = enumSortedBiMapOf<Grade, Int>(numeric)
            m.enumEntries shouldBe enumEntries<Grade>()
            m.valueComparator shouldBe numeric
        }

        test("enumSortedBiMapOf with pairs") {
            val m = enumSortedBiMapOf(numeric, Grade.A to 1, Grade.B to 2)
            m.size shouldBe 2
            m[Grade.A] shouldBe 1
        }

        test("enumSortedBiMapOf rejects duplicate values") {
            shouldThrow<IllegalArgumentException> {
                enumSortedBiMapOf(numeric, Grade.A to 1, Grade.B to 1)
            }
        }

        test("buildEnumSortedBiMap") {
            val m = buildEnumSortedBiMap<Grade, Int>(numeric) {
                put(Grade.A, 1)
                put(Grade.B, 2)
            }
            m.size shouldBe 2
        }

        test("mutableEnumSortedBiMapOf empty") {
            mutableEnumSortedBiMapOf<Grade, Int>(numeric).isEmpty().shouldBeTrue()
        }

        test("mutableEnumSortedBiMapOf with pairs") {
            val m = mutableEnumSortedBiMapOf(numeric, Grade.A to 1)
            m[Grade.A] shouldBe 1
        }
    }

    context("EnumSortedBiMap key ordinal order") {
        test("keys iterate in ordinal order") {
            val m = mutableEnumSortedBiMapOf(numeric, Grade.C to 30, Grade.A to 10)
            m.keys.toList() shouldBe listOf(Grade.A, Grade.C)
        }
    }

    context("EnumSortedBiMap inverse is SortedEnumBiMap") {
        test("inverse maps values to keys") {
            val m = enumSortedBiMapOf(numeric, Grade.A to 1, Grade.B to 2)
            m.inverse[1] shouldBe Grade.A
        }

        test("inverse is a MutableSortedEnumBiMap with correct comparator and valueEnumEntries") {
            val m = enumSortedBiMapOf(numeric, Grade.A to 1)
            m.inverse.comparator shouldBe numeric
            m.inverse.valueEnumEntries shouldBe enumEntries<Grade>()
        }

        test("inverse keys iterate in sorted order") {
            val m = mutableEnumSortedBiMapOf(numeric, Grade.C to 30, Grade.A to 10, Grade.B to 20)
            m.inverse.keys.toList() shouldBe listOf(10, 20, 30)
        }

        test("inverse is a live view") {
            val m = mutableEnumSortedBiMapOf(numeric, Grade.A to 1)
            m[Grade.B] = 2
            m.inverse.containsKey(2).shouldBeTrue()
        }

        test("mutation through inverse reflected in forward") {
            val m = mutableEnumSortedBiMapOf(numeric, Grade.A to 1, Grade.B to 2)
            m.inverse.remove(1)
            m.containsKey(Grade.A).shouldBeFalse()
            m.size shouldBe 1
        }
    }

    context("EnumSortedBiMap put contract") {
        test("put throws when value already exists under different key") {
            val m = mutableEnumSortedBiMapOf(numeric, Grade.A to 1)
            shouldThrow<IllegalArgumentException> { m.put(Grade.B, 1) }
        }

        test("forcePut displaces conflicting key") {
            val m = mutableEnumSortedBiMapOf(numeric, Grade.A to 1, Grade.B to 2)
            m.forcePut(Grade.C, 1)
            m.containsKey(Grade.A).shouldBeFalse()
            m[Grade.C] shouldBe 1
        }
    }

    // ── SortedEnumBiMap (sorted keys, enum values) ──────────────────────────────

    context("SortedEnumBiMap construction") {
        test("sortedEnumBiMapOf with no pairs produces an empty map") {
            sortedEnumBiMapOf<String, Level>(alpha).isEmpty().shouldBeTrue()
        }

        test("sortedEnumBiMapOf exposes comparator and valueEnumEntries") {
            val m = sortedEnumBiMapOf<String, Level>(alpha)
            m.comparator shouldBe alpha
            m.valueEnumEntries shouldBe enumEntries<Level>()
        }

        test("sortedEnumBiMapOf with pairs") {
            val m = sortedEnumBiMapOf(alpha, "x" to Level.LOW, "y" to Level.MID)
            m.size shouldBe 2
            m["x"] shouldBe Level.LOW
        }

        test("sortedEnumBiMapOf rejects duplicate values") {
            shouldThrow<IllegalArgumentException> {
                sortedEnumBiMapOf(alpha, "x" to Level.LOW, "y" to Level.LOW)
            }
        }

        test("mutableSortedEnumBiMapOf empty") {
            mutableSortedEnumBiMapOf<String, Level>(alpha).isEmpty().shouldBeTrue()
        }

        test("mutableSortedEnumBiMapOf with pairs") {
            val m = mutableSortedEnumBiMapOf(alpha, "x" to Level.LOW)
            m["x"] shouldBe Level.LOW
        }
    }

    context("SortedEnumBiMap sorted key order") {
        test("keys iterate in comparator order") {
            val m = mutableSortedEnumBiMapOf(alpha, "z" to Level.HIGH, "a" to Level.LOW)
            m.keys.toList() shouldBe listOf("a", "z")
        }
    }

    context("SortedEnumBiMap inverse is EnumSortedBiMap") {
        test("inverse maps values to keys") {
            val m = sortedEnumBiMapOf(alpha, "x" to Level.LOW, "y" to Level.MID)
            m.inverse[Level.LOW] shouldBe "x"
        }

        test("inverse is a MutableEnumSortedBiMap with correct enumEntries and valueComparator") {
            val m = sortedEnumBiMapOf(alpha, "x" to Level.LOW)
            m.inverse.enumEntries shouldBe enumEntries<Level>()
            m.inverse.valueComparator shouldBe alpha
        }

        test("inverse keys iterate in ordinal order") {
            val m = mutableSortedEnumBiMapOf(alpha, "z" to Level.HIGH, "a" to Level.LOW, "m" to Level.MID)
            m.inverse.keys.toList() shouldBe listOf(Level.LOW, Level.MID, Level.HIGH)
        }

        test("inverse is a live view") {
            val m = mutableSortedEnumBiMapOf(alpha, "x" to Level.LOW)
            m["y"] = Level.MID
            m.inverse.containsKey(Level.MID).shouldBeTrue()
        }

        test("mutation through inverse reflected in forward") {
            val m = mutableSortedEnumBiMapOf(alpha, "x" to Level.LOW, "y" to Level.MID)
            m.inverse.remove(Level.LOW)
            m.containsKey("x").shouldBeFalse()
            m.size shouldBe 1
        }
    }

    context("SortedEnumBiMap put contract") {
        test("put throws when value already exists under different key") {
            val m = mutableSortedEnumBiMapOf(alpha, "x" to Level.LOW)
            shouldThrow<IllegalArgumentException> { m.put("y", Level.LOW) }
        }

        test("forcePut displaces conflicting key") {
            val m = mutableSortedEnumBiMapOf(alpha, "x" to Level.LOW, "y" to Level.MID)
            m.forcePut("z", Level.LOW)
            m.containsKey("x").shouldBeFalse()
            m["z"] shouldBe Level.LOW
        }
    }

    context("equality") {
        test("EnumSortedBiMap equal to regular map") {
            val bm = enumSortedBiMapOf(numeric, Grade.A to 1)
            val rm = mapOf(Grade.A to 1)
            bm shouldBe rm
        }

        test("SortedEnumBiMap equal to regular map") {
            val bm = sortedEnumBiMapOf(alpha, "x" to Level.LOW)
            val rm = mapOf("x" to Level.LOW)
            bm shouldBe rm
        }
    }

    context("immutability") {
        test("EnumSortedBiMap read-only factory results cannot be cast to mutable interface") {
            shouldThrow<ClassCastException> { enumSortedBiMapOf<Grade, Int>(numeric, Grade.A to 1) as MutableEnumSortedBiMap<Grade, Int> }
            shouldThrow<ClassCastException> { buildEnumSortedBiMap<Grade, Int>(numeric) { put(Grade.A, 1) } as MutableEnumSortedBiMap<Grade, Int> }
        }
        test("SortedEnumBiMap read-only factory results cannot be cast to mutable interface") {
            shouldThrow<ClassCastException> { sortedEnumBiMapOf<Int, Level>(numeric, 1 to Level.LOW) as MutableSortedEnumBiMap<Int, Level> }
            shouldThrow<ClassCastException> { buildSortedEnumBiMap<Int, Level>(numeric) { put(1, Level.LOW) } as MutableSortedEnumBiMap<Int, Level> }
        }
    }
})
