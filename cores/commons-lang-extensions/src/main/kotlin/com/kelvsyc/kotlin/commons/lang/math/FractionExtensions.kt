package com.kelvsyc.kotlin.commons.lang.math

import org.apache.commons.lang3.math.Fraction

operator fun Fraction.component1(): Int = numerator
operator fun Fraction.component2(): Int = denominator
operator fun Fraction.unaryPlus(): Fraction = this
operator fun Fraction.unaryMinus(): Fraction = negate()
operator fun Fraction.plus(rhs: Int): Fraction = add(Fraction.getFraction(rhs, 1))
operator fun Fraction.plus(rhs: Fraction): Fraction = add(rhs)
operator fun Fraction.minus(rhs: Int): Fraction = subtract(Fraction.getFraction(rhs, 1))
operator fun Fraction.minus(rhs: Fraction): Fraction = subtract(rhs)
operator fun Fraction.times(rhs: Int): Fraction = multiplyBy(Fraction.getFraction(rhs, 1))
operator fun Fraction.times(rhs: Fraction): Fraction = multiplyBy(rhs)
operator fun Fraction.div(rhs: Int): Fraction = divideBy(Fraction.getFraction(rhs, 1))
operator fun Fraction.div(rhs: Fraction): Fraction = divideBy(rhs)
