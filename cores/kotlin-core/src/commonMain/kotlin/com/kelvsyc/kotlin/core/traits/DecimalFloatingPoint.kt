package com.kelvsyc.kotlin.core.traits

import com.kelvsyc.kotlin.core.PartialComparator

/**
 * `DecimalFloatingPoint` is a trait type that contains structural metadata on a decimal floating-point type [T].
 *
 * Decimal floating-point formats follow IEEE 754-2008 §3.5. Unlike binary formats, the significand is a string
 * of decimal digits rather than a binary integer, and the same mathematical value can be represented by multiple
 * (significand, exponent) pairs — called a *cohort* in the standard. Equality and ordering must account for this.
 */
interface DecimalFloatingPoint<T> {
    /**
     * The total number of bits used to represent the floating-point number.
     *
     * The bit layout follows IEEE 754-2008 §3.5.2:
     * - Bit `sizeBits - 1`: sign
     * - Next [combinationBits] bits: combination field (encodes both biased exponent and leading significand digit)
     * - Remaining [continuationBits] bits: continuation field (remaining significand bits)
     */
    val sizeBits: Int

    /**
     * The number of bits in the continuation field (IEEE 754-2008 calls this the T field).
     *
     * For BID (Binary Integer Decimal) encoding the continuation field holds the low bits of the integer
     * significand. For DPD (Densely Packed Decimal) encoding it holds packed decimal declets.
     */
    val continuationBits: Int

    /**
     * The number of bits in the combination field (IEEE 754-2008 calls this the G field).
     *
     * The combination field uses a two-case encoding: when the top two bits are not `11`, the upper bits
     * carry the biased exponent and the lower bits carry the most significant decimal digit; when the top
     * two bits are `11` (the "large significand" form), the exponent and leading digit positions swap to
     * accommodate a significand whose leading digit is 8 or 9.
     */
    val combinationBits: Int

    /**
     * The number of decimal digits in the significand (the precision `p` in IEEE 754-2008 notation).
     *
     * The significand ranges from `0` to `10^significandDigits - 1`. For decimal32 this is 7.
     */
    val significandDigits: Int

    /**
     * The number of bits used to represent the biased exponent within the combination field.
     *
     * The unbiased quantum exponent `q` satisfies `Emin ≤ q ≤ Emax` where `Emin = 1 - Emax`. Valid biased
     * exponents range from `0` to `2^exponentBits - 1`, representing unbiased exponents `-exponentBias`
     * to `2^exponentBits - 1 - exponentBias`.
     */
    val exponentBits: Int

    /**
     * The bias value used for the exponent, equal to `Emax + p - 2` per IEEE 754-2008 Table 3.6.
     *
     * The unbiased quantum exponent is `biasedExponent - exponentBias`.
     */
    val exponentBias: Int

    /**
     * Value equality suitable for numerical comparisons.
     *
     * NaN is not equal to anything, including itself, and positive zero equals negative zero.
     * Cohort-distinct representations of the same number (e.g. `1 × 10⁰` and `10 × 10⁻¹`) are
     * considered equal. This follows IEEE 754 §5.11 compareQuietEqual semantics.
     */
    val numericalEquality: ValueEquality<T>

    /**
     * Value equality suitable for use in collections and as an equivalence relation.
     *
     * All NaN payloads are considered equal to each other, and positive zero is not equal to
     * negative zero. Unlike [numericalEquality], cohort-distinct representations of the same
     * mathematical value are *not* considered equal — bit-pattern identity is used for finite
     * non-NaN values.
     */
    val equivalenceEquality: ValueEquality<T>

    /**
     * Positive infinity: a value greater than [maxValue].
     */
    val positiveInfinity: T

    /**
     * Negative infinity: a value less than the negation of [maxValue].
     */
    val negativeInfinity: T

    /**
     * The largest finite value representable in this format.
     *
     * Equal to `(10^p - 1) × 10^(Emax - p + 1)` where `p` is [significandDigits] and `Emax` is the
     * maximum unbiased exponent.
     */
    val maxValue: T

    /**
     * The smallest positive value representable in this format.
     *
     * This is a subnormal value with significand `1` and the minimum quantum exponent
     * `Emin - (p - 1) = -exponentBias`. For the smallest positive *normal* value see [minNormal].
     */
    val minValue: T

    /**
     * A canonical quiet NaN value.
     *
     * NaN is unordered and not equal to any value including itself under IEEE 754 numerical
     * equality. There may be other NaN bit patterns (with different payloads); see the concrete
     * type for details.
     */
    val NaN: T

    /**
     * The smallest positive normal value representable in this format.
     *
     * A finite non-zero value is *normal* when its significand is at least `10^(p-1)`. The smallest
     * such value has significand `10^(p-1)` and the minimum quantum exponent `-exponentBias`, giving
     * a mathematical value of `10^(Emin)` where `Emin = 1 - Emax`.
     *
     * Values smaller than this are subnormals: their significand is less than `10^(p-1)` at the
     * minimum quantum exponent, so they lose one or more digits of precision.
     */
    val minNormal: T

    /**
     * The machine epsilon: the difference between 1.0 and the next larger representable value.
     *
     * For decimal floating-point with `p` significant digits, 1.0 is represented as
     * `10^(p-1) × 10^(1-p)`, and the adjacent value is `(10^(p-1) + 1) × 10^(1-p)`. Their
     * difference is `10^(1-p)`. For decimal32 (`p = 7`) this is `10^(-6)`.
     */
    val epsilon: T

    /**
     * [Comparator] defining a total ordering over all values, including NaN.
     *
     * Consistent with [Float.compareTo] and [Double.compareTo]: positive zero and negative zero are
     * distinct (negative zero compares as strictly less), and NaN is ordered after all finite values
     * and infinities. Cohort-distinct representations of the same mathematical value compare as
     * equal. Every pair of values produces a defined result — use this for sorting and ordered
     * collections.
     *
     * @see partialComparator
     */
    val comparator: Comparator<T>

    /**
     * [PartialComparator] for values, returning `null` when either operand is NaN, reflecting the
     * IEEE 754 rule that NaN is unordered with respect to every value including itself. Non-NaN
     * values are ordered consistently with [comparator].
     *
     * Use this when NaN-unordered semantics are required, e.g. implementing IEEE 754
     * `compareQuietLess` or propagating the unordered result to the caller.
     *
     * @see comparator
     */
    val partialComparator: PartialComparator<T>
}

/**
 * `BinaryIntegerDecimal` is a trait type containing structural metadata for decimal floating-point significands
 * that have been encoded in binary integer decimal (BID) format.
 *
 * In BID encoding the full significand is stored as a single binary integer split across the combination and
 * continuation fields, allowing direct integer arithmetic on the significand without conversion.
 */
interface BinaryIntegerDecimal<T> : DecimalFloatingPoint<T> {
    /**
     * The type traits for an unsigned integral type that can hold the BID-encoded significand.
     */
    val significandTraits: UnsignedIntegral<*>
}

/**
 * `DenselyPackedDecimal` is a trait type containing structural metadata for decimal floating-point significands
 * that have been encoded in densely packed decimal (DPD) format.
 *
 * In DPD encoding groups of three decimal digits are packed into 10-bit *declets* in the continuation field,
 * giving a more compact representation of the digits themselves at the cost of arithmetic complexity.
 */
interface DenselyPackedDecimal<T> : DecimalFloatingPoint<T> {
    /**
     * The number of 10-bit declets in the continuation field, each encoding three decimal digits.
     *
     * Equal to `(p - 1) / 3` where `p` is [significandDigits] — one digit is carried by the combination
     * field, and the remaining `p - 1` digits are packed three per declet.
     */
    val decletCount: Int
        get() = (significandDigits - 1) / 3
}

/**
 * `Decimal32` is a trait containing structural metadata on a `decimal32` floating-point type [T].
 *
 * `decimal32` is the 32-bit decimal interchange format defined in IEEE 754-2008 §3.5.2. It provides
 * 7 significant decimal digits and a biased-exponent range of 0–191 (unbiased −101 to +90, where
 * `Emax = 96` and `bias = 101`).
 */
interface Decimal32<T> : DecimalFloatingPoint<T> {
    override val sizeBits: Int get() = 32
    override val combinationBits: Int get() = 11
    override val continuationBits: Int get() = 20
    override val significandDigits: Int get() = 7
    override val exponentBits: Int get() = 8
    override val exponentBias: Int get() = 101
}

/**
 * `Bid32` is a trait containing structural metadata on a `decimal32` floating-point type [T], whose significand
 * is encoded in binary integer decimal format.
 *
 * The BID significand for `decimal32` fits in 24 bits (max value 9,999,999), which is stored as a [UInt32]
 * split across the combination and continuation fields.
 */
interface Bid32<T> : Decimal32<T>, BinaryIntegerDecimal<T> {
    override val significandTraits: UInt32<UInt> get() = UInt32
}

/**
 * `Dpd32` is a trait containing structural metadata on a `decimal32` floating-point type [T], whose significand
 * is encoded in densely packed decimal format.
 *
 * The continuation field holds two 10-bit declets encoding the lower six of the seven significand digits; the
 * leading digit is encoded in the combination field.
 */
interface Dpd32<T> : Decimal32<T>, DenselyPackedDecimal<T>
