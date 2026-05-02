package com.kelvsyc.kotlin.guava

import com.google.common.reflect.TypeToken
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.lang.reflect.ParameterizedType
import kotlin.reflect.typeOf

class TypeTokensTest : FunSpec({

    context("KType.toTypeToken()") {
        test("non-nullable primitive maps to Java primitive") {
            typeOf<Int>().toTypeToken().type shouldBe Int::class.javaPrimitiveType
        }
        test("nullable primitive maps to boxed type") {
            typeOf<Int?>().toTypeToken().type shouldBe Int::class.javaObjectType
        }
        test("reference type") {
            typeOf<String>().toTypeToken().type shouldBe String::class.java
        }
        test("nullable reference type is same as non-nullable") {
            typeOf<String?>().toTypeToken().type shouldBe String::class.java
        }
        test("parameterized type") {
            val token = typeOf<List<String>>().toTypeToken()
            token.type.shouldBeInstanceOf<ParameterizedType>()
            token.rawType shouldBe List::class.java
            (token.type as ParameterizedType).actualTypeArguments[0] shouldBe String::class.java
        }
    }

    context("typeTokenOf<T>()") {
        test("non-nullable primitive is coerced to boxed") {
            typeTokenOf<Int>().type shouldBe Int::class.javaObjectType
        }
        test("reference type") {
            typeTokenOf<String>().type shouldBe String::class.java
        }
        test("parameterized type") {
            val token = typeTokenOf<List<String>>()
            token.type.shouldBeInstanceOf<ParameterizedType>()
            token.rawType shouldBe List::class.java
            (token.type as ParameterizedType).actualTypeArguments[0] shouldBe String::class.java
        }
        test("matches anonymous TypeToken idiom for primitives") {
            typeTokenOf<Int>() shouldBe (object : TypeToken<Int>() {})
        }
        // Note: typeTokenOf<List<String>>() produces TypeToken<List<String>> (invariant),
        // while `object : TypeToken<List<String>>() {}` produces TypeToken<List<? extends String>>
        // due to Kotlin's declaration-site variance on List<out E>. These are not equal.
    }
})
