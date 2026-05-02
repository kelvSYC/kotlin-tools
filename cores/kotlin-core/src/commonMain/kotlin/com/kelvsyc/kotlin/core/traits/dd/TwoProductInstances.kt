package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.traits.Binary32
import com.kelvsyc.kotlin.core.traits.Binary64
import com.kelvsyc.kotlin.core.traits.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.double
import com.kelvsyc.kotlin.core.traits.float

private val floatInstance: TwoProduct<Float> by lazy {
    TwoProduct.from(FloatingPointArithmetic.float, Binary32.Companion)
}
private val doubleInstance: TwoProduct<Double> by lazy {
    TwoProduct.from(FloatingPointArithmetic.double, Binary64.Companion)
}

val TwoProduct.Companion.float: TwoProduct<Float> get() = floatInstance
val TwoProduct.Companion.double: TwoProduct<Double> get() = doubleInstance
