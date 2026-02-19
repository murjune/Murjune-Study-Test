# Navigation Compose 학습

Jetpack Compose Navigation 2를 학습테스트 방식으로 탐구합니다.

- Test Tool: Robolectric + `createComposeRule()` (JVM 기반, 에뮬레이터 불필요)
- Navigation 버전: 2.9.6
- 공식 문서: https://developer.android.com/develop/ui/compose/navigation

---

## 학습 순서

### 1단계: 기본 컴포넌트 (`basic/`)

NavHost, NavController, composable의 역할과 동작 방식을 학습합니다.

**주요 학습 내용:**
- `rememberNavController()` — 탐색 상태를 관리하는 컨트롤러 생성
- `NavHost` — 탐색 그래프를 정의하고 현재 destination의 UI를 표시
- `composable<T>` — NavHost 내에서 개별 화면을 등록
- `backStackEntry.toRoute<T>()` — 라우트 인자를 타입 안전하게 파싱

```kotlin
@Serializable object Home
@Serializable data class Profile(val id: String)

NavHost(navController, startDestination = Home) {
    composable<Home> {
        HomeScreen(onNavigate = { navController.navigate(Profile("123")) })
    }
    composable<Profile> { backStackEntry ->
        val profile = backStackEntry.toRoute<Profile>()
        ProfileScreen(profile.id)
    }
}
```

**테스트 파일:** `NavHostTest.kt`, `TypeSafeNavigationTest.kt`

---

### 2단계: Type-Safe Navigation (`basic/`)

문자열 기반 라우트의 문제점과 Kotlin Serializable 기반 타입 안전 Navigation을 학습합니다.

**주요 학습 내용:**
- `@Serializable` 라우트 정의 — 컴파일 타임에 타입 검증
- 이전 방식(문자열) vs 현재 방식(타입) 비교
- 복잡한 데이터는 ID만 전달 → ViewModel에서 로드하는 패턴

```kotlin
// 이전: 문자열 기반 (런타임 오류 위험)
navController.navigate("profile/$userId")

// 현재: 타입 기반 (컴파일 타임 검증)
navController.navigate(Profile(id = "user123"))
```

**핵심 포인트:**
- 라우트를 `@Serializable data class` 또는 `@Serializable object`로 정의
- 인자는 자동 직렬화/역직렬화

---

### 3단계: 백스택 동작 (`backstack/`)

`popBackStack()`과 `navigateUp()`의 차이, `popUpTo`의 활용을 학습합니다.

**popBackStack vs navigateUp 비교:**

| | `popBackStack()` | `navigateUp()` |
|--|--|--|
| 동작 | 백스택에서 명시적 항목 제거 | 상위 화면으로 이동 |
| 반환값 | Boolean (성공 여부) | Boolean |
| 주 용도 | 프로그래밍적 제어 | AppBar 뒤로 버튼 연동 |
| 백스택 빈 경우 | false 반환 | Activity 종료 시도 |

**popUpTo 시나리오:**

```kotlin
// 로그인 완료 후 Login 화면 백스택에서 제거
navController.navigate(Home) {
    popUpTo(Login) { inclusive = true }
}

// 홈 화면 중복 방지
navController.navigate(Home) {
    popUpTo(Home) { inclusive = true }
    launchSingleTop = true
}
```

**`inclusive` 의미:**
- `true` — popUpTo 대상 화면 포함하여 제거
- `false` — popUpTo 대상 화면은 유지, 그 위만 제거

**테스트 파일:** `PopBackStackTest.kt`, `PopUpToTest.kt`

---

### 4단계: DeepLink / WebLink (`deeplink/`)

외부에서 앱의 특정 화면으로 진입하는 DeepLink와 WebLink를 학습합니다.

**주요 학습 내용:**
- `navDeepLink<T>(basePath = "...")` — 타입 안전 딥링크 설정
- AndroidManifest `intent-filter` 설정 (WebLink용)
- `TaskStackBuilder`로 PendingIntent 생성 (알림 등에서 활용)
- AppLink는 실제 도메인 필요 → 이번 학습에서는 제외

```kotlin
composable<Profile>(
    deepLinks = listOf(
        navDeepLink<Profile>(basePath = "https://example.com/profile")
    )
) { backStackEntry ->
    val profile = backStackEntry.toRoute<Profile>()
    ProfileScreen(profile.id)
}
```

**AndroidManifest 설정:**
```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="https" android:host="example.com" />
</intent-filter>
```

**테스트 파일:** `DeepLinkTest.kt`

---

## 테스트 실행

```bash
# Navigation 전체 테스트 (Robolectric - JVM)
./gradlew :compose-practice:test

# 특정 테스트 클래스 실행
./gradlew :compose-practice:test --tests "*.NavHostTest"
./gradlew :compose-practice:test --tests "*.PopBackStackTest"
./gradlew :compose-practice:test --tests "*.DeepLinkTest"
```

---

## 핵심 정리

1. **NavHost** — 탐색 그래프 컨테이너, 현재 destination UI를 표시
2. **NavController** — navigate(), popBackStack() 등 탐색 동작 제어
3. **Type-Safe Route** — `@Serializable` 클래스로 컴파일 타임 안전성 확보
4. **popBackStack** — 명시적 백스택 제어 (프로그래밍적)
5. **navigateUp** — UX 일관성 있는 뒤로가기 (AppBar 뒤로 버튼용)
6. **popUpTo** — 특정 depth까지 백스택 정리 (로그인 플로우 등)
7. **DeepLink** — 외부 URI로 특정 화면 직접 진입

---

## 참고 링크

- [Navigation Compose 공식 문서](https://developer.android.com/develop/ui/compose/navigation)
- [Type-Safe Navigation 공식 문서](https://developer.android.com/guide/navigation/design/type-safety)
- [Navigation Testing 공식 문서](https://developer.android.com/guide/navigation/testing)
- [NowInAndroid Navigation 구현](https://github.com/android/nowinandroid/tree/main/app/src/main/kotlin/com/google/samples/apps/nowinandroid/navigation)

---

## 다음 학습

- [ ] Navigation 3 (현재 알파) — Scene, NavBackStack API 변화 학습
