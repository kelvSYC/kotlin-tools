package com.kelvsyc.kotlin.guava.collect

import com.google.common.collect.Range
import com.kelvsyc.kotlin.core.EnumSubset
import com.kelvsyc.kotlin.core.enumSubsetOf
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

// Ordinals: RED=0, GREEN=1, BLUE=2
// Subset enumeration order: {}=0, {R}=1, {G}=2, {R,G}=3, {B}=4, {R,B}=5, {G,B}=6, {R,G,B}=7
private enum class Color { RED, GREEN, BLUE }

class EnumSubsetDomainTest : FunSpec({

    val domain = EnumSubsetDomain.of<Color>()

    context("minValue and maxValue") {
        test("minValue is empty") { domain.minValue().isEmpty() shouldBe true }
        test("maxValue contains all constants") {
            domain.maxValue() shouldBe EnumSubset.allOf<Color>()
        }
    }

    context("next") {
        test("next(∅) = {RED}") {
            domain.next(domain.minValue()) shouldBe enumSubsetOf(Color.RED)
        }
        test("next({RED}) = {GREEN}") {
            domain.next(enumSubsetOf(Color.RED)) shouldBe enumSubsetOf(Color.GREEN)
        }
        test("next({GREEN}) = {RED, GREEN}") {
            domain.next(enumSubsetOf(Color.GREEN)) shouldBe enumSubsetOf(Color.RED, Color.GREEN)
        }
        test("next({RED, GREEN}) = {BLUE}") {
            domain.next(enumSubsetOf(Color.RED, Color.GREEN)) shouldBe enumSubsetOf(Color.BLUE)
        }
        test("next({BLUE}) = {RED, BLUE}") {
            domain.next(enumSubsetOf(Color.BLUE)) shouldBe enumSubsetOf(Color.RED, Color.BLUE)
        }
        test("next(maxValue) is null") {
            domain.next(domain.maxValue()).shouldBeNull()
        }
    }

    context("previous") {
        test("previous(∅) is null") {
            domain.previous(domain.minValue()).shouldBeNull()
        }
        test("previous({RED}) = ∅") {
            domain.previous(enumSubsetOf(Color.RED)) shouldBe domain.minValue()
        }
        test("previous({GREEN}) = {RED}") {
            domain.previous(enumSubsetOf(Color.GREEN)) shouldBe enumSubsetOf(Color.RED)
        }
        test("previous({RED, GREEN}) = {GREEN}") {
            domain.previous(enumSubsetOf(Color.RED, Color.GREEN)) shouldBe enumSubsetOf(Color.GREEN)
        }
        test("previous(maxValue) = {GREEN, BLUE}") {
            domain.previous(domain.maxValue()) shouldBe enumSubsetOf(Color.GREEN, Color.BLUE)
        }
    }

    context("distance") {
        test("distance(∅, maxValue) = 7 (2^3 - 1)") {
            domain.distance(domain.minValue(), domain.maxValue()) shouldBe 7L
        }
        test("distance(∅, ∅) = 0") {
            domain.distance(domain.minValue(), domain.minValue()) shouldBe 0L
        }
        test("distance({RED}, {BLUE}) = 3") {
            domain.distance(enumSubsetOf(Color.RED), enumSubsetOf(Color.BLUE)) shouldBe 3L
        }
        test("distance is negative when end < start") {
            domain.distance(enumSubsetOf(Color.GREEN), enumSubsetOf(Color.RED)) shouldBe -1L
        }
    }

    context("next/previous are inverses") {
        test("next then previous returns original") {
            val subset = enumSubsetOf(Color.RED, Color.GREEN)
            domain.previous(domain.next(subset)!!) shouldBe subset
        }
        test("previous then next returns original") {
            val subset = enumSubsetOf(Color.BLUE)
            domain.next(domain.previous(subset)!!) shouldBe subset
        }
    }

    context("Range integration") {
        test("Range.closed encloses expected subsets") {
            val range = Range.closed(enumSubsetOf(Color.GREEN), enumSubsetOf(Color.RED, Color.BLUE))
            // bits 2..5: {G}, {R,G}, {B}, {R,B}
            (enumSubsetOf(Color.GREEN) in range) shouldBe true
            (enumSubsetOf(Color.RED, Color.GREEN) in range) shouldBe true
            (enumSubsetOf(Color.BLUE) in range) shouldBe true
            (enumSubsetOf(Color.RED, Color.BLUE) in range) shouldBe true
            (EnumSubset.empty<Color>() in range) shouldBe false
            (enumSubsetOf(Color.GREEN, Color.BLUE) in range) shouldBe false
        }
        test("distance(min, max) + 1 = 2^3 = 8 subsets") {
            domain.distance(domain.minValue(), domain.maxValue()) + 1L shouldBe 8L
        }
    }
})
