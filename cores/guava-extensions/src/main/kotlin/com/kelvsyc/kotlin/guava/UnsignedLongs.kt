package com.kelvsyc.kotlin.guava

import com.google.common.primitives.UnsignedLong
import com.kelvsyc.kotlin.core.Converter

operator fun UnsignedLong.div(other: UnsignedLong): UnsignedLong = dividedBy(other)
operator fun UnsignedLong.rem(other: UnsignedLong): UnsignedLong = mod(other)

fun ULong.toUnsignedLong(): UnsignedLong = UnsignedLong.fromLongBits(toLong())
fun UnsignedLong.toULong(): ULong = toLong().toULong()

val uLongToUnsignedLong: Converter<ULong, UnsignedLong> = Converter.of(
    forward = { it.toUnsignedLong() },
    backward = { it.toULong() }
)
