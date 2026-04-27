package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.traits.dd.TwoProduct
import com.kelvsyc.kotlin.core.traits.dd.from

private val floatInstance: TwoProduct<Float> by lazy {
    TwoProduct.from(FloatingPointArithmetic.float, Binary32)
}
private val doubleInstance: TwoProduct<Double> by lazy {
    TwoProduct.from(FloatingPointArithmetic.double, Binary64)
}

val TwoProduct.Companion.float: TwoProduct<Float> get() = floatInstance
val TwoProduct.Companion.double: TwoProduct<Double> get() = doubleInstance
