package com.kelvsyc.kotlin.core.collections

/**
 * A [BiMap] whose keys are ordered by a [Comparator].
 *
 * The value side carries no ordering constraint; the [inverse] is therefore a plain [BiMap].
 * For a BiMap where both directions are comparator-ordered, see [BiSortedBiMap].
 */
interface SortedBiMap<K, V> : BiMap<K, V> {
    val comparator: Comparator<in K>

    override val inverse: BiMap<V, K>
}
