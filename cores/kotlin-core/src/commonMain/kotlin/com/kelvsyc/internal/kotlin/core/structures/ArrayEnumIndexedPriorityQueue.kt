package com.kelvsyc.internal.kotlin.core.structures

import com.kelvsyc.kotlin.core.structures.IndexedPriorityQueue
import kotlin.enums.EnumEntries

@PublishedApi
internal class ArrayEnumIndexedPriorityQueue<E : Enum<E>, P>(
    private val comparator: Comparator<in P>,
    private val enumEntries: EnumEntries<E>,
) : IndexedPriorityQueue<E, P> {

    // Slot index = element.ordinal; no T→Int map needed.
    private val n = enumEntries.size
    // heap[heapPos] = slot (ordinal); only indices 0..<heapSize are valid.
    private val heap = IntArray(n)
    // positionOf[slot] = heap position; -1 = not currently in queue.
    private val positionOf = IntArray(n) { -1 }
    // slotPriorities[slot] = P; null when slot is not in the queue.
    private val slotPriorities = arrayOfNulls<Any>(n)
    private var heapSize = 0

    override val size: Int get() = heapSize
    override fun isEmpty(): Boolean = heapSize == 0

    override fun peekMin(): E? {
        if (heapSize == 0) return null
        return enumEntries[heap[0]]
    }

    override fun contains(element: E): Boolean = positionOf[element.ordinal] >= 0

    override fun getPriority(element: E): P? {
        if (positionOf[element.ordinal] < 0) return null
        @Suppress("UNCHECKED_CAST")
        return slotPriorities[element.ordinal] as P
    }

    override fun add(element: E, priority: P) {
        val slot = element.ordinal
        require(positionOf[slot] < 0) { "Element already in queue: $element" }
        slotPriorities[slot] = priority
        heap[heapSize] = slot
        heapSize++
        siftUp(heapSize - 1)
    }

    override fun pollMin(): E? {
        if (heapSize == 0) return null
        val minSlot = heap[0]
        removeAtHeapPos(0)
        return enumEntries[minSlot]
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
        @Suppress("UNCHECKED_CAST")
        val current = slotPriorities[slot] as P
        require(comparator.compare(newPriority, current) < 0) {
            "decreaseKey requires newPriority < currentPriority"
        }
        slotPriorities[slot] = newPriority
        siftUp(positionOf[slot])
    }

    override fun increaseKey(element: E, newPriority: P) {
        val slot = element.ordinal
        if (positionOf[slot] < 0) throw NoSuchElementException("Element not in queue: $element")
        @Suppress("UNCHECKED_CAST")
        val current = slotPriorities[slot] as P
        require(comparator.compare(newPriority, current) > 0) {
            "increaseKey requires newPriority > currentPriority"
        }
        slotPriorities[slot] = newPriority
        siftDown(positionOf[slot])
    }

    override fun updatePriority(element: E, newPriority: P) {
        val slot = element.ordinal
        if (positionOf[slot] < 0) throw NoSuchElementException("Element not in queue: $element")
        @Suppress("UNCHECKED_CAST")
        val current = slotPriorities[slot] as P
        val cmp = comparator.compare(newPriority, current)
        if (cmp == 0) return
        slotPriorities[slot] = newPriority
        if (cmp < 0) siftUp(positionOf[slot]) else siftDown(positionOf[slot])
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
        val slot = heap[startPos]
        @Suppress("UNCHECKED_CAST")
        val priority = slotPriorities[slot] as P
        var i = startPos
        while (i > 0) {
            val parentPos = (i - 1) ushr 1
            val parentSlot = heap[parentPos]
            @Suppress("UNCHECKED_CAST")
            val parentPriority = slotPriorities[parentSlot] as P
            if (comparator.compare(priority, parentPriority) >= 0) break
            heap[i] = parentSlot
            positionOf[parentSlot] = i
            i = parentPos
        }
        heap[i] = slot
        positionOf[slot] = i
    }

    private fun siftDown(startPos: Int) {
        val slot = heap[startPos]
        @Suppress("UNCHECKED_CAST")
        val priority = slotPriorities[slot] as P
        var i = startPos
        val half = heapSize ushr 1
        while (i < half) {
            var childPos = 2 * i + 1
            val rightPos = childPos + 1
            if (rightPos < heapSize) {
                @Suppress("UNCHECKED_CAST")
                val leftPriority = slotPriorities[heap[childPos]] as P
                @Suppress("UNCHECKED_CAST")
                val rightPriority = slotPriorities[heap[rightPos]] as P
                if (comparator.compare(rightPriority, leftPriority) < 0) childPos = rightPos
            }
            val childSlot = heap[childPos]
            @Suppress("UNCHECKED_CAST")
            val childPriority = slotPriorities[childSlot] as P
            if (comparator.compare(priority, childPriority) <= 0) break
            heap[i] = childSlot
            positionOf[childSlot] = i
            i = childPos
        }
        heap[i] = slot
        positionOf[slot] = i
    }
}
