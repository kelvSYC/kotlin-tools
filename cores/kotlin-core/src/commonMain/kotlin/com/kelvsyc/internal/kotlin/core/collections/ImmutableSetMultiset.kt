package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.Multiset
import com.kelvsyc.kotlin.core.collections.SetMultiset

internal class ImmutableSetMultiset<E>(counts: Map<E, Int>) : SetMultiset<E> {
    private val map: Map<E, Int> = counts.filter { it.value > 0 }

    override val size: Int by lazy { map.values.sum() }
    override val elements: Set<E> by lazy { map.keys }
    override val asMap: Map<E, Int> = map

    override fun count(element: @UnsafeVariance E): Int = map[element] ?: 0

    override fun isEmpty(): Boolean = map.isEmpty()

    override fun contains(element: @UnsafeVariance E): Boolean = map.containsKey(element)

    override fun containsAll(elements: Collection<@UnsafeVariance E>): Boolean = elements.all { contains(it) }

    override fun iterator(): Iterator<E> =
        map.entries.flatMap { (e, count) -> List(count) { e } }.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Multiset<*>) return false
        return map == other.asMap
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = map.toString()
}
