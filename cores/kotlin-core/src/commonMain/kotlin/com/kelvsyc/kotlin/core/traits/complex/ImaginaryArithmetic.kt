package com.kelvsyc.kotlin.core.traits.complex

import com.kelvsyc.kotlin.core.Imaginary
import com.kelvsyc.kotlin.core.traits.fp.Binary32
import com.kelvsyc.kotlin.core.traits.fp.Binary64
import com.kelvsyc.kotlin.core.traits.fp.IeeeBinaryFloatingPoint

/**
 * `ImaginaryArithmetic` is a trait providing arithmetic operations over [Imaginary] values of
 * component type [T].
 *
 * Covers negation, addition, subtraction, and the magnitude of a purely imaginary value.
 * Cross-type operations (e.g. `Imaginary × Imaginary → -T`, `Imaginary × T → Imaginary`) are
 * defined as extension functions that consume this trait.
 *
 * Because addition and subtraction of two purely imaginary numbers have no special-value edge
 * cases beyond what [componentTraits] already models, a single implementation per component type
 * is sufficient. Standard instances for [Float] and [Double] are available as [Companion.float]
 * and [Companion.double].
 */
interface ImaginaryArithmetic<T> {
    /**
     * Structural metadata and sign operations for the component type [T].
     */
    val componentTraits: IeeeBinaryFloatingPoint<T>

    /**
     * The additive identity `i·0`: a purely imaginary zero with a positive-zero coefficient.
     */
    val zero: Imaginary<T> get() = Imaginary(componentTraits.positiveZero)

    /**
     * The imaginary unit `i·1`.
     */
    val imaginaryUnit: Imaginary<T>

    /**
     * Returns this value with its sign bit flipped: `-(i·a) = i·(-a)`.
     */
    fun Imaginary<T>.negate(): Imaginary<T>

    /**
     * Returns the sum `(i·a) + (i·b) = i·(a + b)`.
     */
    fun Imaginary<T>.add(other: Imaginary<T>): Imaginary<T>

    /**
     * Returns the difference `(i·a) - (i·b) = i·(a - b)`.
     */
    fun Imaginary<T>.subtract(other: Imaginary<T>): Imaginary<T>

    /**
     * Returns the magnitude of this purely imaginary value: `|i·a| = |a|`.
     *
     * The result is a real [T], not an [Imaginary].
     */
    fun Imaginary<T>.magnitude(): T

    companion object
}

// ── Float ─────────────────────────────────────────────────────────────────────

private val floatInstance: ImaginaryArithmetic<Float> = object : ImaginaryArithmetic<Float> {
    override val componentTraits: IeeeBinaryFloatingPoint<Float> get() = Binary32
    override val imaginaryUnit: Imaginary<Float> get() = Imaginary(1.0f)

    override fun Imaginary<Float>.negate() = Imaginary(-value)
    override fun Imaginary<Float>.add(other: Imaginary<Float>) = Imaginary(value + other.value)
    override fun Imaginary<Float>.subtract(other: Imaginary<Float>) = Imaginary(value - other.value)
    override fun Imaginary<Float>.magnitude(): Float = kotlin.math.abs(value)
}

// ── Double ────────────────────────────────────────────────────────────────────

private val doubleInstance: ImaginaryArithmetic<Double> = object : ImaginaryArithmetic<Double> {
    override val componentTraits: IeeeBinaryFloatingPoint<Double> get() = Binary64
    override val imaginaryUnit: Imaginary<Double> get() = Imaginary(1.0)

    override fun Imaginary<Double>.negate() = Imaginary(-value)
    override fun Imaginary<Double>.add(other: Imaginary<Double>) = Imaginary(value + other.value)
    override fun Imaginary<Double>.subtract(other: Imaginary<Double>) = Imaginary(value - other.value)
    override fun Imaginary<Double>.magnitude(): Double = kotlin.math.abs(value)
}

val ImaginaryArithmetic.Companion.float: ImaginaryArithmetic<Float> get() = floatInstance
val ImaginaryArithmetic.Companion.double: ImaginaryArithmetic<Double> get() = doubleInstance
