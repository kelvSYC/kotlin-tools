package com.kelvsyc.kotlin.dice

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import kotlin.random.Random

class DeckExpressionTest : FunSpec({
    test("draws all cards before reshuffling") {
        val deck = DeckExpression(
            cards = listOf(1, 2, 3, 4, 5, 6),
            shuffleSource = RandomSource(Random(42)),
        )
        val source = RandomSource(Random(0))
        val results = (1..6).map { deck.evaluate(source) }
        results.toSet() shouldBe setOf(1, 2, 3, 4, 5, 6)
    }

    test("reshuffles after exhaustion") {
        val deck = DeckExpression(
            cards = listOf(1, 2, 3, 4, 5, 6),
            shuffleSource = RandomSource(Random(42)),
        )
        val source = RandomSource(Random(0))
        val results = (1..12).map { deck.evaluate(source) }
        results.forEach { it shouldBeInRange 1..6 }
        results.take(6).toSet() shouldBe setOf(1, 2, 3, 4, 5, 6)
        results.drop(6).toSet() shouldBe setOf(1, 2, 3, 4, 5, 6)
    }

    test("typed deck with non-integer values") {
        val deck = DeckExpression(
            cards = listOf("alpha", "beta", "gamma"),
            shuffleSource = RandomSource(Random(42)),
        )
        val source = RandomSource(Random(0))
        val results = (1..3).map { deck.evaluate(source) }
        results.toSet() shouldBe setOf("alpha", "beta", "gamma")
    }

    test("sentinel triggers reshuffle before full deck is drawn") {
        // 6 cards with sentinel 3rd from bottom: only first 4 cards are drawn
        // before the sentinel triggers a reshuffle.
        val deck = DeckExpression(
            cards = listOf(1, 2, 3, 4, 5, 6),
            sentinelFromBottom = 3,
            shuffleSource = RandomSource(Random(42)),
        )
        val source = RandomSource(Random(0))
        val results = (1..6).map { deck.evaluate(source) }
        results.forEach { it shouldBeInRange 1..6 }
        // With sentinel at 3 from bottom, we can only draw ~4 cards per cycle,
        // so 6 draws must span at least 2 shuffles — not all 6 distinct values
        // will appear in a single run of 4.
        (results.toSet().size < 6) shouldBe true
    }

    test("sentinel never appears as a drawn value") {
        val deck = DeckExpression(
            cards = listOf(1, 2, 3),
            sentinelFromBottom = 1,
            shuffleSource = RandomSource(Random(42)),
        )
        val source = RandomSource(Random(0))
        val results = (1..50).map { deck.evaluate(source) }
        results.forEach { it shouldBeInRange 1..3 }
    }

    test("Catan event card distribution") {
        val catanCards: List<Int> = buildList {
            repeat(1) { add(2) }
            repeat(2) { add(3) }
            repeat(3) { add(4) }
            repeat(4) { add(5) }
            repeat(5) { add(6) }
            repeat(6) { add(7) }
            repeat(5) { add(8) }
            repeat(4) { add(9) }
            repeat(3) { add(10) }
            repeat(2) { add(11) }
            repeat(1) { add(12) }
        }

        val deck = DeckExpression(
            cards = catanCards,
            sentinelFromBottom = 6,
            shuffleSource = RandomSource(Random(42)),
        )
        val source = RandomSource(Random(0))
        val results = (1..100).map { deck.evaluate(source) }
        results.forEach { it shouldBeInRange 2..12 }
    }

    test("deck without sentinel draws complete deck each cycle") {
        val deck = DeckExpression(
            cards = listOf(10, 20, 30),
            shuffleSource = RandomSource(Random(42)),
        )
        val source = RandomSource(Random(0))
        // Each cycle of 3 draws should contain all 3 values
        repeat(5) {
            val cycle = (1..3).map { deck.evaluate(source) }
            cycle.toSet() shouldBe setOf(10, 20, 30)
        }
    }

    test("composes with map") {
        val deck = DeckExpression(
            cards = listOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
            shuffleSource = RandomSource(Random(42)),
        )
        val labeled = deck.map { value ->
            when (value) {
                7 -> "lucky seven"
                else -> "rolled $value"
            }
        }
        val source = RandomSource(Random(0))
        val results = (1..11).map { labeled.evaluate(source) }
        results.any { it == "lucky seven" } shouldBe true
        results.any { it.startsWith("rolled ") } shouldBe true
    }
})
