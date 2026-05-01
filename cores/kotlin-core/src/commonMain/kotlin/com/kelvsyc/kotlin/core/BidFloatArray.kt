package com.kelvsyc.kotlin.core

/**
 * An array of [BidFloat] values backed by an [IntArray], storing each element as its raw 32-bit pattern.
 *
 * ## Why not `Array<BidFloat>`?
 *
 * [BidFloat] is a value class, so it is represented as a plain [Int] in non-generic Kotlin code with no boxing
 * overhead. However, `Array<BidFloat>` is a generic array, which forces boxing: each element becomes a heap-allocated
 * wrapper object on the JVM, costing roughly 4–8 bytes for the object reference plus ~16 bytes of object overhead.
 * `BidFloatArray` avoids this entirely — its backing `IntArray` is a contiguous block of 4-byte slots with no
 * per-element allocation, giving a roughly 5× reduction in memory footprint and significantly better cache locality.
 *
 * ## Tradeoffs
 *
 * - `BidFloatArray` does not implement `Iterable<BidFloat>` or `Collection<BidFloat>`. Use [asList] to obtain a
 *   live `List<BidFloat>` view when collection interop is needed; element access through the list still boxes.
 * - [sort] uses a temporary boxed `Array<BidFloat>` and [BidFloat.comparator], so sorting incurs temporary boxing.
 * - A vararg factory cannot be provided: Kotlin prohibits value class types as vararg parameter types. Use the
 *   initializer constructor or [IntArray.toBidFloatArray] instead.
 */
class BidFloatArray(val size: Int) {
    private val storage = IntArray(size)

    constructor(size: Int, init: (Int) -> BidFloat) : this(size) {
        for (i in 0 until size) storage[i] = init(i).bits
    }

    constructor(source: IntArray) : this(source.size) {
        source.copyInto(storage)
    }

    operator fun get(index: Int): BidFloat = BidFloat(storage[index])

    operator fun set(index: Int, value: BidFloat) {
        storage[index] = value.bits
    }

    val indices: IntRange get() = 0 until size
    val lastIndex: Int get() = size - 1

    fun isEmpty(): Boolean = size == 0
    fun isNotEmpty(): Boolean = !isEmpty()

    fun contains(element: BidFloat): Boolean = storage.contains(element.bits)
    fun indexOf(element: BidFloat): Int = storage.indexOf(element.bits)
    fun lastIndexOf(element: BidFloat): Int = storage.lastIndexOf(element.bits)

    operator fun iterator(): BidFloatIterator = object : BidFloatIterator() {
        private var index = 0
        override fun hasNext(): Boolean = index < size
        override fun nextBidFloat(): BidFloat {
            if (index >= size) throw NoSuchElementException(index.toString())
            return get(index++)
        }
    }

    fun fill(element: BidFloat, fromIndex: Int = 0, toIndex: Int = size) {
        storage.fill(element.bits, fromIndex, toIndex)
    }

    fun copyOf(): BidFloatArray = BidFloatArray(storage.copyOf())
    fun copyOf(newSize: Int): BidFloatArray = BidFloatArray(storage.copyOf(newSize))
    fun copyOfRange(fromIndex: Int, toIndex: Int): BidFloatArray = BidFloatArray(storage.copyOfRange(fromIndex, toIndex))

    fun copyInto(destination: BidFloatArray, destinationOffset: Int = 0, startIndex: Int = 0, endIndex: Int = size): BidFloatArray {
        storage.copyInto(destination.storage, destinationOffset, startIndex, endIndex)
        return destination
    }

    fun toIntArray(): IntArray = storage.copyOf()

    fun sort() {
        val temp = Array(size) { BidFloat(storage[it]) }
        temp.sortWith(BidFloat.comparator)
        for (i in temp.indices) storage[i] = temp[i].bits
    }

    fun sort(fromIndex: Int, toIndex: Int) {
        val temp = Array(toIndex - fromIndex) { BidFloat(storage[fromIndex + it]) }
        temp.sortWith(BidFloat.comparator)
        for (i in temp.indices) storage[fromIndex + i] = temp[i].bits
    }

    fun contentEquals(other: BidFloatArray): Boolean = storage.contentEquals(other.storage)
    fun contentHashCode(): Int = storage.contentHashCode()
    fun contentToString(): String {
        if (isEmpty()) return "[]"
        return buildString {
            append('[')
            append(BidFloat(storage[0]))
            for (i in 1 until size) {
                append(", ")
                append(BidFloat(storage[i]))
            }
            append(']')
        }
    }

    fun asList(): List<BidFloat> = object : AbstractList<BidFloat>(), RandomAccess {
        override val size: Int get() = this@BidFloatArray.size
        override fun get(index: Int): BidFloat = this@BidFloatArray[index]
    }

    operator fun component1(): BidFloat = get(0)
    operator fun component2(): BidFloat = get(1)
    operator fun component3(): BidFloat = get(2)
    operator fun component4(): BidFloat = get(3)
    operator fun component5(): BidFloat = get(4)
}
