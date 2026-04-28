package com.kelvsyc.kotlin.core.traits

/**
 * `DecimalFloatingPointEncoding` is a trait type defining encoding-level predicates for decimal floating-point
 * type [T].
 *
 * IEEE 754-2008 §3.5.2 distinguishes *canonical* from *non-canonical* encodings. A finite decimal value is
 * canonical when its significand falls within the valid range `0..10^p − 1`. In BID encoding the large-significand
 * path can represent integers up to `2^24 − 1 ≈ 16,777,215`, so any value whose significand exceeds `9,999,999` is
 * non-canonical. In DPD encoding there are 24 undefined declet bit patterns whose decoded value exceeds the
 * per-declet maximum of 999; a value containing any such declet is non-canonical.
 *
 * NaN payloads are also subject to canonicalization: a NaN is canonical when its quiet bit (G[5]) is set and its
 * payload bits are all zero, matching the default [DecimalFloatingPoint.NaN] constant.
 *
 * Canonical instances for [com.kelvsyc.kotlin.core.BidFloat] and [com.kelvsyc.kotlin.core.DpdFloat] are available
 * as properties on their companion objects.
 */
interface DecimalFloatingPointEncoding<T> {
    /**
     * Returns `true` if this value is in a canonical encoding.
     *
     * - Finite: canonical when the significand is in the range `0..10^p − 1`.
     * - NaN: canonical when G[5] = 1 (quiet bit set) and all payload bits are zero.
     * - Infinity: always canonical.
     */
    fun T.isCanonical(): Boolean

    /**
     * Returns the canonical encoding of this value.
     *
     * - Finite canonical → returned unchanged.
     * - Finite non-canonical → ±zero with the same sign, per IEEE 754-2008 §3.5.2 (undefined significand is
     *   treated as zero).
     * - Infinity → returned unchanged.
     * - NaN → canonical quiet NaN with the same sign (payload cleared, quiet bit set).
     */
    fun T.canonical(): T

    /**
     * Returns `true` if this value is a quiet NaN.
     *
     * A quiet NaN has G[5] = 1 in the combination field. Returns `false` for all non-NaN values.
     */
    fun T.isQuietNaN(): Boolean

    /**
     * Returns `true` if this value is a signalling NaN.
     *
     * A signalling NaN has G[5] = 0 in the combination field. Returns `false` for all non-NaN values,
     * including quiet NaNs.
     */
    fun T.isSignalingNaN(): Boolean
}
