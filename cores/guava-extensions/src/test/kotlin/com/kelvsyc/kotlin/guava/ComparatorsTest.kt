package com.kelvsyc.kotlin.guava

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeNegative
import io.kotest.matchers.ints.shouldBePositive
import io.kotest.matchers.shouldBe

class ComparatorsTest : FunSpec({

    context("unsignedByteComparator") {
        val cmp = Comparators.unsignedByteComparator

        test("equal values") {
            cmp.compare(1, 1) shouldBe 0
        }

        test("two non-negative values ordered as signed") {
            cmp.compare(1, 2).shouldBeNegative()
        }

        test("MIN_VALUE is greater than MAX_VALUE (high bit set sorts last)") {
            cmp.compare(Byte.MIN_VALUE, Byte.MAX_VALUE).shouldBePositive()
        }

        test("two negative values ordered by unsigned magnitude") {
            cmp.compare((-2).toByte(), (-1).toByte()).shouldBeNegative()
        }
    }

    context("unsignedIntComparator") {
        val cmp = Comparators.unsignedIntComparator

        test("equal values") {
            cmp.compare(1, 1) shouldBe 0
        }

        test("two non-negative values ordered as signed") {
            cmp.compare(1, 2).shouldBeNegative()
        }

        test("MIN_VALUE is greater than MAX_VALUE (high bit set sorts last)") {
            cmp.compare(Int.MIN_VALUE, Int.MAX_VALUE).shouldBePositive()
        }

        test("two negative values ordered by unsigned magnitude") {
            cmp.compare(-2, -1).shouldBeNegative()
        }
    }

    context("unsignedLongComparator") {
        val cmp = Comparators.unsignedLongComparator

        test("equal values") {
            cmp.compare(1L, 1L) shouldBe 0
        }

        test("two non-negative values ordered as signed") {
            cmp.compare(1L, 2L).shouldBeNegative()
        }

        test("MIN_VALUE is greater than MAX_VALUE (high bit set sorts last)") {
            cmp.compare(Long.MIN_VALUE, Long.MAX_VALUE).shouldBePositive()
        }

        test("two negative values ordered by unsigned magnitude") {
            cmp.compare(-2L, -1L).shouldBeNegative()
        }
    }
})
