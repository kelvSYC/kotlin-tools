package com.kelvsyc.kotlin.immutable

fun <T> immutableListOf(): ImmutableList<T> =
    ImmutableModule.List().unsafeCast<ImmutableList<T>>()

fun <T> immutableListOf(vararg values: T): ImmutableList<T> =
    ImmutableModule.List(values).unsafeCast<ImmutableList<T>>()

fun <K, V> immutableMapOf(): ImmutableMap<K, V> =
    ImmutableModule.Map().unsafeCast<ImmutableMap<K, V>>()

// Passes an array of [key, value] pairs — works for any key type, not just String.
fun <K, V> immutableMapOf(vararg pairs: Pair<K, V>): ImmutableMap<K, V> {
    val entries = pairs.map { (k, v) -> arrayOf<Any?>(k, v) }.toTypedArray()
    return ImmutableModule.Map(entries).unsafeCast<ImmutableMap<K, V>>()
}

fun <T> immutableSetOf(): ImmutableSet<T> =
    ImmutableModule.Set().unsafeCast<ImmutableSet<T>>()

fun <T> immutableSetOf(vararg values: T): ImmutableSet<T> =
    ImmutableModule.Set(values).unsafeCast<ImmutableSet<T>>()
