package com.kelvsyc.internal.kotlin.core.structures

import com.kelvsyc.kotlin.core.structures.MutableDisjointSet

@PublishedApi
internal class HashDisjointSet<E>(universe: Iterable<E> = emptyList()) : MutableDisjointSet<E> {
    private val parent = HashMap<E, E>()
    private val rank = HashMap<E, Int>()

    init {
        for (e in universe) {
            parent[e] = e
            rank[e] = 0
        }
    }

    override val elements: Set<E> get() = parent.keys.toSet()

    override val partitions: Set<Set<E>> get() {
        val groups = HashMap<E, MutableSet<E>>()
        for (e in parent.keys) {
            groups.getOrPut(find(e)) { mutableSetOf() }.add(e)
        }
        return groups.values.toSet()
    }

    override fun find(element: E): E {
        if (!parent.containsKey(element)) return element
        var current = element
        while (parent[current] != current) {
            // path halving: point to grandparent each step
            val grandparent = parent[parent[current]!!]
            if (grandparent != null) parent[current] = grandparent
            current = parent[current]!!
        }
        return current
    }

    override fun union(a: E, b: E) {
        if (!parent.containsKey(a)) { parent[a] = a; rank[a] = 0 }
        if (!parent.containsKey(b)) { parent[b] = b; rank[b] = 0 }
        val ra = find(a)
        val rb = find(b)
        if (ra == rb) return
        val rankA = rank[ra]!!
        val rankB = rank[rb]!!
        when {
            rankA < rankB -> parent[ra] = rb
            rankA > rankB -> parent[rb] = ra
            else -> { parent[rb] = ra; rank[ra] = rankA + 1 }
        }
    }
}
