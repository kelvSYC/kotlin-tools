package com.kelvsyc.kotlin.commons.numbers

import org.apache.commons.numbers.core.Multiplication

operator fun <T : Multiplication<T>> T.times(rhs: T): T = multiply(rhs)
operator fun <T : Multiplication<T>> T.div(rhs: T): T = multiply(rhs.reciprocal())
