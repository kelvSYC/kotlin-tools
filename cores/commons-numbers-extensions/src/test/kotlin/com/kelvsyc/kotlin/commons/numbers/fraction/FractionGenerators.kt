package com.kelvsyc.kotlin.commons.numbers.fraction

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.bigInt
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.arbitrary.int
import org.apache.commons.numbers.fraction.BigFraction
import org.apache.commons.numbers.fraction.Fraction
import java.math.BigInteger

// Restrict to small values so that Fraction arithmetic (which uses Int internally) never overflows.
val arbitraryFraction = arbitrary {
    val numerator = Arb.int(-1000, 1000).bind()
    val denominator = Arb.int(-1000, 1000).filterNot { it == 0 }.bind()
    Fraction.of(numerator, denominator)
}

val arbitraryBigFraction = arbitrary {
    val numerator = Arb.bigInt(32).bind()
    val denominator = Arb.bigInt(32).filterNot { it == BigInteger.ZERO }.bind()
    BigFraction.of(numerator, denominator)
}
