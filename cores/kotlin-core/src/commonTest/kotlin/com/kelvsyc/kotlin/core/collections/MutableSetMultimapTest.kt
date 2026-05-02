package com.kelvsyc.kotlin.core.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class MutableSetMultimapTest : FunSpec({

    context("construction") {
        test("mutableSetMultimapOf with no arguments produces an empty multimap") {
            mutableSetMultimapOf<String, Int>().isEmpty().shouldBeTrue()
        }

        test("mutableSetMultimapOf with pairs") {
            val m = mutableSetMultimapOf("a" to 1, "b" to 2, "a" to 3)
            m.size shouldBe 3
            m["a"] shouldBe setOf(1, 3)
        }

        test("mutableSetMultimapOf deduplicates identical pairs") {
            val m = mutableSetMultimapOf("a" to 1, "a" to 1)
            m.size shouldBe 1
        }

        test("toMutableSetMultimap from Iterable") {
            val m = listOf("a" to 1, "b" to 2, "a" to 3).toMutableSetMultimap()
            m.size shouldBe 3
            m["a"] shouldBe setOf(1, 3)
        }

        test("toMutableSetMultimap from Sequence") {
            val m = sequenceOf("a" to 1, "b" to 2, "a" to 3).toMutableSetMultimap()
            m.size shouldBe 3
            m["a"] shouldBe setOf(1, 3)
        }
    }

    context("put") {
        test("put returns true when a new pair is added") {
            val m = mutableSetMultimapOf<String, Int>()
            m.put("a", 1).shouldBeTrue()
            m["a"] shouldBe setOf(1)
        }

        test("put returns false when the pair already exists") {
            val m = mutableSetMultimapOf("a" to 1)
            m.put("a", 1).shouldBeFalse()
            m["a"] shouldBe setOf(1)
        }

        test("put adds a new value to an existing key") {
            val m = mutableSetMultimapOf("a" to 1)
            m.put("a", 2).shouldBeTrue()
            m["a"] shouldBe setOf(1, 2)
        }

        test("put preserves key insertion order") {
            val m = mutableSetMultimapOf<String, Int>()
            m.put("b", 1)
            m.put("a", 2)
            m.keys.toList() shouldBe listOf("b", "a")
        }
    }

    context("putAll key+values") {
        test("putAll adds multiple values under a key") {
            val m = mutableSetMultimapOf("a" to 1)
            m.putAll("a", listOf(2, 3))
            m["a"] shouldBe setOf(1, 2, 3)
        }

        test("putAll with empty iterable is a no-op") {
            val m = mutableSetMultimapOf("a" to 1)
            m.putAll("b", emptyList())
            m.containsKey("b").shouldBeFalse()
        }

        test("putAll skips values already present under the key") {
            val m = mutableSetMultimapOf("a" to 1)
            m.putAll("a", listOf(1, 2))
            m["a"] shouldBe setOf(1, 2)
        }
    }

    context("putAll from SetMultimap") {
        test("putAll merges all entries from another multimap") {
            val m = mutableSetMultimapOf("a" to 1)
            val other = setMultimapOf("a" to 2, "b" to 3)
            m.putAll(other)
            m["a"] shouldBe setOf(1, 2)
            m["b"] shouldBe setOf(3)
        }
    }

    context("putAll from pairs") {
        test("putAll adds all distinct pairs in iteration order") {
            val m = mutableSetMultimapOf<String, Int>()
            m.putAll(listOf("a" to 1, "b" to 2, "a" to 3))
            m.asMap shouldBe mapOf("a" to setOf(1, 3), "b" to setOf(2))
        }
    }

    context("replaceValues") {
        test("replaceValues replaces all values for a key and returns the old set") {
            val m = mutableSetMultimapOf("a" to 1, "a" to 2, "b" to 3)
            val old = m.replaceValues("a", listOf(10, 20))
            old shouldBe setOf(1, 2)
            m["a"] shouldBe setOf(10, 20)
            m["b"] shouldBe setOf(3)
        }

        test("replaceValues with empty values removes the key and returns old set") {
            val m = mutableSetMultimapOf("a" to 1, "a" to 2)
            val old = m.replaceValues("a", emptyList())
            old shouldBe setOf(1, 2)
            m.containsKey("a").shouldBeFalse()
        }

        test("replaceValues deduplicates new values") {
            val m = mutableSetMultimapOf("a" to 1)
            m.replaceValues("a", listOf(2, 2, 3))
            m["a"] shouldBe setOf(2, 3)
        }

        test("replaceValues for an absent key returns empty set") {
            val m = mutableSetMultimapOf<String, Int>()
            m.replaceValues("z", listOf(1, 2)) shouldBe emptySet()
            m["z"] shouldBe setOf(1, 2)
        }
    }

    context("remove(key)") {
        test("remove removes all values for the key and returns them") {
            val m = mutableSetMultimapOf("a" to 1, "a" to 2, "b" to 3)
            val removed = m.remove("a")
            removed shouldBe setOf(1, 2)
            m.containsKey("a").shouldBeFalse()
            m["b"] shouldBe setOf(3)
        }

        test("remove on an absent key returns empty set and leaves multimap unchanged") {
            val m = mutableSetMultimapOf("a" to 1)
            m.remove("z") shouldBe emptySet()
            m.size shouldBe 1
        }
    }

    context("remove(key, value)") {
        test("remove removes the pair and returns true") {
            val m = mutableSetMultimapOf("a" to 1, "a" to 2)
            m.remove("a", 1).shouldBeTrue()
            m["a"] shouldBe setOf(2)
        }

        test("remove on an absent value returns false") {
            val m = mutableSetMultimapOf("a" to 1)
            m.remove("a", 99).shouldBeFalse()
            m["a"] shouldBe setOf(1)
        }

        test("remove on an absent key returns false") {
            val m = mutableSetMultimapOf("a" to 1)
            m.remove("z", 1).shouldBeFalse()
        }

        test("remove of the last value for a key removes the key entirely") {
            val m = mutableSetMultimapOf("a" to 1)
            m.remove("a", 1).shouldBeTrue()
            m.containsKey("a").shouldBeFalse()
            m.isEmpty().shouldBeTrue()
        }
    }

    context("clear") {
        test("clear removes all entries") {
            val m = mutableSetMultimapOf("a" to 1, "b" to 2, "a" to 3)
            m.clear()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }
    }

    context("interop with SetMultimap read operations") {
        val m = mutableSetMultimapOf("a" to 1, "a" to 2, "b" to 3)

        test("size reports total distinct pair count") {
            m.size shouldBe 3
        }

        test("keys returns distinct keys") {
            m.keys shouldBe setOf("a", "b")
        }

        test("asMap reflects current state") {
            m.asMap shouldBe mapOf("a" to setOf(1, 2), "b" to setOf(3))
        }

        test("containsKey") {
            m.containsKey("a").shouldBeTrue()
            m.containsKey("z").shouldBeFalse()
        }

        test("containsValue") {
            m.containsValue(2).shouldBeTrue()
            m.containsValue(99).shouldBeFalse()
        }

        test("containsEntry") {
            m.containsEntry("a", 1).shouldBeTrue()
            m.containsEntry("a", 99).shouldBeFalse()
        }
    }

    context("equality and hashCode") {
        test("two mutable multimaps with the same content are equal") {
            mutableSetMultimapOf("a" to 1, "b" to 2) shouldBe mutableSetMultimapOf("a" to 1, "b" to 2)
        }

        test("a mutable multimap equals an immutable multimap with the same content") {
            mutableSetMultimapOf("a" to 1, "b" to 2) shouldBe setMultimapOf("a" to 1, "b" to 2)
        }

        test("equal multimaps have the same hashCode") {
            val a = mutableSetMultimapOf("a" to 1, "b" to 2)
            val b = setMultimapOf("a" to 1, "b" to 2)
            a.hashCode() shouldBe b.hashCode()
        }
    }
})
