package com.kelvsyc.internal.kotlin.core.structures

import com.kelvsyc.kotlin.core.structures.IndexedPriorityQueue

@PublishedApi
internal class HashIndexedPriorityQueue<T, P>(
    private val comparator: Comparator<in P>,
) : IndexedPriorityQueue<T, P> {

    // Maps each in-queue element to its slot index. Canonical membership check.
    private val elementToSlot = HashMap<T, Int>()
    // Slot → T (Any? to handle nullable T; slots are cleared to null when freed).
    private val slotToElement = ArrayList<Any?>()
    // Slot → P (Any? to handle nullable P; cleared to null when slot is freed).
    private val slotPriorities = ArrayList<Any?>()
    // heap[heapPos] = slot index.
    private val heap = ArrayList<Int>()
    // positionOf[slot] = heap position; -1 when the slot is not in the heap.
    private val positionOf = ArrayList<Int>()
    // Recycled slot indices available for reuse after remove/pollMin.
    private val freeSlots = ArrayDeque<Int>()
    // Next fresh slot index when freeSlots is empty.
    private var nextSlot = 0

    override val size: Int get() = heap.size
    override fun isEmpty(): Boolean = heap.isEmpty()

    override fun peekMin(): T? {
        if (heap.isEmpty()) return null
        @Suppress("UNCHECKED_CAST")
        return slotToElement[heap[0]] as T
    }

    override fun contains(element: T): Boolean = elementToSlot.containsKey(element)

    override fun getPriority(element: T): P? {
        val slot = elementToSlot[element] ?: return null
        @Suppress("UNCHECKED_CAST")
        return slotPriorities[slot] as P
    }

    override fun add(element: T, priority: P) {
        require(!elementToSlot.containsKey(element)) { "Element already in queue: $element" }
        val slot: Int
        if (freeSlots.isNotEmpty()) {
            slot = freeSlots.removeFirst()
            slotToElement[slot] = element
            slotPriorities[slot] = priority
            // positionOf[slot] is -1 from when it was freed; siftUp will set the real position.
        } else {
            slot = nextSlot++
            slotToElement.add(element)
            slotPriorities.add(priority)
            positionOf.add(-1) // placeholder; siftUp sets the real position
        }
        elementToSlot[element] = slot
        heap.add(slot)
        siftUp(heap.size - 1)
    }

    override fun pollMin(): T? {
        if (heap.isEmpty()) return null
        val minSlot = heap[0]
        @Suppress("UNCHECKED_CAST")
        val element = slotToElement[minSlot] as T
        removeAtHeapPos(0)
        return element
    }

    override fun remove(element: T): Boolean {
        val slot = elementToSlot[element] ?: return false
        removeAtHeapPos(positionOf[slot])
        return true
    }

    override fun decreaseKey(element: T, newPriority: P) {
        val slot = elementToSlot[element]
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
        val slot = elementToSlot[element]
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
        val slot = elementToSlot[element]
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
        @Suppress("UNCHECKED_CAST")
        elementToSlot.remove(slotToElement[slot] as T)
        slotToElement[slot] = null
        slotPriorities[slot] = null
        positionOf[slot] = -1
        freeSlots.add(slot)

        val lastPos = heap.size - 1
        if (pos == lastPos) {
            heap.removeAt(lastPos)
            return
        }

        val movedSlot = heap.removeAt(lastPos)
        heap[pos] = movedSlot
        positionOf[movedSlot] = pos

        siftDown(pos)
        // If siftDown left movedSlot in place, try siftUp.
        if (heap[pos] == movedSlot) siftUp(pos)
    }

    // Extract-and-bubble up: shift parents down until the slot reaches its correct position.
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

    // Extract-and-bubble down: shift the smaller child up until the slot reaches its position.
    private fun siftDown(startPos: Int) {
        val slot = heap[startPos]
        @Suppress("UNCHECKED_CAST")
        val priority = slotPriorities[slot] as P
        var i = startPos
        val half = heap.size ushr 1
        while (i < half) {
            var childPos = 2 * i + 1
            val rightPos = childPos + 1
            if (rightPos < heap.size) {
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
