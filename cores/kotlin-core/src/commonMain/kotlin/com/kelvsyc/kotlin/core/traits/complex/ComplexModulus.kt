package com.kelvsyc.kotlin.core.traits.complex

import com.kelvsyc.kotlin.core.Complex
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.FusedMultiplyAdd
import com.kelvsyc.kotlin.core.traits.fp.double
import com.kelvsyc.kotlin.core.traits.fp.float

/**
 * `ComplexModulus` is a trait providing modulus-related operations over complex values of type [C]
 * whose components are of type [T].
 *
 * Two implementations are available per supported component type:
 * - **Naive**: computes `re² + im²` directly; fast but can overflow or underflow to infinity/zero
 *   even when the true modulus is representable.
 * - **Strict**: uses [kotlin.math.hypot] for [modulus] (overflow-safe) and FMA for
 *   [squaredModulus] (reduced rounding error).
 *
 * Standard instances are available as [Companion.naiveFloat], [Companion.strictFloat],
 * [Companion.naiveDouble], and [Companion.strictDouble].
 */
interface ComplexModulus<C, T> {
    /**
     * Floating-point arithmetic for the component type [T].
     */
    val componentArithmetic: FloatingPointArithmetic<T>

    /**
     * Returns the real component of this complex value.
     */
    fun C.real(): T

    /**
     * Returns the imaginary component of this complex value.
     */
    fun C.imaginary(): T

    /**
     * Returns `re² + im²`, the square of the modulus.
     *
     * Naive implementations compute this as `re*re + im*im`, which can overflow or underflow
     * when the components are very large or very small. Strict implementations use FMA to reduce
     * the rounding error to a single step.
     *
     * Note that even the strict implementation overflows for very large components; use [modulus]
     * squared when overflow safety is required.
     */
    fun C.squaredModulus(): T

    /**
     * Returns `sqrt(re² + im²)`, the modulus (absolute value) of the complex number.
     *
     * Naive implementations compute `sqrt(re*re + im*im)`, inheriting the overflow risk of
     * [squaredModulus]. Strict implementations use [kotlin.math.hypot], which scales internally
     * to avoid overflow and underflow even when `re² + im²` would not be representable.
     */
    fun C.modulus(): T

    companion object
}

// ── Float — naive ─────────────────────────────────────────────────────────────

private val floatNaiveInstance: ComplexModulus<Complex<Float>, Float> =
    object : ComplexModulus<Complex<Float>, Float> {
        override val componentArithmetic: FloatingPointArithmetic<Float> get() = FloatingPointArithmetic.float
        override fun Complex<Float>.real(): Float = real
        override fun Complex<Float>.imaginary(): Float = imaginary

        override fun Complex<Float>.squaredModulus(): Float = real * real + imaginary * imaginary
        override fun Complex<Float>.modulus(): Float = kotlin.math.sqrt(squaredModulus())
    }

// ── Float — strict ────────────────────────────────────────────────────────────

private val floatStrictInstance: ComplexModulus<Complex<Float>, Float> =
    object : ComplexModulus<Complex<Float>, Float> {
        override val componentArithmetic: FloatingPointArithmetic<Float> get() = FloatingPointArithmetic.float
        override fun Complex<Float>.real(): Float = real
        override fun Complex<Float>.imaginary(): Float = imaginary

        override fun Complex<Float>.squaredModulus(): Float =
            FusedMultiplyAdd.float.fma(real, real, imaginary * imaginary)

        override fun Complex<Float>.modulus(): Float = kotlin.math.hypot(real, imaginary)
    }

// ── Double — naive ────────────────────────────────────────────────────────────

private val doubleNaiveInstance: ComplexModulus<Complex<Double>, Double> =
    object : ComplexModulus<Complex<Double>, Double> {
        override val componentArithmetic: FloatingPointArithmetic<Double> get() = FloatingPointArithmetic.double
        override fun Complex<Double>.real(): Double = real
        override fun Complex<Double>.imaginary(): Double = imaginary

        override fun Complex<Double>.squaredModulus(): Double = real * real + imaginary * imaginary
        override fun Complex<Double>.modulus(): Double = kotlin.math.sqrt(squaredModulus())
    }

// ── Double — strict ───────────────────────────────────────────────────────────

private val doubleStrictInstance: ComplexModulus<Complex<Double>, Double> =
    object : ComplexModulus<Complex<Double>, Double> {
        override val componentArithmetic: FloatingPointArithmetic<Double> get() = FloatingPointArithmetic.double
        override fun Complex<Double>.real(): Double = real
        override fun Complex<Double>.imaginary(): Double = imaginary

        override fun Complex<Double>.squaredModulus(): Double =
            FusedMultiplyAdd.double.fma(real, real, imaginary * imaginary)

        override fun Complex<Double>.modulus(): Double = kotlin.math.hypot(real, imaginary)
    }

val ComplexModulus.Companion.naiveFloat: ComplexModulus<Complex<Float>, Float> get() = floatNaiveInstance
val ComplexModulus.Companion.strictFloat: ComplexModulus<Complex<Float>, Float> get() = floatStrictInstance
val ComplexModulus.Companion.naiveDouble: ComplexModulus<Complex<Double>, Double> get() = doubleNaiveInstance
val ComplexModulus.Companion.strictDouble: ComplexModulus<Complex<Double>, Double> get() = doubleStrictInstance
