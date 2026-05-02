package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [SetMultiset] that supports adding and removing elements.
 *
 * Unlike [MutableListMultiset], removal does not follow FIFO ordering because occurrences have no position.
 * [remove] decrements the count by the requested amount; the element is removed entirely when its count reaches zero.
 */
interface MutableSetMultiset<E> : SetMultiset<E>, MutableMultiset<E>
