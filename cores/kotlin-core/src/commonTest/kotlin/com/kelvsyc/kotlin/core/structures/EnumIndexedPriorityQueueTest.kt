package com.kelvsyc.kotlin.core.structures

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

private enum class Planet { MERCURY, VENUS, EARTH, MARS, JUPITER }

class EnumIndexedPriorityQueueTest : FunSpec({

    context("empty state") {
        test("isEmpty is true on fresh queue") {
            enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder()).isEmpty().shouldBeTrue()
        }

        test("peekMin returns null on empty queue") {
            enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder()).peekMin().shouldBeNull()
        }

        test("pollMin returns null on empty queue") {
            enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder()).pollMin().shouldBeNull()
        }

        test("contains returns false for any element on empty queue") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.contains(Planet.EARTH).shouldBeFalse()
        }

        test("getPriority returns null for any element on empty queue") {
            enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder()).getPriority(Planet.EARTH).shouldBeNull()
        }
    }

    context("add and poll ordering") {
        test("pollMin extracts enum elements in ascending priority order") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.add(Planet.MARS, 4)
            ipq.add(Planet.MERCURY, 1)
            ipq.add(Planet.EARTH, 3)
            ipq.pollMin() shouldBe Planet.MERCURY
            ipq.pollMin() shouldBe Planet.EARTH
            ipq.pollMin() shouldBe Planet.MARS
            ipq.pollMin().shouldBeNull()
        }

        test("peekMin returns minimum without removing") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.add(Planet.EARTH, 3); ipq.add(Planet.MERCURY, 1)
            ipq.peekMin() shouldBe Planet.MERCURY
            ipq.size shouldBe 2
        }
    }

    context("add preconditions") {
        test("add duplicate throws IllegalArgumentException") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.add(Planet.EARTH, 5)
            shouldThrow<IllegalArgumentException> { ipq.add(Planet.EARTH, 99) }
        }
    }

    context("contains and getPriority") {
        test("contains returns true for a present element") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.add(Planet.EARTH, 3)
            ipq.contains(Planet.EARTH).shouldBeTrue()
        }

        test("contains returns false for an element not added") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.add(Planet.EARTH, 3)
            ipq.contains(Planet.MARS).shouldBeFalse()
        }

        test("getPriority returns current priority") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.add(Planet.EARTH, 7)
            ipq.getPriority(Planet.EARTH) shouldBe 7
        }

        test("contains returns false after element is polled") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.add(Planet.EARTH, 1)
            ipq.pollMin()
            ipq.contains(Planet.EARTH).shouldBeFalse()
        }
    }

    context("remove") {
        test("remove returns true and element is no longer present") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.add(Planet.MERCURY, 1); ipq.add(Planet.VENUS, 2); ipq.add(Planet.EARTH, 3)
            ipq.remove(Planet.VENUS).shouldBeTrue()
            ipq.contains(Planet.VENUS).shouldBeFalse()
        }

        test("remove returns false for an absent element") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.remove(Planet.JUPITER).shouldBeFalse()
        }

        test("heap order maintained after remove") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.add(Planet.MERCURY, 1); ipq.add(Planet.VENUS, 2); ipq.add(Planet.EARTH, 3)
            ipq.remove(Planet.MERCURY)
            ipq.pollMin() shouldBe Planet.VENUS
            ipq.pollMin() shouldBe Planet.EARTH
        }
    }

    context("decreaseKey") {
        test("decreaseKey moves element to earlier poll position") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.add(Planet.EARTH, 10); ipq.add(Planet.MARS, 5)
            ipq.decreaseKey(Planet.EARTH, 1)
            ipq.pollMin() shouldBe Planet.EARTH
        }

        test("decreaseKey with new >= current throws IllegalArgumentException") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.add(Planet.EARTH, 5)
            shouldThrow<IllegalArgumentException> { ipq.decreaseKey(Planet.EARTH, 5) }
        }

        test("decreaseKey on absent element throws NoSuchElementException") {
            shouldThrow<NoSuchElementException> {
                enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder()).decreaseKey(Planet.EARTH, 1)
            }
        }
    }

    context("increaseKey") {
        test("increaseKey moves element to later poll position") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.add(Planet.MERCURY, 1); ipq.add(Planet.VENUS, 2)
            ipq.increaseKey(Planet.MERCURY, 10)
            ipq.pollMin() shouldBe Planet.VENUS
        }

        test("increaseKey with new <= current throws IllegalArgumentException") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.add(Planet.EARTH, 5)
            shouldThrow<IllegalArgumentException> { ipq.increaseKey(Planet.EARTH, 5) }
        }

        test("increaseKey on absent element throws NoSuchElementException") {
            shouldThrow<NoSuchElementException> {
                enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder()).increaseKey(Planet.EARTH, 99)
            }
        }
    }

    context("updatePriority") {
        test("updatePriority in decrease direction") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.add(Planet.EARTH, 5); ipq.add(Planet.MARS, 2)
            ipq.updatePriority(Planet.EARTH, 1)
            ipq.pollMin() shouldBe Planet.EARTH
        }

        test("updatePriority in increase direction") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.add(Planet.EARTH, 1); ipq.add(Planet.MARS, 5)
            ipq.updatePriority(Planet.EARTH, 9)
            ipq.pollMin() shouldBe Planet.MARS
        }

        test("updatePriority equal priority is a no-op") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.add(Planet.EARTH, 5); ipq.add(Planet.MARS, 3)
            ipq.updatePriority(Planet.EARTH, 5)
            ipq.getPriority(Planet.EARTH) shouldBe 5
            ipq.pollMin() shouldBe Planet.MARS
        }

        test("updatePriority on absent element throws NoSuchElementException") {
            shouldThrow<NoSuchElementException> {
                enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder()).updatePriority(Planet.EARTH, 5)
            }
        }
    }

    context("element can be re-added after being polled") {
        test("polled element can be re-added with a new priority") {
            val ipq = enumIndexedPriorityQueueOf<Planet, Int>(naturalOrder())
            ipq.add(Planet.EARTH, 3)
            ipq.pollMin()
            ipq.add(Planet.EARTH, 1)
            ipq.getPriority(Planet.EARTH) shouldBe 1
            ipq.pollMin() shouldBe Planet.EARTH
        }
    }
})
