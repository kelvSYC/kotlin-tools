package com.kelvsyc.kotlin.xml

import javax.xml.namespace.QName

/**
 * A node in a parsed XML tree.
 *
 * This sealed hierarchy represents the structural components of an XML document,
 * providing a Kotlin-idiomatic replacement for Groovy's `XmlSlurper` with typed
 * accessors and XPath-based navigation.
 *
 * [XmlElement], [XmlText], [XmlComment], and [XmlProcessingInstruction] appear in
 * the tree structure. [XmlAttribute] is a projection node that only appears in
 * XPath query results when the `@attr` syntax is used.
 */
sealed class XmlNode {
    /**
     * The XPath string-value of this node.
     *
     * For elements, this is the concatenation of all descendant text content.
     * For text nodes, this is the text content itself.
     * For attributes, this is the attribute value.
     * For comments, this is the comment text.
     * For processing instructions, this is the data portion.
     */
    abstract val stringValue: String
}

/**
 * An XML element node with a qualified name, attributes, and ordered children.
 *
 * Attributes are stored as a map and are not part of [children]. They are accessible
 * via [attr] methods directly, or via `@attr` syntax in XPath queries (which projects
 * them as [XmlAttribute] nodes in the result set).
 *
 * @property name The namespace-qualified name of this element.
 * @property attributes The element's attributes, keyed by qualified name.
 * @property children The ordered child nodes (elements, text, comments, processing instructions).
 */
data class XmlElement(
    val name: QName,
    val attributes: Map<QName, String>,
    val children: List<XmlNode>,
) : XmlNode() {

    override val stringValue: String
        get() = buildString { appendTextContent(this@XmlElement, this) }

    /**
     * Returns the value of the attribute with the given local name (namespace-unaware),
     * or `null` if no such attribute exists.
     */
    fun attr(localName: String): String? =
        attributes.entries.firstOrNull { it.key.localPart == localName }?.value

    /**
     * Returns the value of the attribute with the given qualified name,
     * or `null` if no such attribute exists.
     */
    fun attr(qname: QName): String? = attributes[qname]

    /**
     * Returns all direct child elements, skipping text, comment, and PI nodes.
     */
    fun elements(): List<XmlElement> = children.filterIsInstance<XmlElement>()

    /**
     * Returns all direct child elements with the given local name (namespace-unaware).
     */
    fun elements(localName: String): List<XmlElement> =
        children.filterIsInstance<XmlElement>().filter { it.name.localPart == localName }

    /**
     * Returns all direct child elements with the given qualified name.
     */
    fun elements(qname: QName): List<XmlElement> =
        children.filterIsInstance<XmlElement>().filter { it.name == qname }

    /**
     * Returns the first direct child element with the given local name, or `null`.
     */
    fun element(localName: String): XmlElement? =
        children.filterIsInstance<XmlElement>().firstOrNull { it.name.localPart == localName }

    /**
     * Returns the first direct child element with the given qualified name, or `null`.
     */
    fun element(qname: QName): XmlElement? =
        children.filterIsInstance<XmlElement>().firstOrNull { it.name == qname }
}

/**
 * An XML text node (includes both regular text and CDATA sections, which are merged).
 *
 * @property content The text content.
 */
data class XmlText(val content: String) : XmlNode() {
    override val stringValue: String get() = content
}

/**
 * An XML comment node.
 *
 * @property content The comment text (without `<!--` and `-->` delimiters).
 */
data class XmlComment(val content: String) : XmlNode() {
    override val stringValue: String get() = content
}

/**
 * An XML processing instruction node.
 *
 * @property target The PI target (e.g., `xml-stylesheet`).
 * @property data The PI data content.
 */
data class XmlProcessingInstruction(val target: String, val data: String) : XmlNode() {
    override val stringValue: String get() = data
}

/**
 * An XML attribute, projected as a node in XPath query results.
 *
 * This type never appears in [XmlElement.children]. It is created by the XPath query
 * engine when a path expression terminates with `@attr`, allowing attribute values to
 * be returned uniformly as [XmlNode] instances.
 *
 * @property name The namespace-qualified attribute name.
 * @property value The attribute value.
 */
data class XmlAttribute(val name: QName, val value: String) : XmlNode() {
    override val stringValue: String get() = value
}

private fun appendTextContent(node: XmlNode, builder: StringBuilder) {
    when (node) {
        is XmlElement -> node.children.forEach { appendTextContent(it, builder) }
        is XmlText -> builder.append(node.content)
        is XmlComment, is XmlProcessingInstruction, is XmlAttribute -> {}
    }
}
