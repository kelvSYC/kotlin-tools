package com.kelvsyc.kotlin.commons.numbers

import org.apache.commons.numbers.core.DD
import org.apache.commons.numbers.fraction.BigFraction

fun Long.toDD(): DD = DD.of(this)
fun Long.toBigFraction(): BigFraction = BigFraction.of(this)
