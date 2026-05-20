package com.kelvsyc.kotlin.core.collections

/**
 * A mutable [BiMap] that supports adding and removing entries.
 *
 * ### Put contract
 *
 * [put] throws [IllegalArgumentException] if the value already exists under a **different** key
 * (bijection would be violated). Re-inserting the identical key→value pair is a no-op that returns
 * the previous value without throwing.
 *
 * [forcePut] removes any existing entry whose value equals the supplied value (displacing that
 * key), then inserts the new entry. Returns the previous value for the target key, not the
 * displaced key.
 */
interface MutableBiMap<K, V> : BiMap<K, V>, MutableMap<K, V> {
    override val inverse: MutableBiMap<V, K>

    /**
     * Removes any existing entry whose value equals [value], then puts [key] → [value].
     *
     * Returns the value previously associated with [key], or `null` if [key] was absent.
     */
    fun forcePut(key: K, value: V): V?
}
