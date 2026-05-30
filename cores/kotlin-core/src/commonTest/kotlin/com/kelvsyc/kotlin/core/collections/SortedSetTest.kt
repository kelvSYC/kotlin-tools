package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class SortedSetTest : FunSpec({

    context("construction") {
        test("emptySortedSet is empty") {
            val s = emptySortedSet(naturalOrder<Int>())
            s.isEmpty().shouldBeTrue()
            s.size shouldBe 0
        }

        test("sortedSetOf with elements maintains sorted order") {
            val s = sortedSetOf(naturalOrder<Int>(), 5, 1, 3, 2, 4)
            s.toList() shouldContainExactly listOf(1, 2, 3, 4, 5)
        }

        test("sortedSetOf with Comparable type uses natural order") {
            val s = sortedSetOf(3, 1, 2)
            s.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("sortedSetOf deduplicates by comparator") {
            val s = sortedSetOf(naturalOrder<Int>(), 1, 2, 2, 3)
            s.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("sortedSetOf with reverse comparator") {
            val s = sortedSetOf(reverseOrder<Int>(), 1, 2, 3)
            s.toList() shouldContainExactly listOf(3, 2, 1)
        }

        test("Iterable.toSortedSet") {
            val s = listOf(5, 3, 1, 4, 2).toSortedSet(naturalOrder())
            s.toList() shouldContainExactly listOf(1, 2, 3, 4, 5)
        }

        test("Sequence.toSortedSet") {
            val s = sequenceOf(5, 3, 1).toSortedSet(naturalOrder())
            s.toList() shouldContainExactly listOf(1, 3, 5)
        }

        test("buildSortedSet") {
            val s = buildSortedSet(naturalOrder<Int>()) {
                add(3); add(1); add(2)
            }
            s.toList() shouldContainExactly listOf(1, 2, 3)
        }
    }

    context("access") {
        test("first and last") {
            val s = sortedSetOf(naturalOrder<Int>(), 3, 1, 2)
            s.first() shouldBe 1
            s.last() shouldBe 3
        }

        test("firstOrNull and lastOrNull on non-empty set") {
            val s = sortedSetOf(naturalOrder<Int>(), 3, 1, 2)
            s.firstOrNull() shouldBe 1
            s.lastOrNull() shouldBe 3
        }

        test("first throws on empty set") {
            shouldThrow<NoSuchElementException> { emptySortedSet(naturalOrder<Int>()).first() }
        }

        test("last throws on empty set") {
            shouldThrow<NoSuchElementException> { emptySortedSet(naturalOrder<Int>()).last() }
        }

        test("firstOrNull is null on empty set") {
            emptySortedSet(naturalOrder<Int>()).firstOrNull().shouldBeNull()
        }

        test("lastOrNull is null on empty set") {
            emptySortedSet(naturalOrder<Int>()).lastOrNull().shouldBeNull()
        }

        test("contains") {
            val s = sortedSetOf(naturalOrder<Int>(), 1, 2, 3)
            s.contains(2).shouldBeTrue()
            s.contains(5).shouldBeFalse()
        }
    }

    context("floor, ceiling, lower, higher") {
        val s = sortedSetOf(naturalOrder<Int>(), 1, 3, 5, 7, 9)

        test("floor returns greatest element <= given") {
            s.floor(5) shouldBe 5
            s.floor(4) shouldBe 3
            s.floor(0).shouldBeNull()
        }

        test("ceiling returns least element >= given") {
            s.ceiling(5) shouldBe 5
            s.ceiling(4) shouldBe 5
            s.ceiling(10).shouldBeNull()
        }

        test("lower returns greatest element < given") {
            s.lower(5) shouldBe 3
            s.lower(1).shouldBeNull()
        }

        test("higher returns least element > given") {
            s.higher(5) shouldBe 7
            s.higher(9).shouldBeNull()
        }
    }

    context("range views — snapshots") {
        val s = sortedSetOf(naturalOrder<Int>(), 1, 2, 3, 4, 5)

        test("headSet exclusive") {
            s.headSet(3, inclusive = false).toList() shouldContainExactly listOf(1, 2)
        }

        test("headSet inclusive") {
            s.headSet(3, inclusive = true).toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("tailSet inclusive") {
            s.tailSet(3, inclusive = true).toList() shouldContainExactly listOf(3, 4, 5)
        }

        test("tailSet exclusive") {
            s.tailSet(3, inclusive = false).toList() shouldContainExactly listOf(4, 5)
        }

        test("subSet inclusive-exclusive") {
            s.subSet(2, true, 4, false).toList() shouldContainExactly listOf(2, 3)
        }

        test("subSet inclusive-inclusive") {
            s.subSet(2, true, 4, true).toList() shouldContainExactly listOf(2, 3, 4)
        }

        test("subSet exclusive-exclusive") {
            s.subSet(2, false, 4, false).toList() shouldContainExactly listOf(3)
        }

        test("headSet is a snapshot — mutation of mutable original does not affect it") {
            val m = mutableSortedSetOf(naturalOrder<Int>(), 1, 2, 3, 4, 5)
            val snap = m.headSet(4, inclusive = false)
            m.add(0)
            snap.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("range view on empty result is empty") {
            s.subSet(2, false, 3, false).shouldBeEmpty()
        }

        test("subSet throws when fromKey > toKey") {
            shouldThrow<IllegalArgumentException> { s.subSet(4, true, 2, true) }
        }
    }

    context("descending") {
        test("descendingSet reverses order") {
            sortedSetOf(naturalOrder<Int>(), 1, 2, 3).descendingSet().toList() shouldContainExactly listOf(3, 2, 1)
        }

        test("descendingSet is a snapshot — mutation of mutable original does not affect it") {
            val m = mutableSortedSetOf(naturalOrder<Int>(), 1, 2, 3)
            val snap = m.descendingSet()
            m.add(4)
            snap.toList() shouldContainExactly listOf(3, 2, 1)
        }
    }

    context("equality and hashCode") {
        test("two sorted sets with same elements are equal") {
            val a = sortedSetOf(naturalOrder<Int>(), 1, 2, 3)
            val b = sortedSetOf(naturalOrder<Int>(), 3, 1, 2)
            a shouldBe b
        }

        test("sorted set equals a plain Set with same elements") {
            val a = sortedSetOf(naturalOrder<Int>(), 1, 2, 3)
            val b: Set<Int> = setOf(3, 1, 2)
            a shouldBe b
        }

        test("hashCode matches a plain Set with same elements") {
            val a = sortedSetOf(naturalOrder<Int>(), 1, 2, 3)
            val b: Set<Int> = setOf(3, 1, 2)
            a.hashCode() shouldBe b.hashCode()
        }
    }

    context("stress") {
        test("1000 random insertions yield sorted iteration") {
            val values = (1..1000).shuffled(kotlin.random.Random(seed = 7L))
            val s = sortedSetOf(naturalOrder<Int>(), *values.toTypedArray())
            s.toList() shouldContainExactly (1..1000).toList()
        }
    }

    context("immutability") {
        test("read-only factory results cannot be cast to MutableSortedSet") {
            shouldThrow<ClassCastException> { emptySortedSet(naturalOrder<Int>()) as MutableSortedSet<Int> }
            shouldThrow<ClassCastException> { sortedSetOf(naturalOrder(), 1, 2, 3) as MutableSortedSet<Int> }
            shouldThrow<ClassCastException> { buildSortedSet(naturalOrder<Int>()) { add(1) } as MutableSortedSet<Int> }
        }
    }
})
