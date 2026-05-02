package com.kelvsyc.kotlin.core.collections

/**
 * Returns `true` if all key/value pairs in this multimap match the given [predicate].
 */
fun <K, V> FlatMultimap<out K, V>.all(predicate: (Pair<K, V>) -> Boolean): Boolean = entries.all(predicate)

/**
 * Returns `true` if this multimap has at least one key-value pair.
 */
fun <K, V> FlatMultimap<out K, V>.any(): Boolean = entries.any()

/**
 * Returns `true` if at least one key-value pair in this multimap matches the given [predicate].
 */
fun <K, V> FlatMultimap<out K, V>.any(predicate: (Pair<K, V>) -> Boolean): Boolean = entries.any(predicate)

/**
 * Checks if this multimap contains the given [key].
 */
operator fun <K, V> FlatMultimap<out K, V>.contains(key: K): Boolean = asMap.containsKey(key)

/**
 * Returns the number of entries in this multimap.
 */
fun <K, V> FlatMultimap<out K, V>.count(): Int = size

/**
 * Returns the number of entries in this multimap matching the given [predicate].
 */
fun <K, V> FlatMultimap<out K, V>.count(predicate: (Pair<K, V>) -> Boolean): Int = entries.count(predicate)

/**
 * Returns a new [FlatMultimap] containing only the key-value pairs matching the given [predicate].
 */
fun <K, V> FlatMultimap<out K, V>.filter(predicate: (Pair<K, V>) -> Boolean): FlatMultimap<K, V> =
    entries.filter(predicate).toFlatMultimap()

/**
 * Returns a new [FlatMultimap] containing only the entries with keys matching the given [predicate].
 */
fun <K, V> FlatMultimap<out K, V>.filterKeys(predicate: (K) -> Boolean): FlatMultimap<K, V> =
    entries.filter { (k, _) -> predicate(k) }.toFlatMultimap()

/**
 * Returns a new [FlatMultimap] containing only the entries with values matching the given [predicate]. Keys with no
 * remaining values are omitted from the result.
 */
fun <K, V> FlatMultimap<out K, V>.filterValues(predicate: (V) -> Boolean): FlatMultimap<K, V> =
    entries.filter { (_, v) -> predicate(v) }.toFlatMultimap()

/**
 * Returns a single list of all elements yielded from results of [transform] being invoked on each key-value pair in
 * the original multimap.
 */
fun <K, V, R> FlatMultimap<out K, V>.flatMap(transform: (Pair<K, V>) -> Iterable<R>): List<R> = entries.flatMap(transform)

/**
 * Returns a single list of all elements yielded from results of [transform] being invoked on each key-value pair in
 * the original multimap.
 */
@JvmName("flatMapSequence")
fun <K, V, R> FlatMultimap<out K, V>.flatMap(transform: (Pair<K, V>) -> Sequence<R>): List<R> = entries.flatMap(transform)

/**
 * Performs the given [action] on each entry in this multimap.
 */
fun <K, V> FlatMultimap<out K, V>.forEach(action: (Pair<K, V>) -> Unit) = entries.forEach(action)

/**
 * Performs the given [action] on each key-value pair in this multimap.
 */
fun <K, V> FlatMultimap<out K, V>.forEach(action: (K, V) -> Unit) = entries.forEach { (k, v) -> action(k, v) }

/**
 * Returns `true` if this multimap has at least one key/value pair.
 */
fun <K, V> FlatMultimap<out K, V>.isNotEmpty(): Boolean = entries.isNotEmpty()

/**
 * Returns `true` if this nullable multimap is either `null` or empty.
 */
fun <K, V> FlatMultimap<out K, V>?.isNullOrEmpty(): Boolean = this?.entries.isNullOrEmpty()

/**
 * Returns a [List] containing the results of applying the given [transform] function to each key-value pair in the
 * original multimap.
 */
fun <K, V, R> FlatMultimap<out K, V>.map(transform: (Pair<K, V>) -> R): List<R> = entries.map(transform)

/**
 * Returns a new [FlatMultimap] with keys transformed by [transform]. If two original keys produce the same new key,
 * their value lists are concatenated in [entries] order.
 */
fun <K, V, R> FlatMultimap<out K, V>.mapKeys(transform: (K) -> R): FlatMultimap<R, V> =
    entries.map { (k, v) -> transform(k) to v }.toFlatMultimap()

/**
 * Returns a [List] containing only the non-`null` results of applying the given [transform] function to each key-value
 * pair in the original multimap.
 */
fun <K, V, R> FlatMultimap<out K, V>.mapNotNull(transform: (Pair<K, V>) -> R?): List<R> = entries.mapNotNull(transform)

/**
 * Returns a new [FlatMultimap] with the same keys and values transformed by [transform].
 */
fun <K, V, R> FlatMultimap<out K, V>.mapValues(transform: (V) -> R): FlatMultimap<K, R> =
    entries.map { (k, v) -> k to transform(v) }.toFlatMultimap()

/**
 * Returns `true` if this multimap has no key-value pairs.
 */
fun <K, V> FlatMultimap<out K, V>.none(): Boolean = entries.none()

/**
 * Returns a new [FlatMultimap] containing all entries of the original multimap plus the given [pair].
 */
operator fun <K, V> FlatMultimap<out K, V>.plus(pair: Pair<K, V>): FlatMultimap<K, V> =
    (entries.toList() + pair).toFlatMultimap()

/**
 * Returns a new [FlatMultimap] containing all entries of the original multimap plus all entries of [other].
 */
operator fun <K, V> FlatMultimap<out K, V>.plus(other: FlatMultimap<out K, V>): FlatMultimap<K, V> =
    (entries.toList() + other.entries).toFlatMultimap()

/**
 * Returns a new [FlatMultimap] containing all entries of the original multimap plus all entries in [pairs].
 */
operator fun <K, V> FlatMultimap<out K, V>.plus(pairs: Iterable<Pair<K, V>>): FlatMultimap<K, V> =
    (entries.toList() + pairs).toFlatMultimap()

/**
 * Returns a new [FlatMultimap] containing all entries of the original multimap except those with the given [key].
 */
operator fun <K, V> FlatMultimap<out K, V>.minus(key: K): FlatMultimap<K, V> =
    filterKeys { it != key }

/**
 * Returns a new [FlatMultimap] containing all entries of the original multimap except those whose key is in [keys].
 */
operator fun <K, V> FlatMultimap<out K, V>.minus(keys: Iterable<K>): FlatMultimap<K, V> {
    val keySet = keys.toHashSet()
    return filterKeys { it !in keySet }
}

/**
 * Returns `true` if no key-value pairs in this multimap matches the given [predicate].
 */
fun <K, V> FlatMultimap<out K, V>.none(predicate: (Pair<K, V>) -> Boolean): Boolean = entries.none(predicate)

/**
 * Returns this multimap if not `null`, or an empty [FlatMultimap] otherwise.
 */
fun <K, V> FlatMultimap<K, V>?.orEmpty(): FlatMultimap<K, V> = this ?: emptyFlatMultimap()

/**
 * Returns a [List] containing all the key-value pairs in this multimap.
 */
fun <K, V> FlatMultimap<out K, V>.toList(): List<Pair<K, V>> = entries.toList()

/**
 * Returns a [ListMultiset] of the keys in this multimap, in overall insertion order, with each key appearing once
 * per associated value. This is the count-aware counterpart to [FlatMultimap.keys], which returns only distinct keys.
 */
fun <K, V> FlatMultimap<out K, V>.keyMultiset(): ListMultiset<K> =
    entries.map { it.first }.toListMultiset()

/**
 * Returns a [ListMultimap] grouping the entries of this [FlatMultimap] by key. Values per key appear in their
 * original overall insertion order. Key ordering follows first-occurrence order in this multimap.
 *
 * Note: the round-trip `toListMultimap().toFlatMultimap()` produces a key-grouped flat sequence, which may differ
 * from the original interleaved order.
 */
fun <K, V> FlatMultimap<out K, V>.toListMultimap(): ListMultimap<K, V> =
    entries.toList().toListMultimap()
