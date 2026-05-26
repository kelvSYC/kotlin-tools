package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * `FloatingPointHypot` is a trait providing an implementation of the hypotenuse operation for a
 * floating-point type [T].
 *
 * Computes `sqrt(x² + y²)` without intermediate overflow or underflow, where the receiver is `x`
 * and the argument is `y`. The result is within 1 ULP of the true mathematical value.
 *
 * Special values follow IEEE 754: `hypot(±∞, NaN) = +∞` (infinity dominates NaN), and
 * `hypot(x, 0) = |x|`. The operation is symmetric: `hypot(x, y) == hypot(y, x)`.
 *
 * [Float] and [Float16]/[BFloat16] instances are computed via [Double] widening to avoid
 * additional overflow/underflow in intermediate squaring; the precision gap ensures the result
 * is within 1 ULP of the true [Float] or [Float16] hypotenuse.
 *
 * Standard implementations for [Float16], [BFloat16], [Float], and [Double] are available as
 * [Companion.float16], [Companion.bfloat16], [Companion.float], and [Companion.double] respectively.
 */
interface FloatingPointHypot<T> {
    companion object

    /**
     * Returns `sqrt(this² + y²)`, computed without intermediate overflow or underflow.
     * The receiver is the `x` component and [y] is the `y` component.
     */
    fun T.hypot(y: T): T
}

private val bfloat16Instance: FloatingPointHypot<BFloat16> = object : FloatingPointHypot<BFloat16> {
    override fun BFloat16.hypot(y: BFloat16): BFloat16 =
        calculate { kotlin.math.hypot(it.toDouble(), y.toFloat().toDouble()).toFloat() }
}

private val float16Instance: FloatingPointHypot<Float16> = object : FloatingPointHypot<Float16> {
    override fun Float16.hypot(y: Float16): Float16 =
        calculate { kotlin.math.hypot(it.toDouble(), y.toFloat().toDouble()).toFloat() }
}

private val floatInstance: FloatingPointHypot<Float> = object : FloatingPointHypot<Float> {
    override fun Float.hypot(y: Float): Float =
        kotlin.math.hypot(this.toDouble(), y.toDouble()).toFloat()
}

private val doubleInstance: FloatingPointHypot<Double> = object : FloatingPointHypot<Double> {
    override fun Double.hypot(y: Double): Double = kotlin.math.hypot(this, y)
}

val FloatingPointHypot.Companion.bfloat16: FloatingPointHypot<BFloat16> get() = bfloat16Instance
val FloatingPointHypot.Companion.float16: FloatingPointHypot<Float16> get() = float16Instance
val FloatingPointHypot.Companion.float: FloatingPointHypot<Float> get() = floatInstance
val FloatingPointHypot.Companion.double: FloatingPointHypot<Double> get() = doubleInstance
