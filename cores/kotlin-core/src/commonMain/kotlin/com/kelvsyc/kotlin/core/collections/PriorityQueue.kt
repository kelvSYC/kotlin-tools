package com.kelvsyc.kotlin.core.collections

/**
 * An unbounded priority queue backed by a binary min-heap. The head of the queue is the least element with respect to
 * the supplied [Comparator].
 *
 * This is a Kotlin Multiplatform type with no JVM interoperability requirement; it does not implement
 * `java.util.Queue` or any equivalent.
 *
 * ### Nullability
 *
 * The type parameter [T] is unbounded. When [T] is non-nullable, [peek] and [poll] return `null` only when the queue
 * is empty. When [T] is nullable, a `null` return is ambiguous: it may indicate either an empty queue or a `null`
 * head. Callers in that case should check [isEmpty] before interpreting the result. This mirrors the standard
 * library's treatment of `firstOrNull` and similar functions on collections of nullable elements.
 *
 * ### Equality vs. comparator order
 *
 * [contains] and [remove] use structural equality ([equals]), not the [Comparator]. Two elements that compare equal
 * by the comparator are not necessarily equal by [equals], and vice versa.
 *
 * ### Element mutation
 *
 * Mutating an element in a way that changes its ordering relative to other elements while it resides in the queue
 * silently corrupts the heap invariant. As with any ordered collection, callers are responsible for not mutating
 * elements in such a way.
 *
 * ### Iteration order
 *
 * [iterator] visits elements in an unspecified order — specifically, the underlying heap-array order, which is not
 * sorted. For sorted consumption, use [drainSorted] or [toSortedList].
 *
 * ### Thread safety
 *
 * This class is not thread-safe.
 */
class PriorityQueue<T> private constructor(
    private val comparator: Comparator<in T>,
    private val heap: ArrayList<T>,
    @Suppress("UNUSED_PARAMETER") marker: Unit,
) : AbstractMutableCollection<T>() {

    /**
     * Creates an empty priority queue ordered by the given [comparator].
     */
    constructor(comparator: Comparator<in T>) : this(comparator, ArrayList(), Unit)

    internal constructor(comparator: Comparator<in T>, initial: Collection<T>) :
        this(comparator, ArrayList(initial), Unit) {
        // Floyd's bottom-up heap construction: O(n).
        for (i in (heap.size ushr 1) - 1 downTo 0) {
            siftDown(i)
        }
    }

    override val size: Int get() = heap.size

    override fun isEmpty(): Boolean = heap.isEmpty()

    /**
     * Inserts [element] into this queue. Always returns `true`.
     */
    override fun add(element: T): Boolean {
        heap.add(element)
        siftUp(heap.size - 1)
        return true
    }

    /**
     * Inserts [element] into this queue. Equivalent to [add]; provided for parity with `java.util.Queue`-style usage.
     */
    fun offer(element: T): Boolean = add(element)

    /**
     * Returns the head of this queue without removing it, or `null` if this queue is empty. See the class-level
     * note on nullability when [T] is itself nullable.
     */
    fun peek(): T? = if (heap.isEmpty()) null else heap[0]

    /**
     * Removes and returns the head of this queue, or `null` if this queue is empty. See the class-level note on
     * nullability when [T] is itself nullable.
     */
    fun poll(): T? {
        if (heap.isEmpty()) return null
        val head = heap[0]
        val lastIdx = heap.size - 1
        val last = heap.removeAt(lastIdx)
        if (lastIdx > 0) {
            heap[0] = last
            siftDown(0)
        }
        return head
    }

    /**
     * Inserts [element] and then removes the head, returning whichever is least. Equivalent to [add] followed by
     * [poll], but performs at most one sift operation.
     */
    fun addOrPoll(element: T): T {
        if (heap.isEmpty() || comparator.compare(element, heap[0]) <= 0) {
            return element
        }
        val head = heap[0]
        heap[0] = element
        siftDown(0)
        return head
    }

    /**
     * Removes the head and inserts [element] in a single operation, returning the previous head, or `null` if the
     * queue was empty (in which case [element] is still inserted).
     */
    fun pollOrAdd(element: T): T? {
        if (heap.isEmpty()) {
            heap.add(element)
            return null
        }
        val head = heap[0]
        heap[0] = element
        siftDown(0)
        return head
    }

    override fun clear() {
        heap.clear()
    }

    /**
     * Returns an iterator over the elements of this queue in unspecified order. The returned iterator does not
     * support [MutableIterator.remove].
     */
    override fun iterator(): MutableIterator<T> = object : MutableIterator<T> {
        private var cursor = 0
        override fun hasNext(): Boolean = cursor < heap.size
        override fun next(): T {
            if (cursor >= heap.size) throw NoSuchElementException()
            return heap[cursor++]
        }
        override fun remove() {
            throw UnsupportedOperationException(
                "PriorityQueue iterator does not support remove(); use PriorityQueue.remove(element) instead."
            )
        }
    }

    /**
     * Removes a single occurrence of [element] from this queue using [equals], and returns `true` if the queue was
     * modified. Runs in O(n) due to the linear search for the matching element.
     */
    override fun remove(element: T): Boolean {
        val idx = heap.indexOf(element)
        if (idx < 0) return false
        removeAt(idx)
        return true
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        if (elements.isEmpty() || heap.isEmpty()) return false
        val originalSize = heap.size
        heap.removeAll(elements.toSet())
        if (heap.size == originalSize) return false
        reheapify()
        return true
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        if (heap.isEmpty()) return false
        val originalSize = heap.size
        heap.retainAll(elements.toSet())
        if (heap.size == originalSize) return false
        reheapify()
        return true
    }

    private fun reheapify() {
        for (i in (heap.size ushr 1) - 1 downTo 0) {
            siftDown(i)
        }
    }

    private fun removeAt(idx: Int) {
        val lastIdx = heap.size - 1
        if (idx == lastIdx) {
            heap.removeAt(lastIdx)
            return
        }
        val moved = heap.removeAt(lastIdx)
        heap[idx] = moved
        siftDown(idx)
        // siftDown is a no-op if `moved` is already in place; in that case the slot might still need siftUp.
        @Suppress("UNCHECKED_CAST")
        if ((heap[idx] as Any?) === (moved as Any?)) {
            siftUp(idx)
        }
    }

    /**
     * Returns a [Sequence] that drains this queue in sorted order (ascending by the comparator). Each call to the
     * sequence's iterator removes elements from this queue. The sequence is single-use in the sense that consuming
     * it empties the queue.
     */
    fun drainSorted(): Sequence<T> = sequence {
        while (heap.isNotEmpty()) {
            @Suppress("UNCHECKED_CAST")
            yield(poll() as T)
        }
    }

    /**
     * Returns a new [List] containing this queue's elements in sorted order (ascending by the comparator). This
     * queue is not modified.
     */
    fun toSortedList(): List<T> {
        if (heap.isEmpty()) return emptyList()
        val copy = PriorityQueue(comparator, ArrayList(heap), Unit)
        val result = ArrayList<T>(copy.size)
        while (copy.heap.isNotEmpty()) {
            @Suppress("UNCHECKED_CAST")
            result.add(copy.poll() as T)
        }
        return result
    }

    private fun siftUp(start: Int) {
        var i = start
        val item = heap[i]
        while (i > 0) {
            val parentIdx = (i - 1) ushr 1
            val parent = heap[parentIdx]
            if (comparator.compare(item, parent) >= 0) break
            heap[i] = parent
            i = parentIdx
        }
        heap[i] = item
    }

    private fun siftDown(start: Int) {
        var i = start
        val item = heap[i]
        val half = heap.size ushr 1
        while (i < half) {
            var childIdx = 2 * i + 1
            val rightIdx = childIdx + 1
            if (rightIdx < heap.size && comparator.compare(heap[rightIdx], heap[childIdx]) < 0) {
                childIdx = rightIdx
            }
            if (comparator.compare(item, heap[childIdx]) <= 0) break
            heap[i] = heap[childIdx]
            i = childIdx
        }
        heap[i] = item
    }
}
