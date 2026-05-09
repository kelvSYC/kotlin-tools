@file:JvmName("TwoDivInstancesJvm")

package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.FusedMultiplyAdd
import com.kelvsyc.kotlin.core.traits.fp.float

private val floatInstance: TwoDiv<Float> by lazy {
    TwoDiv.from(FloatingPointArithmetic.float, FusedMultiplyAdd.float)
}

actual val TwoDiv.Companion.float: TwoDiv<Float> get() = floatInstance
