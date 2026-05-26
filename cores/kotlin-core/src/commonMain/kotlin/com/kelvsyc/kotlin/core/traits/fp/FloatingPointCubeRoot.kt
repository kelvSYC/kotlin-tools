package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * `FloatingPointCubeRoot` is a trait providing an implementation of the cube root operation for a
 * floating-point type [T].
 *
 * The cube root is defined for all finite inputs, including negative values: `cbrt(-x) == -cbrt(x)`.
 * The result is within 1 ULP of the true mathematical cube root. For NaN inputs the result is NaN;
 * for ±∞ inputs the result is ±∞.
 *
 * [Float] and [Float16]/[BFloat16] instances are computed via [Double] widening since
 * `kotlin.math.cbrt` has no [Float] overload; the precision gap is large enough that the result
 * is within 1 ULP of the true [Float] or [Float16] cube root.
 *
 * Standard implementations for [Float16], [BFloat16], [Float], and [Double] are available as
 * [Companion.float16], [Companion.bfloat16], [Companion.float], and [Companion.double] respectively.
 */
interface FloatingPointCubeRoot<T> {
    companion object

    fun T.cbrt(): T
}

private val bfloat16Instance: FloatingPointCubeRoot<BFloat16> = object : FloatingPointCubeRoot<BFloat16> {
    override fun BFloat16.cbrt(): BFloat16 = calculate { kotlin.math.cbrt(it.toDouble()).toFloat() }
}

private val float16Instance: FloatingPointCubeRoot<Float16> = object : FloatingPointCubeRoot<Float16> {
    override fun Float16.cbrt(): Float16 = calculate { kotlin.math.cbrt(it.toDouble()).toFloat() }
}

private val floatInstance: FloatingPointCubeRoot<Float> = object : FloatingPointCubeRoot<Float> {
    override fun Float.cbrt(): Float = kotlin.math.cbrt(this.toDouble()).toFloat()
}

private val doubleInstance: FloatingPointCubeRoot<Double> = object : FloatingPointCubeRoot<Double> {
    override fun Double.cbrt(): Double = kotlin.math.cbrt(this)
}

val FloatingPointCubeRoot.Companion.bfloat16: FloatingPointCubeRoot<BFloat16> get() = bfloat16Instance
val FloatingPointCubeRoot.Companion.float16: FloatingPointCubeRoot<Float16> get() = float16Instance
val FloatingPointCubeRoot.Companion.float: FloatingPointCubeRoot<Float> get() = floatInstance
val FloatingPointCubeRoot.Companion.double: FloatingPointCubeRoot<Double> get() = doubleInstance
