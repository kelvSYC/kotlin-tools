package com.kelvsyc.kotlin.commons.collections.collect

import org.apache.commons.collections4.MultiValuedMap
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap
import org.apache.commons.collections4.multimap.HashSetValuedHashMap

fun <K, V> arrayListValuedHashMapOf(): ArrayListValuedHashMap<K, V> = ArrayListValuedHashMap()

fun <K, V> arrayListValuedHashMapOf(vararg pairs: Pair<K, V>): ArrayListValuedHashMap<K, V> =
    ArrayListValuedHashMap<K, V>().also { map -> pairs.forEach { (k, v) -> map.put(k, v) } }

fun <K, V> hashSetValuedHashMapOf(): HashSetValuedHashMap<K, V> = HashSetValuedHashMap()

fun <K, V> hashSetValuedHashMapOf(vararg pairs: Pair<K, V>): HashSetValuedHashMap<K, V> =
    HashSetValuedHashMap<K, V>().also { map -> pairs.forEach { (k, v) -> map.put(k, v) } }

fun <K, V> buildListValuedMap(builderAction: MultiValuedMap<K, V>.() -> Unit): MultiValuedMap<K, V> =
    ArrayListValuedHashMap<K, V>().also { it.builderAction() }

fun <K, V> buildSetValuedMap(builderAction: MultiValuedMap<K, V>.() -> Unit): MultiValuedMap<K, V> =
    HashSetValuedHashMap<K, V>().also { it.builderAction() }

// MultiValuedMap does not extend java.util.Map, so [] requires an explicit operator extension.
// Inside this body, get(key) resolves to the Java member method, not this extension.
operator fun <K, V> MultiValuedMap<K, V>.get(key: K): Collection<V> = get(key)

operator fun <K, V> MultiValuedMap<K, V>.plusAssign(pair: Pair<K, V>) { put(pair.first, pair.second) }
