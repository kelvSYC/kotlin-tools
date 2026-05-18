package com.kelvsyc.kotlin.commons.collections.collect

import org.apache.commons.collections4.trie.PatriciaTrie

fun <V> patriciaTrie(): PatriciaTrie<V> = PatriciaTrie()

fun <V> patriciaTrie(vararg pairs: Pair<String, V>): PatriciaTrie<V> =
    PatriciaTrie<V>().also { trie -> pairs.forEach { (k, v) -> trie.put(k, v) } }

fun <V> buildPatriciaTrie(builderAction: PatriciaTrie<V>.() -> Unit): PatriciaTrie<V> =
    PatriciaTrie<V>().also { it.builderAction() }

fun <V> PatriciaTrie<V>.keysWithPrefix(prefix: String): Set<String> = prefixMap(prefix).keys

fun <V> PatriciaTrie<V>.entriesWithPrefix(prefix: String): Set<Map.Entry<String, V>> = prefixMap(prefix).entries
