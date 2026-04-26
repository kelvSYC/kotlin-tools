package com.kelvsyc.kotlin.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class PeekingIteratorTest : FunSpec({
    context("PeekingIterator via makePeeking") {
        test("iterates all elements without peeking") {
            val it = listOf(1, 2, 3).iterator().makePeeking()
            it.next() shouldBe 1
            it.next() shouldBe 2
            it.next() shouldBe 3
        }

        test("hasNext returns false on empty iterator") {
            val it = emptyList<Int>().iterator().makePeeking()
            it.hasNext() shouldBe false
        }

        test("hasNext returns true when elements remain") {
            val it = listOf(1).iterator().makePeeking()
            it.hasNext() shouldBe true
        }

        test("peek returns next element without consuming it") {
            val it = listOf(10, 20).iterator().makePeeking()
            it.peek() shouldBe 10
            it.next() shouldBe 10
            it.next() shouldBe 20
        }

        test("repeated peek returns the same element") {
            val it = listOf(5).iterator().makePeeking()
            it.peek() shouldBe 5
            it.peek() shouldBe 5
        }

        test("hasNext returns true after peek even when at last element") {
            val it = listOf(1).iterator().makePeeking()
            it.peek() shouldBe 1
            it.hasNext() shouldBe true
        }

        test("hasNext returns false after consuming peeked last element") {
            val it = listOf(1).iterator().makePeeking()
            it.peek()
            it.next()
            it.hasNext() shouldBe false
        }

        test("peek on exhausted iterator throws NoSuchElementException") {
            val it = emptyList<Int>().iterator().makePeeking()
            shouldThrow<NoSuchElementException> { it.peek() }
        }

        test("next on exhausted iterator (no peek) throws NoSuchElementException") {
            val it = emptyList<Int>().iterator().makePeeking()
            shouldThrow<NoSuchElementException> { it.next() }
        }

        test("interleaving peek and next traverses sequence correctly") {
            val it = listOf(1, 2, 3).iterator().makePeeking()
            it.peek() shouldBe 1
            it.next() shouldBe 1
            it.next() shouldBe 2
            it.peek() shouldBe 3
            it.peek() shouldBe 3
            it.next() shouldBe 3
            it.hasNext() shouldBe false
        }

        test("makePeeking on a PeekingIterator returns the same instance") {
            val it = listOf(1).iterator().makePeeking()
            it.makePeeking() shouldBeSameInstanceAs it
        }
    }

    context("MutablePeekingIterator via makePeeking") {
        test("iterates all elements without peeking") {
            val it = mutableListOf(1, 2, 3).iterator().makePeeking()
            it.next() shouldBe 1
            it.next() shouldBe 2
            it.next() shouldBe 3
        }

        test("peek returns next element without consuming it") {
            val it = mutableListOf(10, 20).iterator().makePeeking()
            it.peek() shouldBe 10
            it.next() shouldBe 10
        }

        test("remove after next (no peek) removes the element") {
            val list = mutableListOf(1, 2, 3)
            val it = list.iterator().makePeeking()
            it.next() shouldBe 1
            it.remove()
            list shouldBe listOf(2, 3)
        }

        test("remove after peek then next removes the peeked element") {
            val list = mutableListOf(1, 2, 3)
            val it = list.iterator().makePeeking()
            it.peek() shouldBe 1
            it.next() shouldBe 1
            it.remove()
            list shouldBe listOf(2, 3)
        }

        test("remove after peek without consuming throws IllegalStateException") {
            val list = mutableListOf(1, 2, 3)
            val it = list.iterator().makePeeking()
            it.peek()
            shouldThrow<IllegalStateException> { it.remove() }
        }

        test("makePeeking on a MutablePeekingIterator returns the same instance") {
            val it = mutableListOf(1).iterator().makePeeking()
            it.makePeeking() shouldBeSameInstanceAs it
        }
    }
})
