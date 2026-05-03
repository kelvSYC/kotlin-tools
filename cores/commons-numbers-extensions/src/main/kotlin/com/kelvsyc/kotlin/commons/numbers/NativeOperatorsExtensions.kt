package com.kelvsyc.kotlin.commons.numbers

import org.apache.commons.numbers.core.NativeOperators

operator fun <T : NativeOperators<T>> T.minus(rhs: T): T = subtract(rhs)
operator fun <T : NativeOperators<T>> T.times(rhs: Int): T = multiply(rhs)
operator fun <T : NativeOperators<T>> T.div(rhs: T): T = divide(rhs)
