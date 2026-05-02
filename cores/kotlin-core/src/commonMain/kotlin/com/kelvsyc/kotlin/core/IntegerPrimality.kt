package com.kelvsyc.kotlin.core

import com.kelvsyc.kotlin.core.traits.integral.Primality
import com.kelvsyc.kotlin.core.traits.integral.int
import com.kelvsyc.kotlin.core.traits.integral.long

/**
 * Returns `true` if this value is a prime number.
 *
 * @see Primality.int
 */
val Int.isPrime: Boolean get() = with(Primality.int) { isPrime() }

/**
 * Returns `true` if this value is a prime number.
 *
 * @see Primality.long
 */
val Long.isPrime: Boolean get() = with(Primality.long) { isPrime() }
