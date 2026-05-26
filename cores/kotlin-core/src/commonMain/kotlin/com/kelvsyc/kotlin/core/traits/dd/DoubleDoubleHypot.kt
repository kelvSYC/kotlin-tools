package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointHypot
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSquareRoot

private val doubleDoubleInstance: FloatingPointHypot<DoubleDouble> =
    object : FloatingPointHypot<DoubleDouble> {
        private val arith = DoubleBinaryFloatingPointArithmetic.doubleDouble
        private val sqrt = FloatingPointSquareRoot.doubleDouble

        override fun DoubleDouble.hypot(y: DoubleDouble): DoubleDouble {
            val hx = high
            val hy = y.high

            if (hx.isInfinite() || hy.isInfinite()) {
                return DoubleDouble.POSITIVE_INFINITY
            }

            if (hx.isNaN() || hy.isNaN()) {
                return DoubleDouble.NaN
            }

            var ax = if (high < 0.0) with(arith) { this@hypot.negate() } else this
            var ay = if (y.high < 0.0) with(arith) { y.negate() } else y

            if (ax.high < ay.high || (ax.high == ay.high && ax.low < ay.low)) {
                val temp = ax
                ax = ay
                ay = temp
            }

            if (ay.high == 0.0) {
                return ax
            }

            val r = with(arith) { ay.divide(ax) }
            val rSq = with(arith) { r.multiply(r) }
            val one = DoubleDouble(1.0, 0.0)
            val inner = with(arith) { one.add(rSq) }
            val sqrtInner = with(sqrt) { inner.sqrt() }
            return with(arith) { ax.multiply(sqrtInner) }
        }
    }

/**
 * [FloatingPointHypot] instance for [DoubleDouble].
 *
 * Computes `sqrt(x² + y²)` using Knuth scaling: `ax * sqrt(1 + (ay/ax)²)` where `ax ≥ ay ≥ 0`.
 * This avoids intermediate overflow or underflow in the standard formula `sqrt(x² + y²)`.
 *
 * Special cases follow IEEE 754:
 * - `hypot(±∞, y) = +∞` (infinity dominates NaN)
 * - `hypot(x, NaN) = NaN`
 * - `hypot(x, 0) = |x|`
 * - The operation is symmetric: `hypot(x, y) == hypot(y, x)`
 */
val FloatingPointHypot.Companion.doubleDouble: FloatingPointHypot<DoubleDouble>
    get() = doubleDoubleInstance
