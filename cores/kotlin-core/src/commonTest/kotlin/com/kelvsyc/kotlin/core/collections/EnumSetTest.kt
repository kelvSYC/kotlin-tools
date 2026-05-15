package com.kelvsyc.kotlin.core.collections

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.enums.enumEntries

private enum class Hue { RED, GREEN, BLUE }
private enum class EmptyHue

class EnumSetTest : FunSpec({

    context("construction") {
        test("emptyEnumSet produces an empty set") {
            val s = emptyEnumSet<Hue>()
            s.isEmpty().shouldBeTrue()
            s.size shouldBe 0
        }

        test("emptyEnumSet exposes enumEntries") {
            val s = emptyEnumSet<Hue>()
            s.enumEntries shouldBe enumEntries<Hue>()
        }

        test("enumSetOf with no args produces empty set") {
            enumSetOf<Hue>().isEmpty().shouldBeTrue()
        }

        test("enumSetOf with elements") {
            val s = enumSetOf(Hue.RED, Hue.BLUE)
            s.size shouldBe 2
            s.contains(Hue.RED).shouldBeTrue()
            s.contains(Hue.BLUE).shouldBeTrue()
        }

        test("enumSetOf ignores duplicates") {
            val s = enumSetOf(Hue.RED, Hue.RED)
            s.size shouldBe 1
        }

        test("buildEnumSet") {
            val s = buildEnumSet<Hue> {
                add(Hue.GREEN)
                add(Hue.BLUE)
            }
            s.size shouldBe 2
            s.contains(Hue.GREEN).shouldBeTrue()
        }

        test("toEnumSet from Iterable") {
            val s = listOf(Hue.RED, Hue.GREEN).toEnumSet()
            s.size shouldBe 2
            s.contains(Hue.RED).shouldBeTrue()
        }

        test("mutableEnumSetOf empty") {
            mutableEnumSetOf<Hue>().isEmpty().shouldBeTrue()
        }

        test("mutableEnumSetOf with elements") {
            val s = mutableEnumSetOf(Hue.RED)
            s.size shouldBe 1
        }

        test("toMutableEnumSet from Iterable") {
            val s = listOf(Hue.RED).toMutableEnumSet()
            s.contains(Hue.RED).shouldBeTrue()
            s.add(Hue.GREEN).shouldBeTrue()
            s.size shouldBe 2
        }

        test("empty enum type") {
            val s = emptyEnumSet<EmptyHue>()
            s.size shouldBe 0
            s.enumEntries.size shouldBe 0
        }
    }

    context("contains") {
        test("contains true for present element") {
            enumSetOf(Hue.RED).contains(Hue.RED).shouldBeTrue()
        }

        test("contains false for absent element") {
            enumSetOf(Hue.RED).contains(Hue.GREEN).shouldBeFalse()
        }
    }

    context("iteration order") {
        test("iterates in ordinal order regardless of insertion order") {
            val s = enumSetOf(Hue.BLUE, Hue.RED, Hue.GREEN)
            s.toList().shouldContainExactly(Hue.RED, Hue.GREEN, Hue.BLUE)
        }
    }

    context("mutation") {
        test("add returns true for new element") {
            val s = mutableEnumSetOf<Hue>()
            s.add(Hue.RED).shouldBeTrue()
            s.size shouldBe 1
        }

        test("add returns false for existing element") {
            val s = mutableEnumSetOf(Hue.RED)
            s.add(Hue.RED).shouldBeFalse()
            s.size shouldBe 1
        }

        test("remove returns true for present element") {
            val s = mutableEnumSetOf(Hue.RED)
            s.remove(Hue.RED).shouldBeTrue()
            s.size shouldBe 0
            s.contains(Hue.RED).shouldBeFalse()
        }

        test("remove returns false for absent element") {
            mutableEnumSetOf<Hue>().remove(Hue.RED).shouldBeFalse()
        }

        test("clear empties the set") {
            val s = mutableEnumSetOf(Hue.RED, Hue.GREEN, Hue.BLUE)
            s.clear()
            s.isEmpty().shouldBeTrue()
            s.size shouldBe 0
        }

        test("addAll from collection") {
            val s = mutableEnumSetOf<Hue>()
            s.addAll(listOf(Hue.RED, Hue.BLUE)).shouldBeTrue()
            s.size shouldBe 2
        }

        test("removeAll") {
            val s = mutableEnumSetOf(Hue.RED, Hue.GREEN, Hue.BLUE)
            s.removeAll(listOf(Hue.RED, Hue.BLUE)).shouldBeTrue()
            s.size shouldBe 1
            s.contains(Hue.GREEN).shouldBeTrue()
        }

        test("retainAll") {
            val s = mutableEnumSetOf(Hue.RED, Hue.GREEN, Hue.BLUE)
            s.retainAll(listOf(Hue.GREEN, Hue.BLUE)).shouldBeTrue()
            s.size shouldBe 2
            s.contains(Hue.RED).shouldBeFalse()
        }
    }

    context("equality") {
        test("equal to a regular set with same elements") {
            val enumSet = enumSetOf(Hue.RED, Hue.GREEN)
            val regularSet = setOf(Hue.RED, Hue.GREEN)
            enumSet shouldBe regularSet
            regularSet shouldBe enumSet
        }

        test("not equal when different elements") {
            enumSetOf(Hue.RED) shouldNotBe enumSetOf(Hue.GREEN)
        }

        test("empty enum sets are equal") {
            emptyEnumSet<Hue>() shouldBe emptyEnumSet<Hue>()
        }

        test("hashCode consistent with Set contract") {
            val enumSet = enumSetOf(Hue.RED, Hue.GREEN)
            val regularSet = setOf(Hue.RED, Hue.GREEN)
            enumSet.hashCode() shouldBe regularSet.hashCode()
        }
    }

    context("iterator remove") {
        test("iterator remove") {
            val s = mutableEnumSetOf(Hue.RED, Hue.GREEN, Hue.BLUE)
            val iter = s.iterator()
            iter.next()
            iter.remove()
            s.size shouldBe 2
            s.contains(Hue.RED).shouldBeFalse()
        }
    }
})
