package com.kelvsyc.kotlin.core.traits.integral

/**
 * `RoundingRightShift` is a trait type providing a half-even (round-half-to-even) logical right shift on
 * values of type [T].
 *
 * A rounding right shift of `x` by `n` bits proceeds as follows:
 *
 * 1. Compute the truncated quotient `q = x >>> n`.
 * 2. Inspect the shifted-off bits:
 *    - **Round bit** — bit `n-1` of `x`, the most significant shifted-off bit.
 *    - **Sticky bits** — bits `0..n-2` of `x`; OR'd together they form the sticky flag.
 * 3. Round:
 *    - Round bit = 0 → return `q` (strictly below halfway).
 *    - Round bit = 1, any sticky bit set → return `q + 1` (strictly above halfway).
 *    - Round bit = 1, all sticky bits clear (exactly halfway) → return `q` if `q` is even,
 *      `q + 1` if `q` is odd (round to nearest even).
 *
 * An implementation can be derived from [BitCollection], [BitShift], and [IntegerArithmetic]
 * via [Companion.from].
 */
interface RoundingRightShift<T> {
    companion object

    fun T.roundingRightShift(bits: Int): T
}

/**
 * Returns a [RoundingRightShift] instance derived from [bc], [shift], and [arith].
 *
 * The round and sticky bits are extracted with bitwise masks built from [BitCollection.allSet],
 * [BitCollection.lsb], and [BitShift.logicalRightShift]. The `+1` rounding step uses
 * [IntegerArithmetic.add] and [IntegerArithmetic.one] to carry correctly across all bit positions.
 * A shift count of zero is treated as the identity.
 */
fun <T> RoundingRightShift.Companion.from(
    bc: BitCollection<T>,
    shift: BitShift<T>,
    arith: IntegerArithmetic<T>,
): RoundingRightShift<T> = object : RoundingRightShift<T> {
    override fun T.roundingRightShift(bits: Int): T {
        if (bits == 0) return this
        val self = this
        val shifted = with(shift) { self.logicalRightShift(bits) }

        // Mask covering all bits shifted off (positions 0..bits-1).
        val lostMask = with(shift) { bc.allSet.logicalRightShift(bc.sizeBits - bits) }
        // Mask for sticky bits (positions 0..bits-2): everything below the round bit.
        // lostMask AND (lostMask >>> 1) clears the MSB of lostMask, leaving lower bits only.
        val stickyMask = with(bc) { lostMask.bitwiseAnd(with(shift) { lostMask.logicalRightShift(1) }) }
        // Round bit mask: the single bit at position bits-1 (MSB of the lost region).
        val roundBitMask = with(bc) { lostMask.bitwiseXor(stickyMask) }

        // Below halfway: round bit is clear, truncate.
        val roundBit = with(bc) { self.bitwiseAnd(roundBitMask) }
        if (roundBit == bc.allClear) return shifted

        // Above halfway: any sticky bit set, round up.
        val sticky = with(bc) { self.bitwiseAnd(stickyMask) }
        if (sticky != bc.allClear) return with(arith) { shifted.add(one) }

        // Exactly halfway: round to even — increment only if shifted is odd.
        val isOdd = with(bc) { shifted.bitwiseAnd(bc.lsb) } != bc.allClear
        return if (isOdd) with(arith) { shifted.add(one) } else shifted
    }
}

private val byteInstance: RoundingRightShift<Byte> by lazy { RoundingRightShift.from(Int8, Int8, IntegerArithmetic.byte) }
private val shortInstance: RoundingRightShift<Short> by lazy { RoundingRightShift.from(Int16, Int16, IntegerArithmetic.short) }
private val intInstance: RoundingRightShift<Int> by lazy { RoundingRightShift.from(BitCollection.int, BitShift.int, IntegerArithmetic.int) }
private val longInstance: RoundingRightShift<Long> by lazy { RoundingRightShift.from(BitCollection.long, BitShift.long, IntegerArithmetic.long) }
private val ubyteInstance: RoundingRightShift<UByte> by lazy { RoundingRightShift.from(UInt8, UInt8, IntegerArithmetic.ubyte) }
private val ushortInstance: RoundingRightShift<UShort> by lazy { RoundingRightShift.from(UInt16, UInt16, IntegerArithmetic.ushort) }
private val uintInstance: RoundingRightShift<UInt> by lazy { RoundingRightShift.from(UInt32, UInt32, IntegerArithmetic.uint) }
private val ulongInstance: RoundingRightShift<ULong> by lazy { RoundingRightShift.from(UInt64, UInt64, IntegerArithmetic.ulong) }

val RoundingRightShift.Companion.byte: RoundingRightShift<Byte> get() = byteInstance
val RoundingRightShift.Companion.short: RoundingRightShift<Short> get() = shortInstance
val RoundingRightShift.Companion.int: RoundingRightShift<Int> get() = intInstance
val RoundingRightShift.Companion.long: RoundingRightShift<Long> get() = longInstance
val RoundingRightShift.Companion.ubyte: RoundingRightShift<UByte> get() = ubyteInstance
val RoundingRightShift.Companion.ushort: RoundingRightShift<UShort> get() = ushortInstance
val RoundingRightShift.Companion.uint: RoundingRightShift<UInt> get() = uintInstance
val RoundingRightShift.Companion.ulong: RoundingRightShift<ULong> get() = ulongInstance
