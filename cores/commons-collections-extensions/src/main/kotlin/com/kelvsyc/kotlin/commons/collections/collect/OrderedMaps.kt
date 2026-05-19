package com.kelvsyc.kotlin.commons.collections.collect

import org.apache.commons.collections4.map.LinkedMap

fun <K, V> linkedOrderedMapOf(): LinkedMap<K, V> = LinkedMap()

fun <K, V> linkedOrderedMapOf(vararg pairs: Pair<K, V>): LinkedMap<K, V> =
    LinkedMap<K, V>().also { map -> pairs.forEach { (k, v) -> map.put(k, v) } }

fun <K, V> buildLinkedMap(builderAction: LinkedMap<K, V>.() -> Unit): LinkedMap<K, V> =
    LinkedMap<K, V>().also { it.builderAction() }
