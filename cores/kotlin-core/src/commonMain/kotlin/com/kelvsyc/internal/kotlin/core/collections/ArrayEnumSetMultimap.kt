package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.EnumMap
import com.kelvsyc.kotlin.core.collections.MutableEnumSetMultimap
import com.kelvsyc.kotlin.core.collections.SetMultimap
import kotlin.enums.EnumEntries

@PublishedApi
internal class ArrayEnumSetMultimap<K : Enum<K>, V>(
    override val enumEntries: EnumEntries<K>,
) : MutableEnumSetMultimap<K, V> {
    private val backingArray: Array<MutableSet<V>?> = arrayOfNulls(enumEntries.size)

    override val size: Int get() = backingArray.sumOf { it?.size ?: 0 }

    override val asMap: EnumMap<K, Set<V>> get() = AsMapView()

    override fun get(key: K): Set<V> = backingArray[key.ordinal]?.toSet() ?: emptySet()

    override fun containsKey(key: K): Boolean = backingArray[key.ordinal] != null

    override fun containsValue(value: V): Boolean = backingArray.any { it != null && value in it }

    override fun containsEntry(key: K, value: V): Boolean = backingArray[key.ordinal]?.contains(value) == true

    override fun put(key: K, value: V): Boolean {
        val set = backingArray[key.ordinal]
        return if (set != null) {
            set.add(value)
        } else {
            backingArray[key.ordinal] = linkedSetOf(value)
            true
        }
    }

    override fun replaceValues(key: K, values: Iterable<V>): Set<V> {
        val newValues = values.toCollection(LinkedHashSet())
        val ord = key.ordinal
        val old = backingArray[ord]
        return if (newValues.isEmpty()) {
            if (old == null) emptySet()
            else {
                backingArray[ord] = null
                old
            }
        } else {
            backingArray[ord] = newValues
            old ?: emptySet()
        }
    }

    override fun remove(key: K): Set<V> {
        val ord = key.ordinal
        val removed = backingArray[ord] ?: return emptySet()
        backingArray[ord] = null
        return removed
    }

    override fun remove(key: K, value: V): Boolean {
        val set = backingArray[key.ordinal] ?: return false
        val removed = set.remove(value)
        if (removed && set.isEmpty()) backingArray[key.ordinal] = null
        return removed
    }

    override fun clear() {
        backingArray.fill(null)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SetMultimap<*, *>) return false
        return asMap == other.asMap
    }

    override fun hashCode(): Int = asMap.hashCode()

    override fun toString(): String = asMap.toString()

    private inner class AsMapEntry(private val ordinal: Int) : Map.Entry<K, Set<V>> {
        override val key: K get() = enumEntries[ordinal]

        @Suppress("UNCHECKED_CAST")
        override val value: Set<V> get() = backingArray[ordinal] as Set<V>

        override fun equals(other: Any?): Boolean {
            if (other !is Map.Entry<*, *>) return false
            return key == other.key && value == other.value
        }

        override fun hashCode(): Int = key.hashCode() xor value.hashCode()

        override fun toString(): String = "$key=$value"
    }

    private inner class AsMapView : AbstractMap<K, Set<V>>(), EnumMap<K, Set<V>> {
        override val enumEntries: EnumEntries<K> get() = this@ArrayEnumSetMultimap.enumEntries

        override val size: Int
            get() = backingArray.count { it != null }

        override fun containsKey(key: K): Boolean = backingArray[key.ordinal] != null

        @Suppress("UNCHECKED_CAST")
        override fun get(key: K): Set<V>? = backingArray[key.ordinal] as Set<V>?

        override val entries: Set<Map.Entry<K, Set<V>>> = EntrySet()

        private inner class EntrySet : AbstractSet<Map.Entry<K, Set<V>>>() {
            override val size: Int get() = this@AsMapView.size

            override fun iterator(): Iterator<Map.Entry<K, Set<V>>> = iterator {
                for (i in backingArray.indices) {
                    if (backingArray[i] != null) {
                        yield(AsMapEntry(i))
                    }
                }
            }
        }
    }
}
