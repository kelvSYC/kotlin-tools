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

// ── Generic helpers ───────────────────────────────────────────────────────────

/**
 * Computes `ilogb` for any IEEE interchange format using the descriptor's metadata.
 *
 * [getBiasedExp] and [getMantissa] extract the raw exponent and mantissa fields from a
 * value of type [T]. The mantissa must be right-aligned in a 64-bit [Long] so that
 * [Long.countLeadingZeroBits] gives the correct leading-zero count for subnormals.
 */
private fun <T> ilogbGeneric(
    x: T,
    descriptor: IeeeBinaryFloatingPoint<T>,
    getBiasedExp: (T) -> Int,
    getMantissa: (T) -> Long,
): Int {
    with(descriptor.classification) {
        if (x.isNaN() || x.isInfinite()) return Int.MAX_VALUE
        if (x.isZero()) return Int.MIN_VALUE
    }
    val biasedExp = getBiasedExp(x)
    return if (biasedExp != 0) {
        biasedExp - descriptor.exponentBias
    } else {
        // subnormal: logb = −bias − (leading zeros in the mantissa field)
        // The mantissa is right-aligned in a Long, so the leading-zero count includes
        // (Long.SIZE_BITS − mantissaBits) non-field prefix bits to subtract.
        -descriptor.exponentBias - (getMantissa(x).countLeadingZeroBits() - (Long.SIZE_BITS - descriptor.mantissaBits))
    }
}

private fun <T> logbGeneric(
    x: T,
    descriptor: IeeeBinaryFloatingPoint<T>,
    ilogb: (T) -> Int,
    fromInt: (Int) -> T,
): T {
    with(descriptor.classification) {
        if (x.isNaN()) return descriptor.NaN
        if (x.isInfinite()) return descriptor.positiveInfinity
        if (x.isZero()) return descriptor.negativeInfinity
    }
    return fromInt(ilogb(x))
}

// ── Instances ─────────────────────────────────────────────────────────────────

private val doubleInstance: FloatingPointLogb<Double> = object : FloatingPointLogb<Double> {
    private fun compute(x: Double) = ilogbGeneric(
        x, Binary64,
        getBiasedExp = { ((it.toRawBits() ushr Binary64.mantissaBits) and ((1L shl Binary64.exponentBits) - 1L)).toInt() },
        getMantissa  = { it.toRawBits() and ((1L shl Binary64.mantissaBits) - 1L) },
    )

    override fun Double.ilogb(): Int = compute(this)
    override fun Double.logb(): Double = logbGeneric(this, Binary64, ::compute) { it.toDouble() }
}

private val floatInstance: FloatingPointLogb<Float> = object : FloatingPointLogb<Float> {
    private fun compute(x: Float) = ilogbGeneric(
        x, Binary32,
        getBiasedExp = { (it.toRawBits() ushr Binary32.mantissaBits) and ((1 shl Binary32.exponentBits) - 1) },
        getMantissa  = { (it.toRawBits() and ((1 shl Binary32.mantissaBits) - 1)).toLong() },
    )

    override fun Float.ilogb(): Int = compute(this)
    override fun Float.logb(): Float = logbGeneric(this, Binary32, ::compute) { it.toFloat() }
}

private val bfloat16Instance: FloatingPointLogb<BFloat16> = object : FloatingPointLogb<BFloat16> {
    private fun compute(x: BFloat16) = ilogbGeneric(
        x, BFloat16,
        getBiasedExp = { it.biasedExponent },
        getMantissa  = { it.mantissa.toLong() },
    )

    override fun BFloat16.ilogb(): Int = compute(this)
    override fun BFloat16.logb(): BFloat16 = logbGeneric(this, BFloat16, ::compute) { BFloat16(it.toFloat()) }
}

private val float16Instance: FloatingPointLogb<Float16> = object : FloatingPointLogb<Float16> {
    private fun compute(x: Float16) = ilogbGeneric(
        x, Float16,
        getBiasedExp = { it.biasedExponent },
        getMantissa  = { it.mantissa.toLong() },
    )

    override fun Float16.ilogb(): Int = compute(this)
    override fun Float16.logb(): Float16 = logbGeneric(this, Float16, ::compute) { Float16(it.toFloat()) }
}

val FloatingPointLogb.Companion.double: FloatingPointLogb<Double> get() = doubleInstance
val FloatingPointLogb.Companion.float: FloatingPointLogb<Float> get() = floatInstance
val FloatingPointLogb.Companion.bfloat16: FloatingPointLogb<BFloat16> get() = bfloat16Instance
val FloatingPointLogb.Companion.float16: FloatingPointLogb<Float16> get() = float16Instance
