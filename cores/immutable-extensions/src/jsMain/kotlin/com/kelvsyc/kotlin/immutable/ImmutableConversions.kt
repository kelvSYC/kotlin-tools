package com.kelvsyc.kotlin.immutable

import com.kelvsyc.kotlin.core.Converter

// ── List ↔ ImmutableList ──────────────────────────────────────────────────────

fun <T> Iterable<T>.toImmutableList(): ImmutableList<T> =
    ImmutableModule.List(toList().toTypedArray()).unsafeCast<ImmutableList<T>>()

fun <T> ImmutableList<T>.toKotlinList(): List<T> = toArray().toList()

fun <T> kotlinListToImmutableList(): Converter<List<T>, ImmutableList<T>> = Converter.of(
    forward = { it.toImmutableList() },
    backward = { it.toKotlinList() },
)

// ── Map ↔ ImmutableMap ────────────────────────────────────────────────────────
//
// toKotlinMap() delegates to asSequence() which consumes the JS entries() iterator.

fun <K, V> Map<K, V>.toImmutableMap(): ImmutableMap<K, V> {
    val entries = entries.map { (k, v) -> arrayOf<Any?>(k, v) }.toTypedArray()
    return ImmutableModule.Map(entries).unsafeCast<ImmutableMap<K, V>>()
}

fun <K, V> ImmutableMap<K, V>.toKotlinMap(): Map<K, V> = asSequence().toMap()

fun <K, V> kotlinMapToImmutableMap(): Converter<Map<K, V>, ImmutableMap<K, V>> = Converter.of(
    forward = { it.toImmutableMap() },
    backward = { it.toKotlinMap() },
)

// ── Set ↔ ImmutableSet ────────────────────────────────────────────────────────

fun <T> Iterable<T>.toImmutableSet(): ImmutableSet<T> =
    ImmutableModule.Set(toList().toTypedArray()).unsafeCast<ImmutableSet<T>>()

fun <T> ImmutableSet<T>.toKotlinSet(): Set<T> = toArray().toSet()

fun <T> kotlinSetToImmutableSet(): Converter<Set<T>, ImmutableSet<T>> = Converter.of(
    forward = { it.toImmutableSet() },
    backward = { it.toKotlinSet() },
)
