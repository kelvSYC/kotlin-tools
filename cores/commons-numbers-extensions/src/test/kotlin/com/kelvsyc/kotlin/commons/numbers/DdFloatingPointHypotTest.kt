package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointHypot
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.core.DD

class DdFloatingPointHypotTest : FunSpec() {
    init {
        context("FloatingPointHypot.Companion.dd") {
            val ops = FloatingPointHypot.dd

            context("special cases") {
                test("NaN in x returns NaN") {
                    with(ops) { DD.of(Double.NaN).hypot(DD.of(1.0)) }.hi().isNaN() shouldBe true
                }

                test("NaN in y returns NaN") {
                    with(ops) { DD.of(1.0).hypot(DD.of(Double.NaN)) }.hi().isNaN() shouldBe true
                }

                test("positive infinity in x returns positive infinity") {
                    val r = with(ops) { DD.of(Double.POSITIVE_INFINITY).hypot(DD.of(1.0)) }
                    r.hi() shouldBe Double.POSITIVE_INFINITY
                }

                test("positive infinity in y returns positive infinity") {
                    val r = with(ops) { DD.of(1.0).hypot(DD.of(Double.POSITIVE_INFINITY)) }
                    r.hi() shouldBe Double.POSITIVE_INFINITY
                }

                test("infinity dominates NaN: hypot(+∞, NaN) = +∞") {
                    val r = with(ops) { DD.of(Double.POSITIVE_INFINITY).hypot(DD.of(Double.NaN)) }
                    r.hi() shouldBe Double.POSITIVE_INFINITY
                }

                test("infinity dominates NaN: hypot(NaN, +∞) = +∞") {
                    val r = with(ops) { DD.of(Double.NaN).hypot(DD.of(Double.POSITIVE_INFINITY)) }
                    r.hi() shouldBe Double.POSITIVE_INFINITY
                }

                test("hypot(0, 0) = 0") {
                    val r = with(ops) { DD.of(0.0).hypot(DD.of(0.0)) }
                    r.hi() shouldBe 0.0
                    r.lo() shouldBe 0.0
                }

                test("hypot(3, 0) = 3") {
                    val r = with(ops) { DD.of(3.0).hypot(DD.of(0.0)) }
                    r.hi() shouldBe 3.0
                }

                test("hypot(0, 5) = 5") {
                    val r = with(ops) { DD.of(0.0).hypot(DD.of(5.0)) }
                    r.hi() shouldBe 5.0
                }
            }

            context("pythagorean triples") {
                test("hypot(3, 4) ≈ 5") {
                    val r = with(ops) { DD.of(3.0).hypot(DD.of(4.0)) }
                    r.hi() shouldBe 5.0
                }

                test("hypot(5, 12) ≈ 13") {
                    val r = with(ops) { DD.of(5.0).hypot(DD.of(12.0)) }
                    r.hi() shouldBe 13.0
                }
            }

            context("symmetry") {
                test("hypot(3, 4) == hypot(4, 3)") {
                    val r1 = with(ops) { DD.of(3.0).hypot(DD.of(4.0)) }
                    val r2 = with(ops) { DD.of(4.0).hypot(DD.of(3.0)) }
                    r1.hi() shouldBe r2.hi()
                }

                test("hypot(5, 12) == hypot(12, 5)") {
                    val r1 = with(ops) { DD.of(5.0).hypot(DD.of(12.0)) }
                    val r2 = with(ops) { DD.of(12.0).hypot(DD.of(5.0)) }
                    r1.hi() shouldBe r2.hi()
                }
            }

            context("precision") {
                test("hypot(1, 1) high component matches sqrt(2)") {
                    val r = with(ops) { DD.of(1.0).hypot(DD.of(1.0)) }
                    r.hi() shouldBe kotlin.math.sqrt(2.0)
                }
            }

            context("absolute value handling") {
                test("hypot(-3, 4) ≈ 5") {
                    val r = with(ops) { DD.of(-3.0).hypot(DD.of(4.0)) }
                    r.hi() shouldBe 5.0
                }

                test("hypot(3, -4) ≈ 5") {
                    val r = with(ops) { DD.of(3.0).hypot(DD.of(-4.0)) }
                    r.hi() shouldBe 5.0
                }

                test("hypot(-3, -4) ≈ 5") {
                    val r = with(ops) { DD.of(-3.0).hypot(DD.of(-4.0)) }
                    r.hi() shouldBe 5.0
                }
            }
        }
    }
}
