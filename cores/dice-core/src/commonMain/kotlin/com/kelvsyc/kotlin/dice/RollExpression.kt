package com.kelvsyc.kotlin.dice

/**
 * The result of evaluating a [RollExpression].
 */
data class RollResult(
    val total: Int,
    val rolls: List<Int> = listOf(total),
)

/**
 * A [TypedRollExpression] specialized to produce numeric [RollResult]s.
 */
fun interface RollExpression : TypedRollExpression<RollResult> {
    override fun evaluate(source: RandomSource): RollResult
}
