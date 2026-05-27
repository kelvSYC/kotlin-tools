package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointHypot
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSquareRoot
import org.apache.commons.numbers.core.DD

private object DdHypot : FloatingPointHypot<DD> {
    private val sqrt = FloatingPointSquareRoot.dd

    override fun DD.hypot(y: DD): DD {
        if (hi().isInfinite() || y.hi().isInfinite()) {
            return DD.of(Double.POSITIVE_INFINITY)
        }

        if (hi().isNaN() || y.hi().isNaN()) {
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
 * [FloatingPointHypot] instance for Commons Numbers [DD], computing `sqrt(x² + y²)` via Knuth
 * scaling to avoid intermediate overflow or underflow.
 */
val FloatingPointHypot.Companion.dd: FloatingPointHypot<DD> get() = DdHypot
