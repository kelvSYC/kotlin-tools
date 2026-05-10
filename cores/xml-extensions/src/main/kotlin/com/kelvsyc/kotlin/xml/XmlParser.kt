package com.kelvsyc.kotlin.xml

import java.io.InputStream
import java.io.Reader
import java.io.StringReader
import javax.xml.namespace.QName
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants
import javax.xml.stream.XMLStreamReader

/**
 * Parses this string as XML and returns the root [XmlElement].
 *
 * The parser is non-validating, does not resolve external entities, and does not
 * process DTDs — matching XmlSlurper's default behavior and preventing XXE attacks.
 *
 * @throws javax.xml.stream.XMLStreamException if the XML is malformed.
 */
fun String.parseXml(): XmlElement = StringReader(this).parseXml()

/**
 * Parses XML from this [Reader] and returns the root [XmlElement].
 *
 * @throws javax.xml.stream.XMLStreamException if the XML is malformed.
 */
fun Reader.parseXml(): XmlElement {
    val factory = createSecureFactory()
    val reader = factory.createXMLStreamReader(this)
    return reader.use { parseDocument(it) }
}

/**
 * Parses XML from this [InputStream] and returns the root [XmlElement].
 *
 * @throws javax.xml.stream.XMLStreamException if the XML is malformed.
 */
fun InputStream.parseXml(): XmlElement {
    val factory = createSecureFactory()
    val reader = factory.createXMLStreamReader(this)
    return reader.use { parseDocument(it) }
}

private fun createSecureFactory(): XMLInputFactory = XMLInputFactory.newInstance().apply {
    setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false)
    setProperty(XMLInputFactory.SUPPORT_DTD, false)
    setProperty(XMLInputFactory.IS_COALESCING, true)
}

private fun parseDocument(reader: XMLStreamReader): XmlElement {
    while (reader.hasNext()) {
        when (reader.next()) {
            XMLStreamConstants.START_ELEMENT -> return parseElement(reader)
        }
    }
    throw IllegalStateException("No root element found")
}

private fun parseElement(reader: XMLStreamReader): XmlElement {
    val name = reader.name.toQName()

    val attributes = buildMap<QName, String> {
        for (i in 0 until reader.attributeCount) {
            put(reader.getAttributeName(i).toQName(), reader.getAttributeValue(i))
        }
    }

    val children = mutableListOf<XmlNode>()

    while (reader.hasNext()) {
        when (reader.next()) {
            XMLStreamConstants.START_ELEMENT -> children.add(parseElement(reader))
            XMLStreamConstants.CHARACTERS, XMLStreamConstants.CDATA -> {
                val text = reader.text
                if (text.isNotBlank() || children.any { it is XmlText }) {
                    val last = children.lastOrNull()
                    if (last is XmlText) {
                        children[children.lastIndex] = XmlText(last.content + text)
                    } else {
                        children.add(XmlText(text))
                    }
                }
            }
            XMLStreamConstants.COMMENT -> children.add(XmlComment(reader.text))
            XMLStreamConstants.PROCESSING_INSTRUCTION -> {
                children.add(XmlProcessingInstruction(reader.piTarget, reader.piData ?: ""))
            }
            XMLStreamConstants.END_ELEMENT -> break
        }
    }

    return XmlElement(name, attributes, children)
}

private fun javax.xml.namespace.QName.toQName(): QName = this

private fun XMLStreamReader.use(block: (XMLStreamReader) -> XmlElement): XmlElement {
    try {
        return block(this)
    } finally {
        close()
    }
}
