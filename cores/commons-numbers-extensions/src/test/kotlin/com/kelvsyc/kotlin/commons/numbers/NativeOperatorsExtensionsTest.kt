package com.kelvsyc.kotlin.commons.numbers

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import org.apache.commons.numbers.core.NativeOperators

class NativeOperatorsExtensionsTest : FunSpec() {
    // Uses Int arithmetic to keep the implementation simple and avoid floating-point imprecision.
    private data class Arithmetic(val value: Int) : NativeOperators<Arithmetic> {
        override fun add(a: Arithmetic) = Arithmetic(value + a.value)
        override fun negate() = Arithmetic(-value)
        override fun zero() = Arithmetic(0)
        override fun multiply(a: Arithmetic) = Arithmetic(value * a.value)
        override fun reciprocal() = Arithmetic(value)
        override fun one() = Arithmetic(1)
        override fun subtract(a: Arithmetic) = Arithmetic(value - a.value)
        override fun multiply(n: Int) = Arithmetic(value * n)
        override fun divide(a: Arithmetic) = Arithmetic(value / a.value)
        override fun pow(n: Int) = Arithmetic(Math.pow(value.toDouble(), n.toDouble()).toInt())
    }

    init {
        test("minus uses subtract directly") {
            checkAll(Arb.int(), Arb.int()) { m, n ->
                (Arithmetic(m) - Arithmetic(n)).value shouldBe m - n
            }
        }
        test("times Int uses multiply(Int)") {
            checkAll(Arb.int(), Arb.int()) { m, n ->
                (Arithmetic(m) * n).value shouldBe m * n
            }
        }
        test("div uses divide directly") {
            checkAll(Arb.int(), Arb.int().filter { it != 0 }) { m, n ->
                (Arithmetic(m) / Arithmetic(n)).value shouldBe m / n
            }
        }
    }
}
