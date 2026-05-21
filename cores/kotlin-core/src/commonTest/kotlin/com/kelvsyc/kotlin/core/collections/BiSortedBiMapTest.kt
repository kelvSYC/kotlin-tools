package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

class BiSortedBiMapTest : FunSpec({

    val alpha = compareBy<String> { it }
    val numeric = compareBy<Int> { it }

    context("construction") {
        test("biSortedBiMapOf with no pairs produces an empty map") {
            biSortedBiMapOf<String, Int>(alpha, numeric).isEmpty().shouldBeTrue()
        }

        test("biSortedBiMapOf exposes both comparators") {
            val m = biSortedBiMapOf<String, Int>(alpha, numeric)
            m.comparator shouldBe alpha
            m.valueComparator shouldBe numeric
        }

        test("biSortedBiMapOf with pairs") {
            val m = biSortedBiMapOf(alpha, numeric, "b" to 2, "a" to 1)
            m.size shouldBe 2
            m["a"] shouldBe 1
        }

        test("biSortedBiMapOf rejects duplicate values") {
            shouldThrow<IllegalArgumentException> {
                biSortedBiMapOf(alpha, numeric, "a" to 1, "b" to 1)
            }
        }

        test("buildBiSortedBiMap") {
            val m = buildBiSortedBiMap<String, Int>(alpha, numeric) {
                put("a", 1)
                put("b", 2)
            }
            m.size shouldBe 2
        }

        test("mutableBiSortedBiMapOf empty") {
            mutableBiSortedBiMapOf<String, Int>(alpha, numeric).isEmpty().shouldBeTrue()
        }

        test("mutableBiSortedBiMapOf with pairs") {
            val m = mutableBiSortedBiMapOf(alpha, numeric, "a" to 1)
            m["a"] shouldBe 1
        }
    }

    context("sorted key order") {
        test("keys iterate in comparator order regardless of insertion order") {
            val m = mutableBiSortedBiMapOf(alpha, numeric, "c" to 3, "a" to 1, "b" to 2)
            m.keys.toList() shouldBe listOf("a", "b", "c")
        }
    }

    context("get and containsKey") {
        test("get returns value for present key") {
            biSortedBiMapOf(alpha, numeric, "a" to 1)["a"] shouldBe 1
        }

        test("get returns null for absent key") {
            biSortedBiMapOf(alpha, numeric, "a" to 1)["z"].shouldBeNull()
        }

        test("containsKey and containsValue") {
            val m = biSortedBiMapOf(alpha, numeric, "a" to 1)
            m.containsKey("a").shouldBeTrue()
            m.containsKey("z").shouldBeFalse()
            m.containsValue(1).shouldBeTrue()
            m.containsValue(99).shouldBeFalse()
        }
    }

    context("put contract") {
        test("put throws when value already exists under different key") {
            val m = mutableBiSortedBiMapOf(alpha, numeric, "a" to 1)
            shouldThrow<IllegalArgumentException> { m.put("b", 1) }
        }
    }

    context("forcePut") {
        test("forcePut displaces conflicting key") {
            val m = mutableBiSortedBiMapOf(alpha, numeric, "a" to 1, "b" to 2)
            m.forcePut("c", 1)
            m.containsKey("a").shouldBeFalse()
            m["c"] shouldBe 1
            m.size shouldBe 2
        }
    }

    context("inverse") {
        test("inverse is a MutableSortedBiMap with value comparator as key comparator") {
            val m = biSortedBiMapOf(alpha, numeric, "a" to 1, "b" to 2)
            m.inverse[1] shouldBe "a"
            m.inverse.comparator shouldBe numeric
        }

        test("inverse keys iterate in value-comparator order") {
            val m = mutableBiSortedBiMapOf(alpha, numeric, "b" to 20, "a" to 10, "c" to 30)
            m.inverse.keys.toList() shouldBe listOf(10, 20, 30)
        }

        test("inverse is a live view") {
            val m = mutableBiSortedBiMapOf(alpha, numeric, "a" to 1)
            m["b"] = 2
            m.inverse.containsKey(2).shouldBeTrue()
        }

        test("mutation through inverse reflected in forward") {
            val m = mutableBiSortedBiMapOf(alpha, numeric, "a" to 1, "b" to 2)
            m.inverse.remove(1)
            m.containsKey("a").shouldBeFalse()
            m.size shouldBe 1
        }
    }

    context("equality") {
        test("equal to regular map with same entries") {
            val bm = biSortedBiMapOf(alpha, numeric, "a" to 1)
            val rm = mapOf("a" to 1)
            bm shouldBe rm
        }

        test("not equal when different values") {
            biSortedBiMapOf(alpha, numeric, "a" to 1) shouldNotBe biSortedBiMapOf(alpha, numeric, "a" to 2)
        }
    }

    context("navigable key methods") {
        val m = biSortedBiMapOf(alpha, numeric, "a" to 10, "b" to 20, "c" to 30)

        test("firstKey and lastKey") {
            m.firstKey() shouldBe "a"
            m.lastKey() shouldBe "c"
        }

        test("floorKey and ceilingKey") {
            m.floorKey("b") shouldBe "b"
            m.ceilingKey("aa") shouldBe "b"
        }

        test("lowerKey and higherKey") {
            m.lowerKey("b") shouldBe "a"
            m.higherKey("b") shouldBe "c"
        }

        test("keys is a SortedSet in comparator order") {
            m.keys.shouldBeInstanceOf<SortedSet<String>>()
            m.keys.toList() shouldBe listOf("a", "b", "c")
        }
    }

    context("range views") {
        val m = biSortedBiMapOf(alpha, numeric, "a" to 10, "b" to 20, "c" to 30, "d" to 40)

        test("headMap returns SortedBiMap snapshot") {
            val h = m.headMap("c", false)
            h.keys.toList() shouldBe listOf("a", "b")
            h.shouldBeInstanceOf<SortedBiMap<String, Int>>()
        }

        test("inverse of headMap is a live-inverse BiMap") {
            val h = m.headMap("b", true)
            h.inverse[10] shouldBe "a"
            h.inverse[20] shouldBe "b"
        }
    }
})
