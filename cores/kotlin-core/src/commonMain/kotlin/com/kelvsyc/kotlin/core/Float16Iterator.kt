package com.kelvsyc.kotlin.core

/**
 * An iterator over a sequence of [Float16] values.
 *
 * Implementations override [nextFloat16] to return the next element without boxing. The [next] override
 * satisfies the [Iterator] contract but is otherwise identical — it exists solely to allow `Float16Iterator`
 * to be used wherever an `Iterator<Float16>` is expected (for example, in `for` loops and generic APIs).
 */
abstract class Float16Iterator : Iterator<Float16> {
    /** Returns the next [Float16] value in the sequence without boxing. */
    abstract fun nextFloat16(): Float16
    final override fun next(): Float16 = nextFloat16()
}
