package com.kelvsyc.kotlin.commons.numbers.complex

import com.kelvsyc.kotlin.core.traits.complex.ComplexTrigonometry
import org.apache.commons.numbers.complex.Complex as CommonsComplex

// ── ComplexTrigonometry<CommonsComplex, Double> ────────────────────────────────
//
// All 12 operations delegate to CommonsComplex's own implementations, which handle special values
// according to C99 Annex G semantics. Inside each override body, bare function calls (e.g.
// sin()) resolve to the CommonsComplex Java member rather than the trait extension — member
// functions always take priority over extensions in Kotlin.

private val commonsComplexTrigonometryInstance: ComplexTrigonometry<CommonsComplex, Double> =
    object : ComplexTrigonometry<CommonsComplex, Double> {
        override fun CommonsComplex.real(): Double = real
        override fun CommonsComplex.imaginary(): Double = imaginary
        override fun of(real: Double, imaginary: Double): CommonsComplex = CommonsComplex.ofCartesian(real, imaginary)

        override fun CommonsComplex.sin(): CommonsComplex = sin()
        override fun CommonsComplex.cos(): CommonsComplex = cos()
        override fun CommonsComplex.tan(): CommonsComplex = tan()
        override fun CommonsComplex.sinh(): CommonsComplex = sinh()
        override fun CommonsComplex.cosh(): CommonsComplex = cosh()
        override fun CommonsComplex.tanh(): CommonsComplex = tanh()
        override fun CommonsComplex.asin(): CommonsComplex = asin()
        override fun CommonsComplex.acos(): CommonsComplex = acos()
        override fun CommonsComplex.atan(): CommonsComplex = atan()
        override fun CommonsComplex.asinh(): CommonsComplex = asinh()
        override fun CommonsComplex.acosh(): CommonsComplex = acosh()
        override fun CommonsComplex.atanh(): CommonsComplex = atanh()
    }

/**
 * A [ComplexTrigonometry] instance for Commons Numbers [CommonsComplex] with component type [Double].
 *
 * All 12 operations delegate to [CommonsComplex]'s own implementations, which handle special
 * values according to C99 Annex G semantics.
 */
val ComplexTrigonometry.Companion.commonsComplex: ComplexTrigonometry<CommonsComplex, Double>
    get() = commonsComplexTrigonometryInstance
