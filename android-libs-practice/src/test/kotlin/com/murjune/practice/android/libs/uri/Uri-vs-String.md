# Uri vs 하드코딩 String 비교

## Uri 활용의 장점

### 1. 안전한 URL 구성
```kotlin
// Uri.Builder 사용 - 안전함
val uri = Uri.Builder()
    .scheme("https")
    .authority("api.example.com")
    .path("/users")
    .appendQueryParameter("name", "John Doe")
    .appendQueryParameter("age", "30")
    .build()

// 결과: https://api.example.com/users?name=John%20Doe&age=30
```

### 2. 자동 URL 인코딩
- 특수문자, 공백, 한글 등이 자동으로 URL 인코딩됨
- 개발자가 직접 인코딩할 필요 없음
- 보안상 안전한 URL 생성

### 3. 구조화된 URL 파싱
```kotlin
val uri = Uri.parse("https://example.com/users/123?active=true&role=admin")

// 각 구성 요소에 안전하게 접근
val scheme = uri.scheme        // "https"
val host = uri.host           // "example.com"
val path = uri.path           // "/users/123"
val userId = uri.lastPathSegment  // "123"
val active = uri.getQueryParameter("active")  // "true"
```

### 4. 타입 안전성
- 각 구성 요소별로 명확한 API 제공
- 컴파일 타임에 오류 감지 가능
- IDE 자동완성 지원

### 5. 유지보수성
- 각 구성 요소를 독립적으로 수정 가능
- 코드 가독성 향상
- 리팩토링 시 안전함

## 하드코딩 String의 단점과 위험성

### 1. URL 인코딩 문제
```kotlin
// 위험한 예시 - 특수문자가 인코딩되지 않음
val unsafeUrl = "https://api.example.com/search?q=Hello World & More"
// 결과: 잘못된 URL (공백과 &가 인코딩되지 않음)

// 개발자가 직접 인코딩해야 함 (실수하기 쉬움)
val manualEncoding = "https://api.example.com/search?q=Hello%20World%20%26%20More"
```

### 2. 문자열 연결 오류
```kotlin
// 실수하기 쉬운 패턴들
val baseUrl = "https://api.example.com"
val path = "/users"
val userId = "123"

// 잘못된 예시들
val wrongUrl1 = baseUrl + path + userId          // 슬래시 누락
val wrongUrl2 = "$baseUrl$path/$userId?"         // 물음표만 있고 파라미터 없음
val wrongUrl3 = "$baseUrl$path/$userId?&param=value"  // 잘못된 파라미터 구분자
```

### 3. 보안 취약점
```kotlin
// SQL Injection과 유사한 위험
val userInput = "123; DROP TABLE users;"
val dangerousUrl = "https://api.example.com/users/$userInput"
// 결과: 예상치 못한 동작 가능
```

### 4. 파싱의 어려움
```kotlin
// 하드코딩된 문자열에서 파라미터 추출 - 복잡하고 오류 발생 가능
val urlString = "https://example.com/users/123?active=true&role=admin"

// 수동으로 파싱해야 함
val queryIndex = urlString.indexOf("?")
val queryString = urlString.substring(queryIndex + 1)
val params = queryString.split("&")
// ... 복잡한 파싱 로직
```

### 5. 국제화 문제
```kotlin
// 한글이나 특수문자가 포함된 경우
val koreanText = "안녕하세요"
val unsafeUrl = "https://api.example.com/search?q=$koreanText"
// 결과: 깨진 URL 또는 서버 오류
```

## 실제 사용 예시 비교

### 하드코딩 String 방식 (위험)
```kotlin
fun buildSearchUrl(query: String, page: Int, size: Int): String {
    // 실수하기 쉬운 코드
    return "https://api.example.com/search?q=$query&page=$page&size=$size"
    // 문제점:
    // 1. query에 특수문자가 있으면 URL이 깨짐
    // 2. 파라미터 순서 변경 시 실수 가능
    // 3. 필수/선택 파라미터 구분 어려움
}
```

### Uri 방식 (안전)
```kotlin
fun buildSearchUrl(query: String, page: Int, size: Int): Uri {
    return Uri.Builder()
        .scheme("https")
        .authority("api.example.com")
        .path("/search")
        .appendQueryParameter("q", query)      // 자동 인코딩
        .appendQueryParameter("page", page.toString())
        .appendQueryParameter("size", size.toString())
        .build()
    // 장점:
    // 1. 자동 URL 인코딩
    // 2. 타입 안전성
    // 3. 각 구성 요소 독립적 관리
}
```

## 결론

**Uri 사용을 권장하는 이유:**
- 보안성: 자동 인코딩으로 보안 취약점 방지
- 안정성: 타입 안전성과 구조화된 접근
- 유지보수성: 각 구성 요소를 독립적으로 관리
- 가독성: 명확한 의도 표현

**하드코딩 String 사용 시 주의사항:**
- URL 인코딩 필수
- 특수문자 처리 주의
- 파라미터 연결 시 구분자 확인
- 사용자 입력값 검증 필수

안드로이드 개발에서는 항상 `Uri` 클래스를 사용하여 URL을 다루는 것이 안전하고 권장되는 방법입니다.