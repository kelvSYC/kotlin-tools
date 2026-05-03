package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSquare
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.core.DD

class DdFloatingPointSquareTest : FunSpec() {
    init {
        context("FloatingPointSquare.Companion.dd") {
            val ops = FloatingPointSquare.dd

            test("square(3.0) = 9.0") {
                val r = with(ops) { DD.of(3.0).square() }
                r.hi() shouldBe 9.0
                r.lo() shouldBe 0.0
            }
            test("square(-3.0) = 9.0") {
                val r = with(ops) { DD.of(-3.0).square() }
                r.hi() shouldBe 9.0
                r.lo() shouldBe 0.0
            }
            test("square(0.0) = 0.0") {
                val r = with(ops) { DD.of(0.0).square() }
                r.hi() shouldBe 0.0
                r.lo() shouldBe 0.0
            }
            test("square(NaN) → NaN hi") {
                with(ops) { DD.of(Double.NaN).square() }.hi().isNaN() shouldBe true
            }
        }
    }
}
