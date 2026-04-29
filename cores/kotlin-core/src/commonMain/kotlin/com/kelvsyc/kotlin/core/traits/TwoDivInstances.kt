package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.traits.dd.TwoDiv
import com.kelvsyc.kotlin.core.traits.dd.from

private val floatInstance: TwoDiv<Float> by lazy {
    TwoDiv.from(FloatingPointArithmetic.float, FusedMultiplyAdd.float)
}
private val doubleInstance: TwoDiv<Double> by lazy {
    TwoDiv.from(FloatingPointArithmetic.double, FusedMultiplyAdd.double)
}

val TwoDiv.Companion.float: TwoDiv<Float> get() = floatInstance
val TwoDiv.Companion.double: TwoDiv<Double> get() = doubleInstance
