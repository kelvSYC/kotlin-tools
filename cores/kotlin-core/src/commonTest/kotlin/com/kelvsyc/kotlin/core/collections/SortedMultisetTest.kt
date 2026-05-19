package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

class SortedMultisetTest : FunSpec({

    context("construction") {
        test("emptySortedMultiset is empty") {
            val m = emptySortedMultiset(naturalOrder<Int>())
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }

        test("sortedMultisetOf with elements preserves counts in sorted order") {
            val m = sortedMultisetOf(naturalOrder<Int>(), 3, 1, 2, 1)
            m.toList() shouldContainExactly listOf(1, 1, 2, 3)
        }

        test("sortedMultisetOf with Comparable type uses natural order") {
            val m = sortedMultisetOf(3, 1, 2, 1)
            m.toList() shouldContainExactly listOf(1, 1, 2, 3)
        }

        test("sortedMultisetOf with reverse comparator") {
            val m = sortedMultisetOf(reverseOrder<Int>(), 1, 2, 3, 2)
            m.toList() shouldContainExactly listOf(3, 2, 2, 1)
        }

        test("Iterable.toSortedMultiset") {
            val m = listOf(3, 1, 2, 1).toSortedMultiset(naturalOrder())
            m.toList() shouldContainExactly listOf(1, 1, 2, 3)
        }

        test("Sequence.toSortedMultiset") {
            val m = sequenceOf(3, 1, 2, 1).toSortedMultiset(naturalOrder())
            m.toList() shouldContainExactly listOf(1, 1, 2, 3)
        }

        test("buildSortedMultiset") {
            val m = buildSortedMultiset(naturalOrder<Int>()) {
                add(3); add(1); add(1); add(2)
            }
            m.toList() shouldContainExactly listOf(1, 1, 2, 3)
        }
    }

    context("properties") {
        val m = sortedMultisetOf(naturalOrder<Int>(), 3, 1, 2, 1)

        test("size is total occurrences") {
            m.size shouldBe 4
        }

        test("elements returns sorted distinct elements as SortedSet") {
            m.elements.shouldBeInstanceOf<SortedSet<Int>>()
            m.elements.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("asMap returns sorted element-to-count map") {
            m.asMap.shouldBeInstanceOf<SortedMap<Int, Int>>()
            m.asMap shouldBe mapOf(1 to 2, 2 to 1, 3 to 1)
            m.asMap.keys.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("count returns occurrences") {
            m.count(1) shouldBe 2
            m.count(2) shouldBe 1
        }

        test("count returns 0 for absent element") {
            m.count(99) shouldBe 0
        }

        test("isEmpty returns false for non-empty multiset") {
            m.isEmpty().shouldBeFalse()
        }
    }

    context("access") {
        val m = sortedMultisetOf(naturalOrder<Int>(), 1, 2, 2, 3)

        test("contains returns true for present element") {
            m.contains(2).shouldBeTrue()
        }

        test("contains returns false for absent element") {
            m.contains(99).shouldBeFalse()
        }

        test("containsAll returns true when all elements are present") {
            m.containsAll(listOf(1, 2)).shouldBeTrue()
        }

        test("containsAll returns false when any element is absent") {
            m.containsAll(listOf(1, 99)).shouldBeFalse()
        }

        test("containsAll is element-based not count-based") {
            m.containsAll(listOf(2, 2, 2)).shouldBeTrue()
        }
    }

    context("first and last") {
        test("first and last on non-empty multiset") {
            val m = sortedMultisetOf(naturalOrder<Int>(), 3, 1, 2)
            m.first() shouldBe 1
            m.last() shouldBe 3
        }

        test("firstOrNull and lastOrNull on non-empty multiset") {
            val m = sortedMultisetOf(naturalOrder<Int>(), 3, 1, 2)
            m.firstOrNull() shouldBe 1
            m.lastOrNull() shouldBe 3
        }

        test("first throws on empty multiset") {
            shouldThrow<NoSuchElementException> { emptySortedMultiset(naturalOrder<Int>()).first() }
        }

        test("last throws on empty multiset") {
            shouldThrow<NoSuchElementException> { emptySortedMultiset(naturalOrder<Int>()).last() }
        }

        test("firstOrNull is null on empty multiset") {
            emptySortedMultiset(naturalOrder<Int>()).firstOrNull().shouldBeNull()
        }

        test("lastOrNull is null on empty multiset") {
            emptySortedMultiset(naturalOrder<Int>()).lastOrNull().shouldBeNull()
        }
    }

    context("floor, ceiling, lower, higher") {
        val m = sortedMultisetOf(naturalOrder<Int>(), 1, 3, 3, 5, 7, 9)

        test("floor returns greatest element <= given") {
            m.floor(5) shouldBe 5
            m.floor(4) shouldBe 3
            m.floor(0).shouldBeNull()
        }

        test("ceiling returns least element >= given") {
            m.ceiling(5) shouldBe 5
            m.ceiling(4) shouldBe 5
            m.ceiling(10).shouldBeNull()
        }

        test("lower returns greatest element < given") {
            m.lower(5) shouldBe 3
            m.lower(1).shouldBeNull()
        }

        test("higher returns least element > given") {
            m.higher(5) shouldBe 7
            m.higher(9).shouldBeNull()
        }
    }

    context("range views — snapshots") {
        val m = sortedMultisetOf(naturalOrder<Int>(), 1, 2, 2, 3, 4, 5)

        test("headMultiset exclusive") {
            val h = m.headMultiset(3, inclusive = false)
            h.toList() shouldContainExactly listOf(1, 2, 2)
            h.asMap shouldBe mapOf(1 to 1, 2 to 2)
        }

        test("headMultiset inclusive") {
            val h = m.headMultiset(3, inclusive = true)
            h.toList() shouldContainExactly listOf(1, 2, 2, 3)
        }

        test("tailMultiset inclusive") {
            val t = m.tailMultiset(3, inclusive = true)
            t.toList() shouldContainExactly listOf(3, 4, 5)
        }

        test("tailMultiset exclusive") {
            val t = m.tailMultiset(3, inclusive = false)
            t.toList() shouldContainExactly listOf(4, 5)
        }

        test("subMultiset inclusive-exclusive") {
            val s = m.subMultiset(2, true, 4, false)
            s.toList() shouldContainExactly listOf(2, 2, 3)
        }

        test("subMultiset inclusive-inclusive") {
            val s = m.subMultiset(2, true, 4, true)
            s.toList() shouldContainExactly listOf(2, 2, 3, 4)
        }

        test("subMultiset exclusive-exclusive") {
            val s = m.subMultiset(2, false, 4, false)
            s.toList() shouldContainExactly listOf(3)
        }

        test("range view on empty result is empty") {
            m.subMultiset(2, false, 3, false).shouldBeEmpty()
        }

        test("headMultiset is a snapshot — mutations to mutable original do not affect it") {
            val mut = mutableSortedMultisetOf(naturalOrder<Int>(), 1, 2, 3, 4, 5)
            val snap = mut.headMultiset(4, inclusive = false)
            mut.add(0)
            snap.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("subMultiset throws when fromElement > toElement") {
            shouldThrow<IllegalArgumentException> { m.subMultiset(4, true, 2, true) }
        }
    }

    context("descending") {
        test("descendingMultiset reverses order and preserves counts") {
            val m = sortedMultisetOf(naturalOrder<Int>(), 1, 2, 2, 3)
            m.descendingMultiset().toList() shouldContainExactly listOf(3, 2, 2, 1)
        }

        test("descendingMultiset is a snapshot — mutations to mutable original do not affect it") {
            val m = mutableSortedMultisetOf(naturalOrder<Int>(), 1, 2, 3)
            val snap = m.descendingMultiset()
            m.add(4)
            snap.toList() shouldContainExactly listOf(3, 2, 1)
        }
    }

    context("equality and hashCode") {
        test("two sorted multisets with same counts are equal") {
            val a = sortedMultisetOf(naturalOrder<Int>(), 1, 2, 1)
            val b = sortedMultisetOf(naturalOrder<Int>(), 2, 1, 1)
            a shouldBe b
        }

        test("sorted multisets with different counts are not equal") {
            val a = sortedMultisetOf(naturalOrder<Int>(), 1, 1)
            val b = sortedMultisetOf(naturalOrder<Int>(), 1)
            a shouldNotBe b
        }

        test("a SortedMultiset and SetMultiset with same counts are equal") {
            val sorted = sortedMultisetOf(naturalOrder<String>(), "a", "b", "a")
            val unordered = setMultisetOf("a", "b", "a")
            (sorted == unordered).shouldBeTrue()
            (unordered == sorted).shouldBeTrue()
        }

        test("equal multisets have the same hashCode") {
            val a = sortedMultisetOf(naturalOrder<Int>(), 1, 2, 1)
            val b = sortedMultisetOf(naturalOrder<Int>(), 2, 1, 1)
            a.hashCode() shouldBe b.hashCode()
        }
    }

    context("stress") {
        test("1000 random insertions yield sorted iteration with correct counts") {
            val rng = kotlin.random.Random(seed = 42L)
            val values = (1..1000).map { rng.nextInt(100) }
            val m = sortedMultisetOf(naturalOrder<Int>(), *values.toTypedArray())
            val sorted = m.toList()
            for (i in 0 until sorted.size - 1) {
                (sorted[i] <= sorted[i + 1]).shouldBeTrue()
            }
            m.size shouldBe 1000
            val expected = values.groupingBy { it }.eachCount()
            expected.forEach { (e, c) -> m.count(e) shouldBe c }
        }
    }
})
