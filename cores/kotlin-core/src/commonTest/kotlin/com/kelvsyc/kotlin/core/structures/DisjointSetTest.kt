package com.kelvsyc.kotlin.core.structures

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class DisjointSetTest : FunSpec({

    context("find on unregistered element") {
        test("returns the element itself") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.find("A") shouldBe "A"
        }

        test("does not add element to elements set") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.find("A")
            dsu.elements shouldBe emptySet()
        }
    }

    context("connected before any union") {
        test("two unregistered elements are not connected") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.connected("A", "B").shouldBeFalse()
        }

        test("element is connected to itself") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.connected("A", "A").shouldBeTrue()
        }
    }

    context("union") {
        test("registers both elements") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.union("A", "B")
            dsu.elements shouldContainExactlyInAnyOrder listOf("A", "B")
        }

        test("makes elements connected") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.union("A", "B")
            dsu.connected("A", "B").shouldBeTrue()
        }

        test("is symmetric") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.union("A", "B")
            dsu.connected("B", "A").shouldBeTrue()
        }

        test("is idempotent") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.union("A", "B")
            dsu.union("A", "B")
            dsu.elements shouldContainExactlyInAnyOrder listOf("A", "B")
            dsu.connected("A", "B").shouldBeTrue()
        }

        test("is transitive") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.union("A", "B")
            dsu.union("B", "C")
            dsu.connected("A", "C").shouldBeTrue()
        }

        test("does not connect elements from different classes") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.union("A", "B")
            dsu.union("C", "D")
            dsu.connected("A", "C").shouldBeFalse()
            dsu.connected("A", "D").shouldBeFalse()
            dsu.connected("B", "C").shouldBeFalse()
        }

        test("registers element not in supplied universe") {
            val dsu = mutableDisjointSetOf(listOf("A", "B"))
            dsu.union("A", "Z")
            dsu.elements shouldContainExactlyInAnyOrder listOf("A", "B", "Z")
            dsu.connected("A", "Z").shouldBeTrue()
        }
    }

    context("partitions") {
        test("empty structure has no partitions") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.partitions shouldBe emptySet()
        }

        test("reflects singletons before any union") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.union("A", "A")
            dsu.partitions shouldBe setOf(setOf("A"))
        }

        test("reflects merged classes") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.union("A", "B")
            dsu.union("C", "D")
            dsu.partitions shouldBe setOf(setOf("A", "B"), setOf("C", "D"))
        }

        test("reflects transitive merge") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.union("A", "B")
            dsu.union("B", "C")
            dsu.partitions shouldBe setOf(setOf("A", "B", "C"))
        }
    }

    context("getPartition") {
        test("unregistered element returns singleton") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.getPartition("A") shouldBe setOf("A")
        }

        test("registered singleton returns singleton") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.union("A", "A")
            dsu.getPartition("A") shouldBe setOf("A")
        }

        test("returns all elements in the same class") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.union("A", "B")
            dsu.union("B", "C")
            dsu.getPartition("A") shouldContainExactlyInAnyOrder listOf("A", "B", "C")
            dsu.getPartition("B") shouldContainExactlyInAnyOrder listOf("A", "B", "C")
            dsu.getPartition("C") shouldContainExactlyInAnyOrder listOf("A", "B", "C")
        }

        test("does not include elements from other classes") {
            val dsu = mutableDisjointSetOf<String>()
            dsu.union("A", "B")
            dsu.union("C", "D")
            dsu.getPartition("A") shouldContainExactlyInAnyOrder listOf("A", "B")
        }
    }

    context("known universe factory") {
        test("pre-populates elements") {
            val dsu = mutableDisjointSetOf(listOf("A", "B", "C"))
            dsu.elements shouldContainExactlyInAnyOrder listOf("A", "B", "C")
        }

        test("all universe elements start as singletons") {
            val dsu = mutableDisjointSetOf(listOf("A", "B", "C"))
            dsu.connected("A", "B").shouldBeFalse()
            dsu.connected("B", "C").shouldBeFalse()
        }

        test("partitions contains one set per universe element initially") {
            val dsu = mutableDisjointSetOf(listOf("A", "B", "C"))
            dsu.partitions shouldBe setOf(setOf("A"), setOf("B"), setOf("C"))
        }
    }

    context("buildDisjointSet") {
        test("returns a read-only DisjointSet") {
            val dsu: DisjointSet<String> = buildDisjointSet {
                union("A", "B")
                union("C", "D")
            }
            dsu.connected("A", "B").shouldBeTrue()
            dsu.connected("C", "D").shouldBeTrue()
            dsu.connected("A", "C").shouldBeFalse()
        }

        test("partitions reflect built state") {
            val dsu: DisjointSet<String> = buildDisjointSet {
                union("A", "B")
                union("B", "C")
            }
            dsu.partitions shouldBe setOf(setOf("A", "B", "C"))
        }
    }
})
