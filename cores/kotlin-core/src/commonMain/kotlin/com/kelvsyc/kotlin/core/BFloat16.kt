package com.kelvsyc.kotlin.core

import com.kelvsyc.kotlin.core.traits.BinaryBFloat16
import com.kelvsyc.kotlin.core.traits.FloatingPointSign
import com.kelvsyc.kotlin.core.traits.IeeeFloatingPointClassification
import com.kelvsyc.kotlin.core.traits.UInt16
import com.kelvsyc.kotlin.core.traits.ValueEquality
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.sqrt

/**
 * Value representing a 16-bit `bfloat16` format floating-point number.
 *
 * `bfloat16` uses the same 8-bit exponent as `binary32` (`Float`) but truncates the mantissa to 7 bits, giving it
 * the same dynamic range as `Float` at reduced precision. Arithmetic is performed by widening to [Float] and
 * narrowing back; use [calculate] to perform the widening and narrowing only once.
 *
 * ## Equality, hashing, and ordering
 *
 * Kotlin reserves `equals` and `hashCode` in value classes for future language use; they cannot be overridden here.
 * The compiler-generated `equals` compares raw [bits], which gives bit-pattern equality: `+0` and `-0` are not equal
 * (correct), but two NaN values with different payloads are also not equal (diverges from [Float.equals], where all
 * NaNs are equal). Use [BFloat16.equalTo] and [BFloat16.hash] when [Float]-like semantics are required.
 *
 * `BFloat16` does not implement `Comparable<BFloat16>` for the same reason as [Float16]: without overriding
 * `equals`, two NaN values would satisfy `compareTo() == 0` but `equals() == false`, violating the contract.
 * Use [BFloat16.comparator] for ordering instead.
 */
@JvmInline
value class BFloat16(val bits: Short) {
    companion object : BinaryBFloat16<BFloat16> {
        override val significandTraits: UInt16<UShort> get() = UInt16

        /**
         * [Converter] used to convert between `Float` values and the bits of a `BFloat16`.
         *
         * The forward direction truncates the lower 16 mantissa bits of the `Float` bit pattern using
         * round-to-nearest-even. NaN inputs produce a canonical quiet NaN; infinity is preserved.
         * No overflow or underflow can occur because `bfloat16` and `binary32` share the same exponent.
         *
         * The backward direction is a lossless widening: `bfloat16` is exactly the upper 16 bits of a
         * `binary32` bit pattern, so the conversion simply left-shifts and calls [Float.fromBits].
         */
        val converter: Converter<Float, Short> = Converter.of(
            forward = { f ->
                val fbits = f.toRawBits()
                val sign = (fbits ushr 16) and 0x8000
                val exp32 = (fbits ushr 23) and 0xFF
                val mantissa32 = fbits and 0x7FFFFF
                when (exp32) {
                    0xFF ->
                        // NaN → canonical quiet NaN; Infinity → infinity (preserving sign)
                        if (mantissa32 != 0) (sign or 0x7FC0).toShort()
                        else (sign or 0x7F80).toShort()
                    else -> {
                        // Normal and subnormal: same exponent, truncate mantissa from 23 to 7 bits.
                        // Round-to-nearest-even on the 16 bits dropped; use + so carry propagates into exponent.
                        val mantissa7 = mantissa32 ushr 16
                        val dropped = mantissa32 and 0xFFFF
                        val inc = if (dropped > 0x8000 || (dropped == 0x8000 && mantissa7 and 1 != 0)) 1 else 0
                        (sign or ((exp32 shl 7) + mantissa7 + inc)).toShort()
                    }
                }
            },
            backward = { s ->
                // bfloat16 is the upper 16 bits of a binary32; zero-pad the lower 16 bits.
                Float.fromBits((s.toInt() and 0xFFFF) shl 16)
            }
        )

        override val numericalEquality: ValueEquality<BFloat16> = object : ValueEquality<BFloat16> {
            override fun BFloat16.isEqualTo(other: BFloat16): Boolean = toFloat() == other.toFloat()
        }

        override val equivalenceEquality: ValueEquality<BFloat16> = object : ValueEquality<BFloat16> {
            override fun BFloat16.isEqualTo(other: BFloat16): Boolean = equalTo(this, other)
        }

        /**
         * [Comparator] implementation used to compare `BFloat16` values by widening them to [Float] and performing
         * the comparison as [Float] values.
         */
        override val comparator: Comparator<BFloat16> = compareBy { it.toFloat() }

        /**
         * [PartialComparator] implementation for `BFloat16` values that returns `null` when either operand is NaN,
         * reflecting the IEEE 754 rule that NaN is unordered with respect to every value including itself.
         * Non-NaN values are compared by widening to [Float].
         */
        override val partialComparator: PartialComparator<BFloat16> = PartialComparator { a, b ->
            if (a.isNaN() || b.isNaN()) null else comparator.compare(a, b)
        }

        /**
         * Returns `true` if [a] and [b] are numerically equal using [Float]-like semantics: all NaN payloads are
         * considered equal to each other, and `+0` is not equal to `-0`.
         *
         * @see hash
         */
        fun equalTo(a: BFloat16, b: BFloat16): Boolean {
            if (a.isNaN() || b.isNaN()) return a.isNaN() && b.isNaN()
            return a.bits == b.bits
        }

        /**
         * Returns a hash code consistent with [equalTo]: all NaN values produce the same hash, and `+0` and `-0`
         * produce different hashes.
         *
         * @see equalTo
         */
        fun hash(value: BFloat16): Int = if (value.isNaN()) 0x7FC0 else value.bits.toInt() and 0xFFFF

        /**
         * Returns a `BFloat16` with the magnitude of [magnitude] and the sign of [sign].
         *
         * This is a pure bit-level operation: the sign bit of [magnitude] is replaced with the sign bit of [sign]
         * without any numeric conversion or rounding. Works correctly for NaN, infinity, and both zeros.
         */
        fun copySign(magnitude: BFloat16, sign: BFloat16): BFloat16 {
            val signBit = sign.bits.toInt() and 0x8000
            val absBits = magnitude.bits.toInt() and 0x7FFF
            return BFloat16((signBit or absBits).toShort())
        }

        /**
         * Returns the smaller of [a] and [b] using [Float]-like comparison.
         *
         * Returns [NaN] if either argument is NaN. Returns `-0` over `+0` when both are zero.
         */
        fun min(a: BFloat16, b: BFloat16): BFloat16 = BFloat16(kotlin.math.min(a.toFloat(), b.toFloat()))

        /**
         * Returns the larger of [a] and [b] using [Float]-like comparison.
         *
         * Returns [NaN] if either argument is NaN. Returns `+0` over `-0` when both are zero.
         */
        fun max(a: BFloat16, b: BFloat16): BFloat16 = BFloat16(kotlin.math.max(a.toFloat(), b.toFloat()))

        /** A canonical quiet NaN value. Note that there are 63 other NaN bit patterns; see [equalTo]. */
        override val NaN: BFloat16 = BFloat16(0x7FC0.toShort())

        /** Positive infinity. */
        val POSITIVE_INFINITY: BFloat16 = BFloat16(0x7F80.toShort())

        /** Negative infinity. */
        val NEGATIVE_INFINITY: BFloat16 = BFloat16(0xFF80.toShort())

        /** The largest finite `BFloat16` value, approximately 3.39×10³⁸ (same magnitude as [Float.MAX_VALUE]). */
        val MAX_VALUE: BFloat16 = BFloat16(0x7F7F.toShort())

        /**
         * The smallest positive `BFloat16` value, equal to 2⁻¹³³.
         *
         * This is a subnormal value. For the smallest positive *normal* value see [MIN_NORMAL].
         */
        val MIN_VALUE: BFloat16 = BFloat16(0x0001.toShort())

        /**
         * The smallest positive normal `BFloat16` value, equal to 2⁻¹²⁶ (same as [Float.MIN_NORMAL]).
         *
         * Values smaller than this are represented as subnormals with reduced precision.
         */
        val MIN_NORMAL: BFloat16 = BFloat16(0x0080.toShort())

        /**
         * The difference between 1.0 and the next representable value, equal to 2⁻⁷ ≈ 0.0078125.
         *
         * This is the machine epsilon for `BFloat16`: the relative precision of the format near 1.0.
         */
        val EPSILON: BFloat16 = BFloat16(0x3C00.toShort())

        override val positiveInfinity: BFloat16 get() = POSITIVE_INFINITY
        override val negativeInfinity: BFloat16 get() = NEGATIVE_INFINITY
        override val positiveZero: BFloat16 get() = BFloat16(0)
        override val negativeZero: BFloat16 get() = BFloat16(0x8000.toShort())
        override val maxValue: BFloat16 get() = MAX_VALUE
        override val minValue: BFloat16 get() = MIN_VALUE
        override val minNormal: BFloat16 get() = MIN_NORMAL
        override val epsilon: BFloat16 get() = EPSILON

        override val classification: IeeeFloatingPointClassification<BFloat16> =
            object : IeeeFloatingPointClassification<BFloat16> {
                override fun BFloat16.isNaN(): Boolean = this.isNaN()
                override fun BFloat16.isInfinite(): Boolean = this.isInfinite()
                override fun BFloat16.isFinite(): Boolean = this.isFinite()
                override fun BFloat16.isZero(): Boolean = this.isZero()
                override fun BFloat16.isNormal(): Boolean = this.isNormal()
                override fun BFloat16.isSubnormal(): Boolean = this.isSubnormal()
            }

        override val sign: FloatingPointSign<BFloat16> = object : FloatingPointSign<BFloat16> {
            override fun BFloat16.isNegative(): Boolean = this.sign
            override fun BFloat16.negate(): BFloat16 = -this
            override fun BFloat16.abs(): BFloat16 = this.abs()
            override fun BFloat16.copySign(other: BFloat16): BFloat16 =
                BFloat16(((bits.toInt() and 0x7FFF) or (other.bits.toInt() and 0x8000)).toShort())
        }
    }

    /**
     * Initializes a `BFloat16` value by narrowing the supplied [Float] value using round-to-nearest-even.
     */
    constructor(value: Float) : this(converter(value))

    /** `true` for negative values and negative zero; `false` for positive values, positive zero, and NaN. */
    val sign: Boolean
        get() = bits.toInt() < 0

    /**
     * The 8-bit biased exponent field, in the range 0..255.
     *
     * The unbiased exponent is `biasedExponent - 127` for normal values. A biased exponent of 0 indicates zero or
     * a subnormal; 255 indicates infinity or NaN.
     */
    val biasedExponent: Int
        get() = (bits.toInt() and 0x7F80) ushr 7

    /**
     * The 7-bit mantissa (fraction) field, in the range 0..127.
     *
     * For normal values the implicit leading 1 bit is not included; the full significand is `128 + mantissa`.
     * For subnormals the leading bit is not implicit: the full significand is just `mantissa`.
     */
    val mantissa: Int
        get() = bits.toInt() and 0x007F

    /** Returns `true` if this value is Not-a-Number (NaN). */
    fun isNaN(): Boolean = biasedExponent == 255 && mantissa != 0

    /** Returns `true` if this value is positive or negative infinity. */
    fun isInfinite(): Boolean = biasedExponent == 255 && mantissa == 0

    /** Returns `true` if this value is finite (not infinity or NaN). */
    fun isFinite(): Boolean = biasedExponent != 255

    /** Returns `true` if this value is positive or negative zero. */
    fun isZero(): Boolean = bits.toInt() and 0x7FFF == 0

    /**
     * Returns `true` if this value is subnormal: non-zero but smaller than [MIN_NORMAL].
     *
     * Subnormal values have a fixed exponent of 2⁻¹²⁶ and no implicit leading 1 bit, giving them reduced precision.
     */
    fun isSubnormal(): Boolean = biasedExponent == 0 && !isZero()

    /**
     * Returns `true` if this value is a normal floating-point number: finite, non-zero, and not subnormal.
     */
    fun isNormal(): Boolean = biasedExponent in 1..254

    /** Returns this value with its sign bit flipped. */
    operator fun unaryMinus() = BFloat16((bits.toInt() xor 0x8000).toShort())

    /** Returns the absolute value, clearing the sign bit without any rounding or conversion. */
    fun abs() = BFloat16((bits.toInt() and 0x7FFF).toShort())

    /**
     * Performs an operation on this value by first widening it to [Float], then narrowing it back in the end.
     */
    fun calculate(fn: (Float) -> Float) = BFloat16(fn(converter.reverse(bits)))

    /** Converts this value to a [Float] through a lossless widening (zero-padding the lower 16 mantissa bits). */
    fun toFloat() = converter.reverse(bits)

    /** Converts this value to a [Double] through a widening process. */
    fun toDouble() = toFloat().toDouble()

    /**
     * Converts this value to an [Int] by truncating toward zero.
     *
     * Follows the same semantics as [Float.toInt]: infinity and out-of-range values clamp; NaN produces `0`.
     */
    fun toInt() = toFloat().toInt()

    /**
     * Converts this value to a [Long] by truncating toward zero.
     */
    fun toLong() = toFloat().toLong()

    /**
     * Converts this value to a [Byte] by truncating toward zero and then narrowing to 8 bits.
     *
     * Values outside the `Byte` range wrap due to narrowing; prefer [toInt] if clamping is needed.
     */
    fun toByte() = toFloat().toInt().toByte()

    /**
     * Converts this value to a [Short] integer by truncating toward zero and then narrowing to 16 bits.
     *
     * This returns the numeric value as an integer — not the raw bit pattern. Use [bits] or [toRawBits] for
     * the bfloat16 representation.
     */
    fun toShort() = toFloat().toInt().toShort()

    /** Returns the decimal representation of this value, identical to what [toFloat] would produce. */
    override fun toString(): String = toFloat().toString()

    /**
     * Returns the bit pattern of this value as a [Short], with all NaN payloads normalised to [NaN].
     *
     * Two values that are [equalTo] each other always return the same `toBits()` result.
     * Use [toRawBits] to preserve NaN payloads.
     */
    fun toBits(): Short = if (isNaN()) NaN.bits else bits

    /**
     * Returns the raw bit pattern of this value as a [Short], identical to [bits].
     *
     * Unlike [toBits], NaN payloads are preserved exactly.
     */
    fun toRawBits(): Short = bits

    operator fun plus(other: BFloat16) = calculate { it + other.toFloat() }
    operator fun minus(other: BFloat16) = calculate { it - other.toFloat() }
    operator fun times(other: BFloat16) = calculate { it * other.toFloat() }
    operator fun div(other: BFloat16) = calculate { it / other.toFloat() }
    operator fun rem(other: BFloat16) = calculate { it % other.toFloat() }

    /** Returns the positive square root of this value. Returns [NaN] if negative or NaN. */
    fun sqrt(): BFloat16 = calculate { sqrt(it) }

    /** Returns the largest `BFloat16` not greater than this value that equals a mathematical integer. */
    fun floor(): BFloat16 = calculate { floor(it) }

    /** Returns the smallest `BFloat16` not less than this value that equals a mathematical integer. */
    fun ceil(): BFloat16 = calculate { ceil(it) }

    /** Returns this value rounded to the nearest integer, using round-half-to-even for ties. */
    fun round(): BFloat16 = calculate { round(it.toDouble()).toFloat() }

    /**
     * Returns the size of one unit in the last place (ULP) for this value.
     *
     * Special cases follow [Float.ulp]:
     * - NaN → [NaN]
     * - ±infinity → [POSITIVE_INFINITY]
     * - ±0 or any subnormal → [MIN_VALUE]
     */
    fun ulp(): BFloat16 {
        if (isNaN()) return NaN
        if (biasedExponent == 255) return POSITIVE_INFINITY
        if (biasedExponent == 0) return MIN_VALUE
        val ulpExp = biasedExponent - 7
        return if (ulpExp > 0)
            BFloat16((ulpExp shl 7).toShort())
        else
            BFloat16((1 shl (biasedExponent - 1)).toShort())
    }

    /**
     * Returns the smallest representable `BFloat16` value strictly greater than this value.
     *
     * Special cases:
     * - NaN → [NaN]
     * - [POSITIVE_INFINITY] → [POSITIVE_INFINITY]
     * - [NEGATIVE_INFINITY] → negative [MAX_VALUE]
     * - ±0 → [MIN_VALUE]
     */
    fun nextUp(): BFloat16 {
        if (isNaN()) return NaN
        val i = bits.toInt() and 0xFFFF
        return when {
            i == 0x7F80 -> POSITIVE_INFINITY
            i == 0x0000 || i == 0x8000 -> MIN_VALUE
            i < 0x8000 -> BFloat16((i + 1).toShort())
            else -> BFloat16((i - 1).toShort())
        }
    }

    /**
     * Returns the largest representable `BFloat16` value strictly less than this value.
     *
     * Implemented as `-(-this).nextUp()`.
     *
     * Special cases:
     * - NaN → [NaN]
     * - [NEGATIVE_INFINITY] → [NEGATIVE_INFINITY]
     * - [POSITIVE_INFINITY] → positive [MAX_VALUE]
     * - ±0 → negative [MIN_VALUE]
     */
    fun nextDown(): BFloat16 {
        if (isNaN()) return NaN
        return -(-this).nextUp()
    }

    /**
     * Returns the representable `BFloat16` value adjacent to this value in the direction of [other].
     *
     * If this value and [other] are equal, [other] is returned unchanged. If either operand is NaN, [NaN] is
     * returned.
     */
    fun nextTowards(other: BFloat16): BFloat16 {
        if (isNaN() || other.isNaN()) return NaN
        val cmp = comparator.compare(this, other)
        return when {
            cmp == 0 -> other
            cmp < 0 -> nextUp()
            else -> nextDown()
        }
    }
}
