package com.kelvsyc.kotlin.core.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.enums.enumEntries

private enum class Season { SPRING, SUMMER, AUTUMN, WINTER }
private enum class EmptySeason

class EnumSetMultisetTest : FunSpec({

    context("construction") {
        test("emptyEnumSetMultiset produces an empty multiset") {
            val m = emptyEnumSetMultiset<Season>()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }

        test("emptyEnumSetMultiset exposes enumEntries") {
            val m = emptyEnumSetMultiset<Season>()
            m.enumEntries shouldBe enumEntries<Season>()
        }

        test("enumSetMultisetOf with no elements produces an empty multiset") {
            enumSetMultisetOf<Season>().isEmpty().shouldBeTrue()
        }

        test("enumSetMultisetOf with elements") {
            val m = enumSetMultisetOf(Season.SPRING, Season.SUMMER, Season.SPRING)
            m.size shouldBe 3
            m.count(Season.SPRING) shouldBe 2
            m.count(Season.SUMMER) shouldBe 1
        }

        test("buildEnumSetMultiset") {
            val m = buildEnumSetMultiset<Season> {
                add(Season.AUTUMN)
                add(Season.AUTUMN, 3)
                add(Season.WINTER)
            }
            m.size shouldBe 5
            m.count(Season.AUTUMN) shouldBe 4
            m.count(Season.WINTER) shouldBe 1
        }

        test("toEnumSetMultiset from Iterable") {
            val m = listOf(Season.SPRING, Season.SPRING, Season.SUMMER).toEnumSetMultiset()
            m.size shouldBe 3
            m.count(Season.SPRING) shouldBe 2
        }

        test("toEnumSetMultiset from Sequence") {
            val m = sequenceOf(Season.SPRING, Season.SPRING).toEnumSetMultiset()
            m.count(Season.SPRING) shouldBe 2
        }

        test("toEnumSetMultiset from SetMultiset") {
            val source = setMultisetOf(Season.SPRING, Season.SPRING, Season.SUMMER)
            val m = source.toEnumSetMultiset()
            m.size shouldBe 3
            m.count(Season.SPRING) shouldBe 2
            m.count(Season.SUMMER) shouldBe 1
        }

        test("mutableEnumSetMultisetOf empty") {
            val m = mutableEnumSetMultisetOf<Season>()
            m.isEmpty().shouldBeTrue()
        }

        test("mutableEnumSetMultisetOf with elements") {
            val m = mutableEnumSetMultisetOf(Season.SPRING, Season.SPRING)
            m.size shouldBe 2
            m.count(Season.SPRING) shouldBe 2
        }

        test("toMutableEnumSetMultiset from Iterable") {
            val m = listOf(Season.SPRING).toMutableEnumSetMultiset()
            m.add(Season.SUMMER)
            m.size shouldBe 2
        }

        test("toMutableEnumSetMultiset from SetMultiset") {
            val source = setMultisetOf(Season.SPRING, Season.SPRING)
            val m = source.toMutableEnumSetMultiset()
            m.add(Season.SPRING)
            m.count(Season.SPRING) shouldBe 3
        }

        test("empty enum type") {
            val m = emptyEnumSetMultiset<EmptySeason>()
            m.size shouldBe 0
            m.enumEntries.size shouldBe 0
        }
    }

    context("count and contains") {
        test("count returns occurrences for present element") {
            val m = enumSetMultisetOf(Season.SPRING, Season.SPRING, Season.SPRING)
            m.count(Season.SPRING) shouldBe 3
        }

        test("count returns 0 for absent element") {
            val m = enumSetMultisetOf(Season.SPRING)
            m.count(Season.SUMMER) shouldBe 0
        }

        test("contains true for present element") {
            val m = enumSetMultisetOf(Season.SPRING)
            m.contains(Season.SPRING).shouldBeTrue()
        }

        test("contains false for absent element") {
            val m = enumSetMultisetOf(Season.SPRING)
            m.contains(Season.SUMMER).shouldBeFalse()
        }

        test("containsAll for subset") {
            val m = enumSetMultisetOf(Season.SPRING, Season.SUMMER, Season.AUTUMN)
            m.containsAll(listOf(Season.SPRING, Season.SUMMER)).shouldBeTrue()
        }

        test("containsAll false when element missing") {
            val m = enumSetMultisetOf(Season.SPRING)
            m.containsAll(listOf(Season.SPRING, Season.WINTER)).shouldBeFalse()
        }
    }

    context("mutation - add") {
        test("add single element") {
            val m = mutableEnumSetMultisetOf<Season>()
            m.add(Season.SPRING).shouldBeTrue()
            m.count(Season.SPRING) shouldBe 1
            m.size shouldBe 1
        }

        test("add with count") {
            val m = mutableEnumSetMultisetOf<Season>()
            m.add(Season.SPRING, 5)
            m.count(Season.SPRING) shouldBe 5
            m.size shouldBe 5
        }

        test("add with zero count is no-op") {
            val m = mutableEnumSetMultisetOf<Season>()
            m.add(Season.SPRING, 0)
            m.count(Season.SPRING) shouldBe 0
            m.size shouldBe 0
        }

        test("addAll from collection") {
            val m = mutableEnumSetMultisetOf<Season>()
            m.addAll(listOf(Season.SPRING, Season.SPRING, Season.SUMMER)).shouldBeTrue()
            m.count(Season.SPRING) shouldBe 2
            m.count(Season.SUMMER) shouldBe 1
            m.size shouldBe 3
        }

        test("addAll from empty collection") {
            val m = mutableEnumSetMultisetOf<Season>()
            m.addAll(emptyList()).shouldBeFalse()
            m.size shouldBe 0
        }
    }

    context("mutation - remove") {
        test("remove single element") {
            val m = mutableEnumSetMultisetOf(Season.SPRING, Season.SPRING)
            m.remove(Season.SPRING).shouldBeTrue()
            m.count(Season.SPRING) shouldBe 1
            m.size shouldBe 1
        }

        test("remove absent element returns false") {
            val m = mutableEnumSetMultisetOf<Season>()
            m.remove(Season.SPRING).shouldBeFalse()
        }

        test("remove last occurrence removes element entirely") {
            val m = mutableEnumSetMultisetOf(Season.SPRING)
            m.remove(Season.SPRING).shouldBeTrue()
            m.contains(Season.SPRING).shouldBeFalse()
            m.size shouldBe 0
        }

        test("remove with count") {
            val m = mutableEnumSetMultisetOf(Season.SPRING, Season.SPRING, Season.SPRING)
            m.remove(Season.SPRING, 2) shouldBe 2
            m.count(Season.SPRING) shouldBe 1
            m.size shouldBe 1
        }

        test("remove with count exceeding current") {
            val m = mutableEnumSetMultisetOf(Season.SPRING, Season.SPRING)
            m.remove(Season.SPRING, 5) shouldBe 2
            m.count(Season.SPRING) shouldBe 0
            m.contains(Season.SPRING).shouldBeFalse()
            m.size shouldBe 0
        }

        test("remove with zero count returns 0") {
            val m = mutableEnumSetMultisetOf(Season.SPRING)
            m.remove(Season.SPRING, 0) shouldBe 0
            m.count(Season.SPRING) shouldBe 1
        }

        test("remove absent with count returns 0") {
            val m = mutableEnumSetMultisetOf<Season>()
            m.remove(Season.SPRING, 3) shouldBe 0
        }
    }

    context("mutation - removeAll and retainAll") {
        test("removeAll removes all occurrences of given elements") {
            val m = mutableEnumSetMultisetOf(Season.SPRING, Season.SPRING, Season.SUMMER, Season.AUTUMN)
            m.removeAll(listOf(Season.SPRING, Season.SUMMER)).shouldBeTrue()
            m.contains(Season.SPRING).shouldBeFalse()
            m.contains(Season.SUMMER).shouldBeFalse()
            m.count(Season.AUTUMN) shouldBe 1
            m.size shouldBe 1
        }

        test("removeAll returns false when no change") {
            val m = mutableEnumSetMultisetOf(Season.SPRING)
            m.removeAll(listOf(Season.SUMMER)).shouldBeFalse()
            m.size shouldBe 1
        }

        test("retainAll keeps only given elements") {
            val m = mutableEnumSetMultisetOf(Season.SPRING, Season.SPRING, Season.SUMMER, Season.AUTUMN)
            m.retainAll(listOf(Season.SPRING)).shouldBeTrue()
            m.count(Season.SPRING) shouldBe 2
            m.contains(Season.SUMMER).shouldBeFalse()
            m.contains(Season.AUTUMN).shouldBeFalse()
            m.size shouldBe 2
        }

        test("retainAll returns false when no change") {
            val m = mutableEnumSetMultisetOf(Season.SPRING)
            m.retainAll(listOf(Season.SPRING)).shouldBeFalse()
            m.size shouldBe 1
        }
    }

    context("mutation - setCount") {
        test("setCount increases count") {
            val m = mutableEnumSetMultisetOf(Season.SPRING)
            m.setCount(Season.SPRING, 5) shouldBe 1
            m.count(Season.SPRING) shouldBe 5
            m.size shouldBe 5
        }

        test("setCount decreases count") {
            val m = mutableEnumSetMultisetOf(Season.SPRING, Season.SPRING, Season.SPRING)
            m.setCount(Season.SPRING, 1) shouldBe 3
            m.count(Season.SPRING) shouldBe 1
            m.size shouldBe 1
        }

        test("setCount to zero removes element") {
            val m = mutableEnumSetMultisetOf(Season.SPRING, Season.SPRING)
            m.setCount(Season.SPRING, 0) shouldBe 2
            m.contains(Season.SPRING).shouldBeFalse()
            m.size shouldBe 0
        }

        test("setCount on absent element") {
            val m = mutableEnumSetMultisetOf<Season>()
            m.setCount(Season.SPRING, 3) shouldBe 0
            m.count(Season.SPRING) shouldBe 3
            m.size shouldBe 3
        }

        test("clear empties the multiset") {
            val m = mutableEnumSetMultisetOf(Season.SPRING, Season.SUMMER, Season.AUTUMN)
            m.clear()
            m.isEmpty().shouldBeTrue()
            m.size shouldBe 0
        }
    }

    context("ordering") {
        test("elements iterate in enum declaration order") {
            val m = enumSetMultisetOf(Season.WINTER, Season.SPRING, Season.AUTUMN)
            m.elements.toList().shouldContainExactly(Season.SPRING, Season.AUTUMN, Season.WINTER)
        }

        test("asMap keys in enum declaration order") {
            val m = enumSetMultisetOf(Season.WINTER, Season.SPRING)
            m.asMap.keys.toList().shouldContainExactly(Season.SPRING, Season.WINTER)
        }

        test("iterator yields elements in enum declaration order with repeats") {
            val m = enumSetMultisetOf(Season.WINTER, Season.SPRING, Season.SPRING)
            m.toList().shouldContainExactly(Season.SPRING, Season.SPRING, Season.WINTER)
        }
    }

    context("elements and asMap") {
        test("elements returns distinct elements") {
            val m = enumSetMultisetOf(Season.SPRING, Season.SPRING, Season.SUMMER)
            m.elements.shouldContainExactlyInAnyOrder(Season.SPRING, Season.SUMMER)
        }

        test("elements is empty for empty multiset") {
            val m = emptyEnumSetMultiset<Season>()
            m.elements.shouldBeEmpty()
        }

        test("asMap returns EnumMap") {
            val m = enumSetMultisetOf(Season.SPRING)
            m.asMap.shouldBeInstanceOf<EnumMap<Season, Int>>()
        }

        test("asMap is a live view") {
            val m = mutableEnumSetMultisetOf(Season.SPRING)
            val map = m.asMap
            m.add(Season.SPRING)
            map[Season.SPRING] shouldBe 2
        }

        test("asMap skips zero-count elements") {
            val m = enumSetMultisetOf(Season.SPRING, Season.SUMMER)
            m.asMap.size shouldBe 2
            m.asMap.containsKey(Season.AUTUMN).shouldBeFalse()
        }
    }

    context("iterator") {
        test("iterator yields each element repeated by count") {
            val m = enumSetMultisetOf(Season.SPRING, Season.SPRING, Season.SUMMER)
            m.toList().shouldContainExactly(Season.SPRING, Season.SPRING, Season.SUMMER)
        }

        test("mutable iterator remove decrements count") {
            val m = mutableEnumSetMultisetOf(Season.SPRING, Season.SPRING, Season.SUMMER)
            val iter = m.iterator()
            iter.next() // SPRING
            iter.remove()
            m.count(Season.SPRING) shouldBe 1
            m.size shouldBe 2
        }

        test("empty multiset iterator") {
            val m = emptyEnumSetMultiset<Season>()
            m.iterator().hasNext().shouldBeFalse()
        }
    }

    context("equality") {
        test("equal to a SetMultiset with same counts") {
            val enum = enumSetMultisetOf(Season.SPRING, Season.SPRING, Season.SUMMER)
            val regular = setMultisetOf(Season.SPRING, Season.SPRING, Season.SUMMER)
            enum shouldBe regular
        }

        test("not equal when different counts") {
            val m1 = enumSetMultisetOf(Season.SPRING, Season.SPRING)
            val m2 = enumSetMultisetOf(Season.SPRING)
            m1 shouldNotBe m2
        }

        test("not equal when different elements") {
            val m1 = enumSetMultisetOf(Season.SPRING)
            val m2 = enumSetMultisetOf(Season.SUMMER)
            m1 shouldNotBe m2
        }

        test("empty multisets are equal") {
            emptyEnumSetMultiset<Season>() shouldBe emptyEnumSetMultiset<Season>()
        }

        test("hashCode consistent with asMap") {
            val m = enumSetMultisetOf(Season.SPRING, Season.SPRING, Season.SUMMER)
            m.hashCode() shouldBe m.asMap.hashCode()
        }

        test("equal multisets have same hashCode") {
            val m1 = enumSetMultisetOf(Season.SPRING, Season.SPRING, Season.SUMMER)
            val m2 = setMultisetOf(Season.SPRING, Season.SPRING, Season.SUMMER)
            m1.hashCode() shouldBe m2.hashCode()
        }
    }

    context("size tracking") {
        test("size tracks total occurrences") {
            val m = mutableEnumSetMultisetOf<Season>()
            m.add(Season.SPRING)
            m.size shouldBe 1
            m.add(Season.SPRING, 3)
            m.size shouldBe 4
            m.add(Season.SUMMER)
            m.size shouldBe 5
        }

        test("size decrements on remove") {
            val m = mutableEnumSetMultisetOf(Season.SPRING, Season.SPRING, Season.SPRING)
            m.remove(Season.SPRING, 2)
            m.size shouldBe 1
        }

        test("size adjusts on setCount") {
            val m = mutableEnumSetMultisetOf(Season.SPRING, Season.SPRING)
            m.setCount(Season.SPRING, 5)
            m.size shouldBe 5
        }

        test("size resets to zero on clear") {
            val m = mutableEnumSetMultisetOf(Season.SPRING, Season.SUMMER)
            m.clear()
            m.size shouldBe 0
        }
    }

    context("immutability") {
        test("read-only factory results cannot be cast to MutableEnumSetMultiset") {
            shouldThrow<ClassCastException> { emptyEnumSetMultiset<Season>() as MutableEnumSetMultiset<Season> }
            shouldThrow<ClassCastException> { enumSetMultisetOf(Season.SPRING) as MutableEnumSetMultiset<Season> }
            shouldThrow<ClassCastException> { buildEnumSetMultiset<Season> { add(Season.SPRING) } as MutableEnumSetMultiset<Season> }
        }
    }
})
