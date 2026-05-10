package com.kelvsyc.kotlin.dice

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import kotlin.random.Random

class DiceNotationTest : FunSpec({
    val source = RandomSource(Random(42))

    test("parse constant") {
        val expr = DiceNotation.parse("5")
        val result = expr.evaluate(source)
        result.total shouldBe 5
    }

    test("parse single die") {
        val expr = DiceNotation.parse("d6")
        val result = expr.evaluate(source)
        result.total shouldBeInRange 1..6
    }

    test("parse multiple dice") {
        val expr = DiceNotation.parse("2d6")
        val result = expr.evaluate(source)
        result.total shouldBeInRange 2..12
        result.rolls.size shouldBe 2
    }

    test("parse addition") {
        val expr = DiceNotation.parse("2d6+3")
        val result = expr.evaluate(source)
        result.total shouldBeInRange 5..15
    }

    test("parse subtraction") {
        val expr = DiceNotation.parse("2d6-1")
        val result = expr.evaluate(source)
        result.total shouldBeInRange 1..11
    }

    test("parse keep highest") {
        val expr = DiceNotation.parse("4d6kh3")
        val result = expr.evaluate(source)
        result.rolls.size shouldBe 4
        result.total shouldBeInRange 3..18
    }

    test("parse keep lowest") {
        val expr = DiceNotation.parse("4d6kl3")
        val result = expr.evaluate(source)
        result.rolls.size shouldBe 4
        result.total shouldBeInRange 3..18
    }

    test("parse d%") {
        val expr = DiceNotation.parse("d%")
        val result = expr.evaluate(source)
        result.total shouldBeInRange 1..100
        result.rolls.size shouldBe 1
    }

    test("parse 2d%") {
        val expr = DiceNotation.parse("2d%")
        val result = expr.evaluate(source)
        result.total shouldBeInRange 2..200
        result.rolls.size shouldBe 2
    }

    test("parse dF") {
        val expr = DiceNotation.parse("dF")
        val result = expr.evaluate(source)
        result.total shouldBeInRange -1..1
        result.rolls.size shouldBe 1
    }

    test("parse 4dF") {
        val expr = DiceNotation.parse("4dF")
        val result = expr.evaluate(source)
        result.total shouldBeInRange -4..4
        result.rolls.size shouldBe 4
        result.rolls.forEach { it shouldBeInRange -1..1 }
    }

    test("parse d%+dF") {
        val expr = DiceNotation.parse("d%+dF")
        val result = expr.evaluate(source)
        result.total shouldBeInRange 0..101
    }

    test("parse 4d6-L drops lowest 1") {
        val expr = DiceNotation.parse("4d6-L")
        val result = expr.evaluate(source)
        result.rolls.size shouldBe 4
        result.total shouldBeInRange 3..18
    }

    test("parse 4d6-L2 drops lowest 2") {
        val expr = DiceNotation.parse("4d6-L2")
        val result = expr.evaluate(source)
        result.rolls.size shouldBe 4
        result.total shouldBeInRange 2..12
    }

    test("parse 4d6-H drops highest 1") {
        val expr = DiceNotation.parse("4d6-H")
        val result = expr.evaluate(source)
        result.rolls.size shouldBe 4
        result.total shouldBeInRange 3..18
    }

    test("parse 4d6-H2 drops highest 2") {
        val expr = DiceNotation.parse("4d6-H2")
        val result = expr.evaluate(source)
        result.rolls.size shouldBe 4
        result.total shouldBeInRange 2..12
    }

    test("4d6-L is equivalent to 4d6kh3") {
        val dropExpr = DiceNotation.parse("4d6-L")
        val keepExpr = DiceNotation.parse("4d6kh3")
        val dropSource = RandomSource(Random(99))
        val keepSource = RandomSource(Random(99))
        repeat(50) {
            val dropResult = dropExpr.evaluate(dropSource)
            val keepResult = keepExpr.evaluate(keepSource)
            dropResult.total shouldBe keepResult.total
            dropResult.rolls shouldBe keepResult.rolls
        }
    }

    test("subtraction still works after drop support") {
        val expr = DiceNotation.parse("2d6-1")
        val result = expr.evaluate(source)
        result.total shouldBeInRange 1..11
    }

    test("parse dF.1") {
        val expr = DiceNotation.parse("dF.1")
        val result = expr.evaluate(source)
        result.total shouldBeInRange -1..1
        result.rolls.size shouldBe 1
    }

    test("parse 4dF.1") {
        val expr = DiceNotation.parse("4dF.1")
        val result = expr.evaluate(source)
        result.total shouldBeInRange -4..4
        result.rolls.size shouldBe 4
        result.rolls.forEach { it shouldBeInRange -1..1 }
    }

    test("dF.1 produces more zeros than dF") {
        val fudge1Source = RandomSource(Random(42))
        val fudge2Source = RandomSource(Random(42))
        val expr1 = DiceNotation.parse("dF.1")
        val expr2 = DiceNotation.parse("dF")
        val n = 600
        val zeros1 = (1..n).count { expr1.evaluate(fudge1Source).total == 0 }
        val zeros2 = (1..n).count { expr2.evaluate(fudge2Source).total == 0 }
        (zeros1 > zeros2) shouldBe true
    }

    test("dF defaults to grade 2") {
        val expr = DiceNotation.parse("dF")
        val sameSource1 = RandomSource(Random(77))
        val sameSource2 = RandomSource(Random(77))
        val exprExplicit = DiceNotation.parse("dF.2")
        repeat(50) {
            expr.evaluate(sameSource1).total shouldBe exprExplicit.evaluate(sameSource2).total
        }
    }

    test("parse d6! exploding die") {
        val expr = DiceNotation.parse("d6!")
        val result = expr.evaluate(source)
        result.total shouldBeInRange 1..Int.MAX_VALUE
        result.rolls.forEach { it shouldBeInRange 1..6 }
    }

    test("parse 2d6! each die explodes independently") {
        val expr = DiceNotation.parse("2d6!")
        val result = expr.evaluate(source)
        result.total shouldBeInRange 2..Int.MAX_VALUE
        result.rolls.forEach { it shouldBeInRange 1..6 }
    }

    test("parse d6X alternate exploding syntax") {
        val bangSource = RandomSource(Random(42))
        val xSource = RandomSource(Random(42))
        val bangExpr = DiceNotation.parse("d6!")
        val xExpr = DiceNotation.parse("d6X")
        repeat(50) {
            bangExpr.evaluate(bangSource).total shouldBe xExpr.evaluate(xSource).total
        }
    }

    test("exploding die can produce more rolls than dice count") {
        val expr = ExplodingDie(2)
        val localSource = RandomSource(Random(42))
        val results = (1..100).map { expr.evaluate(localSource) }
        results.any { it.rolls.size > 1 } shouldBe true
    }

    test("exploding die respects max depth") {
        val expr = ExplodingDie(2, maxDepth = 3)
        val localSource = RandomSource(Random(42))
        repeat(100) {
            val result = expr.evaluate(localSource)
            result.rolls.size shouldBeInRange 1..3
        }
    }

    test("parse d6!+3") {
        val expr = DiceNotation.parse("d6!+3")
        val result = expr.evaluate(source)
        result.total shouldBeInRange 4..Int.MAX_VALUE
    }
})
