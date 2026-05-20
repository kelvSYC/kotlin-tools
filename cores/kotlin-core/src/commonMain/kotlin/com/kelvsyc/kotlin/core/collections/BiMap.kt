package com.kelvsyc.kotlin.core.collections

/**
 * A [Map] that enforces a bijection: each value maps to exactly one key, and each key maps to
 * exactly one value. Provides a live [inverse] view with keys and values swapped.
 *
 * Unlike `java.util.Map`, this is a Kotlin Multiplatform type with no JVM interoperability
 * requirement.
 */
interface BiMap<K, V> : Map<K, V> {
    /**
     * A live view of this map with keys and values swapped.
     *
     * Mutations to either view are reflected in the other.
     */
    val inverse: BiMap<V, K>
}
