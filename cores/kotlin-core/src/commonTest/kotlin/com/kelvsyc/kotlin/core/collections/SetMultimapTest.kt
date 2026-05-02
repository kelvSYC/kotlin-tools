package com.kelvsyc.kotlin.core.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class SetMultimapTest : FunSpec({

    context("construction") {
        test("emptySetMultimap produces an empty multimap") {
            val m = emptySetMultimap<String, Int>()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }

        test("setMultimapOf with no arguments produces an empty multimap") {
            setMultimapOf<String, Int>().isEmpty().shouldBeTrue()
        }

        test("setMultimapOf with a single pair") {
            val m = setMultimapOf("a" to 1)
            m.size shouldBe 1
            m["a"] shouldBe setOf(1)
        }

        test("setMultimapOf deduplicates identical pairs") {
            val m = setMultimapOf("a" to 1, "a" to 1)
            m.size shouldBe 1
            m["a"] shouldBe setOf(1)
        }

        test("setMultimapOf with multiple distinct pairs per key") {
            val m = setMultimapOf("a" to 1, "b" to 2, "a" to 3)
            m.size shouldBe 3
            m["a"] shouldBe setOf(1, 3)
        }

        test("toSetMultimap from Iterable") {
            val m = listOf("a" to 1, "b" to 2, "a" to 3).toSetMultimap()
            m.size shouldBe 3
            m["a"] shouldBe setOf(1, 3)
        }

        test("toSetMultimap from Sequence") {
            val m = sequenceOf("a" to 1, "b" to 2, "a" to 3).toSetMultimap()
            m.size shouldBe 3
            m["a"] shouldBe setOf(1, 3)
        }
    }

    context("duplicate pair discarding") {
        test("identical pairs are collapsed to one") {
            val m = setMultimapOf("a" to 1, "a" to 1, "a" to 1)
            m.size shouldBe 1
        }

        test("distinct values under the same key are all retained") {
            val m = setMultimapOf("a" to 1, "a" to 2, "a" to 3)
            m.size shouldBe 3
            m["a"] shouldBe setOf(1, 2, 3)
        }

        test("size counts only distinct pairs") {
            val m = setMultimapOf("a" to 1, "a" to 2, "b" to 1, "a" to 1)
            m.size shouldBe 3
        }
    }

    context("properties") {
        val m = setMultimapOf("a" to 1, "b" to 2, "a" to 3, "c" to 4)

        test("size is the total number of distinct pairs") {
            m.size shouldBe 4
        }

        test("keys returns the set of distinct keys") {
            m.keys shouldBe setOf("a", "b", "c")
        }

        test("asMap maps each key to its value set") {
            m.asMap shouldBe mapOf("a" to setOf(1, 3), "b" to setOf(2), "c" to setOf(4))
        }

        test("isEmpty returns false for a non-empty multimap") {
            m.isEmpty().shouldBeFalse()
        }

        test("isNotEmpty returns true for a non-empty multimap") {
            m.isNotEmpty().shouldBeTrue()
        }
    }

    context("access") {
        val m = setMultimapOf("a" to 1, "a" to 2, "b" to 3)

        test("get returns the value set for a present key") {
            m["a"] shouldBe setOf(1, 2)
        }

        test("get returns an empty set for an absent key") {
            m["z"] shouldBe emptySet()
        }

        test("containsKey returns true for a present key") {
            m.containsKey("a").shouldBeTrue()
        }

        test("containsKey returns false for an absent key") {
            m.containsKey("z").shouldBeFalse()
        }

        test("contains operator reflects containsKey") {
            ("a" in m).shouldBeTrue()
            ("z" in m).shouldBeFalse()
        }

        test("containsValue returns true when the value is present under any key") {
            m.containsValue(1).shouldBeTrue()
        }

        test("containsValue returns false when the value is absent") {
            m.containsValue(99).shouldBeFalse()
        }

        test("containsEntry returns true for a present key-value pair") {
            m.containsEntry("a", 1).shouldBeTrue()
        }

        test("containsEntry returns false when the value is not associated with the key") {
            m.containsEntry("a", 99).shouldBeFalse()
        }

        test("containsEntry returns false when the key is absent") {
            m.containsEntry("z", 1).shouldBeFalse()
        }
    }

    context("null and empty helpers") {
        test("isNullOrEmpty returns true for null") {
            val m: SetMultimap<String, Int>? = null
            m.isNullOrEmpty().shouldBeTrue()
        }

        test("isNullOrEmpty returns true for an empty multimap") {
            emptySetMultimap<String, Int>().isNullOrEmpty().shouldBeTrue()
        }

        test("orEmpty returns the same multimap when non-null") {
            val m: SetMultimap<String, Int>? = setMultimapOf("a" to 1)
            m.orEmpty().size shouldBe 1
        }

        test("orEmpty returns an empty multimap when null") {
            val m: SetMultimap<String, Int>? = null
            m.orEmpty().isEmpty().shouldBeTrue()
        }
    }

    context("filter operations") {
        val m = setMultimapOf("a" to 1, "a" to 2, "b" to 3, "c" to 4)

        test("filter retains only matching pairs") {
            val result = m.filter { (_, v) -> v > 1 }
            result.asMap shouldBe mapOf("a" to setOf(2), "b" to setOf(3), "c" to setOf(4))
        }

        test("filterKeys retains entries with matching keys") {
            val result = m.filterKeys { it != "b" }
            result.asMap shouldBe mapOf("a" to setOf(1, 2), "c" to setOf(4))
        }

        test("filterValues retains entries with matching values") {
            val result = m.filterValues { it % 2 == 0 }
            result.asMap shouldBe mapOf("a" to setOf(2), "c" to setOf(4))
        }

        test("filterValues omits keys that have no remaining values") {
            val result = setMultimapOf("a" to 1, "b" to 2).filterValues { it > 5 }
            result.isEmpty().shouldBeTrue()
        }
    }

    context("map operations") {
        test("mapValues transforms each value, preserving key structure") {
            val m = setMultimapOf("a" to 1, "a" to 2, "b" to 3)
            val result = m.mapValues { it * 10 }
            result.asMap shouldBe mapOf("a" to setOf(10, 20), "b" to setOf(30))
        }

        test("mapValues deduplicates colliding results under the same key") {
            val m = setMultimapOf("a" to 1, "a" to 2)
            val result = m.mapValues { 0 }
            result["a"] shouldBe setOf(0)
        }

        test("mapKeys transforms each key") {
            val m = setMultimapOf("a" to 1, "b" to 2)
            val result = m.mapKeys { it.uppercase() }
            result.asMap shouldBe mapOf("A" to setOf(1), "B" to setOf(2))
        }

        test("mapKeys merges value sets when keys collide") {
            val m = setMultimapOf("a" to 1, "b" to 2, "c" to 3)
            val result = m.mapKeys { "x" }
            result["x"] shouldBe setOf(1, 2, 3)
        }
    }

    context("plus operator") {
        val m = setMultimapOf("a" to 1, "b" to 2)

        test("plus a new pair adds it") {
            val result = m + ("a" to 3)
            result["a"] shouldBe setOf(1, 3)
        }

        test("plus a duplicate pair leaves the multimap unchanged") {
            val result = m + ("a" to 1)
            result shouldBe m
        }

        test("plus another multimap merges all entries") {
            val other = setMultimapOf("a" to 3, "c" to 4)
            val result = m + other
            result.asMap shouldBe mapOf("a" to setOf(1, 3), "b" to setOf(2), "c" to setOf(4))
        }

        test("plus an iterable of pairs adds all non-duplicate pairs") {
            val result = m + listOf("b" to 3, "c" to 4)
            result.asMap shouldBe mapOf("a" to setOf(1), "b" to setOf(2, 3), "c" to setOf(4))
        }
    }

    context("minus operator") {
        val m = setMultimapOf("a" to 1, "a" to 2, "b" to 3, "c" to 4)

        test("minus a key removes all pairs for that key") {
            val result = m - "a"
            result.asMap shouldBe mapOf("b" to setOf(3), "c" to setOf(4))
        }

        test("minus an iterable of keys removes all pairs for those keys") {
            val result = m - listOf("a", "c")
            result.asMap shouldBe mapOf("b" to setOf(3))
        }

        test("minus an absent key returns an equivalent multimap") {
            (m - "z") shouldBe m
        }
    }

    context("equality and hashCode") {
        test("two multimaps with the same content are equal") {
            setMultimapOf("a" to 1, "b" to 2) shouldBe setMultimapOf("a" to 1, "b" to 2)
        }

        test("multimaps with different values are not equal") {
            setMultimapOf("a" to 1) shouldNotBe setMultimapOf("a" to 2)
        }

        test("duplicate pairs are collapsed so single and duplicate construction are equal") {
            setMultimapOf("a" to 1) shouldBe setMultimapOf("a" to 1, "a" to 1)
        }

        test("equal multimaps have the same hashCode") {
            val a = setMultimapOf("a" to 1, "b" to 2)
            val b = setMultimapOf("a" to 1, "b" to 2)
            a.hashCode() shouldBe b.hashCode()
        }
    }

    context("toString") {
        test("toString expresses the multimap as a map of value sets") {
            val m = setMultimapOf("a" to 1, "b" to 2, "a" to 3)
            m.toString() shouldBe "{a=[1, 3], b=[2]}"
        }

        test("empty multimap toString is an empty map") {
            emptySetMultimap<String, Int>().toString() shouldBe "{}"
        }
    }
})
