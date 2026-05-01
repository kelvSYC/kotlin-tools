package com.kelvsyc.kotlin.core

/**
 * An iterator over a sequence of [BidFloat] values.
 *
 * Implementations override [nextBidFloat] to return the next element without boxing. The [next] override
 * satisfies the [Iterator] contract but is otherwise identical — it exists solely to allow `BidFloatIterator`
 * to be used wherever an `Iterator<BidFloat>` is expected.
 */
abstract class BidFloatIterator : Iterator<BidFloat> {
    /** Returns the next [BidFloat] value in the sequence without boxing. */
    abstract fun nextBidFloat(): BidFloat
    final override fun next(): BidFloat = nextBidFloat()
}
