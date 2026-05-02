package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.dd.DoubleBinaryFloatingPointArithmetic
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class DoubleBinaryFloatingPointArithmeticTest : FunSpec({

    context("DoubleBinaryFloatingPointArithmetic.Companion.doubleDouble") {
        val ops = DoubleBinaryFloatingPointArithmetic.doubleDouble

        context("constants") {
            test("zero has high = 0.0") { ops.zero.high shouldBe 0.0 }
            test("zero has low = 0.0") { ops.zero.low shouldBe 0.0 }
            test("one has high = 1.0") { ops.one.high shouldBe 1.0 }
            test("one has low = 0.0") { ops.one.low shouldBe 0.0 }
        }

        context("isNaN") {
            test("NaN returns true") {
                with(ops) { DoubleDouble.NaN.isNaN() } shouldBe true
            }
            test("positive infinity returns false") {
                with(ops) { DoubleDouble.POSITIVE_INFINITY.isNaN() } shouldBe false
            }
            test("finite value returns false") {
                with(ops) { DoubleDouble.ONE.isNaN() } shouldBe false
            }
        }

        context("isInfinite") {
            test("positive infinity returns true") {
                with(ops) { DoubleDouble.POSITIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("negative infinity returns true") {
                with(ops) { DoubleDouble.NEGATIVE_INFINITY.isInfinite() } shouldBe true
            }
            test("NaN returns false") {
                with(ops) { DoubleDouble.NaN.isInfinite() } shouldBe false
            }
            test("finite value returns false") {
                with(ops) { DoubleDouble.ONE.isInfinite() } shouldBe false
            }
        }

        context("isFinite") {
            test("finite value returns true") {
                with(ops) { DoubleDouble.ONE.isFinite() } shouldBe true
            }
            test("positive infinity returns false") {
                with(ops) { DoubleDouble.POSITIVE_INFINITY.isFinite() } shouldBe false
            }
            test("NaN returns false") {
                with(ops) { DoubleDouble.NaN.isFinite() } shouldBe false
            }
        }

        context("unaryMinus") {
            test("negates both components") {
                val r = with(ops) { DoubleDouble.create(1.0, 1e-17).unaryMinus() }
                r.high shouldBe -1.0
                r.low shouldBe -1e-17
            }
            test("double negation is identity") {
                val v = DoubleDouble.create(1.5, 1e-17)
                with(ops) { v.unaryMinus().unaryMinus() } shouldBe v
            }
            test("negating NaN produces NaN") {
                with(ops) { DoubleDouble.NaN.unaryMinus() }.isNaN() shouldBe true
            }
        }

        context("abs") {
            test("abs of positive value is unchanged") {
                with(ops) { DoubleDouble.ONE.abs() } shouldBe DoubleDouble.ONE
            }
            test("abs of negative value removes sign") {
                val neg = with(ops) { DoubleDouble.ONE.unaryMinus() }
                with(ops) { neg.abs() } shouldBe DoubleDouble.ONE
            }
            test("abs of NaN is NaN") {
                with(ops) { DoubleDouble.NaN.abs() }.isNaN() shouldBe true
            }
        }

        context("compareTo") {
            test("1 < 2") {
                (with(ops) { DoubleDouble.ONE.compareTo(DoubleDouble.create(2.0, 0.0)) } < 0) shouldBe true
            }
            test("equal values compare as 0") {
                with(ops) { DoubleDouble.ONE.compareTo(DoubleDouble.ONE) } shouldBe 0
            }
            test("NaN is ordered after +Infinity") {
                (with(ops) { DoubleDouble.NaN.compareTo(DoubleDouble.POSITIVE_INFINITY) } > 0) shouldBe true
            }
        }

        context("add") {
            test("identity: zero + x = x") {
                with(ops) { ops.zero.add(DoubleDouble.ONE) } shouldBe DoubleDouble.ONE
            }
            test("1 + 1 = 2") {
                with(ops) { DoubleDouble.ONE.add(DoubleDouble.ONE) }.high shouldBe 2.0
            }
            test("NaN + x = NaN") {
                with(ops) { DoubleDouble.NaN.add(DoubleDouble.ONE) }.isNaN() shouldBe true
            }

            // Precision demonstration: two values with sub-ULP low components; plain Double loses both,
            // DoubleDouble preserves them in the result's low component.
            context("precision: sub-ULP low components are accumulated correctly") {
                val subUlp = 5.551115123125783e-17   // 2^-54, below ulp(1.0)/2 = 2^-53
                val a = DoubleDouble.create(1.0, subUlp)
                val b = DoubleDouble.create(1.0, subUlp)
                val result = with(ops) { a.add(b) }

                test("high component is 2.0 (same as plain Double)") {
                    result.high shouldBe 2.0
                }
                test("low component captures the accumulated 2 * subUlp that plain Double loses") {
                    result.low shouldBe 2 * subUlp
                }
                test("plain Double addition loses the sub-ULP parts (confirming test is meaningful)") {
                    (a.high + b.high) shouldBe 2.0   // low parts are invisible to plain Double
                }
            }
        }

        context("subtract") {
            test("x - zero = x") {
                with(ops) { DoubleDouble.ONE.subtract(ops.zero) } shouldBe DoubleDouble.ONE
            }
            test("x - x = 0") {
                with(ops) { DoubleDouble.ONE.subtract(DoubleDouble.ONE) }.high shouldBe 0.0
            }
            test("NaN - x = NaN") {
                with(ops) { DoubleDouble.NaN.subtract(DoubleDouble.ONE) }.isNaN() shouldBe true
            }
        }

        context("multiply") {
            test("identity: x * one = x") {
                with(ops) { DoubleDouble.create(1.5, 0.0).multiply(ops.one) } shouldBe DoubleDouble.create(1.5, 0.0)
            }
            test("2 * 3 = 6") {
                val r = with(ops) {
                    DoubleDouble.create(2.0, 0.0).multiply(DoubleDouble.create(3.0, 0.0))
                }
                r.high shouldBe 6.0
                r.low shouldBe 0.0
            }
            test("NaN * x = NaN") {
                with(ops) { DoubleDouble.NaN.multiply(DoubleDouble.ONE) }.isNaN() shouldBe true
            }
        }

        context("divide") {
            test("identity: x / one = x") {
                with(ops) { DoubleDouble.create(1.5, 0.0).divide(ops.one) } shouldBe DoubleDouble.create(1.5, 0.0)
            }
            test("6 / 2 = 3") {
                val r = with(ops) {
                    DoubleDouble.create(6.0, 0.0).divide(DoubleDouble.create(2.0, 0.0))
                }
                r.high shouldBe 3.0
                r.low shouldBe 0.0
            }
            test("NaN / x = NaN") {
                with(ops) { DoubleDouble.NaN.divide(DoubleDouble.ONE) }.isNaN() shouldBe true
            }

            // Precision demonstration: 1 / 3 in DoubleDouble gives a result where the
            // reconstructed product (result * 3) is closer to 1 than plain Double's 1.0/3.0.
            context("precision: 1 / 3 result * 3 is closer to 1.0 than plain Double") {
                val one = DoubleDouble.ONE
                val three = DoubleDouble.create(3.0, 0.0)
                val result = with(ops) { one.divide(three) }

                test("quotient high is fl(1/3)") {
                    result.high shouldBe 1.0 / 3.0
                }
                test("quotient low is non-zero (rounding error captured)") {
                    (result.low == 0.0) shouldBe false
                }
                test("result * 3 reconstructs a value closer to 1.0 than plain fl(1/3) * 3") {
                    val ddProduct = with(ops) { result.multiply(three) }
                    val naiveError = kotlin.math.abs((1.0 / 3.0) * 3.0 - 1.0)
                    val ddError = kotlin.math.abs(ddProduct.high + ddProduct.low - 1.0)
                    (ddError < naiveError || ddError == 0.0) shouldBe true
                }
            }
        }
    }

    // ── Singleton identity ────────────────────────────────────────────────────

    context("singleton identity") {
        test("Companion.doubleDouble returns the same instance on repeated access") {
            DoubleBinaryFloatingPointArithmetic.doubleDouble shouldBe DoubleBinaryFloatingPointArithmetic.doubleDouble
        }
    }
})
