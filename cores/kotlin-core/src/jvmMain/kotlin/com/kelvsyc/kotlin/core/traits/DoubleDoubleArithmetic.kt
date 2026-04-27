package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.fp.DoubleDouble
import com.kelvsyc.kotlin.core.traits.dd.DoubleBinaryFloatingPointArithmetic
import com.kelvsyc.kotlin.core.traits.dd.TwoDiv
import com.kelvsyc.kotlin.core.traits.dd.TwoProduct
import com.kelvsyc.kotlin.core.traits.dd.TwoSum
import com.kelvsyc.kotlin.core.traits.dd.from

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
