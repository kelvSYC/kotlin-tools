package com.kelvsyc.kotlin.core.traits.dfp
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointSquareRoot

import com.kelvsyc.kotlin.core.DpdDouble
import com.kelvsyc.kotlin.core.fp.bidDpdDouble

private val dpdDoubleSqrtInstance: FloatingPointSquareRoot<DpdDouble> =
    DelegatingDpdSquareRoot(FloatingPointSquareRoot.bidDouble, bidDpdDouble)

/**
 * Square root for [DpdDouble]: delegates to [FloatingPointSquareRoot.bidDouble] via BID↔DPD conversion.
 */
val FloatingPointSquareRoot.Companion.dpdDouble: FloatingPointSquareRoot<DpdDouble>
    get() = dpdDoubleSqrtInstance
