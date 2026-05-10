package com.kelvsyc.kotlin.xml

/**
 * Evaluates an [XPath] expression string against this element, returning all matching nodes.
 */
fun XmlElement.query(path: String): List<XmlNode> = XPath.parse(path).query(this)

/**
 * Evaluates an [XPath] expression string against this element, returning the single matching
 * node or `null` if zero or multiple nodes match.
 */
fun XmlElement.queryOne(path: String): XmlNode? = XPath.parse(path).queryOne(this)

/**
 * Evaluates an [XPath] expression and returns the string values of all matching nodes.
 */
fun XmlElement.queryStrings(path: String): List<String> = query(path).map { it.stringValue }

/**
 * Evaluates an [XPath] expression and returns the string value of the single matching node,
 * or `null` if zero or multiple nodes match.
 */
fun XmlElement.queryString(path: String): String? = queryOne(path)?.stringValue
