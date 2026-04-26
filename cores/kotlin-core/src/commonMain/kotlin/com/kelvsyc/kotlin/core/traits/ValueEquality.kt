package com.kelvsyc.kotlin.core.traits

/**
 * `ValueEquality` is a trait that denotes that a value equality operation exists for a type.
 *
 * Normally, value equality can be defined in terms of [equals]. However, some types are forced to
 * define value equality independently of [equals] and [hashCode] — for example, value classes such
 * as [com.kelvsyc.kotlin.core.Float16], where the language prohibits overriding those methods. This
 * trait provides a uniform way to express such equality for any type.
 *
 * Note that this trait is intentionally hash-independent: no [hashCode] counterpart is defined.
 * Instances of this trait should not be used as the basis for equality in hash-based collections;
 * that pairing is a separate concern.
 *
 * Multiple instances of `ValueEquality<T>` may exist for the same type, each representing a
 * distinct contract. For example, a floating-point type might have one instance where all NaN
 * values are considered equal (matching [Float.equals] semantics) and another where the IEEE 754
 * rule is strictly enforced and NaN is not equal to anything, including itself.
 */
interface ValueEquality<T> {
    fun T.isEqualTo(other: T): Boolean
}
