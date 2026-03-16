package com.murjune.pratice.compose.study.nav2

import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import kotlinx.serialization.Serializable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NavHostBasicTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Serializable
    object TestHome

    @Serializable
    data class TestDetail(
        val id: String,
    )

    @Test
    fun `NavHost의_startDestination이_첫_화면으로_표시된다`() {
        // given
        composeTestRule.setContent {
            val navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = TestHome) {
                composable<TestHome> { Text("Home Screen") }
                composable<TestDetail> { Text("Detail Screen") }
            }
        }

        // then
        composeTestRule.onNodeWithText("Home Screen").assertIsDisplayed()
    }

    @Test
    fun `navigate로_다른_화면으로_이동할_수_있다`() {
        // given
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = TestHome) {
                composable<TestHome> { Text("Home Screen") }
                composable<TestDetail> { Text("Detail Screen") }
            }
        }

        // when
        navController.navigate(TestDetail(id = "1"))

        // then
        composeTestRule.onNodeWithText("Detail Screen").assertIsDisplayed()
    }

    @Test
    fun `popBackStack으로_이전_화면으로_돌아갈_수_있다`() {
        // given
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = TestHome) {
                composable<TestHome> { Text("Home Screen") }
                composable<TestDetail> { Text("Detail Screen") }
            }
        }

        navController.navigate(TestDetail(id = "1"))
        composeTestRule.onNodeWithText("Detail Screen").assertIsDisplayed()

        // when
        navController.popBackStack()

        // then
        composeTestRule.onNodeWithText("Home Screen").assertIsDisplayed()
    }

    @Test
    fun `백스택이_비어있으면_popBackStack은_false를_반환한다`() {
        // given
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = TestHome) {
                composable<TestHome> { Text("Home Screen") }
                composable<TestDetail> { Text("Detail Screen") }
            }
        }

        // when
        val result = navController.popBackStack()

        // then
        assert(!result) { "startDestination에서 popBackStack은 false를 반환해야 한다" }
        composeTestRule.onNodeWithText("Home Screen").assertIsDisplayed()
    }
}
