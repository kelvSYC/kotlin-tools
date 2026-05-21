package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class SortedBiMapTest : FunSpec({

    val alpha = compareBy<String> { it }

    context("construction") {
        test("sortedBiMapOf with no pairs produces an empty map") {
            sortedBiMapOf<String, Int>(alpha).isEmpty().shouldBeTrue()
        }

        test("sortedBiMapOf exposes comparator") {
            sortedBiMapOf<String, Int>(alpha).comparator shouldBe alpha
        }

        test("sortedBiMapOf with pairs") {
            val m = sortedBiMapOf(alpha, "b" to 2, "a" to 1)
            m.size shouldBe 2
            m["a"] shouldBe 1
            m["b"] shouldBe 2
        }

        test("sortedBiMapOf rejects duplicate values") {
            shouldThrow<IllegalArgumentException> {
                sortedBiMapOf(alpha, "a" to 1, "b" to 1)
            }
        }

        test("buildSortedBiMap") {
            val m = buildSortedBiMap<String, Int>(alpha) {
                put("a", 1)
                put("b", 2)
            }
            m.size shouldBe 2
            m["a"] shouldBe 1
        }

        test("toSortedBiMap from Map") {
            val m = mapOf("a" to 1, "b" to 2).toSortedBiMap(alpha)
            m.size shouldBe 2
            m["a"] shouldBe 1
        }

        test("toSortedBiMap rejects duplicate values") {
            shouldThrow<IllegalArgumentException> {
                mapOf("a" to 1, "b" to 1).toSortedBiMap(alpha)
            }
        }

        test("mutableSortedBiMapOf empty") {
            mutableSortedBiMapOf<String, Int>(alpha).isEmpty().shouldBeTrue()
        }

        test("mutableSortedBiMapOf with pairs") {
            val m = mutableSortedBiMapOf(alpha, "a" to 1)
            m["a"] shouldBe 1
        }

        test("buildMutableSortedBiMap") {
            val m = buildMutableSortedBiMap<String, Int>(alpha) {
                put("a", 1)
                forcePut("b", 2)
            }
            m.size shouldBe 2
        }

        test("toMutableSortedBiMap from Map") {
            val m = mapOf("a" to 1).toMutableSortedBiMap(alpha)
            m["b"] = 2
            m.size shouldBe 2
        }
    }

    context("comparator") {
        test("comparator is returned as declared") {
            mutableSortedBiMapOf<String, Int>(alpha).comparator shouldBe alpha
        }
    }

    context("sorted key order") {
        test("keys iterate in comparator order regardless of insertion order") {
            val m = mutableSortedBiMapOf(alpha, "c" to 3, "a" to 1, "b" to 2)
            m.keys.toList() shouldBe listOf("a", "b", "c")
        }
    }

    context("get and containsKey") {
        test("get returns value for present key") {
            sortedBiMapOf(alpha, "a" to 1)["a"] shouldBe 1
        }

        test("get returns null for absent key") {
            sortedBiMapOf(alpha, "a" to 1)["z"].shouldBeNull()
        }

        test("containsKey true for present key") {
            sortedBiMapOf(alpha, "a" to 1).containsKey("a").shouldBeTrue()
        }

        test("containsKey false for absent key") {
            sortedBiMapOf(alpha, "a" to 1).containsKey("z").shouldBeFalse()
        }

        test("containsValue true for present value") {
            sortedBiMapOf(alpha, "a" to 1).containsValue(1).shouldBeTrue()
        }

        test("containsValue false for absent value") {
            sortedBiMapOf(alpha, "a" to 1).containsValue(99).shouldBeFalse()
        }
    }

    context("put contract") {
        test("put returns null for new key") {
            mutableSortedBiMapOf<String, Int>(alpha).put("a", 1).shouldBeNull()
        }

        test("put returns previous value for existing key") {
            val m = mutableSortedBiMapOf(alpha, "a" to 1)
            m.put("a", 2) shouldBe 1
            m["a"] shouldBe 2
        }

        test("put throws when value already exists under different key") {
            val m = mutableSortedBiMapOf(alpha, "a" to 1)
            shouldThrow<IllegalArgumentException> { m.put("b", 1) }
        }

        test("put with same key and same value is a no-op") {
            val m = mutableSortedBiMapOf(alpha, "a" to 1)
            m.put("a", 1) shouldBe 1
            m.size shouldBe 1
        }
    }

    context("forcePut") {
        test("forcePut displaces conflicting key") {
            val m = mutableSortedBiMapOf(alpha, "a" to 1, "b" to 2)
            m.forcePut("c", 1)
            m.containsKey("a").shouldBeFalse()
            m["c"] shouldBe 1
            m.size shouldBe 2
        }
    }

    context("remove") {
        test("remove returns previous value and keeps inverse consistent") {
            val m = mutableSortedBiMapOf(alpha, "a" to 1, "b" to 2)
            m.remove("a") shouldBe 1
            m.inverse.containsKey(1).shouldBeFalse()
            m.inverse.containsKey(2).shouldBeTrue()
        }
    }

    context("clear") {
        test("clear empties both directions") {
            val m = mutableSortedBiMapOf(alpha, "a" to 1, "b" to 2)
            m.clear()
            m.isEmpty().shouldBeTrue()
            m.inverse.isEmpty().shouldBeTrue()
        }
    }

    context("inverse") {
        test("inverse maps values to keys") {
            val m = sortedBiMapOf(alpha, "a" to 1, "b" to 2)
            m.inverse[1] shouldBe "a"
        }

        test("inverse is a live view") {
            val m = mutableSortedBiMapOf(alpha, "a" to 1)
            m["b"] = 2
            m.inverse.containsKey(2).shouldBeTrue()
        }

        test("mutation through inverse reflected in forward") {
            val m = mutableSortedBiMapOf(alpha, "a" to 1, "b" to 2)
            m.inverse.remove(1)
            m.containsKey("a").shouldBeFalse()
            m.size shouldBe 1
        }
    }

    context("equality") {
        test("equal to regular map with same entries") {
            val bm = sortedBiMapOf(alpha, "a" to 1, "b" to 2)
            val rm = mapOf("a" to 1, "b" to 2)
            bm shouldBe rm
            rm shouldBe bm
        }

        test("not equal when different values") {
            sortedBiMapOf(alpha, "a" to 1) shouldNotBe sortedBiMapOf(alpha, "a" to 2)
        }

        test("hashCode consistent with Map contract") {
            val bm = sortedBiMapOf(alpha, "a" to 1, "b" to 2)
            val rm = mapOf("a" to 1, "b" to 2)
            bm.hashCode() shouldBe rm.hashCode()
        }
    }
})
