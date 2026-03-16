package com.murjune.pratice.compose.study.nav2.challenge.cart.navigation

import kotlinx.serialization.Serializable

@Serializable
data object CartNavGraph

sealed class CartRoute {
    @Serializable
    data object CartScreen : CartRoute()
}
