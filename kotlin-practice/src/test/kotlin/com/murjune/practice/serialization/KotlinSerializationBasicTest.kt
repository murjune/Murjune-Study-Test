package com.murjune.practice.serialization

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.assertThrows

/**
 * Kotlin Serialization 기본 동작 학습 테스트
 *
 * ## 학습 내용:
 * 1. **기본 직렬화/역직렬화**
 *    - JSON ↔ Kotlin 객체 변환
 *    - 필수 필드 누락 시 예외 발생
 *    - unknown keys 처리
 *
 * 2. **Default Argument 동작**
 *    - 필드 누락 시 default 값 사용
 *    - null 값이 오면 예외 발생 (coerceInputValues 없이)
 *    - coerceInputValues로 null → default 대체
 *
 * 3. **Nullable 필드 동작**
 *    - nullable 필드는 null 허용
 *    - nullable + default 조합
 *    - 직렬화 시 null 처리
 *
 * 4. **특수 케이스**
 *    - @Required: default argument 있어도 필수 지정
 *    - @Transient: 직렬화 대상에서 제외
 *    - backing property, lazy: 자동 제외
 *    - Generic 타입 직렬화
 */
class KotlinSerializationBasicTest : FunSpec(
    body = {
        context("기본 직렬화/역직렬화") {
            @Serializable
            data class User(
                val id: Int,
                val name: String,
                val email: String,
            )

            test("객체를 JSON 문자열로 직렬화") {
                val user = User(id = 1, name = "John Doe", email = "john@example.com")
                val basicJson = Json
                val jsonString = basicJson.encodeToString(user)

                jsonString shouldBe """{"id":1,"name":"John Doe","email":"john@example.com"}"""
            }

            test("JSON 문자열을 객체로 역직렬화") {
                val jsonString = """{"id":2,"name":"Jane Doe","email":"jane@example.com"}"""

                val user = Json.decodeFromString<User>(jsonString)

                user.id shouldBe 2
                user.name shouldBe "Jane Doe"
                user.email shouldBe "jane@example.com"
            }

            test("요구되는 필드에 해당하는 값이 없으면 예외 발생") {
                val jsonString = """{"id":3,"name":"Alice"}""" // email 필드가 없음

                assertThrows<RuntimeException> {
                    Json.decodeFromString<User>(jsonString)
                }
            }

            test("요구되는 필드에 없는 추가적인 필드가 오면 예외 발생") {
                val jsonString = """{"id":4,"name":"Bob","email":"june","age": 1}""" // age 필드는 User에 없음

                assertThrows<RuntimeException> {
                    Json.decodeFromString<User>(jsonString)
                }
            }

            test("ignoreUnknownKeys = true 설정하면 추가 필드가 와도 예외가 발생하지 않는다") {
                val jsonString = """{"id":4,"name":"Bob","email":"june","age": 1}""" // age 필드는 User에 없음

                val json = Json {
                    ignoreUnknownKeys = true
                }
                val user = json.decodeFromString<User>(jsonString)

                user.id shouldBe 4
                user.name shouldBe "Bob"
                user.email shouldBe "june"
            }
        }

        context("Default Argument 동작") {
            @Serializable
            data class UserWithDefaultArgument(
                val id: Int,
                val name: String,
                val email: String = "june@gmail.com",
            )

            test("default argument 로 인한 필드 누락 처리") {
                val jsonString = """{"id":3,"name":"June"}"""

                val user = Json.decodeFromString<UserWithDefaultArgument>(jsonString)

                user.id shouldBe 3
                user.name shouldBe "June"
                user.email shouldBe "june@gmail.com"
            }


            test("null 값이 들어오는 경우, default argument 를 넣어도 Exception이 발생한다") {
                val jsonString = """{"id":4,"name":"User","email":null}"""

                assertThrows<RuntimeException> {
                    Json.decodeFromString<UserWithDefaultArgument>(jsonString)
                }
            }


            test("null 값이 들어오는 경우, coerceInputValues = true 설정하면 default argument 로 대체된다") {
                val jsonString = """{"id":4,"name":"User","email":null}"""

                val json = Json {
                    coerceInputValues = true
                }

                val user = json.decodeFromString<UserWithDefaultArgument>(jsonString)

                user.id shouldBe 4
                user.name shouldBe "User"
                user.email shouldBe "june@gmail.com"
            }
        }

        context("Nullable 필드 동작") {
            @Serializable
            data class UserWithNullable(
                val id: Int,
                val name: String,
                val email: String?,
            )

            test("nullable한 필드잇는 JSON 문자열 역직렬화") {
                val jsonString = """{"id":2,"name":"June","email":null}"""

                val user = Json.decodeFromString<UserWithNullable>(jsonString)

                user.id shouldBe 2
                user.name shouldBe "June"
                user.email shouldBe null
            }

            test("nullable한 필드에 아무것도 안오면 예외 발생 - (기본동작)") {
                val jsonString = """{"id":2,"name":"June"}"""

                assertThrows<RuntimeException> {
                    Json.decodeFromString<UserWithNullable>(jsonString)
                }
            }

            test("nullable 한 필드는 직렬화할 때 적용도 null로 직렬화된다") {
                val user = UserWithNullable(id = 1, name = "June", email = null)

                val jsonString = Json.encodeToString(user)

                jsonString shouldBe """{"id":1,"name":"June","email":null}"""
            }
        }

        context("Nullable 필드에 DefaultArgument 동작") {

            @Serializable
            data class UserWithNullableAndDefault(
                val id: Int,
                val name: String,
                val email: String? = null,
            )

            test("nullable한 필드에 default argument 도 있으면 값이 안오면 default argument 로 처리된다") {
                val jsonString = """{"id":2,"name":"June"}"""

                val user = Json.decodeFromString<UserWithNullableAndDefault>(jsonString)

                user.id shouldBe 2
                user.name shouldBe "June"
                user.email shouldBe null
            }

            test("nullable한 필드에 default argument 도 있으면 인코딩 시 직렬화 안된다") {
                val user = UserWithNullableAndDefault(id = 2, name = "June")

                val jsonString = Json.encodeToString(user)

                jsonString shouldBe """{"id":2,"name":"June"}"""
            }

            test("nullable한 필드에 default argument 도 있으면 인코딩 시 직렬화 안된다") {
                val user = UserWithNullableAndDefault(id = 2, name = "June", email = null)

                val jsonString = Json.encodeToString(user)

                jsonString shouldBe """{"id":2,"name":"June"}"""
            }
        }

        context("backing Property 랑 by lazy 는 직렬화 불가") {
            @Serializable
            data class Sample(
                val id: Int,
                val name: String,
            ) {
                val upperName: String
                    get() = name.uppercase()

                val lazyValue: String by lazy {
                    "Lazy-$name"
                }
            }

            test("backing property 와 by lazy 프로퍼티는 직렬화/역직렬화 대상에서 제외된다") {
                val sample = Sample(id = 1, name = "test")
                val jsonString = Json.encodeToString(sample)

                // upperName 과 lazyValue 는 JSON에 포함되지 않음
                jsonString shouldBe """{"id":1,"name":"test"}"""

                val deserializedSample = Json.decodeFromString<Sample>(jsonString)

                deserializedSample.id shouldBe 1
                deserializedSample.name shouldBe "test"
                deserializedSample.upperName shouldBe "TEST"
                deserializedSample.lazyValue shouldBe "Lazy-test"
            }
        }

        context("@Required - 필수 필드 지정") {
            @Serializable
            data class UserWithRequired(
                val id: Int,
                val name: String,
                @Required
                val email: String = "june@gmail.com"
            )

            test("Required 어노테이션을 붙이면 default argument 붙여도 값이 안오면 터진다") {
                val jsonString = """{"id":5,"name":"User"}"""

                assertThrows<RuntimeException> {
                    Json.decodeFromString<UserWithRequired>(jsonString)
                }
            }
        }

        context("@Transient - 직렬화 대상에서 제외") {
            @Serializable
            data class UserWithTransient(
                val id: Int,
                val name: String,
                @kotlinx.serialization.Transient
                val email: String = "june@gmail.com"
            )

            test("Transient 어노테이션을 붙인 프로퍼티는 Encoding 대상에서 제외된다") {
                val user = UserWithTransient(id = 1, name = "June", email = "yeah")

                val jsonString = Json.encodeToString(user)

                jsonString shouldBe """{"id":1,"name":"June"}"""
            }

            test("Transient 어노테이션을 붙인 프로퍼티 역직렬화 대상에서 제외되기 때문에, 값이 오더라도 unKnownKey 예외가 발생한다") {
                val jsonString = """{"id":1,"name":"June", "email":"yeah"}"""

                assertThrows<RuntimeException> {
                    Json.decodeFromString<UserWithTransient>(jsonString)
                }
            }

            test("ignoreUnKnownKeys = true 설정하면 역직렬화 시 예외가 발생하지 않는다") {
                val jsonString = """{"id":1,"name":"June", "email":"yeah"}"""

                val json = Json {
                    ignoreUnknownKeys = true
                }
                val deserializedUser = json.decodeFromString<UserWithTransient>(jsonString)

                deserializedUser.id shouldBe 1
                deserializedUser.name shouldBe "June"
                deserializedUser.email shouldNotBe "yeah"
                deserializedUser.email shouldBe "june@gmail.com"
            }
        }

        context("Generic Box 도 직렬화/역직렬화 가능") {
            @Serializable
            data class Box<T>(
                val value: T
            )

            test("Generic Box 객체를 JSON 문자열로 직렬화/역직렬화") {
                val intBox = Box(123)
                val jsonString = Json.encodeToString(intBox)
                jsonString shouldBe """{"value":123}"""

                val deserializedBox = Json.decodeFromString<Box<Int>>(jsonString)
                deserializedBox.value shouldBe 123
            }
        }
    }
)