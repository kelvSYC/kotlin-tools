package com.kelvsyc.kotlin.commons.numbers

import org.apache.commons.numbers.core.DD

operator fun DD.unaryPlus(): DD = this
operator fun DD.unaryMinus(): DD = negate()
operator fun DD.plus(rhs: Double): DD = add(rhs)
operator fun DD.plus(rhs: DD): DD = add(rhs)
operator fun DD.minus(rhs: Double): DD = subtract(rhs)
operator fun DD.minus(rhs: DD): DD = subtract(rhs)
operator fun DD.times(rhs: Int): DD = multiply(rhs)
operator fun DD.times(rhs: Double): DD = multiply(rhs)
operator fun DD.times(rhs: DD): DD = multiply(rhs)
operator fun DD.div(rhs: Double): DD = divide(rhs)
operator fun DD.div(rhs: DD): DD = divide(rhs)
