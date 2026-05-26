package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointLogb
import org.apache.commons.numbers.core.DD

private object DdLogb : FloatingPointLogb<DD> {
    override fun DD.logb(): DD {
        // Inline the logb computation on the high component
        val hi = hi()
        val logbResult = when {
            hi.isNaN() -> Double.NaN
            hi.isInfinite() -> Double.POSITIVE_INFINITY
            hi == 0.0 -> Double.NEGATIVE_INFINITY
            else -> {
                // Use ilogb and convert back
                val ilogbVal = computeIlogb(hi)
                ilogbVal.toDouble()
            }
        }
        return DD.of(logbResult)
    }

    override fun DD.ilogb(): Int {
        return computeIlogb(hi())
    }

    private fun computeIlogb(x: Double): Int {
        return when {
            x.isNaN() || x.isInfinite() -> Int.MAX_VALUE
            x == 0.0 -> Int.MIN_VALUE
            else -> {
                // Extract biased exponent from double bit pattern
                val bits = x.toBits()
                val biasedExp = ((bits ushr 52) and 0x7FFL).toInt()
                when {
                    biasedExp != 0 -> biasedExp - 1023  // Normal number
                    else -> {
                        // Subnormal number - count leading zeros in mantissa
                        val mantissa = bits and 0xFFFFFFFFFFFFL
                        if (mantissa == 0L) Int.MIN_VALUE
                        else -1023 - (mantissa.countLeadingZeroBits() - 12)
                    }
                }
            }
        }
    }
}

/**
 * [FloatingPointLogb] instance for Commons Numbers [DD].
 *
 * Delegates `logb` and `ilogb` to the high component. Since the binary exponent of the
 * extended-precision value is always the same as the exponent of the high component (due to
 * the invariant `|lo| ≤ ulp(hi)/2`), the low component is ignored.
 * All special cases (NaN, ±∞, ±0) are correctly handled.
 */
val FloatingPointLogb.Companion.dd: FloatingPointLogb<DD> get() = DdLogb
