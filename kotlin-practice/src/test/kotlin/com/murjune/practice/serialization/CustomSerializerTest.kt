package com.murjune.practice.serialization

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * 커스텀 Serializer 구현 학습 테스트
 *
 * ## KSerializer란?
 * - 특정 Kotlin 타입의 직렬화/역직렬화 방식을 정의하는 핵심 인터페이스
 * - Kotlin 객체와 JSON, XML, Protobuf 등 데이터 포맷 사이의 번역가 역할
 *
 * ## NumberAsStringSerializer 예제:
 * - JSON에서 숫자(Int)와 문자열(String)을 모두 String으로 변환
 * - 서버에서 일관성 없이 `"id": 123` 또는 `"id": "123"` 으로 오는 경우 처리
 *
 * ## KSerializer 구현 필수 요소:
 * 1. **descriptor**: 직렬화 시스템에 데이터 형태를 알려주는 메타데이터
 * 2. **deserialize()**: JSON → Kotlin 객체 변환
 * 3. **serialize()**: Kotlin 객체 → JSON 변환
 */
class CustomSerializerTest : FunSpec(
    body = {
        test("NumberAsStringSerializer 테스트") {
            @Serializable
            data class SampleData(
                @Serializable(with = NumberAsStringSerializer::class)
                val id: String,
                val name: String,
            )

            // 숫자 형태의 id
            val jsonString1 = """{"id": 123, "name": "Test1"}"""
            val result1 = Json.decodeFromString<SampleData>(jsonString1)
            result1.id shouldBe "123"

            // 문자열 형태의 id
            val jsonString2 = """{"id": "456", "name": "Test2"}"""
            val result2 = Json.decodeFromString<SampleData>(jsonString2)
            result2.id shouldBe "456"
        }
    }
) {

    /**
     * 1. KSerializer란 무엇인가요? 📦
     * 특정 코틀린 타입 T의 직렬화(Serialization) 및 역직렬화(Deserialization) 방식을 정의하는 핵심 인터페이스입니다.
     * 간단히 말해, KSerializer는 코틀린 객체와 JSON, XML, Protobuf 등 실제 데이터 포맷 사이의 번역가 역할을 합니다.
     * */
    // KSerializer<String>: 이 Serializer가 처리하는 **코틀린 타입은 String**임을 명시합니다.
    // 즉, 이 Serializer를 통해 직렬화된 데이터는 String으로 인코딩되고, 디코딩된 데이터는 String으로 반환됩니다.
    object NumberAsStringSerializer : KSerializer<String> {

        // descriptor: 직렬화 시스템에 이 Serializer가 처리하는 데이터의 형태를 알려주는 메타데이터
        // PrimitiveSerialDescriptor: 이 Serializer가 단일 기본값(Primitive value)을 처리함을 나타냅니다.
        // PrimitiveKind.STRING: 직렬화 시스템에 "결국 이 Serializer는 String 형태의 데이터를 처리한다"고 알려줍니다.
        // 이 정보는 직렬화 포맷(예: JSON, Protobuf)이 데이터를 어떻게 읽고 쓸지 결정하는 데 사용됩니다.

        // 리플렉션을 사용하지 않기 때문에, 런타임에 클래스 구조를 알 수 없습니다. descriptor는 컴파일 시점에 생성되는 메타데이터이므로,
        // 직렬화/역직렬화에 필요한 구조 정보를 미리 확보합니다.
        //
        //만약 Serializer 구현이 잘못되었거나, 구조가 직렬화에 적합하지 않은 경우, 컴파일러 플러그인이 descriptor를 생성하는 과정에서 오류를 잡아낼 수 있습니다.
        override val descriptor = PrimitiveSerialDescriptor("NumberAsString", PrimitiveKind.STRING)

        // deserialize 함수는 JSON 등의 인코딩된 데이터를 읽어와 코틀린 객체(String)로 변환하는 로직을 담고 있습니다.
        override fun deserialize(decoder: Decoder): String {
            // Decoder가 JsonDecoder인지 확인하여 JsonPrimitive에 접근

            val jsonDecoder = decoder as? JsonDecoder
                ?: throw IllegalStateException("Expected JsonDecoder")

            val element = jsonDecoder.decodeJsonElement()

            return when (element) {
                is JsonPrimitive -> {
                    if (element.isString) {
                        // 1. 이미 String인 경우 (예: "1")
                        element.content
                    } else {
                        // 2. Number인 경우 (예: 1)
                        // longOrNull 등으로 숫자를 안전하게 읽은 후, String으로 변환하여 반환
                        element.longOrNull?.toString() ?: element.content // 기본적으로 content가 String으로 표현됨
                    }
                }

                else -> throw IllegalStateException("Expected JsonPrimitive, but found $element")
            }
        }

        override fun serialize(encoder: Encoder, value: String) {
            // 직렬화는 String 그대로 처리
            encoder.encodeString(value)
        }
    }
}