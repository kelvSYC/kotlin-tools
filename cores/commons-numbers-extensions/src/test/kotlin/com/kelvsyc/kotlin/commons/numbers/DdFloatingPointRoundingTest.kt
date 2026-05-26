package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointRounding
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.core.DD

class DdFloatingPointRoundingTest : FunSpec() {
    init {
        context("FloatingPointRounding.Companion.dd") {
            val ops = FloatingPointRounding.dd

            context("floor") {
                test("positive non-integer rounds down") {
                    val r = with(ops) { DD.of(1.5).floor() }
                    r.hi() shouldBe 1.0
                    r.lo() shouldBe 0.0
                }
                test("negative non-integer rounds down") {
                    val r = with(ops) { DD.of(-1.5).floor() }
                    r.hi() shouldBe -2.0
                    r.lo() shouldBe 0.0
                }
                test("integer hi, positive lo: lo has no fractional effect") {
                    // floor(3.0 + 0.3) = floor(3.3) = 3.0
                    val r = with(ops) { DD.ofSum(3.0, 0.3).floor() }
                    r.hi() shouldBe 3.0
                    r.lo() shouldBe 0.0
                }
                test("integer hi, negative lo: lo carries borrow") {
                    // floor(3.0 + (-0.3)) = floor(2.7) = 2.0
                    val r = with(ops) { DD.ofSum(3.0, -0.3).floor() }
                    r.hi() shouldBe 2.0
                    r.lo() shouldBe 0.0
                }
                test("NaN returns NaN") {
                    with(ops) { DD.of(Double.NaN).floor() }.hi().isNaN() shouldBe true
                }
                test("positive infinity returns positive infinity") {
                    val r = with(ops) { DD.of(Double.POSITIVE_INFINITY).floor() }
                    r.hi() shouldBe Double.POSITIVE_INFINITY
                    r.lo() shouldBe 0.0
                }
            }

            context("trunc") {
                test("positive non-integer rounds toward zero") {
                    val r = with(ops) { DD.of(1.7).trunc() }
                    r.hi() shouldBe 1.0
                    r.lo() shouldBe 0.0
                }
                test("negative non-integer rounds toward zero") {
                    val r = with(ops) { DD.of(-1.7).trunc() }
                    r.hi() shouldBe -1.0
                    r.lo() shouldBe 0.0
                }
                test("integer hi, positive lo: lo has no fractional effect") {
                    // trunc(3.0 + 0.3) = trunc(3.3) = 3.0
                    val r = with(ops) { DD.ofSum(3.0, 0.3).trunc() }
                    r.hi() shouldBe 3.0
                    r.lo() shouldBe 0.0
                }
                test("integer hi, negative lo: lo carries borrow") {
                    // trunc(3.0 + (-0.3)) = trunc(2.7) = 2.0
                    val r = with(ops) { DD.ofSum(3.0, -0.3).trunc() }
                    r.hi() shouldBe 2.0
                    r.lo() shouldBe 0.0
                }
                test("NaN returns NaN") {
                    with(ops) { DD.of(Double.NaN).trunc() }.hi().isNaN() shouldBe true
                }
            }

            context("roundUp") {
                test("positive non-integer rounds away from zero") {
                    val r = with(ops) { DD.of(1.3).roundUp() }
                    r.hi() shouldBe 2.0
                    r.lo() shouldBe 0.0
                }
                test("negative non-integer rounds away from zero") {
                    val r = with(ops) { DD.of(-1.3).roundUp() }
                    r.hi() shouldBe -2.0
                    r.lo() shouldBe 0.0
                }
                test("integer is unchanged") {
                    val r = with(ops) { DD.of(3.0).roundUp() }
                    r.hi() shouldBe 3.0
                    r.lo() shouldBe 0.0
                }
                test("NaN returns NaN") {
                    with(ops) { DD.of(Double.NaN).roundUp() }.hi().isNaN() shouldBe true
                }
            }

            context("ceil") {
                test("positive non-integer rounds up") {
                    val r = with(ops) { DD.of(1.5).ceil() }
                    r.hi() shouldBe 2.0
                    r.lo() shouldBe 0.0
                }
                test("negative non-integer rounds up") {
                    val r = with(ops) { DD.of(-1.5).ceil() }
                    r.hi() shouldBe -1.0
                    r.lo() shouldBe 0.0
                }
                test("integer hi, positive lo: lo carries increment") {
                    // ceil(3.0 + 0.3) = ceil(3.3) = 4.0
                    val r = with(ops) { DD.ofSum(3.0, 0.3).ceil() }
                    r.hi() shouldBe 4.0
                    r.lo() shouldBe 0.0
                }
                test("integer hi, negative lo: lo has no fractional effect") {
                    // ceil(3.0 + (-0.3)) = ceil(2.7) = 3.0
                    val r = with(ops) { DD.ofSum(3.0, -0.3).ceil() }
                    r.hi() shouldBe 3.0
                    r.lo() shouldBe 0.0
                }
                test("NaN returns NaN") {
                    with(ops) { DD.of(Double.NaN).ceil() }.hi().isNaN() shouldBe true
                }
                test("negative infinity returns negative infinity") {
                    val r = with(ops) { DD.of(Double.NEGATIVE_INFINITY).ceil() }
                    r.hi() shouldBe Double.NEGATIVE_INFINITY
                    r.lo() shouldBe 0.0
                }
            }
        }
    }
}
