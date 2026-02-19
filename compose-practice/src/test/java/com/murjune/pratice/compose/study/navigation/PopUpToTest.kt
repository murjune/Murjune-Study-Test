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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PopUpToTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Serializable
    object Home

    @Serializable
    object Login

    @Serializable
    object Verify

    @Serializable
    object Dashboard

    @Test
    fun `popUpTo로_특정_화면까지_백스택을_정리할_수_있다`() {
        // given - Home -> Login -> Verify
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = Home) {
                composable<Home> { Text("Home Screen") }
                composable<Login> { Text("Login Screen") }
                composable<Verify> { Text("Verify Screen") }
                composable<Dashboard> { Text("Dashboard Screen") }
            }
        }

        navController.navigate(Login)
        navController.navigate(Verify)
        composeTestRule.onNodeWithText("Verify Screen").assertIsDisplayed()

        // when - Dashboard로 이동하면서 Home까지 백스택 정리 (Home은 유지)
        navController.navigate(Dashboard) {
            popUpTo<Home> {
                inclusive = false
            }
        }

        // then - Dashboard가 표시되고, 백스택에는 Home -> Dashboard만 남음
        composeTestRule.onNodeWithText("Dashboard Screen").assertIsDisplayed()

        // popBackStack하면 Home으로 돌아간다 (Login, Verify는 제거됨)
        navController.popBackStack()
        composeTestRule.onNodeWithText("Home Screen").assertIsDisplayed()
    }

    @Test
    fun `inclusive_true이면_popUpTo_대상도_제거된다`() {
        // given - Login -> Verify
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = Login) {
                composable<Login> { Text("Login Screen") }
                composable<Verify> { Text("Verify Screen") }
                composable<Home> { Text("Home Screen") }
            }
        }

        navController.navigate(Verify)
        composeTestRule.onNodeWithText("Verify Screen").assertIsDisplayed()

        // when - Home으로 이동하면서 Login까지 inclusive로 제거
        navController.navigate(Home) {
            popUpTo<Login> {
                inclusive = true
            }
        }

        // then - Home만 백스택에 남음 (popBackStack하면 false)
        composeTestRule.onNodeWithText("Home Screen").assertIsDisplayed()

        val popResult = navController.popBackStack()
        popResult shouldBe false
    }

    @Test
    fun `launchSingleTop으로_같은_화면_중복을_방지한다`() {
        // given
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = Home) {
                composable<Home> { Text("Home Screen") }
                composable<Dashboard> { Text("Dashboard Screen") }
            }
        }

        navController.navigate(Dashboard)
        composeTestRule.onNodeWithText("Dashboard Screen").assertIsDisplayed()

        // when - 같은 화면을 launchSingleTop으로 두 번 navigate
        navController.navigate(Dashboard) {
            launchSingleTop = true
        }

        // then - popBackStack 한 번이면 Home으로 돌아간다 (중복 없음)
        navController.popBackStack()
        composeTestRule.onNodeWithText("Home Screen").assertIsDisplayed()
    }

    @Test
    fun `로그인_플로우에서_popUpTo_inclusive로_로그인화면을_제거한다`() {
        // given - 실제 시나리오: Login -> Verify -> Home (로그인 성공 후)
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = Login) {
                composable<Login> { Text("Login Screen") }
                composable<Verify> { Text("Verify Screen") }
                composable<Home> { Text("Home Screen") }
            }
        }

        // 로그인 플로우 진행
        navController.navigate(Verify)
        composeTestRule.onNodeWithText("Verify Screen").assertIsDisplayed()

        // when - 인증 완료 후 Home으로 이동, 로그인 관련 화면 모두 제거
        navController.navigate(Home) {
            popUpTo<Login> {
                inclusive = true
            }
        }

        // then - Home이 표시되고, 뒤로가기 시 앱 종료 (로그인 화면으로 돌아가지 않음)
        composeTestRule.onNodeWithText("Home Screen").assertIsDisplayed()

        val popResult = navController.popBackStack()
        popResult shouldBe false
    }
}
