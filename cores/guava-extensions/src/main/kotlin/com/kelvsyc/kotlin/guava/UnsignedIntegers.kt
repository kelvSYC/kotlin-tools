package com.kelvsyc.kotlin.guava

import com.google.common.primitives.UnsignedInteger
import com.kelvsyc.kotlin.core.Converter

operator fun UnsignedInteger.div(other: UnsignedInteger): UnsignedInteger = dividedBy(other)
operator fun UnsignedInteger.rem(other: UnsignedInteger): UnsignedInteger = mod(other)

fun UInt.toUnsignedInteger(): UnsignedInteger = UnsignedInteger.fromIntBits(toInt())
fun UnsignedInteger.toUInt(): UInt = toInt().toUInt()

val uIntToUnsignedInteger: Converter<UInt, UnsignedInteger> = Converter.of(
    forward = { it.toUnsignedInteger() },
    backward = { it.toUInt() }
)
