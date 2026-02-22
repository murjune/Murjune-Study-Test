# Navigation 학습 테스트

Jetpack Compose Navigation의 핵심 개념을 검증하는 학습 테스트 모음입니다.
각 테스트 파일은 Demo 앱의 샘플 화면과 1:1 대응됩니다.

## 테스트 파일 목록

| 테스트 파일 | 데모 샘플 | 핵심 개념 |
|---|---|---|
| `NavHostBasicTest` | NavHostBasicSample | NavHost/NavController 기본, composable 등록, navigate/popBackStack |
| `TypeSafeNavigationTest` | TypeSafeNavSample | @Serializable Route, toRoute()로 인자 추출, 타입 안전 네비게이션 |
| `PopBackStackTest` | PopBackStackSample | popBackStack 동작, 백스택 크기 변화, 연속 pop |
| `PopUpToTest` | PopUpToSample | popUpTo + inclusive, launchSingleTop, 로그인 플로우 패턴 |
| `LaunchSingleTopTest` | LaunchSingleTopSample | 같은 destination 다른 인자, launchSingleTop 시 인자 갱신, 중복 방지 |
| `SavedStateHandleTest` | SavedStateHandleSample | Route 인자 → SavedStateHandle 자동 저장, 결과 전달 패턴, 엔트리 독립성 |
| `SavedStateHandleAdvancedTest` | SavedStateHandleSample | getStateFlow, getMutableStateFlow, ViewModel+toRoute, keys/contains/remove |
| `SaveRestoreStateTest` | SaveRestoreStateSample | 하단 탭 전환 패턴, saveState/restoreState, 독립 백스택 보존 |
| `NestedNavGraphTest` | NestedNavGraphSample | navigation\<GraphRoute\>, 중첩 그래프 캡슐화, popUpTo로 그래프 전체 정리 |
| `DeepLinkHandlingTest` | DeepLinkSample | navDeepLink, handleDeepLink(), Intent 딥링크 처리 흐름 |

## 테스트 환경

- **프레임워크**: JUnit4 + Kotest matchers
- **UI 테스트**: `createComposeRule()` + `androidx.compose.ui.test`
- **런타임**: Robolectric (`@RunWith(RobolectricTestRunner::class)`)
- **Navigation**: `TestNavHostController` + `ComposeNavigator`

## 공통 패턴

```kotlin
@RunWith(RobolectricTestRunner::class)
class SomeNavigationTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // 1. Route 정의 (@Serializable)
    @Serializable object Home
    @Serializable data class Detail(val id: Int)

    // 2. NavHost 설정
    private fun setupNavHost(): TestNavHostController {
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = Home) {
                composable<Home> { Text("Home") }
                composable<Detail> { /* ... */ }
            }
        }
        return navController
    }

    // 3. 테스트: navigate → assert UI → assert 백스택
    @Test
    fun `테스트_시나리오_설명`() {
        val navController = setupNavHost()
        navController.navigate(Detail(id = 1))
        composeTestRule.onNodeWithText("Detail 1").assertIsDisplayed()
        navController.currentBackStack.value.size shouldBe expectedSize
    }
}
```

## 실행

```bash
./gradlew :compose-practice:testDebugUnitTest
```
