package com.kelvsyc.kotlin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private enum class TestHttpStatus(val code: Int) {
    OK(200),
    NOT_FOUND(404),
    ERROR(500);

    companion object {
        private val byCode by enumReverseLookup<TestHttpStatus, Int> { it.code }
        fun fromCode(code: Int): TestHttpStatus? = byCode[code]
    }
}

private enum class TestCompassPoint(val symbol: Char) {
    NORTH('N'),
    SOUTH('S'),
    EAST('E'),
    WEST('W');

    companion object {
        private val bySymbol by enumReverseLookup<TestCompassPoint, Char> { it.symbol }
        fun fromSymbol(symbol: Char): TestCompassPoint? = bySymbol[symbol]
    }
}

class EnumReverseLookupTest : FunSpec({
    context("enumReverseLookup") {
        test("looks up enum constant by associated value") {
            TestHttpStatus.fromCode(200) shouldBe TestHttpStatus.OK
            TestHttpStatus.fromCode(404) shouldBe TestHttpStatus.NOT_FOUND
            TestHttpStatus.fromCode(500) shouldBe TestHttpStatus.ERROR
        }

        test("returns null for unknown key") {
            TestHttpStatus.fromCode(999) shouldBe null
        }

        test("works with Char keys") {
            TestCompassPoint.fromSymbol('N') shouldBe TestCompassPoint.NORTH
            TestCompassPoint.fromSymbol('S') shouldBe TestCompassPoint.SOUTH
            TestCompassPoint.fromSymbol('E') shouldBe TestCompassPoint.EAST
            TestCompassPoint.fromSymbol('W') shouldBe TestCompassPoint.WEST
        }

        test("returns null for unknown Char key") {
            TestCompassPoint.fromSymbol('X') shouldBe null
        }
    }
})
