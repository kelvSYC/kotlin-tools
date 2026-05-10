package com.kelvsyc.kotlin.dice

/**
 * A constant value (e.g. the `3` in `2d6+3`).
 */
data class Constant(val value: Int) : RollExpression {
    override fun evaluate(source: RandomSource) = RollResult(value)
}

/**
 * A single die roll of a die with [sides] faces (1..sides).
 */
data class Die(val sides: Int) : RollExpression {
    override fun evaluate(source: RandomSource): RollResult {
        val roll = source.nextInt(1, sides + 1)
        return RollResult(roll)
    }
}

/**
 * Roll [count] dice of [sides] faces each, summing the results.
 */
data class MultipleDice(val count: Int, val sides: Int) : RollExpression {
    override fun evaluate(source: RandomSource): RollResult {
        val rolls = (1..count).map { source.nextInt(1, sides + 1) }
        return RollResult(rolls.sum(), rolls)
    }
}

/**
 * Roll [count] dice of [sides] faces, keep the highest [keep].
 */
data class KeepHighest(val count: Int, val sides: Int, val keep: Int) : RollExpression {
    override fun evaluate(source: RandomSource): RollResult {
        val rolls = (1..count).map { source.nextInt(1, sides + 1) }
        val kept = rolls.sortedDescending().take(keep)
        return RollResult(kept.sum(), rolls)
    }
}

/**
 * Roll [count] dice of [sides] faces, keep the lowest [keep].
 */
data class KeepLowest(val count: Int, val sides: Int, val keep: Int) : RollExpression {
    override fun evaluate(source: RandomSource): RollResult {
        val rolls = (1..count).map { source.nextInt(1, sides + 1) }
        val kept = rolls.sorted().take(keep)
        return RollResult(kept.sum(), rolls)
    }
}

/**
 * Roll [count] dice of [sides] faces, drop the highest [drop] and sum the rest.
 */
data class DropHighest(val count: Int, val sides: Int, val drop: Int) : RollExpression {
    override fun evaluate(source: RandomSource): RollResult {
        val rolls = (1..count).map { source.nextInt(1, sides + 1) }
        val kept = rolls.sorted().dropLast(drop)
        return RollResult(kept.sum(), rolls)
    }
}

/**
 * Roll [count] dice of [sides] faces, drop the lowest [drop] and sum the rest.
 */
data class DropLowest(val count: Int, val sides: Int, val drop: Int) : RollExpression {
    override fun evaluate(source: RandomSource): RollResult {
        val rolls = (1..count).map { source.nextInt(1, sides + 1) }
        val kept = rolls.sortedDescending().dropLast(drop)
        return RollResult(kept.sum(), rolls)
    }
}

/**
 * Roll [count] Fudge/FATE dice of the given [grade].
 *
 * A Fudge die is a d6 where [grade] faces show +1, [grade] faces show -1, and the
 * remaining (6 - 2 * grade) faces show 0. Standard `dF` is grade 2 (equal thirds);
 * `dF.1` is grade 1 (one +1, one -1, four 0s).
 */
data class MultipleFudgeDice(val count: Int, val grade: Int = 2) : RollExpression {
    init {
        require(grade in 1..3) { "Fudge die grade must be 1, 2, or 3" }
    }

    private val faces: List<Int> = buildList {
        repeat(grade) { add(1) }
        repeat(6 - 2 * grade) { add(0) }
        repeat(grade) { add(-1) }
    }

    override fun evaluate(source: RandomSource): RollResult {
        val rolls = (1..count).map { faces[source.nextInt(0, 6)] }
        return RollResult(rolls.sum(), rolls)
    }
}

/**
 * Sum of two expressions.
 */
data class Add(val left: RollExpression, val right: RollExpression) : RollExpression {
    override fun evaluate(source: RandomSource): RollResult {
        val l = left.evaluate(source)
        val r = right.evaluate(source)
        return RollResult(l.total + r.total, l.rolls + r.rolls)
    }
}

/**
 * Difference of two expressions.
 */
data class Subtract(val left: RollExpression, val right: RollExpression) : RollExpression {
    override fun evaluate(source: RandomSource): RollResult {
        val l = left.evaluate(source)
        val r = right.evaluate(source)
        return RollResult(l.total - r.total, l.rolls + r.rolls)
    }
}
