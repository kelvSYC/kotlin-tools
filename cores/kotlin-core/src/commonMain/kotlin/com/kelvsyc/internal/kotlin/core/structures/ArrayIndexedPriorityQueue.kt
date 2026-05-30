package com.kelvsyc.internal.kotlin.core.structures

import com.kelvsyc.kotlin.core.structures.IndexedPriorityQueue

@PublishedApi
internal class ArrayIndexedPriorityQueue<T, P>(
    private val comparator: Comparator<in P>,
    universe: Iterable<T>,
) : IndexedPriorityQueue<T, P> {

    // Built once from universe at construction; never mutated afterwards.
    private val elementToSlot: HashMap<T, Int>
    // slotToElement[slot] = T element (set at construction; not cleared on remove).
    private val slotToElement: Array<Any?>
    // heap[heapPos] = slot index; only indices 0..<heapSize are valid.
    private val heap: IntArray
    // positionOf[slot] = heap position; -1 = not currently in queue.
    private val positionOf: IntArray
    // slotPriorities[slot] = P; null when the slot is not in the queue.
    private val slotPriorities: Array<Any?>
    private var heapSize = 0

    init {
        val map = LinkedHashMap<T, Int>()
        for (element in universe) {
            map.getOrPut(element) { map.size }
        }
        elementToSlot = HashMap(map)
        val n = elementToSlot.size
        slotToElement = arrayOfNulls(n)
        for ((e, slot) in elementToSlot) slotToElement[slot] = e
        heap = IntArray(n)
        positionOf = IntArray(n) { -1 }
        slotPriorities = arrayOfNulls(n)
    }

    override val size: Int get() = heapSize
    override fun isEmpty(): Boolean = heapSize == 0

    override fun peekMin(): T? {
        if (heapSize == 0) return null
        @Suppress("UNCHECKED_CAST")
        return slotToElement[heap[0]] as T
    }

    override fun contains(element: T): Boolean {
        val slot = elementToSlot[element] ?: return false
        return positionOf[slot] >= 0
    }

    override fun getPriority(element: T): P? {
        val slot = elementToSlot[element] ?: return null
        if (positionOf[slot] < 0) return null
        @Suppress("UNCHECKED_CAST")
        return slotPriorities[slot] as P
    }

    override fun add(element: T, priority: P) {
        val slot = elementToSlot[element]
            ?: throw IllegalArgumentException("Element not in universe: $element")
        require(positionOf[slot] < 0) { "Element already in queue: $element" }
        slotPriorities[slot] = priority
        heap[heapSize] = slot
        heapSize++
        siftUp(heapSize - 1)
    }

    override fun pollMin(): T? {
        if (heapSize == 0) return null
        val minSlot = heap[0]
        @Suppress("UNCHECKED_CAST")
        val element = slotToElement[minSlot] as T
        removeAtHeapPos(0)
        return element
    }

    override fun remove(element: T): Boolean {
        val slot = elementToSlot[element] ?: return false
        val pos = positionOf[slot]
        if (pos < 0) return false
        removeAtHeapPos(pos)
        return true
    }

    override fun decreaseKey(element: T, newPriority: P) {
        val slot = elementToSlot[element]?.also { if (positionOf[it] < 0) throw NoSuchElementException("Element not in queue: $element") }
            ?: throw NoSuchElementException("Element not in queue: $element")
        @Suppress("UNCHECKED_CAST")
        val current = slotPriorities[slot] as P
        require(comparator.compare(newPriority, current) < 0) {
            "decreaseKey requires newPriority < currentPriority"
        }
        slotPriorities[slot] = newPriority
        siftUp(positionOf[slot])
    }

    override fun increaseKey(element: T, newPriority: P) {
        val slot = elementToSlot[element]?.also { if (positionOf[it] < 0) throw NoSuchElementException("Element not in queue: $element") }
            ?: throw NoSuchElementException("Element not in queue: $element")
        @Suppress("UNCHECKED_CAST")
        val current = slotPriorities[slot] as P
        require(comparator.compare(newPriority, current) > 0) {
            "increaseKey requires newPriority > currentPriority"
        }
        slotPriorities[slot] = newPriority
        siftDown(positionOf[slot])
    }

    override fun updatePriority(element: T, newPriority: P) {
        val slot = elementToSlot[element]?.also { if (positionOf[it] < 0) throw NoSuchElementException("Element not in queue: $element") }
            ?: throw NoSuchElementException("Element not in queue: $element")
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
