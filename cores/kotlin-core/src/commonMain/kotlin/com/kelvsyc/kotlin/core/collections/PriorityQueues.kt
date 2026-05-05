package com.kelvsyc.kotlin.core.collections

/**
 * Returns an empty [PriorityQueue] ordered by the given [comparator].
 */
fun <T> priorityQueueOf(comparator: Comparator<in T>): PriorityQueue<T> = PriorityQueue(comparator)

/**
 * Returns a [PriorityQueue] containing the given [elements], ordered by the given [comparator]. Constructed in O(n)
 * via bottom-up heapification.
 */
fun <T> priorityQueueOf(comparator: Comparator<in T>, vararg elements: T): PriorityQueue<T> =
    PriorityQueue(comparator, elements.asList())

/**
 * Returns an empty min-priority queue ordered by the natural ordering of [T].
 */
fun <T : Comparable<T>> minPriorityQueueOf(): PriorityQueue<T> = PriorityQueue(naturalOrder())

/**
 * Returns a min-priority queue containing the given [elements], ordered by the natural ordering of [T]. Constructed
 * in O(n) via bottom-up heapification.
 */
fun <T : Comparable<T>> minPriorityQueueOf(vararg elements: T): PriorityQueue<T> =
    PriorityQueue(naturalOrder(), elements.asList())

/**
 * Returns an empty max-priority queue ordered by the reverse of the natural ordering of [T].
 */
fun <T : Comparable<T>> maxPriorityQueueOf(): PriorityQueue<T> = PriorityQueue(reverseOrder())

/**
 * Returns a max-priority queue containing the given [elements], ordered by the reverse of the natural ordering of
 * [T]. Constructed in O(n) via bottom-up heapification.
 */
fun <T : Comparable<T>> maxPriorityQueueOf(vararg elements: T): PriorityQueue<T> =
    PriorityQueue(reverseOrder(), elements.asList())

/**
 * Returns a new [PriorityQueue] containing all elements of this [Iterable], ordered by the given [comparator].
 * Constructed in O(n) via bottom-up heapification when the source is a [Collection].
 */
fun <T> Iterable<T>.toPriorityQueue(comparator: Comparator<in T>): PriorityQueue<T> {
    val collection = if (this is Collection<T>) this else this.toList()
    return PriorityQueue(comparator, collection)
}

/**
 * Returns a new [PriorityQueue] containing all elements of this [Sequence], ordered by the given [comparator].
 */
fun <T> Sequence<T>.toPriorityQueue(comparator: Comparator<in T>): PriorityQueue<T> =
    PriorityQueue(comparator, toList())

/**
 * Returns the [k] least elements of this [Iterable] in ascending order according to the given [comparator]. If this
 * [Iterable] contains fewer than [k] elements, all of them are returned. Runs in O(n log k).
 */
fun <T> Iterable<T>.nSmallest(k: Int, comparator: Comparator<in T>): List<T> {
    require(k >= 0) { "k must be non-negative, was $k" }
    if (k == 0) return emptyList()
    val maxHeap = PriorityQueue<T>(comparator.reversed())
    for (e in this) {
        if (maxHeap.size < k) {
            maxHeap.add(e)
        } else {
            // addOrPoll on the max-heap pops the larger of (e, current max). If e >= max, e is returned and
            // dropped; otherwise the previous max is dropped and e takes its place.
            maxHeap.addOrPoll(e)
        }
    }
    // The max-heap drains in descending order by the original comparator; reverse for ascending.
    return maxHeap.toSortedList().asReversed()
}

/**
 * Returns the [k] greatest elements of this [Iterable] in descending order according to the given [comparator]. If
 * this [Iterable] contains fewer than [k] elements, all of them are returned. Runs in O(n log k).
 */
fun <T> Iterable<T>.nLargest(k: Int, comparator: Comparator<in T>): List<T> =
    nSmallest(k, comparator.reversed())


/**
 * Returns a [Sequence] that lazily merges the given already-sorted [sources] into a single sorted sequence,
 * according to the given [comparator]. Each source must be sorted ascending by the same [comparator]; if any source
 * is unsorted, the output is also unsorted.
 *
 * The merge runs in O(total · log k) time, where _total_ is the combined length of all sources and _k_ is the
 * number of non-empty sources. The output is produced lazily, so consumers may short-circuit (e.g., via [Sequence.take]).
 *
 * Empty sources are silently skipped.
 */
fun <T> mergeSorted(
    sources: Iterable<Iterable<T>>,
    comparator: Comparator<in T>,
): Sequence<T> = sequence {
    val entryComparator = Comparator<MergeEntry<T>> { a, b -> comparator.compare(a.head, b.head) }
    val heap = priorityQueueOf<MergeEntry<T>>(entryComparator)
    for (source in sources) {
        val iter = source.iterator()
        if (iter.hasNext()) heap.add(MergeEntry(iter.next(), iter))
    }
    while (heap.isNotEmpty()) {
        val entry = heap.poll()!!
        yield(entry.head)
        if (entry.iter.hasNext()) {
            heap.add(MergeEntry(entry.iter.next(), entry.iter))
        }
    }
}

/**
 * Vararg convenience for [mergeSorted]: lazily merges the given already-sorted [sources] into a single sorted
 * sequence, according to the given [comparator].
 */
fun <T> mergeSorted(
    comparator: Comparator<in T>,
    vararg sources: Iterable<T>,
): Sequence<T> = mergeSorted(sources.asList(), comparator)

private class MergeEntry<T>(val head: T, val iter: Iterator<T>)
