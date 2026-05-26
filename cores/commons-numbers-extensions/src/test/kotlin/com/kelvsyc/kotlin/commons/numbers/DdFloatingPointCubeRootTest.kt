package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointCubeRoot
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.numbers.core.DD

class DdFloatingPointCubeRootTest : FunSpec() {
    init {
        context("FloatingPointCubeRoot.Companion.dd") {
            val ops = FloatingPointCubeRoot.dd

            context("special cases") {
                test("NaN returns NaN") {
                    with(ops) { DD.of(Double.NaN).cbrt() }.hi().isNaN() shouldBe true
                }

                test("positive infinity returns positive infinity") {
                    val r = with(ops) { DD.of(Double.POSITIVE_INFINITY).cbrt() }
                    r.hi() shouldBe Double.POSITIVE_INFINITY
                    r.lo() shouldBe 0.0
                }

                test("negative infinity returns negative infinity") {
                    val r = with(ops) { DD.of(Double.NEGATIVE_INFINITY).cbrt() }
                    r.hi() shouldBe Double.NEGATIVE_INFINITY
                    r.lo() shouldBe 0.0
                }

                test("positive zero returns positive zero") {
                    val r = with(ops) { DD.of(0.0).cbrt() }
                    r.hi() shouldBe 0.0
                    r.lo() shouldBe 0.0
                }

                test("negative zero returns negative zero") {
                    val r = with(ops) { DD.of(-0.0).cbrt() }
                    r.hi() shouldBe -0.0
                    r.lo() shouldBe 0.0
                }
            }

            context("perfect cubes") {
                test("cbrt(8) ≈ 2") {
                    val r = with(ops) { DD.of(8.0).cbrt() }
                    r.hi() shouldBe 2.0
                    r.lo() shouldBe 0.0
                }

                test("cbrt(27) ≈ 3") {
                    val r = with(ops) { DD.of(27.0).cbrt() }
                    r.hi() shouldBe 3.0
                    r.lo() shouldBe 0.0
                }

                test("cbrt(-8) ≈ -2") {
                    val r = with(ops) { DD.of(-8.0).cbrt() }
                    r.hi() shouldBe -2.0
                    r.lo() shouldBe 0.0
                }

                test("cbrt(-27) ≈ -3") {
                    val r = with(ops) { DD.of(-27.0).cbrt() }
                    r.hi() shouldBe -3.0
                    r.lo() shouldBe 0.0
                }
            }

            context("roundtrip verification") {
                test("cbrt(2) multiplied three times ≈ 2") {
                    val x = DD.of(2.0)
                    val cbrt_x = with(ops) { x.cbrt() }

                    // Verify (cbrt_x)³ ≈ x using DD arithmetic
                    val cubed = cbrt_x.multiply(cbrt_x).multiply(cbrt_x)

                    // High component should be very close to 2.0
                    cubed.hi() shouldBe 2.0
                }

                test("cbrt(0.5) multiplied three times ≈ 0.5") {
                    val x = DD.of(0.5)
                    val cbrt_x = with(ops) { x.cbrt() }

                    val cubed = cbrt_x.multiply(cbrt_x).multiply(cbrt_x)

                    cubed.hi() shouldBe 0.5
                }

                test("cbrt(-2) multiplied three times ≈ -2") {
                    val x = DD.of(-2.0)
                    val cbrt_x = with(ops) { x.cbrt() }

                    val cubed = cbrt_x.multiply(cbrt_x).multiply(cbrt_x)

                    cubed.hi() shouldBe -2.0
                }

                test("cbrt(3) cubed round-trips back to 3.0") {
                    val x = DD.of(3.0)
                    val r = with(ops) { x.cbrt() }
                    val cubed = r.multiply(r).multiply(r)
                    cubed.hi() shouldBe 3.0
                }
            }
        }
    }
}
