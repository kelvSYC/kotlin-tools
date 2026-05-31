package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.random.Random

class PriorityDequeTest : FunSpec({

    context("empty state") {
        test("isEmpty is true on fresh deque") {
            val pd = minMaxPriorityDequeOf<Int>()
            pd.isEmpty().shouldBeTrue()
            pd.isNotEmpty().shouldBeFalse()
        }

        test("size is 0 on fresh deque") {
            minMaxPriorityDequeOf<Int>().size shouldBe 0
        }

        test("peekMin returns null on empty deque") {
            minMaxPriorityDequeOf<Int>().peekMin().shouldBeNull()
        }

        test("peekMax returns null on empty deque") {
            minMaxPriorityDequeOf<Int>().peekMax().shouldBeNull()
        }

        test("pollMin returns null on empty deque") {
            minMaxPriorityDequeOf<Int>().pollMin().shouldBeNull()
        }

        test("pollMax returns null on empty deque") {
            minMaxPriorityDequeOf<Int>().pollMax().shouldBeNull()
        }

        test("contains returns false on empty deque") {
            minMaxPriorityDequeOf<Int>().contains(1).shouldBeFalse()
        }
    }

    context("single element") {
        test("peekMin and peekMax return the same element") {
            val pd = minMaxPriorityDequeOf<Int>()
            pd.add(42)
            pd.peekMin() shouldBe 42
            pd.peekMax() shouldBe 42
        }

        test("pollMin empties the deque") {
            val pd = minMaxPriorityDequeOf<Int>()
            pd.add(42)
            pd.pollMin() shouldBe 42
            pd.isEmpty().shouldBeTrue()
        }

        test("pollMax empties the deque") {
            val pd = minMaxPriorityDequeOf<Int>()
            pd.add(42)
            pd.pollMax() shouldBe 42
            pd.isEmpty().shouldBeTrue()
        }
    }

    context("two elements") {
        test("peekMin returns the lesser element") {
            val pd = minMaxPriorityDequeOf(3, 7)
            pd.peekMin() shouldBe 3
        }

        test("peekMax returns the greater element") {
            val pd = minMaxPriorityDequeOf(3, 7)
            pd.peekMax() shouldBe 7
        }

        test("pollMin removes the lesser element") {
            val pd = minMaxPriorityDequeOf(3, 7)
            pd.pollMin() shouldBe 3
            pd.size shouldBe 1
            pd.peekMin() shouldBe 7
        }

        test("pollMax removes the greater element") {
            val pd = minMaxPriorityDequeOf(3, 7)
            pd.pollMax() shouldBe 7
            pd.size shouldBe 1
            pd.peekMax() shouldBe 3
        }
    }

    context("construction") {
        test("minMaxPriorityDequeOf with elements is heap-ordered") {
            val pd = minMaxPriorityDequeOf(5, 1, 4, 2, 3)
            pd.size shouldBe 5
            pd.peekMin() shouldBe 1
            pd.peekMax() shouldBe 5
        }

        test("priorityDequeOf with custom comparator") {
            val byLength = compareBy<String> { it.length }
            val pd = priorityDequeOf(byLength, "ccc", "a", "bb")
            pd.peekMin() shouldBe "a"
            pd.peekMax() shouldBe "ccc"
        }

        test("toPriorityDeque from Iterable") {
            val pd = listOf(3, 1, 2).toPriorityDeque(naturalOrder())
            pd.size shouldBe 3
            pd.peekMin() shouldBe 1
            pd.peekMax() shouldBe 3
        }

        test("toPriorityDeque from Sequence") {
            val pd = sequenceOf(3, 1, 2).toPriorityDeque(naturalOrder())
            pd.size shouldBe 3
            pd.peekMin() shouldBe 1
            pd.peekMax() shouldBe 3
        }
    }

    context("pollMin ordering") {
        test("pollMin extracts elements in ascending order") {
            val pd = minMaxPriorityDequeOf(5, 1, 4, 2, 3)
            val extracted = buildList { repeat(5) { add(pd.pollMin()!!) } }
            extracted shouldContainExactly listOf(1, 2, 3, 4, 5)
        }

        test("pollMin interleaved with add maintains invariant") {
            val pd = minMaxPriorityDequeOf<Int>()
            pd.add(5); pd.add(3)
            pd.pollMin() shouldBe 3
            pd.add(1)
            pd.pollMin() shouldBe 1
            pd.peekMin() shouldBe 5
        }
    }

    context("pollMax ordering") {
        test("pollMax extracts elements in descending order") {
            val pd = minMaxPriorityDequeOf(5, 1, 4, 2, 3)
            val extracted = buildList { repeat(5) { add(pd.pollMax()!!) } }
            extracted shouldContainExactly listOf(5, 4, 3, 2, 1)
        }

        test("pollMax interleaved with add maintains invariant") {
            val pd = minMaxPriorityDequeOf<Int>()
            pd.add(3); pd.add(5)
            pd.pollMax() shouldBe 5
            pd.add(7)
            pd.pollMax() shouldBe 7
            pd.peekMax() shouldBe 3
        }
    }

    context("mixed pollMin and pollMax") {
        test("alternating pollMin and pollMax maintains invariant") {
            val pd = minMaxPriorityDequeOf(1, 2, 3, 4, 5, 6)
            pd.pollMin() shouldBe 1
            pd.pollMax() shouldBe 6
            pd.pollMin() shouldBe 2
            pd.pollMax() shouldBe 5
            pd.pollMin() shouldBe 3
            pd.pollMax() shouldBe 4
            pd.isEmpty().shouldBeTrue()
        }
    }

    context("remove") {
        test("remove returns true when element is present") {
            val pd = minMaxPriorityDequeOf(1, 2, 3)
            pd.remove(2).shouldBeTrue()
            pd.size shouldBe 2
        }

        test("remove returns false when element is absent") {
            val pd = minMaxPriorityDequeOf(1, 2, 3)
            pd.remove(99).shouldBeFalse()
            pd.size shouldBe 3
        }

        test("remove uses equals not comparator") {
            val pd = minMaxPriorityDequeOf(1, 2, 3)
            pd.remove(2)
            pd.pollMin() shouldBe 1
            pd.pollMin() shouldBe 3
        }

        test("remove min element preserves invariant") {
            val pd = minMaxPriorityDequeOf(1, 2, 3, 4, 5)
            pd.remove(1)
            pd.peekMin() shouldBe 2
            pd.peekMax() shouldBe 5
        }

        test("remove max element preserves invariant") {
            val pd = minMaxPriorityDequeOf(1, 2, 3, 4, 5)
            pd.remove(5)
            pd.peekMin() shouldBe 1
            pd.peekMax() shouldBe 4
        }

        test("remove interior element preserves invariant") {
            val pd = minMaxPriorityDequeOf(1, 2, 3, 4, 5)
            pd.remove(3)
            val extracted = buildList { repeat(4) { add(pd.pollMin()!!) } }
            extracted shouldContainExactly listOf(1, 2, 4, 5)
        }
    }

    context("contains") {
        test("contains returns true for present elements") {
            val pd = minMaxPriorityDequeOf(1, 2, 3)
            pd.contains(2).shouldBeTrue()
        }

        test("contains returns false for absent elements") {
            val pd = minMaxPriorityDequeOf(1, 2, 3)
            pd.contains(99).shouldBeFalse()
        }

        test("contains uses equals not comparator order") {
            val byLength = compareBy<String> { it.length }
            val pd = priorityDequeOf(byLength, "a", "bb", "ccc")
            pd.contains("bb").shouldBeTrue()
            pd.contains("zz").shouldBeFalse()
        }
    }

    context("removeAll and retainAll") {
        test("removeAll removes matching elements and preserves heap invariant") {
            val pd = minMaxPriorityDequeOf(1, 2, 3, 4, 5)
            pd.removeAll(listOf(2, 4)).shouldBeTrue()
            pd.size shouldBe 3
            val extracted = buildList { repeat(3) { add(pd.pollMin()!!) } }
            extracted shouldContainExactly listOf(1, 3, 5)
        }

        test("removeAll returns false when no elements removed") {
            val pd = minMaxPriorityDequeOf(1, 2, 3)
            pd.removeAll(listOf(99, 100)).shouldBeFalse()
            pd.size shouldBe 3
        }

        test("retainAll retains matching elements and preserves heap invariant") {
            val pd = minMaxPriorityDequeOf(1, 2, 3, 4, 5)
            pd.retainAll(listOf(1, 3, 5)).shouldBeTrue()
            pd.size shouldBe 3
            val extracted = buildList { repeat(3) { add(pd.pollMin()!!) } }
            extracted shouldContainExactly listOf(1, 3, 5)
        }

        test("retainAll returns false when all elements retained") {
            val pd = minMaxPriorityDequeOf(1, 2, 3)
            pd.retainAll(listOf(1, 2, 3)).shouldBeFalse()
            pd.size shouldBe 3
        }
    }

    context("clear") {
        test("clear empties the deque") {
            val pd = minMaxPriorityDequeOf(1, 2, 3)
            pd.clear()
            pd.isEmpty().shouldBeTrue()
            pd.size shouldBe 0
        }

        test("add after clear works correctly") {
            val pd = minMaxPriorityDequeOf(1, 2, 3)
            pd.clear()
            pd.add(5)
            pd.peekMin() shouldBe 5
        }
    }

    context("iterator") {
        test("iterator visits all elements") {
            val pd = minMaxPriorityDequeOf(1, 2, 3, 4, 5)
            val visited = pd.toList()
            visited shouldContainExactlyInAnyOrder listOf(1, 2, 3, 4, 5)
        }

        test("iterator remove throws UnsupportedOperationException") {
            val pd = minMaxPriorityDequeOf(1, 2, 3)
            val it = pd.iterator()
            it.next()
            shouldThrow<UnsupportedOperationException> { it.remove() }
        }
    }

    context("drainSorted") {
        test("drainSorted yields elements in ascending order") {
            val pd = minMaxPriorityDequeOf(5, 1, 4, 2, 3)
            val sorted = pd.drainSorted().toList()
            sorted shouldContainExactly listOf(1, 2, 3, 4, 5)
        }

        test("drainSorted empties the deque") {
            val pd = minMaxPriorityDequeOf(1, 2, 3)
            pd.drainSorted().toList()
            pd.isEmpty().shouldBeTrue()
        }
    }

    context("toSortedList") {
        test("toSortedList returns elements in ascending order") {
            val pd = minMaxPriorityDequeOf(5, 1, 4, 2, 3)
            pd.toSortedList() shouldContainExactly listOf(1, 2, 3, 4, 5)
        }

        test("toSortedList does not modify the deque") {
            val pd = minMaxPriorityDequeOf(5, 1, 4, 2, 3)
            pd.toSortedList()
            pd.size shouldBe 5
            pd.peekMin() shouldBe 1
            pd.peekMax() shouldBe 5
        }
    }

    context("offer") {
        test("offer is an alias for add") {
            val pd = minMaxPriorityDequeOf<Int>()
            pd.offer(3).shouldBeTrue()
            pd.offer(1).shouldBeTrue()
            pd.peekMin() shouldBe 1
        }
    }

    context("MutableCollection contract") {
        test("addAll adds all elements") {
            val pd = minMaxPriorityDequeOf<Int>()
            pd.addAll(listOf(3, 1, 2))
            pd.size shouldBe 3
            pd.peekMin() shouldBe 1
        }

        test("containsAll returns true when all elements are present") {
            val pd = minMaxPriorityDequeOf(1, 2, 3, 4, 5)
            pd.containsAll(listOf(2, 4)).shouldBeTrue()
        }

        test("containsAll returns false when any element is absent") {
            val pd = minMaxPriorityDequeOf(1, 2, 3)
            pd.containsAll(listOf(2, 99)).shouldBeFalse()
        }

        test("usable as MutableCollection") {
            val col: MutableCollection<Int> = minMaxPriorityDequeOf()
            col.add(3)
            col.add(1)
            col.add(2)
            col.size shouldBe 3
        }
    }

    context("nullability") {
        test("peekMin returns null on empty nullable-element deque") {
            val pd = priorityDequeOf<Int?>(nullsFirst(naturalOrder()))
            pd.peekMin().shouldBeNull()
        }

        test("pollMin returns null on empty nullable-element deque") {
            val pd = priorityDequeOf<Int?>(nullsFirst(naturalOrder()))
            pd.pollMin().shouldBeNull()
        }

        test("isEmpty guards against null ambiguity") {
            val pd = priorityDequeOf<Int?>(nullsFirst(naturalOrder()))
            pd.add(null)
            pd.isEmpty().shouldBeFalse()
            pd.peekMin()    // value is null but deque is not empty
            pd.isEmpty().shouldBeFalse()
        }
    }

    context("stress test") {
        test("pollMin extracts 200 random elements in ascending order") {
            val rng = Random(42)
            val values = List(200) { rng.nextInt() }
            val pd = values.toPriorityDeque(naturalOrder())
            val extracted = buildList { repeat(200) { add(pd.pollMin()!!) } }
            extracted shouldContainExactly values.sorted()
        }

        test("pollMax extracts 200 random elements in descending order") {
            val rng = Random(99)
            val values = List(200) { rng.nextInt() }
            val pd = values.toPriorityDeque(naturalOrder())
            val extracted = buildList { repeat(200) { add(pd.pollMax()!!) } }
            extracted shouldContainExactly values.sortedDescending()
        }

        test("mixed operations on 200 elements maintain min-max invariant") {
            val rng = Random(7)
            val values = List(200) { rng.nextInt(1000) }
            val pd = values.toPriorityDeque(naturalOrder())
            val sorted = values.sorted()
            var lo = 0
            var hi = 199
            repeat(100) {
                pd.pollMin() shouldBe sorted[lo++]
                pd.pollMax() shouldBe sorted[hi--]
            }
            pd.isEmpty().shouldBeTrue()
        }
    }
})
