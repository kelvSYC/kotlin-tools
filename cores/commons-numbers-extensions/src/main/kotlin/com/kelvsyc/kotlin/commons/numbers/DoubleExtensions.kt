package com.kelvsyc.kotlin.commons.numbers

import org.apache.commons.numbers.complex.Complex
import org.apache.commons.numbers.core.DD
import org.apache.commons.numbers.fraction.BigFraction
import org.apache.commons.numbers.fraction.Fraction

val Double.i get() = Complex.ofCartesian(0.0, this)
fun Double.toDD(): DD = DD.of(this)
fun Double.toFraction(): Fraction = Fraction.from(this)
fun Double.toBigFraction(): BigFraction = BigFraction.from(this)
operator fun Double.minus(rhs: Complex): Complex = rhs.subtractFrom(this)
