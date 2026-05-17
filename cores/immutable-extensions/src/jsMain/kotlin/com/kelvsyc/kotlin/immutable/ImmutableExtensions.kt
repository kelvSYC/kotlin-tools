package com.kelvsyc.kotlin.immutable

// ── asSequence() ──────────────────────────────────────────────────────────────
//
// Bridges to Kotlin stdlib — unlocks map, filter, fold, any, all, groupBy, etc.
// ImmutableList and ImmutableSet use toArray() (already bound); ImmutableMap
// walks the JS entries() iterator to emit Pair<K, V>.

fun <T> ImmutableList<T>.asSequence(): Sequence<T> = toArray().asSequence()

fun <T> ImmutableSet<T>.asSequence(): Sequence<T> = toArray().asSequence()

fun <K, V> ImmutableMap<K, V>.asSequence(): Sequence<Pair<K, V>> = sequence {
    val iter = entries()
    var item = iter.next()
    while (item.done != true) {
        val entry = item.value
        yield(entry[0].unsafeCast<K>() to entry[1].unsafeCast<V>())
        item = iter.next()
    }
}

// ── isNotEmpty() ──────────────────────────────────────────────────────────────

fun <T> ImmutableList<T>.isNotEmpty(): Boolean = !isEmpty()
fun <K, V> ImmutableMap<K, V>.isNotEmpty(): Boolean = !isEmpty()
fun <T> ImmutableSet<T>.isNotEmpty(): Boolean = !isEmpty()

// ── Safe list access ──────────────────────────────────────────────────────────

fun <T> ImmutableList<T>.getOrElse(index: Int, defaultValue: () -> T): T =
    get(index) ?: defaultValue()

fun <T> ImmutableList<T>.firstOrNull(): T? = if (isEmpty()) null else get(0)
fun <T> ImmutableList<T>.lastOrNull(): T? = if (isEmpty()) null else get(size - 1)

// ── forEachIndexed ────────────────────────────────────────────────────────────

fun <T> ImmutableList<T>.forEachIndexed(action: (index: Int, value: T) -> Unit) {
    var i = 0
    forEach { value -> action(i++, value) }
}

// ── Map safe access and typed views ──────────────────────────────────────────

fun <K, V> ImmutableMap<K, V>.getOrDefault(key: K, defaultValue: V): V = get(key) ?: defaultValue
fun <K, V> ImmutableMap<K, V>.getOrElse(key: K, defaultValue: () -> V): V = get(key) ?: defaultValue()

fun <K, V> ImmutableMap<K, V>.keyList(): List<K> = asSequence().map { it.first }.toList()
fun <K, V> ImmutableMap<K, V>.valueList(): List<V> = asSequence().map { it.second }.toList()
