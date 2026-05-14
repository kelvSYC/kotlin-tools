package com.kelvsyc.kotlin.snakeyaml

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beTheSameInstanceAs
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

private fun <T : Serializable> roundTrip(value: T): T {
    val bytes = ByteArrayOutputStream().also { ObjectOutputStream(it).use { oos -> oos.writeObject(value) } }.toByteArray()
    @Suppress("UNCHECKED_CAST")
    return ObjectInputStream(ByteArrayInputStream(bytes)).use { it.readObject() as T }
}

class YamlValueSerializationTest : FunSpec({

    test("YamlScalar round-trips") {
        val value = YamlScalar("hello")
        roundTrip(value) shouldBe value
    }

    test("YamlNull round-trips and preserves singleton") {
        val result = roundTrip(YamlNull)
        result shouldBe YamlNull
        result should beTheSameInstanceAs(YamlNull)
    }

    test("YamlSequence round-trips") {
        val value = YamlSequence(listOf(YamlScalar("a"), YamlNull))
        roundTrip(value) shouldBe value
    }

    test("YamlMapping round-trips") {
        val value = YamlMapping(mapOf("key" to YamlScalar("value"), "empty" to YamlNull))
        roundTrip(value) shouldBe value
    }
})
