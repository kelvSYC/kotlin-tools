package com.kelvsyc.kotlin.snakeyaml

/**
 * Returns the string value at the given path segments, or `null` if absent or not a scalar.
 */
fun YamlValue.stringAt(vararg segments: String): String? = at(*segments)?.asScalar()?.asString()

/**
 * Returns the integer value at the given path segments, or `null` if absent or not parseable
 * as an integer.
 */
fun YamlValue.intAt(vararg segments: String): Int? = at(*segments)?.asScalar()?.asInt()

/**
 * Returns the long value at the given path segments, or `null` if absent or not parseable
 * as a long.
 */
fun YamlValue.longAt(vararg segments: String): Long? = at(*segments)?.asScalar()?.asLong()

/**
 * Returns the double value at the given path segments, or `null` if absent or not parseable
 * as a double.
 */
fun YamlValue.doubleAt(vararg segments: String): Double? = at(*segments)?.asScalar()?.asDouble()

/**
 * Returns the boolean value at the given path segments, or `null` if absent or not a
 * recognized YAML boolean.
 */
fun YamlValue.booleanAt(vararg segments: String): Boolean? = at(*segments)?.asScalar()?.asBoolean()

/**
 * Returns the [YamlMapping] at the given path segments, or `null` if absent or not a mapping.
 */
fun YamlValue.mappingAt(vararg segments: String): YamlMapping? = at(*segments)?.asMapping()

/**
 * Returns the [YamlSequence] at the given path segments, or `null` if absent or not a sequence.
 */
fun YamlValue.sequenceAt(vararg segments: String): YamlSequence? = at(*segments)?.asSequence()

/**
 * Returns `true` if the value at the given path segments is [YamlNull] or absent.
 */
fun YamlValue.isNullAt(vararg segments: String): Boolean = at(*segments)?.isNull() ?: true

/**
 * Returns the string values of a [YamlSequence], filtering out non-scalar elements.
 */
fun YamlSequence.strings(): List<String> = mapNotNull { it.asScalar()?.asString() }
