package com.kelvsyc.kotlin.core.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ListMultisetTest : FunSpec({

    context("construction") {
        test("emptyListMultiset produces an empty multiset") {
            val m = emptyListMultiset<String>()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }

        test("listMultisetOf with no arguments produces an empty multiset") {
            listMultisetOf<String>().isEmpty().shouldBeTrue()
        }

        test("listMultisetOf with a single element") {
            val m = listMultisetOf("a")
            m.size shouldBe 1
            m.count("a") shouldBe 1
        }

        test("toListMultiset from Iterable") {
            val m = listOf("a", "b", "a").toListMultiset()
            m.size shouldBe 3
            m.count("a") shouldBe 2
        }

        test("toListMultiset from Sequence") {
            val m = sequenceOf("a", "b", "a").toListMultiset()
            m.size shouldBe 3
            m.count("a") shouldBe 2
        }
    }

    context("duplicate element preservation") {
        test("duplicate elements are preserved") {
            val m = listMultisetOf("a", "a")
            m.size shouldBe 2
            m.count("a") shouldBe 2
        }

        test("size counts all occurrences including duplicates") {
            val m = listMultisetOf("a", "b", "a", "a")
            m.size shouldBe 4
        }

        test("size is consistent with sum of counts") {
            val m = listMultisetOf("a", "b", "a", "c")
            m.size shouldBe m.asMap.values.sum()
        }
    }

    context("insertion order") {
        test("elements are iterated in overall insertion order") {
            val m = listMultisetOf("a", "b", "a", "c")
            m.toList() shouldBe listOf("a", "b", "a", "c")
        }

        test("elements property reflects distinct elements in first-occurrence order") {
            val m = listMultisetOf("b", "a", "c", "a")
            m.elements.toList() shouldBe listOf("b", "a", "c")
        }
    }

    context("properties") {
        val m = listMultisetOf("a", "b", "a", "c")

        test("size is the total number of occurrences") {
            m.size shouldBe 4
        }

        test("elements returns the set of distinct elements") {
            m.elements shouldBe setOf("a", "b", "c")
        }

        test("asMap maps each element to its count") {
            m.asMap shouldBe mapOf("a" to 2, "b" to 1, "c" to 1)
        }

        test("count returns the number of occurrences") {
            m.count("a") shouldBe 2
        }

        test("count returns 0 for an absent element") {
            m.count("z") shouldBe 0
        }

        test("isEmpty returns false for a non-empty multiset") {
            m.isEmpty().shouldBeFalse()
        }

        test("isNotEmpty returns true for a non-empty multiset") {
            m.isNotEmpty().shouldBeTrue()
        }
    }

    context("access") {
        val m = listMultisetOf("a", "b", "a")

        test("contains returns true for a present element") {
            m.contains("a").shouldBeTrue()
        }

        test("contains returns false for an absent element") {
            m.contains("z").shouldBeFalse()
        }

        test("in operator reflects contains") {
            ("a" in m).shouldBeTrue()
            ("z" in m).shouldBeFalse()
        }

        test("containsAll returns true when all elements are present") {
            m.containsAll(listOf("a", "b")).shouldBeTrue()
        }

        test("containsAll returns false when any element is absent") {
            m.containsAll(listOf("a", "z")).shouldBeFalse()
        }

        test("containsAll is element-based not count-based") {
            m.containsAll(listOf("a", "a")).shouldBeTrue()
        }
    }

    context("null and empty helpers") {
        test("isNullOrEmpty returns true for null") {
            val m: ListMultiset<String>? = null
            m.isNullOrEmpty().shouldBeTrue()
        }

        test("isNullOrEmpty returns true for an empty multiset") {
            emptyListMultiset<String>().isNullOrEmpty().shouldBeTrue()
        }

        test("orEmpty returns the same multiset when non-null") {
            val m: ListMultiset<String>? = listMultisetOf("a")
            m.orEmpty().size shouldBe 1
        }

        test("orEmpty returns an empty multiset when null") {
            val m: ListMultiset<String>? = null
            m.orEmpty().isEmpty().shouldBeTrue()
        }
    }

    context("filter") {
        val m = listMultisetOf("a", "b", "a", "c")

        test("filter retains only matching elements in insertion order") {
            val result = m.filter { it != "b" }
            result.toList() shouldBe listOf("a", "a", "c")
        }

        test("filter can reduce counts") {
            val m2 = listMultisetOf(1, 2, 3, 2, 1)
            val result = m2.filter { it > 1 }
            result.toList() shouldBe listOf(2, 3, 2)
        }
    }

    context("plus operator") {
        val m = listMultisetOf("a", "b")

        test("plus an element appends it at the end") {
            val result = m + "a"
            result.toList() shouldBe listOf("a", "b", "a")
        }

        test("plus another multiset appends all its elements in insertion order") {
            val other = listMultisetOf("b", "c")
            val result = m + other
            result.toList() shouldBe listOf("a", "b", "b", "c")
        }

        test("plus an iterable appends its elements in iteration order") {
            val result = m + listOf("c", "a")
            result.toList() shouldBe listOf("a", "b", "c", "a")
        }
    }

    context("minus operator") {
        val m = listMultisetOf("a", "b", "a", "c")

        test("minus an element removes the first occurrence FIFO") {
            val result = m - "a"
            result.toList() shouldBe listOf("b", "a", "c")
        }

        test("minus an absent element returns an equivalent multiset") {
            (m - "z") shouldBe m
        }

        test("minus an iterable removes one occurrence of each element FIFO") {
            val result = m - listOf("a", "b")
            result.toList() shouldBe listOf("a", "c")
        }

        test("minus an iterable with duplicates removes multiple occurrences") {
            val result = m - listOf("a", "a")
            result.toList() shouldBe listOf("b", "c")
        }
    }

    context("equality and hashCode") {
        test("two multisets with the same element counts are equal") {
            listMultisetOf("a", "b", "a") shouldBe listMultisetOf("a", "b", "a")
        }

        test("multisets with the same counts but different insertion orders are equal") {
            (listMultisetOf("a", "b", "a") == listMultisetOf("a", "a", "b")).shouldBeTrue()
        }

        test("multisets with different counts are not equal") {
            listMultisetOf("a", "a") shouldNotBe listMultisetOf("a")
        }

        test("multisets with different elements are not equal") {
            listMultisetOf("a") shouldNotBe listMultisetOf("b")
        }

        test("equal multisets have the same hashCode") {
            val a = listMultisetOf("a", "b", "a")
            val b = listMultisetOf("a", "a", "b")
            a.hashCode() shouldBe b.hashCode()
        }
    }

    context("toString") {
        test("toString expresses the multiset as an ordered list of elements") {
            listMultisetOf("a", "b", "a").toString() shouldBe "[a, b, a]"
        }

        test("empty multiset toString is an empty list") {
            emptyListMultiset<String>().toString() shouldBe "[]"
        }
    }
})
