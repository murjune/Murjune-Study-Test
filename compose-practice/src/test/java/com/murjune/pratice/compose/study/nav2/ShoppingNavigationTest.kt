package com.murjune.pratice.compose.study.nav2

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.murjune.pratice.compose.study.nav2.challenge.AppBarNavigator
import com.murjune.pratice.compose.study.nav2.challenge.ShoppingApp
import com.murjune.pratice.compose.study.nav2.challenge.cart.navigation.CartRoute
import com.murjune.pratice.compose.study.nav2.challenge.home.navigation.HomeRoute
import com.murjune.pratice.compose.study.nav2.challenge.my.navigation.MyRoute
import com.murjune.pratice.compose.study.nav2.challenge.navigation.BottomNavDestination
import com.murjune.pratice.compose.study.nav2.challenge.setting.navigation.SettingRoute
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * ShoppingApp Navigation 통합 테스트 (CHALLENGE.md 요구사항 기반)
 */
@RunWith(RobolectricTestRunner::class)
class ShoppingNavigationTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setupShoppingApp(): AppBarNavigator {
        lateinit var appBarNavigator: AppBarNavigator
        composeTestRule.setContent {
            val navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            appBarNavigator = AppBarNavigator(navController = navController)
            ShoppingApp(appBarNavigator = appBarNavigator)
        }
        return appBarNavigator
    }

    @Test
    fun `기본_탭_전환_Home에서_Cart_My로_이동할_수_있다`() {
        val appBarNavigator = setupShoppingApp()
        composeTestRule.onNodeWithText("홈").assertIsDisplayed()

        composeTestRule.onNodeWithText("장바구니").performClick()
        appBarNavigator.navController.currentDestination
            ?.hasRoute<CartRoute.CartScreen>() shouldBe true

        composeTestRule.onNodeWithText("My").performClick()
        appBarNavigator.navController.currentDestination
            ?.hasRoute<MyRoute.MyScreen>() shouldBe true
    }

    @Test
    fun `장바구니_담기_시_ProductDetail이_백스택에서_제거된다`() {
        val appBarNavigator = setupShoppingApp()
        composeTestRule.onNodeWithText("MacBook Pro").performClick()
        composeTestRule.onNodeWithText("상품 ID: 1").assertIsDisplayed()

        composeTestRule.onNodeWithText("장바구니 담기").performClick()
        appBarNavigator.navController.currentDestination
            ?.hasRoute<CartRoute.CartScreen>() shouldBe true

        // popUpTo(HomeNavGraph)로 ProductDetail이 백스택에서 제거됨
        val hasProductDetail = appBarNavigator.navController.currentBackStack.value
            .any { it.destination.hasRoute<HomeRoute.ProductDetail>() }
        hasProductDetail shouldBe false
    }

    @Test
    fun `탭_전환_후_복귀하면_이전_탭의_백스택이_보존된다`() {
        val appBarNavigator = setupShoppingApp()
        composeTestRule.onNodeWithText("MacBook Pro").performClick()
        composeTestRule.onNodeWithText("상품 ID: 1").assertIsDisplayed()

        appBarNavigator.navigateToBottomNavDestination(BottomNavDestination.Cart)
        appBarNavigator.navController.currentDestination
            ?.hasRoute<CartRoute.CartScreen>() shouldBe true

        appBarNavigator.navigateToBottomNavDestination(BottomNavDestination.Home)
        composeTestRule.onNodeWithText("상품 ID: 1").assertIsDisplayed()
    }

    @Test
    fun `하위_화면에서_navigateUp하면_이전_화면으로_복귀한다`() {
        val appBarNavigator = setupShoppingApp()
        composeTestRule.onNodeWithText("MacBook Pro").performClick()
        composeTestRule.onNodeWithText("상품 ID: 1").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("뒤로가기").performClick()
        appBarNavigator.navController.currentDestination
            ?.hasRoute<HomeRoute.HomeScreen>() shouldBe true
    }

    @Test
    fun `DeepLink_Intent로_ProductDetail에_직접_진입할_수_있다`() {
        val appBarNavigator = setupShoppingApp()
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://study.example.com/product/42"),
        )

        val result = appBarNavigator.navController.handleDeepLink(intent)
        result shouldBe true
        composeTestRule.onNodeWithText("상품 ID: 42").assertIsDisplayed()
    }

    @Test
    fun `Setting_진입_시_BottomBar가_사라진다`() {
        val appBarNavigator = setupShoppingApp()
        composeTestRule.onNodeWithText("홈").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("설정").performClick()
        appBarNavigator.navController.currentDestination
            ?.hasRoute<SettingRoute>() shouldBe true
        composeTestRule.onNodeWithText("홈").assertDoesNotExist()
    }

    @Test
    fun `Setting에서_뒤로가기하면_이전_탭과_BottomBar가_복귀한다`() {
        val appBarNavigator = setupShoppingApp()
        composeTestRule.onNodeWithContentDescription("설정").performClick()
        appBarNavigator.navController.currentDestination
            ?.hasRoute<SettingRoute>() shouldBe true

        composeTestRule.onNodeWithContentDescription("뒤로가기").performClick()
        appBarNavigator.navController.currentDestination
            ?.hasRoute<HomeRoute.HomeScreen>() shouldBe true
        composeTestRule.onNodeWithText("홈").assertIsDisplayed()
        composeTestRule.onNodeWithText("장바구니").assertIsDisplayed()
        composeTestRule.onNodeWithText("My").assertIsDisplayed()
    }
}
