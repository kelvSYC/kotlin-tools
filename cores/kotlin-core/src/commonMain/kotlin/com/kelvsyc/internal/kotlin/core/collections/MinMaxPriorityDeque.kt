package com.kelvsyc.internal.kotlin.core.collections

import com.kelvsyc.kotlin.core.collections.PriorityDeque

internal class MinMaxPriorityDeque<T> private constructor(
    private val comparator: Comparator<in T>,
    private val heap: ArrayList<T>,
    @Suppress("UNUSED_PARAMETER") marker: Unit,
) : AbstractMutableCollection<T>(), PriorityDeque<T> {

    constructor(comparator: Comparator<in T>) : this(comparator, ArrayList(), Unit)

    internal constructor(comparator: Comparator<in T>, initial: Collection<T>) :
        this(comparator, ArrayList(initial), Unit) {
        for (i in (heap.size ushr 1) - 1 downTo 0) siftDown(i)
    }

    override val size: Int get() = heap.size
    override fun isEmpty(): Boolean = heap.isEmpty()

    private fun cmp(a: Int, b: Int): Int = comparator.compare(heap[a], heap[b])

    private fun swap(a: Int, b: Int) {
        val tmp = heap[a]; heap[a] = heap[b]; heap[b] = tmp
    }

    private fun isMinLevel(pos: Int): Boolean =
        (31 - (pos + 1).countLeadingZeroBits()) % 2 == 0

    override fun peekMin(): T? = if (heap.isEmpty()) null else heap[0]

    override fun peekMax(): T? = when (heap.size) {
        0 -> null
        1 -> heap[0]
        2 -> heap[1]
        else -> heap[if (cmp(1, 2) >= 0) 1 else 2]
    }

    override fun add(element: T): Boolean {
        heap.add(element)
        siftUp(heap.size - 1)
        return true
    }

    override fun pollMin(): T? {
        if (heap.isEmpty()) return null
        val min = heap[0]
        removeAtPos(0)
        return min
    }

    override fun pollMax(): T? = when (heap.size) {
        0 -> null
        1 -> pollMin()
        2 -> { val e = heap[1]; removeAtPos(1); e }
        else -> {
            val maxPos = if (cmp(1, 2) >= 0) 1 else 2
            val e = heap[maxPos]; removeAtPos(maxPos); e
        }
    }

    override fun remove(element: T): Boolean {
        val idx = heap.indexOf(element)
        if (idx < 0) return false
        removeAtPos(idx)
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

    override fun clear() { heap.clear() }

    override fun drainSorted(): Sequence<T> = sequence {
        while (heap.isNotEmpty()) {
            @Suppress("UNCHECKED_CAST")
            yield(pollMin() as T)
        }
    }

    override fun toSortedList(): List<T> {
        if (heap.isEmpty()) return emptyList()
        val copy = MinMaxPriorityDeque(comparator, ArrayList(heap), Unit)
        val result = ArrayList<T>(copy.size)
        while (copy.heap.isNotEmpty()) {
            @Suppress("UNCHECKED_CAST")
            result.add(copy.pollMin() as T)
        }
        return result
    }

    override fun iterator(): MutableIterator<T> = object : MutableIterator<T> {
        private var cursor = 0
        override fun hasNext(): Boolean = cursor < heap.size
        override fun next(): T {
            if (cursor >= heap.size) throw NoSuchElementException()
            return heap[cursor++]
        }
        override fun remove() {
            throw UnsupportedOperationException(
                "PriorityDeque iterator does not support remove(); use PriorityDeque.remove(element) instead."
            )
        }
    }

    private fun reheapify() {
        for (i in (heap.size ushr 1) - 1 downTo 0) siftDown(i)
    }

    private fun removeAtPos(pos: Int) {
        val lastIdx = heap.size - 1
        if (pos == lastIdx) { heap.removeAt(lastIdx); return }
        val moved = heap.removeAt(lastIdx)
        heap[pos] = moved
        siftDown(pos)
        @Suppress("UNCHECKED_CAST")
        if ((heap[pos] as Any?) === (moved as Any?)) siftUp(pos)
    }

    private fun siftUp(startPos: Int) {
        if (startPos == 0) return
        val parentPos = (startPos - 1) ushr 1
        if (isMinLevel(startPos)) {
            if (cmp(startPos, parentPos) > 0) { swap(startPos, parentPos); siftUpMax(parentPos) }
            else siftUpMin(startPos)
        } else {
            if (cmp(startPos, parentPos) < 0) { swap(startPos, parentPos); siftUpMin(parentPos) }
            else siftUpMax(startPos)
        }
    }

    private fun siftUpMin(pos: Int) {
        if (pos < 3) return
        val gp = (pos - 3) ushr 2
        if (cmp(pos, gp) < 0) { swap(pos, gp); siftUpMin(gp) }
    }

    private fun siftUpMax(pos: Int) {
        if (pos < 3) return
        val gp = (pos - 3) ushr 2
        if (cmp(pos, gp) > 0) { swap(pos, gp); siftUpMax(gp) }
    }

    private fun siftDown(pos: Int) {
        if (isMinLevel(pos)) siftDownMin(pos) else siftDownMax(pos)
    }

    private fun smallestDescendant(pos: Int): Int? {
        val left = 2 * pos + 1
        if (left >= heap.size) return null
        var best = left
        val right = left + 1
        if (right < heap.size && cmp(right, best) < 0) best = right
        val ll = 2 * left + 1; val lr = ll + 1
        if (ll < heap.size && cmp(ll, best) < 0) best = ll
        if (lr < heap.size && cmp(lr, best) < 0) best = lr
        if (right < heap.size) {
            val rl = 2 * right + 1; val rr = rl + 1
            if (rl < heap.size && cmp(rl, best) < 0) best = rl
            if (rr < heap.size && cmp(rr, best) < 0) best = rr
        }
        return best
    }

    private fun largestDescendant(pos: Int): Int? {
        val left = 2 * pos + 1
        if (left >= heap.size) return null
        var best = left
        val right = left + 1
        if (right < heap.size && cmp(right, best) > 0) best = right
        val ll = 2 * left + 1; val lr = ll + 1
        if (ll < heap.size && cmp(ll, best) > 0) best = ll
        if (lr < heap.size && cmp(lr, best) > 0) best = lr
        if (right < heap.size) {
            val rl = 2 * right + 1; val rr = rl + 1
            if (rl < heap.size && cmp(rl, best) > 0) best = rl
            if (rr < heap.size && cmp(rr, best) > 0) best = rr
        }
        return best
    }

    private fun siftDownMin(pos: Int) {
        val m = smallestDescendant(pos) ?: return
        if (m >= 4 * pos + 3) {
            if (cmp(m, pos) < 0) {
                swap(m, pos)
                val parentM = (m - 1) ushr 1
                if (cmp(m, parentM) > 0) swap(m, parentM)
                siftDownMin(m)
            }
        } else {
            if (cmp(m, pos) < 0) swap(m, pos)
        }
    }

    private fun siftDownMax(pos: Int) {
        val m = largestDescendant(pos) ?: return
        if (m >= 4 * pos + 3) {
            if (cmp(m, pos) > 0) {
                swap(m, pos)
                val parentM = (m - 1) ushr 1
                if (cmp(m, parentM) < 0) swap(m, parentM)
                siftDownMax(m)
            }
        } else {
            if (cmp(m, pos) > 0) swap(m, pos)
        }
    }
}
