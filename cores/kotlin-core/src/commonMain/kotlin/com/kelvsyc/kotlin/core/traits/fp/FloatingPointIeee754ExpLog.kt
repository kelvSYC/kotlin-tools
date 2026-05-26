package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * `FloatingPointIeee754ExpLog` is a trait providing the four IEEE 754-2019 / C23
 * "recommended" exponential and logarithmic operations for a floating-point type [T]:
 *
 * - [exp2m1] — 2^x − 1, accurate for all x including |x| ≪ 1 where direct subtraction loses bits
 * - [exp10m1] — 10^x − 1, accurate for all x including |x| ≪ 1
 * - [log2p1] — log₂(1 + x), accurate for all x > −1 including |x| ≪ 1
 * - [log10p1] — log₁₀(1 + x), accurate for all x > −1 including |x| ≪ 1
 *
 * All operations are pure `commonMain` with no platform split — none of these functions
 * are currently exposed by any major platform libm (glibc, macOS libm, MSVCRT).
 *
 * **IEEE 754 special values** — all four functions preserve NaN. Additional:
 * - `exp2m1(+∞) = +∞`, `exp2m1(-∞) = -1`, `exp2m1(±0) = ±0`
 * - `exp10m1(+∞) = +∞`, `exp10m1(-∞) = -1`, `exp10m1(±0) = ±0`
 * - `log2p1(0) = 0`, `log2p1(-1) = -∞`, `log2p1(+∞) = +∞`, `log2p1(x < -1) = NaN`
 * - `log10p1(0) = 0`, `log10p1(-1) = -∞`, `log10p1(+∞) = +∞`, `log10p1(x < -1) = NaN`
 *
 * **Algorithms:**
 * - `exp2m1`: for |x| < 1 uses `expm1(x × ln2)` (accurate for small arguments); for
 *   |x| ≥ 1 uses `exp2(x) − 1` (result ≥ 1, subtraction loses no significant bits).
 *   The internal exp2 is the Cody-Waite emulation from [FloatingPointExp2Emulated].
 * - `exp10m1`: same threshold strategy with |x| < 0.5 using `expm1(x × ln10)` and
 *   |x| ≥ 0.5 using the Dekker-split Cody-Waite exp10 helper from [FloatingPointExp10Emulated].
 * - `log2p1`: `ln1p(x) × log₂(e)`. `ln1p` is accurate near zero; multiplication by the
 *   constant adds ≤ 1 ULP, giving overall ≤ 2 ULP.
 * - `log10p1`: `ln1p(x) × log₁₀(e)`. Same accuracy argument.
 *
 * Standard implementations for [Float16], [BFloat16], [Float], and [Double] are available
 * as [Companion.float16], [Companion.bfloat16], [Companion.float], and [Companion.double].
 * No `DoubleDouble` instance is provided.
 */
interface FloatingPointIeee754ExpLog<T> {
    companion object

    fun T.exp2m1(): T
    fun T.exp10m1(): T
    fun T.log2p1(): T
    fun T.log10p1(): T
}

// ── Internal constants ────────────────────────────────────────────────────────

private val LN2 = kotlin.math.ln(2.0)
private val LN10 = kotlin.math.ln(10.0)
private val LOG2E = 1.0 / LN2    // log₂(e) = 1/ln(2)
private val LOG10E = 1.0 / LN10  // log₁₀(e) = 1/ln(10)

// ── Double helpers ─────────────────────────────────────────────────────────────

private fun doubleExp2m1(x: Double): Double {
    if (x.isNaN()) return Double.NaN
    if (x == Double.POSITIVE_INFINITY) return Double.POSITIVE_INFINITY
    if (x == Double.NEGATIVE_INFINITY) return -1.0
    if (x == 0.0) return x  // preserves -0.0
    return if (kotlin.math.abs(x) < 1.0)
        kotlin.math.expm1(x * LN2)
    else
        exp2DoubleEmulated(x) - 1.0
}

private fun doubleExp10m1(x: Double): Double {
    if (x.isNaN()) return Double.NaN
    if (x == Double.POSITIVE_INFINITY) return Double.POSITIVE_INFINITY
    if (x == Double.NEGATIVE_INFINITY) return -1.0
    if (x == 0.0) return x  // preserves -0.0
    return if (kotlin.math.abs(x) < 0.5)
        kotlin.math.expm1(x * LN10)
    else
        exp10DoubleEmulated(x) - 1.0
}

private fun doubleLog2p1(x: Double): Double = kotlin.math.ln1p(x) * LOG2E

private fun doubleLog10p1(x: Double): Double = kotlin.math.ln1p(x) * LOG10E

// ── Float helpers ──────────────────────────────────────────────────────────────

private fun floatExp2m1(x: Float): Float {
    if (x.isNaN()) return Float.NaN
    if (x == Float.POSITIVE_INFINITY) return Float.POSITIVE_INFINITY
    if (x == Float.NEGATIVE_INFINITY) return -1.0f
    if (x == 0.0f) return x
    return doubleExp2m1(x.toDouble()).toFloat()
}

private fun floatExp10m1(x: Float): Float {
    if (x.isNaN()) return Float.NaN
    if (x == Float.POSITIVE_INFINITY) return Float.POSITIVE_INFINITY
    if (x == Float.NEGATIVE_INFINITY) return -1.0f
    if (x == 0.0f) return x
    return doubleExp10m1(x.toDouble()).toFloat()
}

private fun floatLog2p1(x: Float): Float = doubleLog2p1(x.toDouble()).toFloat()

private fun floatLog10p1(x: Float): Float = doubleLog10p1(x.toDouble()).toFloat()

// ── Instances ──────────────────────────────────────────────────────────────────

private val doubleInstance: FloatingPointIeee754ExpLog<Double> = object : FloatingPointIeee754ExpLog<Double> {
    override fun Double.exp2m1(): Double = doubleExp2m1(this)
    override fun Double.exp10m1(): Double = doubleExp10m1(this)
    override fun Double.log2p1(): Double = doubleLog2p1(this)
    override fun Double.log10p1(): Double = doubleLog10p1(this)
}

private val floatInstance: FloatingPointIeee754ExpLog<Float> = object : FloatingPointIeee754ExpLog<Float> {
    override fun Float.exp2m1(): Float = floatExp2m1(this)
    override fun Float.exp10m1(): Float = floatExp10m1(this)
    override fun Float.log2p1(): Float = floatLog2p1(this)
    override fun Float.log10p1(): Float = floatLog10p1(this)
}

private val bfloat16Instance: FloatingPointIeee754ExpLog<BFloat16> = object : FloatingPointIeee754ExpLog<BFloat16> {
    override fun BFloat16.exp2m1(): BFloat16 = BFloat16(floatExp2m1(toFloat()))
    override fun BFloat16.exp10m1(): BFloat16 = BFloat16(floatExp10m1(toFloat()))
    override fun BFloat16.log2p1(): BFloat16 = BFloat16(floatLog2p1(toFloat()))
    override fun BFloat16.log10p1(): BFloat16 = BFloat16(floatLog10p1(toFloat()))
}

private val float16Instance: FloatingPointIeee754ExpLog<Float16> = object : FloatingPointIeee754ExpLog<Float16> {
    override fun Float16.exp2m1(): Float16 = Float16(floatExp2m1(toFloat()))
    override fun Float16.exp10m1(): Float16 = Float16(floatExp10m1(toFloat()))
    override fun Float16.log2p1(): Float16 = Float16(floatLog2p1(toFloat()))
    override fun Float16.log10p1(): Float16 = Float16(floatLog10p1(toFloat()))
}

val FloatingPointIeee754ExpLog.Companion.double: FloatingPointIeee754ExpLog<Double> get() = doubleInstance
val FloatingPointIeee754ExpLog.Companion.float: FloatingPointIeee754ExpLog<Float> get() = floatInstance
val FloatingPointIeee754ExpLog.Companion.bfloat16: FloatingPointIeee754ExpLog<BFloat16> get() = bfloat16Instance
val FloatingPointIeee754ExpLog.Companion.float16: FloatingPointIeee754ExpLog<Float16> get() = float16Instance
