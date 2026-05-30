package com.kelvsyc.kotlin.core.structures

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

private enum class Star { SIRIUS, VEGA, ARCTURUS, RIGEL, BETELGEUSE }

class EnumIndexedPriorityDequeTest : FunSpec({

    context("empty state") {
        test("isEmpty on fresh deque") {
            enumIndexedPriorityDequeOf<Star, Int>(naturalOrder()).isEmpty().shouldBeTrue()
        }

        test("peekMin returns null on empty deque") {
            enumIndexedPriorityDequeOf<Star, Int>(naturalOrder()).peekMin().shouldBeNull()
        }

        test("peekMax returns null on empty deque") {
            enumIndexedPriorityDequeOf<Star, Int>(naturalOrder()).peekMax().shouldBeNull()
        }
    }

    context("single element") {
        test("peekMin and peekMax return the same element") {
            val ipd = enumIndexedPriorityDequeOf<Star, Int>(naturalOrder())
            ipd.add(Star.VEGA, 5)
            ipd.peekMin() shouldBe Star.VEGA
            ipd.peekMax() shouldBe Star.VEGA
        }

        test("pollMax empties the deque") {
            val ipd = enumIndexedPriorityDequeOf<Star, Int>(naturalOrder())
            ipd.add(Star.VEGA, 5)
            ipd.pollMax() shouldBe Star.VEGA
            ipd.isEmpty().shouldBeTrue()
        }
    }

    context("pollMin ordering") {
        test("pollMin extracts enum elements in ascending priority order") {
            val ipd = enumIndexedPriorityDequeOf<Star, Int>(naturalOrder())
            ipd.add(Star.RIGEL, 4); ipd.add(Star.SIRIUS, 1); ipd.add(Star.ARCTURUS, 3)
            ipd.pollMin() shouldBe Star.SIRIUS
            ipd.pollMin() shouldBe Star.ARCTURUS
            ipd.pollMin() shouldBe Star.RIGEL
        }
    }

    context("pollMax ordering") {
        test("pollMax extracts enum elements in descending priority order") {
            val ipd = enumIndexedPriorityDequeOf<Star, Int>(naturalOrder())
            ipd.add(Star.SIRIUS, 1); ipd.add(Star.VEGA, 2); ipd.add(Star.ARCTURUS, 3)
            ipd.pollMax() shouldBe Star.ARCTURUS
            ipd.pollMax() shouldBe Star.VEGA
            ipd.pollMax() shouldBe Star.SIRIUS
        }
    }

    context("interleaved pollMin and pollMax") {
        test("alternating extraction from both ends is correct") {
            val ipd = enumIndexedPriorityDequeOf<Star, Int>(naturalOrder())
            ipd.add(Star.SIRIUS, 1)
            ipd.add(Star.VEGA, 2)
            ipd.add(Star.ARCTURUS, 3)
            ipd.add(Star.RIGEL, 4)
            ipd.add(Star.BETELGEUSE, 5)
            ipd.pollMin() shouldBe Star.SIRIUS
            ipd.pollMax() shouldBe Star.BETELGEUSE
            ipd.pollMin() shouldBe Star.VEGA
            ipd.pollMax() shouldBe Star.RIGEL
            ipd.pollMin() shouldBe Star.ARCTURUS
        }
    }

    context("add preconditions") {
        test("add duplicate throws IllegalArgumentException") {
            val ipd = enumIndexedPriorityDequeOf<Star, Int>(naturalOrder())
            ipd.add(Star.VEGA, 5)
            shouldThrow<IllegalArgumentException> { ipd.add(Star.VEGA, 99) }
        }
    }

    context("contains and getPriority") {
        test("contains returns true for present element") {
            val ipd = enumIndexedPriorityDequeOf<Star, Int>(naturalOrder())
            ipd.add(Star.VEGA, 3)
            ipd.contains(Star.VEGA).shouldBeTrue()
        }

        test("contains returns false for element not added") {
            val ipd = enumIndexedPriorityDequeOf<Star, Int>(naturalOrder())
            ipd.add(Star.VEGA, 3)
            ipd.contains(Star.RIGEL).shouldBeFalse()
        }

        test("getPriority returns current priority") {
            val ipd = enumIndexedPriorityDequeOf<Star, Int>(naturalOrder())
            ipd.add(Star.VEGA, 7)
            ipd.getPriority(Star.VEGA) shouldBe 7
        }
    }

    context("decreaseKey") {
        test("decreaseKey moves element to earlier pollMin position") {
            val ipd = enumIndexedPriorityDequeOf<Star, Int>(naturalOrder())
            ipd.add(Star.SIRIUS, 1); ipd.add(Star.VEGA, 9)
            ipd.decreaseKey(Star.VEGA, 0)
            ipd.pollMin() shouldBe Star.VEGA
        }

        test("decreaseKey on the max element moves it away from the max end") {
            val ipd = enumIndexedPriorityDequeOf<Star, Int>(naturalOrder())
            ipd.add(Star.SIRIUS, 1); ipd.add(Star.VEGA, 3); ipd.add(Star.ARCTURUS, 9)
            ipd.decreaseKey(Star.ARCTURUS, 2)
            ipd.pollMax() shouldBe Star.VEGA
        }

        test("decreaseKey with new >= current throws IllegalArgumentException") {
            val ipd = enumIndexedPriorityDequeOf<Star, Int>(naturalOrder())
            ipd.add(Star.VEGA, 5)
            shouldThrow<IllegalArgumentException> { ipd.decreaseKey(Star.VEGA, 5) }
        }

        test("decreaseKey on absent element throws NoSuchElementException") {
            shouldThrow<NoSuchElementException> {
                enumIndexedPriorityDequeOf<Star, Int>(naturalOrder()).decreaseKey(Star.VEGA, 1)
            }
        }
    }

    context("increaseKey") {
        test("increaseKey moves element to later pollMin position") {
            val ipd = enumIndexedPriorityDequeOf<Star, Int>(naturalOrder())
            ipd.add(Star.SIRIUS, 1); ipd.add(Star.VEGA, 5)
            ipd.increaseKey(Star.SIRIUS, 10)
            ipd.pollMin() shouldBe Star.VEGA
            ipd.pollMax() shouldBe Star.SIRIUS
        }

        test("increaseKey with new <= current throws IllegalArgumentException") {
            val ipd = enumIndexedPriorityDequeOf<Star, Int>(naturalOrder())
            ipd.add(Star.VEGA, 5)
            shouldThrow<IllegalArgumentException> { ipd.increaseKey(Star.VEGA, 5) }
        }

        test("increaseKey on absent element throws NoSuchElementException") {
            shouldThrow<NoSuchElementException> {
                enumIndexedPriorityDequeOf<Star, Int>(naturalOrder()).increaseKey(Star.VEGA, 99)
            }
        }
    }

    context("updatePriority") {
        test("updatePriority both directions") {
            val ipd = enumIndexedPriorityDequeOf<Star, Int>(naturalOrder())
            ipd.add(Star.SIRIUS, 3); ipd.add(Star.VEGA, 5)
            ipd.updatePriority(Star.SIRIUS, 1)
            ipd.pollMin() shouldBe Star.SIRIUS
            ipd.updatePriority(Star.VEGA, 9)
            ipd.pollMax() shouldBe Star.VEGA
        }

        test("updatePriority equal priority is no-op") {
            val ipd = enumIndexedPriorityDequeOf<Star, Int>(naturalOrder())
            ipd.add(Star.VEGA, 5)
            ipd.updatePriority(Star.VEGA, 5)
            ipd.getPriority(Star.VEGA) shouldBe 5
        }

        test("updatePriority on absent element throws NoSuchElementException") {
            shouldThrow<NoSuchElementException> {
                enumIndexedPriorityDequeOf<Star, Int>(naturalOrder()).updatePriority(Star.VEGA, 5)
            }
        }
    }

    context("element can be re-added after being polled") {
        test("polled element can be re-added") {
            val ipd = enumIndexedPriorityDequeOf<Star, Int>(naturalOrder())
            ipd.add(Star.VEGA, 3)
            ipd.pollMin()
            ipd.add(Star.VEGA, 1)
            ipd.getPriority(Star.VEGA) shouldBe 1
        }
    }
})
