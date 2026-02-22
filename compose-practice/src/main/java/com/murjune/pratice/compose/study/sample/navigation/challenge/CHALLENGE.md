# Navigation 코딩 챌린지: 쇼핑몰 앱

## 시나리오

간단한 쇼핑몰 앱의 Navigation을 구현하세요.

```
화면 구성:
┌─────────────────────────────────┐
│  BottomBar: [Home] [Cart] [My]  │
└─────────────────────────────────┘

Home → ProductDetail(productId) → Review(productId)
Cart → (탭 내 단독)
My   → OrderHistory → OrderDetail(orderId)
```

---

## 요구사항

### 1. Route 정의 (Type-Safe, @Serializable)

| Route | 타입 | 설명 |
|-------|------|------|
| `Home` | object | 하단 탭 — 홈 |
| `Cart` | object | 하단 탭 — 장바구니 |
| `My` | object | 하단 탭 — 마이페이지 |
| `ProductDetail(productId: Int)` | data class | 상품 상세 |
| `Review(productId: Int)` | data class | 리뷰 화면 |
| `OrderHistory` | object | 주문 내역 |
| `OrderDetail(orderId: String)` | data class | 주문 상세 |

---

### 2. 하단 탭 전환

- `popUpTo + saveState + restoreState + launchSingleTop` 패턴 적용
- 각 탭의 내부 백스택이 **보존**되어야 함

**예시:**

```
1. Home 탭에서 ProductDetail(1) 진입
2. Cart 탭으로 전환
3. Home 탭으로 복귀
→ ProductDetail(1)이 보여야 함 (Home이 아님!)
```

---

### 3. 특수 동작

#### 구매 완료 흐름
`ProductDetail`에서 "장바구니 담기" → `Cart` 탭으로 이동하되, `ProductDetail`은 백스택에서 제거

```kotlin
navController.navigate(Cart) {
    popUpTo<Home> { inclusive = false }
}
```

#### TopAppBar 뒤로가기
- 하위 화면(ProductDetail, Review, OrderHistory, OrderDetail)에서만 TopAppBar 표시
- 뒤로가기 버튼: `navigateUp()` 사용

#### 하단 탭 표시/숨김
- 하단 탭 화면(Home, Cart, My)에서만 BottomBar 표시
- 하위 화면에서는 BottomBar 숨김

---

### 4. DeepLink

`ProductDetail`에 deepLink 등록:

```kotlin
navDeepLink<ProductDetail>(basePath = "https://study.example.com/product")
```

---

### 5. 전체화면 탭 (추가 챌린지)

4번째 탭 `Setting`을 추가하되, 기존 탭과 다르게 동작한다.

**동작:**
- Setting 탭 클릭 → BottomBar가 **사라지고** 전체화면으로 Setting 화면 표시
- Setting 화면에서 뒤로가기 → 이전 탭으로 복귀 + BottomBar 다시 표시
- Activity 전환이 아닌 **같은 NavHost 내에서 처리**

**힌트:**
- Setting은 하단 탭이 아닌 **일반 destination**으로 등록
- BottomBar 표시 여부를 `currentDestination`으로 판단

```
[Home] [Cart] [My] [Setting]
                      ↓ 클릭
┌─────────────────────────────┐
│     Setting 화면 (전체)      │
│     BottomBar 없음           │
│     ← 뒤로가기 → 이전 탭    │
└─────────────────────────────┘
```

---

### 6. 테스트 작성 (최소 5개 + 추가 2개)

| # | 테스트 | 검증 내용 |
|---|--------|----------|
| 1 | 기본 탭 전환 | Home → Cart → My 전환 동작 |
| 2 | popUpTo 동작 | ProductDetail → Cart 시 ProductDetail 제거 확인 |
| 3 | 탭 내부 스택 보존 | Home → Detail → Cart → Home 복귀 시 Detail 유지 |
| 4 | navigateUp 동작 | 하위 화면에서 navigateUp 시 이전 화면 복귀 |
| 5 | DeepLink 진입 | Intent로 ProductDetail 직접 진입 확인 |
| 6 | Setting 전체화면 | Setting 클릭 시 BottomBar 숨김 확인 |
| 7 | Setting 뒤로가기 | Setting에서 뒤로가기 시 이전 탭 + BottomBar 복귀 |

---

## 구현 위치

```
challenge/
  ├── CHALLENGE.md              ← 이 파일 (요구사항)
  ├── ShoppingRoutes.kt         ← Route 정의
  └── ShoppingNavHost.kt        ← NavHost + BottomBar + 화면 구성

테스트:
compose-practice/src/test/.../navigation/
  └── ShoppingNavigationTest.kt ← 테스트
```

---

## 힌트

<details>
<summary>힌트 1: 하단 탭 전환 함수</summary>

```kotlin
fun NavController.navigateToTab(route: Any) {
    navigate(route) {
        popUpTo<???> { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
```

</details>

<details>
<summary>힌트 2: 현재 탭 판별 (BottomBar 선택 상태)</summary>

```kotlin
val currentDestination = navController.currentBackStackEntryAsState()
    .value?.destination

// hierarchy로 현재 탭 확인
currentDestination?.hierarchy?.any { it.hasRoute<Home>() } == true
```

</details>

<details>
<summary>힌트 3: BottomBar 표시/숨김</summary>

```kotlin
val showBottomBar = when {
    currentDestination?.hasRoute<Home>() == true -> true
    currentDestination?.hasRoute<Cart>() == true -> true
    currentDestination?.hasRoute<My>() == true -> true
    else -> false
}
```

</details>

---

## 참고

- Phase 1 README: 핵심 개념 정리
- Phase 2 README: JetNews/NowInAndroid 베스트 프랙티스
- `SaveRestoreStateSample.kt` — 하단 탭 전환 샘플 코드
- `PopUpToSample.kt` — popUpTo 패턴 샘플 코드
