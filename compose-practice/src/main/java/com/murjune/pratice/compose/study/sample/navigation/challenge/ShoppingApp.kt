package com.murjune.pratice.compose.study.sample.navigation.challenge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.murjune.pratice.compose.study.sample.navigation.challenge.cart.navigation.cartSection
import com.murjune.pratice.compose.study.sample.navigation.challenge.cart.navigation.navigateToCartFromProduct
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation.HomeNavGraph
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation.homeSection
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation.navigateToProductDetail
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation.navigateToReview
import com.murjune.pratice.compose.study.sample.navigation.challenge.more.navigation.moreScreen
import com.murjune.pratice.compose.study.sample.navigation.challenge.my.navigation.mySection
import com.murjune.pratice.compose.study.sample.navigation.challenge.my.navigation.navigateToOrderDetail
import com.murjune.pratice.compose.study.sample.navigation.challenge.my.navigation.navigateToOrderHistory
import com.murjune.pratice.compose.study.sample.navigation.challenge.navigation.BottomNavDestination
import com.murjune.pratice.compose.study.sample.navigation.challenge.navigation.ShoppingBottomNavigationBar
import com.murjune.pratice.compose.study.sample.navigation.challenge.navigation.isRouteInHierarchy
import com.murjune.pratice.compose.study.sample.navigation.challenge.setting.navigation.navigateToSetting
import com.murjune.pratice.compose.study.sample.navigation.challenge.setting.navigation.settingSection

/**
 * Scaffold의 bottomBar를 사용하여 BottomBar hide/show를 처리하는 방식
 * - innerPadding을 사용하지 않고, 각 Screen에서 수동으로 bottomNavPadding 처리
 */
@Composable
fun ShoppingApp(
    appState: ShoppingAppState,
    modifier: Modifier = Modifier,
) {
    val currentDestination = appState.currentDestination
    val shouldShowBottomNav = appState.bottomNavDestinations.any { destination ->
        destination.isSelectable && currentDestination.isRouteInHierarchy(destination.baseRoute)
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (shouldShowBottomNav) {
                ShoppingBottomNavigationBar(
                    destinations = appState.bottomNavDestinations,
                    currentDestination = currentDestination,
                    onNavigate = { destination ->
                        appState.navigateToBottomNavDestination(destination)
                    },
                )
            }
        }
    ) {
        ShoppingNavHost(
            appState = appState,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/**
 * NavigationBar를 Box 오버레이로 직접 배치하는 방식 (커스텀 BottomNav)
 */
@Composable
fun ShoppingAppCustomBottomNav(
    appState: ShoppingAppState,
    modifier: Modifier = Modifier,
) {
    val currentDestination = appState.currentDestination
    val shouldShowBottomNav = appState.bottomNavDestinations.any { destination ->
        destination.isSelectable && currentDestination.isRouteInHierarchy(destination.baseRoute)
    }

    Box(modifier = modifier.fillMaxSize()) {
        ShoppingNavHost(
            appState = appState,
            modifier = Modifier.fillMaxSize(),
        )

        if (shouldShowBottomNav) {
            ShoppingBottomNavigationBar(
                destinations = appState.bottomNavDestinations,
                currentDestination = currentDestination,
                onNavigate = { destination ->
                    appState.navigateToBottomNavDestination(destination)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding(),
            )
        }
    }
}

@Composable
private fun ShoppingNavHost(
    appState: ShoppingAppState,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = appState.navController,
        startDestination = HomeNavGraph,
        modifier = modifier,
    ) {
        val navController = appState.navController
        homeSection(
            onProductClick = { productId ->
                navController.navigateToProductDetail(productId)
            },
            onReviewClick = { productId ->
                navController.navigateToReview(productId)
            },
            onSettingClick = {
                navController.navigateToSetting()
            },
            onAddToCart = {
                navController.navigateToCartFromProduct()
            },
            onBackClick = { navController.navigateUp() },
        )
        cartSection(
            onNavigateToHome = {
                appState.navigateToBottomNavDestination(BottomNavDestination.Home)
            },
        )
        mySection(
            onOrderHistoryClick = {
                navController.navigateToOrderHistory()
            },
            onOrderDetailClick = { orderId ->
                navController.navigateToOrderDetail(orderId)
            },
            onNavigateToHome = {
                appState.navigateToBottomNavDestination(BottomNavDestination.Home)
            },
            onBackClick = { navController.navigateUp() },
        )
        settingSection(
            onBackClick = { navController.navigateUp() },
        )
        moreScreen(
            onBackClick = { navController.navigateUp() },
        )
    }
}
