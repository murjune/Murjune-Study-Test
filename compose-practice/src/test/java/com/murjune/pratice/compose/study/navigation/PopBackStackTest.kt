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
import kotlinx.serialization.Serializable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PopBackStackTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Serializable
    object ScreenA

    @Serializable
    object ScreenB

    @Serializable
    object ScreenC

    private fun setupNavHost(): TestNavHostController {
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = ScreenA) {
                composable<ScreenA> { Text("Screen A") }
                composable<ScreenB> { Text("Screen B") }
                composable<ScreenC> { Text("Screen C") }
            }
        }
        return navController
    }

    private fun navigateToABC(navController: TestNavHostController) {
        navController.navigate(ScreenB)
        navController.navigate(ScreenC)
        composeTestRule.onNodeWithText("Screen C").assertIsDisplayed()
    }

    @Test
    fun `popBackStack은_현재_화면을_백스택에서_제거한다`() {
        // given - A -> B -> C
        val navController = setupNavHost()
        navigateToABC(navController)

        // when
        navController.popBackStack()

        // then - B가 표시된다
        composeTestRule.onNodeWithText("Screen B").assertIsDisplayed()
    }

    @Test
    fun `popBackStack에_route를_지정하면_해당_화면까지_제거한다`() {
        // given - A -> B -> C
        val navController = setupNavHost()
        navigateToABC(navController)

        // when - A까지 pop (inclusive=false이므로 A는 유지)
        navController.popBackStack<ScreenA>(inclusive = false)

        // then - A가 표시된다
        composeTestRule.onNodeWithText("Screen A").assertIsDisplayed()
    }

    @Test
    fun `inclusive_true이면_지정_화면도_함께_제거한다`() {
        // given - A -> B -> C
        val navController = setupNavHost()
        navigateToABC(navController)

        // when - B까지 pop (inclusive=true이므로 B도 제거)
        navController.popBackStack<ScreenB>(inclusive = true)

        // then - A가 표시된다
        composeTestRule.onNodeWithText("Screen A").assertIsDisplayed()
    }

    @Test
    fun `inclusive_false이면_지정_화면은_유지한다`() {
        // given - A -> B -> C
        val navController = setupNavHost()
        navigateToABC(navController)

        // when - B까지 pop (inclusive=false이므로 B는 유지)
        navController.popBackStack<ScreenB>(inclusive = false)

        // then - B가 표시된다
        composeTestRule.onNodeWithText("Screen B").assertIsDisplayed()
    }
}
