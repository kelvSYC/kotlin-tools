package com.kelvsyc.kotlin.core.structures

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class IndexedPriorityDequeTest : FunSpec({

    context("empty state") {
        test("isEmpty is true on fresh deque") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.isEmpty().shouldBeTrue()
            ipd.isNotEmpty().shouldBeFalse()
        }

        test("size is 0 on fresh deque") {
            indexedPriorityDequeOf<String, Int>(naturalOrder()).size shouldBe 0
        }

        test("peekMin returns null on empty deque") {
            indexedPriorityDequeOf<String, Int>(naturalOrder()).peekMin().shouldBeNull()
        }

        test("peekMax returns null on empty deque") {
            indexedPriorityDequeOf<String, Int>(naturalOrder()).peekMax().shouldBeNull()
        }

        test("pollMin returns null on empty deque") {
            indexedPriorityDequeOf<String, Int>(naturalOrder()).pollMin().shouldBeNull()
        }

        test("pollMax returns null on empty deque") {
            indexedPriorityDequeOf<String, Int>(naturalOrder()).pollMax().shouldBeNull()
        }

        test("contains returns false on empty deque") {
            indexedPriorityDequeOf<String, Int>(naturalOrder()).contains("A").shouldBeFalse()
        }

        test("getPriority returns null on empty deque") {
            indexedPriorityDequeOf<String, Int>(naturalOrder()).getPriority("A").shouldBeNull()
        }
    }

    context("single element") {
        test("peekMin and peekMax return the same element") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 5)
            ipd.peekMin() shouldBe "A"
            ipd.peekMax() shouldBe "A"
        }

        test("pollMin empties the deque") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 5)
            ipd.pollMin() shouldBe "A"
            ipd.isEmpty().shouldBeTrue()
        }

        test("pollMax empties the deque") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 5)
            ipd.pollMax() shouldBe "A"
            ipd.isEmpty().shouldBeTrue()
        }
    }

    context("two elements") {
        test("pollMin returns lower-priority element") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 1); ipd.add("B", 9)
            ipd.pollMin() shouldBe "A"
        }

        test("pollMax returns higher-priority element") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 1); ipd.add("B", 9)
            ipd.pollMax() shouldBe "B"
        }

        test("peekMax does not remove") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 1); ipd.add("B", 9)
            ipd.peekMax() shouldBe "B"
            ipd.size shouldBe 2
        }
    }

    context("pollMin ordering") {
        test("pollMin extracts elements in ascending priority order") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("C", 3); ipd.add("A", 1); ipd.add("B", 2)
            ipd.pollMin() shouldBe "A"
            ipd.pollMin() shouldBe "B"
            ipd.pollMin() shouldBe "C"
            ipd.pollMin().shouldBeNull()
        }

        test("peekMin is non-destructive") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 1); ipd.add("B", 2)
            ipd.peekMin() shouldBe "A"
            ipd.peekMin() shouldBe "A"
            ipd.size shouldBe 2
        }
    }

    context("pollMax ordering") {
        test("pollMax extracts elements in descending priority order") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 1); ipd.add("B", 2); ipd.add("C", 3)
            ipd.pollMax() shouldBe "C"
            ipd.pollMax() shouldBe "B"
            ipd.pollMax() shouldBe "A"
            ipd.pollMax().shouldBeNull()
        }

        test("peekMax is non-destructive") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 1); ipd.add("B", 9)
            ipd.peekMax() shouldBe "B"
            ipd.peekMax() shouldBe "B"
            ipd.size shouldBe 2
        }
    }

    context("interleaved pollMin and pollMax") {
        test("draining from both ends preserves heap invariant") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            (1..7).forEach { ipd.add("E$it", it) }
            ipd.pollMin() shouldBe "E1"
            ipd.pollMax() shouldBe "E7"
            ipd.pollMin() shouldBe "E2"
            ipd.pollMax() shouldBe "E6"
            ipd.pollMin() shouldBe "E3"
            ipd.pollMax() shouldBe "E5"
            ipd.pollMin() shouldBe "E4"
            ipd.pollMin().shouldBeNull()
        }
    }

    context("add preconditions") {
        test("add duplicate element throws IllegalArgumentException") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 1)
            shouldThrow<IllegalArgumentException> { ipd.add("A", 99) }
        }
    }

    context("contains and getPriority") {
        test("contains returns true for present element") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 5)
            ipd.contains("A").shouldBeTrue()
        }

        test("contains returns false for absent element") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 5)
            ipd.contains("Z").shouldBeFalse()
        }

        test("getPriority returns current priority") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 5)
            ipd.getPriority("A") shouldBe 5
        }

        test("getPriority returns null for absent element") {
            indexedPriorityDequeOf<String, Int>(naturalOrder()).getPriority("Z").shouldBeNull()
        }

        test("contains returns false after pollMin removes the element") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 1)
            ipd.pollMin()
            ipd.contains("A").shouldBeFalse()
        }

        test("contains returns false after pollMax removes the element") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 1); ipd.add("B", 9)
            ipd.pollMax()
            ipd.contains("B").shouldBeFalse()
        }
    }

    context("remove") {
        test("remove returns true and element is gone") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 1); ipd.add("B", 2); ipd.add("C", 3)
            ipd.remove("B").shouldBeTrue()
            ipd.contains("B").shouldBeFalse()
            ipd.size shouldBe 2
        }

        test("remove returns false for absent element") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 1)
            ipd.remove("Z").shouldBeFalse()
        }

        test("heap order maintained after removing minimum") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("C", 3); ipd.add("A", 1); ipd.add("B", 2)
            ipd.remove("A")
            ipd.pollMin() shouldBe "B"
            ipd.pollMax() shouldBe "C"
        }

        test("heap order maintained after removing maximum") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("C", 3); ipd.add("A", 1); ipd.add("B", 2)
            ipd.remove("C")
            ipd.pollMax() shouldBe "B"
            ipd.pollMin() shouldBe "A"
        }
    }

    context("decreaseKey") {
        test("decreaseKey moves element to earlier pollMin position") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 1); ipd.add("B", 3); ipd.add("C", 5)
            ipd.decreaseKey("C", 0)
            ipd.pollMin() shouldBe "C"
        }

        test("decreaseKey on the maximum element moves it away from the max end") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 1); ipd.add("B", 3); ipd.add("C", 9)
            ipd.decreaseKey("C", 2)
            ipd.pollMax() shouldBe "B"
        }

        test("decreaseKey updates stored priority") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 10)
            ipd.decreaseKey("A", 2)
            ipd.getPriority("A") shouldBe 2
        }

        test("decreaseKey with new >= current throws IllegalArgumentException") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 5)
            shouldThrow<IllegalArgumentException> { ipd.decreaseKey("A", 5) }
            shouldThrow<IllegalArgumentException> { ipd.decreaseKey("A", 10) }
        }

        test("decreaseKey on absent element throws NoSuchElementException") {
            shouldThrow<NoSuchElementException> {
                indexedPriorityDequeOf<String, Int>(naturalOrder()).decreaseKey("Z", 1)
            }
        }
    }

    context("increaseKey") {
        test("increaseKey moves element to later pollMin position") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 1); ipd.add("B", 3); ipd.add("C", 5)
            ipd.increaseKey("A", 10)
            ipd.pollMin() shouldBe "B"
            ipd.pollMax() shouldBe "A"
        }

        test("increaseKey on the minimum element moves it away from the min end") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 1); ipd.add("B", 3); ipd.add("C", 5)
            ipd.increaseKey("A", 4)
            ipd.pollMin() shouldBe "B"
        }

        test("increaseKey updates stored priority") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 1)
            ipd.increaseKey("A", 8)
            ipd.getPriority("A") shouldBe 8
        }

        test("increaseKey with new <= current throws IllegalArgumentException") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 5)
            shouldThrow<IllegalArgumentException> { ipd.increaseKey("A", 5) }
            shouldThrow<IllegalArgumentException> { ipd.increaseKey("A", 3) }
        }

        test("increaseKey on absent element throws NoSuchElementException") {
            shouldThrow<NoSuchElementException> {
                indexedPriorityDequeOf<String, Int>(naturalOrder()).increaseKey("Z", 99)
            }
        }
    }

    context("updatePriority") {
        test("updatePriority decrease direction works") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 5); ipd.add("B", 2)
            ipd.updatePriority("A", 1)
            ipd.pollMin() shouldBe "A"
        }

        test("updatePriority increase direction works") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 1); ipd.add("B", 5)
            ipd.updatePriority("A", 9)
            ipd.pollMax() shouldBe "A"
        }

        test("updatePriority equal priority is a no-op") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 5); ipd.add("B", 3)
            ipd.updatePriority("A", 5)
            ipd.getPriority("A") shouldBe 5
            ipd.pollMin() shouldBe "B"
        }

        test("updatePriority on absent element throws NoSuchElementException") {
            shouldThrow<NoSuchElementException> {
                indexedPriorityDequeOf<String, Int>(naturalOrder()).updatePriority("Z", 5)
            }
        }
    }

    context("known-universe variant") {
        test("add element in universe succeeds") {
            val ipd = indexedPriorityDequeOf(naturalOrder<Int>(), listOf("A", "B", "C"))
            ipd.add("A", 1)
            ipd.contains("A").shouldBeTrue()
        }

        test("add element not in universe throws IllegalArgumentException") {
            val ipd = indexedPriorityDequeOf(naturalOrder<Int>(), listOf("A", "B", "C"))
            shouldThrow<IllegalArgumentException> { ipd.add("Z", 1) }
        }

        test("pollMin and pollMax work correctly with known universe") {
            val ipd = indexedPriorityDequeOf(naturalOrder<Int>(), listOf("A", "B", "C"))
            ipd.add("A", 3); ipd.add("B", 1); ipd.add("C", 2)
            ipd.pollMin() shouldBe "B"
            ipd.pollMax() shouldBe "A"
            ipd.pollMin() shouldBe "C"
        }

        test("element can be re-added after pollMax") {
            val ipd = indexedPriorityDequeOf(naturalOrder<Int>(), listOf("A", "B"))
            ipd.add("A", 5); ipd.add("B", 1)
            ipd.pollMax()
            ipd.add("A", 3)
            ipd.getPriority("A") shouldBe 3
        }
    }

    context("dynamic-universe slot recycling") {
        test("element removed then re-added succeeds") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            ipd.add("A", 5)
            ipd.remove("A")
            ipd.add("A", 3)
            ipd.getPriority("A") shouldBe 3
            ipd.pollMin() shouldBe "A"
        }
    }

    context("stress") {
        test("draining from both ends on a large heap preserves invariant") {
            val ipd = indexedPriorityDequeOf<String, Int>(naturalOrder())
            val n = 20
            (1..n).shuffled().forEach { ipd.add("E$it", it) }
            var lo = 1; var hi = n
            while (lo <= hi) {
                if (lo == hi) {
                    ipd.pollMin() shouldBe "E$lo"
                    lo++
                } else {
                    ipd.pollMin() shouldBe "E$lo"; lo++
                    ipd.pollMax() shouldBe "E$hi"; hi--
                }
            }
            ipd.isEmpty().shouldBeTrue()
        }
    }
})
