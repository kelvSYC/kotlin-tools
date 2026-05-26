package com.kelvsyc.kotlin.core.traits.fp

// Float.nextUp() is absent from Kotlin/JS; this pure-Kotlin fallback uses bit-pattern arithmetic,
// which is platform-universal since Float.toRawBits() always returns the binary32 representation.
internal fun nextUpFloatEmulated(x: Float): Float {
    if (x.isNaN()) return x
    val bits = x.toRawBits()
    return when {
        bits == 0x7F800000 -> x                             // +∞ has no successor
        bits == 0 || bits == Int.MIN_VALUE -> Float.fromBits(1)  // ±0 → MIN_VALUE
        bits > 0 -> Float.fromBits(bits + 1)                // positive finite: increment bits
        else -> Float.fromBits(bits - 1)                    // negative (incl. -∞): toward zero
    }
}

internal fun nextDownFloatEmulated(x: Float): Float = -nextUpFloatEmulated(-x)
