package com.kelvsyc.kotlin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldBeNull

private enum class Direction { NORTH, SOUTH, EAST, WEST }

class EnumExtensionsTest : FunSpec({

    context("isValidEnum") {
        test("exact match returns true") {
            isValidEnum<Direction>("NORTH") shouldBe true
        }
        test("unknown name returns false") {
            isValidEnum<Direction>("UP") shouldBe false
        }
        test("wrong case without ignoreCase returns false") {
            isValidEnum<Direction>("north") shouldBe false
        }
        test("wrong case with ignoreCase returns true") {
            isValidEnum<Direction>("north", ignoreCase = true) shouldBe true
        }
        test("mixed case with ignoreCase returns true") {
            isValidEnum<Direction>("NoRtH", ignoreCase = true) shouldBe true
        }
        test("empty string returns false") {
            isValidEnum<Direction>("") shouldBe false
        }
    }

    context("enumValueOfOrNull") {
        test("exact match returns the constant") {
            enumValueOfOrNull<Direction>("SOUTH") shouldBe Direction.SOUTH
        }
        test("unknown name returns null") {
            enumValueOfOrNull<Direction>("DOWN").shouldBeNull()
        }
        test("wrong case without ignoreCase returns null") {
            enumValueOfOrNull<Direction>("south").shouldBeNull()
        }
        test("wrong case with ignoreCase returns the constant") {
            enumValueOfOrNull<Direction>("south", ignoreCase = true) shouldBe Direction.SOUTH
        }
        test("mixed case with ignoreCase returns the constant") {
            enumValueOfOrNull<Direction>("EaSt", ignoreCase = true) shouldBe Direction.EAST
        }
    }

    context("enumValueOfOrDefault") {
        test("exact match returns the constant") {
            enumValueOfOrDefault("WEST", defaultValue = Direction.NORTH) shouldBe Direction.WEST
        }
        test("unknown name returns the default") {
            enumValueOfOrDefault("UP", defaultValue = Direction.NORTH) shouldBe Direction.NORTH
        }
        test("wrong case without ignoreCase returns the default") {
            enumValueOfOrDefault("west", defaultValue = Direction.NORTH) shouldBe Direction.NORTH
        }
        test("wrong case with ignoreCase returns the constant") {
            enumValueOfOrDefault("west", ignoreCase = true, defaultValue = Direction.NORTH) shouldBe Direction.WEST
        }
    }
})
