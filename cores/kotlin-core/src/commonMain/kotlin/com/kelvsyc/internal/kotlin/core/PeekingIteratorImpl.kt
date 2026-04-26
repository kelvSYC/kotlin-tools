package com.kelvsyc.internal.kotlin.core

import com.kelvsyc.kotlin.core.PeekingIterator

open class PeekingIteratorImpl<T>(protected open val base: Iterator<T>) : PeekingIterator<T> {
    protected data class Holder<T>(val value: T)

    protected var peek: Holder<T>? = null

    override fun hasNext(): Boolean = peek != null || base.hasNext()

    override fun peek(): T {
        if (peek == null) {
            peek = Holder(base.next())
        }
        return peek!!.value
    }

    override fun next(): T {
        if (peek == null) {
            return base.next()
        } else {
            val result = peek!!.value
            peek = null
            return result
        }
    }

    // kotlin.collections.Iterator<T> maps to java.util.Iterator<T> on JVM, which has a default remove() that
    // throws UnsupportedOperationException. Kotlin's K2 analysis cannot see that Java-default inheritance, so it
    // flags MutablePeekingIteratorImpl (which requires remove() via MutableIterator) as having an unimplemented
    // abstract member. Declaring remove() here makes the chain visible in Kotlin source and gives
    // MutablePeekingIteratorImpl a concrete target to override.
    open fun remove(): Unit = throw UnsupportedOperationException("remove")
}
