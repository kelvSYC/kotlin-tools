package com.kelvsyc.kotlin.commons.collections.collect

import org.apache.commons.collections4.queue.CircularFifoQueue

fun <E> circularFifoQueueOf(maxElements: Int): CircularFifoQueue<E> = CircularFifoQueue(maxElements)

fun <E> circularFifoQueueOf(maxElements: Int, vararg elements: E): CircularFifoQueue<E> =
    CircularFifoQueue<E>(maxElements).also { q -> elements.forEach { q.add(it) } }

operator fun <E> CircularFifoQueue<E>.plusAssign(element: E) { add(element) }

operator fun <E> CircularFifoQueue<E>.minusAssign(element: E) { remove(element) }
