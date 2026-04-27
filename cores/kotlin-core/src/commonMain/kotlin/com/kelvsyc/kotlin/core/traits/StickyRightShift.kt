package com.kelvsyc.kotlin.core.traits

/**
 * `StickyRightShift` is a trait type providing a uniform interface for sticky right shift operations on values of type
 * [T].
 *
 * A "sticky right shift" is a logical right shift, with one important difference: if any of the shifted-off bits is
 * set, the least significant bit of the result will also be set.
 *
 * An artificial implementation can be derived from [BitCollection] and [BitShift] via [Companion.from].
 */
interface StickyRightShift<T> {
    companion object

    fun T.stickyRightShift(bits: Int): T
}

/**
 * Returns a [StickyRightShift] instance derived from [bc] and [shift].
 *
 * The implementation performs a logical right shift, then sets the least significant bit of the result if any of the
 * shifted-off bits were set. A shift count of zero is treated as the identity.
 */
fun <T> StickyRightShift.Companion.from(
    bc: BitCollection<T>,
    shift: BitShift<T>,
): StickyRightShift<T> = object : StickyRightShift<T> {
    override fun T.stickyRightShift(bits: Int): T {
        if (bits == 0) return this
        val self = this
        val shifted = with(shift) { self.logicalRightShift(bits) }
        val lowMask = with(shift) { bc.allSet.logicalRightShift(bc.sizeBits - bits) }
        val lostBits = with(bc) { self.bitwiseAnd(lowMask) }
        if (lostBits == bc.allClear) return shifted
        return with(bc) { shifted.bitwiseOr(bc.lsb) }
    }
}

private val intInstance: StickyRightShift<Int> by lazy { StickyRightShift.from(BitCollection.int, BitShift.int) }
private val longInstance: StickyRightShift<Long> by lazy { StickyRightShift.from(BitCollection.long, BitShift.long) }
private val ushortInstance: StickyRightShift<UShort> by lazy { StickyRightShift.from(UInt16, UInt16) }
private val uintInstance: StickyRightShift<UInt> by lazy { StickyRightShift.from(UInt32, UInt32) }
private val ulongInstance: StickyRightShift<ULong> by lazy { StickyRightShift.from(UInt64, UInt64) }

val StickyRightShift.Companion.int: StickyRightShift<Int> get() = intInstance
val StickyRightShift.Companion.long: StickyRightShift<Long> get() = longInstance
val StickyRightShift.Companion.ushort: StickyRightShift<UShort> get() = ushortInstance
val StickyRightShift.Companion.uint: StickyRightShift<UInt> get() = uintInstance
val StickyRightShift.Companion.ulong: StickyRightShift<ULong> get() = ulongInstance
