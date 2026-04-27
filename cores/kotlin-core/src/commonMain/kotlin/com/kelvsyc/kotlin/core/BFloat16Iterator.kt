package com.kelvsyc.kotlin.core

/**
 * An iterator over a sequence of [BFloat16] values.
 *
 * Implementations override [nextBFloat16] to return the next element without boxing. The [next] override
 * satisfies the [Iterator] contract but is otherwise identical — it exists solely to allow `BFloat16Iterator`
 * to be used wherever an `Iterator<BFloat16>` is expected.
 */
abstract class BFloat16Iterator : Iterator<BFloat16> {
    /** Returns the next [BFloat16] value in the sequence without boxing. */
    abstract fun nextBFloat16(): BFloat16
    final override fun next(): BFloat16 = nextBFloat16()
}
