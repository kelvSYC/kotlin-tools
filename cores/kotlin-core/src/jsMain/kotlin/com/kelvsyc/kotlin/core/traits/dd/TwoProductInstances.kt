package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.traits.fp.Binary32
import com.kelvsyc.kotlin.core.traits.fp.strictFloatArithmetic

private val floatInstance: TwoProduct<Float> by lazy {
    TwoProduct.from(strictFloatArithmetic, Binary32.Companion)
}

actual val TwoProduct.Companion.float: TwoProduct<Float> get() = floatInstance
