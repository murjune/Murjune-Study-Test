package com.murjune.pratice.compose.study.sample.navigation.challenge.my.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.murjune.pratice.compose.study.sample.navigation.challenge.my.MyScreen
import com.murjune.pratice.compose.study.sample.navigation.challenge.my.OrderDetailScreen
import com.murjune.pratice.compose.study.sample.navigation.challenge.my.OrderHistoryScreen

fun NavController.navigateToMy(
    navOptions: NavOptions? = null,
) = navigate(MyRoute, navOptions)

fun NavController.navigateToOrderHistory(
    navOptions: NavOptions? = null,
) = navigate(OrderHistory, navOptions)

fun NavController.navigateToOrderDetail(
    orderId: Int,
    navOptions: NavOptions? = null,
) = navigate(OrderDetail(orderId), navOptions)

fun NavGraphBuilder.mySection(navController: NavController) {
    navigation<MyRoute>(startDestination = MyScreen::class) {
        composable<MyScreen> {
            MyScreen(
                onOrderHistoryClick = {
                    navController.navigateToOrderHistory()
                },
            )
        }
        composable<OrderHistory> {
            OrderHistoryScreen(
                onBackClick = { navController.navigateUp() },
                onOrderDetailClick = { orderId ->
                    navController.navigateToOrderDetail(orderId)
                },
            )
        }
        composable<OrderDetail> { backStackEntry ->
            val orderDetail = backStackEntry.toRoute<OrderDetail>()
            OrderDetailScreen(
                orderId = orderDetail.orderId,
                onBackClick = { navController.navigateUp() },
            )
        }
    }
}
