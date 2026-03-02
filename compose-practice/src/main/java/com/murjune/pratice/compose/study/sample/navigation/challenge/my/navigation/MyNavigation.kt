package com.murjune.pratice.compose.study.sample.navigation.challenge.my.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.murjune.pratice.compose.study.sample.navigation.challenge.navigation.bottomNavPadding
import com.murjune.pratice.compose.study.sample.navigation.challenge.my.MyScreen
import com.murjune.pratice.compose.study.sample.navigation.challenge.my.OrderDetailScreen
import com.murjune.pratice.compose.study.sample.navigation.challenge.my.OrderHistoryScreen

fun NavController.navigateToMyNavGraph(
    navOptions: NavOptions? = null,
) = navigate(MyNavGraph, navOptions)

fun NavController.navigateToMy(
    navOptions: NavOptions? = null,
) = navigate(MyRoute.MyScreen, navOptions)

fun NavController.navigateToOrderHistory(
    navOptions: NavOptions? = null,
) = navigate(MyRoute.OrderHistory, navOptions)

fun NavController.navigateToOrderDetail(
    orderId: Int,
    navOptions: NavOptions? = null,
) = navigate(MyRoute.OrderDetail(orderId), navOptions)

fun NavGraphBuilder.mySection(
    onOrderHistoryClick: () -> Unit,
    onOrderDetailClick: (orderId: Int) -> Unit,
    onNavigateToHomeNavGraph: () -> Unit,
    onBackClick: () -> Unit,
) {
    navigation<MyNavGraph>(startDestination = MyRoute.MyScreen::class) {
        composable<MyRoute.MyScreen> {
            BackHandler { onNavigateToHomeNavGraph() }
            MyScreen(
                onOrderHistoryClick = onOrderHistoryClick,
                modifier = Modifier.bottomNavPadding(),
            )
        }
        composable<MyRoute.OrderHistory> {
            OrderHistoryScreen(
                onBackClick = onBackClick,
                onOrderDetailClick = onOrderDetailClick,
            )
        }
        composable<MyRoute.OrderDetail> { backStackEntry ->
            val orderDetail = backStackEntry.toRoute<MyRoute.OrderDetail>()
            OrderDetailScreen(
                orderId = orderDetail.orderId,
                onBackClick = onBackClick,
            )
        }
    }
}
