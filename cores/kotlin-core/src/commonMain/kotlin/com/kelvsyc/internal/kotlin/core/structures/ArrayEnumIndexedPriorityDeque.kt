package com.kelvsyc.internal.kotlin.core.structures

import com.kelvsyc.kotlin.core.structures.IndexedPriorityDeque
import kotlin.enums.EnumEntries

@PublishedApi
internal class ArrayEnumIndexedPriorityDeque<E : Enum<E>, P>(
    private val comparator: Comparator<in P>,
    private val enumEntries: EnumEntries<E>,
) : IndexedPriorityDeque<E, P> {

    private val n = enumEntries.size
    private val heap = IntArray(n)
    private val positionOf = IntArray(n) { -1 }
    private val slotPriorities = arrayOfNulls<Any>(n)
    private var heapSize = 0

    override val size: Int get() = heapSize
    override fun isEmpty(): Boolean = heapSize == 0

    @Suppress("UNCHECKED_CAST")
    private fun priorityAt(slot: Int): P = slotPriorities[slot] as P

    private fun cmp(posA: Int, posB: Int): Int =
        comparator.compare(priorityAt(heap[posA]), priorityAt(heap[posB]))

    private fun swap(a: Int, b: Int) {
        val sa = heap[a]; val sb = heap[b]
        heap[a] = sb; heap[b] = sa
        positionOf[sa] = b; positionOf[sb] = a
    }

    private fun isMinLevel(pos: Int): Boolean =
        (31 - (pos + 1).countLeadingZeroBits()) % 2 == 0

    override fun peekMin(): E? {
        if (heapSize == 0) return null
        return enumEntries[heap[0]]
    }

    override fun peekMax(): E? = when (heapSize) {
        0 -> null
        1 -> enumEntries[heap[0]]
        2 -> enumEntries[heap[1]]
        else -> enumEntries[heap[if (cmp(1, 2) >= 0) 1 else 2]]
    }

    override fun contains(element: E): Boolean = positionOf[element.ordinal] >= 0

    override fun getPriority(element: E): P? {
        if (positionOf[element.ordinal] < 0) return null
        return priorityAt(element.ordinal)
    }

    override fun add(element: E, priority: P) {
        val slot = element.ordinal
        require(positionOf[slot] < 0) { "Element already in queue: $element" }
        slotPriorities[slot] = priority
        heap[heapSize] = slot
        positionOf[slot] = heapSize
        heapSize++
        siftUp(heapSize - 1)
    }

    override fun pollMin(): E? {
        if (heapSize == 0) return null
        val minSlot = heap[0]
        removeAtHeapPos(0)
        return enumEntries[minSlot]
    }

    override fun pollMax(): E? = when (heapSize) {
        0 -> null
        1 -> pollMin()
        2 -> { val slot = heap[1]; removeAtHeapPos(1); enumEntries[slot] }
        else -> {
            val maxPos = if (cmp(1, 2) >= 0) 1 else 2
            val slot = heap[maxPos]; removeAtHeapPos(maxPos); enumEntries[slot]
        }
    }

    override fun remove(element: E): Boolean {
        val pos = positionOf[element.ordinal]
        if (pos < 0) return false
        removeAtHeapPos(pos)
        return true
    }

    override fun decreaseKey(element: E, newPriority: P) {
        val slot = element.ordinal
        if (positionOf[slot] < 0) throw NoSuchElementException("Element not in queue: $element")
        require(comparator.compare(newPriority, priorityAt(slot)) < 0) {
            "decreaseKey requires newPriority < currentPriority"
        }
        slotPriorities[slot] = newPriority
        siftUp(positionOf[slot])
    }

    override fun increaseKey(element: E, newPriority: P) {
        val slot = element.ordinal
        if (positionOf[slot] < 0) throw NoSuchElementException("Element not in queue: $element")
        require(comparator.compare(newPriority, priorityAt(slot)) > 0) {
            "increaseKey requires newPriority > currentPriority"
        }
        slotPriorities[slot] = newPriority
        siftDown(positionOf[slot])
    }

    override fun updatePriority(element: E, newPriority: P) {
        val slot = element.ordinal
        if (positionOf[slot] < 0) throw NoSuchElementException("Element not in queue: $element")
        val c = comparator.compare(newPriority, priorityAt(slot))
        if (c == 0) return
        slotPriorities[slot] = newPriority
        if (c < 0) siftUp(positionOf[slot]) else siftDown(positionOf[slot])
    }

    private fun removeAtHeapPos(pos: Int) {
        val slot = heap[pos]
        slotPriorities[slot] = null
        positionOf[slot] = -1
        val lastPos = heapSize - 1
        heapSize--
        if (pos == lastPos) return
        val movedSlot = heap[lastPos]
        heap[pos] = movedSlot
        positionOf[movedSlot] = pos
        siftDown(pos)
        if (heap[pos] == movedSlot) siftUp(pos)
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
        if (left >= heapSize) return null
        var best = left
        val right = left + 1
        if (right < heapSize && cmp(right, best) < 0) best = right
        val ll = 2 * left + 1; val lr = ll + 1
        if (ll < heapSize && cmp(ll, best) < 0) best = ll
        if (lr < heapSize && cmp(lr, best) < 0) best = lr
        if (right < heapSize) {
            val rl = 2 * right + 1; val rr = rl + 1
            if (rl < heapSize && cmp(rl, best) < 0) best = rl
            if (rr < heapSize && cmp(rr, best) < 0) best = rr
        }
        return best
    }

    private fun largestDescendant(pos: Int): Int? {
        val left = 2 * pos + 1
        if (left >= heapSize) return null
        var best = left
        val right = left + 1
        if (right < heapSize && cmp(right, best) > 0) best = right
        val ll = 2 * left + 1; val lr = ll + 1
        if (ll < heapSize && cmp(ll, best) > 0) best = ll
        if (lr < heapSize && cmp(lr, best) > 0) best = lr
        if (right < heapSize) {
            val rl = 2 * right + 1; val rr = rl + 1
            if (rl < heapSize && cmp(rl, best) > 0) best = rl
            if (rr < heapSize && cmp(rr, best) > 0) best = rr
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
