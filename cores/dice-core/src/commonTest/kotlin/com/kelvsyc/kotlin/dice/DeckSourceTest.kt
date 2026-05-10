package com.kelvsyc.kotlin.dice

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import kotlin.random.Random

class DeckSourceTest : FunSpec({
    test("deck source produces values in range") {
        val deck = DeckSource(
            decks = mapOf(6 to (0 until 6).toList()),
            shuffleSource = RandomSource(Random(42)),
        )
        val results = (1..6).map { deck.nextInt(1, 7) }
        results.forEach { it shouldBeInRange 1..6 }
        results.toSet().size shouldBe 6
    }

    test("deck reshuffles when exhausted") {
        val deck = DeckSource(
            decks = mapOf(6 to (0 until 6).toList()),
            shuffleSource = RandomSource(Random(42)),
        )
        val results = (1..12).map { deck.nextInt(1, 7) }
        results.forEach { it shouldBeInRange 1..6 }
    }

    test("deck source works with roll expressions") {
        val deck = DeckSource(
            decks = mapOf(6 to (0 until 6).toList()),
            shuffleSource = RandomSource(Random(42)),
        )
        val expr = MultipleDice(2, 6)
        val result = expr.evaluate(deck)
        result.total shouldBeInRange 2..12
        result.rolls.size shouldBe 2
    }
})
