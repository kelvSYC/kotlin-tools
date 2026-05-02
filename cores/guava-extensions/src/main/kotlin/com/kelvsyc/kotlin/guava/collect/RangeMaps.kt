package com.kelvsyc.kotlin.guava.collect

import com.google.common.collect.ImmutableRangeMap
import com.google.common.collect.Range
import com.google.common.collect.RangeMap
import com.google.common.collect.TreeRangeMap

fun <K : Comparable<K>, V : Any> buildRangeMap(action: ImmutableRangeMap.Builder<K, V>.() -> Unit): RangeMap<K, V> =
    ImmutableRangeMap.builder<K, V>().apply(action).build()

fun <K : Comparable<K>, V : Any> emptyRangeMap(): RangeMap<K, V> = ImmutableRangeMap.of()

fun <K : Comparable<K>, V : Any> rangeMapOf(): RangeMap<K, V> = ImmutableRangeMap.of()

fun <K : Comparable<K>, V : Any> rangeMapOf(element: Pair<Range<K>, V>): RangeMap<K, V> =
    ImmutableRangeMap.of(element.first, element.second)

fun <K : Comparable<K>, V : Any> rangeMapOf(vararg elements: Pair<Range<K>, V>): RangeMap<K, V> = buildRangeMap {
    elements.forEach { put(it.first, it.second) }
}

/**
 * Returns `true` if the given [key] falls within any range in this map.
 */
operator fun <K : Comparable<K>, V : Any> RangeMap<K, V>.contains(key: K): Boolean = get(key) != null

fun <K : Comparable<K>, V : Any> treeRangeMapOf(): TreeRangeMap<K, V> = TreeRangeMap.create()

fun <K : Comparable<K>, V : Any> treeRangeMapOf(vararg elements: Pair<Range<K>, V>): TreeRangeMap<K, V> =
    treeRangeMapOf<K, V>().apply { elements.forEach { put(it.first, it.second) } }
