package com.murjune.pratice.compose.study.sample.navigation.challenge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import com.murjune.pratice.compose.study.sample.navigation.challenge.cart.navigation.cartSection
import com.murjune.pratice.compose.study.sample.navigation.challenge.cart.navigation.navigateToCartFromProduct
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation.HomeBaseRoute
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation.homeSection
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation.navigateToProductDetail
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation.navigateToReview
import com.murjune.pratice.compose.study.sample.navigation.challenge.my.navigation.mySection
import com.murjune.pratice.compose.study.sample.navigation.challenge.my.navigation.navigateToOrderDetail
import com.murjune.pratice.compose.study.sample.navigation.challenge.my.navigation.navigateToOrderHistory
import com.murjune.pratice.compose.study.sample.navigation.challenge.setting.navigation.navigateToSetting
import com.murjune.pratice.compose.study.sample.navigation.challenge.setting.navigation.settingSection
import androidx.compose.ui.unit.dp
import kotlin.reflect.KClass

private val BottomNavHeight = 80.dp

fun Modifier.bottomNavPadding(): Modifier = padding(bottom = BottomNavHeight)

val BottomNavContentPadding = PaddingValues(bottom = BottomNavHeight)

@Composable
fun ShoppingApp(
    appState: ShoppingAppState,
    modifier: Modifier = Modifier,
) {
    val currentDestination = appState.currentDestination
    val shouldShowBottomNav = appState.topLevelDestinations.any {
        currentDestination.isRouteInHierarchy(it.route)
    }

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = appState.navController,
            startDestination = HomeBaseRoute,
            modifier = Modifier.fillMaxSize(),
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
                    appState.navigateToTopLevelDestination(TopLevelDestination.Home)
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
                    appState.navigateToTopLevelDestination(TopLevelDestination.Home)
                },
                onBackClick = { navController.navigateUp() },
            )
            settingSection(
                onBackClick = { navController.navigateUp() },
            )
        }

        if (shouldShowBottomNav) {
            NavigationBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding(), // TODO: 외부에서 일괄적으로 window inset 처리하는것이 좋기는함.
            ) {
                appState.topLevelDestinations.forEach { destination ->
                    val isSelected = currentDestination.isRouteInHierarchy(destination.route)
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            appState.navigateToTopLevelDestination(destination)
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = stringResource(destination.iconTextId),
                            )
                        },
                        label = { Text(stringResource(destination.titleTextId)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun NavDestination?.isRouteInHierarchy(route: KClass<*>): Boolean =
    this?.hierarchy?.any {
        it.hasRoute(route)
    } ?: false
