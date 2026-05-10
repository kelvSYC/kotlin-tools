package com.kelvsyc.kotlin.dice

/**
 * A generalized roll expression that produces a result of type [T] when evaluated against
 * a [RandomSource].
 *
 * This is the foundation for composing dice rolls whose outcomes are not necessarily numeric.
 */
fun interface TypedRollExpression<out T> {
    fun evaluate(source: RandomSource): T
}

fun <T, R> TypedRollExpression<T>.map(transform: (T) -> R): TypedRollExpression<R> =
    TypedRollExpression { source -> transform(evaluate(source)) }

fun <T, R> TypedRollExpression<T>.flatMap(transform: (T) -> TypedRollExpression<R>): TypedRollExpression<R> =
    TypedRollExpression { source -> transform(evaluate(source)).evaluate(source) }

fun <A, B, R> TypedRollExpression<A>.zip(
    other: TypedRollExpression<B>,
    transform: (A, B) -> R,
): TypedRollExpression<R> =
    TypedRollExpression { source -> transform(evaluate(source), other.evaluate(source)) }

fun <A, B, C, R> zip(
    first: TypedRollExpression<A>,
    second: TypedRollExpression<B>,
    third: TypedRollExpression<C>,
    transform: (A, B, C) -> R,
): TypedRollExpression<R> =
    TypedRollExpression { source ->
        transform(first.evaluate(source), second.evaluate(source), third.evaluate(source))
    }
