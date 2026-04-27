package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.traits.dd.TwoDiv
import com.kelvsyc.kotlin.core.traits.dd.from

private val floatInstance: TwoDiv<Float> by lazy {
    val arith: FloatingPointArithmetic<Float> = FloatingPointArithmetic.float
    val fma: FusedMultiplyAdd<Float> = FusedMultiplyAdd.float
    TwoDiv.from(arith, fma)
}

private val doubleInstance: TwoDiv<Double> by lazy {
    val arith: FloatingPointArithmetic<Double> = FloatingPointArithmetic.double
    val fma: FusedMultiplyAdd<Double> = FusedMultiplyAdd.double
    TwoDiv.from(arith, fma)
}

val TwoDiv.Companion.float: TwoDiv<Float> get() = floatInstance
val TwoDiv.Companion.double: TwoDiv<Double> get() = doubleInstance
