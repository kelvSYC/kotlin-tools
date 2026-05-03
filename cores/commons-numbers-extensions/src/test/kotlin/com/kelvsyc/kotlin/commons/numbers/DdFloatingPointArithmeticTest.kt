package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSquareRoot
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.core.DD

class DdFloatingPointArithmeticTest : FunSpec() {
    init {
        context("FloatingPointArithmetic.Companion.dd") {
            val ops = FloatingPointArithmetic.dd

            context("constants") {
                test("zero is DD.ZERO") {
                    ops.zero shouldBe DD.ZERO
                }
                test("one is DD.ONE") {
                    ops.one shouldBe DD.ONE
                }
            }

            context("isNaN") {
                test("NaN returns true") {
                    with(ops) { DD.of(Double.NaN).isNaN() } shouldBe true
                }
                test("positive infinity returns false") {
                    with(ops) { DD.of(Double.POSITIVE_INFINITY).isNaN() } shouldBe false
                }
                test("finite value returns false") {
                    with(ops) { DD.of(1.0).isNaN() } shouldBe false
                }
                test("zero returns false") {
                    with(ops) { DD.ZERO.isNaN() } shouldBe false
                }
            }

            context("isInfinite") {
                test("positive infinity returns true") {
                    with(ops) { DD.of(Double.POSITIVE_INFINITY).isInfinite() } shouldBe true
                }
                test("negative infinity returns true") {
                    with(ops) { DD.of(Double.NEGATIVE_INFINITY).isInfinite() } shouldBe true
                }
                test("NaN returns false") {
                    with(ops) { DD.of(Double.NaN).isInfinite() } shouldBe false
                }
                test("finite value returns false") {
                    with(ops) { DD.of(1.0).isInfinite() } shouldBe false
                }
            }

            context("isFinite") {
                test("finite value returns true") {
                    with(ops) { DD.of(1.0).isFinite() } shouldBe true
                }
                test("zero returns true") {
                    with(ops) { DD.ZERO.isFinite() } shouldBe true
                }
                test("positive infinity returns false") {
                    with(ops) { DD.of(Double.POSITIVE_INFINITY).isFinite() } shouldBe false
                }
                test("NaN returns false") {
                    with(ops) { DD.of(Double.NaN).isFinite() } shouldBe false
                }
            }

            context("isZero") {
                test("positive zero returns true") {
                    with(ops) { DD.ZERO.isZero() } shouldBe true
                }
                test("negative zero returns true") {
                    with(ops) { DD.of(-0.0).isZero() } shouldBe true
                }
                test("nonzero finite returns false") {
                    with(ops) { DD.ONE.isZero() } shouldBe false
                }
                test("NaN returns false") {
                    with(ops) { DD.of(Double.NaN).isZero() } shouldBe false
                }
                test("infinity returns false") {
                    with(ops) { DD.of(Double.POSITIVE_INFINITY).isZero() } shouldBe false
                }
            }

            context("isNegative") {
                test("negative value returns true") {
                    with(ops) { DD.of(-1.0).isNegative() } shouldBe true
                }
                test("negative zero returns true") {
                    with(ops) { DD.of(-0.0).isNegative() } shouldBe true
                }
                test("positive value returns false") {
                    with(ops) { DD.of(1.0).isNegative() } shouldBe false
                }
                test("positive zero returns false") {
                    with(ops) { DD.ZERO.isNegative() } shouldBe false
                }
            }

            context("isInteger") {
                test("zero returns true") {
                    with(ops) { DD.ZERO.isInteger() } shouldBe true
                }
                test("one returns true") {
                    with(ops) { DD.ONE.isInteger() } shouldBe true
                }
                test("large integer (biasedExp >= 1075) returns true") {
                    // 2^52 in hi, 0 in lo: both hi and lo are integers
                    with(ops) { DD.of(Double.fromBits(0x4330000000000000L)).isInteger() } shouldBe true
                }
                test("non-integer hi returns false") {
                    with(ops) { DD.of(0.5).isInteger() } shouldBe false
                }
                test("integer hi, non-integer lo returns false") {
                    // ofSum(1.0, 1.0e-16): hi=1.0 (integer), lo≈1.0e-16 (not integer)
                    val d = DD.ofSum(1.0, 1.0e-16)
                    with(ops) { d.isInteger() } shouldBe false
                }
                test("positive infinity returns false") {
                    with(ops) { DD.of(Double.POSITIVE_INFINITY).isInteger() } shouldBe false
                }
                test("NaN returns false") {
                    with(ops) { DD.of(Double.NaN).isInteger() } shouldBe false
                }
            }

            context("negate") {
                test("negating a positive value gives negative") {
                    with(ops) { DD.of(1.0).negate() }.hi() shouldBe -1.0
                }
                test("negating a negative value gives positive") {
                    with(ops) { DD.of(-1.0).negate() }.hi() shouldBe 1.0
                }
                test("double negation is identity") {
                    val v = DD.ofSum(1.0, 1.0e-16)
                    val result = with(ops) { v.negate().negate() }
                    result.hi() shouldBe v.hi()
                    result.lo() shouldBe v.lo()
                }
                test("negating NaN produces NaN") {
                    with(ops) { DD.of(Double.NaN).negate() }.hi().isNaN() shouldBe true
                }
            }

            context("abs") {
                test("abs of positive value is unchanged") {
                    with(ops) { DD.of(1.0).abs() }.hi() shouldBe 1.0
                }
                test("abs of negative value clears sign") {
                    with(ops) { DD.of(-1.0).abs() }.hi() shouldBe 1.0
                }
                test("abs of negative infinity is positive infinity") {
                    with(ops) { DD.of(Double.NEGATIVE_INFINITY).abs() }.hi() shouldBe Double.POSITIVE_INFINITY
                }
            }

            context("add") {
                test("1.0 + 1.0 = 2.0") {
                    val result = with(ops) { DD.ONE.add(DD.ONE) }
                    result.hi() shouldBe 2.0
                    result.lo() shouldBe 0.0
                }
                test("zero + x = x") {
                    val x = DD.of(1.5)
                    val result = with(ops) { DD.ZERO.add(x) }
                    result.hi() shouldBe x.hi()
                    result.lo() shouldBe x.lo()
                }
                test("NaN + x = NaN") {
                    with(ops) { DD.of(Double.NaN).add(DD.ONE) }.hi().isNaN() shouldBe true
                }
            }

            context("subtract") {
                test("2.0 - 1.0 = 1.0") {
                    val result = with(ops) { DD.of(2.0).subtract(DD.ONE) }
                    result.hi() shouldBe 1.0
                    result.lo() shouldBe 0.0
                }
                test("x - zero = x") {
                    val x = DD.of(1.5)
                    val result = with(ops) { x.subtract(DD.ZERO) }
                    result.hi() shouldBe x.hi()
                    result.lo() shouldBe x.lo()
                }
                test("x - x = 0") {
                    with(ops) { DD.ONE.subtract(DD.ONE).isZero() } shouldBe true
                }
                test("NaN - x = NaN") {
                    with(ops) { DD.of(Double.NaN).subtract(DD.ONE) }.hi().isNaN() shouldBe true
                }
            }

            context("multiply") {
                test("2.0 * 3.0 = 6.0") {
                    val result = with(ops) { DD.of(2.0).multiply(DD.of(3.0)) }
                    result.hi() shouldBe 6.0
                    result.lo() shouldBe 0.0
                }
                test("x * one = x") {
                    val x = DD.of(1.5)
                    val result = with(ops) { x.multiply(DD.ONE) }
                    result.hi() shouldBe x.hi()
                    result.lo() shouldBe x.lo()
                }
                test("NaN * x = NaN") {
                    with(ops) { DD.of(Double.NaN).multiply(DD.of(2.0)) }.hi().isNaN() shouldBe true
                }
            }

            context("divide") {
                test("6.0 / 2.0 = 3.0") {
                    val result = with(ops) { DD.of(6.0).divide(DD.of(2.0)) }
                    result.hi() shouldBe 3.0
                    result.lo() shouldBe 0.0
                }
                test("x / one = x") {
                    val x = DD.of(1.5)
                    val result = with(ops) { x.divide(DD.ONE) }
                    result.hi() shouldBe x.hi()
                    result.lo() shouldBe x.lo()
                }
                test("NaN / x = NaN") {
                    with(ops) { DD.of(Double.NaN).divide(DD.of(2.0)) }.hi().isNaN() shouldBe true
                }
            }

            context("compareTo") {
                test("1.0 < 2.0") {
                    (with(ops) { DD.of(1.0).compareTo(DD.of(2.0)) } < 0) shouldBe true
                }
                test("2.0 > 1.0") {
                    (with(ops) { DD.of(2.0).compareTo(DD.of(1.0)) } > 0) shouldBe true
                }
                test("1.0 == 1.0") {
                    with(ops) { DD.of(1.0).compareTo(DD.of(1.0)) } shouldBe 0
                }
                test("equal hi, lesser lo < equal hi, greater lo") {
                    // ofSum(1.0, 1.0e-16): hi=1.0, lo≈1.0e-16
                    // ofSum(1.0, 2.0e-16): hi=1.0, lo≈2.0e-16
                    val smaller = DD.ofSum(1.0, 1.0e-16)
                    val larger = DD.ofSum(1.0, 2.0e-16)
                    (with(ops) { smaller.compareTo(larger) } < 0) shouldBe true
                }
                test("NaN is ordered after +Infinity (NaN-last total order)") {
                    (with(ops) { DD.of(Double.NaN).compareTo(DD.of(Double.POSITIVE_INFINITY)) } > 0) shouldBe true
                }
                test("NaN equals NaN in compareTo") {
                    with(ops) { DD.of(Double.NaN).compareTo(DD.of(Double.NaN)) } shouldBe 0
                }
                test("-Infinity < finite") {
                    (with(ops) { DD.of(Double.NEGATIVE_INFINITY).compareTo(DD.ONE) } < 0) shouldBe true
                }
            }
        }

        context("FloatingPointSquareRoot.Companion.dd") {
            val sqrt = FloatingPointSquareRoot.dd

            test("sqrt(4.0) = 2.0") {
                val result = with(sqrt) { DD.of(4.0).sqrt() }
                result.hi() shouldBe 2.0
                result.lo() shouldBe 0.0
            }
            test("sqrt(1.0) = 1.0") {
                val result = with(sqrt) { DD.ONE.sqrt() }
                result.hi() shouldBe 1.0
                result.lo() shouldBe 0.0
            }
            test("sqrt(0.0) = 0.0") {
                val result = with(sqrt) { DD.ZERO.sqrt() }
                result.isZero() shouldBe true
            }
            test("sqrt(NaN) = NaN") {
                with(sqrt) { DD.of(Double.NaN).sqrt() }.hi().isNaN() shouldBe true
            }
        }
    }
}
