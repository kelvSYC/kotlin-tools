package com.kelvsyc.kotlin.core

/** Copies this array's elements into a new [BidDoubleArray], interpreting each [Long] as a [BidDouble] bit pattern. */
fun LongArray.toBidDoubleArray(): BidDoubleArray = BidDoubleArray(this)
