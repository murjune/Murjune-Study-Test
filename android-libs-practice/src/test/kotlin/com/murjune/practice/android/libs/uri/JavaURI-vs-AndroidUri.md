# java.net.URI vs android.net.Uri 비교

## 개요
 
URI를 다루는 두 가지 주요 클래스의 차이점과 사용법을 비교합니다.

## 기본 정보

### java.net.URI
- **소속**: Java 표준 라이브러리 (Java SE)
- **플랫폼**: 모든 JVM 환경
- **표준**: RFC 3986 URI 표준 준수
- **가변성**: 불변(immutable) 클래스
- **테스트**: 일반 JUnit 테스트에서 바로 사용 가능

### android.net.Uri
- **소속**: Android Framework
- **플랫폼**: Android 환경 전용
- **표준**: RFC 3986 + Android 확장
- **가변성**: 불변(immutable) 클래스
- **테스트**: Robolectric 또는 Android Test 환경 필요

## 생성 방식 비교

### java.net.URI
```kotlin
// 방법 1: 문자열에서 생성
val uri1 = URI.create("https://example.com/path?param=value")

// 방법 2: 구성 요소별로 생성
val uri2 = URI("https", "example.com", "/path", "param=value", null)
//          (scheme, authority, path, query, fragment)

// 방법 3: 더 상세한 생성
val uri3 = URI("https", "user:pass", "example.com", 8080, "/path", "param=value", "section")
//          (scheme, userInfo, host, port, path, query, fragment)
```

### android.net.Uri
```kotlin
// 방법 1: 문자열에서 파싱
val uri1 = Uri.parse("https://example.com/path?param=value")

// 방법 2: Builder 패턴 (권장)
val uri2 = Uri.Builder()
    .scheme("https")
    .authority("example.com")
    .path("/path")
    .appendQueryParameter("param", "value")
    .build()

// 방법 3: 파일에서 생성
val uri3 = Uri.fromFile(File("/path/to/file"))

// 방법 4: 리소스에서 생성
val uri4 = Uri.parse("android.resource://com.example.app/drawable/icon")
```

## API 차이점

### 기본 속성 접근
```kotlin
val url = "https://user:pass@example.com:8080/path/to/resource?param=value&foo=bar#section"

// java.net.URI
val javaUri = URI.create(url)
javaUri.scheme       // "https"
javaUri.userInfo     // "user:pass"
javaUri.host         // "example.com"
javaUri.port         // 8080
javaUri.path         // "/path/to/resource"
javaUri.query        // "param=value&foo=bar"
javaUri.fragment     // "section"

// android.net.Uri
val androidUri = Uri.parse(url)
androidUri.scheme        // "https"
androidUri.userInfo      // "user:pass"
androidUri.host          // "example.com"
androidUri.port          // 8080
androidUri.path          // "/path/to/resource"
androidUri.query         // "param=value&foo=bar"
androidUri.fragment      // "section"
```

### Query Parameter 처리 - 가장 큰 차이점

```kotlin
val url = "https://api.example.com/users?page=1&size=10&sort=name&active=true"

// java.net.URI - 수동 파싱 필요
val javaUri = URI.create(url)
val query = javaUri.query  // "page=1&size=10&sort=name&active=true"

// 개별 파라미터 추출을 위한 수동 파싱
fun getQueryParameter(uri: URI, name: String): String? {
    return uri.query?.split("&")
        ?.find { it.startsWith("$name=") }
        ?.substringAfter("=")
}

val page = getQueryParameter(javaUri, "page")  // "1"
val size = getQueryParameter(javaUri, "size")  // "10"

// android.net.Uri - 직접 접근 가능
val androidUri = Uri.parse(url)
val page = androidUri.getQueryParameter("page")      // "1"
val size = androidUri.getQueryParameter("size")      // "10"
val sort = androidUri.getQueryParameter("sort")      // "name"
val active = androidUri.getQueryParameter("active")  // "true"

// 모든 파라미터 이름 조회
val allParams = androidUri.queryParameterNames  // {"page", "size", "sort", "active"}

// 같은 이름의 여러 파라미터 처리
val categories = androidUri.getQueryParameters("category")  // List<String>
```

## Builder 패턴 비교

### java.net.URI - Builder 없음
```kotlin
// 복잡한 URI 생성이 어려움
val javaUri = URI("https", "api.example.com", "/v1/users", 
                  "page=1&size=10&sort=name%2Casc", null)

// 또는 문자열 조합 (위험함)
val baseUrl = "https://api.example.com"
val path = "/v1/users"
val query = "page=1&size=10&sort=name%2Casc"  // 수동 인코딩 필요
val javaUri2 = URI.create("$baseUrl$path?$query")
```

### android.net.Uri - Builder 패턴 지원
```kotlin
// 단계별로 안전하게 구성
val androidUri = Uri.Builder()
    .scheme("https")
    .authority("api.example.com")
    .path("/v1/users")
    .appendQueryParameter("page", "1")
    .appendQueryParameter("size", "10")
    .appendQueryParameter("sort", "name,asc")  // 자동 인코딩
    .build()

// 경로 세그먼트 추가
val userUri = Uri.Builder()
    .scheme("https")
    .authority("api.example.com")
    .appendPath("v1")
    .appendPath("users")
    .appendPath("123")
    .appendQueryParameter("include", "profile")
    .build()
// 결과: https://api.example.com/v1/users/123?include=profile
```

## 특수 기능 비교

### Path 세그먼트 처리
```kotlin
val url = "https://api.example.com/v1/users/123/posts/456"

// java.net.URI
val javaUri = URI.create(url)
val path = javaUri.path  // "/v1/users/123/posts/456"
val segments = path.split("/").filter { it.isNotEmpty() }  // 수동 분할
val userId = segments[2]  // "123"
val postId = segments[4]  // "456"

// android.net.Uri
val androidUri = Uri.parse(url)
val pathSegments = androidUri.pathSegments  // ["v1", "users", "123", "posts", "456"]
val userId = pathSegments[2]  // "123"
val postId = pathSegments[4]  // "456"
val lastSegment = androidUri.lastPathSegment  // "456"
```

### 인코딩 처리
```kotlin
val specialChars = "Hello World & 안녕하세요"

// java.net.URI - 수동 인코딩
val encoded = URLEncoder.encode(specialChars, "UTF-8")
val javaUri = URI.create("https://example.com/search?q=$encoded")

// android.net.Uri - 자동 인코딩
val androidUri = Uri.Builder()
    .scheme("https")
    .authority("example.com")
    .path("/search")
    .appendQueryParameter("q", specialChars)  // 자동 인코딩
    .build()

// 결과는 동일: https://example.com/search?q=Hello%20World%20%26%20%EC%95%88%EB%85%95%ED%95%98%EC%84%B8%EC%9A%94
```

## Android 특화 기능

### android.net.Uri만 가능한 기능
```kotlin
// 1. Content URI
val contentUri = Uri.parse("content://com.example.provider/items/123")
contentUri.authority  // "com.example.provider"

// 2. 파일 URI
val fileUri = Uri.fromFile(File("/sdcard/photo.jpg"))
fileUri.scheme  // "file"

// 3. 리소스 URI
val resourceUri = Uri.parse("android.resource://com.example.app/drawable/icon")

// 4. 전화번호 URI
val phoneUri = Uri.parse("tel:+821234567890")

// 5. 이메일 URI
val emailUri = Uri.parse("mailto:user@example.com")

// 6. 지도 URI
val mapUri = Uri.parse("geo:37.7749,-122.4194")
```

## 테스트 환경 차이

### java.net.URI 테스트
```kotlin
// 일반 JUnit 테스트
class JavaUriTest {
    @Test
    fun `URI 파싱 테스트`() {
        val uri = URI.create("https://example.com/path?param=value")
        assertEquals("https", uri.scheme)
        assertEquals("example.com", uri.host)
        assertEquals("/path", uri.path)
        assertEquals("param=value", uri.query)
    }
}
```

### android.net.Uri 테스트
```kotlin
// Robolectric 필요
@RunWith(RobolectricTestRunner::class)
class AndroidUriTest {
    @Test
    fun `Uri 파싱 테스트`() {
        val uri = Uri.parse("https://example.com/path?param=value")
        assertEquals("https", uri.scheme)
        assertEquals("example.com", uri.host)
        assertEquals("/path", uri.path)
        assertEquals("value", uri.getQueryParameter("param"))
    }
}
```

## 성능 비교

### 메모리 사용량
- **java.net.URI**: 약간 더 가벼움
- **android.net.Uri**: 추가 기능으로 인해 약간 더 무거움

### 파싱 성능
- **java.net.URI**: 표준 파싱, 빠름
- **android.net.Uri**: 추가 기능 파싱, 약간 느림

## 선택 기준

### java.net.URI를 선택하는 경우
- 순수 JVM 환경 (서버, 데스크톱)
- 단순한 URI 파싱만 필요
- 테스트 환경에서 Robolectric 사용 불가
- 메모리 사용량 최소화 필요

### android.net.Uri를 선택하는 경우
- Android 앱 개발
- Query Parameter 조작이 많은 경우
- Builder 패턴으로 안전하게 URI 구성
- Content URI, 파일 URI 등 Android 특화 기능 사용
- 자동 인코딩 기능 활용

## 마이그레이션 가이드

### android.net.Uri → java.net.URI
```kotlin
// Before (android.net.Uri)
val androidUri = Uri.parse(url)
val param = androidUri.getQueryParameter("param")

// After (java.net.URI)
val javaUri = URI.create(url)
val param = javaUri.query?.split("&")
    ?.find { it.startsWith("param=") }
    ?.substringAfter("=")
```

### java.net.URI → android.net.Uri
```kotlin
// Before (java.net.URI)
val javaUri = URI.create(url)
val query = javaUri.query

// After (android.net.Uri)
val androidUri = Uri.parse(url)
val param = androidUri.getQueryParameter("param")
```

## 결론

**`android.net.Uri`**
- 더 편리한 API
- 자동 인코딩/디코딩
- Android 생태계와 완벽한 통합
- Query Parameter 처리의 편의성

**`java.net.URI`**
- 추가 의존성 불필요
- 표준 라이브러리 사용
- 더 가벼운 메모리 사용량
