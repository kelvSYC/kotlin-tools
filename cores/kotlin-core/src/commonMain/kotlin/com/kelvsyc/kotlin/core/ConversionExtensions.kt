package com.kelvsyc.kotlin.core

// ── Pack: BooleanArray → scalar ───────────────────────────────────────────────
// All functions use little-endian bit order: index 0 maps to the least significant bit.

/**
 * Packs up to [Byte.SIZE_BITS] elements of this [BooleanArray] into a [Byte],
 * treating index 0 as the least significant bit.
 */
fun BooleanArray.toByte(): Byte {
    var result = 0
    for (i in 0 until minOf(size, Byte.SIZE_BITS)) {
        if (this[i]) result = result or (1 shl i)
    }
    return result.toByte()
}

/**
 * Packs up to [Short.SIZE_BITS] elements of this [BooleanArray] into a [Short],
 * treating index 0 as the least significant bit.
 */
fun BooleanArray.toShort(): Short {
    var result = 0
    for (i in 0 until minOf(size, Short.SIZE_BITS)) {
        if (this[i]) result = result or (1 shl i)
    }
    return result.toShort()
}

/**
 * Packs up to [Int.SIZE_BITS] elements of this [BooleanArray] into an [Int],
 * treating index 0 as the least significant bit.
 */
fun BooleanArray.toInt(): Int {
    var result = 0
    for (i in 0 until minOf(size, Int.SIZE_BITS)) {
        if (this[i]) result = result or (1 shl i)
    }
    return result
}

/**
 * Packs up to [Long.SIZE_BITS] elements of this [BooleanArray] into a [Long],
 * treating index 0 as the least significant bit.
 */
fun BooleanArray.toLong(): Long {
    var result = 0L
    for (i in 0 until minOf(size, Long.SIZE_BITS)) {
        if (this[i]) result = result or (1L shl i)
    }
    return result
}

// ── Pack: byte-width arrays → wider scalars ───────────────────────────────────
// All functions use little-endian byte order: index 0 maps to the least significant byte/short/int.

/**
 * Packs up to [Short.SIZE_BYTES] elements of this [ByteArray] into a [Short],
 * treating index 0 as the least significant byte.
 */
fun ByteArray.toShort(): Short {
    var result = 0
    for (i in 0 until minOf(size, Short.SIZE_BYTES)) {
        result = result or ((this[i].toInt() and 0xFF) shl (i * Byte.SIZE_BITS))
    }
    return result.toShort()
}

/**
 * Packs up to [Int.SIZE_BYTES] elements of this [ByteArray] into an [Int],
 * treating index 0 as the least significant byte.
 */
fun ByteArray.toInt(): Int {
    var result = 0
    for (i in 0 until minOf(size, Int.SIZE_BYTES)) {
        result = result or ((this[i].toInt() and 0xFF) shl (i * Byte.SIZE_BITS))
    }
    return result
}

/**
 * Packs up to [Long.SIZE_BYTES] elements of this [ByteArray] into a [Long],
 * treating index 0 as the least significant byte.
 */
fun ByteArray.toLong(): Long {
    var result = 0L
    for (i in 0 until minOf(size, Long.SIZE_BYTES)) {
        result = result or ((this[i].toLong() and 0xFFL) shl (i * Byte.SIZE_BITS))
    }
    return result
}

/**
 * Packs up to `[Int.SIZE_BYTES] / [Short.SIZE_BYTES]` elements of this [ShortArray] into an [Int],
 * treating index 0 as the least significant short.
 */
fun ShortArray.toInt(): Int {
    var result = 0
    for (i in 0 until minOf(size, Int.SIZE_BYTES / Short.SIZE_BYTES)) {
        result = result or ((this[i].toInt() and 0xFFFF) shl (i * Short.SIZE_BITS))
    }
    return result
}

/**
 * Packs up to `[Long.SIZE_BYTES] / [Short.SIZE_BYTES]` elements of this [ShortArray] into a [Long],
 * treating index 0 as the least significant short.
 */
fun ShortArray.toLong(): Long {
    var result = 0L
    for (i in 0 until minOf(size, Long.SIZE_BYTES / Short.SIZE_BYTES)) {
        result = result or ((this[i].toLong() and 0xFFFFL) shl (i * Short.SIZE_BITS))
    }
    return result
}

/**
 * Packs up to `[Long.SIZE_BYTES] / [Int.SIZE_BYTES]` elements of this [IntArray] into a [Long],
 * treating index 0 as the least significant int.
 */
fun IntArray.toLong(): Long {
    var result = 0L
    for (i in 0 until minOf(size, Long.SIZE_BYTES / Int.SIZE_BYTES)) {
        result = result or ((this[i].toLong() and 0xFFFFFFFFL) shl (i * Int.SIZE_BITS))
    }
    return result
}

// ── Unpack: scalar → BooleanArray ────────────────────────────────────────────

/**
 * Unpacks this [Byte] into a [BooleanArray] of size [Byte.SIZE_BITS],
 * with index 0 representing the least significant bit.
 */
fun Byte.toBooleanArray(): BooleanArray =
    BooleanArray(Byte.SIZE_BITS) { i -> (toInt() ushr i) and 1 == 1 }

/**
 * Unpacks this [Short] into a [BooleanArray] of size [Short.SIZE_BITS],
 * with index 0 representing the least significant bit.
 */
fun Short.toBooleanArray(): BooleanArray =
    BooleanArray(Short.SIZE_BITS) { i -> (toInt() ushr i) and 1 == 1 }

/**
 * Unpacks this [Int] into a [BooleanArray] of size [Int.SIZE_BITS],
 * with index 0 representing the least significant bit.
 */
fun Int.toBooleanArray(): BooleanArray =
    BooleanArray(Int.SIZE_BITS) { i -> (this ushr i) and 1 == 1 }

/**
 * Unpacks this [Long] into a [BooleanArray] of size [Long.SIZE_BITS],
 * with index 0 representing the least significant bit.
 */
fun Long.toBooleanArray(): BooleanArray =
    BooleanArray(Long.SIZE_BITS) { i -> (this ushr i) and 1L == 1L }

// ── Unpack: scalar → byte-width arrays ───────────────────────────────────────

/**
 * Unpacks this [Short] into a [ByteArray] of size [Short.SIZE_BYTES],
 * with index 0 representing the least significant byte.
 */
fun Short.toByteArray(): ByteArray =
    ByteArray(Short.SIZE_BYTES) { i -> (toInt() ushr (i * Byte.SIZE_BITS)).toByte() }

/**
 * Unpacks this [Int] into a [ByteArray] of size [Int.SIZE_BYTES],
 * with index 0 representing the least significant byte.
 */
fun Int.toByteArray(): ByteArray =
    ByteArray(Int.SIZE_BYTES) { i -> (this ushr (i * Byte.SIZE_BITS)).toByte() }

/**
 * Unpacks this [Int] into a [ShortArray] of size `[Int.SIZE_BYTES] / [Short.SIZE_BYTES]`,
 * with index 0 representing the least significant short.
 */
fun Int.toShortArray(): ShortArray =
    ShortArray(Int.SIZE_BYTES / Short.SIZE_BYTES) { i -> (this ushr (i * Short.SIZE_BITS)).toShort() }

/**
 * Unpacks this [Long] into a [ByteArray] of size [Long.SIZE_BYTES],
 * with index 0 representing the least significant byte.
 */
fun Long.toByteArray(): ByteArray =
    ByteArray(Long.SIZE_BYTES) { i -> (this ushr (i * Byte.SIZE_BITS)).toByte() }

/**
 * Unpacks this [Long] into a [ShortArray] of size `[Long.SIZE_BYTES] / [Short.SIZE_BYTES]`,
 * with index 0 representing the least significant short.
 */
fun Long.toShortArray(): ShortArray =
    ShortArray(Long.SIZE_BYTES / Short.SIZE_BYTES) { i -> (this ushr (i * Short.SIZE_BITS)).toShort() }

/**
 * Unpacks this [Long] into an [IntArray] of size `[Long.SIZE_BYTES] / [Int.SIZE_BYTES]`,
 * with index 0 representing the least significant int.
 */
fun Long.toIntArray(): IntArray =
    IntArray(Long.SIZE_BYTES / Int.SIZE_BYTES) { i -> (this ushr (i * Int.SIZE_BITS)).toInt() }
