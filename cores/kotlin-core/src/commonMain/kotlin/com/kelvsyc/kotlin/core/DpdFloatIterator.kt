package com.kelvsyc.kotlin.core

/**
 * An iterator over a sequence of [DpdFloat] values.
 *
 * Implementations override [nextDpdFloat] to return the next element without boxing. The [next] override
 * satisfies the [Iterator] contract but is otherwise identical — it exists solely to allow `DpdFloatIterator`
 * to be used wherever an `Iterator<DpdFloat>` is expected.
 */
abstract class DpdFloatIterator : Iterator<DpdFloat> {
    /** Returns the next [DpdFloat] value in the sequence without boxing. */
    abstract fun nextDpdFloat(): DpdFloat
    final override fun next(): DpdFloat = nextDpdFloat()
}
