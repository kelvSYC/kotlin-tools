package com.kelvsyc.kotlin.snakeyaml

import org.snakeyaml.engine.v2.api.LoadSettings
import org.snakeyaml.engine.v2.api.lowlevel.Compose
import org.snakeyaml.engine.v2.nodes.MappingNode
import org.snakeyaml.engine.v2.nodes.Node
import org.snakeyaml.engine.v2.nodes.ScalarNode
import org.snakeyaml.engine.v2.nodes.SequenceNode
import org.snakeyaml.engine.v2.nodes.Tag
import java.io.InputStream
import java.io.Reader
import kotlin.jvm.optionals.getOrNull

private val defaultSettings: LoadSettings = LoadSettings.builder().build()
private val compose: Compose = Compose(defaultSettings)

/**
 * Parses this string as YAML and returns the root [YamlValue], or `null` if the
 * document is empty.
 */
fun String.parseYaml(): YamlValue? {
    val node = compose.composeString(this).getOrNull() ?: return null
    return convertNode(node)
}

/**
 * Parses YAML from this [Reader] and returns the root [YamlValue], or `null` if the
 * document is empty.
 */
fun Reader.parseYaml(): YamlValue? {
    val node = compose.composeReader(this).getOrNull() ?: return null
    return convertNode(node)
}

/**
 * Parses YAML from this [InputStream] and returns the root [YamlValue], or `null` if the
 * document is empty.
 */
fun InputStream.parseYaml(): YamlValue? {
    val node = compose.composeInputStream(this).getOrNull() ?: return null
    return convertNode(node)
}

private fun convertNode(node: Node): YamlValue {
    return when (node) {
        is MappingNode -> convertMapping(node)
        is SequenceNode -> convertSequence(node)
        is ScalarNode -> convertScalar(node)
        else -> YamlNull
    }
}

private fun convertMapping(node: MappingNode): YamlMapping {
    val entries = linkedMapOf<String, YamlValue>()
    for (tuple in node.value) {
        val key = when (val keyNode = tuple.keyNode) {
            is ScalarNode -> keyNode.value
            else -> keyNode.toString()
        }
        entries[key] = convertNode(tuple.valueNode)
    }
    return YamlMapping(entries)
}

private fun convertSequence(node: SequenceNode): YamlSequence {
    val elements = node.value.map { convertNode(it) }
    return YamlSequence(elements)
}

private fun convertScalar(node: ScalarNode): YamlValue {
    if (node.tag == Tag.NULL || node.value == "null" || node.value == "~" || node.value.isEmpty()) {
        return YamlNull
    }
    return YamlScalar(node.value)
}
