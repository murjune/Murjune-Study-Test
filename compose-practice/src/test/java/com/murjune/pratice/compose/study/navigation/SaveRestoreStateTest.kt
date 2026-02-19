package com.murjune.pratice.compose.study.navigation

import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * saveState / restoreState 동작을 검증하는 테스트
 *
 * 핵심 시나리오: 하단 탭 전환 (Bottom Navigation)
 * - 각 탭은 독립적인 백스택을 유지
 * - 탭 전환 시 이전 탭의 백스택을 저장 (saveState)
 * - 다시 돌아오면 이전 백스택을 복원 (restoreState)
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SaveRestoreStateTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // 하단 탭 destination
    @Serializable
    object TabHome

    @Serializable
    object TabSearch

    @Serializable
    object TabProfile

    // TabHome 내부 sub-destination
    @Serializable
    object HomeDetail

    // TabSearch 내부 sub-destination
    @Serializable
    object SearchResult

    private fun setupBottomNavHost(): TestNavHostController {
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = TabHome) {
                composable<TabHome> { Text("Home Tab") }
                composable<HomeDetail> { Text("Home Detail") }
                composable<TabSearch> { Text("Search Tab") }
                composable<SearchResult> { Text("Search Result") }
                composable<TabProfile> { Text("Profile Tab") }
            }
        }
        return navController
    }

    /**
     * 하단 탭 전환 패턴:
     * popUpTo(startDestination) { saveState = true }
     * launchSingleTop = true
     * restoreState = true
     */
    private fun TestNavHostController.navigateToTab(route: Any) {
        navigate(route) {
            popUpTo<TabHome> {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    // =========================================================================
    // 1) saveState + restoreState로 탭 전환 시 백스택 보존
    // =========================================================================

    @Test
    fun `탭_전환_시_saveState로_이전_탭의_백스택이_저장된다`() {
        // given - Home 탭에서 Detail로 이동
        val navController = setupBottomNavHost()
        navController.navigate(HomeDetail)
        composeTestRule.onNodeWithText("Home Detail").assertIsDisplayed()

        // when - Search 탭으로 전환 (Home의 백스택 저장됨)
        navController.navigateToTab(TabSearch)

        // then - Search 탭이 표시됨
        composeTestRule.onNodeWithText("Search Tab").assertIsDisplayed()
    }

    @Test
    fun `restoreState로_이전_탭에_돌아가면_백스택이_복원된다`() {
        // given - Home → HomeDetail → Search 탭 전환
        val navController = setupBottomNavHost()
        navController.navigate(HomeDetail)
        composeTestRule.onNodeWithText("Home Detail").assertIsDisplayed()

        navController.navigateToTab(TabSearch)
        composeTestRule.onNodeWithText("Search Tab").assertIsDisplayed()

        // when - Home 탭으로 다시 전환 (restoreState)
        navController.navigateToTab(TabHome)

        // then - HomeDetail이 복원됨 (Home이 아니라!)
        composeTestRule.onNodeWithText("Home Detail").assertIsDisplayed()
    }

    @Test
    fun `복원된_백스택에서_popBackStack하면_탭_루트로_돌아간다`() {
        // given - Home → HomeDetail → Search → Home (복원)
        val navController = setupBottomNavHost()
        navController.navigate(HomeDetail)
        navController.navigateToTab(TabSearch)
        navController.navigateToTab(TabHome)
        composeTestRule.onNodeWithText("Home Detail").assertIsDisplayed()

        // when - popBackStack
        navController.popBackStack()

        // then - Home 탭 루트로 돌아감
        composeTestRule.onNodeWithText("Home Tab").assertIsDisplayed()
    }

    // =========================================================================
    // 2) saveState 없이 탭 전환하면 백스택이 유지되지 않음
    // =========================================================================

    @Test
    fun `saveState_없이_popUpTo하면_이전_탭의_백스택이_소실된다`() {
        // given - Home → HomeDetail
        val navController = setupBottomNavHost()
        navController.navigate(HomeDetail)
        composeTestRule.onNodeWithText("Home Detail").assertIsDisplayed()

        // when - saveState 없이 Search로 전환
        navController.navigate(TabSearch) {
            popUpTo<TabHome> {
                inclusive = false
                // saveState = false (기본값)
            }
        }
        composeTestRule.onNodeWithText("Search Tab").assertIsDisplayed()

        // Home으로 돌아가기
        navController.navigate(TabHome) {
            popUpTo<TabHome> {
                inclusive = true
            }
            // restoreState = false (기본값)
        }

        // then - Home Tab 루트로 돌아감 (HomeDetail은 소실됨)
        composeTestRule.onNodeWithText("Home Tab").assertIsDisplayed()
    }

    // =========================================================================
    // 3) 여러 탭 간 독립적인 백스택 유지
    // =========================================================================

    @Test
    fun `여러_탭이_각각_독립적인_백스택을_유지한다`() {
        // given
        val navController = setupBottomNavHost()

        // Home 탭에서 Detail로 이동
        navController.navigate(HomeDetail)
        composeTestRule.onNodeWithText("Home Detail").assertIsDisplayed()

        // Search 탭으로 전환 후 SearchResult로 이동
        navController.navigateToTab(TabSearch)
        navController.navigate(SearchResult)
        composeTestRule.onNodeWithText("Search Result").assertIsDisplayed()

        // Profile 탭으로 전환
        navController.navigateToTab(TabProfile)
        composeTestRule.onNodeWithText("Profile Tab").assertIsDisplayed()

        // when - Home 탭으로 복원
        navController.navigateToTab(TabHome)

        // then - HomeDetail이 복원됨
        composeTestRule.onNodeWithText("Home Detail").assertIsDisplayed()

        // when - Search 탭으로 복원
        navController.navigateToTab(TabSearch)

        // then - SearchResult가 복원됨
        composeTestRule.onNodeWithText("Search Result").assertIsDisplayed()
    }

    // =========================================================================
    // 4) launchSingleTop으로 같은 탭 반복 클릭 시 중복 방지
    // =========================================================================

    @Test
    fun `같은_탭을_반복_클릭해도_중복_엔트리가_생기지_않는다`() {
        // given
        val navController = setupBottomNavHost()
        navController.navigateToTab(TabSearch)
        val sizeAfterFirstNav = navController.currentBackStack.value.size

        // when - 같은 탭 반복 클릭
        navController.navigateToTab(TabSearch)
        navController.navigateToTab(TabSearch)

        // then - 백스택 크기 변화 없음
        navController.currentBackStack.value.size shouldBe sizeAfterFirstNav
    }
}
