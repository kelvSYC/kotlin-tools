package com.kelvsyc.kotlin.commons.numbers

import org.apache.commons.numbers.fraction.BigFraction
import java.math.BigInteger

fun BigInteger.toBigFraction(): BigFraction = BigFraction.of(this)
