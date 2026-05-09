package com.kelvsyc.kotlin.core.collections

/**
 * Returns `true` if all key/value pairs in this multimap match the given [predicate].
 */
fun <K, V> SetMultimap<out K, V>.all(predicate: (Pair<K, V>) -> Boolean): Boolean = entries.all(predicate)

/**
 * Returns `true` if this multimap has at least one key-value pair.
 */
fun <K, V> SetMultimap<out K, V>.any(): Boolean = entries.any()

/**
 * Returns `true` if at least one key-value pair in this multimap matches the given [predicate].
 */
fun <K, V> SetMultimap<out K, V>.any(predicate: (Pair<K, V>) -> Boolean): Boolean = entries.any(predicate)

/**
 * Checks if this multimap contains the given [key].
 */
operator fun <K, V> SetMultimap<out K, V>.contains(key: K): Boolean = asMap.containsKey(key)

/**
 * Returns the number of entries in this multimap.
 */
fun <K, V> SetMultimap<out K, V>.count(): Int = size

/**
 * Returns the number of entries in this multimap matching the given [predicate].
 */
fun <K, V> SetMultimap<out K, V>.count(predicate: (Pair<K, V>) -> Boolean): Int = entries.count(predicate)

/**
 * Returns a new [SetMultimap] containing only the key-value pairs matching the given [predicate].
 */
fun <K, V> SetMultimap<out K, V>.filter(predicate: (Pair<K, V>) -> Boolean): SetMultimap<K, V> =
    entries.filter(predicate).toSetMultimap()

/**
 * Returns a new [SetMultimap] containing only the entries with keys matching the given [predicate].
 */
fun <K, V> SetMultimap<out K, V>.filterKeys(predicate: (K) -> Boolean): SetMultimap<K, V> =
    entries.filter { (k, _) -> predicate(k) }.toSetMultimap()

/**
 * Returns a new [SetMultimap] containing only the entries with values matching the given [predicate]. Keys with no
 * remaining values are omitted from the result.
 */
fun <K, V> SetMultimap<out K, V>.filterValues(predicate: (V) -> Boolean): SetMultimap<K, V> =
    entries.filter { (_, v) -> predicate(v) }.toSetMultimap()

/**
 * Returns a single list of all elements yielded from results of [transform] being invoked on each key-value pair in
 * the original multimap.
 */
fun <K, V, R> SetMultimap<out K, V>.flatMap(transform: (Pair<K, V>) -> Iterable<R>): List<R> = entries.flatMap(transform)

/**
 * Returns a single list of all elements yielded from results of [transform] being invoked on each key-value pair in
 * the original multimap.
 */
@kotlin.jvm.JvmName("flatMapSequence")
fun <K, V, R> SetMultimap<out K, V>.flatMap(transform: (Pair<K, V>) -> Sequence<R>): List<R> = entries.flatMap(transform)

/**
 * Performs the given [action] on each entry in this multimap.
 */
fun <K, V> SetMultimap<out K, V>.forEach(action: (Pair<K, V>) -> Unit) = entries.forEach(action)

/**
 * Performs the given [action] on each key-value pair in this multimap.
 */
fun <K, V> SetMultimap<out K, V>.forEach(action: (K, V) -> Unit) = entries.forEach { (k, v) -> action(k, v) }

/**
 * Returns `true` if this multimap has at least one key/value pair.
 */
fun <K, V> SetMultimap<out K, V>.isNotEmpty(): Boolean = entries.isNotEmpty()

/**
 * Returns `true` if this nullable multimap is either `null` or empty.
 */
fun <K, V> SetMultimap<out K, V>?.isNullOrEmpty(): Boolean = this?.entries.isNullOrEmpty()

/**
 * Returns a [List] containing the results of applying the given [transform] function to each key-value pair in the
 * original multimap.
 */
fun <K, V, R> SetMultimap<out K, V>.map(transform: (Pair<K, V>) -> R): List<R> = entries.map(transform)

/**
 * Returns a new [SetMultimap] with keys transformed by [transform]. If two original keys produce the same new key,
 * their value sets are merged.
 */
fun <K, V, R> SetMultimap<out K, V>.mapKeys(transform: (K) -> R): SetMultimap<R, V> =
    entries.map { (k, v) -> transform(k) to v }.toSetMultimap()

/**
 * Returns a [List] containing only the non-`null` results of applying the given [transform] function to each key-value
 * pair in the original multimap.
 */
fun <K, V, R> SetMultimap<out K, V>.mapNotNull(transform: (Pair<K, V>) -> R?): List<R> = entries.mapNotNull(transform)

/**
 * Returns a new [SetMultimap] with the same keys and values transformed by [transform]. If two original values under
 * the same key produce the same new value, the duplicate is silently discarded.
 */
fun <K, V, R> SetMultimap<out K, V>.mapValues(transform: (V) -> R): SetMultimap<K, R> =
    entries.map { (k, v) -> k to transform(v) }.toSetMultimap()

/**
 * Returns `true` if this multimap has no key-value pairs.
 */
fun <K, V> SetMultimap<out K, V>.none(): Boolean = entries.none()

/**
 * Returns `true` if no key-value pairs in this multimap match the given [predicate].
 */
fun <K, V> SetMultimap<out K, V>.none(predicate: (Pair<K, V>) -> Boolean): Boolean = entries.none(predicate)

/**
 * Returns a new [SetMultimap] containing all entries of the original multimap plus the given [pair]. If the pair
 * already exists, the result is equivalent to the original multimap.
 */
operator fun <K, V> SetMultimap<out K, V>.plus(pair: Pair<K, V>): SetMultimap<K, V> =
    (entries.toList() + pair).toSetMultimap()

/**
 * Returns a new [SetMultimap] containing all entries of the original multimap plus all entries of [other]. Duplicate
 * pairs are silently discarded.
 */
operator fun <K, V> SetMultimap<out K, V>.plus(other: SetMultimap<out K, V>): SetMultimap<K, V> =
    (entries.toList() + other.entries).toSetMultimap()

/**
 * Returns a new [SetMultimap] containing all entries of the original multimap plus all entries in [pairs]. Duplicate
 * pairs are silently discarded.
 */
operator fun <K, V> SetMultimap<out K, V>.plus(pairs: Iterable<Pair<K, V>>): SetMultimap<K, V> =
    (entries.toList() + pairs).toSetMultimap()

/**
 * Returns a new [SetMultimap] containing all entries of the original multimap except those with the given [key].
 */
operator fun <K, V> SetMultimap<out K, V>.minus(key: K): SetMultimap<K, V> =
    filterKeys { it != key }

/**
 * Returns a new [SetMultimap] containing all entries of the original multimap except those whose key is in [keys].
 */
operator fun <K, V> SetMultimap<out K, V>.minus(keys: Iterable<K>): SetMultimap<K, V> {
    val keySet = keys.toHashSet()
    return filterKeys { it !in keySet }
}

/**
 * Returns this multimap if not `null`, or an empty [SetMultimap] otherwise.
 */
fun <K, V> SetMultimap<K, V>?.orEmpty(): SetMultimap<K, V> = this ?: emptySetMultimap()

/**
 * Returns a [List] containing all the key-value pairs in this multimap.
 */
fun <K, V> SetMultimap<out K, V>.toList(): List<Pair<K, V>> = entries.toList()
