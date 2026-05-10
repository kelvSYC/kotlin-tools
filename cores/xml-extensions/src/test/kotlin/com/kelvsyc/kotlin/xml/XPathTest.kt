package com.kelvsyc.kotlin.xml

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class XPathTest : FunSpec({
    val pomXml = """
        <project>
            <groupId>com.example</groupId>
            <artifactId>my-app</artifactId>
            <version>1.0.0</version>
            <dependencies>
                <dependency>
                    <groupId>org.lib</groupId>
                    <artifactId>core</artifactId>
                    <scope>compile</scope>
                </dependency>
                <dependency>
                    <groupId>org.test</groupId>
                    <artifactId>runner</artifactId>
                    <scope>test</scope>
                </dependency>
            </dependencies>
        </project>
    """.trimIndent().parseXml()

    test("child navigation") {
        val result = pomXml.query("groupId")
        result shouldHaveSize 1
        result[0].shouldBeInstanceOf<XmlElement>().stringValue shouldBe "com.example"
    }

    test("multi-step child navigation") {
        val result = pomXml.query("dependencies/dependency")
        result shouldHaveSize 2
    }

    test("descendant search with //") {
        val result = pomXml.query("//groupId")
        result shouldHaveSize 3
    }

    test("wildcard selects all child elements") {
        val result = pomXml.query("dependencies/*")
        result shouldHaveSize 2
    }

    test("positional predicate (1-based)") {
        val result = pomXml.query("dependencies/dependency[1]")
        result shouldHaveSize 1
        val dep = result[0].shouldBeInstanceOf<XmlElement>()
        dep.element("groupId").shouldNotBeNull().stringValue shouldBe "org.lib"
    }

    test("positional predicate second element") {
        val result = pomXml.query("dependencies/dependency[2]")
        result shouldHaveSize 1
        val dep = result[0].shouldBeInstanceOf<XmlElement>()
        dep.element("groupId").shouldNotBeNull().stringValue shouldBe "org.test"
    }

    test("child existence predicate") {
        val xml = """
            <root>
                <item><sub/></item>
                <item/>
                <item><sub/></item>
            </root>
        """.trimIndent().parseXml()

        val result = xml.query("item[sub]")
        result shouldHaveSize 2
    }

    test("attribute equality predicate") {
        val xml = """
            <items>
                <item type="a" value="1"/>
                <item type="b" value="2"/>
                <item type="a" value="3"/>
            </items>
        """.trimIndent().parseXml()

        val result = xml.query("item[@type='a']")
        result shouldHaveSize 2
    }

    test("attribute access as terminal step") {
        val xml = """<item id="42" name="widget"/>""".parseXml()

        val result = xml.query("@id")
        result shouldHaveSize 1
        val attr = result[0].shouldBeInstanceOf<XmlAttribute>()
        attr.name.localPart shouldBe "id"
        attr.value shouldBe "42"
    }

    test("attribute access on nested element") {
        val xml = """
            <root>
                <item id="1"/>
                <item id="2"/>
            </root>
        """.trimIndent().parseXml()

        val result = xml.query("item/@id")
        result shouldHaveSize 2
        result.map { it.stringValue } shouldBe listOf("1", "2")
    }

    test("text() node test") {
        val xml = "<p>Hello <b>world</b> goodbye</p>".parseXml()

        val result = xml.query("text()")
        result shouldHaveSize 2
        result[0].stringValue shouldBe "Hello "
        result[1].stringValue shouldBe " goodbye"
    }

    test("node() selects all children") {
        val xml = "<root>text<!-- comment --><child/></root>".parseXml()

        val result = xml.query("node()")
        result shouldHaveSize 3
    }

    test("queryOne returns single match") {
        pomXml.queryOne("version").shouldNotBeNull().stringValue shouldBe "1.0.0"
    }

    test("queryOne returns null for multiple matches") {
        pomXml.queryOne("//groupId").shouldBeNull()
    }

    test("queryOne returns null for no matches") {
        pomXml.queryOne("nonexistent").shouldBeNull()
    }

    test("queryStrings convenience") {
        val result = pomXml.queryStrings("dependencies/dependency/scope")
        result shouldBe listOf("compile", "test")
    }

    test("queryString single value") {
        pomXml.queryString("artifactId") shouldBe "my-app"
    }

    test("descendant with predicate") {
        val xml = """
            <root>
                <a><b type="x"/></a>
                <c><b type="y"/></c>
            </root>
        """.trimIndent().parseXml()

        val result = xml.query("//b[@type='x']")
        result shouldHaveSize 1
    }

    test("empty result for non-matching path") {
        pomXml.query("nonexistent/path") shouldHaveSize 0
    }

    test("positional predicate out of bounds") {
        pomXml.query("dependencies/dependency[99]") shouldHaveSize 0
    }
})
