package com.kelvsyc.kotlin.dice

/**
 * A [TypedRollExpression] backed by a finite deck of pre-determined outcomes.
 *
 * Each call to [evaluate] draws the next card from the deck. When the deck is exhausted,
 * it is reshuffled. This models mechanics where the full distribution of outcomes is dealt
 * out before repeating — for example, a deck of 36 cards representing all 2d6 sums with
 * their correct frequencies.
 *
 * Decks may optionally include a reshuffling sentinel at a fixed position from the bottom.
 * When drawn, the sentinel triggers an immediate reshuffle and re-draw rather than producing
 * a value. This models mechanics like the Catan Event Cards, where a "New Year" card is
 * placed 6th from the bottom and causes a reshuffle when revealed.
 *
 * @param cards the complete set of outcomes in the deck. Duplicates represent cards that
 *   appear more than once (e.g., six 7s in a Catan event deck).
 * @param sentinelFromBottom if set, a sentinel is inserted this many positions from the
 *   bottom of the deck after each shuffle (e.g., 6 means the sentinel is the 6th card from
 *   the bottom). When the sentinel is drawn, the deck reshuffles and a new card is drawn.
 * @param shuffleSource the randomness source used for shuffling the deck.
 */
class DeckExpression<out T>(
    private val cards: List<T>,
    private val sentinelFromBottom: Int? = null,
    private val shuffleSource: RandomSource = RandomSource(kotlin.random.Random),
) : TypedRollExpression<T> {
    init {
        require(cards.isNotEmpty()) { "A deck must have at least one card" }
        if (sentinelFromBottom != null) {
            require(sentinelFromBottom in 1..cards.size) {
                "sentinelFromBottom must be between 1 and the deck size (${cards.size})"
            }
        }
    }

    private sealed class DeckEntry<out T> {
        data class Card<out T>(val value: T) : DeckEntry<T>()
        data object Sentinel : DeckEntry<Nothing>()
    }

    private val currentDeck: MutableList<DeckEntry<T>> = mutableListOf()

    override fun evaluate(source: RandomSource): T {
        while (true) {
            if (currentDeck.isEmpty()) {
                currentDeck.addAll(prepareDeck())
            }
            when (val entry = currentDeck.removeFirst()) {
                is DeckEntry.Card -> return entry.value
                is DeckEntry.Sentinel -> {
                    currentDeck.clear()
                    currentDeck.addAll(prepareDeck())
                }
            }
        }
    }

    private fun prepareDeck(): List<DeckEntry<T>> {
        val shuffled = cards
            .shuffled(kotlin.random.Random(shuffleSource.nextInt(0, Int.MAX_VALUE)))
            .map<T, DeckEntry<T>> { DeckEntry.Card(it) }
            .toMutableList()

        if (sentinelFromBottom != null) {
            val insertIndex = (shuffled.size - sentinelFromBottom + 1).coerceIn(0, shuffled.size)
            shuffled.add(insertIndex, DeckEntry.Sentinel)
        }

        return shuffled
    }
}
