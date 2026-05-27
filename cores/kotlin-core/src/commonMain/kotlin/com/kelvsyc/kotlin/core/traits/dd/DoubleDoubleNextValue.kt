package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointNextValue

// Same bit-pattern nextUp used by the Double instance; inlined here to avoid a cross-file
// dependency on a private function. lo is always finite for a valid DoubleDouble with finite hi,
// so the NaN branch is defensive.
private fun nextUpDouble(x: Double): Double {
    val bits = x.toRawBits()
    return when {
        x.isNaN() -> x
        bits == 0x7FF0000000000000L -> x
        bits == 0L || bits == Long.MIN_VALUE -> Double.fromBits(1L)
        bits > 0L -> Double.fromBits(bits + 1L)
        else -> Double.fromBits(bits - 1L)
    }
}

private val doubleDoubleInstance: FloatingPointNextValue<DoubleDouble> =
    object : FloatingPointNextValue<DoubleDouble> {
        override fun DoubleDouble.nextUp(): DoubleDouble {
            if (high.isNaN()) return this
            if (high == Double.POSITIVE_INFINITY) return this
            // fastTwoSum(-∞, nextUp(lo)) computes s = -∞, e = b - (NaN) = NaN — must short-circuit.
            if (high == Double.NEGATIVE_INFINITY) return -DoubleDouble.maxValue
            val b = nextUpDouble(low)
            val s = high + b
            // When hi = Double.MAX_VALUE and lo is at its maximum, nextUp(lo) tips s over to +∞.
            // The subsequent error term b - (s - hi) = finite - +∞ = -∞, producing an invalid pair.
            if (s == Double.POSITIVE_INFINITY) return DoubleDouble.POSITIVE_INFINITY
            // + 0.0 converts any -0.0 result (from nextUpDouble(-MIN_VALUE) = -0.0) to +0.0,
            // keeping lo canonical. Non-zero values are unaffected.
            val e = (b - (s - high)) + 0.0
            return DoubleDouble(s, e)
        }

        override fun DoubleDouble.nextDown(): DoubleDouble {
            val r = -(-this).nextUp()
            // Negation flips lo's sign bit, potentially producing -0.0; canonicalize to +0.0.
            return DoubleDouble(r.high, r.low + 0.0)
        }
    }

/**
 * [FloatingPointNextValue] instance for [DoubleDouble].
 *
 * `nextUp` returns the smallest representable [DoubleDouble] strictly greater than the receiver;
 * `nextDown` returns the largest representable [DoubleDouble] strictly less than the receiver.
 * The identity `nextDown(x) = −nextUp(−x)` holds. Special values follow IEEE 754:
 * `nextUp(+∞) = +∞`, `nextUp(−∞) = −maxValue`, `nextUp(NaN) = NaN`.
 */
val FloatingPointNextValue.Companion.doubleDouble: FloatingPointNextValue<DoubleDouble>
    get() = doubleDoubleInstance
