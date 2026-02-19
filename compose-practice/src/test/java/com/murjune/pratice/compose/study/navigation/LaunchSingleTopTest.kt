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
import androidx.navigation.toRoute
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * 같은 destination 타입에 다른 인자를 넘겼을 때 백스택 동작과
 * launchSingleTop의 영향을 검증하는 테스트
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LaunchSingleTopTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Serializable
    object Home

    @Serializable
    data class ItemDetail(
        val id: Int,
    )

    private fun setupNavHost(): TestNavHostController {
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = Home) {
                composable<Home> { Text("Home") }
                composable<ItemDetail> { backStackEntry ->
                    val route = backStackEntry.toRoute<ItemDetail>()
                    Text("Item ${route.id}")
                }
            }
        }
        return navController
    }

    private fun TestNavHostController.backStackSize(): Int =
        currentBackStack.value.size

    private fun TestNavHostController.backStackRoutes(): List<String> =
        currentBackStack.value.mapNotNull { it.destination.route?.substringAfterLast(".") }

    // =========================================================================
    // 1) 같은 destination, 다른 인자 → 별도 백스택 엔트리로 쌓인다
    // =========================================================================

    @Test
    fun `같은_Route_타입이라도_다른_인자면_별도_백스택_엔트리로_쌓인다`() {
        // given
        val navController = setupNavHost()
        val initialSize = navController.backStackSize()

        // when - ItemDetail(1) 과 ItemDetail(2) 순서대로 navigate
        navController.navigate(ItemDetail(id = 1))
        navController.navigate(ItemDetail(id = 2))

        // then - 백스택: Home → ItemDetail(1) → ItemDetail(2) = 초기 + 2개 추가
        composeTestRule.onNodeWithText("Item 2").assertIsDisplayed()
        navController.backStackSize() shouldBe initialSize + 2

        // popBackStack하면 ItemDetail(1)로 돌아간다
        navController.popBackStack()
        composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
    }

    // =========================================================================
    // 2) launchSingleTop = true → top이 같은 destination이면 새로 안 쌓임
    // =========================================================================

    @Test
    fun `launchSingleTop이면_top이_같은_destination일_때_새_엔트리를_추가하지_않는다`() {
        // given - Home → ItemDetail(1)
        val navController = setupNavHost()
        navController.navigate(ItemDetail(id = 1))
        composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
        val sizeAfterFirstNav = navController.backStackSize()

        // when - 같은 destination에 다른 인자로 launchSingleTop 이동
        navController.navigate(ItemDetail(id = 2)) {
            launchSingleTop = true
        }

        // then - 백스택 크기가 변하지 않는다 (새 엔트리 안 쌓임)
        navController.backStackSize() shouldBe sizeAfterFirstNav
    }

    @Test
    fun `launchSingleTop시_기존_엔트리의_인자가_새_인자로_갱신된다`() {
        // given - Home → ItemDetail(1)
        val navController = setupNavHost()
        composeTestRule.runOnIdle {
            navController.navigate(ItemDetail(id = 1))
        }
        composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()

        // when - launchSingleTop으로 id=2로 이동
        composeTestRule.runOnIdle {
            navController.navigate(ItemDetail(id = 2)) {
                launchSingleTop = true
            }
        }

        // then - 화면에 표시되는 인자가 id=2로 갱신된다
        composeTestRule.onNodeWithText("Item 2").assertIsDisplayed()
    }

    @Test
    fun `launchSingleTop_후_popBackStack하면_바로_Home으로_돌아간다`() {
        // given - Home → ItemDetail(1) → launchSingleTop ItemDetail(2)
        val navController = setupNavHost()
        navController.navigate(ItemDetail(id = 1))
        navController.navigate(ItemDetail(id = 2)) {
            launchSingleTop = true
        }

        // when - popBackStack
        navController.popBackStack()

        // then - ItemDetail(1)이 아니라 Home으로 돌아간다 (중간 엔트리 없음)
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }

    // =========================================================================
    // 3) launchSingleTop은 top이 다른 destination이면 효과 없음 (정상 쌓임)
    // =========================================================================

    @Test
    fun `top이_다른_destination이면_launchSingleTop이어도_정상적으로_쌓인다`() {
        // given - Home이 top인 상태
        val navController = setupNavHost()
        val initialSize = navController.backStackSize()

        // when - Home(top) ≠ ItemDetail이므로 launchSingleTop이어도 쌓인다
        navController.navigate(ItemDetail(id = 1)) {
            launchSingleTop = true
        }

        // then - 백스택에 추가됨
        composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
        navController.backStackSize() shouldBe initialSize + 1
    }

    // =========================================================================
    // 4) launchSingleTop 없이 같은 인자로 navigate → 중복 쌓임
    // =========================================================================

    @Test
    fun `launchSingleTop_없이_같은_인자로_navigate하면_중복으로_쌓인다`() {
        // given - Home → ItemDetail(1)
        val navController = setupNavHost()
        navController.navigate(ItemDetail(id = 1))
        val sizeAfterFirstNav = navController.backStackSize()

        // when - 같은 인자로 다시 navigate (singleTop 없음)
        navController.navigate(ItemDetail(id = 1))

        // then - 백스택에 중복 쌓임
        navController.backStackSize() shouldBe sizeAfterFirstNav + 1

        // popBackStack 두 번 해야 Home으로 돌아감
        navController.popBackStack()
        composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
        composeTestRule.runOnIdle { navController.popBackStack() }
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }
}
