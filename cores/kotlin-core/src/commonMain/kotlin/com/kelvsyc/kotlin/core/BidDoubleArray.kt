package com.kelvsyc.kotlin.core

/**
 * An array of [BidDouble] values backed by a [LongArray], storing each element as its raw 64-bit pattern.
 *
 * ## Why not `Array<BidDouble>`?
 *
 * [BidDouble] is a value class, so it is represented as a plain [Long] in non-generic Kotlin code with no boxing
 * overhead. However, `Array<BidDouble>` is a generic array, which forces boxing: each element becomes a heap-allocated
 * wrapper object on the JVM, costing roughly 8 bytes for the object reference plus ~16 bytes of object overhead.
 * `BidDoubleArray` avoids this entirely — its backing `LongArray` is a contiguous block of 8-byte slots with no
 * per-element allocation, giving a roughly 3× reduction in memory footprint and significantly better cache locality.
 *
 * ## Tradeoffs
 *
 * - `BidDoubleArray` does not implement `Iterable<BidDouble>` or `Collection<BidDouble>`. Use [asList] to obtain a
 *   live `List<BidDouble>` view when collection interop is needed; element access through the list still boxes.
 * - [sort] uses a temporary boxed `Array<BidDouble>` and [BidDouble.comparator], so sorting incurs temporary boxing.
 * - A vararg factory cannot be provided: Kotlin prohibits value class types as vararg parameter types. Use the
 *   initializer constructor or [LongArray.toBidDoubleArray] instead.
 */
class BidDoubleArray(val size: Int) {
    private val storage = LongArray(size)

    constructor(size: Int, init: (Int) -> BidDouble) : this(size) {
        for (i in 0 until size) storage[i] = init(i).bits
    }

    constructor(source: LongArray) : this(source.size) {
        source.copyInto(storage)
    }

    operator fun get(index: Int): BidDouble = BidDouble(storage[index])

    operator fun set(index: Int, value: BidDouble) {
        storage[index] = value.bits
    }

    val indices: IntRange get() = 0 until size
    val lastIndex: Int get() = size - 1

    fun isEmpty(): Boolean = size == 0
    fun isNotEmpty(): Boolean = !isEmpty()

    fun contains(element: BidDouble): Boolean = storage.contains(element.bits)
    fun indexOf(element: BidDouble): Int = storage.indexOf(element.bits)
    fun lastIndexOf(element: BidDouble): Int = storage.lastIndexOf(element.bits)

    operator fun iterator(): BidDoubleIterator = object : BidDoubleIterator() {
        private var index = 0
        override fun hasNext(): Boolean = index < size
        override fun nextBidDouble(): BidDouble {
            if (index >= size) throw NoSuchElementException(index.toString())
            return get(index++)
        }
    }

    fun fill(element: BidDouble, fromIndex: Int = 0, toIndex: Int = size) {
        storage.fill(element.bits, fromIndex, toIndex)
    }

    fun copyOf(): BidDoubleArray = BidDoubleArray(storage.copyOf())
    fun copyOf(newSize: Int): BidDoubleArray = BidDoubleArray(storage.copyOf(newSize))
    fun copyOfRange(fromIndex: Int, toIndex: Int): BidDoubleArray = BidDoubleArray(storage.copyOfRange(fromIndex, toIndex))

    fun copyInto(destination: BidDoubleArray, destinationOffset: Int = 0, startIndex: Int = 0, endIndex: Int = size): BidDoubleArray {
        storage.copyInto(destination.storage, destinationOffset, startIndex, endIndex)
        return destination
    }

    fun toLongArray(): LongArray = storage.copyOf()

    fun sort() {
        val temp = Array(size) { BidDouble(storage[it]) }
        temp.sortWith(BidDouble.comparator)
        for (i in temp.indices) storage[i] = temp[i].bits
    }

    fun sort(fromIndex: Int, toIndex: Int) {
        val temp = Array(toIndex - fromIndex) { BidDouble(storage[fromIndex + it]) }
        temp.sortWith(BidDouble.comparator)
        for (i in temp.indices) storage[fromIndex + i] = temp[i].bits
    }

    fun contentEquals(other: BidDoubleArray): Boolean = storage.contentEquals(other.storage)
    fun contentHashCode(): Int = storage.contentHashCode()
    fun contentToString(): String {
        if (isEmpty()) return "[]"
        return buildString {
            append('[')
            append(BidDouble(storage[0]))
            for (i in 1 until size) {
                append(", ")
                append(BidDouble(storage[i]))
            }
            append(']')
        }
    }

    fun asList(): List<BidDouble> = object : AbstractList<BidDouble>(), RandomAccess {
        override val size: Int get() = this@BidDoubleArray.size
        override fun get(index: Int): BidDouble = this@BidDoubleArray[index]
    }

    operator fun component1(): BidDouble = get(0)
    operator fun component2(): BidDouble = get(1)
    operator fun component3(): BidDouble = get(2)
    operator fun component4(): BidDouble = get(3)
    operator fun component5(): BidDouble = get(4)
}
