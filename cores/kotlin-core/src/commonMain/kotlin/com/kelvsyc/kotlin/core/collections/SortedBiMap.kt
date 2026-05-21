package com.kelvsyc.kotlin.core.collections

/**
 * A [BiMap] whose keys are ordered by a [Comparator].
 *
 * Extends [SortedMap] — keys iteration, floor/ceiling/lower/higher navigation, and range views
 * (which return snapshot [SortedBiMap] instances preserving the bijection) are all available.
 *
 * The value side carries no ordering constraint; the [inverse] is therefore a plain [BiMap].
 * For a BiMap where both directions are comparator-ordered, see [BiSortedBiMap].
 */
interface SortedBiMap<K, V> : BiMap<K, V>, SortedMap<K, V> {
    override val keys: SortedSet<K>

    override val inverse: BiMap<V, K>

    override fun headMap(toKey: K, inclusive: Boolean): SortedBiMap<K, V>
    override fun tailMap(fromKey: K, inclusive: Boolean): SortedBiMap<K, V>
    override fun subMap(fromKey: K, fromInclusive: Boolean, toKey: K, toInclusive: Boolean): SortedBiMap<K, V>
    override fun descendingMap(): SortedBiMap<K, V>
}
