package com.murjune.pratice.compose.study.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.testing.TestNavHostController
import androidx.navigation.toRoute
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * DeepLink Intent가 Activity와 Navigation에서 어떻게 처리되는지 검증하는 테스트
 *
 * 핵심 동작 (공식 문서):
 * 1) standard launch mode: NavController.handleDeepLink(intent) 자동 호출
 * 2) singleTop launch mode: onNewIntent()에서 수동으로 handleDeepLink() 호출 필요
 * 3) handleDeepLink()는 백스택을 정리하고 딥링크 destination으로 이동
 * 4) Activity의 onCreate()와 Navigation이 중복 처리하지 않음 (Navigation이 담당)
 *
 * 테스트에서는 TestNavHostController.handleDeepLink(intent)를 직접 호출하여 검증
 */
@RunWith(RobolectricTestRunner::class)
class DeepLinkHandlingTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Serializable
    object DLHome

    @Serializable
    data class DLProfile(
        val userId: String,
    )

    @Serializable
    data class DLPost(
        val postId: String,
    )

    private val profileBasePath = "https://app.example.com/profile"
    private val postBasePath = "https://app.example.com/post"

    private fun setupNavHost(): TestNavHostController {
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = DLHome) {
                composable<DLHome> { Text("Home") }
                composable<DLProfile>(
                    deepLinks = listOf(
                        navDeepLink<DLProfile>(basePath = profileBasePath),
                    ),
                ) { backStackEntry ->
                    val route = backStackEntry.toRoute<DLProfile>()
                    Text("Profile: ${route.userId}")
                }
                composable<DLPost>(
                    deepLinks = listOf(
                        navDeepLink<DLPost>(basePath = postBasePath),
                    ),
                ) { backStackEntry ->
                    val route = backStackEntry.toRoute<DLPost>()
                    Text("Post: ${route.postId}")
                }
            }
        }
        return navController
    }

    // =========================================================================
    // 1) handleDeepLink로 딥링크 Intent를 처리할 수 있다
    // =========================================================================

    @Test
    fun `handleDeepLink로_딥링크_Intent를_처리하면_해당_화면으로_이동한다`() {
        // given
        val navController = setupNavHost()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$profileBasePath/user-42"))

        // when - handleDeepLink 호출 (Activity에서 자동/수동 호출하는 것과 동일)
        navController.handleDeepLink(intent)

        // then
        composeTestRule.onNodeWithText("Profile: user-42").assertIsDisplayed()
    }

    // =========================================================================
    // 2) 딥링크 처리 후 백스택 구조 확인
    // =========================================================================

    @Test
    fun `딥링크_처리_후_백스택_구조를_확인할_수_있다`() {
        // given
        val navController = setupNavHost()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$profileBasePath/user-99"))

        // when
        navController.handleDeepLink(intent)
        composeTestRule.onNodeWithText("Profile: user-99").assertIsDisplayed()

        // then - 백스택에 어떤 엔트리가 있는지 확인
        val routes = navController.currentBackStack.value
            .mapNotNull { it.destination.route?.substringAfterLast(".") }
        // handleDeepLink는 기존 백스택 위에 딥링크 destination을 추가한다
        // 백스택: [NavGraph root, Home, Profile]
        routes.any { it.contains("DLProfile") } shouldBe true
    }

    // =========================================================================
    // 3) 이미 다른 화면에 있을 때 딥링크가 오면 백스택이 재구성된다
    // =========================================================================

    @Test
    fun `다른_화면에_있을_때_딥링크가_오면_백스택이_재구성된다`() {
        // given - Home → Profile(user-1) 상태
        val navController = setupNavHost()
        navController.navigate(DLProfile(userId = "user-1"))
        composeTestRule.onNodeWithText("Profile: user-1").assertIsDisplayed()

        // when - 새로운 딥링크 Intent 도착
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$postBasePath/post-777"))
        navController.handleDeepLink(intent)

        // then - Post 화면으로 이동
        composeTestRule.onNodeWithText("Post: post-777").assertIsDisplayed()
    }

    // =========================================================================
    // 4) 여러 딥링크가 연속으로 오면 마지막 것이 적용된다
    // =========================================================================

    @Test
    fun `여러_딥링크가_연속으로_오면_각각_처리된다`() {
        // given
        val navController = setupNavHost()

        // when - 첫 번째 딥링크
        val intent1 = Intent(Intent.ACTION_VIEW, Uri.parse("$profileBasePath/user-1"))
        navController.handleDeepLink(intent1)
        composeTestRule.onNodeWithText("Profile: user-1").assertIsDisplayed()

        // when - 두 번째 딥링크
        val intent2 = Intent(Intent.ACTION_VIEW, Uri.parse("$postBasePath/post-999"))
        navController.handleDeepLink(intent2)

        // then - 마지막 딥링크의 화면이 표시됨
        composeTestRule.onNodeWithText("Post: post-999").assertIsDisplayed()
    }

    // =========================================================================
    // 5) handleDeepLink 반환값으로 딥링크 매칭 여부 확인
    // =========================================================================

    @Test
    fun `handleDeepLink는_매칭_성공_시_true를_반환한다`() {
        // given
        val navController = setupNavHost()
        val validIntent = Intent(Intent.ACTION_VIEW, Uri.parse("$profileBasePath/user-1"))

        // when
        val result = navController.handleDeepLink(validIntent)

        // then
        result shouldBe true
    }

    @Test
    fun `handleDeepLink는_매칭_실패_시_false를_반환한다`() {
        // given
        val navController = setupNavHost()
        val invalidIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://unknown.com/page"))

        // when
        val result = navController.handleDeepLink(invalidIntent)

        // then
        result shouldBe false
    }
}
