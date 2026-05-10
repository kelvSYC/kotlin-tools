package com.kelvsyc.kotlin.dice

/**
 * Parses standard dice notation strings into [RollExpression] trees.
 *
 * Supported syntax:
 * - `N` — constant integer
 * - `dS` — single die with S sides (shorthand for 1dS)
 * - `NdS` — roll N dice with S sides
 * - `NdSkhK` — roll N dice, keep highest K
 * - `NdSklK` — roll N dice, keep lowest K
 * - `NdS-HN` or `NdS-H` — roll N dice, drop highest N (default 1)
 * - `NdS-LN` or `NdS-L` — roll N dice, drop lowest N (default 1)
 * - `d%` or `Nd%` — percentile dice (d100)
 * - `dF` or `NdF` — Fudge/FATE dice (grade 2: two +1, two 0, two -1)
 * - `dF.1` or `NdF.1` — Fudge die grade 1 (one +1, four 0, one -1)
 * - `dS!` or `NdS!` — exploding dice (re-roll and add on max)
 * - `dSX` or `NdSX` — exploding dice (alternate syntax)
 * - `expr + expr` — addition
 * - `expr - expr` — subtraction
 *
 * This parser covers common dice notation for convenience. Modifiers cannot be combined
 * (e.g., `4d6!kh3` is not supported). For more complex use cases, compose [RollExpression]
 * instances directly using [TypedRollExpression] combinators such as [map], [flatMap],
 * and [zip].
 */
object DiceNotation {
    fun parse(notation: String): RollExpression {
        val tokens = tokenize(notation)
        return parseExpression(tokens, 0).first
    }

    private sealed interface Token {
        data class Number(val value: Int) : Token
        data object D : Token
        data object Percent : Token
        data class Fudge(val grade: Int = 2) : Token
        data object Plus : Token
        data object Minus : Token
        data class Keep(val high: Boolean) : Token
        data class Drop(val high: Boolean) : Token
        data object Explode : Token
    }

    private fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        val s = input.replace(" ", "")
        while (i < s.length) {
            when {
                s[i].isDigit() -> {
                    var end = i
                    while (end < s.length && s[end].isDigit()) end++
                    tokens.add(Token.Number(s.substring(i, end).toInt()))
                    i = end
                }
                s[i] == 'd' || s[i] == 'D' -> {
                    tokens.add(Token.D)
                    i++
                    if (i < s.length && s[i] == '%') {
                        tokens.add(Token.Percent)
                        i++
                    } else if (i < s.length && (s[i] == 'F' || s[i] == 'f')) {
                        i++
                        if (i < s.length && s[i] == '.' && i + 1 < s.length && s[i + 1].isDigit()) {
                            val grade = s[i + 1].digitToInt()
                            tokens.add(Token.Fudge(grade))
                            i += 2
                        } else {
                            tokens.add(Token.Fudge())
                        }
                    }
                }
                s[i] == '+' -> {
                    tokens.add(Token.Plus)
                    i++
                }
                s[i] == '-' -> {
                    if (i + 1 < s.length && (s[i + 1] == 'L' || s[i + 1] == 'l')) {
                        tokens.add(Token.Drop(high = false))
                        i += 2
                    } else if (i + 1 < s.length && (s[i + 1] == 'H' || s[i + 1] == 'h')) {
                        tokens.add(Token.Drop(high = true))
                        i += 2
                    } else {
                        tokens.add(Token.Minus)
                        i++
                    }
                }
                s[i] == '!' || s[i] == 'X' || s[i] == 'x' -> {
                    tokens.add(Token.Explode)
                    i++
                }
                s[i] == 'k' || s[i] == 'K' -> {
                    val high = i + 1 < s.length && (s[i + 1] == 'h' || s[i + 1] == 'H')
                    tokens.add(Token.Keep(high))
                    i += 2
                }
                else -> error("Unexpected character '${s[i]}' in dice notation: $input")
            }
        }
        return tokens
    }

    private fun parseExpression(tokens: List<Token>, pos: Int): Pair<RollExpression, Int> {
        var (left, i) = parseTerm(tokens, pos)
        while (i < tokens.size) {
            when (tokens[i]) {
                Token.Plus -> {
                    val (right, next) = parseTerm(tokens, i + 1)
                    left = Add(left, right)
                    i = next
                }
                Token.Minus -> {
                    val (right, next) = parseTerm(tokens, i + 1)
                    left = Subtract(left, right)
                    i = next
                }
                else -> break
            }
        }
        return left to i
    }

    private fun parseTerm(tokens: List<Token>, pos: Int): Pair<RollExpression, Int> {
        if (pos >= tokens.size) error("Unexpected end of expression")

        return when (val token = tokens[pos]) {
            is Token.Number -> {
                if (pos + 1 < tokens.size && tokens[pos + 1] is Token.D) {
                    parseDice(token.value, tokens, pos + 2)
                } else {
                    Constant(token.value) to (pos + 1)
                }
            }
            is Token.D -> {
                if (pos + 1 < tokens.size && tokens[pos + 1] is Token.Percent) {
                    MultipleDice(1, 100) to (pos + 2)
                } else if (pos + 1 < tokens.size && tokens[pos + 1] is Token.Fudge) {
                    MultipleFudgeDice(1, (tokens[pos + 1] as Token.Fudge).grade) to (pos + 2)
                } else {
                    parseDice(1, tokens, pos + 1)
                }
            }
            else -> error("Unexpected token: $token")
        }
    }

    private fun parseDice(count: Int, tokens: List<Token>, pos: Int): Pair<RollExpression, Int> {
        if (pos < tokens.size && tokens[pos] is Token.Percent) {
            return MultipleDice(count, 100) to (pos + 1)
        }
        if (pos < tokens.size && tokens[pos] is Token.Fudge) {
            return MultipleFudgeDice(count, (tokens[pos] as Token.Fudge).grade) to (pos + 1)
        }
        if (pos >= tokens.size || tokens[pos] !is Token.Number) {
            error("Expected number of sides after 'd'")
        }
        val sides = (tokens[pos] as Token.Number).value
        val nextPos = pos + 1

        if (nextPos < tokens.size && tokens[nextPos] is Token.Explode) {
            return MultipleExplodingDice(count, sides) to (nextPos + 1)
        }

        if (nextPos < tokens.size && tokens[nextPos] is Token.Keep) {
            val high = (tokens[nextPos] as Token.Keep).high
            if (nextPos + 1 >= tokens.size || tokens[nextPos + 1] !is Token.Number) {
                error("Expected number after keep modifier")
            }
            val keep = (tokens[nextPos + 1] as Token.Number).value
            val expr = if (high) KeepHighest(count, sides, keep) else KeepLowest(count, sides, keep)
            return expr to (nextPos + 2)
        }

        if (nextPos < tokens.size && tokens[nextPos] is Token.Drop) {
            val high = (tokens[nextPos] as Token.Drop).high
            val drop = if (nextPos + 1 < tokens.size && tokens[nextPos + 1] is Token.Number) {
                (tokens[nextPos + 1] as Token.Number).value
            } else {
                null
            }
            val dropCount = drop ?: 1
            val expr = if (high) DropHighest(count, sides, dropCount) else DropLowest(count, sides, dropCount)
            return expr to (nextPos + if (drop != null) 2 else 1)
        }

        return MultipleDice(count, sides) to nextPos
    }
}
