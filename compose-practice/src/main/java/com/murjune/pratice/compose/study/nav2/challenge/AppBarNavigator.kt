package com.murjune.pratice.compose.study.nav2.challenge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.murjune.pratice.compose.study.nav2.challenge.cart.navigation.navigateToCartNavGraph
import com.murjune.pratice.compose.study.nav2.challenge.home.navigation.navigateToHomeNavGraph
import com.murjune.pratice.compose.study.nav2.challenge.more.navigation.navigateToMore
import com.murjune.pratice.compose.study.nav2.challenge.my.navigation.navigateToMyNavGraph
import com.murjune.pratice.compose.study.nav2.challenge.navigation.BottomNavDestination
import kotlin.reflect.KClass

@Composable
fun rememberAppBarNavigator(
    navController: NavHostController = rememberNavController(),
): AppBarNavigator {
    return remember(navController) {
        AppBarNavigator(navController = navController)
    }
}

@Stable
data class AppBarNavigator(
    val navController: NavHostController,
) {
    val currentDestination
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val bottomNavDestinations: List<BottomNavDestination> = BottomNavDestination.entries

    fun navigateToBottomNavDestination(destination: BottomNavDestination) {
        val navOptions = bottomNavigationNavOptions(destination) ?: return

        when (destination) {
            BottomNavDestination.Home -> navController.navigateToHomeNavGraph(navOptions)
            BottomNavDestination.Cart -> navController.navigateToCartNavGraph(navOptions)
            BottomNavDestination.My -> navController.navigateToMyNavGraph(navOptions)
            BottomNavDestination.More -> navController.navigateToMore()
        }
    }

    private fun bottomNavigationNavOptions(destination: BottomNavDestination): NavOptions? {
        val isCurrentDestination = navController.currentDestination
            ?.hasRoute(destination.route)
            ?: false

        if (isCurrentDestination) {
            return null
        }

        val isCurrentTab = isRouteInHierarchy(navController.currentDestination, destination.baseRoute)

        return navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = !isCurrentTab
            }
            launchSingleTop = true
            restoreState = !isCurrentTab
        }
    }

    fun isRouteInHierarchy(currentDestination: NavDestination?, baseRoute: KClass<*>): Boolean =
        currentDestination?.hierarchy?.any {
            it.hasRoute(baseRoute)
        } ?: false
}
