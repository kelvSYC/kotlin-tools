package com.kelvsyc.kotlin.core

/**
 * An iterator over a sequence of [BidDouble] values.
 *
 * Implementations override [nextBidDouble] to return the next element without boxing. The [next] override
 * satisfies the [Iterator] contract but is otherwise identical — it exists solely to allow `BidDoubleIterator`
 * to be used wherever an `Iterator<BidDouble>` is expected.
 */
abstract class BidDoubleIterator : Iterator<BidDouble> {
    /** Returns the next [BidDouble] value in the sequence without boxing. */
    abstract fun nextBidDouble(): BidDouble
    final override fun next(): BidDouble = nextBidDouble()
}
