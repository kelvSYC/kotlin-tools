package com.kelvsyc.kotlin.core

/**
 * [Iterator] instance that can support a lookahead of one element.
 */
interface PeekingIterator<T> : Iterator<T> {
    fun peek(): T
}

/**
 * [MutableIterator] instance that can support a lookahead of one element.
 *
 * Note that once an element is peeked using [peek], it cannot subsequently be removed by [remove].
 */
interface MutablePeekingIterator<T> : PeekingIterator<T>, MutableIterator<T>
