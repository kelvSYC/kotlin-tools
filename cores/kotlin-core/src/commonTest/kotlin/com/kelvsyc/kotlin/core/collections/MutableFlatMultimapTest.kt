package com.kelvsyc.kotlin.core.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class MutableFlatMultimapTest : FunSpec({

    context("construction") {
        test("mutableFlatMultimapOf with no arguments produces an empty multimap") {
            mutableFlatMultimapOf<String, Int>().isEmpty().shouldBeTrue()
        }

        test("mutableFlatMultimapOf with pairs") {
            val m = mutableFlatMultimapOf("a" to 1, "b" to 2, "a" to 3)
            m.size shouldBe 3
            m["a"] shouldBe listOf(1, 3)
        }

        test("toMutableFlatMultimap from Iterable") {
            val m = listOf("a" to 1, "b" to 2, "a" to 3).toMutableFlatMultimap()
            m.size shouldBe 3
            m["a"] shouldBe listOf(1, 3)
        }

        test("toMutableFlatMultimap from Sequence") {
            val m = sequenceOf("a" to 1, "b" to 2, "a" to 3).toMutableFlatMultimap()
            m.size shouldBe 3
            m["a"] shouldBe listOf(1, 3)
        }
    }

    context("put") {
        test("put adds a new key-value pair") {
            val m = mutableFlatMultimapOf<String, Int>()
            m.put("a", 1)
            m["a"] shouldBe listOf(1)
        }

        test("put appends duplicates rather than replacing") {
            val m = mutableFlatMultimapOf("a" to 1)
            m.put("a", 1)
            m["a"] shouldBe listOf(1, 1)
        }

        test("put preserves overall insertion order") {
            val m = mutableFlatMultimapOf<String, Int>()
            m.put("a", 3)
            m.put("b", 1)
            m.put("a", 2)
            m.entries.toList() shouldBe listOf("a" to 3, "b" to 1, "a" to 2)
        }

        test("put preserves key first-occurrence order") {
            val m = mutableFlatMultimapOf<String, Int>()
            m.put("b", 1)
            m.put("a", 2)
            m.put("b", 3)
            m.keys.toList() shouldBe listOf("b", "a")
        }
    }

    context("putAll key+values") {
        test("putAll adds multiple values under a key") {
            val m = mutableFlatMultimapOf("a" to 1)
            m.putAll("a", listOf(2, 3))
            m["a"] shouldBe listOf(1, 2, 3)
        }

        test("putAll with empty iterable is a no-op") {
            val m = mutableFlatMultimapOf("a" to 1)
            m.putAll("b", emptyList())
            m.containsKey("b").shouldBeFalse()
        }
    }

    context("putAll from FlatMultimap") {
        test("putAll merges all entries from another multimap") {
            val m = mutableFlatMultimapOf("a" to 1)
            val other = flatMultimapOf("a" to 2, "b" to 3)
            m.putAll(other)
            m["a"] shouldBe listOf(1, 2)
            m["b"] shouldBe listOf(3)
        }
    }

    context("putAll from pairs") {
        test("putAll adds all pairs in iteration order") {
            val m = mutableFlatMultimapOf<String, Int>()
            m.putAll(listOf("a" to 1, "b" to 2, "a" to 3))
            m.entries.toList() shouldBe listOf("a" to 1, "b" to 2, "a" to 3)
        }
    }

    context("replaceValues") {
        test("replaceValues replaces all values for a key and returns the old values") {
            val m = mutableFlatMultimapOf("a" to 1, "a" to 2, "b" to 3)
            val old = m.replaceValues("a", listOf(10, 20))
            old shouldBe listOf(1, 2)
            m["a"] shouldBe listOf(10, 20)
            m["b"] shouldBe listOf(3)
        }

        test("replaceValues with empty values removes the key and returns old values") {
            val m = mutableFlatMultimapOf("a" to 1, "a" to 2)
            val old = m.replaceValues("a", emptyList())
            old shouldBe listOf(1, 2)
            m.containsKey("a").shouldBeFalse()
        }

        test("replaceValues for an absent key returns empty list") {
            val m = mutableFlatMultimapOf<String, Int>()
            m.replaceValues("z", listOf(1, 2)) shouldBe emptyList()
            m["z"] shouldBe listOf(1, 2)
        }
    }

    context("remove(key)") {
        test("remove removes all values for the key and returns them") {
            val m = mutableFlatMultimapOf("a" to 1, "a" to 2, "b" to 3)
            val removed = m.remove("a")
            removed shouldBe listOf(1, 2)
            m.containsKey("a").shouldBeFalse()
            m["b"] shouldBe listOf(3)
        }

        test("remove on an absent key returns empty list and leaves multimap unchanged") {
            val m = mutableFlatMultimapOf("a" to 1)
            m.remove("z") shouldBe emptyList()
            m.size shouldBe 1
        }
    }

    context("remove(key, value)") {
        test("remove removes one occurrence of the pair and returns true") {
            val m = mutableFlatMultimapOf("a" to 1, "a" to 2)
            m.remove("a", 1).shouldBeTrue()
            m["a"] shouldBe listOf(2)
        }

        test("remove on an absent value returns false") {
            val m = mutableFlatMultimapOf("a" to 1)
            m.remove("a", 99).shouldBeFalse()
            m["a"] shouldBe listOf(1)
        }

        test("remove on an absent key returns false") {
            val m = mutableFlatMultimapOf("a" to 1)
            m.remove("z", 1).shouldBeFalse()
        }

        test("remove removes only one occurrence when duplicates exist") {
            val m = mutableFlatMultimapOf("a" to 1, "a" to 1, "a" to 2)
            m.remove("a", 1).shouldBeTrue()
            m["a"] shouldBe listOf(1, 2)
        }

        test("remove of the last value for a key removes the key entirely") {
            val m = mutableFlatMultimapOf("a" to 1)
            m.remove("a", 1).shouldBeTrue()
            m.containsKey("a").shouldBeFalse()
            m.isEmpty().shouldBeTrue()
        }
    }

    context("clear") {
        test("clear removes all entries") {
            val m = mutableFlatMultimapOf("a" to 1, "b" to 2, "a" to 3)
            m.clear()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }
    }

    context("equality and hashCode") {
        test("two mutable multimaps with the same content are equal") {
            mutableFlatMultimapOf("a" to 1, "b" to 2) shouldBe mutableFlatMultimapOf("a" to 1, "b" to 2)
        }

        test("a mutable multimap equals an immutable multimap with the same content") {
            mutableFlatMultimapOf("a" to 1, "b" to 2) shouldBe flatMultimapOf("a" to 1, "b" to 2)
        }

        test("equal multimaps have the same hashCode") {
            val a = mutableFlatMultimapOf("a" to 1, "b" to 2)
            val b = flatMultimapOf("a" to 1, "b" to 2)
            a.hashCode() shouldBe b.hashCode()
        }
    }
})
