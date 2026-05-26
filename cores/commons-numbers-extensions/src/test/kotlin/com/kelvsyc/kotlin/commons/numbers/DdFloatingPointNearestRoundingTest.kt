package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointNearestRounding
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.core.DD

class DdFloatingPointNearestRoundingTest : FunSpec() {
    init {
        context("FloatingPointNearestRounding.Companion.dd") {
            val ops = FloatingPointNearestRounding.dd

            context("roundHalfUp") {
                test("positive non-integer 1.3 rounds to nearest") {
                    val r = with(ops) { DD.of(1.3).roundHalfUp() }
                    r.hi() shouldBe 1.0
                    r.lo() shouldBe 0.0
                }
                test("positive non-integer 1.7 rounds up") {
                    val r = with(ops) { DD.of(1.7).roundHalfUp() }
                    r.hi() shouldBe 2.0
                    r.lo() shouldBe 0.0
                }
                test("positive half-integer 0.5 rounds away from zero to 1.0") {
                    val r = with(ops) { DD.of(0.5).roundHalfUp() }
                    r.hi() shouldBe 1.0
                    r.lo() shouldBe 0.0
                }
                test("positive half-integer 1.5 rounds away from zero to 2.0") {
                    val r = with(ops) { DD.of(1.5).roundHalfUp() }
                    r.hi() shouldBe 2.0
                    r.lo() shouldBe 0.0
                }
                test("negative non-integer -1.3 rounds to nearest") {
                    val r = with(ops) { DD.of(-1.3).roundHalfUp() }
                    r.hi() shouldBe -1.0
                    r.lo() shouldBe 0.0
                }
                test("negative non-integer -1.7 rounds away from zero") {
                    val r = with(ops) { DD.of(-1.7).roundHalfUp() }
                    r.hi() shouldBe -2.0
                    r.lo() shouldBe 0.0
                }
                test("negative half-integer -0.5 rounds away from zero to -1.0") {
                    val r = with(ops) { DD.of(-0.5).roundHalfUp() }
                    r.hi() shouldBe -1.0
                    r.lo() shouldBe 0.0
                }
                test("negative half-integer -1.5 rounds away from zero to -2.0") {
                    val r = with(ops) { DD.of(-1.5).roundHalfUp() }
                    r.hi() shouldBe -2.0
                    r.lo() shouldBe 0.0
                }
                test("integer hi, positive lo: lo rounds to 0") {
                    val r = with(ops) { DD.ofSum(3.0, 0.3).roundHalfUp() }
                    r.hi() shouldBe 3.0
                    r.lo() shouldBe 0.0
                }
                test("integer hi, negative lo: lo rounds to 0") {
                    val r = with(ops) { DD.ofSum(3.0, -0.3).roundHalfUp() }
                    r.hi() shouldBe 3.0
                    r.lo() shouldBe 0.0
                }
                test("NaN returns NaN") {
                    with(ops) { DD.of(Double.NaN).roundHalfUp() }.hi().isNaN() shouldBe true
                }
                test("positive infinity returns positive infinity") {
                    val r = with(ops) { DD.of(Double.POSITIVE_INFINITY).roundHalfUp() }
                    r.hi() shouldBe Double.POSITIVE_INFINITY
                    r.lo() shouldBe 0.0
                }
                test("negative infinity returns negative infinity") {
                    val r = with(ops) { DD.of(Double.NEGATIVE_INFINITY).roundHalfUp() }
                    r.hi() shouldBe Double.NEGATIVE_INFINITY
                    r.lo() shouldBe 0.0
                }
            }

            context("roundHalfDown") {
                test("positive half-integer 0.5 rounds toward zero to 0.0") {
                    val r = with(ops) { DD.of(0.5).roundHalfDown() }
                    r.hi() shouldBe 0.0
                    r.lo() shouldBe 0.0
                }
                test("positive half-integer 1.5 rounds toward zero to 1.0") {
                    val r = with(ops) { DD.of(1.5).roundHalfDown() }
                    r.hi() shouldBe 1.0
                    r.lo() shouldBe 0.0
                }
                test("positive half-integer 2.5 rounds toward zero to 2.0") {
                    val r = with(ops) { DD.of(2.5).roundHalfDown() }
                    r.hi() shouldBe 2.0
                    r.lo() shouldBe 0.0
                }
                test("negative half-integer -0.5 rounds toward zero to 0.0") {
                    val r = with(ops) { DD.of(-0.5).roundHalfDown() }
                    r.hi() shouldBe 0.0
                    r.lo() shouldBe 0.0
                }
                test("negative half-integer -1.5 rounds toward zero to -1.0") {
                    val r = with(ops) { DD.of(-1.5).roundHalfDown() }
                    r.hi() shouldBe -1.0
                    r.lo() shouldBe 0.0
                }
                test("positive non-integer 1.3 rounds to nearest") {
                    val r = with(ops) { DD.of(1.3).roundHalfDown() }
                    r.hi() shouldBe 1.0
                    r.lo() shouldBe 0.0
                }
                test("positive non-integer 1.7 rounds to nearest") {
                    val r = with(ops) { DD.of(1.7).roundHalfDown() }
                    r.hi() shouldBe 2.0
                    r.lo() shouldBe 0.0
                }
                test("integer hi, positive lo: lo rounds to 0") {
                    val r = with(ops) { DD.ofSum(3.0, 0.3).roundHalfDown() }
                    r.hi() shouldBe 3.0
                    r.lo() shouldBe 0.0
                }
                test("NaN returns NaN") {
                    with(ops) { DD.of(Double.NaN).roundHalfDown() }.hi().isNaN() shouldBe true
                }
                test("positive infinity returns positive infinity") {
                    val r = with(ops) { DD.of(Double.POSITIVE_INFINITY).roundHalfDown() }
                    r.hi() shouldBe Double.POSITIVE_INFINITY
                    r.lo() shouldBe 0.0
                }
                test("negative infinity returns negative infinity") {
                    val r = with(ops) { DD.of(Double.NEGATIVE_INFINITY).roundHalfDown() }
                    r.hi() shouldBe Double.NEGATIVE_INFINITY
                    r.lo() shouldBe 0.0
                }
            }

            context("roundEven") {
                test("positive half-integer 0.5 rounds to even (0.0)") {
                    val r = with(ops) { DD.of(0.5).roundEven() }
                    r.hi() shouldBe 0.0
                    r.lo() shouldBe 0.0
                }
                test("positive half-integer 1.5 rounds to even (2.0)") {
                    val r = with(ops) { DD.of(1.5).roundEven() }
                    r.hi() shouldBe 2.0
                    r.lo() shouldBe 0.0
                }
                test("positive half-integer 2.5 rounds to even (2.0)") {
                    val r = with(ops) { DD.of(2.5).roundEven() }
                    r.hi() shouldBe 2.0
                    r.lo() shouldBe 0.0
                }
                test("positive half-integer 3.5 rounds to even (4.0)") {
                    val r = with(ops) { DD.of(3.5).roundEven() }
                    r.hi() shouldBe 4.0
                    r.lo() shouldBe 0.0
                }
                test("negative half-integer -0.5 rounds to even (0.0)") {
                    val r = with(ops) { DD.of(-0.5).roundEven() }
                    r.hi() shouldBe 0.0
                    r.lo() shouldBe 0.0
                }
                test("negative half-integer -1.5 rounds to even (-2.0)") {
                    val r = with(ops) { DD.of(-1.5).roundEven() }
                    r.hi() shouldBe -2.0
                    r.lo() shouldBe 0.0
                }
                test("negative half-integer -2.5 rounds to even (-2.0)") {
                    val r = with(ops) { DD.of(-2.5).roundEven() }
                    r.hi() shouldBe -2.0
                    r.lo() shouldBe 0.0
                }
                test("positive non-integer 1.3 rounds to nearest") {
                    val r = with(ops) { DD.of(1.3).roundEven() }
                    r.hi() shouldBe 1.0
                    r.lo() shouldBe 0.0
                }
                test("positive non-integer 1.7 rounds to nearest") {
                    val r = with(ops) { DD.of(1.7).roundEven() }
                    r.hi() shouldBe 2.0
                    r.lo() shouldBe 0.0
                }
                test("integer hi, positive lo: lo rounds to 0") {
                    val r = with(ops) { DD.ofSum(3.0, 0.3).roundEven() }
                    r.hi() shouldBe 3.0
                    r.lo() shouldBe 0.0
                }
                test("NaN returns NaN") {
                    with(ops) { DD.of(Double.NaN).roundEven() }.hi().isNaN() shouldBe true
                }
                test("positive infinity returns positive infinity") {
                    val r = with(ops) { DD.of(Double.POSITIVE_INFINITY).roundEven() }
                    r.hi() shouldBe Double.POSITIVE_INFINITY
                    r.lo() shouldBe 0.0
                }
                test("negative infinity returns negative infinity") {
                    val r = with(ops) { DD.of(Double.NEGATIVE_INFINITY).roundEven() }
                    r.hi() shouldBe Double.NEGATIVE_INFINITY
                    r.lo() shouldBe 0.0
                }
            }
        }
    }
}
