package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class PriorityQueueTest : FunSpec({

    context("construction") {
        test("empty min-priority queue") {
            val pq = minPriorityQueueOf<Int>()
            pq.isEmpty().shouldBeTrue()
            pq.size shouldBe 0
            pq.peek().shouldBeNull()
            pq.poll().shouldBeNull()
        }

        test("min-priority queue with elements is heap-ordered") {
            val pq = minPriorityQueueOf(5, 1, 4, 2, 3)
            pq.size shouldBe 5
            pq.peek() shouldBe 1
        }

        test("max-priority queue inverts order") {
            val pq = maxPriorityQueueOf(5, 1, 4, 2, 3)
            pq.peek() shouldBe 5
        }

        test("priorityQueueOf with custom comparator") {
            val byLength = compareBy<String> { it.length }
            val pq = priorityQueueOf(byLength, "ccc", "a", "bb")
            pq.peek() shouldBe "a"
        }

        test("toPriorityQueue from Iterable") {
            val pq = listOf(3, 1, 2).toPriorityQueue(naturalOrder())
            pq.size shouldBe 3
            pq.peek() shouldBe 1
        }

        test("toPriorityQueue from Sequence") {
            val pq = sequenceOf(3, 1, 2).toPriorityQueue(naturalOrder())
            pq.size shouldBe 3
            pq.peek() shouldBe 1
        }
    }

    context("add and poll") {
        test("add then poll yields least element first") {
            val pq = minPriorityQueueOf<Int>()
            listOf(7, 3, 9, 1, 5, 4).forEach { pq.add(it) }
            val drained = generateSequence { pq.poll() }.toList()
            drained shouldBe listOf(1, 3, 4, 5, 7, 9)
            pq.isEmpty().shouldBeTrue()
        }

        test("offer is an alias for add") {
            val pq = minPriorityQueueOf<Int>()
            pq.offer(2).shouldBeTrue()
            pq.offer(1).shouldBeTrue()
            pq.peek() shouldBe 1
        }

        test("add returns true") {
            minPriorityQueueOf<Int>().add(42).shouldBeTrue()
        }

        test("poll on empty queue returns null") {
            minPriorityQueueOf<Int>().poll().shouldBeNull()
        }
    }

    context("addOrPoll") {
        test("on empty queue returns the element itself") {
            val pq = minPriorityQueueOf<Int>()
            pq.addOrPoll(7) shouldBe 7
            pq.isEmpty().shouldBeTrue()
        }

        test("returns the element when it is the new minimum") {
            val pq = minPriorityQueueOf(5, 6, 7)
            pq.addOrPoll(1) shouldBe 1
            pq.toSortedList() shouldBe listOf(5, 6, 7)
        }

        test("returns the previous head when element is larger") {
            val pq = minPriorityQueueOf(5, 6, 7)
            pq.addOrPoll(8) shouldBe 5
            pq.toSortedList() shouldBe listOf(6, 7, 8)
        }
    }

    context("pollOrAdd") {
        test("on empty queue inserts and returns null") {
            val pq = minPriorityQueueOf<Int>()
            pq.pollOrAdd(7).shouldBeNull()
            pq.peek() shouldBe 7
        }

        test("pops head and inserts new element") {
            val pq = minPriorityQueueOf(2, 4, 6)
            pq.pollOrAdd(5) shouldBe 2
            pq.toSortedList() shouldBe listOf(4, 5, 6)
        }
    }

    context("contains and remove") {
        test("contains uses equals, not the comparator") {
            // Comparator considers all strings equal by length, but contains should still distinguish them.
            val byLength = compareBy<String> { it.length }
            val pq = priorityQueueOf(byLength, "aa", "bb")
            pq.contains("aa").shouldBeTrue()
            pq.contains("cc").shouldBeFalse()
        }

        test("remove deletes a single occurrence") {
            val pq = minPriorityQueueOf(1, 2, 2, 3)
            pq.remove(2).shouldBeTrue()
            pq.toSortedList() shouldBe listOf(1, 2, 3)
        }

        test("remove returns false when element is absent") {
            minPriorityQueueOf(1, 2, 3).remove(99).shouldBeFalse()
        }

        test("remove preserves heap order across many operations") {
            val pq = minPriorityQueueOf(5, 3, 8, 1, 9, 2, 7, 4, 6)
            pq.remove(5).shouldBeTrue()
            pq.remove(1).shouldBeTrue()
            pq.remove(9).shouldBeTrue()
            pq.toSortedList() shouldBe listOf(2, 3, 4, 6, 7, 8)
        }
    }

    context("removeAll and retainAll") {
        test("removeAll deletes all matching elements") {
            val pq = minPriorityQueueOf(1, 2, 2, 3, 4)
            pq.removeAll(listOf(2, 4)).shouldBeTrue()
            pq.toSortedList() shouldBe listOf(1, 3)
        }

        test("removeAll returns false when nothing changes") {
            minPriorityQueueOf(1, 2, 3).removeAll(listOf(99)).shouldBeFalse()
        }

        test("retainAll keeps only matching elements") {
            val pq = minPriorityQueueOf(1, 2, 3, 4, 5)
            pq.retainAll(listOf(2, 4)).shouldBeTrue()
            pq.toSortedList() shouldBe listOf(2, 4)
        }

        test("retainAll on empty filter empties the queue") {
            val pq = minPriorityQueueOf(1, 2, 3)
            pq.retainAll(emptyList<Int>()).shouldBeTrue()
            pq.isEmpty().shouldBeTrue()
        }
    }

    context("clear") {
        test("clear empties the queue") {
            val pq = minPriorityQueueOf(1, 2, 3)
            pq.clear()
            pq.isEmpty().shouldBeTrue()
            pq.peek().shouldBeNull()
        }
    }

    context("iteration") {
        test("iterator visits every element exactly once (order unspecified)") {
            val pq = minPriorityQueueOf(5, 3, 8, 1, 9, 2, 7, 4, 6)
            pq.toList().shouldContainExactlyInAnyOrder(1, 2, 3, 4, 5, 6, 7, 8, 9)
        }

        test("iterator.remove throws UnsupportedOperationException") {
            val pq = minPriorityQueueOf(1, 2, 3)
            val it = pq.iterator()
            it.next()
            shouldThrow<UnsupportedOperationException> { it.remove() }
        }
    }

    context("sorted views") {
        test("toSortedList returns elements in ascending order without modifying the queue") {
            val pq = minPriorityQueueOf(5, 3, 8, 1, 9, 2)
            pq.toSortedList() shouldContainExactly listOf(1, 2, 3, 5, 8, 9)
            pq.size shouldBe 6
        }

        test("drainSorted yields elements in ascending order and empties the queue") {
            val pq = minPriorityQueueOf(5, 3, 8, 1, 9, 2)
            pq.drainSorted().toList() shouldContainExactly listOf(1, 2, 3, 5, 8, 9)
            pq.isEmpty().shouldBeTrue()
        }

        test("toSortedList on max-priority queue is descending") {
            val pq = maxPriorityQueueOf(5, 3, 8, 1, 9, 2)
            pq.toSortedList() shouldContainExactly listOf(9, 8, 5, 3, 2, 1)
        }
    }

    context("nullable element type") {
        test("queue of nullable strings can hold a null element") {
            val cmp = nullsFirst(naturalOrder<String>())
            val pq = priorityQueueOf<String?>(cmp, "b", null, "a")
            pq.size shouldBe 3
            pq.peek().shouldBeNull()
            pq.poll().shouldBeNull()
            pq.poll() shouldBe "a"
            pq.poll() shouldBe "b"
            pq.isEmpty().shouldBeTrue()
        }
    }

    context("top-K extensions") {
        test("nSmallest returns the k least elements ascending") {
            val data = listOf(5, 3, 8, 1, 9, 2, 7, 4, 6)
            data.nSmallest(3, naturalOrder()) shouldContainExactly listOf(1, 2, 3)
        }

        test("nLargest returns the k greatest elements descending") {
            val data = listOf(5, 3, 8, 1, 9, 2, 7, 4, 6)
            data.nLargest(3, naturalOrder()) shouldContainExactly listOf(9, 8, 7)
        }

        test("nSmallest with k larger than collection returns all elements sorted") {
            listOf(3, 1, 2).nSmallest(10, naturalOrder()) shouldContainExactly listOf(1, 2, 3)
        }

        test("nSmallest with k = 0 returns empty list") {
            listOf(3, 1, 2).nSmallest(0, naturalOrder()) shouldBe emptyList()
        }

        test("nSmallest rejects negative k") {
            shouldThrow<IllegalArgumentException> {
                listOf(1, 2, 3).nSmallest(-1, naturalOrder())
            }
        }

        test("nSmallest works with custom comparator") {
            val byLength = compareBy<String> { it.length }
            listOf("aaaa", "b", "cc", "ddd").nSmallest(2, byLength) shouldContainExactly listOf("b", "cc")
        }
    }

    context("mergeSorted") {
        test("merges multiple sorted lists into a single sorted sequence") {
            val a = listOf(1, 4, 7)
            val b = listOf(2, 5, 8)
            val c = listOf(3, 6, 9)
            mergeSorted(listOf(a, b, c), naturalOrder<Int>()).toList() shouldContainExactly (1..9).toList()
        }

        test("vararg overload") {
            mergeSorted(naturalOrder<Int>(), listOf(1, 4), listOf(2, 5), listOf(3, 6)).toList() shouldContainExactly
                (1..6).toList()
        }

        test("handles empty sources") {
            mergeSorted(naturalOrder<Int>(), emptyList(), listOf(1, 2), emptyList(), listOf(3)).toList() shouldBe
                listOf(1, 2, 3)
        }

        test("returns empty sequence when all sources are empty") {
            mergeSorted(naturalOrder<Int>(), emptyList<Int>(), emptyList<Int>()).toList() shouldBe emptyList()
        }

        test("returns empty sequence when no sources given") {
            mergeSorted<Int>(emptyList(), naturalOrder()).toList() shouldBe emptyList()
        }

        test("single source passes through unchanged") {
            mergeSorted(naturalOrder<Int>(), listOf(1, 2, 3, 4)).toList() shouldBe listOf(1, 2, 3, 4)
        }

        test("merges sources of different sizes") {
            mergeSorted(naturalOrder<Int>(), listOf(1, 10), listOf(2, 3, 4, 5, 6)).toList() shouldBe
                listOf(1, 2, 3, 4, 5, 6, 10)
        }

        test("respects custom comparator") {
            val byLength = compareBy<String> { it.length }
            val a = listOf("a", "bb", "cccc")
            val b = listOf("d", "ee", "fff")
            mergeSorted(listOf(a, b), byLength).toList().map { it.length } shouldBe listOf(1, 1, 2, 2, 3, 4)
        }

        test("respects descending comparator when sources are descending") {
            val cmp = reverseOrder<Int>()
            mergeSorted(listOf(listOf(9, 5, 1), listOf(8, 4)), cmp).toList() shouldBe listOf(9, 8, 5, 4, 1)
        }

        test("is lazy — caller can short-circuit") {
            // Use a sequence wrapper that throws if consumed past index 2.
            val tripwire = sequence {
                yield(1); yield(3); yield(5)
                error("consumed past intended limit")
            }.constrainOnce()
            // Wrap as Iterable. We expect to take 4 items total (2+2) without tripping the wire on the second source.
            val safe = listOf(2, 4)
            mergeSorted(listOf(tripwire.asIterable(), safe), naturalOrder<Int>())
                .take(4)
                .toList() shouldBe listOf(1, 2, 3, 4)
        }
    }

    context("stress / heap invariant") {
        test("draining a randomly-built heap gives a sorted sequence") {
            val random = (1..200).shuffled(kotlin.random.Random(seed = 42L))
            val pq = priorityQueueOf<Int>(naturalOrder())
            random.forEach { pq.add(it) }
            pq.drainSorted().toList() shouldBe (1..200).toList()
        }

        test("interleaved add and poll preserves order") {
            val pq = minPriorityQueueOf<Int>()
            pq.add(5); pq.add(3); pq.poll() shouldBe 3
            pq.add(1); pq.add(4); pq.poll() shouldBe 1
            pq.add(2); pq.poll() shouldBe 2
            pq.poll() shouldBe 4
            pq.poll() shouldBe 5
            pq.poll().shouldBeNull()
        }
    }
})
