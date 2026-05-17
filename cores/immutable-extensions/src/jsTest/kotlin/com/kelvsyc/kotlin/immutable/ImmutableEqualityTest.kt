package com.kelvsyc.kotlin.immutable

import com.kelvsyc.kotlin.core.traits.ValueEquality
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.booleans.shouldBeFalse

class ImmutableEqualityTest : FunSpec({
    test("immutableList equality holds for same contents") {
        val a = immutableListOf(1, 2, 3)
        val b = immutableListOf(1, 2, 3)
        ValueEquality.immutableList.run { a.isEqualTo(b) }.shouldBeTrue()
    }

    test("immutableList equality fails for different contents") {
        val a = immutableListOf(1, 2, 3)
        val b = immutableListOf(1, 2, 4)
        ValueEquality.immutableList.run { a.isEqualTo(b) }.shouldBeFalse()
    }

    test("immutableList equality fails for different sizes") {
        val a = immutableListOf(1, 2)
        val b = immutableListOf(1, 2, 3)
        ValueEquality.immutableList.run { a.isEqualTo(b) }.shouldBeFalse()
    }

    test("immutableMap equality holds for same contents") {
        val a = immutableMapOf("x" to 1, "y" to 2)
        val b = immutableMapOf("x" to 1, "y" to 2)
        ValueEquality.immutableMap.run { a.isEqualTo(b) }.shouldBeTrue()
    }

    test("immutableMap equality fails for different values") {
        val a = immutableMapOf("x" to 1)
        val b = immutableMapOf("x" to 99)
        ValueEquality.immutableMap.run { a.isEqualTo(b) }.shouldBeFalse()
    }

    test("immutableSet equality holds for same contents") {
        val a = immutableSetOf(1, 2, 3)
        val b = immutableSetOf(1, 2, 3)
        ValueEquality.immutableSet.run { a.isEqualTo(b) }.shouldBeTrue()
    }

    test("immutableSet equality fails for different contents") {
        val a = immutableSetOf(1, 2, 3)
        val b = immutableSetOf(1, 2, 4)
        ValueEquality.immutableSet.run { a.isEqualTo(b) }.shouldBeFalse()
    }
})
