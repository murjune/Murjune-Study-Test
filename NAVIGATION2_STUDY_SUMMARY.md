# Navigation 2 학습 완료 — 4단계 사이클 요약

## 📚 학습 개요

Jetpack Compose Navigation 2의 공식 문서 → 베스트 프랙티스 → 코딩 챌린지 → 코드 리뷰 사이클을 완료했습니다.

---

## Phase 1: TEACH — 공식 문서 기반 개념 정리

### 1. NavHost — Composable 함수 기반 네비게이션
```kotlin
NavHost(
    navController = navController,
    startDestination = "home",
) {
    composable("home") { HomeScreen() }
    composable("detail/{id}") { backStackEntry ->
        DetailScreen(id = backStackEntry.arguments?.getString("id"))
    }
}
```
- **역할**: 라우트 → Composable 매핑
- **특징**: composable, navigation, dialog 등 여러 destination 타입 지원

### 2. Type-Safe Navigation — Kotlin Serialization 기반
```kotlin
@Serializable
data class DetailRoute(val productId: String)

navController.navigate(DetailRoute(productId = "123"))
```
- **장점**: 컴파일 타임 타입 체크, 자동 직렬화/역직렬화
- **구현**: @Serializable + NavController.navigate<T>()

### 3. BackStack — 화면 스택 관리
```kotlin
// popBackStack: 이전 화면으로 (일반적)
navController.popBackStack()

// navigateUp: 부모 화면으로 (앱 바 '뒤로' 버튼)
navController.navigateUp()

// saveState/restoreState: 상태 보존
navController.navigate("home") {
    popUpTo("home") { saveState = true }
    restoreState = true
}
```
- **popUpTo**: 특정 라우트까지 백스택 제거
- **saveState**: 떠나는 화면의 상태 저장
- **restoreState**: 돌아올 때 상태 복원

### 4. DeepLink — 외부 Intent에서 화면 진입
```kotlin
val deepLinkIntent = Intent(
    Intent.ACTION_VIEW,
    "https://example.com/product/123".toUri(),
    context,
    MainActivity::class.java
)
startActivity(deepLinkIntent)
```
- **문제**: nested NavHost는 외부 NavHost의 Intent를 가로챔
- **해결**: DeepLink 화면은 별도 Activity로 분리

---

## Phase 2: SAMPLE — NowInAndroid 베스트 프랙티스

### Navigation 계층 구조
```
MainActivity
  ├── BottomNavigationBar (Home/Cart/My/More)
  └── NavHost (main)
      ├── HomeNavGraph
      │   ├── HomeScreen
      │   ├── ProductDetailScreen
      │   └── ReviewScreen
      ├── CartNavGraph
      ├── MyNavGraph
      └── MoreNavGraph
```

### SavedStateHandle 패턴
```kotlin
@HiltViewModel
class DetailViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val productId: String? = savedStateHandle["productId"]
    
    // config change, process death 모두 대응
    val productFlow: StateFlow<Product?> = 
        savedStateHandle.getStateFlow("product", null)
}
```
- **특징**: ViewModel ↔ NavController 상태 동기화
- **생존**: config change ✅, process death ✅

### LaunchSingleTop + SaveRestoreState
```kotlin
navController.navigate("home") {
    launchSingleTop = true           // 중복 스택 방지
    popUpTo("home") {
        saveState = true
        inclusive = false
    }
    restoreState = true              // 이전 상태 복원
}
```

---

## Phase 3: CHALLENGE — ShoppingApp 통합 구현

### 구현 범위
| 구성 | 개수 | 예시 |
|------|------|------|
| Screen | 8개 | HomeScreen, ProductDetailScreen, ReviewScreen, CartScreen, ... |
| Navigation | 4개 | HomeNavigation, CartNavigation, MyNavigation, MoreNavigation |
| NavGraph | 4개 | home, cart, my, more |
| BottomBar Tab | 4개 | Home, Cart, My, More |

### 핵심 구현

**1. Type-Safe Route 정의**
```kotlin
@Serializable
data class HomeRoute(val tabIndex: Int = 0)

@Serializable
data class ProductDetailRoute(val productId: String)
```

**2. BottomNavigationBar 상태 관리**
```kotlin
var selectedTab by remember { mutableIntStateOf(0) }

BottomNavigationBar(
    selectedTab = selectedTab,
    onTabSelected = { newTab ->
        selectedTab = newTab
        navController.navigate(routes[newTab])
    }
)
```

**3. Nested NavGraph**
```kotlin
navigation<HomeRoute>(startDestination = HomeScreen) {
    composable<HomeScreen> { HomeScreen() }
    composable<ProductDetailRoute> { DetailScreen() }
    composable<ReviewRoute> { ReviewScreen() }
}
```

**4. SavedStateHandle로 상태 복원**
```kotlin
LaunchedEffect(Unit) {
    savedStateHandle.getStateFlow("selectedTab", 0)
        .collect { selectedTab = it }
}

DisposableEffect(Unit) {
    onDispose {
        savedStateHandle["selectedTab"] = selectedTab
    }
}
```

---

## Phase 4: REVIEW — 배운 패턴 & 주의사항

### ✅ 잘된 부분
- Type-Safe Route로 인자 전달 타입 안전성 확보
- SavedStateHandle로 config change, process death 대응
- Nested NavGraph로 모듈 분리

### ⚠️ 주의사항

**1. DeepLink와 Nested NavHost 충돌**
```
문제: 외부 Intent → MainActivity (outer NavHost)
      outer NavHost가 Intent를 가로채서 inner NavHost의 DeepLink 동작 안 함

해결: DeepLink가 필요한 화면은 별도 Activity로 분리
```

**2. popUpTo + saveState 상태 꼬임**
```kotlin
// ❌ 잘못된 사용
navController.navigate("home") {
    popUpTo("home") {
        saveState = true  // 상태 저장
        restoreState = true  // 복원
    }
}
// → 같은 탭 재클릭 시 상태 꼬임

// ✅ 올바른 사용
navController.navigate("home") {
    popUpTo("home") {
        saveState = false  // 상태 초기화
        restoreState = false
    }
}
```

**3. BackStack 관리: popBackStack vs navigateUp**
```kotlin
popBackStack()    // 물리적 백 버튼: 이전 라우트로
navigateUp()      // 앱 바 버튼: 부모 라우트로 (parent meta-data 필요)
```

**4. BottomBar 덜컹거림**
```kotlin
// 문제: Nested NavHost 각 탭마다 recomposition → BottomBar 움직임
// 해결: contentWindowInsets(0) 적용으로 ime padding 제거
Scaffold(
    contentWindowInsets = WindowInsets(0, 0, 0, 0)
)
```

---

## 📂 산출물

```
compose-practice/src/main/java/com/murjune/pratice/compose/study/
├── nav2/
│   ├── README.md                          # 공식 개념 정리 + 다이어그램
│   ├── DeepLinkSample.kt
│   ├── LaunchSingleTopSample.kt
│   ├── NavHostBasicSample.kt
│   ├── NavigateUpSample.kt
│   ├── NestedNavGraphSample.kt
│   ├── PopBackStackSample.kt
│   ├── PopUpToSample.kt
│   ├── SaveRestoreStateSample.kt
│   ├── SavedStateHandleSample.kt
│   ├── TypeSafeNavSample.kt
│   └── challenge/
│       ├── README.md                      # ShoppingApp 아키텍처
│       ├── STUDY_NOTE.md                  # 문제 해결 기록
│       ├── ShoppingActivity.kt
│       ├── ShoppingApp.kt
│       ├── AppBarNavigator.kt
│       └── {home,cart,my,more,setting}/
│           ├── *Screen.kt
│           ├── *DetailScreen.kt
│           └── navigation/

compose-practice/src/test/java/com/murjune/pratice/compose/study/nav2/
├── NavHostBasicTest.kt
├── TypeSafeNavigationTest.kt
├── BackStackEntryLifecycleTest.kt
├── SavedStateHandleTest.kt
├── PopBackStackTest.kt
├── PopUpToTest.kt
├── LaunchSingleTopTest.kt
├── SaveRestoreStateTest.kt
├── DeepLinkHandlingTest.kt
├── StringRouteVsTypeSafeTest.kt
└── ShoppingNavigationTest.kt                # 통합 테스트
```

**테스트 케이스: 10개+**
- NavHost 기본 동작
- Type-Safe Route 직렬화/역직렬화
- BackStack entry lifecycle
- SavedStateHandle 상태 복원
- popBackStack vs navigateUp
- popUpTo 동작 (saveState, inclusive)
- LaunchSingleTop 중복 방지
- SaveRestoreState 패턴
- DeepLink 핸들링
- ShoppingApp 통합 (8 screens, 4 navigations)

---

## 🔑 핵심 인사이트

### 아키텍처 원칙
1. **Type-Safe가 기본**: String 라우트보다 @Serializable 우선
2. **상태는 SavedStateHandle**: remember보다 lifecycle 안전성 높음
3. **Nested NavGraph로 모듈화**: 각 탭마다 독립적 navigation flow
4. **DeepLink는 별도 Activity**: nested NavHost와 외부 Intent는 호환 안 됨

### 실무 패턴
```kotlin
// 탭 전환 시 상태 보존
navController.navigate(route) {
    popUpTo(navController.graph.startDestinationId) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}

// SavedStateHandle 활용
val viewModel: DetailViewModel = hiltViewModel()
val productId = viewModel.productId  // process death 대응

// DisposableEffect로 상태 저장
DisposableEffect(selectedTab) {
    onDispose {
        savedStateHandle["tab"] = selectedTab
    }
}
```

---

## ✅ 완료 체크리스트
- [x] 공식 문서 학습 (NavHost, Type-Safe, BackStack, DeepLink)
- [x] NowInAndroid 패턴 분석 및 샘플 구현
- [x] ShoppingApp 통합 구현 (8 screens, 4 navigations, BottomBar)
- [x] 학습 테스트 코드 (11개 테스트)
- [x] README 문서화 (개념, 다이어그램, 주의사항)

---

## 📅 다음 단계 (우선순위)

1. **Navigation 3** (높음) — BackStack 소유권, NavDisplay, Multi-Pane
2. **derivedStateOf** (높음) — 불필요한 recomposition 방지
3. **SideEffect** (높음) — LaunchedEffect, DisposableEffect, SideEffect
4. **retain API** (높음) — config change 생존, process death 불가 상태 관리

---

🎓 **학습 방식**: 공식 문서 → 베스트 프랙티스 → 코딩 챌린지 → 코드 리뷰 (4단계 사이클)
