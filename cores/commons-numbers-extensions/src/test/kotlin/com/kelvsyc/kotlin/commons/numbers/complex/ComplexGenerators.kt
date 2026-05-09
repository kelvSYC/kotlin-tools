package com.kelvsyc.kotlin.commons.numbers.complex

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.double
import org.apache.commons.numbers.complex.Complex

// Restrict to finite doubles so that Double component comparisons work (NaN != NaN under ==).
val arbitraryComplex = arbitrary {
    val re = Arb.double(-1e6, 1e6, includeNaNs = false).bind()
    val im = Arb.double(-1e6, 1e6, includeNaNs = false).bind()
    Complex.ofCartesian(re, im)
}
