package com.kelvsyc.kotlin.core.fp

import com.kelvsyc.kotlin.core.BidDouble
import com.kelvsyc.kotlin.core.Converter
import com.kelvsyc.kotlin.core.DpdDouble

/**
 * A [Converter] between [BidDouble] and [DpdDouble], the two `decimal64` encoding forms.
 *
 * The forward direction is [BidDouble] → [DpdDouble]; the reverse is [DpdDouble] → [BidDouble].
 * The entire value space is covered: NaN, infinities, both signed zeros, and all finite values.
 */
val bidDpdDouble: Converter<BidDouble, DpdDouble> = bidDpdConverter(
    bidDescriptor = BidDouble,
    dpdDescriptor = DpdDouble,
    bidToFdp = { it.toRegularDecimalFloatingPoint() },
    dpdToFdp = { it.toRegularDecimalFloatingPoint() },
    fdpToBid = { it.toBidDouble() },
    fdpToDpd = { it.toDpdDouble() },
)
