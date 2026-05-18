package com.kelvsyc.kotlin.commons.collections.collect

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.collections4.Bag

class BagsTest : FunSpec({

    context("hashBagOf") {
        test("empty bag") {
            val bag = hashBagOf<String>()
            bag.size shouldBe 0
        }
        test("bag with vararg elements counts correctly") {
            val bag = hashBagOf("a", "a", "b")
            bag.getCount("a") shouldBe 2
            bag.getCount("b") shouldBe 1
            bag.size shouldBe 3
        }
    }

    context("treeBagOf") {
        test("empty sorted bag") {
            val bag = treeBagOf<String>()
            bag.size shouldBe 0
        }
        test("sorted bag iterates in natural order") {
            val bag = treeBagOf("b", "a", "a")
            bag.uniqueSet().toList() shouldBe listOf("a", "b")
        }
    }

    context("buildBag") {
        test("builder actions are applied") {
            val bag = buildBag<String> {
                add("x")
                add("x")
                add("y")
            }
            bag.getCount("x") shouldBe 2
            bag.getCount("y") shouldBe 1
        }
    }

    context("buildTreeBag") {
        test("builder produces sorted bag") {
            val bag = buildTreeBag<String> { add("b"); add("a") }
            bag.uniqueSet().toList() shouldBe listOf("a", "b")
        }
    }

    context("plusAssign element") {
        test("adds single element") {
            val bag = hashBagOf("a")
            bag += "b"
            bag.getCount("b") shouldBe 1
        }
    }

    context("plusAssign bag") {
        test("merges another bag in-place") {
            val bag = hashBagOf("a")
            val other = hashBagOf("b", "b")
            bag += other
            bag.getCount("a") shouldBe 1
            bag.getCount("b") shouldBe 2
        }
    }

    context("minusAssign element") {
        test("removes one occurrence") {
            val bag = hashBagOf("a", "a")
            bag -= "a"
            bag.getCount("a") shouldBe 1
        }
    }

    context("minusAssign bag") {
        test("removes another bag's elements in-place") {
            val bag = hashBagOf("a", "a", "b")
            val other = hashBagOf("a")
            bag -= other
            bag.getCount("a") shouldBe 1
            bag.getCount("b") shouldBe 1
        }
    }
})
