package com.kelvsyc.kotlin.guava.collect

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MultisetsTest : FunSpec({

    context("multisetOf") {
        test("empty") { multisetOf<String>().isEmpty() shouldBe true }
        test("single element") { multisetOf("a").count("a") shouldBe 1 }
        test("two same elements counted correctly") { multisetOf("a", "a").count("a") shouldBe 2 }
        test("vararg — counts correct") {
            val m = multisetOf("a", "b", "a", "c")
            m.count("a") shouldBe 2
            m.count("b") shouldBe 1
            m.size shouldBe 4
        }
    }

    context("buildMultiset") {
        test("builder adds elements") {
            val m = buildMultiset<String> {
                add("x")
                addCopies("x", 3)
            }
            m.count("x") shouldBe 4
        }
    }

    context("toImmutableMultiset") {
        test("from list") {
            val m = listOf("a", "b", "a").toImmutableMultiset()
            m.count("a") shouldBe 2
            m.count("b") shouldBe 1
        }
    }

    context("hashMultisetOf") {
        test("empty is mutable") {
            val m = hashMultisetOf<String>()
            m.add("z")
            m.count("z") shouldBe 1
        }
        test("with elements") {
            val m = hashMultisetOf("a", "a", "b")
            m.count("a") shouldBe 2
        }
    }

    context("linkedMultisetOf") {
        test("with elements") {
            val m = linkedMultisetOf("c", "a", "b")
            m.size shouldBe 3
        }
    }

    context("treeMultisetOf") {
        test("natural order") {
            val m = treeMultisetOf("b", "a", "c")
            m.first() shouldBe "a"
        }
        test("comparator — reverse order") {
            val m = treeMultisetOf(reverseOrder(), "b", "a", "c")
            m.first() shouldBe "c"
        }
    }

    context("enumMultisetOf") {
        test("empty") {
            val m = enumMultisetOf<java.util.concurrent.TimeUnit>()
            m.isEmpty() shouldBe true
        }
        test("with elements") {
            val m = enumMultisetOf(java.util.concurrent.TimeUnit.SECONDS, java.util.concurrent.TimeUnit.SECONDS)
            m.count(java.util.concurrent.TimeUnit.SECONDS) shouldBe 2
        }
    }
})
