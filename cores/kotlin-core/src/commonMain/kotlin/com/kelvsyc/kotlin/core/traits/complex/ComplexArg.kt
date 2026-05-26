package com.kelvsyc.kotlin.core.traits.complex

import com.kelvsyc.kotlin.core.Complex
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointTrigonometry
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointTrigPi
import com.kelvsyc.kotlin.core.traits.fp.double
import com.kelvsyc.kotlin.core.traits.fp.float

/**
 * `ComplexArg` is a trait providing the argument (phase angle) of a complex value of type [C]
 * whose components are of type [T].
 *
 * Two representations of the argument are provided:
 * - [arg]: the angle in radians, in the range (−π, π].
 * - [argPi]: the angle divided by π, in the range (−1, 1], computed via [FloatingPointTrigPi]
 *   for improved accuracy at rational multiples of π.
 *
 * Standard instances for [Complex]<[Float]> and [Complex]<[Double]> are available as
 * [Companion.float] and [Companion.double].
 */
interface ComplexArg<C, T> {
    /**
     * Returns the real component of this complex value.
     */
    fun C.real(): T

    /**
     * Returns the imaginary component of this complex value.
     */
    fun C.imaginary(): T

    /**
     * Returns the argument (phase angle) of this complex value in radians: `atan2(imaginary, real)`.
     * Result is in the range (−π, π].
     */
    fun C.arg(): T

    /**
     * Returns the argument divided by π: `atan2Pi(imaginary, real)`. Result is in (−1, 1].
     * Computed via [FloatingPointTrigPi] for exact results at rational multiples of π.
     */
    fun C.argPi(): T

    companion object
}

// ── Float ─────────────────────────────────────────────────────────────────────

private val floatInstance: ComplexArg<Complex<Float>, Float> =
    object : ComplexArg<Complex<Float>, Float> {
        private val trig = FloatingPointTrigonometry.float
        private val trigPi = FloatingPointTrigPi.float
        override fun Complex<Float>.real(): Float = real
        override fun Complex<Float>.imaginary(): Float = imaginary
        override fun Complex<Float>.arg(): Float = with(trig) { imaginary.atan2(real) }
        override fun Complex<Float>.argPi(): Float = with(trigPi) { imaginary.atan2Pi(real) }
    }

// ── Double ────────────────────────────────────────────────────────────────────

private val doubleInstance: ComplexArg<Complex<Double>, Double> =
    object : ComplexArg<Complex<Double>, Double> {
        private val trig = FloatingPointTrigonometry.double
        private val trigPi = FloatingPointTrigPi.double
        override fun Complex<Double>.real(): Double = real
        override fun Complex<Double>.imaginary(): Double = imaginary
        override fun Complex<Double>.arg(): Double = with(trig) { imaginary.atan2(real) }
        override fun Complex<Double>.argPi(): Double = with(trigPi) { imaginary.atan2Pi(real) }
    }

val ComplexArg.Companion.float: ComplexArg<Complex<Float>, Float> get() = floatInstance
val ComplexArg.Companion.double: ComplexArg<Complex<Double>, Double> get() = doubleInstance
