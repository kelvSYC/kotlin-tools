package com.kelvsyc.kotlin.core.traits.fp

import kotlin.math.roundToInt

// Cody-Waite range reduction: split x = n + r (|r| ≤ 0.5), then 2^x = scalbn(exp(r * LN2), n).
// The non-finite fast path preserves IEEE 754 semantics without introducing the O(|x|) ULP
// error that would result from using exp(x * LN2) for large finite x.
private val LN2_FLOAT: Float = kotlin.math.ln(2.0f)
private val LN2_DOUBLE: Double = kotlin.math.ln(2.0)

internal fun exp2FloatEmulated(x: Float): Float {
    if (!x.isFinite()) return kotlin.math.exp(x * LN2_FLOAT)
    val n = x.roundToInt()
    return scalbFloat(kotlin.math.exp((x - n) * LN2_FLOAT), n)
}

internal fun exp2DoubleEmulated(x: Double): Double {
    if (!x.isFinite()) return kotlin.math.exp(x * LN2_DOUBLE)
    val n = x.roundToInt()
    return scalbDouble(kotlin.math.exp((x - n) * LN2_DOUBLE), n)
}
