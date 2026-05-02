package com.kelvsyc.kotlin.core.traits.dfp
import com.kelvsyc.kotlin.core.traits.fp.FloatingPointArithmetic

import com.kelvsyc.kotlin.core.Converter
import com.kelvsyc.kotlin.core.wrap

/**
 * A [FloatingPointArithmetic]`<D>` that delegates all operations to a supplied [FloatingPointArithmetic]`<B>`
 * using a [Converter] to translate between the two types.
 *
 * ## Motivation
 *
 * Densely Packed Decimal (DPD) is an IEEE 754-2008 encoding scheme for decimal floating-point values.
 * DPD encodes each group of three decimal digits into a compact 10-bit *declet*, which is efficient for
 * storage but inconvenient for arithmetic: every operation would require decoding the declets, computing
 * in an intermediate representation, then re-encoding the result. Binary Integer Decimal (BID), by
 * contrast, stores the significand directly as a binary integer and admits efficient arithmetic.
 *
 * Both encodings represent the same mathematical values, so a DPD arithmetic implementation can delegate
 * entirely to a BID implementation, paying only a two-way encoding conversion per operation. This class
 * captures that pattern generically for any `(B, D)` pair.
 *
 * ## Converter convention
 *
 * The [Converter] is typed `Converter<B, D>`, where the *forward* direction (`B → D`) converts a
 * delegate result back to the output type, and the *reverse* direction (`D → B`) converts input
 * operands to the delegate's type. This follows [Converter]'s standard convention: [Converter.invoke]
 * is the forward direction and [Converter.reverse] is the backward direction.
 *
 * The converter **must** handle the entire value space — finite values, infinities, both signed zeros,
 * and NaN. Implementations that rely solely on
 * [com.kelvsyc.kotlin.core.fp.toRegularDecimalFloatingPoint] are insufficient because that function
 * rejects non-finite inputs.
 *
 * ## Inherited defaults
 *
 * [FloatingPointSign.abs], [FloatingPointSign.copySign], and [FloatingPointSign.isPositive] are not
 * overridden; they compose correctly from the delegated [FloatingPointSign.isNegative] and
 * [FloatingPointSign.negate] implementations.
 */
class DelegatingDpdArithmetic<B, D>(
    private val delegate: FloatingPointArithmetic<B>,
    private val converter: Converter<B, D>
) : FloatingPointArithmetic<D> {
    override val zero: D get() = converter(delegate.zero)
    override val one: D get() = converter(delegate.one)

    override fun D.isFinite(): Boolean = with(delegate) { converter.reverse(this@isFinite).isFinite() }
    override fun D.isInfinite(): Boolean = with(delegate) { converter.reverse(this@isInfinite).isInfinite() }
    override fun D.isNaN(): Boolean = with(delegate) { converter.reverse(this@isNaN).isNaN() }
    override fun D.isZero(): Boolean = with(delegate) { converter.reverse(this@isZero).isZero() }
    override fun D.isInteger(): Boolean = with(delegate) { converter.reverse(this@isInteger).isInteger() }
    override fun D.isNegative(): Boolean = with(delegate) { converter.reverse(this@isNegative).isNegative() }

    override fun D.negate(): D = converter.wrap { a: B -> with(delegate) { a.negate() } }(this)
    override fun D.add(other: D): D = converter.wrap { a: B, b: B -> with(delegate) { a.add(b) } }(this, other)
    override fun D.subtract(other: D): D = converter.wrap { a: B, b: B -> with(delegate) { a.subtract(b) } }(this, other)
    override fun D.multiply(other: D): D = converter.wrap { a: B, b: B -> with(delegate) { a.multiply(b) } }(this, other)
    override fun D.divide(other: D): D = converter.wrap { a: B, b: B -> with(delegate) { a.divide(b) } }(this, other)
    override fun D.compareTo(other: D): Int =
        with(delegate) { converter.reverse(this@compareTo).compareTo(converter.reverse(other)) }
}
