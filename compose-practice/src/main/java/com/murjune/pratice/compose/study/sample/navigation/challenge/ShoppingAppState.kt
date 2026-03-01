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
     * 현재 네비게이션 스택의 “현재 화면”을 가져오는 property
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

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) { // 백스택의 시작 지점까지 팝업
                saveState = true // 대신 상태는 저장하여 나중에 복원할 수 있도록 한다. (backstack 도 물론 저장된다)
            }
            launchSingleTop = true // 동일한 항목을 다시 선택할 때 동일한 목적지의 여러 복사본을 방지
            restoreState = true // 이전에 saveState = true 로 저장된 상태가 있다면 복원한다.
        }

        //        when (topLevelDestination) {
        //            TopLevelDestination.Home -> navController.navigateToHome(topLevelNavOptions)
        //            TopLevelDestination.Cart -> navController.navigateToCart(topLevelNavOptions)
        //            TopLevelDestination.My -> navController.navigateToMy(topLevelNavOptions)
        //        }
    }
}