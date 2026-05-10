package com.kelvsyc.kotlin.dice

import kotlin.random.Random

/**
 * Abstraction over a source of randomness for producing integer outcomes within a range.
 */
fun interface RandomSource {
    fun nextInt(from: Int, until: Int): Int
}

fun RandomSource(random: Random): RandomSource = RandomSource { from, until -> random.nextInt(from, until) }

fun RandomSource.nextInt(range: IntRange): Int = nextInt(range.first, range.last + 1)
