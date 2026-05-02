package com.kelvsyc.kotlin.commons.lang.reflect

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.apache.commons.lang3.reflect.TypeLiteral
import java.lang.reflect.ParameterizedType
import java.lang.reflect.WildcardType

class TypeExtensionsTest : FunSpec({

    context("typeLiteralOf") {
        test("returns a TypeLiteral instance") {
            typeLiteralOf<String>().shouldBeInstanceOf<TypeLiteral<*>>()
        }
        test("captures the correct raw type for a simple type") {
            typeLiteralOf<String>().type shouldBe String::class.java
        }
        test("captures the correct raw type for a generic type") {
            val literal = typeLiteralOf<List<String>>()
            literal.type.shouldBeInstanceOf<ParameterizedType>()
            (literal.type as ParameterizedType).rawType shouldBe List::class.java
        }
    }

    context("buildWildcardType") {
        test("unbounded wildcard produces a WildcardType with no explicit bounds") {
            val wildcard = buildWildcardType {}
            wildcard.shouldBeInstanceOf<WildcardType>()
            wildcard.lowerBounds.isEmpty() shouldBe true
        }
        test("upper-bounded wildcard captures the declared bound") {
            val wildcard = buildWildcardType {
                withUpperBounds(Number::class.java)
            }
            wildcard.upperBounds.single() shouldBe Number::class.java
        }
        test("lower-bounded wildcard captures the declared bound") {
            val wildcard = buildWildcardType {
                withLowerBounds(Int::class.java)
            }
            wildcard.lowerBounds.single() shouldBe Int::class.java
        }
    }

    context("parameterizedTypeOf(KClass)") {
        test("creates a ParameterizedType with the correct raw type") {
            val type = parameterizedTypeOf(List::class, String::class.java)
            type.rawType shouldBe List::class.java
        }
        test("captures the type argument") {
            val type = parameterizedTypeOf(List::class, String::class.java)
            type.actualTypeArguments.single() shouldBe String::class.java
        }
        test("supports multiple type arguments") {
            val type = parameterizedTypeOf(Map::class, String::class.java, Int::class.java)
            type.actualTypeArguments[0] shouldBe String::class.java
            type.actualTypeArguments[1] shouldBe Int::class.java
        }
    }

    context("parameterizedTypeOf(reified)") {
        test("creates a ParameterizedType with the correct raw type") {
            val type = parameterizedTypeOf<List<*>>(String::class.java)
            type.rawType shouldBe List::class.java
        }
        test("captures the type argument") {
            val type = parameterizedTypeOf<List<*>>(String::class.java)
            type.actualTypeArguments.single() shouldBe String::class.java
        }
    }
})
