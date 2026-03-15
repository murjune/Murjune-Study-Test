package com.murjune.pratice.compose.study.navigation

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
import com.murjune.pratice.compose.study.sample.navigation.challenge.AppBarNavigator
import com.murjune.pratice.compose.study.sample.navigation.challenge.ShoppingApp
import com.murjune.pratice.compose.study.sample.navigation.challenge.cart.navigation.CartRoute
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation.HomeRoute
import com.murjune.pratice.compose.study.sample.navigation.challenge.my.navigation.MyRoute
import com.murjune.pratice.compose.study.sample.navigation.challenge.navigation.BottomNavDestination
import com.murjune.pratice.compose.study.sample.navigation.challenge.setting.navigation.SettingRoute
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * ShoppingApp Navigation 통합 테스트
 *
 * CHALLENGE.md 요구사항 기반 7개 테스트:
 * 1. 기본 탭 전환 (Home → Cart → My)
 * 2. popUpTo 동작 (ProductDetail → 장바구니 담기 시 ProductDetail 제거)
 * 3. 탭 내부 스택 보존 (Home → Detail → Cart → Home 복귀 시 Detail 유지)
 * 4. navigateUp 동작 (하위 화면 → 이전 화면 복귀)
 * 5. DeepLink 진입 (Intent로 ProductDetail 직접 진입)
 * 6. Setting 전체화면 (BottomBar 숨김 확인)
 * 7. Setting 뒤로가기 (이전 탭 + BottomBar 복귀)
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

    // =========================================================================
    // 테스트 1: 기본 탭 전환 (Home → Cart → My)
    // =========================================================================

    @Test
    fun `기본_탭_전환_Home에서_Cart_My로_이동할_수_있다`() {
        // given
        val appBarNavigator = setupShoppingApp()
        // Home 탭의 BottomNav 라벨 확인
        composeTestRule.onNodeWithText("홈").assertIsDisplayed()

        // when - Cart 탭 클릭 (BottomNav 라벨)
        composeTestRule.onNodeWithText("장바구니").performClick()
        

        // then
        appBarNavigator.navController.currentDestination
            ?.hasRoute<CartRoute.CartScreen>() shouldBe true

        // when - My 탭 클릭
        composeTestRule.onNodeWithText("My").performClick()
        

        // then
        appBarNavigator.navController.currentDestination
            ?.hasRoute<MyRoute.MyScreen>() shouldBe true
    }

    // =========================================================================
    // 테스트 2: popUpTo 동작 (ProductDetail → 장바구니 담기 → Cart)
    // =========================================================================

    @Test
    fun `장바구니_담기_시_ProductDetail이_백스택에서_제거된다`() {
        // given - Home → ProductDetail (MacBook Pro 클릭)
        val appBarNavigator = setupShoppingApp()
        composeTestRule.onNodeWithText("MacBook Pro").performClick()
        
        composeTestRule.onNodeWithText("상품 ID: 1").assertIsDisplayed()

        // when - 장바구니 담기 클릭 (popUpTo(HomeNavGraph) 적용)
        composeTestRule.onNodeWithText("장바구니 담기").performClick()
        

        // then - Cart 화면 도착
        appBarNavigator.navController.currentDestination
            ?.hasRoute<CartRoute.CartScreen>() shouldBe true

        // then - 백스택에 ProductDetail이 없어야 함
        val hasProductDetail = appBarNavigator.navController.currentBackStack.value
            .any { it.destination.hasRoute<HomeRoute.ProductDetail>() }
        hasProductDetail shouldBe false
    }

    // =========================================================================
    // 테스트 3: 탭 내부 스택 보존
    // =========================================================================

    @Test
    fun `탭_전환_후_복귀하면_이전_탭의_백스택이_보존된다`() {
        // given - Home → ProductDetail(1)
        val appBarNavigator = setupShoppingApp()
        composeTestRule.onNodeWithText("MacBook Pro").performClick()
        
        composeTestRule.onNodeWithText("상품 ID: 1").assertIsDisplayed()

        // when - Cart 탭으로 전환
        appBarNavigator.navigateToBottomNavDestination(BottomNavDestination.Cart)
        
        appBarNavigator.navController.currentDestination
            ?.hasRoute<CartRoute.CartScreen>() shouldBe true

        // when - Home 탭으로 복귀
        appBarNavigator.navigateToBottomNavDestination(BottomNavDestination.Home)
        

        // then - ProductDetail(1)이 복원되어야 함
        composeTestRule.onNodeWithText("상품 ID: 1").assertIsDisplayed()
    }

    // =========================================================================
    // 테스트 4: navigateUp 동작
    // =========================================================================

    @Test
    fun `하위_화면에서_navigateUp하면_이전_화면으로_복귀한다`() {
        // given - Home → ProductDetail
        val appBarNavigator = setupShoppingApp()
        composeTestRule.onNodeWithText("MacBook Pro").performClick()
        
        composeTestRule.onNodeWithText("상품 ID: 1").assertIsDisplayed()

        // when - TopAppBar 뒤로가기 버튼 클릭
        composeTestRule.onNodeWithContentDescription("뒤로가기").performClick()
        

        // then - Home 화면으로 복귀
        appBarNavigator.navController.currentDestination
            ?.hasRoute<HomeRoute.HomeScreen>() shouldBe true
    }

    // =========================================================================
    // 테스트 5: DeepLink 진입
    // =========================================================================

    @Test
    fun `DeepLink_Intent로_ProductDetail에_직접_진입할_수_있다`() {
        // given
        val appBarNavigator = setupShoppingApp()
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://study.example.com/product/42"),
        )

        // when - handleDeepLink 호출 (ShoppingActivity에서는 자동 처리)
        val result = appBarNavigator.navController.handleDeepLink(intent)

        // then - DeepLink 매칭 성공
        result shouldBe true

        // then - ProductDetail(42) 화면 도착
        
        composeTestRule.onNodeWithText("상품 ID: 42").assertIsDisplayed()
    }

    // =========================================================================
    // 테스트 6: Setting 전체화면 (BottomBar 숨김)
    // =========================================================================

    @Test
    fun `Setting_진입_시_BottomBar가_사라진다`() {
        // given - Home 화면에서 BottomBar 표시 확인
        val appBarNavigator = setupShoppingApp()
        composeTestRule.onNodeWithText("홈").assertIsDisplayed()

        // when - Setting 화면 진입 (TopAppBar 설정 아이콘 - contentDescription)
        composeTestRule.onNodeWithContentDescription("설정").performClick()
        

        // then - Setting 화면 도착
        appBarNavigator.navController.currentDestination
            ?.hasRoute<SettingRoute>() shouldBe true

        // then - BottomBar가 사라져야 함 (홈 탭 라벨이 존재하지 않음)
        composeTestRule.onNodeWithText("홈").assertDoesNotExist()
    }

    // =========================================================================
    // 테스트 7: Setting 뒤로가기 → 이전 탭 + BottomBar 복귀
    // =========================================================================

    @Test
    fun `Setting에서_뒤로가기하면_이전_탭과_BottomBar가_복귀한다`() {
        // given - Home → Setting
        val appBarNavigator = setupShoppingApp()
        composeTestRule.onNodeWithContentDescription("설정").performClick()
        
        appBarNavigator.navController.currentDestination
            ?.hasRoute<SettingRoute>() shouldBe true

        // when - 뒤로가기
        composeTestRule.onNodeWithContentDescription("뒤로가기").performClick()
        

        // then - Home 화면 복귀
        appBarNavigator.navController.currentDestination
            ?.hasRoute<HomeRoute.HomeScreen>() shouldBe true

        // then - BottomBar 다시 표시
        composeTestRule.onNodeWithText("홈").assertIsDisplayed()
        composeTestRule.onNodeWithText("장바구니").assertIsDisplayed()
        composeTestRule.onNodeWithText("My").assertIsDisplayed()
    }
}
