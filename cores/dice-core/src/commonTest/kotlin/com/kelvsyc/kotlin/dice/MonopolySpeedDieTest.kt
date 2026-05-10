package com.kelvsyc.kotlin.dice

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.random.Random

sealed class SpeedDieFace {
    data class Number(val value: Int) : SpeedDieFace()
    data object MrMonopoly : SpeedDieFace()
    data object Bus : SpeedDieFace()
}

sealed class MonopolyRoll {
    abstract val die1: Int
    abstract val die2: Int

    data class Normal(override val die1: Int, override val die2: Int, val speedValue: Int) : MonopolyRoll()
    data class MrMonopoly(override val die1: Int, override val die2: Int) : MonopolyRoll()
    data class Bus(override val die1: Int, override val die2: Int) : MonopolyRoll()
}

class MonopolySpeedDieTest : FunSpec({
    val speedDie = TypedDie(
        listOf(
            SpeedDieFace.Number(1),
            SpeedDieFace.Number(2),
            SpeedDieFace.Number(3),
            SpeedDieFace.MrMonopoly,
            SpeedDieFace.MrMonopoly,
            SpeedDieFace.Bus,
        )
    )

    val monopolyRoll: TypedRollExpression<MonopolyRoll> = zip(Die(6), Die(6), speedDie) { d1, d2, speed ->
        when (speed) {
            is SpeedDieFace.Number -> MonopolyRoll.Normal(d1.total, d2.total, speed.value)
            is SpeedDieFace.MrMonopoly -> MonopolyRoll.MrMonopoly(d1.total, d2.total)
            is SpeedDieFace.Bus -> MonopolyRoll.Bus(d1.total, d2.total)
        }
    }

    test("monopoly roll produces valid die values") {
        val source = RandomSource(Random(42))
        repeat(100) {
            val result = monopolyRoll.evaluate(source)
            result.die1 shouldBeInRange 1..6
            result.die2 shouldBeInRange 1..6
        }
    }

    test("monopoly roll produces all variant types") {
        val source = RandomSource(Random(42))
        val results = (1..200).map { monopolyRoll.evaluate(source) }
        results.any { it is MonopolyRoll.Normal } shouldBe true
        results.any { it is MonopolyRoll.MrMonopoly } shouldBe true
        results.any { it is MonopolyRoll.Bus } shouldBe true
    }

    test("normal roll includes speed value in 1..3") {
        val source = RandomSource(Random(42))
        val normals = (1..200).map { monopolyRoll.evaluate(source) }.filterIsInstance<MonopolyRoll.Normal>()
        normals.forEach { it.speedValue shouldBeInRange 1..3 }
    }

    test("typed die with map") {
        val source = RandomSource(Random(42))
        val doubleDie = Die(6).map { it.total * 2 }
        repeat(50) {
            val result = doubleDie.evaluate(source)
            result shouldBeInRange 2..12
        }
    }

    test("typed die with flatMap") {
        val source = RandomSource(Random(42))
        val explodingD6 = Die(6).flatMap { roll ->
            if (roll.total == 6) {
                Die(6).map { bonus -> roll.total + bonus.total }
            } else {
                TypedRollExpression { roll.total }
            }
        }
        repeat(100) {
            val result = explodingD6.evaluate(source)
            result shouldBeInRange 1..12
        }
    }

    test("speed die individually") {
        val source = RandomSource(Random(42))
        val results = (1..60).map { speedDie.evaluate(source) }
        results.filterIsInstance<SpeedDieFace.Number>().forEach {
            it.value shouldBeInRange 1..3
        }
    }
})
