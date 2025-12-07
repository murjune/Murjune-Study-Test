package com.murjune.practice.serialization

import com.google.gson.Gson
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Kotlin Serialization 사용 시 주의사항 테스트
 *
 * 1. default argument 는 역직렬화 시 적용되지 않는다
 *    - RequestBody에 default argument 값과 동일한 값이 들어올 경우 직렬화 안됨
 *    - 서버에서 null 로 처리할 경우 문제 생길 수 있음
 *    -> 해결법: @EncodeDefault 사용
 * */
@OptIn(ExperimentalSerializationApi::class)
class KotlinSerializationCautionTest : FunSpec(
    body = {
        context("default argument 는 ⚠️ 역직렬화 시 적용되지 않는다") {
            // ⚠️ 매우 주의해야할 케이스 - RequestBody에 default argument 값과 동일한 값이 들어올 경우 직렬화 안된다
            test("⚠️ Default Argument 적용 시 역직렬화 안됨") {
                @Serializable
                data class Project(
                    val name: String,
                    val language: String = "Kotlin",
                )

                val project = Project("june")

                val result = Json.encodeToString<Project>(project)

                result shouldBe """{"name":"june"}"""
            }

            // ⚠️ 매우 주의해야할 케이스 - RequestBody에 default argument 값과 동일한 값이 들어올 경우 직렬화 안된다
            test("⚠️ Default Argument에 동일한 값 넣을 때도 역직렬화 안됨") {
                @Serializable
                data class Project(
                    val name: String,
                    val language: String = "Kotlin",
                )

                val project = Project(name = "june", language = "Kotlin")

                val result = Json.encodeToString<Project>(project)

                result shouldBe """{"name":"june"}"""
            }

            test("Default Argument에 다른값 넣으면 역직렬화 잘됨") {
                @Serializable
                data class Project(
                    val name: String,
                    val language: String = "Kotlin",
                )

                val project = Project(name = "june", language = "Kotlin2")

                val result = Json.encodeToString<Project>(project)

                result shouldBe """{"name":"june","language":"Kotlin2"}"""
            }

            // ⚠️ 매우 주의해야할 케이스 - 서버에서 null 로 처리할 경우 문제 생길 수 있음
            test("⚠️ Null Default Argument도 동일하게 역직렬화 안된다") {
                @Serializable
                data class ProjectNullable(
                    val name: String,
                    val language: String? = null,
                )

                val project = ProjectNullable(name = "june")

                val result = Json.encodeToString<ProjectNullable>(project)

                result shouldBe """{"name":"june"}"""
            }

            // ⚠️ 매우 주의해야할 케이스 - 서버에서 null 로 처리할 경우 문제 생길 수 있음
            test("⚠️ Null Default Argument 적용할 때, null 넣으면 역직렬화 안된다") {
                @Serializable
                data class ProjectNullable(
                    val name: String,
                    val language: String? = null,
                )

                val project = ProjectNullable(name = "june", language = null)

                val result = Json.encodeToString<ProjectNullable>(project)

                result shouldBe """{"name":"june"}"""
            }
        }

        context("✅ @EncodeDefault 를 사용하면 default argument도 직렬화된다") {
            @Serializable
            data class ProjectWithEncodeDefault(
                val name: String,
                @EncodeDefault
                val language: String = "Kotlin",
            )
            test("@EncodeDefault 사용 시 default argument 도 직렬화됨") {
                val project = ProjectWithEncodeDefault("june")

                val result = Json.encodeToString<ProjectWithEncodeDefault>(project)

                result shouldBe """{"name":"june","language":"Kotlin"}"""
            }

            test("@EncodeDefault 사용 시 default argument 에 동일한 값 넣어도 직렬화됨") {
                val project = ProjectWithEncodeDefault(name = "june", language = "Kotlin")

                val result = Json.encodeToString<ProjectWithEncodeDefault>(project)

                result shouldBe """{"name":"june","language":"Kotlin"}"""
            }
        }

        context("Kotlin-Serialization과 Gson의 타입 변환 차이") {
            test("coerceInputValues 설정 시 null 을 default argument 로 대체") {
                @Serializable
                data class User(
                    val id: Int,
                    val name: String,
                    val email: String = "2"
                )

                val json = Json {
                    coerceInputValues = true // null이 들어오면 default argument로 대체
                }
                val jsonString = """{"id":1,"name":"june","email":"1"}"""
                val result = json.decodeFromString<User>(jsonString)
                result.email shouldBe "1"
            }

            test("Gson은 타입이 다른 값도 자동으로 형변환해준다 (예: Int -> String)") {
                data class User(
                    val id: Int,
                    val name: String,
                    val email: String, // String 타입
                )

                val gson = Gson()
                val jsonString = """{"id":1,"name":"june","email":1}""" // email이 Int(1)로 들어옴

                val user = gson.fromJson(jsonString, User::class.java)

                // Gson은 Int(1)을 String("1")로 자동 변환
                user.email shouldBe "1"
                println("Gson은 자동 형변환: ${user.email}")
            }
        }
    }
)
