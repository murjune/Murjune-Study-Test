package com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.HomeScreen
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.ProductDetailScreen
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.ReviewScreen

fun NavController.navigateToHome(
    navOptions: NavOptions? = null,
) = navigate(HomeBaseRoute, navOptions)

fun NavController.navigateToProductDetail(
    productId: Int,
    navOptions: NavOptions? = null,
) = navigate(HomeRoute.ProductDetail(productId), navOptions)

fun NavController.navigateToReview(
    productId: Int,
    navOptions: NavOptions? = null,
) = navigate(HomeRoute.Review(productId), navOptions)

fun NavGraphBuilder.homeSection(
    onProductClick: (productId: Int) -> Unit,
    onReviewClick: (productId: Int) -> Unit,
    onSettingClick: () -> Unit,
    onAddToCart: () -> Unit,
    onBackClick: () -> Unit,
) {
    navigation<HomeBaseRoute>(startDestination = HomeRoute.HomeScreen::class) {
        composable<HomeRoute.HomeScreen> {
            HomeScreen(
                onProductClick = onProductClick,
                onSettingClick = onSettingClick,
            )
        }
        composable<HomeRoute.ProductDetail> { backStackEntry ->
            val productDetail = backStackEntry.toRoute<HomeRoute.ProductDetail>()
            ProductDetailScreen(
                productId = productDetail.productId,
                onBackClick = onBackClick,
                onReviewClick = onReviewClick,
                onAddToCart = onAddToCart,
            )
        }
        composable<HomeRoute.Review> { backStackEntry ->
            val review = backStackEntry.toRoute<HomeRoute.Review>()
            ReviewScreen(
                productId = review.productId,
                onBackClick = onBackClick,
            )
        }
    }
}
