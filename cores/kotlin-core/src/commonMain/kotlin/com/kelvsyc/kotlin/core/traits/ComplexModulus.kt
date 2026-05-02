package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.Complex

/**
 * `ComplexModulus` is a trait providing modulus-related operations over [Complex] values of
 * component type [T].
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
interface ComplexModulus<T> {
    /**
     * Structural metadata for the component type [T].
     */
    val componentTraits: IeeeBinaryFloatingPoint<T>

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
    fun Complex<T>.squaredModulus(): T

    /**
     * Returns `sqrt(re² + im²)`, the modulus (absolute value) of the complex number.
     *
     * Naive implementations compute `sqrt(re*re + im*im)`, inheriting the overflow risk of
     * [squaredModulus]. Strict implementations use [kotlin.math.hypot], which scales internally
     * to avoid overflow and underflow even when `re² + im²` would not be representable.
     */
    fun Complex<T>.modulus(): T

    companion object
}

// ── Float — naive ─────────────────────────────────────────────────────────────

private val floatNaiveInstance: ComplexModulus<Float> = object : ComplexModulus<Float> {
    override val componentTraits: IeeeBinaryFloatingPoint<Float> get() = Binary32

    override fun Complex<Float>.squaredModulus(): Float = real * real + imaginary * imaginary
    override fun Complex<Float>.modulus(): Float = kotlin.math.sqrt(squaredModulus())
}

// ── Float — strict ────────────────────────────────────────────────────────────

private val floatStrictInstance: ComplexModulus<Float> = object : ComplexModulus<Float> {
    override val componentTraits: IeeeBinaryFloatingPoint<Float> get() = Binary32

    // fma(re, re, im*im) reduces two multiplications + one addition to two rounding errors,
    // compared to three rounding errors in the naive formula.
    override fun Complex<Float>.squaredModulus(): Float =
        FusedMultiplyAdd.float.fma(real, real, imaginary * imaginary)

    // hypot handles scaling internally, remaining finite whenever the true modulus is finite.
    override fun Complex<Float>.modulus(): Float = kotlin.math.hypot(real, imaginary)
}

// ── Double — naive ────────────────────────────────────────────────────────────

private val doubleNaiveInstance: ComplexModulus<Double> = object : ComplexModulus<Double> {
    override val componentTraits: IeeeBinaryFloatingPoint<Double> get() = Binary64

    override fun Complex<Double>.squaredModulus(): Double = real * real + imaginary * imaginary
    override fun Complex<Double>.modulus(): Double = kotlin.math.sqrt(squaredModulus())
}

// ── Double — strict ───────────────────────────────────────────────────────────

private val doubleStrictInstance: ComplexModulus<Double> = object : ComplexModulus<Double> {
    override val componentTraits: IeeeBinaryFloatingPoint<Double> get() = Binary64

    override fun Complex<Double>.squaredModulus(): Double =
        FusedMultiplyAdd.double.fma(real, real, imaginary * imaginary)

    override fun Complex<Double>.modulus(): Double = kotlin.math.hypot(real, imaginary)
}

val ComplexModulus.Companion.naiveFloat: ComplexModulus<Float> get() = floatNaiveInstance
val ComplexModulus.Companion.strictFloat: ComplexModulus<Float> get() = floatStrictInstance
val ComplexModulus.Companion.naiveDouble: ComplexModulus<Double> get() = doubleNaiveInstance
val ComplexModulus.Companion.strictDouble: ComplexModulus<Double> get() = doubleStrictInstance
