package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.EnumMap
import com.kelvsyc.kotlin.core.collections.ListMultimap
import com.kelvsyc.kotlin.core.collections.MutableEnumListMultimap
import kotlin.enums.EnumEntries

@PublishedApi
internal class ArrayEnumListMultimap<K : Enum<K>, V>(
    override val enumEntries: EnumEntries<K>,
) : MutableEnumListMultimap<K, V> {
    private val backingArray: Array<MutableList<V>?> = arrayOfNulls(enumEntries.size)
    private var _size: Int = 0

    override val size: Int get() = _size

    override val asMap: EnumMap<K, List<V>> get() = AsMapView()

    override fun get(key: K): List<V> = backingArray[key.ordinal]?.toList() ?: emptyList()

    override fun containsKey(key: K): Boolean = backingArray[key.ordinal] != null

    override fun containsValue(value: V): Boolean = backingArray.any { it != null && value in it }

    override fun containsEntry(key: K, value: V): Boolean = backingArray[key.ordinal]?.contains(value) == true

    override fun put(key: K, value: V) {
        val list = backingArray[key.ordinal]
        if (list != null) {
            list.add(value)
        } else {
            backingArray[key.ordinal] = mutableListOf(value)
        }
        _size++
    }

    override fun putAll(key: K, values: Iterable<V>) {
        val newValues = values.toList()
        if (newValues.isEmpty()) return
        val list = backingArray[key.ordinal]
        if (list != null) {
            list.addAll(newValues)
        } else {
            backingArray[key.ordinal] = newValues.toMutableList()
        }
        _size += newValues.size
    }

    override fun replaceValues(key: K, values: Iterable<V>): List<V> {
        val newValues = values.toMutableList()
        val ord = key.ordinal
        val old = backingArray[ord]
        return if (newValues.isEmpty()) {
            if (old == null) emptyList()
            else {
                backingArray[ord] = null
                _size -= old.size
                old
            }
        } else {
            backingArray[ord] = newValues
            val oldSize = old?.size ?: 0
            _size += newValues.size - oldSize
            old ?: emptyList()
        }
    }

    override fun remove(key: K): List<V> {
        val ord = key.ordinal
        val removed = backingArray[ord] ?: return emptyList()
        backingArray[ord] = null
        _size -= removed.size
        return removed
    }

    override fun remove(key: K, value: V): Boolean {
        val list = backingArray[key.ordinal] ?: return false
        val removed = list.remove(value)
        if (removed) {
            _size--
            if (list.isEmpty()) backingArray[key.ordinal] = null
        }
        return removed
    }

    override fun clear() {
        backingArray.fill(null)
        _size = 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ListMultimap<*, *>) return false
        return asMap == other.asMap
    }

    override fun hashCode(): Int = asMap.hashCode()

    override fun toString(): String = asMap.toString()

    private inner class AsMapEntry(private val ordinal: Int) : Map.Entry<K, List<V>> {
        override val key: K get() = enumEntries[ordinal]

        @Suppress("UNCHECKED_CAST")
        override val value: List<V> get() = backingArray[ordinal] as List<V>

        override fun equals(other: Any?): Boolean {
            if (other !is Map.Entry<*, *>) return false
            return key == other.key && value == other.value
        }

        override fun hashCode(): Int = key.hashCode() xor value.hashCode()

        override fun toString(): String = "$key=$value"
    }

    private inner class AsMapView : AbstractMap<K, List<V>>(), EnumMap<K, List<V>> {
        override val enumEntries: EnumEntries<K> get() = this@ArrayEnumListMultimap.enumEntries

        override val size: Int
            get() = backingArray.count { it != null }

        override fun containsKey(key: K): Boolean = backingArray[key.ordinal] != null

        @Suppress("UNCHECKED_CAST")
        override fun get(key: K): List<V>? = backingArray[key.ordinal] as List<V>?

        override val entries: Set<Map.Entry<K, List<V>>> = EntrySet()

        private inner class EntrySet : AbstractSet<Map.Entry<K, List<V>>>() {
            override val size: Int get() = this@AsMapView.size

            override fun iterator(): Iterator<Map.Entry<K, List<V>>> = iterator {
                for (i in backingArray.indices) {
                    if (backingArray[i] != null) {
                        yield(AsMapEntry(i))
                    }
                }
            }
        }
    }
}
