package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.DpdFloat
import com.kelvsyc.kotlin.core.fp.bidDpdFloat

private val dpdFloatSqrtInstance: FloatingPointSquareRoot<DpdFloat> =
    DelegatingDpdSquareRoot(FloatingPointSquareRoot.bidFloat, bidDpdFloat)

/**
 * Square root for [DpdFloat]: delegates to [FloatingPointSquareRoot.bidFloat] via BID↔DPD conversion.
 */
val FloatingPointSquareRoot.Companion.dpdFloat: FloatingPointSquareRoot<DpdFloat>
    get() = dpdFloatSqrtInstance
