package com.murjune.pratice.compose.study.sample.navigation.challenge.cart.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.murjune.pratice.compose.study.sample.navigation.challenge.cart.CartScreen
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation.HomeBaseRoute

fun NavController.navigateToCart(
    navOptions: NavOptions? = null,
) = navigate(CartBaseRoute, navOptions)

fun NavController.navigateToCartFromProduct() {
    navigate(CartBaseRoute) {
        popUpTo<HomeBaseRoute> { inclusive = false }
    }
}

fun NavGraphBuilder.cartSection() {
    navigation<CartBaseRoute>(startDestination = CartRoute.CartScreen::class) {
        composable<CartRoute.CartScreen> {
            CartScreen()
        }
    }
}
