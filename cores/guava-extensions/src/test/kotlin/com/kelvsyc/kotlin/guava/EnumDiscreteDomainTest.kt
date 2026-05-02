package com.kelvsyc.kotlin.guava

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private enum class Season { SPRING, SUMMER, AUTUMN, WINTER }

class EnumDiscreteDomainTest : FunSpec({
    val domain = enumDiscreteDomain<Season>()

    context("next") {
        test("returns successor for non-last constant") {
            domain.next(Season.SPRING) shouldBe Season.SUMMER
            domain.next(Season.SUMMER) shouldBe Season.AUTUMN
            domain.next(Season.AUTUMN) shouldBe Season.WINTER
        }
        test("returns null at last constant") {
            domain.next(Season.WINTER) shouldBe null
        }
    }

    context("previous") {
        test("returns predecessor for non-first constant") {
            domain.previous(Season.WINTER) shouldBe Season.AUTUMN
            domain.previous(Season.AUTUMN) shouldBe Season.SUMMER
            domain.previous(Season.SUMMER) shouldBe Season.SPRING
        }
        test("returns null at first constant") {
            domain.previous(Season.SPRING) shouldBe null
        }
    }

    context("distance") {
        test("positive distance") {
            domain.distance(Season.SPRING, Season.WINTER) shouldBe 3L
        }
        test("negative distance") {
            domain.distance(Season.WINTER, Season.SPRING) shouldBe -3L
        }
        test("zero distance") {
            domain.distance(Season.SUMMER, Season.SUMMER) shouldBe 0L
        }
    }

    context("minValue and maxValue") {
        test("minValue is first constant") {
            domain.minValue() shouldBe Season.SPRING
        }
        test("maxValue is last constant") {
            domain.maxValue() shouldBe Season.WINTER
        }
    }

    context("Class constructor") {
        test("produces equivalent domain") {
            val classDomain = EnumDiscreteDomain(Season::class.java)
            classDomain.minValue() shouldBe Season.SPRING
            classDomain.maxValue() shouldBe Season.WINTER
            classDomain.next(Season.SPRING) shouldBe Season.SUMMER
        }
    }

    context("empty enum guard") {
        test("throws on empty constants array") {
            shouldThrow<IllegalArgumentException> {
                EnumDiscreteDomain(emptyArray<Season>())
            }
        }
    }
})
