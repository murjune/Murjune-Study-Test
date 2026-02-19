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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TypeSafeNavigationTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Serializable
    object TypeSafeHome

    @Serializable
    data class UserProfile(
        val userId: String,
        val userName: String,
    )

    @Serializable
    data class MultiArgRoute(
        val name: String,
        val age: Int,
        val isPremium: Boolean,
    )

    @Test
    fun `Serializable_route로_화면을_등록할_수_있다`() {
        // given
        composeTestRule.setContent {
            val navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = TypeSafeHome) {
                composable<TypeSafeHome> { Text("Type Safe Home") }
                composable<UserProfile> { Text("User Profile") }
            }
        }

        // then
        composeTestRule.onNodeWithText("Type Safe Home").assertIsDisplayed()
    }

    @Test
    fun `navigate에_인자를_전달하고_toRoute로_추출할_수_있다`() {
        // given
        lateinit var navController: TestNavHostController
        var extractedUserId = ""
        var extractedUserName = ""

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = TypeSafeHome) {
                composable<TypeSafeHome> { Text("Type Safe Home") }
                composable<UserProfile> { backStackEntry ->
                    val route = backStackEntry.toRoute<UserProfile>()
                    extractedUserId = route.userId
                    extractedUserName = route.userName
                    Text("User: ${route.userName}")
                }
            }
        }

        // when
        composeTestRule.runOnUiThread {
            navController.navigate(UserProfile(userId = "user-123", userName = "murjune"))
        }

        // then
        composeTestRule.onNodeWithText("User: murjune").assertIsDisplayed()
        extractedUserId shouldBe "user-123"
        extractedUserName shouldBe "murjune"
    }

    @Test
    fun `여러_타입의_인자를_전달할_수_있다`() {
        // given
        lateinit var navController: TestNavHostController
        var extractedName = ""
        var extractedAge = 0
        var extractedIsPremium = false

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = TypeSafeHome) {
                composable<TypeSafeHome> { Text("Home") }
                composable<MultiArgRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<MultiArgRoute>()
                    extractedName = route.name
                    extractedAge = route.age
                    extractedIsPremium = route.isPremium
                    Text("${route.name}, ${route.age}, ${route.isPremium}")
                }
            }
        }

        // when
        composeTestRule.runOnUiThread {
            navController.navigate(MultiArgRoute(name = "murjune", age = 28, isPremium = true))
        }

        // then
        composeTestRule.onNodeWithText("murjune, 28, true").assertIsDisplayed()
        extractedName shouldBe "murjune"
        extractedAge shouldBe 28
        extractedIsPremium shouldBe true
    }
}
