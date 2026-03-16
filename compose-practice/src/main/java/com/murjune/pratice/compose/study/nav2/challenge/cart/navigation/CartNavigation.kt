package com.murjune.pratice.compose.study.nav2.challenge.cart.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.murjune.pratice.compose.study.nav2.challenge.navigation.bottomNavPadding
import com.murjune.pratice.compose.study.nav2.challenge.cart.CartScreen

fun NavController.navigateToCartNavGraph(
    navOptions: NavOptions? = null,
) = navigate(CartNavGraph, navOptions)

fun NavController.navigateToCart(
    navOptions: NavOptions? = null,
) = navigate(CartRoute.CartScreen, navOptions)

fun NavGraphBuilder.cartSection(
    onBack: () -> Unit,
) {
    navigation<CartNavGraph>(startDestination = CartRoute.CartScreen::class) {
        composable<CartRoute.CartScreen> {
            BackHandler { onBack() }
            CartScreen(modifier = Modifier.bottomNavPadding())
        }
    }
}
