package com.murjune.pratice.compose.study.sample.navigation.challenge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.murjune.pratice.compose.study.sample.navigation.challenge.cart.navigation.cartSection
import com.murjune.pratice.compose.study.sample.navigation.challenge.cart.navigation.navigateToCart
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
import com.murjune.pratice.compose.study.sample.navigation.challenge.setting.navigation.navigateToSetting
import com.murjune.pratice.compose.study.sample.navigation.challenge.setting.navigation.settingSection

/**
 * Scaffold의 bottomBar를 사용하여 BottomBar hide/show를 처리하는 방식
 * - innerPadding을 사용하지 않고, 각 Screen에서 수동으로 bottomNavPadding 처리
 */
@Composable
fun ShoppingApp(
    appBarNavigator: AppBarNavigator,
    modifier: Modifier = Modifier,
) {
    val currentDestination = appBarNavigator.currentDestination
    val shouldShowBottomNav = appBarNavigator.bottomNavDestinations.any { destination ->
        destination.isSelectable && appBarNavigator.isRouteInHierarchy(
            currentDestination = currentDestination,
            baseRoute = destination.baseRoute
        )
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (shouldShowBottomNav) {
                ShoppingBottomNavigationBar(
                    destinations = appBarNavigator.bottomNavDestinations,
                    currentDestination = currentDestination,
                    onNavigate = { destination ->
                        appBarNavigator.navigateToBottomNavDestination(destination)
                    },
                )
            }
        }
    ) {
        ShoppingNavHost(
            appBarNavigator = appBarNavigator,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/**
 * NavigationBar를 Box 오버레이로 직접 배치하는 방식 (커스텀 BottomNav)
 */
@Composable
fun ShoppingAppCustomBottomNav(
    appBarNavigator: AppBarNavigator,
    modifier: Modifier = Modifier,
) {
    val currentDestination = appBarNavigator.currentDestination
    val shouldShowBottomNav = appBarNavigator.bottomNavDestinations.any { destination ->
        destination.isSelectable && appBarNavigator.isRouteInHierarchy(
            currentDestination = currentDestination,
            baseRoute = destination.baseRoute
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        ShoppingNavHost(
            appBarNavigator = appBarNavigator,
            modifier = Modifier.fillMaxSize(),
        )

        if (shouldShowBottomNav) {
            ShoppingBottomNavigationBar(
                destinations = appBarNavigator.bottomNavDestinations,
                currentDestination = currentDestination,
                onNavigate = { destination ->
                    appBarNavigator.navigateToBottomNavDestination(destination)
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
    appBarNavigator: AppBarNavigator,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = appBarNavigator.navController,
        startDestination = HomeNavGraph,
        modifier = modifier,
    ) {
        val navController = appBarNavigator.navController
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
                navController.navigateToCart(navOptions = navOptions {
                    popUpTo(HomeNavGraph) {}
                })
            },
            onBack = { navController.navigateUp() },
        )
        cartSection(
            onBack = {
                appBarNavigator.navigateToBottomNavDestination(BottomNavDestination.Home)
            },
        )
        mySection(
            onOrderHistoryClick = {
                navController.navigateToOrderHistory()
            },
            onOrderDetailClick = { orderId ->
                navController.navigateToOrderDetail(orderId)
            },
            onNavigateToHomeNavGraph = {
                appBarNavigator.navigateToBottomNavDestination(BottomNavDestination.Home)
            },
            onBack = { navController.navigateUp() },
        )
        settingSection(
            onBack = { navController.navigateUp() },
        )
        moreScreen(
            onBack = { navController.navigateUp() },
        )
    }
}
