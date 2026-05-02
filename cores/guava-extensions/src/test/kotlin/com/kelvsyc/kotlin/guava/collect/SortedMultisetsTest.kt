package com.kelvsyc.kotlin.guava.collect

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SortedMultisetsTest : FunSpec({

    context("sortedMultisetOf") {
        test("empty") { sortedMultisetOf<String>().isEmpty() shouldBe true }
        test("natural order — first/last correct") {
            val m = sortedMultisetOf("b", "a", "c")
            m.firstEntry()!!.element shouldBe "a"
            m.lastEntry()!!.element shouldBe "c"
        }
        test("natural order — duplicate counts") {
            val m = sortedMultisetOf("a", "a", "b")
            m.count("a") shouldBe 2
        }
        test("comparator — reverse order") {
            val m = sortedMultisetOf(reverseOrder(), "b", "a", "c")
            m.firstEntry()!!.element shouldBe "c"
        }
    }

    context("buildSortedMultiset") {
        test("natural order builder") {
            val m = buildSortedMultiset<Int> {
                add(3)
                add(1)
                add(2)
            }
            m.firstEntry()!!.element shouldBe 1
        }
        test("comparator builder") {
            val m = buildSortedMultiset<String>(reverseOrder()) {
                add("a")
                add("z")
            }
            m.firstEntry()!!.element shouldBe "z"
        }
    }
})
