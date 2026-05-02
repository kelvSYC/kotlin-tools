package com.kelvsyc.kotlin.guava.collect

import com.google.common.collect.DiscreteDomain
import com.kelvsyc.kotlin.core.EnumSubset
import java.util.EnumSet
import kotlin.enums.EnumEntries
import kotlin.enums.enumEntries

/**
 * [DiscreteDomain] for [EnumSubset], enumerating all subsets of an enum type in subset
 * enumeration order (binary counting over ordinals: ∅, {A}, {B}, {A,B}, {C}, …).
 *
 * The ordering is consistent with [EnumSubset.compareTo] and [EnumSubset.bits]: the domain
 * walks subsets by incrementing/decrementing the [Long] bitmask value.
 *
 * [minValue] is the empty subset; [maxValue] is the subset of all constants. The total number
 * of values in the domain is `2^n` where `n` is the number of enum constants (at most 63,
 * matching [EnumSubset]'s size limit).
 *
 * @param E The enum type.
 */
class EnumSubsetDomain<E : Enum<E>>(
    private val enumClass: Class<E>,
    private val entries: EnumEntries<E>,
) : DiscreteDomain<EnumSubset<E>>() {

    companion object {
        inline fun <reified E : Enum<E>> of(): EnumSubsetDomain<E> =
            EnumSubsetDomain(E::class.java, enumEntries())
    }

    private val maxBits: Long = entries.fold(0L) { acc, e -> acc or (1L shl e.ordinal) }

    private fun fromBits(bits: Long): EnumSubset<E> {
        if (bits == 0L) return EnumSubset.of(EnumSet.noneOf(enumClass))
        val result = EnumSet.noneOf(enumClass)
        for (entry in entries) {
            if (bits and (1L shl entry.ordinal) != 0L) result.add(entry)
        }
        return EnumSubset.of(result)
    }

    override fun next(value: EnumSubset<E>): EnumSubset<E>? {
        val bits = value.bits()
        if (bits == maxBits) return null
        return fromBits(bits + 1L)
    }

    override fun previous(value: EnumSubset<E>): EnumSubset<E>? {
        val bits = value.bits()
        if (bits == 0L) return null
        return fromBits(bits - 1L)
    }

    override fun distance(start: EnumSubset<E>, end: EnumSubset<E>): Long =
        end.bits() - start.bits()

    override fun minValue(): EnumSubset<E> = EnumSubset.of(EnumSet.noneOf(enumClass))

    override fun maxValue(): EnumSubset<E> = EnumSubset.of(EnumSet.allOf(enumClass))
}
