package com.kelvsyc.kotlin.moshi

/**
 * A segment in a parsed [JsonPath] expression.
 */
sealed class JsonPathSegment {
    /** Access an object property by name. */
    data class Key(val name: String) : JsonPathSegment()

    /** Access an array element by index. */
    data class Index(val index: Int) : JsonPathSegment()

    /** Wildcard: all children of an object or array. */
    data object Wildcard : JsonPathSegment()

    /** Recursive descent: search all descendants. */
    data object RecursiveDescent : JsonPathSegment()
}

/**
 * A parsed JSONPath expression supporting a subset of the JSONPath query language.
 *
 * Supported syntax:
 * - `$` — the root element
 * - `.key` or `['key']` — child access by name
 * - `[0]` — child access by array index
 * - `[*]` or `.*` — wildcard (all children)
 * - `..key` — recursive descent
 *
 * This implementation does not aim for full RFC 9535 compatibility. Notable omissions
 * include filter expressions, array slices, and function extensions.
 *
 * @property segments The parsed path segments.
 */
@JvmInline
value class JsonPath private constructor(val segments: List<JsonPathSegment>) {

    /**
     * Evaluates this path against the given [root] value, returning all matching nodes.
     */
    fun query(root: JsonValue): List<JsonValue> {
        return evaluate(listOf(root), segments)
    }

    /**
     * Evaluates this path against the given [root] value, returning the single matching
     * node or `null` if zero or multiple nodes match.
     */
    fun queryOne(root: JsonValue): JsonValue? {
        val results = query(root)
        return results.singleOrNull()
    }

    override fun toString(): String = buildString {
        append('$')
        var i = 0
        while (i < segments.size) {
            when (val segment = segments[i]) {
                is JsonPathSegment.RecursiveDescent -> {
                    append("..")
                    if (i + 1 < segments.size) {
                        val next = segments[i + 1]
                        if (next is JsonPathSegment.Key) {
                            append(next.name)
                            i += 2
                            continue
                        }
                    }
                }
                is JsonPathSegment.Key -> {
                    if (segment.name.all { it.isLetterOrDigit() || it == '_' }) {
                        append('.').append(segment.name)
                    } else {
                        append("['").append(segment.name).append("']")
                    }
                }
                is JsonPathSegment.Index -> append('[').append(segment.index).append(']')
                is JsonPathSegment.Wildcard -> append(".*")
            }
            i++
        }
    }

    companion object {
        /**
         * Parses a JSONPath expression string.
         *
         * @throws IllegalArgumentException if the expression is malformed.
         */
        fun parse(expression: String): JsonPath {
            require(expression.startsWith("$")) { "JsonPath must start with '$'" }
            val segments = mutableListOf<JsonPathSegment>()
            var i = 1

            while (i < expression.length) {
                when {
                    expression.startsWith("..", i) -> {
                        i += 2
                        segments.add(JsonPathSegment.RecursiveDescent)
                        if (i < expression.length && expression[i] != '[' && expression[i] != '.') {
                            val start = i
                            while (i < expression.length && expression[i] != '.' && expression[i] != '[') i++
                            segments.add(JsonPathSegment.Key(expression.substring(start, i)))
                        }
                    }
                    expression[i] == '.' -> {
                        i++
                        if (i < expression.length && expression[i] == '*') {
                            segments.add(JsonPathSegment.Wildcard)
                            i++
                        } else {
                            val start = i
                            while (i < expression.length && expression[i] != '.' && expression[i] != '[') i++
                            require(i > start) { "Empty key at position $start" }
                            segments.add(JsonPathSegment.Key(expression.substring(start, i)))
                        }
                    }
                    expression[i] == '[' -> {
                        i++
                        when {
                            i < expression.length && expression[i] == '*' -> {
                                segments.add(JsonPathSegment.Wildcard)
                                i++
                                require(i < expression.length && expression[i] == ']') { "Expected ']' after '[*'" }
                                i++
                            }
                            i < expression.length && expression[i] == '\'' -> {
                                i++
                                val start = i
                                while (i < expression.length && expression[i] != '\'') i++
                                require(i < expression.length) { "Unterminated string in bracket notation" }
                                segments.add(JsonPathSegment.Key(expression.substring(start, i)))
                                i++
                                require(i < expression.length && expression[i] == ']') { "Expected ']' after quoted key" }
                                i++
                            }
                            else -> {
                                val start = i
                                while (i < expression.length && expression[i] != ']') i++
                                require(i < expression.length) { "Unterminated bracket notation" }
                                val content = expression.substring(start, i)
                                val index = content.toIntOrNull()
                                require(index != null) { "Invalid array index: $content" }
                                segments.add(JsonPathSegment.Index(index))
                                i++
                            }
                        }
                    }
                    else -> error("Unexpected character '${expression[i]}' at position $i")
                }
            }
            return JsonPath(segments)
        }
    }
}

private fun evaluate(nodes: List<JsonValue>, segments: List<JsonPathSegment>): List<JsonValue> {
    if (segments.isEmpty()) return nodes

    val segment = segments.first()
    val rest = segments.subList(1, segments.size)

    return when (segment) {
        is JsonPathSegment.Key -> {
            val next = nodes.mapNotNull { node ->
                when (node) {
                    is JsonObject -> node.entries[segment.name]
                    else -> null
                }
            }
            evaluate(next, rest)
        }
        is JsonPathSegment.Index -> {
            val next = nodes.mapNotNull { node ->
                when (node) {
                    is JsonArray -> node.elements.getOrNull(segment.index)
                    else -> null
                }
            }
            evaluate(next, rest)
        }
        is JsonPathSegment.Wildcard -> {
            val next = nodes.flatMap { node ->
                when (node) {
                    is JsonObject -> node.entries.values
                    is JsonArray -> node.elements
                    else -> emptyList()
                }
            }
            evaluate(next, rest)
        }
        is JsonPathSegment.RecursiveDescent -> {
            val descendants = nodes.flatMap { collectAll(it) }
            evaluate(descendants, rest)
        }
    }
}

private fun collectAll(node: JsonValue): List<JsonValue> = buildList {
    add(node)
    when (node) {
        is JsonObject -> node.entries.values.forEach { addAll(collectAll(it)) }
        is JsonArray -> node.elements.forEach { addAll(collectAll(it)) }
        else -> {}
    }
}
