package com.kelvsyc.kotlin.core

import java.util.EnumSet

/**
 * A compact, ordered representation of a subset of enum constants.
 *
 * Internally backed by a [java.util.EnumSet] and implementing [Set]<[E]> via delegation.
 * The total order is the **subset enumeration order**: subsets are compared by treating each
 * constant's [Enum.ordinal] as a bit position in a [Long] bitmask and comparing bitmasks with
 * ordinary signed integer comparison. This enumerates subsets as if counting in binary over the
 * constants in ordinal order (∅, {A}, {B}, {A,B}, {C}, {A,C}, …). The ordering is consistent
 * with any `DiscreteDomain` companion that walks subsets by incrementing/decrementing [bits].
 *
 * **Limitation — enum size:** [EnumSubset] only supports enums with **at most 63 constants**.
 * This matches the internal threshold of [java.util.EnumSet] (`RegularEnumSet` uses a single
 * `long` bitmask for enums with ≤ 64 constants); the limit here is 63 rather than 64 because
 * we rely on signed [Long] comparison — bit 63 is the sign bit, so keeping all ordinals in
 * bits 0–62 ensures the bitmask is always non-negative and [compareTo]/[distance] arithmetic
 * needs no unsigned handling.
 */
@JvmInline
value class EnumSubset<E : Enum<E>> @PublishedApi internal constructor(
    @PublishedApi internal val elements: EnumSet<E>,
) : Set<E> by elements, Comparable<EnumSubset<E>> {

    init {
        // elements.size + complement.size == total enum constant count.
        // EnumSet.complementOf works for empty sets because the EnumSet carries its element type.
        val totalConstants = elements.size + EnumSet.complementOf(elements).size
        require(totalConstants <= 63) {
            "EnumSubset only supports enums with at most 63 constants (found $totalConstants)"
        }
    }

    companion object {
        /** Returns an empty [EnumSubset] for [E]. */
        inline fun <reified E : Enum<E>> empty(): EnumSubset<E> =
            EnumSubset(EnumSet.noneOf(E::class.java))

        /** Returns an [EnumSubset] containing all constants of [E]. */
        inline fun <reified E : Enum<E>> allOf(): EnumSubset<E> =
            EnumSubset(EnumSet.allOf(E::class.java))

        /**
         * Returns an [EnumSubset] that is a copy of [source].
         *
         * Unlike [EnumSet.copyOf] taking a [Collection], this overload accepts an empty [EnumSet]
         * because the element type is carried by the [EnumSet] itself.
         */
        fun <E : Enum<E>> of(source: EnumSet<E>): EnumSubset<E> =
            EnumSubset(EnumSet.copyOf(source))

        /** Returns an [EnumSubset] containing the given [elements]. */
        inline fun <reified E : Enum<E>> of(elements: Iterable<E>): EnumSubset<E> {
            val list = elements.toList()
            return if (list.isEmpty()) empty() else EnumSubset(EnumSet.copyOf(list))
        }

        /** Returns an [EnumSubset] containing the given [elements]. */
        inline fun <reified E : Enum<E>> of(vararg elements: E): EnumSubset<E> =
            if (elements.isEmpty()) empty() else EnumSubset(EnumSet.copyOf(elements.asList()))
    }

    /**
     * Returns the subset enumeration value: a non-negative [Long] with bit [i] set iff the
     * enum constant with ordinal [i] is in this subset. Consistent with [compareTo]; the bitmask
     * is the canonical key for `next`/`previous`/`distance` operations in any `DiscreteDomain`
     * over subsets of [E].
     */
    fun bits(): Long = elements.fold(0L) { acc, e -> acc or (1L shl e.ordinal) }

    override fun compareTo(other: EnumSubset<E>): Int = bits().compareTo(other.bits())

    /** Returns a mutable [EnumSet] copy of this subset. */
    fun toEnumSet(): EnumSet<E> = EnumSet.copyOf(elements)

    /** Returns the union of this subset and [other]. */
    operator fun plus(other: EnumSubset<E>): EnumSubset<E> =
        EnumSubset(EnumSet.copyOf(elements).also { it.addAll(other.elements) })

    /** Returns this subset with all elements of [other] removed. */
    operator fun minus(other: EnumSubset<E>): EnumSubset<E> =
        EnumSubset(EnumSet.copyOf(elements).also { it.removeAll(other.elements) })

    /** Returns the intersection of this subset and [other]. */
    infix fun intersect(other: EnumSubset<E>): EnumSubset<E> =
        EnumSubset(EnumSet.copyOf(elements).also { it.retainAll(other.elements) })

    /** Returns all enum constants not in this subset. */
    fun complement(): EnumSubset<E> = EnumSubset(EnumSet.complementOf(elements))
}

/** Returns an [EnumSubset] containing the given [elements]. */
inline fun <reified E : Enum<E>> enumSubsetOf(vararg elements: E): EnumSubset<E> =
    EnumSubset.of(*elements)
