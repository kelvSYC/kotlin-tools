package com.kelvsyc.kotlin.commons.numbers

import org.apache.commons.numbers.fraction.BigFraction
import org.apache.commons.numbers.fraction.Fraction

@Throws(NumberFormatException::class)
fun String.toFraction(): Fraction = Fraction.parse(this)

fun String.toFractionOrNull(): Fraction? = try {
    Fraction.parse(this)
} catch (_: NumberFormatException) {
    null
}

@Throws(NumberFormatException::class)
fun String.toBigFraction(): BigFraction = BigFraction.parse(this)

fun String.toBigFractionOrNull(): BigFraction? = try {
    BigFraction.parse(this)
} catch (_: NumberFormatException) {
    null
}
