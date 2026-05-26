package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointCubeRoot
import org.apache.commons.numbers.core.DD

private object DdCubeRoot : FloatingPointCubeRoot<DD> {
    private val arith = FloatingPointArithmetic.dd

    override fun DD.cbrt(): DD {
        val hi = hi()
        if (hi.isNaN()) return DD.of(Double.NaN)
        if (hi.isInfinite()) return DD.of(hi)
        if (this.isZero()) return this

        val negative = hi < 0.0
        val absThis = if (negative) this.negate() else this

        val r = kotlin.math.cbrt(absThis.hi())
        val rDD = DD.of(r)
        val r2 = rDD.multiply(rDD)
        val r3 = r2.multiply(rDD)
        val diff = absThis.subtract(r3)
        val three_r2 = r2.multiply(DD.of(3.0))
        val correction = diff.divide(three_r2)
        val result = rDD.add(correction)

        return if (negative) result.negate() else result
    }
}

/**
 * [FloatingPointCubeRoot] instance for Commons Numbers [DD].
 *
 * Computes the cube root using a Newton refinement step over a double-precision seed.
 * The seed `r ≈ ∛(hi)` is ~52 bits accurate; one Newton step (`r' = r + (|x| - r³) / (3r²)`)
 * yields ~104 bits of accuracy.
 *
 * The cube root is defined for negative inputs: `cbrt(-x) = -cbrt(x)`. The implementation
 * works on the absolute value and restores the sign of the input at the end.
 *
 * Special cases:
 * - `NaN → NaN`
 * - `±∞ → ±∞` (sign preserved)
 * - `±0 → ±0` (sign preserved)
 */
val FloatingPointCubeRoot.Companion.dd: FloatingPointCubeRoot<DD> get() = DdCubeRoot
