package com.kelvsyc.kotlin.core

/**
 * An iterator over a sequence of [DpdDouble] values.
 *
 * Implementations override [nextDpdDouble] to return the next element without boxing. The [next] override
 * satisfies the [Iterator] contract but is otherwise identical — it exists solely to allow `DpdDoubleIterator`
 * to be used wherever an `Iterator<DpdDouble>` is expected.
 */
abstract class DpdDoubleIterator : Iterator<DpdDouble> {
    /** Returns the next [DpdDouble] value in the sequence without boxing. */
    abstract fun nextDpdDouble(): DpdDouble
    final override fun next(): DpdDouble = nextDpdDouble()
}
