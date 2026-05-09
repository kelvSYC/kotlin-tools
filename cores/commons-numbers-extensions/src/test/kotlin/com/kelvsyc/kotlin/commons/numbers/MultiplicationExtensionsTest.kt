package com.kelvsyc.kotlin.commons.numbers

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.checkAll
import org.apache.commons.numbers.core.Multiplication

class MultiplicationExtensionsTest : FunSpec() {
    private data class Multipliable(val value: Double) : Multiplication<Multipliable> {
        override fun multiply(a: Multipliable) = Multipliable(value * a.value)
        override fun reciprocal() = Multipliable(1.0 / value)
        override fun one() = Multipliable(1.0)
    }

    init {
        test("times multiplies") {
            checkAll(Arb.double(0.0, 100.0, includeNaNs = false), Arb.double(0.0, 100.0, includeNaNs = false)) { m, n ->
                (Multipliable(m) * Multipliable(n)).value shouldBe (m * n plusOrMinus 1e-9)
            }
        }
        test("div divides via multiply-reciprocal") {
            checkAll(Arb.double(1.0, 100.0, includeNaNs = false), Arb.double(1.0, 100.0, includeNaNs = false)) { m, n ->
                (Multipliable(m) / Multipliable(n)).value shouldBe (m / n plusOrMinus 1e-9)
            }
        }
    }
}
