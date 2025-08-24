# Android Uri 학습 테스트

Android의 `android.net.Uri` 클래스를 학습하고 정리한 테스트 코드와 문서 모음입니다.

## 📚 학습 내용

### 1. Uri 파싱 ([UriParseTest.kt](./UriParseTest.kt))

Uri를 문자열로부터 파싱하고 각 구성 요소를 추출하는 방법을 학습합니다.

**주요 학습 내용:**
- `Uri.parse()`로 문자열 URI 파싱
- scheme, authority, host, port, path 추출
- query parameter 추출 (`getQueryParameter()`)
- path segment 추출 (`lastPathSegment`)
- 인코딩/디코딩 처리 (`encodedPath`, `encodedAuthority`)

```kotlin
val uri = Uri.parse("https://example.com:8080/path?param=value")
uri.scheme       // "https"
uri.host         // "example.com"
uri.port         // 8080
uri.path         // "/path"
uri.getQueryParameter("param")  // "value"
```

### 2. Uri 생성 ([UriBuilderTest.kt](./UriBuilderTest.kt))

`Uri.Builder`를 사용하여 안전하게 URI를 구성하는 방법을 학습합니다.

**주요 학습 내용:**
- `Uri.Builder()`로 처음부터 URI 구성
- `appendPath()`로 경로 단계별 추가
- `appendQueryParameter()`로 쿼리 파라미터 추가
- 특수문자 자동 인코딩
- `buildUpon()`으로 기존 URI 수정

```kotlin
val uri = Uri.Builder()
    .scheme("https")
    .authority("api.example.com")
    .path("/v1/users")
    .appendQueryParameter("page", "1")
    .appendQueryParameter("size", "10")
    .build()
// 결과: https://api.example.com/v1/users?page=1&size=10
```

**핵심 포인트:**
- 특수문자가 자동으로 URL 인코딩됨
- `buildUpon()`으로 기존 URI의 일부만 수정 가능 (경로, 호스트, 쿼리 파라미터 등)

### 3. UriMatcher ([UriMatcherTest.kt](./UriMatcherTest.kt))

`ContentProvider`에서 받은 URI의 종류를 구분하는 `UriMatcher`를 학습합니다.

**주요 학습 내용:**
- URI 패턴을 숫자 코드로 매핑
- `#`: 숫자 와일드카드 (예: `/books/#` → `/books/123`)
- `*`: 문자열 와일드카드 (예: `/books/*` → `/books/fiction`)
- 복잡한 URI 패턴 매칭 (예: `/books/#/reviews/#`)

```kotlin
val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
uriMatcher.addURI("com.example.provider", "books", 100)      // 전체 책 목록
uriMatcher.addURI("com.example.provider", "books/#", 101)    // 특정 책
uriMatcher.addURI("com.example.provider", "books/#/reviews", 102)  // 특정 책의 리뷰들

// 매칭 테스트
uriMatcher.match(Uri.parse("content://com.example.provider/books"))     // 100
uriMatcher.match(Uri.parse("content://com.example.provider/books/42"))  // 101
```

**실무 활용:**
`ContentProvider`에서 `query()`, `insert()`, `update()`, `delete()` 메서드에서 URI 종류에 따라 다른 동작을 수행할 때 사용합니다.

### 4. File URI vs Content URI ([FileAndContentUriTest.kt](./FileAndContentUriTest.kt))

파일 시스템 접근과 ContentProvider 접근의 차이를 학습합니다.

**File URI:**
- 형태: `file://` 스키마
- 접근: 파일 시스템 직접 접근
- 보안: 파일 경로 노출, 보안상 취약
- 예시: `file:///storage/emulated/0/Pictures/image.jpg`
- 주의: Android 7.0+ 에서 File URI 공유 시 `FileUriExposedException` 발생

```kotlin
val fileUri = Uri.fromFile(File("/storage/emulated/0/Documents/test.txt"))
// file:///storage/emulated/0/Documents/test.txt
```

**Content URI:**
- 형태: `content://` 스키마
- 접근: ContentProvider를 통한 간접 접근
- 보안: 권한 기반 접근 제어, 더 안전
- 예시: `content://media/external/images/media/123`

```kotlin
val uri = Uri.parse("content://com.example.provider/items/123")
uri.scheme       // "content"
uri.authority    // "com.example.provider"
uri.path         // "/items/123"
uri.lastPathSegment  // "123"
```

## 📝 학습 문서

- [Uri vs 하드코딩 String](./docs/Uri-vs-String.md) - 왜 문자열 대신 Uri를 사용해야 하는지
- [java.net.URI vs android.net.Uri](./docs/JavaURI-vs-AndroidUri.md) - Java URI와 Android Uri의 차이점 비교
- [Robolectric with Kotest](./docs/robolectric-with-kotest.md) - Kotest와 Robolectric 연동 이슈
- [Uri 사용에 대한 개인적인 생각](./docs/uri-usage.md) - 아키텍처 관점에서의 Uri 사용 원칙

## 🧪 테스트 실행

```bash
# 전체 Uri 테스트 실행
./gradlew :android-libs-practice:test

# 특정 테스트 클래스 실행
./gradlew :android-libs-practice:test --tests "*.UriParseTest"
./gradlew :android-libs-practice:test --tests "*.UriBuilderTest"
./gradlew :android-libs-practice:test --tests "*.UriMatcherTest"
./gradlew :android-libs-practice:test --tests "*.FileAndContentUriTest"
```

## 📌 핵심 정리

1. **Uri 파싱**: `Uri.parse()`로 문자열을 파싱하고 각 구성 요소 추출
2. **Uri 생성**: `Uri.Builder()`로 안전하게 URI 구성, 특수문자 자동 인코딩
3. **UriMatcher**: ContentProvider에서 URI 패턴 매칭용
4. **File vs Content URI**: Content URI가 더 안전, File URI는 Android 7.0+에서 제한
5. **하드코딩 String 금지**: 항상 Uri 클래스 사용하여 안전성 확보
6. **아키텍처**: UI Layer에서 Uri 처리, Domain Layer에는 primitive 타입 전달

## 🔗 첨부 링크

- [Android Uri 공식 문서](https://developer.android.com/reference/android/net/Uri)
- [UriMatcher 공식 문서](https://developer.android.com/reference/android/content/UriMatcher)
- [Kotest Robolectric 문서](https://kotest.io/docs/5.4.x/extensions/robolectric.html)
- [Android Uri 소스 코드](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/tests/coretests/src/android/net/UriTest.java)
