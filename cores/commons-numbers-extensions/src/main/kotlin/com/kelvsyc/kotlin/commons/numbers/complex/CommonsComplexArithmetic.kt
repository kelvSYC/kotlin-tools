package com.kelvsyc.kotlin.commons.numbers.complex

import com.kelvsyc.kotlin.core.traits.complex.ComplexArithmetic
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.double
import org.apache.commons.numbers.complex.Complex as CommonsComplex

// ── ComplexArithmetic<CommonsComplex, Double> ─────────────────────────────────
//
// Multiply and divide delegate to CommonsComplex's own implementations, which use Smith's method
// for division and handle special values (NaN, infinity) according to C99 Annex G semantics.
// Add, subtract, negate, and conjugate are inherited from the default implementations in
// ComplexArithmetic<C, T>.
//
// At concrete call sites inside a with(ops) block, CommonsComplex's Java member functions
// (add, subtract, multiply, divide, negate) shadow the trait extensions — both produce the same
// results.

private val commonsComplexInstance: ComplexArithmetic<CommonsComplex, Double> =
    object : ComplexArithmetic<CommonsComplex, Double> {
        override val componentArithmetic: FloatingPointArithmetic<Double> get() = FloatingPointArithmetic.double
        override fun CommonsComplex.real(): Double = real
        override fun CommonsComplex.imaginary(): Double = imaginary
        override fun of(real: Double, imaginary: Double): CommonsComplex = CommonsComplex.ofCartesian(real, imaginary)

        override fun CommonsComplex.multiply(other: CommonsComplex): CommonsComplex = multiply(other)
        override fun CommonsComplex.divide(other: CommonsComplex): CommonsComplex = divide(other)
    }

/**
 * A [ComplexArithmetic] instance for Commons Numbers [CommonsComplex] with component type [Double].
 *
 * [ComplexArithmetic.multiply] and [ComplexArithmetic.divide] delegate to
 * [CommonsComplex.multiply] and [CommonsComplex.divide], which use Smith's method for division
 * and handle special values according to C99 Annex G semantics. All other operations use the
 * default component-wise implementations.
 */
val ComplexArithmetic.Companion.commonsComplex: ComplexArithmetic<CommonsComplex, Double>
    get() = commonsComplexInstance
