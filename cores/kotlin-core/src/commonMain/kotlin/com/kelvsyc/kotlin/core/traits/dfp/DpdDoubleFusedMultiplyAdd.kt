package com.kelvsyc.kotlin.core.traits.dfp
import com.kelvsyc.kotlin.core.traits.fp.FusedMultiplyAdd

import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.DpdDouble
import com.kelvsyc.kotlin.core.fp.bidDpdDouble

private val dpdDoubleFmaInstance: FusedMultiplyAdd<DpdDouble> = object : FusedMultiplyAdd<DpdDouble> {
    override fun fma(a: DpdDouble, b: DpdDouble, c: DpdDouble): DpdDouble {
        val aB: BidDouble = bidDpdDouble.reverse(a)
        val bB: BidDouble = bidDpdDouble.reverse(b)
        val cB: BidDouble = bidDpdDouble.reverse(c)
        return bidDpdDouble(FusedMultiplyAdd.bidDouble.fma(aB, bB, cB))
    }
}

/** FMA for [DpdDouble]: delegates to [FusedMultiplyAdd.bidDouble] via BID↔DPD conversion. */
val FusedMultiplyAdd.Companion.dpdDouble: FusedMultiplyAdd<DpdDouble>
    get() = dpdDoubleFmaInstance
