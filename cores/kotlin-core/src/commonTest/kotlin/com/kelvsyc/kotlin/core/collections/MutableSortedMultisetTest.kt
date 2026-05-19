package com.kelvsyc.kotlin.core.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class MutableSortedMultisetTest : FunSpec({

    context("construction") {
        test("mutableSortedMultisetOf returns MutableSortedMultiset") {
            val m = mutableSortedMultisetOf(naturalOrder<Int>())
            m.isEmpty().shouldBeTrue()
            m.shouldBeInstanceOf<MutableSortedMultiset<Int>>()
        }

        test("mutableSortedMultisetOf with Comparable type uses natural order") {
            val m = mutableSortedMultisetOf(3, 1, 2, 1)
            m.toList() shouldContainExactly listOf(1, 1, 2, 3)
        }
    }

    context("mutation — single element") {
        test("add inserts in sorted order") {
            val m = mutableSortedMultisetOf(naturalOrder<Int>())
            m.add(3); m.add(1); m.add(1); m.add(2)
            m.toList() shouldContainExactly listOf(1, 1, 2, 3)
        }

        test("add returns true") {
            val m = mutableSortedMultisetOf(naturalOrder<Int>())
            m.add(1).shouldBeTrue()
            m.add(1).shouldBeTrue()
        }

        test("remove decrements count by one") {
            val m = mutableSortedMultisetOf(naturalOrder<Int>(), 1, 1, 2)
            m.remove(1)
            m.count(1) shouldBe 1
            m.toList() shouldContainExactly listOf(1, 2)
        }

        test("remove eliminates element when count reaches zero") {
            val m = mutableSortedMultisetOf(naturalOrder<Int>(), 1, 2)
            m.remove(1)
            m.contains(1) shouldBe false
            m.toList() shouldContainExactly listOf(2)
        }

        test("clear empties the multiset") {
            val m = mutableSortedMultisetOf(naturalOrder<Int>(), 1, 2, 2)
            m.clear()
            m.isEmpty().shouldBeTrue()
        }
    }

    context("mutation — count-based operations") {
        test("add with count increments by given amount") {
            val m = mutableSortedMultisetOf(naturalOrder<Int>())
            m.add(5, 3)
            m.count(5) shouldBe 3
            m.toList() shouldContainExactly listOf(5, 5, 5)
        }

        test("add with count 0 has no effect") {
            val m = mutableSortedMultisetOf(naturalOrder<Int>(), 1)
            m.add(1, 0)
            m.count(1) shouldBe 1
        }

        test("remove with count removes up to that many occurrences") {
            val m = mutableSortedMultisetOf(naturalOrder<Int>(), 1, 1, 1, 2)
            val removed = m.remove(1, 2)
            removed shouldBe 2
            m.count(1) shouldBe 1
        }

        test("remove with count exceeding actual count removes all") {
            val m = mutableSortedMultisetOf(naturalOrder<Int>(), 1, 1, 2)
            val removed = m.remove(1, 10)
            removed shouldBe 2
            m.contains(1) shouldBe false
        }

        test("setCount sets the count and returns the old count") {
            val m = mutableSortedMultisetOf(naturalOrder<Int>(), 1, 1, 2)
            val old = m.setCount(1, 5)
            old shouldBe 2
            m.count(1) shouldBe 5
        }

        test("setCount to zero removes the element") {
            val m = mutableSortedMultisetOf(naturalOrder<Int>(), 1, 2)
            m.setCount(1, 0)
            m.contains(1) shouldBe false
            m.toList() shouldContainExactly listOf(2)
        }
    }

    context("range views return MutableSortedMultiset") {
        val m = mutableSortedMultisetOf(naturalOrder<Int>(), 1, 2, 2, 3, 4, 5)

        test("headMultiset returns MutableSortedMultiset") {
            m.headMultiset(3, inclusive = false).shouldBeInstanceOf<MutableSortedMultiset<Int>>()
        }

        test("tailMultiset returns MutableSortedMultiset") {
            m.tailMultiset(3, inclusive = true).shouldBeInstanceOf<MutableSortedMultiset<Int>>()
        }

        test("subMultiset returns MutableSortedMultiset") {
            m.subMultiset(2, true, 4, false).shouldBeInstanceOf<MutableSortedMultiset<Int>>()
        }

        test("descendingMultiset returns MutableSortedMultiset") {
            m.descendingMultiset().shouldBeInstanceOf<MutableSortedMultiset<Int>>()
        }
    }

    context("snapshot independence") {
        test("mutations to snapshot do not affect original") {
            val m = mutableSortedMultisetOf(naturalOrder<Int>(), 1, 2, 3, 4, 5)
            val snap = m.headMultiset(4, inclusive = false)
            snap.add(0)
            m.toList() shouldContainExactly listOf(1, 2, 3, 4, 5)
        }

        test("mutations to original do not affect snapshot") {
            val m = mutableSortedMultisetOf(naturalOrder<Int>(), 1, 2, 3, 4, 5)
            val snap = m.headMultiset(4, inclusive = false)
            m.add(0)
            snap.toList() shouldContainExactly listOf(1, 2, 3)
        }
    }

    context("stress") {
        test("1000 random add/remove operations preserve sorted iteration") {
            val rng = kotlin.random.Random(seed = 99L)
            val m = mutableSortedMultisetOf(naturalOrder<Int>())
            repeat(1000) {
                val v = rng.nextInt(100)
                if (rng.nextBoolean()) m.add(v) else m.remove(v)
            }
            val sorted = m.toList()
            for (i in 0 until sorted.size - 1) {
                (sorted[i] <= sorted[i + 1]).shouldBeTrue()
            }
        }
    }
})
