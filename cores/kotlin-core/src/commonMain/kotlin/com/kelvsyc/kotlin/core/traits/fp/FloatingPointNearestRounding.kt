package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * `FloatingPointNearestRounding` is a trait providing nearest-integer rounding operations
 * for a floating-point type [T], differing only in how half-integer ties are broken.
 *
 * All three operations round to an integer-valued result in the same floating-point type,
 * preserve NaN (returning NaN), infinities, and signed zeros:
 *
 * - [roundHalfUp] — nearest, ties away from zero; equivalent to C99 `round` and Java `RoundingMode.HALF_UP`
 * - [roundHalfDown] — nearest, ties toward zero; equivalent to Java `RoundingMode.HALF_DOWN`
 * - [roundEven] — nearest, ties to even (banker's rounding); equivalent to C99 `rint` under
 *   the default IEEE 754 rounding mode and Java `RoundingMode.HALF_EVEN`
 *
 * All operations are implemented in `commonMain` using standard floating-point arithmetic
 * with no platform split.
 *
 * Standard implementations for [Float16], [BFloat16], [Float], and [Double] are available as
 * [Companion.float16], [Companion.bfloat16], [Companion.float], and [Companion.double].
 */
interface FloatingPointNearestRounding<T> {
    companion object

    fun T.roundHalfUp(): T
    fun T.roundHalfDown(): T
    fun T.roundEven(): T
}

// ── Generic helpers ───────────────────────────────────────────────────────────

// Ties away from zero: floor(|x| + 0.5) × sign(x).
// For |x| ≥ 2^mantissaBits, x is already integral; |x| + 0.5 rounds back to |x|.
private fun floatRound(x: Float): Float {
    if (x.isNaN() || x.isInfinite() || x == 0.0f) return x
    val abs = kotlin.math.abs(x)
    val rounded = kotlin.math.floor(abs + 0.5f)
    return if (x < 0.0f) -rounded else rounded
}

private fun doubleRound(x: Double): Double {
    if (x.isNaN() || x.isInfinite() || x == 0.0) return x
    val abs = kotlin.math.abs(x)
    val rounded = kotlin.math.floor(abs + 0.5)
    return if (x < 0.0) -rounded else rounded
}

// Ties toward zero: ceil(x − 0.5) for x ≥ 0, floor(x + 0.5) for x < 0.
private fun floatRoundHalfDown(x: Float): Float {
    if (x.isNaN() || x.isInfinite() || x == 0.0f) return x
    return if (x >= 0.0f) kotlin.math.ceil(x - 0.5f) else kotlin.math.floor(x + 0.5f)
}

private fun doubleRoundHalfDown(x: Double): Double {
    if (x.isNaN() || x.isInfinite() || x == 0.0) return x
    return if (x >= 0.0) kotlin.math.ceil(x - 0.5) else kotlin.math.floor(x + 0.5)
}

// Ties to even: add 2^mantissaBits to |x|, which forces the FPU to round using the
// default (ties-to-even) rounding mode, then subtract back. Only applies when
// |x| < 2^mantissaBits; larger values are already integral.
private val FLOAT_ROUND_EVEN_CONST = 8388608.0f   // 2^23
private val DOUBLE_ROUND_EVEN_CONST = 4503599627370496.0  // 2^52

private fun floatRoundEven(x: Float): Float {
    if (x.isNaN() || x.isInfinite() || x == 0.0f) return x
    val abs = kotlin.math.abs(x)
    if (abs >= FLOAT_ROUND_EVEN_CONST) return x
    val rounded = (abs + FLOAT_ROUND_EVEN_CONST) - FLOAT_ROUND_EVEN_CONST
    return if (x < 0.0f) -rounded else rounded
}

private fun doubleRoundEven(x: Double): Double {
    if (x.isNaN() || x.isInfinite() || x == 0.0) return x
    val abs = kotlin.math.abs(x)
    if (abs >= DOUBLE_ROUND_EVEN_CONST) return x
    val rounded = (abs + DOUBLE_ROUND_EVEN_CONST) - DOUBLE_ROUND_EVEN_CONST
    return if (x < 0.0) -rounded else rounded
}

// ── Instances ─────────────────────────────────────────────────────────────────

private val doubleInstance: FloatingPointNearestRounding<Double> = object : FloatingPointNearestRounding<Double> {
    override fun Double.roundHalfUp(): Double = doubleRound(this)
    override fun Double.roundHalfDown(): Double = doubleRoundHalfDown(this)
    override fun Double.roundEven(): Double = doubleRoundEven(this)
}

private val floatInstance: FloatingPointNearestRounding<Float> = object : FloatingPointNearestRounding<Float> {
    override fun Float.roundHalfUp(): Float = floatRound(this)
    override fun Float.roundHalfDown(): Float = floatRoundHalfDown(this)
    override fun Float.roundEven(): Float = floatRoundEven(this)
}

private val bfloat16Instance: FloatingPointNearestRounding<BFloat16> = object : FloatingPointNearestRounding<BFloat16> {
    override fun BFloat16.roundHalfUp(): BFloat16 = BFloat16(doubleRound(toFloat().toDouble()).toFloat())
    override fun BFloat16.roundHalfDown(): BFloat16 = BFloat16(doubleRoundHalfDown(toFloat().toDouble()).toFloat())
    override fun BFloat16.roundEven(): BFloat16 = BFloat16(doubleRoundEven(toFloat().toDouble()).toFloat())
}

private val float16Instance: FloatingPointNearestRounding<Float16> = object : FloatingPointNearestRounding<Float16> {
    override fun Float16.roundHalfUp(): Float16 = Float16(doubleRound(toFloat().toDouble()).toFloat())
    override fun Float16.roundHalfDown(): Float16 = Float16(doubleRoundHalfDown(toFloat().toDouble()).toFloat())
    override fun Float16.roundEven(): Float16 = Float16(doubleRoundEven(toFloat().toDouble()).toFloat())
}

val FloatingPointNearestRounding.Companion.double: FloatingPointNearestRounding<Double> get() = doubleInstance
val FloatingPointNearestRounding.Companion.float: FloatingPointNearestRounding<Float> get() = floatInstance
val FloatingPointNearestRounding.Companion.bfloat16: FloatingPointNearestRounding<BFloat16> get() = bfloat16Instance
val FloatingPointNearestRounding.Companion.float16: FloatingPointNearestRounding<Float16> get() = float16Instance
