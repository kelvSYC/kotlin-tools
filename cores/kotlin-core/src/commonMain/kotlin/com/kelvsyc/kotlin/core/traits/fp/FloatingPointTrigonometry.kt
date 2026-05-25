package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * `FloatingPointTrigonometry` is a trait providing implementations of the circular and hyperbolic
 * trigonometric functions for a floating-point type [T].
 *
 * All circular functions ([sin], [cos], [tan], [asin], [acos], [atan], [atan2]) and hyperbolic
 * functions ([sinh], [cosh], [tanh], [asinh], [acosh], [atanh]) follow IEEE 754 semantics for
 * special values (NaN, infinities, signed zeros).
 *
 * Standard instances are available for [Float16], [BFloat16], [Float], and [Double] via the
 * companion. The [Float16] and [BFloat16] instances widen to [Float] for computation and narrow
 * back; precision loss beyond what those types can represent does not occur. No instance is
 * provided for `DoubleDouble`.
 */
interface FloatingPointTrigonometry<T> {
    companion object

    fun T.sin(): T
    fun T.cos(): T
    fun T.tan(): T
    fun T.asin(): T
    fun T.acos(): T
    fun T.atan(): T

    /**
     * Returns the angle whose tangent is `this / x`, using the signs of both to determine the
     * quadrant. The receiver is the y component (numerator) and [x] is the x component
     * (denominator), matching the argument order of `kotlin.math.atan2(y, x)`.
     */
    fun T.atan2(x: T): T

    fun T.sinh(): T
    fun T.cosh(): T
    fun T.tanh(): T
    fun T.asinh(): T
    fun T.acosh(): T
    fun T.atanh(): T
}

private val bfloat16Instance: FloatingPointTrigonometry<BFloat16> = object : FloatingPointTrigonometry<BFloat16> {
    override fun BFloat16.sin(): BFloat16 = calculate { kotlin.math.sin(it) }
    override fun BFloat16.cos(): BFloat16 = calculate { kotlin.math.cos(it) }
    override fun BFloat16.tan(): BFloat16 = calculate { kotlin.math.tan(it) }
    override fun BFloat16.asin(): BFloat16 = calculate { kotlin.math.asin(it) }
    override fun BFloat16.acos(): BFloat16 = calculate { kotlin.math.acos(it) }
    override fun BFloat16.atan(): BFloat16 = calculate { kotlin.math.atan(it) }
    override fun BFloat16.atan2(x: BFloat16): BFloat16 = calculate { kotlin.math.atan2(it, x.toFloat()) }
    override fun BFloat16.sinh(): BFloat16 = calculate { kotlin.math.sinh(it) }
    override fun BFloat16.cosh(): BFloat16 = calculate { kotlin.math.cosh(it) }
    override fun BFloat16.tanh(): BFloat16 = calculate { kotlin.math.tanh(it) }
    override fun BFloat16.asinh(): BFloat16 = calculate { kotlin.math.asinh(it) }
    override fun BFloat16.acosh(): BFloat16 = calculate { kotlin.math.acosh(it) }
    override fun BFloat16.atanh(): BFloat16 = calculate { kotlin.math.atanh(it) }
}

private val float16Instance: FloatingPointTrigonometry<Float16> = object : FloatingPointTrigonometry<Float16> {
    override fun Float16.sin(): Float16 = calculate { kotlin.math.sin(it) }
    override fun Float16.cos(): Float16 = calculate { kotlin.math.cos(it) }
    override fun Float16.tan(): Float16 = calculate { kotlin.math.tan(it) }
    override fun Float16.asin(): Float16 = calculate { kotlin.math.asin(it) }
    override fun Float16.acos(): Float16 = calculate { kotlin.math.acos(it) }
    override fun Float16.atan(): Float16 = calculate { kotlin.math.atan(it) }
    override fun Float16.atan2(x: Float16): Float16 = calculate { kotlin.math.atan2(it, x.toFloat()) }
    override fun Float16.sinh(): Float16 = calculate { kotlin.math.sinh(it) }
    override fun Float16.cosh(): Float16 = calculate { kotlin.math.cosh(it) }
    override fun Float16.tanh(): Float16 = calculate { kotlin.math.tanh(it) }
    override fun Float16.asinh(): Float16 = calculate { kotlin.math.asinh(it) }
    override fun Float16.acosh(): Float16 = calculate { kotlin.math.acosh(it) }
    override fun Float16.atanh(): Float16 = calculate { kotlin.math.atanh(it) }
}

private val floatInstance: FloatingPointTrigonometry<Float> = object : FloatingPointTrigonometry<Float> {
    override fun Float.sin(): Float = kotlin.math.sin(this)
    override fun Float.cos(): Float = kotlin.math.cos(this)
    override fun Float.tan(): Float = kotlin.math.tan(this)
    override fun Float.asin(): Float = kotlin.math.asin(this)
    override fun Float.acos(): Float = kotlin.math.acos(this)
    override fun Float.atan(): Float = kotlin.math.atan(this)
    override fun Float.atan2(x: Float): Float = kotlin.math.atan2(this, x)
    override fun Float.sinh(): Float = kotlin.math.sinh(this)
    override fun Float.cosh(): Float = kotlin.math.cosh(this)
    override fun Float.tanh(): Float = kotlin.math.tanh(this)
    override fun Float.asinh(): Float = kotlin.math.asinh(this)
    override fun Float.acosh(): Float = kotlin.math.acosh(this)
    override fun Float.atanh(): Float = kotlin.math.atanh(this)
}

private val doubleInstance: FloatingPointTrigonometry<Double> = object : FloatingPointTrigonometry<Double> {
    override fun Double.sin(): Double = kotlin.math.sin(this)
    override fun Double.cos(): Double = kotlin.math.cos(this)
    override fun Double.tan(): Double = kotlin.math.tan(this)
    override fun Double.asin(): Double = kotlin.math.asin(this)
    override fun Double.acos(): Double = kotlin.math.acos(this)
    override fun Double.atan(): Double = kotlin.math.atan(this)
    override fun Double.atan2(x: Double): Double = kotlin.math.atan2(this, x)
    override fun Double.sinh(): Double = kotlin.math.sinh(this)
    override fun Double.cosh(): Double = kotlin.math.cosh(this)
    override fun Double.tanh(): Double = kotlin.math.tanh(this)
    override fun Double.asinh(): Double = kotlin.math.asinh(this)
    override fun Double.acosh(): Double = kotlin.math.acosh(this)
    override fun Double.atanh(): Double = kotlin.math.atanh(this)
}

val FloatingPointTrigonometry.Companion.bfloat16: FloatingPointTrigonometry<BFloat16> get() = bfloat16Instance
val FloatingPointTrigonometry.Companion.float16: FloatingPointTrigonometry<Float16> get() = float16Instance
val FloatingPointTrigonometry.Companion.float: FloatingPointTrigonometry<Float> get() = floatInstance
val FloatingPointTrigonometry.Companion.double: FloatingPointTrigonometry<Double> get() = doubleInstance
