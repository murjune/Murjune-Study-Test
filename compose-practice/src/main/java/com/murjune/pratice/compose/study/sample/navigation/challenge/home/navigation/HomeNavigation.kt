package com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.murjune.pratice.compose.study.sample.navigation.challenge.cart.navigation.navigateToCartFromProduct
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.HomeScreen
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.ProductDetailScreen
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.ReviewScreen

fun NavController.navigateToHome(
    navOptions: NavOptions? = null,
) = navigate(HomeRoute, navOptions)

fun NavController.navigateToProductDetail(
    productId: Int,
    navOptions: NavOptions? = null,
) = navigate(ProductDetail(productId), navOptions)

fun NavController.navigateToReview(
    productId: Int,
    navOptions: NavOptions? = null,
) = navigate(Review(productId), navOptions)

fun NavGraphBuilder.homeSection(navController: NavController) {
    navigation<HomeRoute>(startDestination = HomeScreen::class) {
        composable<HomeScreen> {
            HomeScreen(
                onProductClick = { productId ->
                    navController.navigateToProductDetail(productId)
                },
            )
        }
        composable<ProductDetail> { backStackEntry ->
            val productDetail = backStackEntry.toRoute<ProductDetail>()
            ProductDetailScreen(
                productId = productDetail.productId,
                onBackClick = { navController.navigateUp() },
                onReviewClick = { productId ->
                    navController.navigateToReview(productId)
                },
                onAddToCart = {
                    navController.navigateToCartFromProduct()
                },
            )
        }
        composable<Review> { backStackEntry ->
            val review = backStackEntry.toRoute<Review>()
            ReviewScreen(
                productId = review.productId,
                onBackClick = { navController.navigateUp() },
            )
        }
    }
}
