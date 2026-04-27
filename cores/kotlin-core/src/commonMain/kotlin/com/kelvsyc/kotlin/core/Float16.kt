package com.kelvsyc.kotlin.core

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.sqrt

/**
 * Value representing a 16-bit `binary16` floating-point value.
 *
 * Arithmetic operations are performed by widening the values and narrowing the result back to a `Float16`. This may
 * result in a loss of precision over multiple operations; to avoid this, [calculate] should be used to only perform
 * the widening and narrowing operations once.
 *
 * ## Equality, hashing, and ordering
 *
 * Kotlin reserves `equals` and `hashCode` in value classes for future language use; they cannot be overridden here.
 * The compiler-generated `equals` compares raw [bits], which gives bit-pattern equality: `+0` and `-0` are not equal
 * (correct), but two NaN values with different payloads are also not equal (diverges from [Float.equals], where all
 * NaNs are equal). Use [Float16.equalTo] and [Float16.hash] when [Float]-like semantics are required, for example
 * when implementing a data structure that must treat all NaN values identically.
 *
 * `Float16` does not implement `Comparable<Float16>` for the same reason. [Float] can implement `Comparable` because
 * its overridden `equals` normalises all NaN payloads to equal, keeping `compareTo() == 0` consistent with
 * `equals() == true`. Without that override, two `Float16` NaN values with different payloads would satisfy
 * `compareTo() == 0` but `equals() == false`, violating the `Comparable` contract. Use [Float16.comparator] for
 * ordering instead.
 */
@JvmInline
value class Float16(val bits: Short) {
    companion object {
        /**
         * [Converter] used to convert between `Float` values and the bits of a `Float16`.
         *
         * The forward direction is a narrowing conversion that uses round-to-nearest-even, matching the IEEE 754
         * default rounding mode. NaN inputs produce a canonical quiet NaN; values too large to represent become
         * infinity; values too small to represent become ±0.
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
                        if (mantissa32 != 0) (sign or 0x7E00).toShort()
                        else (sign or 0x7C00).toShort()
                    in 143..254 ->
                        // Overflow: magnitude too large for Float16 → ±infinity
                        (sign or 0x7C00).toShort()
                    in 113..142 -> {
                        // Normal Float16 range. Rebias exponent: exp16 = exp32 - 127 + 15 = exp32 - 112.
                        // Round-to-nearest-even on the 13 bits dropped from the mantissa.
                        // Use + (not or) to allow a rounding carry to propagate into the exponent field.
                        val exp16 = (exp32 - 112) shl 10
                        val mantissa16 = mantissa32 ushr 13
                        val dropped = mantissa32 and 0x1FFF
                        val inc = if (dropped > 0x1000 || (dropped == 0x1000 && mantissa16 and 1 != 0)) 1 else 0
                        (sign or (exp16 + mantissa16 + inc)).toShort()
                    }
                    in 103..112 -> {
                        // Float16 subnormal range. The implicit leading 1 bit is shifted into the 10-bit mantissa.
                        // shift ranges from 14 (exp32=112) to 23 (exp32=103).
                        val shift = 126 - exp32
                        val full = 0x800000 or mantissa32
                        val mantissa16 = full ushr shift
                        val dropped = full and ((1 shl shift) - 1)
                        val halfPoint = 1 shl (shift - 1)
                        val inc = if (dropped > halfPoint || (dropped == halfPoint && mantissa16 and 1 != 0)) 1 else 0
                        (sign or (mantissa16 + inc)).toShort()
                    }
                    else ->
                        // Underflow: Float32 subnormals and very small normals (exp32 < 103) are below Float16's
                        // minimum subnormal, so they round to ±0.
                        sign.toShort()
                }
            },
            backward = { s ->
                val bits = s.toInt() and 0xFFFF
                val sign = (bits and 0x8000) shl 16
                val exp16 = (bits ushr 10) and 0x1F
                val mantissa16 = bits and 0x03FF
                when (exp16) {
                    0x1F ->
                        // Infinity or NaN (exponent all-ones). Preserve the sign and payload.
                        if (mantissa16 != 0) Float.fromBits(sign or 0x7FC00000 or (mantissa16 shl 13))
                        else Float.fromBits(sign or 0x7F800000)
                    0 ->
                        // Zero or subnormal. Subnormals are normalized when widened: find the leading bit
                        // position k, then reconstruct a normal Float32 with the appropriate exponent.
                        if (mantissa16 == 0) Float.fromBits(sign)
                        else {
                            val k = 31 - mantissa16.countLeadingZeroBits()
                            val exp32 = (k + 103) shl 23   // unbiased = k - 24; rebiased for Float32 = k + 103
                            val fraction = (mantissa16 and ((1 shl k) - 1)) shl (23 - k)
                            Float.fromBits(sign or exp32 or fraction)
                        }
                    else ->
                        // Normal Float16. Rebias exponent: exp32 = exp16 + 127 - 15 = exp16 + 112.
                        // Widen the 10-bit mantissa to 23 bits by shifting left 13 positions.
                        Float.fromBits(sign or ((exp16 + 112) shl 23) or (mantissa16 shl 13))
                }
            }
        )

        /**
         * [Comparator] implementation used to compare `Float16` values by widening them to [Float] and performing the
         * comparison as [Float] values.
         */
        val comparator: Comparator<Float16> = compareBy { it.toFloat() }

        /**
         * [PartialComparator] implementation for `Float16` values that returns `null` when either operand is NaN,
         * reflecting the IEEE 754 rule that NaN is unordered with respect to every value including itself.
         * Non-NaN values are compared by widening to [Float].
         */
        val partialComparator: PartialComparator<Float16> = PartialComparator { a, b ->
            if (a.isNaN() || b.isNaN()) null else comparator.compare(a, b)
        }

        /**
         * Returns `true` if [a] and [b] are numerically equal using [Float]-like semantics: all NaN payloads are
         * considered equal to each other, and `+0` is not equal to `-0`.
         *
         * Use this instead of `==` when NaN payload differences should not affect equality, for example in
         * `equals`/`hashCode` implementations of types that embed a [Float16].
         *
         * @see hash
         */
        fun equalTo(a: Float16, b: Float16): Boolean {
            if (a.isNaN() || b.isNaN()) return a.isNaN() && b.isNaN()
            return a.bits == b.bits
        }

        /**
         * Returns a hash code consistent with [equalTo]: all NaN values produce the same hash, and `+0` and `-0`
         * produce different hashes.
         *
         * @see equalTo
         */
        fun hash(value: Float16): Int = if (value.isNaN()) 0x7E00 else value.bits.toInt() and 0xFFFF

        /**
         * Returns a `Float16` with the magnitude of [magnitude] and the sign of [sign].
         *
         * This is a pure bit-level operation: the sign bit of [magnitude] is replaced with the sign bit of [sign]
         * without any numeric conversion or rounding. Works correctly for NaN, infinity, and both zeros.
         */
        fun copySign(magnitude: Float16, sign: Float16): Float16 {
            val signBit = sign.bits.toInt() and 0x8000
            val absBits = magnitude.bits.toInt() and 0x7FFF
            return Float16((signBit or absBits).toShort())
        }

        /**
         * Returns the smaller of [a] and [b] using [Float]-like comparison.
         *
         * Returns [NaN] if either argument is NaN. Returns `-0` over `+0` when both are zero, consistent with
         * IEEE 754 `minNum`.
         */
        fun min(a: Float16, b: Float16): Float16 = Float16(kotlin.math.min(a.toFloat(), b.toFloat()))

        /**
         * Returns the larger of [a] and [b] using [Float]-like comparison.
         *
         * Returns [NaN] if either argument is NaN. Returns `+0` over `-0` when both are zero, consistent with
         * IEEE 754 `maxNum`.
         */
        fun max(a: Float16, b: Float16): Float16 = Float16(kotlin.math.max(a.toFloat(), b.toFloat()))

        /** A canonical quiet NaN value. Note that there are 1023 other NaN bit patterns; see [equalTo]. */
        val NaN: Float16 = Float16(0x7E00.toShort())

        /** Positive infinity: a value greater than [MAX_VALUE]. */
        val POSITIVE_INFINITY: Float16 = Float16(0x7C00.toShort())

        /** Negative infinity: a value less than negative [MAX_VALUE]. */
        val NEGATIVE_INFINITY: Float16 = Float16(0xFC00.toShort())

        /** The largest finite `Float16` value, equal to 65504. */
        val MAX_VALUE: Float16 = Float16(0x7BFF.toShort())

        /**
         * The smallest positive `Float16` value, equal to 2⁻²⁴ ≈ 5.96×10⁻⁸.
         *
         * This is a subnormal value. For the smallest positive *normal* value see [MIN_NORMAL].
         */
        val MIN_VALUE: Float16 = Float16(0x0001.toShort())

        /**
         * The smallest positive normal `Float16` value, equal to 2⁻¹⁴ ≈ 6.10×10⁻⁵.
         *
         * Values smaller than this are represented as subnormals and have reduced precision.
         */
        val MIN_NORMAL: Float16 = Float16(0x0400.toShort())

        /**
         * The difference between 1.0 and the next representable value, equal to 2⁻¹⁰ ≈ 0.000977.
         *
         * This is the machine epsilon for `Float16`: the relative precision of the format near 1.0.
         */
        val EPSILON: Float16 = Float16(0x1400.toShort())
    }

    /**
     * Initializes a `Float16` value by narrowing the supplied [Float] value.
     */
    constructor(value: Float) : this(converter(value))

    /** `true` for negative values and negative zero; `false` for positive values, positive zero, and NaN. */
    val sign: Boolean
        get() = bits.toInt() < 0

    /**
     * The 5-bit biased exponent field, in the range 0..31.
     *
     * The unbiased exponent is `biasedExponent - 15` for normal values. A biased exponent of 0 indicates zero or
     * a subnormal; 31 indicates infinity or NaN.
     */
    val biasedExponent: Int
        get() = (bits.toInt() and 0x7C00) shr 10

    /**
     * The 10-bit mantissa (fraction) field, in the range 0..1023.
     *
     * For normal values the implicit leading 1 bit is not included; the full significand is `1024 + mantissa`.
     * For subnormals the leading bit is not implicit: the full significand is just `mantissa`.
     */
    val mantissa: Int
        get() = bits.toInt() and 0x03FF

    /** Returns `true` if this value is Not-a-Number (NaN). NaN is unordered and not equal to any value including itself. */
    fun isNaN(): Boolean = biasedExponent == 31 && mantissa != 0

    /** Returns `true` if this value is positive or negative infinity. */
    fun isInfinite(): Boolean = biasedExponent == 31 && mantissa == 0

    /** Returns `true` if this value is finite (not infinity or NaN). */
    fun isFinite(): Boolean = biasedExponent != 31

    /** Returns `true` if this value is positive or negative zero. Both zeros compare equal via [comparator]. */
    fun isZero(): Boolean = bits.toInt() and 0x7FFF == 0

    /**
     * Returns `true` if this value is subnormal: non-zero but smaller than [MIN_NORMAL].
     *
     * Subnormal values have a fixed exponent of 2⁻¹⁴ and no implicit leading 1 bit, giving them reduced but
     * non-zero precision down to [MIN_VALUE].
     */
    fun isSubnormal(): Boolean = biasedExponent == 0 && !isZero()

    /**
     * Returns `true` if this value is a normal floating-point number: finite, non-zero, and not subnormal.
     *
     * Normal values have a biased exponent in 1..30 and carry the full 10-bit mantissa precision. Values outside
     * the normal range are zero, subnormal, infinite, or NaN.
     */
    fun isNormal(): Boolean = biasedExponent in 1..30

    /** Returns this value with its sign bit flipped. */
    operator fun unaryMinus() = Float16((bits.toInt() xor 0x8000).toShort())

    /** Returns the absolute value, clearing the sign bit without any rounding or conversion. */
    fun abs() = Float16((bits.toInt() and 0x7FFF).toShort())

    /**
     * Performs an operation on this value by first widening it to [Float], then narrowing it back in the end.
     */
    fun calculate(fn: (Float) -> Float) = Float16(fn(converter.reverse(bits)))

    /**
     * Converts this value to a [Float] through a widening process.
     */
    fun toFloat() = converter.reverse(bits)

    /**
     * Converts this value to a [Double] through a widening process.
     */
    fun toDouble() = toFloat().toDouble()

    /**
     * Converts this value to an [Int] by truncating toward zero.
     *
     * Follows the same semantics as [Float.toInt]: infinity and values out of range clamp to [Int.MAX_VALUE] or
     * [Int.MIN_VALUE]; NaN produces `0`.
     */
    fun toInt() = toFloat().toInt()

    /**
     * Converts this value to a [Long] by truncating toward zero.
     *
     * Follows the same semantics as [Float.toLong]: infinity and values out of range clamp to [Long.MAX_VALUE] or
     * [Long.MIN_VALUE]; NaN produces `0`.
     */
    fun toLong() = toFloat().toLong()

    /**
     * Converts this value to a [Byte] by truncating toward zero and then narrowing to 8 bits.
     *
     * Values outside the `Byte` range wrap due to the narrowing; prefer [toInt] if clamping is needed.
     */
    fun toByte() = toFloat().toInt().toByte()

    /**
     * Converts this value to a [Short] integer by truncating toward zero and then narrowing to 16 bits.
     *
     * This returns the numeric value as an integer — not the raw bit pattern. Use [bits] or [toRawBits] for
     * the binary16 representation.
     */
    fun toShort() = toFloat().toInt().toShort()

    /**
     * Returns the decimal representation of this value, identical to what [toFloat] would produce.
     */
    override fun toString(): String = toFloat().toString()

    /**
     * Returns the bit pattern of this value as a [Short], with all NaN payloads normalised to [NaN].
     *
     * Two values that are [equalTo] each other always return the same `toBits()` result, making this suitable
     * for use in `hashCode` implementations. Use [toRawBits] to preserve NaN payloads.
     */
    fun toBits(): Short = if (isNaN()) NaN.bits else bits

    /**
     * Returns the raw bit pattern of this value as a [Short], identical to [bits].
     *
     * Unlike [toBits], NaN payloads are preserved exactly. Use this when the exact bit pattern matters, for
     * example when serialising Float16 data.
     */
    fun toRawBits(): Short = bits

    operator fun plus(other: Float16) = calculate { it + other.toFloat() }
    operator fun minus(other: Float16) = calculate { it - other.toFloat() }
    operator fun times(other: Float16) = calculate { it * other.toFloat() }
    operator fun div(other: Float16) = calculate { it / other.toFloat() }
    operator fun rem(other: Float16) = calculate { it % other.toFloat() }

    /**
     * Returns the positive square root of this value.
     *
     * Returns [NaN] if this value is negative or NaN; returns [POSITIVE_INFINITY] if this value is infinite.
     */
    fun sqrt(): Float16 = calculate { sqrt(it) }

    /**
     * Returns the largest `Float16` not greater than this value that is equal to a mathematical integer.
     *
     * Special values (NaN, ±infinity, ±0) are returned unchanged.
     */
    fun floor(): Float16 = calculate { floor(it) }

    /**
     * Returns the smallest `Float16` not less than this value that is equal to a mathematical integer.
     *
     * Special values (NaN, ±infinity, ±0) are returned unchanged.
     */
    fun ceil(): Float16 = calculate { ceil(it) }

    /**
     * Returns this value rounded to the nearest integer, using round-half-to-even for ties.
     *
     * Special values (NaN, ±infinity, ±0) are returned unchanged.
     */
    fun round(): Float16 = calculate { round(it.toDouble()).toFloat() }

    /**
     * Returns the size of one unit in the last place (ULP) for this value: the magnitude of the least significant
     * bit in this value's representation, which equals the gap between this value and the nearest adjacent
     * representable value of greater magnitude.
     *
     * Concretely: if you add or subtract the returned value to `this`, you move to the next representable
     * `Float16` in that direction. For subnormal values and zero, all ULPs are equal to [MIN_VALUE] because the
     * exponent is fixed. For normal values the ULP grows as the magnitude grows, which is why arithmetic on large
     * floats loses more absolute precision than arithmetic on small floats.
     *
     * Special cases follow [Float.ulp]:
     * - NaN → [NaN]
     * - ±[POSITIVE_INFINITY] or ±[NEGATIVE_INFINITY] → [POSITIVE_INFINITY]
     * - ±0 or any subnormal → [MIN_VALUE]
     */
    fun ulp(): Float16 {
        if (isNaN()) return NaN
        if (biasedExponent == 31) return POSITIVE_INFINITY
        if (biasedExponent == 0) return MIN_VALUE          // ±0 and subnormals share the same ULP
        val ulpExp = biasedExponent - 10
        return if (ulpExp > 0)
            Float16((ulpExp shl 10).toShort())             // normal ULP: clear mantissa, reduce exp by 10
        else
            Float16((1 shl (biasedExponent - 1)).toShort()) // ULP is itself subnormal
    }

    /**
     * Returns the smallest representable `Float16` value that is strictly greater than this value.
     *
     * This works because IEEE 754 bit patterns for non-NaN values are monotone with their numeric order: for
     * positive values, incrementing [bits] by one moves to the next float; for negative values, decrementing
     * [bits] by one does the same (moving toward zero, i.e. toward +∞). Both zeros are treated identically —
     * the next value above zero is [MIN_VALUE].
     *
     * Special cases:
     * - NaN → [NaN]
     * - [POSITIVE_INFINITY] → [POSITIVE_INFINITY] (no value is larger)
     * - [NEGATIVE_INFINITY] → negative [MAX_VALUE]
     * - ±0 → [MIN_VALUE]
     */
    fun nextUp(): Float16 {
        if (isNaN()) return NaN
        val i = bits.toInt() and 0xFFFF
        return when {
            i == 0x7C00 -> POSITIVE_INFINITY               // +∞ has no successor
            i == 0x0000 || i == 0x8000 -> MIN_VALUE        // ±0 → smallest positive
            i < 0x8000 -> Float16((i + 1).toShort())       // positive finite: increment bits
            else -> Float16((i - 1).toShort())             // negative (incl. −∞): decrement bits toward zero
        }
    }

    /**
     * Returns the largest representable `Float16` value that is strictly less than this value.
     *
     * Implemented as `-(-this).nextUp()`, mirroring the relationship between the positive and negative halves
     * of the Float16 number line.
     *
     * Special cases:
     * - NaN → [NaN]
     * - [NEGATIVE_INFINITY] → [NEGATIVE_INFINITY] (no value is smaller)
     * - [POSITIVE_INFINITY] → positive [MAX_VALUE]
     * - ±0 → negative [MIN_VALUE]
     */
    fun nextDown(): Float16 {
        if (isNaN()) return NaN
        return -(-this).nextUp()
    }

    /**
     * Returns the representable `Float16` value adjacent to this value in the direction of [other].
     *
     * If this value and [other] are equal, [other] is returned unchanged (so that, for example,
     * `nextTowards(+0, -0)` returns `-0`). If either operand is NaN, [NaN] is returned.
     *
     * This is the `Float16` equivalent of [java.lang.Math.nextAfter].
     */
    fun nextTowards(other: Float16): Float16 {
        if (isNaN() || other.isNaN()) return NaN
        val cmp = comparator.compare(this, other)
        return when {
            cmp == 0 -> other
            cmp < 0 -> nextUp()
            else -> nextDown()
        }
    }
}
