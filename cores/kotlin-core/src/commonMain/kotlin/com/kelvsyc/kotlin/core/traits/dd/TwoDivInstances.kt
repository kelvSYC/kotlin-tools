package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.FusedMultiplyAdd
import com.kelvsyc.kotlin.core.traits.fp.double
import com.kelvsyc.kotlin.core.traits.fp.float

private val floatInstance: TwoDiv<Float> by lazy {
    TwoDiv.from(FloatingPointArithmetic.float, FusedMultiplyAdd.float)
}
private val doubleInstance: TwoDiv<Double> by lazy {
    TwoDiv.from(FloatingPointArithmetic.double, FusedMultiplyAdd.double)
}

val TwoDiv.Companion.float: TwoDiv<Float> get() = floatInstance
val TwoDiv.Companion.double: TwoDiv<Double> get() = doubleInstance
