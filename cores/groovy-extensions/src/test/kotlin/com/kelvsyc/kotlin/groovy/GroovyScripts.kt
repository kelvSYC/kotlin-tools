package com.kelvsyc.kotlin.groovy

import groovy.lang.Binding
import groovy.lang.GroovyShell

/**
 * Evaluates [script] in a fresh [GroovyShell] whose binding holds [bindings].
 */
fun evalGroovy(script: String, vararg bindings: Pair<String, Any?>): Any? {
    val binding = Binding()
    bindings.forEach { (name, value) -> binding.setVariable(name, value) }
    return GroovyShell(binding).evaluate(script)
}
