package com.kelvsyc.kotlin.core.structures

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

private enum class Color { RED, GREEN, BLUE, YELLOW }

class EnumDisjointSetTest : FunSpec({

    context("initial state") {
        test("all enum values are pre-registered in elements") {
            val dsu = mutableEnumDisjointSetOf<Color>()
            dsu.elements shouldContainExactlyInAnyOrder Color.entries
        }

        test("each enum value starts as its own singleton partition") {
            val dsu = mutableEnumDisjointSetOf<Color>()
            dsu.partitions shouldBe Color.entries.map { setOf(it) }.toSet()
        }

        test("no two distinct values are initially connected") {
            val dsu = mutableEnumDisjointSetOf<Color>()
            dsu.connected(Color.RED, Color.GREEN).shouldBeFalse()
            dsu.connected(Color.RED, Color.BLUE).shouldBeFalse()
            dsu.connected(Color.GREEN, Color.YELLOW).shouldBeFalse()
        }

        test("each value is connected to itself") {
            val dsu = mutableEnumDisjointSetOf<Color>()
            Color.entries.forEach { dsu.connected(it, it).shouldBeTrue() }
        }
    }

    context("union") {
        test("makes two values connected") {
            val dsu = mutableEnumDisjointSetOf<Color>()
            dsu.union(Color.RED, Color.GREEN)
            dsu.connected(Color.RED, Color.GREEN).shouldBeTrue()
        }

        test("is symmetric") {
            val dsu = mutableEnumDisjointSetOf<Color>()
            dsu.union(Color.RED, Color.GREEN)
            dsu.connected(Color.GREEN, Color.RED).shouldBeTrue()
        }

        test("is idempotent") {
            val dsu = mutableEnumDisjointSetOf<Color>()
            dsu.union(Color.RED, Color.GREEN)
            dsu.union(Color.RED, Color.GREEN)
            dsu.connected(Color.RED, Color.GREEN).shouldBeTrue()
        }

        test("is transitive") {
            val dsu = mutableEnumDisjointSetOf<Color>()
            dsu.union(Color.RED, Color.GREEN)
            dsu.union(Color.GREEN, Color.BLUE)
            dsu.connected(Color.RED, Color.BLUE).shouldBeTrue()
        }

        test("does not connect elements from different classes") {
            val dsu = mutableEnumDisjointSetOf<Color>()
            dsu.union(Color.RED, Color.GREEN)
            dsu.connected(Color.RED, Color.BLUE).shouldBeFalse()
            dsu.connected(Color.GREEN, Color.YELLOW).shouldBeFalse()
        }
    }

    context("partitions") {
        test("reflects merged classes") {
            val dsu = mutableEnumDisjointSetOf<Color>()
            dsu.union(Color.RED, Color.GREEN)
            dsu.union(Color.BLUE, Color.YELLOW)
            dsu.partitions shouldBe setOf(
                setOf(Color.RED, Color.GREEN),
                setOf(Color.BLUE, Color.YELLOW),
            )
        }

        test("reflects transitive merge") {
            val dsu = mutableEnumDisjointSetOf<Color>()
            dsu.union(Color.RED, Color.GREEN)
            dsu.union(Color.GREEN, Color.BLUE)
            dsu.partitions shouldBe setOf(
                setOf(Color.RED, Color.GREEN, Color.BLUE),
                setOf(Color.YELLOW),
            )
        }
    }

    context("getPartition") {
        test("returns all values in the same class") {
            val dsu = mutableEnumDisjointSetOf<Color>()
            dsu.union(Color.RED, Color.GREEN)
            dsu.union(Color.GREEN, Color.BLUE)
            dsu.getPartition(Color.RED) shouldContainExactlyInAnyOrder
                listOf(Color.RED, Color.GREEN, Color.BLUE)
        }

        test("does not include values from other classes") {
            val dsu = mutableEnumDisjointSetOf<Color>()
            dsu.union(Color.RED, Color.GREEN)
            dsu.getPartition(Color.RED) shouldContainExactlyInAnyOrder
                listOf(Color.RED, Color.GREEN)
        }
    }

    context("buildEnumDisjointSet") {
        test("returns a read-only DisjointSet") {
            val dsu: DisjointSet<Color> = buildEnumDisjointSet {
                union(Color.RED, Color.GREEN)
                union(Color.BLUE, Color.YELLOW)
            }
            dsu.connected(Color.RED, Color.GREEN).shouldBeTrue()
            dsu.connected(Color.BLUE, Color.YELLOW).shouldBeTrue()
            dsu.connected(Color.RED, Color.BLUE).shouldBeFalse()
        }
    }
})
