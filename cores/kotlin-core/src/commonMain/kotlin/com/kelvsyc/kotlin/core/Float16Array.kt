package com.kelvsyc.kotlin.core

/**
 * An array of [Float16] values backed by a [ShortArray], storing each element as its raw 16-bit pattern.
 *
 * ## Why not `Array<Float16>`?
 *
 * [Float16] is a value class, so it is represented as a plain [Short] in non-generic Kotlin code with no boxing
 * overhead. However, `Array<Float16>` is a generic array, which forces boxing: each element becomes a heap-allocated
 * wrapper object on the JVM, costing roughly 4–8 bytes for the object reference plus ~16 bytes of object overhead.
 * `Float16Array` avoids this entirely — its backing `ShortArray` is a contiguous block of 2-byte slots with no
 * per-element allocation, giving an 8–12× reduction in memory footprint and significantly better cache locality.
 *
 * ## Tradeoffs
 *
 * The price for compact storage is reduced interoperability with generic Kotlin APIs:
 *
 * - `Float16Array` does not implement `Iterable<Float16>` or `Collection<Float16>`, so it cannot be passed directly
 *   to functions that expect those interfaces. Use [asList] to obtain a live `List<Float16>` view when collection
 *   interop is needed; note that element access through the list still boxes.
 * - [sort] must widen each element to [Float] for comparison (because [Float16] is not [Comparable]), so sorting
 *   incurs temporary boxing even though storage does not.
 * - A vararg factory function analogous to `floatArrayOf` cannot be provided: Kotlin prohibits value class types
 *   as vararg parameter types. Use the initializer constructor or [ShortArray.toFloat16Array] instead.
 */
class Float16Array(val size: Int) {
    private val storage = ShortArray(size)

    constructor(size: Int, init: (Int) -> Float16) : this(size) {
        for (i in 0 until size) storage[i] = init(i).bits
    }

    constructor(source: ShortArray) : this(source.size) {
        source.copyInto(storage)
    }

    operator fun get(index: Int): Float16 = Float16(storage[index])

    operator fun set(index: Int, value: Float16) {
        storage[index] = value.bits
    }

    val indices: IntRange get() = 0 until size
    val lastIndex: Int get() = size - 1

    fun isEmpty(): Boolean = size == 0
    fun isNotEmpty(): Boolean = !isEmpty()

    fun contains(element: Float16): Boolean = storage.contains(element.bits)
    fun indexOf(element: Float16): Int = storage.indexOf(element.bits)
    fun lastIndexOf(element: Float16): Int = storage.lastIndexOf(element.bits)

    operator fun iterator(): Float16Iterator = object : Float16Iterator() {
        private var index = 0
        override fun hasNext(): Boolean = index < size
        override fun nextFloat16(): Float16 {
            if (index >= size) throw NoSuchElementException(index.toString())
            return get(index++)
        }
    }

    fun fill(element: Float16, fromIndex: Int = 0, toIndex: Int = size) {
        storage.fill(element.bits, fromIndex, toIndex)
    }

    fun copyOf(): Float16Array = Float16Array(storage.copyOf())
    fun copyOf(newSize: Int): Float16Array = Float16Array(storage.copyOf(newSize))
    fun copyOfRange(fromIndex: Int, toIndex: Int): Float16Array = Float16Array(storage.copyOfRange(fromIndex, toIndex))

    fun copyInto(destination: Float16Array, destinationOffset: Int = 0, startIndex: Int = 0, endIndex: Int = size): Float16Array {
        storage.copyInto(destination.storage, destinationOffset, startIndex, endIndex)
        return destination
    }

    fun toShortArray(): ShortArray = storage.copyOf()

    fun sort() {
        val temp = Array(size) { Float16(storage[it]) }
        temp.sortWith(Float16.comparator)
        for (i in temp.indices) storage[i] = temp[i].bits
    }

    fun sort(fromIndex: Int, toIndex: Int) {
        val temp = Array(toIndex - fromIndex) { Float16(storage[fromIndex + it]) }
        temp.sortWith(Float16.comparator)
        for (i in temp.indices) storage[fromIndex + i] = temp[i].bits
    }

    fun contentEquals(other: Float16Array): Boolean = storage.contentEquals(other.storage)
    fun contentHashCode(): Int = storage.contentHashCode()
    fun contentToString(): String {
        if (isEmpty()) return "[]"
        return buildString {
            append('[')
            append(Float16(storage[0]))
            for (i in 1 until size) {
                append(", ")
                append(Float16(storage[i]))
            }
            append(']')
        }
    }

    fun asList(): List<Float16> = object : AbstractList<Float16>(), RandomAccess {
        override val size: Int get() = this@Float16Array.size
        override fun get(index: Int): Float16 = this@Float16Array[index]
    }

    operator fun component1(): Float16 = get(0)
    operator fun component2(): Float16 = get(1)
    operator fun component3(): Float16 = get(2)
    operator fun component4(): Float16 = get(3)
    operator fun component5(): Float16 = get(4)
}
