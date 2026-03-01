package com.murjune.pratice.compose.study.sample.navigation.challenge.my.navigation

import kotlinx.serialization.Serializable

@Serializable
data object MyRoute

@Serializable
data object MyScreen

@Serializable
data object OrderHistory

@Serializable
data class OrderDetail(val orderId: Int)