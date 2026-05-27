package com.kelvsyc.kotlin.commons.numbers.complex

import com.kelvsyc.kotlin.core.traits.complex.ComplexSquareRoot
import org.apache.commons.numbers.complex.Complex as CommonsComplex

private val commonsComplexSquareRootInstance: ComplexSquareRoot<CommonsComplex, Double> =
    object : ComplexSquareRoot<CommonsComplex, Double> {
        override fun CommonsComplex.real(): Double = real
        override fun CommonsComplex.imaginary(): Double = imaginary
        override fun of(real: Double, imaginary: Double): CommonsComplex = CommonsComplex.ofCartesian(real, imaginary)

        override fun CommonsComplex.sqrt(): CommonsComplex = sqrt()
    }

/**
 * A [ComplexSquareRoot] instance for Commons Numbers [CommonsComplex] with component type [Double].
 *
 * [ComplexSquareRoot.sqrt] delegates to [CommonsComplex.sqrt], which returns the principal square
 * root following C99 Annex G semantics.
 */
val ComplexSquareRoot.Companion.commonsComplex: ComplexSquareRoot<CommonsComplex, Double>
    get() = commonsComplexSquareRootInstance
