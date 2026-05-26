package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointLogb
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.core.DD

class DdFloatingPointLogbTest : FunSpec() {
    init {
        context("FloatingPointLogb.Companion.dd") {
            val ops = FloatingPointLogb.dd

            context("logb") {
                test("NaN returns NaN") {
                    val r = with(ops) { DD.of(Double.NaN).logb() }
                    r.hi().isNaN() shouldBe true
                }

                test("positive infinity returns positive infinity") {
                    val r = with(ops) { DD.of(Double.POSITIVE_INFINITY).logb() }
                    r.hi() shouldBe Double.POSITIVE_INFINITY
                    r.lo() shouldBe 0.0
                }

                test("negative infinity returns positive infinity") {
                    val r = with(ops) { DD.of(Double.NEGATIVE_INFINITY).logb() }
                    r.hi() shouldBe Double.POSITIVE_INFINITY
                    r.lo() shouldBe 0.0
                }

                test("zero returns negative infinity") {
                    val r = with(ops) { DD.of(0.0).logb() }
                    r.hi() shouldBe Double.NEGATIVE_INFINITY
                    r.lo() shouldBe 0.0
                }

                test("4.0 returns 2.0") {
                    val r = with(ops) { DD.of(4.0).logb() }
                    r.hi() shouldBe 2.0
                    r.lo() shouldBe 0.0
                }

                test("1.0 returns 0.0") {
                    val r = with(ops) { DD.of(1.0).logb() }
                    r.hi() shouldBe 0.0
                    r.lo() shouldBe 0.0
                }

                test("4.0 with non-zero lo still returns 2.0") {
                    val r = with(ops) { DD.ofSum(4.0, 1e-15).logb() }
                    r.hi() shouldBe 2.0
                    r.lo() shouldBe 0.0
                }

                test("lo component in result is always 0.0") {
                    val r = with(ops) { DD.ofSum(8.0, 0.5).logb() }
                    r.lo() shouldBe 0.0
                }
            }

            context("ilogb") {
                test("NaN returns Int.MAX_VALUE") {
                    with(ops) { DD.of(Double.NaN).ilogb() } shouldBe Int.MAX_VALUE
                }

                test("positive infinity returns Int.MAX_VALUE") {
                    with(ops) { DD.of(Double.POSITIVE_INFINITY).ilogb() } shouldBe Int.MAX_VALUE
                }

                test("negative infinity returns Int.MAX_VALUE") {
                    with(ops) { DD.of(Double.NEGATIVE_INFINITY).ilogb() } shouldBe Int.MAX_VALUE
                }

                test("zero returns Int.MIN_VALUE") {
                    with(ops) { DD.of(0.0).ilogb() } shouldBe Int.MIN_VALUE
                }

                test("4.0 returns 2") {
                    with(ops) { DD.of(4.0).ilogb() } shouldBe 2
                }

                test("1.0 returns 0") {
                    with(ops) { DD.of(1.0).ilogb() } shouldBe 0
                }

                test("4.0 with non-zero lo still returns 2") {
                    with(ops) { DD.ofSum(4.0, 1e-15).ilogb() } shouldBe 2
                }
            }
        }
    }
}
