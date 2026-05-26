package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * `FloatingPointLogb` is a trait providing the IEEE 754 §5.3.3 `logB` and the C99 `ilogb`
 * operations for a floating-point type [T].
 *
 * - [logb] returns the unbiased exponent of `x` in the same floating-point format as `x`:
 *   `logb(x) = floor(log₂(|x|))` for normal values, extended to subnormals.
 *   Special values: `logb(0) = −∞`, `logb(±∞) = +∞`, `logb(NaN) = NaN`.
 *   The sign of `x` is ignored: `logb(−x) = logb(x)`.
 *
 * - [ilogb] returns the same quantity as a plain [Int].
 *   Special values: `ilogb(0) = Int.MIN_VALUE`, `ilogb(±∞) = Int.MAX_VALUE`,
 *   `ilogb(NaN) = Int.MAX_VALUE`.
 *
 * Both operations are implemented via bit-pattern arithmetic (`toRawBits`/`fromBits`)
 * in `commonMain` with no platform split, since `Float.toRawBits()` always returns the
 * binary32 bit pattern on all platforms including Kotlin/JS.
 *
 * Standard implementations for [Float16], [BFloat16], [Float], and [Double] are available as
 * [Companion.float16], [Companion.bfloat16], [Companion.float], and [Companion.double] respectively.
 */
interface FloatingPointLogb<T> {
    companion object

    fun T.logb(): T
    fun T.ilogb(): Int
}

// ── Double ────────────────────────────────────────────────────────────────────

private fun doubleIlogb(x: Double): Int {
    if (x.isNaN() || x.isInfinite()) return Int.MAX_VALUE
    if (x == 0.0) return Int.MIN_VALUE
    val bits = x.toRawBits()
    val biasedExp = ((bits ushr 52) and 0x7FFL).toInt()
    return if (biasedExp != 0) {
        biasedExp - 1023
    } else {
        // subnormal: logb = -bias - (leading zeros in the 52-bit mantissa field)
        val mantissa = bits and 0x000FFFFFFFFFFFFFL
        -1023 - (mantissa.countLeadingZeroBits() - 12)
    }
}

private fun doubleLogb(x: Double): Double = when {
    x.isNaN() -> Double.NaN
    x.isInfinite() -> Double.POSITIVE_INFINITY
    x == 0.0 -> Double.NEGATIVE_INFINITY
    else -> doubleIlogb(x).toDouble()
}

// ── Float ─────────────────────────────────────────────────────────────────────

private fun floatIlogb(x: Float): Int {
    if (x.isNaN() || x.isInfinite()) return Int.MAX_VALUE
    if (x == 0.0f) return Int.MIN_VALUE
    val bits = x.toRawBits()
    val biasedExp = (bits ushr 23) and 0xFF
    return if (biasedExp != 0) {
        biasedExp - 127
    } else {
        // subnormal: logb = -bias - (leading zeros in the 23-bit mantissa field)
        val mantissa = bits and 0x007FFFFF
        -127 - (mantissa.countLeadingZeroBits() - 9)
    }
}

private fun floatLogb(x: Float): Float = when {
    x.isNaN() -> Float.NaN
    x.isInfinite() -> Float.POSITIVE_INFINITY
    x == 0.0f -> Float.NEGATIVE_INFINITY
    else -> floatIlogb(x).toFloat()
}

// ── BFloat16 ──────────────────────────────────────────────────────────────────

private fun bfloat16Ilogb(x: BFloat16): Int {
    if (x.isNaN() || x.isInfinite()) return Int.MAX_VALUE
    if (x.toFloat() == 0.0f) return Int.MIN_VALUE
    val bits = x.bits.toInt() and 0xFFFF
    val biasedExp = (bits ushr 7) and 0xFF
    return if (biasedExp != 0) {
        biasedExp - 127
    } else {
        // subnormal: logb = -bias - (leading zeros in the 7-bit mantissa field)
        val mantissa = bits and 0x7F
        -127 - (mantissa.countLeadingZeroBits() - 25)
    }
}

private fun bfloat16Logb(x: BFloat16): BFloat16 = when {
    x.isNaN() -> BFloat16.NaN
    x.isInfinite() -> BFloat16.POSITIVE_INFINITY
    x.toFloat() == 0.0f -> BFloat16.NEGATIVE_INFINITY
    else -> BFloat16(bfloat16Ilogb(x).toFloat())
}

// ── Float16 ───────────────────────────────────────────────────────────────────

private fun float16Ilogb(x: Float16): Int {
    if (x.isNaN() || x.isInfinite()) return Int.MAX_VALUE
    if (x.toFloat() == 0.0f) return Int.MIN_VALUE
    val bits = x.bits.toInt() and 0xFFFF
    val biasedExp = (bits ushr 10) and 0x1F
    return if (biasedExp != 0) {
        biasedExp - 15
    } else {
        // subnormal: logb = -bias - (leading zeros in the 10-bit mantissa field)
        val mantissa = bits and 0x3FF
        -15 - (mantissa.countLeadingZeroBits() - 22)
    }
}

private fun float16Logb(x: Float16): Float16 = when {
    x.isNaN() -> Float16.NaN
    x.isInfinite() -> Float16.POSITIVE_INFINITY
    x.toFloat() == 0.0f -> Float16.NEGATIVE_INFINITY
    else -> Float16(float16Ilogb(x).toFloat())
}

// ── Instances ─────────────────────────────────────────────────────────────────

private val doubleInstance: FloatingPointLogb<Double> = object : FloatingPointLogb<Double> {
    override fun Double.logb(): Double = doubleLogb(this)
    override fun Double.ilogb(): Int = doubleIlogb(this)
}

private val floatInstance: FloatingPointLogb<Float> = object : FloatingPointLogb<Float> {
    override fun Float.logb(): Float = floatLogb(this)
    override fun Float.ilogb(): Int = floatIlogb(this)
}

private val bfloat16Instance: FloatingPointLogb<BFloat16> = object : FloatingPointLogb<BFloat16> {
    override fun BFloat16.logb(): BFloat16 = bfloat16Logb(this)
    override fun BFloat16.ilogb(): Int = bfloat16Ilogb(this)
}

private val float16Instance: FloatingPointLogb<Float16> = object : FloatingPointLogb<Float16> {
    override fun Float16.logb(): Float16 = float16Logb(this)
    override fun Float16.ilogb(): Int = float16Ilogb(this)
}

val FloatingPointLogb.Companion.double: FloatingPointLogb<Double> get() = doubleInstance
val FloatingPointLogb.Companion.float: FloatingPointLogb<Float> get() = floatInstance
val FloatingPointLogb.Companion.bfloat16: FloatingPointLogb<BFloat16> get() = bfloat16Instance
val FloatingPointLogb.Companion.float16: FloatingPointLogb<Float16> get() = float16Instance
