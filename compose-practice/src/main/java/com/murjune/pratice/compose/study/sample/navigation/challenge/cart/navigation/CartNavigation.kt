package com.murjune.pratice.compose.study.sample.navigation.challenge.cart.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.murjune.pratice.compose.study.sample.navigation.challenge.navigation.bottomNavPadding
import com.murjune.pratice.compose.study.sample.navigation.challenge.cart.CartScreen

fun NavController.navigateToCartNavGraph(
    navOptions: NavOptions? = null,
) = navigate(CartNavGraph, navOptions)

fun NavController.navigateToCart(
    navOptions: NavOptions? = null,
) = navigate(CartRoute.CartScreen, navOptions)

fun NavGraphBuilder.cartSection(
    onNavigateToHomeNavGraph: () -> Unit,
) {
    navigation<CartNavGraph>(startDestination = CartRoute.CartScreen::class) {
        composable<CartRoute.CartScreen> {
            BackHandler { onNavigateToHomeNavGraph() }
            CartScreen(modifier = Modifier.bottomNavPadding())
        }
    }
}
