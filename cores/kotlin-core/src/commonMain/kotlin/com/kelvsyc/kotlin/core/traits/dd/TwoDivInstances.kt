package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.traits.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.FusedMultiplyAdd
import com.kelvsyc.kotlin.core.traits.double
import com.kelvsyc.kotlin.core.traits.float

private val floatInstance: TwoDiv<Float> by lazy {
    TwoDiv.from(FloatingPointArithmetic.float, FusedMultiplyAdd.float)
}
private val doubleInstance: TwoDiv<Double> by lazy {
    TwoDiv.from(FloatingPointArithmetic.double, FusedMultiplyAdd.double)
}

val TwoDiv.Companion.float: TwoDiv<Float> get() = floatInstance
val TwoDiv.Companion.double: TwoDiv<Double> get() = doubleInstance
