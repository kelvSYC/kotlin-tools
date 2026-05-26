package com.kelvsyc.kotlin.core.traits.fp

// Dekker split of π into a 26-bit high part and a low correction.
// PI_HI * reducedFrac + PI_LO * reducedFrac gives the argument to sin/cos/tan with
// error ≤ O(ε²) relative to π * reducedFrac, keeping total function error ≤ 2 ULP.
private val PI_HI: Double = run {
    val c = (1L shl 27).toDouble() + 1.0
    val t = c * kotlin.math.PI; t - (t - kotlin.math.PI)
}
private val PI_LO: Double = kotlin.math.PI - PI_HI
private val INV_PI: Double = 1.0 / kotlin.math.PI

// ── Argument reduction helpers ─────────────────────────────────────────────────

// Returns (frac, parity) where frac = abs(x) - floor(abs(x)) ∈ [0,1) and
// parity = floor(abs(x)) mod 2. Safe for |x| < 2^52 (checked by callers).
private fun reduceAbsX(absX: Double): Pair<Double, Long> {
    val n = kotlin.math.floor(absX)
    val frac = absX - n
    // n < 2^52 guaranteed when frac ≠ 0 (see below), so toLong() is safe
    return Pair(frac, n.toLong() and 1L)
}

// ── Double helpers ─────────────────────────────────────────────────────────────

internal fun doubleSinPi(x: Double): Double {
    if (x.isNaN() || x.isInfinite()) return Double.NaN
    if (x == 0.0) return x  // preserve ±0
    val absX = kotlin.math.abs(x)
    val (frac, parity) = reduceAbsX(absX)
    // For |x| ≥ 2^52, all doubles are integers so frac = 0 always (handled below).
    // For |x| < 2^52, n = floor(absX) < 2^52, so toLong() in reduceAbsX is safe.
    if (frac == 0.0) return if (x < 0.0) -0.0 else 0.0
    val absSin = when {
        frac == 0.5 -> 1.0
        frac < 0.5  -> kotlin.math.sin(PI_HI * frac + PI_LO * frac)
        else        -> kotlin.math.sin(PI_HI * (1.0 - frac) + PI_LO * (1.0 - frac))
    }
    val signedSin = if (parity == 0L) absSin else -absSin
    return if (x < 0.0) -signedSin else signedSin
}

internal fun doubleCosPi(x: Double): Double {
    if (x.isNaN() || x.isInfinite()) return Double.NaN
    if (x == 0.0) return 1.0
    val absX = kotlin.math.abs(x)
    val (frac, parity) = reduceAbsX(absX)
    val absCos = when {
        frac == 0.0 -> 1.0                                                    // integer → |cos| = 1
        frac == 0.5 -> 0.0
        frac < 0.5  -> kotlin.math.cos(PI_HI * frac + PI_LO * frac)
        else        -> -kotlin.math.cos(PI_HI * (1.0 - frac) + PI_LO * (1.0 - frac))
    }
    return if (parity == 0L) absCos else -absCos
}

internal fun doubleTanPi(x: Double): Double {
    if (x.isNaN() || x.isInfinite()) return Double.NaN
    if (x == 0.0) return x  // preserve ±0
    // tan has period 1: tan(π(n+frac)) = tan(πfrac) regardless of parity.
    // Use odd symmetry: compute for abs(x) and apply x's sign.
    val absX = kotlin.math.abs(x)
    val frac = absX - kotlin.math.floor(absX)
    if (frac == 0.0) return if (x < 0.0) -0.0 else 0.0
    if (frac == 0.5) return if (x < 0.0) Double.NEGATIVE_INFINITY else Double.POSITIVE_INFINITY
    val absTan = when {
        frac < 0.5 -> kotlin.math.tan(PI_HI * frac + PI_LO * frac)
        else       -> -kotlin.math.tan(PI_HI * (1.0 - frac) + PI_LO * (1.0 - frac))
    }
    return if (x < 0.0) -absTan else absTan
}

internal fun doubleAsinPi(x: Double): Double {
    if (x.isNaN() || x < -1.0 || x > 1.0) return Double.NaN
    if (x == 0.0) return x           // preserve ±0
    if (x == 1.0) return 0.5
    if (x == -1.0) return -0.5
    return kotlin.math.asin(x) * INV_PI
}

internal fun doubleAcosPi(x: Double): Double {
    if (x.isNaN() || x < -1.0 || x > 1.0) return Double.NaN
    if (x == 1.0)  return 0.0
    if (x == -1.0) return 1.0
    if (x == 0.0)  return 0.5
    return kotlin.math.acos(x) * INV_PI
}

internal fun doubleAtanPi(x: Double): Double {
    if (x.isNaN()) return Double.NaN
    if (x == 0.0) return x           // preserve ±0
    if (x == Double.POSITIVE_INFINITY) return 0.5
    if (x == Double.NEGATIVE_INFINITY) return -0.5
    return kotlin.math.atan(x) * INV_PI
}

internal fun doubleAtan2Pi(y: Double, x: Double): Double {
    if (y.isNaN() || x.isNaN()) return Double.NaN
    // Hard-code IEEE 754-mandated exact outputs (multiples of 0.25) to avoid 1/π rounding.
    if (y == Double.POSITIVE_INFINITY && x == Double.POSITIVE_INFINITY) return 0.25
    if (y == Double.POSITIVE_INFINITY && x == Double.NEGATIVE_INFINITY) return 0.75
    if (y == Double.NEGATIVE_INFINITY && x == Double.POSITIVE_INFINITY) return -0.25
    if (y == Double.NEGATIVE_INFINITY && x == Double.NEGATIVE_INFINITY) return -0.75
    if (y == Double.POSITIVE_INFINITY) return 0.5
    if (y == Double.NEGATIVE_INFINITY) return -0.5
    // Use 1.0/y to detect sign of y==0.0 without a separate negative-zero check.
    if (x == Double.POSITIVE_INFINITY) return if (1.0 / y > 0) 0.0 else -0.0
    if (x == Double.NEGATIVE_INFINITY) return if (1.0 / y > 0) 1.0 else -1.0
    if (y == 0.0) return if (x >= 0.0) y else if (1.0 / y > 0) 1.0 else -1.0
    if (x == 0.0) return if (y > 0.0) 0.5 else -0.5
    return kotlin.math.atan2(y, x) * INV_PI
}

// ── Float helpers (widen to Double) ───────────────────────────────────────────

internal fun floatSinPi(x: Float): Float {
    if (x.isNaN() || x.isInfinite()) return Float.NaN
    if (x == 0.0f) return x
    return doubleSinPi(x.toDouble()).toFloat()
}

internal fun floatCosPi(x: Float): Float {
    if (x.isNaN() || x.isInfinite()) return Float.NaN
    return doubleCosPi(x.toDouble()).toFloat()
}

internal fun floatTanPi(x: Float): Float {
    if (x.isNaN() || x.isInfinite()) return Float.NaN
    if (x == 0.0f) return x
    return doubleTanPi(x.toDouble()).toFloat()
}

internal fun floatAsinPi(x: Float): Float {
    if (x.isNaN() || x < -1.0f || x > 1.0f) return Float.NaN
    if (x == 0.0f) return x
    if (x == 1.0f) return 0.5f
    if (x == -1.0f) return -0.5f
    return (kotlin.math.asin(x.toDouble()) * INV_PI).toFloat()
}

internal fun floatAcosPi(x: Float): Float {
    if (x.isNaN() || x < -1.0f || x > 1.0f) return Float.NaN
    if (x == 1.0f) return 0.0f
    if (x == -1.0f) return 1.0f
    if (x == 0.0f) return 0.5f
    return (kotlin.math.acos(x.toDouble()) * INV_PI).toFloat()
}

internal fun floatAtanPi(x: Float): Float {
    if (x.isNaN()) return Float.NaN
    if (x == 0.0f) return x
    if (x == Float.POSITIVE_INFINITY) return 0.5f
    if (x == Float.NEGATIVE_INFINITY) return -0.5f
    return (kotlin.math.atan(x.toDouble()) * INV_PI).toFloat()
}

internal fun floatAtan2Pi(y: Float, x: Float): Float {
    if (y.isNaN() || x.isNaN()) return Float.NaN
    if (y == Float.POSITIVE_INFINITY && x == Float.POSITIVE_INFINITY) return 0.25f
    if (y == Float.POSITIVE_INFINITY && x == Float.NEGATIVE_INFINITY) return 0.75f
    if (y == Float.NEGATIVE_INFINITY && x == Float.POSITIVE_INFINITY) return -0.25f
    if (y == Float.NEGATIVE_INFINITY && x == Float.NEGATIVE_INFINITY) return -0.75f
    if (y == Float.POSITIVE_INFINITY) return 0.5f
    if (y == Float.NEGATIVE_INFINITY) return -0.5f
    if (x == Float.POSITIVE_INFINITY) return if (1.0f / y > 0) 0.0f else -0.0f
    if (x == Float.NEGATIVE_INFINITY) return if (1.0f / y > 0) 1.0f else -1.0f
    if (y == 0.0f) return if (x >= 0.0f) y else if (1.0f / y > 0) 1.0f else -1.0f
    if (x == 0.0f) return if (y > 0.0f) 0.5f else -0.5f
    return (kotlin.math.atan2(y.toDouble(), x.toDouble()) * INV_PI).toFloat()
}
