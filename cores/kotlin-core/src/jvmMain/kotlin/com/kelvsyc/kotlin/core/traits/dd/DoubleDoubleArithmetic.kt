package com.kelvsyc.kotlin.core.traits.dd

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.fp.FusedMultiplyAdd
import com.kelvsyc.kotlin.core.traits.fp.double

private val doubleDoubleInstance: DoubleBinaryFloatingPointArithmetic<DoubleDouble, Double> by lazy {
    // Build an FMA-backed TwoProduct inline rather than using TwoProduct.Companion.double, which is
    // Veltkamp-Dekker on all platforms. FMA gives the same result with ~10x fewer operations.
    val fmaTwoProduct = TwoProduct.from(FloatingPointArithmetic.double, FusedMultiplyAdd.double)
    DoubleBinaryFloatingPointArithmetic.from(
        arith = FloatingPointArithmetic.double,
        twoProduct = fmaTwoProduct,
        twoSum = TwoSum.double,
        construct = ::DoubleDouble,
        twoDiv = TwoDiv.double,
    )
}

actual val DoubleBinaryFloatingPointArithmetic.Companion.doubleDouble: DoubleBinaryFloatingPointArithmetic<DoubleDouble, Double>
    get() = doubleDoubleInstance
