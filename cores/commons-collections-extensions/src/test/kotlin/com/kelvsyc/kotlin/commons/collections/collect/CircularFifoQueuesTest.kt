package com.kelvsyc.kotlin.commons.collections.collect

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CircularFifoQueuesTest : FunSpec({

    context("circularFifoQueueOf capacity only") {
        test("empty queue with capacity") {
            val q = circularFifoQueueOf<Int>(3)
            q.size shouldBe 0
            q.maxSize() shouldBe 3
        }
    }

    context("circularFifoQueueOf with elements") {
        test("queue contains initial elements") {
            val q = circularFifoQueueOf(3, 10, 20, 30)
            q.size shouldBe 3
            q.contains(10) shouldBe true
        }
        test("adding beyond capacity evicts oldest element") {
            val q = circularFifoQueueOf(2, 1, 2)
            q += 3
            q.contains(1) shouldBe false
            q.contains(2) shouldBe true
            q.contains(3) shouldBe true
        }
    }

    context("plusAssign operator") {
        test("adds element to tail") {
            val q = circularFifoQueueOf<Int>(5)
            q += 42
            q.size shouldBe 1
            q.peek() shouldBe 42
        }
    }

    context("minusAssign operator") {
        test("removes element by value") {
            val q = circularFifoQueueOf(3, 1, 2, 3)
            q -= 2
            q.contains(2) shouldBe false
            q.size shouldBe 2
        }
    }

    context("in operator") {
        test("element in queue returns true") {
            val q = circularFifoQueueOf(3, 10, 20)
            (10 in q) shouldBe true
        }
        test("absent element returns false") {
            val q = circularFifoQueueOf(3, 10, 20)
            (99 in q) shouldBe false
        }
    }
})
