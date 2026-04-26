package com.kelvsyc.internal.kotlin.core

import com.kelvsyc.kotlin.core.MutablePeekingIterator

class MutablePeekingIteratorImpl<T>(override val base: MutableIterator<T>) :
    PeekingIteratorImpl<T>(base), MutablePeekingIterator<T> {
    override fun remove() {
        // This null check is to be consistent with Guava
        check(peek == null) { "Cannot remove element once peeked" }
        base.remove()
    }
}
