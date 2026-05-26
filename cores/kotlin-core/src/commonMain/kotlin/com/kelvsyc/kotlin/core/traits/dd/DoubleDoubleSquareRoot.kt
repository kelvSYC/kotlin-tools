package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSquareRoot

private val doubleDoubleInstance: FloatingPointSquareRoot<DoubleDouble> =
    object : FloatingPointSquareRoot<DoubleDouble> {
        override fun DoubleDouble.sqrt(): DoubleDouble {
            // IEEE 754 special cases
            if (high.isNaN() || high < 0.0) return DoubleDouble.NaN
            if (high == 0.0) return this  // preserves ±0
            if (high.isInfinite()) return this  // +∞ → +∞ (negative already caught above)

            // Use one Newton refinement step:
            // s ≈ √x (double-precision seed, ~52 bits)
            // s' ≈ s + (x - s²) / (2s)  (one refinement step, ~104 bits)
            val s = kotlin.math.sqrt(high)
            val sDD = DoubleDouble(s, 0.0)

            // DoubleDouble arithmetic to compute correction: (x - s²) / (2s)
            val arith = DoubleBinaryFloatingPointArithmetic.doubleDouble
            val s2 = with(arith) { sDD.multiply(sDD) }
            val diff = with(arith) { this@sqrt.subtract(s2) }
            val correction = with(arith) { diff.divide(DoubleDouble(2.0 * s, 0.0)) }
            return with(arith) { sDD.add(correction) }
        }
    }

/**
 * [FloatingPointSquareRoot] instance for [DoubleDouble].
 *
 * Computes the square root using a Newton refinement step over a double-precision seed.
 * The seed `s ≈ √(high)` is ~52 bits accurate; one Newton step (`s' = s + (x - s²) / (2s)`)
 * yields ~104 bits of accuracy, sufficient for DoubleDouble arithmetic (~106 bits).
 *
 * Special cases:
 * - `NaN → NaN`
 * - Negative → `NaN`
 * - `+∞ → +∞`
 * - `±0 → ±0` (preserves sign of zero)
 */
val FloatingPointSquareRoot.Companion.doubleDouble: FloatingPointSquareRoot<DoubleDouble>
    get() = doubleDoubleInstance
