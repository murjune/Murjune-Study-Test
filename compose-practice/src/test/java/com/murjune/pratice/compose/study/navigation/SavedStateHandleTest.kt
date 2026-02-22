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
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.Serializable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * SavedStateHandle이 Navigation에서 어떻게 동작하는지 검증하는 테스트
 *
 * SavedStateHandle은 NavBackStackEntry마다 존재하며:
 * 1) Route의 인자가 자동으로 들어간다
 * 2) previousBackStackEntry.savedStateHandle로 이전 화면에 결과를 전달할 수 있다
 * 3) Configuration Change / Process Death에서도 살아남는다
 */
@RunWith(RobolectricTestRunner::class)
class SavedStateHandleTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Serializable
    object ResultHome

    @Serializable
    data class ProfileRoute(
        val userId: String,
        val age: Int,
    )

    @Serializable
    object EditScreen

    // =========================================================================
    // 1) Route 인자가 SavedStateHandle에 자동으로 들어간다
    // =========================================================================

    @Test
    fun `Route_인자가_SavedStateHandle에_자동으로_저장된다`() {
        // given
        lateinit var navController: TestNavHostController
        var savedUserId: String? = null
        var savedAge: Int? = null

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = ResultHome) {
                composable<ResultHome> { Text("Home") }
                composable<ProfileRoute> { backStackEntry ->
                    // SavedStateHandle에서 Route 인자 꺼내기
                    val savedStateHandle = backStackEntry.savedStateHandle
                    savedUserId = savedStateHandle.get<String>("userId")
                    savedAge = savedStateHandle.get<Int>("age")
                    Text("Profile: $savedUserId, $savedAge")
                }
            }
        }

        // when
        navController.navigate(ProfileRoute(userId = "user-42", age = 28))

        // then - SavedStateHandle에 Route 인자가 자동으로 들어가 있다
        composeTestRule.onNodeWithText("Profile: user-42, 28").assertIsDisplayed()
        savedUserId shouldBe "user-42"
        savedAge shouldBe 28
    }

    // =========================================================================
    // 2) previousBackStackEntry.savedStateHandle로 이전 화면에 결과 전달
    // =========================================================================

    @Test
    fun `previousBackStackEntry의_savedStateHandle로_이전_화면에_결과를_전달할_수_있다`() {
        // given
        lateinit var navController: TestNavHostController
        var receivedResult: String? = null

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = ResultHome) {
                composable<ResultHome> { backStackEntry ->
                    // 돌아왔을 때 SavedStateHandle에서 결과 읽기
                    receivedResult =
                        backStackEntry.savedStateHandle
                            .get<String>("edit_result")
                    Text("Home - result: ${receivedResult ?: "없음"}")
                }
                composable<EditScreen> {
                    Text("Edit Screen")
                }
            }
        }

        // Home → EditScreen 이동
        navController.navigate(EditScreen)
        composeTestRule.onNodeWithText("Edit Screen").assertIsDisplayed()

        // when - EditScreen에서 이전 화면(Home)의 SavedStateHandle에 결과 저장 후 pop
        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set("edit_result", "수정 완료!")
        navController.popBackStack()

        // then - Home으로 돌아온 후 결과를 받을 수 있다
        composeTestRule.onNodeWithText("Home - result: 수정 완료!").assertIsDisplayed()
        receivedResult shouldBe "수정 완료!"
    }

    // =========================================================================
    // 3) SavedStateHandle은 NavBackStackEntry마다 독립적이다
    // =========================================================================

    @Test
    fun `각_백스택_엔트리의_SavedStateHandle은_독립적이다`() {
        // given
        lateinit var navController: TestNavHostController

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = ResultHome) {
                composable<ResultHome> { Text("Home") }
                composable<EditScreen> { Text("Edit") }
            }
        }

        // Home의 SavedStateHandle에 값 설정
        navController.currentBackStackEntry?.savedStateHandle?.set("key", "home_value")

        // Home 엔트리 참조 보관
        val homeEntry = navController.currentBackStackEntry

        // EditScreen으로 이동
        navController.navigate(EditScreen)

        // then - EditScreen의 SavedStateHandle에는 Home에서 설정한 값이 없다
        val editEntry = navController.currentBackStackEntry
        editEntry?.savedStateHandle?.get<String>("key") shouldBe null

        // Home 엔트리의 SavedStateHandle에는 값이 그대로 남아있다
        homeEntry?.savedStateHandle?.get<String>("key") shouldBe "home_value"
    }

    // =========================================================================
    // 4) currentBackStackEntry는 navigate 전후로 다른 엔트리를 가리킨다
    // =========================================================================

    @Test
    fun `navigate_후_currentBackStackEntry가_새_엔트리로_변경된다`() {
        // given
        lateinit var navController: TestNavHostController

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = ResultHome) {
                composable<ResultHome> { Text("Home") }
                composable<EditScreen> { Text("Edit") }
            }
        }

        val homeEntry = navController.currentBackStackEntry
        homeEntry shouldNotBe null

        // when
        navController.navigate(EditScreen)

        // then - currentBackStackEntry가 변경됨
        val editEntry = navController.currentBackStackEntry
        editEntry shouldNotBe homeEntry

        // previousBackStackEntry는 Home을 가리킴
        navController.previousBackStackEntry shouldBe homeEntry
    }
}
