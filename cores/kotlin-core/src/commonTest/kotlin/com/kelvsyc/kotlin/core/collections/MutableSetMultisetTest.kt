package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class MutableSetMultisetTest : FunSpec({

    context("construction") {
        test("mutableSetMultisetOf with no arguments produces an empty multiset") {
            mutableSetMultisetOf<String>().isEmpty().shouldBeTrue()
        }

        test("mutableSetMultisetOf with elements") {
            val m = mutableSetMultisetOf("a", "b", "a")
            m.size shouldBe 3
            m.count("a") shouldBe 2
        }

        test("toMutableSetMultiset from Iterable") {
            val m = listOf("a", "b", "a").toMutableSetMultiset()
            m.size shouldBe 3
            m.count("a") shouldBe 2
        }

        test("toMutableSetMultiset from Sequence") {
            val m = sequenceOf("a", "b", "a").toMutableSetMultiset()
            m.size shouldBe 3
            m.count("a") shouldBe 2
        }
    }

    context("add(element)") {
        test("add returns true and increments the count") {
            val m = mutableSetMultisetOf<String>()
            m.add("a").shouldBeTrue()
            m.count("a") shouldBe 1
        }

        test("add of a duplicate returns true and increments the count") {
            val m = mutableSetMultisetOf("a")
            m.add("a").shouldBeTrue()
            m.count("a") shouldBe 2
        }
    }

    context("add(element, count)") {
        test("add with count adds multiple occurrences") {
            val m = mutableSetMultisetOf<String>()
            m.add("a", 3)
            m.count("a") shouldBe 3
        }

        test("add with count 0 is a no-op") {
            val m = mutableSetMultisetOf("a")
            m.add("a", 0)
            m.count("a") shouldBe 1
        }

        test("add with negative count throws IllegalArgumentException") {
            val m = mutableSetMultisetOf<String>()
            shouldThrow<IllegalArgumentException> { m.add("a", -1) }
        }
    }

    context("remove(element)") {
        test("remove decrements the count and returns true") {
            val m = mutableSetMultisetOf("a", "a")
            m.remove("a").shouldBeTrue()
            m.count("a") shouldBe 1
        }

        test("remove of the last occurrence removes the element entirely") {
            val m = mutableSetMultisetOf("a")
            m.remove("a").shouldBeTrue()
            m.contains("a").shouldBeFalse()
        }

        test("remove of an absent element returns false") {
            val m = mutableSetMultisetOf<String>()
            m.remove("z").shouldBeFalse()
        }
    }

    context("remove(element, count)") {
        test("remove with count decrements by that amount") {
            val m = mutableSetMultisetOf("a", "a", "a")
            val removed = m.remove("a", 2)
            removed shouldBe 2
            m.count("a") shouldBe 1
        }

        test("remove with count greater than occurrences removes all and returns actual count") {
            val m = mutableSetMultisetOf("a", "a")
            val removed = m.remove("a", 5)
            removed shouldBe 2
            m.contains("a").shouldBeFalse()
        }

        test("remove with count 0 returns 0 and leaves multiset unchanged") {
            val m = mutableSetMultisetOf("a")
            m.remove("a", 0) shouldBe 0
            m.count("a") shouldBe 1
        }

        test("remove with negative count throws IllegalArgumentException") {
            val m = mutableSetMultisetOf("a")
            shouldThrow<IllegalArgumentException> { m.remove("a", -1) }
        }

        test("remove of an absent element returns 0") {
            val m = mutableSetMultisetOf<String>()
            m.remove("z", 1) shouldBe 0
        }
    }

    context("setCount") {
        test("setCount sets the count and returns the previous count") {
            val m = mutableSetMultisetOf("a", "a")
            val old = m.setCount("a", 5)
            old shouldBe 2
            m.count("a") shouldBe 5
        }

        test("setCount to 0 removes the element") {
            val m = mutableSetMultisetOf("a", "a")
            m.setCount("a", 0)
            m.contains("a").shouldBeFalse()
        }

        test("setCount on an absent element returns 0") {
            val m = mutableSetMultisetOf<String>()
            m.setCount("a", 3) shouldBe 0
            m.count("a") shouldBe 3
        }

        test("setCount with negative count throws IllegalArgumentException") {
            val m = mutableSetMultisetOf("a")
            shouldThrow<IllegalArgumentException> { m.setCount("a", -1) }
        }
    }

    context("removeAll and retainAll") {
        test("removeAll removes all occurrences of matching elements") {
            val m = mutableSetMultisetOf("a", "b", "a", "c")
            m.removeAll(listOf("a", "c")).shouldBeTrue()
            m.asMap shouldBe mapOf("b" to 1)
        }

        test("retainAll removes all occurrences of non-matching elements") {
            val m = mutableSetMultisetOf("a", "b", "a", "c")
            m.retainAll(listOf("a")).shouldBeTrue()
            m.asMap shouldBe mapOf("a" to 2)
        }
    }

    context("clear") {
        test("clear removes all entries") {
            val m = mutableSetMultisetOf("a", "b", "a")
            m.clear()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }
    }

    context("interop with SetMultiset read operations") {
        val m = mutableSetMultisetOf("a", "b", "a")

        test("size reports total occurrence count") {
            m.size shouldBe 3
        }

        test("elements returns distinct elements") {
            m.elements shouldBe setOf("a", "b")
        }

        test("asMap reflects current state") {
            m.asMap shouldBe mapOf("a" to 2, "b" to 1)
        }
    }

    context("equality and hashCode") {
        test("two mutable multisets with the same counts are equal") {
            (mutableSetMultisetOf("a", "b", "a") == mutableSetMultisetOf("a", "b", "a")).shouldBeTrue()
        }

        test("a mutable SetMultiset equals an immutable SetMultiset with the same counts") {
            (mutableSetMultisetOf("a", "b") == setMultisetOf("a", "b")).shouldBeTrue()
        }

        test("a mutable SetMultiset equals a ListMultiset with the same counts") {
            (mutableSetMultisetOf("a", "b", "a") == listMultisetOf("a", "a", "b")).shouldBeTrue()
        }

        test("equal multisets have the same hashCode") {
            val a = mutableSetMultisetOf("a", "b", "a")
            val b = setMultisetOf("a", "b", "a")
            a.hashCode() shouldBe b.hashCode()
        }
    }
})
