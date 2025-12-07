# Gson vs Kotlin-Serialization

서버 통신을 위한 JSON 직렬화 라이브러리 비교 학습

## 개요

JSON 직렬화를 할 때 가장 많이 사용하는 라이브러리는 **Gson**입니다.
구글이 직접 제공하는 라이브러리이고, 오랜 기간 많은 사람들이 사용해온 만큼 공신력도 있고 사용하기도 굉장히 쉽습니다.

하지만 Kotlin 환경에서는 **Kotlin-Serialization**이 더 나은 선택이 될 수 있습니다.
이 문서에서는 Gson과 Kotlin-Serialization의 차이점을 설명하고, Kotlin-Serialization을 선택한 이유를 서술합니다.

## Kotlin-Serialization을 선택한 이유

### 1. 불안정한 타입 컨버팅 & Default-Argument의 부재

**Gson의 문제점:**

```kotlin
data class Project(
    val name: String,
    val language: String = "Kotlin", // default argument 설정
)

val jsonString = """{"name":"TestProject"}""" // language 필드 없음
val result = gson.fromJson(jsonString, Project::class.java)

println(result.language) // null 출력!
```

Project data class의 `language` 변수에 default argument를 설정했음에도, `language`는 `"Kotlin"`이 아닌 **null**이 할당됩니다.

따라서 `language` 변수는 **Not-Nullable한 String 타입**으로 지정되었는데, null 값이 들어가서 Kotlin 문법에 맞지 않는 상황이 만들어져 **예상치 못한 오류**를 일으킬 수 있습니다.

**Kotlin-Serialization의 해결:**

```kotlin
@Serializable
data class Project(
    val name: String,
    val language: String = "Kotlin",
)

val jsonString = """{"name":"TestProject"}"""
val result = json.decodeFromString<Project>(jsonString)

println(result.language) // "Kotlin" 출력!
```

Kotlin-Serialization의 경우에는 **default argument가 올바르게 적용**됩니다.

### 2. 리플렉션의 사용

**Gson**: 리플렉션을 활용하여 타입 컨버팅을 하기에 비용이 Kotlin-Serialization에 비해 많이 듭니다.

**Kotlin-Serialization**: Serializer를 사용하기에 속도면에서 이득을 볼 수 있습니다.

- Gson은 **런타임에 리플렉션**을 사용하여 객체를 변환
- Kotlin-Serialization은 **컴파일 타임에 Serializer를 생성**하여 성능이 더 빠름

### 3. Kotlin 100%

Kotlin-Serialization은 100% Kotlin으로 만들어져 있고, Kotlin을 만든 **JetBrains 사가 공식적으로 지원**하는 라이브러리입니다.

Kotlin 안에서는 보다 강력한 기능과 확장성을 제공합니다. (예: Multiplatform 지원)

최근에 Kotlin 2.0이 나왔는데, Kotlin-Serialization은 벌써 대응을 완료했습니다.

### 4. 쉽게 커스터마이징 가능한 Kotlin-Serialization

Kotlin-Serialization의 경우 **Json Option**으로 개발자가 원하는 기능을 쉽게 추가할 수 있습니다.

```kotlin
val json = Json {
    ignoreUnknownKeys = true  // 정의되지 않은 필드 무시
    coerceInputValues = true  // null이 들어온 경우 default argument 값으로 대체, 정의되지 않은 enum 값을 default 값으로 대체
    prettyPrint = true        // 읽기 쉬운 형식으로 출력
}
```

이러한 옵션들을 자주 사용하여 안전하고 유연한 JSON 처리가 가능합니다.

### 5. sealed class 지원

**Gson의 한계:**

Gson은 sealed class를 직접 지원하지 않습니다. 커스텀 Adapter를 작성해야만 sealed class를 직렬화/역직렬화할 수 있습니다.

**Kotlin-Serialization의 강점:**

```kotlin
@Serializable
sealed class ApiResponse {
    @Serializable
    data class Success(val data: String) : ApiResponse()

    @Serializable
    data class Error(val message: String, val code: Int) : ApiResponse()

    @Serializable
    data object Loading : ApiResponse()
}

// 직렬화
val response: ApiResponse = ApiResponse.Success("Hello")
val jsonString = json.encodeToString(response)
// {"type":"Success","data":"Hello"}

// 역직렬화 - 타입에 따라 자동 분기
val decoded = json.decodeFromString<ApiResponse>(jsonString)
when (decoded) {
    is ApiResponse.Success -> println(decoded.data)
    is ApiResponse.Error -> println(decoded.message)
    is ApiResponse.Loading -> println("Loading...")
}
```

Kotlin-Serialization은 sealed class를 **자동으로 처리**하여, 타입 안전성과 함께 다형성을 쉽게 구현할 수 있습니다.

## 주요 차이점 요약

| 특징 | Gson | Kotlin-Serialization |
|------|------|---------------------|
| **Default Argument** | 무시됨 (null 할당) | 올바르게 적용됨 |
| **Type Safety** | 타입 안정성 깨질 수 있음 | 컴파일 타임에 보장 |
| **성능** | 리플렉션 사용 (느림) | Serializer 사용 (빠름) |
| **Kotlin 지원** | Java 라이브러리 | Kotlin 100% 지원 |
| **sealed class** | 직접 지원 안 함 | 자동 지원 |
| **커스터마이징** | 복잡함 | Json 옵션으로 쉽게 설정 |
| **멀티플랫폼** | 지원 안 함 | 지원 |

## 학습 테스트 파일 구조

### 1. Gson vs Kotlin-Serialization 비교
**`GsonVsKotlinSerializatonTest.kt`** - Gson과 Kotlin-Serialization의 차이점 비교
- Default Argument 처리 차이
- Null Handling과 Type Safety
- Unknown Keys 처리
- sealed class 지원
- 성능 차이 (리플렉션 vs Serializer)
- 타입 변환 처리

### 2. Kotlin-Serialization 기본
**`KotlinSerializationBasicTest.kt`** - 기본 직렬화/역직렬화 동작
- 기본 직렬화/역직렬화 (JSON ↔ Kotlin 객체)
- Default Argument 동작 (필드 누락, null 처리)
- Nullable 필드 동작
- @Required, @Transient 어노테이션
- backing property, lazy 프로퍼티
- Generic 타입 직렬화

**`KotlinSerializationTest.kt`** - 전반적인 사용법
- 기본 직렬화/역직렬화
- Json Configuration (ignoreUnknownKeys, coerceInputValues, prettyPrint)
- 중첩 객체 직렬화
- 리스트와 컬렉션 직렬화
- @SerialName을 통한 필드명 커스터마이징
- Nullable과 Optional 필드
- sealed class를 활용한 다형성 처리

### 3. Json 옵션 설정
**`JsonOptionsTest.kt`** - Json 옵션 상세 테스트
- ignoreUnknownKeys 옵션 (unknown keys 처리)
- coerceInputValues 옵션 (null → default, unknown enum → default)

### 4. 주의사항
**`KotlinSerializationCautionTest.kt`** - 사용 시 주의해야 할 사항
- ⚠️ default argument는 인코딩 시 적용되지 않음
- ✅ @EncodeDefault로 default argument도 직렬화 가능
- Kotlin-Serialization과 Gson의 타입 변환 차이

### 5. 커스텀 Serializer
**`CustomSerializerTest.kt`** - 커스텀 Serializer 구현
- KSerializer 인터페이스
- NumberAsStringSerializer 예제 (Int/String 모두 String으로 변환)
- descriptor, deserialize(), serialize() 구현

## 결론

Kotlin 프로젝트에서는 **Kotlin-Serialization**을 사용하는 것이 좋습니다:

1. **타입 안정성**: Default argument와 Null Safety를 올바르게 처리
2. **성능**: 컴파일 타임 Serializer 생성으로 빠른 성능
3. **Kotlin 친화적**: JetBrains 공식 지원, Kotlin 문법과 잘 어울림
4. **확장성**: sealed class, multiplatform 등 강력한 기능 지원
5. **유연성**: Json 옵션으로 쉬운 커스터마이징

## 참고 자료
- [Kotlin Serialization 공식 문서](https://github.com/Kotlin/kotlinx.serialization)
- [신입 안드로이드 개발자의 kotlinx.serialization 리팩토링 서사시](https://blog.mathpresso.com/%EC%8B%A0%EC%9E%85-%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EA%B0%9C%EB%B0%9C%EC%9E%90%EC%9D%98-kotlinx-serialization-%EB%A6%AC%ED%8C%A9%ED%86%A0%EB%A7%81-%EC%84%9C%EC%82%AC%EC%8B%9C-740597911e2e)
