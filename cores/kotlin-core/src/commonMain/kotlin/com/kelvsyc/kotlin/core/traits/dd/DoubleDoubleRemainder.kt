package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointNearestRounding
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointRemainder
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointRounding

// Returns a short-circuit result for special inputs, or null to proceed with the general formula.
// x = ±∞ → NaN (same as Double %); y = 0 → NaN; y = ±∞ → x (n = 0, but 0 × ∞ = NaN via arithmetic,
// so the general formula would return NaN — hence the explicit branch here).
private fun specialCase(x: DoubleDouble, y: DoubleDouble): DoubleDouble? = when {
    x.high.isNaN() || y.high.isNaN() -> DoubleDouble.NaN
    x.high.isInfinite() -> DoubleDouble.NaN
    y.high == 0.0 -> DoubleDouble.NaN
    y.high.isInfinite() -> x
    else -> null
}

private val truncatingInstance: FloatingPointRemainder<DoubleDouble> =
    object : FloatingPointRemainder<DoubleDouble> {
        private val arith = DoubleBinaryFloatingPointArithmetic.doubleDouble
        private val rounding = FloatingPointRounding.doubleDouble

        override fun DoubleDouble.rem(other: DoubleDouble): DoubleDouble {
            specialCase(this, other)?.let { return it }
            val q = with(arith) { this@rem.divide(other) }
            val n = with(rounding) { q.trunc() }
            val product = with(arith) { n.multiply(other) }
            return with(arith) { this@rem.subtract(product) }
        }
    }

private val ieee754Instance: FloatingPointRemainder<DoubleDouble> =
    object : FloatingPointRemainder<DoubleDouble> {
        private val arith = DoubleBinaryFloatingPointArithmetic.doubleDouble
        private val nearestRounding = FloatingPointNearestRounding.doubleDouble

        override fun DoubleDouble.rem(other: DoubleDouble): DoubleDouble {
            specialCase(this, other)?.let { return it }
            val q = with(arith) { this@rem.divide(other) }
            val n = with(nearestRounding) { q.roundEven() }
            val product = with(arith) { n.multiply(other) }
            return with(arith) { this@rem.subtract(product) }
        }
    }

/**
 * [FloatingPointRemainder] instance for [DoubleDouble], truncating variant.
 *
 * Computes `x − trunc(x / y) × y`. The result has the same sign as `x` (or is zero), matching
 * the semantics of Kotlin's `%` operator on [Double].
 *
 * Precision degrades when `|x / y|` exceeds approximately 2^106: at that scale the full integer
 * quotient exhausts the representable precision of a [DoubleDouble] and the fractional part — along
 * with the remainder — can no longer be recovered. This is the same structural limitation as
 * Kotlin's `%` operator on [Double] (where the threshold is approximately 2^53).
 */
val FloatingPointRemainder.Companion.doubleDoubleTruncating: FloatingPointRemainder<DoubleDouble>
    get() = truncatingInstance

/**
 * [FloatingPointRemainder] instance for [DoubleDouble], IEEE 754 nearest-integer variant.
 *
 * Computes `x − roundEven(x / y) × y`. The result lies in `[−|y| / 2, +|y| / 2]` and may have
 * the opposite sign from `x`. Precision degrades when `|x / y|` exceeds approximately 2^106, for
 * the same reason as [doubleDoubleTruncating].
 */
val FloatingPointRemainder.Companion.doubleDoubleIeee754: FloatingPointRemainder<DoubleDouble>
    get() = ieee754Instance
