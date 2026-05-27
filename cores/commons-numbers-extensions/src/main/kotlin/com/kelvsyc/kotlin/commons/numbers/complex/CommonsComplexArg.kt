package com.kelvsyc.kotlin.commons.numbers.complex

import com.kelvsyc.kotlin.core.traits.complex.ComplexArg
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointTrigPi
import com.kelvsyc.kotlin.core.traits.fp.double
import org.apache.commons.numbers.complex.Complex as CommonsComplex

private val commonsComplexArgInstance: ComplexArg<CommonsComplex, Double> =
    object : ComplexArg<CommonsComplex, Double> {
        private val trigPi = FloatingPointTrigPi.double
        override fun CommonsComplex.real(): Double = real
        override fun CommonsComplex.imaginary(): Double = imaginary
        override fun CommonsComplex.arg(): Double = arg()
        override fun CommonsComplex.argPi(): Double = with(trigPi) { imaginary.atan2Pi(real) }
    }

/**
 * A [ComplexArg] instance for Commons Numbers [CommonsComplex] with component type [Double].
 *
 * [ComplexArg.arg] delegates to [CommonsComplex.arg], which computes `atan2(imaginary, real)`.
 * [ComplexArg.argPi] uses [FloatingPointTrigPi] for improved accuracy at rational multiples of π.
 */
val ComplexArg.Companion.commonsComplex: ComplexArg<CommonsComplex, Double>
    get() = commonsComplexArgInstance
