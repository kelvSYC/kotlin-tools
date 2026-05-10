package com.kelvsyc.kotlin.xml

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldBeEmpty as shouldBeEmptyMap
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import javax.xml.namespace.QName

class XmlParserTest : FunSpec({
    test("parse simple element") {
        val root = "<root/>".parseXml()
        root.name.localPart shouldBe "root"
        root.attributes.shouldBeEmptyMap()
        root.children shouldHaveSize 0
    }

    test("parse element with text content") {
        val root = "<greeting>Hello, World!</greeting>".parseXml()
        root.name.localPart shouldBe "greeting"
        root.children shouldHaveSize 1
        root.children[0].shouldBeInstanceOf<XmlText>().content shouldBe "Hello, World!"
    }

    test("parse element with attributes") {
        val root = """<item id="42" type="widget"/>""".parseXml()
        root.attr("id") shouldBe "42"
        root.attr("type") shouldBe "widget"
    }

    test("parse nested elements") {
        val xml = """
            <project>
                <name>my-project</name>
                <version>1.0.0</version>
            </project>
        """.trimIndent()
        val root = xml.parseXml()

        root.name.localPart shouldBe "project"
        root.elements() shouldHaveSize 2
        root.element("name").shouldNotBeNull().stringValue shouldBe "my-project"
        root.element("version").shouldNotBeNull().stringValue shouldBe "1.0.0"
    }

    test("parse mixed content") {
        val xml = "<p>Hello <b>world</b> and goodbye</p>"
        val root = xml.parseXml()

        root.children shouldHaveSize 3
        root.children[0].shouldBeInstanceOf<XmlText>().content shouldBe "Hello "
        root.children[1].shouldBeInstanceOf<XmlElement>().name.localPart shouldBe "b"
        root.children[2].shouldBeInstanceOf<XmlText>().content shouldBe " and goodbye"
    }

    test("stringValue concatenates all descendant text") {
        val xml = "<p>Hello <b>world</b> and goodbye</p>"
        val root = xml.parseXml()
        root.stringValue shouldBe "Hello world and goodbye"
    }

    test("parse comments") {
        val xml = "<root><!-- a comment --><child/></root>"
        val root = xml.parseXml()

        root.children shouldHaveSize 2
        root.children[0].shouldBeInstanceOf<XmlComment>().content shouldBe " a comment "
        root.children[1].shouldBeInstanceOf<XmlElement>()
    }

    test("parse processing instructions") {
        val xml = "<root><?my-pi some data?><child/></root>"
        val root = xml.parseXml()

        root.children shouldHaveSize 2
        val pi = root.children[0].shouldBeInstanceOf<XmlProcessingInstruction>()
        pi.target shouldBe "my-pi"
        pi.data shouldBe "some data"
    }

    test("parse namespaced elements") {
        val xml = """<ns:root xmlns:ns="http://example.com"><ns:child/></ns:root>"""
        val root = xml.parseXml()

        root.name shouldBe QName("http://example.com", "root", "ns")
        root.elements()[0].name shouldBe QName("http://example.com", "child", "ns")
    }

    test("parse namespaced attributes") {
        val xml = """<root xmlns:x="http://example.com" x:attr="value"/>"""
        val root = xml.parseXml()

        root.attr("attr") shouldBe "value"
        root.attr(QName("http://example.com", "attr", "x")) shouldBe "value"
    }

    test("elements filters by local name") {
        val xml = """
            <dependencies>
                <dependency><groupId>com.example</groupId></dependency>
                <dependency><groupId>org.other</groupId></dependency>
                <plugin><groupId>com.plugin</groupId></plugin>
            </dependencies>
        """.trimIndent()
        val root = xml.parseXml()

        root.elements("dependency") shouldHaveSize 2
        root.elements("plugin") shouldHaveSize 1
    }

    test("element returns first match") {
        val xml = """
            <root>
                <item>first</item>
                <item>second</item>
            </root>
        """.trimIndent()
        val root = xml.parseXml()

        root.element("item").shouldNotBeNull().stringValue shouldBe "first"
    }

    test("element returns null for missing") {
        val root = "<root><child/></root>".parseXml()
        root.element("missing").shouldBeNull()
    }
})
