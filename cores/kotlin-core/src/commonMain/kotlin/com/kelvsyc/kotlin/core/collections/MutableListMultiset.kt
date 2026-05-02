package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [ListMultiset] that supports adding and removing elements.
 *
 * All removal operations follow FIFO ordering: when multiple occurrences of an element are present, the earliest
 * inserted occurrences are removed first.
 *
 * [setCount] adds new occurrences at the end and removes the earliest occurrences first (FIFO).
 */
interface MutableListMultiset<E> : ListMultiset<E>, MutableMultiset<E>
