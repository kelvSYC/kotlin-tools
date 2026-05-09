@file:JvmName("TwoProductInstancesJvm")

package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.traits.fp.Binary32
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.float

private val floatInstance: TwoProduct<Float> by lazy {
    TwoProduct.from(FloatingPointArithmetic.float, Binary32.Companion)
}

actual val TwoProduct.Companion.float: TwoProduct<Float> get() = floatInstance
