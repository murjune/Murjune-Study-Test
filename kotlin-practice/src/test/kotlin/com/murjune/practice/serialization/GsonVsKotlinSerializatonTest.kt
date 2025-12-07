package com.murjune.practice.serialization

import com.google.gson.Gson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * Gson vs Kotlin-Serialization 차이점 비교 학습 테스트
 *
 * ## 주요 차이점:
 * 1. **Default Argument 처리**
 *    - Gson: default argument 무시, null 할당 (타입 안정성 깨짐)
 *    - Kotlin-Serialization: default argument 올바르게 적용
 *
 * 2. **Null Handling과 Type Safety**
 *    - Gson: null을 non-nullable 타입에 할당 가능 (위험)
 *    - Kotlin-Serialization: coerceInputValues 옵션으로 안전하게 처리
 *
 * 3. **Unknown Keys 처리**
 *    - Gson: 기본적으로 모르는 필드 무시
 *    - Kotlin-Serialization: ignoreUnknownKeys 옵션으로 제어
 *
 * 4. **sealed class 지원**
 *    - Gson: 직접 지원 없음 (커스텀 Adapter 필요)
 *    - Kotlin-Serialization: 자동 지원
 *
 * 5. **성능 (리플렉션 vs Serializer)**
 *    - Gson: 런타임 리플렉션 사용 (느림)
 *    - Kotlin-Serialization: 컴파일 타임 Serializer 생성 (빠름)
 */
class GsonVsKotlinSerializatonTest : FunSpec(
    body = {
        val gson = Gson()
        val json = Json {
            ignoreUnknownKeys = true // 모르는 필드 무시
            coerceInputValues = true // null이 들어오면 default argument로 대체
        }

        context("1. Default Argument 처리 차이") {
            data class GsonProject(
                val name: String,
                val language: String = "Kotlin", // default argument
            )

            @Serializable
            data class KotlinSerializationProject(
                val name: String,
                val language: String = "Kotlin", // default argument
            )

            test("Gson은 default argument를 무시하고 null을 할당한다") {
                // language 필드가 없는 JSON
                val jsonString = """{"name":"TestProject"}"""

                val result = gson.fromJson(jsonString, GsonProject::class.java)

                // Gson은 default argument를 무시하고 null을 할당
                // Not-Nullable한 String 타입인데 null이 들어가서 타입 안정성이 깨짐!
                result.language shouldBe null
            }

            test("Kotlin-Serialization은 default argument를 올바르게 사용한다") {
                val jsonString = """{"name":"TestProject"}"""

                val result = json.decodeFromString<KotlinSerializationProject>(jsonString)

                // Kotlin-Serialization은 default argument를 올바르게 사용
                result.language shouldBe "Kotlin"
            }
        }

        context("2. Null Handling과 Type Safety") {
            @Serializable
            data class User(
                val id: Int,
                val name: String,
                val email: String = "unknown@example.com",
            )

            test("Gson은 null을 그대로 받아서 Non-nullable 타입에 null 할당됨 (타입 안정성 깨짐)") {
                data class GsonUser(
                    val id: Int,
                    val name: String,
                    val email: String = "unknown@example.com",
                )

                // email이 null인 JSON
                val jsonString = """{"id":1,"name":"John","email":null}"""

                val result = gson.fromJson(jsonString, GsonUser::class.java)

                // Gson은 타입 안정성을 보장하지 않음!
                // non-nullable String 타입인데 null이 들어감
                result.email shouldBe null
                println("Gson 결과: $result (email이 null임!)")
            }

            test("Kotlin-Serialization은 coerceInputValues로 null을 default로 대체") {
                // email이 null인 JSON
                val jsonString = """{"id":1,"name":"John","email":null}"""

                val result = json.decodeFromString<User>(jsonString)

                // coerceInputValues = true 설정으로 null이 default argument로 대체됨
                result.email shouldBe "unknown@example.com"
                println("Kotlin-Serialization 결과: $result")
            }

            test("Kotlin-Serialization은 coerceInputValues 없이 null이 오면 에러 발생") {
                val strictJson = Json {
                    ignoreUnknownKeys = true
                    // coerceInputValues = false (기본값)
                }

                val jsonString = """{"id":1,"name":"John","email":null}"""

                // null이 non-nullable 타입에 들어가면 SerializationException 발생
                shouldThrow<SerializationException> {
                    strictJson.decodeFromString<User>(jsonString)
                }
                println("coerceInputValues 없으면 에러 발생!")
            }

            test("Gson은 unknown keys를 기본적으로 무시함") {
                data class GsonUser(
                    val id: Int,
                    val name: String,
                )

                // 정의되지 않은 age, address 필드가 있는 JSON
                val jsonString = """{"id":1,"name":"John","age":30,"address":"Seoul"}"""

                val result = gson.fromJson(jsonString, GsonUser::class.java)

                // Gson은 별도 설정 없이 모르는 필드를 무시
                result.id shouldBe 1
                result.name shouldBe "John"
                println("Gson은 unknown keys 자동 무시")
            }


            test("Gson은 null 값으로 오면 Non-Null한 타입에 null 할당됨") {
                data class GsonUser(
                    val id: Int,
                    val name: String,
                )

                // name 필드가 null인 JSON
                val jsonString = """{"id":1,"name":null}"""

                val result = gson.fromJson(jsonString, GsonUser::class.java)

                // Gson은 Non-Null한 String 타입인데 null이 들어감
                result.name shouldBe null
            }

            test("Kotlin-Serialization은 ignoreUnknownKeys로 모르는 필드 무시") {
                // 정의되지 않은 age 필드가 있는 JSON
                val jsonString = """{"id":1,"name":"John","age":30}"""

                val result = json.decodeFromString<User>(jsonString)

                // ignoreUnknownKeys = true 설정으로 모르는 필드(age)를 무시
                result.id shouldBe 1
                result.name shouldBe "John"
                println("Kotlin-Serialization 결과: $result")
            }

            test("Kotlin-Serialization은 ignoreUnknownKeys 없이 unknown key가 오면 에러 발생") {
                val strictJson = Json {
                    // ignoreUnknownKeys = false (기본값)
                    coerceInputValues = true
                }

                val jsonString = """{"id":1,"name":"John","email":"test@test.com","age":30}"""

                // unknown key가 있으면 SerializationException 발생
                shouldThrow<SerializationException> {
                    strictJson.decodeFromString<User>(jsonString)
                }
                println("ignoreUnknownKeys 없으면 에러 발생!")
            }
        }

        context("3. sealed class 직렬화 지원") {
            // Gson은 sealed class를 직접 지원하지 않음
            // Kotlin-Serialization은 sealed class를 자동으로 처리

            test("Kotlin-Serialization은 sealed class를 자동으로 직렬화/역직렬화") {
                val success: ApiResponse = ApiResponse.Success("Hello")
                val error: ApiResponse = ApiResponse.Error("Not Found", 404)
                val loading: ApiResponse = ApiResponse.Loading

                // 직렬화
                val successJson = json.encodeToString(success)
                val errorJson = json.encodeToString(error)
                val loadingJson = json.encodeToString(loading)

                println("Success JSON: $successJson")
                println("Error JSON: $errorJson")
                println("Loading JSON: $loadingJson")

                // 역직렬화
                val decodedSuccess = json.decodeFromString<ApiResponse>(successJson)
                val decodedError = json.decodeFromString<ApiResponse>(errorJson)
                val decodedLoading = json.decodeFromString<ApiResponse>(loadingJson)

                decodedSuccess shouldBe success
                decodedError shouldBe error
                decodedLoading shouldBe loading
            }

            test("sealed class는 타입 정보를 포함한 JSON으로 변환된다") {
                val response: ApiResponse = ApiResponse.Success("Data loaded")

                val jsonString = json.encodeToString(response)

                // type 필드가 자동으로 추가됨
                println("JSON: $jsonString")
                jsonString shouldNotBe null
                // 출력 예: {"type":"Success","data":"Data loaded"}
            }
        }

        context("4. 성능 차이 - 리플렉션 vs Serializer") {
            @Serializable
            data class Product(
                val id: Long,
                val name: String,
                val price: Double,
                val description: String = "No description",
            )

            test("Gson은 리플렉션을 사용하여 비용이 많이 든다") {
                val jsonString = """{"id":1,"name":"Laptop","price":1500.0}"""

                // Gson은 런타임에 리플렉션을 사용
                val result = gson.fromJson(jsonString, Product::class.java)

                result.id shouldBe 1L
                result.name shouldBe "Laptop"
                result.price shouldBe 1500.0
                // default argument가 무시되고 null이 들어감
                result.description shouldBe null

                println("Gson은 리플렉션 사용 - 런타임 비용 발생")
            }

            test("Kotlin-Serialization은 컴파일 타임에 Serializer를 생성한다") {
                val jsonString = """{"id":1,"name":"Laptop","price":1500.0}"""

                // Kotlin-Serialization은 컴파일 타임에 Serializer 생성
                val result = json.decodeFromString<Product>(jsonString)

                result.id shouldBe 1L
                result.name shouldBe "Laptop"
                result.price shouldBe 1500.0
                // default argument가 올바르게 사용됨
                result.description shouldBe "No description"

                println("Kotlin-Serialization은 Serializer 사용 - 컴파일 타임 생성으로 빠른 성능")
            }
        }


        context("5. 타입 변환 처리 차이") {
            test("Kotlin-Serialization은 coerceInputValues로 null을 default argument로 대체") {
                @Serializable
                data class User(
                    val id: Int,
                    val name: String,
                    val email: String = "default@example.com",
                )

                val json = Json {
                    coerceInputValues = true // null이 들어오면 default argument로 대체
                }
                val jsonString = """{"id":1,"name":"june","email":null}"""
                val result = json.decodeFromString<User>(jsonString)

                // null이 들어왔지만 default argument로 대체됨
                result.email shouldBe "default@example.com"
            }

            test("Gson은 타입이 맞지 않아도 자동 형변환 (Int -> String)") {
                data class User(
                    val id: Int,
                    val name: String,
                    val email: String, // String 타입
                )

                val gson = Gson()
                val jsonString = """{"id":1,"name":"june","email":1}""" // email이 Int(1)

                val user = gson.fromJson(jsonString, User::class.java)

                // Gson은 Int(1)을 자동으로 String("1")로 변환
                user.email shouldBe "1"
                println("Gson 자동 형변환: ${user.email}")
            }
        }
    },
) {
    @Serializable
    private sealed class ApiResponse {
        @Serializable
        data class Success(val data: String) : ApiResponse()

        @Serializable
        data class Error(val message: String, val code: Int) : ApiResponse()

        @Serializable
        data object Loading : ApiResponse()
    }
}