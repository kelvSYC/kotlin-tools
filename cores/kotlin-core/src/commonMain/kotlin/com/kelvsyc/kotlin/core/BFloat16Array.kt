package com.kelvsyc.kotlin.core

/**
 * An array of [BFloat16] values backed by a [ShortArray], storing each element as its raw 16-bit pattern.
 *
 * ## Why not `Array<BFloat16>`?
 *
 * [BFloat16] is a value class, so it is represented as a plain [Short] in non-generic Kotlin code with no boxing
 * overhead. However, `Array<BFloat16>` is a generic array, which forces boxing: each element becomes a heap-allocated
 * wrapper object on the JVM, costing roughly 4–8 bytes for the object reference plus ~16 bytes of object overhead.
 * `BFloat16Array` avoids this entirely — its backing `ShortArray` is a contiguous block of 2-byte slots with no
 * per-element allocation, giving an 8–12× reduction in memory footprint and significantly better cache locality.
 *
 * ## Tradeoffs
 *
 * - `BFloat16Array` does not implement `Iterable<BFloat16>` or `Collection<BFloat16>`. Use [asList] to obtain a
 *   live `List<BFloat16>` view when collection interop is needed; element access through the list still boxes.
 * - [sort] widens each element to [Float] for comparison, so sorting incurs temporary boxing.
 * - A vararg factory analogous to `floatArrayOf` cannot be provided: Kotlin prohibits value class types as vararg
 *   parameter types. Use the initializer constructor or [ShortArray.toBFloat16Array] instead.
 */
class BFloat16Array(val size: Int) {
    private val storage = ShortArray(size)

    constructor(size: Int, init: (Int) -> BFloat16) : this(size) {
        for (i in 0 until size) storage[i] = init(i).bits
    }

    constructor(source: ShortArray) : this(source.size) {
        source.copyInto(storage)
    }

    operator fun get(index: Int): BFloat16 = BFloat16(storage[index])

    operator fun set(index: Int, value: BFloat16) {
        storage[index] = value.bits
    }

    val indices: IntRange get() = 0 until size
    val lastIndex: Int get() = size - 1

    fun isEmpty(): Boolean = size == 0
    fun isNotEmpty(): Boolean = !isEmpty()

    fun contains(element: BFloat16): Boolean = storage.contains(element.bits)
    fun indexOf(element: BFloat16): Int = storage.indexOf(element.bits)
    fun lastIndexOf(element: BFloat16): Int = storage.lastIndexOf(element.bits)

    operator fun iterator(): BFloat16Iterator = object : BFloat16Iterator() {
        private var index = 0
        override fun hasNext(): Boolean = index < size
        override fun nextBFloat16(): BFloat16 {
            if (index >= size) throw NoSuchElementException(index.toString())
            return get(index++)
        }
    }

    fun fill(element: BFloat16, fromIndex: Int = 0, toIndex: Int = size) {
        storage.fill(element.bits, fromIndex, toIndex)
    }

    fun copyOf(): BFloat16Array = BFloat16Array(storage.copyOf())
    fun copyOf(newSize: Int): BFloat16Array = BFloat16Array(storage.copyOf(newSize))
    fun copyOfRange(fromIndex: Int, toIndex: Int): BFloat16Array = BFloat16Array(storage.copyOfRange(fromIndex, toIndex))

    fun copyInto(destination: BFloat16Array, destinationOffset: Int = 0, startIndex: Int = 0, endIndex: Int = size): BFloat16Array {
        storage.copyInto(destination.storage, destinationOffset, startIndex, endIndex)
        return destination
    }

    fun toShortArray(): ShortArray = storage.copyOf()

    fun sort() {
        val temp = Array(size) { BFloat16(storage[it]) }
        temp.sortWith(BFloat16.comparator)
        for (i in temp.indices) storage[i] = temp[i].bits
    }

    fun sort(fromIndex: Int, toIndex: Int) {
        val temp = Array(toIndex - fromIndex) { BFloat16(storage[fromIndex + it]) }
        temp.sortWith(BFloat16.comparator)
        for (i in temp.indices) storage[fromIndex + i] = temp[i].bits
    }

    fun contentEquals(other: BFloat16Array): Boolean = storage.contentEquals(other.storage)
    fun contentHashCode(): Int = storage.contentHashCode()
    fun contentToString(): String {
        if (isEmpty()) return "[]"
        return buildString {
            append('[')
            append(BFloat16(storage[0]))
            for (i in 1 until size) {
                append(", ")
                append(BFloat16(storage[i]))
            }
            append(']')
        }
    }

    fun asList(): List<BFloat16> = object : AbstractList<BFloat16>(), RandomAccess {
        override val size: Int get() = this@BFloat16Array.size
        override fun get(index: Int): BFloat16 = this@BFloat16Array[index]
    }

    operator fun component1(): BFloat16 = get(0)
    operator fun component2(): BFloat16 = get(1)
    operator fun component3(): BFloat16 = get(2)
    operator fun component4(): BFloat16 = get(3)
    operator fun component5(): BFloat16 = get(4)
}
