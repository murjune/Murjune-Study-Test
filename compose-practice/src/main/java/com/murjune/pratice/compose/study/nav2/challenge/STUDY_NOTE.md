# Navigation Challenge 학습 노트

## currentDestination 안전하게 가져오기 패턴

### 문제: NavController는 Compose 상태가 아님

```kotlin
// Compose에서 이렇게 쓰면 안 됨
navController.currentDestination
```
- NavController는 상태 객체가 아니라 **변경 감지가 안 됨** (recomposition 안 됨)

### 해결: Flow + 이전값 캐싱

```kotlin
@Stable
data class NavigationChallengeStates(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            val currentEntry = navController.currentBackStackEntryFlow
                .collectAsState(initial = null)

            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }
}
```

### 왜 `previousDestination` 캐싱이 필요한가?

Navigation 전환 순간에 destination이 **잠깐 null이 되는 타이밍**이 존재함:
- 그래프 초기화 중
- recomposition 타이밍
- back stack 변경 중

```
화면 A → B 이동 시 내부 흐름:
t0: destination = A
t1: destination = null   ← 전환 중 (순간적)
t2: destination = B
```

| | 캐싱 없음 | 캐싱 있음 |
|---|---|---|
| 흐름 | A → null → B | A → A → B |
| 결과 | UI 깜빡임, 탭 상태 튐 | 안정적 |

### 핵심 포인트

- `currentBackStackEntryFlow`: 현재 화면 변경을 Compose에서 관찰 가능한 Flow
- `collectAsState(initial = null)`: Flow → Compose State 변환, 초기값 null
- `also` 블록: 현재 값이 있으면 저장
- `?: previousDestination.value`: 현재 값이 null이면 마지막 정상값 반환
- `previousDestination`이 `MutableState`인 이유: Compose recomposition 트리거

---

## hierarchy와 hasRoute

### `NavDestination.hierarchy`

```kotlin
public val NavDestination.hierarchy: Sequence<NavDestination>
    get() = generateSequence(this) { it.parent }
```
- 현재 destination부터 부모 NavGraph를 거슬러 올라가는 시퀀스
- 예: `ProductDetail → HomeNavGraph → RootGraph`

### `NavDestination.hasRoute<T>()`

```kotlin
public inline fun <reified T : Any> NavDestination.hasRoute(): Boolean =
    hasRoute(T::class)

public fun <T : Any> NavDestination.hasRoute(route: KClass<T>): Boolean =
    route.serializer().generateHashCode() == id
```
- `@Serializable` Route의 serializer 해시코드와 destination id를 비교
- 정확히 해당 Route 타입인지 판별

### 탭 판별 시 hierarchy가 필요한 이유

```kotlin
// hasRoute만 쓰면: 탭 화면 자체에서만 동작
currentDestination?.hasRoute<HomeRoute>() == true
// HomeScreen에서만 true, ProductDetail에서는 false!

// hierarchy + hasRoute: 하위 화면에서도 동작
currentDestination?.hierarchy?.any { it.hasRoute<HomeRoute>() } == true
// ProductDetail → HomeNavGraph에 HomeRoute 있으므로 true!
```

| 현재 화면 | hasRoute만 | hierarchy + hasRoute |
|-----------|-----------|---------------------|
| HomeScreen | true | true |
| ProductDetail | false | true (부모 탐색) |
| Review | false | true (부모 탐색) |

**전제조건**: Home 하위 화면들이 Home 중첩 NavGraph 안에 있어야 함

---

## NIA의 baseRoute 패턴 (PR #1902)

### 문제: 중첩 NavGraph에서 탭 선택 상태가 풀림

ForYou 탭 안에 ForYouScreen + TopicScreen이 같은 NavGraph에 있는 구조:

```kotlin
@Serializable data object ForYouRoute       // 실제 ForYou 화면
@Serializable data object ForYouBaseRoute    // 중첩 NavGraph의 루트

fun NavGraphBuilder.forYouSection(...) {
    navigation<ForYouBaseRoute>(startDestination = ForYouRoute) {
        composable<ForYouRoute> { ForYouScreen(...) }
        topicDestination()  // Topic 화면도 이 그래프 안에 포함
    }
}
```

ForYou 탭에서 Topic 화면으로 들어가면 `currentDestination`이 `TopicRoute`이므로:
- `ForYouRoute`로만 비교 → **ForYou 탭 선택 해제됨!**
- `ForYouBaseRoute`로 hierarchy 비교 → **ForYou 탭 선택 유지!**

### 해결: route(실제 화면)와 baseRoute(NavGraph 루트) 분리

```kotlin
enum class TopLevelDestination(
    val route: KClass<*>,              // 실제 화면
    val baseRoute: KClass<*> = route,  // NavGraph 루트 (기본값: route와 동일)
) {
    FOR_YOU(
        route = ForYouRoute::class,
        baseRoute = ForYouBaseRoute::class,  // 중첩 NavGraph가 있으니 별도 지정
    ),
    BOOKMARKS(
        route = BookmarksRoute::class,
        // baseRoute = route (중첩 없으니 기본값 사용)
    ),
}
```

```kotlin
// BottomBar 선택 판별
val selected = currentDestination.isRouteInHierarchy(destination.baseRoute)

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any { it.hasRoute(route) } ?: false
```

| 현재 화면 | `route`(ForYouRoute) 비교 | `baseRoute`(ForYouBaseRoute) 비교 |
|-----------|--------------------------|----------------------------------|
| ForYouScreen | O | O |
| TopicScreen | X | **O (hierarchy로 부모 탐색)** |

### Navigation 3에서의 변화

Nav 3 마이그레이션(PR #1902)에서 `baseRoute` 제거 → `key: NiaNavKey`로 교체:
```kotlin
// Nav 2: hierarchy 기반 판별
val selected = currentDestination.isRouteInHierarchy(destination.baseRoute)

// Nav 3: 자체 key 관리
val selected = destination.key == currentTopLevelKey
```

Nav 3에서는 `NavDestination.hierarchy` 대신 자체적으로 `currentTopLevelKey`를 관리하므로 `baseRoute`가 불필요해짐.

---

## 백스택은 NavGraph별로 분리되지 않는다

### 핵심: 백스택은 NavController당 하나

NavGraph 단위로 백스택이 분리되는 게 아니라, **하나의 백스택**에 모든 entry가 쌓임.

```
단일 백스택:
[HomeNavGraph/HomeScreen] → [HomeNavGraph/ProductDetail] → [HomeNavGraph/Review]
```

### 탭 전환 시 상태가 보존되는 원리: saveState/restoreState

```kotlin
navOptions {
    popUpTo(startDestination) { saveState = true }  // ① 현재 탭 백스택을 Bundle에 저장 후 pop
    launchSingleTop = true
    restoreState = true  // ② 이동할 탭의 이전 저장된 스냅샷을 복원
}
```

**Home에서 ProductDetail 보고 있다가 Cart 탭 클릭:**
```
① saveState=true → Home 탭의 [HomeScreen → ProductDetail] 상태를 메모리에 저장
② popUpTo(startDestination) → 백스택에서 Home 관련 entry 제거
③ CartNavGraph로 navigate
④ restoreState=true → Cart 탭에 이전 저장된 상태가 있으면 복원
```

**다시 Home 탭 클릭:**
```
① saveState=true → Cart 탭 상태 저장
② popUpTo(startDestination) → Cart 관련 entry 제거
③ HomeNavGraph로 navigate
④ restoreState=true → 저장해뒀던 [HomeScreen → ProductDetail] 복원!
```

### 왜 NavGraph 루트로 navigate해야 하나?

`saveState/restoreState`는 **NavGraph 단위로 스냅샷을 매칭**한다.

```kotlin
// ❌ 개별 Screen으로 navigate → 저장된 스냅샷과 매칭 안 됨
navController.navigate(MyRoute.MyScreen, navOptions)  // My 탭 백스택 초기화됨!

// ✅ NavGraph 루트로 navigate → 스냅샷 매칭 성공
navController.navigate(MyNavGraph, navOptions)  // My 탭 백스택 정상 복원!
```

---

## popUpTo 동작 원리와 함정

### 기본 동작

`popUpTo<Route>`는 백스택에서 `Route`를 찾아서 **그 위의 모든 entry를 pop**한다.

### 함정: 타겟이 백스택에 없으면 아무 일도 안 일어남

```kotlin
// Home 탭에서 ProductDetail 보는 중
// 백스택: [HomeNavGraph/HomeScreen] → [HomeNavGraph/ProductDetail]

fun NavController.navigateToCartFromProduct() {
    navigate(CartRoute.CartScreen) {
        popUpTo<CartRoute.CartScreen> { inclusive = false }  // ❌ 백스택에 CartScreen 없음!
    }
}
// 결과: 아무것도 pop 안 함 → ProductDetail 위에 CartScreen 쌓임
// 백스택: [HomeNavGraph/HomeScreen] → [HomeNavGraph/ProductDetail] → [CartScreen]
// → 이 상태에서 Back/Home탭 클릭 = 먹통
```

### 올바른 방법

```kotlin
// 방법 1: HomeNavGraph까지 pop (하위 화면 정리)
navController.navigateToCart(navOptions = navOptions {
    popUpTo(HomeNavGraph) {}  // HomeNavGraph는 유지, 그 위의 ProductDetail만 제거
})
// 백스택: [HomeNavGraph/HomeScreen] → [CartScreen]
// Back 누르면 HomeScreen으로 돌아감

// 방법 2: 탭 전환 방식 사용 (saveState/restoreState)
appBarNavigator.navigateToBottomNavDestination(BottomNavDestination.Cart)
// Home 탭 상태 저장 후 Cart 탭으로 전환
```

### 먹통 원인 상세 분석

백스택이 꼬인 상태(`[HomeNavGraph/HomeScreen → ProductDetail → CartScreen]`)에서:

1. **Back 버튼**: Cart의 `BackHandler`가 `navigateToBottomNavDestination(Home)` 호출
2. 이 함수는 `popUpTo(startDestination) { saveState = true } + launchSingleTop = true`
3. `startDestination`은 HomeNavGraph인데, 이미 스택 바닥에 있음
4. `launchSingleTop = true` → 이미 있으니 새로 안 만듦
5. 꼬여있는 ProductDetail, CartScreen은 HomeNavGraph 바깥이라 정리 안 됨
6. → **아무 일도 안 일어남 = 먹통**

---

## navDeepLink & NavDeepLinkDslBuilder

### 기본 형태 (수동 URI 패턴)

```kotlin
navDeepLink {
    uriPattern = "https://example.com/profile/{userId}"
    action = "android.intent.action.VIEW"  // 선택
    mimeType = "text/plain"                // 선택
}
```

`NavDeepLinkDslBuilder`의 3가지 프로퍼티:
- `uriPattern` — URI 패턴 (`{arg}`로 인자 추출)
- `action` — Intent action (빈 문자열 불가)
- `mimeType` — MIME 타입 (컨텐츠 타입 기반 매칭, `ACTION_SEND`/`ACTION_VIEW`에서 사용)

**셋 중 최소 하나는 필수.**

### Type-Safe 형태 (reified)

```kotlin
navDeepLink<ProductDetail>(basePath = "https://example.com/product")
```

내부적으로 `@Serializable` 클래스의 serializer로 필드를 추출해서 URI 패턴을 **자동 생성**:

```
basePath + /{필수인자} + ?선택인자={선택인자}
```

```kotlin
@Serializable data class ProductDetail(val productId: Int)
// → "https://example.com/product/{productId}"

@Serializable data class Search(val query: String, val page: Int = 1)
// → "https://example.com/search/{query}?page={page}"
//   page는 기본값이 있으니까 query parameter로 감
```

### 필수 인자가 여러 개인 경우

기본값 없는 필드들은 **선언 순서대로 path segment**로 이어붙음:

```kotlin
@Serializable
data class ProductDetail(
    val productId: Int,     // 필수 → path
    val color: String,      // 필수 → path
)
// → "example.com/product/{productId}/{color}"
// → example.com/product/42/red
```

필수 + 선택 혼합:
```kotlin
@Serializable
data class ProductDetail(
    val productId: Int,              // 필수 → path
    val color: String,               // 필수 → path
    val referrer: String = "direct", // 기본값 → query
    val page: Int = 1,               // 기본값 → query
)
// → "example.com/product/{productId}/{color}?referrer={referrer}&page={page}"
// → example.com/product/42/red?referrer=banner&page=3
```

| 조건 | 위치 | 형태 |
|------|------|------|
| 기본값 없음 | path | `/{arg}` (선언 순서) |
| 기본값 있음 | query | `?arg={arg}` |
| CollectionNavType | query | `?arg={arg}` |

추가 커스텀도 가능:
```kotlin
navDeepLink<ProductDetail>(basePath = "https://example.com/product") {
    action = "android.intent.action.VIEW"
}
```

### mimeType 활용 예시

URI가 아닌 **컨텐츠 타입** 기반 DeepLink:

```kotlin
// 다른 앱에서 텍스트 공유 시 우리 앱에서 받기
navDeepLink {
    mimeType = "text/plain"
    action = Intent.ACTION_SEND
}

// 이미지 파일 열기
navDeepLink {
    mimeType = "image/*"
    action = Intent.ACTION_VIEW
}
```

일반적인 URL DeepLink에서는 거의 안 씀.

---

## DeepLink와 백스택 자동 빌드

### DeepLink 진입 시 백스택은 자동 구성됨

`navDeepLink`를 Profile에 등록하면, Navigation은 **startDestination을 백스택 바닥에 깔고** 타겟을 그 위에 쌓음:

```
DeepLink로 Profile 직접 진입 시:
백스택: [StartDestination] → [Profile]
← back 누르면 StartDestination으로 감
```

별도 코드 없이 동작. 중첩 NavGraph의 경우 중간 NavGraph의 startDestination도 자동 추가됨.

---

## DeepLink scheme 선택

### https scheme의 함정 (Android 12+)

Android 12+에서 `https` scheme은 **App Links 인증**(`.well-known/assetlinks.json`)이 없으면 앱이 아닌 **브라우저로 열림**.

### 해결: 커스텀 scheme + https 병행

```kotlin
// navDeepLink에 두 scheme 모두 등록
composable<ProductDetail>(
    deepLinks = listOf(
        navDeepLink<ProductDetail>(basePath = "app://study.example.com/product"),    // 커스텀
        navDeepLink<ProductDetail>(basePath = "https://study.example.com/product"),  // https
    ),
)
```

```xml
<!-- AndroidManifest.xml -->
<!-- 커스텀 scheme: adb에서 바로 동작 -->
<intent-filter>
    <data android:scheme="app" android:host="study.example.com" android:pathPrefix="/product" />
</intent-filter>
<!-- https scheme: autoVerify=false로 앱 선택 다이얼로그 표시 -->
<intent-filter android:autoVerify="false">
    <data android:scheme="https" android:host="study.example.com" android:pathPrefix="/product" />
</intent-filter>
```

```bash
# 커스텀 scheme — 바로 앱으로 열림
adb shell am start -a android.intent.action.VIEW -d "app://study.example.com/product/42"

# https scheme — 앱 선택 다이얼로그
adb shell am start -a android.intent.action.VIEW -d "https://study.example.com/product/42"
```

### adb로 https DeepLink 테스트하기

패키지명을 명시하면 브라우저를 우회하고 **직접 앱으로 Intent 전달** 가능:

```bash
adb shell am start \
  -a android.intent.action.VIEW \
  -d "https://study.example.com/product/42" \
  com.murjune.pratice.compose.study
```

이것이 사실상 App Links 인증이 하는 일을 수동으로 하는 것.

### App Links 없이 웹에서 앱 열기

브라우저에서 앱으로 전환하려면 서버가 **Intent redirect**를 내려줘야 함:

```html
<a href="intent://study.example.com/product/42#Intent;scheme=app;package=com.murjune.pratice.compose.study;end">
  앱에서 열기
</a>
```

실제 서버가 필요하므로 학습용으로는 커스텀 scheme(`app://`) 사용이 적합.

### DeepLink scheme 비교

| | Android 11 이하 | Android 12+ |
|---|---|---|
| https + App Links 인증 성공 | 바로 앱 | 바로 앱 |
| https + 인증 실패/없음 | 앱 선택 다이얼로그 | **브라우저로 직행** |
| 커스텀 scheme (app://) | 바로 앱 | 바로 앱 |

### NavHost 이중 중첩과 DeepLink

외부 NavHost가 Intent를 가로채서 내부 NavHost의 DeepLink가 동작하지 않음.
**→ DeepLink가 필요한 NavHost는 별도 Activity로 분리하는 것이 정석.**
