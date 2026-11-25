package com.kelvsyc.kotlin.groovy

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GStringsTest : FunSpec({
    test("splits text and values") {
        val g = gstring { "a ${value(1)} b ${value("x")} c" }
        g.strings.toList() shouldBe listOf("a ", " b ", " c")
        g.values.toList() shouldBe listOf(1, "x")
        g.toString() shouldBe "a 1 b x c"
    }

    test("no values") {
        val g = gstring { "plain" }
        g.values.size shouldBe 0
        g.strings.toList() shouldBe listOf("plain")
    }

    test("adjacent and edge values") {
        val g = gstring { "${value(1)}${value(2)}" }
        g.strings.toList() shouldBe listOf("", "", "")
        g.values.toList() shouldBe listOf(1, 2)
    }

    test("null value") {
        gstring { "x ${value(null)}" }.values.toList() shouldBe listOf(null)
    }

    test("unused value fails") {
        shouldThrow<IllegalStateException> {
            gstring {
                value(1)
                "no marker"
            }
        }
    }

    test("marker used twice fails") {
        shouldThrow<IllegalStateException> {
            gstring {
                val m = value(1)
                "$m $m"
            }
        }
    }

    test("marker from another builder fails") {
        var leaked = ""
        gstring {
            leaked = value(1)
            leaked
        }
        shouldThrow<IllegalStateException> { gstring { "x $leaked" } }
    }

    test("Groovy treats it as a GString") {
        evalGroovy("g instanceof GString && g.toUpperCase() == 'A 1'", "g" to gstring { "a ${value(1)}" }) shouldBe true
    }

    test("a forged marker with a bad index fails with IllegalStateException") {
        shouldThrow<IllegalStateException> {
            gstring {
                val marker = value(1)
                marker + marker.replace(":0", ":99")
            }
        }
    }

    test("a forged marker with an overlong number fails with IllegalStateException") {
        shouldThrow<IllegalStateException> {
            gstring {
                val marker = value(1)
                marker + marker.replace(":0", ":99999999999999999999")
            }
        }
    }
})
