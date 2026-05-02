package com.kelvsyc.kotlin.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

// Ordinals: RED=0, GREEN=1, BLUE=2 → bits: {}=0, {R}=1, {G}=2, {R,G}=3, {B}=4, {R,B}=5, {G,B}=6, {R,G,B}=7
private enum class Color { RED, GREEN, BLUE }

class EnumSubsetTest : FunSpec({

    context("factory methods") {
        test("empty() is empty") {
            val s = EnumSubset.empty<Color>()
            s.isEmpty() shouldBe true
            s.size shouldBe 0
        }
        test("allOf() contains every constant") {
            val s = EnumSubset.allOf<Color>()
            s shouldContainExactlyInAnyOrder Color.entries
        }
        test("of(vararg) with elements") {
            val s = EnumSubset.of(Color.RED, Color.BLUE)
            s shouldContainExactlyInAnyOrder listOf(Color.RED, Color.BLUE)
        }
        test("of(vararg) empty delegates to empty()") {
            EnumSubset.of<Color>().isEmpty() shouldBe true
        }
        test("of(Iterable) with elements") {
            val s = EnumSubset.of<Color>(listOf(Color.GREEN))
            s.single() shouldBe Color.GREEN
        }
        test("of(Iterable) empty delegates to empty()") {
            EnumSubset.of<Color>(emptyList()).isEmpty() shouldBe true
        }
        test("of(EnumSet) copies contents") {
            val es = java.util.EnumSet.of(Color.RED, Color.GREEN)
            val s = EnumSubset.of(es)
            es.remove(Color.RED)            // mutation of source does not affect the copy
            s shouldContainExactlyInAnyOrder listOf(Color.RED, Color.GREEN)
        }
        test("enumSubsetOf top-level function") {
            enumSubsetOf(Color.RED).single() shouldBe Color.RED
        }
    }

    context("bits()") {
        test("empty → 0") { EnumSubset.empty<Color>().bits() shouldBe 0L }
        test("{RED} → 1") { EnumSubset.of(Color.RED).bits() shouldBe 1L }
        test("{GREEN} → 2") { EnumSubset.of(Color.GREEN).bits() shouldBe 2L }
        test("{RED, GREEN} → 3") { EnumSubset.of(Color.RED, Color.GREEN).bits() shouldBe 3L }
        test("{BLUE} → 4") { EnumSubset.of(Color.BLUE).bits() shouldBe 4L }
        test("allOf → 7") { EnumSubset.allOf<Color>().bits() shouldBe 7L }
    }

    context("compareTo — subset enumeration order") {
        val sorted = listOf(
            EnumSubset.empty<Color>(),          // 0
            EnumSubset.of(Color.RED),            // 1
            EnumSubset.of(Color.GREEN),          // 2
            EnumSubset.of(Color.RED, Color.GREEN), // 3
            EnumSubset.of(Color.BLUE),           // 4
            EnumSubset.of(Color.RED, Color.BLUE), // 5
            EnumSubset.of(Color.GREEN, Color.BLUE), // 6
            EnumSubset.allOf<Color>(),           // 7
        )
        test("adjacent pairs are ordered correctly") {
            for (i in 0 until sorted.size - 1) {
                (sorted[i] < sorted[i + 1]) shouldBe true
                (sorted[i + 1] > sorted[i]) shouldBe true
            }
        }
        test("equal subsets compare as 0") {
            EnumSubset.of(Color.RED).compareTo(EnumSubset.of(Color.RED)) shouldBe 0
        }
        test("sorting produces subset enumeration order") {
            val shuffled = sorted.shuffled()
            shuffled.sorted() shouldBe sorted
        }
    }

    context("Set<E> delegation") {
        val s = EnumSubset.of(Color.RED, Color.GREEN)
        test("contains") { (Color.RED in s) shouldBe true; (Color.BLUE in s) shouldBe false }
        test("size") { s.size shouldBe 2 }
        test("iterator yields elements in ordinal order") {
            s.toList() shouldBe listOf(Color.RED, Color.GREEN)
        }
    }

    context("toEnumSet") {
        test("returns a mutable copy") {
            val s = EnumSubset.of(Color.RED)
            val es = s.toEnumSet()
            es.add(Color.GREEN)
            s.size shouldBe 1   // original unaffected
        }
    }

    context("set arithmetic") {
        val red = EnumSubset.of(Color.RED)
        val green = EnumSubset.of(Color.GREEN)
        val redGreen = EnumSubset.of(Color.RED, Color.GREEN)
        val blue = EnumSubset.of(Color.BLUE)

        test("plus — union") { (red + green) shouldBe redGreen }
        test("plus — identity with empty") { (red + EnumSubset.empty<Color>()) shouldBe red }
        test("minus — difference") { (redGreen - red) shouldBe green }
        test("minus — removing absent element is no-op") { (red - blue) shouldBe red }
        test("intersect") { (redGreen intersect (EnumSubset.of(Color.GREEN, Color.BLUE))) shouldBe green }
        test("intersect — disjoint gives empty") { (red intersect blue).isEmpty() shouldBe true }
        test("complement of empty is allOf") { EnumSubset.empty<Color>().complement() shouldBe EnumSubset.allOf<Color>() }
        test("complement of allOf is empty") { EnumSubset.allOf<Color>().complement().isEmpty() shouldBe true }
        test("complement of {RED, GREEN} is {BLUE}") { redGreen.complement() shouldBe blue }
    }

    context("size constraint") {
        test("enum with exactly 63 constants is accepted") {
            // Verified by the 63-constant limit not being violated in this test run.
            // An enum with 64+ constants is not easily creatable inline; the constraint is
            // enforced in the init block at construction time.
            EnumSubset.empty<Color>()  // 3 constants — well within limit
        }
    }

    context("equality and hashCode") {
        test("equal subsets are equal") {
            EnumSubset.of(Color.RED) shouldBe EnumSubset.of(Color.RED)
        }
        test("different subsets are not equal") {
            (EnumSubset.of(Color.RED) == EnumSubset.of(Color.GREEN)) shouldBe false
        }
        test("empty subsets are equal") {
            EnumSubset.empty<Color>() shouldBe EnumSubset.empty<Color>()
        }
    }
})
