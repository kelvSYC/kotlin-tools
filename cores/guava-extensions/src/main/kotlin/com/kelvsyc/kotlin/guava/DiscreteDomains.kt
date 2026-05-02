package com.kelvsyc.kotlin.guava

import com.google.common.collect.DiscreteDomain
import com.google.common.primitives.UnsignedInteger
import com.google.common.primitives.UnsignedLong
import java.io.Serializable

object ShortDiscreteDomain : DiscreteDomain<Short>(), Serializable {
    private const val serialVersionUID: Long = 0L

    override fun next(value: Short): Short? = if (value == Short.MAX_VALUE) null else (value + 1).toShort()
    override fun previous(value: Short): Short? = if (value == Short.MIN_VALUE) null else (value - 1).toShort()
    override fun distance(start: Short, end: Short): Long = end.toLong() - start.toLong()
    override fun minValue(): Short = Short.MIN_VALUE
    override fun maxValue(): Short = Short.MAX_VALUE
    override fun toString(): String = "DiscreteDomain.shorts()"
}

object UByteDiscreteDomain : DiscreteDomain<UByte>(), Serializable {
    private const val serialVersionUID: Long = 0L

    override fun next(value: UByte): UByte? = if (value == UByte.MAX_VALUE) null else (value + 1u).toUByte()
    override fun previous(value: UByte): UByte? = if (value == UByte.MIN_VALUE) null else (value - 1u).toUByte()
    override fun distance(start: UByte, end: UByte): Long = end.toLong() - start.toLong()
    override fun minValue(): UByte = UByte.MIN_VALUE
    override fun maxValue(): UByte = UByte.MAX_VALUE
    override fun toString(): String = "DiscreteDomain.uBytes()"
}

object UShortDiscreteDomain : DiscreteDomain<UShort>(), Serializable {
    private const val serialVersionUID: Long = 0L

    override fun next(value: UShort): UShort? = if (value == UShort.MAX_VALUE) null else (value + 1u).toUShort()
    override fun previous(value: UShort): UShort? = if (value == UShort.MIN_VALUE) null else (value - 1u).toUShort()
    override fun distance(start: UShort, end: UShort): Long = end.toLong() - start.toLong()
    override fun minValue(): UShort = UShort.MIN_VALUE
    override fun maxValue(): UShort = UShort.MAX_VALUE
    override fun toString(): String = "DiscreteDomain.uShorts()"
}

object UIntDiscreteDomain : DiscreteDomain<UInt>(), Serializable {
    private const val serialVersionUID: Long = 0L

    override fun next(value: UInt): UInt? = if (value == UInt.MAX_VALUE) null else value + 1u
    override fun previous(value: UInt): UInt? = if (value == UInt.MIN_VALUE) null else value - 1u
    override fun distance(start: UInt, end: UInt): Long = end.toLong() - start.toLong()
    override fun minValue(): UInt = UInt.MIN_VALUE
    override fun maxValue(): UInt = UInt.MAX_VALUE
    override fun toString(): String = "DiscreteDomain.uInts()"
}

object ULongDiscreteDomain : DiscreteDomain<ULong>(), Serializable {
    private const val serialVersionUID: Long = 0L

    override fun next(value: ULong): ULong? = if (value == ULong.MAX_VALUE) null else value + 1uL
    override fun previous(value: ULong): ULong? = if (value == ULong.MIN_VALUE) null else value - 1uL
    override fun distance(start: ULong, end: ULong): Long =
        if (end >= start) {
            val diff = end - start
            if (diff > Long.MAX_VALUE.toULong()) Long.MAX_VALUE else diff.toLong()
        } else {
            val diff = start - end
            if (diff > Long.MAX_VALUE.toULong()) Long.MIN_VALUE else -diff.toLong()
        }
    override fun minValue(): ULong = ULong.MIN_VALUE
    override fun maxValue(): ULong = ULong.MAX_VALUE
    override fun toString(): String = "DiscreteDomain.uLongs()"
}

object UnsignedIntegerDiscreteDomain : DiscreteDomain<UnsignedInteger>(), Serializable {
    private const val serialVersionUID: Long = 0L

    override fun next(value: UnsignedInteger): UnsignedInteger? =
        if (value == UnsignedInteger.MAX_VALUE) null else value.plus(UnsignedInteger.ONE)
    override fun previous(value: UnsignedInteger): UnsignedInteger? =
        if (value == UnsignedInteger.ZERO) null else value.minus(UnsignedInteger.ONE)
    override fun distance(start: UnsignedInteger, end: UnsignedInteger): Long =
        end.toLong() - start.toLong()
    override fun minValue(): UnsignedInteger = UnsignedInteger.ZERO
    override fun maxValue(): UnsignedInteger = UnsignedInteger.MAX_VALUE
    override fun toString(): String = "DiscreteDomain.unsignedIntegers()"
}

object UnsignedLongDiscreteDomain : DiscreteDomain<UnsignedLong>(), Serializable {
    private const val serialVersionUID: Long = 0L
    private val maxLong = UnsignedLong.valueOf(Long.MAX_VALUE)

    override fun next(value: UnsignedLong): UnsignedLong? =
        if (value == UnsignedLong.MAX_VALUE) null else value.plus(UnsignedLong.ONE)
    override fun previous(value: UnsignedLong): UnsignedLong? =
        if (value == UnsignedLong.ZERO) null else value.minus(UnsignedLong.ONE)
    override fun distance(start: UnsignedLong, end: UnsignedLong): Long =
        if (end >= start) {
            val diff = end.minus(start)
            if (diff > maxLong) Long.MAX_VALUE else diff.toLong()
        } else {
            val diff = start.minus(end)
            if (diff > maxLong) Long.MIN_VALUE else -diff.toLong()
        }
    override fun minValue(): UnsignedLong = UnsignedLong.ZERO
    override fun maxValue(): UnsignedLong = UnsignedLong.MAX_VALUE
    override fun toString(): String = "DiscreteDomain.unsignedLongs()"
}
