package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.enums.enumEntries

private enum class Planet { MERCURY, VENUS, EARTH }


class EnumBiMapTest : FunSpec({

    context("construction") {
        test("emptyEnumBiMap produces an empty map") {
            val m = emptyEnumBiMap<Planet, Int>()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }

        test("emptyEnumBiMap exposes enumEntries") {
            emptyEnumBiMap<Planet, Int>().enumEntries shouldBe enumEntries<Planet>()
        }

        test("enumBiMapOf with no pairs produces an empty map") {
            enumBiMapOf<Planet, Int>().isEmpty().shouldBeTrue()
        }

        test("enumBiMapOf with pairs") {
            val m = enumBiMapOf(Planet.MERCURY to 1, Planet.EARTH to 3)
            m.size shouldBe 2
            m[Planet.MERCURY] shouldBe 1
            m[Planet.EARTH] shouldBe 3
        }

        test("enumBiMapOf rejects duplicate values") {
            shouldThrow<IllegalArgumentException> {
                enumBiMapOf(Planet.MERCURY to 1, Planet.VENUS to 1)
            }
        }

        test("buildEnumBiMap") {
            val m = buildEnumBiMap<Planet, Int> {
                put(Planet.VENUS, 2)
                put(Planet.EARTH, 3)
            }
            m.size shouldBe 2
            m[Planet.VENUS] shouldBe 2
        }

        test("toEnumBiMap from Map") {
            val m = mapOf(Planet.MERCURY to 1, Planet.EARTH to 3).toEnumBiMap()
            m.size shouldBe 2
            m[Planet.MERCURY] shouldBe 1
        }

        test("toEnumBiMap rejects duplicate values") {
            shouldThrow<IllegalArgumentException> {
                mapOf(Planet.MERCURY to 1, Planet.VENUS to 1).toEnumBiMap()
            }
        }

        test("mutableEnumBiMapOf empty") {
            mutableEnumBiMapOf<Planet, Int>().isEmpty().shouldBeTrue()
        }

        test("mutableEnumBiMapOf with pairs") {
            val m = mutableEnumBiMapOf(Planet.MERCURY to 1)
            m.size shouldBe 1
            m[Planet.MERCURY] shouldBe 1
        }

        test("buildMutableEnumBiMap") {
            val m = buildMutableEnumBiMap<Planet, Int> {
                put(Planet.MERCURY, 1)
                forcePut(Planet.VENUS, 2)
            }
            m.size shouldBe 2
        }

        test("toMutableEnumBiMap from Map") {
            val m = mapOf(Planet.MERCURY to 1).toMutableEnumBiMap()
            m[Planet.VENUS] = 2
            m.size shouldBe 2
        }
    }

    context("enumEntries") {
        test("returns the full enum entries") {
            mutableEnumBiMapOf<Planet, Int>().enumEntries shouldBe enumEntries<Planet>()
        }
    }

    context("ordinal iteration order") {
        test("keys iterate in ordinal order regardless of insertion order") {
            val m = mutableEnumBiMapOf(Planet.EARTH to 3, Planet.MERCURY to 1, Planet.VENUS to 2)
            m.keys.toList().shouldContainExactly(Planet.MERCURY, Planet.VENUS, Planet.EARTH)
        }

        test("entries iterate in ordinal order") {
            val m = enumBiMapOf(Planet.EARTH to 3, Planet.MERCURY to 1)
            m.entries.map { it.key }.shouldContainExactly(Planet.MERCURY, Planet.EARTH)
        }
    }

    context("get and containsKey") {
        test("get returns value for present key") {
            enumBiMapOf(Planet.MERCURY to 1)[Planet.MERCURY] shouldBe 1
        }

        test("get returns null for absent key") {
            enumBiMapOf(Planet.MERCURY to 1)[Planet.VENUS].shouldBeNull()
        }

        test("containsKey true for present key") {
            enumBiMapOf(Planet.MERCURY to 1).containsKey(Planet.MERCURY).shouldBeTrue()
        }

        test("containsKey false for absent key") {
            enumBiMapOf(Planet.MERCURY to 1).containsKey(Planet.VENUS).shouldBeFalse()
        }

        test("containsValue true for present value") {
            enumBiMapOf(Planet.MERCURY to 1).containsValue(1).shouldBeTrue()
        }

        test("containsValue false for absent value") {
            enumBiMapOf(Planet.MERCURY to 1).containsValue(99).shouldBeFalse()
        }
    }

    context("put contract") {
        test("put returns null for new key") {
            mutableEnumBiMapOf<Planet, Int>().put(Planet.MERCURY, 1).shouldBeNull()
        }

        test("put returns previous value for existing key") {
            val m = mutableEnumBiMapOf(Planet.MERCURY to 1)
            m.put(Planet.MERCURY, 2) shouldBe 1
            m[Planet.MERCURY] shouldBe 2
        }

        test("put throws when value already exists under different key") {
            val m = mutableEnumBiMapOf(Planet.MERCURY to 1)
            shouldThrow<IllegalArgumentException> { m.put(Planet.VENUS, 1) }
        }

        test("put with same key and same value is a no-op") {
            val m = mutableEnumBiMapOf(Planet.MERCURY to 1)
            m.put(Planet.MERCURY, 1) shouldBe 1
            m.size shouldBe 1
        }
    }

    context("forcePut") {
        test("forcePut inserts new entry") {
            val m = mutableEnumBiMapOf<Planet, Int>()
            m.forcePut(Planet.MERCURY, 1).shouldBeNull()
            m[Planet.MERCURY] shouldBe 1
        }

        test("forcePut displaces conflicting key") {
            val m = mutableEnumBiMapOf(Planet.MERCURY to 1, Planet.VENUS to 2)
            m.forcePut(Planet.EARTH, 1)
            m.containsKey(Planet.MERCURY).shouldBeFalse()
            m[Planet.EARTH] shouldBe 1
            m.size shouldBe 2
        }

        test("forcePut same key and value is a no-op") {
            val m = mutableEnumBiMapOf(Planet.MERCURY to 1)
            m.forcePut(Planet.MERCURY, 1) shouldBe 1
            m.size shouldBe 1
        }
    }

    context("remove") {
        test("remove returns previous value") {
            val m = mutableEnumBiMapOf(Planet.MERCURY to 1)
            m.remove(Planet.MERCURY) shouldBe 1
            m.containsKey(Planet.MERCURY).shouldBeFalse()
        }

        test("remove returns null for absent key") {
            mutableEnumBiMapOf<Planet, Int>().remove(Planet.MERCURY).shouldBeNull()
        }

        test("remove keeps inverse consistent") {
            val m = mutableEnumBiMapOf(Planet.MERCURY to 1, Planet.VENUS to 2)
            m.remove(Planet.MERCURY)
            m.inverse.containsKey(1).shouldBeFalse()
            m.inverse.containsKey(2).shouldBeTrue()
        }
    }

    context("clear") {
        test("clear empties both forward and inverse") {
            val m = mutableEnumBiMapOf(Planet.MERCURY to 1, Planet.VENUS to 2)
            m.clear()
            m.isEmpty().shouldBeTrue()
            m.inverse.isEmpty().shouldBeTrue()
        }
    }

    context("inverse") {
        test("inverse maps values to keys") {
            val m = enumBiMapOf(Planet.MERCURY to 1, Planet.VENUS to 2)
            m.inverse[1] shouldBe Planet.MERCURY
            m.inverse[2] shouldBe Planet.VENUS
        }

        test("inverse is a live view of mutations") {
            val m = mutableEnumBiMapOf(Planet.MERCURY to 1)
            m[Planet.VENUS] = 2
            m.inverse.containsKey(2).shouldBeTrue()
        }

        test("mutation through inverse is reflected in forward") {
            val m = mutableEnumBiMapOf(Planet.MERCURY to 1, Planet.VENUS to 2)
            m.inverse.remove(1)
            m.containsKey(Planet.MERCURY).shouldBeFalse()
            m.size shouldBe 1
        }
    }

    context("equality") {
        test("equal to regular map with same entries") {
            val bm = enumBiMapOf(Planet.MERCURY to 1, Planet.VENUS to 2)
            val rm = mapOf(Planet.MERCURY to 1, Planet.VENUS to 2)
            bm shouldBe rm
            rm shouldBe bm
        }

        test("not equal when different values") {
            enumBiMapOf(Planet.MERCURY to 1) shouldNotBe enumBiMapOf(Planet.MERCURY to 2)
        }

        test("hashCode consistent with Map contract") {
            val bm = enumBiMapOf(Planet.MERCURY to 1, Planet.VENUS to 2)
            val rm = mapOf(Planet.MERCURY to 1, Planet.VENUS to 2)
            bm.hashCode() shouldBe rm.hashCode()
        }
    }

    context("views") {
        test("keys in ordinal order") {
            val m = enumBiMapOf(Planet.EARTH to 3, Planet.MERCURY to 1)
            m.keys.toList().shouldContainExactly(Planet.MERCURY, Planet.EARTH)
        }

        test("values contains all values") {
            val m = enumBiMapOf(Planet.MERCURY to 1, Planet.VENUS to 2)
            m.values.shouldContainExactlyInAnyOrder(1, 2)
        }
    }

    context("immutability") {
        test("read-only factory results cannot be cast to MutableEnumBiMap") {
            shouldThrow<ClassCastException> { emptyEnumBiMap<Planet, Int>() as MutableEnumBiMap<Planet, Int> }
            shouldThrow<ClassCastException> { enumBiMapOf(Planet.EARTH to 1) as MutableEnumBiMap<Planet, Int> }
            shouldThrow<ClassCastException> { buildEnumBiMap<Planet, Int> { put(Planet.EARTH, 1) } as MutableEnumBiMap<Planet, Int> }
        }
    }
})
