@file:Suppress("UNUSED_PARAMETER")

package com.kelvsyc.kotlin.lodash

@JsModule("lodash")
@JsNonModule
internal external object Lodash {
    // Function utilities
    fun debounce(func: dynamic, wait: Int, options: dynamic = definedExternally): dynamic
    fun throttle(func: dynamic, wait: Int, options: dynamic = definedExternally): dynamic
    fun memoize(func: dynamic, resolver: dynamic = definedExternally): dynamic
    fun once(func: dynamic): dynamic

    // String utilities
    fun camelCase(string: String): String
    fun kebabCase(string: String): String
    fun snakeCase(string: String): String
    fun truncate(string: String, options: dynamic = definedExternally): String
    fun escape(string: String): String
    fun unescape(string: String): String
}
