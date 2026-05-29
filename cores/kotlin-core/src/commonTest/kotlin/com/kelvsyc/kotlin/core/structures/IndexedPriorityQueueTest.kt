package com.kelvsyc.kotlin.core.structures

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class IndexedPriorityQueueTest : FunSpec({

    context("empty state") {
        test("isEmpty is true on fresh queue") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.isEmpty().shouldBeTrue()
            ipq.isNotEmpty().shouldBeFalse()
        }

        test("size is 0 on fresh queue") {
            indexedPriorityQueueOf<String, Int>(naturalOrder()).size shouldBe 0
        }

        test("peekMin returns null on empty queue") {
            indexedPriorityQueueOf<String, Int>(naturalOrder()).peekMin().shouldBeNull()
        }

        test("pollMin returns null on empty queue") {
            indexedPriorityQueueOf<String, Int>(naturalOrder()).pollMin().shouldBeNull()
        }

        test("contains returns false on empty queue") {
            indexedPriorityQueueOf<String, Int>(naturalOrder()).contains("A").shouldBeFalse()
        }

        test("getPriority returns null on empty queue") {
            indexedPriorityQueueOf<String, Int>(naturalOrder()).getPriority("A").shouldBeNull()
        }
    }

    context("add and poll ordering") {
        test("pollMin extracts elements in ascending priority order") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("C", 3); ipq.add("A", 1); ipq.add("B", 2)
            ipq.pollMin() shouldBe "A"
            ipq.pollMin() shouldBe "B"
            ipq.pollMin() shouldBe "C"
            ipq.pollMin().shouldBeNull()
        }

        test("peekMin does not remove the element") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 1); ipq.add("B", 2)
            ipq.peekMin() shouldBe "A"
            ipq.peekMin() shouldBe "A"
            ipq.size shouldBe 2
        }

        test("size tracks element count correctly") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.isEmpty().shouldBeTrue()
            ipq.add("A", 1)
            ipq.size shouldBe 1
            ipq.isNotEmpty().shouldBeTrue()
            ipq.add("B", 2)
            ipq.size shouldBe 2
            ipq.pollMin()
            ipq.size shouldBe 1
        }
    }

    context("add preconditions") {
        test("add duplicate element throws IllegalArgumentException") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 1)
            shouldThrow<IllegalArgumentException> { ipq.add("A", 99) }
        }
    }

    context("contains and getPriority") {
        test("contains returns true for a present element") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 5)
            ipq.contains("A").shouldBeTrue()
        }

        test("contains returns false for an absent element") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 5)
            ipq.contains("Z").shouldBeFalse()
        }

        test("getPriority returns the current priority of a present element") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 5)
            ipq.getPriority("A") shouldBe 5
        }

        test("getPriority returns null for an absent element") {
            indexedPriorityQueueOf<String, Int>(naturalOrder()).getPriority("Z").shouldBeNull()
        }

        test("contains returns false after the element is polled") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 1)
            ipq.pollMin()
            ipq.contains("A").shouldBeFalse()
        }
    }

    context("remove") {
        test("remove returns true and element is no longer present") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 1); ipq.add("B", 2); ipq.add("C", 3)
            ipq.remove("B").shouldBeTrue()
            ipq.contains("B").shouldBeFalse()
            ipq.size shouldBe 2
        }

        test("remove returns false for an absent element") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 1)
            ipq.remove("Z").shouldBeFalse()
        }

        test("heap order is maintained after removing the minimum") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("C", 3); ipq.add("A", 1); ipq.add("B", 2)
            ipq.remove("A")
            ipq.pollMin() shouldBe "B"
            ipq.pollMin() shouldBe "C"
        }

        test("heap order is maintained after removing an interior element") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("C", 3); ipq.add("A", 1); ipq.add("B", 2)
            ipq.remove("B")
            ipq.pollMin() shouldBe "A"
            ipq.pollMin() shouldBe "C"
        }

        test("getPriority of surviving elements is unaffected by remove") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 1); ipq.add("B", 5)
            ipq.remove("A")
            ipq.getPriority("B") shouldBe 5
        }
    }

    context("decreaseKey") {
        test("decreaseKey moves element to earlier poll position") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 1); ipq.add("B", 3); ipq.add("C", 5)
            ipq.decreaseKey("C", 0)
            ipq.pollMin() shouldBe "C"
            ipq.pollMin() shouldBe "A"
            ipq.pollMin() shouldBe "B"
        }

        test("decreaseKey updates the stored priority") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 10)
            ipq.decreaseKey("A", 2)
            ipq.getPriority("A") shouldBe 2
        }

        test("decreaseKey with new priority equal to current throws IllegalArgumentException") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 5)
            shouldThrow<IllegalArgumentException> { ipq.decreaseKey("A", 5) }
        }

        test("decreaseKey with new priority greater than current throws IllegalArgumentException") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 5)
            shouldThrow<IllegalArgumentException> { ipq.decreaseKey("A", 10) }
        }

        test("decreaseKey on absent element throws NoSuchElementException") {
            shouldThrow<NoSuchElementException> {
                indexedPriorityQueueOf<String, Int>(naturalOrder()).decreaseKey("Z", 1)
            }
        }
    }

    context("increaseKey") {
        test("increaseKey moves element to later poll position") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 1); ipq.add("B", 3); ipq.add("C", 5)
            ipq.increaseKey("A", 10)
            ipq.pollMin() shouldBe "B"
            ipq.pollMin() shouldBe "C"
            ipq.pollMin() shouldBe "A"
        }

        test("increaseKey updates the stored priority") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 1)
            ipq.increaseKey("A", 8)
            ipq.getPriority("A") shouldBe 8
        }

        test("increaseKey with new priority equal to current throws IllegalArgumentException") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 5)
            shouldThrow<IllegalArgumentException> { ipq.increaseKey("A", 5) }
        }

        test("increaseKey with new priority less than current throws IllegalArgumentException") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 5)
            shouldThrow<IllegalArgumentException> { ipq.increaseKey("A", 3) }
        }

        test("increaseKey on absent element throws NoSuchElementException") {
            shouldThrow<NoSuchElementException> {
                indexedPriorityQueueOf<String, Int>(naturalOrder()).increaseKey("Z", 99)
            }
        }
    }

    context("updatePriority") {
        test("updatePriority decrease direction moves element earlier") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 5); ipq.add("B", 2)
            ipq.updatePriority("A", 1)
            ipq.pollMin() shouldBe "A"
        }

        test("updatePriority increase direction moves element later") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 1); ipq.add("B", 5)
            ipq.updatePriority("A", 9)
            ipq.pollMin() shouldBe "B"
        }

        test("updatePriority with equal priority is a no-op") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 5); ipq.add("B", 3)
            ipq.updatePriority("A", 5)
            ipq.getPriority("A") shouldBe 5
            ipq.pollMin() shouldBe "B"
            ipq.pollMin() shouldBe "A"
        }

        test("updatePriority updates the stored priority") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 3)
            ipq.updatePriority("A", 7)
            ipq.getPriority("A") shouldBe 7
        }

        test("updatePriority on absent element throws NoSuchElementException") {
            shouldThrow<NoSuchElementException> {
                indexedPriorityQueueOf<String, Int>(naturalOrder()).updatePriority("Z", 5)
            }
        }
    }

    context("known-universe variant") {
        test("add element in universe succeeds") {
            val ipq = indexedPriorityQueueOf(naturalOrder<Int>(), listOf("A", "B", "C"))
            ipq.add("A", 1)
            ipq.contains("A").shouldBeTrue()
        }

        test("add element not in universe throws IllegalArgumentException") {
            val ipq = indexedPriorityQueueOf(naturalOrder<Int>(), listOf("A", "B", "C"))
            shouldThrow<IllegalArgumentException> { ipq.add("Z", 1) }
        }

        test("contains returns false for universe element not yet added") {
            val ipq = indexedPriorityQueueOf(naturalOrder<Int>(), listOf("A", "B", "C"))
            ipq.contains("A").shouldBeFalse()
        }

        test("known-universe queue preserves heap order across operations") {
            val ipq = indexedPriorityQueueOf(naturalOrder<Int>(), listOf("A", "B", "C"))
            ipq.add("C", 3); ipq.add("A", 1); ipq.add("B", 2)
            ipq.decreaseKey("C", 0)
            ipq.pollMin() shouldBe "C"
            ipq.pollMin() shouldBe "A"
            ipq.pollMin() shouldBe "B"
        }

        test("element can be re-added after being polled") {
            val ipq = indexedPriorityQueueOf(naturalOrder<Int>(), listOf("A", "B"))
            ipq.add("A", 5)
            ipq.pollMin()
            ipq.add("A", 3)
            ipq.getPriority("A") shouldBe 3
        }
    }

    context("dynamic-universe slot recycling") {
        test("element removed then re-added is treated as a fresh insertion") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 5)
            ipq.remove("A")
            ipq.add("A", 3)
            ipq.getPriority("A") shouldBe 3
            ipq.pollMin() shouldBe "A"
        }

        test("slot recycling preserves heap order for other elements") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("A", 1); ipq.add("B", 3); ipq.add("C", 5)
            ipq.remove("B")
            ipq.add("B", 2)
            ipq.pollMin() shouldBe "A"
            ipq.pollMin() shouldBe "B"
            ipq.pollMin() shouldBe "C"
        }
    }

    context("stress") {
        test("interleaved add, updatePriority, and pollMin preserves order") {
            val ipq = indexedPriorityQueueOf<String, Int>(naturalOrder())
            ipq.add("D", 4); ipq.add("B", 2); ipq.add("E", 5); ipq.add("A", 1); ipq.add("C", 3)
            ipq.decreaseKey("E", 0)
            ipq.pollMin() shouldBe "E"
            ipq.increaseKey("A", 10)
            ipq.pollMin() shouldBe "B"
            ipq.pollMin() shouldBe "C"
            ipq.pollMin() shouldBe "D"
            ipq.pollMin() shouldBe "A"
        }
    }
})
