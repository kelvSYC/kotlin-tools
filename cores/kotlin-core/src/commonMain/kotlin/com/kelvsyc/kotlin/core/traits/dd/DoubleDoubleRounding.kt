package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointRounding

// Port of DD.floorOrCeil — see Apache Commons Numbers DD source for the rationale.
//
// When op(hi) == hi the high part is already an integer (or non-finite/zero).
//   Non-finite and zero: lo contributes nothing; return (op(hi), 0).
//   Finite integer: apply op to lo as well and re-normalise via fastTwoSum.
//     Adding 0.0 to op(lo) converts any -0.0 result to +0.0, which keeps the
//     low part of the fastTwoSum result at exactly 0.0 when op(lo) == 0 —
//     this matters only for ceil when lo ∈ (−1, 0].
// When op(hi) != hi (hi is not an integer), lo has no effect; return (op(hi), 0).
private fun ddFloorOrCeil(hi: Double, lo: Double, op: (Double) -> Double): DoubleDouble {
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

private val doubleDoubleInstance: FloatingPointRounding<DoubleDouble> =
    object : FloatingPointRounding<DoubleDouble> {
        override fun DoubleDouble.floor(): DoubleDouble =
            ddFloorOrCeil(high, low) { kotlin.math.floor(it) }
        override fun DoubleDouble.ceil(): DoubleDouble =
            ddFloorOrCeil(high, low) { kotlin.math.ceil(it) }
    }

/**
 * [FloatingPointRounding] instance for [DoubleDouble].
 *
 * Implements the floor and ceiling operations for double-double values, following the same
 * algorithm as Apache Commons Numbers [DD][org.apache.commons.numbers.core.DD]:
 * - If the high part is already an integer (or non-finite), the low part is independently
 *   rounded and the result is re-normalised via a fast two-sum.
 * - Otherwise the result is the rounded high part with a zero low part.
 */
val FloatingPointRounding.Companion.doubleDouble: FloatingPointRounding<DoubleDouble>
    get() = doubleDoubleInstance
