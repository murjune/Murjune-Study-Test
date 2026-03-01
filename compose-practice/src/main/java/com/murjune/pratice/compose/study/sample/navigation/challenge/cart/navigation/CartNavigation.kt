package com.murjune.pratice.compose.study.sample.navigation.challenge.cart.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.murjune.pratice.compose.study.sample.navigation.challenge.cart.CartScreen
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation.HomeRoute

fun NavController.navigateToCart(
    navOptions: NavOptions? = null,
) = navigate(CartRoute, navOptions)

fun NavController.navigateToCartFromProduct() {
    navigate(CartRoute) {
        popUpTo<HomeRoute> { inclusive = false }
    }
}

fun NavGraphBuilder.cartSection(navController: NavController) {
    navigation<CartRoute>(startDestination = CartScreen::class) {
        composable<CartScreen> {
            CartScreen()
        }
    }
}
