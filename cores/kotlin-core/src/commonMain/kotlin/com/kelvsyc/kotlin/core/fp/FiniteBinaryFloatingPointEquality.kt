package com.kelvsyc.kotlin.core.fp

import com.kelvsyc.kotlin.core.traits.UInt16
import com.kelvsyc.kotlin.core.traits.UInt32
import com.kelvsyc.kotlin.core.traits.UInt64
import com.kelvsyc.kotlin.core.traits.UnsignedIntegral
import com.kelvsyc.kotlin.core.traits.ValueEquality

/**
 * Constructs a [ValueEquality] for [FiniteBinaryFloatingPoint] based on cohort membership.
 *
 * Two values are considered equal if they represent the same numerical value — that is, if they
 * are in the same cohort. Differently-scaled representations of the same value (e.g.
 * `significand=1u, exponent=1` and `significand=2u, exponent=0`) compare equal. Any two zero
 * significands compare equal regardless of sign or exponent, reflecting the unspecified
 * sign-of-zero convention of [FiniteBinaryFloatingPoint].
 */
fun <T> UnsignedIntegral<T>.cohortEquality(): ValueEquality<FiniteBinaryFloatingPoint<T>> {
    val ops = this
    return object : ValueEquality<FiniteBinaryFloatingPoint<T>> {
        override fun FiniteBinaryFloatingPoint<T>.isEqualTo(other: FiniteBinaryFloatingPoint<T>): Boolean =
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

private val uShortInstance: ValueEquality<FiniteBinaryFloatingPoint<UShort>> = UInt16.cohortEquality()
private val uIntInstance: ValueEquality<FiniteBinaryFloatingPoint<UInt>> = UInt32.cohortEquality()
private val uLongInstance: ValueEquality<FiniteBinaryFloatingPoint<ULong>> = UInt64.cohortEquality()

val FiniteBinaryFloatingPoint.Companion.uShortCohortEquality: ValueEquality<FiniteBinaryFloatingPoint<UShort>>
    get() = uShortInstance

val FiniteBinaryFloatingPoint.Companion.uIntCohortEquality: ValueEquality<FiniteBinaryFloatingPoint<UInt>>
    get() = uIntInstance

val FiniteBinaryFloatingPoint.Companion.uLongCohortEquality: ValueEquality<FiniteBinaryFloatingPoint<ULong>>
    get() = uLongInstance
