package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.ListMultiset

internal class ImmutableListMultiset<E>(elements: List<E>) : ListMultiset<E> {
    private val list: List<E> = elements.toList()
    private val map: Map<E, Int> by lazy { list.groupingBy { it }.eachCount() }

    override val size: Int get() = list.size
    override val elements: Set<E> by lazy { map.keys }
    override val asMap: Map<E, Int> by this::map

    override fun count(element: @UnsafeVariance E): Int = map[element] ?: 0

    override fun isEmpty(): Boolean = list.isEmpty()

    override fun contains(element: @UnsafeVariance E): Boolean = map.containsKey(element)

    override fun containsAll(elements: Collection<@UnsafeVariance E>): Boolean = elements.all { contains(it) }

    override fun iterator(): Iterator<E> = list.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ListMultiset<*>) return false
        return map == other.asMap
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = list.toString()
}
