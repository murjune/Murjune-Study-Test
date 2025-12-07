package com.murjune.practice.serialization

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonClassDiscriminator
import org.junit.jupiter.api.assertThrows

/**
 * Kotlin-Serialization 사용법 학습 테스트
 *
 * 주요 기능:
 * 1. 기본 직렬화/역직렬화
 * 2. Json Configuration
 * 3. 중첩 객체 직렬화
 * 4. 리스트 직렬화
 * 5. @SerialName을 통한 필드명 커스터마이징
 */
class KotlinSerializationTest : FunSpec(
    body = {
        context("1. 기본 직렬화/역직렬화") {
            @Serializable
            data class User(
                val id: Int,
                val name: String,
                val email: String = "june@gmail.com",
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

            test("default argument 로 인한 필드 누락 처리") {
                val jsonString = """{"id":3,"name":"June"}"""

                val user = Json.decodeFromString<User>(jsonString)

                user.id shouldBe 3
                user.name shouldBe "June"
                user.email shouldBe "june@gmail.com"

            }

            test("null 값이 들어오는 경우, default argument 를 넣어도 Exception이 발생한다") {
                val jsonString = """{"id":4,"name":"NullUser","email":null}"""

                assertThrows<RuntimeException> {
                    Json.decodeFromString<User>(jsonString)
                }
            }
        }

        context("2. Json Configuration - 다양한 옵션 설정") {
            @Serializable
            data class Config(
                val setting: String,
                val value: Int = 100,
            )

            test("ignoreUnknownKeys - 정의되지 않은 필드 무시") {
                val json = Json { ignoreUnknownKeys = true }
                val jsonString = """{"setting":"test","value":200,"unknownField":"ignored"}"""

                val config = json.decodeFromString<Config>(jsonString)

                config.setting shouldBe "test"
                config.value shouldBe 200
            }

            test("coerceInputValues - null 값을 default argument로 대체") {
                val json = Json { coerceInputValues = true }
                val jsonString = """{"setting":"test","value":null}"""

                val config = json.decodeFromString<Config>(jsonString)

                config.setting shouldBe "test"
                config.value shouldBe 100
            }

            test("prettyPrint - 읽기 쉬운 형식으로 출력") {
                val json = Json { prettyPrint = true }
                val config = Config(setting = "debug", value = 42)

                val jsonString = json.encodeToString(config)

                println("Pretty Print 결과:\n$jsonString")
            }

            test("여러 옵션 동시 사용") {
                val json = Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                    prettyPrint = true
                }

                val jsonString = """{"setting":"production","value":null,"extra":"ignored"}"""
                val config = json.decodeFromString<Config>(jsonString)

                config.setting shouldBe "production"
                config.value shouldBe 100

                val encoded = json.encodeToString(config)
                println("결과:\n$encoded")
            }
        }

        context("3. 중첩 객체 직렬화") {
            @Serializable
            data class Address(
                val city: String,
                val street: String,
                val zipCode: String,
            )

            @Serializable
            data class Person(
                val name: String,
                val age: Int,
                val address: Address, // 중첩 객체
            )

            test("중첩된 객체를 직렬화/역직렬화") {
                val person = Person(
                    name = "Alice",
                    age = 30,
                    address = Address(
                        city = "Seoul",
                        street = "Gangnam-daero",
                        zipCode = "12345",
                    ),
                )

                // 직렬화
                val jsonString = Json.encodeToString(person)
                println("중첩 객체 직렬화: $jsonString")

                // 역직렬화
                val decoded = Json.decodeFromString<Person>(jsonString)

                decoded.name shouldBe "Alice"
                decoded.age shouldBe 30
                decoded.address.city shouldBe "Seoul"
                decoded.address.street shouldBe "Gangnam-daero"
                decoded.address.zipCode shouldBe "12345"
            }
        }

        context("4. 리스트와 컬렉션 직렬화") {
            @Serializable
            data class Product(
                val id: Long,
                val name: String,
                val price: Double,
            )

            test("리스트 직렬화") {
                val products = listOf(
                    Product(1, "Laptop", 1500.0),
                    Product(2, "Mouse", 25.0),
                    Product(3, "Keyboard", 75.0),
                )

                val jsonString = Json.encodeToString(products)

                println("리스트 직렬화: $jsonString")
            }

            test("리스트 역직렬화") {
                val jsonString = """
                    [
                        {"id":1,"name":"Laptop","price":1500.0},
                        {"id":2,"name":"Mouse","price":25.0}
                    ]
                """.trimIndent()

                val products = Json.decodeFromString<List<Product>>(jsonString)

                products.size shouldBe 2
                products[0].name shouldBe "Laptop"
                products[1].name shouldBe "Mouse"
                println("리스트 역직렬화: $products")
            }
        }

        context("5. @SerialName - JSON 필드명 커스터마이징") {
            @Serializable
            data class ApiUser(
                @SerialName("user_id")
                val userId: Int, // JSON에서는 user_id, Kotlin에서는 userId

                @SerialName("user_name")
                val userName: String,

                @SerialName("email_address")
                val emailAddress: String,
            )

            test("@SerialName으로 JSON 필드명 변경") {
                val user = ApiUser(
                    userId = 123,
                    userName = "john_doe",
                    emailAddress = "john@example.com",
                )

                val jsonString = Json.encodeToString(user)

                println("@SerialName 결과: $jsonString")
                // JSON에서는 snake_case 사용
                jsonString shouldBe """{"user_id":123,"user_name":"john_doe","email_address":"john@example.com"}"""
            }

            test("@SerialName으로 역직렬화") {
                // snake_case JSON
                val jsonString = """{"user_id":456,"user_name":"jane_doe","email_address":"jane@example.com"}"""

                val user = Json.decodeFromString<ApiUser>(jsonString)

                // Kotlin에서는 camelCase로 사용
                user.userId shouldBe 456
                user.userName shouldBe "jane_doe"
                user.emailAddress shouldBe "jane@example.com"
                println("역직렬화 결과: $user")
            }
        }

        context("6. Nullable과 Optional 필드") {
            @Serializable
            data class OptionalData(
                val required: String,
                val nullable: String? = null, // nullable with default
                val optional: Int = 42, // optional with default
            )

            test("nullable 필드 처리") {
                val jsonString = """{"required":"test","nullable":null}"""

                val data = Json.decodeFromString<OptionalData>(jsonString)

                data.required shouldBe "test"
                data.nullable shouldBe null
                data.optional shouldBe 42
                println("결과: $data")
            }

            test("필드가 없는 경우 default 값 사용") {
                val jsonString = """{"required":"test"}"""

                val data = Json.decodeFromString<OptionalData>(jsonString)

                data.required shouldBe "test"
                data.nullable shouldBe null
                data.optional shouldBe 42
                println("결과: $data")
            }
        }

        context("7. sealed class를 활용한 다형성 처리") {

            test("sealed class 각 타입별 직렬화") {
                val success: Result = Result.Success("Operation completed", 1234567890L)
                val failure: Result = Result.Failure("Network error", 500)
                val empty: Result = Result.Empty

                val successJson = Json.encodeToString<Result>(success)
                val failureJson = Json.encodeToString<Result>(failure)
                val emptyJson = Json.encodeToString<Result>(empty)

                // 타입 정보가 포함됨
                successJson shouldBe """{"type":"Success","data":"Operation completed","timestamp":1234567890}"""
                failureJson shouldBe """{"type":"Failure","error":"Network error","code":500}"""
                emptyJson shouldBe """{"type":"Empty"}"""
            }

            test("sealed class 역직렬화 - 타입에 따라 자동 분기") {
                val originalSuccess = Result.Success("Done", 1234567890L)
                val originalFailure = Result.Failure("Error occurred", 404)

                val successJson = """{"type":"Success","data":"Done","timestamp":1234567890}"""
                val failureJson = """{"type":"Failure","error":"Error occurred","code":404}"""

                val success = Json.decodeFromString<Result>(successJson)
                val failure = Json.decodeFromString<Result>(failureJson)

                success shouldBe originalSuccess
                failure shouldBe originalFailure
            }

            test("만약 type 필드가 없으면 예외 발생") {
                val invalidJson = """{"customType":"Success","data":"Done","timestamp":1234567890}"""

                assertThrows<RuntimeException> {
                    Json.decodeFromString<Result>(invalidJson)
                }
            }

            // 1. Json discriminator 변경
            test("discriminator 필드명 변경") {
                val json = Json {
                    classDiscriminator = "customType"
                }

                val originalSuccess: Result = Result.Success("Done", 1234567890L)
                val successJson: String = json.encodeToString<Result>(originalSuccess)

                successJson shouldBe """{"customType":"Success","data":"Done","timestamp":1234567890}"""

                val decoded = json.decodeFromString<Result>(successJson)
                decoded shouldBe originalSuccess
            }

            // 2. JsonClassDicriminator
            test("JsonClassDiscriminator 어노테이션으로 discriminator 필드명 변경") {
                val originalSuccess: CustomResult = CustomResult.Success("Done", 1234567890L)
                val successJson: String = Json.encodeToString<CustomResult>(originalSuccess)

                successJson shouldBe """{"kind":"Success","data":"Done","timestamp":1234567890}"""

                val decoded = Json.decodeFromString<CustomResult>(successJson)
                decoded shouldBe originalSuccess
            }
        }
    },
) {
    @Serializable
    @JsonClassDiscriminator("kind")
    private sealed class CustomResult {
        @Serializable
        @SerialName("Success")
        data class Success(val data: String, val timestamp: Long) : CustomResult()

        @Serializable
        @SerialName("Failure")
        data class Failure(val error: String, val code: Int) : CustomResult()
    }

    @Serializable
    private sealed class Result {
        @Serializable
        @SerialName("Success")
        data class Success(val data: String, val timestamp: Long) : Result()

        @Serializable
        @SerialName("Failure")
        data class Failure(val error: String, val code: Int) : Result()

        @Serializable
        @SerialName("Empty")
        data object Empty : Result()
    }
}