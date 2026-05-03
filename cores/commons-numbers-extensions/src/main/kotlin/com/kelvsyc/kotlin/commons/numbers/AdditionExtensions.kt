package com.kelvsyc.kotlin.commons.numbers

import org.apache.commons.numbers.core.Addition

operator fun <T : Addition<T>> T.unaryPlus() = this
operator fun <T : Addition<T>> T.plus(rhs: T): T = add(rhs)
operator fun <T : Addition<T>> T.unaryMinus() = negate()
operator fun <T : Addition<T>> T.minus(rhs: T): T = add(-rhs)
