package com.kelvsyc.kotlin.commons.collections.collect

import org.apache.commons.collections4.BidiMap
import org.apache.commons.collections4.bidimap.DualHashBidiMap
import org.apache.commons.collections4.bidimap.DualTreeBidiMap

fun <K, V> dualHashBidiMapOf(): DualHashBidiMap<K, V> = DualHashBidiMap()

fun <K, V> dualHashBidiMapOf(vararg pairs: Pair<K, V>): DualHashBidiMap<K, V> =
    DualHashBidiMap<K, V>().also { map -> pairs.forEach { (k, v) -> map.put(k, v) } }

fun <K, V> dualTreeBidiMapOf(): DualTreeBidiMap<K, V> = DualTreeBidiMap()

fun <K, V> dualTreeBidiMapOf(vararg pairs: Pair<K, V>): DualTreeBidiMap<K, V> =
    DualTreeBidiMap<K, V>().also { map -> pairs.forEach { (k, v) -> map.put(k, v) } }

fun <K, V> buildBidiMap(builderAction: BidiMap<K, V>.() -> Unit): BidiMap<K, V> =
    DualHashBidiMap<K, V>().also { it.builderAction() }

fun <K, V> buildDualTreeBidiMap(builderAction: BidiMap<K, V>.() -> Unit): BidiMap<K, V> =
    DualTreeBidiMap<K, V>().also { it.builderAction() }

val <K, V> BidiMap<K, V>.inverse: BidiMap<V, K> get() = inverseBidiMap()
