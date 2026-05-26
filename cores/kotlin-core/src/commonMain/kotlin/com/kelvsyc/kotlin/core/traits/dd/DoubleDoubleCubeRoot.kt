package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointCubeRoot

private val doubleDoubleInstance: FloatingPointCubeRoot<DoubleDouble> =
    object : FloatingPointCubeRoot<DoubleDouble> {
        private val arith = DoubleBinaryFloatingPointArithmetic.doubleDouble

        override fun DoubleDouble.cbrt(): DoubleDouble {
            if (high.isNaN()) return DoubleDouble.NaN
            if (high.isInfinite()) return DoubleDouble(high, 0.0)  // ±∞ → ±∞ with lo=0
            if (high == 0.0) return this  // preserves ±0

            val negative = high < 0.0
            val absThis = if (negative) with(arith) { this@cbrt.negate() } else this@cbrt

            val r = kotlin.math.cbrt(absThis.high)
            val rDD = DoubleDouble(r, 0.0)
            val r2 = with(arith) { rDD.multiply(rDD) }
            val r3 = with(arith) { r2.multiply(rDD) }
            val diff = with(arith) { absThis.subtract(r3) }
            val threeR2 = with(arith) { r2.multiply(DoubleDouble(3.0, 0.0)) }
            val correction = with(arith) { diff.divide(threeR2) }
            val result = with(arith) { rDD.add(correction) }

            return if (negative) with(arith) { result.negate() } else result
        }
    }

/**
 * [FloatingPointCubeRoot] instance for [DoubleDouble].
 *
 * Computes the cube root using a Newton refinement step over a double-precision seed.
 * The seed `r ≈ ∛(high)` is ~52 bits accurate; one Newton step (`r' = r + (|x| - r³) / (3r²)`)
 * yields ~104 bits of accuracy, sufficient for DoubleDouble arithmetic (~106 bits).
 *
 * The cube root is defined for negative inputs: `cbrt(-x) = -cbrt(x)`. The implementation
 * works on the absolute value and restores the sign of the input at the end.
 *
 * Special cases:
 * - `NaN → NaN`
 * - `±∞ → ±∞` (sign preserved)
 * - `±0 → ±0` (sign preserved)
 */
val FloatingPointCubeRoot.Companion.doubleDouble: FloatingPointCubeRoot<DoubleDouble>
    get() = doubleDoubleInstance
