package com.kelvsyc.kotlin.commons.numbers.fraction

import org.apache.commons.numbers.fraction.BigFraction
import java.math.BigInteger

operator fun BigFraction.component1(): BigInteger = numerator
operator fun BigFraction.component2(): BigInteger = denominator
operator fun BigFraction.unaryPlus(): BigFraction = this
operator fun BigFraction.unaryMinus(): BigFraction = negate()
operator fun BigFraction.plus(rhs: Int): BigFraction = add(rhs)
operator fun BigFraction.plus(rhs: Long): BigFraction = add(rhs)
operator fun BigFraction.plus(rhs: BigInteger): BigFraction = add(rhs)
operator fun BigFraction.plus(rhs: BigFraction): BigFraction = add(rhs)
operator fun BigFraction.minus(rhs: Int): BigFraction = subtract(rhs)
operator fun BigFraction.minus(rhs: Long): BigFraction = subtract(rhs)
operator fun BigFraction.minus(rhs: BigInteger): BigFraction = subtract(rhs)
operator fun BigFraction.minus(rhs: BigFraction): BigFraction = subtract(rhs)
operator fun BigFraction.times(rhs: Int): BigFraction = multiply(rhs)
operator fun BigFraction.times(rhs: Long): BigFraction = multiply(rhs)
operator fun BigFraction.times(rhs: BigInteger): BigFraction = multiply(rhs)
operator fun BigFraction.times(rhs: BigFraction): BigFraction = multiply(rhs)
operator fun BigFraction.div(rhs: Int): BigFraction = divide(rhs)
operator fun BigFraction.div(rhs: Long): BigFraction = divide(rhs)
operator fun BigFraction.div(rhs: BigInteger): BigFraction = divide(rhs)
operator fun BigFraction.div(rhs: BigFraction): BigFraction = divide(rhs)
