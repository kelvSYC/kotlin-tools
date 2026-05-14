package com.kelvsyc.kotlin.xml

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beTheSameInstanceAs
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import javax.xml.namespace.QName

private fun <T : Serializable> roundTrip(value: T): T {
    val bytes = ByteArrayOutputStream().also { ObjectOutputStream(it).use { oos -> oos.writeObject(value) } }.toByteArray()
    @Suppress("UNCHECKED_CAST")
    return ObjectInputStream(ByteArrayInputStream(bytes)).use { it.readObject() as T }
}

class XmlSerializationTest : FunSpec({

    context("XmlNode round-trips") {
        test("XmlText") {
            val value = XmlText("hello")
            roundTrip(value) shouldBe value
        }

        test("XmlComment") {
            val value = XmlComment("a comment")
            roundTrip(value) shouldBe value
        }

        test("XmlProcessingInstruction") {
            val value = XmlProcessingInstruction("xml-stylesheet", "type=\"text/css\"")
            roundTrip(value) shouldBe value
        }

        test("XmlAttribute") {
            val value = XmlAttribute(QName("id"), "main")
            roundTrip(value) shouldBe value
        }

        test("XmlElement with children") {
            val value = XmlElement(
                name = QName("root"),
                attributes = mapOf(QName("id") to "1"),
                children = listOf(XmlText("content"), XmlElement(QName("child"), emptyMap(), emptyList())),
            )
            roundTrip(value) shouldBe value
        }
    }

    context("XPathStep round-trips") {
        test("Child") {
            val value = XPathStep.Child("foo")
            roundTrip(value) shouldBe value
        }

        test("QualifiedChild") {
            val value = XPathStep.QualifiedChild("ns", "foo")
            roundTrip(value) shouldBe value
        }

        test("Attribute") {
            val value = XPathStep.Attribute("id")
            roundTrip(value) shouldBe value
        }

        test("Wildcard preserves singleton") {
            val result = roundTrip(XPathStep.Wildcard)
            result shouldBe XPathStep.Wildcard
            result should beTheSameInstanceAs(XPathStep.Wildcard)
        }

        test("TextNodes preserves singleton") {
            val result = roundTrip(XPathStep.TextNodes)
            result shouldBe XPathStep.TextNodes
            result should beTheSameInstanceAs(XPathStep.TextNodes)
        }

        test("AllNodes preserves singleton") {
            val result = roundTrip(XPathStep.AllNodes)
            result shouldBe XPathStep.AllNodes
            result should beTheSameInstanceAs(XPathStep.AllNodes)
        }

        test("RecursiveDescent preserves singleton") {
            val result = roundTrip(XPathStep.RecursiveDescent)
            result shouldBe XPathStep.RecursiveDescent
            result should beTheSameInstanceAs(XPathStep.RecursiveDescent)
        }
    }

    context("XPathPredicate round-trips") {
        test("Position") {
            val value = XPathPredicate.Position(2)
            roundTrip(value) shouldBe value
        }

        test("AttributeEquals") {
            val value = XPathPredicate.AttributeEquals("id", "main")
            roundTrip(value) shouldBe value
        }

        test("ChildExists") {
            val value = XPathPredicate.ChildExists("item")
            roundTrip(value) shouldBe value
        }
    }

    context("XPathLocation round-trips") {
        test("step only") {
            val value = XPathLocation(XPathStep.Child("foo"))
            roundTrip(value) shouldBe value
        }

        test("step with predicates") {
            val value = XPathLocation(XPathStep.Child("item"), listOf(XPathPredicate.Position(1)))
            roundTrip(value) shouldBe value
        }
    }

    context("XPath round-trips") {
        test("simple path") {
            val value = XPath.parse("root/child")
            roundTrip(value) shouldBe value
        }

        test("path with descendant and predicate") {
            val value = XPath.parse("root//item[@id='1']")
            roundTrip(value) shouldBe value
        }
    }
})
