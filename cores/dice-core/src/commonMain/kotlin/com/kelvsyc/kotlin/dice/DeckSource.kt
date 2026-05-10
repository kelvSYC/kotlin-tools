package com.kelvsyc.kotlin.dice

/**
 * A [RandomSource] backed by a finite deck of pre-determined outcomes for each die size.
 *
 * When a value is requested for a given range, the next value is drawn from the corresponding
 * deck. When the deck is exhausted, it is reshuffled using the provided [shuffleSource].
 *
 * This models draw-without-replacement at the individual die level. For pre-composed decks
 * representing aggregate outcomes (e.g., a deck of 36 cards for 2d6 sums), use
 * [DeckExpression] instead.
 */
class DeckSource(
    private val decks: Map<Int, List<Int>>,
    private val shuffleSource: RandomSource = RandomSource(kotlin.random.Random),
) : RandomSource {
    private val currentDecks: MutableMap<Int, MutableList<Int>> = mutableMapOf()

    override fun nextInt(from: Int, until: Int): Int {
        val size = until - from
        val deck = currentDecks.getOrPut(size) { prepareDeck(size) }
        if (deck.isEmpty()) {
            deck.addAll(prepareDeck(size))
        }
        return deck.removeFirst() + from
    }

    private fun prepareDeck(size: Int): MutableList<Int> {
        val source = decks[size] ?: (0 until size).toList()
        return source.shuffled(kotlin.random.Random(shuffleSource.nextInt(0, Int.MAX_VALUE))).toMutableList()
    }
}
