package com.kelvsyc.kotlin.commons.numbers

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import org.apache.commons.numbers.core.Addition

class AdditionExtensionsTest : FunSpec() {
    private data class Addable(val value: Int) : Addition<Addable> {
        override fun add(a: Addable) = Addable(value + a.value)
        override fun negate() = Addable(-value)
        override fun zero() = Addable(0)
    }

    init {
        test("unaryPlus is identity") {
            checkAll(Arb.int()) { n -> +Addable(n) shouldBe Addable(n) }
        }
        test("unaryMinus negates") {
            checkAll(Arb.int()) { n -> (-Addable(n)).value shouldBe -n }
        }
        test("plus adds") {
            checkAll(Arb.int(), Arb.int()) { m, n ->
                (Addable(m) + Addable(n)).value shouldBe m + n
            }
        }
        test("minus subtracts via add-negate") {
            checkAll(Arb.int(), Arb.int()) { m, n ->
                (Addable(m) - Addable(n)).value shouldBe m - n
            }
        }
    }
}
