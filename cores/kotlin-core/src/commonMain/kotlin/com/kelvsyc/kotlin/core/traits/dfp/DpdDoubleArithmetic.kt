package com.kelvsyc.kotlin.core.traits.dfp
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic

import com.kelvsyc.kotlin.core.DpdDouble
import com.kelvsyc.kotlin.core.fp.bidDpdDouble

private val dpdDoubleArithmeticInstance: FloatingPointArithmetic<DpdDouble> =
    DelegatingDpdArithmetic(FloatingPointArithmetic.bidDouble, bidDpdDouble)

/**
 * IEEE 754-2008 arithmetic for [DpdDouble]: delegates to [FloatingPointArithmetic.bidDouble] via BID↔DPD conversion.
 */
val FloatingPointArithmetic.Companion.dpdDouble: FloatingPointArithmetic<DpdDouble>
    get() = dpdDoubleArithmeticInstance
