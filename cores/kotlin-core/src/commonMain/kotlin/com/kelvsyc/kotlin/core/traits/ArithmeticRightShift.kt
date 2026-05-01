package com.kelvsyc.kotlin.core.traits

/**
 * `ArithmeticRightShift` is a trait type providing a uniform interface for arithmetic right shift operations on values
 * of type [T].
 *
 * An arithmetic right shift, or a signed right shift, is a right shift where the shifted-in bits are filled with the
 * most significant bit of the original value. For two's-complement representations this is equivalent to division by
 * a power of 2, rounding toward negative infinity.
 *
 * An artificial implementation can be derived from [BitCollection] and [BitShift] via [Companion.from].
 */
interface ArithmeticRightShift<T> {
    companion object

    fun T.arithmeticRightShift(bits: Int): T
}

private val intInstance: ArithmeticRightShift<Int> = object : ArithmeticRightShift<Int> {
    override fun Int.arithmeticRightShift(bits: Int): Int = this shr bits
}

private val longInstance: ArithmeticRightShift<Long> = object : ArithmeticRightShift<Long> {
    override fun Long.arithmeticRightShift(bits: Int): Long = this shr bits
}

/**
 * Returns an [ArithmeticRightShift] instance derived from [bc] and [shift].
 *
 * The implementation performs a logical right shift, then fills the vacated high bits with the original sign bit
 * (the [BitCollection.msb] value). For types with two's-complement representation this matches the native
 * arithmetic right shift semantics.
 */
fun <T> ArithmeticRightShift.Companion.from(
    bc: BitCollection<T>,
    shift: BitShift<T>,
): ArithmeticRightShift<T> = object : ArithmeticRightShift<T> {
    override fun T.arithmeticRightShift(bits: Int): T {
        val self = this
        val shifted = with(shift) { self.logicalRightShift(bits) }
        val signSet = with(bc) { self.bitwiseAnd(bc.msb) } != bc.allClear
        if (!signSet) return shifted
        val fillMask = with(bc) { with(shift) { bc.allSet.logicalRightShift(bits) }.invert() }
        return with(bc) { shifted.bitwiseOr(fillMask) }
    }
}

val ArithmeticRightShift.Companion.int: ArithmeticRightShift<Int> get() = intInstance
val ArithmeticRightShift.Companion.long: ArithmeticRightShift<Long> get() = longInstance
val ArithmeticRightShift.Companion.short: ArithmeticRightShift<Short> get() = Int16
