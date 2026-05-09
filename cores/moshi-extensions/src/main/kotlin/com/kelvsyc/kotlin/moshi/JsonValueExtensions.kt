package com.kelvsyc.kotlin.moshi

/**
 * Returns the string value at the given path segments, or `null` if absent or not a string.
 */
fun JsonValue.stringAt(vararg segments: String): String? = at(*segments)?.asString()

/**
 * Returns the numeric value at the given path segments, or `null` if absent or not a number.
 */
fun JsonValue.numberAt(vararg segments: String): Number? = at(*segments)?.asNumber()

/**
 * Returns the integer value at the given path segments, or `null` if absent or not a number.
 */
fun JsonValue.intAt(vararg segments: String): Int? = at(*segments)?.asNumber()?.toInt()

/**
 * Returns the long value at the given path segments, or `null` if absent or not a number.
 */
fun JsonValue.longAt(vararg segments: String): Long? = at(*segments)?.asNumber()?.toLong()

/**
 * Returns the double value at the given path segments, or `null` if absent or not a number.
 */
fun JsonValue.doubleAt(vararg segments: String): Double? = at(*segments)?.asNumber()?.toDouble()

/**
 * Returns the boolean value at the given path segments, or `null` if absent or not a boolean.
 */
fun JsonValue.booleanAt(vararg segments: String): Boolean? = at(*segments)?.asBoolean()

/**
 * Returns the [JsonObject] at the given path segments, or `null` if absent or not an object.
 */
fun JsonValue.objectAt(vararg segments: String): JsonObject? = at(*segments)?.asObject()

/**
 * Returns the [JsonArray] at the given path segments, or `null` if absent or not an array.
 */
fun JsonValue.arrayAt(vararg segments: String): JsonArray? = at(*segments)?.asArray()

/**
 * Returns `true` if the value at the given path segments is [JsonNull] or absent.
 */
fun JsonValue.isNullAt(vararg segments: String): Boolean = at(*segments)?.isNull() ?: true

/**
 * Returns the string values of a [JsonArray], filtering out non-string elements.
 */
fun JsonArray.strings(): List<String> = elements.mapNotNull { it.asString() }

/**
 * Returns the numeric values of a [JsonArray], filtering out non-number elements.
 */
fun JsonArray.numbers(): List<Number> = elements.mapNotNull { it.asNumber() }

/**
 * Evaluates a [JsonPath] expression string against this value, returning all matching nodes.
 */
fun JsonValue.query(path: String): List<JsonValue> = JsonPath.parse(path).query(this)

/**
 * Evaluates a [JsonPath] expression string against this value, returning the single matching
 * node or `null` if zero or multiple nodes match.
 */
fun JsonValue.queryOne(path: String): JsonValue? = JsonPath.parse(path).queryOne(this)
