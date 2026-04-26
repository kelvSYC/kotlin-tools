package com.kelvsyc.kotlin.core

import com.kelvsyc.internal.kotlin.core.ComposedConverter

/**
 * Wraps a unary operation on the forward type, producing a binary operation on the converted type
 */
fun <A, B> Converter<A, B>.wrap(op: (A) -> A): (B) -> B = {
    this(op(reverse(it)))
}

/**
 * Wraps a binary operation on the forward type, producing a binary operation on the converted type.
 */
fun <A, B> Converter<A, B>.wrap(op: (A, A) -> A): (B, B) -> B = { lhs, rhs ->
    this(op(reverse(lhs), reverse(rhs)))
}

/**
 * Creates a new `Converter` by applying this and another converter in succession.
 */
fun <A, B, C> Converter<A, B>.andThen(fn: Converter<B, C>): Converter<A, C> = ComposedConverter(this, fn)
