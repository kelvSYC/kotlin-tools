package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16

/**
 * `FloatingPointSquareRoot` is a trait providing an implementation of the square root operation for a
 * floating-point type [T].
 *
 * The square root is correctly rounded: the result is the nearest representable value to the true
 * mathematical square root, following IEEE 754 requirements. For negative inputs the result is NaN.
 *
 * Standard implementations for [Float16], [Float], and [Double] are available as [Companion.float16],
 * [Companion.float], and [Companion.double] respectively.
 */
interface FloatingPointSquareRoot<T> {
    companion object

    fun T.sqrt(): T
}

private val bfloat16Instance: FloatingPointSquareRoot<BFloat16> = object : FloatingPointSquareRoot<BFloat16> {
    override fun BFloat16.sqrt(): BFloat16 = calculate { kotlin.math.sqrt(it) }
}

private val float16Instance: FloatingPointSquareRoot<Float16> = object : FloatingPointSquareRoot<Float16> {
    override fun Float16.sqrt(): Float16 = calculate { kotlin.math.sqrt(it) }
}

private val floatInstance: FloatingPointSquareRoot<Float> = object : FloatingPointSquareRoot<Float> {
    override fun Float.sqrt(): Float = kotlin.math.sqrt(this)
}

private val doubleInstance: FloatingPointSquareRoot<Double> = object : FloatingPointSquareRoot<Double> {
    override fun Double.sqrt(): Double = kotlin.math.sqrt(this)
}

val FloatingPointSquareRoot.Companion.bfloat16: FloatingPointSquareRoot<BFloat16> get() = bfloat16Instance
val FloatingPointSquareRoot.Companion.float16: FloatingPointSquareRoot<Float16> get() = float16Instance
val FloatingPointSquareRoot.Companion.float: FloatingPointSquareRoot<Float> get() = floatInstance
val FloatingPointSquareRoot.Companion.double: FloatingPointSquareRoot<Double> get() = doubleInstance
