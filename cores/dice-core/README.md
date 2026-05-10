# dice-core

A Kotlin Multiplatform library for dice rolling, dice notation parsing, and generalized randomness-from-a-deck mechanics. Targets JVM and JS.

## Installation

Published as `com.kelvsyc.kotlin:dice-core` to GitHub Packages.

```kotlin
dependencies {
    implementation("com.kelvsyc.kotlin:dice-core:<version>")
}
```

## Quick start

```kotlin
import com.kelvsyc.kotlin.dice.*
import kotlin.random.Random

val source = RandomSource(Random)
val expr = DiceNotation.parse("2d6+3")
val result = expr.evaluate(source)
println("Total: ${result.total}, Rolls: ${result.rolls}")
```

## Dice notation

`DiceNotation.parse(notation)` parses a string into a `RollExpression`.

| Notation      | Meaning                                        |
|---------------|------------------------------------------------|
| `N`           | Constant integer                               |
| `dS`          | Single die with S sides (shorthand for `1dS`)  |
| `NdS`         | Roll N dice with S sides, sum the results      |
| `NdSkhK`      | Roll N dice, keep highest K                    |
| `NdSklK`      | Roll N dice, keep lowest K                     |
| `NdS-L`       | Roll N dice, drop lowest 1                     |
| `NdS-LN`      | Roll N dice, drop lowest N                     |
| `NdS-H`       | Roll N dice, drop highest 1                    |
| `NdS-HN`      | Roll N dice, drop highest N                    |
| `d%` / `Nd%`  | Percentile dice (d100)                         |
| `dF` / `NdF`  | Fudge/FATE dice (grade 2: two +1, two 0, two -1 on a d6) |
| `dF.1`        | Fudge die grade 1 (one +1, four 0, one -1)     |
| `expr + expr` | Addition                                       |
| `expr - expr` | Subtraction                                    |

```kotlin
// D&D ability score: roll 4d6, drop the lowest
val abilityScore = DiceNotation.parse("4d6-L")

// Percentile roll plus a modifier
val percentile = DiceNotation.parse("d%+5")

// FATE dice
val fate = DiceNotation.parse("4dF")
```

## Core concepts

### RandomSource

A `fun interface` abstracting over a source of randomness. Wraps any `kotlin.random.Random`:

```kotlin
val source = RandomSource(Random(seed = 42))
val value = source.nextInt(1, 7)  // 1..6
```

### RollExpression

A `fun interface` that evaluates against a `RandomSource` to produce a `RollResult` (total + individual rolls). All parsed dice notation produces `RollExpression` instances.

Expression types: `Constant`, `Die`, `MultipleDice`, `KeepHighest`, `KeepLowest`, `DropHighest`, `DropLowest`, `MultipleFudgeDice`, `Add`, `Subtract`.

### TypedRollExpression\<T\>

The generalized form of `RollExpression`. Produces any type `T`, not just numeric results. `RollExpression` extends `TypedRollExpression<RollResult>`.

Combinators for composition:

```kotlin
// map: transform the result
val doubled = Die(6).map { it.total * 2 }

// flatMap: chain expressions (e.g., exploding dice)
val explodingD6 = Die(6).flatMap { roll ->
    if (roll.total == 6) Die(6).map { bonus -> roll.total + bonus.total }
    else TypedRollExpression { roll.total }
}

// zip: combine two expressions
val combined = Die(6).zip(Die(8)) { a, b -> a.total + b.total }

// zip (3-ary top-level function)
val triple = zip(Die(6), Die(6), Die(6)) { a, b, c ->
    listOf(a.total, b.total, c.total)
}
```

### TypedDie\<T\>

A die whose faces can be any type. Duplicates in the face list model faces that appear more than once:

```kotlin
sealed class CoinFlip {
    data object Heads : CoinFlip()
    data object Tails : CoinFlip()
}

val coin = TypedDie(listOf(CoinFlip.Heads, CoinFlip.Tails))
val flip = coin.evaluate(source)
```

## Composing typed expressions

Multiple dice can be composed into a sealed result type. Here's the Monopoly Speed Die as an example:

```kotlin
sealed class SpeedDieFace {
    data class Number(val value: Int) : SpeedDieFace()
    data object MrMonopoly : SpeedDieFace()
    data object Bus : SpeedDieFace()
}

val speedDie = TypedDie(listOf(
    SpeedDieFace.Number(1),
    SpeedDieFace.Number(2),
    SpeedDieFace.Number(3),
    SpeedDieFace.MrMonopoly,
    SpeedDieFace.MrMonopoly,
    SpeedDieFace.Bus,
))

sealed class MonopolyRoll {
    abstract val die1: Int
    abstract val die2: Int

    data class Normal(override val die1: Int, override val die2: Int, val speedValue: Int) : MonopolyRoll()
    data class MrMonopoly(override val die1: Int, override val die2: Int) : MonopolyRoll()
    data class Bus(override val die1: Int, override val die2: Int) : MonopolyRoll()
}

val monopolyRoll: TypedRollExpression<MonopolyRoll> =
    zip(Die(6), Die(6), speedDie) { d1, d2, speed ->
        when (speed) {
            is SpeedDieFace.Number -> MonopolyRoll.Normal(d1.total, d2.total, speed.value)
            is SpeedDieFace.MrMonopoly -> MonopolyRoll.MrMonopoly(d1.total, d2.total)
            is SpeedDieFace.Bus -> MonopolyRoll.Bus(d1.total, d2.total)
        }
    }
```

## Deck-based mechanics

### DeckSource

A `RandomSource` that draws without replacement from a finite deck per die size. When the deck is exhausted, it reshuffles. Use this when you want individual die rolls backed by a deck:

```kotlin
val deck = DeckSource(
    decks = mapOf(6 to (0 until 6).toList()),
    shuffleSource = RandomSource(Random(42)),
)
// Each face appears exactly once before reshuffling
val result = MultipleDice(2, 6).evaluate(deck)
```

### DeckExpression\<T\>

A `TypedRollExpression<T>` for pre-composed decks of aggregate outcomes. Each `evaluate()` draws the next card; the deck reshuffles on exhaustion. Supports an optional reshuffling sentinel card.

```kotlin
// Catan Event Cards: a 36-card deck representing 2d6 sums
val catanCards: List<Int> = buildList {
    repeat(1) { add(2) }   // one "2"
    repeat(2) { add(3) }   // two "3"s
    repeat(3) { add(4) }
    repeat(4) { add(5) }
    repeat(5) { add(6) }
    repeat(6) { add(7) }
    repeat(5) { add(8) }
    repeat(4) { add(9) }
    repeat(3) { add(10) }
    repeat(2) { add(11) }
    repeat(1) { add(12) }  // one "12"
}

val catanDeck = DeckExpression(
    cards = catanCards,
    sentinelFromBottom = 6,  // "New Year" card, 6th from bottom
    shuffleSource = RandomSource(Random(42)),
)

val source = RandomSource(Random)
val roll = catanDeck.evaluate(source)  // draws next card (2..12)
```

The key distinction: `DeckSource` is a *randomness source* you plug under any `RollExpression`, while `DeckExpression` *is* the expression — it represents the entire deck as a draw-from sequence.
