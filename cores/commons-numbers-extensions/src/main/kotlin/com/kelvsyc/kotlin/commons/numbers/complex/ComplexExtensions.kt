package com.kelvsyc.kotlin.commons.numbers.complex

import org.apache.commons.numbers.complex.Complex

operator fun Complex.component1(): Double = real
operator fun Complex.component2(): Double = imaginary
operator fun Complex.unaryPlus(): Complex = this
operator fun Complex.unaryMinus(): Complex = negate()
operator fun Complex.plus(rhs: Double): Complex = add(rhs)
operator fun Complex.plus(rhs: Complex): Complex = add(rhs)
operator fun Complex.minus(rhs: Double): Complex = subtract(rhs)
operator fun Complex.minus(rhs: Complex): Complex = subtract(rhs)
operator fun Complex.times(rhs: Double): Complex = multiply(rhs)
operator fun Complex.times(rhs: Complex): Complex = multiply(rhs)
operator fun Complex.div(rhs: Double): Complex = divide(rhs)
operator fun Complex.div(rhs: Complex): Complex = divide(rhs)
