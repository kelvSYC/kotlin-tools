package com.kelvsyc.internal.kotlin.core.structures

import com.kelvsyc.kotlin.core.structures.IndexedPriorityDeque

@PublishedApi
internal class ArrayIndexedPriorityDeque<T, P>(
    private val comparator: Comparator<in P>,
    universe: Iterable<T>,
) : IndexedPriorityDeque<T, P> {

    private val elementToSlot: HashMap<T, Int>
    private val slotToElement: Array<Any?>
    private val heap: IntArray
    private val positionOf: IntArray
    private val slotPriorities: Array<Any?>
    private var heapSize = 0

    init {
        val map = LinkedHashMap<T, Int>()
        for (element in universe) map.getOrPut(element) { map.size }
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

    @Suppress("UNCHECKED_CAST")
    private fun elementAt(slot: Int): T = slotToElement[slot] as T

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

    override fun peekMin(): T? {
        if (heapSize == 0) return null
        return elementAt(heap[0])
    }

    override fun peekMax(): T? = when (heapSize) {
        0 -> null
        1 -> elementAt(heap[0])
        2 -> elementAt(heap[1])
        else -> elementAt(heap[if (cmp(1, 2) >= 0) 1 else 2])
    }

    override fun contains(element: T): Boolean {
        val slot = elementToSlot[element] ?: return false
        return positionOf[slot] >= 0
    }

    override fun getPriority(element: T): P? {
        val slot = elementToSlot[element] ?: return null
        if (positionOf[slot] < 0) return null
        return priorityAt(slot)
    }

    override fun add(element: T, priority: P) {
        val slot = elementToSlot[element]
            ?: throw IllegalArgumentException("Element not in universe: $element")
        require(positionOf[slot] < 0) { "Element already in queue: $element" }
        slotPriorities[slot] = priority
        heap[heapSize] = slot
        positionOf[slot] = heapSize
        heapSize++
        siftUp(heapSize - 1)
    }

    override fun pollMin(): T? {
        if (heapSize == 0) return null
        val minSlot = heap[0]
        val element = elementAt(minSlot)
        removeAtHeapPos(0)
        return element
    }

    override fun pollMax(): T? = when (heapSize) {
        0 -> null
        1 -> pollMin()
        2 -> { val slot = heap[1]; val e = elementAt(slot); removeAtHeapPos(1); e }
        else -> {
            val maxPos = if (cmp(1, 2) >= 0) 1 else 2
            val e = elementAt(heap[maxPos]); removeAtHeapPos(maxPos); e
        }
    }

    override fun remove(element: T): Boolean {
        val slot = elementToSlot[element] ?: return false
        val pos = positionOf[slot]
        if (pos < 0) return false
        removeAtHeapPos(pos)
        return true
    }

    override fun decreaseKey(element: T, newPriority: P) {
        val slot = elementToSlot[element]?.also {
            if (positionOf[it] < 0) throw NoSuchElementException("Element not in queue: $element")
        } ?: throw NoSuchElementException("Element not in queue: $element")
        require(comparator.compare(newPriority, priorityAt(slot)) < 0) {
            "decreaseKey requires newPriority < currentPriority"
        }
        slotPriorities[slot] = newPriority
        siftUp(positionOf[slot])
    }

    override fun increaseKey(element: T, newPriority: P) {
        val slot = elementToSlot[element]?.also {
            if (positionOf[it] < 0) throw NoSuchElementException("Element not in queue: $element")
        } ?: throw NoSuchElementException("Element not in queue: $element")
        require(comparator.compare(newPriority, priorityAt(slot)) > 0) {
            "increaseKey requires newPriority > currentPriority"
        }
        slotPriorities[slot] = newPriority
        siftDown(positionOf[slot])
    }

    override fun updatePriority(element: T, newPriority: P) {
        val slot = elementToSlot[element]?.also {
            if (positionOf[it] < 0) throw NoSuchElementException("Element not in queue: $element")
        } ?: throw NoSuchElementException("Element not in queue: $element")
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
