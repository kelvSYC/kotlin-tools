package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import kotlin.math.pow

private val floatPow: Float.(Float) -> Float = Float::pow
private val doublePow: Double.(Double) -> Double = Double::pow

/**
 * `FloatingPointExpLog` is a trait providing implementations of exponential and logarithmic
 * functions for a floating-point type [T].
 *
 * All exponential functions ([exp], [expm1]) and logarithmic functions ([ln], [ln1p], [log2],
 * [log10]) and the power function ([pow]) follow IEEE 754 semantics for special values
 * (NaN, infinities, signed zeros). For example:
 * - `exp(+∞) = +∞`, `exp(-∞) = 0`
 * - `ln(0) = -∞`, `ln` of negative values = NaN
 * - `pow` follows IEEE 754 semantics for special cases
 * - NaN propagates through all operations
 *
 * Standard instances are available for [Float16], [BFloat16], [Float], and [Double] via the
 * companion. The [Float16] and [BFloat16] instances widen to [Float] for computation and narrow
 * back; precision loss beyond what those types can represent does not occur. No instance is
 * provided for `DoubleDouble`.
 */
interface FloatingPointExpLog<T> {
    companion object

    fun T.exp(): T
    fun T.expm1(): T
    fun T.ln(): T
    fun T.ln1p(): T
    fun T.log2(): T
    fun T.log10(): T
    fun T.pow(y: T): T
}

private val bfloat16Instance: FloatingPointExpLog<BFloat16> = object : FloatingPointExpLog<BFloat16> {
    override fun BFloat16.exp(): BFloat16 = calculate { kotlin.math.exp(it) }
    override fun BFloat16.expm1(): BFloat16 = calculate { kotlin.math.expm1(it) }
    override fun BFloat16.ln(): BFloat16 = calculate { kotlin.math.ln(it) }
    override fun BFloat16.ln1p(): BFloat16 = calculate { kotlin.math.ln1p(it) }
    override fun BFloat16.log2(): BFloat16 = calculate { kotlin.math.log2(it) }
    override fun BFloat16.log10(): BFloat16 = calculate { kotlin.math.log10(it) }
    override fun BFloat16.pow(y: BFloat16): BFloat16 = calculate { floatPow(it, y.toFloat()) }
}

private val float16Instance: FloatingPointExpLog<Float16> = object : FloatingPointExpLog<Float16> {
    override fun Float16.exp(): Float16 = calculate { kotlin.math.exp(it) }
    override fun Float16.expm1(): Float16 = calculate { kotlin.math.expm1(it) }
    override fun Float16.ln(): Float16 = calculate { kotlin.math.ln(it) }
    override fun Float16.ln1p(): Float16 = calculate { kotlin.math.ln1p(it) }
    override fun Float16.log2(): Float16 = calculate { kotlin.math.log2(it) }
    override fun Float16.log10(): Float16 = calculate { kotlin.math.log10(it) }
    override fun Float16.pow(y: Float16): Float16 = calculate { floatPow(it, y.toFloat()) }
}

private val floatInstance: FloatingPointExpLog<Float> = object : FloatingPointExpLog<Float> {
    override fun Float.exp(): Float = kotlin.math.exp(this)
    override fun Float.expm1(): Float = kotlin.math.expm1(this)
    override fun Float.ln(): Float = kotlin.math.ln(this)
    override fun Float.ln1p(): Float = kotlin.math.ln1p(this)
    override fun Float.log2(): Float = kotlin.math.log2(this)
    override fun Float.log10(): Float = kotlin.math.log10(this)
    override fun Float.pow(y: Float): Float = floatPow(this, y)
}

private val doubleInstance: FloatingPointExpLog<Double> = object : FloatingPointExpLog<Double> {
    override fun Double.exp(): Double = kotlin.math.exp(this)
    override fun Double.expm1(): Double = kotlin.math.expm1(this)
    override fun Double.ln(): Double = kotlin.math.ln(this)
    override fun Double.ln1p(): Double = kotlin.math.ln1p(this)
    override fun Double.log2(): Double = kotlin.math.log2(this)
    override fun Double.log10(): Double = kotlin.math.log10(this)
    override fun Double.pow(y: Double): Double = doublePow(this, y)
}

val FloatingPointExpLog.Companion.bfloat16: FloatingPointExpLog<BFloat16> get() = bfloat16Instance
val FloatingPointExpLog.Companion.float16: FloatingPointExpLog<Float16> get() = float16Instance
val FloatingPointExpLog.Companion.float: FloatingPointExpLog<Float> get() = floatInstance
val FloatingPointExpLog.Companion.double: FloatingPointExpLog<Double> get() = doubleInstance
