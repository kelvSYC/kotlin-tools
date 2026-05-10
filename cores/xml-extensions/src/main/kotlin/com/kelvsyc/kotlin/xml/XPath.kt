package com.kelvsyc.kotlin.xml

import javax.xml.namespace.QName

/**
 * A step in a parsed [XPath] expression.
 */
sealed class XPathStep {
    /** Navigate to a child element by name. */
    data class Child(val name: String) : XPathStep()

    /** Navigate to a child element by qualified name. */
    data class QualifiedChild(val prefix: String, val localName: String) : XPathStep()

    /** Wildcard: all child elements. */
    data object Wildcard : XPathStep()

    /** Select text nodes. */
    data object TextNodes : XPathStep()

    /** Select all child nodes regardless of type. */
    data object AllNodes : XPathStep()

    /** Access an attribute by name. */
    data class Attribute(val name: String) : XPathStep()

    /** Recursive descent (from `//`): search all descendants. */
    data object RecursiveDescent : XPathStep()
}

/**
 * A predicate that filters nodes selected by an [XPathStep].
 */
sealed class XPathPredicate {
    /** Positional predicate (1-based index). */
    data class Position(val index: Int) : XPathPredicate()

    /** Attribute value equality predicate. */
    data class AttributeEquals(val name: String, val value: String) : XPathPredicate()

    /** Child element existence predicate. */
    data class ChildExists(val name: String) : XPathPredicate()
}

/**
 * A single location step with optional predicates.
 */
data class XPathLocation(val step: XPathStep, val predicates: List<XPathPredicate> = emptyList())

/**
 * A parsed XPath expression supporting a practical subset of XPath 1.0.
 *
 * Supported syntax:
 * - `root/child/grandchild` — child element navigation
 * - `//element` — descendant search
 * - `*` — any child element
 * - `@attr` — attribute access (as terminal step)
 * - `text()` — text node selection
 * - `node()` — any node type
 * - `[n]` — positional predicate (1-based)
 * - `[@attr='value']` — attribute equality predicate
 * - `[child]` — child element existence predicate
 *
 * Notable omissions: axes syntax, functions, arithmetic, boolean operators,
 * union (`|`), and parent navigation (`..`).
 *
 * @property locations The parsed location steps.
 */
@JvmInline
value class XPath private constructor(val locations: List<XPathLocation>) {

    /**
     * Evaluates this path against the given [root] element, returning all matching nodes.
     */
    fun query(root: XmlElement): List<XmlNode> {
        return evaluate(listOf(root), locations)
    }

    /**
     * Evaluates this path against the given [root] element, returning the single matching
     * node or `null` if zero or multiple nodes match.
     */
    fun queryOne(root: XmlElement): XmlNode? {
        val results = query(root)
        return results.singleOrNull()
    }

    companion object {
        /**
         * Parses an XPath expression string.
         *
         * @throws IllegalArgumentException if the expression is malformed.
         */
        fun parse(expression: String): XPath {
            require(expression.isNotEmpty()) { "XPath expression must not be empty" }
            val locations = mutableListOf<XPathLocation>()
            val tokens = tokenize(expression)
            var i = 0

            while (i < tokens.size) {
                val token = tokens[i]
                when {
                    token == "//" -> {
                        locations.add(XPathLocation(XPathStep.RecursiveDescent))
                        i++
                    }
                    token == "/" -> {
                        i++
                    }
                    token.startsWith("@") -> {
                        locations.add(XPathLocation(XPathStep.Attribute(token.substring(1))))
                        i++
                    }
                    token == "*" -> {
                        val predicates = mutableListOf<XPathPredicate>()
                        i++
                        while (i < tokens.size && tokens[i] == "[") {
                            i++
                            val (pred, newI) = parsePredicate(tokens, i)
                            predicates.add(pred)
                            i = newI
                        }
                        locations.add(XPathLocation(XPathStep.Wildcard, predicates))
                    }
                    token == "text()" -> {
                        locations.add(XPathLocation(XPathStep.TextNodes))
                        i++
                    }
                    token == "node()" -> {
                        locations.add(XPathLocation(XPathStep.AllNodes))
                        i++
                    }
                    else -> {
                        val step = if (':' in token) {
                            val parts = token.split(':', limit = 2)
                            XPathStep.QualifiedChild(parts[0], parts[1])
                        } else {
                            XPathStep.Child(token)
                        }
                        val predicates = mutableListOf<XPathPredicate>()
                        i++
                        while (i < tokens.size && tokens[i] == "[") {
                            i++
                            val (pred, newI) = parsePredicate(tokens, i)
                            predicates.add(pred)
                            i = newI
                        }
                        locations.add(XPathLocation(step, predicates))
                    }
                }
            }

            return XPath(locations)
        }

        private fun tokenize(expression: String): List<String> {
            val tokens = mutableListOf<String>()
            var i = 0

            while (i < expression.length) {
                when {
                    expression.startsWith("//", i) -> {
                        tokens.add("//")
                        i += 2
                    }
                    expression[i] == '/' -> {
                        tokens.add("/")
                        i++
                    }
                    expression[i] == '[' -> {
                        tokens.add("[")
                        i++
                        val start = i
                        var depth = 1
                        while (i < expression.length && depth > 0) {
                            if (expression[i] == '[') depth++
                            if (expression[i] == ']') depth--
                            i++
                        }
                        tokens.add(expression.substring(start, i - 1))
                        tokens.add("]")
                    }
                    expression[i] == '@' -> {
                        i++
                        val start = i
                        while (i < expression.length && expression[i] != '/' && expression[i] != '[') i++
                        tokens.add("@${expression.substring(start, i)}")
                    }
                    else -> {
                        val start = i
                        while (i < expression.length && expression[i] != '/' && expression[i] != '[') i++
                        val token = expression.substring(start, i)
                        if (token.isNotEmpty()) tokens.add(token)
                    }
                }
            }

            return tokens
        }

        private fun parsePredicate(tokens: List<String>, startIndex: Int): Pair<XPathPredicate, Int> {
            require(startIndex < tokens.size) { "Unexpected end of predicate" }
            val content = tokens[startIndex]
            val endIndex = startIndex + 1
            require(endIndex < tokens.size && tokens[endIndex] == "]") { "Expected ']' closing predicate" }

            val predicate = when {
                content.startsWith("@") && '=' in content -> {
                    val attrExpr = content.substring(1)
                    val eqIndex = attrExpr.indexOf('=')
                    val attrName = attrExpr.substring(0, eqIndex)
                    val attrValue = attrExpr.substring(eqIndex + 1).removeSurrounding("'").removeSurrounding("\"")
                    XPathPredicate.AttributeEquals(attrName, attrValue)
                }
                content.toIntOrNull() != null -> {
                    XPathPredicate.Position(content.toInt())
                }
                else -> {
                    XPathPredicate.ChildExists(content)
                }
            }
            return predicate to (endIndex + 1)
        }
    }
}

private fun evaluate(nodes: List<XmlNode>, locations: List<XPathLocation>): List<XmlNode> {
    if (locations.isEmpty()) return nodes

    val location = locations.first()
    val rest = locations.subList(1, locations.size)

    val selected = when (location.step) {
        is XPathStep.Child -> nodes.flatMap { node ->
            when (node) {
                is XmlElement -> node.children.filterIsInstance<XmlElement>()
                    .filter { it.name.localPart == location.step.name }
                else -> emptyList()
            }
        }
        is XPathStep.QualifiedChild -> nodes.flatMap { node ->
            when (node) {
                is XmlElement -> node.children.filterIsInstance<XmlElement>()
                    .filter { it.name.prefix == location.step.prefix && it.name.localPart == location.step.localName }
                else -> emptyList()
            }
        }
        is XPathStep.Wildcard -> nodes.flatMap { node ->
            when (node) {
                is XmlElement -> node.children.filterIsInstance<XmlElement>()
                else -> emptyList()
            }
        }
        is XPathStep.TextNodes -> nodes.flatMap { node ->
            when (node) {
                is XmlElement -> node.children.filterIsInstance<XmlText>()
                else -> emptyList()
            }
        }
        is XPathStep.AllNodes -> nodes.flatMap { node ->
            when (node) {
                is XmlElement -> node.children
                else -> emptyList()
            }
        }
        is XPathStep.Attribute -> nodes.mapNotNull { node ->
            when (node) {
                is XmlElement -> {
                    val value = node.attr(location.step.name)
                    value?.let { XmlAttribute(QName(location.step.name), it) }
                }
                else -> null
            }
        }
        is XPathStep.RecursiveDescent -> nodes.flatMap { node ->
            collectAllElements(node)
        }
    }

    val filtered = applyPredicates(selected, location.predicates)
    return evaluate(filtered, rest)
}

private fun applyPredicates(nodes: List<XmlNode>, predicates: List<XPathPredicate>): List<XmlNode> {
    var result = nodes
    for (predicate in predicates) {
        result = when (predicate) {
            is XPathPredicate.Position -> {
                val index = predicate.index - 1
                if (index in result.indices) listOf(result[index]) else emptyList()
            }
            is XPathPredicate.AttributeEquals -> result.filter { node ->
                node is XmlElement && node.attr(predicate.name) == predicate.value
            }
            is XPathPredicate.ChildExists -> result.filter { node ->
                node is XmlElement && node.element(predicate.name) != null
            }
        }
    }
    return result
}

private fun collectAllElements(node: XmlNode): List<XmlNode> = buildList {
    add(node)
    if (node is XmlElement) {
        node.children.forEach { addAll(collectAllElements(it)) }
    }
}
