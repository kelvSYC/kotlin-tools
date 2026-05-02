package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class MutableListMultisetTest : FunSpec({

    context("construction") {
        test("mutableListMultisetOf with no arguments produces an empty multiset") {
            mutableListMultisetOf<String>().isEmpty().shouldBeTrue()
        }

        test("mutableListMultisetOf with elements") {
            val m = mutableListMultisetOf("a", "b", "a")
            m.size shouldBe 3
            m.count("a") shouldBe 2
        }

        test("toMutableListMultiset from Iterable") {
            val m = listOf("a", "b", "a").toMutableListMultiset()
            m.size shouldBe 3
            m.toList() shouldBe listOf("a", "b", "a")
        }

        test("toMutableListMultiset from Sequence") {
            val m = sequenceOf("a", "b", "a").toMutableListMultiset()
            m.size shouldBe 3
            m.toList() shouldBe listOf("a", "b", "a")
        }
    }

    context("add single") {
        test("add appends a new occurrence at the end") {
            val m = mutableListMultisetOf("a", "b")
            m.add("a")
            m.toList() shouldBe listOf("a", "b", "a")
        }

        test("add returns true") {
            mutableListMultisetOf<String>().add("a").shouldBeTrue()
        }

        test("add increments the count") {
            val m = mutableListMultisetOf("a")
            m.add("a")
            m.count("a") shouldBe 2
        }
    }

    context("add count") {
        test("add with count appends occurrences at the end in order") {
            val m = mutableListMultisetOf("a", "b")
            m.add("a", 2)
            m.toList() shouldBe listOf("a", "b", "a", "a")
        }

        test("add with count 0 is a no-op") {
            val m = mutableListMultisetOf("a")
            m.add("b", 0)
            m.contains("b").shouldBeFalse()
        }

        test("add with negative count throws IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> { mutableListMultisetOf<String>().add("a", -1) }
        }
    }

    context("addAll") {
        test("addAll adds all elements in iteration order") {
            val m = mutableListMultisetOf("a")
            m.addAll(listOf("b", "a"))
            m.toList() shouldBe listOf("a", "b", "a")
        }

        test("addAll returns false for an empty collection") {
            mutableListMultisetOf("a").addAll(emptyList<String>()).shouldBeFalse()
        }

        test("addAll returns true for a non-empty collection") {
            mutableListMultisetOf<String>().addAll(listOf("a")).shouldBeTrue()
        }
    }

    context("remove single") {
        test("remove removes the first occurrence FIFO") {
            val m = mutableListMultisetOf("a", "b", "a")
            m.remove("a")
            m.toList() shouldBe listOf("b", "a")
        }

        test("remove returns true when the element was present") {
            mutableListMultisetOf("a").remove("a").shouldBeTrue()
        }

        test("remove returns false when the element is absent") {
            mutableListMultisetOf("a").remove("z").shouldBeFalse()
        }

        test("remove of the last occurrence removes the element entirely") {
            val m = mutableListMultisetOf("a")
            m.remove("a")
            m.contains("a").shouldBeFalse()
            m.isEmpty().shouldBeTrue()
        }
    }

    context("remove count") {
        test("remove with count removes the first N occurrences FIFO") {
            val m = mutableListMultisetOf("a", "b", "a", "c", "a")
            m.remove("a", 2)
            m.toList() shouldBe listOf("b", "c", "a")
        }

        test("remove with count greater than actual removes all and returns actual count") {
            val m = mutableListMultisetOf("a", "b", "a")
            val removed = m.remove("a", 5)
            removed shouldBe 2
            m.contains("a").shouldBeFalse()
        }

        test("remove with count returns number of occurrences actually removed") {
            val m = mutableListMultisetOf("a", "a", "a")
            m.remove("a", 2) shouldBe 2
            m.count("a") shouldBe 1
        }

        test("remove with count 0 is a no-op and returns 0") {
            val m = mutableListMultisetOf("a")
            m.remove("a", 0) shouldBe 0
            m.count("a") shouldBe 1
        }

        test("remove with negative count throws IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> { mutableListMultisetOf("a").remove("a", -1) }
        }

        test("remove on an absent element returns 0") {
            mutableListMultisetOf("a").remove("z", 2) shouldBe 0
        }
    }

    context("setCount") {
        test("setCount increasing appends occurrences at the end") {
            val m = mutableListMultisetOf("a", "b")
            m.setCount("a", 3)
            m.toList() shouldBe listOf("a", "b", "a", "a")
        }

        test("setCount decreasing removes the earliest occurrences FIFO") {
            val m = mutableListMultisetOf("a", "b", "a", "c", "a")
            m.setCount("a", 1)
            m.toList() shouldBe listOf("b", "c", "a")
        }

        test("setCount to 0 removes the element entirely") {
            val m = mutableListMultisetOf("a", "b", "a")
            m.setCount("a", 0)
            m.contains("a").shouldBeFalse()
            m.toList() shouldBe listOf("b")
        }

        test("setCount returns the previous count") {
            val m = mutableListMultisetOf("a", "a", "b")
            m.setCount("a", 1) shouldBe 2
        }

        test("setCount on an absent element returns 0 and appends occurrences") {
            val m = mutableListMultisetOf("b")
            m.setCount("a", 2) shouldBe 0
            m.toList() shouldBe listOf("b", "a", "a")
        }

        test("setCount with negative count throws IllegalArgumentException") {
            shouldThrow<IllegalArgumentException> { mutableListMultisetOf("a").setCount("a", -1) }
        }
    }

    context("removeAll") {
        test("removeAll removes all occurrences of matching elements") {
            val m = mutableListMultisetOf("a", "b", "a", "c")
            m.removeAll(listOf("a", "c"))
            m.toList() shouldBe listOf("b")
        }

        test("removeAll returns true if any element was removed") {
            mutableListMultisetOf("a", "b").removeAll(listOf("a")).shouldBeTrue()
        }

        test("removeAll returns false if no element was removed") {
            mutableListMultisetOf("a", "b").removeAll(listOf("z")).shouldBeFalse()
        }
    }

    context("retainAll") {
        test("retainAll keeps only elements in the collection, all occurrences") {
            val m = mutableListMultisetOf("a", "b", "a", "c")
            m.retainAll(listOf("a", "c"))
            m.toList() shouldBe listOf("a", "a", "c")
        }

        test("retainAll returns true if any element was removed") {
            mutableListMultisetOf("a", "b").retainAll(listOf("a")).shouldBeTrue()
        }

        test("retainAll returns false if nothing changed") {
            mutableListMultisetOf("a", "b").retainAll(listOf("a", "b")).shouldBeFalse()
        }
    }

    context("clear") {
        test("clear removes all elements") {
            val m = mutableListMultisetOf("a", "b", "a")
            m.clear()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }
    }

    context("interop with read operations") {
        val m = mutableListMultisetOf("a", "b", "a")

        test("size reports total occurrence count") {
            m.size shouldBe 3
        }

        test("elements returns distinct elements") {
            m.elements shouldBe setOf("a", "b")
        }

        test("toList returns elements in insertion order") {
            m.toList() shouldBe listOf("a", "b", "a")
        }

        test("asMap reflects current counts") {
            m.asMap shouldBe mapOf("a" to 2, "b" to 1)
        }

        test("count returns current occurrence count") {
            m.count("a") shouldBe 2
        }

        test("contains") {
            m.contains("a").shouldBeTrue()
            m.contains("z").shouldBeFalse()
        }
    }

    context("equality and hashCode") {
        test("two mutable multisets with the same counts are equal") {
            mutableListMultisetOf("a", "b", "a") shouldBe mutableListMultisetOf("a", "b", "a")
        }

        test("a mutable multiset equals an immutable multiset with the same counts") {
            mutableListMultisetOf("a", "b", "a") shouldBe listMultisetOf("a", "b", "a")
        }

        test("equal multisets have the same hashCode") {
            val a = mutableListMultisetOf("a", "b")
            val b = listMultisetOf("a", "b")
            a.hashCode() shouldBe b.hashCode()
        }
    }
})
