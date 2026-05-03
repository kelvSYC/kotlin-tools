package com.kelvsyc.kotlin.commons.numbers.complex

import com.kelvsyc.kotlin.core.Complex
import com.kelvsyc.kotlin.core.Converter
import org.apache.commons.numbers.complex.Complex as CommonsComplex

/**
 * Converts this Commons Numbers [CommonsComplex] to a kotlin-core [Complex]<[Double]>.
 */
fun CommonsComplex.toKotlinComplex(): Complex<Double> = Complex(real, imaginary)

/**
 * Converts this kotlin-core [Complex]<[Double]> to a Commons Numbers [CommonsComplex].
 */
fun Complex<Double>.toCommonsComplex(): CommonsComplex = CommonsComplex.ofCartesian(real, imaginary)

private object CommonsComplexConverter : Converter<CommonsComplex, Complex<Double>>() {
    override fun doForward(a: CommonsComplex): Complex<Double> = a.toKotlinComplex()
    override fun doBackward(b: Complex<Double>): CommonsComplex = b.toCommonsComplex()
}

/**
 * [Converter] between Commons Numbers [CommonsComplex] and kotlin-core [Complex]<[Double]>.
 *
 * Forward: [CommonsComplex.toKotlinComplex]. Backward: [Complex.toCommonsComplex].
 */
val commonsComplexConverter: Converter<CommonsComplex, Complex<Double>>
    get() = CommonsComplexConverter
