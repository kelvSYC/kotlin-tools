package com.kelvsyc.kotlin.guava

import com.google.common.collect.Range
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CharRangesTest : FunSpec({

    context("CharRange.toGuavaRange()") {
        test("produces a closed Guava range") {
            ('a'..'z').toGuavaRange() shouldBe Range.closed('a', 'z')
        }

        test("single-element range") {
            ('m'..'m').toGuavaRange() shouldBe Range.closed('m', 'm')
        }
    }

    context("Range<Char>.toCharRange()") {
        test("closed bounded range converts correctly") {
            Range.closed('a', 'z').toCharRange() shouldBe 'a'..'z'
        }

        test("throws for open lower bound") {
            shouldThrow<IllegalArgumentException> { Range.openClosed('a', 'z').toCharRange() }
        }

        test("throws for open upper bound") {
            shouldThrow<IllegalArgumentException> { Range.closedOpen('a', 'z').toCharRange() }
        }

        test("throws for unbounded lower") {
            shouldThrow<IllegalArgumentException> { Range.atMost('z').toCharRange() }
        }

        test("throws for unbounded upper") {
            shouldThrow<IllegalArgumentException> { Range.atLeast('a').toCharRange() }
        }
    }

    context("charRangeToGuavaRange") {
        val conv = charRangeToGuavaRange

        test("forward produces closed Guava range") {
            conv('a'..'z') shouldBe Range.closed('a', 'z')
        }

        test("backward returns CharRange") {
            conv.reverse(Range.closed('a', 'z')) shouldBe 'a'..'z'
        }

        test("round-trip forward then backward") {
            val r = 'A'..'Z'
            conv.reverse(conv(r)) shouldBe r
        }

        test("round-trip backward then forward") {
            val r = Range.closed('A', 'Z')
            conv(conv.reverse(r)) shouldBe r
        }

        test("backward throws for non-closed range") {
            shouldThrow<IllegalArgumentException> { conv.reverse(Range.closedOpen('a', 'z')) }
        }

        test("singleton identity") {
            charRangeToGuavaRange shouldBe charRangeToGuavaRange
        }
    }
})
