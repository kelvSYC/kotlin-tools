package com.kelvsyc.kotlin.core.collections

/**
 * A [Multiset] with no guaranteed iteration order. Elements are iterated in an unspecified order.
 *
 * [equals] and [hashCode] are count-based and order-insensitive, consistent with [Multiset].
 */
interface SetMultiset<out E> : Multiset<E>
