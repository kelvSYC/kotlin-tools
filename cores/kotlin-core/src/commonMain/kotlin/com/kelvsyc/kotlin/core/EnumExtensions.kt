package com.kelvsyc.kotlin.core

/**
 * Returns `true` if there is an enum constant of [E] with the specified name.
 */
inline fun <reified E : Enum<E>> isValidEnum(value: String, ignoreCase: Boolean = false): Boolean =
    enumValues<E>().any { it.name.equals(value, ignoreCase = ignoreCase) }

/**
 * Returns the enum constant of [E] with the specified name, or `null` if no such constant exists.
 */
inline fun <reified E : Enum<E>> enumValueOfOrNull(value: String, ignoreCase: Boolean = false): E? =
    enumValues<E>().firstOrNull { it.name.equals(value, ignoreCase = ignoreCase) }

/**
 * Returns the enum constant of [E] with the specified name, or [defaultValue] if no such constant exists.
 */
inline fun <reified E : Enum<E>> enumValueOfOrDefault(value: String, ignoreCase: Boolean = false, defaultValue: E): E =
    enumValueOfOrNull<E>(value, ignoreCase) ?: defaultValue
