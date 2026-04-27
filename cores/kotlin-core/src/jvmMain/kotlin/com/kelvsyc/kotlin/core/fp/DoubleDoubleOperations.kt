package com.kelvsyc.kotlin.core.fp

import com.kelvsyc.kotlin.core.traits.dd.DoubleBinaryFloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.doubleDouble

private val arith get() = DoubleBinaryFloatingPointArithmetic.doubleDouble

actual operator fun DoubleDouble.plus(other: DoubleDouble): DoubleDouble = with(arith) { add(other) }
actual operator fun DoubleDouble.minus(other: DoubleDouble): DoubleDouble = with(arith) { subtract(other) }
actual operator fun DoubleDouble.times(other: DoubleDouble): DoubleDouble = with(arith) { multiply(other) }
actual operator fun DoubleDouble.div(other: DoubleDouble): DoubleDouble = with(arith) { divide(other) }
