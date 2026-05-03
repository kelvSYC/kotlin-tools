package com.kelvsyc.kotlin.commons.numbers

import org.apache.commons.numbers.core.DD
import org.apache.commons.numbers.fraction.BigFraction
import org.apache.commons.numbers.fraction.Fraction

fun Int.toDD(): DD = DD.of(this)
fun Int.toFraction(): Fraction = Fraction.of(this)
fun Int.toBigFraction(): BigFraction = BigFraction.of(this)
