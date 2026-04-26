package com.kelvsyc.kotlin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class ConverterTest : FunSpec({
    val intToString = Converter.of(Int::toString, String::toInt)

    context("identity") {
        test("forward returns the same value") {
            val id = Converter.identity<String>()
            id("hello") shouldBe "hello"
        }

        test("reverse returns the same value") {
            val id = Converter.identity<String>()
            id.reverse("hello") shouldBe "hello"
        }

        test("reverse is the same instance") {
            val id = Converter.identity<String>()
            id.reverse shouldBeSameInstanceAs id
        }

        test("identity is a singleton regardless of type parameter") {
            val idInt = Converter.identity<Int>()
            val idString = Converter.identity<String>()
            idInt shouldBeEqual idString
        }

        test("identity handles null") {
            val id = Converter.identity<String?>()
            id(null) shouldBe null
            id.reverse(null) shouldBe null
        }
    }

    context("Converter.of") {
        test("invoke applies the forward function") {
            intToString(42) shouldBe "42"
        }

        test("reverse applies the backward function") {
            intToString.reverse("42") shouldBe 42
        }

        test("reverse.reverse round-trips back to the original direction") {
            intToString.reverse.reverse(42) shouldBe "42"
        }

        test("round-trip forward then backward") {
            intToString.reverse(intToString(99)) shouldBe 99
        }

        test("round-trip backward then forward") {
            intToString(intToString.reverse("7")) shouldBe "7"
        }
    }

    context("equality and hashCode") {
        val forward: (Int) -> String = Int::toString
        val backward: (String) -> Int = String::toInt
        val c1 = Converter.of(forward, backward)
        val c2 = Converter.of(forward, backward)
        val different = Converter.of({ i: Int -> i.toString(16) }, { s: String -> s.toInt(16) })

        test("converters with the same functions are equal") {
            c1 shouldBeEqual c2
        }

        test("converters with different functions are not equal") {
            c1 shouldNotBeEqual different
        }

        test("equal converters have equal hashCodes") {
            c1.hashCode() shouldBe c2.hashCode()
        }

        test("converter is not equal to null") {
            (c1.equals(null)) shouldBe false
        }

        test("converter is not equal to a different type") {
            (c1.equals("not a converter")) shouldBe false
        }

        test("reverse of equal converters are equal") {
            c1.reverse shouldBeEqual c2.reverse
        }

        test("reverse of different converters are not equal") {
            c1.reverse shouldNotBeEqual different.reverse
        }

        test("equal reverses have equal hashCodes") {
            c1.reverse.hashCode() shouldBe c2.reverse.hashCode()
        }
    }

    context("andThen composition") {
        val doubler = Converter.of({ i: Int -> i * 2 }, { i: Int -> i / 2 })
        val addOne = Converter.of({ i: Int -> i + 1 }, { i: Int -> i - 1 })
        val composed = doubler.andThen(addOne)

        test("forward applies both converters in order") {
            composed(3) shouldBe 7  // 3 * 2 + 1 = 7
        }

        test("reverse applies both reverses in reverse order") {
            composed.reverse(7) shouldBe 3  // (7 - 1) / 2 = 3
        }

        test("composed equals another composed with the same parts") {
            val composed2 = doubler.andThen(addOne)
            composed shouldBeEqual composed2
        }

        test("composed does not equal one with different parts") {
            val subtractOne = Converter.of({ i: Int -> i - 1 }, { i: Int -> i + 1 })
            val other = doubler.andThen(subtractOne)
            composed shouldNotBeEqual other
        }

        test("composed hashCode matches for equal composed converters") {
            val composed2 = doubler.andThen(addOne)
            composed.hashCode() shouldBe composed2.hashCode()
        }
    }

    context("wrap unary") {
        val negate = intToString.wrap { i: Int -> -i }

        test("wraps a unary operation through the converter") {
            // "-42" -> reverse -> -42 -> negate -> 42 -> forward -> "42"
            negate("-42") shouldBe "42"
        }
    }

    context("wrap binary") {
        val add = intToString.wrap { a: Int, b: Int -> a + b }

        test("wraps a binary operation through the converter") {
            // "3", "4" -> reverse -> 3, 4 -> add -> 7 -> forward -> "7"
            add("3", "4") shouldBe "7"
        }
    }
})
