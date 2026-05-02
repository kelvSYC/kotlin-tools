package com.kelvsyc.kotlin.core

// ── Pack: BooleanArray → unsigned scalar ─────────────────────────────────────

/**
 * Packs up to [UByte.SIZE_BITS] elements of this [BooleanArray] into a [UByte],
 * treating index 0 as the least significant bit.
 *
 * @see BooleanArray.toByte
 */
fun BooleanArray.toUByte(): UByte = toByte().toUByte()

/**
 * Packs up to [UShort.SIZE_BITS] elements of this [BooleanArray] into a [UShort],
 * treating index 0 as the least significant bit.
 *
 * @see BooleanArray.toShort
 */
fun BooleanArray.toUShort(): UShort = toShort().toUShort()

/**
 * Packs up to [UInt.SIZE_BITS] elements of this [BooleanArray] into a [UInt],
 * treating index 0 as the least significant bit.
 *
 * @see BooleanArray.toInt
 */
fun BooleanArray.toUInt(): UInt = toInt().toUInt()

/**
 * Packs up to [ULong.SIZE_BITS] elements of this [BooleanArray] into a [ULong],
 * treating index 0 as the least significant bit.
 *
 * @see BooleanArray.toLong
 */
fun BooleanArray.toULong(): ULong = toLong().toULong()

// ── Unpack: unsigned scalar → BooleanArray ────────────────────────────────────

/**
 * Unpacks this [UByte] into a [BooleanArray] of size [UByte.SIZE_BITS],
 * with index 0 representing the least significant bit.
 *
 * @see Byte.toBooleanArray
 */
fun UByte.toBooleanArray(): BooleanArray = toByte().toBooleanArray()

/**
 * Unpacks this [UShort] into a [BooleanArray] of size [UShort.SIZE_BITS],
 * with index 0 representing the least significant bit.
 *
 * @see Short.toBooleanArray
 */
fun UShort.toBooleanArray(): BooleanArray = toShort().toBooleanArray()

/**
 * Unpacks this [UInt] into a [BooleanArray] of size [UInt.SIZE_BITS],
 * with index 0 representing the least significant bit.
 *
 * @see Int.toBooleanArray
 */
fun UInt.toBooleanArray(): BooleanArray = toInt().toBooleanArray()

/**
 * Unpacks this [ULong] into a [BooleanArray] of size [ULong.SIZE_BITS],
 * with index 0 representing the least significant bit.
 *
 * @see Long.toBooleanArray
 */
fun ULong.toBooleanArray(): BooleanArray = toLong().toBooleanArray()

// ── Unpack: unsigned scalar → byte-width arrays ───────────────────────────────

/**
 * Unpacks this [UShort] into a [ByteArray] of size [UShort.SIZE_BYTES],
 * with index 0 representing the least significant byte.
 *
 * @see Short.toByteArray
 */
fun UShort.toByteArray(): ByteArray = toShort().toByteArray()

/**
 * Unpacks this [UInt] into a [ByteArray] of size [UInt.SIZE_BYTES],
 * with index 0 representing the least significant byte.
 *
 * @see Int.toByteArray
 */
fun UInt.toByteArray(): ByteArray = toInt().toByteArray()

/**
 * Unpacks this [UInt] into a [ShortArray] of size `[UInt.SIZE_BYTES] / [Short.SIZE_BYTES]`,
 * with index 0 representing the least significant short.
 *
 * @see Int.toShortArray
 */
fun UInt.toShortArray(): ShortArray = toInt().toShortArray()

/**
 * Unpacks this [ULong] into a [ByteArray] of size [ULong.SIZE_BYTES],
 * with index 0 representing the least significant byte.
 *
 * @see Long.toByteArray
 */
fun ULong.toByteArray(): ByteArray = toLong().toByteArray()

/**
 * Unpacks this [ULong] into a [ShortArray] of size `[ULong.SIZE_BYTES] / [Short.SIZE_BYTES]`,
 * with index 0 representing the least significant short.
 *
 * @see Long.toShortArray
 */
fun ULong.toShortArray(): ShortArray = toLong().toShortArray()

/**
 * Unpacks this [ULong] into an [IntArray] of size `[ULong.SIZE_BYTES] / [Int.SIZE_BYTES]`,
 * with index 0 representing the least significant int.
 *
 * @see Long.toIntArray
 */
fun ULong.toIntArray(): IntArray = toLong().toIntArray()
