package com.kelvsyc.kotlin.core.traits.dfp
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointRemainder

import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.DpdDouble
import com.kelvsyc.kotlin.core.fp.bidDpdDouble
import com.kelvsyc.kotlin.core.wrap

private val dpdDoubleIeee754Instance: FloatingPointRemainder<DpdDouble> =
    object : FloatingPointRemainder<DpdDouble> {
        override fun DpdDouble.rem(other: DpdDouble): DpdDouble =
            bidDpdDouble.wrap { a: BidDouble, b: BidDouble ->
                with(FloatingPointRemainder.bidDoubleIeee754) { a.rem(b) }
            }(this, other)
    }

private val dpdDoubleTruncatingInstance: FloatingPointRemainder<DpdDouble> =
    object : FloatingPointRemainder<DpdDouble> {
        override fun DpdDouble.rem(other: DpdDouble): DpdDouble =
            bidDpdDouble.wrap { a: BidDouble, b: BidDouble ->
                with(FloatingPointRemainder.bidDoubleTruncating) { a.rem(b) }
            }(this, other)
    }

/** IEEE 754-2008 §5.3.1 remainder for [DpdDouble]: delegates to [FloatingPointRemainder.bidDoubleIeee754] via BID↔DPD conversion. */
val FloatingPointRemainder.Companion.dpdDoubleIeee754: FloatingPointRemainder<DpdDouble>
    get() = dpdDoubleIeee754Instance

/** Truncating remainder for [DpdDouble]: delegates to [FloatingPointRemainder.bidDoubleTruncating] via BID↔DPD conversion. */
val FloatingPointRemainder.Companion.dpdDoubleTruncating: FloatingPointRemainder<DpdDouble>
    get() = dpdDoubleTruncatingInstance
