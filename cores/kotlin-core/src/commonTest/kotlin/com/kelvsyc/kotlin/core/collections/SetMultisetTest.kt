package com.kelvsyc.kotlin.core.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class SetMultisetTest : FunSpec({

    context("construction") {
        test("emptySetMultiset produces an empty multiset") {
            val m = emptySetMultiset<String>()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }

        test("setMultisetOf with no arguments produces an empty multiset") {
            setMultisetOf<String>().isEmpty().shouldBeTrue()
        }

        test("setMultisetOf with a single element") {
            val m = setMultisetOf("a")
            m.size shouldBe 1
            m.count("a") shouldBe 1
        }

        test("toSetMultiset from Iterable") {
            val m = listOf("a", "b", "a").toSetMultiset()
            m.size shouldBe 3
            m.count("a") shouldBe 2
        }

        test("toSetMultiset from Sequence") {
            val m = sequenceOf("a", "b", "a").toSetMultiset()
            m.size shouldBe 3
            m.count("a") shouldBe 2
        }
    }

    context("properties") {
        val m = setMultisetOf("a", "b", "a", "c")

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
        val m = setMultisetOf("a", "b", "a")

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
            val m: SetMultiset<String>? = null
            m.isNullOrEmpty().shouldBeTrue()
        }

        test("isNullOrEmpty returns true for an empty multiset") {
            emptySetMultiset<String>().isNullOrEmpty().shouldBeTrue()
        }

        test("orEmpty returns the same multiset when non-null") {
            val m: SetMultiset<String>? = setMultisetOf("a")
            m.orEmpty().size shouldBe 1
        }

        test("orEmpty returns an empty multiset when null") {
            val m: SetMultiset<String>? = null
            m.orEmpty().isEmpty().shouldBeTrue()
        }
    }

    context("filter") {
        val m = setMultisetOf("a", "b", "a", "c")

        test("filter retains only matching elements") {
            val result = m.filter { it != "b" }
            result.asMap shouldBe mapOf("a" to 2, "c" to 1)
        }

        test("filter can reduce counts") {
            val m2 = setMultisetOf(1, 2, 3, 2, 1)
            val result = m2.filter { it > 1 }
            result.asMap shouldBe mapOf(2 to 2, 3 to 1)
        }
    }

    context("plus operator") {
        val m = setMultisetOf("a", "b")

        test("plus an element increments its count") {
            val result = m + "a"
            result.count("a") shouldBe 2
            result.size shouldBe 3
        }

        test("plus another multiset adds counts") {
            val other = setMultisetOf("b", "c")
            val result = m + other
            result.asMap shouldBe mapOf("a" to 1, "b" to 2, "c" to 1)
        }

        test("plus an iterable adds counts") {
            val result = m + listOf("c", "a")
            result.asMap shouldBe mapOf("a" to 2, "b" to 1, "c" to 1)
        }
    }

    context("minus operator") {
        val m = setMultisetOf("a", "b", "a", "c")

        test("minus an element decrements its count by one") {
            val result = m - "a"
            result.count("a") shouldBe 1
            result.size shouldBe 3
        }

        test("minus an element with count 1 removes it entirely") {
            val result = m - "b"
            result.contains("b").shouldBeFalse()
        }

        test("minus an absent element returns an equivalent multiset") {
            (m - "z") shouldBe m
        }

        test("minus an iterable decrements one count per occurrence") {
            val result = m - listOf("a", "b")
            result.asMap shouldBe mapOf("a" to 1, "c" to 1)
        }

        test("minus an iterable with duplicates decrements multiple times") {
            val result = m - listOf("a", "a")
            result.asMap shouldBe mapOf("b" to 1, "c" to 1)
        }
    }

    context("equality and hashCode") {
        test("two multisets with the same element counts are equal") {
            (setMultisetOf("a", "b", "a") == setMultisetOf("a", "b", "a")).shouldBeTrue()
        }

        test("multisets with different counts are not equal") {
            setMultisetOf("a", "a") shouldNotBe setMultisetOf("a")
        }

        test("multisets with different elements are not equal") {
            setMultisetOf("a") shouldNotBe setMultisetOf("b")
        }

        test("equal multisets have the same hashCode") {
            val a = setMultisetOf("a", "b", "a")
            val b = setMultisetOf("a", "b", "a")
            a.hashCode() shouldBe b.hashCode()
        }

        test("a SetMultiset and ListMultiset with the same counts are equal") {
            val s = setMultisetOf("a", "b", "a")
            val l = listMultisetOf("a", "b", "a")
            (s == l).shouldBeTrue()
            (l == s).shouldBeTrue()
        }

        test("a SetMultiset and ListMultiset with different insertion orders but same counts are equal") {
            val s = setMultisetOf("a", "b", "a")
            val l = listMultisetOf("a", "a", "b")
            (s == l).shouldBeTrue()
            (l == s).shouldBeTrue()
        }

        test("equal cross-type multisets have the same hashCode") {
            val s = setMultisetOf("a", "b", "a")
            val l = listMultisetOf("a", "a", "b")
            s.hashCode() shouldBe l.hashCode()
        }
    }

    context("toString") {
        test("toString expresses the multiset as a count map") {
            val m = setMultisetOf("a", "b", "a")
            m.toString() shouldBe "{a=2, b=1}"
        }

        test("empty multiset toString is an empty map") {
            emptySetMultiset<String>().toString() shouldBe "{}"
        }
    }
})
