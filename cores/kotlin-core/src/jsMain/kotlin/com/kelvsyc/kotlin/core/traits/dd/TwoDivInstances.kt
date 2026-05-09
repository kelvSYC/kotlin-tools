package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.traits.fp.FusedMultiplyAdd
import com.kelvsyc.kotlin.core.traits.fp.float
import com.kelvsyc.kotlin.core.traits.fp.strictFloatArithmetic

private val floatInstance: TwoDiv<Float> by lazy {
    TwoDiv.from(strictFloatArithmetic, FusedMultiplyAdd.float)
}

actual val TwoDiv.Companion.float: TwoDiv<Float> get() = floatInstance
