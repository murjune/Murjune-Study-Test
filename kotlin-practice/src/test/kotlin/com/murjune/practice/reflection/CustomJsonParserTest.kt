package com.murjune.practice.reflection

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.test.Test

class CustomJsonParserTest {

    data class Person(val name: String, val age: Int)

    fun serialize(obj: Any): String = buildString {
        append("{")
        obj::class.memberProperties.reversed().forEach { prop ->
            append("\"${prop.name}\": \"${prop.getter.call(obj)}\",")
        }
        deleteCharAt(length - 1)
        append("}")
    }

    inline fun <reified T : Any> deserialize(json: String): T {
        val clean: (String) -> String = { it.trim().trim('"') }
        val jsonMap = json
            .drop(1) // { 제거
            .dropLast(1) // } 제거
            .split(",") // ,로 분리 ["name": "Alice", "age": "29"]
            .associate {
                val (key, value) = it.split(":") // "name" to "Alice"
                clean(key) to clean(value)
            }
        val clazz = T::class
        val primaryConstructor = clazz.primaryConstructor
        primaryConstructor.shouldNotBeNull()
        println(clazz.declaredMemberProperties.map { it.returnType })
        return primaryConstructor.call(*clazz.declaredMemberProperties.map {
            when (it.returnType.classifier) {
                String::class -> jsonMap[it.name]!!
                Int::class -> jsonMap[it.name]!!.toInt()
                else -> throw IllegalArgumentException()
            }
        }.toTypedArray())
    }

    @Test
    fun `serialize 함수는 객체를 JSON 문자열로 변환한다`() {
        val person = Person("Odoong", 28)
        // when
        val actual = serialize(person)
        // then
        actual shouldBe """{"name": "Odoong","age": "28"}"""
    }

    @Test
    fun `deserialize 함수는 JSON 문자열을 객체로 변환한다`() {
        val json = """{"name": "Odoong","age": "28"}"""
        // when
        val actual = deserialize<Person>(json)
        // then
        actual shouldBe Person("Odoong", 29)
    }
}