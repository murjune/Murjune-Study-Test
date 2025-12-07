package com.murjune.practice.serialization

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.assertThrows

/**
 * Json 옵션 설정 학습 테스트
 *
 * ## Json 주요 옵션:
 * 1. **ignoreUnknownKeys**
 *    - true: JSON에 정의되지 않은 필드가 있어도 무시
 *    - false (기본값): unknown key 있으면 예외 발생
 *
 * 2. **coerceInputValues**
 *    - true: null이나 잘못된 값이 들어오면 default argument로 대체
 *      - null → default 값
 *      - unknown enum 값 → default 값
 *    - false (기본값): null이나 잘못된 값 있으면 예외 발생
 */
class JsonOptionsTest : FunSpec(body = {

    context("IgnoreUnknownKeys = true 설정 - 모르는 필드 무시") {
        @Serializable
        data class User(
            val id: Int,
            val name: String,
            val email: String,
        )

        test("JSON에 없는 필드가 오면 예외 발생") {
            val jsonString = """{"id":1,"name":"june","email":"1","age":30}"""
            assertThrows<RuntimeException> {
                Json.decodeFromString<User>(jsonString)
            }
            val json = Json {
                ignoreUnknownKeys = true // 모르는 필드 무시
            }

            val result = json.decodeFromString<User>(jsonString)
            result.id shouldBe 1
            result.name shouldBe "june"
            result.email shouldBe "1"
        }
    }
    context(
        "coerceInputValues = true 설정 " +
            "1. null 이 들어오면 default argument 로 대체" +
            "2. Enum 에 unKnown 값이 들어오면 기본값으로 대체"
    ) {
        @Serializable
        data class User(
            val id: Int,
            val name: String,
            val email: String = "2"
        )

        test("null 이 들어오면 default argument 로 대체") {
            val notCoercedJson = Json

            assertThrows<RuntimeException> {
                val jsonString = """{"id":1,"name":"june","email":null}"""
                notCoercedJson.decodeFromString<User>(jsonString)
            }

            val json = Json {
                coerceInputValues = true // null이 들어오면 default argument로 대체
            }
            val jsonString = """{"id":1,"name":"june","email":null}"""
            val result = json.decodeFromString<User>(jsonString)
            result.email shouldBe "2"
        }

        test("Enum 에 unKnown 값이 들어오면 기본값으로 대체") {
            @Serializable
            data class UserWithEnum(
                val id: Int,
                val exampleEnum: ExampleEnum = ExampleEnum.FIRST
            )

            val notCoercedJson = Json

            assertThrows<RuntimeException> {
                val jsonString = """{"id":1,"exampleEnum":"junewon"}"""
                notCoercedJson.decodeFromString<UserWithEnum>(jsonString)
            }

            val json = Json {
                coerceInputValues = true // unknown enum 값이 들어오면 기본값으로 대체
            }
            val jsonString = """{"id":1,"exampleEnum":"junewon"}"""
            val result = json.decodeFromString<UserWithEnum>(jsonString)
            result.exampleEnum shouldBe ExampleEnum.FIRST
        }
    }
}) {
    enum class ExampleEnum {
        FIRST,
        SECOND,
        THIRD
    }
}