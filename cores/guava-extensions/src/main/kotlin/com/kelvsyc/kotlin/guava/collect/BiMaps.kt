@file:Suppress("detekt:TooManyFunctions")
package com.kelvsyc.kotlin.guava.collect

import com.google.common.collect.BiMap
import com.google.common.collect.EnumBiMap
import com.google.common.collect.EnumHashBiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableBiMap

fun <K : Any, V : Any> buildBiMap(action: ImmutableBiMap.Builder<K, V>.() -> Unit): BiMap<K, V> =
    ImmutableBiMap.builder<K, V>().apply(action).build()

fun <K : Any, V : Any> emptyBiMap(): BiMap<K, V> = ImmutableBiMap.of()

fun <K : Any, V : Any> biMapOf(): BiMap<K, V> = ImmutableBiMap.of()

fun <K : Any, V : Any> biMapOf(element: Pair<K, V>): BiMap<K, V> =
    ImmutableBiMap.of(element.first, element.second)

fun <K : Any, V : Any> biMapOf(e1: Pair<K, V>, e2: Pair<K, V>): BiMap<K, V> =
    ImmutableBiMap.of(e1.first, e1.second, e2.first, e2.second)

fun <K : Any, V : Any> biMapOf(e1: Pair<K, V>, e2: Pair<K, V>, e3: Pair<K, V>): BiMap<K, V> =
    ImmutableBiMap.of(e1.first, e1.second, e2.first, e2.second, e3.first, e3.second)

fun <K : Any, V : Any> biMapOf(e1: Pair<K, V>, e2: Pair<K, V>, e3: Pair<K, V>, e4: Pair<K, V>): BiMap<K, V> =
    ImmutableBiMap.of(e1.first, e1.second, e2.first, e2.second, e3.first, e3.second, e4.first, e4.second)

fun <K : Any, V : Any> biMapOf(
    e1: Pair<K, V>,
    e2: Pair<K, V>,
    e3: Pair<K, V>,
    e4: Pair<K, V>,
    e5: Pair<K, V>,
): BiMap<K, V> =
    ImmutableBiMap.of(
        e1.first, e1.second, e2.first, e2.second, e3.first, e3.second,
        e4.first, e4.second, e5.first, e5.second,
    )

fun <K : Any, V : Any> biMapOf(vararg elements: Pair<K, V>): BiMap<K, V> = buildBiMap {
    elements.forEach { put(it.first, it.second) }
}

fun <K : Any, V : Any> Map<K, V>.toImmutableBiMap(): BiMap<K, V> = ImmutableBiMap.copyOf(this)

inline fun <reified K : Enum<K>, reified V : Enum<V>> enumBiMapOf(): EnumBiMap<K, V> =
    EnumBiMap.create(K::class.java, V::class.java)

fun <K : Enum<K>, V : Enum<V>> enumBiMapOf(vararg elements: Pair<K, V>): EnumBiMap<K, V> =
    EnumBiMap.create(mapOf(*elements))

inline fun <reified K : Enum<K>, V> enumHashBiMapOf(): EnumHashBiMap<K, V> =
    EnumHashBiMap.create(K::class.java)

fun <K : Enum<K>, V> enumHashBiMapOf(vararg elements: Pair<K, V>): EnumHashBiMap<K, V> =
    EnumHashBiMap.create(mapOf(*elements))

fun <K, V> hashBiMapOf(): HashBiMap<K, V> = HashBiMap.create()

fun <K, V> hashBiMapOf(vararg elements: Pair<K, V>): HashBiMap<K, V> = HashBiMap.create(mapOf(*elements))
