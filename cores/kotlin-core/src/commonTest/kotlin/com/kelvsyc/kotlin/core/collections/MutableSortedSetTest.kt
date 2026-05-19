package com.kelvsyc.kotlin.core.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class MutableSortedSetTest : FunSpec({

    context("construction") {
        test("mutableSortedSetOf creates an empty mutable set") {
            val s = mutableSortedSetOf(naturalOrder<Int>())
            s.isEmpty().shouldBeTrue()
            s.shouldBeInstanceOf<MutableSortedSet<Int>>()
        }

        test("mutableSortedSetOf with Comparable type uses natural order") {
            val s = mutableSortedSetOf(3, 1, 2)
            s.toList() shouldContainExactly listOf(1, 2, 3)
        }
    }

    context("mutation") {
        test("add inserts in sorted order") {
            val s = mutableSortedSetOf(naturalOrder<Int>())
            s.add(3).shouldBeTrue()
            s.add(1).shouldBeTrue()
            s.add(2).shouldBeTrue()
            s.toList() shouldContainExactly listOf(1, 2, 3)
        }

        test("add returns false for duplicate") {
            val s = mutableSortedSetOf(naturalOrder<Int>(), 1, 2, 3)
            s.add(2).shouldBeFalse()
            s.size shouldBe 3
        }

        test("remove deletes element and returns true") {
            val s = mutableSortedSetOf(naturalOrder<Int>(), 1, 2, 3)
            s.remove(2).shouldBeTrue()
            s.toList() shouldContainExactly listOf(1, 3)
        }

        test("remove returns false for absent element") {
            val s = mutableSortedSetOf(naturalOrder<Int>(), 1, 2, 3)
            s.remove(99).shouldBeFalse()
        }

        test("clear empties the set") {
            val s = mutableSortedSetOf(naturalOrder<Int>(), 1, 2, 3)
            s.clear()
            s.isEmpty().shouldBeTrue()
        }

        test("addAll maintains sorted order") {
            val s = mutableSortedSetOf(naturalOrder<Int>())
            s.addAll(listOf(5, 3, 1, 4, 2))
            s.toList() shouldContainExactly listOf(1, 2, 3, 4, 5)
        }

        test("sorting is maintained after interleaved adds and removes") {
            val s = mutableSortedSetOf(naturalOrder<Int>())
            s.add(5); s.add(3); s.add(7)
            s.remove(3)
            s.add(1); s.add(6)
            s.toList() shouldContainExactly listOf(1, 5, 6, 7)
        }
    }

    context("keys type") {
        test("range views on MutableSortedSet return MutableSortedSet") {
            val s = mutableSortedSetOf(naturalOrder<Int>(), 1, 2, 3, 4, 5)
            s.headSet(4, inclusive = false).shouldBeInstanceOf<MutableSortedSet<Int>>()
            s.tailSet(2, inclusive = true).shouldBeInstanceOf<MutableSortedSet<Int>>()
            s.descendingSet().shouldBeInstanceOf<MutableSortedSet<Int>>()
        }

        test("mutations to mutable range snapshot do not affect original") {
            val s = mutableSortedSetOf(naturalOrder<Int>(), 1, 2, 3, 4, 5)
            val snap = s.headSet(4, inclusive = false)
            snap.add(0)
            s.toList() shouldContainExactly listOf(1, 2, 3, 4, 5)
        }
    }

    context("stress") {
        test("1000 random add/remove operations preserve sorted order") {
            val rng = kotlin.random.Random(seed = 13L)
            val s = mutableSortedSetOf(naturalOrder<Int>())
            val mirror = mutableSortedSetOf(naturalOrder<Int>())
            repeat(1000) {
                val v = rng.nextInt(200)
                if (rng.nextBoolean()) { s.add(v); mirror.add(v) }
                else { s.remove(v); mirror.remove(v) }
            }
            s.toList() shouldContainExactly mirror.toList()
        }
    }
})
