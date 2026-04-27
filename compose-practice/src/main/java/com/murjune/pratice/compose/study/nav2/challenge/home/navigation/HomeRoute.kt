package com.murjune.pratice.compose.study.nav2.challenge.home.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeNavGraph

sealed class HomeRoute {
    @Serializable
    data object HomeScreen : HomeRoute()

    @Serializable
    data class ProductDetail(val productId: Int) : HomeRoute()

    @Serializable
    data class Review(val productId: Int) : HomeRoute()
}
