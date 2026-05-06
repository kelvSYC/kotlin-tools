package com.kelvsyc.kotlin.core

import com.kelvsyc.internal.kotlin.core.ComposedConverter
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

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

/**
 * Transforms a [ReadOnlyProperty] that yields `A` values into one that yields `B` values
 * by applying this converter's forward direction on each read.
 */
fun <R, A, B> Converter<A, B>.compose(delegate: ReadOnlyProperty<R, A>): ReadOnlyProperty<R, B> =
    ReadOnlyProperty { thisRef, property -> this(delegate.getValue(thisRef, property)) }

/**
 * Transforms a [ReadWriteProperty] that stores `A` values into one that exposes `B` values
 * by applying this converter's forward direction on reads and reverse direction on writes.
 */
fun <R, A, B> Converter<A, B>.compose(delegate: ReadWriteProperty<R, A>): ReadWriteProperty<R, B> =
    object : ReadWriteProperty<R, B> {
        override fun getValue(thisRef: R, property: KProperty<*>): B =
            this@compose(delegate.getValue(thisRef, property))

        override fun setValue(thisRef: R, property: KProperty<*>, value: B) =
            delegate.setValue(thisRef, property, this@compose.reverse(value))
    }
