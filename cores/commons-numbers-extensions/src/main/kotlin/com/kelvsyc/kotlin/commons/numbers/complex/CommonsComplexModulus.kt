package com.kelvsyc.kotlin.commons.numbers.complex

import com.kelvsyc.kotlin.core.traits.complex.ComplexModulus
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.double
import org.apache.commons.numbers.complex.Complex as CommonsComplex

// ── ComplexModulus<CommonsComplex, Double> ─────────────────────────────────────
//
// modulus() delegates to CommonsComplex.abs(), which uses Math.hypot internally (overflow-safe).
// squaredModulus() uses the naive formula since CommonsComplex does not expose a norm² method.

private val commonsComplexModulusInstance: ComplexModulus<CommonsComplex, Double> =
    object : ComplexModulus<CommonsComplex, Double> {
        override val componentArithmetic: FloatingPointArithmetic<Double> get() = FloatingPointArithmetic.double
        override fun CommonsComplex.real(): Double = real
        override fun CommonsComplex.imaginary(): Double = imaginary

        override fun CommonsComplex.squaredModulus(): Double = real * real + imaginary * imaginary
        override fun CommonsComplex.modulus(): Double = abs()
    }

/**
 * A [ComplexModulus] instance for Commons Numbers [CommonsComplex] with component type [Double].
 *
 * [ComplexModulus.modulus] delegates to [CommonsComplex.abs], which uses `Math.hypot` internally
 * for overflow safety. [ComplexModulus.squaredModulus] uses the naive formula `re² + im²`.
 */
val ComplexModulus.Companion.commonsComplex: ComplexModulus<CommonsComplex, Double>
    get() = commonsComplexModulusInstance
