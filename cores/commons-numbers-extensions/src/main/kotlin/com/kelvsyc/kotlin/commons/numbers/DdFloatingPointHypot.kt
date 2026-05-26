package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointHypot
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSquareRoot
import org.apache.commons.numbers.core.DD

private object DdHypot : FloatingPointHypot<DD> {
    private val sqrt = FloatingPointSquareRoot.dd

    override fun DD.hypot(y: DD): DD {
        val hx = hi()
        val hy = y.hi()

        if (hx.isInfinite() || hy.isInfinite()) {
            return DD.of(Double.POSITIVE_INFINITY)
        }

        if (hx.isNaN() || hy.isNaN()) {
            return DD.of(Double.NaN)
        }

        var ax = if (hi() < 0.0) this.negate() else this
        var ay = if (y.hi() < 0.0) y.negate() else y

        if (ax.hi() < ay.hi() || (ax.hi() == ay.hi() && ax.lo() < ay.lo())) {
            val temp = ax
            ax = ay
            ay = temp
        }

        if (ay.isZero()) {
            return ax
        }

        val r = ay.divide(ax)
        val rSq = r.multiply(r)
        val one = DD.of(1.0)
        val inner = one.add(rSq)
        val sqrtInner = with(sqrt) { inner.sqrt() }
        return ax.multiply(sqrtInner)
    }
}

/**
 * [FloatingPointHypot] instance for Commons Numbers [DD].
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
val FloatingPointHypot.Companion.dd: FloatingPointHypot<DD> get() = DdHypot
