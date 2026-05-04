package com.kelvsyc.kotlin.core.traits.integral

import java.math.BigInteger

/**
 * Implementation of [Primality] for integer types [T] that delegates to [BigInteger.isProbablePrime] for primality
 * testing. Values of [T] are converted to [BigInteger] via [fn] before testing; non-positive results always return
 * `false`.
 *
 * **Limitation — probabilistic result:** unlike the deterministic fixed-width companions ([Primality.int],
 * [Primality.long], etc.), this implementation delegates to [BigInteger.isProbablePrime] with the configured
 * [certainty]. The JDK uses a combined Miller-Rabin / strong-Lucas (BPSW) test; no counterexample to BPSW is
 * known, and the false-positive probability is below 2^(-[certainty]) per call. The default of 64 yields a
 * false-positive probability below 5.4 × 10^(-20). For cryptographic primality proofs, use a dedicated library
 * instead.
 */
class BigIntegerPrimality<T>(private val certainty: Int = 64, private val fn: (T) -> BigInteger) : Primality<T> {
    override fun T.isPrime(): Boolean = fn(this).let { it.signum() > 0 && it.isProbablePrime(certainty) }
}
