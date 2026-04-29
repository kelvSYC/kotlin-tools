package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.Converter
import com.kelvsyc.kotlin.core.wrap

/**
 * A [FloatingPointSquareRoot]`<D>` that delegates the square root to a supplied
 * [FloatingPointSquareRoot]`<B>` using a [Converter] to translate between the two types.
 *
 * This is the unary analogue of [DelegatingDpdArithmetic]: it computes `sqrt(x)` by converting
 * `x` from `D` to `B`, delegating to the `B` implementation, and converting the result back to `D`.
 *
 * ## Converter convention
 *
 * The [Converter] is typed `Converter<B, D>` following the same convention as
 * [DelegatingDpdArithmetic]: [Converter.invoke] is the forward direction (`B → D`) and
 * [Converter.reverse] is the backward direction (`D → B`). The converter must handle the entire
 * value space — NaN, ±infinity, ±zero, and all finite values.
 */
class DelegatingDpdSquareRoot<B, D>(
    private val delegate: FloatingPointSquareRoot<B>,
    private val converter: Converter<B, D>
) : FloatingPointSquareRoot<D> {
    override fun D.sqrt(): D = converter.wrap { a: B -> with(delegate) { a.sqrt() } }(this)
}
