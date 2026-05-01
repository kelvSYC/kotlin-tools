package com.kelvsyc.kotlin.core

/**
 * An array of [DpdDouble] values backed by a [LongArray], storing each element as its raw 64-bit pattern.
 *
 * ## Why not `Array<DpdDouble>`?
 *
 * [DpdDouble] is a value class, so it is represented as a plain [Long] in non-generic Kotlin code with no boxing
 * overhead. However, `Array<DpdDouble>` is a generic array, which forces boxing: each element becomes a heap-allocated
 * wrapper object on the JVM, costing roughly 8 bytes for the object reference plus ~16 bytes of object overhead.
 * `DpdDoubleArray` avoids this entirely — its backing `LongArray` is a contiguous block of 8-byte slots with no
 * per-element allocation, giving a roughly 3× reduction in memory footprint and significantly better cache locality.
 *
 * ## Tradeoffs
 *
 * - `DpdDoubleArray` does not implement `Iterable<DpdDouble>` or `Collection<DpdDouble>`. Use [asList] to obtain a
 *   live `List<DpdDouble>` view when collection interop is needed; element access through the list still boxes.
 * - [sort] uses a temporary boxed `Array<DpdDouble>` and [DpdDouble.comparator], so sorting incurs temporary boxing.
 * - A vararg factory cannot be provided: Kotlin prohibits value class types as vararg parameter types. Use the
 *   initializer constructor or [LongArray.toDpdDoubleArray] instead.
 */
class DpdDoubleArray(val size: Int) {
    private val storage = LongArray(size)

    constructor(size: Int, init: (Int) -> DpdDouble) : this(size) {
        for (i in 0 until size) storage[i] = init(i).bits
    }

    constructor(source: LongArray) : this(source.size) {
        source.copyInto(storage)
    }

    operator fun get(index: Int): DpdDouble = DpdDouble(storage[index])

    operator fun set(index: Int, value: DpdDouble) {
        storage[index] = value.bits
    }

    val indices: IntRange get() = 0 until size
    val lastIndex: Int get() = size - 1

    fun isEmpty(): Boolean = size == 0
    fun isNotEmpty(): Boolean = !isEmpty()

    fun contains(element: DpdDouble): Boolean = storage.contains(element.bits)
    fun indexOf(element: DpdDouble): Int = storage.indexOf(element.bits)
    fun lastIndexOf(element: DpdDouble): Int = storage.lastIndexOf(element.bits)

    operator fun iterator(): DpdDoubleIterator = object : DpdDoubleIterator() {
        private var index = 0
        override fun hasNext(): Boolean = index < size
        override fun nextDpdDouble(): DpdDouble {
            if (index >= size) throw NoSuchElementException(index.toString())
            return get(index++)
        }
    }

    fun fill(element: DpdDouble, fromIndex: Int = 0, toIndex: Int = size) {
        storage.fill(element.bits, fromIndex, toIndex)
    }

    fun copyOf(): DpdDoubleArray = DpdDoubleArray(storage.copyOf())
    fun copyOf(newSize: Int): DpdDoubleArray = DpdDoubleArray(storage.copyOf(newSize))
    fun copyOfRange(fromIndex: Int, toIndex: Int): DpdDoubleArray = DpdDoubleArray(storage.copyOfRange(fromIndex, toIndex))

    fun copyInto(destination: DpdDoubleArray, destinationOffset: Int = 0, startIndex: Int = 0, endIndex: Int = size): DpdDoubleArray {
        storage.copyInto(destination.storage, destinationOffset, startIndex, endIndex)
        return destination
    }

    fun toLongArray(): LongArray = storage.copyOf()

    fun sort() {
        val temp = Array(size) { DpdDouble(storage[it]) }
        temp.sortWith(DpdDouble.comparator)
        for (i in temp.indices) storage[i] = temp[i].bits
    }

    fun sort(fromIndex: Int, toIndex: Int) {
        val temp = Array(toIndex - fromIndex) { DpdDouble(storage[fromIndex + it]) }
        temp.sortWith(DpdDouble.comparator)
        for (i in temp.indices) storage[fromIndex + i] = temp[i].bits
    }

    fun contentEquals(other: DpdDoubleArray): Boolean = storage.contentEquals(other.storage)
    fun contentHashCode(): Int = storage.contentHashCode()
    fun contentToString(): String {
        if (isEmpty()) return "[]"
        return buildString {
            append('[')
            append(DpdDouble(storage[0]))
            for (i in 1 until size) {
                append(", ")
                append(DpdDouble(storage[i]))
            }
            append(']')
        }
    }

    fun asList(): List<DpdDouble> = object : AbstractList<DpdDouble>(), RandomAccess {
        override val size: Int get() = this@DpdDoubleArray.size
        override fun get(index: Int): DpdDouble = this@DpdDoubleArray[index]
    }

    operator fun component1(): DpdDouble = get(0)
    operator fun component2(): DpdDouble = get(1)
    operator fun component3(): DpdDouble = get(2)
    operator fun component4(): DpdDouble = get(3)
    operator fun component5(): DpdDouble = get(4)
}
