package com.murjune.pratice.compose.study.sample.navigation.challenge.cart.navigation

import kotlinx.serialization.Serializable

@Serializable
data object CartBaseRoute

sealed class CartRoute {
    @Serializable
    data object CartScreen : CartRoute()
}
