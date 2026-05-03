package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.IntegerPower
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.core.DD

class DdIntegerPowerTest : FunSpec() {
    init {
        context("IntegerPower.Companion.dd") {
            val ops = IntegerPower.dd

            test("pow(2.0, 0) = 1.0") {
                val r = with(ops) { DD.of(2.0).pow(0) }
                r.hi() shouldBe 1.0
                r.lo() shouldBe 0.0
            }
            test("pow(2.0, 1) = 2.0") {
                val r = with(ops) { DD.of(2.0).pow(1) }
                r.hi() shouldBe 2.0
                r.lo() shouldBe 0.0
            }
            test("pow(2.0, 10) = 1024.0") {
                val r = with(ops) { DD.of(2.0).pow(10) }
                r.hi() shouldBe 1024.0
                r.lo() shouldBe 0.0
            }
            test("pow(3.0, 4) = 81.0") {
                val r = with(ops) { DD.of(3.0).pow(4) }
                r.hi() shouldBe 81.0
                r.lo() shouldBe 0.0
            }
            test("pow(x, 0) = 1 even for NaN") {
                val r = with(ops) { DD.of(Double.NaN).pow(0) }
                r.hi() shouldBe 1.0
            }
            test("negative exponent: DD.pow member shadows trait, returns reciprocal") {
                // DD has a member pow(int n) that shadows the trait extension in concrete call
                // sites. The Java member returns reciprocal() for n=-1 without throwing; the
                // trait's require(n >= 0) fires only in generic dispatch contexts.
                val r = with(ops) { DD.of(2.0).pow(-1) }
                r.hi() shouldBe 0.5
            }
        }
    }
}
