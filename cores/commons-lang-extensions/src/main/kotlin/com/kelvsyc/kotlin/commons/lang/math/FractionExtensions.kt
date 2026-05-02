package com.kelvsyc.kotlin.commons.lang.math

import org.apache.commons.lang3.math.Fraction

operator fun Fraction.unaryPlus(): Fraction = this

operator fun Fraction.unaryMinus(): Fraction = negate()

operator fun Fraction.plus(rhs: Fraction): Fraction = add(rhs)

operator fun Fraction.minus(rhs: Fraction): Fraction = subtract(rhs)

operator fun Fraction.times(rhs: Fraction): Fraction = multiplyBy(rhs)

operator fun Fraction.div(rhs: Fraction): Fraction = divideBy(rhs)
