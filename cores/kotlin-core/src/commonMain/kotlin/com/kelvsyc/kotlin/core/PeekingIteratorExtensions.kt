package com.kelvsyc.kotlin.core

import com.kelvsyc.internal.kotlin.core.MutablePeekingIteratorImpl
import com.kelvsyc.internal.kotlin.core.PeekingIteratorImpl

/**
 * Creates a [PeekingIterator] from this iterator.
 */
fun <T> Iterator<T>.makePeeking(): PeekingIterator<T> = PeekingIteratorImpl(this)

/**
 * Returns itself, as it is already a [PeekingIterator].
 */
fun <T> PeekingIterator<T>.makePeeking(): PeekingIterator<T> = this

/**
 * Creates a [MutablePeekingIterator] from this iterator.
 */
fun <T> MutableIterator<T>.makePeeking(): MutablePeekingIterator<T> = MutablePeekingIteratorImpl(this)

/**
 * Returns itself, as it is already a [MutablePeekingIterator].
 */
fun <T> MutablePeekingIterator<T>.makePeeking(): MutablePeekingIterator<T> = this
