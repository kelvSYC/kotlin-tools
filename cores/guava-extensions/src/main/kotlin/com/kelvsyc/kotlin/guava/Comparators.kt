package com.kelvsyc.kotlin.guava

import com.google.common.primitives.UnsignedBytes
import com.google.common.primitives.UnsignedInts
import com.google.common.primitives.UnsignedLongs

object Comparators {
    /**
     * Orders [Byte] values by their unsigned interpretation, so values with the high bit set sort after all non-negative values.
     *
     * Prefer this over converting to [UByte] and using natural ordering: [UByte] is an inline class, and [Comparator]
     * is a generic interface, so the JVM boxes each value on every comparison call.
     */
    val unsignedByteComparator = Comparator(UnsignedBytes::compare)

    /**
     * Orders [Int] values by their unsigned interpretation, so values with the high bit set sort after all non-negative values.
     *
     * Prefer this over converting to [UInt] and using natural ordering: [UInt] is an inline class, and [Comparator]
     * is a generic interface, so the JVM boxes each value on every comparison call.
     */
    val unsignedIntComparator = Comparator(UnsignedInts::compare)

    /**
     * Orders [Long] values by their unsigned interpretation, so values with the high bit set sort after all non-negative values.
     *
     * Prefer this over converting to [ULong] and using natural ordering: [ULong] is an inline class, and [Comparator]
     * is a generic interface, so the JVM boxes each value on every comparison call.
     */
    val unsignedLongComparator = Comparator(UnsignedLongs::compare)
}
