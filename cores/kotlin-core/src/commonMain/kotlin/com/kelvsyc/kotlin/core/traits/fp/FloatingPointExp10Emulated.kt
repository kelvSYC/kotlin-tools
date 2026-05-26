package com.kelvsyc.kotlin.core.traits.fp

import kotlin.math.roundToInt

// Cody-Waite range reduction for exp10: split x = n*log10(2) + r where n = round(x/log10(2)),
// |r| ≤ 0.5*log10(2) ≈ 0.15, then 10^x = scalbn(exp(r*ln10), n).
//
// The Dekker-split of LOG10_2 into a 26-bit high part and a low correction eliminates
// catastrophic cancellation in the subtraction (x - n*LOG10_2), bounding total error to
// ≤ 2 ULP for Double. For Float, widening to Double before the Cody-Waite step is
// sufficient — the 24-bit Float mantissa is absorbed by the 53-bit Double precision.
private val LOG10_2 = kotlin.math.log10(2.0)
private val LOG10_2_HI = run {
    // Veltkamp split at bit 26: splitter = 2^27 + 1. Product t = splitter * LOG10_2 is rounded
    // to double, forcing the top 26 significant bits of LOG10_2 into LOG10_2_HI.
    val c = (1L shl 27).toDouble() + 1.0
    val t = c * LOG10_2; t - (t - LOG10_2)
}
private val LOG10_2_LO = LOG10_2 - LOG10_2_HI
private val LOG2_10 = 1.0 / LOG10_2
private val LN10 = kotlin.math.ln(10.0)
private val LN10_FLOAT: Float = kotlin.math.ln(10.0f)

internal fun exp10FloatEmulated(x: Float): Float {
    if (!x.isFinite()) return kotlin.math.exp(x * LN10_FLOAT)
    val xd = x.toDouble()
    val n = (xd * LOG2_10).roundToInt()
    // Full-precision LOG10_2 is sufficient for Float: the 24-bit mantissa leaves no room
    // for the Dekker correction to matter after narrowing back to Float.
    val r = xd - n * LOG10_2
    return scalbFloat(kotlin.math.exp(r * LN10).toFloat(), n)
}

internal fun exp10DoubleEmulated(x: Double): Double {
    if (!x.isFinite()) return kotlin.math.exp(x * LN10)
    val n = (x * LOG2_10).roundToInt()
    // Two-step subtraction: n*LOG10_2_HI is exact because n has ≤ 11 bits and LOG10_2_HI
    // has ≤ 26 bits, so their product fits in 37 bits — within the 53-bit double mantissa.
    val r = (x - n * LOG10_2_HI) - n * LOG10_2_LO
    return scalbDouble(kotlin.math.exp(r * LN10), n)
}
