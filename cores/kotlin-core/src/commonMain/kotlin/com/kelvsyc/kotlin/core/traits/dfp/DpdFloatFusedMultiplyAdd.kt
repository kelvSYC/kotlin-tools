package com.kelvsyc.kotlin.core.traits.dfp
import com.kelvsyc.kotlin.core.traits.fp.FusedMultiplyAdd

import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.DpdFloat
import com.kelvsyc.kotlin.core.fp.bidDpdFloat

private val dpdFloatFmaInstance: FusedMultiplyAdd<DpdFloat> = object : FusedMultiplyAdd<DpdFloat> {
    override fun fma(a: DpdFloat, b: DpdFloat, c: DpdFloat): DpdFloat {
        val aB: BidFloat = bidDpdFloat.reverse(a)
        val bB: BidFloat = bidDpdFloat.reverse(b)
        val cB: BidFloat = bidDpdFloat.reverse(c)
        return bidDpdFloat(FusedMultiplyAdd.bidFloat.fma(aB, bB, cB))
    }
}

/** FMA for [DpdFloat]: delegates to [FusedMultiplyAdd.bidFloat] via BID↔DPD conversion. */
val FusedMultiplyAdd.Companion.dpdFloat: FusedMultiplyAdd<DpdFloat>
    get() = dpdFloatFmaInstance
