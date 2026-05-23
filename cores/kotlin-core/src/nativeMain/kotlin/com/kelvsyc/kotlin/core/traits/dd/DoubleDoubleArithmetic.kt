package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.FusedMultiplyAdd
import com.kelvsyc.kotlin.core.traits.fp.double

private val doubleDoubleInstance: DoubleBinaryFloatingPointArithmetic<DoubleDouble, Double> by lazy {
    val fma = FusedMultiplyAdd.double
    val fmaTwoProduct = TwoProduct.from(FloatingPointArithmetic.double, fma)
    val fmaTwoDiv = TwoDiv.from(FloatingPointArithmetic.double, fma)
    DoubleBinaryFloatingPointArithmetic.from(
        arith = FloatingPointArithmetic.double,
        twoProduct = fmaTwoProduct,
        twoSum = TwoSum.double,
        construct = ::DoubleDouble,
        twoDiv = fmaTwoDiv,
    )
}

actual val DoubleBinaryFloatingPointArithmetic.Companion.doubleDouble: DoubleBinaryFloatingPointArithmetic<DoubleDouble, Double>
    get() = doubleDoubleInstance
