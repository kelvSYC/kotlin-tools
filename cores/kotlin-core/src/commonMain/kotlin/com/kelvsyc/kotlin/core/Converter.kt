package com.kelvsyc.kotlin.core

/**
 * A `Converter` is an object representing a reversible conversion between objects of two different types.
 *
 * This class is a Kotlinized implementation of its counterpart from Guava, though the two are otherwise incompatible.
 */
abstract class Converter<A, B> {
    companion object {
        /**
         * Returns a [Converter] representing the identity conversion.
         */
        @Suppress("UNCHECKED_CAST")
        fun <T> identity(): Converter<T, T> = Identity as Converter<T, T>

        /**
         * Creates a new [Converter] from two functions representing the forward and reverse operations.
         */
        fun <A, B> of(forward: (A) -> B, backward: (B) -> A): Converter<A, B> = FunctionBased(forward, backward)
    }

    private object Identity : Converter<Any?, Any?>() {
        override val reverse = this
        override fun doForward(a: Any?): Any? = a
        override fun doBackward(b: Any?): Any? = b
    }

    private class Reverse<A, B>(override val reverse: Converter<A, B>) : Converter<B, A>() {
        override operator fun invoke(a: B): A = reverse.doBackward(a)

        // Since we delegate back to the original converter, these two should be treated as unreachable.
        override fun doForward(a: B): A = throw AssertionError()
        override fun doBackward(b: A): B = throw AssertionError()

        // equals() and hashCode() implementations consistent with Guava
        override fun equals(other: Any?): Boolean {
            return if (other is Reverse<*, *>) {
                reverse == other.reverse
            } else false
        }

        override fun hashCode(): Int = reverse.hashCode().inv()
    }

    private class FunctionBased<A, B>(
        private val forward: (A) -> B,
        private val backward: (B) -> A
    ) : Converter<A, B>() {
        override fun doForward(a: A): B = forward(a)
        override fun doBackward(b: B): A = backward(b)

        // equals() and hashCode() implementations consistent with Guava
        override fun equals(other: Any?): Boolean {
            return if (other is FunctionBased<*, *>) {
                forward == other.forward && backward == other.backward
            } else false
        }

        override fun hashCode(): Int = forward.hashCode() * 31 + backward.hashCode()
    }

    open val reverse: Converter<B, A> by lazy { Reverse(this) }

    protected abstract fun doForward(a: A): B
    protected abstract fun doBackward(b: B): A

    open operator fun invoke(a: A): B = doForward(a)
}
