package com.murjune.pratice.compose.study.navigation

import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.testing.TestNavHostController
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Nested Navigation Graph 동작을 검증하는 테스트
 *
 * 핵심 개념:
 * - navigation<GraphRoute>(startDestination = ...) { } 로 중첩 그래프 생성
 * - 중첩 그래프는 독립적인 서브 플로우를 캡슐화
 * - 외부에서는 그래프 자체(GraphRoute)로 navigate, 내부 startDestination이 표시됨
 * - popUpTo로 중첩 그래프 전체를 한번에 정리 가능
 */
@RunWith(RobolectricTestRunner::class)
class NestedNavGraphTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Top-level destinations
    @Serializable
    object Home

    // Auth 중첩 그래프의 route (그래프 자체를 가리킴)
    @Serializable
    object AuthGraph

    // Auth 중첩 그래프 내부 destinations
    @Serializable
    object Login

    @Serializable
    object Signup

    @Serializable
    object VerifyEmail

    // Main (로그인 완료 후)
    @Serializable
    object Dashboard

    private fun setupNavHost(): TestNavHostController {
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = Home) {
                composable<Home> { Text("Home") }

                // Auth 중첩 그래프
                navigation<AuthGraph>(startDestination = Login) {
                    composable<Login> { Text("Login Screen") }
                    composable<Signup> { Text("Signup Screen") }
                    composable<VerifyEmail> { Text("Verify Email Screen") }
                }

                composable<Dashboard> { Text("Dashboard") }
            }
        }
        return navController
    }

    // =========================================================================
    // 1) 중첩 그래프로 navigate하면 startDestination이 표시된다
    // =========================================================================

    @Test
    fun `중첩_그래프로_navigate하면_startDestination이_표시된다`() {
        // given
        val navController = setupNavHost()

        // when - AuthGraph로 이동
        navController.navigate(AuthGraph)

        // then - AuthGraph의 startDestination인 Login이 표시됨
        composeTestRule.onNodeWithText("Login Screen").assertIsDisplayed()
    }

    // =========================================================================
    // 2) 중첩 그래프 내부에서 navigate 가능
    // =========================================================================

    @Test
    fun `중첩_그래프_내부에서_다른_화면으로_이동할_수_있다`() {
        // given
        val navController = setupNavHost()
        navController.navigate(AuthGraph)
        composeTestRule.onNodeWithText("Login Screen").assertIsDisplayed()

        // when - Login → Signup
        navController.navigate(Signup)

        // then
        composeTestRule.onNodeWithText("Signup Screen").assertIsDisplayed()
    }

    @Test
    fun `중첩_그래프_내부에서_여러_화면을_탐색할_수_있다`() {
        // given
        val navController = setupNavHost()
        navController.navigate(AuthGraph)

        // Login → Signup → VerifyEmail
        navController.navigate(Signup)
        navController.navigate(VerifyEmail)

        // then
        composeTestRule.onNodeWithText("Verify Email Screen").assertIsDisplayed()

        // popBackStack으로 Signup → Login 순서로 돌아감
        navController.popBackStack()
        composeTestRule.onNodeWithText("Signup Screen").assertIsDisplayed()

        navController.popBackStack()
        composeTestRule.onNodeWithText("Login Screen").assertIsDisplayed()
    }

    // =========================================================================
    // 3) popUpTo로 중첩 그래프 전체를 한번에 정리
    // =========================================================================

    @Test
    fun `popUpTo로_중첩_그래프_전체를_정리하고_다른_화면으로_이동할_수_있다`() {
        // given - Home → AuthGraph(Login → Signup → VerifyEmail)
        val navController = setupNavHost()
        navController.navigate(AuthGraph)
        navController.navigate(Signup)
        navController.navigate(VerifyEmail)
        composeTestRule.onNodeWithText("Verify Email Screen").assertIsDisplayed()

        // when - 인증 완료 후 Dashboard로 이동, AuthGraph 전체 정리
        navController.navigate(Dashboard) {
            popUpTo<AuthGraph> { inclusive = true }
        }

        // then - Dashboard 표시, AuthGraph 전체 제거됨
        composeTestRule.onNodeWithText("Dashboard").assertIsDisplayed()

        // popBackStack하면 Home으로 (AuthGraph 내부 화면은 모두 제거됨)
        navController.popBackStack()
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }

    // =========================================================================
    // 4) 중첩 그래프 외부에서 내부 destination으로 직접 navigate 가능
    // =========================================================================

    @Test
    fun `외부에서_중첩_그래프_내부_destination으로_직접_navigate할_수_있다`() {
        // given
        val navController = setupNavHost()

        // when - Home에서 직접 Signup으로 이동 (AuthGraph의 내부 destination)
        navController.navigate(Signup)

        // then - Signup이 표시됨
        composeTestRule.onNodeWithText("Signup Screen").assertIsDisplayed()
    }

    // =========================================================================
    // 5) 중첩 그래프 popBackStack 시 외부로 나감
    // =========================================================================

    @Test
    fun `중첩_그래프의_startDestination에서_popBackStack하면_그래프_밖으로_나간다`() {
        // given - Home → AuthGraph(Login)
        val navController = setupNavHost()
        navController.navigate(AuthGraph)
        composeTestRule.onNodeWithText("Login Screen").assertIsDisplayed()

        // when - Login(startDestination)에서 popBackStack
        navController.popBackStack()

        // then - Home으로 돌아감
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }

    // =========================================================================
    // 6) 백스택 크기 확인: 중첩 그래프 진입 시 엔트리 구조
    // =========================================================================

    @Test
    fun `중첩_그래프_진입_시_백스택에_그래프_엔트리와_startDestination이_추가된다`() {
        // given
        val navController = setupNavHost()
        val initialSize = navController.currentBackStack.value.size

        // when - AuthGraph로 이동
        navController.navigate(AuthGraph)

        // then - 백스택에 2개 추가 (AuthGraph 엔트리 + Login 엔트리)
        val afterSize = navController.currentBackStack.value.size
        (afterSize - initialSize) shouldBe 2
    }
}
