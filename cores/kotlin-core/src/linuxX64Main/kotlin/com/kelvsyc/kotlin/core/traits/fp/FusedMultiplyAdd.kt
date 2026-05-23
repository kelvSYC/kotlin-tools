package com.kelvsyc.kotlin.core.traits.fp

import com.kelvsyc.kotlin.core.BFloat16
import com.kelvsyc.kotlin.core.Float16
import com.kelvsyc.kotlin.core.traits.dd.TwoProduct
import com.kelvsyc.kotlin.core.traits.dd.TwoSum
import com.kelvsyc.kotlin.core.traits.dd.from

private val floatInstance: FusedMultiplyAdd<Float> by lazy {
    FusedMultiplyAdd.from(FloatingPointArithmetic.float, TwoProduct.from(FloatingPointArithmetic.float, Binary32.Companion), TwoSum.from(FloatingPointArithmetic.float))
}

private val doubleInstance: FusedMultiplyAdd<Double> by lazy {
    val arith = FloatingPointArithmetic.double
    FusedMultiplyAdd.from(arith, TwoProduct.from(arith, Binary64.Companion), TwoSum.from(arith))
}

private val bfloat16Instance: FusedMultiplyAdd<BFloat16> by lazy {
    val floatFma = FusedMultiplyAdd.float
    object : FusedMultiplyAdd<BFloat16> {
        override fun fma(a: BFloat16, b: BFloat16, c: BFloat16): BFloat16 =
            BFloat16(floatFma.fma(a.toFloat(), b.toFloat(), c.toFloat()))
    }
}

private val float16Instance: FusedMultiplyAdd<Float16> by lazy {
    val floatFma = FusedMultiplyAdd.float
    object : FusedMultiplyAdd<Float16> {
        override fun fma(a: Float16, b: Float16, c: Float16): Float16 =
            Float16(floatFma.fma(a.toFloat(), b.toFloat(), c.toFloat()))
    }
}

actual val FusedMultiplyAdd.Companion.float: FusedMultiplyAdd<Float>
    get() = floatInstance

actual val FusedMultiplyAdd.Companion.double: FusedMultiplyAdd<Double>
    get() = doubleInstance

actual val FusedMultiplyAdd.Companion.bfloat16: FusedMultiplyAdd<BFloat16>
    get() = bfloat16Instance

actual val FusedMultiplyAdd.Companion.float16: FusedMultiplyAdd<Float16>
    get() = float16Instance
