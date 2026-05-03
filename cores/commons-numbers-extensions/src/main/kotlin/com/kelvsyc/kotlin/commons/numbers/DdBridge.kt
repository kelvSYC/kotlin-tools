package com.kelvsyc.kotlin.commons.numbers

import com.kelvsyc.kotlin.core.Converter
import com.kelvsyc.kotlin.core.fp.DoubleDouble
import org.apache.commons.numbers.core.DD

/**
 * Converts this [DD] to a [DoubleDouble].
 *
 * The (hi, lo) pair is transferred directly. NaN [DD] values map to [DoubleDouble.NaN] rather
 * than invoking [DoubleDouble.create], which rejects NaN arguments.
 */
fun DD.toDoubleDouble(): DoubleDouble {
    if (hi().isNaN()) return DoubleDouble.NaN
    return DoubleDouble.create(hi(), lo())
}

/**
 * Converts this [DoubleDouble] to a [DD].
 *
 * Uses [DD.ofSum] (TwoSum) to reconstruct the double-double pair from the (high, low) components.
 * For normalised pairs where `|low| < ulp(high) / 2`, the result has the same hi/lo as this value.
 * For the boundary case `|low| == ulp(high) / 2`, TwoSum may return an equivalent but structurally
 * different pair (same mathematical value). NaN maps to `DD.of(Double.NaN)`.
 */
fun DoubleDouble.toDD(): DD {
    if (isNaN()) return DD.of(Double.NaN)
    return DD.ofSum(high, low)
}

private object DdDoubleDoubleConverter : Converter<DD, DoubleDouble>() {
    override fun doForward(a: DD): DoubleDouble = a.toDoubleDouble()
    override fun doBackward(b: DoubleDouble): DD = b.toDD()
}

/**
 * [Converter] between Commons Numbers [DD] and kotlin-core [DoubleDouble].
 *
 * Forward: [DD.toDoubleDouble]. Backward: [DoubleDouble.toDD].
 * Both directions preserve the mathematical value; structural (hi, lo) identity is guaranteed
 * only when `|lo| < ulp(hi) / 2` strictly.
 */
val DoubleDouble.Companion.ddConverter: Converter<DD, DoubleDouble>
    get() = DdDoubleDoubleConverter
