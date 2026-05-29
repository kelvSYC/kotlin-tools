package com.kelvsyc.internal.kotlin.core.structures

import com.kelvsyc.kotlin.core.structures.MutableDisjointSet
import kotlin.enums.EnumEntries

@PublishedApi
internal class ArrayEnumDisjointSet<E : Enum<E>>(
    private val enumEntries: EnumEntries<E>,
) : MutableDisjointSet<E> {
    private val parent = IntArray(enumEntries.size) { it }
    private val rank = IntArray(enumEntries.size) { 0 }

    override val elements: Set<E> get() = enumEntries.toSet()

    override val partitions: Set<Set<E>> get() {
        val groups = HashMap<Int, MutableSet<E>>()
        for (e in enumEntries) {
            groups.getOrPut(findByOrdinal(e.ordinal)) { mutableSetOf() }.add(e)
        }
        return groups.values.toSet()
    }

    private fun findByOrdinal(i: Int): Int {
        var current = i
        while (parent[current] != current) {
            parent[current] = parent[parent[current]]
            current = parent[current]
        }
        return current
    }

    override fun find(element: E): E = enumEntries[findByOrdinal(element.ordinal)]

    override fun union(a: E, b: E) {
        val ra = findByOrdinal(a.ordinal)
        val rb = findByOrdinal(b.ordinal)
        if (ra == rb) return
        when {
            rank[ra] < rank[rb] -> parent[ra] = rb
            rank[ra] > rank[rb] -> parent[rb] = ra
            else -> { parent[rb] = ra; rank[ra]++ }
        }
    }
}
