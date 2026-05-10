package com.kelvsyc.kotlin.dice

/**
 * A die whose faces can be any type, not just integers.
 *
 * Each face has equal probability. The [faces] list defines the complete set of outcomes;
 * duplicates in the list represent faces that appear more than once (increasing their probability).
 */
class TypedDie<out T>(val faces: List<T>) : TypedRollExpression<T> {
    init {
        require(faces.isNotEmpty()) { "A die must have at least one face" }
    }

    override fun evaluate(source: RandomSource): T = faces[source.nextInt(0, faces.size)]
}
