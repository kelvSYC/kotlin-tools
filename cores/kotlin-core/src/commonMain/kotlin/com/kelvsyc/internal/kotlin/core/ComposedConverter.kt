package com.kelvsyc.internal.kotlin.core

import com.kelvsyc.kotlin.core.Converter

internal class ComposedConverter<A, B, C>(
    private val first: Converter<A, B>,
    private val second: Converter<B, C>
) : Converter<A, C>() {
    override fun doForward(a: A): C = second(first(a))
    override fun doBackward(b: C): A = first.reverse(second.reverse(b))

    // equals() and hashCode() implementations consistent with Guava
    override fun equals(other: Any?): Boolean {
        return if (other is ComposedConverter<*, *, *>) {
            first == other.first && second == other.second
        } else false
    }

    override fun hashCode(): Int = 31 * first.hashCode() + second.hashCode()
}
