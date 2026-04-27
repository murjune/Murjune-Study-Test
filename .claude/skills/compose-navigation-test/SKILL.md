---
name: compose-navigation-test
description: "Robolectric 기반 Compose Navigation 통합 테스트 작성 가이드. 패턴, 함정, 스타일 규칙."
allowed-tools:
  - Read
  - Write
  - Edit
  - Bash
  - Glob
  - Grep
---

# Compose Navigation 테스트 작성 가이드

Navigation 테스트 작성 시 반드시 따라야 하는 패턴과 스타일 규칙.

## 환경 설정

- **JUnit4** + **Robolectric** + **Compose UI Test**
- assertion: **Kotest matchers** (`shouldBe`)

```kotlin
@RunWith(RobolectricTestRunner::class)
class SomeNavigationTest {
    @get:Rule
    val composeTestRule = createComposeRule()
}
```

## NavController 셋업

```kotlin
private fun setupNavHost(): AppBarNavigator {
    lateinit var appBarNavigator: AppBarNavigator
    composeTestRule.setContent {
        val navController = TestNavHostController(LocalContext.current).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
        }
        appBarNavigator = AppBarNavigator(navController = navController)
        ShoppingApp(appBarNavigator = appBarNavigator)
    }
    return appBarNavigator
}
```

- `ComposeNavigator()` 반드시 추가 — 없으면 composable destination을 못 찾음

## UI 텍스트 매칭 규칙

| 대상 | 올바른 방법 | 잘못된 방법 |
|------|------------|-----------|
| BottomNav 라벨 | `onNodeWithText("홈")` — strings.xml 값 확인 | `onNodeWithText("Home")` — 영문 추측 |
| TopAppBar 아이콘 | `onNodeWithContentDescription("설정")` | `onNodeWithText("설정")` — Icon은 text 없음 |
| 리스트 아이템 | `onNodeWithText("MacBook Pro")` — 실제 data 확인 | `onNodeWithText("상품 1")` — 존재하지 않는 텍스트 |
| 뒤로가기 버튼 | `onNodeWithContentDescription("뒤로가기")` | `onNodeWithText("뒤로가기")` |

**[필수]** 테스트 작성 전 반드시 Read로 확인:
- `strings.xml` — BottomNav 라벨 값
- 각 Screen 파일 — Text/contentDescription 실제 값
- data source — 리스트에 표시되는 실제 텍스트

## waitForIdle / runOnUiThread 규칙

**기본 원칙: 넣지 않는다.** `performClick()`, `assertIsDisplayed()` 등이 내부적으로 idle을 기다린다.

```kotlin
// GOOD
composeTestRule.onNodeWithText("장바구니").performClick()
appBarNavigator.navController.currentDestination
    ?.hasRoute<CartRoute.CartScreen>() shouldBe true

// BAD
composeTestRule.onNodeWithText("장바구니").performClick()
composeTestRule.waitForIdle()  // ← 불필요
```

**예외 (테스트 실패할 때만 추가):**
- NavController를 코드에서 직접 호출한 후 (`navigateToBottomNavDestination()` 등)
- `handleDeepLink()` 호출 후
- 추가할 때 반드시 주석으로 이유 명시

## 테스트 스타일 규칙

```kotlin
// GOOD — 함수명이 충분히 설명
@Test
fun `Setting에서_뒤로가기하면_이전_탭과_BottomBar가_복귀한다`() {
    val appBarNavigator = setupShoppingApp()
    composeTestRule.onNodeWithContentDescription("설정").performClick()
    // ...
}

// BAD — 불필요한 구분선 주석
// =========================================================================
// 테스트 7: Setting 뒤로가기 → 이전 탭 + BottomBar 복귀
// =========================================================================
```

- **구분선/번호 주석 금지** — 한글 함수명이 이미 충분한 설명
- given/when/then 주석은 최소한으로 — 흐름이 명확하면 생략
- 인라인 주석은 비자명한 동작에만 (`// popUpTo로 ProductDetail 제거됨`)

## 자주 빠지는 함정

| 함정 | 증상 | 해결 |
|------|------|------|
| `assertIsNotDisplayed` vs `assertDoesNotExist` | 노드 없으면 crash | 노드 자체가 사라지면 `assertDoesNotExist` 사용 |
| Cart의 `BackHandler` | `navigateUp()`이 예상과 다르게 동작 | BackHandler가 탭 전환 로직을 오버라이드. 백스택 직접 확인 |
| BottomNav 라벨 중복 | "장바구니" 텍스트가 화면 본문에도 있음 | `onAllNodesWithText` 후 특정 노드 선택, 또는 testTag 사용 |
