package com.kelvsyc.kotlin.commons.numbers.complex

import com.kelvsyc.kotlin.core.traits.complex.ComplexExpLog
import org.apache.commons.numbers.complex.Complex as CommonsComplex

// ── ComplexExpLog<CommonsComplex, Double> ─────────────────────────────────────
//
// All operations delegate to CommonsComplex's own implementations, which handle special
// values (NaN, infinity, zero) according to C99 Annex G and ISO standard semantics.
// ln() maps to Complex.log() (Commons naming); powComplex() maps to Complex.pow(Complex).

private val commonsComplexExpLogInstance: ComplexExpLog<CommonsComplex, Double> =
    object : ComplexExpLog<CommonsComplex, Double> {
        override fun CommonsComplex.real(): Double = real
        override fun CommonsComplex.imaginary(): Double = imaginary
        override fun of(real: Double, imaginary: Double): CommonsComplex = CommonsComplex.ofCartesian(real, imaginary)

        override fun CommonsComplex.exp(): CommonsComplex = exp()
        override fun CommonsComplex.ln(): CommonsComplex = log()
        override fun CommonsComplex.pow(y: Double): CommonsComplex = pow(y)
        override fun CommonsComplex.powComplex(w: CommonsComplex): CommonsComplex = pow(w)
    }

/**
 * A [ComplexExpLog] instance for Commons Numbers [CommonsComplex] with component type [Double].
 *
 * All operations delegate to [CommonsComplex]'s own implementations, which handle special values
 * according to C99 Annex G semantics. [ComplexExpLog.ln] maps to [CommonsComplex.log];
 * [ComplexExpLog.powComplex] maps to [CommonsComplex.pow].
 */
val ComplexExpLog.Companion.commonsComplex: ComplexExpLog<CommonsComplex, Double>
    get() = commonsComplexExpLogInstance
