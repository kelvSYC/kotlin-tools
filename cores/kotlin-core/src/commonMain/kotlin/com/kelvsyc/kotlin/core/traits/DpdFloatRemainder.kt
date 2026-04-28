package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.DpdFloat
import com.kelvsyc.kotlin.core.fp.bidDpdFloat
import com.kelvsyc.kotlin.core.wrap

private val dpdFloatIeee754Instance: FloatingPointRemainder<DpdFloat> =
    object : FloatingPointRemainder<DpdFloat> {
        override fun DpdFloat.rem(other: DpdFloat): DpdFloat =
            bidDpdFloat.wrap { a: BidFloat, b: BidFloat ->
                with(FloatingPointRemainder.bidFloatIeee754) { a.rem(b) }
            }(this, other)
    }

private val dpdFloatTruncatingInstance: FloatingPointRemainder<DpdFloat> =
    object : FloatingPointRemainder<DpdFloat> {
        override fun DpdFloat.rem(other: DpdFloat): DpdFloat =
            bidDpdFloat.wrap { a: BidFloat, b: BidFloat ->
                with(FloatingPointRemainder.bidFloatTruncating) { a.rem(b) }
            }(this, other)
    }

/** IEEE 754-2008 §5.3.1 remainder for [DpdFloat]: delegates to [FloatingPointRemainder.bidFloatIeee754] via BID↔DPD conversion. */
val FloatingPointRemainder.Companion.dpdFloatIeee754: FloatingPointRemainder<DpdFloat>
    get() = dpdFloatIeee754Instance

/** Truncating remainder for [DpdFloat]: delegates to [FloatingPointRemainder.bidFloatTruncating] via BID↔DPD conversion. */
val FloatingPointRemainder.Companion.dpdFloatTruncating: FloatingPointRemainder<DpdFloat>
    get() = dpdFloatTruncatingInstance
