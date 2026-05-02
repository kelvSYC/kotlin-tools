package com.kelvsyc.kotlin.core.traits.dfp
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic

import com.kelvsyc.kotlin.core.DpdFloat
import com.kelvsyc.kotlin.core.fp.bidDpdFloat

private val dpdFloatArithmeticInstance: FloatingPointArithmetic<DpdFloat> =
    DelegatingDpdArithmetic(FloatingPointArithmetic.bidFloat, bidDpdFloat)

/**
 * IEEE 754-2008 arithmetic for [DpdFloat]: delegates to [FloatingPointArithmetic.bidFloat] via BID↔DPD conversion.
 */
val FloatingPointArithmetic.Companion.dpdFloat: FloatingPointArithmetic<DpdFloat>
    get() = dpdFloatArithmeticInstance
