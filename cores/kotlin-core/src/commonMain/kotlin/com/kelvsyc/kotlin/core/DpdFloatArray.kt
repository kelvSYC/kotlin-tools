package com.kelvsyc.kotlin.core

/**
 * An array of [DpdFloat] values backed by an [IntArray], storing each element as its raw 32-bit pattern.
 *
 * ## Why not `Array<DpdFloat>`?
 *
 * [DpdFloat] is a value class, so it is represented as a plain [Int] in non-generic Kotlin code with no boxing
 * overhead. However, `Array<DpdFloat>` is a generic array, which forces boxing: each element becomes a heap-allocated
 * wrapper object on the JVM, costing roughly 4–8 bytes for the object reference plus ~16 bytes of object overhead.
 * `DpdFloatArray` avoids this entirely — its backing `IntArray` is a contiguous block of 4-byte slots with no
 * per-element allocation, giving a roughly 5× reduction in memory footprint and significantly better cache locality.
 *
 * ## Tradeoffs
 *
 * - `DpdFloatArray` does not implement `Iterable<DpdFloat>` or `Collection<DpdFloat>`. Use [asList] to obtain a
 *   live `List<DpdFloat>` view when collection interop is needed; element access through the list still boxes.
 * - [sort] uses a temporary boxed `Array<DpdFloat>` and [DpdFloat.comparator], so sorting incurs temporary boxing.
 * - A vararg factory cannot be provided: Kotlin prohibits value class types as vararg parameter types. Use the
 *   initializer constructor or [IntArray.toDpdFloatArray] instead.
 */
class DpdFloatArray(val size: Int) {
    private val storage = IntArray(size)

    constructor(size: Int, init: (Int) -> DpdFloat) : this(size) {
        for (i in 0 until size) storage[i] = init(i).bits
    }

    constructor(source: IntArray) : this(source.size) {
        source.copyInto(storage)
    }

    operator fun get(index: Int): DpdFloat = DpdFloat(storage[index])

    operator fun set(index: Int, value: DpdFloat) {
        storage[index] = value.bits
    }

    val indices: IntRange get() = 0 until size
    val lastIndex: Int get() = size - 1

    fun isEmpty(): Boolean = size == 0
    fun isNotEmpty(): Boolean = !isEmpty()

    fun contains(element: DpdFloat): Boolean = storage.contains(element.bits)
    fun indexOf(element: DpdFloat): Int = storage.indexOf(element.bits)
    fun lastIndexOf(element: DpdFloat): Int = storage.lastIndexOf(element.bits)

    operator fun iterator(): DpdFloatIterator = object : DpdFloatIterator() {
        private var index = 0
        override fun hasNext(): Boolean = index < size
        override fun nextDpdFloat(): DpdFloat {
            if (index >= size) throw NoSuchElementException(index.toString())
            return get(index++)
        }
    }

    fun fill(element: DpdFloat, fromIndex: Int = 0, toIndex: Int = size) {
        storage.fill(element.bits, fromIndex, toIndex)
    }

    fun copyOf(): DpdFloatArray = DpdFloatArray(storage.copyOf())
    fun copyOf(newSize: Int): DpdFloatArray = DpdFloatArray(storage.copyOf(newSize))
    fun copyOfRange(fromIndex: Int, toIndex: Int): DpdFloatArray = DpdFloatArray(storage.copyOfRange(fromIndex, toIndex))

    fun copyInto(destination: DpdFloatArray, destinationOffset: Int = 0, startIndex: Int = 0, endIndex: Int = size): DpdFloatArray {
        storage.copyInto(destination.storage, destinationOffset, startIndex, endIndex)
        return destination
    }

    fun toIntArray(): IntArray = storage.copyOf()

    fun sort() {
        val temp = Array(size) { DpdFloat(storage[it]) }
        temp.sortWith(DpdFloat.comparator)
        for (i in temp.indices) storage[i] = temp[i].bits
    }

    fun sort(fromIndex: Int, toIndex: Int) {
        val temp = Array(toIndex - fromIndex) { DpdFloat(storage[fromIndex + it]) }
        temp.sortWith(DpdFloat.comparator)
        for (i in temp.indices) storage[fromIndex + i] = temp[i].bits
    }

    fun contentEquals(other: DpdFloatArray): Boolean = storage.contentEquals(other.storage)
    fun contentHashCode(): Int = storage.contentHashCode()
    fun contentToString(): String {
        if (isEmpty()) return "[]"
        return buildString {
            append('[')
            append(DpdFloat(storage[0]))
            for (i in 1 until size) {
                append(", ")
                append(DpdFloat(storage[i]))
            }
            append(']')
        }
    }

    fun asList(): List<DpdFloat> = object : AbstractList<DpdFloat>(), RandomAccess {
        override val size: Int get() = this@DpdFloatArray.size
        override fun get(index: Int): DpdFloat = this@DpdFloatArray[index]
    }

    operator fun component1(): DpdFloat = get(0)
    operator fun component2(): DpdFloat = get(1)
    operator fun component3(): DpdFloat = get(2)
    operator fun component4(): DpdFloat = get(3)
    operator fun component5(): DpdFloat = get(4)
}
