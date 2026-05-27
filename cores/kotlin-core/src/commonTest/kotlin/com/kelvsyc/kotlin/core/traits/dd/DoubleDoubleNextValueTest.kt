package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointNextValue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DoubleDoubleNextValueTest : FunSpec({

    context("FloatingPointNextValue.Companion.doubleDouble") {
        val ops = FloatingPointNextValue.doubleDouble

        context("nextUp special cases") {
            test("NaN returns NaN") {
                with(ops) { DoubleDouble.NaN.nextUp() }.high.isNaN() shouldBe true
            }

            test("positive infinity returns positive infinity") {
                val r = with(ops) { DoubleDouble.POSITIVE_INFINITY.nextUp() }
                r.high shouldBe Double.POSITIVE_INFINITY
                r.low shouldBe 0.0
            }

            test("negative infinity returns -maxValue") {
                val r = with(ops) { DoubleDouble.NEGATIVE_INFINITY.nextUp() }
                r shouldBe -DoubleDouble.maxValue
            }

            test("maxValue returns positive infinity") {
                val r = with(ops) { DoubleDouble.maxValue.nextUp() }
                r.high shouldBe Double.POSITIVE_INFINITY
                r.low shouldBe 0.0
            }

            test("positive zero returns minValue") {
                val r = with(ops) { DoubleDouble(0.0, 0.0).nextUp() }
                r shouldBe DoubleDouble.minValue
            }

            test("negative zero returns minValue") {
                val r = with(ops) { DoubleDouble(-0.0, 0.0).nextUp() }
                r shouldBe DoubleDouble.minValue
            }
        }

        context("nextDown special cases") {
            test("NaN returns NaN") {
                with(ops) { DoubleDouble.NaN.nextDown() }.high.isNaN() shouldBe true
            }

            test("negative infinity returns negative infinity") {
                val r = with(ops) { DoubleDouble.NEGATIVE_INFINITY.nextDown() }
                r.high shouldBe Double.NEGATIVE_INFINITY
                r.low shouldBe 0.0
            }

            test("positive infinity returns maxValue") {
                val r = with(ops) { DoubleDouble.POSITIVE_INFINITY.nextDown() }
                r shouldBe DoubleDouble.maxValue
            }

            test("-maxValue returns negative infinity") {
                val r = with(ops) { (-DoubleDouble.maxValue).nextDown() }
                r.high shouldBe Double.NEGATIVE_INFINITY
                r.low shouldBe 0.0
            }

            test("positive zero returns -minValue") {
                val r = with(ops) { DoubleDouble(0.0, 0.0).nextDown() }
                r.high shouldBe -Double.MIN_VALUE
                r.low shouldBe 0.0
            }
        }

        context("monotonicity") {
            test("nextUp(1.0) > 1.0") {
                val x = DoubleDouble(1.0, 0.0)
                val r = with(ops) { x.nextUp() }
                (r > x) shouldBe true
            }

            test("nextDown(1.0) < 1.0") {
                val x = DoubleDouble(1.0, 0.0)
                val r = with(ops) { x.nextDown() }
                (r < x) shouldBe true
            }

            test("nextUp(-1.0) > -1.0") {
                val x = DoubleDouble(-1.0, 0.0)
                val r = with(ops) { x.nextUp() }
                (r > x) shouldBe true
            }

            test("nextUp with positive lo still increases") {
                val x = DoubleDouble(1.0, 1.0e-20)
                val r = with(ops) { x.nextUp() }
                (r > x) shouldBe true
            }

            test("nextUp with negative lo still increases") {
                val x = DoubleDouble(1.0, -1.0e-20)
                val r = with(ops) { x.nextUp() }
                (r > x) shouldBe true
            }
        }

        context("round-trip") {
            test("nextDown(nextUp(x)) == x for normal value") {
                val x = DoubleDouble(1.5, 0.0)
                val r = with(ops) { x.nextUp().nextDown() }
                r shouldBe x
            }

            test("nextUp(nextDown(x)) == x for normal value") {
                val x = DoubleDouble(1.5, 0.0)
                val r = with(ops) { x.nextDown().nextUp() }
                r shouldBe x
            }

            test("round-trip for value with nonzero lo") {
                val x = DoubleDouble(2.0, 1.0e-20)
                val r = with(ops) { x.nextDown().nextUp() }
                r shouldBe x
            }
        }

        context("exponent boundary crossing") {
            // The last double before 2.0 lies in the [1, 2) exponent band with ulp = 2^-52.
            // Its max valid lo is ulp/2 = 2^-53.  At this extremum, nextUp must promote hi
            // from the [1, 2) band into the [2, 4) band (hi = 2.0) with a negative lo.
            // Concrete bit values: lastBefore2 = 0x3FFFFFFFFFFFFFFF, maxLo = 0x3CA0000000000000.
            test("nextUp carries across the power-of-2 boundary at 2.0") {
                val lastBefore2 = Double.fromBits(0x3FFFFFFFFFFFFFFFL) // 2.0 - 2^-52
                val maxLo = Double.fromBits(0x3CA0000000000000L)        // 2^-53 = ulp(lastBefore2)/2
                val x = DoubleDouble(lastBefore2, maxLo)
                val r = with(ops) { x.nextUp() }
                r.high shouldBe 2.0
                (r.low < 0.0) shouldBe true  // lo is negative inside the wider [2, 4) band
                (r > x) shouldBe true
            }

            // The minimum valid lo for hi = 2.0 is -ulp(2.0)/2 = -2^-52.
            // nextDown from this point must land back in the [1, 2) band.
            test("nextDown carries across the power-of-2 boundary at 2.0") {
                val minLo2 = -Double.fromBits(0x3CB0000000000000L)       // -2^-52 = -ulp(2.0)/2
                val x = DoubleDouble(2.0, minLo2)
                val r = with(ops) { x.nextDown() }
                r.high shouldBe Double.fromBits(0x3FFFFFFFFFFFFFFFL)    // 2.0 - 2^-52 = lastBefore2
                (r < x) shouldBe true
            }
        }

        context("identity nextDown(x) == -nextUp(-x)") {
            test("holds for positive normal value") {
                val x = DoubleDouble(3.14, 1.0e-18)
                val byIdentity = with(ops) { -(-x).nextUp() }
                val direct = with(ops) { x.nextDown() }
                direct shouldBe byIdentity
            }

            test("holds for negative normal value") {
                val x = DoubleDouble(-2.71, -1.0e-17)
                val byIdentity = with(ops) { -(-x).nextUp() }
                val direct = with(ops) { x.nextDown() }
                direct shouldBe byIdentity
            }
        }
    }
})
