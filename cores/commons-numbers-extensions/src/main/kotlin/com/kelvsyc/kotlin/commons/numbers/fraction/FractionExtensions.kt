package com.kelvsyc.kotlin.commons.numbers.fraction

import org.apache.commons.numbers.fraction.BigFraction
import org.apache.commons.numbers.fraction.Fraction

operator fun Fraction.component1(): Int = numerator
operator fun Fraction.component2(): Int = denominator
operator fun Fraction.unaryPlus(): Fraction = this
operator fun Fraction.unaryMinus(): Fraction = negate()
operator fun Fraction.plus(rhs: Int): Fraction = add(rhs)
operator fun Fraction.plus(rhs: Fraction): Fraction = add(rhs)
operator fun Fraction.minus(rhs: Int): Fraction = subtract(rhs)
operator fun Fraction.minus(rhs: Fraction): Fraction = subtract(rhs)
operator fun Fraction.times(rhs: Int): Fraction = multiply(rhs)
operator fun Fraction.times(rhs: Fraction): Fraction = multiply(rhs)
operator fun Fraction.div(rhs: Int): Fraction = divide(rhs)
operator fun Fraction.div(rhs: Fraction): Fraction = divide(rhs)
fun Fraction.toBigFraction(): BigFraction = BigFraction.of(numerator, denominator)
