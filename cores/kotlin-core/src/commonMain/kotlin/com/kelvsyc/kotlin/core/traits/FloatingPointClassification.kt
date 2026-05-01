package com.kelvsyc.kotlin.core.traits

/**
 * Trait for the four basic IEEE 754-2008 §5.7.2 classification predicates on a floating-point type [T].
 *
 * These predicates partition the set of all values: every value is exactly one of finite, infinite,
 * or NaN.  Zero is a special case of finite.  The predicates never raise an exception.
 */
interface FloatingPointClassification<T> {
    /**
     * Returns `true` if this value is finite (not infinite and not NaN).
     *
     * Zero and subnormals are finite.
     */
    fun T.isFinite(): Boolean

    /**
     * Returns `true` if this value is positive or negative infinity.
     *
     * Equivalent to IEEE 754-2008 §5.7.2 `isInfinite`.
     */
    fun T.isInfinite(): Boolean

    /**
     * Returns `true` if this value is a NaN (Not a Number).
     *
     * Both quiet NaN and signalling NaN return `true`.  Equivalent to IEEE 754-2008 §5.7.2
     * `isNaN`.
     */
    fun T.isNaN(): Boolean

    /**
     * Returns `true` if this value is positive or negative zero.
     *
     * This is a numerical predicate: both `+0` and `-0` return `true`.  To distinguish the two
     * zeros, use [FloatingPointSign.isNegative].
     */
    fun T.isZero(): Boolean

    /**
     * Returns `true` if this value represents a mathematical integer (including zero).
     *
     * NaN and infinities return `false`.  A finite value is an integer when its fractional part is
     * exactly zero — that is, the value equals `m × base^e` where all digits below the decimal
     * point are zero.  For binary formats this means the last `−e` mantissa bits are zero when
     * `e < 0`; for decimal formats it means the last `−e` significand digits are zero when `e < 0`.
     */
    fun T.isInteger(): Boolean
}

/**
 * Trait extending [FloatingPointClassification] with the two IEEE 754-2008 §5.7.2 normality
 * predicates that apply to formats with an explicit normal/subnormal distinction.
 *
 * All IEEE 754 binary and decimal interchange formats define a notion of normality.  Compound
 * types such as `DoubleDouble` do not have a single-value normal/subnormal distinction and
 * therefore do not implement this interface.
 */
interface IeeeFloatingPointClassification<T> : FloatingPointClassification<T> {
    /**
     * Returns `true` if this value is a normal finite number.
     *
     * A normal value has a full-precision significand — its biased exponent is neither all-zeros
     * nor all-ones.  NaN, infinity, zero, and subnormals all return `false`.
     *
     * For binary formats: biased exponent in `1..(2^e - 2)` where `e` is the exponent width.
     * For decimal formats: biased exponent non-zero, or (biased exponent zero and leading digit
     * non-zero), i.e. the significand is at least `10^(p-1)`.
     */
    fun T.isNormal(): Boolean

    /**
     * Returns `true` if this value is subnormal (also called denormal).
     *
     * Per IEEE 754-2008 §5.7.2, a finite non-zero value is subnormal when its magnitude is less
     * than the smallest positive normal value.  Such values have reduced precision.  NaN,
     * infinity, and zero all return `false`.
     *
     * For binary formats: biased exponent zero, significand non-zero — a single structural test.
     *
     * For decimal formats: the criterion is purely value-based (`|x| < 10^Emin`), not structural.
     * A non-zero biased exponent does **not** guarantee normality: a value can be subnormal with
     * `biasedExponent` in `1..5` if its significand is small enough.  Conversely, a value with
     * `biasedExponent = 0` and significand ≥ `10^(p-1)` is the smallest normal value, not subnormal.
     */
    fun T.isSubnormal(): Boolean
}
