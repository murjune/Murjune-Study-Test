package com.murjune.pratice.compose.study.sample.navigation.challenge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.murjune.pratice.compose.study.sample.navigation.challenge.cart.navigation.navigateToCartNavGraph
import com.murjune.pratice.compose.study.sample.navigation.challenge.home.navigation.navigateToHomeNavGraph
import com.murjune.pratice.compose.study.sample.navigation.challenge.more.navigation.navigateToMore
import com.murjune.pratice.compose.study.sample.navigation.challenge.my.navigation.navigateToMyNavGraph
import com.murjune.pratice.compose.study.sample.navigation.challenge.navigation.BottomNavDestination
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberShoppingAppState(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): ShoppingAppState {
    return remember(
        navController,
        coroutineScope,
    ) {
        ShoppingAppState(
            navController = navController,
            coroutineScope = coroutineScope,
        )
    }
}

@Stable
data class ShoppingAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    /**
     * 현재 네비게이션 스택의 "현재 화면"을 가져오는 property
     * NavController의 currentBackStackEntryFlow를 수집하여 현재 백스택 항목을 관찰합니다.
     * */
    val currentDestination: NavDestination?
        @Composable get() {
            // Collect the currentBackStackEntryFlow as a state
            val currentEntry by navController.currentBackStackEntryAsState()

            // Fallback to previousDestination if currentEntry is null
            return currentEntry?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }

    val bottomNavDestinations: List<BottomNavDestination> = BottomNavDestination.entries

    fun navigateToBottomNavDestination(destination: BottomNavDestination) {
        val navOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (destination) {
            BottomNavDestination.Home -> navController.navigateToHomeNavGraph(navOptions)
            BottomNavDestination.Cart -> navController.navigateToCartNavGraph(navOptions)
            BottomNavDestination.My -> navController.navigateToMyNavGraph(navOptions)
            BottomNavDestination.More -> navController.navigateToMore()
        }
    }
}
