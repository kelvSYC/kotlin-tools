package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointHypot
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSquareRoot

private val doubleDoubleInstance: FloatingPointHypot<DoubleDouble> =
    object : FloatingPointHypot<DoubleDouble> {
        private val arith = DoubleBinaryFloatingPointArithmetic.doubleDouble
        private val sqrt = FloatingPointSquareRoot.doubleDouble

        override fun DoubleDouble.hypot(y: DoubleDouble): DoubleDouble {
            if (high.isInfinite() || y.high.isInfinite()) {
                return DoubleDouble.POSITIVE_INFINITY
            }

            if (high.isNaN() || y.high.isNaN()) {
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
 * [FloatingPointHypot] instance for [DoubleDouble], computing `sqrt(x² + y²)` via Knuth scaling
 * to avoid intermediate overflow or underflow.
 */
val FloatingPointHypot.Companion.doubleDouble: FloatingPointHypot<DoubleDouble>
    get() = doubleDoubleInstance
