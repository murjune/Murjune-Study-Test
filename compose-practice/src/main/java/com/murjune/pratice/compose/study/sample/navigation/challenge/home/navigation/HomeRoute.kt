package com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object HomeScreen

@Serializable
data class ProductDetail(val productId: Int)

@Serializable
data class Review(val productId: Int)
