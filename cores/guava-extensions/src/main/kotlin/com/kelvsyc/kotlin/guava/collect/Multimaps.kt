@file:Suppress("detekt:TooManyFunctions")
package com.kelvsyc.kotlin.guava.collect

import com.google.common.collect.ImmutableListMultimap
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.ImmutableSetMultimap
import com.google.common.collect.ListMultimap
import com.google.common.collect.Multimap
import com.google.common.collect.SetMultimap

fun <K : Any, V : Any> buildMultimap(action: ImmutableMultimap.Builder<K, V>.() -> Unit): Multimap<K, V> =
    ImmutableMultimap.builder<K, V>().apply(action).build()

fun <K : Any, V : Any> emptyMultimap(): Multimap<K, V> = ImmutableMultimap.of()

fun <K : Any, V : Any> multimapOf(): Multimap<K, V> = ImmutableMultimap.of()

fun <K : Any, V : Any> multimapOf(element: Pair<K, V>): Multimap<K, V> =
    ImmutableMultimap.of(element.first, element.second)

fun <K : Any, V : Any> multimapOf(vararg elements: Pair<K, V>): Multimap<K, V> = buildMultimap {
    elements.forEach { put(it.first, it.second) }
}

fun <K : Any, V : Any> Multimap<K, V>.toImmutableMultimap(): Multimap<K, V> = ImmutableMultimap.copyOf(this)

fun <K : Any, V : Any> buildListMultimap(action: ImmutableListMultimap.Builder<K, V>.() -> Unit): ListMultimap<K, V> =
    ImmutableListMultimap.builder<K, V>().apply(action).build()

fun <K : Any, V : Any> emptyListMultimap(): ListMultimap<K, V> = ImmutableListMultimap.of()

fun <K : Any, V : Any> listMultimapOf(): ListMultimap<K, V> = ImmutableListMultimap.of()

fun <K : Any, V : Any> listMultimapOf(vararg elements: Pair<K, V>): ListMultimap<K, V> =
    ImmutableListMultimap.builder<K, V>().apply {
        elements.forEach { put(it.first, it.second) }
    }.build()

fun <K : Any, V : Any> buildSetMultimap(action: ImmutableSetMultimap.Builder<K, V>.() -> Unit): SetMultimap<K, V> =
    ImmutableSetMultimap.builder<K, V>().apply(action).build()

fun <K : Any, V : Any> emptySetMultimap(): SetMultimap<K, V> = ImmutableSetMultimap.of()

fun <K : Any, V : Any> setMultimapOf(): SetMultimap<K, V> = ImmutableSetMultimap.of()

fun <K : Any, V : Any> setMultimapOf(vararg elements: Pair<K, V>): SetMultimap<K, V> = buildSetMultimap {
    elements.forEach { put(it.first, it.second) }
}
