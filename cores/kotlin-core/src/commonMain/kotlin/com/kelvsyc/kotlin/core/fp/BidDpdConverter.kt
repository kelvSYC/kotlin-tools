package com.kelvsyc.kotlin.core.fp

import com.kelvsyc.kotlin.core.BidFloat
import com.kelvsyc.kotlin.core.Converter
import com.kelvsyc.kotlin.core.DpdFloat
import com.kelvsyc.kotlin.core.traits.dfp.BinaryIntegerDecimal
import com.kelvsyc.kotlin.core.traits.dfp.DenselyPackedDecimal

/**
 * Creates a [Converter] between a [BinaryIntegerDecimal]-encoded type [B] and a [DenselyPackedDecimal]-encoded
 * type [D] that represent the same decimal floating-point format.
 *
 * The forward direction converts [B] → [D]; the reverse converts [D] → [B]. Both directions handle the full
 * value space: NaN and infinities are mapped to their counterparts via [BinaryIntegerDecimal.NaN],
 * [DenselyPackedDecimal.positiveInfinity], etc., without passing through [FiniteDecimalFloatingPoint]. Only
 * finite values (including zero) are routed through [bidToFdp]/[dpdToFdp] and [fdpToBid]/[fdpToDpd].
 *
 * The significand type [S] is determined by the format: [UInt] for `decimal32`, [ULong] for `decimal64`.
 * Concrete instances for the `decimal32` pair are available as [Converter.Companion.bidDpdFloat].
 *
 * @param bidDescriptor Companion-level descriptor for [B], supplying special values and classification.
 * @param dpdDescriptor Companion-level descriptor for [D], supplying special values and classification.
 * @param bidToFdp Converts a finite [B] value to [FiniteDecimalFloatingPoint]. Must not be called on NaN or infinity.
 * @param dpdToFdp Converts a finite [D] value to [FiniteDecimalFloatingPoint]. Must not be called on NaN or infinity.
 * @param fdpToBid Packs a [FiniteDecimalFloatingPoint] into [B].
 * @param fdpToDpd Packs a [FiniteDecimalFloatingPoint] into [D].
 */
fun <B, D, S> bidDpdConverter(
    bidDescriptor: BinaryIntegerDecimal<B>,
    dpdDescriptor: DenselyPackedDecimal<D>,
    bidToFdp: (B) -> FiniteDecimalFloatingPoint<S>,
    dpdToFdp: (D) -> FiniteDecimalFloatingPoint<S>,
    fdpToBid: (FiniteDecimalFloatingPoint<S>) -> B,
    fdpToDpd: (FiniteDecimalFloatingPoint<S>) -> D,
): Converter<B, D> = Converter.of(
    forward = { bid ->
        with(bidDescriptor.classification) {
            when {
                bid.isNaN() -> dpdDescriptor.NaN
                bid.isInfinite() -> with(bidDescriptor.sign) {
                    if (bid.isNegative()) dpdDescriptor.negativeInfinity else dpdDescriptor.positiveInfinity
                }
                else -> fdpToDpd(bidToFdp(bid))
            }
        }
    },
    backward = { dpd ->
        with(dpdDescriptor.classification) {
            when {
                dpd.isNaN() -> bidDescriptor.NaN
                dpd.isInfinite() -> with(dpdDescriptor.sign) {
                    if (dpd.isNegative()) bidDescriptor.negativeInfinity else bidDescriptor.positiveInfinity
                }
                else -> fdpToBid(dpdToFdp(dpd))
            }
        }
    }
)

/**
 * A [Converter] between [BidFloat] and [DpdFloat], the two `decimal32` encoding forms.
 *
 * The forward direction is [BidFloat] → [DpdFloat]; the reverse is [DpdFloat] → [BidFloat].
 * The entire value space is covered: NaN, infinities, both signed zeros, and all finite values.
 */
val bidDpdFloat: Converter<BidFloat, DpdFloat> = bidDpdConverter(
    bidDescriptor = BidFloat,
    dpdDescriptor = DpdFloat,
    bidToFdp = { it.toRegularDecimalFloatingPoint() },
    dpdToFdp = { it.toRegularDecimalFloatingPoint() },
    fdpToBid = { it.toBidFloat() },
    fdpToDpd = { it.toDpdFloat() },
)
