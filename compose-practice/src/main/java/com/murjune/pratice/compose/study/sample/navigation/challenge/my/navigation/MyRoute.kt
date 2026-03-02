package com.murjune.pratice.compose.study.sample.navigation.challenge.my.navigation

import kotlinx.serialization.Serializable

@Serializable
data object MyNavGraph

sealed class MyRoute {
    @Serializable
    data object MyScreen : MyRoute()

    @Serializable
    data object OrderHistory : MyRoute()

    @Serializable
    data class OrderDetail(val orderId: Int) : MyRoute()
}
