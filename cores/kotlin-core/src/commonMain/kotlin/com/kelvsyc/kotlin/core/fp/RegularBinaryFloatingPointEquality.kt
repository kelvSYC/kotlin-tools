package com.kelvsyc.kotlin.core.fp

import com.kelvsyc.kotlin.core.traits.UInt16
import com.kelvsyc.kotlin.core.traits.UInt32
import com.kelvsyc.kotlin.core.traits.UInt64
import com.kelvsyc.kotlin.core.traits.UnsignedIntegral
import com.kelvsyc.kotlin.core.traits.ValueEquality

/**
 * Constructs a [ValueEquality] for [RegularBinaryFloatingPoint] based on cohort membership.
 *
 * Two values are considered equal if they represent the same numerical value — that is, if they
 * are in the same cohort. Differently-scaled representations of the same value (e.g.
 * `significand=1u, exponent=1` and `significand=2u, exponent=0`) compare equal. Any two zero
 * significands compare equal regardless of sign or exponent, reflecting the unspecified
 * sign-of-zero convention of [RegularBinaryFloatingPoint].
 */
fun <T> UnsignedIntegral<T>.cohortEquality(): ValueEquality<RegularBinaryFloatingPoint<T>> {
    val ops = this
    return object : ValueEquality<RegularBinaryFloatingPoint<T>> {
        override fun RegularBinaryFloatingPoint<T>.isEqualTo(other: RegularBinaryFloatingPoint<T>): Boolean =
            with(ops) {
                val thisZero = significand.isEqualTo(zero)
                val otherZero = other.significand.isEqualTo(zero)
                when {
                    thisZero && otherZero -> true
                    thisZero || otherZero -> false
                    sign != other.sign -> false
                    else -> {
                        val thisTz = significand.countTrailingClearBits()
                        val otherTz = other.significand.countTrailingClearBits()
                        exponent + thisTz == other.exponent + otherTz &&
                            significand.logicalRightShift(thisTz).isEqualTo(other.significand.logicalRightShift(otherTz))
                    }
                }
            }
    }
}

private val uShortInstance: ValueEquality<RegularBinaryFloatingPoint<UShort>> = UInt16.cohortEquality()
private val uIntInstance: ValueEquality<RegularBinaryFloatingPoint<UInt>> = UInt32.cohortEquality()
private val uLongInstance: ValueEquality<RegularBinaryFloatingPoint<ULong>> = UInt64.cohortEquality()

val RegularBinaryFloatingPoint.Companion.uShortCohortEquality: ValueEquality<RegularBinaryFloatingPoint<UShort>>
    get() = uShortInstance

val RegularBinaryFloatingPoint.Companion.uIntCohortEquality: ValueEquality<RegularBinaryFloatingPoint<UInt>>
    get() = uIntInstance

val RegularBinaryFloatingPoint.Companion.uLongCohortEquality: ValueEquality<RegularBinaryFloatingPoint<ULong>>
    get() = uLongInstance
