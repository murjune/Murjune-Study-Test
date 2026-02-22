package com.murjune.pratice.compose.study.navigation

import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavType
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.testing.TestNavHostController
import androidx.navigation.toRoute
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * String 기반 Route(Navigation 2.7 이전) vs Type-Safe Route(2.8+) 비교 테스트
 *
 * 핵심 차이:
 * - String route: composable("profile/{id}") + navigate("profile/123")
 * - Type-Safe route: composable<Profile> + navigate(Profile(id = "123"))
 *
 * Type-Safe route는 컴파일 타임에 인자 타입/이름을 검증하여 런타임 오류를 방지한다.
 */
@RunWith(RobolectricTestRunner::class)
class StringRouteVsTypeSafeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Type-Safe Route 정의
    @Serializable
    object Home

    @Serializable
    data class Profile(
        val id: String,
        val age: Int,
    )

    @Serializable
    data class Item(
        val id: Int,
    )

    @Serializable
    object Login

    @Serializable
    object Dashboard

    // =========================================================================
    // 1) String 기반 Route — composable("route/{arg}") 방식
    // =========================================================================

    @Test
    fun `String_route로_화면을_등록하고_navigate할_수_있다`() {
        // given
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { Text("Home") }
                composable("profile/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")
                    Text("Profile: $id")
                }
            }
        }

        // when
        navController.navigate("profile/user-42")

        // then
        composeTestRule.onNodeWithText("Profile: user-42").assertIsDisplayed()
    }

    @Test
    fun `String_route는_인자_타입을_navArgument로_명시해야_한다`() {
        // given
        lateinit var navController: TestNavHostController
        var extractedAge = 0

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { Text("Home") }
                composable(
                    route = "profile/{id}/{age}",
                    arguments =
                        listOf(
                            navArgument("id") { type = NavType.StringType },
                            navArgument("age") { type = NavType.IntType },
                        ),
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: ""
                    val age = backStackEntry.arguments?.getInt("age") ?: 0
                    extractedAge = age
                    Text("Profile: $id, Age: $age")
                }
            }
        }

        // when — 문자열로 인자를 전달 (타입 실수 가능)
        navController.navigate("profile/murjune/28")

        // then
        composeTestRule.onNodeWithText("Profile: murjune, Age: 28").assertIsDisplayed()
        extractedAge shouldBe 28
    }

    // =========================================================================
    // 2) Type-Safe Route — composable<T> 방식
    // =========================================================================

    @Test
    fun `TypeSafe_route는_data_class로_인자_타입이_컴파일_타임에_보장된다`() {
        // given
        lateinit var navController: TestNavHostController
        var extractedId = ""
        var extractedAge = 0

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = Home) {
                composable<Home> { Text("Home") }
                composable<Profile> { backStackEntry ->
                    val route = backStackEntry.toRoute<Profile>()
                    extractedId = route.id
                    extractedAge = route.age
                    Text("Profile: ${route.id}, Age: ${route.age}")
                }
            }
        }

        // when — 타입 안전하게 인자 전달 (컴파일 타임 검증)
        navController.navigate(Profile(id = "murjune", age = 28))

        // then
        composeTestRule.onNodeWithText("Profile: murjune, Age: 28").assertIsDisplayed()
        extractedId shouldBe "murjune"
        extractedAge shouldBe 28
    }

    // =========================================================================
    // 3) launchSingleTop 비교 — 두 방식 모두 동일하게 동작
    // =========================================================================

    @Test
    fun `String_route에서도_launchSingleTop은_같은_destination_중복을_방지한다`() {
        // given
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { Text("Home") }
                composable(
                    route = "item/{id}",
                    arguments =
                        listOf(
                            navArgument("id") { type = NavType.IntType },
                        ),
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("id") ?: 0
                    Text("Item $id")
                }
            }
        }

        // Home → Item(1)
        navController.navigate("item/1")
        composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
        val sizeAfterFirst = navController.currentBackStack.value.size

        // when — launchSingleTop으로 같은 destination(다른 인자) navigate
        navController.navigate("item/2") {
            launchSingleTop = true
        }

        // then — 백스택 크기 동일 (새 엔트리 안 쌓임), 인자만 갱신
        navController.currentBackStack.value.size shouldBe sizeAfterFirst
        composeTestRule.onNodeWithText("Item 2").assertIsDisplayed()
    }

    @Test
    fun `TypeSafe_route에서도_launchSingleTop은_동일하게_동작한다`() {
        // given
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = Home) {
                composable<Home> { Text("Home") }
                composable<Item> { backStackEntry ->
                    val route = backStackEntry.toRoute<Item>()
                    Text("Item ${route.id}")
                }
            }
        }

        // Home → Item(1)
        navController.navigate(Item(id = 1))
        composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
        val sizeAfterFirst = navController.currentBackStack.value.size

        // when — launchSingleTop으로 같은 destination(다른 인자) navigate
        navController.navigate(Item(id = 2)) {
            launchSingleTop = true
        }

        // then — 동일한 동작: 백스택 안 쌓이고 인자만 갱신
        navController.currentBackStack.value.size shouldBe sizeAfterFirst
        composeTestRule.onNodeWithText("Item 2").assertIsDisplayed()
    }

    // =========================================================================
    // 4) popUpTo 비교 — String route는 문자열, Type-Safe는 KClass 사용
    // =========================================================================

    @Test
    fun `String_route의_popUpTo는_문자열_route를_사용한다`() {
        // given
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { Text("Home") }
                composable("login") { Text("Login") }
                composable("dashboard") { Text("Dashboard") }
            }
        }

        navController.navigate("login")
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()

        // when — 문자열로 popUpTo 지정
        navController.navigate("dashboard") {
            popUpTo("home") { inclusive = false }
        }

        // then — home 위의 login이 제거되고 dashboard가 쌓임
        composeTestRule.onNodeWithText("Dashboard").assertIsDisplayed()
        navController.popBackStack()
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }

    @Test
    fun `TypeSafe_route의_popUpTo는_KClass를_사용한다`() {
        // given
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavHost(navController = navController, startDestination = Home) {
                composable<Home> { Text("Home") }
                composable<Login> { Text("Login") }
                composable<Dashboard> { Text("Dashboard") }
            }
        }

        navController.navigate(Login)
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()

        // when — KClass로 popUpTo 지정 (타입 안전)
        navController.navigate(Dashboard) {
            popUpTo<Home> { inclusive = false }
        }

        // then — 동일한 동작: Home 위의 Login 제거, Dashboard 쌓임
        composeTestRule.onNodeWithText("Dashboard").assertIsDisplayed()
        navController.popBackStack()
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }
}
