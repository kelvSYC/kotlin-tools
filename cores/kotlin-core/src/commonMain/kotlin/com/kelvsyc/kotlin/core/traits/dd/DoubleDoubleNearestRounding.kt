package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointNearestRounding
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

// Ties away from zero: floor(|x| + 0.5) × sign(x).
private fun nearestRoundHalfUp(x: Double): Double {
    if (x.isNaN() || x.isInfinite() || x == 0.0) return x
    val abs = abs(x)
    val rounded = floor(abs + 0.5)
    return if (x < 0.0) -rounded else rounded
}

// Ties toward zero: ceil(x − 0.5) for x ≥ 0, floor(x + 0.5) for x < 0.
private fun nearestRoundHalfDown(x: Double): Double {
    if (x.isNaN() || x.isInfinite() || x == 0.0) return x
    return if (x >= 0.0) ceil(x - 0.5) else floor(x + 0.5)
}

// Ties to even: add 2^52 to |x|, which forces the FPU to round using the default
// (ties-to-even) rounding mode, then subtract back. Only applies when |x| < 2^52;
// larger values are already integral.
private val NEAREST_ROUND_EVEN_CONST = 4503599627370496.0  // 2^52

private fun nearestRoundEven(x: Double): Double {
    if (x.isNaN() || x.isInfinite() || x == 0.0) return x
    val abs = abs(x)
    if (abs >= NEAREST_ROUND_EVEN_CONST) return x
    val rounded = (abs + NEAREST_ROUND_EVEN_CONST) - NEAREST_ROUND_EVEN_CONST
    return if (x < 0.0) -rounded else rounded
}

// Port of nearest-rounding algorithm for DoubleDouble using the same pattern as ddFloorOrCeil.
//
// When op(hi) == hi the high part is already an integer (or non-finite/zero).
//   Non-finite and zero: lo contributes nothing; return (op(hi), 0).
//   Finite integer: apply op to lo as well and re-normalise via fastTwoSum.
//     Adding 0.0 to op(lo) converts any -0.0 result to +0.0, which keeps the
//     low part of the fastTwoSum result at exactly 0.0 when op(lo) == 0.
// When op(hi) != hi (hi is not an integer), lo has no effect; return (op(hi), 0).
private fun ddNearestRound(hi: Double, lo: Double, op: (Double) -> Double): DoubleDouble {
    val y = op(hi)
    return if (y == hi) {
        if (!hi.isFinite() || hi == 0.0) {
            DoubleDouble(y, 0.0)
        } else {
            val yy = op(lo) + 0.0
            val s = y + yy
            DoubleDouble(s, yy - (s - y))
        }
    } else {
        DoubleDouble(y, 0.0)
    }
}

private val doubleDoubleInstance: FloatingPointNearestRounding<DoubleDouble> =
    object : FloatingPointNearestRounding<DoubleDouble> {
        override fun DoubleDouble.roundHalfUp(): DoubleDouble =
            ddNearestRound(high, low, ::nearestRoundHalfUp)
        override fun DoubleDouble.roundHalfDown(): DoubleDouble =
            ddNearestRound(high, low, ::nearestRoundHalfDown)
        override fun DoubleDouble.roundEven(): DoubleDouble =
            ddNearestRound(high, low, ::nearestRoundEven)
    }

/**
 * [FloatingPointNearestRounding] instance for [DoubleDouble].
 *
 * Implements the three nearest-integer rounding operations for double-double values:
 * - [roundHalfUp] — nearest, ties away from zero (C99 `round` / `RoundingMode.HALF_UP`)
 * - [roundHalfDown] — nearest, ties toward zero (`RoundingMode.HALF_DOWN`)
 * - [roundEven] — nearest, ties to even / banker's rounding (C99 `rint` / `RoundingMode.HALF_EVEN`)
 *
 * Uses the same algorithm as directed rounding: when the high part is an integer, the low part
 * is independently rounded and the result is re-normalised via a fast two-sum. Otherwise the
 * result is the rounded high part with a zero low part.
 */
val FloatingPointNearestRounding.Companion.doubleDouble: FloatingPointNearestRounding<DoubleDouble>
    get() = doubleDoubleInstance
